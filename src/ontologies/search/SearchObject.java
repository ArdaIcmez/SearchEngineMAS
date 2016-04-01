package ontologies.search;

import jade.content.Concept;

public class SearchObject implements Concept{
	private String subject;
	private jade.util.leap.List contentList;
	public SearchObject() {
		this.contentList = new jade.util.leap.ArrayList();
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public jade.util.leap.List getContentList() {
		return contentList;
	}
	public void setContentList(jade.util.leap.List contentList) {
		this.contentList = contentList;
	}
}
