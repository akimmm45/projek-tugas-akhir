package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import model.Penyakit;
import net.miginfocom.swing.MigLayout;
import service.ServicePenyakit;


public class FormPenyakit extends JPanel implements Form {

    private final ServicePenyakit servicePenyakit = new ServicePenyakit();

    private JTable tabel;
    private DefaultTableModel model;

    private JTextField txtKode, txtNama;
    private JTextArea txtDeskripsi, txtSolusi, txtPencegahan;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBersihkan;

    private String kodeTerpilih = null;
    private int selectedRow = -1; 

    public FormPenyakit(FormManager formManager) {
        setLayout(new MigLayout("insets 24, fill", "[grow 2, fill][grow 1, fill]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
        loadData();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Kategori / Tingkat Hipertensi");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelForm(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        model = new DefaultTableModel(new Object[]{"No", "Kode", "Nama Kategori"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        tabel.getColumnModel().getColumn(0).setMaxWidth(40); // No
        tabel.getColumnModel().getColumn(1).setMaxWidth(80); // Kode

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = tabel.getSelectedRow();
                if (idx != -1) {
                    selectedRow = idx;
                    String kode = (String) model.getValueAt(idx, 1);
                    isiFormDariKode(kode);
                }
            }
        });

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelForm() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 16, gapy 6", "[grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulForm = new JLabel("Form Kategori Hipertensi");
        judulForm.setFont(judulForm.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulForm, "gapbottom 8");

        panel.add(new JLabel("Kode"));
        txtKode = new JTextField();
        txtKode.setEditable(false);
        panel.add(txtKode, "h 30!, gapbottom 6");

        panel.add(new JLabel("Nama Kategori*"));
        txtNama = new JTextField();
        panel.add(txtNama, "h 30!, gapbottom 6");

        panel.add(new JLabel("Deskripsi"));
        txtDeskripsi = buatTextArea();
        panel.add(new JScrollPane(txtDeskripsi), "h 70!, gapbottom 6");

        panel.add(new JLabel("Solusi / Penanganan"));
        txtSolusi = buatTextArea();
        panel.add(new JScrollPane(txtSolusi), "h 70!, gapbottom 6");

        panel.add(new JLabel("Pencegahan"));
        txtPencegahan = buatTextArea();
        panel.add(new JScrollPane(txtPencegahan), "h 70!, gapbottom 10");

        JPanel panelTombol = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelTombol.setOpaque(false);

        btnSimpan = new JButton("Tambah Kategori Baru");
        btnSimpan.setBackground(new Color(0x2E7D6B));
        btnSimpan.setForeground(Color.WHITE);
        panelTombol.add(btnSimpan, "span 2, h 34!");

        btnUpdate = new JButton("Update");
        panelTombol.add(btnUpdate, "h 34!");

        btnHapus = new JButton("Hapus");
        btnHapus.setForeground(new Color(0xC0392B));
        panelTombol.add(btnHapus, "h 34!");

        btnBersihkan = new JButton("Bersihkan Form");
        panelTombol.add(btnBersihkan, "span 2, h 34!");

        panel.add(panelTombol, "gaptop 4");

        return panel;
    }

    private JTextArea buatTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void pasangAksi() {
        btnSimpan.addActionListener(e -> simpanBaru());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());
    }

    @Override
    public void onShow() {
        loadData();
        bersihkanForm();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Penyakit> daftar = servicePenyakit.getAll();
        int no = 1;
        for (Penyakit p : daftar) {
            model.addRow(new Object[]{no++, p.getKodePenyakit(), p.getNamaPenyakit()});
        }
        clearInput();
    }

    private void isiFormDariKode(String kode) {
        kodeTerpilih = kode;
        Penyakit p = servicePenyakit.getByKode(kode);
        if (p == null) {
            return;
        }
        txtKode.setText(p.getKodePenyakit());
        txtNama.setText(p.getNamaPenyakit());
        txtDeskripsi.setText(p.getDeskripsi());
        txtSolusi.setText(p.getSolusi());
        txtPencegahan.setText(p.getPencegahan());
    }

    private Penyakit ambilInputForm() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Penyakit p = new Penyakit();
        p.setNamaPenyakit(txtNama.getText().trim());
        p.setDeskripsi(txtDeskripsi.getText().trim());
        p.setSolusi(txtSolusi.getText().trim());
        p.setPencegahan(txtPencegahan.getText().trim());
        return p;
    }

    private void simpanBaru() {
        if (!validateInput()) return;
        Penyakit p = ambilInputForm();
        if (p == null) return;
        p.setKodePenyakit(servicePenyakit.generateKodeBaru());
        if (servicePenyakit.simpan(p)) {
            JOptionPane.showMessageDialog(this, "Kategori baru (" + p.getKodePenyakit() + ") berhasil disimpan!");
            loadData();
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan kategori!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateData() {
        if (kodeTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data kategori pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;
        Penyakit p = ambilInputForm();
        if (p == null) return;
        p.setKodePenyakit(kodeTerpilih);
        if (servicePenyakit.update(p)) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil diperbarui!");
            loadData();
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kategori!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if (kodeTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data kategori pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Hapus kategori " + kodeTerpilih + "?\nAturan CF yang memakai kategori ini juga akan ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pilihan == JOptionPane.YES_OPTION) {
            if (servicePenyakit.hapus(kodeTerpilih)) {
                JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus.");
                loadData();
                bersihkanForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kategori (kemungkinan masih dipakai pada riwayat diagnosa)!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bersihkanForm() {
        kodeTerpilih = null;
        selectedRow = -1;
        txtKode.setText(servicePenyakit.generateKodeBaru());
        txtKode.setEditable(false);
        txtNama.setText("");
        txtDeskripsi.setText("");
        txtSolusi.setText("");
        txtPencegahan.setText("");
        tabel.clearSelection();
    }

    private boolean validateInput() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori harus diisi!");
            return false;
        }
        return true;
    }

    private void clearInput() {
        txtKode.setText(servicePenyakit.generateKodeBaru());
        txtKode.setEditable(false);
        txtNama.setText("");
        txtDeskripsi.setText("");
        txtSolusi.setText("");
        txtPencegahan.setText("");
        kodeTerpilih = null;
        selectedRow = -1;
        tabel.clearSelection();
    }
}