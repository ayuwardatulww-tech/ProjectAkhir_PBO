package id.ac.polinema.frontend;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class form_login {
    public static void main(String[] args) {
        JFrame frame = new JFrame("SIMASIF - Login");
        frame.setSize(440, 430);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(245, 247, 250));
        frame.setLayout(null);

        JPanel card = new JPanel();
        card.setBounds(50, 35, 340, 330);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 232)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.setLayout(null);
        frame.add(card);

        JLabel lblJudul = new JLabel("SIMASIF", SwingConstants.CENTER);
        lblJudul.setBounds(25, 15, 290, 35);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblJudul.setForeground(new Color(41, 98, 255));
        card.add(lblJudul);

        JLabel lblSubJudul = new JLabel("Silakan masuk ke akun Anda", SwingConstants.CENTER);
        lblSubJudul.setBounds(25, 50, 290, 25);
        lblSubJudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubJudul.setForeground(new Color(110, 115, 125));
        card.add(lblSubJudul);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(25, 90, 290, 20);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblUsername);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(25, 112, 290, 38);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 196, 205)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(25, 160, 290, 20);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(25, 182, 290, 38);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 196, 205)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        card.add(txtPassword);

        JCheckBox chkPassword = new JCheckBox("Tampilkan password");
        chkPassword.setBounds(20, 225, 180, 24);
        chkPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkPassword.setBackground(Color.WHITE);
        chkPassword.setForeground(new Color(90, 95, 105));
        card.add(chkPassword);

        chkPassword.addActionListener(e -> {
            txtPassword.setEchoChar(chkPassword.isSelected() ? (char) 0 : '•');
        });

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(25, 260, 290, 42);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 98, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        card.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Username dan password wajib diisi.",
                    "Login Gagal",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(frame,
                "Login berhasil untuk pengguna: " + username,
                "Login Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
        });

        frame.getRootPane().setDefaultButton(btnLogin);
        frame.setVisible(true);
    }
}
