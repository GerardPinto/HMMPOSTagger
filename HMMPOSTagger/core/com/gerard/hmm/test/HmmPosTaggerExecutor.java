/**
 * 
 */
package com.gerard.hmm.test;

import java.io.IOException;
import java.util.HashMap;

import com.gerard.hmm.app.HMMGenerator;
import com.gerard.hmm.app.HMMParser;
import com.gerard.hmm.app.ViterbiExecutor;
import com.gerard.hmm.exception.HMMParserException;

/**
 * @author Gerard
 */
public class HmmPosTaggerExecutor
{
	private static final String trainingFile = "config/entrain.txt";
	private static final String testFile     = "config/entest.txt";
	
	public static void main(String[] args)
	{
		try
		{
			// testHMM();
			// testHMMGenerator();
			// testViterbi();
			errorRate();
		}
		catch (IOException | HMMParserException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException
	 * @throws HMMParserException
	 */
	private static void errorRate() throws HMMParserException, IOException
	{
		HMMGenerator generator = new HMMGenerator(new HMMParser(trainingFile));
		ViterbiExecutor viterbiExecutor = new ViterbiExecutor(testFile,
		        generator);
		System.out.println(viterbiExecutor.errorRate());
	}
	
	/**
	 * @throws HMMParserException
	 * @throws IOException
	 */
	private static void testHMMGenerator() throws IOException,
	        HMMParserException
	{
		HMMGenerator hmmGenerator = new HMMGenerator(
		        new HMMParser(trainingFile));
		System.out.println(hmmGenerator.getTransitionProbabilities().get("R")
		        .get("###"));
		System.out.println(hmmGenerator.getEmissionProbabilities().get("###")
		        .get("###"));
	}
	
	/**
	 * @throws IOException
	 * @throws HMMParserException
	 */
	private static void testViterbi() throws HMMParserException, IOException
	{
		HMMGenerator generator = new HMMGenerator(new HMMParser(trainingFile));
		ViterbiExecutor viterbiExecutor = new ViterbiExecutor(testFile,
		        generator);
	}
	
	/**
	 * @throws HMMParserException
	 * @throws IOException
	 */
	private static void testHMM() throws IOException, HMMParserException
	{
		HMMParser hmmParser = new HMMParser(trainingFile);
		System.out.println("Total Tag count : " + hmmParser.getTagCount()
		        + " of Size : " + hmmParser.getTagCount().size());
		System.out.println("Tag - D | Word - The : "
		        + ((HashMap<String, Integer>) hmmParser.getTagToWordCount()
		                .get("D")).get("the"));
		System.out.println("Word - The | Tag - D : "
		        + ((HashMap<String, Integer>) hmmParser.getWordToTagCount()
		                .get("the")).get("D"));
		System.out.println("Word - financing : "
		        + ((HashMap<String, Integer>) hmmParser.getWordToTagCount()
		                .get("financing")).size());
		System.out.println("Word - financing : "
		        + ((HashMap<String, Integer>) hmmParser.getWordToTagCount()
		                .get("financing")));
	}
}
