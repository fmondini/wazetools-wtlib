////////////////////////////////////////////////////////////////////////////////////////////////////
//
// AuthRole.java
//
// AUTH User Roles
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import javax.servlet.http.HttpServletRequest;

import net.danisoft.dslib.SysTool;

/**
 * AUTH User Roles
 */
public enum AuthRole {

	// Please keep roles in order of importance, see getRole()

	UNKWN ("AUTH_Unknown", "AUTH Anonymous"       ),
	EUSER ("AUTH_User",    "AUTH Registered User" ),
	ADMIN ("AUTH_Admin",   "AUTH Administrator"   );

	private final String _Code;
	private final String _Desc;

	AuthRole(String code, String desc) {
		this._Code = code;
		this._Desc = desc;
    }

	public String getCode() { return(this._Code); }
	public String getDesc() { return(this._Desc); }

	/**
	 * Get a logged in user's role
	 */
	public static AuthRole getRole(HttpServletRequest request) {

		AuthRole rc = UNKWN;

		if (SysTool.isUserLoggedIn(request))
			for (AuthRole X : AuthRole.values())
				if (request.isUserInRole(X.getCode()))
					rc = X; // Don't break, use the LATEST found

		return(rc);
	}

	/**
	 * GET Enum by Code
	 */
	public static AuthRole getEnum(String Code) {

		AuthRole rc = UNKWN;

		for (AuthRole X : AuthRole.values())
			if (X.getCode().equals(Code))
				rc = X;

		return(rc);
	}

	/**
	 * Description
	 */
	public static String getDesc(String Code) {

		String rc = UNKWN.getDesc();

		for (AuthRole X : AuthRole.values())
			if (X.getCode().equals(Code))
				rc = X.getDesc();

		return(rc);
	}

}
