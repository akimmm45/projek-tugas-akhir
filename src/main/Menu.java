package main;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

/**
 * Panel menu (sidebar) di sisi kiri MainForm. Setiap tombol memanggil
 * {@link FormManager#tampilkan(AllForms)} untuk menukar panel konten yang
 * sedang tampil.
 */
public class Menu extends JPanel {

    private static final Color WARNA_SIDEBAR = new Color(0x1F3A34);
    private static final Color WARNA_TEKS = Color.WHITE;
    private static final Color WARNA_HOVER = new Color(0x2E7D6B);

    private final FormManager formManager;

    public Menu(FormManager formManager) {
        this.formManager = formManager;
        setBackground(WARNA_SIDEBAR);
        setPreferredSize(new Dimension(230, 0));
        setLayout(new MigLayout("wrap 1, insets 16 12 16 12, gapy 4", "[grow, fill]"));
        initComponents();
    }

    private void initComponents() {
        JLabel judul = new JLabel("<html><b>POSYANDU MELATI<br></b></html>");
        judul.setForeground(WARNA_TEKS);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 16f));
        judul.setHorizontalAlignment(SwingConstants.CENTER);
        judul.setBorder(BorderFactory.createEmptyBorder(4, 4, 16, 4));
        add(judul);

        add(tombolMenu("Dashboard", AllForms.DASHBOARD));
        add(tombolMenu("Pasien", AllForms.PASIEN));
        add(tombolMenu("Diagnosis", AllForms.DIAGNOSA));
        add(tombolMenu("Riwayat Diagnosis", AllForms.RIWAYAT));
        
        add(pemisah("Basis Pengetahuan (CF)"));
        add(tombolMenu("Gejala", AllForms.GEJALA));
        add(tombolMenu("Kategori Hipertensi", AllForms.PENYAKIT));
        add(tombolMenu("Aturan Certainty Factor", AllForms.ATURAN_CF));


        add(pemisah("Laporan"));
        add(tombolMenu("Data Lansia", AllForms.LAPORAN_PASIEN));
        add(tombolMenu("Data Gejala", AllForms.LAPORAN_GEJALA));
        add(tombolMenu("Hasil Diagnosis", AllForms.LAPORAN_DIAGNOSA));
        add(tombolMenu("Rekap Bulanan", AllForms.LAPORAN_REKAP));

      
        add(pemisah("Pengaturan"));
        add(tombolMenu("Manajemen User", AllForms.USER));
    }

    private JLabel pemisah(String teks) {
        JLabel label = new JLabel(teks.toUpperCase());
        label.setForeground(new Color(0xA9C6BE));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setBorder(BorderFactory.createEmptyBorder(14, 6, 4, 0));
        return label;
    }

    private JButton tombolMenu(String label, AllForms target) {
        JButton btn = new JButton(label);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(WARNA_TEKS);
        btn.setBackground(WARNA_SIDEBAR);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setIcon(muatIkon(target.getIconPath()));
        btn.setIconTextGap(10);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(WARNA_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(WARNA_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            formManager.tampilkan(target);
            SwingUtilities.getWindowAncestor(this).repaint();
        });
        return btn;
    }

   private FlatSVGIcon muatIkon(String path) {
    if (path == null || path.isBlank()) {
        return null;
    }
    try {
        FlatSVGIcon icon = new FlatSVGIcon(path, 18, 18);
        if (!icon.hasFound()) {
            System.out.println("Ikon tidak ditemukan: " + path);
            return null;
        }
        return icon;
    } catch (Exception e) {
        return null;
    }
}
}