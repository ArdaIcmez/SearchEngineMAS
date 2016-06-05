package roles.personalize;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import organizations.PersonalizeOrganization;

public class RankingHandle extends PersonalizeOrganization {
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();
	private String indexLocation;
	public RankingHandle (String indexDir) {
		indexLocation = "/tmp/"+indexDir;
		FSDirectory dir;
		IndexWriterConfig config;
		try {
			dir = FSDirectory.open(new File(indexLocation));
			config = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
			writer = new IndexWriter(dir, config);
		} catch (IOException e) {
			System.out.println("RankingHandle constructor exception");
			e.printStackTrace();
		}
		 
	}
	public void indexFileOrDirectory(String fileName) throws IOException {
		 addFiles(new File(fileName));
		 int originalNumDocs = writer.numDocs();
		 for (File f : queue) {
		   FileReader fr = null;
		   try {
		     Document doc = new Document();
		     fr = new FileReader(f);
		     doc.add(new TextField("contents", fr));
		     doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		     doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		     writer.addDocument(doc);
		     System.out.println("Added: " + f);
		   } catch (Exception e) {
		     System.out.println("Could not add: " + f);
		   } finally {
		     fr.close();
		   }
		 }
		 int newNumDocs = writer.numDocs();
		 System.out.println("");
		 System.out.println("************************");
		 System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		 System.out.println("************************");

		 queue.clear();
	 }
	private void addFiles(File file) {

	    if (!file.exists()) {
	      System.out.println(file + " does not exist.");
	    }
	    if (file.isDirectory()) {
	      for (File f : file.listFiles()) {
	        addFiles(f);
	      }
	    } else {
	      String filename = file.getName().toLowerCase();
	      //Taramaya gerek kalmadan SPARQL ile direk at
	      
	      if (filename.endsWith(".rdf") || filename.endsWith(".owl")) {
	        queue.add(file);
	      } else {
	        System.out.println("Skipped " + filename);
	      }
	    }
	  }
	public void closeIndex() throws IOException {
	    writer.close();
	  }
	
public HashMap<String,Float> getRelevance(String searchQuery) throws IOException {
	//---------------------------------------------------------INDEX

	//---------------------------------------------------SEARCH
	 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
	 IndexSearcher searcher = new IndexSearcher(reader);
	 TopScoreDocCollector collector = TopScoreDocCollector.create(15,true);
	 HashMap<String,Float> resultTuples = new HashMap<String,Float>();
	 try {
	 Query q = new QueryParser( "contents", analyzer).parse(searchQuery);
	 
	 searcher.search(q, collector);
	 ScoreDoc[] hits = collector.topDocs().scoreDocs;
	 
	 
	 //-------------------------------------------------- PRINT RESULTS
	   
       System.out.println("Found " + hits.length + " hits.");
       for(int i=0;i<hits.length;++i) {
         int docId = hits[i].doc;
         Document d = searcher.doc(docId);
        
         resultTuples.put(d.get("filename").toString(), hits[i].score);
         //System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
       }
       
       /*Set<Map.Entry<String, Float>> set = resultTuples.entrySet();

       for (Map.Entry<String, Float> me : set) {
         System.out.println("Filename :"+me.getKey() +" Score : "+ me.getValue());

       } */
       
	 }
	 catch(Exception e) {
		 e.printStackTrace();
		 resultTuples = null;
	 }
	 
	return resultTuples;
}
public HashMap<String,Float> getListRelevance(jade.util.leap.List searchList) throws IOException {
	 
	HashMap<String,Float> resultTuples = new HashMap<String,Float>();
	
	//----------------------------------------------------INDEX
	String searchQuery = "";
	jade.util.leap.Iterator iter = searchList.iterator();
	while(iter.hasNext()) {
		searchQuery += iter.next().toString();
		if(iter.hasNext()) {
			searchQuery += " ";
		}
	}
	if(searchQuery == "") {
		return resultTuples;
	}
	//---------------------------------------------------SEARCH
	 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
	 IndexSearcher searcher = new IndexSearcher(reader);
	 TopScoreDocCollector collector = TopScoreDocCollector.create(15,true);
	
	 try {
	 Query q = new QueryParser( "contents", analyzer).parse(searchQuery);
	 
	 searcher.search(q, collector);
	 ScoreDoc[] hits = collector.topDocs().scoreDocs;
	 
	 
	 //-------------------------------------------------- PRINT RESULTS
	   
       //System.out.println("Found " + hits.length + " hits.");
       for(int i=0;i<hits.length;++i) {
         int docId = hits[i].doc;
         Document d = searcher.doc(docId);
        
         resultTuples.put(d.get("filename").toString(), hits[i].score);
         //System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
       }
       
       /*Set<Map.Entry<String, Float>> set = resultTuples.entrySet();

       for (Map.Entry<String, Float> me : set) {
         System.out.println("Filename :"+me.getKey() +" Score : "+ me.getValue());

       } */
       
	 }
	 catch(Exception e) {
		 e.printStackTrace();
		 resultTuples = null;
	 }
	 
	return resultTuples;
}
public jade.util.leap.List calculateRelevance(HashMap<String,Float> v1,HashMap<String,Float> v2 ,
		HashMap<String,Float>v3, float quer, float inter, float hist, jade.util.leap.List pageList) throws IOException {
	
	jade.util.leap.List resultList = new jade.util.leap.ArrayList();
    
	jade.util.leap.Iterator iter = pageList.iterator();
	while(iter.hasNext()) {
		String myKey = iter.next().toString();
		
		float totalScore=0;
		if(v1.get(myKey) != null) {
			totalScore += quer*v1.get(myKey);
		}
    	if(v2.get(myKey) != null) {
    		totalScore += inter*v2.get(myKey);
    	}
    	if(v3.get(myKey) != null) {
    		totalScore += hist*v3.get(myKey);
    	}	
    	String element = myKey + ":" + Float.toString(totalScore);
    	resultList.add(element);
	}

	return resultList;
}
}
