/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

import com.mycompany.linedetection.Line;
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
    
    public AestheticScoreCalculator(Mat img, List<Rect> objectList, List<Rect> faceList, List<Line> lineList, List<Line> diagonalLineList) {
        ruleOfThirdsAnalyzer = new RuleOfThirdsAnalyzer(img, objectList, faceList, lineList);
        diagonalDominanceAnalyzer = new DiagonalDominanceAnalyzer(img, diagonalLineList);
        visualBalanceAnalyzer = new VisualBalanceAnalyzer(img, objectList, faceList); 
    }
    
    public double calcAestheticScore() {
        double rtValue = ruleOfThirdsAnalyzer.calcERuleOfThirds();
        double ddValue = diagonalDominanceAnalyzer.calcEDiagonalDominance();
        double vbValue = visualBalanceAnalyzer.calcEVisualBalance();
        double wRT = 1;
        double wDD = 0.3;
        double wVB = 1;
        
        System.out.println("RULE OF THIRDS: " + rtValue);
        System.out.println("DIAGONAL DOMINANCE: " + ddValue);
        System.out.println("VISUAL BALANCE: " + vbValue);
        
        
        return (wRT* rtValue + wDD*ddValue + wVB * vbValue) / (wRT + wDD + wVB);
    }
}
