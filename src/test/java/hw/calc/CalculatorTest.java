package hw.calc;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;

class CalculatorTest {

    private Calculator calc;

    @BeforeEach
    void setup() {
        calc = new Calculator();
    }

    @Test
    void readNextToken_return_tokenWithPlus() {
        String sentence = "1 + 2 + 3 * 4 + 1";
        Token token = calc.readNextToken(sentence, 2, sentence.length());
        Assert.assertSame(MathSign.PLUS, token.getMathSign());
        Assert.assertEquals(2, token.getNumber());
        Assert.assertEquals(6, token.getIndexOfMathSign());

        token = calc.readNextToken(sentence, 10, sentence.length());
        Assert.assertSame(MathSign.PLUS, token.getMathSign());
        Assert.assertEquals(4, token.getNumber());
        Assert.assertEquals(14, token.getIndexOfMathSign());
    }

    @Test
    void readNextToken_return_tokenWithAsterisk() {
        String sentence = "1 + 2 + 3 * 4 + 1";
        Token token = calc.readNextToken(sentence, 6, sentence.length());
        Assert.assertSame(MathSign.ASTERISK, token.getMathSign());
        Assert.assertEquals(3, token.getNumber());
        Assert.assertEquals(10, token.getIndexOfMathSign());
    }

    @Test
    void readNextToken_return_tokenWithIndex_asEndIndex_withPlusSign() {
        String sentence = "1 + 2 + 3 * 4 + 1";
        Token token = calc.readNextToken(sentence, 14, sentence.length());
        Assert.assertSame(MathSign.PLUS, token.getMathSign());
        Assert.assertEquals(1, token.getNumber());
        Assert.assertEquals(17, token.getIndexOfMathSign());
    }

    @ParameterizedTest(name = "calc for sentence '{0}' is {1}")
    @CsvSource({
            "1 + 4 + 3*5 *2 + 4 +5 *  4+1, 60",
            "3*5 *2 +1+4 + 4 +5 *  4+1, 60",
            "1 + 3*5 *2 + 4 +5 *  4+1 +4, 60",
            "1 + 3*5 *2 + 4 +1 +4 + 5 *  4, 60"
    })
    void calc_onDifferentSentence_haveSameResult(String sentence, long expectedResult) {
        Assert.assertEquals(expectedResult, calc.calc(sentence));
    }

    @Test
    void calc_hugeSentence() {
        final int howManySameSentenceParts = 30_000_000;
        //sentence with sum = 10
        final String sentenceWithPredefinedSum = "1 + 2*2 +2 + 3*1*1*1*1 ";
        final long sentenceWithPredefinedSumResult = calc.calc(sentenceWithPredefinedSum);
        final long expectedResult = howManySameSentenceParts * sentenceWithPredefinedSumResult;

        String hugeSentence = generateHugeSentence(howManySameSentenceParts, sentenceWithPredefinedSum);
        calc = new Calculator(hugeSentence.length());
        long result = calc.calc(hugeSentence);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    void calc_hugeSentence__with_parallel_streams() {
        final int howManySameSentenceParts = 30_000_000;
        //sentence with sum = 10
        final String sentenceWithPredefinedSum = "1 + 2*2 +2 + 3*1*1*1*1 ";
        final long sentenceWithPredefinedSumResult = calc.calc(sentenceWithPredefinedSum);
        final long expectedResult = howManySameSentenceParts * sentenceWithPredefinedSumResult;

        String hugeSentence = generateHugeSentence(howManySameSentenceParts, sentenceWithPredefinedSum);
        calc = new Calculator(1_000_000);
        long result = calc.calc(hugeSentence);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    void getListOfSentenceParts_containsTwoIndexes() {
        final String sentence = "1 + 2 *4 + 1 + 1*2*2 + 1";
        calc = new Calculator(10);
        List<SentenceIndex> listOSentenceParts = calc.getListOfSentenceParts(sentence);
        Assert.assertEquals(2, listOSentenceParts.size());
    }

    @Test
    void getListOfSentenceParts_containsSingleIndexes() {
        final String sentence = "1 + 2 *4 + 1 + 2*2 + 1*4*3* 4  * 1 * 2";
        calc = new Calculator(20);
        List<SentenceIndex> listOSentenceParts = calc.getListOfSentenceParts(sentence);
        Assert.assertEquals(1, listOSentenceParts.size());
    }

    private String generateHugeSentence(int howManySameSentenceParts, String sentenceWithPredefinedSum) {
        final StringBuilder sb = new StringBuilder(1000);
        IntStream.range(0, howManySameSentenceParts)
                .forEach(i -> sb.append(sentenceWithPredefinedSum).append("+"));
        sb.append("0");
        return sb.toString();
    }
}
