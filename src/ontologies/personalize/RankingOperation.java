package ontologies.personalize;

import jade.content.AgentAction;

public class RankingOperation implements AgentAction{
	private int type;
	private RankBean rank;
	private jade.util.leap.List rankedList;
	public RankingOperation(){
		rankedList = new jade.util.leap.ArrayList();
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public RankBean getRank() {
		return rank;
	}
	public void setRank(RankBean rank) {
		this.rank = rank;
	}
	public jade.util.leap.List getRankedList() {
		return rankedList;
	}
	public void setRankedList(jade.util.leap.List rankedList) {
		this.rankedList = rankedList;
	}
	
}
