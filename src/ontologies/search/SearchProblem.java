package ontologies.search;

import jade.content.Concept;

public class SearchProblem implements Concept {
	private int num;
	private String msg;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
