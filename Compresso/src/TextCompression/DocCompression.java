package TextCompression;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.aspose.words.*;
import application.Main;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DocCompression extends Application {

    private VBox root;
    private String fullPath;
    private Label fileSizeLabel;
    private Label compressedFileSizeLabel;
    private ComboBox<String> qualityComboBox;
    private WebView webView;
    private Map<String, CompressionLevel> qualityMap;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DOC Compression");

        Button chooseButton = new Button("Choose DOC File");
        chooseButton.setOnAction(e -> selectDocFile(primaryStage));

        Button compressButton = new Button("Compress");
        compressButton.setOnAction(e -> compressDoc());

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

    private void selectDocFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select DOC File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DOC Files (*.doc, *.docx)", "*.doc", "*.docx");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fullPath = selectedFile.getAbsolutePath();
            System.out.println(fullPath);
            updateFileSizeLabel(selectedFile.length());
        }
    }

    private void compressDoc() {
        if (fullPath != null) {
            File inputFile = new File(fullPath);
            try {
                if (fullPath.endsWith(".doc") || fullPath.endsWith(".docx")) {
                    Document doc = new Document(inputFile.getAbsolutePath());
                    doc.cleanup();

                    OoxmlSaveOptions saveOptions = new OoxmlSaveOptions();
                    saveOptions.setCompressionLevel(CompressionLevel.MAXIMUM);

                    String compressedFileName = inputFile.getParent() + File.separator + getCompressedFileName(inputFile.getName());
                    doc.save(compressedFileName, saveOptions);

                    File compressedFile = new File(compressedFileName);
                    updateCompressedFileSizeLabel(compressedFile.length());

                    showAlert("Compression Completed", "Compression done. Compressed file saved at: " + compressedFileName);
                } else {
                    showAlert("Unsupported Format", "Unsupported file format.");
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                if (fullPath.endsWith(".doc") || fullPath.endsWith(".docx")) {
                    // Load the document content into the WebView
                    loadDocumentInWebView(inputFile);
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

    private void loadDocumentInWebView(File file) throws Exception {
        Document doc = new Document(file.getAbsolutePath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlSaveOptions options = new HtmlSaveOptions();
        doc.save(outputStream, options);
        byte[] htmlBytes = outputStream.toByteArray();
        String htmlContent = new String(htmlBytes);
        webView.getEngine().loadContent(htmlContent);
    }

    private String getCompressedFileName(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf(".");
        String nameWithoutExtension = originalFileName.substring(0, dotIndex);
        String extension = originalFileName.substring(dotIndex);
        return nameWithoutExtension + "_compressed" + extension;
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

    public static void main(String[] args) {
        launch(args);
    }
}
