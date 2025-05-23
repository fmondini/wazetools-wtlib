////////////////////////////////////////////////////////////////////////////////////////////////////
//
// CifpRole.java
//
// CIFP Roles
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.cifp;

/**
 * CIFP Roles
 */
public enum CifpRole {

	MGR ("CIFP_Manager", "Authorized to create/edit/delete only its own closures");

	private final String _Code;
	private final String _Desc;

	/**
	 * Constructor
	 */
	CifpRole(String code, String desc) {
		this._Code = code;
		this._Desc = desc;
    }

	public String getCode() { return(this._Code); }
	public String getDesc() { return(this._Desc); }
}
