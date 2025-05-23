////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Contact.java
//
// DB Interface for the AUTH_users table to retrieve user's contact method
//
// First Release: Jan 2023 by Fulvio Mondini (https://danisoft.net/)
//       Revised: Jan 2024 - Moved to V3
//       Revised: Mar 2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import net.danisoft.dslib.Database;

/**
 * DB Interface for the AUTH_users table to retrieve user's contact method
 */
public class WazerContacts {

	/**
	 * Contact Method
	 */
	public enum Method {
		NONE, MAIL, SLACK
	}

	// Fields
	private Method _Method;
	private String _Id;
	private String _Head;
	private String _Error;

	// Getters
	public Method getMethod()	{ return this._Method;	}
	public String getId()		{ return this._Id;		} // This can be a MemberID or an e-Mail
	public String getHead()		{ return this._Head;	}
	public String getError()	{ return this._Error;	}

	// Setters
	public void setMethod(Method m)	{ this._Method = m;	}
	public void setId(String s)		{ this._Id = s;		}
	public void setHead(String s)	{ this._Head = s;	}
	public void setError(String s)	{ this._Error = s;	}

	/**
	 * Constructor (with user read)
	 */
	public WazerContacts(String UserID) {

		Database DB = null;

		try {

			if (UserID.equals(""))
				throw new Exception("Empty class initialization");

			DB = new Database();
			User USR = new User(DB.getConnection());
			User.Data usrData = USR.Read(UserID);

			_select_contact(usrData.getName(), usrData.getSlackID(), usrData.getMail());

		} catch (Exception e) {
			System.err.println("WazerContacts(): " + e.toString());
			setError(e.getMessage());
		}

		if (DB != null)
			DB.destroy();
	}

	/**
	 * Constructor (with NO user read)
	 */
	public WazerContacts(String userName, String slackId, String mailAddress) {
		_select_contact(userName, slackId, mailAddress);
	}

	/**
	 * Check for NO contact methods
	 */
	public boolean isEmpty() {
		return (this.getMethod() == Method.NONE);
	}

	/**
	 * Select Contact
	 */
	private void _select_contact(String user, String slack, String mail) {

		setMethod(Method.NONE);
		setId("");
		setHead("");
		setError("");

		if (!slack.equals("")) {

			setMethod(Method.SLACK);
			setId(slack);
			setHead("Enter the text of the message to send to <b>" + user + "</b> via <b>Slack DM</b>");

		} else if (!mail.equals("")) {

			setMethod(Method.MAIL);
			setId(mail);
			setHead("Enter the text of the message to send to <b>" + user + "</b> via <b>e-Mail</b>");

			if (!this.getId().contains("@"))
				this.setId("");
		}

		if (this.getId().equals("")) {
			setError(
				"<div>No SlackID or e-Mail in archive, messaging feature not available.</div>" +
				"<div>The only way to reach <b>" + user + "</b> is via WME chat.</div>" +
				"<br>" +
				"<div>The user must enter his SlackID or email address in his AUTH panel.</div>" +
				"<div>Alternatively, a Country Administrator can do it for him.</div>"
			);
		}
	}

}
