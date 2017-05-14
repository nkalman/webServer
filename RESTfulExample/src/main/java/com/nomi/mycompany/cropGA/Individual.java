/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.cropGA;

import com.nomi.mycompany.analyzer.Analyzer;
import java.util.Random;

/**
 *
 * @author Nomi
 */
public class Individual {
    private double x;
    private double y;
    private double width;
    private double height;
    
    
    private double aestheticScore = 0;
    private Analyzer analyzer;
    
    public Individual(Analyzer an) {
        analyzer = an;
    }

    // Create a random individual
    public void generateIndividual(int originalWidth, int originalHeight) {
        //generate x y and width randomly between the possible and optimum bounds
        double frameRatio =  (double)originalHeight / (double)originalWidth;
        
        Random rand = new Random();
        x = rand.nextInt(originalWidth/2 + 1);
        rand = new Random();
        y = rand.nextInt(originalHeight/2 + 1);
        
        //System.out.println("x= " + x + "  y= " + y);
        
        //give some impossible values at the beginning
        width = originalWidth + 100;
        height = originalHeight + 100;
        
        //if it is between the bounds, the stop the iteration
        while (width + x > originalWidth || height + y > originalHeight) {
            rand = new Random();
            width = rand.nextInt(originalWidth - originalWidth/2 + 1) + originalWidth/2;
                //height = rand.nextInt(originalHeight - originalHeight/2 + 1) + originalHeight/2;
            height = frameRatio * width;
            height = (int)height;
        }
        //System.out.println("w1= " + width + "  h1= " + height);
        
        //height = (int)height;
    }
    
    public void generateFirstIndividual(int originalWidth, int originalHeight) {
        x = 0;
        y = 0;
        width = originalWidth;
        height = originalHeight;
    }
    

    /* Getters and setters */
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }

    public double getAestheticScore() {
        if (aestheticScore == 0) {
            analyzer.setFrame((int)x, (int)y, (int)width, (int)height);
            aestheticScore = analyzer.calcCombinedAestheticScore();
        }
        return aestheticScore;
    }

    @Override
    public String toString() {
        String genes;
        genes = "geneX= " + x + " geneY= " + y + " geneWidth= " + width + " geneheight= " + height;
        return genes;
    }
}
