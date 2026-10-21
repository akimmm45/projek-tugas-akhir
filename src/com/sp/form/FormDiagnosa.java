package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import main.Form;
import main.FormManager;
import model.DiagnosaResult;
import model.Gejala;
import model.Pasien;
import net.miginfocom.swing.MigLayout;
import service.ServiceCF;
import service.ServiceGejala;
import service.ServicePasien;

public class FormDiagnosa extends JPanel implements Form {

    private final FormManager formManager;
    private final ServiceCF cfService = new ServiceCF();
    private final ServiceGejala serviceGejala = new ServiceGejala();
    private final ServicePasien servicePasien = new ServicePasien();

    private JComboBox<Pasien> cmbPasien;
    private JPanel panelGejala;
    private JTextArea txtHasil;
    private JButton btnProses, btnSimpan, btnReset;

    private final Map<String, JComboBox<String>> comboGejalaMap = new LinkedHashMap<>();

    public FormDiagnosa(FormManager formManager) {
        this.formManager = formManager;
        setLayout(new MigLayout("insets 24, fill", "[grow,fill][460!]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Diagnosis \u2014 Metode Certainty Factor");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelKiri(), "grow");
        add(buatPanelKanan(), "grow, wrap");
    }

    private JPanel buatPanelKiri() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow,fill]", "[][grow,fill]"));
        panel.setOpaque(false);

        JPanel panelPasien = new JPanel(new MigLayout("insets 14", "[100!][grow,fill]"));
        panelPasien.setBackground(Color.WHITE);
        panelPasien.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        panelPasien.add(new JLabel("Pasien Lansia*"));
        cmbPasien = new JComboBox<>();
        panelPasien.add(cmbPasien, "h 30!");
        panel.add(panelPasien, "growx, gapbottom 10");

        JLabel labelGejala = new JLabel("Centang tingkat keyakinan untuk setiap gejala/faktor risiko yang dialami pasien:");
        panel.add(labelGejala, "gapbottom 6");

        panelGejala = new JPanel();
        panelGejala.setLayout(new MigLayout("wrap 1, insets 12", "[grow,fill]"));
        panelGejala.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(panelGejala);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll, "grow, push");

        return panel;
    }

    private JPanel buatPanelKanan() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 16", "[grow,fill]", "[][grow,fill][]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulHasil = new JLabel("Hasil Perhitungan CF");
        judulHasil.setFont(judulHasil.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulHasil, "gapbottom 8");

        txtHasil = new JTextArea();
        txtHasil.setEditable(false);
        txtHasil.setLineWrap(true);
        txtHasil.setWrapStyleWord(true);
        txtHasil.setFont(txtHasil.getFont().deriveFont(13f));
        txtHasil.setText("Belum ada hasil. Pilih pasien, tentukan gejala, lalu klik \"Proses Diagnosa\".");
        JScrollPane scrollHasil = new JScrollPane(txtHasil);
        panel.add(scrollHasil, "grow");

        btnProses = new JButton("Proses Diagnosa");
        btnProses.putClientProperty("JButton.buttonType", "roundRect");
        btnProses.setBackground(new Color(0x1F3A34));
        btnProses.setForeground(Color.WHITE);
        panel.add(btnProses, "h 38!, gaptop 10");

        btnSimpan = new JButton("Simpan Hasil Diagnosa");
        btnSimpan.setBackground(new Color(0x2E7D6B));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setEnabled(false);
        panel.add(btnSimpan, "h 38!, gaptop 6");

        btnReset = new JButton("Diagnosa Baru (Reset)");
        panel.add(btnReset, "h 34!, gaptop 6");

        return panel;
    }

    private void pasangAksi() {
        btnProses.addActionListener(e -> prosesDiagnosa());
        btnSimpan.addActionListener(e -> simpanHasil());
        btnReset.addActionListener(e -> resetForm());
    }

    @Override
    public void onShow() {
        muatComboPasien();
        muatPanelGejala();
        resetHasil();
    }

    private void muatComboPasien() {
        cmbPasien.removeAllItems();
        for (Pasien p : servicePasien.getAll()) {
            cmbPasien.addItem(p);
        }
    }

    private void muatPanelGejala() {
        panelGejala.removeAll();
        comboGejalaMap.clear();

        List<Gejala> semuaGejala = serviceGejala.getAll();
        String kategoriSebelumnya = null;

        for (Gejala g : semuaGejala) {
            if (!g.getKategoriGejala().equals(kategoriSebelumnya)) {
                JLabel header = new JLabel(g.getKategoriGejala().toUpperCase());
                header.setFont(header.getFont().deriveFont(Font.BOLD, 11f));
                header.setForeground(new Color(0x2E7D6B));
                header.setBorder(BorderFactory.createEmptyBorder(kategoriSebelumnya == null ? 0 : 10, 0, 4, 0));
                panelGejala.add(header);
                kategoriSebelumnya = g.getKategoriGejala();
            }
            panelGejala.add(buatBarisGejala(g));
        }

        panelGejala.revalidate();
        panelGejala.repaint();
    }

    private JPanel buatBarisGejala(Gejala g) {
        JPanel baris = new JPanel(new MigLayout("insets 3 0 3 0", "[grow,fill][170!]"));
        baris.setOpaque(false);

        JLabel label = new JLabel("<html>" + g.getNamaGejala() + "</html>");
        baris.add(label);

        JComboBox<String> combo = new JComboBox<>(ServiceCF.SKALA_CF.keySet().toArray(new String[0]));
        combo.setSelectedItem("Tidak (0.0)");
        baris.add(combo, "h 26!");

        comboGejalaMap.put(g.getKodeGejala(), combo);
        return baris;
    }

    private void prosesDiagnosa() {
        Pasien pasien = (Pasien) cmbPasien.getSelectedItem();
        if (pasien == null) {
            JOptionPane.showMessageDialog(this, "Pilih pasien lansia terlebih dahulu!\n"
                    + "Jika belum terdaftar, tambahkan dulu lewat menu Data Lansia.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Double> selectedGejala = new LinkedHashMap<>();
        for (Map.Entry<String, JComboBox<String>> entry : comboGejalaMap.entrySet()) {
            String labelTerpilih = (String) entry.getValue().getSelectedItem();
            double nilaiCf = ServiceCF.SKALA_CF.getOrDefault(labelTerpilih, 0.0);
            if (nilaiCf > 0) {
                selectedGejala.put(entry.getKey(), nilaiCf);
            }
        }

        if (selectedGejala.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu gejala/faktor risiko yang dialami pasien!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<DiagnosaResult> results = cfService.hitungDiagnosa(selectedGejala);

        if (results.isEmpty()) {
            txtHasil.setText("Berdasarkan gejala yang dipilih, sistem belum dapat menyimpulkan kategori "
                    + "hipertensi tertentu. Coba lengkapi kembali data hasil pemeriksaan tekanan darah.");
            btnSimpan.setEnabled(false);
            txtHasil.putClientProperty("results", null);
            txtHasil.putClientProperty("selectedGejala", null);
            return;
        }

        txtHasil.setText(formatHasil(pasien, results));
        txtHasil.setCaretPosition(0);

        // Simpan hasil perhitungan sementara pada komponen txtHasil, dipakai
        // kembali oleh simpanHasil() saat tombol "Simpan Hasil Diagnosa" ditekan.
        txtHasil.putClientProperty("results", results);
        txtHasil.putClientProperty("selectedGejala", selectedGejala);
        btnSimpan.setEnabled(true);
    }

    private String formatHasil(Pasien pasien, List<DiagnosaResult> results) {
        DiagnosaResult top = results.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("Pasien : ").append(pasien.getNama()).append(" (").append(pasien.getUmur()).append(" tahun)\n\n");
        sb.append("HASIL DIAGNOSA:\n");
        sb.append(top.getNamaPenyakit()).append("\n");
        sb.append("Tingkat Keyakinan (CF) : ").append(String.format("%.2f", top.getPersen())).append(" %\n\n");

        if (top.getDeskripsi() != null && !top.getDeskripsi().isBlank()) {
            sb.append("Deskripsi:\n").append(top.getDeskripsi()).append("\n\n");
        }
        if (top.getSolusi() != null && !top.getSolusi().isBlank()) {
            sb.append("Solusi/Penanganan:\n").append(top.getSolusi()).append("\n\n");
        }
        if (top.getPencegahan() != null && !top.getPencegahan().isBlank()) {
            sb.append("Pencegahan:\n").append(top.getPencegahan()).append("\n\n");
        }

        if (results.size() > 1) {
            sb.append("Kemungkinan lain:\n");
            for (int i = 1; i < results.size(); i++) {
                DiagnosaResult r = results.get(i);
                sb.append("- ").append(r.getNamaPenyakit()).append(" (Akurasi: ")
                        .append(String.format("%.2f", r.getPersen())).append(" %)\n");
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void simpanHasil() {
        List<DiagnosaResult> results = (List<DiagnosaResult>) txtHasil.getClientProperty("results");
        Map<String, Double> selectedGejala = (Map<String, Double>) txtHasil.getClientProperty("selectedGejala");
        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada hasil diagnosa untuk disimpan!");
            return;
        }

        Pasien pasien = (Pasien) cmbPasien.getSelectedItem();
        if (pasien == null) {
            return;
        }

        int idUserAktif = formManager.getUserAktif() != null ? formManager.getUserAktif().getIdUser() : 1;

        // Simpan ke database melalui service
        boolean saved = cfService.simpanDiagnosa(pasien.getIdPasien(), results, selectedGejala, idUserAktif);
        if (saved) {
            JOptionPane.showMessageDialog(this, "Hasil diagnosa berhasil disimpan!");
            btnSimpan.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan diagnosa!");
        }
    }

    private void resetForm() {
        for (JComboBox<String> combo : comboGejalaMap.values()) {
            combo.setSelectedItem("Tidak (0.0)");
        }
        if (cmbPasien.getItemCount() > 0) {
            cmbPasien.setSelectedIndex(0);
        }
        resetHasil();
    }

    private void resetHasil() {
        txtHasil.setText("Belum ada hasil. Pilih pasien, tentukan gejala, lalu klik \"Proses Diagnosa\".");
        txtHasil.putClientProperty("results", null);
        txtHasil.putClientProperty("selectedGejala", null);
        btnSimpan.setEnabled(false);
    }
}
