package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Gejala;

/**
 * Service untuk tabel tbl_gejala (dipakai oleh FormGejala untuk CRUD, dan
 * oleh FormAturanCF & FormDiagnosa untuk menampilkan daftar pilihan gejala).
 */
public class ServiceGejala {

    public List<Gejala> getAll() {
        List<Gejala> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_gejala ORDER BY kode_gejala";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data gejala: " + e.getMessage());
        }
        return list;
    }

    public Gejala getByKode(String kodeGejala) {
        String sql = "SELECT * FROM tbl_gejala WHERE kode_gejala = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil gejala: " + e.getMessage());
        }
        return null;
    }

    /** Menghasilkan kode gejala baru berurutan, mis. G31, G32, dst. */
    public String generateKodeBaru() {
        String sql = "SELECT kode_gejala FROM tbl_gejala ORDER BY CAST(SUBSTRING(kode_gejala,2) AS UNSIGNED) DESC LIMIT 1";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int nomor = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                return String.format("G%02d", nomor);
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuat kode gejala baru: " + e.getMessage());
        }
        return "G01";
    }

    public boolean simpan(Gejala g) {
        String sql = "INSERT INTO tbl_gejala (kode_gejala, nama_gejala, kategori_gejala) VALUES (?,?,?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getKodeGejala());
            ps.setString(2, g.getNamaGejala());
            ps.setString(3, g.getKategoriGejala());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan gejala: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Gejala g) {
        String sql = "UPDATE tbl_gejala SET nama_gejala=?, kategori_gejala=? WHERE kode_gejala=?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getNamaGejala());
            ps.setString(2, g.getKategoriGejala());
            ps.setString(3, g.getKodeGejala());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengubah gejala: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(String kodeGejala) {
        String sql = "DELETE FROM tbl_gejala WHERE kode_gejala = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus gejala (kemungkinan masih dipakai di tbl_aturan): " + e.getMessage());
            return false;
        }
    }

    private Gejala mapRow(ResultSet rs) throws SQLException {
        return new Gejala(rs.getString("kode_gejala"), rs.getString("nama_gejala"), rs.getString("kategori_gejala"));
    }
}
