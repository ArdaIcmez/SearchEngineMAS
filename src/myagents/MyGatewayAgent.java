package myagents;
import jade.content.ContentElement;
import jade.content.lang.*;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.*;

import ontologies.profile.*;
import ontologies.search.*;

public class MyGatewayAgent extends GatewayAgent {
	/**
	 * 
	 */
	
	private Codec codec = new SLCodec();
	private Ontology profileOntology = ProfileOntology.getInstance();
	private Ontology searchOntology = SearchOntology.getInstance();
	AMSAgentDescription [] agents =null;
	ProfileOperation profOperation;
	SearchOperation searchOperation;
	protected void processCommand(Object obj) {
		
		System.out.println("\n### GatewayAgent : Got "+obj.getClass()+" from servlet. ###");
		
		// Requested operation belongs to User Organization
		if(obj instanceof ProfileOperation) {
			
			
			this.profOperation = (ProfileOperation) obj;
			// Here, I can ask agents & send only to profile agents
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			
			// Setting up the broadcast & Prepare the message to send
			msg.setLanguage(codec.getName());
			msg.setOntology(profileOntology.getName());
			
			for (AMSAgentDescription recAgents : agents) {
				if(recAgents.getName().equals(this.getAID())==false) {
					try {
						getContentManager().fillContent(msg, new Action(recAgents.getName(),this.profOperation));
						msg.addReceiver(recAgents.getName());
					} catch ( CodecException | OntologyException e) {
						e.printStackTrace();
					}
				}
			}
            send(msg);
		}
		
		// Requested operation belongs to Search Organization
		else if(obj instanceof SearchOperation) {
			this.searchOperation = (SearchOperation) obj;
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setLanguage(codec.getName());
			msg.setOntology(searchOntology.getName());
			
			for (AMSAgentDescription recAgents : agents) {
				if(recAgents.getName().equals(this.getAID())==false) {
					try {
						getContentManager().fillContent(msg, new Action(recAgents.getName(),this.searchOperation));
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
    	getContentManager().registerOntology(searchOntology);
    	
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
        
        //Print out all the agents in the system
        AID myID = this.getAID();
        for (AMSAgentDescription agent : agents) {
        	AID agentID = agent.getName();
        	System.out.println(
    				( agentID.equals( myID ) ? "*** " : "    ")
    			     + ": " + agentID.getName() 
    			);
        }
        
        // Adding the Message receiver behaviour
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
    								profOperation.setUser((UserBean)result.getValue());
    								releaseCommand(profOperation);
    							}
    							else if(result.getValue() instanceof SearchBean) {
    								searchOperation.setSearch((SearchBean)result.getValue());
    								releaseCommand(searchOperation);
    							}
    							else if(result.getValue() instanceof jade.util.leap.List) {
    								searchOperation.setResultList((jade.util.leap.List)result.getValue());
    								releaseCommand(searchOperation);
    							}
    							System.out.println("### GatewayAgent : "+result.getClass()+" released. ###");
    						}
    						
    					} catch ( CodecException | OntologyException e) {
    						e.printStackTrace();
    					}
                	}
                } else block();
             }
        		});
        
        super.setup();
    }
}
