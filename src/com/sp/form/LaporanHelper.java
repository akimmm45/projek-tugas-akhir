package com.sp.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public final class LaporanHelper {

    
    private static final String KOTA_INSTANSI = "Jakarta Timur";
    /** Ganti sesuai nama admin yang benar-benar mengeluarkan laporan (atau sambungkan ke formManager.getUserAktif() kalau nama getter di User.java sudah kamu kasih tahu). */
    private static final String NAMA_PENANDA_TANGAN = "( Nama Admin )";
   
    private static final String ALAMAT_INSTANSI = "Jl. Pangeran No.8 RT 03 RW 02, Condet Balekabang, Kramat Jati, Jakarta Timur | Telp: 08xx-xxxx-xxxx";

    private static final Color WARNA_AKSEN = new Color(0x0F6E56);
    private static final Color WARNA_TEKS_MUTED = new Color(0x777777);
    private static final Color WARNA_GARIS_TIPIS = new Color(0xBBBBBB);

    private static final String[] NAMA_HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
    private static final String[] NAMA_BULAN = {
        "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    private static final String TEKS_FALLBACK = "Posyandu";

    private LaporanHelper() {
    }

    public static String formatTanggalIndonesia(LocalDate tanggal) {
        int indeksHari = tanggal.getDayOfWeek().getValue() - 1;
        return NAMA_HARI[indeksHari] + ", " + tanggal.getDayOfMonth() + " "
                + NAMA_BULAN[tanggal.getMonthValue()] + " " + tanggal.getYear();
    }

    public static String formatBulanTahun(YearMonth bulan) {
        return NAMA_BULAN[bulan.getMonthValue()] + " " + bulan.getYear();
    }

    private static JLabel buatLogo(String pathResource, int ukuran) {
        java.net.URL url = LaporanHelper.class.getResource(pathResource);
        if (url != null) {
            try {
                ImageIcon asli = new ImageIcon(url);
                Image diskalakan = asli.getImage().getScaledInstance(ukuran, ukuran, Image.SCALE_SMOOTH);
                JLabel labelGambar = new JLabel(new ImageIcon(diskalakan));
                labelGambar.setPreferredSize(new Dimension(ukuran, ukuran));
                labelGambar.setHorizontalAlignment(SwingConstants.CENTER);
                return labelGambar;
            } catch (Exception e) {
            }
        }
        JLabel badge = new JLabel(TEKS_FALLBACK, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(WARNA_AKSEN);
        badge.setForeground(Color.WHITE);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 13f));
        badge.setPreferredSize(new Dimension(ukuran, ukuran));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        return badge;
    }


    private static JLabel buatBadge(String pathResource, int ukuran) {
        return buatLogo(pathResource, ukuran);
    }
    
    public static JPanel buatKopSurat(String judulLaporan) {
        JPanel kop = new JPanel(new BorderLayout(0, 10));
        kop.setOpaque(false);
        kop.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel baris = new JPanel(new BorderLayout(14, 0));
        baris.setOpaque(false);

        baris.add(buatLogo("/img/Logo.png", 70), BorderLayout.WEST);

        JPanel teksInstansi = new JPanel(new GridLayout(3, 1));
        teksInstansi.setOpaque(false);
        JLabel namaInstansi = new JLabel("Posyandu Melati", SwingConstants.CENTER);
        namaInstansi.setFont(namaInstansi.getFont().deriveFont(Font.BOLD, 18f));
        JLabel tagline = new JLabel("Sistem Pakar Diagnosis Dini Hipertensi pada Lansia \u2014 Metode Certainty Factor", SwingConstants.CENTER);
        tagline.setFont(tagline.getFont().deriveFont(Font.PLAIN, 11f));
        tagline.setForeground(WARNA_TEKS_MUTED);
        JLabel alamat = new JLabel(ALAMAT_INSTANSI, SwingConstants.CENTER);
        alamat.setFont(alamat.getFont().deriveFont(Font.PLAIN, 10.5f));
        alamat.setForeground(WARNA_TEKS_MUTED);
        teksInstansi.add(namaInstansi);
        teksInstansi.add(tagline);
        teksInstansi.add(alamat);
        baris.add(teksInstansi, BorderLayout.CENTER);

        JPanel garisGanda = new JPanel();
        garisGanda.setOpaque(false);
        garisGanda.setLayout(new BoxLayout(garisGanda, BoxLayout.Y_AXIS));
        garisGanda.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel garisTebal = new JPanel();
        garisTebal.setBackground(WARNA_AKSEN);
        garisTebal.setPreferredSize(new Dimension(10, 2));
        garisTebal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        JPanel garisTipis = new JPanel();
        garisTipis.setBackground(WARNA_GARIS_TIPIS);
        garisTipis.setPreferredSize(new Dimension(10, 1));
        garisTipis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        garisGanda.add(garisTebal);
        garisGanda.add(Box.createVerticalStrut(2));
        garisGanda.add(garisTipis);

        JPanel blokAtas = new JPanel(new BorderLayout());
        blokAtas.setOpaque(false);
        blokAtas.add(baris, BorderLayout.NORTH);
        blokAtas.add(garisGanda, BorderLayout.SOUTH);

        JPanel blokJudul = new JPanel(new BorderLayout());
        blokJudul.setOpaque(false);
        blokJudul.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        JLabel judul = new JLabel(judulLaporan, SwingConstants.CENTER);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 15f));
        JLabel tanggalCetak = new JLabel("Dicetak pada " + formatTanggalIndonesia(LocalDate.now()), SwingConstants.RIGHT);
        tanggalCetak.setFont(tanggalCetak.getFont().deriveFont(Font.PLAIN, 11f));
        tanggalCetak.setForeground(WARNA_TEKS_MUTED);
        tanggalCetak.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        blokJudul.add(judul, BorderLayout.CENTER);
        blokJudul.add(tanggalCetak, BorderLayout.SOUTH);

        kop.add(blokAtas, BorderLayout.NORTH);
        kop.add(blokJudul, BorderLayout.SOUTH);
        return kop;
    }
    
    public static JPanel buatBlokTandaTangan(String jabatan) {
        JPanel luar = new JPanel(new BorderLayout());
        luar.setOpaque(false);
        luar.setBorder(BorderFactory.createEmptyBorder(26, 0, 4, 0));

        JPanel isi = new JPanel(new GridLayout(7, 1));
        isi.setOpaque(false);
        isi.setPreferredSize(new Dimension(260, 185));

        JLabel tanggal = new JLabel(KOTA_INSTANSI + ", " + formatTanggalIndonesia(LocalDate.now()), SwingConstants.CENTER);
        JLabel dikeluarkan = new JLabel("Dikeluarkan Oleh,", SwingConstants.CENTER);
        JLabel spacer1 = new JLabel(" ");
        JLabel spacer2 = new JLabel(" ");
        JLabel nama = new JLabel(NAMA_PENANDA_TANGAN, SwingConstants.CENTER);
        nama.setFont(nama.getFont().deriveFont(Font.BOLD));

        JLabel jabatanLabel = new JLabel(jabatan, SwingConstants.CENTER);
        jabatanLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, WARNA_GARIS_TIPIS),
                BorderFactory.createEmptyBorder(3, 0, 0, 0)));

        isi.add(tanggal);
        isi.add(new JLabel(" "));
        isi.add(dikeluarkan);
        isi.add(spacer1);
        isi.add(spacer2);
        isi.add(nama);
        isi.add(jabatanLabel);

        luar.add(isi, BorderLayout.EAST);
        return luar;
    }

    public static JScrollPane bungkusHalaman(JPanel isiKertas) {
        isiKertas.setBackground(Color.WHITE);
        isiKertas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));
        pembungkus.add(isiKertas, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(pembungkus);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public static JPanel bungkusTabelUntukCetak(JTable tabel) {
        tabel.setRowHeight(24);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        panel.add(tabel.getTableHeader(), BorderLayout.NORTH);
        panel.add(tabel, BorderLayout.CENTER);
        return panel;
    }

 
    public static void cetak(JComponent komponen) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double lebarKomponen = Math.max(1, komponen.getWidth());
            double skala = pageFormat.getImageableWidth() / lebarKomponen;
            if (skala < 1) {
                g2.scale(skala, skala);
            }
            komponen.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Gagal mencetak: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}