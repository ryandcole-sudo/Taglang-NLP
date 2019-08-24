/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colex.taglang;

/**
 * This exception is thrown when there is an error loading a grammar file
 * @author Neville
 */
public class GrammarFileLoadException extends Exception{

    @Override
    public void printStackTrace(){
        System.err.print("com.colex.taglang.GrammarFileLoadException - Can't read or write grammar file\n");
     
    }
    
    
}
