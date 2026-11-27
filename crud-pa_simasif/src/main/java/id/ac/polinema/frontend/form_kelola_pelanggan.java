package id.ac.polinema.frontend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class form_kelola_pelanggan {

    private static final String URL = "jdbc:mysql://localhost:3306/db_simasif";
    private static final String USER = "root";
    private static final String PASS = "";

    static DefaultTableModel model;
    static JTable tabel;
    static JFrame frame;
    static JTextField txtNama;
    static JTextField txtNomor;
    static JTextField txtCari;
    static JComboBox<String> cmbKategori;
    static JButton btnTambah;
    static JButton btnUbah;
    static JButton btnHapus;
    static JLabel lblInfo;
    static int selectedId = -1; // id_pelanggan baris yang sedang dipilih
    static boolean sedangMemuat = false;

    private static Connection conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Aktif = punya pemesanan aktif, Baru = daftar <= 7 hari lalu, selain itu Nonaktif
    private static String tentukanStatus(int aktif, LocalDate daftar) {
        if (aktif > 0) {
            return "Aktif";
        }
        return daftar.isBefore(LocalDate.now().minusDays(7)) ? "Nonaktif" : "Baru";
    }

    // Mengembalikan pesan error, atau null jika input valid
    private static String validasi(String nama, String nomor) {
        if (nama.isEmpty() || nomor.isEmpty()) {
            return "Nama dan Nomor Telepon harus diisi!";
        }
        if (!nama.matches("[\\p{L} .'-]{2,100}")) {
            return "Nama hanya boleh huruf (2-100 karakter)!";
        }
        if (!nomor.matches("08\\d{8,11}")) {
            return "Nomor Telepon harus berupa angka, diawali 08, 10-13 digit!";
        }
        return null;
    }

    // Mengembalikan form ke keadaan awal
    private static void resetForm() {
        txtNama.setText("");
        txtNomor.setText("");
        selectedId = -1;
        tabel.clearSelection();
        btnTambah.setEnabled(true);
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    // Membaca data dari database ke tabel, sesuai kata kunci dan kategori
    private static void muatData() {
        String kategori = (String) cmbKategori.getSelectedItem();
        String sql = "SELECT p.id_pelanggan, p.nama, p.nomor_telepon, p.tanggal_daftar, "
            + "COUNT(o.id_pemesanan) AS jumlah, COALESCE(SUM(o.status = 'Aktif'), 0) AS aktif "
            + "FROM pelanggan p LEFT JOIN pemesanan o ON o.id_pelanggan = p.id_pelanggan "
            + "WHERE p.nama LIKE ? OR p.nomor_telepon LIKE ? "
            + "GROUP BY p.id_pelanggan, p.nama, p.nomor_telepon, p.tanggal_daftar ORDER BY p.id_pelanggan";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            String kata = "%" + txtCari.getText().trim() + "%";
            ps.setString(1, kata);
            ps.setString(2, kata);

            sedangMemuat = true;
            model.setRowCount(0);
            int total = 0;
            int aktifCount = 0;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate daftar = rs.getDate("tanggal_daftar").toLocalDate();
                    String status = tentukanStatus(rs.getInt("aktif"), daftar);
                    if (!"Semua Kategori".equals(kategori) && !kategori.equals(status)) {
                        continue;
                    }
                    total++;
                    if (status.equals("Aktif")) {
                        aktifCount++;
                    }
                    model.addRow(new Object[]{rs.getInt("id_pelanggan"), rs.getString("nama"),
                        rs.getString("nomor_telepon"), rs.getInt("jumlah"), status,
                        daftar.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))});
                }
            }
            sedangMemuat = false;
            lblInfo.setText("Ditampilkan " + total + " pelanggan | Status Aktif: " + aktifCount);
            resetForm();
        } catch (SQLException ex) {
            sedangMemuat = false;
            JOptionPane.showMessageDialog(frame, "Gagal memuat data. Pastikan MySQL berjalan dan "
                + "database db_simasif sudah dibuat.\n" + ex.getMessage());
        }
    }

    // CREATE
    private static void tambah() {
        String nama = txtNama.getText().trim();
        String nomor = txtNomor.getText().trim();
        String pesan = validasi(nama, nomor);
        if (pesan != null) {
            JOptionPane.showMessageDialog(frame, pesan);
            return;
        }
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO pelanggan(nama, nomor_telepon, tanggal_daftar) VALUES(?,?,?)")) {
            ps.setString(1, nama);
            ps.setString(2, nomor);
            ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            muatData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, ex.getErrorCode() == 1062
                ? "Nomor Telepon sudah terdaftar!" : "Gagal menambah: " + ex.getMessage());
        }
    }

    // UPDATE
    private static void ubah() {
        String nama = txtNama.getText().trim();
        String nomor = txtNomor.getText().trim();
        String pesan = validasi(nama, nomor);
        if (pesan != null) {
            JOptionPane.showMessageDialog(frame, pesan);
            return;
        }
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE pelanggan SET nama = ?, nomor_telepon = ? WHERE id_pelanggan = ?")) {
            ps.setString(1, nama);
            ps.setString(2, nomor);
            ps.setInt(3, selectedId);
            ps.executeUpdate();
            muatData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, ex.getErrorCode() == 1062
                ? "Nomor Telepon sudah terdaftar!" : "Gagal mengubah: " + ex.getMessage());
        }
    }

    // DELETE
    private static void hapus() {
        int pilih = JOptionPane.showConfirmDialog(frame,
            "Hapus pelanggan " + txtNama.getText() + "?\nData pemesanannya juga ikut terhapus.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?")) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            muatData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Gagal menghapus: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(form_kelola_pelanggan::buatForm);
    }

    private static void buatForm() {
        frame = new JFrame();
        frame.setSize(640, 540);
        frame.setTitle("Form Kelola Pelanggan");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel lblJudul = new JLabel("Data Pelanggan Studio", SwingConstants.CENTER);
        lblJudul.setBounds(0, 15, 630, 25);
        frame.add(lblJudul);

        // Label dan input
        JLabel lblNama = new JLabel("Nama");
        lblNama.setBounds(60, 60, 120, 28);
        frame.add(lblNama);
        txtNama = new JTextField();
        txtNama.setBounds(190, 60, 380, 28);
        frame.add(txtNama);

        JLabel lblNomor = new JLabel("Nomor Telepon");
        lblNomor.setBounds(60, 98, 120, 28);
        frame.add(lblNomor);
        txtNomor = new JTextField();
        txtNomor.setBounds(190, 98, 380, 28);
        frame.add(txtNomor);

        // Tombol CRUD
        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        JButton btnReset = new JButton("Reset");
        btnTambah.setBounds(60, 145, 120, 30);
        btnUbah.setBounds(190, 145, 120, 30);
        btnHapus.setBounds(320, 145, 120, 30);
        btnReset.setBounds(450, 145, 120, 30);
        frame.add(btnTambah);
        frame.add(btnUbah);
        frame.add(btnHapus);
        frame.add(btnReset);

        // Pencarian
        JLabel lblCari = new JLabel("Cari");
        lblCari.setBounds(60, 195, 120, 28);
        frame.add(lblCari);
        txtCari = new JTextField();
        txtCari.setBounds(190, 195, 230, 28);
        frame.add(txtCari);
        cmbKategori = new JComboBox<>(new String[]{"Semua Kategori", "Baru", "Aktif", "Nonaktif"});
        cmbKategori.setBounds(430, 195, 140, 28);
        frame.add(cmbKategori);

        // Tabel (sel tidak bisa diedit langsung, kolom id disembunyikan)
        String[] kolom = {"ID", "Nama", "Nomor", "Pemesanan", "Status", "Terdaftar"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.removeColumn(tabel.getColumnModel().getColumn(0));
        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBounds(60, 240, 510, 190);
        frame.add(scrollPane);

        lblInfo = new JLabel("", SwingConstants.CENTER);
        lblInfo.setBounds(0, 445, 630, 25);
        frame.add(lblInfo);

        // Klik baris: isi input, aktifkan Ubah dan Hapus
        tabel.getSelectionModel().addListSelectionListener(e -> {
            int row = tabel.getSelectedRow();
            if (e.getValueIsAdjusting() || sedangMemuat || row == -1) {
                return;
            }
            selectedId = (int) model.getValueAt(row, 0);
            txtNama.setText(model.getValueAt(row, 1).toString());
            txtNomor.setText(model.getValueAt(row, 2).toString());
            btnTambah.setEnabled(false);
            btnUbah.setEnabled(true);
            btnHapus.setEnabled(true);
        });

        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> resetForm());
        cmbKategori.addActionListener(e -> muatData());
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                muatData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                muatData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                muatData();
            }
        });

        resetForm();
        frame.setVisible(true);
        muatData();
    }
}
