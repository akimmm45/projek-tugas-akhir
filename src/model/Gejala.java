package model;

/**
 * Model untuk tabel tbl_gejala: daftar keluhan klinis, faktor risiko, dan
 * hasil pengukuran tekanan darah yang dipakai sebagai input basis
 * pengetahuan Certainty Factor.
 */
public class Gejala {

    private String kodeGejala;
    private String namaGejala;
    private String kategoriGejala;

    public Gejala() {
    }

    public Gejala(String kodeGejala, String namaGejala, String kategoriGejala) {
        this.kodeGejala = kodeGejala;
        this.namaGejala = namaGejala;
        this.kategoriGejala = kategoriGejala;
    }

    public String getKodeGejala() {
        return kodeGejala;
    }

    public void setKodeGejala(String kodeGejala) {
        this.kodeGejala = kodeGejala;
    }

    public String getNamaGejala() {
        return namaGejala;
    }

    public void setNamaGejala(String namaGejala) {
        this.namaGejala = namaGejala;
    }

    public String getKategoriGejala() {
        return kategoriGejala;
    }

    public void setKategoriGejala(String kategoriGejala) {
        this.kategoriGejala = kategoriGejala;
    }

    @Override
    public String toString() {
        return kodeGejala + " - " + namaGejala;
    }
}
