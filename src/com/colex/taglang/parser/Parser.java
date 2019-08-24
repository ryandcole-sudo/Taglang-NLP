package com.colex.taglang.parser;

import com.colex.taglang.Tag;

/**
 * A parser object has various methods to parse strings and extract. The class 
 * is abstract, so it can't be instantiated directly. The parser should rely on 
 * the grammar rules, lexicon and alphabet to determine how to parse the word. A
 * parser determines how to interpret each tag. 
 * @author Ryan Cole
 * @version 1.0
 */
abstract public class Parser{

	private String lastTagText;

/**
 *  The default constructor for Parser. Classes that extend parser can replace 
 * this constructor.
 */        
public  Parser() {
	
}

/**
 *  This method should return a list of Tags relating to the parsed text. These
 * tags should relate to
 * 
 * @param parseString the string to be parsed
 * @return a list of tags
 * @see pack
 */  
public abstract Tag[] parse(String parseString);



/**
 * 
 * @param string
 * @param taglist
 * @return 
 */
public abstract String pack(String string, Tag[] taglist);


/**
 * 
 * @param string
 * @param taglist
 * @return 
 */
public abstract String pack(String string, String taglist);


/**
 * Pack does the opposite of parse. Given a string and a set of tags. 
 * @return The last parsed string from the parsed text 
 */
public String parsedString() { //Returns the string of the last parsed text
	return lastTagText;
}


}
