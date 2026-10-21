package main;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import config.Koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import model.User;
import net.miginfocom.swing.MigLayout;

/**
 * Jendela utama aplikasi (tampil setelah login berhasil). Terdiri dari
 * header atas, sidebar menu di kiri (Menu), dan area konten di tengah yang
 * dikelola oleh FormManager memakai CardLayout.
 */
public class MainForm extends JFrame {

    private final FormManager formManager;

    public MainForm(User userLogin) {
        setTitle("Sistem Pakar Diagnosis Dini Hipertensi pada Lansia - Posyandu Lansia Melati");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        JPanel panelKonten = new JPanel();
        formManager = new FormManager(panelKonten);
        formManager.setUserAktif(userLogin);

        Menu menu = new Menu(formManager);

        setLayout(new BorderLayout());
        add(buatHeader(userLogin), BorderLayout.NORTH);
        add(menu, BorderLayout.WEST);
        add(panelKonten, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Koneksi.closeConnection();
            }
        });

        formManager.tampilkan(AllForms.DASHBOARD);
    }

    private JPanel buatHeader(User user) {
        JPanel header = new JPanel(new MigLayout("insets 10 20 10 20", "[grow][]", "[]"));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));

        JLabel judul = new JLabel("Diagnosis Dini Hipertensi pada Lansia \u2014 Metode Certainty Factor");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 15f));
        header.add(judul, "growx");

        JPanel kananPanel = new JPanel(new MigLayout("insets 0", "[][]"));
        kananPanel.setOpaque(false);

        JLabel labelUser = new JLabel(user.getNamaLengkap() + " (" + user.getLevel() + ")", SwingConstants.RIGHT);
        kananPanel.add(labelUser);

        JButton btnLogout = new JButton("Keluar", new FlatSVGIcon("icon/logout.svg", 16, 16));
        btnLogout.addActionListener(e -> logout());
        kananPanel.add(btnLogout);

        header.add(kananPanel, "align right");
        return header;
    }

    private void logout() {
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Keluar dari aplikasi dan kembali ke halaman login?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            dispose();
            new com.sp.form.FormLogin().setVisible(true);
        }
    }

    public FormManager getFormManager() {
        return formManager;
    }
}
