package com.sp.form;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import main.MainForm;
import model.User;
import net.miginfocom.swing.MigLayout;
import service.ServiceUser;

public class FormLogin extends JFrame {

    private static final Color WARNA_BRAND = new Color(0x1F3A34);
    private static final Color WARNA_ACCENT = new Color(0x2E7D6B);

    private final ServiceUser serviceUser = new ServiceUser();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public FormLogin() {
        setTitle("Login - Sistem Pakar Diagnosis Dini Hipertensi pada Lansia");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(880, 520);
        setMinimumSize(new Dimension(760, 480));
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new MigLayout("insets 0, fill", "[40%!][grow,fill]", "[grow,fill]"));
        add(buatPanelBrand(), "grow");
        add(buatPanelForm(), "grow");

        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel buatPanelBrand() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 30, align center center"));
        panel.setBackground(WARNA_BRAND);

        JLabel logo = new JLabel(muatLogo());
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(logo, "align center, gapbottom 16");

        JLabel judul = new JLabel("<html><div style='text-align:center;color:white;'>"
                + "<b style='font-size:16px;'>POSYANDU MELATI</b><br><br>"
                + "Sistem Pakar Diagnosis Dini<br>Hipertensi pada Lansia<br>"
                + "<span style='font-size:11px;'>Metode Certainty Factor</span></div></html>");
        panel.add(judul, "align center");

        return panel;
    }

    private ImageIcon muatLogo() {
        try {
            java.net.URL url = getClass().getClassLoader().getResource("img/Logo.png");
            if (url != null) {
                ImageIcon asli = new ImageIcon(url);
                java.awt.Image scaled = asli.getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("Logo tidak ditemukan: " + e.getMessage());
        }
        return new ImageIcon();
    }

    private JPanel buatPanelForm() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 50 60 50 60, align center center", "[grow,fill]"));
        panel.setBackground(Color.WHITE);

        JLabel judul = new JLabel("Masuk ke Aplikasi");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(judul, "gapbottom 4");

        JLabel subJudul = new JLabel("Silakan login menggunakan akun petugas/kader Anda.");
        subJudul.setForeground(new Color(0x757575));
        panel.add(subJudul, "gapbottom 24");

        panel.add(new JLabel("Username"), "gapbottom 4");
        txtUsername = new JTextField();
        txtUsername.putClientProperty("JTextField.leadingIcon", new FlatSVGIcon("icon/username.svg", 16, 16));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");
        panel.add(txtUsername, "h 38!, gapbottom 14");

        panel.add(new JLabel("Password"), "gapbottom 4");
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.leadingIcon", new FlatSVGIcon("icon/password.svg", 16, 16));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        panel.add(txtPassword, "h 38!, gapbottom 22");

        btnLogin = new JButton("LOGIN");
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        btnLogin.setBackground(WARNA_ACCENT);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(btnLogin.getFont().deriveFont(Font.BOLD, 13f));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> prosesLogin());
        panel.add(btnLogin, "h 40!, gapbottom 10");

        JLabel hint = new JLabel("<html><i>Akun default: admin / admin123</i></html>");
        hint.setForeground(new Color(0xAAAAAA));
        hint.setFont(hint.getFont().deriveFont(11f));
        panel.add(hint);

        return panel;
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");

        User user = serviceUser.login(username, password);

        btnLogin.setEnabled(true);
        btnLogin.setText("LOGIN");

        if (user != null) {
            dispose();
            new MainForm(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah!",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        main.Main.main(args);
    }
}
