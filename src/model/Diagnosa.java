package model;

import java.time.LocalDateTime;

/**
 * Model untuk tabel tbl_diagnosa: satu baris riwayat pemeriksaan/diagnosa
 * yang sudah tersimpan untuk seorang pasien (lansia). Dipakai terutama oleh
 * FormRiwayatDiagnosa dan bagian riwayat pada FormPasien.
 */
public class Diagnosa {

    private int idDiagnosa;
    private Integer idUser;
    private int idPasien;
    private String namaPasien;   // hasil join tbl_pasien
    private LocalDateTime tanggalDiagnosa;
    private Integer tensiSistolik;
    private Integer tensiDiastolik;
    private String hasilPenyakit;      // kode_penyakit
    private String namaHasilPenyakit;  // hasil join tbl_penyakit
    private double confidence;
    private String kemungkinanLain;

    public int getIdDiagnosa() {
        return idDiagnosa;
    }

    public void setIdDiagnosa(int idDiagnosa) {
        this.idDiagnosa = idDiagnosa;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public LocalDateTime getTanggalDiagnosa() {
        return tanggalDiagnosa;
    }

    public void setTanggalDiagnosa(LocalDateTime tanggalDiagnosa) {
        this.tanggalDiagnosa = tanggalDiagnosa;
    }

    public Integer getTensiSistolik() {
        return tensiSistolik;
    }

    public void setTensiSistolik(Integer tensiSistolik) {
        this.tensiSistolik = tensiSistolik;
    }

    public Integer getTensiDiastolik() {
        return tensiDiastolik;
    }

    public void setTensiDiastolik(Integer tensiDiastolik) {
        this.tensiDiastolik = tensiDiastolik;
    }

    public String getTensiLabel() {
        if (tensiSistolik == null || tensiDiastolik == null) {
            return "-";
        }
        return tensiSistolik + "/" + tensiDiastolik + " mmHg";
    }

    public String getHasilPenyakit() {
        return hasilPenyakit;
    }

    public void setHasilPenyakit(String hasilPenyakit) {
        this.hasilPenyakit = hasilPenyakit;
    }

    public String getNamaHasilPenyakit() {
        return namaHasilPenyakit;
    }

    public void setNamaHasilPenyakit(String namaHasilPenyakit) {
        this.namaHasilPenyakit = namaHasilPenyakit;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getKemungkinanLain() {
        return kemungkinanLain;
    }

    public void setKemungkinanLain(String kemungkinanLain) {
        this.kemungkinanLain = kemungkinanLain;
    }
}

