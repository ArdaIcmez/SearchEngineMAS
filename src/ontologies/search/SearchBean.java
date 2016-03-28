package ontologies.search;

import jade.content.Concept;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class SearchBean implements Concept {  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String word;  
	private List resultList;  
	
	public SearchBean(){
		resultList = new ArrayList();
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}  
