package twitterProj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class query {
	public static void main(String[] args) {
		 Connection con = null;
		 try {
		 Class.forName("com.mysql.cj.jdbc.Driver");
		 String url = "jdbc:mysql://localhost/mydb";
		 String user = "root", passwd = "12345";
		 con = DriverManager.getConnection(url, user, passwd);
		 System.out.println(con);
		 } catch (ClassNotFoundException e) {
		 e.printStackTrace();
		 } catch (SQLException e) {
		 e.printStackTrace();
		 }
		 // database operations ...

		// statement
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			stmt = con.createStatement();
			String update = "update user set phone_number = '303003030', birthdate = '2000-01-01 11:11:11' where user_id = 'first';";
			int count = stmt.executeUpdate(update);
			System.out.println(count);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// result set
		try {
			stmt = con.createStatement();
			String sql = "select user_id, password from user";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String user_id = rs.getString(1);
				if (rs.wasNull()) {
					user_id = "null";
				}
				String password = rs.getString(2);
				if (rs.wasNull()) {
					password = "null";
				}
				System.out.println(user_id + "\t" + password);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		//Prepared statement
		try {
			String psql = "insert into user (user_id, password) values(?, ?)";
			pstmt = con.prepareStatement(psql);

			String user_id = "third", password = "what";
			pstmt.setString(1, user_id);
			pstmt.setString(2, password);
			 
			int count = pstmt.executeUpdate();
			System.out.println(count);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// close connection
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}


