package com.theironyard;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;
import spark.Spark;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS drivers (id IDENTITY, name VARCHAR UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS requests (id IDENTITY, user_id INT, driver_id INT, request VARCHAR, status VARCHAR NOT NULL)");
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
    public static void deleteUser(Connection conn, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
        stmt.setInt(1, user.getId());
        stmt.execute();
    }
    public static void deleteDriver(Connection conn, Driver driver) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM drivers WHERE id = ?");
        stmt.setInt(1, driver.getId());
        stmt.execute();
    }
    public static void deleteRequest(Connection conn, int id, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM requests WHERE id = ? AND user_id = ?");
        stmt.setInt(1, id);
        stmt.setInt(2, user.getId());
        stmt.execute();
    }
    public static User getUserFromSession(Session session){
        return session.attribute("name");
    }
    public static Driver getDriverFromSession(Session session){
        return session.attribute("name");
    }
    public static int usersSize(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(id) AS size FROM users");
        ResultSet results = stmt.executeQuery();
        results.next();
        return results.getInt("size");
    }
    public static int driversSize(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(id) AS size FROM drivers");
        ResultSet results = stmt.executeQuery();
        results.next();
        return results.getInt("size");
    }
    public static int requestsSize(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(id) AS size FROM requests");
        ResultSet results = stmt.executeQuery();
        results.next();
        return results.getInt("size");
    }
    public static void userLoginCheck(User user){
        if(user==null){
            logger.error("User Not Logged In");
            Spark.halt(401, "User Not Logged In");
        }
    }
    public static void driverLoginCheck(Driver driver){
        if(driver==null){
            logger.error("Driver Not Logged In");
            Spark.halt(401, "Driver Not Logged In");
        }
    }
    public static void populateDatabaseWithTestUsers(Connection conn,String file) throws FileNotFoundException, SQLException {
        File f = new File(file);
        Scanner s = new Scanner(f);
        while(s.hasNext()){
            insertUser(conn, s.nextLine());
        }
    }
    public static void populateDatabaseWithTestDrivers(Connection conn, String file) throws FileNotFoundException, SQLException {
        File f = new File(file);
        Scanner s = new Scanner(f);
        while(s.hasNext()){
            insertDriver(conn, s.nextLine());
        }
    }
    public static void populateDatabaseWithTestRequests(Connection conn, String file) throws FileNotFoundException, SQLException {
        File f = new File(file);
        Scanner s = new Scanner(f);
        while(s.hasNext()){
            String[] requests = s.nextLine().split(",");
            insertRequest(conn, Integer.valueOf(requests[0]), requests[1]);
        }
    }
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:fetch");
        createTables(conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        Spark.awaitInitialization();
        Server.createWebServer().start();
        insertUser(conn, "Bob");
        insertDriver(conn, "Tom");
        insertRequest(conn, 1, "This is a request");
        updateStatus(conn, 1, "Request Received", 1);
        populateDatabaseWithTestUsers(conn, "users.rtf");
        populateDatabaseWithTestDrivers(conn, "drivers.rtf");
        populateDatabaseWithTestRequests(conn, "requests.rtf");

        Spark.post(
                "/login-User",
                ((request, response) -> {
                    User user = null;
                    String name = request.queryParams("name");
                    if(name.isEmpty()){
                        logger.error("User Name Cannot Be Empty");
                        Spark.halt(400, "User Name Cannot Be Empty");
                    }
                    try{
                        user = selectUser(conn, name);
                    }catch (SQLException e){
                        logger.error("Error Retrieving User From Database");
                        Spark.halt(404, "Error Retrieving User From Database: " + e.getMessage());
                    }
                    Session session = request.session();
                    session.attribute("name", user);
                    return user;
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
                    try{
                        selectDriver(conn, name);
                    }catch (SQLException e){
                        logger.error("Error Retrieving Driver From Database");
                        Spark.halt(404, "Error Retrieving Driver From Database: " + e.getMessage());
                    }
                    Session session = request.session();
                    session.attribute("name", selectDriver(conn, name));
                    return selectDriver(conn, name);
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
                    String driverStr = request.queryParams("driver");
                    Driver driver = getDriverFromSession(request.session());
                    driverLoginCheck(driver);
                    if(driverStr.isEmpty()){
                        logger.error("Driver Field Cannot Be Empty");
                        Spark.halt(400, "Driver Field Cannot Be Empty");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriver(conn, driverStr));
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
                    String userStr = request.queryParams("user");
                    User user = getUserFromSession(request.session());
                    userLoginCheck(user);
                    if(userStr.isEmpty()){
                        logger.error("User Field Cannot Be Empty");
                        Spark.halt(400, "User Field Cannot Be Empty");
                    }
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUser(conn, userStr));
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
                    userLoginCheck(user);
                    String requestText = request.queryParams("requestText");
                    try {
                        insertRequest(conn, user.getId(), requestText);
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
                    userLoginCheck(user);
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectUserRequests(conn, user.getId()));
                })
        );
        Spark.get(
                "/driver-requests",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    driverLoginCheck(driver);
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(selectDriverRequests(conn, driver.getId()));
                })
        );
               Spark.post(
                "/update-request",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    driverLoginCheck(driver);
                    String status = request.queryParams("status");
                    String requestIdStr = request.queryParams("id");
                    if(!requestIdStr.isEmpty()) {
                        try {
                            int requestId = Integer.valueOf(requestIdStr);
                            updateStatus(conn, requestId, status, driver.getId());
                        }catch(SQLException e){
                            logger.error("Error Updating Request Status");
                            Spark.halt(500, "Error Updating Request Status: " + e.getMessage());
                        }catch (NumberFormatException n){
                            logger.error("Error Converting String To ID");
                            Spark.halt(400, "Error Converting String To ID: " + n.getMessage());
                        }
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete-user",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    userLoginCheck(user);
                    try{
                        deleteUser(conn, user);
                    }catch (SQLException e){
                        logger.error("Error Deleting User");
                        Spark.halt(500, "Error Deleting User: " + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete-driver",
                ((request, response) -> {
                    Driver driver = getDriverFromSession(request.session());
                    driverLoginCheck(driver);
                    try{
                        deleteDriver(conn, driver);
                    }catch (SQLException e){
                        logger.error("Error Deleting Driver");
                        Spark.halt(500, "Error Deleting Driver: " + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete-request",
                ((request, response) -> {
                    int requestId = -1;
                    User user = getUserFromSession(request.session());
                    String requestIdStr = request.queryParams("requestId");
                    userLoginCheck(user);
                    if(requestIdStr.isEmpty()){
                        logger.error("Error Deleting Request: Request ID Cannot Be Empty");
                        Spark.halt(400, "Error Deleting Request: Request ID Cannot Be Empty");
                    }
                    try{
                        requestId = Integer.valueOf(requestIdStr);
                        deleteRequest(conn, requestId, user);
                    }catch (SQLException e){
                        logger.error("Error Deleting Request");
                        Spark.halt(500, "Error Deleting Request: " + e.getMessage());
                    }catch (NumberFormatException n){
                        logger.error("Error Converting String To ID");
                        Spark.halt(400, "Error Converting String To ID: " + n.getMessage());
                    }
                    return "";
                })
        );
    }
}
