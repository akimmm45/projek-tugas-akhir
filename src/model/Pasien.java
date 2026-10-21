package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Model untuk tabel tbl_pasien: data induk lansia yang terdaftar sebagai
 * anggota Posyandu Lansia Melati. Kelas ini menggantikan peran
 * "kode_sampel" (String bebas) pada db_ikan_nila.sql, karena setiap lansia
 * di sini adalah entitas tetap yang bisa diperiksa berkali-kali.
 */
public class Pasien {

    private int idPasien;
    private String nik;
    private String nama;
    private LocalDate tanggalLahir;
    private String jenisKelamin; // "L" atau "P"
    private String alamat;
    private String rt;
    private String rw;
    private String noHp;
    private LocalDateTime tanggalDaftar;

    public Pasien() {
    }

    public Pasien(int idPasien, String nama) {
        this.idPasien = idPasien;
        this.nama = nama;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public LocalDateTime getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(LocalDateTime tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    /** Menghitung umur (tahun) berdasarkan tanggal lahir dan tanggal hari ini. */
    public int getUmur() {
        if (tanggalLahir == null) {
            return 0;
        }
        return Period.between(tanggalLahir, LocalDate.now()).getYears();
    }

    public String getJenisKelaminLabel() {
        return "L".equalsIgnoreCase(jenisKelamin) ? "Laki-laki" : "Perempuan";
    }

    /**
     * Dipakai sebagai teks tampilan pada JComboBox (mis. cmbPasien di
     * FormDiagnosa) dan komponen list/combo lainnya.
     */
    @Override
    public String toString() {
        return nama + (nik != null && !nik.isBlank() ? " - " + nik : "");
    }
}
