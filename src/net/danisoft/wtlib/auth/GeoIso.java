////////////////////////////////////////////////////////////////////////////////////////////////////
//
// GeoIso.java
//
// DB Interface for the ISO Geo Table - This class replaces the old GeoRef.java
//
// First Release: Apr 2025 by Fulvio Mondini (https://danisoft.software/)
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

/**
 * DB Interface for the Geo Table
 */
public class GeoIso {

	private final static String TBL_NAME = "AUTH_geo";
	
	private static final String COMMON_SELECT_STATEMENT =
		"SELECT " +
			"GEO_Code AS ThisCode, GEO_Iso3 AS ThisIso3, GEO_Iso2 AS ThisIso2, GEO_Name AS ThisName, " +
			"(SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = LEFT(ThisCode, 3) LIMIT 1) AS Country, " +
			"IF (LENGTH(GEO_Code) >= 7, (SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = LEFT(ThisCode, 7) LIMIT 1), '') AS State, " +
			"IF (LENGTH(GEO_Code) = 10, (SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = ThisCode LIMIT 1), '') AS District, " +
			"CONCAT( " +
				"(SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = LEFT(ThisCode, 3) LIMIT 1), " +
				"IF (LENGTH(GEO_Code) >= 7, CONCAT(' :: ', (SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = LEFT(ThisCode, 7) LIMIT 1)), ''), " +
				"IF (LENGTH(GEO_Code) = 10, CONCAT(' :: ', (SELECT GEO_Name FROM " + TBL_NAME + " WHERE GEO_Code = ThisCode LIMIT 1)) , '') " +
			") AS Location " +
		"FROM " + TBL_NAME
	;

	public static String getTblName() { return TBL_NAME; }

	private Connection cn;

	/**
	 * Constructor
	 */
	public GeoIso(Connection conn) {
		this.cn = conn;
	}

	/**
	 * GeoRef Data
	 */
	public class Data {

		// Fields
		private String _Code;
		private String _Desc;
		private String _Iso3;
		private String _Iso2;
		private String _Country;
		private String _State;
		private String _District;
		private String _Location;

		// Getters
		public String getCode()		{ return this._Code;		}
		public String getDesc()		{ return this._Desc;		}
		public String getIso3()		{ return this._Iso3;		}
		public String getIso2()		{ return this._Iso2;		}
		public String getCountry()	{ return this._Country;		}
		public String getState()	{ return this._State;		}
		public String getDistrict()	{ return this._District;	}
		public String getLocation()	{ return this._Location;	}

		// Setters
		public void setCode(String code)			{ this._Code = code;			}
		public void setDesc(String desc)			{ this._Desc = desc;			}
		public void setIso3(String iso3)			{ this._Iso3 = iso3;			}
		public void setIso2(String iso2)			{ this._Iso2 = iso2;			}
		public void setCountry(String country)		{ this._Country = country;		}
		public void setState(String state)			{ this._State = state;			}
		public void setDistrict(String district)	{ this._District = district;	}
		public void setLocation(String location)	{ this._Location = location;	}

		/**
		 * Constructor
		 */
		public Data() {
			super();

			this._Code = "";
			this._Desc = "";
			this._Iso3 = "";
			this._Iso2 = "";
			this._Country = "";
			this._State = "";
			this._District = "";
			this._Location = "";
		}

	}

	/**
	 * Read by ISO3 Code
	 * @param iso3Code ISO Code (3 chars)
	 * @throws Exception
	 */
	public Data Read(String iso3Code) throws Exception {
		return(
			_read_geo_by_code(iso3Code)
		);
	}

	/**
	 * Read by ISO2 Code
	 * @param code ISO Code (2 chars)
	 * @throws Exception
	 */
	public Data ReadByIso2(String code) throws Exception {

		String iso3Code = "";

		Statement st = this.cn.createStatement();
		ResultSet rs = st.executeQuery("SELECT GEO_Iso3 FROM " + TBL_NAME + " WHERE GEO_Iso2 = '" + code + "'");

		if (rs.next())
			iso3Code = rs.getString("GEO_Iso3");

		rs.close();
		st.close();

		return(
			_read_geo_by_code(iso3Code)
		);
	}

	/**
	 * Get all states for a country
	 * @return Vector<GeoRef.Data> of results
	 * @throws Exception
	 */
	public Vector<Data> getStates(String country) throws Exception {
		return(
			_fill_geo_vector(
				"WHERE LEFT(GEO_Code, 4) = '" + country + ":' AND LENGTH(GEO_Code) = 7",
				"ORDER BY GEO_Name"
			)
		);
	}

	/**
	 * Create a COUNTRY Combo
	 * @param defaultCountryCode The selected country, or NULL for nothing
	 * @throws Exception
	 */
	public String getCountryCombo(String defaultCountryCode) {

		String rc = "";

		try {

			Vector<Data> vecGeoData = _fill_geo_vector(
				"WHERE NOT (GEO_Code LIKE '%:%')",
				"ORDER BY GEO_Name"
			);

			for (Data geoData : vecGeoData) {
				rc += "<option " +
					"value=\"" + geoData.getCode() + "\"" +
						(defaultCountryCode == null
							? ""
							: (geoData.getCode().equals(defaultCountryCode)
								? " selected"
								: ""
							)
						) +
					">" +
					"[" + geoData.getCode() + "] " + geoData.getDesc() +
				"</option>";
			}

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Create a STATE Combo for a given country
	 * @throws Exception
	 */
	public String getStateCombo(String country, String defaultStateCode) {

		String rc = "";

		try {

			Vector<Data> vecData = _fill_geo_vector(
				"WHERE (GEO_Code LIKE '%" + country + ":%') AND NOT (GEO_Code LIKE '%.%')",
				"ORDER BY GEO_Name"
			);

			for (Data data : vecData)
				rc += "<option " +
					"value=\"" + data.getCode() + "\"" + (data.getCode().equals(defaultStateCode) ? " selected" : "") + ">" +
					"[" + data.getCode() + "] " + data.getDesc() +
				"</option>";

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Create a DISTRICT Combo for a given state
	 * @throws Exception
	 */
	public String getDistrictCombo(String state, String defaultDistrictCode) {

		String rc = "";

		try {

			Vector<Data> vecData = _fill_geo_vector(
				"WHERE (GEO_Code LIKE '%" + state + ".%')",
				"ORDER BY GEO_Name"
			);

			for (Data data : vecData)
				rc += "<option " +
					"value=\"" + data.getCode() + "\"" + (data.getCode().equals(defaultDistrictCode) ? " selected" : "") + ">" +
					"[" + data.getCode() + "] " + data.getDesc() +
				"</option>";

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Create a STATE+DISTRICT Combo for a given Country
	 * @throws Exception
	 */
	public String getStateDistrictCombo(String country, String defaultCode) {

		String rc = "";

		try {

			Vector<Data> vecData = _fill_geo_vector(
				"WHERE LEFT(GEO_Code, 3) = '" + country + "'",
				"ORDER BY GEO_Code"
			);

			for (Data data : vecData) {

				if (data.getCode().length() == 7)
					rc += "<optgroup label=\"" + data.getLocation() + "\">";
				else
					rc += "<option " +
						"value=\"" + data.getCode() + "\"" + (data.getCode().equals(defaultCode) ? " selected" : "") + ">" +
						data.getLocation() +
					"</option>";
			}

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Get GeoReference description
	 * @throws Exception
	 */
	public String getDesc(String code) throws Exception {
		return(
			_read_geo_by_code(code).getDesc()
		);
	}

	/**
	 * Get GeoReference full description<br>
	 * Format: "<tt>Country :: State :: District</tt>"
	 * @throws Exception
	 */
	public String getFullDesc(String code) throws Exception {
		return(
			_read_geo_by_code(code).getLocation()
		);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// +++ PRIVATE +++
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Read GEO Record by GEO_Code
	 * @return <GeoIso.Data> object 
	 */
	private Data _read_geo_by_code(String code) {

		Data data = new Data();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(COMMON_SELECT_STATEMENT + " WHERE GEO_Code = '" + code + "';");

			if (rs.next()) {

				data.setCode(rs.getString("ThisCode"));
				data.setDesc(rs.getString("ThisName"));
				data.setIso3(rs.getString("ThisIso3"));
				data.setIso2(rs.getString("ThisIso2"));
				data.setCountry(rs.getString("Country"));
				data.setState(rs.getString("State"));
				data.setDistrict(rs.getString("District"));
				data.setLocation(rs.getString("Location"));
			}

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("_read_geo_by_code(): " + e.toString());
		}

		return(data);
	}

	/**
	 * Read GEO Records based on given query
	 * @return Vector<GeoRef.Data> of results
	 * @throws Exception
	 */
	private Vector<Data> _fill_geo_vector(String where, String order) {

		Data data;
		Vector<Data> vecData = new Vector<Data>();

		try {

			Statement st = this.cn.createStatement();
			ResultSet rs = st.executeQuery(COMMON_SELECT_STATEMENT + " " + where + " " + order + ";");

			while (rs.next()) {

				data = new Data();

				data.setCode(rs.getString("ThisCode"));
				data.setDesc(rs.getString("ThisName"));
				data.setIso3(rs.getString("ThisIso3"));
				data.setIso2(rs.getString("ThisIso2"));
				data.setCountry(rs.getString("Country"));
				data.setState(rs.getString("State"));
				data.setDistrict(rs.getString("District"));
				data.setLocation(rs.getString("Location"));

				vecData.add(data);
			}

			rs.close();
			st.close();

		} catch (Exception e) {
			System.err.println("_fill_geo_vector(): " + e.toString());
		}

		return(vecData);
	}

}
