package myagents;


import java.io.IOException;
import java.util.HashMap;

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
import ontologies.personalize.RankBean;
import ontologies.personalize.RankingOntology;
import ontologies.personalize.RankingOperation;
import ontologies.personalize.RankingVocabulary;
import ontologies.profile.*;
import roles.personalize.RankingHandle;


public class DocumentAgent extends Agent implements RankingVocabulary{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object obj = null;
	private RankingHandle roleExecution = new RankingHandle("QueryDocument");
	private Codec codec = new SLCodec();
	private Ontology ontology = RankingOntology.getInstance();
	private float constQuery = (float) 0.8;
	private float constInterest = (float) 0.1;
	private float constHistory = (float) 0.1;
	
	protected void setup()
	{
		//index files
		try {
			roleExecution.indexFileOrDirectory("/home/arda/workspace/SearchEngine/rfds");
			roleExecution.closeIndex();
		} catch (IOException e1) {
			System.out.println("Error indexing "  + " : " + e1.getMessage()); 
			e1.printStackTrace();
		}
		
		// Register language and ontology
	      getContentManager().registerLanguage(codec);
	      getContentManager().registerOntology(ontology);
	      
	      
	    // Set the behaviour to receive messages.
		 addBehaviour(new CyclicBehaviour(this)
     	{
		public void action() {
     		 
             ACLMessage msg = receive();
             System.out.println("### QueryDocumentAgent : Message received ###");
             if (msg!=null)    {
            	 try {
					ContentElement content = getContentManager().extractContent(msg);
					Concept action = ((Action)content).getAction();
					
					switch(msg.getPerformative()) {
					
						case(ACLMessage.REQUEST) :
							
							System.out.print("\n###Request from : " + msg.getSender().getLocalName());
							if(action instanceof RankingOperation) {
								addBehaviour(new HandleProfileOperation(myAgent, msg));
							}
							else replyNotUnderstood(msg);
							break;
						case(ACLMessage.INFORM) :
							System.out.print("\n###Inform from : " + msg.getSender().getLocalName());
							break;
					}
				} catch ( CodecException | OntologyException e) {
					System.out.println("\n ### QueryDocumentAgent does not know this ontology");
					//e.printStackTrace();
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
		            RankingOperation ro = (RankingOperation)((Action)content).getAction();
		            ACLMessage reply = request.createReply();
		            Object obj = processOperation(ro);
		            
		            if (obj == null) replyNotUnderstood(request);
		            else {
		            	ro.setRankedList((jade.util.leap.List)obj);
		            	reply.setPerformative(ACLMessage.REQUEST);
		            	reply.setSender(getAID());
		              // Result result = new Result((Action)content, obj);
		            	getContentManager().fillContent(reply,new Action(request.getSender(),ro));
		              // getContentManager().fillContent(reply, result);
		            	send(reply);
		            	System.out.println("\n### QueryDocumentAgent : Operation processed. ###");
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
		         reply.setSender(this.getAID());
		         reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		         getContentManager().fillContent(reply, content);
		         send(reply);
		         System.out.println("\n### QueryDocumentAgent reply : Not understood! ###");
		      }
		      catch(Exception ex) { ex.printStackTrace(); }
		   }
	
	Object processOperation(RankingOperation ro) {
	// -------------------------------------------
		RankBean rank = ro.getRank();
		if(ro.getType() == RANK_RESULT){
			try {
				HashMap<String,Float> v1 = roleExecution.getRelevance(rank.getSearchQuery());
				HashMap<String,Float> v2 = roleExecution.getListRelevance(rank.getInterestList());
				HashMap<String,Float> v3 = roleExecution.getListRelevance(rank.getHistoryList());
				jade.util.leap.List resultVector = roleExecution.calculateRelevance(v1, v2, v3,
						constQuery,constInterest,constHistory,rank.getResultList());
				return resultVector;
			} catch (IOException e) {
				System.out.println("###QueryDocumentAgent : Job could not be processed ###");
				e.printStackTrace();
			}
		}
	     return null;
	   }
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}
}
