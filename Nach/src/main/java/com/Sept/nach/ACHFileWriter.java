package com.Sept.nach;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ACHFileWriter {
	private String pattern = "ddMMyyyy";
	
//    private static final String DB_URL = "jdbc:oracle:thin:@10.191.216.41:1524:cerpdev"; 
//    private static final String USER =  "cerpdev"; 
//    private static final String PASS =  "password"; 
//    private static final String OUTPUT_FILE_PATH = "E:\\John\\John_Workspace ide\\ACH_NACH\\Z_TEST_OUTPUT\\ACHCR9.txt"; 
	  private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl"; 
	    private static final String USER =  "SYS as SYSDBA"; 
	    private static final String PASS =  "Root@123"; 
	    private static final String OUTPUT_FILE_PATH = "J:\\W.O.R.K___\\Sample\\Nach\\src\\main\\java\\main\\RETURN_OUTPUT\\ACHCRv1.txt"; 


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
                    "SETTLEMENT_CYCLE, FILE_NAME, INW_GEN_DATE FROM ACHHEADER WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW'AND SETTLEMENT_DATE='13092021'";
            ResultSet headerResult = stmt.executeQuery(headerQuery);
            if (headerResult.next()) {
                String headerLine = createHeaderLine(headerResult);
                writer.write(headerLine);
                writer.newLine();
            }

            // Fetch data records //ACH-CR-SBIN-13092021-TPZ000106691-INW
            //CR -ACH-CR-SBIN-28082024-TPZECS535072-INW
            //DR- ACH-DR-SBIN-28082024-TPZ000534979-INW //'ACH-CR-SBIN-28082024-TPZECS535093-INW'AND SETTLEMENT_DATE='28082024'
            String dataQuery = "SELECT 	ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME,"+
            " USER_NAME, AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO,"+
            		"SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, PRODUCT_TYPE, BENEFICIARY_AADHAAR_NO, UMRN, FLAG , REASON_CODE "+
	"from ACHFILE WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'"; 
            ResultSet dataResult = stmt.executeQuery(dataQuery);
            while (dataResult.next()) {
                String dataLine = createDataLine(dataResult);
                writer.write(dataLine);
                writer.newLine();
            }

            System.out.println("ACH file written successfully to " + OUTPUT_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createHeaderLine(ResultSet rs) throws Exception {
    	
    
        StringBuilder header = new StringBuilder();
        header.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH transaction code
        header.append(String.format("%-7s", (rs.getString("CONTROL1")== null)? "       " : "")); // Control
        header.append(String.format("%-87s", "")); // Filler
        header.append(String.format("%-7s", (rs.getString("CONTROL_CHARACTER")== null)? "       " : "")); // Control
        header.append(String.format("%-9s", rs.getString("TOT_NO_OF_ITEMS"))); // Total Number of Items
        header.append(String.format("%-13s", rs.getString("TOT_AMOUNT"))); // Total Amount
        header.append(String.format("%-8s", rs.getString("SETTLEMENT_DATE"))); // Settlement Date
        header.append(String.format("%-8s", (rs.getString("INW_GEN_DATE")== null)? "        " : "        ")); // Inward Generation Date
        header.append(String.format("%-19s", "")); // Filler
        header.append(String.format("%-11s", rs.getString("DESTINATION_BANK"))); // Destination Bank IFSC
        header.append(String.format("%-2s", rs.getString("SETTLEMENT_CYCLE"))); // Settlement Cycle
        header.append(String.format("%-133s", "                                                                                                                                     .")); // Filler with dot
        return header.toString();
    }

//    // Fetch data records
//    String dataQuery = "SELECT 	ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME,"+
//    " USER_NAME, AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO,"+
//    		"SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, PRODUCT_TYPE,  BENEFICIARY_AADHAAR_NO, UMRN, FLAG , REASON_CODE "+
//"from ACHFILEDR WHERE FILE_NAME ='ACH-DR-SBIN-28082024-TPZ000534982-INW'"; 
    
    
    private String createDataLine(ResultSet rs) throws Exception {
    	String dateInString =new SimpleDateFormat(pattern).format(new Date());
    	
        StringBuilder data = new StringBuilder();
        data.append(String.format("%-2s", rs.getString("ACH_TRANSACTION_CODE"))); // ACH Transaction Code
        data.append(String.format("%-9s","")); // Control
        data.append(String.format("%-2s", rs.getString("DESTINATION_ACCOUNT_TYPE"))); // Destination Account Type
        data.append(String.format("%-3s", (rs.getString("LEDGER_FOLIO_NUMBER")== null)? "   " : "   ")); // Ledger Folio Number  control2 = (control2 == null) ? "       " : control2;
        data.append(String.format("%-15s", "")); // Spaces User defined return reason
        data.append(String.format("%-40s", rs.getString("BENEFICIARY_HOLDER_NAME"))); // Beneficiary Account Holder's Name
        data.append(String.format("%-8s", "")); // Control spaces 
        data.append(String.format("%-8s", "")); // User defined return reason spaces 
        data.append(String.format("%-20s", rs.getString("USER_NAME"))); // User Name / Narration
        data.append(String.format("%-13s","")); // Control
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
      //  data.append(String.format("%-8s", rs.getString("processed_date"))); // Processed date
        return data.toString();
    }
}