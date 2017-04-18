/**
 * 
 */
package com.gerard.hmm.app;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Gerard
 *         The class generates an HMM Model for probabilistic analysis.
 */
public class HMMGenerator
{
	/**
	 * {@link HMMParser} is used to parse the given sentence - word | tag.
	 */
	private HMMParser                                parser;
	
	private HashMap<String, HashMap<String, Double>> transitionProbabilities;
	
	private HashMap<String, HashMap<String, Double>> emissionProbabilities;
	
	/**
	 * Initializze HMM Model using the {@link HMMParser}
	 */
	public HMMGenerator(HMMParser parser)
	{
		this.parser = parser;
		transitionProbabilities = new HashMap<String, HashMap<String, Double>>();
		emissionProbabilities = new HashMap<String, HashMap<String, Double>>();
		transitionAndEmissionProbabilities();
	}
	
	/**
	 * @return the transitionProbabilities
	 */
	public HashMap<String, HashMap<String, Double>> getTransitionProbabilities()
	{
		return transitionProbabilities;
	}
	
	/**
	 * @return the emissionProbabilities
	 */
	public HashMap<String, HashMap<String, Double>> getEmissionProbabilities()
	{
		return emissionProbabilities;
	}
	
	/**
	 * For each tag calculate the transition probability and emission
	 * probability
	 * and stores them in transitionProbabilities and emissionProbabilities
	 * where transitionProbabilities is map (fromTagKey of map ( toTagKey,
	 * Prob(fromTag - toTag))
	 * and emissionProbabilities is map (fromTagKey of map ( toWordKey,
	 * Prob(fromTag - toTag))
	 */
	private void transitionAndEmissionProbabilities()
	{
		HashMap<String, HashMap<String, Integer>> tagToTagCount = parser
		        .getTagToTagCount();
		for (Entry<String, HashMap<String, Integer>> tagEntry : tagToTagCount
		        .entrySet())
		{
			String fromTag = tagEntry.getKey();
			for (String toTag : tagEntry.getValue().keySet())
			{
				if (transitionProbabilities.containsKey(fromTag))
				{
					transitionProbabilities.get(fromTag).put(toTag,
					        probabilityForTagGivenPrevTag(fromTag, toTag));
				}
				else
				{
					HashMap<String, Double> transProb = new HashMap<String, Double>();
					transProb.put(toTag,
					        probabilityForTagGivenPrevTag(fromTag, toTag));
					transitionProbabilities.put(fromTag, transProb);
				}
			}
			
			HashMap<String, HashMap<String, Integer>> tagToWordCount = parser
			        .getTagToWordCount();
			HashMap<String, Integer> wordCount = tagToWordCount.get(fromTag);
			for (String word : wordCount.keySet())
			{
				if (emissionProbabilities.containsKey(fromTag))
				{
					emissionProbabilities.get(fromTag).put(word,
					        probabilityForWordGivenTag(fromTag, word));
				}
				else
				{
					HashMap<String, Double> emisProb = new HashMap<String, Double>();
					emisProb.put(word,
					        probabilityForWordGivenTag(fromTag, word));
					emissionProbabilities.put(fromTag, emisProb);
				}
				
			}
		}
	}
	
	/**
	 * Returns the probability for the given word among all the tags present.
	 * Which is similar to P (word|tag) = P (word and tag) / P(tag)
	 * Which is similar to finding the most probable word that belongs to a
	 * particular tag.
	 * Which is similar to tag-to-word emission probabilities
	 * 
	 * @param tag
	 *            {@link String}
	 * @param word
	 *            {@link String}
	 * @return {@link Double}
	 */
	public double probabilityForWordGivenTag(String tag, String word)
	{
		HashMap<String, HashMap<String, Integer>> tagToWordCount = parser
		        .getTagToWordCount();
		HashMap<String, Integer> tagCount = parser.getTagCount();
		int totalWordCountForTag = getTagToWordCount(tagToWordCount, tag, word);
		int totalWordCountForAllTags = getCount(tagCount, tag);
		
		return (double) totalWordCountForTag
		        / (double) (totalWordCountForAllTags);
	}
	
	/**
	 * Calculates the probability of the given tag | the previous tag has
	 * has been visited.
	 * Whcih is similar to tag-to-tag transition probabilities
	 * 
	 * @param prevTag
	 *            {@link String}
	 * @param currentTag
	 *            {@link String}
	 * @return
	 */
	public double probabilityForTagGivenPrevTag(String prevTag,
	        String currentTag)
	{
		HashMap<String, HashMap<String, Integer>> tagToTagCount = parser
		        .getTagToTagCount();
		
		HashMap<String, Integer> tagCounts = parser.getTagCount();
		
		int prevTagsCount = getTagToTagCount(tagToTagCount, prevTag, currentTag);
		int totalTagCounts = getCount(tagCounts, prevTag);
		
		return (double) prevTagsCount / (double) (totalTagCounts);
	}
	
	/**
	 * Returns the total count for the current tag pointing to the previous tag.
	 * Which is the same as get the total counts(value) from the
	 * previousTag(key)
	 * where the currentTag(key).
	 * 
	 * @param tagToTagCount
	 *            {@link HashMap}
	 * @param currentTag
	 *            {@link String}
	 * @param prevTag
	 *            {@link String}
	 * @return {@link Integer}
	 */
	public int getTagToTagCount(
	        HashMap<String, HashMap<String, Integer>> tagToTagCount,
	        String prevTag, String currentTag)
	{
		if (tagToTagCount.containsKey(prevTag))
		{
			return getCount(tagToTagCount.get(prevTag), currentTag);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Returns the word count associated withing this tag.
	 * Which is similar to number of times(value) this word(key) is present in
	 * this tag(key).
	 * 
	 * @param tagToWordCount
	 *            {@link HashMap}
	 * @param tag
	 *            {@link String}
	 * @param word
	 *            {@link String}
	 * @return
	 */
	public int getTagToWordCount(
	        HashMap<String, HashMap<String, Integer>> tagToWordCount,
	        String tag, String word)
	{
		if (tagToWordCount.containsKey(tag))
		{
			return getCount(tagToWordCount.get(tag), word);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Returns the total count withing a container for a key.
	 * 
	 * @param container
	 *            {@link HashMap}
	 * @param key
	 *            {@link String}
	 * @return {@link Integer}
	 */
	private int getCount(HashMap<String, Integer> container, String key)
	{
		if (container.containsKey(key))
		{
			return container.get(key);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * @param word
	 * @return
	 */
	public HashMap<String, Integer> getTagsForWord(String word)
	{
		if (parser.getWordToTagCount().containsKey(word))
		{
			return parser.getWordToTagCount().get(word);
		}
		else
		{
			return new HashMap<String, Integer>();
		}
	}
	
	/**
	 * @param tagForWord
	 * @return
	 */
	public HashMap<String, Integer> getPrevTagsForGivenTag(String tagForWord)
	{
		if (parser.getTagToTagCount().containsKey(tagForWord))
		{
			return parser.getTagToTagCount().get(tagForWord);
		}
		else
		{
			return new HashMap<String, Integer>();
		}
	}
}
