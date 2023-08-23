package com.transferarbeit;

import com.transferarbeit.CompressionService;
import com.transferarbeit.DictionaryBuilder;
import com.transferarbeit.FileService;
import com.transferarbeit.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

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

    private File selectedFile;
    private Map<String, String> dictionary;

    @FXML
    public void handleChooseFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wählen Sie eine Datei aus");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
            statusTextArea.setText("Datei ausgewählt: " + selectedFile.getName());
        } else {
            statusTextArea.setText("Keine Datei ausgewählt.");
        }
    }

    @FXML
    public void handleCompressAction(ActionEvent event) {
        if (selectedFile == null) {
            statusTextArea.setText("Bitte zuerst eine Datei auswählen.");
            return;
        }

        try {
            String text = FileService.readFile(selectedFile);
            dictionary = DictionaryBuilder.buildDictionary(selectedFile);
            String compressedText = Utils.compressText(text, dictionary);
            File outputFile = new File(selectedFile.getParent(), "compressed_" + selectedFile.getName());
            FileService.writeCompressedFile(outputFile, dictionary, compressedText);
            statusTextArea.setText("Datei erfolgreich komprimiert: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            statusTextArea.setText("Fehler beim Komprimieren: " + e.getMessage());
        }
    }

    @FXML
    public void handleDecompressAction(ActionEvent event) {
        if (selectedFile == null) {
            statusTextArea.setText("Bitte zuerst eine Datei auswählen.");
            return;
        }

        try {
            String compressedText = FileService.readFile(selectedFile);
            String decompressedText = Utils.decompressText(compressedText, dictionary);
            File outputFile = new File(selectedFile.getParent(), "decompressed_" + selectedFile.getName());
            FileService.writeCompressedFile(outputFile, dictionary, decompressedText); // Hier könnten Sie eine separate Methode zum Schreiben von reinem Text verwenden
            statusTextArea.setText("Datei erfolgreich dekomprimiert: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            statusTextArea.setText("Fehler beim Dekomprimieren: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteFileAction(ActionEvent event) {
        if (selectedFile != null) {
            selectedFile = null;
            selectedFileLabel.setText("Keine Datei ausgewählt");
            statusTextArea.setText("Dateiauswahl zurückgesetzt.");
        } else {
            statusTextArea.setText("Keine Datei zum Zurücksetzen ausgewählt.");
        }
    }
}
