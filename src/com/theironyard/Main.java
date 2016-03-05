package com.theironyard;
import jodd.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;
import spark.Spark;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS drivers (id IDENTITY, name VARCHAR UNIQUE)");
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
        String status = "OPEN";
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO requests VALUES (NULL, ?, NULL, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, request);
        stmt.setString(3, status);
        stmt.execute();
    }
    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        results.next();
        return new User(results.getInt("id"), results.getString("name"));
    }
    public static Driver selectDriver(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM drivers WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        results.next();
        return new Driver(results.getInt("id"), results.getString("name"));
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
    public static void updateStatus(Connection conn, int id, String status, int driverId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE requests SET status = ?, driver_id = ? WHERE id = ?");
        stmt.setString(1, status);
        stmt.setInt(2, driverId);
        stmt.setInt(3, id);
        stmt.execute();
    }
    public static User getUserFromSession(Session session){
        return session.attribute("name");
    }
    public static Driver getDriverFromSession(Session session){
        return session.attribute("name");
    }
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        createTables(conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        insertUser(conn, "Bob");
        insertDriver(conn, "Tom");
        insertRequest(conn, 1, "This is a request");
        updateStatus(conn, 1, "Request Received", 1);

        Spark.post(
                "/login-User",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    if(name.isEmpty()){
                        logger.error("User Name Cannot Be Empty");
                        Spark.halt(400, "User Name Cannot Be Empty");
                    }
                    Session session = request.session();
                    session.attribute("name", selectUser(conn, name));
                    return "";
                })
        );
        Spark.post(
                "/login-Driver",
                ((request, response) -> {
                    String name = request.queryParams("name");
                    if(name.isEmpty()){
                        logger.error("Driver Name Cannot Be Empty");
                        Spark.halt(400, "Driver Name Cannot Be Empty");
                    }
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
                    if(driver.isEmpty()){
                        logger.error("Driver Field Cannot Be Empty");
                        Spark.halt(400, "Driver Field Cannot Be Empty");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriver(conn, driver));
                })
        );
        Spark.post(
                "/driver",
                ((request, response) -> {
                    String driver = request.queryParams("driver");
                    if(driver.isEmpty()){
                        logger.error("Driver Field Cannot Be Empty");
                        Spark.halt(400, "Driver Field Cannot Be Empty");
                    }
                    try {
                        insertDriver(conn, driver);
                    }catch(SQLException e){
                        logger.error("Error Inserting Driver");
                        Spark.halt(500, "Error Inserting Driver: " + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.get(
                "/user",
                ((request, response) -> {
                    String user = request.queryParams("user");
                    if(user.isEmpty()){
                        logger.error("User Field Cannot Be Empty");
                        Spark.halt(400, "User Field Cannot Be Empty");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUser(conn, user));
                })
        );
        Spark.post(
                "/user",
                ((request, response) -> {
                    String user = request.queryParams("user");
                    if(user.isEmpty()){
                        logger.error("User Field Cannot Be Empty");
                        Spark.halt(400, "User Field Cannot Be Empty");
                    }
                    try {
                        insertUser(conn, user);
                    }catch(SQLException e){
                        logger.error("Error Inserting User");
                        Spark.halt(500, "Error Inserting User: " + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.post(
                "/request",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    if(user==null){
                        logger.error("User Not Logged In");
                        Spark.halt(401, "User Not Logged In");
                    }
                    String requestText = request.queryParams("requestText");
                    try {
                        insertRequest(conn, user.id, requestText);
                    }catch(SQLException e){
                        logger.error("Error Inserting Request");
                        Spark.halt(500, "Error Inserting Request: " + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.get(
                "/user-requests",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    if(user==null){
                        logger.error("User Not Logged In");
                        Spark.halt(401, "User Not Logged In");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUserRequests(conn, user.id));
                })
        );
        Spark.get(
                "/driver-requests",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    if(driver==null){
                        logger.error("Driver Not Logged In");
                        Spark.halt(401, "Driver Not Logged In");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriverRequests(conn, driver.id));
                })
        );
        Spark.post(
                "/update-request",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    if(driver==null){
                        logger.error("Driver Not Logged In");
                        Spark.halt(401, "Driver Not Logged In");
                    }
                    String status = request.queryParams("status");
                    String requestIdStr = request.queryParams("id");
                    if(!requestIdStr.isEmpty()) {
                        int requestId = Integer.valueOf(requestIdStr);
                        try {
                            updateStatus(conn, requestId, status, driver.id);
                        }catch(SQLException e){
                            logger.error("Error Updating Request Status");
                            Spark.halt(500, "Error Updating Request Status: " + e.getMessage());
                        }
                    }
                    return "";
                })
        );
    }
}
