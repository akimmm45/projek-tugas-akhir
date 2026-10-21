package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Diagnosa;
import service.ServiceCF;

/** Laporan cetak seluruh riwayat hasil diagnosis yang pernah disimpan sistem. */
public class FormLaporanDiagnosa extends JPanel implements Form {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final ServiceCF serviceCF = new ServiceCF();
    private JPanel panelKertas;
    private DefaultTableModel model;

    public FormLaporanDiagnosa(FormManager formManager) {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF4F7F6));
        initComponents();
        muatData();
    }

    private void initComponents() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setOpaque(false);
        JButton btnCetak = new JButton("Cetak");
        btnCetak.addActionListener(e -> LaporanHelper.cetak(panelKertas));
        toolbar.add(btnCetak);

        JTable tabel = buatTabel();

        panelKertas = new JPanel(new BorderLayout());
        panelKertas.add(LaporanHelper.buatKopSurat("Laporan Hasil Diagnosis"), BorderLayout.NORTH);
        panelKertas.add(LaporanHelper.bungkusTabelUntukCetak(tabel), BorderLayout.CENTER);
        panelKertas.add(LaporanHelper.buatBlokTandaTangan("Admin Posyandu Melati"), BorderLayout.SOUTH);

        add(toolbar, BorderLayout.NORTH);
        add(LaporanHelper.bungkusHalaman(panelKertas), BorderLayout.CENTER);
    }

    private JTable buatTabel() {
        model = new DefaultTableModel(new Object[]{"No", "Tanggal", "Nama Pasien", "Hasil Kategori", "CF (%)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        tabel.getColumnModel().getColumn(0).setMaxWidth(40);
        tabel.getColumnModel().getColumn(4).setMaxWidth(70);

        // Rata tengah semua kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return tabel;
    }

    private void muatData() {
        model.setRowCount(0);
        List<Diagnosa> daftar = serviceCF.getAllRiwayat();
        int no = 1;
        for (Diagnosa d : daftar) {
            model.addRow(new Object[]{
                no++,
                d.getTanggalDiagnosa() != null ? d.getTanggalDiagnosa().format(FORMAT_WAKTU) : "-",
                d.getNamaPasien(),
                d.getNamaHasilPenyakit() != null ? d.getNamaHasilPenyakit() : "-",
                String.format("%.2f", d.getConfidence())
            });
        }
    }

    @Override
    public void onShow() {
        muatData();
    }
}