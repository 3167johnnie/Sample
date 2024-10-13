
// Modifications in ACHCR_Validation.java as per user's requirement
package com.Sept.nach.Z;

import java.sql.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ACHCR_Validation {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "Root@123";
    private static final long TWO_MINUTES = 120000; // 2 minutes in milliseconds

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new ProcessACHFilesTask(), 0, TWO_MINUTES); // Schedule task every 2 minutes
    }

    static class ProcessACHFilesTask extends TimerTask {
        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Fetch multiple rows where FILE_STATUS = 10
                String query = "SELECT ID, FILE_NAME FROM ACHHEADER WHERE FILE_STATUS = 10";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String fileName = rs.getString("FILE_NAME");
                    
                    processACHFile(conn, id, fileName);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void processACHFile(Connection conn, int id, String fileName) throws SQLException {
            // Update FILE_STATUS to 11 and set LAST_STATUS_TIME to current time
            String updateQuery = "UPDATE ACHHEADER SET FILE_STATUS = 11, LAST_STATUS_TIME = ? WHERE ID = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();

                System.out.println("Processing ACH file with ID: " + id + " and FILE_NAME: " + fileName);

                // Generate return file
                ReturnFileGenerator_seq generator = new ReturnFileGenerator_seq();
                String generatedFileName = generator.generateReturnFile(id);

                // Update the table based on success or failure of file generation
                if (generatedFileName != null) {
                    updateFileStatus(conn, id, 12, generatedFileName); // Success, update to status 12
                } else {
                    updateFileStatus(conn, id, 13, null); // Failure, update to status 13
                }
            }
        }

        private void updateFileStatus(Connection conn, int id, int status, String returnFileName) throws SQLException {
            String updateQuery = "UPDATE ACHHEADER SET FILE_STATUS = ?, LAST_STATUS_TIME = ?, RETURN_FILENAME = ? WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, status);
                stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
                stmt.setString(3, returnFileName);
                stmt.setInt(4, id);
                stmt.executeUpdate();

                System.out.println("Updated ACH file with ID: " + id + " to FILE_STATUS: " + status);
            }
        }
    }
}
