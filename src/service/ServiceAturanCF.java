package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.AturanCF;

/**
 * Service untuk tabel tbl_aturan: basis pengetahuan Certainty Factor
 * (pasangan kategori hipertensi - gejala beserta nilai MB/MD). Dipakai oleh
 * FormAturanCF (CRUD aturan) dan ServiceCF (mesin perhitungan diagnosa).
 */
public class ServiceAturanCF {

    private static final String SELECT_JOIN =
            "SELECT a.*, p.nama_penyakit, g.nama_gejala FROM tbl_aturan a "
            + "JOIN tbl_penyakit p ON a.kode_penyakit = p.kode_penyakit "
            + "JOIN tbl_gejala g ON a.kode_gejala = g.kode_gejala ";

    public List<AturanCF> getAll() {
        List<AturanCF> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY a.kode_penyakit, a.kode_gejala";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data aturan CF: " + e.getMessage());
        }
        return list;
    }

    /** Mengambil semua aturan CF untuk satu kategori hipertensi tertentu (dipakai ServiceCF). */
    public List<AturanCF> getByKodePenyakit(String kodePenyakit) {
        List<AturanCF> list = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE a.kode_penyakit = ? ORDER BY a.kode_gejala";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodePenyakit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil aturan CF per kategori: " + e.getMessage());
        }
        return list;
    }

    public boolean simpan(AturanCF a) {
        String sql = "INSERT INTO tbl_aturan (kode_penyakit, kode_gejala, nilai_mb, nilai_md) VALUES (?,?,?,?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getKodePenyakit());
            ps.setString(2, a.getKodeGejala());
            ps.setDouble(3, a.getNilaiMb());
            ps.setDouble(4, a.getNilaiMd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan aturan CF: " + e.getMessage());
            return false;
        }
    }

    public boolean update(AturanCF a) {
        String sql = "UPDATE tbl_aturan SET kode_penyakit=?, kode_gejala=?, nilai_mb=?, nilai_md=? WHERE id_aturan=?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getKodePenyakit());
            ps.setString(2, a.getKodeGejala());
            ps.setDouble(3, a.getNilaiMb());
            ps.setDouble(4, a.getNilaiMd());
            ps.setInt(5, a.getIdAturan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengubah aturan CF: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(int idAturan) {
        String sql = "DELETE FROM tbl_aturan WHERE id_aturan = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAturan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus aturan CF: " + e.getMessage());
            return false;
        }
    }

    private AturanCF mapRow(ResultSet rs) throws SQLException {
        AturanCF a = new AturanCF();
        a.setIdAturan(rs.getInt("id_aturan"));
        a.setKodePenyakit(rs.getString("kode_penyakit"));
        a.setKodeGejala(rs.getString("kode_gejala"));
        a.setNilaiMb(rs.getDouble("nilai_mb"));
        a.setNilaiMd(rs.getDouble("nilai_md"));
        a.setNamaPenyakit(rs.getString("nama_penyakit"));
        a.setNamaGejala(rs.getString("nama_gejala"));
        return a;
    }
}
