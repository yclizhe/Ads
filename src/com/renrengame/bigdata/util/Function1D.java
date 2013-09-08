/**
 * Function1D.java 
 * This is the 1D function
 */
package com.renrengame.bigdata.util;

//import Function;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * This class provides an implementation for one-dimensional tabular functions,
 * namely, vectors.
 * 
 * @author Tao Chen
 * 
 */
public class Function1D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2074095867284121817L;

	/**
	 * the shortcut to the only variable in this function.
	 */
	protected Variable _var;

	/**
	 * the one-dimensional array representation of the cells of this function.
	 * Note that it is a double array, not int array
	 */
	protected double[] _cells;

	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(_var.toString());
		for(int i = 0; i<_cells.length; i++){
			stringBuffer.append(_cells[i] + "\t");
		}
		stringBuffer.append("\n");
		return stringBuffer.toString();
	}
	
	/**
	 * Save the function information into a CSV file in the destination directory, named by the class variable. 
	 */
	public void saveAsCSV(PrintWriter out) throws FileNotFoundException,
	UnsupportedEncodingException {
		out.println(_var.getName());
		for(int i = 0; i<_cells.length; i++){
			out.println(_var.getStates().get(i)+ "," + _cells[i]);
		}
		out.flush();
	}

	/**
	 * Suppose that this function is a probability of some variable X. We Compute the entropy of X. 
	 * @return
	 */
	public double computeEntropy(){
		// ensure the distribution sum up to one
		assert sumUp() == 1.0;

		// H(X) = - sum_X P(X) log P(X)
		double ent = 0.0;
		for (double cell : _cells) {
			// if P(x) = 0, skip this term
			if (cell != 0.0) {
				ent -= cell * Math.log(cell);
			}
		}
		return ent;
	}
	
	/**
	 * Add the double value to the appropriate cell indicated by stateName
	 * @param stateName
	 * @param add
	 */
	public void addCell(String stateName, double add){
		int index = _var.getStates().indexOf(stateName);
		_cells[index] += add;
	}
	
	/**
	 * <p>
	 * Constructs a function of the specified array of variables.
	 * </p>
	 * 
	 * <p>
	 * Note: Only function classes are supposed to call this method.
	 * </p>
	 * 
	 * @param variables
	 *            array of variables to be involved. There is only one Variable.
	 */
	public Function1D(Variable var) {
		_var = var;
		_cells = new double[_var.getStates().size()];
	}
	
	/**
	 * New a 1D function given the variable and its value.
	 * @param var
	 * @param cell
	 */
	public Function1D(Variable var, double[] cell) {
		_var = var;
		_cells = cell;
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

		int domainSize = _cells.length;
		for (int i = 0; i < domainSize; i++) {
			sum += _cells[i];
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
		int domainSize = _cells.length;
		for (int i = 0; i < domainSize; i++) {
			_cells[i] /= constant;
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
		for(int i=0; i<this._cells.length; i++)
			_cells[i] += constant;
	}
	
	
	/*
	 * Normalize the function s.t. it is a probability. Note that when the sumup
	 * of the function is ZERO, it will be uniformly distributed.
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
			Arrays.fill(_cells, 1.0 / _cells.length);
			return false;
		}
	}
	


}
