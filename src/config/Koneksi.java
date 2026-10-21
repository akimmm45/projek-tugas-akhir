package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "db_posyandu_lansia_melati";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Jakarta"
            + "&useSSL=false&allowPublicKeyRetrieval=true";

    private static Connection connection;

    private Koneksi() {
      
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Driver MySQL (mysql-connector-j) tidak ditemukan di classpath project!\n" + e.getMessage(),
                    "Kesalahan Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database '" + DATABASE + "'.\n"
                    + "Pastikan MySQL Server sudah aktif dan database sudah di-import.\n\n"
                    + "Detail: " + e.getMessage(),
                    "Kesalahan Koneksi Database", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

 
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }

  
    public static boolean testConnection() {
        Connection c = getConnection();
        try {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
