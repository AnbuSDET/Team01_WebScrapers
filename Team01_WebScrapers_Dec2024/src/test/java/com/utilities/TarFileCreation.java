package com.utilities;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TarFileCreation {

    private static final Logger logger = Logger.getLogger(TarFileCreation.class.getName());
    private static String dbName;
    private static String dbUsername;
    private static String dbPassword;
    private static final String backupFilePath = "backup.sql";
    private static final String tarFilePath = "backup.tar";

    public static void main(String[] args) throws Throwable {
        try {
            // Load database credentials
        	 try {
             	dbName = PropertyFileReader.getGlobalValue("dbName");
         		dbUsername = PropertyFileReader.getGlobalValue("dbUsername");
         		dbPassword = PropertyFileReader.getGlobalValue("dbPassword");
             } catch (Exception e) {
                 logger.log(Level.SEVERE, "Error loading database credentials: ", e);
             }

            // Validate database credentials
            if (dbName == null || dbUsername == null || dbPassword == null) {
                throw new IllegalArgumentException("Database credentials are missing. Please check your configuration.");
            }

            // Create Postgres backup and TAR file
            createPostgresBackup(dbName, dbUsername, dbPassword, backupFilePath);
            createTarFile(backupFilePath, tarFilePath);

            logger.info("Backup completed successfully. TAR file created: " + tarFilePath);

            // Cleanup the temporary backup file
            deleteFile(backupFilePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during backup: ", e);
        }
    }

   

    public static void createPostgresBackup(String dbName, String dbUser, String dbPassword, String backupFilePath)
            throws IOException, InterruptedException {
        // Command for pg_dump
    	
    	 String pgDumpPath = "/Applications/Postgres.app/Contents/Versions/15/bin/pg_dump"; // Replace with actual path
    	    String command = String.format("%s -U %s -d %s -F p -f %s", pgDumpPath, dbUser, dbName, backupFilePath);
    	
       // String command = String.format("pg_dump -U %s -d %s -F p -f %s", dbUser, dbName, backupFilePath);

        // Set up process builder
        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        pb.environment().put("PGPASSWORD", dbPassword); // Pass password securely
        pb.redirectErrorStream(true); // Merge stdout and stderr

        Process process = pb.start();

        // Capture output (optional, useful for debugging)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Error creating database dump. Exit code: " + exitCode);
        }
    }

    public static void createTarFile(String sourceFilePath, String tarFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("Source file not found: " + sourceFilePath);
        }

        // Initialize the TAR output stream
        try (FileOutputStream fos = new FileOutputStream(tarFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             TarArchiveOutputStream tos = new TarArchiveOutputStream(bos)) {

            // Add the file to the TAR archive
            TarArchiveEntry tarEntry = new TarArchiveEntry(sourceFile, sourceFile.getName());
            tarEntry.setSize(sourceFile.length());
            tos.putArchiveEntry(tarEntry);

            // Write file content into the archive
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    tos.write(buffer, 0, bytesRead);
                }
            }

            // Close the current entry
            tos.closeArchiveEntry();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating TAR file: ", e);
            throw e;
        }
    }

    private static void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(new File(filePath).toPath());
            logger.info("Temporary file deleted: " + filePath);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to delete temporary file: " + filePath, e);
        }
    }
}
