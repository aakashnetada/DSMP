package ImageCompression;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.File;
import java.io.IOException;

import application.Main;

public class ImageCompression extends Application {

	private ImageView imageView;
    private MenuButton qualityMenu;
    private ProgressBar progressBar;
    private Label originalSizeLabel, compressedSizeLabel;
    private String fullPath;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Compression");

        // Create UI components
        Label label = new Label("Select Image:");
        TextField filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setPrefWidth(300);

        Button chooseButton = new Button("Choose File");
        chooseButton.setOnAction(e -> selectImageFile(primaryStage, filePathField));

        Label qualityLabel = new Label("Quality:");
        qualityMenu = new MenuButton("Select Quality");
        qualityMenu.setPrefWidth(200);
        qualityMenu.setDisable(true);

        for (int i = 10; i <= 100; i += 10) {
            MenuItem item = new MenuItem(Integer.toString(i));
            item.setOnAction(event -> qualityMenu.setText(item.getText()));
            qualityMenu.getItems().add(item);
        }

        Button compressButton = new Button("Compress");
        compressButton.setOnAction(e -> compressImage());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack(primaryStage));

        originalSizeLabel = new Label();
        compressedSizeLabel = new Label();

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Layout
        VBox controlsVBox = new VBox(10, label, filePathField, chooseButton, qualityLabel, qualityMenu, compressButton, backButton, originalSizeLabel, compressedSizeLabel, progressBar);
        controlsVBox.setPadding(new Insets(10));
        controlsVBox.setAlignment(Pos.CENTER);

        HBox mainHBox = new HBox(10, imageView, controlsVBox);
        mainHBox.setPadding(new Insets(10));
        mainHBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(mainHBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private void selectImageFile(Stage stage, TextField filePathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fullPath = selectedFile.getAbsolutePath();
            filePathField.setText(selectedFile.getAbsolutePath());
            imageView.setImage(new javafx.scene.image.Image(selectedFile.toURI().toString()));
            originalSizeLabel.setText("Original Size: " + selectedFile.length() + " bytes");
            qualityMenu.setDisable(false); // Enable quality menu after selecting the image file
        }
    }

    private void compressImage() {
        File selectedFile = new File(fullPath);
        String selectedQualityText = qualityMenu.getText();
        if (selectedQualityText.isEmpty()) {
            showAlert("Please select a quality level.");
            return;
        }

        float quality = Float.parseFloat(selectedQualityText) / 100;
        String extension = getFileExtension(selectedFile);

        if ("png".equalsIgnoreCase(extension)) {
            compressPNG(selectedFile, quality);
        } else if ("jpeg".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension)) {
            compressJPEG(selectedFile, quality);
        } else {
            showAlert("Unsupported file format.");
        }
    }

//    private void compressPNG(File selectedFile, float quality) {
//        progressBar.setProgress(0);
//        new Thread(() -> {
//            try {
//                File parentDir = selectedFile.getParentFile();
//                if (parentDir != null) {
//                    String outputFileName = selectedFile.getName().replaceFirst("[.][^.]+$", "_compressed.png");
//                    File outputFile = new File(parentDir, outputFileName);
//
//                    PNGCompression.compressPNG(fullPath, quality, outputFile, progressBar);
//
//                    long compressedSize = outputFile.length();
//                    double compressionRatio = (double) selectedFile.length() / compressedSize;
//                    compressedSizeLabel.setText("Compressed Size: " + compressedSize + " bytes");
//                    showAlert("PNG compression completed. Compression Ratio: " + compressionRatio);
//                } else {
//                    showAlert("Error: Parent directory does not exist.");
//                }
//            } catch (IOException e) {
//                showAlert("Error compressing PNG: " + e.getMessage());
//            }
//        }).start();
//    }
    private void compressPNG(File selectedFile, float quality) {
        progressBar.setProgress(0);
        new Thread(() -> {
            try {
                File parentDir = selectedFile.getParentFile();
                if (parentDir != null) {
                    String outputFileName = selectedFile.getName().replaceFirst("[.][^.]+$", "_compressed.png");
                    File outputFile = new File(parentDir, outputFileName);

                    PNGCompression.compressPNG(fullPath, quality, outputFile, progressBar);

                    long compressedSize = outputFile.length();
                    double compressionRatio = (double) selectedFile.length() / compressedSize;

                    // Update UI on the JavaFX application thread
                    Platform.runLater(() -> {
                        compressedSizeLabel.setText("Compressed Size: " + compressedSize + " bytes");
                        showAlert("PNG compression completed. Compression Ratio: " + compressionRatio);
                    });
                } else {
                    showAlert("Error: Parent directory does not exist.");
                }
            } catch (IOException e) {
                showAlert("Error compressing PNG: " + e.getMessage());
            }
        }).start();
    }

//    private void compressJPEG(File selectedFile, float quality) {
//        progressBar.setProgress(0);
//        new Thread(() -> {
//            try {
//                File parentDir = selectedFile.getParentFile();
//                if (parentDir != null) {
//                    String outputFileName = selectedFile.getName().replaceFirst("[.][^.]+$", "_compressed.jpg");
//                    File outputFile = new File(parentDir, outputFileName);
//
//                    JPEGCompression.compressJPEG(fullPath, quality, outputFile, progressBar);
//
//                    long compressedSize = outputFile.length();
//                    double compressionRatio = (double) selectedFile.length() / compressedSize;
//                    compressedSizeLabel.setText("Compressed Size: " + compressedSize + " bytes");
//                    showAlert("JPEG compression completed. Compression Ratio: " + compressionRatio);
//                } else {
//                    showAlert("Error: Parent directory does not exist.");
//                }
//            } catch (IOException e) {
//                showAlert("Error compressing JPEG: " + e.getMessage());
//            }
//        }).start();
//    }
    private void compressJPEG(File selectedFile, float quality) {
        progressBar.setProgress(0);
        new Thread(() -> {
            try {
                File parentDir = selectedFile.getParentFile();
                if (parentDir != null) {
                    String outputFileName = selectedFile.getName().replaceFirst("[.][^.]+$", "_compressed.jpg");
                    File outputFile = new File(parentDir, outputFileName);

                    JPEGCompression.compressJPEG(fullPath, quality, outputFile, progressBar);

                    long compressedSize = outputFile.length();
                    double compressionRatio = (double) selectedFile.length() / compressedSize;

                    // Update UI on the JavaFX application thread
                    Platform.runLater(() -> {
                        compressedSizeLabel.setText("Compressed Size: " + compressedSize + " bytes");
                        showAlert("JPEG compression completed. Compression Ratio: " + compressionRatio);
                    });
                } else {
                    showAlert("Error: Parent directory does not exist.");
                }
            } catch (IOException e) {
                showAlert("Error compressing JPEG: " + e.getMessage());
            }
        }).start();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return name.substring(lastIndexOfDot + 1);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Image Compression");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
