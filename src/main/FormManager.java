package main;

import com.sp.form.FormAturanCF;
import com.sp.form.FormDashboard;
import com.sp.form.FormDiagnosa;
import com.sp.form.FormGejala;
import com.sp.form.FormLaporanDiagnosa;
import com.sp.form.FormLaporanGejala;
import com.sp.form.FormLaporanPasien;
import com.sp.form.FormLaporanRekap;
import com.sp.form.FormPasien;
import com.sp.form.FormPenyakit;
import com.sp.form.FormRiwayatDiagnosa;
import com.sp.form.FormUser;
import java.awt.CardLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JPanel;
import model.User;

/**
 * Mengatur perpindahan antar panel konten (com.sp.form.Form*) di dalam satu
 * container (CardLayout) pada MainForm, tanpa perlu membuka jendela baru
 * setiap kali menu di sidebar diklik. Setiap panel dibuat sekali lalu
 * disimpan pada cache supaya perpindahan menu terasa cepat.
 */
public class FormManager {

    private final JPanel container;
    private final CardLayout cardLayout;
    private final Map<AllForms, JPanel> cache = new EnumMap<>(AllForms.class);
    private User userAktif;

    public FormManager(JPanel container) {
        this.container = container;
        this.cardLayout = new CardLayout();
        this.container.setLayout(cardLayout);
    }

    public void setUserAktif(User user) {
        this.userAktif = user;
    }

    public User getUserAktif() {
        return userAktif;
    }

    /** Menampilkan panel sesuai target menu, membuatnya terlebih dulu bila belum pernah dibuka. */
    public void tampilkan(AllForms target) {
        JPanel panel = cache.get(target);
        if (panel == null) {
            panel = buatPanel(target);
            cache.put(target, panel);
            container.add(panel, target.name());
        }
        cardLayout.show(container, target.name());
        if (panel instanceof Form) {
            ((Form) panel).onShow();
        }
    }

    /** Membuang cache satu panel supaya dibangun ulang dari awal pada kunjungan berikutnya. */
    public void segarkanUlang(AllForms target) {
        JPanel panel = cache.remove(target);
        if (panel != null) {
            container.remove(panel);
        }
    }

    private JPanel buatPanel(AllForms target) {
        switch (target) {
            case DASHBOARD:
                return new FormDashboard(this);
            case PASIEN:
                return new FormPasien(this);
            case GEJALA:
                return new FormGejala(this);
            case ATURAN_CF:
                return new FormAturanCF(this);
            case PENYAKIT:
                return new FormPenyakit(this);
            case DIAGNOSA:
                return new FormDiagnosa(this);
            case RIWAYAT:
                return new FormRiwayatDiagnosa(this);
            case USER:
                return new FormUser(this);
            case LAPORAN_PASIEN:
                return new FormLaporanPasien(this);
            case LAPORAN_GEJALA:
                return new FormLaporanGejala(this);
            case LAPORAN_DIAGNOSA:
                return new FormLaporanDiagnosa(this);
            case LAPORAN_REKAP:
                return new FormLaporanRekap(this);
            default:
                throw new IllegalArgumentException("Form tidak dikenali: " + target);
        }
    }
}