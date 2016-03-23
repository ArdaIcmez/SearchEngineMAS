package ontologies;

import jade.content.AgentAction;

public class ProfileOperation implements AgentAction {
/** 
 * The ProfileOperation class which will be used to pass ontology objects
 * between agents
 * 
 * @author arda
 	*/
	private static final long serialVersionUID = -1792405125783138081L;
	private int type;
	private UserBean user;
	private UserBean modifyUser;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public UserBean getUser() {
		return user;
	}
	public void setUser(UserBean user) {
		this.user = user;
	}
	public UserBean getModifyUser() {
		return modifyUser;
	}
	public void setModifyUser(UserBean modifyUser) {
		this.modifyUser = modifyUser;
	}
	
}
