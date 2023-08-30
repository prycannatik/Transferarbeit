package com.transferarbeit;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.awt.Desktop;

import java.io.File;
import java.util.Map;

public class GUIController {

    @FXML
    private Button compressButton;
    @FXML
    private Button decompressButton;
    @FXML
    private Button chooseFileButton;
    @FXML
    private Button deleteFileButton;
    @FXML
    private Label selectedFileLabel;
    @FXML
    private TextArea statusTextArea;
    @FXML
    private Text deleteFileIcon;
    @FXML
    private ProgressBar progressBar;

    private File selectedFile;
    private Map<String, String> dictionary;

    @FXML
    public void btnChooseFileAction(ActionEvent event) {
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wählen Sie eine Datei aus");
        selectedFile = fileChooser.showOpenDialog(null);

        // Update UI
        if (selectedFile != null) {
            updateSelectedFile(selectedFile.getName());
            updateStatusText("Datei ausgewählt: " + selectedFile.getName());

            // Check if the file is compressed and update buttons accordingly
            boolean isCompressed = Utils.isCompressed(selectedFile);
            compressButton.setDisable(isCompressed);
            decompressButton.setDisable(!isCompressed);
        } else {
            updateStatusText("Keine Datei ausgewählt.");
        }
    }

    @FXML
    public void btnCompressAction(ActionEvent event) {
        if (selectedFile == null) {
            updateStatusText("Bitte zuerst eine Datei auswählen.");
            return;
        }
        // Disable buttons during compression
        compressButton.setDisable(true);
        decompressButton.setDisable(true);
        deleteFileIcon.setVisible(false);
        chooseFileButton.setDisable(true);

        // Create and start a new compression
        Task<Void> compressionTask = createCompressionTask();
        progressBar.progressProperty().bind(compressionTask.progressProperty());
        Thread compressionThread = new Thread(compressionTask);
        compressionThread.setDaemon(true);
        compressionThread.start();
    }

    @FXML
    public void btnDecompressAction(ActionEvent event) {
        if (selectedFile == null) {
            updateStatusText("Bitte zuerst eine Datei auswählen.");
            return;
        }

        // Disable buttons during decompression
        decompressButton.setDisable(true);
        compressButton.setDisable(true);
        deleteFileIcon.setVisible(false);
        chooseFileButton.setDisable(true);

        // Create and start a new decompression
        Task<Void> decompressionTask = createDecompressionTask();
        progressBar.progressProperty().bind(decompressionTask.progressProperty());
        Thread decompressionThread = new Thread(decompressionTask);
        decompressionThread.setDaemon(true);
        decompressionThread.start();
    }

    @FXML
    public void btnDeleteFileAction() {
        // Check if a file is selected and reset if yes
        if (selectedFile != null) {
            resetSelectedFile();
            updateStatusText("Dateiauswahl zurückgesetzt.");
        } else {
            updateStatusText("Keine Datei zum Zurücksetzen ausgewählt.");
        }
    }

    private Task<Void> createCompressionTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String text = FileService.readFile(selectedFile);

                    // Build the dictionary for compression
                    dictionary = DictionaryBuilder.buildDictionary(selectedFile);

                    // Compress the text using the dictionary
                    String compressedText = Utils.compressText(text, dictionary);
                    // Create output file for compressed content
                    File outputFile = new File(selectedFile.getParent(), "compressed_" + selectedFile.getName());

                    // Write the compressed text and dictionary to the output file
                    FileService.writeCompressedFile(outputFile, dictionary, compressedText);

                    // Calculate and display the compression rate and file sizes
                    long originalFileSize = selectedFile.length();
                    long compressedFileSize = outputFile.length();
                    double compressionRate = (1 - (double) compressedFileSize / originalFileSize) * 100;
                    double originalSizeMB = originalFileSize / (1024.0 * 1024.0);
                    double compressedSizeMB = compressedFileSize / (1024.0 * 1024.0);
                    updateStatusText("File successfully compressed: " + outputFile.getAbsolutePath() +
                            "\n\nCompression rate: " + String.format("%.2f%%", compressionRate) +
                            "\nOriginal size: " + String.format("%.2f MB", originalSizeMB) +
                            "\nCompressed size: " + String.format("%.2f MB", compressedSizeMB));

                    // Update progress and reset file selection
                    updateProgress(0, 1);
                    Platform.runLater(() -> resetSelectedFile());

                    // Open the folder containing the output file
                    Desktop.getDesktop().open(outputFile.getParentFile());

                } catch (Exception e) {
                    Platform.runLater(() -> updateStatusText("Error during compression: " + e.getMessage()));
                }
                return null;
            }
        };
    }

    private Task<Void> createDecompressionTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String compressedText = FileService.readFile(selectedFile);

                    // Decompress the text using the dictionary
                    String decompressedText = Utils.decompressText(compressedText);

                    // Create an output file for decompressed content
                    String fileName = selectedFile.getName();
                    String fileNameWithoutCompressed = fileName.substring(11); // Remove 'compressed_' prefix
                    File outputFile = new File(selectedFile.getParent(), "decompressed_" + fileNameWithoutCompressed);

                    FileService.writeDecompressedFile(outputFile, decompressedText);

                    // Update ui
                    updateProgress(0, 1);
                    Platform.runLater(() -> {
                        updateStatusText("File successfully decompressed: " + outputFile.getAbsolutePath());
                        resetSelectedFile();
                    });

                    Desktop.getDesktop().open(outputFile.getParentFile());

                } catch (Exception e) {
                    Platform.runLater(() -> updateStatusText("Error during decompression: " + e.getMessage()));
                }
                return null;
            }
        };
    }

    // Update label (selected file)
    private void updateSelectedFile(String filename) {
        selectedFileLabel.setText(filename);
        deleteFileIcon.setVisible(true);
    }

    // Reset selected file
    private void resetSelectedFile() {
        selectedFile = null;
        selectedFileLabel.setText("No file selected");
        deleteFileIcon.setVisible(false);
        compressButton.setDisable(true);
        decompressButton.setDisable(true);
        chooseFileButton.setDisable(false);
    }

    // Update the status text
    private void updateStatusText(String message) {
        statusTextArea.setText(message);
    }
}