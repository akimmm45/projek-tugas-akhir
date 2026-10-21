package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Diagnosa;
import model.DiagnosaDetail;
import net.miginfocom.swing.MigLayout;
import service.ServiceCF;

public class FormRiwayatDiagnosa extends JPanel implements Form {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final ServiceCF cfService = new ServiceCF();

    private JTextField txtCari;
    private JTable tabel;
    private DefaultTableModel model;
    private JTextArea txtDetail;

    public FormRiwayatDiagnosa(FormManager formManager) {
        // Proporsi tabel 2, detail 1
        setLayout(new MigLayout("insets 24, fill", "[grow 2, fill][grow 1, fill]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Riwayat Diagnosa");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelDetail(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel panelCari = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panelCari.setOpaque(false);
        txtCari = new JTextField();
        txtCari.putClientProperty("JTextField.placeholderText", "Cari nama pasien...");
        panelCari.add(txtCari, "h 32!, growx");
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> muatTabel());
        panelCari.add(btnCari, "h 32!, w 80!");
        panel.add(panelCari, BorderLayout.NORTH);

        // Header: ID, Tanggal, Nama Pasien, Hasil Kategori, CF (%)
        model = new DefaultTableModel(
            new Object[]{"No", "Tanggal", "Nama Pasien", "Hasil Kategori", "CF (%)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        // Lebar kolom
        tabel.getColumnModel().getColumn(0).setMaxWidth(40); // No
        tabel.getColumnModel().getColumn(4).setMaxWidth(70); // CF

        // Rata tengah semua kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Agar kolom menyesuaikan proporsional
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() != -1) {
                tampilkanDetail();
            }
        });
        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatPanelDetail() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 16", "[grow,fill]", "[][grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulDetail = new JLabel("Rincian Gejala Diagnosa Terpilih");
        judulDetail.setFont(judulDetail.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulDetail, "gapbottom 8");

        txtDetail = new JTextArea();
        txtDetail.setEditable(false);
        txtDetail.setLineWrap(true);
        txtDetail.setWrapStyleWord(true);
        txtDetail.setText("Pilih salah satu baris riwayat pada tabel di sebelah kiri untuk melihat rinciannya.");
        panel.add(new JScrollPane(txtDetail), "grow");

        return panel;
    }

    @Override
    public void onShow() {
        muatTabel();
        txtDetail.setText("Pilih salah satu baris riwayat pada tabel di sebelah kiri untuk melihat rinciannya.");
    }

    private void muatTabel() {
        model.setRowCount(0);
        String kataKunci = txtCari.getText().trim().toLowerCase();
        List<Diagnosa> daftar = cfService.getAllRiwayat();
        int no = 1;
        for (Diagnosa d : daftar) {
            if (!kataKunci.isEmpty() && (d.getNamaPasien() == null || !d.getNamaPasien().toLowerCase().contains(kataKunci))) {
                continue;
            }
            model.addRow(new Object[]{
                no++,
                d.getTanggalDiagnosa() != null ? d.getTanggalDiagnosa().format(FORMAT_WAKTU) : "-",
                d.getNamaPasien(),
                d.getNamaHasilPenyakit() != null ? d.getNamaHasilPenyakit() : "-",
                String.format("%.2f", d.getConfidence())
            });
        }
    }

    private void tampilkanDetail() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        // No urut ada di kolom 0, tetapi kita tidak punya ID. Ambil ID dari data asli?
        // Karena kita tidak simpan ID di tabel, perlu ambil dari daftar ulang.
        // Alternatif: simpan ID di hidden atau ambil dari model? Kita akan ambil dari service lagi.
        // Cara praktis: dapatkan nama pasien dan tanggal lalu cari di daftar.
        String tanggal = (String) model.getValueAt(row, 1);
        String namaPasien = (String) model.getValueAt(row, 2);
        String hasilKategori = (String) model.getValueAt(row, 3);

        // Cari diagnosa yang cocok
        List<Diagnosa> semua = cfService.getAllRiwayat();
        Diagnosa target = null;
        for (Diagnosa d : semua) {
            String tglStr = d.getTanggalDiagnosa() != null ? d.getTanggalDiagnosa().format(FORMAT_WAKTU) : "-";
            if (tglStr.equals(tanggal) && namaPasien.equals(d.getNamaPasien()) && hasilKategori.equals(d.getNamaHasilPenyakit())) {
                target = d;
                break;
            }
        }
        if (target == null) {
            txtDetail.setText("Data tidak ditemukan.");
            return;
        }

        int idDiagnosa = target.getIdDiagnosa();
        List<DiagnosaDetail> detailList = cfService.getDetailByDiagnosa(idDiagnosa);

        StringBuilder sb = new StringBuilder();
        sb.append("Hasil Utama: ").append(hasilKategori).append("\n\n");
        sb.append("Gejala yang dipilih saat pemeriksaan:\n");
        for (DiagnosaDetail d : detailList) {
            sb.append("- ").append(d.getNamaGejala())
                    .append(" [").append(d.getKondisiGejala()).append("]\n");
        }

        if (target.getKemungkinanLain() != null && !target.getKemungkinanLain().isBlank()) {
            sb.append("\nKemungkinan kategori lain:\n").append(target.getKemungkinanLain());
        }

        txtDetail.setText(sb.toString());
        txtDetail.setCaretPosition(0);
    }
}