package hw.calc;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    
    private final static int DEFAULT_BUFFER_SIZE = 100_000;
    private final int bufferSize;
    
    public Calculator() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    public Calculator(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public long calc(String sentence) {
        if (sentence.length() <= this.bufferSize) {
            return calc(sentence, -1, sentence.length());
        } else {
            return parallelCalc(sentence);
        }
    }

    private int getNextPlusSign(String sentence, int fromIndex) {
        int indexOfNextPlus = sentence.indexOf("+", fromIndex);
        if (indexOfNextPlus == -1) {
            return sentence.length();
        } else {
            return indexOfNextPlus;
        }
    }

    private boolean isSentencePartIsWholeSentence(int sentencePartEndIndex, int sentenceSize) {
        return sentencePartEndIndex == sentenceSize;
    }

    List<SentenceIndex> getListOfSentenceParts(String sentence) {
        List<SentenceIndex> indexList = new ArrayList<>();

        int startIndex = -1;
        int nextPlusSignIndex = getNextPlusSign(sentence, this.bufferSize);
        int endIndex = nextPlusSignIndex;
        indexList.add(new SentenceIndex(startIndex, endIndex));
        if (isSentencePartIsWholeSentence(endIndex, sentence.length())) {
            return indexList;
        }

        while (nextPlusSignIndex != sentence.length()) {
            startIndex = endIndex;
            nextPlusSignIndex = getNextPlusSign(sentence, startIndex + 1 + this.bufferSize);
            endIndex = nextPlusSignIndex;
            indexList.add(new SentenceIndex(startIndex, endIndex));
        }

        return indexList;
    }

    private long parallelCalc(String sentence) {
        List<SentenceIndex> indexList = getListOfSentenceParts(sentence);

        if (indexList.size() == 1) {
            return this.calc(sentence, -1, sentence.length());
        } else {
            return indexList.parallelStream().mapToLong(il -> this.calc(sentence, il.start, il.end)).sum();
        }
    }
    
    private long calc(String sentence, int startIndex, int endIndex) {
        long sum = 0;

        Token currentToken = new Token(0, MathSign.PLUS, startIndex);
        long multiplication = currentToken.getNumber();

        while (currentToken.indexOfMathSign != endIndex) {
            Token nextToken = readNextToken(sentence, currentToken.indexOfMathSign, endIndex);
            if (currentToken.getMathSign() == MathSign.PLUS) {
                sum = sum + multiplication;
                multiplication = nextToken.getNumber();
            }
            if (currentToken.getMathSign() == MathSign.ASTERISK) {
                multiplication = multiplication * nextToken.getNumber();
            }
            currentToken = nextToken;
        }

        if (multiplication != 0) {
            sum = sum + multiplication;
        }

        return sum;
    }

    Token readNextToken(String sentence, int prevIndexOfMathSign, int endIndex) {
        int indexOfNextPlus = sentence.indexOf("+", prevIndexOfMathSign + 1);
        int indexOfNextAsterisk = sentence.indexOf("*", prevIndexOfMathSign + 1);

        MathSign mathSign = MathSign.PLUS;
        int indexOfMathSign = indexOfNextPlus;

        if (indexOfNextAsterisk == -1 && indexOfNextPlus == -1) {
            indexOfMathSign = endIndex;
        } else if (indexOfNextPlus == -1) {
            mathSign = MathSign.ASTERISK;
            indexOfMathSign = indexOfNextAsterisk;
        } else if (indexOfNextAsterisk != -1 && indexOfNextAsterisk < indexOfNextPlus) {
            mathSign = MathSign.ASTERISK;
            indexOfMathSign = indexOfNextAsterisk;
        }

        String rawNumber = sentence.substring(prevIndexOfMathSign + 1, indexOfMathSign);
        long number = Long.parseLong(rawNumber.trim());

        return new Token(number, mathSign, indexOfMathSign);
    }
    
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        String sentence;
        if (args.length > 0) {
            sentence  = args[0];
            System.out.println("Result for your sentence is " + calculator.calc(sentence, -1, sentence.length()));
        }
    }

}
