////////////////////////////////////////////////////////////////////////////////////////////////////
//
// TomcatRole.java
//
// DB Interface for the TomCat roles table
//
// First Release: Jan/2013 by Fulvio Mondini (fmondini[at]danisoft.net)
//       Revised: Mar/2025 Ported to Waze wtlib.jar
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package net.danisoft.wtlib.auth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import net.danisoft.wtlib.cifp.CifpRole;
import net.danisoft.wtlib.code.CodeRole;
import net.danisoft.wtlib.ureq.UreqRole;

/**
 * DB Interface for the TomCat roles table
 * @apiNote Also used by Tomcat JDBCRealm
 */
public class TomcatRole {

	private final static String TBL_NAME = "AUTH_roles";

	private Connection cn;

	/**
	 * Constructor
	 */
	public TomcatRole(Connection conn) {
		this.cn = conn;
	}

	/**
	 * Delete ALL Roles for a UserName
	 * @throws Exception
	 */
	public void clearRoles(String userName) throws Exception {

		Statement st = this.cn.createStatement();
		st.execute("DELETE FROM " + TBL_NAME + " WHERE USR_Name = '" + userName + "'");
		st.close();
	}

	/**
	 * Create default roles for a UserName
	 * @throws Exception
	 */
	public void createDefault(String userName) throws Exception {

		clearRoles(userName);

		Insert(userName, AuthRole.EUSER.getCode());
		Insert(userName, CodeRole.DEFAULT_ROLE);
	}

	/**
	 * Insert a new Role for a UserName
	 * @throws Exception
	 */
	public void Insert(String userName, String userRole) throws Exception {

		try {

			Statement st = this.cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery("SELECT * FROM " + TBL_NAME + " LIMIT 1");

			rs.moveToInsertRow();
			
			rs.updateString("USR_Name", userName);
			rs.updateString("USR_Role", userRole);

			rs.insertRow();

			rs.close();
			st.close();

		} catch (Exception e) {
			throw new Exception("TomcatRole.Insert('" + userName + "', '" + userRole + "'): " + e.toString());
		}
	}

	/**
	 * Check if a UserName have the specified AUTH Role
	 */
	public boolean haveRoleAUTH(String UserName, AuthRole authRole) {

		boolean rc = false;

		try {

			Statement st = this.cn.createStatement();

			ResultSet rs = st.executeQuery(
				"SELECT * " +
				"FROM " + TBL_NAME + " " +
				"WHERE (" +
					"USR_Name = '" + UserName + "' AND " +
					"USR_Role = '" + authRole.getCode() + "'" +
				");"
			);

			rc = rs.next();

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Check if a UserName have the specified UREQ Role
	 * @return In case of (ureqRole.Code().equals(UreqRole.GUEST.Code())) returns always TRUE
	 */
	public boolean haveRoleUREQ(String UserName, UreqRole ureqRole) {

		boolean rc = false;

		try {

			Statement st = this.cn.createStatement();

			ResultSet rs = st.executeQuery(
				"SELECT * " +
				"FROM " + TBL_NAME + " " +
				"WHERE (" +
					"USR_Name = '" + UserName + "' AND " +
					"USR_Role = '" + ureqRole.getCode() + "'" +
				");"
			);

			rc = rs.next();

			if (ureqRole.getCode().equals(UreqRole.GUEST.getCode()))
				rc = true; // Not found but requested a "" (guest) auth

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Check if a UserName have the specified CIFP Role
	 */
	public boolean haveRoleCIFP(String UserName, CifpRole cifpRole) {

		boolean rc = false;

		try {

			Statement st = this.cn.createStatement();

			ResultSet rs = st.executeQuery(
				"SELECT * " +
				"FROM " + TBL_NAME + " " +
				"WHERE (" +
					"USR_Name = '" + UserName + "' AND " +
					"USR_Role = '" + cifpRole.getCode() + "'" +
				");"
			);

			rc = rs.next();

			rs.close();
			st.close();

		} catch (Exception e) { }

		return(rc);
	}

	/**
	 * Get a vector of all active countries
	 * @apiNote Active &rarr; with a user with <tt>AUTH_Role.ADMIN</tt> or role <tt>AUTH_Role.STAFF</tt>
	 */
	public Vector<String> getActiveCountries() throws Exception {

		int i;
		Vector<String> vecRaw = new Vector<>();
		Vector<String> vecSorted = new Vector<>();
		Set<String> hashSet = new HashSet<>();

		String Query =
			"SELECT DISTINCT USR_Country " +
			"FROM " + TBL_NAME + " " +
			"LEFT JOIN " + User.getTblName() + " ON " +
				TBL_NAME + ".USR_Name = " + User.getTblName() + ".USR_Name " +
			"WHERE (" +
				"USR_Country <> '' AND (" +
					"USR_Role = '" + AuthRole.ADMIN.getCode() + "' OR " +
					"USR_Role = '" + AuthRole.STAFF + "'" +
				")" +
			")";

		Statement st = this.cn.createStatement();
		ResultSet rs = st.executeQuery(Query);

		while (rs.next()) {

			String couList[] = rs.getString("USR_Country").split(User.getHomeCountriesSeparator());

			for (i=0; i<couList.length; i++)
				vecRaw.addElement(couList[i]);
		}

		rs.close();
		st.close();

		// Delete dupes and sort vector

		hashSet.addAll(vecRaw);

		for (String ctryCode : hashSet)
			vecSorted.add(ctryCode);

		Collections.sort(vecSorted);

		return(vecSorted);
	}

	/**
	 * Get a combobox of all active countries
	 */
	public String getActiveCountriesCombo(String Default) throws Exception {

		GeoIso GEO = new GeoIso(this.cn);
		String rc = "<option selected value=\"\">&laquo; Please select a Country &raquo;</option>";

		Vector<String> vecCtry = getActiveCountries();

		for (int idx=0; idx<vecCtry.size(); idx++)
			rc +=
				"<option value=\"" + vecCtry.get(idx) + "\"" + (vecCtry.get(idx).equals(Default) ? " selected" : "") + ">" +
					"[" + vecCtry.get(idx) + "] " + GEO.getFullDesc(vecCtry.get(idx)) +
				"</option>"
			;

		return(rc);
	}

}
