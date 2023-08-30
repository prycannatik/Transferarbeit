package com.transferarbeit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileService {

    // Reads the content of a given file and returns it as a string
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

    // Writes a compressed file with a dictionary and compressed text
    public static void writeCompressedFile(File file, Map<String, String> dictionary, String compressedText)
            throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Write dictionary entries to the file
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                writer.write(entry.getValue() + "=" + entry.getKey());
                writer.newLine();
            }

            // Write separator between dictionary and compressed text
            writer.write("---");
            writer.newLine();

            // Write compressed text
            writer.write(compressedText);
        }
    }

    // Writes a decompressed file with the given decompressed text
    public static void writeDecompressedFile(File file, String decompressedText) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(decompressedText);
        }
    }

    // Reads a dictionary from a BufferedReader and returns it as a Map
    public static Map<String, String> readDictionary(BufferedReader reader) throws IOException {
        Map<String, String> dictionary = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).equals("---")) {
            String[] parts = line.split("=");
            dictionary.put(parts[1], parts[0]);
        }
        return dictionary;
    }

    // Reads the compressed text from a BufferedReader and returns it as a string
    public static String readCompressedText(BufferedReader reader) throws IOException {
        StringBuilder compressedText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            compressedText.append(line).append("\n");
        }
        return compressedText.toString();
    }
}
