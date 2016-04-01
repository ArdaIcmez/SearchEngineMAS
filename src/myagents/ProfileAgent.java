package myagents;


import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.lang.acl.*;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.*;
import ontologies.profile.*;
import roles.user.ProfileHandle;


public class ProfileAgent extends Agent implements ProfileVocabulary{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object obj = null;
	private ProfileHandle roleExecution = new ProfileHandle();
	private Codec codec = new SLCodec();
	private Ontology ontology = ProfileOntology.getInstance();
	
	protected void setup()
	{
		// Register language and ontology
	      getContentManager().registerLanguage(codec);
	      getContentManager().registerOntology(ontology);
	      
	      
	    // Set the behaviour to receive messages.
		 addBehaviour(new CyclicBehaviour(this)
     	{
		public void action() {
     		 
             ACLMessage msg = receive();
             System.out.println("### ProfileAgent : Message received ###");
             if (msg!=null)    {
            	 try {
					ContentElement content = getContentManager().extractContent(msg);
					Concept action = ((Action)content).getAction();
					
					switch(msg.getPerformative()) {
					
						case(ACLMessage.REQUEST) :
							
							System.out.print("\n###Request from : " + msg.getSender().getLocalName());
							if(action instanceof ProfileOperation) {
								addBehaviour(new HandleProfileOperation(myAgent, msg));
							}
							else replyNotUnderstood(msg);
							break;
						case(ACLMessage.INFORM) :
							System.out.print("\n###Inform from : " + msg.getSender().getLocalName());
							break;
					}
				} catch ( CodecException | OntologyException e) {
					System.out.println("\n ### ProfileAgent does not know this ontology");
				} 
             } 
             else {
            	 block();
             }
          }
     		});
	}
	
	class HandleProfileOperation extends OneShotBehaviour {
		// ------------------------------------------------  Handler for an Profile Operation request

		      private ACLMessage request;

		      HandleProfileOperation(Agent a, ACLMessage request) {

		         super(a);
		         this.request = request;
		      }

		      public void action() {

		         try {
		            ContentElement content = getContentManager().extractContent(request);
		            ProfileOperation po = (ProfileOperation)((Action)content).getAction();
		            ACLMessage reply = request.createReply();
		            Object obj = processOperation(po);
		            if (obj == null) replyNotUnderstood(request);
		            else {
		               reply.setPerformative(ACLMessage.INFORM);
		               Result result = new Result((Action)content, obj);
		               getContentManager().fillContent(reply, result);
		               send(reply);
		               System.out.println("\n### ProfileAgent : Operation processed. ###");
		            }
		         }
		         catch(Exception ex) { ex.printStackTrace(); }
		      }
		   }
	
	void replyNotUnderstood(ACLMessage msg) {
		// ----------------------------------------- Message is not understood

		      try {
		         ContentElement content = getContentManager().extractContent(msg);
		         ACLMessage reply = msg.createReply();
		         reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		         getContentManager().fillContent(reply, content);
		         send(reply);
		         System.out.println("\n### Profile Agent reply : Not understood! ###");
		      }
		      catch(Exception ex) { ex.printStackTrace(); }
		   }
	
	Object processOperation(ProfileOperation po) {
	// -------------------------------------------
		UserBean usr = po.getUser();
		if (po.getType() == MODIFY_PROFILE) {
			usr = roleExecution.doEditGeneral(usr,po.getModifyUser());
			System.out.println("### ProfileAgent : MODIFY_PROFILE called ###");
	    }
		else if (po.getType() == AUTHENTICATE_USER) {
			usr.setAuthentication(roleExecution.doAuthentication(usr));
			System.out.println("### ProfileAgent : AUTHENTICATE_USER called ###");
	    }
		else if (po.getType() == REGISTER_USER) {
			roleExecution.doRegister(usr.getNickname(), usr.getPassword());
			System.out.println("### ProfileAgent : REGISTER_USER called ###");
		}
		else if (po.getType() == DELETE_USER) {
			roleExecution.doDeleteProfile(usr.getNickname());
			System.out.println("### ProfileAgent : DELETE_USER called ###");
		}
		else if (po.getType() == POPULATE_PROFILE) {
			usr = roleExecution.doPopulate(usr, "");
			System.out.println("### ProfileAgent : POPULATE_PROFILE called ###");
		}
		else {
			return null;
		}
	     return usr;
	   }
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}
}
