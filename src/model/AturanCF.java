package model;

/**
 * Model untuk tabel tbl_aturan: satu baris basis pengetahuan Certainty
 * Factor yang menghubungkan satu gejala dengan satu kategori hipertensi,
 * lengkap dengan nilai MB (Measure of Belief) dan MD (Measure of Disbelief).
 * CF aturan = MB - MD.
 *
 * Field namaPenyakit dan namaGejala bersifat opsional (hasil JOIN), dipakai
 * untuk menampilkan data yang mudah dibaca pada JTable FormAturanCF tanpa
 * perlu query tambahan.
 */
public class AturanCF {

    private int idAturan;
    private String kodePenyakit;
    private String kodeGejala;
    private double nilaiMb;
    private double nilaiMd;

    // hasil join, untuk tampilan
    private String namaPenyakit;
    private String namaGejala;

    public AturanCF() {
    }

    public AturanCF(String kodePenyakit, String kodeGejala, double nilaiMb, double nilaiMd) {
        this.kodePenyakit = kodePenyakit;
        this.kodeGejala = kodeGejala;
        this.nilaiMb = nilaiMb;
        this.nilaiMd = nilaiMd;
    }

    public int getIdAturan() {
        return idAturan;
    }

    public void setIdAturan(int idAturan) {
        this.idAturan = idAturan;
    }

    public String getKodePenyakit() {
        return kodePenyakit;
    }

    public void setKodePenyakit(String kodePenyakit) {
        this.kodePenyakit = kodePenyakit;
    }

    public String getKodeGejala() {
        return kodeGejala;
    }

    public void setKodeGejala(String kodeGejala) {
        this.kodeGejala = kodeGejala;
    }

    public double getNilaiMb() {
        return nilaiMb;
    }

    public void setNilaiMb(double nilaiMb) {
        this.nilaiMb = nilaiMb;
    }

    public double getNilaiMd() {
        return nilaiMd;
    }

    public void setNilaiMd(double nilaiMd) {
        this.nilaiMd = nilaiMd;
    }

    /** CF aturan tunggal = MB - MD (rumus dasar Certainty Factor). */
    public double getNilaiCf() {
        return nilaiMb - nilaiMd;
    }

    public String getNamaPenyakit() {
        return namaPenyakit;
    }

    public void setNamaPenyakit(String namaPenyakit) {
        this.namaPenyakit = namaPenyakit;
    }

    public String getNamaGejala() {
        return namaGejala;
    }

    public void setNamaGejala(String namaGejala) {
        this.namaGejala = namaGejala;
    }
}
