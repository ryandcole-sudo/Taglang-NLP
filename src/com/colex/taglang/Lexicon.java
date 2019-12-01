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
    }catch(FileNotFoundException e){
        file =f ;
    }
}    

public final void addTags(Tag[] tags){
    addTags(tags,true);
}

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
           // FileAccess.insert(rf, (byte)0); //Insert tthe seperator (0) byte for tags. //CONSIDER: REMOVE
           System.out.print(tagi[i]); //DEBUG
            FileAccess.insertUTF(rf, tags[i].text); 
            
            wordlp += tags[i].text.length() + 1; 
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
    
    Tag tag;
    
    short taglp; //Pointer to the taglist
    int tagll; //Number of tags in the taglist
    
   try{ 
    rf = new RandomAccessFile(this.file,"rw");
    
    rf.seek(0x03);
    
    taglp = rf.readShort();
    
    rf.seek(taglp);
    
    tagll = rf.readInt();
    
    if(index > tagll){
        //ERROR: Index out of bounds
        System.out.println("Error: Tag index out of bounds");
    }
    
    int b = 3; //Temporary variable initialized to arbitrary non zero value
    
    while( b != 0){
        rf.readUTF();
        
    }
    
    
    
   }catch(FileNotFoundException e){
       
       e.printStackTrace();
       
   }catch(IOException e){
       e.printStackTrace();
   }
   
    tag = new Tag("");
    return tag;
    
}

/**
 * Finds the index of various tags
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
         String tagstr = "$";
         tagstr = tagstr.concat(str);
         if(tagstr.equals(tags[j].toString())){
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
 * Finds a tag
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

  short taglp; //Pointer to taglist
  long wordlp; //Pointer to wordlist
  
  int tagll; //Number of tags in the taglist
  int wordll; //Number of words in the wordlist
  
  int tagi[] = new int[tags.length]; //Index for each tag
  
  if(checkWordDuplicates){
     if(findWord(word)>0){
         return;
     }
  }
 
  try{
    rf = new RandomAccessFile(this.file,"rw");
  
    rf.seek(0x03);
    taglp = rf.readShort(); //Pointer to taglist
    wordlp = rf.readLong(); //Pointer to wordlist
  
   
    addTags(tags,checkTagDuplicates); //Adds list of tags 
    tagi = findTags(tags);
  
    rf.seek(wordlp);
    wordll = rf.readInt();
  //Add word tag indices
    FileAccess.insertUTF(rf,word); //Adds word
    FileAccess.insert(rf, (int)0);
    for(int i=0;i<tags.length;i++){
       FileAccess.insert(rf, tagi[i]);
    }
    rf.seek(wordlp);
    rf.writeInt(wordll);
    System.out.print(wordll); //What? DEBUG
  
    rf.close();
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
 *  Finds a word in the lexicon and returns the index of the word found. The word
 *  tags can later be accessed by getWordTags.
 * @param word the word to search for.
 * @return  the index of the word found, -1 if word isn't found.
 */
public final int findWord(String word){
    long wordlp; //Pointer to the word list
    long taglp; //Pointer to the taglist
    
    boolean found; //Whether or not the word was found
    
    int wordll; //Number of words in the file
    int tagll;
    boolean[] tagb; //Boolean expressing a list of all tags in the file
    int tagn = 0; //Number of tags for the word
    
    int index = -1;
    
    RandomAccessFile rf;
   try{ 
    rf = new RandomAccessFile(this.file,"rw");
    
    rf.seek(0x03);
 
    
    rf.readShort();
    wordlp = rf.readLong();
    
    //Find the word
    rf.seek(wordlp);
    wordll = rf.readInt();
    
    for(int i=0; i<wordll;i++){
        String str;
        int b = 3; //not zero
        tagn = 0; //Number of tags on the word
        while (b != 0){
            b = rf.readInt(); //Read tag indices
            tagn++;
        }
        str = rf.readUTF();
        System.out.println(str); //DEBUG
        if(str.equals(word)){
            break;
        }
        
    }
    
    //TODO:REMOVE
    /*
    for(int i=0;i<wordll;i++){     
        String text;
        int b =-1;
        
        
       text = FileAccess.readZString(rf);
        
       if(text.equals(word)){
           found = true;
           index = wordll - i;
           
           while(b != 0){
               b = rf.readInt();
               tagn++;
           }
          
       }else{
           while(b != 0){
               b = rf.readInt(); //Skip through the tag pointers
           }
           break; //Exit the for loop, the word has been found;
           
       }
    }
    */
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
 * Returns the tags of the most recently accessed word in the lexicon
 * @return The list of tags 
 */
public final Tag[] getWordTags(){
    return wordTags;
}

/**
 * Finds a word and returns the list of tags associated with the word
 * @param word
 * @return List of tags, if word is found. null otherwise.
 */
public final Tag[] findWordTags(String word){
 if(this.findWord(word) != -1){
   return wordTags;   
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
 */
public final void setWordFile(File wordFile) throws FileNotFoundException{
    
    byte[] S_HEADER = {0, 0x50,0,0,0,0,0,0,0,(byte)0x80}; //This is the standard header file. 
    
    if(!wordFile.exists()){
        throw new FileNotFoundException();
    }
    this.file = wordFile;
    
    //Set up the file if its not valid.
    if(!this.hasMagic()){
        RandomAccessFile f = new RandomAccessFile(wordFile,"rw");
       try{ 
         f.write(MAGIC);
         f.write(S_HEADER);
         f.writeUTF("Taglang version 1.0");
         for(int i=0; i<100; i++){
             f.write(0);
         }
         f.close();
         
       }catch(IOException e){
          System.out.println("IO Error while setting lexicon file");
       }
    }
    
    
}
/**
 * Test whether or not the file has a valid magic number. The magic number is
 * <i>54 4c 00</i>.
 * @return true if the magic number is valid, false otherwise
 */
public final boolean hasMagic(){
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
        FileChannel tChannel = rtmp.getChannel(); //Target channel
        
        sChannel.transferTo(rf.getFilePointer(), fileSize - rf.getFilePointer(), tChannel);
        sChannel.truncate(rf.getFilePointer());
        
        long oldOffset = rf.getFilePointer(); 
        
        rf.write(data);
        
        long newOffset = rf.getFilePointer();
        tChannel.position(0L);
        sChannel.transferFrom(tChannel, newOffset, fileSize - oldOffset );
        
        tChannel.close();
        rf.seek(oldOffset);
        
       
      
    }
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
