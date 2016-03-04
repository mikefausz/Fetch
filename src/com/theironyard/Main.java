package com.theironyard;
import jodd.json.JsonSerializer;
import spark.Session;
import spark.Spark;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS drivers (id IDENTITY, name VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS requests (id IDENTITY, user_id INT, driver_id INT, request VARCHAR, status VARCHAR)");
    }
    public static void insertUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }
    public static void insertDriver(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO drivers VALUES (NULL, ?)");
        stmt.setString(1, name);
        stmt.execute();
    }
    public static void insertRequest(Connection conn, int userId, String request) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO requests VALUES (NULL, ?, NULL, ?, OPEN)");
        stmt.setInt(1, userId);
        stmt.setString(2, request);
        stmt.execute();
    }
    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        results.next();
        User user = new User(results.getInt("id"), results.getString("name"));
        return user;
    }
    public static Driver selectDriver(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM drivers WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        results.next();
        Driver driver = new Driver(results.getInt("id"), results.getString("name"));
        return driver;
    }
    public static ArrayList<Request> selectUserRequests(Connection conn, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM requests WHERE user_id = ?");
        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Request> userRequests = new ArrayList<>();
        while(results.next()){
            userRequests.add(new Request(results.getInt("id"), results.getInt("user_id"), results.getInt("driver_id"), results.getString("request"), results.getString("status")));
        }
        return userRequests;
    }
    public static ArrayList<Request> selectDriverRequests(Connection conn, int driverId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM requests WHERE driver_id = ?");
        stmt.setInt(1, driverId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Request> driverRequests = new ArrayList<>();
        while(results.next()){
            driverRequests.add(new Request(results.getInt("id"), results.getInt("user_id"), results.getInt("driver_id"), results.getString("request"), results.getString("status")));
        }
        return driverRequests;
    }
    public static void updateStatus(Connection conn, int id, String status) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE requests SET status = ? WHERE id = ?");
        stmt.setInt(1, id);
        stmt.setString(2, status);
        stmt.execute();
    }
    public static User getUserFromSession(Session session){
        User user = session.attribute("name");
        return user;
    }
    public static Driver getDriverFromSession(Session session){
        Driver driver = session.attribute("name");
        return driver;
    }
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        createTables(conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        Spark.post(
                "/login-User",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    Session session = request.session();
                    session.attribute("name", selectUser(conn, name));
                    return "";
                })
        );
        Spark.post(
                "/login-Driver",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    Session session = request.session();
                    session.attribute("name", selectDriver(conn, name));
                    return "";
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    return "";
                })
        );
        Spark.get(
                "/driver",
                ((request, response) -> {
                    String driver = request.queryParams("driver");
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriver(conn, driver));
                })
        );
        Spark.post(
                "/driver",
                ((request, response) -> {
                    String driver = request.queryParams("driver");
                    insertDriver(conn, driver);
                    return "";
                })
        );
        Spark.get(
                "/user",
                ((request, response) -> {
                    String user = request.queryParams("user");
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUser(conn, user));
                })
        );
        Spark.post(
                "/user",
                ((request, response) -> {
                    String user = request.queryParams("user");
                    insertUser(conn, user);
                    return "";
                })
        );
        Spark.post(
                "/request",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    String requestText = request.queryParams("requestText");
                    insertRequest(conn, user.id, requestText);
                    return "";
                })
        );
        Spark.get(
                "/user-requests",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUserRequests(conn, user.id));
                })
        );
        Spark.get(
                "/driver-requests",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriverRequests(conn, driver.id));
                })
        );
        Spark.post(
                "/update-request",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    String status = request.queryParams("status");
                    String requestIdStr = request.queryParams("id");
                    if(!requestIdStr.isEmpty()) {
                        int requestId = Integer.valueOf(requestIdStr);
                        updateStatus(conn, requestId, status);
                    }
                    return "";
                })
        );
    }
}
