/*
 * Team Members
 * Harshitha Akkaraju
 * Shaarika Kaul
 */

package calculator.ast;
import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import calculator.gui.ImageDrawer;
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
    		// to avoid the extraneous toDouble() string
    		if (node.isOperation() && node.getName().equals("toDouble")) {
    			return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
		}
        return new AstNode(toDoubleHelper(env.getVariables(), node));
    }

    /*
     * Takes a node of type either operation, variable, or number, and a dictionary of variables
     * Evaluates the given AST node tree and returns a double
     * throws EvaluationError exception if there is an undefined variable
     */
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
    		// to avoid the extraneous simplify() string
    		if (node.isOperation() && node.getName().equals("simplify")) {
    			return simplifyHelper(env.getVariables(), node.getChildren().get(0));
    		}
    		return simplifyHelper(env.getVariables(), node);
    }
    
    /*
     * Takes an AstNode of type either variable, number, or operation and simplifies it
     * Returns a node that's been simplified to the lowest level, leaving placeholder variables
     */
    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) {
    		if (node.isNumber()) {
    			return node;
    		} else if (node.isVariable()) {
    			if (variables.containsKey(node.getName())) {
    				return variables.get(node.getName());
    			}
    			return node; // already in it's simplest form
    		} else {
    			String name = node.getName();
    			IList<AstNode> nodes = node.getChildren();
    			if (name.equals("+") || name.equals("-") || name.equals("*") || name.equals("/")) {
    				// to simplify farther down the tree
    				AstNode child1 = simplifyHelper(variables, nodes.get(0));
    				AstNode child2 = simplifyHelper(variables, nodes.get(1));
    				// performs the 'operation' if the nodes are of type numeric
    				if (!name.equals("/") && child1.isNumber() && child2.isNumber()) {
    					return new AstNode(toDoubleHelper(variables, node));
    				} else {
    					nodes.set(0, child1);
    					nodes.set(1, child2);
    					// returns a new node with updated child nodes
    					return new AstNode(name, nodes);
    				}
    			} else {
    				AstNode child = simplifyHelper(variables, nodes.get(0));
    				nodes.set(0, child);
    				// returns a new node with updated child nodes
    				return new AstNode(name, nodes);
    			}
    		}
	}

    /*
     * Takes a node with plot parameters and env object as parameters
     * Uses the plot parameters to generate the x-values and y-values for the plot
     * Plots the function and returns the expression.
     * 
     * throws EvaluationError if:
     * - expressions contain undefined variables
     * - if the lower bound of the plot is greater than the upper bound
     * - if var is already defined
     * - if step (the value to increment by) is <= 0
     */
    public static AstNode plot(Environment env, AstNode node) {
    		// get the plot parameters
    		IList<AstNode> params = node.getChildren();
    		// simplify varMin, varMax and step values
    		for (int i = 2; i < params.size(); i++) {
    			AstNode input = params.get(i);
			input = new AstNode(toDoubleHelper(env.getVariables(), input));
			params.set(i, input); // update
    		}
    		AstNode exp = params.get(0); // expression
    		AstNode var = params.get(1); // variable
    		AstNode min = params.get(2); // min bound for x
    		AstNode max = params.get(3); // max bound for x
    		AstNode step = params.get(4); // interval size
    		if (env.getVariables().containsKey(var.getName())
			||(min.getNumericValue() > max.getNumericValue()) 
			|| step.getNumericValue() <= 0 
			|| env.getVariables().containsKey(var.getName())) {
    			throw new EvaluationError("");
    		}
    		// number of values to generate given the min and the max
    		double numIterations = (max.getNumericValue() - min.getNumericValue()) / step.getNumericValue();
    		IList<Double> xValues = new DoubleLinkedList<Double>();
    		IList<Double> yValues = new DoubleLinkedList<Double>();
    		for (int i = 0; i <= (int) numIterations; i++) {
    			double x = min.getNumericValue() + (i * step.getNumericValue());
    			xValues.add(x);
    			// populate the variables dictionary
    			env.getVariables().put(var.getName(), new AstNode(x));
    			double y = toDoubleHelper(env.getVariables(), exp);
    			yValues.add(y);	
    		}
    		env.getVariables().remove(var.getName()); // reset the variable dictionary
    		// draw scatter plot
    		env.getImageDrawer().drawScatterPlot(exp.getName(), var.getName(), exp.getName(), xValues, yValues);
    		return exp;
    }
}
