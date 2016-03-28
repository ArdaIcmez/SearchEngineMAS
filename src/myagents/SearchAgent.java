package myagents;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpSession;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import ontologies.search.*;
import roles.search.SearchHandle;

public class SearchAgent extends Agent implements SearchVocabulary {
	private static final long serialVersionUID = 1L;
	private Object obj = null;
	private SearchHandle roleExecution = new SearchHandle();
	private Codec codec = new SLCodec();
	private Ontology ontology = SearchOntology.getInstance();
	protected void setup()
	{
		getContentManager().registerLanguage(codec);
	     getContentManager().registerOntology(ontology);
		// Adding the behaviour to receive messages
		addBehaviour(new CyclicBehaviour(this)
     	{
		public void action() {
     		 
             ACLMessage msg = receive();
             System.out.println("### SearchAgent : Message received. ###");
             if (msg!=null)    {
            	 try {
					ContentElement content = getContentManager().extractContent(msg);
					Concept action = ((Action)content).getAction();
					
					switch(msg.getPerformative()) {
					
						case(ACLMessage.REQUEST) :
							
							System.out.print("\n###Request from : " + msg.getSender().getLocalName());
							if(action instanceof SearchOperation) {
								addBehaviour(new HandleOperation(myAgent, msg));
							}
							else replyNotUnderstood(msg);
							break;
						case(ACLMessage.INFORM) :
							System.out.print("\n###Inform from : " + msg.getSender().getLocalName());
							break;
					}
				} catch ( CodecException | OntologyException e) {
					System.out.println("\n ### SearchAgent does not know this ontology");
				} 
             } 
             else {
            	 block();
             }
          }
     		});
	}
	
	class HandleOperation extends OneShotBehaviour {
		// ------------------------------------------------  Handler for an Search Operation request

		      private ACLMessage request;

		      HandleOperation(Agent a, ACLMessage request) {

		         super(a);
		         this.request = request;
		      }

		      public void action() {

		         try {
		            ContentElement content = getContentManager().extractContent(request);
		            SearchOperation so = (SearchOperation)((Action)content).getAction();
		            ACLMessage reply = request.createReply();
		            Object obj = processOperation(so);
		            if (obj == null) replyNotUnderstood(request);
		            else {
		               reply.setPerformative(ACLMessage.INFORM);
		               Result result = new Result((Action)content, obj);
		               getContentManager().fillContent(reply, result);
		               send(reply);
		               System.out.println("\n### SearchAgent : Operation processed. ###");
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
		         System.out.println("\n### SearchAgent reply : Not understood! ###");
		      }
		      catch(Exception ex) { ex.printStackTrace(); }
		   }
	
	Object processOperation(SearchOperation so) {
		// -------------------------------------------
			SearchBean search = so.getSearch();
			if (so.getType() == SEARCH_KEYWORD) {
				roleExecution.doSearch(search);
				System.out.println("### SearchAgent : SEARCH_KEYWORD called ###");
		    }
			else if (so.getType() == ADD_SEARCH_HISTORY) {
				roleExecution.doAddHistory(so.getNickname(), search.getWord());
				System.out.println("### SearchAgent : ADD_SEARCH_HISTORY called ###");
		    }
			else {
				return null;
			}
		     return search;
		   }
	protected void takeDown()
	{
		try { DFService.deregister(this); }
        catch (Exception e) {}
	}
}
