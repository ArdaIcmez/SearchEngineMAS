package myagents;



public class AgentMessage implements java.io.Serializable{
	private String roleMessage = new String("");
	private String receiver = new String("");
	private Object msgObj = null;
	private String redirectStr = "";
	
	public AgentMessage() {
		
	}
	public AgentMessage(String role) {
		this.roleMessage=role;
	}

	public String getRoleMessage() {
		return roleMessage;
	}
	public void setRoleMessage(String roleMessage) {
		this.roleMessage = roleMessage;
	}
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Object getMsgObj() {
		return msgObj;
	}

	public void setMsgObj(Object msgObj) {
		this.msgObj = msgObj;
	}

	public String getRedirectStr() {
		return redirectStr;
	}

	public void setRedirectStr(String redirectStr) {
		this.redirectStr = redirectStr;
	}
	
}
