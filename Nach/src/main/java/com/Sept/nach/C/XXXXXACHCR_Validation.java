package com.Sept.nach.C;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class XXXXXACHCR_Validation {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe"; // Update DB URL
    private static final String USER = "your-username"; // DB username
    private static final String PASS = "your-password"; // DB password
    private static final long INTERVAL = 2 * 60 * 1000; // 2 minutes

    public static void main(String[] args) {
    	XXXXXACHCR_Validation achValidation = new XXXXXACHCR_Validation();
        achValidation.scheduleACHProcessing();
    }

    public void scheduleACHProcessing() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processACHFiles();
            }
        }, 0, INTERVAL);

        System.out.println("ACH file processing scheduled to run every 2 minutes.");
    }

    private void processACHFiles() {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Fetch rows with FILE_STATUS = 10
            String selectSQL = "SELECT * FROM ACHHEADER WHERE FILE_STATUS = 10";
            selectStmt = conn.prepareStatement(selectSQL);
            rs = selectStmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ID");
                String fileName = rs.getString("FILE_NAME");

                // Update FILE_STATUS to 11 (Processing)
                String updateSQL = "UPDATE ACHHEADER SET FILE_STATUS = 11, LAST_STATUS_TIME = ? WHERE ID = ?";
                updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();

                // Process the file and generate the return file
                ReturnFileGenerator_seq returnFileGen = new ReturnFileGenerator_seq();
                boolean success = returnFileGen.generateReturnFile(fileName);

                // Update the table based on the result of the return file generation
                if (success) {
                    updateFileStatus(conn, id, 12, "ReturnFile_" + fileName); // Success
                } else {
                    updateFileStatus(conn, id, 13, null); // Failure
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateFileStatus(Connection conn, int id, int status, String returnFileName) throws SQLException {
        String updateSQL = "UPDATE ACHHEADER SET FILE_STATUS = ?, LAST_STATUS_TIME = ?, RETURN_FILENAME = ? WHERE ID = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
            updateStmt.setInt(1, status);
            updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            updateStmt.setString(3, returnFileName);
            updateStmt.setInt(4, id);
            updateStmt.executeUpdate();
        }
    }
}
