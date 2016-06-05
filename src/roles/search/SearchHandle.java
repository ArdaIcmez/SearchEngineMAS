package roles.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.iri.impl.Main;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

import ontologies.search.PredObj;
import ontologies.search.SearchBean;
import ontologies.search.SearchObject;
import organizations.SearchOrganization;

public class SearchHandle extends SearchOrganization{
	private String myFile;
	public SearchHandle()
	{
		myFile = "/home/arda/workspace/SearchEngine/rfds/gsu.owl" ;
	}
	public void doSearch(SearchBean bean)
	{
		List<myTuple> myModels = new ArrayList<myTuple>();
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		
		// Getting the directory contents
		String myDirectory = "/home/arda/workspace/SearchEngine/rfds/";
		File f = new File(myDirectory);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		for(String name:names) {
			if(!names.equals("gsu.owl")) {
				System.out.println(myDirectory+name);
				Model myModel = FileManager.get().loadModel(myDirectory+name);
				myModels.add(new myTuple(myModel,name));
			}
		} 	
		String queryString;
		String unionList[] = bean.getWord().split(" ");
		
		if (unionList.length > 1) {
			
			queryString =
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"SELECT * WHERE { " +
					"{ ?subject ?predicate ?x " +
					"FILTER regex(?x,\"" + unionList[0] + "\",\"i\" )" +
					"}"; 
			
			for (int i = 1;i < unionList.length;i++){
		         queryString += 
						 " UNION { " +
						 " ?subject ?predicate ?x " +
						 "FILTER regex(?x,\"" + unionList[i] + "\",\"i\" )" +
						 "}"; 
		      }
			queryString += " }";
		}
		
		else {
			// Setting up the SPARQL query for search
			queryString =
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"SELECT * WHERE { " +
					" ?subject ?predicate ?x " +
					"FILTER regex(?x,\"" + bean.getWord() + "\",\"i\" )" +
					"}"; 
		}
		
		
		// Populating the search result list
		Query query = QueryFactory.create(queryString);
		jade.util.leap.List resultList = bean.getResultList();
		for(myTuple t : myModels)
		{
			QueryExecution qexec = QueryExecutionFactory.create(query,t.getModel());
			try {
				ResultSet results = qexec.execSelect();
				if (results.hasNext() && !t.getUri().equals("gsu.owl")) { // DEGISTIRME BURDA
					resultList.add(t.getUri());
				}
			} finally {
				qexec.close();
			}
		}
		bean.setResultList(resultList);
	}
	
	public void doAddHistory(String nickname,String word) {
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"INSERT DATA { "+
        		"User:"+nickname+" User:BrowseHistory \""+word+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
	}
	public jade.util.leap.List doPageResult(String url)
	{
		jade.util.leap.List resultList = new jade.util.leap.ArrayList();
		HashMap<String,jade.util.leap.List> hm = new HashMap<String,jade.util.leap.List>();
		String myLink = "/home/arda/workspace/SearchEngine/rfds/"+url;
		Model model = FileManager.get().loadModel(myLink);
		String getNicknames = ""+
        		"SELECT * WHERE { " +
				"?s ?p ?o ." +
				"}"; 
		Query query = QueryFactory.create(getNicknames);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		try {
			String pastSubject = "";
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("o");
				RDFNode subject = soln.get("s");
				RDFNode predicate = soln.get("p");
				Resource subj = subject.asResource();
				String subjectString = "";
				if(subj.getLocalName() != null) { 
					subjectString= subj.getLocalName().toString();
					pastSubject = subjectString;
				}
				else {
					subjectString = pastSubject;
				}
				
				PredObj k = new PredObj();
				if(predicate.isResource()) {
					k.setPredicate(((Resource)predicate).getLocalName().toString());
				}
				else {
					k.setPredicate(predicate.toString());
				}
				k.setObj(" ");
				
				if(obj != null) {
					k.setObj(obj.toString());
				}
				
				if(hm.containsKey(subjectString)) {
					jade.util.leap.List nodeList = (jade.util.leap.List)hm.get(subjectString);
					nodeList.add(k);
					hm.put(subjectString, nodeList);
				}
				else {
					jade.util.leap.List nodeList = new jade.util.leap.ArrayList();
					nodeList.add(k);
					hm.put(subjectString, nodeList);
				}
			}
		} finally {
			qexec.close();
		}

		for(String m : hm.keySet()) {
			SearchObject myObject = new SearchObject();
			myObject.setSubject(m);
			myObject.setContentList(hm.get(m));
			resultList.add(myObject);
		}
		System.out.println("MY SIZE : " + hm.size());
		return resultList;
	}
	public void doModifyPage(jade.util.leap.List modifyList,String url) {
		
	}
	public jade.util.leap.List doGetHistory(String nickname) {

		String queryString =
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX User: <http://www.aicmez.com/user/#> " +
				"SELECT * WHERE { " +
				" User:"+nickname+" User:BrowseHistory ?x ."+ // Genel olması lazım
				"}";
		Model model = FileManager.get().loadModel(this.myFile);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		jade.util.leap.List historyList = new jade.util.leap.ArrayList();
		try {
			
			ResultSet results = qexec.execSelect();
			//ResultSetFormatter.out(System.out, results, query) ;
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("x");
				if ( obj.isLiteral() )  {
					((Literal)obj).getLexicalForm() ;
				}
					historyList.add(obj.toString());
			}
		} finally {
			qexec.close();
		}
		return historyList;
	}
	public jade.util.leap.List doGetInterest(String nickname) {

		String queryString =
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX User: <http://www.aicmez.com/user/#> " +
				"SELECT * WHERE { " +
				" User:"+nickname+" User:Interests ?x ."+ // Genel olması lazım
				"}";
		Model model = FileManager.get().loadModel(this.myFile);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		jade.util.leap.List interestList = new jade.util.leap.ArrayList();
		try {
			
			ResultSet results = qexec.execSelect();
			//ResultSetFormatter.out(System.out, results, query) ;
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("x");
				if ( obj.isLiteral() )  {
					((Literal)obj).getLexicalForm() ;
				}
				interestList.add(obj.toString());
			}
		} finally {
			qexec.close();
		}
		return interestList;
	}
	public jade.util.leap.List selectBest(jade.util.leap.List querList,jade.util.leap.List interList,
			jade.util.leap.List histList){
		jade.util.leap.Iterator iter = querList.iterator();
		float qValue=0,iValue=0,hValue=0;
		HashMap<String,Float> qVector = new HashMap<String,Float>();
		HashMap<String,Float> iVector = new HashMap<String,Float>();
		HashMap<String,Float> hVector = new HashMap<String,Float>();
		while(iter.hasNext()) {
			String docScore = iter.next().toString();
			String temp[] = docScore.split(":");
			qValue += Float.parseFloat(temp[1]);
			qVector.put(temp[0], Float.parseFloat(temp[1]));
		}
		iter = histList.iterator();
		while(iter.hasNext()) {
			String docScore = iter.next().toString();
			String temp[] = docScore.split(":");
			hValue += Float.parseFloat(temp[1]);
			hVector.put(temp[0], Float.parseFloat(temp[1]));
		}
		iter = interList.iterator();
		while(iter.hasNext()) {
			String docScore = iter.next().toString();
			String temp[] = docScore.split(":");
			iValue += Float.parseFloat(temp[1]);
			iVector.put(temp[0], Float.parseFloat(temp[1]));
		}
		
		jade.util.leap.List rankedList = new jade.util.leap.ArrayList();
		if(qValue >= iValue && qValue >= hValue) { // Query Vector is the best
			Map.Entry<String, Float> elem = null;
			while(!qVector.isEmpty()){
				Set<Map.Entry<String, Float>> set = qVector.entrySet();
				float bigScore = 0;
				String bigDoc = "";
				for (Map.Entry<String, Float> me : set) {
			    	if(me.getValue() >= bigScore){
			    		bigDoc = me.getKey();
			    		bigScore = me.getValue();
			    		elem = me;
			    	}
			    }
				rankedList.add(bigDoc);
				System.out.println(bigDoc + bigScore +" QUERY ADDED TO LIST");
				qVector.remove(bigDoc);
				set.remove(elem);
			}
		}
		else if(iValue >= qValue && iValue >= hValue) { // Interest Vector is the best
			
			Map.Entry<String, Float> elem = null;
			while(!iVector.isEmpty()){
				float bigScore = 0;
				String bigDoc = "";
				Set<Map.Entry<String, Float>> set = iVector.entrySet();
				for (Map.Entry<String, Float> me : set) {
			    	if(me.getValue() >= bigScore){
			    		bigDoc = me.getKey();
			    		bigScore = me.getValue();
			    		elem = me;
			    	}
			    }
				rankedList.add(bigDoc);
				System.out.println(bigDoc + bigScore +" INTEREST ADDED TO LIST");
				iVector.remove(bigDoc);
				set.remove(elem);
			}
		}
		else{					// History Vector is the best
			Map.Entry<String, Float> elem = null;
			while(!hVector.isEmpty()){
				float bigScore = 0;
				String bigDoc = "";
				Set<Map.Entry<String, Float>> set = hVector.entrySet();
				for (Map.Entry<String, Float> me : set) {
			    	if(me.getValue() >= bigScore){
			    		bigDoc = me.getKey();
			    		bigScore = me.getValue();
			    		elem = me;
			    	}
			    }
				rankedList.add(bigDoc);
				System.out.println(bigDoc + bigScore +" HISTORY ADDED TO LIST");
				hVector.remove(bigDoc);
				set.remove(elem);
			}
		}
		return rankedList;
	}
	public void doUpdateRDF(Model oldModel, String editQuery)
	{
		UpdateAction.parseExecute(editQuery,oldModel);
        FileWriter out = null;
		try {
			out = new FileWriter( this.myFile );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
            oldModel.write( out, "RDF/XML" );
        }
        finally {
           try {
               out.close();
           }
           catch (IOException closeException) {
               // ignore
           }
        }
	}
}

class myTuple { 
	  public org.apache.jena.rdf.model.Model model; 
	  public java.lang.String uri; 
	  public myTuple(org.apache.jena.rdf.model.Model x,  java.lang.String  y) { 
	    this.model = x; 
	    this.uri = y; 
	  }
	  public org.apache.jena.rdf.model.Model getModel()
	  {
		  return this.model;
	  }
	  public java.lang.String getUri()
	  {
		  return this.uri;
	  }
} 