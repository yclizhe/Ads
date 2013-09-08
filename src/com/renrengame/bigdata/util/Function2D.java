/**
 * Function2D.java 
 */
package com.renrengame.bigdata.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * This class provides an implementation for two-dimensional tabular functions,
 * namely, matrices.
 * 
 * @author Tao Chen
 * 
 */
public class Function2D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8837903290069055788L;

	protected Variable _x, _y;

	/**
	 * the two-dimensional array representation of the cells of this function.
	 * Note that it is a double array, not int array
	 */
	protected double[][] _cells;

	/**
	 * <p>
	 * Constructs a function of the specified two variables.
	 * </p>
	 * 
	 * @param variables
	 *            array of variables to be involved. There are two Variables
	 *            soorted in the Variable array.
	 */
	public Function2D(Variable x, Variable y) {
		_x = x;
		_y = y;
		_cells = new double[_x.getCardinality()][_y.getCardinality()];
	}

	/**
	 * return the function as a string
	 */
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(_x.toString());
		stringBuffer.append(_y.toString());
		for (int i = 0; i < _x.getCardinality(); i++) {
			for (int j = 0; j < _y.getCardinality(); j++) {
				stringBuffer.append(_cells[i][j] + "\t");
			}
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}

	/**
	 * Save the function information into a CSV file in the destination
	 * directory, named by the two variable.
	 */
	public void saveAsCSV(PrintWriter out) throws FileNotFoundException,
			UnsupportedEncodingException {
		out.print(_x.getName() + "-" + _y.getName());
		for (int i = 0; i < _y.getCardinality(); i++) {
			out.print("," + _y.getName() + "=" + _y.getStates().get(i));
		}
		out.print("\n");

		for (int i = 0; i < _x.getCardinality(); i++) {
			out.print(_x.getName() + "=" + _x.getStates().get(i));
			for (int j = 0; j < _y.getCardinality(); j++) {
				out.print("," + _cells[i][j]);
			}
			out.print("\n");
		}
		out.flush();
	}

	/**
	 * Constructs a function of the specified array of variables. The value of
	 * the function are also specified in the cell array.
	 * 
	 * @param x
	 * @param y
	 */
	public Function2D(Variable x, Variable y, double[][] cells) {
		_x = x;
		_y = y;
		_cells = cells;
	}

	/**
	 * Add the double value to the appropriate cell indicated by XStateName and
	 * YStateName. Note the order of the two state names.
	 * 
	 * @param stateName
	 * @param add
	 */
	public void addCell(String XStateName, String YStateName, double add) {
		int index1 = _x.getStates().indexOf(XStateName);
		int index2 = _y.getStates().indexOf(YStateName);
		_cells[index1][index2] += add;
		
	}

	/**
	 * <p>
	 * Returns the sum of the cells in this function.
	 * </p>
	 * 
	 * @return the sum of the cells in this function.
	 */
	public double sumUp() {
		double sum = 0.0;

		int XSize = _x.getCardinality();
		int YSize = _y.getCardinality();
		for (int i = 0; i < XSize; i++)
			for (int j = 0; j < YSize; j++) {
				sum += _cells[i][j];
			}
		return sum;
	}

	/**
	 * <p>
	 * Scales down the cells in this function by the specified constant. When
	 * call this method, be aware and note that the dividend is non-zero.
	 * </p>
	 * 
	 * @param constant
	 *            constant by which the cells are to be scaled down.
	 */
	public void divide(double constant) {
		int XSize = _x.getCardinality();
		int YSize = _y.getCardinality();
		for (int i = 0; i < XSize; i++)
			for (int j = 0; j < YSize; j++) {
				_cells[i][j] /= constant;
			}
	}
	
	/**
	 * add a number to all cells 
	 * @param dou
	 * @return
	 */
	public void add(double constant) {
		if(constant<=0)
			return;
		int XSize = _x.getCardinality();
		int YSize = _y.getCardinality();
		for (int i = 0; i < XSize; i++)
			for (int j = 0; j < YSize; j++) {
				_cells[i][j] += constant;
			}
	}

	/*
	 * Normalize the function s.t. it is a joint probability. Note that when the
	 * sumup of the function is ZERO, it will be uniformly distributed.
	 */
	public boolean normalize(double smoothVal) {
		//smooth: using Laplace Estimation
		this.add(smoothVal);
		
		// reduces to normalization over all variable(s)
		double sum = sumUp();

		if (sum != 0.0) {
			divide(sum);
			return true;
		} else {
			// uniformly distributes it if normalizing constant equals 0
			double pro = 1.0 / (_x.getCardinality() * _y.getCardinality());
			for (int i = 0; i < _x.getCardinality(); i++)
				for (int j = 0; j < _y.getCardinality(); j++) {
					_cells[i][j] = pro;
				}
			return false;
		}
	}

	/*
	 * Suppose this function2D is a joint distribution of P(X,Y). Then one
	 * computation that may happen is to compute a Function1D of P(X). The
	 * argument variable is the one to sumout.
	 */
	public Function1D sumOut(Variable variable) {
		assert variable == _x || variable == _y;
		// result is an one-dimensional function
		double[] cells;
		int xCard = _x.getCardinality();
		int yCard = _y.getCardinality();

		Variable mainVar = _x;

		if (variable == _x) {
			// To sumout _x, reamin is a function of _y.
			mainVar = _y;
			cells = new double[yCard];
			for (int i = 0; i < xCard; i++) {
				for (int j = 0; j < yCard; j++) {
					cells[j] += _cells[i][j];
				}
			}
		} else {
			// To sumout _y, reamin is a function of _x.
			cells = new double[xCard];
			for (int i = 0; i < xCard; i++) {
				for (int j = 0; j < yCard; j++) {
					cells[i] += _cells[i][j];
				}
			}
		}

		return (new Function1D(mainVar, cells));
	}

	/*
	 * Suppose the function present a empirical distribution of two variables (x
	 * and Y). This method compute the conditional probability of P(X|Y) or
	 * P(Y|X) from the function. The argument variable must be x or y. If it is
	 * x, then the result is P(X|Y), otherwise it is P(Y|X).
	 * 
	 * If P(Y=y)=0 for some y, then P(X|Y=y) is uniformly distributed.
	 */
	public final boolean normalize(Variable variable, double smoothVal) {
		
		//smooth: using Laplace Estimation
		this.add(smoothVal);

		// argument variable must be either of the variables in this function
		assert variable == _x || variable == _y;

		boolean hasZero = false;

		int xCard = _x.getCardinality();
		int yCard = _y.getCardinality();

		double sum;
		if (variable == _x) {
			// uniform probability that may be used
			double uniform = 1.0 / xCard;

			for (int i = 0; i < yCard; i++) {
				// computes sum
				sum = 0.0;
				for (int j = 0; j < xCard; j++) {
					sum += _cells[j][i];
				}

				// normalizes
				if (sum != 0.0) {
					for (int j = 0; j < xCard; j++) {
						_cells[j][i] /= sum;
					}
				} else {
					for (int j = 0; j < xCard; j++) {
						_cells[j][i] = uniform;
					}
					hasZero = true;
				}
			}
		} else {
			// P(Y|X); uniform probability that may be used
			double uniform = 1.0 / yCard;
			for (int i = 0; i < xCard; i++) {
				// computes sum
				sum = 0.0;
				for (int j = 0; j < yCard; j++) {
					sum += _cells[i][j];
				}

				// normalizes
				if (sum != 0.0) {
					for (int j = 0; j < yCard; j++) {
						_cells[i][j] /= sum;
					}
				} else {
					for (int j = 0; j < yCard; j++) {
						_cells[i][j] = uniform;
					}
					hasZero = true;
				}
			}
		}

		return hasZero;
	}

	/**
	 * Creates and returns a deep copy of this function2D. This implementation
	 * copies everything in this function but reuses the reference to each
	 * variable it involves.
	 * 
	 * @return a deep copy of this function.
	 */
	public Function2D clone() {
		int XSize = _x.getCardinality();
		int YSize = _y.getCardinality();
		double[][] cells = new double[XSize][YSize];

		for (int i = 0; i < XSize; i++)
			for (int j = 0; j < YSize; j++) {
				cells[i][j] = _cells[i][j];
			}
		Function2D f2d = new Function2D(_x, _y, cells);
		return f2d;
	}

	/**
	 * Suppose this function is a representation of a joint probability of two
	 * variables _x and _y. This function is supposed to compute the mutual
	 * information of these two.
	 * 
	 * Note: the computation will not change any value of this function.
	 * 
	 * @return
	 */
	public double computeMI() {
		// To avoid the change pf the original function, we make a clone of
		// this.
		Function2D f2d = this.clone();

		// Make it a joint probability.
		f2d.normalize(0);

		// cells of joint and two marginal distributions
		double[][] jointCells = f2d._cells;
		double[] XCells = f2d.sumOut(_y)._cells;
		double[] YCells = f2d.sumOut(_x)._cells;

		// I(X;Y) = sum_X,Y P(X,Y) log P(X,Y)/P(X)P(Y)
		double mi = 0.0;
		for (int i = 0; i < _x.getCardinality(); i++)
			for (int j = 0; j < _y.getCardinality(); j++) {
				// if P(x, y) = 0, skip this term
				if (jointCells[i][j] != 0) {
					mi += jointCells[i][j]
							* Math.log(jointCells[i][j]
									/ (XCells[i] * YCells[j]));
				}
			}
		return mi;
	}

	/**
	 * Suppose this function is a representation of a joint probability of two
	 * variables _x and _y. This function is supposed to compute the normalized mutual
	 * information of these two.
	 * 
	 * Note: the computation will not change any value of this function.
	 * 
	 * @return
	 */
	public double computeNMI() {
		double MI = computeMI();
		
		Function1D f1 = this.sumOut(_x);
		f1.normalize(0);
		double ent1 = f1.computeEntropy();
		
		Function1D f2 = this.sumOut(_y);
		f2.normalize(0);
		double ent2 = f2.computeEntropy();
		
		if(ent1 == 0.0 || ent2 == 0.0)
			return 0.0;
				
		return MI/(Math.sqrt(ent1)*Math.sqrt(ent1));
	}
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.latlab.util.Function#project(org.latlab.util.Variable, int)
	// */
	// public Function project(Variable variable, int state) {
	//
	// // For Test
	// // System.out.println("Function2D.project(Variable, int) executed");
	//
	// // argument variable must be either of the variables in this function
	// assert variable == _x || variable == _y;
	//
	// // state must be valid
	// assert variable.isValid(state);
	//
	// // result is an one-dimensional function
	// Variable[] variables;
	// double[] cells;
	// int[] magnitudes = new int[] { 1 };
	//
	// if (variable == _x) {
	// variables = new Variable[] { _y };
	//
	// int yCard = _y.getCardinality();
	// cells = new double[yCard];
	//
	// System.arraycopy(_cells, state * yCard, cells, 0, yCard);
	// } else {
	// variables = new Variable[] { _x };
	//
	// int xCard = _x.getCardinality();
	// int yCard = _y.getCardinality();
	// cells = new double[xCard];
	//
	// int index = state;
	// for (int i = 0; i < xCard; i++) {
	// cells[i] = _cells[index];
	// index += yCard;
	// }
	// }
	//
	// return (new Function1D(variables, cells, magnitudes));
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.latlab.util.Function#sumOut(org.latlab.util.Variable)
	// */
	// public Function sumOut(Variable variable) {
	//
	// // For Test
	// // System.out.println("Function2D.sumOut(Variable) executed");
	//
	// // argument variable must be either of the variables in this function
	// assert variable == _x || variable == _y;
	//
	// // result is an one-dimensional function
	// Variable[] variables;
	// double[] cells;
	// int[] magnitudes = new int[] { 1 };
	//
	// int xCard = _x.getCardinality();
	// int yCard = _y.getCardinality();
	//
	// if (variable == _x) {
	// variables = new Variable[] { _y };
	//
	// cells = new double[yCard];
	//
	// int index = 0;
	// for (int i = 0; i < xCard; i++) {
	// for (int j = 0; j < yCard; j++) {
	// cells[j] += _cells[index++];
	// }
	// }
	// } else {
	// variables = new Variable[] { _x };
	//
	// cells = new double[xCard];
	//
	// int index = 0;
	// for (int i = 0; i < xCard; i++) {
	// for (int j = 0; j < yCard; j++) {
	// cells[i] += _cells[index++];
	// }
	// }
	// }
	//
	// return (new Function1D(variables, cells, magnitudes));
	// }
}
