package main;

public enum AllForms {

    DASHBOARD("Dashboard", "icon/dashboard.svg"),
    PASIEN("Data Lansia", "icon/pasien.svg"),
    GEJALA("Data Gejala", "icon/gejala.svg"),
    ATURAN_CF("Aturan Certainty Factor", "icon/aturan.svg"),
    PENYAKIT("Kategori Hipertensi", "icon/penyakit.svg"),
    DIAGNOSA("Diagnosa Baru", "icon/diagnosa.svg"),
    RIWAYAT("Riwayat Diagnosa", "icon/riwayat.svg"),
    USER("Manajemen User", "icon/user.svg"),
    LAPORAN_PASIEN("Laporan Data Lansia", "icon/laporan_pasien.svg"),
    LAPORAN_GEJALA("Laporan Data Gejala", "icon/laporan_gejala.svg"),
    LAPORAN_DIAGNOSA("Laporan Hasil Diagnosis", "icon/laporan_diagnosa.svg"),
    LAPORAN_REKAP("Rekap Bulanan", "icon/laporan_rekap.svg");

    private final String label;
    private final String iconPath;

    AllForms(String label, String iconPath) {
        this.label = label;
        this.iconPath = iconPath;
    }

    public String getLabel() {
        return label;
    }

    public String getIconPath() {
        return iconPath;
    }
}