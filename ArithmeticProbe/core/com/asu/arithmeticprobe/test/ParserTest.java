package com.asu.arithmeticprobe.test;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
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
	private static StanfordCoreNLP stanfordCoreNLP;
	private static Annotation      annotation;
	static
	{
		Properties properties = new Properties();
		properties.setProperty("annotators",
		        "tokenize, ssplit, pos, lemma, ner, parse, stopword");
		stanfordCoreNLP = new StanfordCoreNLP(properties);
		annotation = new Annotation(
		        "Joan found 70 seashells on the beach . she gave Sam some of her seashells . She has 27 seashell . How many seashells did she give to Sam ? ");
		stanfordCoreNLP.annotate(annotation);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		basicprint(annotation);
		printPOSTags(annotation);
		printTree(annotation);
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
		List<CoreMap> sentences = annotation
		        .get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences)
		{
			for (CoreLabel token : sentence
			        .get(CoreAnnotations.TokensAnnotation.class))
			{
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String pos = token
				        .get(CoreAnnotations.PartOfSpeechAnnotation.class);
				System.out.println(word + " - " + pos);
			}
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
}
