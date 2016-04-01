package ontologies.search;

import jade.content.AgentAction;

public class SearchOperation implements AgentAction{
	private int type;
	private SearchBean search;
	private String nickname;
	private String url;
	private jade.util.leap.List resultList;
	public SearchOperation() {
		this.resultList = new jade.util.leap.ArrayList();
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String userName) {
		this.nickname = userName;
	}
	public SearchBean getSearch() {
		return search;
	}
	public void setSearch(SearchBean search) {
		this.search = search;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public jade.util.leap.List getResultList() {
		return resultList;
	}
	public void setResultList(jade.util.leap.List resultList) {
		this.resultList = resultList;
	}
	
}
