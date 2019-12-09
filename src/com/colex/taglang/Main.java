package com.colex.taglang;


import java.util.Scanner;
import java.io.*;

public class Main {

    final static double VERSION = 1.0;
    
    private static Lexicon lex;
    private static Grammar rule;

    /**
     * The Taglang Main class provides a command-line interface to the taglang
     * utility.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) throws Exception {

        String command;
        Scanner input = new Scanner(System.in);
    
        File f;
  
        System.out.println("Taglang " + VERSION + " NLP Command Line Utility");

        while (true) {
            System.out.print("$>");
            command = input.next();
            if (command.equals("exit")) {
                input.close();
                System.exit(0);
            }

            if (command.equals("help")) {

                try {
                    f = new File("src/com/colex/taglang/help.txt");
                    Scanner fs = new Scanner(f);
                    while (fs.hasNext()) {
                        System.out.print(fs.nextLine());
                        System.out.println();
                    }
                    System.out.println();
                    fs.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Configuration Error: Invalid Configuration: 1021"); //TODO: Change this
                }

            }

            if (command.equals("version")) {
                System.out.println(Main.VERSION);
            }
            
            if (command.equals("add")) { //Adds a new...
              
               command = input.next();
               
               if(command.equals("rule")){
                   command = input.nextLine();
                   rule.addRule(command.trim());
               }
               if(command.equals("word")){
                   command = input.next();
                   String tags = input.nextLine();
                   String taga[] = tags.split(" ");
                   Tag tag[] = new Tag[taga.length];
                   for(int i=0;i<taga.length;i++){
                       tag[i] = new Tag(taga[i],Tag.TagType.DEFAULT);
                   }
                   lex.addWord(command, tag);
               }
               continue;
            }
            
            if(command.equals("count")){ //Returns the number of...
                command = input.nextLine();
                if(command.equals("words")){
                    input.nextLine();
                    System.out.println(lex.wordCount());
                }
                if(command.equals("rule")){
                    input.nextLine();
                    System.out.println(rule.ruleCount());
                }
                continue;
            }
            
            if(command.equals("list")){ //List all...
              command = input.next();
              if(command.equals("words")){
                  //TODO: Add method to list words here
              }
            }
            if(command.equals("lex")){ //Sets lexicon 
                command = input.nextLine();
                f = new File(command.trim());
                lex = new Lexicon(f);
            }
            if(command.equals("rule")){ //Sets grammar file
                command = input.nextLine();
                f = new File(command.trim());
                rule =new Grammar(f);
            }

        }
    }

    @Override
    public String toString() {
        return "Taglang version " + VERSION;
    }

}
