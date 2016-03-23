package myagents;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpSession;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import myfunctions.FuncBean;
import ontologies.*;

public class SearchAgent extends Agent {
	private static final long serialVersionUID = 1L;
	private Object obj = null;
	private FuncBean execFunc = new FuncBean();
	protected void setup()
	{
		 addBehaviour(new CyclicBehaviour(this)
     	{
     	/**
			 * 
			 */
			private static final long serialVersionUID = 6136775480499296849L;

		public void action() {
     		 
             ACLMessage msg = receive();
             System.out.println("Message received");
             if (msg!=null)    {
            	 try {
					obj = msg.getContentObject();
					System.out.println("Object is : "+obj.getClass().toString());
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	 AgentMessage myMessage = (AgentMessage) obj;
            	 if ((myMessage.getMsgObj()) instanceof SearchBean) {
            		 System.out.println("Search ajana geldi");
            		 SearchBean searchBean = (SearchBean)myMessage.getMsgObj();
            		 execFunc.doSearch(searchBean);
            		 ACLMessage reply = msg.createReply();
            		 reply.setPerformative(ACLMessage.INFORM);
            		 try {
						reply.setContentObject((Serializable) searchBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
            		 send(reply);
            		 System.out.println("msg gonderildi");
            	 }
             } else {
            	 System.out.println("else dustum :(");
            	 block();
             }
          }
     		});
	}
	protected void takeDown()
	{
		try { DFService.deregister(this); }
        catch (Exception e) {}
	}
}
