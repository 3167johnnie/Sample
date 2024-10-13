
// Modifications in ReturnFileGenerator_seq.java as per user's requirement
package com.Sept.nach.Z;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReturnFileGenerator_seq {
    private static final String OUTPUT_FILE_PATH = "/output/path/";

    // Generate return file based on ACH header ID
    public String generateReturnFile(int achHeaderId) {
        try {
            String fileName = "ReturnFile_" + achHeaderId + ".txt";
            File file = new File(OUTPUT_FILE_PATH + fileName);

            // Ensure directories exist
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file");
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("This is a return file for ACH header ID: " + achHeaderId);
            }

            System.out.println("File generated successfully: " + file.getAbsolutePath());
            return file.getName(); // Return the file name on success
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null on failure
        }
    }
}
