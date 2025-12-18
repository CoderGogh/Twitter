package twitterProj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Post {
	private final Connection con;
	private final String writerId; // ID of the logged-in user
	private Scanner s = new Scanner(System.in);

	// Constructor that accepts a logged-in user's ID and an active DB connection
	public Post(Connection con, String writerId) {
		this.con = con;
		this.writerId = writerId;
	}

	// Method to create a new post
	public void createPost() {
		System.out.println("Enter the content of your post:");
		String content = s.nextLine();

		try (PreparedStatement pstmt = con.prepareStatement(
				"INSERT INTO post (post_id, post_content, writer_id) VALUES (?, ?, ?)")) {

			String postId = generatePostId(); // Generate a unique post ID
			pstmt.setString(1, postId);
			pstmt.setString(2, content);
			pstmt.setString(3, writerId);

			pstmt.executeUpdate();
			incrementUserPostCount(writerId);
			System.out.println("Post created successfully with ID: " + postId);
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Method to generate a unique post ID
	private String generatePostId() {
		String postId = "p" + System.currentTimeMillis(); // Example ID based on timestamp
		// Alternatively, you could query the database to ensure uniqueness
		return postId;
	}

	private void incrementUserPostCount(String userId) {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE user SET total_posts = total_posts + 1 WHERE user_id = ?")) {
			pstmt.setString(1, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error incrementing user's post count: " + e.getMessage());
		}
	}

	// Main method for testing
	public static void main(String[] args) {
		// Simulate user login and connection setup
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/mydb", "root", "12345")) {
			String loggedInUserId = "user123"; // Simulated logged-in user ID
			Post post = new Post(con, loggedInUserId);
			post.createPost();
		} catch (SQLException e) {
			System.out.println("Database connection error: " + e.getMessage());
		}
	}
}
