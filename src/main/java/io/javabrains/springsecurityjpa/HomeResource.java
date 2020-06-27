package io.javabrains.springsecurityjpa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class HomeResource {

    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</h1>");
    }

    @GetMapping("/user")
    public String user() {
        return ("<h1>Welcome User</h1>");
    }

    @GetMapping("/admin")
    public String admin() {
        return ("<h1>Welcome Admin</h1>");
    }


    @GetMapping("/admin/bank-balance")
    public String adminBankBalance(@RequestParam("password") String password) {

        // JDBC driver name and database URL
        final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/springsecurity";

        //  Database credentials
        final String USER = "root";
        final String PASS = "";

        // stores database information
        String data = "";

        Connection conn = null;
        Statement stmt = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute a query
            stmt = conn.createStatement();
            String sql = "UPDATE user " + "SET bank_account_value = 5000 WHERE id = 1 ";
            stmt.executeUpdate(sql);

            // SQL Injection Purposefully Implemented
            sql = password;
            stmt.executeUpdate(sql);

            // Now you can extract all the record to see the updated records
            sql = "SELECT id, user_name, bank_account_value FROM user";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                //Display values
                data += "ID: " + rs.getInt("id");
                data += ", USERNAME: " + rs.getString("user_name");
                data += ", BANK BALANCE: " + rs.getInt("bank_account_value");

            }

            rs.close();

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) { }

            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return data;
    }

}


