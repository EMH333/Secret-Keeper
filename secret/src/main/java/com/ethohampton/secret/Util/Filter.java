package com.ethohampton.secret.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ethohampton on 6/5/17.
 * <p>
 * Filters out words as needed
 */

public class Filter {
    private static final String URL_REGEX = "(http:\\/\\/\\s*|https:\\/\\/\\s*|ftp:\\/\\/\\s*)?(www\\s*)?([a-zA-Z0-9.-]{2,256})(\\s*[.]\\s*)(ru|pl|kz|by|ua|com|in|pt|br|co.uk)(?![a-zA-Z])([?|#]{1}[=&#a-zA-Z0-9]{2,128})?";

    private static HashMap<String, String[]> words = new HashMap<>();
    private static ArrayList<String> bannedHashes = new ArrayList<>();
    private static int largestWordLength = 0;

    private static boolean loadedConfigs = false;

    public static boolean hasConfigs() {
        return loadedConfigs;
    }

    public static void loadConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Constants.BAD_WORDS_FILENAME))));
            String line = "";
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                counter++;
                String[] content;
                try {
                    content = line.split(",");
                    if (content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignore_in_combination_with_words = new String[]{};
                    if (content.length > 1) {
                        ignore_in_combination_with_words = content[1].split("_");
                    }

                    if (word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Loaded " + counter + " words to filter out");

            BufferedReader hashesReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Constants.BAD_HASHES_FILENAME))));
            String hash = "";
            while ((hash = hashesReader.readLine()) != null) {
                bannedHashes.add(hash);
            }
            System.out.println("Loaded " + bannedHashes.size() + " hashes to filter out");

            loadedConfigs = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     *
     * @param input string that should be filtered
     * @return array list of words found
     */
    public static ArrayList<String> badWordsFound(String input) {
        if (input == null) {
            return new ArrayList<>();
        }

        // remove leetspeak
        input = input.replaceAll("1", "i");
        input = input.replaceAll("!", "i");
        input = input.replaceAll("3", "e");
        input = input.replaceAll("4", "a");
        input = input.replaceAll("@", "a");
        input = input.replaceAll("5", "s");
        input = input.replaceAll("7", "t");
        input = input.replaceAll("0", "o");
        input = input.replaceAll("9", "g");

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z<>]", "");

        // iterate over each letter in the word
        for (int start = 0; start < input.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
                String wordToCheck = input.substring(start, start + offset);
                if (words.containsKey(wordToCheck)) {
                    // for example, if you want to say the word bass, that should be possible.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (String anIgnoreCheck : ignoreCheck) {
                        if (input.contains(anIgnoreCheck)) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }

        return badWords;

    }


    public static boolean passesAllFilters(String input) {

        if (input.length() > Constants.MAX_SECRET_LENGTH || input.length() < Constants.MIN_SECRET_LENGTH)//insures acceptable length
        {
            return false;
        }
        if (input.matches("[0-9]+")) {
            return false;//if only numbers
        }

        ArrayList<String> badWords = badWordsFound(input);//check for badwords
        if (badWords.size() > 0) {
            return false;
        }

        //check to see if that secret is banned
        if (bannedHashes.contains(Utils.hash(input))) {
            return false;
        }

        return !input.matches(URL_REGEX);//if everything else passes, then make sure we don't have any urls included

    }
}
