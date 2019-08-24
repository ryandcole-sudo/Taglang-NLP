package com.colex.taglang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Ryan Cole
 * @version 1.0
 */
public class Grammar {
	/**
	 * The grammar class requires a file, which stores a list of all the grammar
	 * rules used. 
	 *
	 */
	private File file; //file used by grammar class
        
        
	
   /**
    * Constructor constructs a Grammar object from a file. 
    * The file must be readable and writable. 
    * @param GrammarFile the grammar file to use
    * @throws FileNotFoundException if the grammar file can't be found this exception will be thrown.	
    */
   public Grammar(File GrammarFile) throws FileNotFoundException {
        if(!GrammarFile.exists()){
            throw new FileNotFoundException();
        }
        file = GrammarFile;
   }
   
   /**
    * Adds a new grammar rule to a file, using a string. The string is
    * formatted using the taglang syntax. <br>
    * @param grammarRule a string description of a grammar rule.
    * @throws GrammarFileLoadException if grammar file can't be read or written
    */
   public final void addRule(String grammarRule) throws GrammarFileLoadException{
      FileWriter fw;
      Scanner fs;
      
      try{ 
       fs = new Scanner(this.file);
       fw = new FileWriter(this.file,true);
       
      }
      catch(FileNotFoundException e){
         throw new GrammarFileLoadException();    
      }
      catch(IOException e){
          throw new GrammarFileLoadException();
      }
      
      if(this.file.canRead() && this.file.canWrite()){
        if(Grammar.validateSyntax(grammarRule)){
            try{  
             fw.write(System.lineSeparator() + grammarRule);
              fw.close();
            }catch(Exception e){
              throw new GrammarFileLoadException(); 
            }
            
        }    
      }else{
         
      }
      fs.close();
      try{
      fw.close();
      }catch(IOException e){
          throw new GrammarFileLoadException();
      }
   }
   
   /**
    * Adds a new grammar rule to a file, using an array of array of Tags(a "matrix" of tags.<br> 
    * A grammar rule in taglang is simply a 2D array of tags. It is the task of the parser
    * to determine how to interpret this array.
    * @param grammarRule 2D array of tags that is interpreted by a parser.
    * @throws GrammarFileLoadException if grammar file can't be read or written
    */
   public final void addRule(Tag[][] grammarRule) throws GrammarFileLoadException{
     String rule_g="";  
     for(int i=0;i<grammarRule.length-1;i++){
         for(int j=0;j<grammarRule[i].length-1;j++){
             rule_g = rule_g.concat(grammarRule[i][j].toString());
         }
          rule_g = rule_g.concat(" ");
     }
     addRule(rule_g);
   }
   
   /**
    * Removes a grammar rule from a file, by specifying the index.
    * @param index 
    */ 
   public final void removeRule(int index){
      FileWriter fw;
      try{
       fw = new FileWriter(this.file); 
               
       
       fw.close();
      }catch(IOException e){
          
      }
      
   }
  
   /**
    * A Grammar object requires a grammar file. The grammar file stores the tags
    * needed to describe the grammar for use by the parser. The grammar class
    * can work without a file, in this case it simply has no grammatical rules.
    
    * @param GrammarFile the grammar file to be used
    * @throws GrammarFileLoadException if the grammar file can be read or written to.
    */
   public final void setGrammarFile(File GrammarFile) throws GrammarFileLoadException{
       
       if(file.canRead() && file.canWrite()){
         this.file = GrammarFile;
       }else{
           throw new GrammarFileLoadException();
       }
   
   }

   
   /**
    * The number of grammar rules associated with the grammar object
    * @return the number of grammar rules
    * @throws GrammarFileLoadException if grammar file can't be read or written to.
    */
   public final int ruleCount() throws GrammarFileLoadException{
       Scanner fs;
       int count = 0;
       String str;
       try{
         fs = new Scanner(this.file);
       }catch(FileNotFoundException e){
           throw new GrammarFileLoadException();
       }
       while(fs.hasNext()){
         str = fs.nextLine();
         
         if(str.startsWith("//") || str.startsWith(" ") || str.equals("")){
             
             continue;
         }else{
             if(Grammar.validateSyntax(str)){
                 count++;
             }
         }
       }
       fs.close();
       return count;
   }
   /**
    * Finds the particular rule at the specified index and returns a string value
    * representing the rule. If a rule is not found at the specified index an
    * empty string is returned.
    * @param index The index of the rule
    * @return A string representing the the grammar rule.
    * @throws GrammarFileLoadException 
    */
   public final String ruleAt(int index) throws GrammarFileLoadException{ 
       Scanner fs;
       int count = 0;
       String str;
       String rule="";
       
   
       try{
         fs = new Scanner(this.file);
       }catch(FileNotFoundException e){
           throw new GrammarFileLoadException();
       }
       while(fs.hasNext()){
         str = fs.nextLine();
         
         
         if(str.startsWith("//") || str.startsWith(" ") || str.equals("")){
              continue;
         }else{
             if(Grammar.validateSyntax(str)){
                 count++;
                 
                 if(count == index+1){
                     rule=str;
                     break;
                 }
             }
         }
       }
       fs.close();   
       return rule;
   }
   
   /**
    * Validates a string to see whether or not it follows valid taglang syntax.
    * Tag texts can only contain letters A-z a-z, numbers 0-9 or underscore(_);
    *The valid tag type identifiers are: <br>
    * <b> $</b> - Default tag <br>
    * <b> +</b> - Prefix tag <br>
    * <b> -</b> - Suffix tag <br>
    * <b> .</b> - Infix tag <br>
    * Comments start with <span style="color:#00ff00;">//</span>
    * @param grammarRule
    * @return true if the syntax is valid, false otherwise.
    */
   public final static boolean validateSyntax(String grammarRule){
       boolean istag = false;
       char c;
       if(grammarRule.startsWith("//")|| grammarRule.startsWith(" ") || grammarRule.equals("")){
           return true;
       }
       for(int i = 0;i<grammarRule.length()-1;i++){
           c = grammarRule.charAt(i);
           if(c == '$' || c == '-' || c== '+' || c=='.'){
            if(istag == true){
                return false;
            }
            istag = true;
            continue;
           }
           
           istag = false;
           if((c<'A' && c>'Z') || (c<'a' && c>'z') || (c<'0' && c>'9')){
               return false;
           }
       }
       
       return true;
   }
   
   
   
  
   
}