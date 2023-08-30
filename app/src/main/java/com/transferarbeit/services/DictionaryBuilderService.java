package com.transferarbeit.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DictionaryBuilderService {

    private static final int MAX_DICTIONARY_SIZE = 100;

    // Builds a dictionary based on the frequency of words in the input file
    public static Map<String, String> buildDictionary(File inputFile) throws IOException {
        Map<String, Integer> wordFrequency = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line into words
                String[] words = line.split("\\W+");

                // Update the frequency of each word
                // (get current count associated with the key in the map. If the key is not present in the map, it returns 0 as the default value.)
                for (String word : words) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return Collections.emptyMap(); 
        }

        // Sorts the words by frequency (descending)
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordFrequency.entrySet());
        sortedWords.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Builds Hashmap dictionary (LInkedHashMap to preserve insertion order, a normal HashMap would not preserve the order)
        Map<String, String> dictionary = new LinkedHashMap<>();
        int keyIndex = 0;

        // Add entries to the dictionary based on frequency and size criteria
        for (int i = 0; i < sortedWords.size() && dictionary.size() < MAX_DICTIONARY_SIZE; i++) {
            String word = sortedWords.get(i).getKey();

            // Only add word to dictionary if key uses less charachters then the word itself
            if (word.length() > ("%" + (keyIndex + 1)).length()) {
                dictionary.put(word, "%" + (keyIndex + 1));
                keyIndex++;
            }
        }

        return dictionary;
    }
}
