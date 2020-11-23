import java.util.ArrayList;

/**
 * Class represents a very large number
 * Including mathematics functions (add,subtract,multiply, divide and so)
 * @author - Eldar Erel
 */
public class BigInt implements Comparable<BigInt> {
    private enum Sign {POSITIVE, NEGATIVE} // to mark a number
    private final char PLUS = '+';
    private final char MINUS = '-';
    protected Sign sign; // to store the sign of the number
    protected ArrayList<Integer> number; // to list represent the number

    public BigInt(String number) { //initializing a number into a bigInt
        this.number = new ArrayList<>();
        if (number.isEmpty()) // if string is empty
            throw new IllegalArgumentException("Number must be an integer");
        if (number.charAt(0) == '-') { // getting the sign
            this.sign = Sign.NEGATIVE;
            number = number.substring(1); //subtracting the sign from the number
        } else {
            if (number.charAt(0) == '+') // same here
                number = number.substring(1);
            this.sign = Sign.POSITIVE;
        }
        for (int i = 0; i < number.length(); i++) { //parsing the string into integers and adding to the list
            try {
                int temp = Integer.parseInt(number.substring(i, i + 1)); // getting the digits
                this.number.add(temp);
            } catch (NumberFormatException e) { // throwing if one of the digits is not an integer
                throw new IllegalArgumentException("Number must be an integer");
            }
        } // end of for loop
        while (this.number.get(0) == 0) {
            if (this.number.size() == 1) {
                this.sign = Sign.POSITIVE;
                break;
            }
            this.number.remove(0);
        }

    }

    private BigInt() { // default constructor
        number = new ArrayList<>();
        sign = Sign.POSITIVE;
    }

    private void changeSign(BigInt number) {
        number.sign = number.sign == Sign.NEGATIVE ? Sign.POSITIVE : Sign.NEGATIVE;
    }

    private BigInt(BigInt other) { // copy constructor
        this.number = new ArrayList<>(other.number);
        this.sign = other.sign;
    }

    private boolean isBigger(BigInt other) { //  returns if this number is bigger than the other (without the sign)
        int len1 = this.number.size(); //getting the sizes
        int len2 = other.number.size();
        int p1 = 0, p2 = 0; // temps;
        if (len1 > len2) // first is bigger
            return true;
        else if (len2 > len1) //second is bigger
            return false;
        for (int i = 0; i < len1; i++) {
            p1 = this.number.get(i);
            p2 = other.number.get(i);
            if (p1 != p2)
                break;
        }
        return (p1 > p2);
    }

    public BigInt plus(BigInt other) {
        BigInt temp;
        if (other.isBigger(this)) {// checking which number is bigger (absolut) first number must be bigger for the calculation so we swap if needed
            return other.plus(this);
        }
        if (this.sign != other.sign) {
            temp = this.calc(other, MINUS); // one negative and one positive so we use we use the subtracting method
            if (this.sign == Sign.NEGATIVE && temp.number.get(0) != 0) // first number is bigger, so we can get either 0 nor a negative number
                temp.sign = Sign.NEGATIVE; // updating the sign as the results not 0
        } else { // both numbers has the same sign
            temp = this.calc(other, PLUS); // first adding them
            if (this.sign == Sign.NEGATIVE) { // if both numbers are negative its exactly as them both positive, ONLY THE RESULT WILL BE NEGATIVE
                temp.sign = Sign.NEGATIVE; //  so we update the sign
            }
        }
        return temp;
    }

    public BigInt minus(BigInt other) { // minus is like plus if we changing the subtract number's sign
        BigInt temp;
        changeSign(other); //changing sign
        temp = this.plus(other); // adding
        changeSign(other); // changing sign back
        return temp;
    }

    private BigInt calc(BigInt other, char m) { // adding 2 positive numbers with '+' or subtracting numbers with '-'
        int len1 = this.number.size(); // first number length
        int len2 = other.number.size(); // second number length
        int maxLen = Math.max(len1, len2); //max length
        int carry = 0;
        BigInt res = new BigInt(); //to store the results
        for (int i = 0; i < maxLen; i++) {
            int temp;
            int p = i < len1 ? this.number.get(len1 - 1 - i) : 0; //  while we not exceeding the size, we store the number, 0 otherwise
            int q = i < len2 ? other.number.get(len2 - 1 - i) : 0; // same goes here
            if (m == PLUS) { // for 2 positive numbers its a simple mathematics form
                temp = p + q + carry;
                carry = temp / 10;
                res.number.add(temp % 10);
            } else { // for subtracting we use a simple subtracting formula
                p -= carry; // subtracting the carry
                if (p - q < 0) { // if we need to "borrow" we update the carry to subtract in the next iteration
                    temp = p + 10 - q;
                    carry = 1;
                } else { // subtract normally
                    temp = p - q;
                    carry = 0;
                }
                res.number.add(temp); //adding
            }
        }
        // for positive numbers the carry should be added to the beginning
        if (carry > 0) {
            if (m == PLUS)
                res.number.add(carry);
            else // for different signed numbers if we have carry meaning the result is negative
                res.sign = Sign.NEGATIVE;
        }
        replaceOrder(res);
        return res;
    }

    private static void replaceOrder(BigInt res) { // changing the order of a BigInt number, from end to start
        int start = 0;
        int end = res.number.size() - 1;
        while (start < end) {
            int temp = res.number.get(end);
            res.number.set(end--, res.number.get(start));
            res.number.set(start++, temp);
        }//removing zeros from the beginning of the number
        while (res.number.size() > 1 && res.number.get(0) == 0) {
            res.number.remove(0);
        }
    }

    @Override
    public String toString() { // printing the number
        int len = number.size();
        StringBuilder num = new StringBuilder();
        num.append(sign == Sign.NEGATIVE ? "-" : "");
        for (int i = 0; i < len; i++) {
            num.append(number.get(i));
            if ((len - 1 - i) % 3 == 0 && (i + 1) < len)
                num.append(",");
        }
        return num.toString();
    }

    public BigInt multiply(BigInt other) {
        int mul;
        int len1 = this.number.size(); // first number length
        int len2 = other.number.size(); // second number length
        BigInt res1 = new BigInt();
        BigInt res2;
        if (this.number.get(0) == 0 || other.number.get(0) == 0)
            return new BigInt("0");
        for (int i = len2 - 1; i >= 0; i--) { // multiply
            int carry = 0;
            res2 = new BigInt();
            if (i != len2 - 1) // first time we dont need to add zeros
                initZero(res2, len2 - 1 - i); // but after every digit in the second number that we multiply with all of the first number digits
            for (int j = len1 - 1; j >= 0; j--) {     // we need to start from the next power of 10, so we adding couple of zeros to represent it
                int sum;
                mul = this.number.get(j) * other.number.get(i) + carry; // multiply the first digit with the second number digit and adding the carry
                sum = mul % 10;  // results can have more than one digit
                carry = mul / 10; // getting the remain
                res2.number.add(sum); //adding the sum
            }
            if (carry > 0) { // if carry is remaining from last multiplication we adding it
                res2.number.add(carry);
            }
            replaceOrder(res2); // we must change the order of the number is it adding it backwards
            res2 = res2.plus(res1); // adding results
            res1.number = new ArrayList<>(res2.number); // copy results
        }
        res1.sign = this.sign == other.sign ? Sign.POSITIVE : Sign.NEGATIVE; // multiply 2 number with the same sign will always be positive and negative otherwise
        return res1;
    }

    private static void initZero(BigInt num, int numOfZero) { // initializing a number and adding numbers of zeros to the number, tool for multiply (10^numOfZero likewise)
        num.number.clear();
        while (numOfZero-- > 0) {
            num.number.add(0);
        }
    }

    public BigInt divide(BigInt other) {
        Sign flag = this.sign == other.sign ? Sign.POSITIVE : Sign.NEGATIVE; // dividing 2 number with the same sign will always be positive, negative otherwise
        BigInt dividend = new BigInt(this); // copy of the dividend
        BigInt divisor = new BigInt(other); // copy of the divisor
        BigInt quotient; // the result
        if (other.number.get(0) == 0) // cant divide by 0
            throw new ArithmeticException("Cant divide by 0");
        if (this.number.get(0) == 0) //0 divide to anything is 0
            return new BigInt("0");
        dividend.sign = Sign.POSITIVE; // changing sign to positive for the calculation
        divisor.sign = Sign.POSITIVE; //  as it is just a copy it wont affect the original numbers
        quotient = dividend.div(divisor); // dividing
        quotient.sign = flag; // updating the sign
        return quotient; // return the result
    }

    private BigInt div(BigInt divisor) { // a recursive long division method for division very large numbers
        final BigInt one = new BigInt("1"); // represent the digit "1"
        BigInt remain; // to store the remains
        BigInt dividend = new BigInt(this); // the divided
        BigInt sum = new BigInt(divisor); // for the quotient calculation
        BigInt quotient = new BigInt(one);
        BigInt carry = new BigInt();
        BigInt NumOfDivisions = new BigInt(one); // the number of times we dividing by 10
        int len1 = this.number.size() - 1; // first number length
        int len2 = divisor.number.size() - 1; // first number length
        if (divisor.isBigger(this))
            return new BigInt("0");
        while (len1 > len2) { // while first number is bigger we dividing it by 10
            NumOfDivisions.number.add(0); // multiply by 10
            carry.number.add(this.number.get(len1)); // adding carry
            dividend.number.remove(len1); // divide by 10
            if (divisor.isBigger(dividend)) { //if divisor is bigger we need to return one step backwards, so we adding back what we have removed and stopping the loop
                NumOfDivisions.number.remove(1);
                carry.number.remove(carry.number.size() - 1);
                dividend.number.add(this.number.get(len1));
                break;
            }
            len1--; // updating the length
        }
        do { // calculating the quotient
            sum = sum.plus(divisor); // adding the divisor
            quotient = quotient.plus(one); // and updating the counter + 1
        } while (dividend.isBigger(sum)); // while sum is still smaller than the dividend
        if (!dividend.equals(sum)) {// we need the integer part of the division so if we passed the divider we need to subtract one
            quotient = quotient.minus(one);
            remain = new BigInt(dividend.minus(sum.minus(divisor))); // getting the remains
        } else {
            remain = new BigInt("0"); // if no remain
        }
        replaceOrder(carry); //  changing the order of the array as it is representing the number backwards
        if (carry.number.isEmpty()) // if carry is 0 we have done.
            return quotient;
        dividend = remain.multiply((NumOfDivisions)).plus(carry); // setting the new dividend
        quotient = quotient.multiply(NumOfDivisions).plus(dividend.div(divisor)); // adding this result + the leftovers(calling the method again)
        return quotient; //result is the quotient
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // if its the same object so its true by default
        if (!(o instanceof BigInt)) // if its not a BigInt so it cant be true
            return false;
        if (this.sign != ((BigInt) o).sign || this.number.size() != ((BigInt) o).number.size()) // both should have the same sign and the same amount of digits
            return false;
        for (int i = 0; i < this.number.size(); i++) { // going over the numbers
            int num1 = this.number.get(i);
            int num2 = ((BigInt) o).number.get(i);
            if (num1 != num2) // if the digits are not equal => the numbers are not equal
                return false;
        }
        return true; // the numbers are equal
    }

    @Override
    public int compareTo(BigInt o) {
        if (this.equals(o)) // if equals returns 0
            return 0;
        else if (this.isBigger(o)) { // first is bigger
            if (this.sign == Sign.POSITIVE) // if its sign is positive so is bigger
                return 1;
            return -1; // if not its smaller
        } else { // if second number is bigger
            if (o.sign == Sign.POSITIVE) // same here, the sign is matter so if positive it is bigger
                return -1;
        }
        return 1; // second number is smaller
    }
}
