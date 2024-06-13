package ImageCompression;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.event.*;
import javafx.scene.control.ProgressBar;

public class PNGCompression {

    public static void compressPNG(String fullPath, float compressionQuality, File outputFile, ProgressBar progressBar) throws IOException {
        File inputFile = new File(fullPath);
        BufferedImage image = ImageIO.read(inputFile);

        // Get the PNG writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = writers.next();

        // Set compression parameters
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(compressionQuality);

        // Set up progress bar
        final long totalBytes = inputFile.length();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
        writer.setOutput(outputStream);
        writer.prepareWriteSequence(null);

        // Write the compressed image
        final ImageWriteParam finalWriteParam = writeParam;
        writer.addIIOWriteProgressListener(new PNGProgressBarListener(progressBar, totalBytes, finalWriteParam));
        writer.write(null, new IIOImage(image, null, null), writeParam);

        // Close the writer
        writer.endWriteSequence();
        writer.dispose();
        outputStream.close();
    }

    private static class PNGProgressBarListener implements IIOWriteProgressListener {

        private final ProgressBar progressBar;
        private final long totalBytes;
        private final ImageWriteParam writeParam;
        private long bytesWritten;

        public PNGProgressBarListener(ProgressBar progressBar, long totalBytes, ImageWriteParam writeParam) {
            this.progressBar = progressBar;
            this.totalBytes = totalBytes;
            this.writeParam = writeParam;
            this.bytesWritten = 0;
        }

        @Override
        public void imageStarted(ImageWriter source, int imageIndex) {
            // Reset the bytesWritten counter before starting to write the image
            bytesWritten = 0;
        }

        @Override
        public void imageProgress(ImageWriter source, float percentageDone) {
            // Update progress bar based on percentage
            progressBar.setProgress(percentageDone);
        }

        @Override
        public void imageComplete(ImageWriter source) {
            // Set progress to 100% after image completion
            progressBar.setProgress(1.0);
        }

        @Override
        public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {
            // Do nothing
        }

        @Override
        public void thumbnailProgress(ImageWriter source, float percentageDone) {
            // Do nothing
        }

        @Override
        public void thumbnailComplete(ImageWriter source) {
            // Do nothing
        }

        @Override
        public void writeAborted(ImageWriter source) {
            // Do nothing
        }
    }
}
