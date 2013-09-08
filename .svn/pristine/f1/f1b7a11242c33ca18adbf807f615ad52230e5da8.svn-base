/**
 * Function3D.java 
 */
package com.renrengame.bigdata.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class provides an implementation for a number of two-dimensional tabular
 * functions.
 * 
 * It is a special 3-d function, where the domensions are denoted by C=0,1; X
 * and Y respectively. In our case, the class are 0=unclicked and 1=clicked. So
 * we use two Function2D f0 and f1 to present each of the case.
 * 
 * Where f0 and f1 are a 2-d function of X and Y.
 * 
 * @author Tao Chen
 * 
 */
public class Function3D implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9001906153566546629L;

	// The two functions given C=0 or C=1.
	protected Function2D _f0, _f1;

	// The summation of all cells in _f0. Make sure update when the _f0 is
	// changed.
	double _sum0 = 0.0;

	// The summation of all cells in _f1. Make sure update when the _f1 is
	// changed.
	double _sum1 = 0.0;

	/**
	 * <p>
	 * Constructs a function of the specified array of variables.
	 * </p>
	 * 
	 * @param variables
	 *            array of variables to be involved. There are two Variables
	 *            soorted in the Variable array.
	 */
	public Function3D(Variable x, Variable y) {
		_f0 = new Function2D(x, y);
		_f1 = new Function2D(x, y);
	}

	/**
	 * <p>
	 * Returns the sum of the cells in this function.
	 * </p>
	 * 
	 * @return the sum of the cells in this function.
	 */
	public double sumUp() {
		return _sum0 + _sum1;
	}

	/**
	 * Add the double value to the appropriate cell indicated by label,
	 * XStateName and YStateName. Note the order of the two state names.
	 * 
	 * @param stateName
	 * @param add
	 */
	public void addCell(String label, String XStateName, String YStateName,
			double add) {
		if (label.equals("0")) {
			_f0.addCell(XStateName, YStateName, add);
			_sum0 += add;
		} else if (label.equals("1")) {
			_f1.addCell(XStateName, YStateName, add);
			_sum1 += add;
		} else {
			System.out.println("There is bug");
			System.exit(2);
		}
	}

	/**
	 * return the function as a string
	 */
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(_sum0 + "\t" + _sum1 + "\n");
		stringBuffer.append(_f0);
		stringBuffer.append(_f1);
		return stringBuffer.toString();
	}

	/**
	 * Save the function information into a CSV file in the destination
	 * directory, named for example app_id-C-deviceType.csv.
	 */
	public void saveAsCSV(PrintWriter out) throws FileNotFoundException,
			UnsupportedEncodingException {
		out.print("Number of C=0" + _sum0);
		_f0.saveAsCSV(out);
		
		out.print("\n");

		out.print("Number of C=1" + _sum1);
		_f1.saveAsCSV(out);
		
		out.flush();
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
		_f0.divide(constant);
		_sum0 /= constant;

		_f1.divide(constant);
		_sum1 /= constant;
	}
	
	/**
	 * add a number to all cells 
	 * @param dou
	 * @return
	 */
	public void add(double constant) {
		if(constant<=0)
			return;
		_f0.add(constant);
		_sum0 = _f0.sumUp();

		_f1.add(constant);
		_sum1 = _f1.sumUp();
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
			_f0.normalize(smoothVal);
			_f0.divide(2.0);

			_f1.normalize(smoothVal);
			_f1.divide(2.0);

			_sum0 = 0.5;
			_sum1 = 0.5;

			return false;
		}
	}
	
	/*
	 * This method compute the conditional probability of P(XY|C)
	 */
	public boolean normalizeCondition(double smoothVal) {
		
		//smooth: using Laplace Estimation
		this.add(smoothVal);

		boolean NonZeroSum = true;
		// reduces to normalization over all variable(s)
		if(_sum0 != 0.0){
			_f0.divide(_sum0);
		}else{
			_f0.normalize(0.0);
			NonZeroSum = false;
		}
		_sum0 = 1.0;
		
		if(_sum1 != 0.0){
			_f1.divide(_sum1);
		}else{
			_f1.normalize(0.0);
			NonZeroSum = false;
		}
		_sum1 = 1.0;
		
		return NonZeroSum;
	}

	/**
	 * To compute the conditional mutual information of the _x,_y given the
	 * class label.
	 * 
	 * @return conditional mutual info.
	 */
	public double computeCMI() {
		double mi0 = _f0.computeMI();
		double mi1 = _f1.computeMI();

		double w0 = _sum0 / (_sum0 + _sum1);
		double w1 = 1 - w0;

		return (w1 * mi1 + w0 * mi0);
	}

	/**
	 * To compute the conditional normalized mutual information of the _x,_y given the
	 * class label.
	 * 
	 * @return
	 */
	public double computeCNMI(){
		double NMI0 = _f0.computeNMI();
		double NMI1 = _f1.computeNMI();
		double w0 = _sum0 / (_sum0 + _sum1);
		double w1 = 1 - w0;
		return (w1 * NMI1 + w0 * NMI0);
	}
	
	// /*
	// * Suppose the function present a empirical distribution of two variables
	// (x
	// * and Y). This method compute the conditional probability of P(X|Y) or
	// * P(Y|X) from the function. The argument variable must be x or y. If it
	// is
	// * x, then the result is P(X|Y), otherwise it is P(Y|X).
	// *
	// * If P(Y=y)=0 for some y, then P(X|Y=y) is uniformly distributed.
	// */
	// public final boolean normalize(Variable variable) {
	//
	// // argument variable must be either of the variables in this function
	// assert variable == _x || variable == _y;
	//
	// boolean hasZero = false;
	//
	// int xCard = _x.getCardinality();
	// int yCard = _y.getCardinality();
	//
	// double sum;
	// if (variable == _x) {
	// // uniform probability that may be used
	// double uniform = 1.0 / xCard;
	//
	// for (int i = 0; i < yCard; i++) {
	// // computes sum
	// sum = 0.0;
	// for (int j = 0; j < xCard; j++) {
	// sum += _cells[j][i];
	// }
	//
	// // normalizes
	// if (sum != 0.0) {
	// for (int j = 0; j < xCard; j++) {
	// _cells[j][i] /= sum;
	// }
	// } else {
	// for (int j = 0; j < xCard; j++) {
	// _cells[j][i] = uniform;
	// }
	// hasZero = true;
	// }
	// }
	// } else {
	// // P(Y|X); uniform probability that may be used
	// double uniform = 1.0 / yCard;
	// for (int i = 0; i < xCard; i++) {
	// // computes sum
	// sum = 0.0;
	// for (int j = 0; j < yCard; j++) {
	// sum += _cells[i][j];
	// }
	//
	// // normalizes
	// if (sum != 0.0) {
	// for (int j = 0; j < yCard; j++) {
	// _cells[i][j] /= sum;
	// }
	// } else {
	// for (int j = 0; j < yCard; j++) {
	// _cells[i][j] = uniform;
	// }
	// hasZero = true;
	// }
	// }
	// }
	//
	// return hasZero;
	// }

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
