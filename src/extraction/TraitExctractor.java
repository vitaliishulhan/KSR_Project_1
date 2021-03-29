package extraction;


import java.io.IOException;
import java.util.ArrayList;

import java.lang.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static extraction.FileParser.parse;
import static java.lang.Character.isLetter;

public class TraitExctractor {
    ArrayList<String[]> articlesData;

    {
        try {
            articlesData = parse("C:/Users/Kamil/Downloads/KSR_Project_1-main/KSR_Project_1-main/articles/reut2-000.sgm");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        double averageWordLenght = characterNumber/wordsNumber.length;
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

    private static char TheMostOccuringLetter(String body) {

        String lowerCase=body.toLowerCase();
        int[] ascii_count = new int[128];  // fast path for ASCII

        for (int i = 0 ; i < lowerCase.length() ; i++)
        {
            char ch = lowerCase.charAt(i);  // This does appear to be the recommended way to iterate over a String
            // alternatively, iterate over 32bit Unicode codepoints, not UTF-16 chars, if that matters.
            if (ch<=90 && ch>=65) {
                ascii_count[ch]++;
            }
        }
        int temp=0;
        char ch;
        int Ascii = 0;
        for (int i = 0 ; i < 25 ; i++)
        {
            if(temp<=ascii_count[65+i]){
            temp=ascii_count[65+i];
            Ascii=i+65;
            }
        }
        ch= (char) Ascii;
        return ch;
    }
    private static char TheLeastOccuringLetter(String body) {

        String lowerCase=body.toLowerCase();
        int[] ascii_count = new int[128];  // fast path for ASCII

        for (int i = 0 ; i < lowerCase.length() ; i++)
        {
            char ch = lowerCase.charAt(i);  // This does appear to be the recommended way to iterate over a String
            // alternatively, iterate over 32bit Unicode codepoints, not UTF-16 chars, if that matters.
            if (ch<=90 && ch>=65) {
                ascii_count[ch]++;
            }
        }
        int temp=ascii_count[65];
        char ch;
        int Ascii = 0;
        for (int i = 0 ; i < 25 ; i++)
        {
            if(temp>=ascii_count[65+i]){
                temp=ascii_count[65+i];
                Ascii=i+65;
            }
        }
        ch= (char) Ascii;
        return ch;
    }










}
