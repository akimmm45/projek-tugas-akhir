package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.AturanCF;
import model.Gejala;
import model.Penyakit;
import net.miginfocom.swing.MigLayout;
import service.ServiceAturanCF;
import service.ServiceGejala;
import service.ServicePenyakit;

public class FormAturanCF extends JPanel implements Form {

    private final ServiceAturanCF serviceAturanCF = new ServiceAturanCF();
    private final ServicePenyakit servicePenyakit = new ServicePenyakit();
    private final ServiceGejala serviceGejala = new ServiceGejala();

    private JTable tabel;
    private DefaultTableModel model;
    private List<Integer> idList = new ArrayList<>();

    private JComboBox<Penyakit> cmbPenyakit;
    private JComboBox<Gejala> cmbGejala;
    private JSpinner spinnerMb, spinnerMd;
    private JLabel lblCfHasil;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBersihkan;

    private Integer idAturanTerpilih = null;

    public FormAturanCF(FormManager formManager) {
        // Proporsi tabel 3, form 1 agar tabel jauh lebih luas
        setLayout(new MigLayout("insets 20, fill", "[grow 3, fill][grow 1, fill]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Aturan Certainty Factor (Basis Pengetahuan)");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 18f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelForm(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        model = new DefaultTableModel(
            new Object[]{"No", "Kategori Hipertensi", "Gejala", "MB", "MD", "CF (MB-MD)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setRowHeight(24);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        // Kolom No dan nilai numerik diberi batas, sisanya tidak dibatasi
        tabel.getColumnModel().getColumn(0).setMaxWidth(40); // No
        tabel.getColumnModel().getColumn(3).setMaxWidth(60); // MB
        tabel.getColumnModel().getColumn(4).setMaxWidth(60); // MD
        tabel.getColumnModel().getColumn(5).setMaxWidth(90); // CF

        // Agar kolom menyesuaikan lebar secara proporsional
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Rata tengah semua kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelForm() {
        // Form lebih kompak: inset lebih kecil, gap lebih rapat
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 12, gapy 4", "[grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulForm = new JLabel("Form Aturan CF");
        judulForm.setFont(judulForm.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(judulForm, "gapbottom 6");

        panel.add(new JLabel("Kategori Hipertensi (Hipotesis)"));
        cmbPenyakit = new JComboBox<>();
        panel.add(cmbPenyakit, "h 28!, growx");

        panel.add(new JLabel("Gejala (Evidence)"));
        cmbGejala = new JComboBox<>();
        panel.add(cmbGejala, "h 28!, growx");

        JPanel panelNilai = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelNilai.setOpaque(false);
        panelNilai.add(new JLabel("MB"), "span 2");
        spinnerMb = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
        panelNilai.add(spinnerMb, "h 28!, span 2, growx");

        panelNilai.add(new JLabel("MD"), "span 2");
        spinnerMd = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1.0, 0.1));
        panelNilai.add(spinnerMd, "h 28!, span 2, growx");
        panel.add(panelNilai, "growx");

        lblCfHasil = new JLabel("CF = 0.40");
        lblCfHasil.setFont(lblCfHasil.getFont().deriveFont(Font.BOLD));
        lblCfHasil.setForeground(new Color(0x2E7D6B));
        panel.add(lblCfHasil, "gapbottom 8");

        JPanel panelTombol = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelTombol.setOpaque(false);

        btnSimpan = new JButton("Tambah");
        btnSimpan.setBackground(new Color(0x2E7D6B));
        btnSimpan.setForeground(Color.WHITE);
        panelTombol.add(btnSimpan, "span 2, h 30!, growx");

        btnUpdate = new JButton("Update");
        panelTombol.add(btnUpdate, "h 30!, growx");

        btnHapus = new JButton("Hapus");
        btnHapus.setForeground(new Color(0xC0392B));
        panelTombol.add(btnHapus, "h 30!, growx");

        btnBersihkan = new JButton("Bersih");
        panelTombol.add(btnBersihkan, "span 2, h 30!, growx");

        panel.add(panelTombol, "gaptop 6");
        return panel;
    }

    private void pasangAksi() {
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() != -1) {
                isiFormDariTabel();
            }
        });
        spinnerMb.addChangeListener(e -> perbaruiLabelCf());
        spinnerMd.addChangeListener(e -> perbaruiLabelCf());
        btnSimpan.addActionListener(e -> simpanBaru());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());
    }

    @Override
    public void onShow() {
        muatComboPenyakit();
        muatComboGejala();
        muatTabel();
        bersihkanForm();
    }

    private void muatComboPenyakit() {
        cmbPenyakit.removeAllItems();
        for (Penyakit p : servicePenyakit.getAll()) {
            cmbPenyakit.addItem(p);
        }
    }

    private void muatComboGejala() {
        cmbGejala.removeAllItems();
        for (Gejala g : serviceGejala.getAll()) {
            cmbGejala.addItem(g);
        }
    }

    private void muatTabel() {
        model.setRowCount(0);
        idList.clear();
        List<AturanCF> daftar = serviceAturanCF.getAll();
        int no = 1;
        for (AturanCF a : daftar) {
            idList.add(a.getIdAturan());
            model.addRow(new Object[]{
                no++,
                a.getNamaPenyakit(),
                a.getNamaGejala(),
                a.getNilaiMb(),
                a.getNilaiMd(),
                String.format("%.2f", a.getNilaiCf())
            });
        }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0 || row >= idList.size()) return;
        idAturanTerpilih = idList.get(row);
        List<AturanCF> semua = serviceAturanCF.getAll();
        for (AturanCF a : semua) {
            if (a.getIdAturan() == idAturanTerpilih) {
                pilihComboBerdasarkanKode(cmbPenyakit, a.getKodePenyakit());
                pilihComboBerdasarkanKode(cmbGejala, a.getKodeGejala());
                spinnerMb.setValue(a.getNilaiMb());
                spinnerMd.setValue(a.getNilaiMd());
                perbaruiLabelCf();
                break;
            }
        }
    }

    private void pilihComboBerdasarkanKode(JComboBox<?> combo, String kode) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Penyakit && ((Penyakit) item).getKodePenyakit().equals(kode)) {
                combo.setSelectedIndex(i);
                return;
            }
            if (item instanceof Gejala && ((Gejala) item).getKodeGejala().equals(kode)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void perbaruiLabelCf() {
        double mb = (double) spinnerMb.getValue();
        double md = (double) spinnerMd.getValue();
        lblCfHasil.setText(String.format("CF = %.2f", (mb - md)));
    }

    private AturanCF ambilInputForm() {
        Penyakit penyakit = (Penyakit) cmbPenyakit.getSelectedItem();
        Gejala gejala = (Gejala) cmbGejala.getSelectedItem();
        if (penyakit == null || gejala == null) {
            JOptionPane.showMessageDialog(this, "Kategori hipertensi dan gejala wajib dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        AturanCF a = new AturanCF();
        a.setKodePenyakit(penyakit.getKodePenyakit());
        a.setKodeGejala(gejala.getKodeGejala());
        a.setNilaiMb((double) spinnerMb.getValue());
        a.setNilaiMd((double) spinnerMd.getValue());
        return a;
    }

    private void simpanBaru() {
        AturanCF a = ambilInputForm();
        if (a == null) return;
        if (serviceAturanCF.simpan(a)) {
            JOptionPane.showMessageDialog(this, "Aturan CF berhasil disimpan!");
            bersihkanForm();
            muatTabel();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan aturan CF! (Pastikan kombinasi kategori-gejala belum pernah ada)", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateData() {
        if (idAturanTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data aturan pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AturanCF a = ambilInputForm();
        if (a == null) return;
        a.setIdAturan(idAturanTerpilih);
        if (serviceAturanCF.update(a)) {
            JOptionPane.showMessageDialog(this, "Aturan CF berhasil diperbarui!");
            bersihkanForm();
            muatTabel();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui aturan CF!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if (idAturanTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data aturan pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this, "Hapus aturan CF terpilih?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pilihan == JOptionPane.YES_OPTION) {
            if (serviceAturanCF.hapus(idAturanTerpilih)) {
                JOptionPane.showMessageDialog(this, "Aturan CF berhasil dihapus.");
                bersihkanForm();
                muatTabel();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus aturan CF!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bersihkanForm() {
        idAturanTerpilih = null;
        if (cmbPenyakit.getItemCount() > 0) cmbPenyakit.setSelectedIndex(0);
        if (cmbGejala.getItemCount() > 0) cmbGejala.setSelectedIndex(0);
        spinnerMb.setValue(0.5);
        spinnerMd.setValue(0.1);
        perbaruiLabelCf();
        tabel.clearSelection();
    }
}