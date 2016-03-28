package ontologies.profile;

public interface ProfileVocabulary {
/**
 * Vocabulary for profile ontology
 * 
 * @author arda
 */
	// Basic Vocabulary of Profile Ontology
	public static final int MODIFY_PROFILE = 1;
	public static final int AUTHENTICATE_USER = 2;
	public static final int REGISTER_USER = 3;
	public static final int DELETE_USER = 4;
	public static final int POPULATE_PROFILE = 5;
	public static final int LOAD_USERS = 6;
	public static final String AUTHENT_CORRECT = "Authentication valid";
	public static final String AUTHENT_INCORRECT = "Authentication unvalid";
	public static final String PROFILE_MODIFIED = "Profile modified";
	public static final String USER_REGISTERED = "User registered";
	public static final String USER_DELETED = "User deleted";
	public static final String PROFILE_POPULATED = "Profile populated";
	
	// Ontology vocabulary

	public static final String USER = "UserBean";
	public static final String USER_NAME = "name";
	public static final String USER_USERNAME = "nickname";
	public static final String USER_SURNAME = "surname";
	public static final String USER_PASSWORD = "password";
	public static final String USER_ROLE = "role";
	public static final String USER_JOB = "job";
	public static final String USER_INTERESTS = "interests";
	public static final String USER_HISTORY = "browseHistory";
	public static final String USER_AUTHENTICATION = "authentication";
	
	public static final String PROFILE_OPERATION = "ProfileOperation";
	public static final String PROFILE_OPERATION_TYPE = "type";
	public static final String PROFILE_OPERATION_USER = "user";
	public static final String PROFILE_OPERATION_MODIFYUSER = "modifyUser";
	
	public static final String PROFILE_INFORMATION = "ProfileInformation";
	public static final String PROFILE_INFORMATION_TYPE = "type";
	public static final String PROFILE_INFORMATION_USERNAME = "username";
	
	public static final String FILE_RESULT = "FileResult";
	public static final String FILE_RESULT_TYPE = "type";
	public static final String FILE_RESULT_URL = "url";
	public static final String FILE_RESULT_USERLIST = "userList";
	
	public static final String PROFILE_PROBLEM = "ProfileProblem";
	public static final String PROFILE_PROBLEM_NUM = "num";
	public static final String PROFILE_PROBLEM_MSG = "msg";
}
