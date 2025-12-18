package twitterProj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Hashtag {
	private final Connection con;

	// Constructor with database connection
	public Hashtag(Connection con) {
		this.con = con;
	}

	// Method to add hashtags to a post
	public void addHashtagsToPost(String postId, String hashtagsInput) {
		// Split the input string by spaces (or use a custom delimiter if necessary)
		String[] hashtags = hashtagsInput.split("\\s+");

		try {
			// Iterate through each hashtag provided by the user
			for (String hashtag : hashtags) {
				// Trim the hashtag to remove any leading/trailing spaces
				hashtag = hashtag.trim();

				if (hashtag.isEmpty()) {
					continue; // Skip empty hashtags
				}

				// Check if the hashtag already exists in the hashtag table
				String checkQuery = "SELECT hashtag_id FROM hashtag WHERE hashtag_name = ?";
				try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
					checkStmt.setString(1, hashtag);

					ResultSet rs = checkStmt.executeQuery();
					String hashtagId = null;

					if (rs.next()) {
						// If the hashtag exists, use the existing hashtag_id
						hashtagId = rs.getString("hashtag_id");
					} else {
						// If the hashtag does not exist, insert it and get the generated hashtag_id
						String insertQuery = "INSERT INTO hashtag (hashtag_id, hashtag_name) VALUES (?, ?)";
						try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
							hashtagId = generateHashtagId(); // Generate a new unique ID for the hashtag
							insertStmt.setString(1, hashtagId);
							insertStmt.setString(2, hashtag);
							insertStmt.executeUpdate();
						}
					}

					// First, check if the post already has the hashtag
					String checkHashtagQuery = "SELECT COUNT(*) FROM hashtag_to_post WHERE post_id = ? AND hashtag_id = ?";

					try (PreparedStatement checkPostStmt = con.prepareStatement(checkHashtagQuery)) {
						checkPostStmt.setString(1, postId); // The post ID you're checking
						checkPostStmt.setString(2, hashtagId); // The hashtag ID you're associating with the post

						ResultSet rs2 = checkPostStmt.executeQuery();
						if (rs2.next() && rs2.getInt(1) > 0) {
							// The post already has the hashtag, so don't insert it again
							System.out.println("This post already has the hashtag.");
						} else {
							// The hashtag is not associated with the post, so proceed with the insertion
							String insertHashtagToPostQuery = "INSERT INTO hashtag_to_post (hashtag_id, post_id) VALUES (?, ?)";
							try (PreparedStatement insertHashtagStmt = con.prepareStatement(insertHashtagToPostQuery)) {
								insertHashtagStmt.setString(1, hashtagId);
								insertHashtagStmt.setString(2, postId);
								insertHashtagStmt.executeUpdate();
								System.out.println("Hashtag #" + hashtagId + " successfully associated with the post.");
							}
						}
					} catch (SQLException e) {
						System.out.println("SQL Error: " + e.getMessage());
					}

				}
			}

			System.out.println("Hashtags added to post successfully.");
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}


	// Method to retrieve all hashtags associated with a post
	public Set<String> getHashtagsForPost(String postId) {
		Set<String> hashtags = new HashSet<>();

		String query = "SELECT hashtag_name FROM hashtag as h "
				+ "JOIN hashtag_to_post as htp ON h.hashtag_id = htp.hashtag_id " + "WHERE htp.post_id = ?";

		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, postId);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				hashtags.add(rs.getString("hashtag_name"));
			}

		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}

		return hashtags;
	}

	// Method to search posts by a hashtag
	public Set<String> searchPostsByHashtag(String hashtag) {
		Set<String> postIds = new HashSet<>();

		String query = "SELECT post_id FROM hashtag_to_post as htp "
				+ "JOIN hashtag as h ON htp.hashtag_id = h.hashtag_id "
				+ "WHERE h.hashtag_name = ?";

		try (PreparedStatement pstmt = con.prepareStatement(query)) {
			pstmt.setString(1, hashtag);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				postIds.add(rs.getString("post_id"));
			}

		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}

		return postIds;
	}

	// Generate a unique ID for the hashtag
	private String generateHashtagId() {
		return "h" + System.currentTimeMillis();
	}
}
