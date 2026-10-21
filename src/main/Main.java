package main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import service.ServiceUser;

/**
 * Titik masuk aplikasi Sistem Pakar Diagnosis Dini Hipertensi pada Lansia
 * di Posyandu Lansia Melati (Metode Certainty Factor).
 */
public class Main {

    public static void main(String[] args) {
        // Daftarkan folder "themes" sebagai sumber file kustomisasi tampilan
        // FlatLaf (themes/FlatLaf.properties) SEBELUM look-and-feel dipasang.
        FlatLaf.registerCustomDefaultsSource("themes");

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Gagal memasang FlatLaf, memakai tampilan bawaan Java: " + e.getMessage());
        }

        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);

        // Pastikan minimal ada satu akun admin yang bisa dipakai login.
        new ServiceUser().pastikanAdminDefaultTersedia();

        SwingUtilities.invokeLater(() -> new com.sp.form.FormLogin().setVisible(true));
    }
}
