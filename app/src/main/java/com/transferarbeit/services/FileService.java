package com.transferarbeit.services;

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
        } catch (IOException e) {
            e.printStackTrace();
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
            // Separator between dictionary and content
            writer.write("---");
            writer.newLine();
            writer.write(compressedText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDecompressedFile(File file, String decompressedText) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(decompressedText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> readDictionary(BufferedReader reader) throws IOException {
        Map<String, String> dictionary = new HashMap<>();
        String line;
        try {
            while (!(line = reader.readLine()).equals("---")) {
                String[] parts = line.split("=");
                dictionary.put(parts[1], parts[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    public static String readCompressedText(BufferedReader reader) throws IOException {
        StringBuilder compressedText = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                compressedText.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedText.toString();
    }
}
