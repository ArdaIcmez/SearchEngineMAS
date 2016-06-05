package myagents;


import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import ontologies.personalize.RankBean;
import ontologies.personalize.RankingOntology;
import ontologies.personalize.RankingOperation;
import ontologies.personalize.RankingVocabulary;
import ontologies.search.*;
import roles.search.SearchHandle;

public class SearchAgent extends Agent implements SearchVocabulary,RankingVocabulary {
	private static final long serialVersionUID = 1L;
	private Object obj = null;
	private ACLMessage gatewayRequest = null;
	private SearchBean gatewayBean = null;
	// Assume a role from Search Organization
	private SearchHandle roleExecution = new SearchHandle();
	
	// Fill up the knowledge base
	AMSAgentDescription [] agents =null;
	private Codec codec = new SLCodec();
	private Ontology ontology = SearchOntology.getInstance();
	private Ontology rankOntology = RankingOntology.getInstance();
	
	// Ranking vectors
	private int vectors = 0;
	private jade.util.leap.List queryVector = new jade.util.leap.ArrayList();
	private jade.util.leap.List interestVector = new jade.util.leap.ArrayList();
	private jade.util.leap.List historyVector = new jade.util.leap.ArrayList();
	
	protected void setup()
	{
		getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);
	    getContentManager().registerOntology(rankOntology);
	    
		// Adding the behaviour to receive messages
		addBehaviour(new CyclicBehaviour(this)
     	{
		public void action() {
     		 
             ACLMessage msg = receive();
             System.out.println("### SearchAgent : Message received ###");
             if (msg!=null)    {
            	 try {
					ContentElement content = getContentManager().extractContent(msg);
					Concept action = ((Action)content).getAction();
					
					switch(msg.getPerformative()) {
					
						case(ACLMessage.REQUEST) :
							
							System.out.print("\n### SearchAgent : Request from : " + msg.getSender().getLocalName());
							if(action instanceof SearchOperation) {
								addBehaviour(new HandleOperation(myAgent, msg));
							}
							else if(action instanceof RankingOperation) {
								addBehaviour(new HandleRanking(myAgent,msg));
							}
							else {replyNotUnderstood(msg); }
							break;
						case(ACLMessage.INFORM) :
							System.out.print("\n### SearchAgent : Inform from : " + msg.getSender().getLocalName());
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
		            	
		            	if((so.getNickname() != null) && (so.getType() == SEARCH_KEYWORD) ) {
		            		//---------------------------------------- prepare the RankingOperation here
		            		jade.util.leap.List histList = roleExecution.doGetHistory(so.getNickname());
			            	jade.util.leap.List interList = roleExecution.doGetInterest(so.getNickname());
			            	gatewayRequest = request;
							gatewayBean = so.getSearch();
							RankBean bean = new RankBean();
							bean.setHistoryList(histList);
							bean.setInterestList(interList);
							bean.setResultList(so.getSearch().getResultList());
							bean.setSearchQuery(so.getSearch().getWord());
							RankingOperation ro = new RankingOperation();
							ro.setType(RANK_RESULT);
							ro.setRank(bean);
							
							//-----------------------------------------------------Find DocumentAgents
							AID docAgent=null;
			            	ACLMessage newMsg = new ACLMessage(ACLMessage.REQUEST);
							newMsg.setLanguage(codec.getName());
							newMsg.setOntology(rankOntology.getName());
							newMsg.setSender(getAID());
							for(AMSAgentDescription tempAgent : agents){
								if(tempAgent.getName().getLocalName().equals("QueryDocumentAgent")||
										tempAgent.getName().getLocalName().equals("InterestDocumentAgent") ||
										tempAgent.getName().getLocalName().equals("HistoryDocumentAgent")){
									
									docAgent = tempAgent.getName();
									getContentManager().fillContent(newMsg,new Action(docAgent,ro));
									newMsg.addReceiver(docAgent);
									System.out.println("sent to "+docAgent+docAgent.getLocalName());
								}
							}
							send(newMsg);
						//--------------------------------------------Send completed, needs to wait

		            	}
		            	else {
		            		reply.setPerformative(ACLMessage.INFORM);
							Result result = new Result((Action)content, obj);
							getContentManager().fillContent(reply, result);
							send(reply);
							System.out.println("\n### SearchAgent : Operation processed. ###");
		            	}
		            }
		         }
		         catch(Exception ex) { ex.printStackTrace(); }
		      }
		   }
	
	class HandleRanking extends OneShotBehaviour {
		// ------------------------------------------------  Handler for a Ranking Operation request

		      private ACLMessage request;

		      HandleRanking(Agent a, ACLMessage request) {

		         super(a);
		         this.request = request;
		      }

		      public void action() {

		         try {
		            ContentElement content = getContentManager().extractContent(request);
		            RankingOperation ro = (RankingOperation)((Action)content).getAction();

		            if(request.getSender() != null) {
		           		String sender = request.getSender().getLocalName().toString();
		           		System.out.println("### SearchAgent : Got a vector! "+sender+"###");
		           		
		           		if(sender.equals("QueryDocumentAgent")) {
		           			vectors++;
		           			queryVector = ro.getRankedList();
		           			jade.util.leap.Iterator it = queryVector.iterator();
		           			while(it.hasNext()) {
		           				System.out.println(it.next().toString());
		           			}
		           		}
		           		else if(sender.equals("InterestDocumentAgent")) {
	            			vectors++;
	            			interestVector = ro.getRankedList();
		            		jade.util.leap.Iterator it = interestVector.iterator();
		            		while(it.hasNext()) {
		           				System.out.println(it.next().toString());
		           			}
		           		}
		           		else if(sender.equals("HistoryDocumentAgent")) {
		           			vectors++;
		           			historyVector = ro.getRankedList();
		           			jade.util.leap.Iterator it = historyVector.iterator();
		           			while(it.hasNext()) {
		           				System.out.println(it.next().toString());
		           			}
		           		}
		           		
		           		if(vectors == 3) {	
			            	// Select the best option!
		           			jade.util.leap.List rankedList = roleExecution.selectBest(queryVector,
		           					interestVector, historyVector);
		           			jade.util.leap.Iterator iter = rankedList.iterator();
		           			while(iter.hasNext()) {
		           				System.out.println(iter.next().toString());
		           			}
		           			
		           			gatewayBean.setResultList(rankedList);
			            	
				           	//Send back to the Gateway Agent
		           			ContentElement rankedContent = getContentManager().extractContent(gatewayRequest);
				           	Object obj = gatewayBean; //Change to best vector
				           	ACLMessage reply = gatewayRequest.createReply();
			            	reply.setPerformative(ACLMessage.INFORM);
							Result result = new Result((Action)rankedContent, obj);
							getContentManager().fillContent(reply, result);
							send(reply); 
							System.out.println("\n### SearchAgent : GOT ALL 3 VECTORS!. ###");
							vectors = 0;
				           	queryVector = new jade.util.leap.ArrayList();
				           	interestVector = new jade.util.leap.ArrayList();
				           	historyVector =  new jade.util.leap.ArrayList();
				        }
		            }
		            else {
		           		System.out.println("### SearchAgent : Sender is empty!! ###");
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
		         reply.setSender(this.getAID());
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
			else if (so.getType() == LOAD_PAGE) {
				jade.util.leap.List  resList = roleExecution.doPageResult(so.getUrl());
				System.out.println("### SearchAgent : LOAD_PAGE called ###");
				return resList;
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
