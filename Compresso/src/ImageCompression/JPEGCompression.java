package ImageCompression;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javafx.scene.control.ProgressBar;
import javax.imageio.event.*;
import javax.imageio.stream.ImageInputStream;

public class JPEGCompression {

    public static void compressJPEG(String fullPath, float compressionQuality, File outputFile, ProgressBar progressBar) throws IOException {
    File inputFile = new File(fullPath);
    BufferedImage image = ImageIO.read(inputFile);
    System.out.println(compressionQuality);

    // Get the JPEG writer
    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
    ImageWriter writer = writers.next();

    // Set compression parameters
    JPEGImageWriteParam writeParam = new JPEGImageWriteParam(null);
    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    writeParam.setCompressionQuality(compressionQuality);

    // Set up progress bar
    final long totalBytes = inputFile.length();
    ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile);
    writer.setOutput(outputStream);
    writer.prepareWriteSequence(null);

    // Write the compressed image
    writer.write(null, new IIOImage(image, null, null), writeParam);

    // Close the streams
    writer.endWriteSequence();
    writer.dispose();
    outputStream.close();
}


    private static class ProgressBarListener implements IIOWriteProgressListener {

        private final ProgressBar progressBar;
        private final long totalBytes;
        private final ImageWriteParam writeParam;
        private long bytesWritten;

        public ProgressBarListener(ProgressBar progressBar, long totalBytes, ImageWriteParam writeParam) {
            this.progressBar = progressBar;
            this.totalBytes = totalBytes;
            this.writeParam = writeParam;
        }

//        @Override
//        public void imageProgress(ImageWriter source, float percentageDone) {
//            // Update the progress bar based on the percentage of work done
//            progressBar.setProgress(percentageDone / 100.0);
//        }
        @Override
        public void imageProgress(ImageWriter source, float bytesWritten) {
            this.bytesWritten += bytesWritten;
            float progress = (float) this.bytesWritten / totalBytes;
            if (writeParam.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
                progressBar.setProgress(progress
                );
            } else {
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            }
        }

        @Override
        public void imageStarted(ImageWriter source, int imageIndex
        ) {
            // Reset the bytesWritten counter before starting to write the image
            bytesWritten = 0;
        }

        @Override
        public void imageComplete(ImageWriter source
        ) {
            // Do nothing
        }

        @Override
        public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex
        ) {
            // Do nothing
        }

        @Override
        public void thumbnailProgress(ImageWriter source, float percentageDone
        ) {
            // Do nothing
        }

        @Override
        public void thumbnailComplete(ImageWriter source
        ) {
            // Do nothing
        }

        @Override
        public void writeAborted(ImageWriter source
        ) {
            // Do nothing
        }

//        @Override
//        public void imageProgress(ImageWriter source, long bytesWritten) {
//            this.bytesWritten += bytesWritten;
//            float progress = (float) this.bytesWritten / totalBytes;
//            if (writeParam.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
//                progressBar.setProgress(progress
//                );
//            } else {
//                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
//            }
//        }
    }
}
