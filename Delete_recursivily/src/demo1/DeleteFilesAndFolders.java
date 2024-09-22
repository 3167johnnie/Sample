package demo1;

import java.io.*;
import java.nio.file.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class DeleteFilesAndFolders {

    public static void main(String[] args) {
        try {
            Properties configProperties = new Properties();
            InputStream inputConfig = new FileInputStream("J:\\W.O.R.K___\\Sample\\Delete_recursivily\\src\\demo1\\config.properties");
            configProperties.load(inputConfig);

			/*
			 * Properties outputProperties = new Properties(); InputStream inputOutput = new
			 * FileInputStream("output.properties"); outputProperties.load(inputOutput);
			 */

            String sourcePath = configProperties.getProperty("sourcePath");
            String outputPath = configProperties.getProperty("outputPath");
            String deletedControlFileName = configProperties.getProperty("deletedControlFileName");

            File sourceFolder = new File(sourcePath);
            File outputFolder = new File(outputPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            deleteFilesAndFolders(sourceFolder, outputFolder, deletedControlFileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFilesAndFolders(File sourceFolder, File outputFolder, String deletedControlFileName) throws IOException {
        if (sourceFolder.isDirectory()) {
            File[] files = sourceFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        File newOutputFolder = new File(outputFolder, file.getName());
                        newOutputFolder.mkdirs();
                        deleteFilesAndFolders(file, newOutputFolder, deletedControlFileName);
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String deletionTime = dateFormat.format(new Date());

                        Path sourcePath = file.toPath();
                        Path destinationPath = new File(outputFolder, file.getName()).toPath();
                        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                        String logFileName = deletedControlFileName + "_" + dateFormat.format(new Date()) + ".txt";
                        FileWriter writer = new FileWriter(new File(outputFolder, logFileName), true);
                        writer.write("Deleted File Name: " + file.getName() + "\n");
                        writer.write("Creation Date: " + new Date(file.lastModified()) + "\n");
                        writer.write("Deletion Time: " + deletionTime + "\n\n");
                        writer.close();

                        file.delete();
                    }
                }
            }
        }
        sourceFolder.delete();
    }
}
