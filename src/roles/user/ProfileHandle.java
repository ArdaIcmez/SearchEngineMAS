package roles.user;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.iri.impl.Main;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

import jade.util.leap.Iterator;
import ontologies.profile.UserBean;
import organizations.UserOrganization;

public class ProfileHandle extends UserOrganization{

	private String myFile;
	public ProfileHandle()
	{
		myFile = "/home/arda/workspace/SearchEngine/rfds/gsu.owl" ;
	}
	public boolean doAuthentication(UserBean usrBean)
	{
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		System.out.println(usrBean.getNickname()+usrBean.getPassword());
		// This part needs to be dynamic
		Model model = FileManager.get().loadModel(this.myFile);

		String queryString =
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX User: <http://www.aicmez.com/user/#> " +
				"SELECT * WHERE { " +
				" ?s ?p ?x ."+
				" ?s User:Username \""+usrBean.getNickname()+"\" ."+
				" ?s User:Password \""+usrBean.getPassword()+"\" ."+
				"}"; 
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		try {
			ResultSet results = qexec.execSelect();
			if (results.hasNext()) {
				return true;
			}
		} finally {
			qexec.close();
		}
		return false;
	}
	
	public void doRegister(String username, String pass)  {
		final String modelText = "\n"
                + "<rdf:RDF \n"
                + "    	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n"
                + "	   	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"  \n "
                + "		xmlns:foaf=\"http://xmlns.com/foaf/0.1/\"   \n"
                + "    xmlns=\"http://www.aicmez.com/user/#\"   \n"
                + "    xmlns:User=\"http://www.aicmez.com/user/#\"   \n"
                + "    xml:base=\"http://www.aicmez.com/user/\">    \n"
                + "    <rdf:Description rdf:ID=\""+username+"\">\n"
                + "   <rdf:type rdf:resource=\"http://www.aicmez.com/user#\"/>  \n"
                + "   <User:Username>"+username+"</User:Username>	\n "
                + "   <User:Password>"+pass+"</User:Password>	\n "
                + "  </rdf:Description>\n" + "</rdf:RDF>";
		final Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(modelText.getBytes()), null);
        
        
        System.out.println("NEW USER REGISTRATION!!");
        model.write(System.out, "TTL");
	
        Model oldModel = FileManager.get().loadModel(this.myFile);
		oldModel.add(model);
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

	public UserBean doPopulate(UserBean user, String link)  {
		user.setBrowseHistory(new jade.util.leap.ArrayList());
		user.setInterests(new jade.util.leap.ArrayList());
		String queryString =
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX User: <http://www.aicmez.com/user/#> " +
				"SELECT * WHERE { " +
				" User:"+user.getNickname()+" ?predicate ?x ."+
				"}"; 
		Model model = null;
		if (link != "")
		{
			model = FileManager.get().loadModel(link);
		}
		else {
			model = FileManager.get().loadModel(this.myFile);			
		}

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		
		try {
			
			ResultSet results = qexec.execSelect();
			//ResultSetFormatter.out(System.out, results, query) ;
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("x");
				Resource pred = soln.getResource("predicate");
				if ( obj.isLiteral() )  {
					((Literal)obj).getLexicalForm() ;
				}
				if ( pred.isLiteral() )  {
					((Literal)pred).getLexicalForm() ;
				}
				switch (pred.getLocalName().toLowerCase())
				{
					case "interests":
						jade.util.leap.List intList = user.getInterests();
						intList.add(obj.toString());
						user.setInterests(intList);
						break;
					case "job":
						user.setJob(obj.toString());
						break;
					case "name":
						user.setName(obj.toString());
						break;
					case "browsehistory":
						jade.util.leap.List browseList = user.getBrowseHistory();
						browseList.add(obj.toString());
						user.setBrowseHistory(browseList);
						break;
					case "role":
						user.setRole(obj.toString());
						break;
					case "surname":
						user.setSurname(obj.toString());
						break;
					default:
						break;	
				}
			}
		} finally {
			qexec.close();
		}
		return user;
	}
	
	public UserBean doEditPass(UserBean user, String newPass) {
		String username = user.getNickname();
		String password = user.getPassword();
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"DELETE DATA { "+
        		"User:"+username+" User:Password \""+password+"\" "+
        		"};"+
        		"INSERT DATA { "+
        		"User:"+username+" User:Password \""+newPass+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
		user.setPassword(newPass);
		
		return user;
	}
	
	public UserBean doEditName(UserBean user, String newName) {
		String username = user.getNickname();
		String name = user.getName();
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"DELETE DATA { "+
        		"User:"+username+" User:Name \""+name+"\" "+
        		"};"+
        		"INSERT DATA { "+
        		"User:"+username+" User:Name \""+newName+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
		user.setName(newName);
		
		return user;
	}
	
	public UserBean doEditSurname(UserBean user, String newSurname) {
		String username = user.getNickname();
		String surname = user.getSurname();
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"DELETE DATA { "+
        		"User:"+username+" User:Surname \""+surname+"\" "+
        		"};"+
        		"INSERT DATA { "+
        		"User:"+username+" User:Surname \""+newSurname+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
		user.setSurname(newSurname);
		
		return user;
	}
	
	public UserBean doEditJob(UserBean user, String newJob) {
		String username = user.getNickname();
		String job = user.getJob();
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"DELETE DATA { "+
        		"User:"+username+" User:Job \""+job+"\" "+
        		"};"+
        		"INSERT DATA { "+
        		"User:"+username+" User:Job \""+newJob+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
		user.setJob(newJob);
		
		return user;
	}
	
	public UserBean doEditRole(UserBean user, String newRole) {
		String username = user.getNickname();
		String role = user.getRole();
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"DELETE DATA { "+
        		"User:"+username+" User:Role \""+role+"\" "+
        		"};"+
        		"INSERT DATA { "+
        		"User:"+username+" User:Role \""+newRole+"\" "+
        		"}";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
		user.setRole(newRole);
		
		return user;
	}
	
	public UserBean doEditGeneral(UserBean user, UserBean modifyUser) {
		String pass = modifyUser.getPassword();
		String name = modifyUser.getName();
		String surn = modifyUser.getSurname();
		String job = modifyUser.getJob();
		String role = modifyUser.getRole();
		jade.util.leap.List interList = modifyUser.getInterests();
		jade.util.leap.List histList = modifyUser.getBrowseHistory();
		if(user.getPassword() != pass) 	{
			user = this.doEditPass(user, pass);
		}
		if(user.getName() != name)	{
			user = this.doEditName(user, name);
		}
		if(user.getSurname() != surn) {
			user = this.doEditSurname(user, surn);
		}
		if(user.getJob() != job) {
			user = this.doEditJob(user, job);
		}
		if(user.getRole() != role) {
			user = this.doEditRole(user, role);
		}
		
		String deleteInterests = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        		"DELETE { "+
        		"User:"+user.getNickname()+" User:Interests ?x ."+
        		"}"+
        		"WHERE {"+
        		"SELECT * WHERE { " +
				" User:"+user.getNickname()+" User:Interests ?x ."+
				"} }";
		String deleteHistory = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        		"DELETE { "+
        		"User:"+user.getNickname()+" User:BrowseHistory ?x ."+
        		"}"+
        		"WHERE {"+
        		"SELECT * WHERE { " +
				" User:"+user.getNickname()+" User:BrowseHistory ?x ."+
				"} }";
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, deleteInterests);
		this.doUpdateRDF(oldModel, deleteHistory);
		
		
		String intQuery = "";
		String hisQuery = "";
		Iterator iter = interList.iterator();
		while(iter.hasNext()) {
			intQuery+="User:"+user.getNickname()+" User:Interests \""+iter.next().toString()+"\" .";
		}
		iter = histList.iterator();
		while(iter.hasNext()) {
			hisQuery+="User:"+user.getNickname()+" User:BrowseHistory \""+iter.next().toString()+"\" .";
		}
		user.setInterests(interList);
		user.setBrowseHistory(histList);
		
		String upQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"INSERT DATA { "+
        		intQuery +
        		hisQuery +
        		"}";
		this.doUpdateRDF(oldModel, upQuery); 
		return user;
	}
	
	public void doDeleteProfile(String username) {
		String editQuery = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        		"DELETE { "+
        		"User:"+username+" ?p ?x ."+
        		"}"+
        		"WHERE {"+
        		"SELECT ?p ?x WHERE { " +
				" User:"+username+" ?p ?x ."+
				"} }";
		
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model oldModel = FileManager.get().loadModel(this.myFile);
		this.doUpdateRDF(oldModel, editQuery);
	}
	
	public jade.util.leap.List doPageResult(String url)
	{
		jade.util.leap.List tempList = new jade.util.leap.ArrayList();
		jade.util.leap.List resultList = new jade.util.leap.ArrayList();
		String myLink = "/home/arda/workspace/SearchEngine/rfds/"+url;
		Model model = FileManager.get().loadModel(myLink);
		String getNicknames = ""+
        		"PREFIX User: <http://www.aicmez.com/user/#> \n" +
        		"SELECT ?x WHERE { " +
				"?s User:Username ?x ." +
				"}"; 
		Query query = QueryFactory.create(getNicknames);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				RDFNode obj = soln.get("x");
				if ( obj.isLiteral() )  {
					((Literal)obj).getLexicalForm() ;
				}
				UserBean usr = new UserBean();
				usr.setNickname(obj.toString());
				tempList.add(usr);
			}
		} finally {
			qexec.close();
		}
		Iterator iter = tempList.iterator();
		while(iter.hasNext()) {
			UserBean user = (UserBean)iter.next();
			user = this.doPopulate(user,myLink);
			resultList.add(user);
		}
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
