package id.ac.polinema.frontend;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class form_upload_fotografi {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(400, 360);
        frame.setTitle("SIMASIF Form Upload File Fotografi");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);


        //kotakan info pesanan
        JPanel Info = new JPanel();
        Info.setLayout(null);
        Info.setBounds(30, 20, 325, 75);
        Info.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        frame.add(Info);

        JLabel IbKodePesanan = new JLabel("Kode Pesanan : SS-S399WF");
        IbKodePesanan.setBounds(12, 8, 300, 20);
        Info.add(IbKodePesanan);

        JLabel IbNamaPelanggan = new JLabel("Pelanggan : Ayu Wardatul Widad");
        IbNamaPelanggan.setBounds(12, 28, 300, 20);
        Info.add(IbNamaPelanggan);

        JLabel IbLayanan = new JLabel("Layanan : Self Studio");
        IbLayanan.setBounds(12, 48, 300, 20);
        Info.add(IbLayanan);



        //button upload file foto
        JLabel gUp = new JLabel("File Foto");
        gUp.setBounds(30, 100, 325, 25);
        frame.add(gUp);

        JButton btnBrowser = new JButton("Browse");
        btnBrowser.setBounds(30, 130, 100, 30);
        frame.add(btnBrowser);

        JTextField txtFoto = new JTextField("No file selected");
        txtFoto.setBounds(135, 130, 220, 30);
        txtFoto.setEditable(false);
        frame.add(txtFoto);

        //input catatan
        JLabel IbCatatan = new JLabel("Catatan (Opsional)");
        IbCatatan.setBounds(30, 180, 325, 25);
        frame.add(IbCatatan);

        JTextField txtCatatan = new JTextField();
        txtCatatan.setBounds(30, 210, 325, 30);
        frame.add(txtCatatan);

        //button kirim file foto
        JButton btnUpload = new JButton("Upload File Foto");
        btnUpload.setBounds(30, 260, 325, 35);

        frame.add(btnUpload);

        frame.setVisible(true);
    }
}