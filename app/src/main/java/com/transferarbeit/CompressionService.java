package com.transferarbeit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CompressionService {

    public static void compress(File inputFile, File outputFile) throws IOException {
        // 1. Erstellen Sie das Wörterbuch
        Map<String, String> dictionary = DictionaryBuilder.buildDictionary(inputFile);

        // 2. Lesen Sie den Text aus der Eingabedatei
        String text = FileService.readFile(inputFile);

        // 3. Komprimieren Sie den Text mit dem Wörterbuch
        String compressedText = Utils.compressText(text, dictionary);

        // 4. Schreiben Sie das Wörterbuch und den komprimierten Text in die Ausgabedatei
        FileService.writeCompressedFile(outputFile, dictionary, compressedText);
    }

    public static void decompress(File inputFile, File outputFile) throws IOException {
        // 1. Lesen Sie das Wörterbuch und den komprimierten Text aus der Eingabedatei
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            Map<String, String> dictionary = FileService.readDictionary(reader);
            String compressedText = FileService.readCompressedText(reader);

            // 2. Dekomprimieren Sie den Text mit dem Wörterbuch
            String decompressedText = Utils.decompressText(compressedText, dictionary);

            // 3. Schreiben Sie den dekomprimierten Text in die Ausgabedatei
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
                writer.write(decompressedText);
            }
        }
    }
}
