package model;

/**
 * Model untuk tabel tbl_diagnosa_detail: gejala-gejala apa saja yang dipilih
 * pada satu sesi diagnosa, beserta nilai CF kepercayaan pengguna atas gejala
 * tersebut (skala: Tidak=0, Sedikit Yakin=0.4, Yakin=0.6, Sangat Yakin=0.8,
 * Pasti=1.0 - lihat ServiceCF.SKALA_CF).
 */
public class DiagnosaDetail {

    private int idDetail;
    private int idDiagnosa;
    private String kodeGejala;
    private String namaGejala; // hasil join tbl_gejala
    private double nilaiCf;
    private String kondisiGejala;

    public DiagnosaDetail() {
    }

    public DiagnosaDetail(String kodeGejala, double nilaiCf, String kondisiGejala) {
        this.kodeGejala = kodeGejala;
        this.nilaiCf = nilaiCf;
        this.kondisiGejala = kondisiGejala;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public int getIdDiagnosa() {
        return idDiagnosa;
    }

    public void setIdDiagnosa(int idDiagnosa) {
        this.idDiagnosa = idDiagnosa;
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

    public double getNilaiCf() {
        return nilaiCf;
    }

    public void setNilaiCf(double nilaiCf) {
        this.nilaiCf = nilaiCf;
    }

    public String getKondisiGejala() {
        return kondisiGejala;
    }

    public void setKondisiGejala(String kondisiGejala) {
        this.kondisiGejala = kondisiGejala;
    }
}
