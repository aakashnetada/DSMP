package ZIPGZCompression; 

import java.io.ByteArrayOutputStream;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import application.Main;

public class Zipgzui extends Application {

    private VBox root;
    private ProgressBar progressBar;

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        // Creating UI components
        Label titleLabel = new Label("Zip/GZip Compression and Decompression");
        Button zipCompressButton = new Button("Zip Compression");
        Button gzipCompressButton = new Button("Gzip Compression");
        Button zipDecompressButton = new Button("Zip Decompression");
        Button gzipDecompressButton = new Button("Gzip Decompression");
        Button backButton = new Button("Back");
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);

        zipCompressButton.setOnAction(e -> {
            List<File> filesToCompress = getSelectedFilesOrDirectories(primaryStage, "Select files or directories to compress", true);
            if (filesToCompress != null && !filesToCompress.isEmpty()) {
                File zipFile = getZipFile(primaryStage, "Save Zip File As", filesToCompress);
                if (zipFile != null) {
                    try {
                        compressToZip(filesToCompress, zipFile, progressBar);
                        showNotification("Files have been compressed and saved as: " + zipFile.getName());
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Handle or display error
                    }
                }
            }
        });

        // Handling compression - Gzip Compression
        gzipCompressButton.setOnAction(e -> {
            List<File> filesToCompress = getSelectedFilesOrDirectories(primaryStage, "Select files to compress", false);
            if (filesToCompress != null && !filesToCompress.isEmpty()) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Choose output directory");
                File outputDirectory = directoryChooser.showDialog(primaryStage);
                if (outputDirectory != null) {
                    try {
                        compressToGzip(filesToCompress, outputDirectory, progressBar);
                        showNotification("Files have been compressed and saved to: " + outputDirectory.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Handle or display error
                    }
                }
            }
        });

        // Handling decompression - Zip Decompression
        zipDecompressButton.setOnAction(e -> {
            File selectedFile = getFileToDecompress(primaryStage, "Select Zip file to decompress");
            if (selectedFile != null) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select output directory");
                File outputDirectory = directoryChooser.showDialog(primaryStage);
                if (outputDirectory != null) {
                    try {
                        decompressZip(selectedFile, outputDirectory, progressBar);
                        showNotification("Zip file has been decompressed to: " + outputDirectory.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Handle or display error
                    }
                }
            }
        });

        // Handling decompression - Gzip Decompression
        gzipDecompressButton.setOnAction(e -> {
            File selectedFile = getFileToDecompress(primaryStage, "Select Gzip file to decompress");
            if (selectedFile != null) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select output directory");
                File outputDirectory = directoryChooser.showDialog(primaryStage);
                if (outputDirectory != null) {
                    try {
                        decompressGzip(selectedFile, outputDirectory, progressBar);
                        showNotification("Gzip file has been decompressed to: " + outputDirectory.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Handle or display error
                    }
                }
            }
        });


        // Back button action
        backButton.setOnAction(e -> goBack(primaryStage));

        // Adding components to the scene
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleBox, zipCompressButton, gzipCompressButton, zipDecompressButton, gzipDecompressButton, progressBar, backButton);
        Scene scene = new Scene(root, 600, 300);

        // Setting up the stage
        primaryStage.setTitle("File Compression and Decompression");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void goBack(Stage primaryStage) {
        Stage mainStage = new Stage();
        Main Main = new Main();
        try {
            Main.start(mainStage);
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<File> getSelectedFilesOrDirectories(Stage primaryStage, String title, boolean allowMultipleSelection) {
        progressBar.setProgress(0);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (allowMultipleSelection) {
            return fileChooser.showOpenMultipleDialog(primaryStage);
        } else {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            return selectedFile != null ? List.of(selectedFile) : null;
        }
    }

    private File getFileToDecompress(Stage primaryStage, String title) {
        progressBar.setProgress(0);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(primaryStage);
    }

    private void compressToZip(List<File> files, File zipFile, ProgressBar progressBar) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            long totalBytes = calculateTotalSize(files);
            long processedBytes = 0;
            for (File file : files) {
                if (file.isFile()) {
                    compressFile(null, file, zos, progressBar, totalBytes, processedBytes);
                    processedBytes += file.length();
                } else if (file.isDirectory()) {
                    addFilesInDirectory(file, file, zos, progressBar, totalBytes, processedBytes);
                }
            }
        }
    }

    private void addFilesInDirectory(File rootDir, File directory, ZipOutputStream zos, ProgressBar progressBar, long totalBytes, long processedBytes) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addFilesInDirectory(rootDir, file, zos, progressBar, totalBytes, processedBytes);
                } else {
                    String entryName = getRelativePath(rootDir, file);
                    compressFile(rootDir, file, zos, progressBar, totalBytes, processedBytes);
                    processedBytes += file.length();
                }
            }
        }
    }

    private void compressToGzip(List<File> filesToCompress, File outputDirectory, ProgressBar progressBar) throws IOException {
        for (File fileToCompress : filesToCompress) {
            if (fileToCompress.isDirectory()) {
                compressDirectoryToGzip(fileToCompress, new File(outputDirectory, fileToCompress.getName() + ".gz"), progressBar);
            } else {
                File gzipFile = new File(outputDirectory, fileToCompress.getName() + ".gz");
                try (FileInputStream fis = new FileInputStream(fileToCompress); FileOutputStream fos = new FileOutputStream(gzipFile); GZIPOutputStream gzipOS = new GZIPOutputStream(fos)) {
                    compressFileToGzip(fileToCompress, gzipOS, progressBar, fileToCompress.length(), 0);
                }
            }
        }
    }

    private void compressDirectoryToGzip(File directory, File gzipFile, ProgressBar progressBar) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(gzipFile); GZIPOutputStream gzipOS = new GZIPOutputStream(fos)) {
            long totalBytes = calculateDirectorySize(directory);
            long processedBytes = 0;
            compressDirectoryContentsToGzip(directory, gzipOS, progressBar, totalBytes, processedBytes);
        }
    }

    private void compressDirectoryContentsToGzip(File directory, GZIPOutputStream gzipOS, ProgressBar progressBar, long totalBytes, long processedBytes) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    compressDirectoryContentsToGzip(file, gzipOS, progressBar, totalBytes, processedBytes);
                } else {
                    compressFileToGzip(file, gzipOS, progressBar, totalBytes, processedBytes);
                    processedBytes += file.length();
                }
            }
        }
    }

    private static void compressFileToGzip(File fileToCompress, GZIPOutputStream gzipOS, ProgressBar progressBar, long totalBytes, long processedBytes) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileToCompress)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzipOS.write(buffer, 0, len);
                processedBytes += len;
                updateProgressBar(progressBar, processedBytes, totalBytes);
            }
        }
    }

    public static void decompressZip(File zipFile, File outputDirectory, ProgressBar progressBar) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            long totalBytes = zipFile.length();
            long processedBytes = 0;
            while (zipEntry != null) {
                File newFile = new File(outputDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                            processedBytes += len;
                            updateProgressBar(progressBar, processedBytes, totalBytes);
                        }	
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }

    public static void decompressGzip(File gzipFile, File outputDirectory, ProgressBar progressBar) throws IOException {
        byte[] buffer = new byte[1024];
        try (GZIPInputStream gzipIS = new GZIPInputStream(new FileInputStream(gzipFile)); 
             FileOutputStream fos = new FileOutputStream(new File(outputDirectory, gzipFile.getName().replace(".gz", "")))) {
            int len;
            long totalBytes = gzipFile.length();
            long processedBytes = 0;
            while ((len = gzipIS.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                processedBytes += len;
                updateProgressBar(progressBar, processedBytes, totalBytes);
            }
        }
    }

    private static String readOriginalFileName(GZIPInputStream gzipIS) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int byteRead;
        while ((byteRead = gzipIS.read()) != 0) {
            baos.write(byteRead);
        }
        return baos.toString();
    }

    private void compressFile(File rootDir, File file, ZipOutputStream zos, ProgressBar progressBar, long totalBytes, long processedBytes) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            String entryName = rootDir == null ? file.getName() : getRelativePath(rootDir, file);
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
                processedBytes += len;
                updateProgressBar(progressBar, processedBytes, totalBytes);
            }
            zos.closeEntry();
        }
    }

    private long calculateTotalSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                totalSize += calculateDirectorySize(file);
            } else {
                totalSize += file.length();
            }
        }
        return totalSize;
    }

    private long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += calculateDirectorySize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private String getRelativePath(File rootDir, File file) {
        String rootPath = rootDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        return filePath.startsWith(rootPath) ? filePath.substring(rootPath.length()) : filePath;
    }

    private static void updateProgressBar(ProgressBar progressBar, long processedBytes, long totalBytes) {
        double progress = (double) processedBytes / totalBytes;
        progressBar.setProgress(progress);
    }

    private File getZipFile(Stage primaryStage, String title, List<File> selectedFiles) {
        progressBar.setProgress(0);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(generateDefaultZipFileName(selectedFiles));
        return fileChooser.showSaveDialog(primaryStage);
    }

    private String generateDefaultZipFileName(List<File> selectedFiles) {
        StringBuilder sb = new StringBuilder();
        for (File file : selectedFiles) {
            if (file.isDirectory()) {
                sb.append(file.getName()).append("_");
            } else {
                sb.append(getNameWithoutExtension(file.getName()));
            }
        }
        sb.append(".zip");
        return sb.toString();
    }


    private static String generateDefaultGzipFileName(File file) {
        String fileName = file.getName();
        return getNameWithoutExtension(file.toPath().getFileName().toString()) + ".gz";

    }

    private static String getNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private static void showNotification(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("File Compression");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
