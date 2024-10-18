package com.nach.impl;

import java.io.BufferedWriter;     
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nach.Utils.ExtractProperties;
import com.nach.Utils.ReturnFileGenerator_seq;



class Helper extends TimerTask{
	private static final Logger log = LogManager.getLogger(Helper.class);
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("ACHCR_File generation started");
        ACHCR_Validation writer = new ACHCR_Validation();
        try {
			writer.processACHFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Exception in processing files");
			//e.printStackTrace();
		}
	}

}
public class ACHCR_Validation {
	private static final Logger log = LogManager.getLogger(ACHCR_Validation.class);
    private String pattern = "ddMMyyyy";
    //private static final String DB_URL = "jdbc:oracle:thin:@10.191.216.41:1542:cerpdev"; 
    private static final String DB_URL = ExtractProperties.getInstance().getExtractProperty("DB_URL");
    
    private static final String USER =  ExtractProperties.getInstance().getExtractProperty("USER");
    private static final String PASS =  ExtractProperties.getInstance().getExtractProperty("PASS");
    private static final String Timer_Delay= ExtractProperties.getInstance().getExtractProperty("Timer_Delay");
    private static final String Timer_Period= ExtractProperties.getInstance().getExtractProperty("Timer_Period");

    //private static final String PASS =  "password"; 
   
    
//    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
//    private static final String USER = "SYS as SYSDBA";
//    private static final String PASS = "Root@123";
    private static final long INTERVAL =   1000;   //2 * 60 * 1000; // 2 minutes
    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");

    // Using dynamic file name generated from ReturnFileGenerator_seq
    private String outputFileName;

    public static void main(String[] args) {
    	
//    	  ACHCR_Validation writer = new ACHCR_Validation();
//    	  try {
//			writer.processACHFiles();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	long Delay =Long.parseLong(Timer_Delay);
    	long Period =Long.parseLong(Timer_Period);
    	Timer timer =new Timer();
		TimerTask task=new Helper();
		timer.schedule(task, Delay,Period);
        
    }
    
//    public void scheduleACHProcessing() {
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                	
//                	String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                    System.out.println("ACH Processing started at: " + timestamp);
//
//					processACHFiles();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//        }, 0, INTERVAL);
//
//        System.out.println("ACH file processing scheduled to run every 2 minutes.");
//    }

    void processACHFiles() throws Exception {
//     
    	Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Fetch rows with FILE_STATUS = 10
            String selectSQL = "SELECT FILE_NAME ,SETTLEMENT_DATE FROM ACHHEADER WHERE FILE_STATUS = 10 FETCH FIRST 1 ROWS ONLY";
            selectStmt = conn.prepareStatement(selectSQL);
            rs = selectStmt.executeQuery();

            // Ensure we are processing the result set correctly
            if (rs.next()) {
      
                String fileName = rs.getString("FILE_NAME");
                String settleDate = rs.getString("SETTLEMENT_DATE");
                //System.out.println("fileName and settleDate: " + fileName + " and " + settleDate);
                log.info("Return file generation for file name: "+ fileName+ "and settlement date: "+ settleDate+" started");
                // Update FILE_STATUS to 11 (Processing)String currentTime = sdf.format(new Date());
                String updateSQL = "UPDATE ACHHEADER SET FILE_STATUS = 11, LAST_STATUS_TIME = ? WHERE FILE_NAME = ? AND SETTLEMENT_DATE = ?";
                updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setString(1, sdf.format(new Date()));
                System.out.println("--------- "+ sdf.format(new Date()) );
                updateStmt.setString(2, fileName);
                updateStmt.setString(3, settleDate);
                updateStmt.executeUpdate();
                log.info("ACHFile process initiated");
                // Process the file and generate the return file
                writeACHFile(fileName, settleDate);
            } else {
                log.warn("No records found with FILE_STATUS = 10.");
            }

        } catch (SQLException e) {
        	log.error("SQL Exception in processing ACH files");
            //e.printStackTrace(); // Consider using a logging framework
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            	log.error("SQL Exception in closing the connection");
                //e.printStackTrace();
            }
        }
    }
    
    private void updateFileStatus(int status, String returnFileName, String Filename) throws SQLException {
        String updateSQL = "UPDATE ACHHEADER SET FILE_STATUS = ?, LAST_STATUS_TIME = ?, RETURN_FILENAME = ? WHERE FILE_NAME = ?";
        
        // Ensure the connection is properly initialized
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
            
            updateStmt.setInt(1, status);
            updateStmt.setString(2, sdf.format(new Date()));
            updateStmt.setString(3, returnFileName);
            updateStmt.setString(4, Filename); // Set the ID for the WHERE clause
            
            updateStmt.executeUpdate();
            log.info("File status updated");
        } catch (SQLException e) {
            // Handle SQL exceptions
        	log.error("SQLException in updating file status");
            //e.printStackTrace();
            throw e; // Rethrow the exception after logging
        }
    }

    
    public void writeACHFile(String fileName1, String settleDate) throws Exception {
    	log.info("Writing to the ACH_Return FILE generation");
        Connection conn = null;
        PreparedStatement headerStmt = null;
        PreparedStatement dataStmt = null;
        ResultSet headerResult = null;
        ResultSet dataResult = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Directly use the provided fileName and settlementDate for processing
            String fileName = fileName1;                        
            String settlementDate = settleDate;
            String newReturnFile = generateDynamicFileName();
            String returnFileName= getFileName(newReturnFile);
             String processFileLocation ="E:\\John\\John_Workspace ide\\ACH_NACH\\Z_TEST_OUTPUT\\ReturnFileProcessed\\";
            File source=new File(newReturnFile);
            File dest=new File(processFileLocation);
            // Validate row count before proceeding
            if (validateRowCount(conn, fileName, settlementDate)) {
                // Proceed to write the file using the provided fileName and settlementDate
            	log.info("Row counts are same as total no. of rows");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newReturnFile))) {

                    // Dynamically generate header query
                    String headerQuery = "SELECT ACH_TRANSACTION_CODE, CONTROL1, CONTROL_CHARACTER, TOT_NO_OF_ITEMS, TOT_AMOUNT, SETTLEMENT_DATE, " +
                            "DESTINATION_BANK, SETTLEMENT_CYCLE, FILE_NAME, INW_GEN_DATE FROM ACHHEADER " +
                            "WHERE FILE_NAME = ? AND SETTLEMENT_DATE = ?";

                    headerStmt = conn.prepareStatement(headerQuery);
                    headerStmt.setString(1, fileName);
                    headerStmt.setString(2, settlementDate);
                    headerResult = headerStmt.executeQuery();

                    if (headerResult.next()) {
                        String headerLine = createHeaderLine(headerResult);
                        writer.write(headerLine);
                        writer.newLine();
                        log.info("Header line genearted in the file");
                    }
                    
                    
                    // Dynamically generate data query
                    String dataQuery = "SELECT ACH_TRANSACTION_CODE, DESTINATION_ACCOUNT_TYPE, LEDGER_FOLIO_NUMBER, BENEFICIARY_HOLDER_NAME, USER_NAME, " +
                            "AMOUNT, ACH_SEQ_NO, CHECKSUM, DEST_BANK, BENEFICIARY_BANK_ACCNO, SPONSOR_BANK, USER_NUMBER, TRANSACTION_REFERENCE, " +
                            "PRODUCT_TYPE, BENEFICIARY_AADHAAR_NO, UMRN, FLAG, REASON_CODE FROM ACHFILE " +
                            "WHERE FILE_NAME = ? AND SETTLEMENT_DATE = ?";

                    dataStmt = conn.prepareStatement(dataQuery);
                    dataStmt.setString(1, fileName);
                    dataStmt.setString(2, settlementDate);
                    dataResult = dataStmt.executeQuery();

                    // Write each data row to the file
                    while (dataResult.next()) {
                        String dataLine = createDataLine(dataResult);
                        writer.write(dataLine);
                        writer.newLine();
                    }
                    writer.close();
                    log.info("All Data files generated");
                    log.info("ACH file written successfully to " + newReturnFile);
                    copyReturnFile(source, processFileLocation);
                    updateFileStatus(12, returnFileName, fileName);  
                    
                } catch (IOException e) {
                	log.error("IO Exception in WriteACHFiles");
                	 updateFileStatus(13, null, fileName);
                	source.delete();
//                	dest.delete();
                    //e.printStackTrace();
                }
            } else {
                log.info("The file contents are invalid. The number of rows does not match.");
                updateFileStatus(13, null, fileName);
            }
           
        } catch (SQLException e) {
        	log.error("SQLEXception in WRITE_ACH FILES");
        	//source.delete();
            //e.printStackTrace();
        } finally {
            try {
                if (headerResult != null) headerResult.close();
                if (dataResult != null) dataResult.close();
                if (headerStmt != null) headerStmt.close();
                if (dataStmt != null) dataStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            	log.error("SQL EXception in closing the connections in writeACHFiles");
                //e.printStackTrace();
            }
        }
    } 
   
    private void copyReturnFile(File source, String destinationDir) {
        File dest = new File(destinationDir, source.getName());
        try {
            Files.copy(source.toPath(), dest.toPath());
        	//Files.move(source.toPath(), dest.toPath());
            log.info("File copied successfully to " + dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to copy file to " + dest.getAbsolutePath(), e);
        }
    }

    private boolean validateRowCount(Connection conn, String file_Name , String Settlement_date) {
        try {
        //    String headerQuery = "SELECT TOT_NO_OF_ITEMS FROM ACHHEADER WHERE FILE_NAME ='ACH-CR-SBIN-13092021-TPZ000106691-INW' AND SETTLEMENT_DATE='13092021'";
            String headerQuery = "SELECT TOT_NO_OF_ITEMS FROM ACHHEADER WHERE FILE_NAME = ? AND SETTLEMENT_DATE= ? ";
            PreparedStatement headerStatement = conn.prepareStatement(headerQuery);
            headerStatement.setString(1, file_Name);
            headerStatement.setString(2, Settlement_date);
            ResultSet headerResult = headerStatement.executeQuery();

            if (headerResult.next()) {
                int totalNoOfItems = headerResult.getInt("TOT_NO_OF_ITEMS");

                String dataQuery = "SELECT COUNT(*) AS ROW_COUNT FROM ACHFILE WHERE FILE_NAME = ? AND SETTLEMENT_DATE = ?";
                PreparedStatement dataStatement = conn.prepareStatement(dataQuery);
                dataStatement.setString(1, file_Name);
                dataStatement.setString(2, Settlement_date);
                ResultSet dataResult = dataStatement.executeQuery();

                if (dataResult.next()) {
                    int rowCount = dataResult.getInt("ROW_COUNT");
                    return rowCount == totalNoOfItems;
                }
            }
        } catch (Exception e) {
        	log.error("Exception in validating row count");
            //e.printStackTrace();
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
        if(rs.getString("REASON_CODE").equals("00")){
        	data.append(String.format("%-8s", dateInString)); // Processed date
        } else {
        	data.append(String.format("%-8s", ""));
        }
        return data.toString();
    }

    // Method to generate the file name dynamically from ReturnFileGenerator_seq
    private String generateDynamicFileName() throws IOException {
        return ReturnFileGenerator_seq.generateReturnFile();
    }
    
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName(); // Return the file name
    }
    
    
}
