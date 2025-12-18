package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentLike {
	private final Connection con;
	private final String userId; // ID of the logged-in user

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public CommentLike(Connection con, String userId) {
		this.con = con;
		this.userId = userId;
	}

	// Method to like a specific comment
	public void likeComment(String commentId) {
		String likeId = generateLikeId();

		try {
			// Check if the user has already liked this comment
			String checkLikeQuery = "SELECT comment_like_id FROM comment_like WHERE liked_comment_id = ? AND liked_user_id = ?";
			try (PreparedStatement checkStmt = con.prepareStatement(checkLikeQuery)) {
				checkStmt.setString(1, commentId);
				checkStmt.setString(2, userId);

				try (ResultSet rs = checkStmt.executeQuery()) {
					if (rs.next()) {
						String likeRecordId = rs.getString("comment_like_id");
						unlikeComment(commentId, likeRecordId);
						return;
					}
				}
			}

			// If not already liked, proceed to like the comment
			String insertLikeQuery = "INSERT INTO comment_like (comment_like_id, liked_comment_id, liked_user_id) VALUES (?, ?, ?)";
			try (PreparedStatement pstmt = con.prepareStatement(insertLikeQuery)) {
				pstmt.setString(1, likeId);
				pstmt.setString(2, commentId);
				pstmt.setString(3, userId);

				int rowsInserted = pstmt.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Comment " + commentId + " liked successfully.");
					incrementCommentLikeCount(commentId);
				} else {
					System.out.println("Failed to like the comment.");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Method to increment the like count for a comment
	private void incrementCommentLikeCount(String commentId) {
		try (PreparedStatement pstmt = con.prepareStatement(
				"UPDATE comment SET comment_num_of_likes = comment_num_of_likes + 1 WHERE comment_id = ?")) {
			pstmt.setString(1, commentId);
			pstmt.executeUpdate();
			System.out.println("Incremented like count for comment " + commentId);
		} catch (SQLException e) {
			System.out.println("Error incrementing like count: " + e.getMessage());
		}
	}

	// Method to decrement the like count for a comment
	private void decrementCommentLikeCount(String commentId) {
		try (PreparedStatement pstmt = con.prepareStatement(
				"UPDATE comment SET comment_num_of_likes = comment_num_of_likes - 1 WHERE comment_id = ?")) {
			pstmt.setString(1, commentId);
			pstmt.executeUpdate();
			System.out.println("Decremented like count for comment " + commentId);
		} catch (SQLException e) {
			System.out.println("Error decrementing like count: " + e.getMessage());
		}
	}

	private void unlikeComment(String commentId, String likeRecordId) {
		// Unlike: Remove from comment_like table
		String unlikeQuery = "DELETE FROM comment_like WHERE comment_like_id = ?";
		try (PreparedStatement unlikeStmt = con.prepareStatement(unlikeQuery)) {
			unlikeStmt.setString(1, likeRecordId);
			int rowsDeleted = unlikeStmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Comment " + commentId + " unliked successfully.");
				decrementCommentLikeCount(commentId);
			} else {
				System.out.println("Failed to unlike the comment.");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Generate a unique ID for each like
	private String generateLikeId() {
		return "comment_like_" + System.currentTimeMillis();
	}
}
