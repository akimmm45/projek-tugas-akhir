package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.AturanCF;
import model.Diagnosa;
import model.DiagnosaDetail;
import model.DiagnosaResult;
import model.Penyakit;

/**
 * ServiceCF adalah mesin utama metode Certainty Factor pada aplikasi ini:
 * <ol>
 *   <li>{@link #hitungDiagnosa(Map)} - menghitung CF gabungan setiap kategori
 *       hipertensi (tbl_penyakit) dari kumpulan gejala yang dipilih pengguna
 *       beserta tingkat keyakinannya, lalu mengurutkannya dari CF tertinggi
 *       ke terendah.</li>
 *   <li>{@link #simpanDiagnosa} - menyimpan hasil yang sudah dihitung ke
 *       tbl_diagnosa dan tbl_diagnosa_detail.</li>
 * </ol>
 *
 * <p><b>Rumus Certainty Factor yang dipakai</b> (Shortliffe &amp; Buchanan):</p>
 * <pre>
 *   CF(rule)          = MB - MD
 *   CF(evidence,H)     = CF(rule) x CF(user)
 *   CFcombine(CF1,CF2):
 *      CF1&gt;=0 dan CF2&gt;=0  : CF1 + CF2 * (1 - CF1)
 *      CF1&lt;0  dan CF2&lt;0   : CF1 + CF2 * (1 + CF1)
 *      lainnya (beda tanda): (CF1 + CF2) / (1 - min(|CF1|,|CF2|))
 * </pre>
 */
public class ServiceCF {

    /** Skala tingkat keyakinan pengguna terhadap satu gejala (dipakai FormDiagnosa). */
    public static final LinkedHashMap<String, Double> SKALA_CF = new LinkedHashMap<>();

    static {
        SKALA_CF.put("Tidak (0.0)", 0.0);
        SKALA_CF.put("Sedikit Yakin (0.2)", 0.2);
        SKALA_CF.put("Cukup Yakin (0.4)", 0.4);
        SKALA_CF.put("Yakin (0.6)", 0.6);
        SKALA_CF.put("Sangat Yakin (0.8)", 0.8);
        SKALA_CF.put("Pasti (1.0)", 1.0);
    }

    private final ServicePenyakit servicePenyakit = new ServicePenyakit();
    private final ServiceAturanCF serviceAturanCF = new ServiceAturanCF();

    // ------------------------------------------------------------------
    // 1) PERHITUNGAN CERTAINTY FACTOR
    // ------------------------------------------------------------------

    /**
     * Menghitung nilai CF gabungan untuk setiap kategori hipertensi
     * berdasarkan peta {kode_gejala -> nilai CF keyakinan pengguna (0-1)}.
     * Hanya kategori dengan CF akhir &gt; 0 yang dikembalikan, terurut
     * menurun (kandidat terkuat pada indeks ke-0).
     */
    public List<DiagnosaResult> hitungDiagnosa(Map<String, Double> selectedGejala) {
        List<DiagnosaResult> hasil = new ArrayList<>();
        if (selectedGejala == null || selectedGejala.isEmpty()) {
            return hasil;
        }

        for (Penyakit kategori : servicePenyakit.getAll()) {
            List<AturanCF> aturanList = serviceAturanCF.getByKodePenyakit(kategori.getKodePenyakit());
            Double cfKategori = null;

            for (AturanCF aturan : aturanList) {
                Double cfUser = selectedGejala.get(aturan.getKodeGejala());
                if (cfUser == null || cfUser <= 0) {
                    continue; // gejala ini tidak dipilih / tidak dialami
                }
                double cfRule = aturan.getNilaiMb() - aturan.getNilaiMd();
                double cfEvidence = cfRule * cfUser;
                cfKategori = (cfKategori == null) ? cfEvidence : combineCF(cfKategori, cfEvidence);
            }

            if (cfKategori != null && cfKategori > 0) {
                DiagnosaResult dr = new DiagnosaResult(kategori.getKodePenyakit(), kategori.getNamaPenyakit(), cfKategori);
                dr.setDeskripsi(kategori.getDeskripsi());
                dr.setSolusi(kategori.getSolusi());
                dr.setPencegahan(kategori.getPencegahan());
                hasil.add(dr);
            }
        }

        java.util.Collections.sort(hasil);
        return hasil;
    }

    /** Rumus kombinasi CF standar (Shortliffe &amp; Buchanan). */
    public double combineCF(double cf1, double cf2) {
        if (cf1 >= 0 && cf2 >= 0) {
            return cf1 + cf2 * (1 - cf1);
        } else if (cf1 < 0 && cf2 < 0) {
            return cf1 + cf2 * (1 + cf1);
        } else {
            double penyebut = 1 - Math.min(Math.abs(cf1), Math.abs(cf2));
            return penyebut == 0 ? 0 : (cf1 + cf2) / penyebut;
        }
    }

    /** Label skala keyakinan terdekat untuk sebuah nilai CF numerik (untuk kolom kondisi_gejala). */
    public String labelDariNilaiCf(double nilaiCf) {
        String terdekat = "Tidak (0.0)";
        double selisihTerkecil = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : SKALA_CF.entrySet()) {
            double selisih = Math.abs(entry.getValue() - nilaiCf);
            if (selisih < selisihTerkecil) {
                selisihTerkecil = selisih;
                terdekat = entry.getKey();
            }
        }
        return terdekat;
    }

    // ------------------------------------------------------------------
    // 2) PENYIMPANAN HASIL DIAGNOSA
    // ------------------------------------------------------------------

    /**
     * Menyimpan hasil diagnosa yang sudah dihitung {@link #hitungDiagnosa}.
     * Signature ini dipertahankan sesuai pemanggilan pada
     * FormDiagnosa.simpanHasil(): {@code cfService.simpanDiagnosa(pasien.getIdPasien(), results, selectedGejala, idUser)}.
     */
    public boolean simpanDiagnosa(int idPasien, List<DiagnosaResult> results, Map<String, Double> selectedGejala, int idUser) {
        return simpanDiagnosa(idPasien, results, selectedGejala, idUser, null, null);
    }

    /** Varian lengkap: menyertakan hasil ukur tensi aktual (boleh null bila tidak diisi). */
    public boolean simpanDiagnosa(int idPasien, List<DiagnosaResult> results, Map<String, Double> selectedGejala,
            int idUser, Integer tensiSistolik, Integer tensiDiastolik) {

        if (results == null || results.isEmpty()) {
            return false;
        }

        Connection con = null;
        try {
            con = Koneksi.getConnection();
            con.setAutoCommit(false);

            DiagnosaResult top = results.get(0);
            int idDiagnosaBaru;

            String sqlDiagnosa = "INSERT INTO tbl_diagnosa "
                    + "(id_user, id_pasien, tensi_sistolik, tensi_diastolik, hasil_penyakit, confidence, kemungkinan_lain) "
                    + "VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlDiagnosa, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUser);
                ps.setInt(2, idPasien);
                if (tensiSistolik != null) {
                    ps.setInt(3, tensiSistolik);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                if (tensiDiastolik != null) {
                    ps.setInt(4, tensiDiastolik);
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setString(5, top.getKodePenyakit());
                ps.setDouble(6, top.getPersen());
                ps.setString(7, formatKemungkinanLain(results));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        idDiagnosaBaru = keys.getInt(1);
                    } else {
                        throw new SQLException("Gagal memperoleh id_diagnosa baru");
                    }
                }
            }

            String sqlDetail = "INSERT INTO tbl_diagnosa_detail (id_diagnosa, kode_gejala, nilai_cf, kondisi_gejala) VALUES (?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlDetail)) {
                for (Map.Entry<String, Double> entry : selectedGejala.entrySet()) {
                    if (entry.getValue() == null || entry.getValue() <= 0) {
                        continue;
                    }
                    ps.setInt(1, idDiagnosaBaru);
                    ps.setString(2, entry.getKey());
                    ps.setDouble(3, entry.getValue());
                    ps.setString(4, labelDariNilaiCf(entry.getValue()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan diagnosa: " + e.getMessage());
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Gagal rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Gagal mengembalikan auto-commit: " + e.getMessage());
            }
        }
    }

    /** Format teks kemungkinan lain, meniru gaya "- Nama (Akurasi: xx.xx %)" seperti pada db_ikan_nila.sql. */
    private String formatKemungkinanLain(List<DiagnosaResult> results) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < results.size(); i++) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            DiagnosaResult r = results.get(i);
            sb.append("- ").append(r.getNamaPenyakit()).append(" (Akurasi: ")
                    .append(String.format("%.2f", r.getPersen())).append(" %)");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // 3) RIWAYAT & LAPORAN
    // ------------------------------------------------------------------

    private static final String SELECT_RIWAYAT =
            "SELECT d.*, ps.nama AS nama_pasien, pk.nama_penyakit AS nama_hasil "
            + "FROM tbl_diagnosa d "
            + "JOIN tbl_pasien ps ON d.id_pasien = ps.id_pasien "
            + "LEFT JOIN tbl_penyakit pk ON d.hasil_penyakit = pk.kode_penyakit ";

    public List<Diagnosa> getAllRiwayat() {
        List<Diagnosa> list = new ArrayList<>();
        String sql = SELECT_RIWAYAT + "ORDER BY d.tanggal_diagnosa DESC";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowDiagnosa(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil riwayat diagnosa: " + e.getMessage());
        }
        return list;
    }

    public List<Diagnosa> getRiwayatByPasien(int idPasien) {
        List<Diagnosa> list = new ArrayList<>();
        String sql = SELECT_RIWAYAT + "WHERE d.id_pasien = ? ORDER BY d.tanggal_diagnosa DESC";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowDiagnosa(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil riwayat diagnosa pasien: " + e.getMessage());
        }
        return list;
    }

    public List<DiagnosaDetail> getDetailByDiagnosa(int idDiagnosa) {
        List<DiagnosaDetail> list = new ArrayList<>();
        String sql = "SELECT dd.*, g.nama_gejala FROM tbl_diagnosa_detail dd "
                + "JOIN tbl_gejala g ON dd.kode_gejala = g.kode_gejala WHERE dd.id_diagnosa = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDiagnosa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiagnosaDetail d = new DiagnosaDetail();
                    d.setIdDetail(rs.getInt("id_detail"));
                    d.setIdDiagnosa(rs.getInt("id_diagnosa"));
                    d.setKodeGejala(rs.getString("kode_gejala"));
                    d.setNamaGejala(rs.getString("nama_gejala"));
                    d.setNilaiCf(rs.getDouble("nilai_cf"));
                    d.setKondisiGejala(rs.getString("kondisi_gejala"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil detail diagnosa: " + e.getMessage());
        }
        return list;
    }

    /** Statistik ringkas untuk FormDashboard: jumlah diagnosa per kategori hipertensi. */
    public Map<String, Integer> getJumlahDiagnosaPerKategori() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT pk.nama_penyakit, COUNT(*) AS jumlah FROM tbl_diagnosa d "
                + "JOIN tbl_penyakit pk ON d.hasil_penyakit = pk.kode_penyakit "
                + "GROUP BY pk.kode_penyakit, pk.nama_penyakit ORDER BY pk.kode_penyakit";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("nama_penyakit"), rs.getInt("jumlah"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil statistik kategori: " + e.getMessage());
        }
        return map;
    }

    public int getJumlahDiagnosaHariIni() {
        String sql = "SELECT COUNT(*) FROM tbl_diagnosa WHERE DATE(tanggal_diagnosa) = CURDATE()";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Gagal menghitung diagnosa hari ini: " + e.getMessage());
        }
        return 0;
    }

    public int getJumlahDiagnosaBulanIni() {
        String sql = "SELECT COUNT(*) FROM tbl_diagnosa WHERE YEAR(tanggal_diagnosa) = YEAR(CURDATE()) "
                + "AND MONTH(tanggal_diagnosa) = MONTH(CURDATE())";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Gagal menghitung diagnosa bulan ini: " + e.getMessage());
        }
        return 0;
    }

    private Diagnosa mapRowDiagnosa(ResultSet rs) throws SQLException {
        Diagnosa d = new Diagnosa();
        d.setIdDiagnosa(rs.getInt("id_diagnosa"));
        int idUser = rs.getInt("id_user");
        d.setIdUser(rs.wasNull() ? null : idUser);
        d.setIdPasien(rs.getInt("id_pasien"));
        d.setNamaPasien(rs.getString("nama_pasien"));
        if (rs.getTimestamp("tanggal_diagnosa") != null) {
            d.setTanggalDiagnosa(rs.getTimestamp("tanggal_diagnosa").toLocalDateTime());
        }
        int sistol = rs.getInt("tensi_sistolik");
        d.setTensiSistolik(rs.wasNull() ? null : sistol);
        int diastol = rs.getInt("tensi_diastolik");
        d.setTensiDiastolik(rs.wasNull() ? null : diastol);
        d.setHasilPenyakit(rs.getString("hasil_penyakit"));
        d.setNamaHasilPenyakit(rs.getString("nama_hasil"));
        d.setConfidence(rs.getDouble("confidence"));
        d.setKemungkinanLain(rs.getString("kemungkinan_lain"));
        return d;
    }
}
