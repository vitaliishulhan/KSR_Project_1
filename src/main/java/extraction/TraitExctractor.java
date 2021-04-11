package extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isLetter;
import static extraction.FileParser.parse;

/**
 * Implements traits extractor for articles
 */
public class TraitExctractor {
    /**
     * Alphabet representation
     */
    private static final List<Character> alphabet = new ArrayList<>();

    // alphabet initialization
    static {
        alphabet.add('a');
        alphabet.add('b');
        alphabet.add('c');
        alphabet.add('d');
        alphabet.add('e');
        alphabet.add('f');
        alphabet.add('g');
        alphabet.add('h');
        alphabet.add('i');
        alphabet.add('g');
        alphabet.add('k');
        alphabet.add('l');
        alphabet.add('m');
        alphabet.add('n');
        alphabet.add('o');
        alphabet.add('p');
        alphabet.add('q');
        alphabet.add('r');
        alphabet.add('s');
        alphabet.add('t');
        alphabet.add('u');
        alphabet.add('w');
        alphabet.add('q');
        alphabet.add('x');
        alphabet.add('y');
        alphabet.add('z');
    }

    /**
     * Returns average word length
     * @param body article text
     * @return average word length
     */
    private static double getAverageWordLength(String body) {
        int characterNumber = 0;

        String text = body.replaceAll("&([a-z]|[A-Z])+;","");

        //counts letters in the article text
        for (int i = 0; i < text.length( ); i++ ) {
            if (isLetter(body.charAt(i))) {
                characterNumber++;
            }
        }

        return 1.0 * characterNumber / body.split("\\s+").length;
    }

    /**
     * Returns amount of words with only upper case letters
     * @param body article text
     * @return amount of words
     */
    private static int getWordsWithTheFirstUpperCaseLetterAmount(String body){
        int count = 0;

        Pattern patternObject = Pattern.compile("\\b[A-Z][a-z]+\\b");
        Matcher matcher = patternObject.matcher(body);

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * Returns amount of words with only lower case letters
     * @param body article text
     * @return amount of words
     */
    private static int getUpperCaseWordsAmount(String body) {
        int count = 0;

        Pattern p = Pattern.compile("\\b[A-Z]+\\b");
        Matcher m = p.matcher(body);

        while (m.find()) {
            count++;
        }

        return count;
    }

    /**
     * Returns amount of words containing more than 10 characters
     * @param body article text
     * @return amount of words
     */
    private static int getWordsLongerThan10(String body) {
        int count = 0;

        Pattern p = Pattern.compile("\\b[a-zA-Z]+(-[a-zA-Z]+)?('[a-zA-Z]+)?\\b");
        Matcher m = p.matcher(body);

        while (m.find()) {
            String word = m.group(0);
            word = word.replaceAll("['-]", "");

            if (word.length() > 10) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns amount of words containing less than 4 characters
     * @param body article text
     * @return amount of words
     */
    private static int getWordsShorterThan4(String body) {
        int count = 0;
        Pattern p = Pattern.compile("\\b[a-zA-Z]+(-[a-zA-Z]+)?('[a-zA-Z]+)?\\b");
        Matcher m = p.matcher(body);
        while (m.find()) {
            String word = m.group(0);
            word = word.replaceAll("['-]", "");
            if (word.length() <= 4) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns words amount
     * @param body article text
     * @return amount of words
     */
    private static int getWordsAmount(String body) {
        int count = 0;

        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(body);

        while (m.find()) {
            count++;
        }

        return count;
    }

    /**
     * Returns digits amount, i.e. characters between 0 and 9
     * @param body article text
     * @return digits amount
     */
    private static int getDigitsAmount(String body) {
        int count = 0;

        for (int i = 0; i < body.length(); i++) {
            if (Character.isDigit(body.charAt(i))) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns amount of punctuation marks
     * @param body article text
     * @return amount of punctuation marks
     */
    private static int numberOfPunctuation(String body){
        int count = 0;
        Pattern p = Pattern.compile("[\\p{Punct}]");
        Matcher m = p.matcher(body);

        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * Returns 2-element table with the least and the most occurring letter correspondingly
     * @param body article text
     * @return the least and the most occurring letter correspondingly
     */
    private static char[] getTheLeastAndTheMostOccurringLetter(String body) {

        String lowerCase = body.toLowerCase();

        int[] counter = new int[alphabet.size()];

        for (int i = 0; i < lowerCase.length(); i++) {
            int counterIndex = alphabet.indexOf(lowerCase.charAt(i));
            if (counterIndex != -1) {
                counter[counterIndex]++;
            }
        }

        if (isEachZero(counter)) {
            return new char[] {(char)0b11111111, (char)0b11111111};
        }

        List<Integer[]> counterList = new ArrayList<>();

        for (int i = 0; i < counter.length; i++) {
            if (counter[i] != 0) {
                counterList.add(new Integer[] {i, counter[i]});
            }
        }

        return new char[] {
                alphabet.get(counterList.stream().min(Comparator.comparingInt(o -> o[1])).get()[0]),
                alphabet.get(counterList.stream().max(Comparator.comparingInt(o -> o[1])).get()[0])
        };
    }

    /**
     * Verifies if each element of int table is 0
     * @param arr table for verification
     * @return if each element of given table is zero
     */
    private static boolean isEachZero(int[] arr) {
        for(int i: arr) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extracts traits from articles of the given file
     * @param pathname path to the file
     * @return list of the traits vector for each article from the given file
     */
    public static List<Traits> getTraitsVectorFor(String pathname) {
        ArrayList<String[]> articlesData;

        try {
            articlesData = parse(pathname);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }

        List<Traits> res = new ArrayList<>();

        for(String[] article: articlesData) {
            String body = article[2];

            char[] minMaxLetters = getTheLeastAndTheMostOccurringLetter(body);

            res.add(new Traits(
                    Integer.parseInt(article[0]),
                    minMaxLetters[1],
                    minMaxLetters[0],
                    getAverageWordLength(body),
                    getWordsWithTheFirstUpperCaseLetterAmount(body),
                    getDigitsAmount(body),
                    numberOfPunctuation(body),
                    getWordsAmount(body),
                    getWordsShorterThan4(body),
                    getWordsLongerThan10(body),
                    getUpperCaseWordsAmount(body),
                    Place.getPlaceFromString(article[1])
            ));
        }

        return res;
    }
}
