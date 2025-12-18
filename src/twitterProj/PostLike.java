package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostLike {
	private final Connection con;
	private final String userId; // ID of the logged-in user

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public PostLike(Connection con, String userId) {
		this.con = con;
		this.userId = userId;
	}

	// Method to like a specific post
	public void likePost(String postId) {
		String likeId = generateLikeId();

		try {
			// Check if the user has already liked this post
			String checkLikeQuery = "SELECT post_like_id FROM post_like WHERE liked_post_id = ? AND liked_user_id = ?";
			try (PreparedStatement checkStmt = con.prepareStatement(checkLikeQuery)) {
				checkStmt.setString(1, postId);
				checkStmt.setString(2, userId);

				try (ResultSet rs = checkStmt.executeQuery()) {
					if (rs.next()) {
						String likeRecordId = rs.getString("post_like_id");
						unlikePost(postId, likeRecordId);
						return;
					}
				}
			}

			// If not already liked, proceed to like the post
			String insertLikeQuery = "INSERT INTO post_like (post_like_id, liked_post_id, liked_user_id) VALUES (?, ?, ?)";
			try (PreparedStatement pstmt = con.prepareStatement(insertLikeQuery)) {
				pstmt.setString(1, likeId);
				pstmt.setString(2, postId);
				pstmt.setString(3, userId);

				int rowsInserted = pstmt.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Post " + postId + " liked successfully.");
					incrementPostLikeCount(postId);
				} else {
					System.out.println("Failed to like the post.");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Method to unlike a post and decrement the like count
	private void unlikePost(String postId, String likeRecordId) {
		String deleteLikeQuery = "DELETE FROM post_like WHERE post_like_id = ?";

		try (PreparedStatement pstmt = con.prepareStatement(deleteLikeQuery)) {
			pstmt.setString(1, likeRecordId);
			int rowsDeleted = pstmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Post " + postId + " unliked successfully.");
				decrementPostLikeCount(postId);
			} else {
				System.out.println("Failed to unlike the post.");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Method to increment the like count for a post
	private void incrementPostLikeCount(String postId) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE post SET post_num_of_like = post_num_of_like + 1 WHERE post_id = ?")) {
			pstmt.setString(1, postId);
			pstmt.executeUpdate();
			System.out.println("Incremented like count for post " + postId);
		} catch (SQLException e) {
			System.out.println("Error incrementing like count: " + e.getMessage());
		}
	}

	// Method to decrement the like count for a post
	private void decrementPostLikeCount(String postId) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE post SET post_num_of_like = post_num_of_like - 1 WHERE post_id = ?")) {
			pstmt.setString(1, postId);
			pstmt.executeUpdate();
			System.out.println("Decremented like count for post " + postId);
		} catch (SQLException e) {
			System.out.println("Error decrementing like count: " + e.getMessage());
		}
	}

	// Generate a unique ID for each like
	private String generateLikeId() {
		return "post_like_" + System.currentTimeMillis();
	}
}
