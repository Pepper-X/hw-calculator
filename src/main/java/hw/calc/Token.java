package hw.calc;

public class Token {
    final private long number;
    final private MathSign mathSign;
    final int indexOfMathSign;

    public Token(long number, MathSign mathSign, int indexOfMathSign) {
        this.number = number;
        this.mathSign = mathSign;
        this.indexOfMathSign = indexOfMathSign;
    }

    public long getNumber() {
        return number;
    }

    public MathSign getMathSign() {
        return mathSign;
    }

    public int getIndexOfMathSign() {
        return indexOfMathSign;
    }
}
