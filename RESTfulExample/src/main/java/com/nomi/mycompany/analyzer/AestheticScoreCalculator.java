/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.analyzer;

import com.nomi.mycompany.linedetection.Line;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 *
 * @author Nomi
 */
public class AestheticScoreCalculator {
    private RuleOfThirdsAnalyzer ruleOfThirdsAnalyzer;
    private DiagonalDominanceAnalyzer diagonalDominanceAnalyzer;
    private VisualBalanceAnalyzer visualBalanceAnalyzer;
    
    private int x;
    private int y;
    private int width;
    private int height;
    
    public AestheticScoreCalculator(Mat img, List<Rect> objectList, List<Rect> faceList, List<Line> lineList, List<Line> diagonalLineList) {
        ruleOfThirdsAnalyzer = new RuleOfThirdsAnalyzer(img, objectList, faceList, lineList);
        diagonalDominanceAnalyzer = new DiagonalDominanceAnalyzer(img, diagonalLineList);
        visualBalanceAnalyzer = new VisualBalanceAnalyzer(img, objectList, faceList); 
    }
    
    public void setFrame(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public double calcAestheticScore() {
        ruleOfThirdsAnalyzer.setFrame(x, y, width, height);
        double rtValue = ruleOfThirdsAnalyzer.calcERuleOfThirds();
        diagonalDominanceAnalyzer.setFrame(x, y, width, height);
        double ddValue = diagonalDominanceAnalyzer.calcEDiagonalDominance();
        visualBalanceAnalyzer.setFrame(x, y, width, height);
        double vbValue = visualBalanceAnalyzer.calcEVisualBalance();
        double wRT = 1;
        double wDD = 0.3;
        double wVB = 1;
        
//        System.out.println("RULE OF THIRDS: " + rtValue);
//        System.out.println("DIAGONAL DOMINANCE: " + ddValue);
//        System.out.println("VISUAL BALANCE: " + vbValue);
        
        
        return (wRT* rtValue + wDD*ddValue + wVB * vbValue) / (wRT + wDD + wVB);
    } 
}
