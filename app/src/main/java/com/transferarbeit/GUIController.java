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
    private ProgressBar compressionRateBar;

    private File selectedFile;
    private Map<String, String> dictionary;

    @FXML
    public void handleChooseFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wählen Sie eine Datei aus");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            updateSelectedFile(selectedFile.getName());
            updateStatusText("Datei ausgewählt: " + selectedFile.getName());

            boolean isCompressed = Utils.isCompressed(selectedFile);
            compressButton.setDisable(isCompressed);
            decompressButton.setDisable(!isCompressed);
        } else {
            updateStatusText("Keine Datei ausgewählt.");
        }
    }

    @FXML
    public void handleCompressAction(ActionEvent event) {
        if (selectedFile == null) {
            updateStatusText("Bitte zuerst eine Datei auswählen.");
            return;
        }
        compressButton.setDisable(true);
        decompressButton.setDisable(true);
        deleteFileIcon.setVisible(false);
        chooseFileButton.setDisable(true);

        Task<Void> compressionTask = createCompressionTask();
        compressionRateBar.progressProperty().bind(compressionTask.progressProperty());

        Thread compressionThread = new Thread(compressionTask);
        compressionThread.setDaemon(true);
        compressionThread.start();
    }

    @FXML
    public void handleDecompressAction(ActionEvent event) {
        if (selectedFile == null) {
            updateStatusText("Bitte zuerst eine Datei auswählen.");
            return;
        }
        decompressButton.setDisable(true);
        compressButton.setDisable(true);
        deleteFileIcon.setVisible(false);
        chooseFileButton.setDisable(true);

        Task<Void> decompressionTask = createDecompressionTask();
        compressionRateBar.progressProperty().bind(decompressionTask.progressProperty());

        Thread decompressionThread = new Thread(decompressionTask);
        decompressionThread.setDaemon(true);
        decompressionThread.start();
    }

    @FXML
    public void handleDeleteFileAction() {
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
                    dictionary = DictionaryBuilder.buildDictionary(selectedFile);
                    String compressedText = Utils.compressText(text, dictionary);
                    File outputFile = new File(selectedFile.getParent(), "compressed_" + selectedFile.getName());
                    FileService.writeCompressedFile(outputFile, dictionary, compressedText);

                    updateStatusText("Datei erfolgreich komprimiert: " + outputFile.getAbsolutePath());

                    updateProgress(0, 1);
                    Platform.runLater(() -> resetSelectedFile());
                    Desktop.getDesktop().open(outputFile.getParentFile());

                } catch (Exception e) {
                    Platform.runLater(() -> updateStatusText("Fehler beim Komprimieren: " + e.getMessage()));
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

                    String decompressedText = Utils.decompressText(compressedText);
                    String fileName = selectedFile.getName();
                    String fileNameWithoutCompressed = fileName.substring(11);
                    File outputFile = new File(selectedFile.getParent(), "decompressed_" + fileNameWithoutCompressed);
                    FileService.writeDecompressedFile(outputFile, decompressedText);

                    updateProgress(0, 1);

                    Platform.runLater(() -> {
                        updateStatusText("Datei erfolgreich dekomprimiert: " + outputFile.getAbsolutePath());
                        resetSelectedFile();
                    });

                    Desktop.getDesktop().open(outputFile.getParentFile());

                } catch (Exception e) {
                    Platform.runLater(() -> updateStatusText("Fehler beim Dekomprimieren: " + e.getMessage()));
                }
                return null;
            }
        };
    }

    private void updateSelectedFile(String filename) {
        selectedFileLabel.setText(filename);
        deleteFileIcon.setVisible(true);
    }

    private void resetSelectedFile() {
        selectedFile = null;
        selectedFileLabel.setText("Keine Datei ausgewählt");
        deleteFileIcon.setVisible(false);
        compressButton.setDisable(true);
        decompressButton.setDisable(true);
        chooseFileButton.setDisable(false);
    }

    private void updateStatusText(String message) {
        statusTextArea.setText(message);
    }
}
