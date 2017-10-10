package calculator.ast;
import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import misc.exceptions.NotYetImplementedException;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Takes the given AstNode node and attempts to convert it into a double.
     *
     * Returns a number AstNode containing the computed double.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode toDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the TODOs in the 'toDoubleHelper' method.
    		if (node.isOperation() && node.getName().equals("toDouble")) {
    			return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
		}
        return new AstNode(toDoubleHelper(env.getVariables(), node));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
        		return node.getNumericValue();
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                // If the expression contains an undefined variable, we give up.
                throw new EvaluationError("Undefined variable: " + node.getName());
            }
            AstNode var = variables.get(node.getName());
            return toDoubleHelper(variables, var);
        } else {
            String name = node.getName();
            IList<AstNode> nodes = node.getChildren();
            if (name.equals("+")) {
            		return toDoubleHelper(variables, nodes.get(0)) + toDoubleHelper(variables, nodes.get(1));
            } else if (name.equals("-")) {
            		return toDoubleHelper(variables, nodes.get(0)) - toDoubleHelper(variables, nodes.get(1));
            } else if (name.equals("*")) {
            		return toDoubleHelper(variables, nodes.get(0)) * toDoubleHelper(variables, nodes.get(1));
            } else if (name.equals("/")) {
            		return toDoubleHelper(variables, nodes.get(0)) / toDoubleHelper(variables, nodes.get(1));
            } else if (name.equals("^")) {
            		double base = toDoubleHelper(variables, nodes.get(0));
            		double exp = (int) toDoubleHelper(variables, nodes.get(1));
            		return Math.pow(base, exp);
            } else if (name.equals("negate")) {
            		return (-1) * (toDoubleHelper(variables, nodes.get(0)));
            } else if (name.equals("sin")) {
                double result = toDoubleHelper(variables, nodes.get(0));
                return Math.sin(result);
            } else if (name.equals("cos")) {
	            	double result = toDoubleHelper(variables, nodes.get(0));
            		return Math.cos(result);
            } else {
                throw new EvaluationError("Unknown operation: " + name);
            }
        }
    }

    public static AstNode simplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "toDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "toDouble" method in some way

        // TODO: Your code here
    		if (node.isOperation() && node.getName().equals("simplify")) {
    			return simplifyHelper(env.getVariables(), node.getChildren().get(0));
    		}
    		return simplifyHelper(env.getVariables(), node);
    }
    
    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) {
    		if (node.isNumber()) {
    			return node;
    		} else if (node.isVariable()) {
    			if (variables.containsKey(node.getName())) {
    				return variables.get(node.getName());
    			}
    			return node;
    		} else {
    			String name = node.getName();
    			IList<AstNode> nodes = node.getChildren();
    			if (name.equals("+") || name.equals("-") || name.equals("*") || name.equals("/")) {
    				AstNode child1 = simplifyHelper(variables, nodes.get(0));
    				AstNode child2 = simplifyHelper(variables, nodes.get(1));
    				if (!name.equals("/") && child1.isNumber() && child2.isNumber()) {
    					return new AstNode(toDoubleHelper(variables, node));
    				} else {
    					nodes.set(0, child1);
    					nodes.set(1, child2);
    					return new AstNode(name, nodes);
    				}
    			} else {
    				AstNode child = simplifyHelper(variables, nodes.get(0));
    				nodes.set(0, child);
    				return new AstNode(name, nodes);
    			}
    		}
	}

    /**
     * Expected signature of plot:
     *
     * >>> plot(exprToPlot, var, varMin, varMax, step)
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This command will plot the equation "3 * x", varying "x" from 2 to 5 in 0.5
     * increments. In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
    		AstNode simplified = simplifyHelper(env.getVariables(), node);
    		IList<AstNode> plotInput = simplified.getChildren();
    		AstNode exp = plotInput.get(0);
    		AstNode var = plotInput.get(1);
    		AstNode varMin = plotInput.get(2);
    		AstNode varMax = plotInput.get(3);
    		AstNode step = plotInput.get(4);
    		
    		if (varMin.getNumericValue() > varMax.getNumericValue()) {
    			throw new EvaluationError("Invalid range");
    		} else if (!varMin.isNumber() || !varMax.isNumber() || !step.isNumber()) {
    			throw new EvaluationError("Undefined variable");
    		} else if (exp.isNumber() || var.isNumber()) {
    			throw new EvaluationError("Variable already defined");
    		} else if (step.getNumericValue() < 1) {
    			throw new EvaluationError("Invalid step value");
    		}
    		
    		IList<Double> xValues = new DoubleLinkedList<Double>();
    		
    		double range = (varMax.getNumericValue() - varMin.getNumericValue()) / step.getNumericValue();
    		for (int i = 0; i < (int) range; i++) {
    			xValues.add(i + step.getNumericValue());
    		}
    		
    		IList<Double> yValues = new DoubleLinkedList<Double>();
    		for (Double x: xValues) {
    			
    		}
       /*
        * vars called: exprToPlot, var, varMin, varMax, step need to be saved! check if they already exist
        * name of list: "plot"
        * children: x^2 + x, x, -10 * a, 10 * a, 0.1
        * - First child is expression to be plotted
        * - Second child is variable being used in expression; make sure everything is if var != chosenVariable
        * 		if they do, 
        * - third child is the min of range
        * - fourth child is the max of the range
        * - fifth child is the step
        * 
        * 1) retrieve variable values EXCEPT the chosen variable
        * 		(if one doesn't exist throw new EvaluationError)
        * 
        * 2) save the range (throw EvaluationError if varMin > varMax)
        * 	x value is just whatever goes up to 
        * 	compute the y value as the list of y values that goes from min to max;
        * 	
        * 
        * 3) save the step (throw EvaluationError if step is >1)
        * 
        * String expression = "";
        * String staticVariable = "";
        * String min=;
        * String max=;
        * String step=;
        * 
        */
    	
    	// throw new NotYetImplementedException();

        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}
