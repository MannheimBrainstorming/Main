import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RepositoryProcessLocation;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.ProcessEntry;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.tools.XMLException;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class Main {

	public static StringBuilder stringBuilder = new StringBuilder();
	
	public static List<String> csvFilePhrases(){
		
		// This function should be modified to load all csv files
		String csvFile = "C:\\Users\\hp\\workspace\\StanfordNLP\\Clustered Questions\\Topic 1-Q1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		List<String> phrasesToSend = new ArrayList<String>();
		try {

			br = new BufferedReader(new FileReader(csvFile));
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {

			        // use comma as separator
				String[] phrases = line.split(cvsSplitBy);

				//we do not save first line because it is just a question and set first line flag to false after first iteration
				if(!firstLine){
					System.out.println(phrases[0]);
					phrasesToSend.add(phrases[0]);
				}else{
					firstLine = false;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return phrasesToSend;
	}
	
	public static void preprocesPhrase(String phrase){
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		//String text = "Managers should motivate employees";

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(phrase);

		// run all Annotators on this text
		pipeline.annotate(document);
	
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
		  // traversing the words in the current sentence
		  // a CoreLabel is a CoreMap with additional token-specific methods
		  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		    // this is the text of the token
		    String word = token.get(TextAnnotation.class);
		    // this is the POS tag of the token
		    String pos = token.get(PartOfSpeechAnnotation.class);
		    // this is the NER label of the token
		    String ne = token.get(NamedEntityTagAnnotation.class);
		    
		    System.out.println("Word:("+word+") POS("+pos+") EntityName("+ne+")"+"\n");
		    
		    if(!pos.equalsIgnoreCase("RBR")&&
		       !pos.equalsIgnoreCase("CC") &&
		       !pos.equalsIgnoreCase("IN") &&
		       !pos.equalsIgnoreCase("CD") &&
		       !pos.equalsIgnoreCase("VB") &&
		       !pos.equalsIgnoreCase("DT") &&
		       !pos.equalsIgnoreCase("WRB") &&
		       !pos.equalsIgnoreCase("NN") &&
		       !pos.equalsIgnoreCase("PRP")&&
		       !pos.equalsIgnoreCase("PRP")&&
		       !pos.equalsIgnoreCase("PRP$")&&
		       !pos.equalsIgnoreCase("``")&&
		       !pos.equalsIgnoreCase("TO")){
		    	//stringBuilder.
		    	//check if the word does not exit in the string
		    	if(!stringBuilder.toString().contains(word))
		    		stringBuilder.append(word+" ");
		    }
		  }
		
		  // this is the parse tree of the current sentence
		  Tree tree = sentence.get(TreeAnnotation.class);
		
		// this is the Stanford dependency graph of the current sentence
		  SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		  
		 // System.out.println(dependencies);
		  
		  //get Root Node
		  Collection<IndexedWord> indexRootCollection =  dependencies.getRoots();
		 
		  //iterate through all root nodes
		  for(IndexedWord indexRootWord: indexRootCollection){
			  //System.out.println(indexRootWord.originalText());  
			  
			  //get Leaf vertices of Root nodes
			  for(IndexedWord indexSiblingWord : dependencies.descendants(indexRootWord)){
				  
				 // System.out.println(indexSiblingWord +
				//		  ":"+indexSiblingWord.toString().substring(indexSiblingWord.toString().indexOf("/")+1));
			  }
		  }
		}	
	}
	/**
	 * Load a url and return the parsed html document
	 * 
	 * @param url	an absolute url giving the base location of a page 
	 * @return a Jsoup Document representing the website behind the url
	 * @throws IOException
	 */
	public static Document load_doc_from_url(String url) throws IOException {
		//TODO 1 create correct return value
		Document document = Jsoup.connect(url).timeout(10000000).get();
		return document;
	}
	
	//get Data From Galago and Save Document
	
	public static void getFilesFromGilagoAndSaveThem(String inputString) throws IOException{
		
		Document doc = null;
		//open Galago URL with Query
		try {
			
			//add + sign in empty string for URL generations
			inputString = inputString.replace(" ", "+");
			inputString = inputString.substring(0, inputString.length()-1);
			StringBuilder urlInputString = new StringBuilder();
			urlInputString.append("http://192.168.56.1:3002/search?q=%23sdm%28");
			urlInputString.append(inputString);
			urlInputString.append("%29");
			
			
			doc = load_doc_from_url(urlInputString.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//get URL Elements
		Elements links = doc.select("a[href]");
		
		//Next Tasks 
		//get documents 
		//store them 
		int index = 0;
		for(Element element: links){
			String parsedNextpargeURL = element.toString().substring(element.toString().indexOf("href=\"")+6, element.toString().indexOf("\">"));
			if(parsedNextpargeURL.contains("document")){
				// Request Pages of next documents
				Document localdoc = load_doc_from_url("http://192.168.56.1:3002/"+parsedNextpargeURL);
				
				System.out.print(localdoc+"\n");
				System.out.print("**************************");
				System.out.print("**************************");
				saveDocument(localdoc,index);
				index++;
			}
		}
		
	}
	
	//save document locally
	public static void saveDocument(Document docToSave, int index) throws IOException{
		
		final File file = new File(index+".text");
		BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
		 out.write(Jsoup.parse(docToSave.html()).text());
		 out.close();
	}
	
	//run Rapid Miner Process
	public static void runRapidMinerProcess() throws RepositoryException, IOException, XMLException, OperatorException{
		
	   	// this initializes RapidMiner with your repositories available
    	// you have to call this for all your programs, otherwise your RapidMiner integration will not work correctly
    	RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
    	RapidMiner.init();

    	// loads the process from the repository (if you do not have one, see alternative below)
    	RepositoryLocation pLoc = new RepositoryLocation("//Local Repository/processes/TermCount");
    	ProcessEntry pEntry = (ProcessEntry) pLoc.locateEntry();
    	String processXML = pEntry.retrieveXML();
    	Process myProcess = new Process(processXML);
    	myProcess.setProcessLocation(new RepositoryProcessLocation(pLoc));
    	IOContainer ioResult = myProcess.run();
    	
	}
	
//	public static void main(String[] args) throws IOException, RepositoryException, XMLException, OperatorException {
//
//		//parses csv files
//		List<String> phraseList = csvFilePhrases();
//		
//		//preprocess phrases
//		for(int i=0; i< phraseList.size(); i++){
//			System.out.println("************************\n");
//			preprocesPhrase(phraseList.get(i));
//			System.out.println("************************\n");
//		}
//
//		//String to send to Gilago
//		System.out.println("************************\n");
//		System.out.println("String to send to Gilago: "+stringBuilder.toString());
//		System.out.println("************************\n");
//	
//		
//		// This function sends String to Galago and searches string in Galago and returns them
//		getFilesFromGilagoAndSaveThem(stringBuilder.toString());
//		
//		//This function instiates Rapid Miner Process which will get files Generated by Gilago Function and God knows what will do will 
//		// them pleas look into Rapid Miner process for file details this code just exectues the process
//		runRapidMinerProcess();
//	}
}
