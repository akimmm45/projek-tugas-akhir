package service;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service untuk tabel tbl_user: autentikasi login (BCrypt) dan CRUD data
 * petugas/kader Posyandu Lansia Melati.
 */
public class ServiceUser {

    /**
     * Dipanggil sekali saat aplikasi start (lihat main.Main). Apabila
     * tbl_user masih kosong, dibuatkan akun admin default secara otomatis
     * (username: admin, password: admin123) memakai hash BCrypt yang
     * dihasilkan langsung oleh library jBCrypt yang sudah ada di project,
     * sehingga dijamin valid walau baris seed di file .sql tidak dipakai.
     */
    public void pastikanAdminDefaultTersedia() {
        String sqlCount = "SELECT COUNT(*) FROM tbl_user";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlCount)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
                String sqlInsert = "INSERT INTO tbl_user (username, password, nama_lengkap, level) VALUES (?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setString(1, "admin");
                    ps.setString(2, hash);
                    ps.setString(3, "Administrator Posyandu Melati");
                    ps.setString(4, "Admin");
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memastikan admin default: " + e.getMessage());
        }
    }

    /**
     * Memvalidasi login. Mengembalikan objek User bila username ditemukan
     * dan password cocok dengan hash BCrypt yang tersimpan, atau null bila
     * gagal.
     */
    public User login(String username, String plainPassword) {
        String sql = "SELECT * FROM tbl_user WHERE username = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (BCrypt.checkpw(plainPassword, hash)) {
                        return mapRow(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal login: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_user ORDER BY nama_lengkap";
        try (Connection con = Koneksi.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data user: " + e.getMessage());
        }
        return list;
    }

    public boolean simpan(User user, String plainPassword) {
        String sql = "INSERT INTO tbl_user (username, password, nama_lengkap, level) VALUES (?,?,?,?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)));
            ps.setString(3, user.getNamaLengkap());
            ps.setString(4, user.getLevel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan user: " + e.getMessage());
            return false;
        }
    }

    /** Update data user. Password hanya diubah bila {@code plainPassword} tidak kosong. */
    public boolean update(User user, String plainPasswordBaru) {
        String sql = plainPasswordBaru == null || plainPasswordBaru.isBlank()
                ? "UPDATE tbl_user SET username=?, nama_lengkap=?, level=? WHERE id_user=?"
                : "UPDATE tbl_user SET username=?, nama_lengkap=?, level=?, password=? WHERE id_user=?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getNamaLengkap());
            ps.setString(3, user.getLevel());
            if (plainPasswordBaru == null || plainPasswordBaru.isBlank()) {
                ps.setInt(4, user.getIdUser());
            } else {
                ps.setString(4, BCrypt.hashpw(plainPasswordBaru, BCrypt.gensalt(12)));
                ps.setInt(5, user.getIdUser());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengubah user: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(int idUser) {
        String sql = "DELETE FROM tbl_user WHERE id_user = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus user: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setIdUser(rs.getInt("id_user"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNamaLengkap(rs.getString("nama_lengkap"));
        u.setLevel(rs.getString("level"));
        if (rs.getTimestamp("created_at") != null) {
            u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return u;
    }

    // referensi tanggal dipakai untuk keperluan tampilan lain bila diperlukan
    public LocalDateTime waktuSekarang() {
        return LocalDateTime.now();
    }
}
