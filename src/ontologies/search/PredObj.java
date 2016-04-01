package ontologies.search;

import jade.content.Concept;

public class PredObj implements Concept{
	private String predicate;
	private String obj;
	 
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObj() {
		return obj;
	}
	public void setObj(String obj) {
		this.obj = obj;
	}
 
}
