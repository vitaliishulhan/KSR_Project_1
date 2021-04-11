package extraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for saving article's traits
 */
public class Traits {
    private final int id;
    /**
     * The most common letter which occurs in article
     */
    private final char theMostCommonLetter;
    /**
     * The least common letter which occurs in article
     */
    private final char theLeastCommonLetter;
    /**
     * Average length of all words in article
     */
    private final double avgWordLength;
    /**
     * Amount of words with the first big letter
     */
    private final double wordsWithTheFirstBigLetterAmount;
    /**
     * Amount of digits, i.e. characters from 0 to 9, in article
     */
    private final double digitsAmount;
    /**
     * Amount of punctuation marks
     */
    private final double punctuationMarksAmount;
    /**
     * Amount of words
     */
    private final double wordsAmount;
    /**
     * Amount of words containing not more than 4 letter
     */
    private final double wordsMax4LettersAmount;
    /**
     * Amount of words containing more than 10 letters
     */
    private final double wordsMin11LettersAmount;
    /**
     * Amount of words containing only upper case letters
     */
    private final double upperCaseWordsAmount;
    /**
     * Country representation, which article belongs to
     */
    private final Place place;

    /**
     * Single constructor. Take all traits for saving
     * @param theMostCommonLetter The most common letter which occurs in article
     * @param theLeastCommonLetter The least common letter which occurs in article
     * @param avgWordLength Average length of all words in article
     * @param wordsWithTheFirstBigLetterAmount Amount of words with the first big letter
     * @param digitsAmount Amount of digits, i.e. characters from 0 to 9, in article
     * @param punctuationMarksAmount Amount of punctuation marks
     * @param wordsAmount Amount of words
     * @param wordsMax4LettersAmount Amount of words containing not more than 4 letter
     * @param wordsMin11LettersAmount Amount of words containing more than 10 letters
     * @param upperCaseWordsAmount Amount of words containing only upper case letters
     * @param place Country representation, which article belongs to
     */
    public Traits(
            final int id,
            final char theMostCommonLetter,
            final char theLeastCommonLetter,
            final double avgWordLength,
            final double wordsWithTheFirstBigLetterAmount,
            final double digitsAmount,
            final double punctuationMarksAmount,
            final double wordsAmount,
            final double wordsMax4LettersAmount,
            final double wordsMin11LettersAmount,
            final double upperCaseWordsAmount,
            final Place place) {
        this.id = id;
        this.theMostCommonLetter = theMostCommonLetter;
        this.theLeastCommonLetter= theLeastCommonLetter;
        this.avgWordLength = avgWordLength;
        this.wordsWithTheFirstBigLetterAmount = wordsWithTheFirstBigLetterAmount;
        this.digitsAmount = digitsAmount;
        this.punctuationMarksAmount = punctuationMarksAmount;
        this.wordsAmount = wordsAmount;
        this.wordsMax4LettersAmount = wordsMax4LettersAmount;
        this.wordsMin11LettersAmount = wordsMin11LettersAmount;
        this.upperCaseWordsAmount = upperCaseWordsAmount;
        this.place = place;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns amount of only numerical traits of article
     * @return amount of only numerical traits of article
     */
    public List<Double> getNumberTraits() {
        List<Double> res = new ArrayList<>();
        res.add(avgWordLength);
        res.add(wordsWithTheFirstBigLetterAmount);
        res.add(digitsAmount);
        res.add(punctuationMarksAmount);
        res.add(wordsAmount);
        res.add(wordsMax4LettersAmount);
        res.add(wordsMin11LettersAmount);
        res.add(upperCaseWordsAmount);

        return res;
    }

    /**
     * Returns amount of only text traits of article
     * @return amount of only text traits of article
     */
    public List<Character> getTextTraits() {
        List<Character> res = new ArrayList<>();

        res.add(theMostCommonLetter);
        res.add(theLeastCommonLetter);

        return res;
    }

    /**
     * Returns country, which article belongs to
     * @return country, which article belongs to
     */
    public Place getPlace() {
        return place;
    }

    /**
     * Returns amount of article traits despite of place
     * @return amount of article traits
     */
    public static int getTraitsAmount() {
        return Traits.class.getClass().getDeclaredFields().length - 2;
    }

    @Override
    public String toString() {
        return "Traits{" +
                "id=" + id +
                "theMostCommonLetter=" + theMostCommonLetter +
                ", theLeastCommonLetter=" + theLeastCommonLetter +
                ", avgWordLength=" + avgWordLength +
                ", wordsWithTheFirstBigLetterAmount=" + wordsWithTheFirstBigLetterAmount +
                ", digitsAmount=" + digitsAmount +
                ", punctuationMarksAmount=" + punctuationMarksAmount +
                ", wordsAmount=" + wordsAmount +
                ", wordsMax4LettersAmount=" + wordsMax4LettersAmount +
                ", wordsMin11LettersAmount=" + wordsMin11LettersAmount +
                ", capsLockWordsAmount=" + upperCaseWordsAmount +
                ", place=" + place +
                '}';
    }
}
