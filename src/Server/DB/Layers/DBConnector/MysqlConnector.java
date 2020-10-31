/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.DB.Layers.DBConnector;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author nguye
 */
public class MysqlConnector {

    PreparedStatement stm = null;
    Connection conn = null;
    ResultSet rs = null;

    String server = "localhost:3306";
    String db = "CaroDB";
    String user = "root";
    String pass = "123456abc";

    public MysqlConnector() {
        connectDB();
    }

    public void connectDB() {
        checkDriver();
        setupConnection();
    }

    public void logIn(String userName, String pass) {
        this.user = userName;
        this.pass = pass;
        setupConnection();
    }

    public void connectDB(String server, String db, String user, String pass) {
        checkDriver();
        this.server = server;
        this.db = db;
        this.user = user;
        this.pass = pass;
        setupConnection();
    }

    boolean checkDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Khong tim thay Driver mysql !!");
            return false;
        }
    }

    public boolean setupConnection() {
        try {
            String url = "jdbc:mysql://" + server + "/" + db;
            conn = DriverManager.getConnection(url, user, pass);
            return true;
        } catch (SQLException e) {
            System.err.println("Loi ket noi DB: " + e.getMessage());
            return false;
        }
    }

    public ResultSet sqlQry(PreparedStatement stm) {
        if (checkConnection()) {
            try {
                rs = stm.executeQuery();
                return rs;
            } catch (SQLException e) {
                System.err.println("Loi thuc thi query !!");
            }
        }
        return null;
    }

    public boolean sqlUpdate(PreparedStatement stm) {
        if (checkConnection()) {
            try {
                stm.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("Gia tri cua khoa vua nhap khong ton tai !!");
                // System.err.println(e);
            }
        }
        return false;
    }
    
    public boolean checkConnection() {
        if (conn == null ) {
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stm != null) {
                stm.close();
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("-- ERROR! Không thể đóng kết nối tới " + db);
            return false;
        }
    }

    public Connection getConnection() {
        return conn;
    }
    

}
