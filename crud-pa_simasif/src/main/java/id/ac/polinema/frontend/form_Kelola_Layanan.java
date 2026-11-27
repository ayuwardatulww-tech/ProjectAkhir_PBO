package id.ac.polinema.frontend;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class form_Kelola_Layanan {
    static DefaultTableModel model;
    static JTable tabel;
    static int selectedRow = -1; // atribut kelas, supaya bisa dipakai di lambda

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(520, 640);
        frame.setTitle("Kelola Layanan");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(null);

        // Label
        String[] teksLabel = {"Nama Layanan *", "Harga (Rp) *", "Durasi (menit) *",
            "Hasil (hari), boleh kosong", "Maks. orang, boleh kosong", "Poin hasil layanan"};
        int[] posisiY = {20, 55, 120, 155, 190, 225};
        for (int i = 0; i < teksLabel.length; i++) {
            JLabel label = new JLabel(teksLabel[i]);
            label.setBounds(30, posisiY[i], 180, 25);
            frame.add(label);
        }

        // Input
        JTextField txtNama = new JTextField();
        txtNama.setBounds(220, 20, 250, 25);
        frame.add(txtNama);

        JTextField txtHarga = new JTextField();
        txtHarga.setBounds(220, 55, 250, 25);
        frame.add(txtHarga);

        JCheckBox chkMulai = new JCheckBox("Harga \"Mulai dari\"");
        chkMulai.setBounds(220, 85, 250, 25);
        frame.add(chkMulai);

        JTextField txtDurasi = new JTextField();
        txtDurasi.setBounds(220, 120, 250, 25);
        frame.add(txtDurasi);

        JTextField txtHasil = new JTextField();
        txtHasil.setBounds(220, 155, 250, 25);
        frame.add(txtHasil);

        JTextField txtMaks = new JTextField();
        txtMaks.setBounds(220, 190, 250, 25);
        frame.add(txtMaks);

        JTextArea txtPoin = new JTextArea();
        JScrollPane scrollPoin = new JScrollPane(txtPoin);
        scrollPoin.setBounds(220, 225, 250, 80);
        frame.add(scrollPoin);

        JCheckBox chkAktif = new JCheckBox("Tampilkan pada layanan publik", true);
        chkAktif.setBounds(220, 315, 250, 25);
        frame.add(chkAktif);

        // Tombol CRUD
        JButton btnCreate = new JButton("Create");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        btnCreate.setBounds(30, 355, 100, 30);
        btnUpdate.setBounds(140, 355, 100, 30);
        btnDelete.setBounds(250, 355, 100, 30);

        frame.add(btnCreate);
        frame.add(btnUpdate);
        frame.add(btnDelete);

        // Tabel (sel tidak bisa diedit langsung)
        String[] kolom = {"Nama", "Harga", "Mulai", "Durasi", "Hasil", "Maks", "Poin", "Publik"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // bisa digeser ke samping

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBounds(30, 400, 330, 170);
        frame.add(scrollPane);

        // Tombol Edit dan View
        JButton btnEdit = new JButton("Edit");
        JButton btnView = new JButton("View");

        btnEdit.setBounds(370, 400, 100, 30);
        btnView.setBounds(370, 440, 100, 30);

        frame.add(btnEdit);
        frame.add(btnView);

        // Mengembalikan form ke keadaan awal
        Runnable resetForm = () -> {
            txtNama.setText("");
            txtHarga.setText("");
            txtDurasi.setText("");
            txtHasil.setText("");
            txtMaks.setText("");
            txtPoin.setText("");
            chkMulai.setSelected(false);
            chkAktif.setSelected(true);
            selectedRow = -1;
            tabel.clearSelection();
            btnCreate.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        };

        // CREATE
        btnCreate.addActionListener(e -> {
            String pesan = validasi(txtNama.getText(), txtHarga.getText(), txtDurasi.getText(),
                    txtHasil.getText(), txtMaks.getText());
            if (pesan != null) {
                JOptionPane.showMessageDialog(frame, pesan);
                return;
            }

            model.addRow(new Object[]{txtNama.getText().trim(), txtHarga.getText().trim(),
                yaTidak(chkMulai.isSelected()), txtDurasi.getText().trim(), txtHasil.getText().trim(),
                txtMaks.getText().trim(), txtPoin.getText().trim(), yaTidak(chkAktif.isSelected())});
            resetForm.run();
        });

        // VIEW
        btnView.addActionListener(e -> {
            int row = tabel.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Pilih dulu satu baris di tabel!");
                return;
            }

            StringBuilder detail = new StringBuilder();
            for (int i = 0; i < model.getColumnCount(); i++) {
                detail.append(model.getColumnName(i)).append(" : ").append(model.getValueAt(row, i)).append("\n");
            }
            JOptionPane.showMessageDialog(frame, detail.toString(), "Detail Data",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // EDIT: isi kolom input dari baris yang dipilih
        btnEdit.addActionListener(e -> {
            int row = tabel.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Pilih dulu satu baris di tabel!");
                return;
            }

            selectedRow = row;
            txtNama.setText(model.getValueAt(row, 0).toString());
            txtHarga.setText(model.getValueAt(row, 1).toString());
            chkMulai.setSelected(model.getValueAt(row, 2).equals("Ya"));
            txtDurasi.setText(model.getValueAt(row, 3).toString());
            txtHasil.setText(model.getValueAt(row, 4).toString());
            txtMaks.setText(model.getValueAt(row, 5).toString());
            txtPoin.setText(model.getValueAt(row, 6).toString());
            chkAktif.setSelected(model.getValueAt(row, 7).equals("Ya"));

            btnCreate.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {
            String pesan = validasi(txtNama.getText(), txtHarga.getText(), txtDurasi.getText(),
                    txtHasil.getText(), txtMaks.getText());
            if (pesan != null) {
                JOptionPane.showMessageDialog(frame, pesan);
                return;
            }

            Object[] data = {txtNama.getText().trim(), txtHarga.getText().trim(),
                yaTidak(chkMulai.isSelected()), txtDurasi.getText().trim(), txtHasil.getText().trim(),
                txtMaks.getText().trim(), txtPoin.getText().trim(), yaTidak(chkAktif.isSelected())};
            for (int i = 0; i < data.length; i++) {
                model.setValueAt(data[i], selectedRow, i);
            }
            resetForm.run();
        });

        // DELETE
        btnDelete.addActionListener(e -> {
            model.removeRow(selectedRow);
            resetForm.run();
        });

        frame.setVisible(true);
    }

    // Mengembalikan pesan kesalahan, atau null jika semua isian valid
    static String validasi(String nama, String harga, String durasi, String hasil, String maks) {
        if (nama.trim().isEmpty() || harga.trim().isEmpty() || durasi.trim().isEmpty()) {
            return "Nama, Harga, dan Durasi harus diisi!";
        }
        if (!angka(harga) || !angka(durasi)) {
            return "Harga dan Durasi harus berupa angka!";
        }
        if (!hasil.trim().isEmpty() && !angka(hasil) || !maks.trim().isEmpty() && !angka(maks)) {
            return "Hasil dan Maks. orang harus berupa angka!";
        }
        return null;
    }

    // Hanya menerima bilangan bulat tidak negatif
    static boolean angka(String teks) {
        try {
            return Integer.parseInt(teks.trim()) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    static String yaTidak(boolean nilai) {
        return nilai ? "Ya" : "Tidak";
    }
}
    



