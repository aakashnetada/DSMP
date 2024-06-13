package application;

import ImageCompression.ImageCompression;
import TextCompression.TextCompression;
import ZIPGZCompression.Zipgzui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	// Inside your start() method
	@Override
	public void start(Stage primaryStage) {
		
	    primaryStage.setTitle("File Compression Project");

	    Label titleLabel = new Label("File Compression Project");
	    titleLabel.getStyleClass().add("title-label"); // Apply CSS class to titleLabel

	    Button imageCompressionButton = new Button("Image Compression");
	    imageCompressionButton.getStyleClass().add("button"); // Apply CSS class to imageCompressionButton
	    imageCompressionButton.setOnAction(e -> openImageCompression(primaryStage));

	    Button textCompressionButton = new Button("Text Compression");
	    textCompressionButton.getStyleClass().add("button"); // Apply CSS class to textCompressionButton
	    textCompressionButton.setOnAction(e -> openTextCompression(primaryStage));

	    Button gzCompressionButton = new Button("ZIP/GZ Compression");
	    gzCompressionButton.getStyleClass().add("button"); // Apply CSS class to gzCompressionButton
	    gzCompressionButton.setOnAction(e -> openZipGzUI(primaryStage));

	    VBox root = new VBox(20);
	    root.getStyleClass().add("root-vbox"); // Apply CSS class to root VBox
	    root.getChildren().addAll(titleLabel, imageCompressionButton, textCompressionButton, gzCompressionButton);

	    Scene scene = new Scene(root, 400, 300);
	    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Load CSS file
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}


    private void openImageCompression(Stage primaryStage) {
        ImageCompression ic = new ImageCompression();
        ic.start(primaryStage);
    }

    private void openZipGzUI(Stage primaryStage) {
        Zipgzui zipgzui = new Zipgzui();
        zipgzui.start(primaryStage);
    }

    private void openTextCompression(Stage primaryStage) {
        TextCompression tc = new TextCompression();
        tc.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
