package com.transferarbeit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DictionaryBuilder {

    private static final int MAX_DICTIONARY_SIZE = 100;

    public static Map<String, String> buildDictionary(File inputFile) throws IOException {
        Map<String, Integer> wordFrequency = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+");
                for (String word : words) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordFrequency.entrySet());
        sortedWords.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        Map<String, String> dictionary = new LinkedHashMap<>();
        int keyIndex = 0;

        for (int i = 0; i < sortedWords.size() && dictionary.size() < MAX_DICTIONARY_SIZE; i++) {
            String word = sortedWords.get(i).getKey();
            if (word.length() > ("%" + (keyIndex + 1)).length()) {
                dictionary.put(word, "%" + (keyIndex + 1));
                keyIndex++;
            }
        }

        return dictionary;
    }
}
