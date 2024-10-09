package com.Sept.nach.B;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACHCR_Validation316 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "Root@123";
    
    // Using dynamic file name generated from ReturnFileGenerator_seq
    private String outputFileName;

    public static void main(String[] args) {
        ACHCR_Validation writer = new ACHCR_Validation();
        writer.writeACHFile();
    }

    public ACHCR_Validation316() {
        // Generate the output file name dynamically
        try {
            this.outputFileName = ReturnFileGenerator_seq.generateReturnFile(); // Get the output file name
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeACHFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (validateRowCount(conn)) {
                // Proceed to write the file using the dynamically generated file name
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFileName));
                     Statement stmt = conn.createStatement()) {

                    // Get today's date in the required format
                    String currentDate = new SimpleDateFormat("ddMMyyyy").format(new Date());

                    // Fetch header data for writing to the file
                    String headerQuery = "SELECT ACH_TRANSACTION_CODE, CONTROL1, CONTROL_CHARACTER, TOT_NO_OF_ITEMS, TOT_AMOUNT, SETTLEMENT_DATE, " +
                            "DESTINATION_BANK, SETTLEMENT_CYCLE, FILE_NAME, INW_GEN_DATE FROM ACHHEADER " +
                            "WHERE STATUS_CODE = 10 AND SETTLEMENT_DATE = ?"; // Using parameterized query to prevent SQL injection

                    try (PreparedStatement headerStatement = conn.prepareStatement(headerQuery)) {
                        headerStatement.setString(1, currentDate); // Set the current date parameter
                        ResultSet headerResult = headerStatement.executeQuery();

                        if (headerResult.next()) {
                            String headerLine = createHeaderLine(headerResult);
                            writer.write(headerLine);
                            writer.newLine();
                        }

                        // Fetch data records and write them to the file
                        String dataQuery = "SELECT ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME, USER_NAME, " +
                                "AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO, SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, " +
                                "PRODUCT_TYPE, BENEFICIARY_AADHAAR_NO, UMRN, FLAG, REASON_CODE FROM ACHFILE " +
                                "WHERE FILE_NAME IN (SELECT FILE_NAME FROM ACHHEADER WHERE STATUS_CODE = 10 AND SETTLEMENT_DATE = ?)"; // Using the same condition for the data records

                        try (PreparedStatement dataStatement = conn.prepareStatement(dataQuery)) {
                            dataStatement.setString(1, currentDate); // Set the current date parameter
                            ResultSet dataResult = dataStatement.executeQuery();

                            while (dataResult.next()) {
                                String dataLine = createDataLine(dataResult);
                                writer.write(dataLine);
                                writer.newLine();
                            }
                        }

                        System.out.println("ACH file written successfully to " + this.outputFileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("The file contents are invalid. The number of rows does not match.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateRowCount(Connection conn) {
        try {
            String headerQuery = "SELECT TOT_NO_OF_ITEMS FROM ACHHEADER WHERE STATUS_CODE = 10 AND SETTLEMENT_DATE = ?";
            try (PreparedStatement headerStatement = conn.prepareStatement(headerQuery)) {
                headerStatement.setString(1, new SimpleDateFormat("ddMMyyyy").format(new Date())); // Set the current date parameter
                ResultSet headerResult = headerStatement.executeQuery();

                if (headerResult.next()) {
                    int totalNoOfItems = headerResult.getInt("TOT_NO_OF_ITEMS");

                    String dataQuery = "SELECT COUNT(*) AS ROW_COUNT FROM ACHFILE WHERE FILE_NAME IN " +
                            "(SELECT FILE_NAME FROM ACHHEADER WHERE STATUS_CODE = 10 AND SETTLEMENT_DATE = ?)";
                    try (PreparedStatement dataStatement = conn.prepareStatement(dataQuery)) {
                        dataStatement.setString(1, new SimpleDateFormat("ddMMyyyy").format(new Date())); // Set the current date parameter
                        ResultSet dataResult = dataStatement.executeQuery();

                        if (dataResult.next()) {
                            int rowCount = dataResult.getInt("ROW_COUNT");
                            return rowCount == totalNoOfItems;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createHeaderLine(ResultSet rs) throws Exception {
        StringBuilder header = new StringBuilder();
        header.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH transaction code
        header.append(String.format("%-7s", (rs.getString("CONTROL1") == null) ? "       " : "")); // Control
        header.append(String.format("%-87s", "")); // Filler
        header.append(String.format("%-7s", (rs.getString("CONTROL_CHARACTER") == null) ? "       " : "")); // Control
        header.append(String.format("%-9s", rs.getString("TOT_NO_OF_ITEMS"))); // Total Number of Items
        header.append(String.format("%-13s", rs.getString("TOT_AMOUNT"))); // Total Amount
        header.append(String.format("%-8s", rs.getString("SETTLEMENT_DATE"))); // Settlement Date
        header.append(String.format("%-8s", (rs.getString("DESTINATION_BANK") == null) ? "       " : rs.getString("DESTINATION_BANK"))); // Destination Bank
        header.append(String.format("%-8s", (rs.getString("SETTLEMENT_CYCLE") == null) ? "       " : rs.getString("SETTLEMENT_CYCLE"))); // Settlement Cycle
        header.append(String.format("%-10s", rs.getString("FILE_NAME"))); // File Name
        header.append(String.format("%-10s", rs.getString("INW_GEN_DATE"))); // Incoming Generation Date

        return header.toString();
    }

    private String createDataLine(ResultSet rs) throws Exception {
        StringBuilder data = new StringBuilder();
        data.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH transaction code
        data.append(String.format("%-1s", rs.getString("DESTINATION_ACCOUNT_TYPE"))); // Destination Account Type
        data.append(String.format("%-12s", rs.getString("LEDGER_FOLIO_NUMBER"))); // Ledger Folio Number
        data.append(String.format("%-30s", rs.getString("BENEFICIARY_HOLDER_NAME"))); // Beneficiary Holder Name
        data.append(String.format("%-10s", rs.getString("USER_NAME"))); // User Name
        data.append(String.format("%-13s", rs.getString("AMOUNT"))); // Amount
        data.append(String.format("%-10s", rs.getString("ACH_SEQ_NO"))); // ACH Sequence Number
        data.append(String.format("%-5s", rs.getString("CHECKSUM"))); // Checksum
        data.append(String.format("%-8s", rs.getString("DEST_BANK"))); // Destination Bank
        data.append(String.format("%-18s", rs.getString("BENEFICIARY_BANK_ACCNO"))); // Beneficiary Bank Account Number
        data.append(String.format("%-20s", rs.getString("SPONSOR_BANK"))); // Sponsor Bank
        data.append(String.format("%-10s", rs.getString("USER_NUMBER"))); // User Number
        data.append(String.format("%-18s", rs.getString("TRANSACTION_REFERENCE"))); // Transaction Reference
        data.append(String.format("%-15s", rs.getString("PRODUCT_TYPE"))); // Product Type
        data.append(String.format("%-15s", rs.getString("BENEFICIARY_AADHAAR_NO"))); // Beneficiary Aadhaar Number
        data.append(String.format("%-10s", rs.getString("UMRN"))); // UMRN
        data.append(String.format("%-5s", rs.getString("FLAG"))); // Flag
        data.append(String.format("%-10s", rs.getString("REASON_CODE"))); // Reason Code

        return data.toString();
    }
}
