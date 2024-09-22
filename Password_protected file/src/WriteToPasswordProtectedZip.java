import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WriteToPasswordProtectedZip {

    public static void main(String[] args) {
        // Data to be written to the text file
        String data = "Hello, this is a sample text data.";

        // Create a zip file with a password
        String zipFileName = "protectedFile.zip";
        String password = "YourPasswordHere";

        try {
            // Create a FileWriter and write data to a text file
            try (FileWriter fileWriter = new FileWriter("J:\\W.O.R.K___\\Sample\\File\\data.txt")) {
                fileWriter.write(data);
            }

            // Create a zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            zipOut.setMethod(ZipOutputStream.DEFLATED);
            zipOut.setComment("Password protected file");

            // Write data.txt to the zip file
            try (FileInputStream fis = new FileInputStream("J:\\W.O.R.K___\\Sample\\File\\data.txt")) {
                byte[] buffer = new byte[1024];
                int length;
                zipOut.putNextEntry(new ZipEntry("J:\\W.O.R.K___\\Sample\\File\\data.txt"));
                while ((length = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }
                zipOut.closeEntry();
            }

            zipOut.close();
            fos.close();

            // Delete the temporary data.txt file
            File file = new File("J:\\W.O.R.K___\\Sample\\File1\\data.txt");
            
            System.out.println("(file.delete())"+ file);
			/*
			 * if (file.delete()) {
			 * System.out.println("Temporary file deleted successfully!"); } else {
			 * System.out.println("Failed to delete the temporary file."); }
			 */

            System.out.println("Data has been written to the zip file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
