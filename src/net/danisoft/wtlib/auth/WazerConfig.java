////////////////////////////////////////////////////////////////////////////////////////////////////
//
// WazerConfig.java
//
// Wazer Config
//
// First Release: ???/???? by Fulvio Mondini (https://danisoft.software/)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import java.util.Collections;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import net.danisoft.dslib.Database;
import net.danisoft.dslib.SysTool;
import net.danisoft.wtlib.cmon.CmonRole;
import net.danisoft.wtlib.code.CodeRole;
import net.danisoft.wtlib.ureq.EmailFrequency;

/**
 * Wazer Config
 */
public class WazerConfig {

	// Fields
	private CfgAuth _Auth;
	private CfgCifp _Cifp;
	private CfgCmon _Cmon;
	private CfgCode _Code;
	private CfgUreq _Ureq;

	// Getters
	public CfgAuth getAuth() { return this._Auth; }
	public CfgCifp getCifp() { return this._Cifp; }
	public CfgCmon getCmon() { return this._Cmon; }
	public CfgCode getCode() { return this._Code; }
	public CfgUreq getUreq() { return this._Ureq; }

	// Setters
	public void setAuth(CfgAuth auth) { this._Auth = auth; }
	public void setCifp(CfgCifp cifp) { this._Cifp = cifp; }
	public void setCmon(CfgCmon cmon) { this._Cmon = cmon; }
	public void setCode(CfgCode code) { this._Code = code; }
	public void setUreq(CfgUreq ureq) { this._Ureq = ureq; }

	/**
	 * Constructor
	 */
	public WazerConfig(JSONObject jConfig) {
		super();

		this._Auth = new CfgAuth(jConfig);
		this._Cifp = new CfgCifp(jConfig);
		this._Cmon = new CfgCmon(jConfig);
		this._Code = new CfgCode(jConfig);
		this._Ureq = new CfgUreq(jConfig);
	}

	/**
	 * Class to JSON
	 */
	public JSONObject toJson() {

		JSONObject jObj = new JSONObject();

		jObj.put("auth", getAuth().toJson());
		jObj.put("cifp", getCifp().toJson());
		jObj.put("cmon", getCmon().toJson());
		jObj.put("code", getCode().toJson());
		jObj.put("ureq", getUreq().toJson());

		return(jObj);
	}

	/**
	 * AUTH SubClass
	 */
	public class CfgAuth {

		// Fields
		private JSONArray _ActiveCountries;

		// Getters
		public JSONArray getActiveCountries() { return this._ActiveCountries; }

		// Setters
		public void setActiveCountries(JSONArray activeCountries) { this._ActiveCountries = activeCountries; }

		/**
		 * Constructor
		 */
		public CfgAuth(JSONObject jConfig) {
			super();

			this._ActiveCountries = new JSONArray();

			try {

				JSONObject jAuth = jConfig.getJSONObject("auth");

				try {
					setActiveCountries(jAuth.getJSONArray("activeCountries"));
				} catch (Exception e) { }

			} catch (Exception e ) { }
		}

		/**
		 * Get Active Country Codes
		 */
		public Vector<String> getActiveCountryCodes() {

			Vector<String> vecCodes = new Vector<String>();

			for (int i=0; i<this.getActiveCountries().length(); i++)
				vecCodes.add(this.getActiveCountries().getString(i));

			Collections.sort(vecCodes);

			return(vecCodes);
		}

		/**
		 * Get a combo containing all the countries this user can manage
		 */
		public String getActiveCountriesCombo(String Default) {

			Database DB = null;
			String rc = "";

			try {

				DB = new Database();
				GeoIso GEO = new GeoIso(DB.getConnection());
				Vector<String> vecCountries = getActiveCountryCodes();

				for (String countryCode : vecCountries) {
					rc +=
						"<option value=\"" + countryCode + "\"" + (countryCode.equals(Default) ? " selected" : "") + ">" +
							"[" + countryCode + "] " + GEO.getFullDesc(countryCode) +
						"</option>"
					;
				}

			} catch (Exception e) { }

			if (DB != null)
				DB.destroy();

			return(rc);
		}

		/**
		 * Class to JSON
		 */
		public JSONObject toJson() {

			JSONObject jObj = new JSONObject();

			jObj.put("activeCountries", getActiveCountries());

			return(jObj);
		}
	}

	/**
	 * CIFP SubClass
	 */
	public class CfgCifp {

		// Fields
		private String		_ApiKey;
		private boolean		_IsAdmin;
		private JSONArray	_ActiveCountries;
		private boolean		_IsExpireMail;
		private boolean		_IsExpiredMail;
		private boolean		_IsExpireSlack;
		private boolean		_IsExpiredSlack;

		// Getters
		public String		getApiKey()				{ return this._ApiKey;			}
		public boolean		isAdmin()				{ return this._IsAdmin;			}
		public JSONArray	getActiveCountries()	{ return this._ActiveCountries; }
		public boolean		isExpireMail()			{ return this._IsExpireMail;	}
		public boolean		isExpiredMail()			{ return this._IsExpiredMail;	}
		public boolean		isExpireSlack()			{ return this._IsExpireSlack;	}
		public boolean		isExpiredSlack()		{ return this._IsExpiredSlack;	}

		// Setters
		public void setApiKey(String apiKey)						{ this._ApiKey = apiKey;					}
		public void setIsAdmin(boolean isAdmin)						{ this._IsAdmin = isAdmin;					}
		public void setActiveCountries(JSONArray activeCountries)	{ this._ActiveCountries = activeCountries;	}
		public void setIsExpireMail(boolean isExpireMail)			{ this._IsExpireMail = isExpireMail;		}
		public void setIsExpiredMail(boolean isExpiredMail)			{ this._IsExpiredMail = isExpiredMail;		}
		public void setIsExpireSlack(boolean isExpireSlack)			{ this._IsExpireSlack = isExpireSlack;		}
		public void setIsExpiredSlack(boolean isExpiredSlack)		{ this._IsExpiredSlack = isExpiredSlack;	}

		/**
		 * Constructor
		 */
		public CfgCifp(JSONObject jConfig) {
			super();

			this._ApiKey = SysTool.getEmptyUuidValue();
			this._IsAdmin = false;
			this._ActiveCountries = new JSONArray();
			this._IsExpireMail = false;
			this._IsExpiredMail = false;
			this._IsExpireSlack = false;
			this._IsExpiredSlack = false;

			try {

				JSONObject jCifp = jConfig.getJSONObject("cifp");

				try { setApiKey(jCifp.getString("apiKey")); } catch (Exception e) { }
				try { setIsAdmin(jCifp.getBoolean("isAdmin")); } catch (Exception e) { }
				try { setActiveCountries(jCifp.getJSONArray("activeCountries")); } catch (Exception e) { }
				try { setIsExpireMail(jCifp.getBoolean("isExpireMail")); } catch (Exception e) { }
				try { setIsExpiredMail(jCifp.getBoolean("isExpiredMail")); } catch (Exception e) { }
				try { setIsExpireSlack(jCifp.getBoolean("isExpireSlack")); } catch (Exception e) { }
				try { setIsExpiredSlack(jCifp.getBoolean("isExpiredSlack")); } catch (Exception e) { }

			} catch (Exception e ) { }
		}

		/**
		 * Get Active Country Codes
		 */
		public Vector<String> getActiveCountryCodes() {

			Vector<String> vecCodes = new Vector<String>();

			for (int i=0; i<this.getActiveCountries().length(); i++)
				vecCodes.add(this.getActiveCountries().getString(i));

			Collections.sort(vecCodes);

			return(vecCodes);
		}

		/**
		 * Class to JSON
		 */
		public JSONObject toJson() {

			JSONObject jObj = new JSONObject();

			jObj.put("apiKey", getApiKey());
			jObj.put("isAdmin", isAdmin());
			jObj.put("activeCountries", getActiveCountries());
			jObj.put("isExpireMail", isExpireMail());
			jObj.put("isExpiredMail", isExpiredMail());
			jObj.put("isExpireSlack", isExpireSlack());
			jObj.put("isExpiredSlack", isExpiredSlack());

			return(jObj);
		}
	}

	/**
	 * CMON SubClass
	 */
	public class CfgCmon {

		// Fields
		private int _Role;

		// Getters
		public int getRole() { return this._Role; }

		// Setters
		public void setRole(int role) { this._Role = role; }

		/**
		 * Constructor
		 */
		public CfgCmon(JSONObject jConfig) {
			super();

			this._Role = 0;

			try {

				JSONObject jCmon = jConfig.getJSONObject("cmon");

				try {
					setRole(jCmon.getInt("role"));
				} catch (Exception e) { }

			} catch (Exception e ) { }
		}

		/**
		 * Is Role enabled?
		 */
		public boolean isEnabled(CmonRole requestedRole) {
			return((this.getRole() & requestedRole.getValue()) == requestedRole.getValue());
		}

		/**
		 * Class to JSON
		 */
		public JSONObject toJson() {

			JSONObject jObj = new JSONObject();

			jObj.put("role", getRole());

			return(jObj);
		}
	}

	/**
	 * CODE SubClass
	 */
	public class CfgCode {

		// Fields
		private int _Role;

		// Getters
		public int getRole() { return this._Role; }

		// Setters
		public void setRole(int role) { this._Role = role; }

		/**
		 * Constructor
		 */
		public CfgCode(JSONObject jConfig) {
			super();

			this._Role = 0;

			try {

				JSONObject jCode = jConfig.getJSONObject("code");

				try {
					setRole(jCode.getInt("role"));
				} catch (Exception e) { }

			} catch (Exception e ) { }
		}

		/**
		 * is Role enabled?
		 */
		public boolean isEnabled(CodeRole requestedRole) {
			return((this.getRole() & requestedRole.getValue()) == requestedRole.getValue());
		}

		/**
		 * Class to JSON
		 */
		public JSONObject toJson() {

			JSONObject jObj = new JSONObject();

			jObj.put("role", getRole());

			return(jObj);
		}
	}

	/**
	 * UREQ SubClass
	 */
	public class CfgUreq {

		// Fields
		private boolean		_WholeCountry;
		private JSONArray	_ActiveCountries;
		private JSONArray	_ActiveStatuses;
		private Mail		_Mail;
		private Slack		_Slack;
		private Rss			_Rss;
		private Recheck		_Recheck;

		// Getters
		public boolean		getWholeCountry()		{ return this._WholeCountry;	}
		public JSONArray	getActiveCountries()	{ return this._ActiveCountries; }
		public JSONArray	getActiveStatuses()		{ return this._ActiveStatuses;	}
		public Mail			getMail()				{ return this._Mail;			}
		public Slack		getSlack()				{ return this._Slack;			}
		public Rss			getRss()				{ return this._Rss;				}
		public Recheck		getRecheck()			{ return this._Recheck;			}

		// Setters
		public void setWholeCountry(boolean wholeCountry)			{ this._WholeCountry = wholeCountry;		}
		public void setActiveCountries(JSONArray activeCountries)	{ this._ActiveCountries = activeCountries;	}
		public void setActiveStatuses(JSONArray activeStatuses)		{ this._ActiveStatuses = activeStatuses;	}
		public void setMail(Mail mail)								{ this._Mail = mail;						}
		public void setSlack(Slack slack)							{ this._Slack = slack;						}
		public void setRss(Rss rss)									{ this._Rss = rss;							}
		public void setRecheck(Recheck recheck)						{ this._Recheck = recheck;					}

		/**
		 * Constructor
		 */
		public CfgUreq(JSONObject jConfig) {
			super();

			this._WholeCountry		= false;
			this._ActiveCountries	= new JSONArray();
			this._ActiveStatuses	= new JSONArray();
			this._Mail				= new Mail(new JSONObject());
			this._Slack				= new Slack(new JSONObject());
			this._Rss				= new Rss(new JSONObject());
			this._Recheck			= new Recheck(new JSONObject());

			try {

				JSONObject jUreq = jConfig.getJSONObject("ureq");

				try { setWholeCountry(jUreq.getBoolean("wholeCountry")); } catch (Exception e) { }
				try { setActiveCountries(jUreq.getJSONArray("activeCountries")); } catch (Exception e) { }
				try { setActiveStatuses(jUreq.getJSONArray("activeStatuses")); } catch (Exception e) { }
				try { setMail(new Mail(jUreq.getJSONObject("mail"))); } catch (Exception e) { }
				try { setSlack(new Slack(jUreq.getJSONObject("slack"))); } catch (Exception e) { }
				try { setRss(new Rss(jUreq.getJSONObject("rss"))); } catch (Exception e) { }
				try { setRecheck(new Recheck(jUreq.getJSONObject("recheck"))); } catch (Exception e) { }

			} catch (Exception e ) { }
		}

		/**
		 * Get Active Country Codes
		 */
		public Vector<String> getActiveCountryCodes() {

			Vector<String> vecCodes = new Vector<String>();

			for (int i=0; i<this.getActiveCountries().length(); i++)
				vecCodes.add(this.getActiveCountries().getString(i));

			Collections.sort(vecCodes);

			return(vecCodes);
		}

		/**
		 * Class to JSON
		 */
		public JSONObject toJson() {

			JSONObject jObj = new JSONObject();

			jObj.put("wholeCountry", getWholeCountry());
			jObj.put("activeCountries", getActiveCountries());
			jObj.put("activeStatuses", getActiveStatuses());
			jObj.put("mail", getMail().toJson());
			jObj.put("slack", getSlack().toJson());
			jObj.put("rss", getRss().toJson());
			jObj.put("recheck", getRecheck().toJson());

			return(jObj);
		}

		/**
		 * UREQ.MAIL SubClass 
		 */
		public class Mail {

			// Fields
			private boolean			_IsEnabled;
			private EmailFrequency	_Frequency;
			private boolean			_OnCreate;
			private boolean			_OnModify;
			private boolean			_OnModifyMine;
			private boolean			_OnClose;

			// Getters
			public boolean			isEnabled()			{ return this._IsEnabled;		}
			public EmailFrequency	getFrequency()		{ return this._Frequency;		}
			public boolean			isOnCreate()		{ return this._OnCreate;		}
			public boolean			isOnModify()		{ return this._OnModify;		}
			public boolean			isOnModifyMine()	{ return this._OnModifyMine;	}
			public boolean			isOnClose()			{ return this._OnClose;			}

			// Setters
			public void setEnabled(boolean enabled)				{ this._IsEnabled = enabled;			}
			public void setFrequency(EmailFrequency frequency)	{ this._Frequency = frequency;			}
			public void setOnCreate(boolean onCreate)			{ this._OnCreate = onCreate;			}
			public void setOnModify(boolean onModify)			{ this._OnModify = onModify;			}
			public void setOnModifyMine(boolean onModifyMine)	{ this._OnModifyMine = onModifyMine;	}
			public void setOnClose(boolean onClose)				{ this._OnClose = onClose;				}

			/**
			 * Constructor
			 */
			public Mail(JSONObject jMail) {
				super();

				this._IsEnabled		= false;
				this._Frequency		= EmailFrequency.UNK;
				this._OnCreate		= false;
				this._OnModify		= false;
				this._OnModifyMine	= false;
				this._OnClose		= false;

				try { setEnabled(jMail.getBoolean("enabled")); } catch (Exception e) { }
				try { setFrequency(EmailFrequency.getByCode(jMail.getString("frequency"))); } catch (Exception e) { }
				try { setOnCreate(jMail.getBoolean("onCreate")); } catch (Exception e) { }
				try { setOnModify(jMail.getBoolean("onModify")); } catch (Exception e) { }
				try { setOnModifyMine(jMail.getBoolean("onModifyMine")); } catch (Exception e) { }
				try { setOnClose(jMail.getBoolean("onClose")); } catch (Exception e) { }
			}

			/**
			 * Class to JSON
			 */
			public JSONObject toJson() {

				JSONObject jObj = new JSONObject();

				jObj.put("enabled", isEnabled());
				jObj.put("frequency", getFrequency().getCode());
				jObj.put("onCreate", isOnCreate());
				jObj.put("onModify", isOnModify());
				jObj.put("onModifyMine", isOnModifyMine());
				jObj.put("onClose", isOnClose());

				return(jObj);
			}
		}

		/**
		 * UREQ.SLACK SubClass
		 */
		public class Slack {

			// Fields
			private boolean _IsEnabled;

			// Getters
			public boolean isEnabled() { return this._IsEnabled; }

			// Setters
			public void setEnabled(boolean enabled) { this._IsEnabled = enabled; }

			/**
			 * Constructor
			 */
			public Slack(JSONObject jSlack) {
				super();

				this._IsEnabled = false;

				try {
					setEnabled(jSlack.getBoolean("enabled"));
				} catch (Exception e) { }
			}

			/**
			 * Class to JSON
			 */
			public JSONObject toJson() {

				JSONObject jObj = new JSONObject();

				jObj.put("enabled", isEnabled());

				return(jObj);
			}
		}

		/**
		 * UREQ.RSS SubClass
		 */
		public class Rss {

			// Fields
			private int _MaxEntries;

			// Getters
			public int getMaxEntries() { return this._MaxEntries; }

			// Setters
			public void setMaxEntries(int maxEntries) { this._MaxEntries = maxEntries; }

			/**
			 * Constructor
			 */
			public Rss(JSONObject jRss) {
				super();

				this._MaxEntries = 0;

				try {
					setMaxEntries(jRss.getInt("maxEntries"));
				} catch (Exception e) { }
			}

			/**
			 * Class to JSON
			 */
			public JSONObject toJson() {

				JSONObject jObj = new JSONObject();

				jObj.put("maxEntries", getMaxEntries());

				return(jObj);
			}
		}

		/**
		 * UREQ.RECHECK SubClass
		 */
		public class Recheck {

			// Fields
			private boolean	_OnlyMine;
			private int		_Days;

			// Getters
			public boolean	isOnlyMine()	{ return this._OnlyMine;	}
			public int		getDays() 		{ return this._Days;		}

			// Setters
			public void setOnlyMine(boolean onlyMine)	{ this._OnlyMine = onlyMine;	}
			public void setDays(int days)				{ this._Days = days;			}

			/**
			 * Constructor
			 */
			public Recheck(JSONObject jRecheck) {
				super();

				this._OnlyMine = false;
				this._Days = 0;

				try {
					setOnlyMine(jRecheck.getBoolean("onlyMine"));
					setDays(jRecheck.getInt("days"));
				} catch (Exception e) { }
			}

			/**
			 * Class to JSON
			 */
			public JSONObject toJson() {

				JSONObject jObj = new JSONObject();

				jObj.put("onlyMine", isOnlyMine());
				jObj.put("days", getDays());

				return(jObj);
			}
		}

	}
}
