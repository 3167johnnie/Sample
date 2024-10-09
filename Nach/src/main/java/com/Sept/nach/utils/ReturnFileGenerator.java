package com.Sept.nach.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnFileGenerator {

    private static final String OUTPUT_FILE_PATH = "J:\\W.O.R.K___\\Sample\\Nach\\src\\main\\java\\main\\RETURN_OUTPUT\\";
    private static final String PROCESS_NAME = "ACH";
    private static final String TRANS_TYPE = "CR";
    private static final String BANK_IDENTIFIER = "SBIN";
    private static final String LOGIN_ID = "SBIN4352203";  // Example login ID
    private static final int MAX_SEQUENCE = 999999;
    
    // Method to generate the file with the desired naming structure
    public static void generateReturnFile(int sequenceNumber) throws IOException {
        // Ensure sequence number is within the valid range
        if (sequenceNumber > MAX_SEQUENCE) {
            sequenceNumber = 1; // Reset to 1 after reaching the maximum value
        }
        
        // Format the current date as ddMMyyyy
        String currentDate = new SimpleDateFormat("ddMMyyyy").format(new Date());
        
        // Format the sequence number to a 6-digit number with leading zeros
        String sequence = String.format("%06d", sequenceNumber);
        
        // Generate the filename according to the specified format
        String fileName = String.format("%s-%s-%s-%s-%s-%s-RTN.txt", 
                            PROCESS_NAME, TRANS_TYPE, BANK_IDENTIFIER, LOGIN_ID, currentDate, sequence);
        
        // Create the file in the specified output path
        File file = new File(OUTPUT_FILE_PATH + fileName);
        FileWriter writer = new FileWriter(file);
        writer.write("This is a sample return file generated based on the given format."); // Add file content
        writer.close();
        
        System.out.println("File generated: " + file.getAbsolutePath());
    }
    
    public static void main(String[] args) {
        try {
            int sequenceNumber = 145086; // Example sequence number
            generateReturnFile(sequenceNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
