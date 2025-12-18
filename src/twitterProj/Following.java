package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Following {
	private final Connection con;
	private final String myId; // ID of the logged-in user (the follower)

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public Following(Connection con, String myId) {
		this.con = con;
		this.myId = myId;
	}

	public void viewFollowingUsers() {
		try (PreparedStatement pstmt = con
				.prepareStatement("SELECT followee_id FROM following_list WHERE my_id = ?")) {
			pstmt.setString(1, myId);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("Your followees:");
			while (rs.next()) {
				System.out.println("followee ID: " + rs.getString("followee_id"));
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}
}
