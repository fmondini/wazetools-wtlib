////////////////////////////////////////////////////////////////////////////////////////////////////
//
// EmailFrequency.java
//
// UREQ Email Frequency
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.ureq;

/**
 * UREQ Email Frequency
 */
public enum EmailFrequency {

	UNK	("?", -1, "Unknown"),
	RTM	("R",  0, "Immediately after the request is created"),
	HOU	("H",  1, "One per hour"),
	DAY	("D", 24, "One per day");

	private final String _Code;
	private final int _Hour;
	private final String _Desc;

	/**
	 * Constructor
	 */
	EmailFrequency(String code, int hour, String desc) {
		this._Code = code;
		this._Hour = hour;
		this._Desc = desc;
    }

	public String getCode() { return(this._Code); }
	public int    getHour() { return(this._Hour); }
	public String getDesc() { return(this._Desc); }

	/**
	 * Get Object by Db Field
	 */
	public static EmailFrequency getByCode(String code) {

		EmailFrequency rc = UNK;

		for (EmailFrequency X : EmailFrequency.values())
			if (X._Code.equals(code))
				rc = X;

		return(rc);
	}
}
