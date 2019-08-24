package com.colex.taglang.parser;

import com.colex.taglang.*;

/**
 *  A mophology parser is used to parse words into various morphemes. It can 
 * determine what morpheme class a word belongs to. For example, given a noun it
 * can determine whether or not it is plural. It can determine the proper declension 
 * of nouns and the conjugations of verbs. This is all based on the grammar rule 
 * used by the specific MorphologyParser object. 
 * @author Ryan Cole
 * @version 1.0
 */
public class MorphologyParser extends Parser {
        /**
         * 
         * @param parseString
         * @return 
         */
	@Override
	public Tag[] parse(String parseString) {
		return null;
	}
	/**
         * 
         * @param string
         * @param taglist
         * @return 
         */
	@Override
	public String pack(String string, Tag[] taglist) {
		return null;
	}
        
        @Override
        public String pack(String string, String taglist){
            return null;
        }
	
        
       
    
	
	
	
	
	
}
