package com.theironyard;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by PiratePowWow on 3/4/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testSelectUser() throws Exception {
        Connection conn = startConnection();
        Main.insertUser(conn, "Bob");
        User user = Main.selectUser(conn, "Bob");
        conn.close();
        assertTrue(user.getName().equals("Bob"));
    }

    @Test
    public void testSelectDriver() throws Exception {
        Connection conn = startConnection();
        Main.insertDriver(conn, "Bob");
        Driver driver = Main.selectDriver(conn, "Bob");
        conn.close();
        assertTrue(driver.getName().equals("Bob"));
    }

    @Test
    public void testSelectUserRequests() throws Exception {
        Connection conn = startConnection();
        Main.insertUser(conn, "Bob");
        Main.insertRequest(conn, 1, "get");
        ArrayList<Request> requests = Main.selectUserRequests(conn, 1);
        conn.close();
        assertTrue(requests.size()==1);
    }

    @Test
    public void testSelectDriverRequests() throws Exception {
        Connection conn = startConnection();
        Main.insertUser(conn, "Bob");
        Main.insertRequest(conn, 1, "get");
        Main.insertDriver(conn, "Bob");
        Main.updateStatus(conn, 1, "Closed", 1);
        ArrayList<Request> requests = Main.selectUserRequests(conn, 1);
        conn.close();
        assertTrue(requests.size()==1);
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Connection conn = startConnection();
        Main.insertUser(conn, "Bob");
        Main.insertRequest(conn, 1, "get");
        Main.updateStatus(conn, 1, "Closed", 1);
        ArrayList<Request> requests = Main.selectUserRequests(conn, 1);
        conn.close();
        assertTrue(requests.get(0).getStatus().equals("Closed"));
    }

    @Test
    public void testDeleteUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Bob");
        Main.deleteUser(conn, new User(1, "Bob"));
        assertTrue(Main.usersSize(conn)==0);
        conn.close();
    }

    @Test
    public void testDeleteDriver() throws SQLException {
        Connection conn = startConnection();
        Main.insertDriver(conn, "Bob");
        Main.deleteDriver(conn, new Driver(1, "Bob"));
        assertTrue(Main.driversSize(conn)==0);
        conn.close();
    }

    @Test
    public void testDeleteRequest() throws SQLException {
        Connection conn = startConnection();
        User user = new User(1, "Bob");
        Main.insertRequest(conn, 1, "test");
        Main.deleteRequest(conn, 1, user);
        assertTrue(Main.requestsSize(conn)==0);
        conn.close();
    }

    @Test
    public void testSelectOpenRequests() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();
        Main.populateDatabaseWithTestUsers(conn, "users.rtf");
        Main.populateDatabaseWithTestDrivers(conn, "drivers.rtf");
        Main.populateDatabaseWithTestRequests(conn, "requests.rtf");
        ArrayList<Request> openRequests = Main.selectOpenRequests(conn, "OPEN");
        conn.close();
        assertTrue(openRequests.size()==19);
    }
}