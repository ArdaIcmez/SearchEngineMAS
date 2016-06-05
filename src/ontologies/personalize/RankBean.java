package ontologies.personalize;

import jade.content.Concept;

public class RankBean implements Concept{
	private jade.util.leap.List resultList;
	private jade.util.leap.List interestList;
	private jade.util.leap.List historyList;
	private String searchQuery;
	public RankBean() {
		resultList = new jade.util.leap.ArrayList();
		interestList = new jade.util.leap.ArrayList();
		historyList = new jade.util.leap.ArrayList();
	}
	public jade.util.leap.List getResultList() {
		return resultList;
	}
	public void setResultList(jade.util.leap.List resultList) {
		this.resultList = resultList;
	}
	public jade.util.leap.List getInterestList() {
		return interestList;
	}
	public void setInterestList(jade.util.leap.List interestList) {
		this.interestList = interestList;
	}
	public jade.util.leap.List getHistoryList() {
		return historyList;
	}
	public void setHistoryList(jade.util.leap.List historyList) {
		this.historyList = historyList;
	}
	public String getSearchQuery() {
		return searchQuery;
	}
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
}
