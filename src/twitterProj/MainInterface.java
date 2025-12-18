package twitterProj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class MainInterface {

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	String rub = null;
	Scanner s = new Scanner(System.in);

	// connect
	public MainInterface() {
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
	}

	public void beforeLogin() {
		try {
			while (true) {
				System.out.println(
						"Enter option (0 for sign-up, 1 for login, 2 to find user by first name, 3 to search posts with its hashtag):");

				int op1;
				try {
					op1 = s.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Invalid option. Please enter a number (0, 1, or 2).");
					s.nextLine(); // Clear the scanner buffer
					continue; // Restart the loop
				}

				s.nextLine();
				String user_id;
				String password;

				if (op1 == 0) { // Sign-up
					System.out.println("Enter user_id and password:");
					user_id = s.next();
					password = s.next();

					String queryCheck = "SELECT user_id FROM user WHERE user_id = ?";
					pstmt = con.prepareStatement(queryCheck);
					pstmt.setString(1, user_id);
					rs = pstmt.executeQuery();

					if (rs.next()) {
						System.out.println("Error: User already exists.");
					} else {
						String queryInsert = "INSERT INTO user (user_id, password) VALUES (?, ?)";
						pstmt = con.prepareStatement(queryInsert);
						pstmt.setString(1, user_id);
						pstmt.setString(2, password);
						pstmt.executeUpdate();
						System.out.println("User registered successfully.");
					}
				} else if (op1 == 1) { // Login
					System.out.println("Enter user_id and password:");
					user_id = s.next();
					password = s.next();

					String queryLogin = "SELECT user_id FROM user WHERE user_id = ? AND password = ?";
					pstmt = con.prepareStatement(queryLogin);
					pstmt.setString(1, user_id);
					pstmt.setString(2, password);
					rs = pstmt.executeQuery();

					if (rs.next()) {
						System.out.println("Logged in successfully.");
						afterLoginSession(user_id);
					} else {
						System.out.println("No such user or incorrect password.");
					}
				} else if (op1 == 2) {
					s.nextLine(); // Clear scanner buffer before taking further input
					findUserWithFirstName();
				} else if (op1 == 3) {
					System.out.println("Enter the hashtag to search for posts (without '#'):");
					String hashtag = s.nextLine();

					// Create an instance of the Hashtag class to search posts by hashtag
					Hashtag hashtagSearch = new Hashtag(con);

					// Retrieve posts associated with the hashtag
					Set<String> postIds = hashtagSearch.searchPostsByHashtag(hashtag);

					if (postIds.isEmpty()) {
						System.out.println("No posts found with the hashtag #" + hashtag);
					} else {
						System.out.println("Posts with the hashtag #" + hashtag + ":");
						for (String postId : postIds) {
							System.out.println("Post ID: " + postId);
						}
					}
				}
				else {
					System.out.println("Invalid option.");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		} finally {
			// Close resources in the correct order
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
				s.close();
			} catch (SQLException e) {
				System.out.println("Error closing resources: " + e.getMessage());
			}
		}
	}


	public void afterLoginSession(String user_id) {
		String loggedInUserId = user_id;
		String currentPost = null;
		String currentComment = null;

		while (true) {
			System.out.println("Choose an option from the following list:");
			System.out.println("0: Log out");
			System.out.println("1: Find user");
			System.out.println("10: Create post, 11: Browse posts");
			System.out.println(
					"20: Create comment on a post, 21: Create child comment to an existing comment 22: Browse comments");
			System.out.println("31: Like post, 32: Like comment");
			System.out.println("40: Follow other user, 41: Show followers, 42: Show following users");
			System.out.println(
					"51: Bookmark current post, 52: Bookmark other post, 53: Show bookmarked user list of current post");
			System.out.println(
					"60: Add hashtag to a post, 61: retrieve hashtag list of the post, 62: search posts with hashtag");
			System.out.println("98: Update title, 99: Update user info");

			int op2;
			try {
				op2 = s.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid option. Please enter a valid op.");
				s.nextLine(); // Clear the scanner buffer
				continue; // Restart the loop
			}
			s.nextLine(); // Consume the newline after integer input

			try {
				switch (op2) {
				case 0: // Termination and return to login
					System.out.println("Logging out...");
					return;
				case 1:
					findUserWithFirstName();
					break;
				case 10: // create post
					Post post = new Post(con, loggedInUserId);
					post.createPost();
					break;

				case 11: // browse post
					System.out.println("move to (post_id):");
					String moveToPost = s.nextLine();
					String queryBrowsePost = "SELECT post_id FROM post WHERE post_id = ?";
					pstmt = con.prepareStatement(queryBrowsePost);
					pstmt.setString(1, moveToPost);
					rs = pstmt.executeQuery();

					if (rs.next()) {
						currentPost = rs.getString(1);
						System.out.println("Moved to post: " + currentPost);
					} else {
						System.out.println("No such post with the corresponding ID.");
					}
					break;

				case 20: // create comment
					if (currentPost == null) {
						System.out.println("You need to select a post first.");
					} else {
						Comment comment = new Comment(con, loggedInUserId);
						comment.addComment(currentPost);
					}
					break;

				case 21: // Add child comment to an existing comment
					if (currentComment == null) {
						System.out.println("You need to select a comment first.");
					} else {
						Comment comment = new Comment(con, loggedInUserId);
						comment.addChildComment(currentComment);
					}
					break;

				case 22: // browse comment
					System.out.println("move to (comment_id):");
					String moveToComment = s.nextLine();
					String queryBrowseComment = "SELECT comment_id FROM comment WHERE comment_id = ?";
					pstmt = con.prepareStatement(queryBrowseComment);
					pstmt.setString(1, moveToComment);
					rs = pstmt.executeQuery();

					if (rs.next()) {
						currentComment = rs.getString(1);
						System.out.println("Moved to comment: " + currentComment);
					} else {
						System.out.println("No such comment with the corresponding ID.");
					}
					break;

				case 31: // like post
					if (currentPost == null) {
						System.out.println("You need to select a post first.");
					} else {
						PostLike postLike = new PostLike(con, loggedInUserId);
						postLike.likePost(currentPost);
					}
					break;

				case 32: // like comment
					if (currentComment == null) {
						System.out.println("You need to select a comment first.");
					} else {
						CommentLike commentLike = new CommentLike(con, loggedInUserId);
						commentLike.likeComment(currentComment);
					}
					break;

				case 40: // follow other user
					UserAction userActionFollow = new UserAction(con, loggedInUserId);
					userActionFollow.followUser();
					break;
				case 41: // show followers
					Follower follower = new Follower(con, loggedInUserId);
					follower.viewFollowers();
					break;
				case 42: // show following users
					Following following = new Following(con, loggedInUserId);
					following.viewFollowingUsers();
					break;
				case 51: // Bookmark current post
					if (currentPost != null) {
						// Create a Bookmark instance for the current post
						Bookmark bookmark = new Bookmark(con, loggedInUserId);
						bookmark.bookmarkPost(currentPost); // This will either bookmark or unbookmark the post
					} else {
						System.out.println("No post selected.");
					}
					break;
				case 52: // Bookmark other post
					System.out.println("Enter the post ID you want to bookmark:");
					String otherPostId = s.nextLine();
					Bookmark otherBookmark = new Bookmark(con, loggedInUserId);
					otherBookmark.bookmarkPost(otherPostId); // Bookmark the specified post
					break;
				case 53: // Show bookmarked user list of current post
					if (currentPost != null) {
						// Show details about the current post's bookmarks
						Bookmark showBookmarkList = new Bookmark(con, loggedInUserId);
						showBookmarkList.viewBookmark(currentPost); // Display the post details with bookmark count
					} else {
						System.out.println("No post selected.");
					}
					break;

				case 60: //60: Add hashtag to a post
					if (currentPost == null) {
						System.out.println("You need to select a post first.");
					} else {
						Hashtag hashtag = new Hashtag(con);
						System.out.println("input hashtags with blank:");
						String inputHashtag = s.nextLine();
						hashtag.addHashtagsToPost(currentPost, inputHashtag);
					}
					break;
				case 61: // Retrieve hashtag list of the post
					if (currentPost == null) {
						System.out.println("You need to select a post first.");
					} else {
						Hashtag hashtag = new Hashtag(con);

						// Retrieve hashtags for the current post
						Set<String> hashtags = hashtag.getHashtagsForPost(currentPost);

						if (hashtags.isEmpty()) {
							System.out.println("No hashtags found for this post.");
						} else {
							System.out.println("Hashtags for the current post:");
							for (String tag : hashtags) {
								System.out.println(tag);
							}
						}
					}
					break;

				case 62: // Search posts with hashtag
					System.out.println("Enter the hashtag to search for posts (without '#'):");
					String hashtag = s.nextLine();

					// Create an instance of the Hashtag class to search posts by hashtag
					Hashtag hashtagSearch = new Hashtag(con);

					// Retrieve posts associated with the hashtag
					Set<String> postIds = hashtagSearch.searchPostsByHashtag(hashtag);

					if (postIds.isEmpty()) {
						System.out.println("No posts found with the hashtag #" + hashtag);
					} else {
						System.out.println("Posts with the hashtag #" + hashtag + ":");
						for (String postId : postIds) {
							System.out.println("Post ID: " + postId);
						}
					}
					break;

				case 98: // update title
					UserAction userTitleUpdate = new UserAction(con, loggedInUserId);
					userTitleUpdate.updateUserTitles(loggedInUserId);
					break;
				case 99: // update user info
					UserAction userActionUpdate = new UserAction(con, loggedInUserId);

					System.out.println("Select the information to update:");
					System.out.println("1. First Name");
					System.out.println("2. Last Name");
					System.out.println("3. Birthdate");
					System.out.println("4. Phone Number");
					System.out.println("5. Address");
					System.out.println("0. Cancel");

					int updateOption;
					try {
						updateOption = s.nextInt();
					} catch (InputMismatchException e) {
						System.out.println("Invalid option. Please enter a valid op. returning to the mainSession...");
						s.nextLine(); // Clear the scanner buffer
						continue; // Restart the loop
					}
					s.nextLine();

					switch (updateOption) {
					case 1: // Update First Name
						System.out.println("Enter new First Name:");
						String newFirstName = s.nextLine();
						userActionUpdate.updateFirstName(newFirstName);
						break;

					case 2: // Update Last Name
						System.out.println("Enter new Last Name:");
						String newLastName = s.nextLine();
						userActionUpdate.updateLastName(newLastName);
						break;

					case 3: // Update Birthdate
						System.out.println("Enter new Birthdate (YYYY-MM-DD):");
						String newBirthdate = s.nextLine();
						userActionUpdate.updateBirthdate(newBirthdate);
						break;

					case 4: // Update Phone Number
						System.out.println("Enter new Phone Number:");
						String newPhoneNumber = s.nextLine();
						userActionUpdate.updatePhoneNumber(newPhoneNumber);
						break;

					case 5: // Update Address
						System.out.println("Enter new Address:");
						String newAddress = s.nextLine();
						userActionUpdate.updateAddress(newAddress);
						break;

					case 0: // Cancel update
						System.out.println("Update canceled.");
						break;

					default:
						System.out.println("Invalid option. Please try again.");
						break;
					}
					break;

				default:
					System.out.println("Invalid option. Please try again.");
					break;
				}
			} catch (SQLException e) {
				System.out.println("SQL Error: " + e.getMessage());
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (SQLException e) {
					System.out.println("Error closing resources: " + e.getMessage());
				}
			}
		}
	}

	public void findUserWithFirstName() {
		System.out.println("Enter the name to search for users:");
		String name = s.nextLine();

		try {
			String searchQuery = "SELECT user_id FROM user WHERE first_name = ?";
			try (PreparedStatement pstmt = con.prepareStatement(searchQuery)) {
				pstmt.setString(1, name);
				try (ResultSet rs = pstmt.executeQuery()) {
					boolean found = false;
					System.out.println("Users with the name \"" + name + "\":");

					while (rs.next()) {
						found = true;
						String userId = rs.getString("user_id");
						System.out.println("User ID: " + userId);
					}

					if (!found) {
						System.out.println("No users found with the name \"" + name + "\".");
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		MainInterface maininterface = new MainInterface();
		maininterface.beforeLogin();
	}

}
