package ontologies;

import jade.content.onto.*;
import jade.content.schema.*;

public class ProfileOntology extends Ontology implements ProfileVocabulary{
	/**
	 * Ontology for Profile Organization, using ProfileVocabulary
	 * 
	 * @author arda
	 */
	public static final String ONTOLOGY_NAME = "Profile-Ontology";
	
	private static Ontology instance = new ProfileOntology();
	
	public static Ontology getInstance() { return instance; }
	
	private ProfileOntology(){
		super(ONTOLOGY_NAME,BasicOntology.getInstance());
		
		try {
			//-----------Adding Concepts
			
			//UserBean
			ConceptSchema cs = new ConceptSchema(USER);
			add(cs,UserBean.class);
			cs.add(USER_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
			cs.add(USER_USERNAME, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
			cs.add(USER_SURNAME, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
			cs.add(USER_PASSWORD, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
			cs.add(USER_ROLE, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
			cs.add(USER_JOB, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
			cs.add(USER_INTERESTS, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			cs.add(USER_HISTORY, (PrimitiveSchema) getSchema(BasicOntology.STRING),0,ObjectSchema.UNLIMITED);
			cs.add(USER_AUTHENTICATION, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN),ObjectSchema.OPTIONAL);
			
			//ProfileProblem
			add(cs = new ConceptSchema(PROFILE_PROBLEM), ProfileProblem.class);
			cs.add(PROFILE_PROBLEM_NUM, (PrimitiveSchema) getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
			cs.add(PROFILE_PROBLEM_MSG, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
		
			//----------------Adding AgentActions
			
			//ProfileOperation
			AgentActionSchema as = new AgentActionSchema(PROFILE_OPERATION);
			add(as, ProfileOperation.class);
			as.add(PROFILE_OPERATION_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
			as.add(PROFILE_OPERATION_USER, (ConceptSchema) getSchema(USER), ObjectSchema.MANDATORY);
			as.add(PROFILE_OPERATION_MODIFYUSER, (ConceptSchema) getSchema(USER), ObjectSchema.OPTIONAL);
			
			//ProfileInformation
			add(as = new AgentActionSchema(PROFILE_INFORMATION), ProfileInformation.class);
			as.add(PROFILE_INFORMATION_TYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
			as.add(PROFILE_INFORMATION_USERNAME, (PrimitiveSchema) getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
