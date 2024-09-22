package demo2_with_controlfile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DeleteFolderAndFiles {

    private static Properties properties;
    private static final Logger LOGGER = Logger.getLogger(DeleteFolderAndFiles.class.getName());

    public static void main(String[] args) throws IOException {
        
            setupLogger();

            loadProperties();
            String sourcePath = properties.getProperty("source.path");
            String outputPath = properties.getProperty("output.path");
            String controlFolderPath = properties.getProperty("control.folder.path");
            File sourceFolder = new File(sourcePath);
            File outputFolder = new File(outputPath);
            File controlFolder = new File(controlFolderPath);

            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            if (!controlFolder.exists()) {
                controlFolder.mkdirs();
            }

            File[] files = sourceFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    moveFile(file, outputFolder);
                }
            }

            Files.walkFileTree(sourceFolder.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        Files.delete(file);
                        recordDeletion(file.toFile(), controlFolder);
                    } catch (IOException e) {
                        LOGGER.severe("Failed to delete file: " + e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    try {
                        Files.delete(dir);
                        recordDeletion(dir.toFile(), controlFolder);
                    } catch (IOException e) {
                        LOGGER.severe("Failed to delete directory: " + e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

    }

    private static void moveFile(File file, File outputFolder) throws IOException {
        if (file.isDirectory()) {
            File newDir = new File(outputFolder, file.getName());
            newDir.mkdir();
            for (File subFile : file.listFiles()) {
                moveFile(subFile, newDir);
            }
        } else {
            try {
                Files.move(file.toPath(), outputFolder.toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.severe("Failed to move file: " + e.getMessage());
            }
        }
    }

    private static void recordDeletion(File file, File controlFolder) throws IOException {
        try {
            String fileName = "DeletedControl_" + new Date().toString().replace(" ", "_").replace(":", "-") + ".txt";
            File recordFile = new File(controlFolder, fileName);

            try (FileWriter writer = new FileWriter(recordFile, true)) {
                writer.write("Name: " + file.getName() + "\n");
                writer.write("Path: " + file.getPath() + "\n");
                writer.write("Creation Date: " + new Date(file.lastModified()).toString() + "\n");
                writer.write("Deletion Time: " + new Date().toString() + "\n");
                writer.write("\n");
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to record deletion: " + e.getMessage());
        }
    }

    private static void loadProperties() throws IOException {
        properties = new Properties();
        InputStream input = new FileInputStream("J:\\W.O.R.K___\\Sample\\Delete_recursivily\\src\\demo2_with_controlfile\\config.properties");
        properties.load(input);
    }

    private static void setupLogger() throws IOException {
        FileHandler fileHandler = new FileHandler("logging.log", true);
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
        LOGGER.addHandler(fileHandler);
    }
}
