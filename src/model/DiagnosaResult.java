package model;

/**
 * Model hasil perhitungan Certainty Factor untuk SATU kategori/tingkat
 * hipertensi (satu baris tbl_penyakit), dipakai oleh ServiceCF saat
 * menghitung seluruh kemungkinan lalu mengurutkannya dari CF tertinggi ke
 * terendah. Kategori dengan CF tertinggi menjadi "hasil_penyakit", sisanya
 * ditulis ke kolom "kemungkinan_lain" pada tbl_diagnosa.
 */
public class DiagnosaResult implements Comparable<DiagnosaResult> {

    private String kodePenyakit;
    private String namaPenyakit;
    private double nilaiCf;
    private String deskripsi;
    private String solusi;
    private String pencegahan;

    public DiagnosaResult() {
    }

    public DiagnosaResult(String kodePenyakit, String namaPenyakit, double nilaiCf) {
        this.kodePenyakit = kodePenyakit;
        this.namaPenyakit = namaPenyakit;
        this.nilaiCf = nilaiCf;
    }

    public String getKodePenyakit() {
        return kodePenyakit;
    }

    public void setKodePenyakit(String kodePenyakit) {
        this.kodePenyakit = kodePenyakit;
    }

    public String getNamaPenyakit() {
        return namaPenyakit;
    }

    public void setNamaPenyakit(String namaPenyakit) {
        this.namaPenyakit = namaPenyakit;
    }

    public double getNilaiCf() {
        return nilaiCf;
    }

    public void setNilaiCf(double nilaiCf) {
        this.nilaiCf = nilaiCf;
    }

    /** Nilai CF dalam bentuk persen (0-100), dibulatkan 2 desimal untuk ditampilkan. */
    public double getPersen() {
        return Math.round(nilaiCf * 10000.0) / 100.0;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getSolusi() {
        return solusi;
    }

    public void setSolusi(String solusi) {
        this.solusi = solusi;
    }

    public String getPencegahan() {
        return pencegahan;
    }

    public void setPencegahan(String pencegahan) {
        this.pencegahan = pencegahan;
    }

    /** Diurutkan menurun berdasarkan nilai CF (hasil tertinggi berada di indeks 0). */
    @Override
    public int compareTo(DiagnosaResult other) {
        return Double.compare(other.nilaiCf, this.nilaiCf);
    }

    @Override
    public String toString() {
        return namaPenyakit + " (CF: " + getPersen() + "%)";
    }
}
