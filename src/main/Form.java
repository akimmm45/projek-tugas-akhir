package main;

/**
 * Kontrak untuk setiap panel konten (com.sp.form.Form*) yang dikelola oleh
 * {@link FormManager} dan ditampilkan bergantian di dalam {@link MainForm}.
 */
public interface Form {

    /**
     * Dipanggil setiap kali panel ini ditampilkan (termasuk saat pertama
     * kali dibuka). Implementasinya sebaiknya me-refresh data dari database
     * di sini, supaya data selalu terbaru setiap kali menu dibuka.
     */
    void onShow();
}
