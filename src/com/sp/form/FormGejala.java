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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.Gejala;
import net.miginfocom.swing.MigLayout;
import service.ServiceGejala;

public class FormGejala extends JPanel implements Form {

    private final FormManager formManager;
    private final ServiceGejala serviceGejala = new ServiceGejala();

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtKode, txtNama, txtKategori, txtCari;
    private JButton btnTambah, btnUbah, btnHapus, btnRefresh, btnCari;
    private int selectedRow = -1;

    public FormGejala(FormManager formManager) {
        this.formManager = formManager;
        setLayout(new MigLayout("insets 24, fill", "[grow 2, fill][grow 1, fill]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
        loadData();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Data Gejala Hipertensi");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelForm(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel panelCari = new JPanel(new MigLayout("insets 0", "[grow,fill][]"));
        panelCari.setOpaque(false);
        txtCari = new JTextField();
        txtCari.putClientProperty("JTextField.placeholderText", "Cari kode atau nama gejala...");
        panelCari.add(txtCari, "h 32!, growx");
        btnCari = new JButton("Cari");
        panelCari.add(btnCari, "h 32!, w 80!");
        panel.add(panelCari, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"No", "Kode", "Nama Gejala", "Kategori"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        table.getColumnModel().getColumn(0).setMaxWidth(40); // No
        table.getColumnModel().getColumn(1).setMaxWidth(80); // Kode

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = table.getSelectedRow();
                if (idx != -1) {
                    selectedRow = idx;
                    String kode = (String) model.getValueAt(idx, 1);
                    String nama = (String) model.getValueAt(idx, 2);
                    String kategori = (String) model.getValueAt(idx, 3);
                    txtKode.setText(kode);
                    txtNama.setText(nama);
                    txtKategori.setText(kategori != null ? kategori : "");
                    txtKode.setEditable(false);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelForm() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, insets 16, gapy 6", "[90!][grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulForm = new JLabel("Form Gejala");
        judulForm.setFont(judulForm.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulForm, "span 2, gapbottom 8");

        panel.add(new JLabel("Kode Gejala"));
        txtKode = new JTextField();
        panel.add(txtKode, "h 30!");

        panel.add(new JLabel("Nama Gejala"));
        txtNama = new JTextField();
        panel.add(txtNama, "h 30!");

        panel.add(new JLabel("Kategori"));
        txtKategori = new JTextField();
        panel.add(txtKategori, "h 30!");

        JPanel panelTombol = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelTombol.setOpaque(false);

        btnTambah = new JButton("Tambah");
        btnTambah.setBackground(new Color(0x2E7D6B));
        btnTambah.setForeground(Color.WHITE);
        panelTombol.add(btnTambah, "h 34!, wmin 80");

        btnUbah = new JButton("Ubah");
        panelTombol.add(btnUbah, "h 34!, wmin 80");

        btnHapus = new JButton("Hapus");
        btnHapus.setForeground(new Color(0xC0392B));
        panelTombol.add(btnHapus, "h 34!, wmin 80");

        btnRefresh = new JButton("Refresh");
        panelTombol.add(btnRefresh, "h 34!, wmin 80");

        panel.add(panelTombol, "span 2, gaptop 12");
        return panel;
    }

    private void pasangAksi() {
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnRefresh.addActionListener(e -> loadData());
        btnCari.addActionListener(e -> cari());
    }

    @Override
    public void onShow() {
        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        List<Gejala> list = serviceGejala.getAll();
        int no = 1;
        for (Gejala g : list) {
            model.addRow(new Object[]{no++, g.getKodeGejala(), g.getNamaGejala(), g.getKategoriGejala()});
        }
        clearInput();
    }

    private void cari() {
        String keyword = txtCari.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        model.setRowCount(0);
        int no = 1;
        for (Gejala g : serviceGejala.getAll()) {
            boolean cocok = (g.getKodeGejala() != null && g.getKodeGejala().toLowerCase().contains(keyword))
                    || (g.getNamaGejala() != null && g.getNamaGejala().toLowerCase().contains(keyword));
            if (cocok) {
                model.addRow(new Object[]{no++, g.getKodeGejala(), g.getNamaGejala(), g.getKategoriGejala()});
            }
        }
    }

    private void tambah() {
        if (!validateInput()) return;
        Gejala g = new Gejala(txtKode.getText().trim(), txtNama.getText().trim(), txtKategori.getText().trim());
        if (serviceGejala.simpan(g)) {
            JOptionPane.showMessageDialog(this, "Data gejala berhasil ditambahkan!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data gejala. Kode mungkin sudah dipakai.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubah() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diubah!");
            return;
        }
        if (!validateInput()) return;
        Gejala g = new Gejala(txtKode.getText().trim(), txtNama.getText().trim(), txtKategori.getText().trim());
        if (serviceGejala.update(g)) {
            JOptionPane.showMessageDialog(this, "Data gejala berhasil diubah!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data gejala!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapus() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String kode = (String) model.getValueAt(selectedRow, 1);
            if (serviceGejala.hapus(kode)) {
                JOptionPane.showMessageDialog(this, "Data gejala berhasil dihapus!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data gejala. Mungkin masih dipakai di Aturan CF.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        if (txtKode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode gejala harus diisi!");
            return false;
        }
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama gejala harus diisi!");
            return false;
        }
        if (txtKategori.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kategori gejala harus diisi!");
            return false;
        }
        return true;
    }

    private void clearInput() {
        txtKode.setText(serviceGejala.generateKodeBaru());
        txtKode.setEditable(true);
        txtNama.setText("");
        txtKategori.setText("");
        selectedRow = -1;
        table.clearSelection();
    }
}