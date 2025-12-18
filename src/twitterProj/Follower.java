package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Follower {
	private final Connection con;
	private final String myId; // ID of the logged-in user

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public Follower(Connection con, String myId) {
		this.con = con;
		this.myId = myId;
	}

	// Method to view followers of the logged-in user
	public void viewFollowers() {
		try (PreparedStatement pstmt = con
				.prepareStatement("SELECT follower_id FROM follower_list WHERE my_id = ?")) {
			pstmt.setString(1, myId);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("Your followers:");
			while (rs.next()) {
				System.out.println("Follower ID: " + rs.getString("follower_id"));
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}
}
