package myagents;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.gateway.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import myfunctions.FuncBean;
import ontologies.*;

public class MyGatewayAgent extends GatewayAgent {
	/**
	 * 
	 */

	ProfileOperation theOper;
	AMSAgentDescription [] agents =null;
	private Codec codec = new SLCodec();
	private Ontology profileOntology = ProfileOntology.getInstance();
	
	protected void processCommand(Object obj) {
		
		if(obj instanceof ProfileOperation) {
			System.out.println("\n### GatewayAgent : Got "+obj.getClass()+" from servlet. ###");
			
			this.theOper = (ProfileOperation) obj;
			// Here, I can ask agents & send only to profile agents
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			
			// Setting up the broadcast & Prepare the message to send
			msg.setLanguage(codec.getName());
			msg.setOntology(profileOntology.getName());
			
			for (AMSAgentDescription recAgents : agents) {
				if(recAgents.getName().equals(this.getAID())==false) {
					try {
						getContentManager().fillContent(msg, new Action(recAgents.getName(),this.theOper));
						msg.addReceiver(recAgents.getName());
					} catch ( CodecException | OntologyException e) {
						e.printStackTrace();
					}
				}
			}
            send(msg);
		}
	}
	
	public void setup()
    {	
		getContentManager().registerLanguage(codec);
    	getContentManager().registerOntology(profileOntology);
    	
		// Get all agents in the current container
        try {
        	SearchConstraints c = new SearchConstraints();
        	c.setMaxResults(new Long(-1));
        	agents = AMSService.search(this, new AMSAgentDescription(), c);
        }
        catch (Exception e) {
        	System.out.println("Problem with the search");
        	e.printStackTrace();
        }
        
        AID myID = this.getAID();
        for (AMSAgentDescription agent : agents) {
        	AID agentID = agent.getName();
        	System.out.println(
    				( agentID.equals( myID ) ? "*** " : "    ")
    			     + ": " + agentID.getName() 
    			);
        }
        
        addBehaviour(new CyclicBehaviour(this) 	{
        	public void action() {
        		 
                ACLMessage msg = receive();
                
                if (msg!=null)    {
                	if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD){
                        System.out.println("\n ### GatewayAgent : Response from agent = NOT UNDERSTOOD!###");
                     }
                	else if (msg.getPerformative() != ACLMessage.INFORM){
                        System.out.println("### GatewayAgent : Unexpected msg from agent! ###");
                     }
                	else{
                		try {
    						ContentElement content = getContentManager().extractContent(msg);
    						if (content instanceof Result) {
    							Result result = (Result) content;
    							
    							if (result.getValue() instanceof UserBean) {
    								theOper.setUser((UserBean)result.getValue());
    								System.out.println("### GatewayAgent : UserBean set to release. ###");
    							}
    						}
    						releaseCommand(theOper);
    					} catch ( CodecException | OntologyException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                	}
                } else block();
             }
        		});
        super.setup();
    }
}
