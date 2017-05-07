/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

import com.mycompany.facedetection.FaceDetector;
import com.mycompany.linedetection.Line;
import com.mycompany.linedetection.LineDetector;
import com.mycompany.objectdetection.ObjectDetector;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import toolclasstoloadlibrary.ToolClassToLoadLibrary;

/**
 *
 * @author Nomi
 */
public class Analyzer {
    private Mat img; 
    private ObjectDetector objectDetector;
    private FaceDetector faceDetector;
    private LineDetector lineDetector;
    
    private List<Rect> objectList;
    private List<Rect> faceList;
    private List<Line> lineList;
    private List<Line> diagonalLineList;
    
    private RuleOfThirdsAnalyzer ruleOfThirdsAnalyzer;
    private DiagonalDominanceAnalyzer diagonalDominanceAnalyzer;
    private VisualBalanceAnalyzer visualBalanceAnalyzer;
    
    private AestheticScoreCalculator aestheticScoreCalculator;
    private RegionSizeAnalyzer regionSizeAnalyzer;
    
    static {
        ToolClassToLoadLibrary.loadNativeLibrary();
    }
    
    public Analyzer(String fileName) {
        objectDetector = new ObjectDetector(fileName);
        faceDetector = new FaceDetector(fileName);
        lineDetector = new LineDetector(fileName);
        img = objectDetector.getImg();
        
        objectDetector.findObjects();
        objectList = objectDetector.getMainObjects();
        faceDetector.findFaces();
        faceList = faceDetector.getFaceList();
        lineDetector.findLines();
        lineList = lineDetector.getLineList();
        diagonalLineList = lineDetector.getDiagonalLineList();
        
        aestheticScoreCalculator = new AestheticScoreCalculator(img, objectList, faceList, lineList, diagonalLineList);
        regionSizeAnalyzer = new RegionSizeAnalyzer(img, objectList, faceList);
    }
    
    public double calcCombinedAestheticScore() {
        double wSZ = 0.08;
        double aestheticScore = aestheticScoreCalculator.calcAestheticScore();
        double regionsSizeScore = regionSizeAnalyzer.calcRegionSize();
        System.out.println("AESTHETIC SCORE: " + aestheticScore);
        System.out.println("REGION SIZE: " + regionsSizeScore);
        return ((1-wSZ) * aestheticScore + wSZ * regionsSizeScore);
    }
    

}
