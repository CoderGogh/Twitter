package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Comment {
	private final Connection con;
	private final String commenterId; // ID of the logged-in user
	private Scanner s = new Scanner(System.in);

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public Comment(Connection con, String commenterId) {
		this.con = con;
		this.commenterId = commenterId;
	}

	// Method to add a comment to a specific post
	public void addComment(String post_id) {
		System.out.println("Enter your comment:");
		String content = s.nextLine();

		try (PreparedStatement pstmt = con.prepareStatement(
				"INSERT INTO comment (comment_id, comment_content, parent_post_id, writer_id) VALUES (?, ?, ?, ?)")) {
			String commentId = generateCommentId(); // Generate a unique comment ID
			pstmt.setString(1, commentId);
			pstmt.setString(2, content);
			pstmt.setString(3, post_id);
			pstmt.setString(4, commenterId);

			pstmt.executeUpdate();
			incrementUserCommentCount(commenterId);
			System.out.println("Comment " + commentId + " added successfully to post: " + post_id);
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	public void addChildComment(String comment_id) {
		System.out.println("Enter your comment:");
		String content = s.nextLine();

		try (PreparedStatement pstmt = con.prepareStatement(
				"INSERT INTO comment (comment_id, comment_content, parent_comment_id, writer_id) VALUES (?, ?, ?, ?)")) {
			String commentId = generateCommentId(); // Generate a unique comment ID
			pstmt.setString(1, commentId);
			pstmt.setString(2, content);
			pstmt.setString(3, comment_id);
			pstmt.setString(4, commenterId);

			pstmt.executeUpdate();
			incrementUserCommentCount(commenterId);
			System.out.println("Comment " + commentId + " added successfully to comment: " + comment_id);
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	private void incrementUserCommentCount(String userId) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE user SET total_comments = total_comments + 1 WHERE user_id = ?")) {
			pstmt.setString(1, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error incrementing user's comment count: " + e.getMessage());
		}
	}

	// Method to generate a unique comment ID
	private String generateCommentId() {
		return "c" + System.currentTimeMillis(); // Example ID based on timestamp
	}
}
