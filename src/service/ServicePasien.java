package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Pasien;

/**
 * Service untuk tabel tbl_pasien: CRUD data induk lansia anggota
 * Posyandu Lansia Melati (dipakai oleh FormPasien dan combo pasien pada
 * FormDiagnosa).
 */
public class ServicePasien {

    public List<Pasien> getAll() {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_pasien ORDER BY nama";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data pasien: " + e.getMessage());
        }
        return list;
    }

    public List<Pasien> cari(String kataKunci) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_pasien WHERE nama LIKE ? OR nik LIKE ? ORDER BY nama";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + kataKunci + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mencari pasien: " + e.getMessage());
        }
        return list;
    }

    public Pasien getById(int idPasien) {
        String sql = "SELECT * FROM tbl_pasien WHERE id_pasien = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil pasien: " + e.getMessage());
        }
        return null;
    }

    public boolean simpan(Pasien p) {
        String sql = "INSERT INTO tbl_pasien (nik, nama, tanggal_lahir, jenis_kelamin, alamat, rt, rw, no_hp) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindParams(ps, p);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan pasien: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Pasien p) {
        String sql = "UPDATE tbl_pasien SET nik=?, nama=?, tanggal_lahir=?, jenis_kelamin=?, "
                + "alamat=?, rt=?, rw=?, no_hp=? WHERE id_pasien=?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            bindParams(ps, p);
            ps.setInt(9, p.getIdPasien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengubah data pasien: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(int idPasien) {
        String sql = "DELETE FROM tbl_pasien WHERE id_pasien = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPasien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus pasien (kemungkinan masih punya riwayat diagnosa): " + e.getMessage());
            return false;
        }
    }

    public int jumlahPasien() {
        String sql = "SELECT COUNT(*) FROM tbl_pasien";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Gagal menghitung jumlah pasien: " + e.getMessage());
        }
        return 0;
    }

    private void bindParams(PreparedStatement ps, Pasien p) throws SQLException {
        ps.setString(1, p.getNik());
        ps.setString(2, p.getNama());
        ps.setDate(3, p.getTanggalLahir() != null ? Date.valueOf(p.getTanggalLahir()) : null);
        ps.setString(4, p.getJenisKelamin());
        ps.setString(5, p.getAlamat());
        ps.setString(6, p.getRt());
        ps.setString(7, p.getRw());
        ps.setString(8, p.getNoHp());
    }

    private Pasien mapRow(ResultSet rs) throws SQLException {
        Pasien p = new Pasien();
        p.setIdPasien(rs.getInt("id_pasien"));
        p.setNik(rs.getString("nik"));
        p.setNama(rs.getString("nama"));
        if (rs.getDate("tanggal_lahir") != null) {
            p.setTanggalLahir(rs.getDate("tanggal_lahir").toLocalDate());
        }
        p.setJenisKelamin(rs.getString("jenis_kelamin"));
        p.setAlamat(rs.getString("alamat"));
        p.setRt(rs.getString("rt"));
        p.setRw(rs.getString("rw"));
        p.setNoHp(rs.getString("no_hp"));
        if (rs.getTimestamp("tanggal_daftar") != null) {
            p.setTanggalDaftar(rs.getTimestamp("tanggal_daftar").toLocalDateTime());
        }
        return p;
    }
}
