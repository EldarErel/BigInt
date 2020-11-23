import java.util.Scanner;

public class Run {
    public static void main(String[] args) {
        System.out.println("Please enter the first number");
        BigInt num1 = inputBigInt();
        System.out.println("Please enter the second number");
        BigInt num2 = inputBigInt();
        System.out.println("-----------------------------");
        System.out.println("The first number is: " + num1);
        System.out.println("The second number is: " + num2);
        System.out.println("-----------------------------");
        System.out.println("Lets do some math");
        System.out.println("Checking + results:" + num1.plus(num2));
        System.out.println(num1 + " - " + num2 + " = " + num1.minus(num2));
        System.out.println(num1 + " * " + num2 + " = " + num1.multiply(num2));
        try {
            System.out.println(num1 + " / " + num2 + " = " + num1.divide(num2));
        } catch (ArithmeticException e) {
            System.out.println(" OUCH! You tried to divide by zero");
        }
        System.out.println("The number: " + num1 + " And the number: " + num2 + " Are equal?: " + num1.equals(num2));
        System.out.println("The number: " + num1 + " Compare to the number: " + num2 + " " + num1.compareTo(num2));
    }

    private static BigInt inputBigInt() { // to input a big int number
        Scanner scan = new Scanner(System.in);
        BigInt number = null;
        do {
            String num1 = scan.nextLine();
            try {
                number = new BigInt(num1);
            } catch (Exception e) { // exception if number is not an integer
                System.out.println("Number must be an integer");
                System.out.println("Please enter a number");
            }
        } while (number == null);
        return number;
    }

}
