package com.Sept.nach;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TotalValadition {
    private String pattern = "ddMMyyyy";

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "SYS as SYSDBA";
    private static final String PASS = "Root@123";
    private static final String OUTPUT_FILE_PATH = "J:\\W.O.R.K___\\Sample\\Nach\\src\\main\\java\\main\\RETURN_OUTPUT\\ACHCR with Valadition.txt";

    public static void main(String[] args) {
        TotalValadition writer = new TotalValadition();
        writer.writeACHFile();
    }

    public void writeACHFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Fetch header data for validation
            String headerQuery = "SELECT TOT_NO_OF_ITEMS FROM ACHHEADER WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
            PreparedStatement headerStatement = conn.prepareStatement(headerQuery);
            ResultSet headerResult = headerStatement.executeQuery();
            //-------------------------
            //System.out.println("headerResult1 :::: "+ ResultSet.toString(headerResult));

            ResultSetMetaData rsmd = headerResult.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("Column name: " + rsmd.getColumnName(i));
            }
            //-------------------------
            
            if (headerResult.next()) {
                int totalNoOfItems = headerResult.getInt("TOT_NO_OF_ITEMS");

                // Fetch data records and count the number of rows
                String dataQuery = "SELECT * FROM ACHFILE WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
                PreparedStatement dataStatement = conn.prepareStatement(dataQuery);
                ResultSet dataResult = dataStatement.executeQuery();

                int rowCount = 0;
                while (dataResult.next()) {
                    rowCount++;
                }

                // Validate if row count matches TOT_NO_OF_ITEMS
                if (rowCount == totalNoOfItems) {
                    // Proceed to write the file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
                         Statement stmt = conn.createStatement()) {

                        // Fetch header data for writing to the file
                        headerResult = headerStatement.executeQuery();
                        
                        //-------------------------
                        //System.out.println("headerResult1 :::: "+ ResultSet.toString(headerResult));

                        ResultSetMetaData rsmd1 = headerResult.getMetaData();
                        int columnCount1 = rsmd.getColumnCount();
                        for (int i = 1; i <= columnCount1; i++) {
                            System.out.println("Column name: " + rsmd1.getColumnName(i));
                        }
                        //-------------------------
                        
                        if (headerResult.next()) {
                            String headerLine = createHeaderLine(headerResult);
                            writer.write(headerLine);
                            writer.newLine();
                        }

                        // Fetch data records and write them to the file
                        dataResult = dataStatement.executeQuery();
                        while (dataResult.next()) {
                            String dataLine = createDataLine(dataResult);
                            writer.write(dataLine);
                            writer.newLine();
                        }

                        System.out.println("ACH file written successfully to " + OUTPUT_FILE_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("The file contents are invalid. The number of rows does not match.");
                }
            } else {
                System.out.println("No record found in ACHHEADER for the given file name and settlement date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}

