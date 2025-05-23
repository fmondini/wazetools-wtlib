////////////////////////////////////////////////////////////////////////////////////////////////////
//
// UreqRole.java
//
// UREQ Roles
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.ureq;

import javax.servlet.http.HttpServletRequest;

import net.danisoft.dslib.SysTool;

/**
 * UREQ Roles
 */
public enum UreqRole {

	// Please keep roles in order of importance (why?!? verify...)

	GUEST ("", "Can only post new unlock requests" ),
	UNLCK ("UREQ_Unlocker", "Authorized to handle unlock requests" );

	private final String _Code;
	private final String _Desc;

	/**
	 * Constructor
	 */
	UreqRole(String code, String desc) {
		this._Code = code;
		this._Desc = desc;
    }

	public String getCode() { return(this._Code); }
	public String getDesc() { return(this._Desc); }

	/**
	 * GET Enum by Code
	 */
	public static UreqRole getEnum(String code) {

		UreqRole rc = GUEST;

		for (UreqRole X : UreqRole.values())
			if (X.getCode().equals(code))
				rc = X;

		return(rc);
	}

	/**
	 * Description
	 */
	public static String getDesc(String code) {

		String rc = GUEST.getDesc();

		for (UreqRole X : UreqRole.values())
			if (X.getCode().equals(code))
				rc = X.getDesc();

		return(rc);
	}

	/**
	 * Get a logged in user's role
	 */
	public static UreqRole getRole(HttpServletRequest request) {

		UreqRole rc = GUEST;

		if (SysTool.isUserLoggedIn(request))
			for (UreqRole X : UreqRole.values())
				if (request.isUserInRole(X.getCode()))
					rc = X; // Dont break, use the LATEST found

		return(rc);
	}

}
