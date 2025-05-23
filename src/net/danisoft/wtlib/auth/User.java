////////////////////////////////////////////////////////////////////////////////////////////////////
//
// User.java
//
// DB Interface for the users table
//
// First Release: January 2013 by Fulvio Mondini (https://danisoft.software/)
//       Revised: Jan 2024 - Moved to V3
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import net.danisoft.dslib.FmtTool;
import net.danisoft.dslib.SysTool;
import net.danisoft.wtlib.cmon.CmonRole;
import net.danisoft.wtlib.code.CodeRole;

/**
 * DB Interface for the AUTH User table
 */
public class User {

	private static final String TBL_NAME = "AUTH_users";

	private static final int	AUTOCOMPLETE_MAX_RESULTS	= 25;
	private static final String	CHANGE_FIELD_REMINDER		= "ChangeMe!";
	private static final String	HOME_COUNTRIES_SEPARATOR	= ",";
	private static final String	NO_IMAGE_DEFAULT_URL		= "../images/no-image.png";

	public static String	getTblName()				{ return TBL_NAME; 					}
	public static int		getAutocompleteMaxResults()	{ return AUTOCOMPLETE_MAX_RESULTS;	}
	public static String	getChangeFieldReminder()	{ return CHANGE_FIELD_REMINDER;		}
	public static String	getHomeCountriesSeparator()	{ return HOME_COUNTRIES_SEPARATOR;	}

	private Connection cn;

	/**
	 * Constructor
	 */
	public User(Connection conn) {
		this.cn = conn;
	}

	/**
	 * User Data
	 */
	public class Data {

		// Fields
		private String			_Name;			// `USR_Name` varchar(32) NOT NULL DEFAULT '',
		private String			_Pass;			// `USR_Pass` varchar(32) NOT NULL DEFAULT '',
		private String			_GoogleID;		// `USR_GoogleID` varchar(255) NOT NULL DEFAULT '',
		private String			_FirstName;		// `USR_Firstname` varchar(255) NOT NULL DEFAULT 'UNKNOWN',
		private String			_LastName;		// `USR_LastName` varchar(255) NOT NULL DEFAULT 'UNKNOWN',
		private String			_PictURL;		// `USR_PictURL` varchar(255) NOT NULL DEFAULT '../images/no-image.png',
		private String			_Mail;			// `USR_Mail` varchar(255) NOT NULL DEFAULT '',
		private String			_SlackID;		// `USR_SlackID` varchar(32) NOT NULL DEFAULT '' COMMENT 'Slack ID (i.e. U03AA2A45)',
		private String			_Phone;			// `USR_Phone` varchar(32) NOT NULL DEFAULT '',
		private int				_Rank;			// `USR_Rank` int NOT NULL DEFAULT '0',
		private String			_Country;		// `USR_Country` varchar(255) NOT NULL DEFAULT '',
		private String			_Notes;			// `USR_Notes` text NOT NULL,
		private JSONObject		_Config;		// `USR_Config` json NOT NULL,
		private WazerConfig		_WazerConfig;
		private WazerContacts	_WazerContacts;

		// Getters
		public String			getName()			{ return this._Name;		}
		public String			getPass()			{ return this._Pass;		}
		public String			getGoogleID()		{ return this._GoogleID;	}
		public String			getFirstName()		{ return this._FirstName;	}
		public String			getLastName()		{ return this._LastName;	}
		public String			getPictURL()		{ return this._PictURL;		}
		public String			getMail()			{ return this._Mail;		}
		public String			getSlackID()		{ return this._SlackID;		}
		public String			getPhone()			{ return this._Phone;		}
		public int				getRank()			{ return this._Rank;		}
		public String			getCountry()		{ return this._Country;		}
		public String			getNotes()			{ return this._Notes;		}
		public JSONObject		getConfig()			{ return this._Config;		}
		public WazerConfig		getWazerConfig()	{ return this._WazerConfig;	}
		public WazerContacts	getWazerContacts()	{ return this._WazerContacts;	}

		// Setters
		public void setName(String name)							{ this._Name = name;					}
		public void setPass(String pass)							{ this._Pass = pass;					}
		public void setGoogleID(String googleID)					{ this._GoogleID = googleID;			}
		public void setFirstName(String firstName)					{ this._FirstName = firstName;			}
		public void setLastName(String lastName)					{ this._LastName = lastName;			}
		public void setPictURL(String pictURL)						{ this._PictURL = pictURL;				}
		public void setMail(String mail)							{ this._Mail = mail;					}
		public void setSlackID(String slackID)						{ this._SlackID = slackID;				}
		public void setPhone(String phone)							{ this._Phone = phone;					}
		public void setRank(int rank)								{ this._Rank = rank;					}
		public void setCountry(String country)						{ this._Country = country;				}
		public void setNotes(String notes)							{ this._Notes = notes;					}
		public void setConfig(JSONObject config)					{ this._Config = config;				}
		public void setWazerConfig(WazerConfig wazerConfig)			{ this._WazerConfig = wazerConfig;		}
		public void setWazerContacts(WazerContacts wazerContacts)	{ this._WazerContacts = wazerContacts;	}

		/**
		 * Constructor
		 */
		public Data() {
			super();

			this._Name			= "";
			this._Pass			= "";
			this._GoogleID		= "";
			this._FirstName		= "";
			this._LastName		= "";
			this._PictURL		= "";
			this._Mail			= "";
			this._SlackID		= "";
			this._Phone			= "";
			this._Rank			= 0;
			this._Country		= "";
			this._Notes			= "";
			this._Config		= new JSONObject();
			this._WazerConfig	= new WazerConfig(new JSONObject());
			this._WazerContacts	= new WazerContacts("", "", "");
		}
	}

	/**
	 * Check if a user exists
	 */
	public boolean Exists(String userName) {

		boolean rc = false;

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "'");

			rc = rs.next();

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Create a new user with default params and default roles
	 * @throws Exception
	 */
	public void Create(String UserName, String UserCountry, String Creator) throws Exception {

		Data usrData = new Data();
		WazerConfig usrConfig = new WazerConfig(new JSONObject());

		usrData.setName(UserName);
		usrData.setPass(CHANGE_FIELD_REMINDER);
		usrData.setGoogleID("");
		usrData.setFirstName(CHANGE_FIELD_REMINDER);
		usrData.setLastName(CHANGE_FIELD_REMINDER);
		usrData.setPictURL(NO_IMAGE_DEFAULT_URL);
		usrData.setMail(CHANGE_FIELD_REMINDER);
		usrData.setSlackID("");
		usrData.setPhone("");
		usrData.setRank(1);
		usrData.setCountry(UserCountry);
		usrData.setNotes("Account created " + FmtTool.fmtDateTime() + " by " + Creator + "\n");
		// Config
		usrConfig.getCmon().setRole(CmonRole.BASIC.getValue());
		usrConfig.getCode().setRole(CodeRole.EUSER.getValue());
		usrData.setConfig(usrConfig.toJson());

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " LIMIT 1");

		rs.moveToInsertRow();
		_update_rs_from_obj(rs, usrData);
		rs.insertRow();

		rs.close();
		st.close();

		new TomcatRole(this.cn).createDefault(usrData.getName());
	}

	/**
	 * Read
	 */
	public Data Read(String userName) {
		return(
			_read_obj_by_id(userName)
		);
	}

	/**
	 * Update
	 * @throws Exception
	 */
	public void Update(String userName, Data usrData) throws Exception {

		Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "';");

		if (rs.next()) {

			_update_rs_from_obj(rs, usrData);
			rs.updateRow();

		} else
			throw new Exception("User.Update(): USR_Name '" + userName + "' NOT found");

		rs.close();
		st.close();
	}

	/**
	 * Delete
	 */
	public void Delete(String userName) {

		try {

			Statement st = this.cn.createStatement();
			st.executeUpdate("DELETE FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "'");
			st.close();

		} catch (Exception e) { }
	}

	/**
	 * Get User Mail by Username
	 */
	public String getMailByUser(String userName) {

		String rc = "";

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT USR_Mail FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "'");

			if (rs.next())
				rc = rs.getString("USR_Mail");

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("User.getMail(\"" + userName + "\"): " + e.toString());
		}

		return(rc);
	}

	/**
	 * Get ALL Users
	 */
	public Vector<Data> getAll() {

		return(
			_fill_usr_vector("SELECT * FROM " + TBL_NAME + " ORDER BY USR_Name;")
		);
	}

	/**
	 * Get ALL Users for a country
	 * @param countryCode The country code, or "" for all countries
	 */
	public Vector<Data> getAllByCountry(String countryCode) {

		return(
			_fill_usr_vector(
				"SELECT * FROM " + TBL_NAME + " " +
				(countryCode.equals("")
					? ""
					: "WHERE USR_Country LIKE '%" + countryCode + "%' "
				) +
				"ORDER BY USR_Name;"
			)
		);
	}

	/**
	 * Get ALL Users for a mail
	 * @param eMail The e-Mail address
	 */
	public Vector<Data> getAllByMail(String eMail) {

		return(
			_fill_usr_vector(
				"SELECT * FROM " + TBL_NAME + " WHERE USR_Mail = '" + eMail + "' ORDER BY USR_Name;"
			)
		);
	}

	/**
	 * Search ALL users (in current user's countries) by keyword (for autocomplete)
	 */
	public Vector<Data> Search(HttpServletRequest request, String keyw) {

		Data currUsrData = Read(SysTool.getCurrentUser(request));
		Vector<String> vecEnabledCountries = currUsrData.getWazerConfig().getAuth().getActiveCountryCodes();

		Vector<Data> vecUsrData = _fill_usr_vector(
			"SELECT * " +
			"FROM " + TBL_NAME + " " +
			"WHERE (" +
				"USR_Name LIKE '%" + keyw + "%' OR " +
				"USR_Mail LIKE '%" + keyw + "%' OR " +
				"USR_FirstName LIKE '%" + keyw + "%' OR " +
				"USR_LastName LIKE '%" + keyw + "%'" +
			") ORDER BY USR_Name;"
		);

		// Filter

		Vector<Data> vecResult = new Vector<Data>();

		for (Data usrData : vecUsrData) {
			for (String enabledCountry : vecEnabledCountries) {
				if (usrData.getCountry().equals(enabledCountry)) {
					if (vecResult.size() < AUTOCOMPLETE_MAX_RESULTS)
						vecResult.add(usrData);
				}
			}
		}

		return(vecResult);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// +++ PRIVATE +++
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parse a given ResultSet into a User.Data object
	 * @return <User.Data> result 
	 */
	private Data _parse_obj_from_rs(ResultSet rs) {

		Data usrData = new Data();

		try {

			usrData.setName(rs.getString("USR_Name"));
			usrData.setPass(rs.getString("USR_Pass"));
			usrData.setGoogleID(rs.getString("USR_GoogleID"));
			usrData.setFirstName(rs.getString("USR_FirstName"));
			usrData.setLastName(rs.getString("USR_LastName"));
			usrData.setPictURL(rs.getString("USR_PictURL"));
			usrData.setMail(rs.getString("USR_Mail"));
			usrData.setSlackID(rs.getString("USR_SlackID"));
			usrData.setPhone(rs.getString("USR_Phone"));
			usrData.setRank(rs.getInt("USR_Rank"));
			usrData.setCountry(rs.getString("USR_Country"));
			usrData.setNotes(rs.getString("USR_Notes"));

			// JSON Special Treatment

			usrData.setConfig(new JSONObject());
			try { usrData.setConfig(new JSONObject(rs.getString("USR_Config"))); } catch (Exception e) { }

			usrData.setWazerConfig(new WazerConfig(new JSONObject()));
			try { usrData.setWazerConfig(new WazerConfig(usrData.getConfig())); } catch (Exception e) { }

			usrData.setWazerContacts(new WazerContacts("", "", ""));
			try { usrData.setWazerContacts(new WazerContacts(usrData.getName(), usrData.getSlackID(), usrData.getMail())); } catch (Exception e) { }

		} catch (Exception e) {
			System.err.println("_parse_obj_from_rs(): " + e.toString());
		}

		return(usrData);
	}

	/**
	 * Read USR Record based on given UserName
	 * @return <User.Data> result 
	 */
	private Data _read_obj_by_id(String userName) {

		Data usrData = new Data();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "';");

			if (rs.next())
				usrData = _parse_obj_from_rs(rs);

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("_read_obj_by_id('" + userName + "')" + e.toString());
		}

		return(usrData);
	}

	/**
	 * Read USR Records based on given query
	 * @return Vector<User.Data> of results 
	 */
	private Vector<Data> _fill_usr_vector(String query) {

		Vector<Data> vecUsrData = new Vector<Data>();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next())
				vecUsrData.add(_parse_obj_from_rs(rs));

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(vecUsrData);
	}

	/**
	 * Update a given ResultSet from a given User.Data object
	 */
	private static void _update_rs_from_obj(ResultSet rs, Data dataObject) {

		try {

			rs.updateString("USR_Name", dataObject.getName());
			rs.updateString("USR_Pass", dataObject.getPass());
			rs.updateString("USR_GoogleID", dataObject.getGoogleID());
			rs.updateString("USR_Firstname", dataObject.getFirstName());
			rs.updateString("USR_LastName", dataObject.getLastName());
			rs.updateString("USR_PictURL", dataObject.getPictURL());
			rs.updateString("USR_Mail", dataObject.getMail());
			rs.updateString("USR_SlackID", dataObject.getSlackID());
			rs.updateString("USR_Phone", dataObject.getPhone());
			rs.updateInt("USR_Rank", dataObject.getRank());
			rs.updateString("USR_Country", dataObject.getCountry());
			rs.updateString("USR_Notes", dataObject.getNotes());
			rs.updateString("USR_Config", dataObject.getWazerConfig().toJson().toString());

		} catch (Exception e) {
			System.out.println("User._update_rs_from_obj(): " + e.toString());
		}
	}

}
