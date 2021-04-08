package gui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    public final Container content = getContentPane();

    public final JSpinner kValueSpinner = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
    public final JSpinner trainSetRelationSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 99, 1));
    public final JSpinner testSetRelationSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 99, 1));

    public final JCheckBox theMostCommonLetterCheckBox = new JCheckBox("Najcięściej występująca litera");
    public final JCheckBox theLeastCommonLetterCheckBox = new JCheckBox("Najrzadziej występująca litera");
    public final JCheckBox avgWordLengthCheckBox = new JCheckBox("Średnia długość słowa");
    public final JCheckBox wordsWithTheFirstBigLetterAmountCheckBox = new JCheckBox("Liczba słów się zaczynających z dużej litery");
    public final JCheckBox digitsAmountCheckBox = new JCheckBox("Liczba cyfr");
    public final JCheckBox punctuationMarksAmountCheckBox = new JCheckBox("Liczba znaków interpunkcyjnych");
    public final JCheckBox wordsAmountCheckBox = new JCheckBox("Liczba słów");
    public final JCheckBox wordsMax4LettersAmountCheckBox = new JCheckBox("Liczba słów zawierających max 4 litery");
    public final JCheckBox wordsMin11LettersAmountCheckBox = new JCheckBox("Liczba słów zawierających min 11 liter");
    public final JCheckBox capsLockWordsAmountCheckBox = new JCheckBox("Liczba słów napisanych wyłącznie dużymi literami");

    public final JComboBox metricComboBox = new JComboBox(new String[] {
            "Metryka euklidesowa",
            "Metryka uliczna",
            "Metryka Czebyszewa"
    });

    public final JTextArea outputTextArea = new JTextArea();

    public final JButton submitButton = new JButton("Leczimy!");
    public final JButton loadButton = new JButton("Pobrać artykuły");

    public GUI(String windowTitle) {
        super(windowTitle);
        Insets insets = this.getInsets();
        setSize(900,540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel knnParamsLabel = new JLabel("Parametry klasyfikatora");
        knnParamsLabel.setSize(200, 20);
        knnParamsLabel.setLocation(10,10);
        getContentPane().add(knnParamsLabel);

        JLabel kValueLabel = new JLabel("Wartość k");
        kValueLabel.setSize(100, 20);
        kValueLabel.setLocation(10, 40);
        content.add(kValueLabel);

        kValueSpinner.setSize(100, 30);
        kValueSpinner.setLocation(10, 60);
        content.add(kValueSpinner);

        JLabel setRelationLabel = new JLabel("Proporcja podziału train/test");
        setRelationLabel.setSize(220, 20);
        setRelationLabel.setLocation(10, 90);
        content.add(setRelationLabel);

        trainSetRelationSpinner.setSize(50, 30);
        trainSetRelationSpinner.setLocation(10, 110);
        trainSetRelationSpinner.addChangeListener(e -> testSetRelationSpinner.setValue(100 - (int) trainSetRelationSpinner.getValue()));
        content.add(trainSetRelationSpinner);

        JLabel slashLabel = new JLabel("/");
        slashLabel.setSize(20, 20);
        slashLabel.setLocation(70, 115);
        slashLabel.setFont(new Font(slashLabel.getFont().getFontName(), Font.PLAIN, 24));
        content.add(slashLabel);

        testSetRelationSpinner.setSize(50, 30);
        testSetRelationSpinner.setLocation(90, 110);
        testSetRelationSpinner.addChangeListener(e -> trainSetRelationSpinner.setValue(100 - (int) testSetRelationSpinner.getValue()));
        content.add(testSetRelationSpinner);

        JLabel traitsLabel = new JLabel("Cechy");
        traitsLabel.setSize(100, 20);
        traitsLabel.setLocation(10, 145);
        content.add(traitsLabel);

        theMostCommonLetterCheckBox.setSize(280,20);
        theMostCommonLetterCheckBox.setLocation(10, 160);
        content.add(theMostCommonLetterCheckBox);

        theLeastCommonLetterCheckBox.setSize(280, 20);
        theLeastCommonLetterCheckBox.setLocation(10, 180);
        content.add(theLeastCommonLetterCheckBox);

        avgWordLengthCheckBox.setSize(380, 20);
        avgWordLengthCheckBox.setLocation(10, 200);
        content.add(avgWordLengthCheckBox);
        wordsWithTheFirstBigLetterAmountCheckBox.setSize(380, 20);
        wordsWithTheFirstBigLetterAmountCheckBox.setLocation(10, 220);
        content.add(wordsWithTheFirstBigLetterAmountCheckBox);
        digitsAmountCheckBox.setSize(380, 20);
        digitsAmountCheckBox.setLocation(10, 240);
        content.add(digitsAmountCheckBox);
        punctuationMarksAmountCheckBox.setSize(380, 20);
        punctuationMarksAmountCheckBox.setLocation(10, 260);
        content.add(punctuationMarksAmountCheckBox);
        wordsAmountCheckBox.setSize(380, 20);
        wordsAmountCheckBox.setLocation(10, 280);
        content.add(wordsAmountCheckBox);
        wordsMax4LettersAmountCheckBox.setSize(380, 20);
        wordsMax4LettersAmountCheckBox.setLocation(10, 300);
        content.add(wordsMax4LettersAmountCheckBox);
        wordsMin11LettersAmountCheckBox.setSize(380, 20);
        wordsMin11LettersAmountCheckBox.setLocation(10, 320);
        content.add(wordsMin11LettersAmountCheckBox);
        capsLockWordsAmountCheckBox.setSize(380, 20);
        capsLockWordsAmountCheckBox.setLocation(10, 340);
        content.add(capsLockWordsAmountCheckBox);

        theMostCommonLetterCheckBox.setSelected(true);
        theLeastCommonLetterCheckBox.setSelected(true);
        avgWordLengthCheckBox.setSelected(true);
        wordsWithTheFirstBigLetterAmountCheckBox.setSelected(true);
        digitsAmountCheckBox.setSelected(true);
        punctuationMarksAmountCheckBox.setSelected(true);
        wordsAmountCheckBox.setSelected(true);
        wordsMax4LettersAmountCheckBox.setSelected(true);
        wordsMin11LettersAmountCheckBox.setSelected(true);
        capsLockWordsAmountCheckBox.setSelected(true);

        JLabel metricLabel = new JLabel("Metryka");
        metricLabel.setSize(180, 20);
        metricLabel.setLocation(10, 370);
        content.add(metricLabel);

        metricComboBox.setSize(200, 30);
        metricComboBox.setLocation(10, 400);
        content.add(metricComboBox);

        submitButton.setSize(100, 50);
        submitButton.setLocation(10, 450);
        content.add(submitButton);

        loadButton.setSize(150, 50);
        loadButton.setLocation(120, 450);
        content.add(loadButton);

        JButton clearButton = new JButton("Wycyścz output");
        clearButton.setSize(150, 50);
        clearButton.setLocation(280, 450);
        clearButton.addActionListener(e -> clear());
        content.add(clearButton);

        JLabel outputLabel = new JLabel("Output");
        outputLabel.setSize(120, 20);
        outputLabel.setLocation(440, 10);
        content.add(outputLabel);

        JScrollPane jsp = new JScrollPane(outputTextArea);
        jsp.setSize(450, 470);
        jsp.setLocation(440, 30);
        outputTextArea.setEditable(false);
        content.add(jsp);

        setResizable(false);
    }

    public void clear() {
        outputTextArea.setText("");
    }

    public void println(Object x) {
        outputTextArea.setText(outputTextArea.getText() + x.toString() + '\n');
    }

    public void println() {
        outputTextArea.setText(outputTextArea.getText() + '\n');
    }

    public boolean[] getTraitsFilter() {
        return new boolean[] {
            avgWordLengthCheckBox.isSelected(),
            wordsWithTheFirstBigLetterAmountCheckBox.isSelected(),
            digitsAmountCheckBox.isSelected(),
            punctuationMarksAmountCheckBox.isSelected(),
            wordsAmountCheckBox.isSelected(),
            wordsMax4LettersAmountCheckBox.isSelected(),
            wordsMin11LettersAmountCheckBox.isSelected(),
            capsLockWordsAmountCheckBox.isSelected(),
            theMostCommonLetterCheckBox.isSelected(),
            theLeastCommonLetterCheckBox.isSelected()
        };
    }

    public static void main(String[] args) {

    }
}
