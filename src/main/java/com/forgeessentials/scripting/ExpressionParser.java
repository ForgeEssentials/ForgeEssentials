package com.forgeessentials.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.scripting.ExpressionParser.Operator.Type;

public class ExpressionParser {
	
	public static double processFunction(String name, Double... input)
	{
		if (input.length > 0 && input[0] != null)
		{
			if (name.equals("round"))
			{
				return (double) Math.round(input[0]);
			}
			else if (name.equals("floor"))
			{
				return Math.floor(input[0]);
			}
			else if (name.equals("ceil"))
			{
				return Math.ceil(input[0]);
			}
			else if (name.equals("abs"))
			{
				return Math.abs(input[0]);
			}
			else if (name.equals("sin"))
			{
				return Math.sin(input[0]);
			}
			else if (name.equals("cos"))
			{
				return Math.cos(input[0]);
			}
			else if (name.equals("tan"))
			{
				return Math.tan(input[0]);
			}
			else if (name.equals("asin"))
			{
				return Math.asin(input[0]);
			}
			else if (name.equals("acos"))
			{
				return Math.acos(input[0]);
			}
			else if (name.equals("atan"))
			{
				return Math.atan(input[0]);
			}
		}
		
		if (name.equals("randi"))
		{
			int min = (int) (input.length > 0 && input[0] != null ? input[0] : 0);
			int max = (int) (input.length > 1 && input[1] != null ? input[1] : 100);
			int tmp0 = min;
			int tmp1 = max;
			max = Math.max(tmp0, tmp1);
			min = Math.min(tmp0, tmp1);
			int range = max - min;
			return ForgeEssentials.rnd.nextInt(range) + min;
		}
		else if (name.equals("randf"))
		{
			return ForgeEssentials.rnd.nextDouble();
		}
		
		return 0;
	}
	public static Double toNumber(String num)
    {
    	try
    	{
    		return Double.parseDouble(num);
    	} catch (NumberFormatException e)
    	{
    		return null;
    	}
    }
	
	private static final Pattern FUNCTION_PATTERN = Pattern.compile("(\\w+)\\((([^,)]*,?)*)\\)");
	private static final Pattern FUNCTION_ARG_PATTERN = Pattern.compile("([^,)]*),?");
	public static String parseFunctions(String input)
	{
		Matcher m = FUNCTION_PATTERN.matcher(input);
		while (m.find())
		{
			Matcher m2 = FUNCTION_ARG_PATTERN.matcher(m.group(2));
			ArrayList<Double> nums = new ArrayList<Double>();
			while (m2.find())
				nums.add(computeExpression(m2.group(1)));
				
			
			input = m.replaceFirst(Double.toString(processFunction(m.group(1),nums.toArray(new Double[0]))));
			m.reset(input);
		}
		return input;
	}
	public static class Operator {
		enum Type {add,sub,mul,div,mod,exp};
		static HashMap<Type,Integer> precedence = new HashMap<>();
		
		static
		{
			precedence.put(Type.add, 0);
			precedence.put(Type.sub, 0);
			precedence.put(Type.mul, 1);
			precedence.put(Type.div, 1);
			precedence.put(Type.mod, 1);
		}
		
		public int getPrecedence()
		{
			return precedence.get(type);
		}
		Type type;
		public static Operator parseOp(String op)
		{
			Operator o = new Operator();
			if (op.equals("+"))
				o.type = Type.add;
			else if (op.equals("-"))
				o.type = Type.sub;
			else if (op.equals("*"))
				o.type = Type.mul;
			else if (op.equals("/"))
				o.type = Type.div;
			else if (op.equals("%"))
				o.type = Type.mod;
			else
				return null;
			return o;
		}
	}
	private static final Pattern OPERATION_PATTERN = Pattern.compile("([+\\-*/%]|\\d+\\.?\\d*)([+\\-*/%\\d\\.]*)");
    public static Double computeExpression(String expr)
    {
    	expr = parseFunctions(expr);
    	Matcher m = OPERATION_PATTERN.matcher(expr.replaceAll("\\s*", ""));
    	boolean matchedOnce = false;
    	Stack<Operator> opStack = new Stack<Operator>();
    	@SuppressWarnings("rawtypes")
		LinkedList outputQueue = new LinkedList();
    	Boolean lastTokenWasNumber = null;
    	while (m.matches())
    	{
    		matchedOnce = true;
    		Double d = null;
    		if ((d = toNumber(m.group(1))) != null)    			
    		{
    			outputQueue.add(d);
    			lastTokenWasNumber = true;
    		}
    		else
    		{
    			if (lastTokenWasNumber != null && !lastTokenWasNumber)
    			{
    				Operator op = opStack.peek();
    				if (m.group(1).equals("-"))
					{
    					if (op.type == Type.add)
        				{
    						opStack.pop();
        					opStack.push(Operator.parseOp("-"));
        				}
    					else if  (op.type == Type.sub)
    					{
    						opStack.pop();
        					opStack.push(Operator.parseOp("+"));
    					}
    					else
    						return null;
    						
					}
    				else if (m.group(1).equals("+"))
    				{
    					if (!op.equals("+") && !op.equals("-"))        				
    						return null;
    				}
    				else
	    				return null;
    				
    			}
    			else
    			{
    				Operator op1 = Operator.parseOp(m.group(1));
    				if (!opStack.isEmpty())
    				{
	    				Operator op2 = opStack.peek();
	    				if (op1.getPrecedence() <= op2.getPrecedence())
	    				{
	    					outputQueue.add(opStack.pop());
	    				}
    				}
    				opStack.push(op1);
    				
    			}
    			lastTokenWasNumber = false;
    		}
    		
    		
			m.reset(m.group(2));
    	}
    	while (!opStack.isEmpty())
    	{
    		outputQueue.add(opStack.pop());
    	}
    	Stack<Double> numStack = new Stack<Double>();
    	while (!outputQueue.isEmpty())
    	{
    		Object output = outputQueue.remove();
    		if (output instanceof Double)
    		{
    			numStack.push((Double) output);
    		}
    		else if (output instanceof Operator)
    		{
    			if (numStack.size() >= 2)
    			{
    				Double n1 = numStack.pop();
    				Double n0 = numStack.pop();
    				switch (((Operator)output).type)
    				{
    				case add:
    					n0 += n1;
    					break;
    				case sub:
    					n0 -= n1;
    					break;
    				case mul:
    					n0 *= n1;
    					break;
    				case div:
    					n0 /= n1;
    					break;
    				case mod:
    					n0 %= n1;
    					break;
    				default:
    					return null;    					
    				}
    				numStack.push(n0);
    			}
    			else
    				return null;
    			
    		}
    	}
			/*switch (op.type)
			{
			case add:
				n0 += n1;
				break;
			case sub:
				n0 -= n1;
				break;
			case mul:
				n0 *= n1;
				break;
			case div:
				n0 /= n1;
				break;
			case mod:
				n0 %= n1;
				break;
			default:
				return null;
				
			}*/	
		
    	if (!matchedOnce)    		
    		return null;
    	else
    		return numStack.pop();
    	
    }
}
