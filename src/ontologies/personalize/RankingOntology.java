package ontologies.personalize;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

public class RankingOntology extends Ontology implements RankingVocabulary{
	/**
	 * Ontology for Profile Organization, using ProfileVocabulary
	 * 
	 * @author arda
	 */
	public static final String ONTOLOGY_NAME = "Ranking-Ontology";
	
	private static Ontology instance = new RankingOntology();
	
	public static Ontology getInstance() { return instance; }
	
	private RankingOntology(){
		super(ONTOLOGY_NAME,BasicOntology.getInstance());
		
		try {
			//-----------Adding Concepts
			
			//RankBean
			ConceptSchema cs = new ConceptSchema(RANK);
			add(cs,RankBean.class);
			cs.add(RANK_RESULTLIST, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			cs.add(RANK_INTERESTLIST, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			cs.add(RANK_HISTORYLIST, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			cs.add(RANK_SEARCHQUERY, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
		
			//----------------Adding AgentActions
			
			//RankingOperation
			AgentActionSchema as = new AgentActionSchema(RANKING_OPERATION);
			add(as, RankingOperation.class);
			as.add(RANKING_OPERATION_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
			as.add(RANKING_OPERATION_RANK, (ConceptSchema) getSchema(RANK), ObjectSchema.MANDATORY);
			as.add(RANKING_OPERATION_RANKEDLIST, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
						
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
