package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Diagnosa;
import service.ServiceCF;

/**
 * Rekap jumlah diagnosis per bulan, dikelompokkan per kategori hasil.
 */
public class FormLaporanRekap extends JPanel implements Form {

    private static final DateTimeFormatter FORMAT_BULAN =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

    private final ServiceCF serviceCF = new ServiceCF();
    private JPanel panelKertas;
    private DefaultTableModel model;
    private JLabel lblTotal;

    public FormLaporanRekap(FormManager formManager) {
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

        lblTotal = new JLabel("Total: 0 diagnosis");
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.PLAIN, 12f));
        lblTotal.setForeground(new Color(0x777777));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel panelTabelDanTotal = new JPanel(new BorderLayout());
        panelTabelDanTotal.setOpaque(false);
        panelTabelDanTotal.add(LaporanHelper.bungkusTabelUntukCetak(tabel), BorderLayout.CENTER);
        panelTabelDanTotal.add(lblTotal, BorderLayout.SOUTH);

        panelKertas = new JPanel(new BorderLayout());
        panelKertas.add(LaporanHelper.buatKopSurat("Rekap Bulanan Hasil Diagnosis"), BorderLayout.NORTH);
        panelKertas.add(panelTabelDanTotal, BorderLayout.CENTER);
        panelKertas.add(LaporanHelper.buatBlokTandaTangan("Admin Posyandu Melati"), BorderLayout.SOUTH);

        add(toolbar, BorderLayout.NORTH);
        add(LaporanHelper.bungkusHalaman(panelKertas), BorderLayout.CENTER);
    }

    private JTable buatTabel() {
        model = new DefaultTableModel(new Object[]{"No", "Bulan", "Kategori Hasil", "Jumlah Diagnosis"}, 0) {
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

        Map<YearMonth, Map<String, Integer>> rekap = new TreeMap<>();
        int total = 0;
        for (Diagnosa d : daftar) {
            if (d.getTanggalDiagnosa() == null) {
                continue;
            }
            YearMonth bulan = YearMonth.from(d.getTanggalDiagnosa());
            String kategori = d.getNamaHasilPenyakit() != null ? d.getNamaHasilPenyakit() : "Tidak diketahui";
            rekap.computeIfAbsent(bulan, k -> new TreeMap<>()).merge(kategori, 1, Integer::sum);
            total++;
        }

        int no = 1;
        for (Map.Entry<YearMonth, Map<String, Integer>> entriBulan : rekap.entrySet()) {
            String labelBulan = kapital(entriBulan.getKey().format(FORMAT_BULAN));
            for (Map.Entry<String, Integer> entriKategori : entriBulan.getValue().entrySet()) {
                model.addRow(new Object[]{no++, labelBulan, entriKategori.getKey(), entriKategori.getValue()});
            }
        }
        lblTotal.setText("Total: " + total + " diagnosis");
    }

    private String kapital(String teks) {
        if (teks == null || teks.isEmpty()) {
            return teks;
        }
        return teks.substring(0, 1).toUpperCase() + teks.substring(1);
    }

    @Override
    public void onShow() {
        muatData();
    }
}