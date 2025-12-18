package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bookmark {
    private Connection con;
    private String userId;

    // Constructor to initialize with database connection and user ID
    public Bookmark(Connection con, String userId) {
        this.con = con;
        this.userId = userId;
    }

	// Method to bookmark or un-bookmark a post based on current state
	public void bookmarkPost(String postId) {
		try {
			// Check if the post is already bookmarked by the user
			String checkQuery = "SELECT * FROM bookmark WHERE post_id = ? AND user_id = ?";
			try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
				checkStmt.setString(1, postId);
				checkStmt.setString(2, userId);
				ResultSet rs = checkStmt.executeQuery();

				if (rs.next()) {
					// If the post is already bookmarked, un-bookmark it
					unbookmarkPost(postId);
				} else {
					// Otherwise, bookmark the post
					addBookmark(postId);
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Helper method to add a bookmark and increment the post's bookmark count
	private void addBookmark(String postId) {
		String insertQuery = "INSERT INTO bookmark (bookmark_id, post_id, user_id) VALUES (?, ?, ?)";
		String updateBookmarkCountQuery = "UPDATE post SET bookmarked_count = bookmarked_count + 1 WHERE post_id = ?";

		try (PreparedStatement insertStmt = con.prepareStatement(insertQuery);
				PreparedStatement updateBookmarkCountStmt = con.prepareStatement(updateBookmarkCountQuery)) {

			// Insert bookmark record
			insertStmt.setString(1, generateBookmarkId());
			insertStmt.setString(2, postId);
			insertStmt.setString(3, userId);
			insertStmt.executeUpdate();

			// Increment the like count on the post
			updateBookmarkCountStmt.setString(1, postId);
			updateBookmarkCountStmt.executeUpdate();

			System.out.println("Post " + postId + " bookmarked successfully.");
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	// Helper method to remove a bookmark and decrement the post's like count
	private void unbookmarkPost(String postId) {
		String deleteQuery = "DELETE FROM bookmark WHERE post_id = ? AND user_id = ?";
		String updateBookmarkCountQuery = "UPDATE post SET bookmarked_count = bookmarked_count - 1 WHERE post_id = ?";

		try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
				PreparedStatement updateBookmarkCountStmt = con.prepareStatement(updateBookmarkCountQuery)) {

			// Remove bookmark record
			deleteStmt.setString(1, postId);
			deleteStmt.setString(2, userId);
			deleteStmt.executeUpdate();

			// Decrement the like count on the post
			updateBookmarkCountStmt.setString(1, postId);
			updateBookmarkCountStmt.executeUpdate();

			System.out.println("Post " + postId + " un-bookmarked successfully.");
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

    // Method to view all bookmarks for a specific post ID
    public void viewBookmark(String postId) {
		String query = "SELECT * FROM bookmark WHERE post_id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, postId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
				System.out.println("users that Bookmarked post ID \"" + postId + "\":");
                
                while (rs.next()) {
                    found = true;
					String userId = rs.getString("user_id");

					System.out.println("user ID: " + userId);
                    System.out.println("------");
                }
                
                if (!found) {
					System.out.println("No bookmarks found for post ID \"" + postId + "\".");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

	// Generates a unique ID for the bookmark
	private String generateBookmarkId() {
		return "b" + System.currentTimeMillis();
	}
}

