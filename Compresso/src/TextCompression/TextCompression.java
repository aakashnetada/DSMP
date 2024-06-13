package TextCompression;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import application.Main;

public class TextCompression extends Application {

    private VBox root;
    private String fullPath;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Text Compression");

        Button pdfCompressionButton = new Button("PDF Compression");
        pdfCompressionButton.setOnAction(e -> pdfCompression(primaryStage));

        Button docCompressionButton = new Button("DOC Compression");
        docCompressionButton.setOnAction(e -> docCompression(primaryStage));
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack(primaryStage));

        root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(pdfCompressionButton, docCompressionButton, backButton);

        Scene scene = new Scene(root, 300, 250);
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

    private void pdfCompression(Stage stage) {
        // Add your PDF compression logic here
        PDFCompression pdf = new PDFCompression();
        pdf.start(stage);
        System.out.println("PDF Compression");
    }

    private void docCompression(Stage stage) {
        // Add your DOC compression logic here
        DocCompression doc = new DocCompression();
        doc.start(stage);
        System.out.println("DOC Compression");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
