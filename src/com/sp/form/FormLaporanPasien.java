package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Pasien;
import service.ServicePasien;

public class FormLaporanPasien extends JPanel implements Form {

    private final ServicePasien servicePasien = new ServicePasien();
    private JPanel panelKertas;
    private DefaultTableModel model;

    public FormLaporanPasien(FormManager formManager) {
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
        panelKertas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panelKertas.add(LaporanHelper.buatKopSurat("Laporan Data Lansia"), BorderLayout.NORTH);
        panelKertas.add(LaporanHelper.bungkusTabelUntukCetak(tabel), BorderLayout.CENTER);
        panelKertas.add(LaporanHelper.buatBlokTandaTangan("Admin Posyandu Melati"), BorderLayout.SOUTH);

        add(toolbar, BorderLayout.NORTH);
        add(LaporanHelper.bungkusHalaman(panelKertas), BorderLayout.CENTER);
    }

    private JTable buatTabel() {
        model = new DefaultTableModel(new Object[]{"No", "NIK", "Nama", "Umur", "Jenis Kelamin", "Alamat", "No. HP"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        // Ukuran kolom disesuaikan
        tabel.getColumnModel().getColumn(0).setMaxWidth(40);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(130); // NIK
        tabel.getColumnModel().getColumn(2).setPreferredWidth(160); // Nama
        tabel.getColumnModel().getColumn(3).setMaxWidth(50);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(50);
        tabel.getColumnModel().getColumn(4).setMaxWidth(100);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(100);
        // Kolom Alamat dan No. HP akan menyesuaikan sisa ruang

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
        List<Pasien> daftar = servicePasien.getAll();
        int no = 1;
        for (Pasien p : daftar) {
            model.addRow(new Object[]{
                no++, p.getNik(), p.getNama(), p.getUmur(), p.getJenisKelamin(),
                p.getAlamat(), p.getNoHp()
            });
        }
    }

    @Override
    public void onShow() {
        muatData();
    }
}