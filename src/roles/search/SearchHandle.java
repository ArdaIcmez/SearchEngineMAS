package roles.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.iri.impl.Main;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
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
			Model myModel = FileManager.get().loadModel(myDirectory+name);
			myModels.add(new myTuple(myModel,name));
		}

		// Setting up the SPARQL query for search
		String queryString =
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"SELECT * WHERE { " +
				" ?subject ?predicate ?x " +
				"FILTER( ?x = \"" + bean.getWord() + "\" )" +
				"}"; 
		
		// Populating the search result list
		Query query = QueryFactory.create(queryString);
		jade.util.leap.List resultList = bean.getResultList();
		for(myTuple t : myModels)
		{
			QueryExecution qexec = QueryExecutionFactory.create(query,t.getModel());
			try {
				ResultSet results = qexec.execSelect();
				if (results.hasNext()) {
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
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("o");
				RDFNode subject = soln.get("s");
				RDFNode predicate = soln.get("p");
				Resource subj = subject.asResource();
				String subjectString = subj.getLocalName().toString();
				System.out.println("kak"+subj.toString()+"kak"+predicate.toString()+"kak"+obj.toString()+"kak");
				PredObj k = new PredObj();
				if(predicate.isResource()) {
					k.setPredicate(((Resource)predicate).getLocalName().toString());
				}
				else {
					k.setPredicate(predicate.toString());
				}
				k.setObj(obj.toString());
				
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