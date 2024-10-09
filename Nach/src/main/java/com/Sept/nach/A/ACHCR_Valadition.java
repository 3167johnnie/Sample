package com.Sept.nach.A;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACHCR_Valadition {
    private String pattern = "ddMMyyyy";
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "Root@123";

    // Using dynamic file name generated from ReturnFileGenerator_seq
    private String outputFileName;

    public static void main(String[] args) {
    	ACHCR_Valadition writer = new ACHCR_Valadition();
        writer.writeACHFile();
    }

    public ACHCR_Valadition() {
        // Generate the output file name dynamically
        try {
            this.outputFileName = generateDynamicFileName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeACHFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (validateRowCount(conn)) {
                // Proceed to write the file using the dynamically generated file name
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFileName));  //(this.outputFileName));
                     Statement stmt = conn.createStatement()) {

                    // Fetch header data for writing to the file
                    String headerQuery = "SELECT ACH_TRANSACTION_CODE, CONTROL1, CONTROL_CHARACTER, TOT_NO_OF_ITEMS, TOT_AMOUNT, SETTLEMENT_DATE, " +
                            "DESTINATION_BANK, SETTLEMENT_CYCLE, FILE_NAME, INW_GEN_DATE FROM ACHHEADER " +
                            "WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
                    
                    ResultSet headerResult = stmt.executeQuery(headerQuery);
                    if (headerResult.next()) {
                        String headerLine = createHeaderLine(headerResult);
                        writer.write(headerLine);
                        writer.newLine();
                    }

                    // Fetch data records and write them to the file
                    String dataQuery = "SELECT ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME, USER_NAME, " +
                            "AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO, SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, " +
                            "PRODUCT_TYPE, BENEFICIARY_AADHAAR_NO, UMRN, FLAG, REASON_CODE FROM ACHFILE " +
                            "WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
                    
                    ResultSet dataResult = stmt.executeQuery(dataQuery);
                    while (dataResult.next()) {
                        String dataLine = createDataLine(dataResult);
                        writer.write(dataLine);
                        writer.newLine();
                    }

                    System.out.println("ACH file written successfully to " + this.outputFileName);
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
            String headerQuery = "SELECT TOT_NO_OF_ITEMS FROM ACHHEADER WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
            PreparedStatement headerStatement = conn.prepareStatement(headerQuery);
            ResultSet headerResult = headerStatement.executeQuery();

            if (headerResult.next()) {
                int totalNoOfItems = headerResult.getInt("TOT_NO_OF_ITEMS");

                String dataQuery = "SELECT COUNT(*) AS ROW_COUNT FROM ACHFILE WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
                PreparedStatement dataStatement = conn.prepareStatement(dataQuery);
                ResultSet dataResult = dataStatement.executeQuery();

                if (dataResult.next()) {
                    int rowCount = dataResult.getInt("ROW_COUNT");
                    return rowCount == totalNoOfItems;
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
        header.append(String.format("%-8s", (rs.getString("INW_GEN_DATE") == null) ? "        " : "        ")); // Inward Generation Date
        header.append(String.format("%-19s", "")); // Filler
        header.append(String.format("%-11s", rs.getString("DESTINATION_BANK"))); // Destination Bank IFSC
        header.append(String.format("%-2s", rs.getString("SETTLEMENT_CYCLE"))); // Settlement Cycle
        header.append(String.format("%-133s", "                                                                                                                                     .")); // Filler with dot
        return header.toString();
    }

    private String createDataLine(ResultSet rs) throws Exception {
        String dateInString = new SimpleDateFormat(pattern).format(new Date());

        StringBuilder data = new StringBuilder();
        data.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH Transaction Code
        data.append(String.format("%-9s", "")); // Control
        data.append(String.format("%-2s", rs.getString("DESTINATION_ACCOUNT_TYPE"))); // Destination Account Type
        data.append(String.format("%-3s", (rs.getString("LEDGER_FOLIO_NUMBER") == null) ? "   " : "   ")); // Ledger Folio Number
        data.append(String.format("%-15s", "")); // Spaces User defined return reason
        data.append(String.format("%-40s", rs.getString("BENEFICIARY_HOLDER_NAME"))); // Beneficiary Account Holder's Name
        data.append(String.format("%-8s", "")); // Control spaces
        data.append(String.format("%-8s", "")); // User defined return reason spaces
        data.append(String.format("%-20s", rs.getString("USER_NAME"))); // User Name / Narration
        data.append(String.format("%-13s", "")); // Control
        data.append(String.format("%-13s", rs.getString("AMOUNT"))); // Amount
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
        data.append(String.format("%-8s", dateInString)); // Processed date
        return data.toString();
    }

    // Method to generate the file name dynamically from ReturnFileGenerator_seq
    private String generateDynamicFileName() throws IOException {
        return ReturnFileGenerator_seq.generateReturnFile();
    }
}
