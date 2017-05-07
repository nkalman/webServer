/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 *
 * @author Nomi
 */
public class RegionSizeAnalyzer {
    private Mat img; 
    private List<Rect> objectList;
    private List<Rect> faceList;
    private List<Double> rValues;
    private List<Double> rhoValues;
    
    public RegionSizeAnalyzer(Mat image, List<Rect> oList, List<Rect> fList) {
        img = image;
        objectList = oList;
        faceList = fList;
        rValues = new ArrayList();
        rValues.add(0.1);
        rValues.add(0.56);
        rValues.add(0.82);
        
        rhoValues = new ArrayList();
        rhoValues.add(0.07);
        rhoValues.add(0.2);
        rhoValues.add(0.16);
        
        
    }
    
    public double calcRegionSize() {
        double sum = 0;
        double num = 0;
        int nr = 0;
        List<Double> vals;
        if(objectList.size() + faceList.size() > 0) {
            for (Rect rect : objectList) {
                vals = new ArrayList();
                for (int i = 0; i < 3; ++i) {
                    num = (calcAreaRatio(rect) - rValues.get(i)) * 
                            (calcAreaRatio(rect) - rValues.get(i))  * (-1);
                    num = num / (2*rhoValues.get(i));
                    vals.add(Math.exp(num));
                }
                sum += maxima(vals);
                nr++;
            }

            for (Rect rect : faceList) {
                vals = new ArrayList();
                for (int i = 0; i < 3; ++i) {
                    num = (calcAreaRatio(rect) - rValues.get(i)) * 
                            (calcAreaRatio(rect) - rValues.get(i))  * (-1);
                    num = num / (2*rhoValues.get(i));
                    vals.add(Math.exp(num));
                }
                sum += maxima(vals);
                nr++;
            }
        
        return sum / nr;
        }
        else {
            return 0;
        }
    }
    
    private double maxima(List<Double> list) {
        double max = list.get(0);
        for (int i = 1; i < list.size(); ++i) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }
    
    private double calcAreaRatio(Rect rect) {
        double imgArea = img.width() * img.height();
        return ( rect.area() / imgArea );
    }
}
