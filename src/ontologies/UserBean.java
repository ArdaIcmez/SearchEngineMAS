package ontologies;



import jade.content.Concept;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class UserBean implements Concept {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private String name;
private String surname;
private String nickname;
private String password;
private String role;
private String job;
private List interests;
private List browseHistory;
private boolean authentication;
public UserBean()
{
	this.interests = new ArrayList();
	this.browseHistory = new ArrayList();
	this.name = "";
	this.surname = "";
	this.role = "";
	this.job = "";
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getSurname() {
	return surname;
}

public void setSurname(String surname) {
	this.surname = surname;
}

public String getNickname() {
	return nickname;
}

public void setNickname(String nickname) {
	this.nickname = nickname;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

public String getJob() {
	return job;
}

public void setJob(String job) {
	this.job = job;
}

public List getInterests() {
	return interests;
}

public void setInterests(List interests) {
	this.interests = interests;
}

public List getBrowseHistory() {
	return browseHistory;
}

public void setBrowseHistory(List browseHistory) {
	this.browseHistory = browseHistory;
}

public boolean getAuthentication() {
	return authentication;
}

public void setAuthentication(boolean authentication) {
	this.authentication = authentication;
}

}
