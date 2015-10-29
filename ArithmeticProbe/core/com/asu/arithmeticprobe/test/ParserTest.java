package com.asu.arithmeticprobe.test;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
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
	private static final String    sentence = "The weather is perfect during the growing season , so he harvests 684 bushels of wheat than expected .";
	private static StanfordCoreNLP stanfordCoreNLP;
	private static Annotation      annotation;
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
	 */
	private static void printPOSTags(Annotation annotation)
	{
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
				String namedEntityTag = token
				        .get(NamedEntityTagAnnotation.class);
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String pos = token
				        .get(CoreAnnotations.PartOfSpeechAnnotation.class);
				if (pos.contains("VB"))
				{
					dependencies.put(word, new HashMap<String, String>());
				}
			}
			
			for (SemanticGraphEdge semanticGraphEdge : semanticGraphEdges)
			{
				IndexedWord dep = semanticGraphEdge.getDependent();
				String dependent = dep.word();
				int dependent_index = dep.index();
				IndexedWord gov = semanticGraphEdge.getGovernor();
				String governor = gov.word();
				int governor_index = gov.index();
				GrammaticalRelation relation = semanticGraphEdge.getRelation();
				
				if (dependencies.containsKey(governor))
				{
					dependencies.get(governor).put(dependent,
					        semanticGraphEdge.getRelation().toString());
				}
				
			}
			
			System.out.println(dependencies);
		}
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
