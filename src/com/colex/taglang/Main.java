package com.colex.taglang;



import java.util.Scanner;
import java.io.*;
public class Main {
	final static double VERSION = 1.0;
         /**
          * The Taglang Main class provides a command-line 
          * interface to the taglang utility.
          * @param args Command-line arguments
          */
	public static void main(String[] args) throws Exception{
		String command;
		Scanner input = new Scanner(System.in);
                
                Tag[] tg = new Tag[3];
                
                //delete the s
                File fl = new File("C:/Users/Neville/Documents/code/Dum/tag.lx");    
                Lexicon lex = new Lexicon(fl);
                
              
                System.out.println(lex.findWord("ship"));
      
                //deelete
	
		System.out.println("Taglang 1.0 NLP Command Line Utility");
                
                while(true) {
		   System.out.print("$>");
		   command = input.next();
		   if(command.equals("exit")) {
			   input.close();
			   System.exit(0);
		   }
		   
		   if(command.equals("help")) {
			  
			   
			try {
				File f = new File("src/com/colex/taglang/help.txt");
				Scanner fs = new Scanner(f);
				while(fs.hasNext()) {
				System.out.print(fs.nextLine());
				
				
				

                System.out.println();
				}
				System.out.println();
				fs.close();
			 }catch (FileNotFoundException e) {
				System.err.println("Configuration Error: Invalid Configuration: 1021");
			 }
			
			    
	       }
		   
		   if(command.equals("version")) {
				 System.out.println(Main.VERSION);	
				 continue;	
		    }  
				
		   
        
		
	  }
   }
	@Override
	public String toString() {
		return "Taglang version 1.0";
	}
        
        
        
        
        
	
	
}


