/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.webserver;

/**
 *
 * @author Nomi
 */
public class ToolClassToLoadLibrary {

    public static void loadNativeLibrary() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + new StringBuilder(System.getProperty("java.library.path")).reverse().toString());
        System.loadLibrary("opencv_java320");
    }
    
}
