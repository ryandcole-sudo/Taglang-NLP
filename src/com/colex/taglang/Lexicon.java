package com.colex.taglang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * A lexicon object is an object that contains a link to  a file that contains 
 * a word list, Each word in the list is tagged. The object 
 * @author Ryan Cole
 * @version 1.0
 */
public class Lexicon {
    
private File file;    

private Tag[] wordTags; //The tag of the most recently accessed word

private final static byte[] MAGIC ={0x54, 0x4c, 0x00}; 

/**
 * Constructor constructs a Lexicon object from a file. The Lexicon object is 
 * associated with this file. The file is required to access the list of words.
 * @param f 
 */
public Lexicon(File f){
    
    try{
       this.setWordFile(f);
    }catch(IOException e){
        file =f ; //TODO: Change this
    }
    
}    
/**
 * Adds a new tag to the tag list.
 * @param tags The list of tags to be added.
 */
public final void addTags(Tag[] tags){
    addTags(tags,true);
}
/**
 * Adds a new tag to the tag list.
 * @param tags The list of tags to be added.
 * @param checkDuplicates If true the function checks for previously added tags 
 * to avoid duplicates otherwise the function ignores duplicates and adds any ways
 * 
 */
public final void addTags(Tag[] tags, boolean checkDuplicates){
    
    RandomAccessFile rf;
    short taglp; //Pointer to taglist
    int tagll; //Number of tags in the taglist
    long wordlp;
    
    int[] tagi = new int[tags.length]; //Shows the location of each tag (If it already exists) or -1 if it doesn't. To void duplicates.
    
    if(checkDuplicates){ 
        tagi = findTags(tags); //Checks to see whether or not tags already exists.
    }
       try{ 
        rf = new RandomAccessFile(this.file,"rw"); //Opens the file
        rf.seek(0x03); //jumps to right after the magic number.
        taglp = rf.readShort();
        wordlp = rf.readLong();
        
        rf.seek(taglp); //Jumps to the taglist
        
        tagll = rf.readInt(); //Reads the tag length
        
        for(int i =0;i < tags.length; i++){
         if(tagi[i] == -1 && checkDuplicates){ //If the tag doesn't exist yet. add it
            FileAccess.insertUTF(rf, tags[i].text); 
            wordlp += tags[i].text.length() + 2; 
            tagll++; 
          }
        }
        rf.seek(taglp); //Jumps back to the start of the taglist
        rf.writeInt(tagll); //update the length of the taglist
        
        rf.seek(0x03); //Back to the header of the file(after the magic number). 
        rf.writeShort(taglp); //update the tag pointer
        rf.writeLong(wordlp); //update the word pointer.
        rf.close();
       }catch(EOFException e){
           //TODO: Throw appropriate exception here
           System.out.print("EOF");
       }
       catch(FileNotFoundException e){
           //TODO: Throw appropriate exception here
           System.out.println("FNF");
       }catch(IOException e){
           //TODO: Throw apprpriate exception here
           System.out.print("IOE");
       }
}

/**
 * Gets the tag at a particular index
 * @param index
 * @return 
 */
public Tag getTag(int index){
    
    RandomAccessFile rf;
    
    Tag tag = new Tag("");
    
    short taglp; //Pointer to the taglist
    int tagll; //Number of tags in the taglist
    
   try{ 
    rf = new RandomAccessFile(this.file,"rw");
    
    rf.seek(0x03);
    
    taglp = rf.readShort();
    
    rf.seek(taglp);
    
    tagll = rf.readInt();
    
    if(index == 0){
        return null; //Tag indices start with 1
    }
    
    if(index > tagll){
        //ERROR: Index out of bounds
        System.out.println("Error: Tag index out of bounds");
    }
    
    for(int i=0;i<tagll-index+1;i++){
        tag = new Tag(rf.readUTF());
    }
    if(tag.text.equals(""))
        return null;
    return tag;
    
    
   }catch(FileNotFoundException e){
       
       e.printStackTrace();
       
   }catch(IOException e){
       e.printStackTrace();
   }
   
    tag = new Tag("");
    return tag;
    
}

/**
 * Finds the index of various tags.
 * 
 * All tags are assumed to be of type default even if they are otherwise.
 * @param tags
 * @return an array of integer of length equal to than of the @param tag, each 
 * element of the array is an index of the  corresponding tag, or -1 if the tag doesn't exist
 */

public final int[] findTags(Tag[] tags){
  int[] idx = new int[tags.length];
  short taglp;
  int tagll;
  
  //Set Default idx values to -1
  for(int i=0;i<tags.length;i++){
      idx[i] = -1;
  }
  
  RandomAccessFile rf;
 try{ 
  rf = new RandomAccessFile(this.file,"rw");
  rf.seek(0x03); //Jumps to right after the magic number.
  taglp = rf.readShort(); 
  rf.seek(taglp);
  tagll = rf.readInt();
  
  if(tagll == 0){
      return idx;
  }
  
  for(int i=0;i<tagll;i++){
      String str = rf.readUTF();
      for(int j =0; j<tags.length;j++){
         if(str.equals(tags[j].text)){
           idx[j] = tagll -j;
         }        
      }
  }
  
  return idx;
  
 }catch(FileNotFoundException e){
     
 }catch(IOException e){
     
 } 
  
  
 return idx;  
    
}

/**
 * Finds a tag in the tag list of the file.
 * 
 * All tags are assumed to be of type default even if they are otherwise.
 * @param tg - The tag to search for
 * @return true if the tag is found and false otherwise.
 */
public final boolean findTag(Tag tg){
    Tag tgs[] = new Tag[1];
    int idx[] = new int[1];
    tgs[0] = tg;
    idx = findTags(tgs);
    if(idx[0] == -1)
        return false;
    else
        return true;
}

/**
 * Adds a word to the lexicon along with a sequence of tags associated with it.
 * 
 * All tags are assumed to be of type default, even if they are otherwise.
 * @param word The new word to add to the lexicon
 * @param tags The array of tags associated with the tags
 */
public final void addWord(String word, Tag[] tags){
    addWord(word,tags,true,true);
} 

/**
 * Adds a word to a lexicon along with a sequence of tags associated with it.
 * 
 * All tags are assumed to be of type default, even if they are otherwise.
 * @param word The new word to add to the lexicon
 * @param tags The array of tags associated with the word
 * @param checkDuplicates 
 */
public final void addWord(String word, Tag[] tags,boolean checkDuplicates){
   addWord(word,tags,checkDuplicates,checkDuplicates);
}

/**
 * Adds a word to the lexicon along with a sequence of tags associated with it.
 * 
 * All tags are assumed to be of type default, even if they are otherwise.
 * @param word The new word to add to the lexicon
 * @param tags The array of tags associated with the word
 * @param checkTagDuplicates 
 * @param checkWordDuplicates 
 */
public final void addWord(String word, Tag[] tags, boolean checkTagDuplicates,boolean checkWordDuplicates){
  RandomAccessFile rf; 

  long wordlp; //Pointer to wordlist

  int wordll; //Number of words in the wordlist
  
  int tagi[] = new int[tags.length]; //Index for each tag
  
  if(checkWordDuplicates){
     if(findWord(word)>0){
         return;
     }
  }
 
  try{
    rf = new RandomAccessFile(this.file,"rw");
    
    addTags(tags,checkTagDuplicates); //Adds list of tags 
    tagi = findTags(tags);
    
    rf.seek(0x03);
    rf.readShort(); //Pointer to taglist
    wordlp = rf.readLong(); //Pointer to wordlist
  
    rf.seek(wordlp);
    wordll = rf.readInt();
    
    for(int i=0;i<tags.length;i++){
        FileAccess.insert(rf, (int)tagi[i]);
    }
    FileAccess.insert(rf,(int)tags.length); //tag counter [tagc]
    FileAccess.insertUTF(rf,word);
    
    
    wordll++;
    rf.seek(wordlp);
    rf.writeInt(wordll);
   } 
   catch(EOFException e){
    //TODO: Throw appropriate exception here   
    System.out.println("EOF:add");
  
   }catch(FileNotFoundException e){
     //TODO: Throw appropriate error here 
     System.out.println("FNF:add");
   }
   catch(IOException e){
     System.out.println("IOEa:add");
     //TODO: Throw appropriate exception here
   }
}
/**
 *  Finds a word in the lexicon and returns the index of the word found. The tags
 *  for the word can later be accessed through the getWordTags() method. 
 * @param word the word to search for.
 * @return  the index of the word found, -1 if word isn't found.
 * @see getWordTags
 */
public final int findWord(String word){
    long wordlp; //Pointer to the word list
    long taglp; //Pointer to the taglist
    boolean found; //Whether or not the word was found
    
    int wordll; //Number of words in the file
    int tagll;
    int tagi[]; // a list of all tags in the file
    int tagn = 0; //Number of tags for the word
    int index = -1;
    RandomAccessFile rf;
   try{ 
    rf = new RandomAccessFile(this.file,"rw");
    
    rf.seek(0x03);
 
    rf.readShort();
    wordlp = rf.readLong();
  
    rf.seek(wordlp);
    wordll = rf.readInt();
    
    for(int i=0;i<wordll;i++){
        String str= rf.readUTF();
        tagn = rf.readInt();
        
        tagi = new int[tagn];
        if(str.equals(word)){
            return wordll-i;
        }
        
        for(int j=0;j<tagn;j++){
            tagi[j] = rf.readInt();
        }
    }
    
    this.wordTags = new Tag[tagn]; //TODO : Change this
   
    rf.close();
   }catch(EOFException e){
        return -1; //Tag not found at end of file 
   }catch(FileNotFoundException e){
       //TODO: Throw appropraiate exception here
       System.out.print("Adw:FNF");
   } 
   catch(IOException e){
       //Throw Approprate Exception here
       System.out.print("IOEx");
   }
    
    return index;
}

/**
 * Returns the tags of the most recently accessed word in the lexicon. This 
 * method should only be called after the findWord method. findWord will find a 
 * list of tags associated with the word and store them.
 * @return The list of tags 
 * @see findWord
 * @since v1.0
 */
public final Tag[] getWordTags(){
    return wordTags;
}

/**
 * Finds a word and returns the list of tags associated with the word
 * @param word
 * @return List of tags, if word is found. null otherwise.
 * @see getWordTags
 * @see findWord
 */
public final Tag[] findWordTags(String word){
 if(this.findWord(word) != -1){
   return wordTags;   
 }
 return null;
}
/**
 * Lists all the words in the word list.
 * @return an array containing all the words
 */
public String[] listWords(){
  
  long wordlp;
  int wordll;
  
  String worda[];
  RandomAccessFile rf;
  
  try{
      rf = new RandomAccessFile(file,"rw");
      rf.seek(0x03);
      rf.readShort();
      wordlp = rf.readLong();
      
      rf.seek(wordlp);
      
      wordll = rf.readInt();
      
      worda = new String[wordll];
      for(int i=0;i<wordll;i++){
         worda[i] = rf.readUTF();
         int tagc = rf.readInt();
         for(int j=0;j<tagc;j++){
             rf.readInt(); //Skips through the list of tags
         }
      }
      rf.close();
      return worda;
  }catch(FileNotFoundException e){
      
  }catch(IOException e){
      
  }  
  return null;   
}

/**
 * Finds the number of words in the lexicon.
 * @return the number of words in the lexicon
 * @throws FileNotFoundException if the lexicon file can't be found
 */
public final int wordCount() throws FileNotFoundException{
    int count = -1;
    
    RandomAccessFile rf = new RandomAccessFile(this.file,"rw");
    try{
     rf.seek(0x03);
     rf.readShort();
     
     rf.seek(rf.readLong());
     
     count = rf.readInt();
     
     
    }
    catch(IOException e){
        //Throw appropriate exception here
        System.out.println("C:ERR");
    }
    return count;
}

/**
 * Changes the word file used for the lexicon.
 * @param wordFile 
 * @throws FileNotFoundException if the lexicon file cannot be found.
 * @throws IOException if there was an error writing to the file
 */
public final void setWordFile(File wordFile) throws IOException,FileNotFoundException{
    
    byte[] S_HEADER = {0, 0x50,0,0,0,0,0,0,0,(byte)0x80}; //This is the standard header file. 
    
    if(!wordFile.exists()){
        wordFile.createNewFile();
    }
    this.file = wordFile;
    
    //Set up the file if its not valid.
    if(!this.hasMagic()){
        RandomAccessFile f = new RandomAccessFile(wordFile,"rw");
       try{ 
         f.write(MAGIC);
         f.write(S_HEADER);
         f.writeUTF("Taglang version "+Main.VERSION);
         for(int i=0; i<100; i++){
             f.write(0);
         }
         f.close();
         
       }catch(IOException e){
          System.out.println("IO Error while setting lexicon file"); //TODO: Change this
       }
    }
    
    
}

/**
 *  Appends tag to a word.
 * @param tags - Tag indices
 * @param wordlp - Pointer to the word to append to
 * @param rf
 * @throws IOException 
 */
private void appendTags(int[] tags, long wordlp, RandomAccessFile rf) throws IOException{
    int wordll;
    rf.seek(wordlp);
    
    rf.readUTF();
    int tagll= rf.readInt();
    for(int i=0;i<tagll;i++){
        rf.readInt();
    }
    for(int i=0;i<tags.length;i++){
        FileAccess.insert(rf, (int)tags[i]);
    }
}

/**
 * Test whether or not the file has a valid magic number. The magic number is
 * <i>54 4c 00</i>.
 * @return true if the magic number is valid, false otherwise
 */
private boolean hasMagic(){
   RandomAccessFile raf; 
   byte[] B = {0,0,0};
   try{ 
     raf = new RandomAccessFile(file,"rw");
     raf.read(B);
     raf.close();
   }catch(IOException e){
       return false;
   }
   
   
   
  
   if(Arrays.equals(B, MAGIC)){
       return true;
   }
   return false;
}



}
/*
 *Contains various various static methods for accessing the file
 */
class FileAccess{
    /*Inserts data into the file (at the current location of the pointer) without
    overriding the previously inserted data.
    */
    public static  void insert(RandomAccessFile rf, byte[] data) throws IOException{
        RandomAccessFile rtmp = new RandomAccessFile(File.createTempFile("_graammar", ".tmp"),"rw");
        long fileSize = rf.length();
        
        FileChannel sChannel = rf.getChannel(); //Source channel
        long oldOffset;
        try (FileChannel tChannel = rtmp.getChannel() //Target channel
        ) {
            sChannel.transferTo(rf.getFilePointer(), fileSize - rf.getFilePointer(), tChannel);
            sChannel.truncate(rf.getFilePointer());
            oldOffset = rf.getFilePointer();
            rf.write(data);
            long newOffset = rf.getFilePointer();
            tChannel.position(0L);
            sChannel.transferFrom(tChannel, newOffset, fileSize - oldOffset );
        }
        rf.seek(oldOffset);
        
      
    }
    /*Inserts a String formatted in a modified UTF-8 format without overwritting
    the previosly inserted data there.
    
    */
    public static void insertUTF(RandomAccessFile rf, String str) throws IOException{
        RandomAccessFile rtmp = new RandomAccessFile(File.createTempFile("__grammar",".tmp"),"rw");
        long fileSize = rf.length();
        
        FileChannel sChannel = rf.getChannel(); //Source Channel
        FileChannel tChannel = rtmp.getChannel(); //Target Channel
        
        sChannel.transferTo(rf.getFilePointer(), fileSize - rf.getFilePointer(), tChannel);
        sChannel.truncate(rf.getFilePointer());
        
        long oldOffset = rf.getFilePointer();
        
        rf.writeUTF(str);
        
        long newOffset = rf.getFilePointer();
        tChannel.position(0L);
        sChannel.transferFrom(tChannel, newOffset, fileSize - oldOffset );
        
        tChannel.close();
        rf.seek(oldOffset);
        
    }
    
    public static void insert(RandomAccessFile rf, byte data) throws IOException{
        byte[] b = new byte[1];
        
        b[0] = data; 
        insert(rf,b);
    }
    public static void insert(RandomAccessFile rf, int data) throws IOException{
        //Integer is 4 bytes
        byte[] b = new byte[4];
        
        b[3] = (byte)(data%256);
        b[2] = (byte) ((data/256)%256);
        b[1] = (byte) ((data/65536)%256);
        b[0] = (byte) ((data/65536/256)%256);
        
        insert(rf,b);
    }
    public static void insert(RandomAccessFile rf,String data) throws IOException{
        insert(rf,data.getBytes());
    }
    
    public static String readZString(RandomAccessFile rf) throws IOException{
        byte [] b = new byte[14];
        boolean done = false;
        
        String string ="";
        StringBuilder sb = new StringBuilder();
        
        while(!done){
          long sz = rf.length() - rf.getFilePointer();
          
          if(sz < b.length){
              b = new byte[(int)sz];
          }
          
          rf.read(b);
          
          for(int i = 0;i<b.length;i++){
              if(b[i] == 0){
                  done = true;
                 return string;
              }
              sb.append(b[i]);
              string = sb.toString();
          }
          
            
        }
        return string;
        
    }
    
}
