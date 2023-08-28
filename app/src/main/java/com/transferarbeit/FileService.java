package com.transferarbeit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileService {

    public static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static void writeCompressedFile(File file, Map<String, String> dictionary, String compressedText)
            throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                writer.write(entry.getValue() + "=" + entry.getKey());
                writer.newLine();
            }
            // Trennzeichen zwischen Wörterbuch und komprimiertem Text
            writer.write("---");
            writer.newLine();
            writer.write(compressedText);
        }
    }

    public static void writeDecompressedFile(File file, String decompressedText) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(decompressedText);
        }
    }

    public static Map<String, String> readDictionary(BufferedReader reader) throws IOException {
        Map<String, String> dictionary = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).equals("---")) {
            String[] parts = line.split("=");
            dictionary.put(parts[1], parts[0]);
        }
        return dictionary;
    }

    public static String readCompressedText(BufferedReader reader) throws IOException {
        StringBuilder compressedText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            compressedText.append(line).append("\n");
        }
        return compressedText.toString();
    }
}
