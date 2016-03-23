package ontologies;

import jade.content.AgentAction;

public class ProfileInformation implements AgentAction {
//------------------------------------------------------------------
	private int type;
	private String username;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
