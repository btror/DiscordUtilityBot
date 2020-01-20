
package discordutilitybot;

import java.math.BigDecimal;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * A class that allows the bot to perform basic to mid-tier math operations
 * Command: !math Example: !math 5 + 4 !math (3 * 3) / 5 + 3 !math sin(0) +
 * cos(3.14) Supported operations: basic trig: sin, cos, tan, csc, sec and cot
 * ("pi" is not a supported representation of 3.14....) basic algebra: addition,
 * subtraction, multiplication, division, modulus, factorials, square root
 * (written as sqrt()), exponents (written as ^)
 *
 *
 *
 *
 * @author Brandon Rorie
 */
public class MathOperations extends ListenerAdapter {

    private static boolean b = false;

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String messageContent = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        char[] chars = messageContent.toCharArray();
        boolean containsLetters = false;

        for (int i = 5; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                containsLetters = true;
                if (messageContent.toLowerCase().contains("cos")
                        || messageContent.toLowerCase().contains("sin")
                        || messageContent.toLowerCase().contains("tan")
                        || messageContent.toLowerCase().contains("csc")
                        || messageContent.toLowerCase().contains("sec")
                        || messageContent.toLowerCase().contains("cot")
                        || messageContent.toLowerCase().contains("sqrt")) {
                    containsLetters = false;
                }
            }

        }

        //does not support factorial
        for (int i = 5; i < chars.length; i++) { //!do -> change to !math
            if (chars[i] == '@' //remvoed chars[i] == '!'
                    || chars[i] == '#'
                    || chars[i] == '$'
                    || chars[i] == '&'
                    || chars[i] == '~'
                    || chars[i] == '`'
                    || chars[i] == '"'
                    || chars[i] == '\''
                    || chars[i] == ';'
                    || chars[i] == ':'
                    || chars[i] == '?'
                    || chars[i] == '<'
                    || chars[i] == '>') {
                containsLetters = true;
            }
        }

        //if there is not a number written before the '!' do not run the program
        for (int i = 5; i < messageContent.length(); i++) {
            if (messageContent.charAt(i) == '!') {
                if (!Character.isDigit(chars[i - 1])) {
                    containsLetters = true;
                }
            }
        }

        //does not support use of the word pi to represent 3.14.....
        if (messageContent.toLowerCase().contains("pi")) {
            containsLetters = true;
        }

        //if the message doesn't contain letters
        if (containsLetters == false && messageContent.toLowerCase().startsWith("!math")) {
            System.out.println("!math caught");
            String operation = messageContent.substring(5, messageContent.length());
            double o = eval(operation);
            String output = Double.toString(o);
            if (b != true) {
                channel.sendMessage(operation + " = " + output).queue();
            }
            b = false;
        } else if (messageContent.toLowerCase().startsWith("!math") && containsLetters == true) {
            String operation = messageContent.substring(5, messageContent.length());
            channel.sendMessage("Cannot perform math on \"" + operation + " \". Either the syntax of the question is incorrect or this math operation is not supported.").queue();
        }
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm(); // addition
                    } else if (eat('-')) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) {
                        x *= parseFactor(); // multiplication
                    } else if (eat('/')) {
                        x /= parseFactor(); // division
                    } else if (eat('^')) {
                        x = Math.pow(x, parseFactor()); //exponentiation -> Moved in to here. So the problem is fixed
                    } else if (eat('!')) {
                        x = factorial(x);
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                try {
                    if (eat('+')) {
                        return parseFactor(); // unary plus
                    }
                    if (eat('-')) {
                        return -parseFactor(); // unary minus
                    }
                    double x;
                    int startPos = this.pos;
                    if (eat('(')) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                        while ((ch >= '0' && ch <= '9') || ch == '.') {
                            nextChar();
                        }
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // functions
                        while (ch >= 'a' && ch <= 'z') {
                            nextChar();
                        }
                        String func = str.substring(startPos, this.pos); 
                        x = parseFactor();
                        if (func.equals("sqrt")) {
                            x = Math.sqrt(x);
                        } else if (func.equals("sin")) {
                            x = Math.sin(Math.toRadians(x));
                        } else if (func.equals("cos")) {
                            x = Math.cos(Math.toRadians(x));
                        } else if (func.equals("tan")) {
                            x = Math.tan(Math.toRadians(x));
                        } else if (func.equals("csc")) {
                            x = 1 / Math.sin(Math.toRadians(x));
                        } else if (func.equals("sec")) {
                            x = 1 / Math.cos(Math.toRadians(x));
                        } else if (func.equals("cot")) {
                            x = 1 / Math.tan(Math.toRadians(x));
                        } else {
                            throw new RuntimeException("Unknown function: " + func);
                        }
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }
                    return x;
                } catch (Exception e) {
                    System.out.println("exception");
                }
                b = true;
                return 0;
            }
        }.parse();
    }

    public static double factorial(double d) {

        if (d > 170) {
            return Double.POSITIVE_INFINITY;
        }
        boolean negative = false;
        if (d < 0) {
            d *= -1;
            negative = true;
        }

        if (gamma(d + 1) == -1) {

            double dFact = 1;
            for (int i = 1; i <= (int) d; i++) {
                dFact = dFact * i;
            }
            if (negative == true) {
                return dFact * -1;
            } else {
                return dFact;
            }
        } else {
            if (negative == true) {
                double temp = gamma(d + 1);
                return temp * -1;
            } else {
                return gamma(d + 1);
            }
        }

    }

    public static double gamma(double d) {
        //if d has decimal values
        String dString = Double.toString(d);
        int placesBeforeDecimal = 0;
        boolean containsDecimal = false;
        for (int i = 0; i < dString.length(); i++) {
            if (dString.charAt(i) != '.') {
                placesBeforeDecimal++;
            } else {
                break;
            }
        }
        if (d % 1 != 0) {
            containsDecimal = true;
        }
        int scale = placesBeforeDecimal + 4;
        if (containsDecimal == true) { //if there is a decimal
            double gammaFunc = Math.sqrt(2 * Math.PI / d) * Math.pow((1 / Math.E) * (d + 1 / (12 * d - 1 / (10 * d))), d);
            BigDecimal bd = new BigDecimal(gammaFunc);

            bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
            d = bd.doubleValue();

            return d;
        } else { //if there is not a decimal
            return -1;
        }

    }
}
