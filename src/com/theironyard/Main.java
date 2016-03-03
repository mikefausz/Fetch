package com.theironyard;

import spark.Spark;

import java.sql.*;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR");
        stmt.execute("CREATE TABLE IF NOT EXISTS drivers (id IDENTITY, name VARCHAR");
    }
    public static void insertUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }
    public static void insertDriver(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO drivers VALUES (NULL, ?");
        stmt.setString(1, name);
        stmt.execute();
    }
    public static User selectUser(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        results.next();
        User user = new User(results.getInt("id"), results.getString("name"));
        return user;
    }
    public static Driver selectDriver(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        results.next();
        Driver driver = new Driver(results.getInt("id"), results.getString("name"));
        return driver;
    }



    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        createTables(conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
    }
}
