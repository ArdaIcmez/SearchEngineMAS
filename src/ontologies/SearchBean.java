package ontologies;

import java.util.ArrayList;
import java.util.List;

public class SearchBean implements java.io.Serializable {  
	private String word;  
	private List<String> resultList;  
	
	public SearchBean(){
		resultList = new ArrayList<String>();
	}
	public String getWord() {  
	    return word;  
	}  
	public void setWord(String word) {  
	    this.word = word;  
	}
	public List<String> getList(){
		return this.resultList;
	}
	public void addList(String newlink)
	{
		this.resultList.add(newlink);
	}
}  
