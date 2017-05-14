/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.analyzer;

import java.awt.Rectangle;
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
    
    private int frameX;
    private int frameY;
    private int frameWidth;
    private int frameHeight;
    
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
    
    public void setFrame(int x, int y, int width, int height) {
        this.frameX = x;
        this.frameY = y;
        this.frameWidth = width;
        this.frameHeight = height;
    }
    
    public double calcRegionSize() {
        actualizeObjectList();
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
//        System.out.println("REGION SIZE: " + sum / nr);
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
        double imgArea = frameWidth * frameHeight;
        return ( rect.area() / imgArea );
    }
    
    private Rect intersection(Rect r2) {
        Rectangle awtRect1 = new Rectangle(frameX, frameY, frameX+frameWidth+1, frameY+frameHeight+1);
        Rectangle awtRect2 = new Rectangle(r2.x, r2.y, r2.width, r2.height);
        
        Rectangle intersect = awtRect1.intersection(awtRect2);
        if (intersect.width > 0 && intersect.height > 0) {
            return new Rect(intersect.x, intersect.y, intersect.width, intersect.height);
        }
        else {
            return null;
        }
    }
    
    private void actualizeObjectList() {
        List<Rect> objectsInFrame = new ArrayList(0);
        for (Rect rect : objectList) {
            Rect inters = intersection(rect);
            if (inters != null) {
                objectsInFrame.add(inters);
            }

        }
        objectList = objectsInFrame;
        objectsInFrame = new ArrayList();
        for (Rect rect : faceList) {
            Rect inters = intersection(rect);
            if (inters != null) {
                objectsInFrame.add(inters);
            }

        }
        faceList = objectsInFrame;
    }
}
