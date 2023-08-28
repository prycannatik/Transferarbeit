package com.transferarbeit;

import java.io.File;
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

    public static String decompressText(String compressedText, Map<String, String> dictionary) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String word = entry.getKey();
            String code = entry.getValue();
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(code) + "\\b");
            Matcher matcher = pattern.matcher(compressedText);
            compressedText = matcher.replaceAll(word);
        }
        return compressedText;
    }

    public static boolean isCompressed(File file) {
        return file.getName().contains("compressed_");
    }

}
