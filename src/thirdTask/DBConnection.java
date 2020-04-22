package thirdTask;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "skillbox";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName + "?user="
                                + dbUser + "&password=" + dbPass + "&useSSL=false");
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "`count` INT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "KEY(name(50)));");
//                        "UNIQUE KEY name_date(name(50), birthDate))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void executeMultiInsert() throws SQLException, InterruptedException, ExecutionException {
        insertQuery.insert(0, "INSERT INTO voter_count(name, birthDate, count) VALUES");
        insertQuery.append(" ON DUPLICATE KEY UPDATE `count`=count+1");
        Set<Future<?>> futures = new HashSet<>();
        ExecutorService service = Executors.newSingleThreadExecutor();
        futures.add(service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DBConnection.getConnection().createStatement().execute(insertQuery.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        insertQuery.setLength(0); //Очистим билдер
        service.shutdown();
    }

    public static void countVoter(String name, String birthDay) throws SQLException, ExecutionException, InterruptedException {
        birthDay = birthDay.replace('.', '-');
        boolean isStart = insertQuery.length() == 0;
        insertQuery.append(isStart ? "" : ",").append("('").append(name).append("', '").append(birthDay).append("', 1)");
        if (insertQuery.length() > 2_140_000) { //Будем делать Multi Insert, если размер StringBuilder больше 2_140_000
            executeMultiInsert();
        }
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }
}
