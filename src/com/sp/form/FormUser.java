package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.Form;
import main.FormManager;
import model.User;
import net.miginfocom.swing.MigLayout;
import service.ServiceUser;

public class FormUser extends JPanel implements Form {

    private final FormManager formManager;
    private final ServiceUser serviceUser = new ServiceUser();

    private JTable tabel;
    private DefaultTableModel model;

    private JTextField txtUsername, txtNamaLengkap;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbLevel;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBersihkan;

    private Integer idUserTerpilih = null;
    private int selectedRow = -1;

    public FormUser(FormManager formManager) {
        this.formManager = formManager;
        setLayout(new MigLayout("insets 24, fill", "[grow,fill][380!]", "[][grow,fill]"));
        setBackground(new Color(0xF4F7F6));
        initComponents();
        pasangAksi();
    }

    private void initComponents() {
        JLabel judul = new JLabel("Manajemen User (Petugas/Kader)");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        add(judul, "span 2, gapbottom 12, wrap");

        add(buatPanelTabel(), "grow");
        add(buatPanelForm(), "grow, wrap");
    }

    private JPanel buatPanelTabel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        model = new DefaultTableModel(new Object[]{"No", "ID", "Username", "Nama Lengkap", "Level"}, 0) {
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
        tabel.getColumnModel().getColumn(1).setMaxWidth(50); // ID

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
                    isiFormDariTabel();
                }
            }
        });

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelForm() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 16", "[grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));

        JLabel judulForm = new JLabel("Form User");
        judulForm.setFont(judulForm.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(judulForm, "gapbottom 8");

        panel.add(new JLabel("Username*"));
        txtUsername = new JTextField();
        panel.add(txtUsername, "h 30!, gapbottom 6");

        panel.add(new JLabel("Nama Lengkap*"));
        txtNamaLengkap = new JTextField();
        panel.add(txtNamaLengkap, "h 30!, gapbottom 6");

        panel.add(new JLabel("Level"));
        cmbLevel = new JComboBox<>(new String[]{"Admin", "Kader"});
        panel.add(cmbLevel, "h 30!, gapbottom 6");

        panel.add(new JLabel("Password"));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Isi untuk set/ganti password");
        panel.add(txtPassword, "h 30!, gapbottom 4");

        JLabel hint = new JLabel("<html><i>Kosongkan password saat update apabila tidak ingin menggantinya.</i></html>");
        hint.setForeground(new Color(0x9E9E9E));
        hint.setFont(hint.getFont().deriveFont(11f));
        panel.add(hint, "gapbottom 10");

        JPanel panelTombol = new JPanel(new MigLayout("insets 0, wrap 2", "[grow,fill][grow,fill]"));
        panelTombol.setOpaque(false);

        btnSimpan = new JButton("Tambah User Baru");
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

    private void pasangAksi() {
        btnSimpan.addActionListener(e -> simpanBaru());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersihkan.addActionListener(e -> bersihkanForm());
    }

    @Override
    public void onShow() {
        muatTabel();
        bersihkanForm();
    }

    private void muatTabel() {
        model.setRowCount(0);
        List<User> daftar = serviceUser.getAll();
        int no = 1;
        for (User u : daftar) {
            model.addRow(new Object[]{no++, u.getIdUser(), u.getUsername(), u.getNamaLengkap(), u.getLevel()});
        }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        idUserTerpilih = (Integer) model.getValueAt(row, 1);
        txtUsername.setText((String) model.getValueAt(row, 2));
        txtNamaLengkap.setText((String) model.getValueAt(row, 3));
        cmbLevel.setSelectedItem(model.getValueAt(row, 4));
        txtPassword.setText("");
    }

    private void simpanBaru() {
        if (txtUsername.getText().trim().isEmpty() || txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan nama lengkap wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String password = new String(txtPassword.getPassword());
        if (password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Password wajib diisi untuk user baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User u = new User();
        u.setUsername(txtUsername.getText().trim());
        u.setNamaLengkap(txtNamaLengkap.getText().trim());
        u.setLevel((String) cmbLevel.getSelectedItem());

        if (serviceUser.simpan(u, password)) {
            JOptionPane.showMessageDialog(this, "User baru berhasil disimpan!");
            bersihkanForm();
            muatTabel();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan user! (Kemungkinan username sudah dipakai)", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateData() {
        if (idUserTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data user pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtUsername.getText().trim().isEmpty() || txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan nama lengkap wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User u = new User();
        u.setIdUser(idUserTerpilih);
        u.setUsername(txtUsername.getText().trim());
        u.setNamaLengkap(txtNamaLengkap.getText().trim());
        u.setLevel((String) cmbLevel.getSelectedItem());

        String passwordBaru = new String(txtPassword.getPassword());
        if (serviceUser.update(u, passwordBaru)) {
            JOptionPane.showMessageDialog(this, "Data user berhasil diperbarui!");
            bersihkanForm();
            muatTabel();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui user!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusData() {
        if (idUserTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih data user pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User userAktif = formManager.getUserAktif();
        if (userAktif != null && userAktif.getIdUser() == idUserTerpilih) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menghapus akun yang sedang digunakan untuk login!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this, "Hapus user \"" + txtUsername.getText() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pilihan == JOptionPane.YES_OPTION) {
            if (serviceUser.hapus(idUserTerpilih)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus.");
                bersihkanForm();
                muatTabel();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void bersihkanForm() {
        idUserTerpilih = null;
        selectedRow = -1;
        txtUsername.setText("");
        txtNamaLengkap.setText("");
        cmbLevel.setSelectedIndex(0);
        txtPassword.setText("");
        tabel.clearSelection();
    }
}