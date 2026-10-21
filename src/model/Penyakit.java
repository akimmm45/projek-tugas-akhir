package model;

/**
 * Model untuk tabel tbl_penyakit. Nama kelas & tabel dipertahankan sama
 * seperti project asal (FormPenyakit.java) agar tidak perlu me-rename file
 * yang sudah dibuat sebelumnya; secara isi, satu baris "penyakit" di sini
 * merepresentasikan satu KATEGORI/TINGKAT HIPERTENSI (Normal, Pra-Hipertensi,
 * Hipertensi Tingkat 1/2/3), bukan nama penyakit ikan seperti pada
 * db_ikan_nila.sql.
 */
public class Penyakit {

    private String kodePenyakit;
    private String namaPenyakit;
    private String deskripsi;
    private String solusi;
    private String pencegahan;

    public Penyakit() {
    }

    public Penyakit(String kodePenyakit, String namaPenyakit) {
        this.kodePenyakit = kodePenyakit;
        this.namaPenyakit = namaPenyakit;
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

    @Override
    public String toString() {
        return kodePenyakit + " - " + namaPenyakit;
    }
}
