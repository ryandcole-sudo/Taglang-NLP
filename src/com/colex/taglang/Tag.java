package com.colex.taglang;

/**
 * 
 * @author Ryan Cole
 * @version 1.0
 */
public class Tag{
   /**
    * A Tag object can be of various inbuilt types, these types can help 
    * Parsers to identify what each tag means. A parser may ignore tag types 
    * (but this is not recommended). It is recommended  that all Parsers use
    * a manner similar to it's intended use. <br>
    * <b> SUFFIX </b> <br>
    * <b> PREFIX </b>  <br>
    * <b> INFIX </b>  <br> 
    */ 
   public enum TagType{
       /**
        * to mark the category or class of a word
        */
       DEFAULT,
       /**
        * Should be interpreted as a regular string
        */
       WORD,
       /**
        *  to mark the last part of a string
        */
       SUFFIX,
       /**
        *  to mark the last part of a string
        */
       PREFIX,
       /**
        * A sub-string that's not a prefix or suffix
        */
       INFIX
   }
   /**
    *  
    */
   public TagType type;
   
   
  /*
   * Tags are used in to identify the grammatical features of a word 
   * 	
   */  
  public String text;
  
  /**
   * Constructs a tag of default type from a string text
   * @param tagText the text of the tag, this can be accessed through the property 'text' of the tag.
   */
  public Tag(String tagText) {
	  this.text = tagText;
          this.type = TagType.DEFAULT;
  }
  
  /**
   * Constructs a tag from string text and the tag type.
   * @param tagText the text of the tag, this can be accessed through the property 'text' of the tag.
   * @param tagType  the type of the tag, this can be accessed through the property 'type' of the tag. 
   */
  public Tag(String tagText,TagType tagType){
      this.text = tagText;
      this.type = tagType;
  }
  
  /**
   * Returns a string representation of a Tag object.
   * 
   * @return 
   */
  @Override
  public String toString() {
          String t = " ";
          
          switch(this.type){
              case DEFAULT:
                  t = "$";
                  break;
              case WORD:
                  t="";
                  break;
              case SUFFIX:
                  t="-";
                  break;
              case PREFIX:
                  t="+";
                  break;
              case INFIX:
                  t=".";
                  break;
          }
	   
         return t.concat(this.text);
          
  }
  
  /**
   * Creates a tag from a taglang formatted string.
   * @param tagString 
   */
  public static Tag fromString(String tagString){
       Tag tag = new Tag(" ");
       switch(tagString.charAt(0)){
           case '$':
               tag.type = TagType.DEFAULT;
               break;
           case '-':
               tag.type = TagType.SUFFIX;
               break;
           case '+':
               tag.type = TagType.PREFIX;
               break;
           case '.':
               tag.type = TagType.INFIX;
               break;
           default:
               tag.type = TagType.WORD;            
       }      
       
       if(tag.type != TagType.WORD){
          tag.text = tagString.substring(1);
       }else{
           tag.text = tagString;
       }
       return tag;
  }
  
}