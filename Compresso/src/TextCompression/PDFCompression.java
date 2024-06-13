package TextCompression;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.conversion.compression.ImageCompressionOptions;
import com.spire.pdf.conversion.compression.ImageQuality;
import com.spire.pdf.conversion.compression.PdfCompressor;
import com.spire.pdf.conversion.compression.TextCompressionOptions;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import application.Main;
import java.io.File;
import java.text.DecimalFormat;

public class PDFCompression extends Application {

    private VBox root;
    private String fullPath;
    private Label fileSizeLabel;
    private Label compressedFileSizeLabel;
    private WebView webView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PDF Compression");

        Button chooseButton = new Button("Choose PDF File");
        chooseButton.setOnAction(e -> selectPDFFile(primaryStage));

        Button compressButton = new Button("Compress");
        compressButton.setOnAction(e -> compressPDF());

        Button viewButton = new Button("View Document");
        viewButton.setOnAction(e -> viewDocument());

        fileSizeLabel = new Label();
        compressedFileSizeLabel = new Label();

        VBox sizeBox = new VBox(10);
        sizeBox.setAlignment(Pos.CENTER_LEFT);
        sizeBox.getChildren().addAll(new Label("Original File Size:"), fileSizeLabel, new Label("Compressed File Size:"), compressedFileSizeLabel);

        webView = new WebView();

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack(primaryStage));

        root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(chooseButton, compressButton, viewButton, sizeBox, webView, backButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void selectPDFFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fullPath = selectedFile.getAbsolutePath();
            System.out.println(fullPath);
            updateFileSizeLabel(selectedFile.length());
        }
    }

    private void compressPDF() {
        if (fullPath != null) {
            File inputFile = new File(fullPath);
            try {
                if (fullPath.endsWith(".pdf")) {
                    PdfCompressor compressor = new PdfCompressor(inputFile.getAbsolutePath());

                    TextCompressionOptions textCompression = compressor.getOptions().getTextCompressionOptions();
                    textCompression.setCompressFonts(true);

                    ImageCompressionOptions imageCompression = compressor.getOptions().getImageCompressionOptions();
                    imageCompression.setImageQuality(ImageQuality.Low);
                    imageCompression.setResizeImages(true);
                    imageCompression.setCompressImage(true);

                    String originalDirectory = inputFile.getParent(); // Get the directory of the original file
                    String compressedFileName = getCompressedFileName(inputFile.getName(), originalDirectory); // Pass the original directory
                    compressor.compressToFile(compressedFileName);

                    File compressedFile = new File(compressedFileName);
                    updateCompressedFileSizeLabel(compressedFile.length());

                    showAlert("Compression Completed", "Compression done. Compressed file saved at: " + compressedFileName);
                } else {
                    showAlert("Unsupported Format", "Unsupported file format.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No File Selected", "Please select a valid file.");
        }
    }

    private void viewDocument() {
        if (fullPath != null) {
            File inputFile = new File(fullPath);
            try {
                if (fullPath.endsWith(".pdf")) {
                    loadPDFInWebView(inputFile);
                } else {
                    showAlert("Unsupported Format", "Unsupported file format.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred while viewing the document: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("No File Selected", "Please select a valid file.");
        }
    }

    private void loadPDFInWebView(File file) throws Exception {
        PdfDocument document = new PdfDocument();
        document.loadFromFile(file.getAbsolutePath());

        // Save the compressed PDF to a temporary file
        File tempFile = File.createTempFile("compressed_pdf", ".pdf");
        document.saveToFile(tempFile.getAbsolutePath(), com.spire.pdf.FileFormat.PDF);

        // Load the temporary file into the WebView
        webView.getEngine().load(tempFile.toURI().toString());
    }

    private String getCompressedFileName(String originalFileName, String originalDirectory) {
        int dotIndex = originalFileName.lastIndexOf(".");
        String nameWithoutExtension = originalFileName.substring(0, dotIndex);
        String extension = originalFileName.substring(dotIndex);
        return originalDirectory + File.separator + nameWithoutExtension + "_compressed" + extension;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateFileSizeLabel(long fileSize) {
        fileSizeLabel.setText(formatFileSize(fileSize));
    }

    private void updateCompressedFileSizeLabel(long fileSize) {
        compressedFileSizeLabel.setText(formatFileSize(fileSize));
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

    private String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.##");
        String fileSizeString;
        double fileSizeInKB = (double) fileSize / 1024;
        if (fileSizeInKB < 1024) {
            fileSizeString = df.format(fileSizeInKB) + " KB";
        } else {
            double fileSizeInMB = fileSizeInKB / 1024;
            fileSizeString = df.format(fileSizeInMB) + " MB";
        }
        return fileSizeString;
    }

    public static void main(String[] args) {
        launch(args);
    }
}