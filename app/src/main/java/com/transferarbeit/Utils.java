package com.transferarbeit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String compressText(String text, Map<String, String> dictionary) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String word = entry.getKey();
            String code = entry.getValue();
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
            Matcher matcher = pattern.matcher(text);
            text = matcher.replaceAll(code);
        }
        return text;
    }

    public static String decompressText(String compressedText) {
        // Find the index where the dictionary ends
        int separatorIndex = compressedText.indexOf("---\n");
        if (separatorIndex == -1) {
            // No separator found, return the original text
            return compressedText;
        }
    
        // Extract the dictionary section and the content section
        String dictionarySection = compressedText.substring(0, separatorIndex);
        String contentSection = compressedText.substring(separatorIndex + 4);
    
        // Parse the dictionary section to reconstruct the dictionary
        String[] dictionaryLines = dictionarySection.split("\n");
        Map<String, String> dictionary = new HashMap<>();
        for (String line : dictionaryLines) {
            String[] keyValue = line.split("=");
            if (keyValue.length == 2) {
                dictionary.put(keyValue[0], keyValue[1]);
            }
        }
    
        // Sort codes by length to avoid partial overlap issues
        List<String> sortedKeys = new ArrayList<>(dictionary.keySet());
        Collections.sort(sortedKeys, Comparator.comparingInt(String::length).reversed());
    
        // Replace compressed codes with corresponding words
        for (String code : sortedKeys) {
            String word = dictionary.get(code);
            contentSection = contentSection.replace(code, word);
        }
    
        return contentSection;
    }
    

    public static boolean isCompressed(File file) {
        return file.getName().contains("compressed");
    }

}
