package com.forgeessentials.scripting;

import com.forgeessentials.core.ForgeEssentials;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser
{

    static
    {
        try
        {
            for (Field field : Functions.class.getDeclaredFields())
                if (Function.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
                    Functions.add(field.getName().toLowerCase(), (Function) field.get(null));
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Double toNumber(String num)
    {
        try
        {
            return Double.parseDouble(num);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public static class Token
    {
        private Token()
        {
        }

        public static Token parseToken(String s)
        {
            Token t = Operator.parseToken(s);
            if (t == null)
                t = NumericToken.parseToken(s);

            return t;
        }

    }

    public static class NumericToken extends Token
    {
        public double number;

        public NumericToken(double number)
        {
            this.number = number;
        }

        public double getNumber()
        {
            return number;
        }

        public static NumericToken parseToken(String s)
        {
            Double num = toNumber(s);
            if (num != null)
            {
                return new NumericToken(num);
            }
            return null;
        }
    }

    public static class Operator extends Token
    {
        private int precedence;
        private boolean leftAssociative;

        public int numArgs()
        {
            return 2;
        }

        public final String name;

        public String getDocumentation()
        {
            return "function '" + name + "' takes " + numArgs() + " arguments.";
        }

        public double execute(double... inputs)
        {
            double n0 = inputs[0];
            double n1 = numArgs() >= 2 ? inputs[1] : 0;
            if (this == Operator.Add)
                n0 += n1;
            else if (this == Operator.Sub)
                n0 -= n1;
            else if (this == Operator.Mul)
                n0 *= n1;
            else if (this == Operator.Div)
                n0 /= n1;
            else if (this == Operator.Mod)
                n0 %= n1;
            else if (this == Operator.Pow)
                n0 = Math.pow(n0, n1);
            else if (this == Operator.Neg)
                n0 = -n0;
            else
                return 0;
            return n0;
        }

        private Operator(int precedence, boolean leftAssociative, String name)
        {
            this.precedence = precedence;
            this.leftAssociative = leftAssociative;
            this.name = name;
        }

        public static Operator Add = new Operator(1, true, "Add");
        public static Operator Sub = new Operator(1, true, "Sub");
        public static Operator Neg = new Operator(1, true, "Neg") {
            @Override
            public int numArgs()
            {
                return 1;
            }
        };
        public static Operator Mul = new Operator(2, true, "Mul");
        public static Operator Div = new Operator(2, true, "Div");
        public static Operator Mod = new Operator(2, true, "Mod");
        public static Operator Pow = new Operator(3, false, "Pow");
        public static Operator LeftParen = new Operator(0, false, "LeftParen");
        public static Operator RightParen = new Operator(0, false, "RightParen");
        public static Operator Comma = new Operator(0, false, "Comma");

        public int getPrecedence()
        {
            return precedence;
        }

        public boolean isLeftAssociative()
        {
            return leftAssociative;
        }

        public static Operator parseToken(String op)
        {
            if (op.equals("+"))
                return Add;
            else if (op.equals("-"))
                return Sub;
            else if (op.equals("*"))
                return Mul;
            else if (op.equals("/"))
                return Div;
            else if (op.equals("%"))
                return Mod;
            else if (op.equals("^"))
                return Pow;
            if (op.equals("("))
                return LeftParen;
            else if (op.equals(")"))
                return RightParen;
            else if (op.equals(","))
                return Comma;
            else
                return Function.parseToken(op);
        }
    }

    public static class Function extends Operator
    {
        public final String extendedDocumentation;

        @Override
        public String getDocumentation()
        {
            return super.getDocumentation() + "\nThis function: " + extendedDocumentation;
        }

        private Function(String name, String docs)
        {
            super(0, false, name);
            extendedDocumentation = docs;
        }

        public static Function parseToken(String s)
        {
            return Functions.get(s);
        }

    }

    public static class Functions
    {
        public static void add(String name, Function argument)
        {
            if (functions.containsKey(name))
                throw new RuntimeException(String.format("Script function name @%s already registered", name));
            functions.put(name, argument);
        }

        public static Function get(String name)
        {
            return functions.get(name);
        }

        public static Map<String, Function> getAll()
        {
            return ImmutableMap.copyOf(functions);
        }

        private static HashMap<String, Function> functions = new HashMap<>();

        public static Function floor = new Function("floor", "Returns the largest integer less than or equal to it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.floor(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function ceil = new Function("ceil", "Returns the smallest integer greater than or equal to it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.ceil(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };

        public static Function round = new Function("round", "Returns it's input rounded to the nearest integer") {

            @Override
            public double execute(double... input)
            {
                return Math.round(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function sin = new Function("sin", "Returns the sine of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.sin(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function cos = new Function("cos", "Returns the cosine of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.cos(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function tan = new Function("tan", "Returns the tangent of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.tan(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function asin = new Function("asin", "Returns the arc sine of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.asin(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function acos = new Function("acos", "Returns the arc cosine of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.acos(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function atan = new Function("atan", "Returns the arc tangent of it's input") {

            @Override
            public double execute(double... input)
            {
                return Math.atan(input[0]);
            }

            @Override
            public int numArgs()
            {
                return 1;
            }

        };
        public static Function max = new Function("max", "Returns the max of 2 inputs") {

            @Override
            public double execute(double... input)
            {
                return Math.max(input[0], input[1]);
            }

            @Override
            public int numArgs()
            {
                return 2;
            }

        };

        public static Function min = new Function("min", "Returns the min of 2 inputs") {

            @Override
            public double execute(double... input)
            {
                return Math.max(input[0], input[1]);
            }

            @Override
            public int numArgs()
            {
                return 2;
            }

        };

        public static Function randi = new Function("randi", "Returns a random integer bounded by it's 2 inputs") {

            @Override
            public double execute(double... input)
            {
                int min = (int) input[0];
                int max = (int) input[1];
                int tmp0 = min;
                int tmp1 = max;
                max = Math.max(tmp0, tmp1);
                min = Math.min(tmp0, tmp1);
                int range = max - min;
                return ForgeEssentials.rnd.nextInt(range) + min;
            }

            @Override
            public int numArgs()
            {
                return 2;
            }

        };

        public static Function randf = new Function("randf", "Returns a random floating point value between 0.0 and 1.0") {

            @Override
            public double execute(double... input)
            {
                return ForgeEssentials.rnd.nextDouble();
            }

            @Override
            public int numArgs()
            {
                return 0;
            }

        };
    }

    private static final Pattern OPERATION_PATTERN = Pattern.compile("([+\\-*/%^(),]|\\d+\\.?\\d*|\\w+)([+\\-*/%^\\w\\.(),]*)");

    public static Double computeExpression(String expr)
    {
        // expr = parseFunctions(expr);
        Matcher m = OPERATION_PATTERN.matcher(expr.replaceAll("\\s*", ""));
        boolean matchedOnce = false;
        Stack<Operator> opStack = new Stack<Operator>();

        LinkedList<Token> outputQueue = new LinkedList<Token>();
        Boolean lastTokenWasNumber = null;

        Stack<Integer> functionArgs = new Stack<>();
        Stack<Function> lastFunction = new Stack<>();
        while (m.matches())
        {
            matchedOnce = true;
            Token t = Token.parseToken(m.group(1));

            // Double d = null;
            if (t instanceof NumericToken)
            {
                if (functionArgs.isEmpty() || functionArgs.peek() == null || (functionArgs.peek() < lastFunction.peek().numArgs()))
                {
                    outputQueue.add(t);
                }

                lastTokenWasNumber = true;
            }
            else if (t instanceof Operator)
            {
                if (lastTokenWasNumber == null || !lastTokenWasNumber)
                {
                    if (t == Operator.Sub)
                    {
                        opStack.push(Operator.Neg);
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    Operator op1 = (Operator) t;
                    if (op1 != Operator.LeftParen && op1 != Operator.RightParen && !opStack.isEmpty() && !(op1 instanceof Function))
                    {
                        Operator op2 = opStack.peek();
                        if (op2 != Operator.LeftParen && op1.getPrecedence() <= op2.getPrecedence())
                        {
                            outputQueue.add(opStack.pop());
                        }
                    }
                    if (op1 == Operator.LeftParen)
                    {
                        if (opStack.peek() instanceof Function)
                        {
                            functionArgs.push(0);
                            lastFunction.push((Function) opStack.peek());
                        }
                        else
                            functionArgs.push(null);
                    }
                    if (op1 == Operator.RightParen || op1 == Operator.Comma)
                    {
                        Operator op = opStack.pop();

                        while (op != Operator.LeftParen)
                        {
                            if (functionArgs.peek() == null || (functionArgs.peek() < lastFunction.peek().numArgs()))
                                outputQueue.add(op);
                            if (!opStack.isEmpty())
                                op = opStack.pop();
                            else
                                return null;
                        }
                        Integer fa;
                        if ((fa = functionArgs.pop()) != null)
                            if (op1 == Operator.Comma)
                            {
                                functionArgs.push(fa + 1);

                                opStack.push(Operator.LeftParen);
                            }
                            else
                            {
                                if (lastTokenWasNumber)
                                    fa++;
                                if (fa < lastFunction.peek().numArgs())
                                    return null;
                                outputQueue.add(opStack.pop());
                                lastFunction.pop();

                            }
                    }
                    else
                        opStack.push(op1);

                }
                if (t != Operator.RightParen)
                    lastTokenWasNumber = false;
                else
                    lastTokenWasNumber = true;
            }
            else
                return null;

            m.reset(m.group(2));
        }
        while (!opStack.isEmpty())
        {
            outputQueue.add(opStack.pop());
        }
        Stack<NumericToken> numStack = new Stack<NumericToken>();
        while (!outputQueue.isEmpty())
        {
            Object output = outputQueue.remove();
            if (output instanceof NumericToken)
            {
                numStack.push((NumericToken) output);
            }
            else if (output instanceof Operator)
            {
                Operator o = (Operator) output;
                if (numStack.size() >= o.numArgs())
                {
                    double[] args = new double[o.numArgs()];
                    for (int i = o.numArgs() - 1; i >= 0; i--)
                    {
                        args[i] = numStack.pop().getNumber();
                    }

                    numStack.push(new NumericToken(o.execute(args)));
                }
                else
                    return null;

            }
        }

        if (!matchedOnce)
            return null;
        else
            return numStack.pop().number;

    }
}
