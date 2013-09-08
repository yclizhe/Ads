/**
 * Variable.java 
 */
package com.renrengame.bigdata.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class provides an implementation for nominal variables.
 * </p>
 * 
 * @author Tao Chen
 * 
 */
public class Variable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9092854235989987053L;

	/**
	 * the name of this variable.
	 */
	private String _name;

	/**
	 * the list of states of this variable.
	 */
	private ArrayList<String> _states = new ArrayList<String>();

	/**
	 * Constructs a variable with the specified name
	 * 
	 * @param name
	 *            name of the variable to be created.
	 */
	public Variable(String name) {
		name = name.trim();
		// name cannot be blank
		assert name.length() > 0;
		_name = name;
	}

	/**
	 * Add the state to this variable if it is not a state in the current state
	 * list. Otherwise do nothing.
	 * 
	 * TODO: Check missing value dealt with.
	 * 
	 * @param stateName
	 */
	public void addState(String stateName) {
		if (stateName.equalsIgnoreCase("unknown")
				|| stateName.equalsIgnoreCase("\\N")
				|| stateName.equalsIgnoreCase("NULL")) {
			return;
		}
		if (!_states.contains(stateName)) {
			_states.add(stateName);
		}
	}

	/**
	 * To return whether this variable is a class variable, indicated by name.
	 * 
	 * @return true if it is a class variable.
	 */
	public boolean isClassVar() {
		return (_name.equalsIgnoreCase("is_click") || _name
				.equalsIgnoreCase("label"));
	}

	/**
	 * To return whether this variable is a App variable, indicated by name;
	 * 
	 * @return true if it is an APP variable
	 */
	public boolean isAppVar() {
		boolean flag = false;
		String[] appVarNames = { "log_version", "Req_Day", "Req_Time",
				"app_id", "cp_id", "terminal_type", "os", "sdk_type",
				"sdk_version", "oper_company", "net_type", "ad_size",
				"is_henping", "is_broke", "app_type_1", "app_type_2",
				"app_type_3", "app_type_4", "app_type_5", "app_type_6",
				"app_type_7", "app_type_8", "app_type_9", "app_type_10",
				"app_type_11", "app_type_12", "app_type_13", "app_type_14",
				"app_type_15", "app_type_16", "app_type_17", "app_type_18",
				"app_type_19", "app_type_20", "app_type_21", "province",
				"app_createTime", "app_updateTime"};
		for (int i = 0; i < appVarNames.length; i++) {
			if (_name.equalsIgnoreCase(appVarNames[i]))
				flag = true;
		}
		return flag;
	}

	/**
	 * To return whether this variable is an Ads variable, indicated by name;
	 * 
	 * @return true if it is an Ad variable
	 */
	public boolean isAdVar() {
		boolean flag = false;
		String[] adVarNames = { "ad_id", "ad_group_id", "ad_owner",
				"ad_activity", "showType", "deviceType", "clickAction",
				"ad_createTime", "ad_updateTime" };
		for (int i = 0; i < adVarNames.length; i++) {
			if (_name.equalsIgnoreCase(adVarNames[i]))
				flag = true;
		}
		return flag;
	}

	/**
	 * Returns <code>true</code> if the specified object is equals to this
	 * variable. An object is equals to this variable if (1) it is a variable;
	 * and (2) it has the same name.
	 * 
	 * @return <code>true</code> if the specified object is equals to this
	 *         variable.
	 */
	public final boolean equals(Object object) {
		// tests identity
		if (this == object) {
			return true;
		}

		Variable variable = (Variable) object;
		return (_name.equals(variable._name));
	}

	/**
	 * Returns the cardinality of this variable. The cardinality of a nominal
	 * variable equals the number of states that this variable can take.
	 * 
	 * @return the cardinality of this variable.
	 */
	public final int getCardinality() {
		return _states.size();
	}

	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name of this variable.
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * Returns the list of states of this variable.
	 * 
	 * @return the list of states of this variable.
	 */
	public final ArrayList<String> getStates() {
		return _states;
	}

	/**
	 * Returns the index of the specified state in the domain of this variable.
	 * 
	 * @param state
	 *            state whose index is to be returned.
	 * @return the index of the specified state in the domain of this variable.
	 */
	public final int indexOf(String state) {
		return _states.indexOf(state);
	}

	/**
	 * Output content of the variable as a string
	 * 
	 * @return
	 */
	public final String toString() {
		// builds string representation
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("Variable ");
		stringBuffer.append("\"" + _name + "(");
		stringBuffer.append(_states.size() + ")\":");

		for (int i = 0; i < _states.size(); i++) {
			stringBuffer.append(" " + _states.get(i));
		}
		stringBuffer.append("\n");
		return stringBuffer.toString();
	}

	/**
	 * Updates the name of this variable.
	 * 
	 * <p>
	 * Note: Only <code>BeliefNode.setName(String></code> is supposed to call
	 * this method. Abusing this method may cause inconsistency between names of
	 * a belief node and the variable attached to it.
	 * </p>
	 * 
	 * @param name
	 *            new name of this variable.
	 */
	public void setName(String name) {
		name = name.trim();
		// name cannot be blank
		assert name.length() > 0;
		_name = name;
	}
}
