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
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 *
 * @author Nomi
 */
public class VisualBalanceAnalyzer {
    private Mat img;
    private List<Rect> objectList;
    private List<Rect> faceList;
    
    private int frameX;
    private int frameY;
    private int frameWidth;
    private int frameHeight;
    
    public VisualBalanceAnalyzer(Mat image, List<Rect> oList, List<Rect> fList) {
        img = image;
        objectList = oList;
        faceList = fList;
    }
    
    public void setFrame(int x, int y, int width, int height) {
        this.frameX = x;
        this.frameY = y;
        this.frameWidth = width;
        this.frameHeight = height;
    }
    
    public double calcSumOfMass() {
        double imgArea = frameWidth * frameHeight;
        double weight = imgArea * 3/100;
        double sumOfMass = 0;
        for (Rect rect : objectList) {
            sumOfMass += rect.area();
        }
        for (Rect rect : faceList) {
            sumOfMass += rect.area() + weight;
        }    
        return sumOfMass;
    }
    
    public Point getRectCenter(Rect rect) {
        int width = rect.width - 1;
        int height = rect.height - 1;
        return new Point(rect.x + (width / 2), rect.y + (height / 2) );
    }
    
    public Point getImgCenter() {
        int width = frameWidth - 1;
        int height = frameHeight - 1;
        return new Point(frameX + width / 2, frameY + height / 2);
    }
    
    public Point getAllRegionsCenter() {
        int x = 0; 
        int y = 0;
        int sumX = 0;
        int sumY = 0;
        double imgArea = frameWidth * frameHeight;
        double weight = imgArea * 3/100;
        Point actualCenter;
        if (objectList.size() + faceList.size() > 0) {
            for (Rect rect : objectList) {
                actualCenter = getRectCenter(rect);
                x = (int)actualCenter.x;
                y = (int)actualCenter.y;
                sumX += (x * rect.area());
                sumY += (y * rect.area());
            }
            for (Rect rect : faceList) {
                actualCenter = getRectCenter(rect);
                x = (int)actualCenter.x;
                y = (int)actualCenter.y;
                sumX += (x * (rect.area() + weight));
                sumY += (y * (rect.area() + weight));
            }
            sumX = (int)(sumX / calcSumOfMass());
            sumY = (int)(sumY / calcSumOfMass());
        }
        return new Point(sumX, sumY);
    }
    
    private double distanceBtwPoints(Point a, Point b) {
        double xDiff = Math.abs(a.x - b.x);
        double yDiff = Math.abs(a.y - b.y);
        return xDiff / frameWidth + yDiff / frameHeight;
    }
    
    public double calcEVisualBalance() {
        actualizeObjectList();
        double distance = distanceBtwPoints(getAllRegionsCenter(), getImgCenter());
        double vb =  Math.exp(-1 * Math.pow(distance, 2) / (2 * 0.2));
        return vb;
    }
    
    private Rect intersection(Rect r2) {
        Rectangle awtRect1 = new Rectangle(frameX, frameY, frameX + frameWidth+1, frameY + frameHeight+1);
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
