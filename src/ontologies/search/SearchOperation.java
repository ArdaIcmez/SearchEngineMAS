package ontologies.search;

import jade.content.AgentAction;

public class SearchOperation implements AgentAction{
	private int type;
	private SearchBean search;
	private String nickname;
	
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
	
}
