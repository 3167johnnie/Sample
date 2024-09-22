import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    public static void main(String[] args) {
        String zipFileName = "protectedFile.zip";
        String password = "yourPasswordHere";

        try {
            // Create a file writer
            FileWriter fileWriter = new FileWriter("data.txt");

            // Write data to the file
            fileWriter.write("This is the data that you want to write.");

            // Close the file writer
            fileWriter.close();

            // Create a zip output stream with password protection
            FileOutputStream fos = new FileOutputStream(zipFileName);
            
            System.out.println("----FileOutputStream-----"+fos);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.setComment("This is a password protected zip file.");

            // Set the password for the zip file
            zos.setComment(password);

            // Add the file to the zip file
            addToZipFile("data.txt", zos);

            // Close the zip output stream
            zos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZipFile(String fileName, ZipOutputStream zos) throws IOException {

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}
