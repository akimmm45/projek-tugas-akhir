package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Penyakit;

/**
 * Service untuk tabel tbl_penyakit. Nama kelas dipertahankan sesuai project
 * asal (dipakai oleh FormPenyakit.java); isi data adalah kategori/tingkat
 * hipertensi (Normal, Pra-Hipertensi, Hipertensi Tingkat 1/2/3).
 */
public class ServicePenyakit {

    public List<Penyakit> getAll() {
        List<Penyakit> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_penyakit ORDER BY kode_penyakit";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data kategori hipertensi: " + e.getMessage());
        }
        return list;
    }

    public Penyakit getByKode(String kodePenyakit) {
        String sql = "SELECT * FROM tbl_penyakit WHERE kode_penyakit = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodePenyakit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil kategori hipertensi: " + e.getMessage());
        }
        return null;
    }

    public String generateKodeBaru() {
        String sql = "SELECT kode_penyakit FROM tbl_penyakit ORDER BY CAST(SUBSTRING(kode_penyakit,2) AS UNSIGNED) DESC LIMIT 1";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int nomor = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                return String.format("P%02d", nomor);
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuat kode kategori baru: " + e.getMessage());
        }
        return "P01";
    }

    public boolean simpan(Penyakit p) {
        String sql = "INSERT INTO tbl_penyakit (kode_penyakit, nama_penyakit, Deskripsi, solusi, pencegahan) VALUES (?,?,?,?,?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindParams(ps, p);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan kategori hipertensi: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Penyakit p) {
        String sql = "UPDATE tbl_penyakit SET nama_penyakit=?, Deskripsi=?, solusi=?, pencegahan=? WHERE kode_penyakit=?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNamaPenyakit());
            ps.setString(2, p.getDeskripsi());
            ps.setString(3, p.getSolusi());
            ps.setString(4, p.getPencegahan());
            ps.setString(5, p.getKodePenyakit());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengubah kategori hipertensi: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(String kodePenyakit) {
        String sql = "DELETE FROM tbl_penyakit WHERE kode_penyakit = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kodePenyakit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus kategori (kemungkinan masih dipakai di tbl_aturan/tbl_diagnosa): " + e.getMessage());
            return false;
        }
    }

    private void bindParams(PreparedStatement ps, Penyakit p) throws SQLException {
        ps.setString(1, p.getKodePenyakit());
        ps.setString(2, p.getNamaPenyakit());
        ps.setString(3, p.getDeskripsi());
        ps.setString(4, p.getSolusi());
        ps.setString(5, p.getPencegahan());
    }

    private Penyakit mapRow(ResultSet rs) throws SQLException {
        Penyakit p = new Penyakit();
        p.setKodePenyakit(rs.getString("kode_penyakit"));
        p.setNamaPenyakit(rs.getString("nama_penyakit"));
        p.setDeskripsi(rs.getString("Deskripsi"));
        p.setSolusi(rs.getString("solusi"));
        p.setPencegahan(rs.getString("pencegahan"));
        return p;
    }
}
