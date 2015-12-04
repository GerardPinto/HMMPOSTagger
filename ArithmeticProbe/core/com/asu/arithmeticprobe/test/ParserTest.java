package com.asu.arithmeticprobe.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Gerard
 *         The class provides basic implementation of Stanford Parser and
 *         variations of output.
 */
public class ParserTest
{
	/**
	 * 
	 */
	private static final String            sentence = "Last week Fred had 23 dollars and Jason had 46 dollars . Fred washed cars over the weekend and now he has 86 dollars . How much money did Fred make washing cars ? ";
	private static StanfordCoreNLP         stanfordCoreNLP;
	private static Annotation              annotation;
	public static HashMap<String, Integer> verbMean;
	
	static
	{
		Properties properties = new Properties();
		properties.setProperty("annotators",
		        "tokenize, ssplit, pos, lemma, ner, parse");
		stanfordCoreNLP = new StanfordCoreNLP(properties);
		annotation = new Annotation(sentence);
		stanfordCoreNLP.annotate(annotation);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// basicprint(annotation);
		printPOSTags(annotation);
		// printTree(annotation);
	}
	
	/**
	 * @param annotation
	 */
	private static void basicprint(Annotation annotation)
	{
		stanfordCoreNLP.prettyPrint(annotation, System.out);
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	private static List<HashMap<String, Integer>> printPOSTags(
	        Annotation annotation)
	{
		HashMap<String, String> lemmaVerbs = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> dependencies = new HashMap<String, HashMap<String, String>>();
		List<CoreMap> sentences = annotation
		        .get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences)
		{
			SemanticGraph semanticGraph = sentence
			        .get(CollapsedDependenciesAnnotation.class);
			Iterable<SemanticGraphEdge> semanticGraphEdges = semanticGraph
			        .edgeIterable();
			
			for (CoreLabel token : sentence
			        .get(CoreAnnotations.TokensAnnotation.class))
			{
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String pos = token
				        .get(CoreAnnotations.PartOfSpeechAnnotation.class);
				String lemma = token
				        .getString(CoreAnnotations.LemmaAnnotation.class);
				
				if (pos.contains("VB"))
				{
					lemmaVerbs.put(word, lemma);
					dependencies.put(word, new HashMap<String, String>());
				}
			}
			
			for (SemanticGraphEdge semanticGraphEdge : semanticGraphEdges)
			{
				IndexedWord dep = semanticGraphEdge.getDependent();
				String dependent = dep.word();
				IndexedWord gov = semanticGraphEdge.getGovernor();
				String governor = gov.originalText();
				if (dependencies.containsKey(governor)
				        && semanticGraphEdge.getRelation().toString()
				                .equals("nsubj"))
				{
					dependencies.get(governor).put(dependent,
					        semanticGraphEdge.getRelation().toString());
				}
			}
		}
		
		System.out.println(lemmaVerbs);
		System.out.println(dependencies);
		
		List<HashMap<String, Integer>> verbMean = new ArrayList<HashMap<String, Integer>>();
		for (Entry<String, String> entry : lemmaVerbs.entrySet())
		{
			String word = entry.getKey();
			String lemma = entry.getValue();
			
			if (dependencies.get(word) != null
			        && !dependencies.get(word).isEmpty())
			{
				HashMap<String, Integer> verbMeanScore = new HashMap<String, Integer>();
				verbMeanScore.put(lemma, 0);
				verbMean.add(verbMeanScore);
			}
		}
		
		return verbMean;
		
	}
	
	/**
	 * @param annotation
	 *            {@link Annotation}
	 */
	private static void printTree(Annotation annotation)
	{
		List<CoreMap> sentences = annotation
		        .get(CoreAnnotations.SentencesAnnotation.class);
		if (sentences != null && sentences.size() > 0)
		{
			for (CoreMap sentence : sentences)
			{
				Tree tree = sentence
				        .get(TreeCoreAnnotations.TreeAnnotation.class);
				System.out.println("First line parsed tree");
				System.out.println(tree.pennString());
			}
		}
	}
	
	/**
	 * 
	 */
	public void getQuestionVerb()
	{
		
	}
	
	/**
	 * 
	 */
	public void getImplicitStateVerb()
	{
		
	}
}
