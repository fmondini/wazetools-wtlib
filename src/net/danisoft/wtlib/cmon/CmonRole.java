////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CmonRole.java
//
// CMON User Roles
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.cmon;

/**
 * CMON User Roles
 */
public enum CmonRole {

	BASIC		(Integer.parseInt("0000000000000001", 2), "Basic Access"),
	EDITOR		(Integer.parseInt("0000000000000010", 2), "Edit City Data"),
	CANDRAGPIN	(Integer.parseInt("0000000000000100", 2), "Drag City Center"),
	ADDNEWCITY	(Integer.parseInt("0000000000001000", 2), "Add New Cities"),
	ADDEDITOR	(Integer.parseInt("0000000000010000", 2), "Add City Editors"),
//	FREE		(Integer.parseInt("0000000000100000", 2), ""),
	EDITGEOREF	(Integer.parseInt("0000000001000000", 2), "Edit GEO Reference"),
//	EDITREGION	(Integer.parseInt("0000000010000000", 2), "Edit Regions"),
//	EDITCOUNTRY	(Integer.parseInt("0000000100000000", 2), "Edit All Country"),
	ACTIVEUSR	(Integer.parseInt("0000001000000000", 2), "View Active Users"),
//	FREE		(Integer.parseInt("0000010000000000", 2), ""),
//	FREE		(Integer.parseInt("0000100000000000", 2), ""),
//	FREE		(Integer.parseInt("0001000000000000", 2), ""),
	SHOWLOG		(Integer.parseInt("0010000000000000", 2), "View Activity Log"),
	USERMGR		(Integer.parseInt("0100000000000000", 2), "Edit Users DB"),
	SYSADM		(Integer.parseInt("1000000000000000", 2), "System Administrator");

	public static final String DEFAULT_ROLE = "CMON_User"; // Value for AUTH_roles table

	private final int		_Value;
	private final String	_Descr;

	/**
	 * Constructor
	 */
	CmonRole(int value, String descr) {
		this._Value = value;
		this._Descr = descr;
    }

	public int    getValue() { return(this._Value); }
	public String getDescr() { return(this._Descr); }
}
