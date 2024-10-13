package com.Sept.nach.C;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReturnFileGenerator_seq {

    private static final String OUTPUT_FILE_PATH = "J:\\W.O.R.K___\\Sample\\Nach\\src\\main\\java\\main\\RETURN_OUTPUT\\";
    private static final String SEQUENCE_FILE_PATH = "J:\\W.O.R.K___\\Sample\\Nach\\src\\main\\java\\main\\RETURN_OUTPUT\\sequence.txt";
    private static final String PROCESS_NAME = "ACH";
    private static final String TRANS_TYPE = "CR";
    private static final String BANK_IDENTIFIER = "SBIN";
    private static final String LOGIN_ID = "SBIN4353316";  // Example login ID
    private static final int MAX_SEQUENCE = 999999;

    // Method to generate the file with the desired naming structure
    public static String generateReturnFile() throws IOException {
        // Get the current sequence number from the file
        int sequenceNumber = readSequenceNumber();

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
        file.getParentFile().mkdirs(); // Ensure output directory exists
        file.createNewFile(); // Create the file if it does not exist

        System.out.println("File generated: " + file.getAbsolutePath());

        // Increment and save the updated sequence number back to the file
        saveSequenceNumber(sequenceNumber + 1);
        return file.getAbsolutePath(); // Return the full file path
    }

    // Method to read the sequence number from the file
    private static int readSequenceNumber() {
        try {
            // Check if the sequence file exists
            File sequenceFile = new File(SEQUENCE_FILE_PATH);
            if (!sequenceFile.exists()) {
                // If the file doesn't exist, create it with an initial sequence number of 1
                saveSequenceNumber(1);
                return 1;
            }
            // Read the sequence number from the file
            String sequenceStr = new String(Files.readAllBytes(Paths.get(SEQUENCE_FILE_PATH))).trim();
            return Integer.parseInt(sequenceStr);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            // In case of any error, return 1 as the default sequence number
            return 1;
        }
    }

    // Method to save the updated sequence number to the file
    private static void saveSequenceNumber(int sequenceNumber) {
        try (FileWriter writer = new FileWriter(SEQUENCE_FILE_PATH)) {
            writer.write(String.valueOf(sequenceNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            generateReturnFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
