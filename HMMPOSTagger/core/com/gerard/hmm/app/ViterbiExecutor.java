/**
 * 
 */
package com.gerard.hmm.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gerard.hmm.exception.HMMParserException;

/**
 * @author Gerard
 */
public class ViterbiExecutor
{
	/**
	 * {@link HMMGenerator}
	 */
	private HMMGenerator            generator;
	
	/**
	 * This is basically used as memoized dynamic programming to avoid
	 * re-computation of the recursive functions.
	 */
	private HashMap<String, Double> viterbiStore;
	/**
	 * Total words in the test set.
	 */
	private double                  totalWordCount = 0;
	
	/**
	 * Dismatch count is the count of words that hand tags are not matching with
	 * the gold set tags.
	 */
	private double                  dismatchCount  = 0;
	
	private String                  testFile;
	
	/**
	 * Initialized Viterbi algorithm that uses {@link HMMGenerator} and a test
	 * file.
	 * 
	 * @throws HMMParserException
	 * @throws IOException
	 */
	public ViterbiExecutor(String testFile, HMMGenerator generator)
	{
		this.generator = generator;
		this.viterbiStore = new HashMap<String, Double>();
		this.testFile = testFile;
	}
	
	/**
	 * Error rate is defined as = ( # words in test set for which predicated
	 * label
	 * matches the hand tagged label / # total words in the test set )
	 * To compute the error rate we use the following.
	 * 1. totalWordCount in the test file.
	 * 2. wordCountMatch in the test file
	 * 
	 * @throws IOException
	 * @throws HMMParserException
	 */
	public double errorRate() throws HMMParserException, IOException
	{
		parseTestFile();
		return dismatchCount / totalWordCount;
	}
	
	/**
	 * Parses the test file
	 * 
	 * @throws HMMParserException
	 * @throws IOException
	 */
	private void parseTestFile() throws HMMParserException, IOException
	{
		boolean isStart = false;
		BufferedReader reader = null;
		String prevWord = "###";
		try
		{
			reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(testFile)));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.equals("###/###"))
				{
					viterbiStore.clear();
					isStart = (isStart) ? false : true;
					viterbiStore.put(
					        "###~###",
					        this.generator.getTransitionProbabilities()
					                .get("###").get("###")
					                * this.generator.getEmissionProbabilities()
					                        .get("###").get("###"));
				}
				else
				{
					prevWord = parseLine(line, prevWord);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new HMMParserException(e);
		}
		finally
		{
			if (reader != null)
				reader.close();
		}
	}
	
	/**
	 * ParseLine
	 * 
	 * @param line
	 *            {@link String}
	 * @param prevWord
	 * @return
	 */
	private String parseLine(String line, String prevWord)
	{
		String[] posTaggerWord = line.split("/");
		String word = posTaggerWord[0].toLowerCase();
		String tag = posTaggerWord[1].trim();
		
		String probableTag = execute(word, prevWord);
		
		totalWordCount++;
		if (!tag.equals(probableTag.trim()))
			dismatchCount++;
		
		return word;
	}
	
	/**
	 * Executes the viterbi algorithm.
	 * 
	 * @param word
	 *            {@link String}
	 * @param prevWord
	 * @return
	 */
	public String execute(String word, String prevWord)
	{
		HashMap<String, Integer> tagsForWord = this.generator
		        .getTagsForWord(word);
		
		if (tagsForWord.isEmpty())
		{
			return unknownWord(word, prevWord);
		}
		else
		{
			return knownWord(tagsForWord, word, prevWord);
		}
		
	}
	
	/**
	 * Test set may contain some unknown words that do not appear in the
	 * training
	 * set. Handle these unknown words by assuming that the probability of
	 * observing these words from is same for all the states
	 * 
	 * @param prevWord
	 * @param word
	 * @return the maximum prbability Tag
	 */
	private String unknownWord(String word, String prevWord)
	{
		HashMap<String, Integer> tagsForWord = this.generator
		        .getTagsForWord(prevWord);
		
		double maxProb = 0;
		String maxProbableTag = "";
		for (String tagforWord : tagsForWord.keySet())
		{
			HashMap<String, Double> emissionTags = this.generator
			        .getTransitionProbabilities().get(tagforWord);
			
			for (Entry<String, Double> emissionTag : emissionTags.entrySet())
			{
				double maxEmissionProb = emissionTag.getValue();
				
				if (maxEmissionProb > maxProb)
				{
					maxProb = maxEmissionProb;
					maxProbableTag = emissionTag.getKey();
				}
			}
		}
		if (maxProbableTag.isEmpty())
		{
			for (String prevWordTag : viterbiStore.keySet())
			{
				if (prevWordTag.startsWith(prevWord + "~"))
				{
					String prevTag = prevWordTag.split("~")[1];
					double maxProbabilityTag = viterbiStore.get(prevWordTag);
					if (maxProbabilityTag > maxProb)
					{
						maxProb = maxProbabilityTag;
						maxProbableTag = prevTag;
					}
				}
			}
		}
		viterbiStore.put(word + "~" + maxProbableTag, maxProb);
		return maxProbableTag;
	}
	
	/**
	 * @param tagsForWord
	 *            {@link Map}
	 * @param prevWord
	 *            {@link String}
	 * @param word
	 *            {@link String}
	 * @return the maximum prbability Tag
	 */
	private String knownWord(HashMap<String, Integer> tagsForWord, String word,
	        String prevWord)
	{
		double maxProbStateForObservation = 0.0;
		String maxProbTag = "";
		for (String tagForWord : tagsForWord.keySet())
		{
			double maxProb = maxProbStateForObservation(word, tagForWord,
			        prevWord);
			
			if (maxProb > maxProbStateForObservation)
			{
				maxProbStateForObservation = maxProb;
				maxProbTag = tagForWord;
			}
			
			viterbiStore.put(word + "~" + tagForWord,
			        maxProbStateForObservation);
		}
		return maxProbTag;
		
	}
	
	/**
	 * Finds out the maximum probability of the given word in the given tag.
	 * 
	 * @param observationWord
	 *            {@link String}
	 * @param tagForWord
	 *            {@link String}
	 * @param prevWord
	 *            {@link String}
	 * @return
	 */
	private double maxProbStateForObservation(String observationWord,
	        String tagForWord, String prevWord)
	{
		return findMaximum(observationWord, tagForWord, prevWord)
		        * this.generator.probabilityForWordGivenTag(tagForWord,
		                observationWord);
	}
	
	/**
	 * @param observationWord
	 * @param tagForWord
	 * @param prevWord
	 * @return
	 */
	private double findMaximum(String observationWord, String tagForWord,
	        String prevWord)
	{
		double maxProbability = 0.0;
		
		try
		{
			for (String prevWordTag : viterbiStore.keySet())
			{
				if (prevWordTag.startsWith(prevWord + "~"))
				{
					String prevTag = prevWordTag.split("~")[1];
					double maxProbabilityTag = viterbiStore.get(prevWordTag)
					        * this.generator.probabilityForTagGivenPrevTag(
					                prevTag, tagForWord);
					if (maxProbabilityTag > maxProbability)
						maxProbability = maxProbabilityTag;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return maxProbability;
	}
}
