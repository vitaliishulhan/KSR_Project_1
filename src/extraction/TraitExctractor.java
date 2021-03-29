package extraction;


import java.io.IOException;
import java.util.*;

import java.lang.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static extraction.FileParser.parse;
import static java.lang.Character.isLetter;

public class TraitExctractor {
    private static final List<Character> alfabet = new ArrayList<Character>();

    static {
        alfabet.add('a');
        alfabet.add('b');
        alfabet.add('c');
        alfabet.add('d');
        alfabet.add('e');
        alfabet.add('f');
        alfabet.add('g');
        alfabet.add('h');
        alfabet.add('i');
        alfabet.add('g');
        alfabet.add('k');
        alfabet.add('l');
        alfabet.add('m');
        alfabet.add('n');
        alfabet.add('o');
        alfabet.add('p');
        alfabet.add('q');
        alfabet.add('r');
        alfabet.add('s');
        alfabet.add('t');
        alfabet.add('u');
        alfabet.add('w');
        alfabet.add('q');
        alfabet.add('x');
        alfabet.add('y');
        alfabet.add('z');
    }

    public static double calculateAverageWordLengthInArticle(String body) {
        String[] wordsNumber = body.split("\\s+");//counting number of words
        int characterNumber=0;
        for( int i = 0; i < body.length( ); i++ )
        { boolean isChar=isLetter(body.charAt(i));
            if (isChar) {
                characterNumber++;
            }
        }

        double averageWordLenght = 1.0*characterNumber/wordsNumber.length;
        return averageWordLenght;
    }


    private  static int wordsStartingWithUpperCase(String body){
        int count = 0;
        Pattern patternObject = Pattern.compile("[A-Z][a-z]");
        Matcher matcher = patternObject.matcher(body);
        while(matcher.find()){
            count++;
        }
        return  count;
    }
    private static int wordsWithOnlyUpperCase(String body) {
        int count = 0;
        Pattern p = Pattern.compile("\\b[A-Z]{4,}\\b");
        Matcher m = p.matcher(body);
        while (m.find()) {
            count++;
        }
        return count;
    }
    private static int wordsLongerThan10(String body) {
        int count = 0;
        Pattern p = Pattern.compile("^\\w{10,}$");
        Matcher m = p.matcher(body);
        while (m.find()) {
            count++;
        }
        return count;
    }
    private static int wordsShorterThan4(String body) {
        int count = 0;
        Pattern p = Pattern.compile("\\b\\w{1,4}\\b");
        Matcher m = p.matcher(body);
        while (m.find()) {
            count++;
        }
        return count;
    }
    private static int numberOfWords(String body) {
        int count = 0;
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(body);
        while (m.find()) {
            count++;
        }
        return count;
    }
    private static int numberOfDigits(String body) {
        int count = 0;
        for (int i = 0; i < body.length(); i++) {
            if (Character.isDigit(body.charAt(i))) count++;
        }
        return count;
    }
    private static int numberOfPunctuation(String body){
        int count = 0;
        Pattern p = Pattern.compile("[\\p{Punct}]");
        Matcher m = p.matcher(body);

        while (m.find()) {
            count++;
        }
        return count;
    }

    private static char[] getTheLeastAndTheMostOccuringLetter(String body) {

        String lowerCase = body.toLowerCase();

        int[] counter = new int[alfabet.size()];

        for (int i = 0; i < lowerCase.length(); i++) {
            int counterIndex = alfabet.indexOf(lowerCase.charAt(i));
            if (counterIndex != -1) {
                counter[counterIndex]++;
            }
        }

        List<Integer[]> counterList = new ArrayList<>();

        for (int i = 0; i < counter.length; i++) {
            if (counter[i] != 0) {
                counterList.add(new Integer[] {i, counter[i]});
            }
        }

        return new char[] {
                alfabet.get(counterList.stream().min(Comparator.comparingInt(o -> o[1])).get()[0]),
                alfabet.get(counterList.stream().max(Comparator.comparingInt(o -> o[1])).get()[0])
        };
    }

    public static List<Traits> getTraitsVectorFor(String pathname) {
        ArrayList<String[]> articlesData;

        try {
            articlesData = parse(pathname);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Traits> res = new ArrayList<>();

        for(String[] article: articlesData) {
            String body = article[2];

            char[] minMaxLetters = getTheLeastAndTheMostOccuringLetter(body);

            res.add(new Traits(
                    minMaxLetters[1],
                    minMaxLetters[0],
                    calculateAverageWordLengthInArticle(body),
                    wordsStartingWithUpperCase(body),
                    numberOfDigits(body),
                    numberOfPunctuation(body),
                    numberOfWords(body),
                    wordsShorterThan4(body),
                    wordsLongerThan10(body),
                    wordsWithOnlyUpperCase(body),
                    Place.getPlaceFromString(article[1])
            ));

        }

        return res;
    }

}
