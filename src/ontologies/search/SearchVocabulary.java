package ontologies.search;

public interface SearchVocabulary {
	
		// Basic Vocabulary of Profile Ontology
		public static final int SEARCH_KEYWORD = 1;
		public static final int ADD_SEARCH_HISTORY = 2;
		public static final String RESULTS_FOUND = "Results found";
		public static final String RESULTS_NOT_FOUND = "No results found.";
		
		// Ontology vocabulary

		public static final String SEARCH = "SearchBean";
		public static final String SEARCH_WORD = "word";
		public static final String SEARCH_RESULT_LIST = "resultList";

		public static final String SEARCH_OPERATION = "SearchOperation";
		public static final String SEARCH_OPERATION_TYPE = "type";
		public static final String SEARCH_OPERATION_SEARCH = "search";
		public static final String SEARCH_OPERATION_NICKNAME = "nickname";
		
		public static final String SEARCH_PROBLEM = "SearchProblem";
		public static final String SEARCH_PROBLEM_NUM = "num";
		public static final String SEARCH_PROBLEM_MSG = "msg";
}
