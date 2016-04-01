package ontologies.search;


import jade.content.onto.*;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

public class SearchOntology extends Ontology implements SearchVocabulary {
	/**
	 * Ontology for Search action, using SearchVocabulary as its knowledge source
	 * defined in the same package
	 * 
	 * @author arda
	 */
	public static final String ONTOLOGY_NAME = "Search-Ontology";
	
	private static Ontology instance = new SearchOntology();
	
	public static Ontology getInstance() { return instance; }
	
	private SearchOntology() {
		
		super(ONTOLOGY_NAME,BasicOntology.getInstance());
		
		try {
			
			//----------------------------------------------------Adding Concepts
			
			//Search
			ConceptSchema cs = new ConceptSchema(SEARCH);
			add(cs, SearchBean.class);
			cs.add(SEARCH_WORD, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
			cs.add(SEARCH_RESULT_LIST,(PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			
			//SearchProblem
			add(cs = new ConceptSchema(SEARCH_PROBLEM), SearchProblem.class);
			cs.add(SEARCH_PROBLEM_NUM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
			cs.add(SEARCH_PROBLEM_MSG, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
			
			//PredObj
			add(cs = new ConceptSchema(PRED_OBJ),PredObj.class);
			cs.add(PRED_OBJ_OBJ, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
			cs.add(PRED_OBJ_PREDICATE, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
			
			//SearchObject
			add(cs = new ConceptSchema(SEARCH_OBJECT),SearchObject.class);
			cs.add(SEARCH_OBJECT_SUBJECT, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
			cs.add(SEARCH_OBJECT_CONTENTLIST, (ConceptSchema) getSchema(PRED_OBJ),0,ObjectSchema.UNLIMITED);
			
			//----------------Adding AgentActions
			
			//SearchOperation
			AgentActionSchema as = new AgentActionSchema(SEARCH_OPERATION);
			add(as, SearchOperation.class);
			as.add(SEARCH_OPERATION_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.MANDATORY);
			as.add(SEARCH_OPERATION_SEARCH, (ConceptSchema) getSchema(SEARCH),ObjectSchema.OPTIONAL);
			as.add(SEARCH_OPERATION_NICKNAME, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			as.add(SEARCH_OPERATION_URL, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
			as.add(SEARCH_OPERATION_RESULTLIST, (ConceptSchema) getSchema(SEARCH_OBJECT),0,ObjectSchema.UNLIMITED);
		}
		catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}
}
