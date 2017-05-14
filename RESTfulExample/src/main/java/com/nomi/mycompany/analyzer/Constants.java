/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.analyzer;

/**
 *
 * @author Nomi
 */
public class Constants {
    //termcriteria parameters
    public static final int COUNT_VALUE = 5;
    public static final int EPS_VALUE = 1;
    //pyrMeanShiftFiltering parameters
    public static final int COLOR_WINDOW_RADIUS = 100;
    public static final int MAX_LEVEL = 2;
    
    //adaptiveThreshold parameters
    public static final int MAX_VALUE = 255;
    public static final int BLOCK_SIZE = 15;
    public static final int CONSTANT_OF_MEAN = 4;
    
    //canny parametes
    public static final int THRESHOLD1 = 120;
    public static final int THRESHOLD2 = 250;
    public static final int APERTURE_SIZE = 3;
    
    //diagonal lines detection
    public static final double DIAGONAL_TRESHOLD = 0.174533;
}
