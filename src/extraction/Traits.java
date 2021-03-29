package extraction;

import java.util.ArrayList;
import java.util.List;

public class Traits {
    private final char theMostCommonLetter;
    private final char theLeastCommonLetter;
    private final double avgWordLength;
    private final double wordsWithTheFirstBigLetterAmount;
    private final double digitsAmount;
    private final double punctuationMarksAmount;
    private final double wordsAmount;
    private final double wordsMax4LettersAmount;
    private final double wordsMin11LettersAmount;
    private final double capsLockWordsAmount;
    private Place place;

    public Traits(final char theMostCommonLetter,
                  final char theLeastCommonLetter,
                  final double avgWordLength,
                  final double wordsWithTheFirstBigLetterAmount,
                  final double digitsAmount,
                  final double punctuationMarksAmount,
                  final double wordsAmount,
                  final double wordsMax4LettersAmount,
                  final double wordsMin11LettersAmount,
                  final double capsLockWordsAmount,
                  final Place place) {
        this.theMostCommonLetter = theMostCommonLetter;
        this.theLeastCommonLetter= theLeastCommonLetter;
        this.avgWordLength = avgWordLength;
        this.wordsWithTheFirstBigLetterAmount = wordsWithTheFirstBigLetterAmount;
        this.digitsAmount = digitsAmount;
        this.punctuationMarksAmount = punctuationMarksAmount;
        this.wordsAmount = wordsAmount;
        this.wordsMax4LettersAmount = wordsMax4LettersAmount;
        this.wordsMin11LettersAmount = wordsMin11LettersAmount;
        this.capsLockWordsAmount = capsLockWordsAmount;
        this.place = place;
    }

    public List<Double> getNumberTraits() {
        List<Double> res = new ArrayList<>();
        res.add(avgWordLength);
        res.add(wordsWithTheFirstBigLetterAmount);
        res.add(digitsAmount);
        res.add(punctuationMarksAmount);
        res.add(wordsAmount);
        res.add(wordsMax4LettersAmount);
        res.add(wordsMin11LettersAmount);
        res.add(capsLockWordsAmount);

        return res;
    }

    public List<Character> getTextTraits() {
        List<Character> res = new ArrayList<>();

        res.add(theMostCommonLetter);
        res.add(theLeastCommonLetter);

        return res;
    }

    public Place getPlace() {
        return place;
    }

    public int getTraitsAmount() {
        return getClass().getDeclaredFields().length - 1;
    }

    @Override
    public String toString() {
        return "Traits{" +
                "theMostCommonLetter=" + theMostCommonLetter +
                ", theLeastCommonLetter=" + theLeastCommonLetter +
                ", avgWordLength=" + avgWordLength +
                ", wordsWithTheFirstBigLetterAmount=" + wordsWithTheFirstBigLetterAmount +
                ", digitsAmount=" + digitsAmount +
                ", punctuationMarksAmount=" + punctuationMarksAmount +
                ", wordsAmount=" + wordsAmount +
                ", wordsMax4LettersAmount=" + wordsMax4LettersAmount +
                ", wordsMin11LettersAmount=" + wordsMin11LettersAmount +
                ", capsLockWordsAmount=" + capsLockWordsAmount +
                ", place=" + place +
                '}';
    }
}
