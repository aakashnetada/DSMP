module Compresso {
	requires javafx.controls;
	requires java.desktop;
	requires javafx.web;
	requires spire.pdf;
	requires aspose.words;
	exports ZIPGZCompression;
	opens application to javafx.graphics, javafx.fxml;
	opens ZIPGZCompression to javafx.fxml;
}
