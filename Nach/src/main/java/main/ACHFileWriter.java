package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class ACHFileWriter {
///this is 316
    private static final String DB_URL = "jdbc:oracle:thin:@10.191.216.41:1524:cerpdev"; 
    private static final String USER =  "cerpdev"; 
    private static final String PASS =  "password"; 
    private static final String OUTPUT_FILE_PATH = "E:\\John\\John_Workspace ide\\ACH_NACH\\Z_TEST_OUTPUT\\ACH1.txt"; 

    public static void main(String[] args) {
        ACHFileWriter writer = new ACHFileWriter();
        writer.writeACHFile();
    }

    public void writeACHFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {

            // Fetch header data
            String headerQuery = "SELECT ACH_TRANSACTION_CODE, CONTROL1, CONTROL_CHARACTER, TOT_NO_OF_ITEMS, " +
                    "TOT_AMOUNT, SETTLEMENT_DATE, DESTINATION_BANK, " +
                    "SETTLEMENT_CYCLE, FILE_NAME, INW_GEN_DATE FROM ACHHEADER WHERE FILE_NAME ='ACH-CR-SBIN-28082014-016008-INW'";
            ResultSet headerResult = stmt.executeQuery(headerQuery);
            if (headerResult.next()) {
                String headerLine = createHeaderLine(headerResult);
                writer.write(headerLine);
                writer.newLine();
            }

            // Fetch data records
            String dataQuery = "SELECT ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME," +
                    " USER_NAME, AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO," +
                    " SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, PRODUCT_TYPE, BENEFICIARY_AADHAAR_NO, UMRN, FLAG , REASON_CODE " +
                    "FROM ACHFILE WHERE FILE_NAME ='ACH-DR-SBIN-28082024-TPZ000534979-INW'";
            ResultSet dataResult = stmt.executeQuery(dataQuery);
            while (dataResult.next()) {
                String dataLine = createDataLine(dataResult);
                writer.write(dataLine);
                writer.newLine();
            }

            System.out.println("ACH file written successfully to " + OUTPUT_FILE_PATH);

            // Update processed date after writing the file
            updateProcessedDate(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createHeaderLine(ResultSet rs) throws Exception {
        StringBuilder header = new StringBuilder();
        header.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH transaction code
        header.append(String.format("%-7s", rs.getString("CONTROL1"))); // Control
        header.append(String.format("%-87s", "")); // Filler
        header.append(String.format("%-7s", rs.getString("CONTROL_CHARACTER"))); // Control
        header.append(String.format("%-9s", rs.getInt("TOT_NO_OF_ITEMS"))); // Total Number of Items
        header.append(String.format("%-13s", rs.getLong("TOT_AMOUNT"))); // Total Amount
        header.append(String.format("%-8s", rs.getString("SETTLEMENT_DATE"))); // Settlement Date
        header.append(String.format("%-8s", rs.getString("INW_GEN_DATE"))); // Inward Generation Date
        header.append(String.format("%-19s", "")); // Filler
        header.append(String.format("%-11s", rs.getString("DESTINATION_BANK"))); // Destination Bank IFSC
        header.append(String.format("%-2s", rs.getString("SETTLEMENT_CYCLE"))); // Settlement Cycle
        header.append(String.format("%-133s", ".")); // Filler with dot
        return header.toString();
    }

    private String createDataLine(ResultSet rs) throws Exception {
        StringBuilder data = new StringBuilder();
        data.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH Transaction Code
        data.append(String.format("%-9s","")); // Control
        data.append(String.format("%-2s", rs.getString("DESTINATION_ACCOUNT_TYPE"))); // Destination Account Type
        data.append(String.format("%-3s", rs.getString("LEDGER_FOLIO_NUMBER"))); // Ledger Folio Number
        data.append(String.format("%-15s", "")); // Spaces User defined return reason
        data.append(String.format("%-40s", rs.getString("BENEFICIARY_HOLDER_NAME"))); // Beneficiary Account Holder's Name
        data.append(String.format("%-8s", "")); // Control spaces 
        data.append(String.format("%-8s", "")); // User defined return reason spaces 
        data.append(String.format("%-20s", rs.getString("USER_NAME"))); // User Name / Narration
        data.append(String.format("%-13s", rs.getLong("AMOUNT"))); // Amount
        data.append(String.format("%-10s", rs.getString("ACH_SEQ_NO"))); // ACH Item Seq No.
        data.append(String.format("%-10s", rs.getString("CHECKSUM"))); // Checksum
        data.append(String.format("%-7s", "")); // Reserved (Filler)
        data.append(String.format("%-11s", rs.getString("DEST_BANK"))); // Destination Bank IFSC
        data.append(String.format("%-35s", rs.getString("BENEFICIARY_BANK_ACCNO"))); // Beneficiary's Bank Account number
        data.append(String.format("%-11s", rs.getString("SPONSOR_BANK"))); // Sponsor Bank IFSC
        data.append(String.format("%-18s", rs.getString("USER_NUMBER"))); // User Number
        data.append(String.format("%-30s", rs.getString("TRANSACTION_REFERENCE"))); // Transaction Reference
        data.append(String.format("%-3s", rs.getString("PRODUCT_TYPE"))); // Product Type
        data.append(String.format("%-15s", rs.getString("BENEFICIARY_AADHAAR_NO"))); // Beneficiary Aadhaar Number
        data.append(String.format("%-20s", rs.getString("UMRN"))); // UMRN
        data.append(String.format("%-1s", rs.getString("FLAG"))); // Flag for success / return
        data.append(String.format("%-2s", rs.getString("REASON_CODE"))); // Reason Code
        return data.toString();
    }

    private void updateProcessedDate(Connection conn) {
        String updateQuery = "UPDATE ACHHEADER SET PROCESSED_DATE = ? WHERE FILE_NAME = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, LocalDate.now().toString()); // Set processed date to current date
            pstmt.setString(2, "ACH-CR-SBIN-28082014-016008-INW"); // Use the same FILE_NAME used in the header query
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Processed date updated successfully in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
