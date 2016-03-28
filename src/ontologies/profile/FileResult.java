package ontologies.profile;

import jade.content.AgentAction;

public class FileResult implements AgentAction{
	private int type;
	private String url;
	private jade.util.leap.List userList;
	public FileResult() {
		this.userList = new jade.util.leap.ArrayList();
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public jade.util.leap.List getUserList() {
		return userList;
	}
	public void setUserList(jade.util.leap.List userList) {
		this.userList = userList;
	}
	
}
