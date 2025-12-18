package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserAction {
	private Connection con;
	private String userId;
	private PreparedStatement pstmt;
	private Scanner s = new Scanner(System.in);

	public UserAction(Connection con, String userId) {
		this.con = con;
		this.userId = userId;
	}

	public void followUser() {
		System.out.println("Type in the ID of the user to follow/unfollow: ");
		String targetUserId = s.nextLine();

		if (targetUserId.equals(userId)) {
			System.out.println("Can't follow/unfollow yourself!");
			return;
		}

		try {
			// Check if the user is already following the target user
			String checkFollowQuery = "SELECT following_entity_id FROM following_list WHERE my_id = ? AND followee_id = ?";
			pstmt = con.prepareStatement(checkFollowQuery);
			pstmt.setString(1, userId); // Logged-in user's ID
			pstmt.setString(2, targetUserId); // Target user ID

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If a record is found, the user is already following the target user, so
				// proceed to unfollow
				System.out.println("You are already following " + targetUserId + ". Unfollowing...");

				// Unfollow: Delete from following_list
				String unfollowQuery = "DELETE FROM following_list WHERE my_id = ? AND followee_id = ?";
				try (PreparedStatement unfollowStmt = con.prepareStatement(unfollowQuery)) {
					unfollowStmt.setString(1, userId);
					unfollowStmt.setString(2, targetUserId);
					unfollowStmt.executeUpdate();
				}

				// Unfollow: Delete from follower_list
				String removeFollowerQuery = "DELETE FROM follower_list WHERE my_id = ? AND follower_id = ?";
				try (PreparedStatement removeFollowerStmt = con.prepareStatement(removeFollowerQuery)) {
					removeFollowerStmt.setString(1, targetUserId);
					removeFollowerStmt.setString(2, userId);
					removeFollowerStmt.executeUpdate();
				}

				// Decrement following and follower counts
				updateFollowingCount(userId, -1);
				updateFollowerCount(targetUserId, -1);

				System.out.println("You have unfollowed " + targetUserId + ".");
			} else {
				// If no record is found, proceed to follow
				System.out.println("You are not following " + targetUserId + ". Following...");

				// Follow: Insert into following_list
				String followQuery = "INSERT INTO following_list (following_entity_id, my_id, followee_id) VALUES (?, ?, ?)";
				String followingId = generateFollowingId(); // Generate a unique following ID
				try (PreparedStatement followStmt = con.prepareStatement(followQuery)) {
					followStmt.setString(1, followingId);
					followStmt.setString(2, userId); // Logged-in user's ID
					followStmt.setString(3, targetUserId); // Target user ID
					followStmt.executeUpdate();
				}

				// Follow: Insert into follower_list
				String followerQuery = "INSERT INTO follower_list (follower_entity_id, my_id, follower_id) VALUES (?, ?, ?)";
				String followerId = generateFollowerId(); // Generate a unique follower ID
				try (PreparedStatement followerStmt = con.prepareStatement(followerQuery)) {
					followerStmt.setString(1, followerId);
					followerStmt.setString(2, targetUserId); // Target user as "my_id"
					followerStmt.setString(3, userId); // Logged-in user as "follower_id"
					followerStmt.executeUpdate();
				}

				// Increment following and follower counts
				updateFollowingCount(userId, 1);
				updateFollowerCount(targetUserId, 1);

				System.out.println("You are now following " + targetUserId + ".");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		} finally {
			// Close resources
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Error closing resources: " + e.getMessage());
			}
		}
	}

	// Method to update the following count
	private void updateFollowingCount(String userId, int countChange) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE user SET following_count = following_count + ? WHERE user_id = ?")) {
			pstmt.setInt(1, countChange);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error updating following count: " + e.getMessage());
		}
	}

	// Method to update the follower count
	private void updateFollowerCount(String userId, int countChange) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE user SET follower_count = follower_count + ? WHERE user_id = ?")) {
			pstmt.setInt(1, countChange);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error updating follower count: " + e.getMessage());
		}
	}



	// Sample methods to generate unique IDs for following and follower lists
	private String generateFollowingId() {
		return "FLW-" + System.currentTimeMillis(); // Simple example based on timestamp
	}

	private String generateFollowerId() {
		return "FRW-" + System.currentTimeMillis(); // Simple example based on timestamp
	}


	// Method to bookmark a post
	public void bookmarkPost(String postId) {
		try {
			String bookmarkQuery = "INSERT INTO bookmarks (user_id, post_id) VALUES (?, ?)";
			pstmt = con.prepareStatement(bookmarkQuery);
			pstmt.setString(1, userId);
			pstmt.setString(2, postId);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Post " + postId + " bookmarked successfully.");
			} else {
				System.out.println("Failed to bookmark post.");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// update user's title holdings
	public void updateUserTitles(String userId) {
		try {
			// Get user's post, comment, like, and follower counts
			String query = "SELECT " + "(SELECT COUNT(*) FROM post WHERE writer_id = ?) AS post_count, "
					+ "(SELECT COUNT(*) FROM comment WHERE writer_id = ?) AS comment_count, "
					+ "(SELECT SUM(post_num_of_like) FROM post WHERE writer_id = ?) + "
					+ "(SELECT SUM(comment_num_of_likes) FROM comment WHERE writer_id = ?) AS likes_received, "
					+ "(SELECT following_count FROM user WHERE user_id = ?) AS follower_count";

			try (PreparedStatement pstmt = con.prepareStatement(query)) {
				pstmt.setString(1, userId);
				pstmt.setString(2, userId);
				pstmt.setString(3, userId);
				pstmt.setString(4, userId);
				pstmt.setString(5, userId);

				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int postCount = rs.getInt("post_count");
					int commentCount = rs.getInt("comment_count");
					int likesReceived = rs.getInt("likes_received");
					int followerCount = rs.getInt("follower_count");

					// Fetch all eligible titles from the title table
					String titleQuery = "SELECT title_id FROM title WHERE "
							+ "(post_threshold <= ? AND post_threshold IS NOT NULL) OR "
							+ "(comment_threshold <= ? AND comment_threshold IS NOT NULL) OR "
							+ "(like_threshold <= ? AND like_threshold IS NOT NULL) OR "
							+ "(follower_threshold <= ? AND follower_threshold IS NOT NULL)";
					try (PreparedStatement titleStmt = con.prepareStatement(titleQuery)) {
						titleStmt.setInt(1, postCount);
						titleStmt.setInt(2, commentCount);
						titleStmt.setInt(3, likesReceived);
						titleStmt.setInt(4, followerCount);

						ResultSet titleRs = titleStmt.executeQuery();

						while (titleRs.next()) {
							String titleId = titleRs.getString("title_id");

							// Check if the title is already in the user_title table
							String checkQuery = "SELECT * FROM user_to_title WHERE user_id = ? AND title_id = ?";
							try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
								checkStmt.setString(1, userId);
								checkStmt.setString(2, titleId);

								ResultSet checkRs = checkStmt.executeQuery();
								if (!checkRs.next()) {
									// Insert the new title into user_title
									String insertQuery = "INSERT INTO user_to_title (user_id, title_id) VALUES (?, ?)";
									try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
										insertStmt.setString(1, userId);
										insertStmt.setString(2, titleId);
										insertStmt.executeUpdate();
									}
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}


	// Update first name
	public void updateFirstName(String firstName) {
		updateUserInfo("first_name", firstName);
	}

	// Update last name
	public void updateLastName(String lastName) {
		updateUserInfo("last_name", lastName);
	}

	// Update birthdate
	public void updateBirthdate(String birthdate) {
		updateUserInfo("birthdate", birthdate);
	}

	// Update phone number
	public void updatePhoneNumber(String phoneNumber) {
		updateUserInfo("phone_number", phoneNumber);
	}

	// Update address
	public void updateAddress(String address) {
		updateUserInfo("address", address);
	}

	// Helper method to update a user's information
	private void updateUserInfo(String column, String newValue) {
		String updateQuery = "UPDATE user SET " + column + " = ? WHERE user_id = ?";

		try {
			pstmt = con.prepareStatement(updateQuery);
			pstmt.setString(1, newValue);
			pstmt.setString(2, userId);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println(column + " updated successfully.");
			} else {
				System.out.println("Failed to update " + column + ".");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error while updating " + column + ": " + e.getMessage());
		}
	}
}
