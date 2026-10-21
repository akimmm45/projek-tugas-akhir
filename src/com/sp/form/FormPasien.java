package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Pasien;
import net.miginfocom.swing.MigLayout;
import service.ServicePasien;

public class FormPasien extends JPanel implements Form {

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FormManager formManager;
    private final ServicePasien servicePasien = new ServicePasien();

    private JTable tabel;
    private DefaultTableModel model;
    private List<Integer> idList = new ArrayList<>();

    private JTextField txtCari;

    private JTextField txtNik, txtNama, txtTanggalLahir, txtAlamat, txtRt, txtRw, txtNoHp;
    private JComboBox<String> cmbJenisKelamin;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBersihkan;

    private Integer idPasienTerpilih = null;

    public FormPasien(FormManager formManager) {
        this.formManager = formManager;
        // Tabel lebih lebar (2), form lebih sempit (1)
        setLayout(new MigLayout("insets 24, fill", "[grow 2, fill][grow 1, fill]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Data Pasien");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 16, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelForm(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel panelCari = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panelCari.setOpaque(false);
        txtCari = new JTextField();
        txtCari.putClientProperty("JTextField.placeholderText", "Cari nama atau NIK...");
        panelCari.add(txtCari, "h 32!, growx");
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> muatTabel(txtCari.getText().trim()));
        panelCari.add(btnCari, "h 32!, w 80!");
        panel.add(panelCari, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new Object[]{"No", "Nama", "NIK", "Umur", "Jenis Kelamin", "Alamat", "No. HP"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setShowGrid(true);
        tabel.setGridColor(Color.LIGHT_GRAY);

        tabel.getColumnModel().getColumn(0).setMaxWidth(40);  // No
        tabel.getColumnModel().getColumn(3).setMaxWidth(60);  // Umur

        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelForm() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, insets 16, gapy 6", "[90!][grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulForm = new JLabel("Form Data Pasien");
        judulForm.setFont(judulForm.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulForm, "span 2, gapbottom 8");

        panel.add(new JLabel("NIK"));
        txtNik = new JTextField();
        panel.add(txtNik, "h 30!");

        panel.add(new JLabel("Nama*"));
        txtNama = new JTextField();
        panel.add(txtNama, "h 30!");

        panel.add(new JLabel("Tgl. Lahir"));
        txtTanggalLahir = new JTextField();
        txtTanggalLahir.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");
        panel.add(txtTanggalLahir, "h 30!");

        panel.add(new JLabel("Jenis Kelamin*"));
        cmbJenisKelamin = new JComboBox<>(new String[]{"L", "P"});
        panel.add(cmbJenisKelamin, "h 30!");

        panel.add(new JLabel("Alamat"));
        txtAlamat = new JTextField();
        panel.add(txtAlamat, "h 30!");

        panel.add(new JLabel("RT"));
        txtRt = new JTextField();
        panel.add(txtRt, "h 30!");

        panel.add(new JLabel("RW"));
        txtRw = new JTextField();
        panel.add(txtRw, "h 30!");

        panel.add(new JLabel("No. HP"));
        txtNoHp = new JTextField();
        panel.add(txtNoHp, "h 30!");

        JPanel panelTombol = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelTombol.setOpaque(false);

        btnSimpan = new JButton("Simpan Baru");
        btnSimpan.setBackground(new Color(0x2E7D6B));
        btnSimpan.setForeground(Color.WHITE);
        panelTombol.add(btnSimpan, "h 34!, wmin 80");

        btnUpdate = new JButton("Update");
        panelTombol.add(btnUpdate, "h 34!, wmin 80");

        btnHapus = new JButton("Hapus");
        btnHapus.setForeground(new Color(0xC0392B));
        panelTombol.add(btnHapus, "h 34!, wmin 80");

        btnBersihkan = new JButton("Bersihkan Form");
        panelTombol.add(btnBersihkan, "h 34!, wmin 80");

        panel.add(panelTombol, "span 2, gaptop 12");
        return panel;
    }

    private void pasangAksi() {
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() != -1) {
                isiFormDariTabel();
            }
        });
        btnSimpan.addActionListener(e -> simpanBaru());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        txtCari.addActionListener(e -> muatTabel(txtCari.getText().trim()));
    }

    @Override
    public void onShow() {
        muatTabel(null);
        bersihkanForm();
    }

    private void muatTabel(String kataKunci) {
        model.setRowCount(0);
        idList.clear();
        List<Pasien> daftar = (kataKunci == null || kataKunci.isEmpty())
                ? servicePasien.getAll() : servicePasien.cari(kataKunci);
        int no = 1;
        for (Pasien p : daftar) {
            idList.add(p.getIdPasien());
            model.addRow(new Object[]{
                no++,
                p.getNama(),
                p.getNik(),
                p.getUmur(),
                p.getJenisKelamin(),
                p.getAlamat(),
                p.getNoHp()
            });
        }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0 || row >= idList.size()) return;
        idPasienTerpilih = idList.get(row);
        Pasien p = servicePasien.getById(idPasienTerpilih);
        if (p == null) return;

        txtNik.setText(p.getNik());
        txtNama.setText(p.getNama());
        txtTanggalLahir.setText(p.getTanggalLahir() != null ? p.getTanggalLahir().format(FORMAT_TANGGAL) : "");
        cmbJenisKelamin.setSelectedItem(p.getJenisKelamin());
        txtAlamat.setText(p.getAlamat());
        txtRt.setText(p.getRt());
        txtRw.setText(p.getRw());
        txtNoHp.setText(p.getNoHp());
    }

    private Pasien ambilInputForm() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lansia wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Pasien p = new Pasien();
        p.setNik(txtNik.getText().trim());
        p.setNama(txtNama.getText().trim());
        String tgl = txtTanggalLahir.getText().trim();
        if (!tgl.isEmpty()) {
            try {
                p.setTanggalLahir(LocalDate.parse(tgl, FORMAT_TANGGAL));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal lahir salah, gunakan yyyy-MM-dd (contoh: 1960-05-17).",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        p.setJenisKelamin((String) cmbJenisKelamin.getSelectedItem());
        p.setAlamat(txtAlamat.getText().trim());
        p.setRt(txtRt.getText().trim());
        p.setRw(txtRw.getText().trim());
        p.setNoHp(txtNoHp.getText().trim());
        return p;
    }

    private void simpanBaru() {
        Pasien p = ambilInputForm();
        if (p == null) return;
        if (servicePasien.simpan(p)) {
            JOptionPane.showMessageDialog(this, "Data lansia berhasil disimpan!");
            bersihkanForm();
            muatTabel(null);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data lansia!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateData() {
        if (idPasienTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data lansia pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pasien p = ambilInputForm();
        if (p == null) return;
        p.setIdPasien(idPasienTerpilih);
        if (servicePasien.update(p)) {
            JOptionPane.showMessageDialog(this, "Data lansia berhasil diperbarui!");
            bersihkanForm();
            muatTabel(null);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data lansia!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if (idPasienTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data lansia pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Hapus data lansia \"" + txtNama.getText() + "\"?\nSeluruh riwayat diagnosa terkait juga akan terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pilihan == JOptionPane.YES_OPTION) {
            if (servicePasien.hapus(idPasienTerpilih)) {
                JOptionPane.showMessageDialog(this, "Data lansia berhasil dihapus.");
                bersihkanForm();
                muatTabel(null);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data lansia!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bersihkanForm() {
        idPasienTerpilih = null;
        txtNik.setText("");
        txtNama.setText("");
        txtTanggalLahir.setText("");
        cmbJenisKelamin.setSelectedIndex(0);
        txtAlamat.setText("");
        txtRt.setText("");
        txtRw.setText("");
        txtNoHp.setText("");
        tabel.clearSelection();
    }
}