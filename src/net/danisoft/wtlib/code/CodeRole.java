////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CodeRole.java
//
// CODE User Roles
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.code;

import javax.servlet.http.HttpSession;

import net.danisoft.dslib.EnvTool;

/**
 * CODE User Roles
 */
public enum CodeRole {

	EUSER	(Integer.parseInt("0000000000000000000000000000001", 2), "End User"),
	AREAM	(Integer.parseInt("0000000000000000000000000001000", 2), "Area Manager"),
	DISTM	(Integer.parseInt("0000000000000000000000001000000", 2), "District Manager"),
	STATM	(Integer.parseInt("0000000000000000000001000000000", 2), "State Manager"),
	LOCLC	(Integer.parseInt("0000000000000000001000000000000", 2), "Local Champ"),
	CTRYM	(Integer.parseInt("0000000000000001000000000000000", 2), "Country Manager"),
	GLOBC	(Integer.parseInt("0000000000001000000000000000000", 2), "Global Champ"),
	SITEM	(Integer.parseInt("0000000000100000000000000000000", 2), "Site Manager"),
	SYSOP	(Integer.parseInt("1000000000000000000000000000000", 2), "SysOp Only Reserved Areas");

	public static final String DEFAULT_ROLE = "CODE_User"; // Value for AUTH_roles table

	private final int		_Value;
	private final String	_Descr;

	/**
	 * Constructor
	 */
	CodeRole(int value, String descr) {
		this._Value = value;
		this._Descr = descr;
    }

	public int    getValue() { return(this._Value); }
	public String getDescr() { return(this._Descr); }

	/**
	 * Get UserRole Object by Value
	 * @return UserRole.EUSER if unknown
	 */
	public static CodeRole getRole(int Value) {

		CodeRole rc = EUSER;

		for (CodeRole X : CodeRole.values())
			if (X.getValue() == Value)
				rc = X;

		return(rc);
	}

	/**
	 * Get UserRole Description
	 */
	public static String getDescr(int Value, boolean UseHtml) {

		String rc =
			(UseHtml ? "<div class=\"DS-text-exception\">" : "") +
			"[Unknown UserRole: " + Value + "]" +
			(UseHtml ? "</div>" : "")
		;

		if (Value > 0) {
			for (CodeRole X : CodeRole.values())
				if (X.getValue() == Value)
					rc = X.getDescr();
		} else {
			rc =
				(UseHtml ? "<div class=\"DS-text-exception\">" : "") +
				"[ERR] No UserRole Set" +
				(UseHtml ? "</div>" : "")
			;
		}

		return(rc);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// App-related static functions
	//

	private static final String USR_ROLE_SESSVAR = "CODE_UserRoleValue";

	/**
	 * Put User Role Value in USR_ROLE_SESSVAR session variable
	 */
	public static void setUserRoleValue(HttpSession session, int UserRoleValue) {
		session.setAttribute(USR_ROLE_SESSVAR, UserRoleValue);
	}

	/**
	 * Get Current User Role Value from USR_ROLE_SESSVAR session variable or 0 (zero) if not logged in
	 */
	public static int getUserRoleValue(HttpSession session) {
		return(EnvTool.getInt(session, USR_ROLE_SESSVAR, 0));
	}

	/**
	 * Check if logged in user have the specified role
	 */
	public static boolean userHasRole(HttpSession session, CodeRole requestedRole) {
		return((EnvTool.getInt(session, USR_ROLE_SESSVAR, 0) & requestedRole.getValue()) == requestedRole.getValue());
	}

}

