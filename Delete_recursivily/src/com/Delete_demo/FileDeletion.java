package com.Delete_demo;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileDeletion {
    public static void main(String[] args) {
        try {
            // Load properties file
            Properties prop = new Properties();
            InputStream input = new FileInputStream("J:\\W.O.R.K___\\Sample\\Delete_recursivily\\src\\com\\Delete_demo\\config.properties");
            prop.load(input);

            // Retrieve source and output paths
            String sourcePath = prop.getProperty("sourcePath");
            String outputPath = prop.getProperty("outputPath");
            String logFilePath = prop.getProperty("logFilePath");

            // Create output folder if it doesn't exist
            File outputFolder = new File(outputPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            // Create log file
            FileWriter logFileWriter = new FileWriter(logFilePath, true);

            // Iterate through files and folders in source directory
            File sourceFolder = new File(sourcePath);
            File[] files = sourceFolder.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    // Move file to output folder
                    Path source = Paths.get(file.getAbsolutePath());
                    Path target = Paths.get(outputPath + File.separator + file.getName());
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

                    // Write to log file
                    logFileWriter.write("File Name: " + file.getName() + "\n");
                    logFileWriter.write("Creation Date: " + getFileCreationTime(file.toPath()) + "\n");
                    logFileWriter.write("Deletion Time: " + getCurrentTime() + "\n\n");

                    // Delete file from source folder
                    file.delete();
                } else if (file.isDirectory()) {
                    // Move folder to output folder
                    Path source = Paths.get(file.getAbsolutePath());
                    Path target = Paths.get(outputPath + File.separator + file.getName());
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

                    // Write to log file
                    logFileWriter.write("Folder Name: " + file.getName() + "\n");
                    logFileWriter.write("Creation Date: " + getFileCreationTime(file.toPath()) + "\n");
                    logFileWriter.write("Deletion Time: " + getCurrentTime() + "\n\n");

                    // Delete folder from source folder
                    deleteFolder(file);
                }
            }
            logFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileCreationTime(Path path) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(attr.creationTime().toMillis()));
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
