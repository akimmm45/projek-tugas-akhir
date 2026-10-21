package com.sp.form;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import main.AllForms;
import main.Form;
import main.FormManager;
import net.miginfocom.swing.MigLayout;
import service.ServiceCF;
import service.ServicePasien;

public class FormDashboard extends JPanel implements Form {

    private final FormManager formManager;
    private final ServicePasien servicePasien = new ServicePasien();
    private final ServiceCF serviceCF = new ServiceCF();

    private JPanel panelKartu;
    private JPanel panelStatistik;

    public FormDashboard(FormManager formManager) {
        this.formManager = formManager;
        setLayout(new MigLayout("wrap 1, insets 24, fillx", "[grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Dashboard");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "gapbottom 2");

        JLabel sub = new JLabel("Ringkasan kegiatan skrining hipertensi Posyandu Melati.");
        sub.setForeground(new Color(0x757575));
        add(sub, "gapbottom 16");

        panelKartu = new JPanel(new MigLayout("insets 0, gap 16", "[grow,fill][grow,fill][grow,fill]"));
        panelKartu.setOpaque(false);
        add(panelKartu, "growx, gapbottom 16");

        JButton btnDiagnosaBaru = new JButton("+ Mulai Diagnosa Baru");
        btnDiagnosaBaru.putClientProperty("JButton.buttonType", "roundRect");
        btnDiagnosaBaru.setBackground(new Color(0x2E7D6B));
        btnDiagnosaBaru.setForeground(Color.WHITE);
        btnDiagnosaBaru.addActionListener(e -> formManager.tampilkan(AllForms.DIAGNOSA));
        add(btnDiagnosaBaru, "align left, h 38!, gapbottom 16");

        JLabel judulStat = new JLabel("Distribusi Hasil Diagnosa per Kategori");
        judulStat.setFont(judulStat.getFont().deriveFont(Font.BOLD, 14f));
        add(judulStat, "gapbottom 8");

        panelStatistik = new JPanel(new MigLayout("wrap 1, insets 16", "[grow,fill]"));
        panelStatistik.setBackground(Color.WHITE);
        panelStatistik.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        add(panelStatistik, "grow, push");
    }

    @Override
    public void onShow() {
        muatKartuRingkasan();
        muatStatistikKategori();
    }

    private void muatKartuRingkasan() {
        panelKartu.removeAll();
        panelKartu.add(buatKartu("Lansia Terdaftar", String.valueOf(servicePasien.jumlahPasien()), new Color(0x2E7D6B)));
        panelKartu.add(buatKartu("Diagnosa Hari Ini", String.valueOf(serviceCF.getJumlahDiagnosaHariIni()), new Color(0x1F3A34)));
        panelKartu.add(buatKartu("Diagnosa Bulan Ini", String.valueOf(serviceCF.getJumlahDiagnosaBulanIni()), new Color(0x607D8B)));
        panelKartu.revalidate();
        panelKartu.repaint();
    }

    private JPanel buatKartu(String label, String nilai, Color warna) {
        JPanel kartu = new JPanel(new MigLayout("wrap 1, insets 18", "[grow,fill]"));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0)),
                BorderFactory.createMatteBorder(0, 4, 0, 0, warna)));

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(lblNilai.getFont().deriveFont(Font.BOLD, 28f));
        lblNilai.setForeground(warna);
        kartu.add(lblNilai);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(new Color(0x757575));
        kartu.add(lblLabel);

        return kartu;
    }

    private void muatStatistikKategori() {
        panelStatistik.removeAll();
        Map<String, Integer> data = serviceCF.getJumlahDiagnosaPerKategori();

        if (data.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada data diagnosa yang tersimpan.");
            kosong.setForeground(new Color(0x9E9E9E));
            kosong.setHorizontalAlignment(SwingConstants.CENTER);
            panelStatistik.add(kosong, "align center, gaptop 20, gapbottom 20");
        } else {
            int maksimum = data.values().stream().max(Integer::compareTo).orElse(1);
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                JPanel baris = new JPanel(new MigLayout("insets 4 0 4 0", "[150!][grow,fill][40!]"));
                baris.setOpaque(false);
                baris.add(new JLabel(entry.getKey()));

                JProgressBar bar = new JProgressBar(0, maksimum);
                bar.setValue(entry.getValue());
                bar.setStringPainted(false);
                baris.add(bar, "h 14!");

                baris.add(new JLabel(String.valueOf(entry.getValue())), "align center");
                panelStatistik.add(baris, "growx");
            }
        }
        panelStatistik.revalidate();
        panelStatistik.repaint();
    }
}
