/**
 * 
 */
package com.gerard.hmm.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.gerard.hmm.exception.HMMParserException;

/**
 * @author Gerard
 *         The class takes input from training data set and generates HMM.
 */
public class HMMParser
{
	/** Stores tag count used in the training set */
	private HashMap<String, Integer>                  tagCount;
	/**
	 * The data structure is used to store tag(key) that contains multiple
	 * words(key)
	 * and its corresponding count(value) being in that tag.
	 */
	private HashMap<String, HashMap<String, Integer>> tagToWordCount;
	
	/**
	 * The data structure is used to store word(key) that contains correspond to
	 * multiple tags(key) and its corresponding count(value) of being in that
	 * tag.
	 */
	private HashMap<String, HashMap<String, Integer>> wordToTagCount;
	
	/** The data structure is used to store tag-to-tag counts */
	private HashMap<String, HashMap<String, Integer>> tagToTagCount;
	
	/**
	 * @return the tagCount
	 */
	public HashMap<String, Integer> getTagCount()
	{
		return tagCount;
	}
	
	/**
	 * @return the tagToWordCount
	 */
	public HashMap<String, HashMap<String, Integer>> getTagToWordCount()
	{
		return tagToWordCount;
	}
	
	/**
	 * @return the wordToTagCount
	 */
	public HashMap<String, HashMap<String, Integer>> getWordToTagCount()
	{
		return wordToTagCount;
	}
	
	/**
	 * @return the tagToTagCount
	 */
	public HashMap<String, HashMap<String, Integer>> getTagToTagCount()
	{
		return tagToTagCount;
	}
	
	/**
	 * @throws IOException
	 * @throws HMMParserException
	 */
	public HMMParser(String trainingSetFile) throws IOException,
	        HMMParserException
	{
		this.tagCount = new HashMap<String, Integer>();
		this.tagToWordCount = new HashMap<String, HashMap<String, Integer>>();
		this.wordToTagCount = new HashMap<String, HashMap<String, Integer>>();
		this.tagToTagCount = new HashMap<String, HashMap<String, Integer>>();
		parseTrainingSet(trainingSetFile);
	}
	
	/**
	 * @param trainingSetFile
	 * @throws HMMParserException
	 * @throws IOException
	 */
	private void parseTrainingSet(String trainingSetFile)
	        throws HMMParserException, IOException
	{
		boolean isStart = false;
		String prevTag = "###";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(trainingSetFile)));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.equals("###/###"))
					isStart = (isStart) ? false : true;
				
				prevTag = parseLine(line, prevTag);
				if (!isStart)
				{
					isStart = true;
					tagtoTagCount(prevTag, "###");
					prevTag = "###";
				}
			}
		}
		catch (Exception e)
		{
			throw new HMMParserException(e);
		}
		finally
		{
			if (reader != null)
				reader.close();
		}
	}
	
	/**
	 * @param line
	 * @return
	 */
	private String parseLine(String line, String prevTag)
	{
		String[] posTaggerWord = line.split("/");
		String word = posTaggerWord[0].toLowerCase();
		String tag = posTaggerWord[1];
		
		tagCount(tag);
		tagToWordCount(tag, word);
		wordToTagCount(word, tag);
		tagtoTagCount(prevTag, tag);
		
		return tag;
		
	}
	
	/**
	 * Update the number of tags used in the training set.
	 * 
	 * @param tag
	 *            {@link String}
	 */
	private void tagCount(String tag)
	{
		put(tagCount, tag);
	}
	
	/**
	 * Updates the word count under the tag.
	 * 
	 * @param tag
	 *            {@link String}
	 * @param word
	 *            {@link String}
	 */
	private void tagToWordCount(String tag, String word)
	{
		if (this.tagToWordCount.containsKey(tag))
		{
			put(this.tagToWordCount.get(tag), word);
		}
		else
		{
			HashMap<String, Integer> wordCountContainer = new HashMap<String, Integer>();
			wordCountContainer.put(word, 1);
			this.tagToWordCount.put(tag, wordCountContainer);
		}
	}
	
	/**
	 * Updates the corresponding word counts being in the state of the tag in a
	 * given sentence.
	 * 
	 * @param word
	 *            {@link String}
	 * @param tag
	 *            {@link String}
	 */
	private void wordToTagCount(String word, String tag)
	{
		if (this.wordToTagCount.containsKey(word))
		{
			put(this.wordToTagCount.get(word), tag);
		}
		else
		{
			HashMap<String, Integer> tagCountContainer = new HashMap<String, Integer>();
			tagCountContainer.put(tag, 1);
			this.wordToTagCount.put(word, tagCountContainer);
		}
	}
	
	/**
	 * Insert the relations between current tag and prvious tag.
	 * Updates the tag count pointing to a tag.
	 * 
	 * @param prevTag
	 *            {@link String}
	 * @param currentTag
	 *            {@link String}
	 */
	private void tagtoTagCount(String prevTag, String currentTag)
	{
		if (this.tagToTagCount.containsKey(prevTag))
		{
			put(this.tagToTagCount.get(prevTag), currentTag);
		}
		else
		{
			HashMap<String, Integer> tagCountContainer = new HashMap<String, Integer>();
			tagCountContainer.put(currentTag, 1);
			this.tagToTagCount.put(prevTag, tagCountContainer);
		}
	}
	
	/**
	 * Increase the value of the key in the conatiner by 1.
	 * 
	 * @param container
	 *            {@link HashMap}
	 * @param key
	 *            {@link String}
	 */
	private void put(HashMap<String, Integer> container, String key)
	{
		if (container.containsKey(key))
		{
			container.put(key, container.get(key) + 1);
		}
		else
		{
			container.put(key, 1);
		}
	}
}
