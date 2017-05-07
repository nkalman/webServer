/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

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
    
    public VisualBalanceAnalyzer(Mat image, List<Rect> oList, List<Rect> fList) {
        img = image;
        objectList = oList;
        faceList = fList;
        
        //System.out.println("VISUAL BALANCE: " + calcEVisualBalance());
    }
    
    public double calcSumOfMass() {
        double imgArea = img.width() * img.height();
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
        int width = img.width() - 1;
        int height = img.height() - 1;
        return new Point(width / 2, height / 2);
    }
    
    public Point getAllRegionsCenter() {
        int x = 0; 
        int y = 0;
        int sumX = 0;
        int sumY = 0;
        double imgArea = img.width() * img.height();
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
        return xDiff / img.width() + yDiff / img.height();
    }
    
    public double calcEVisualBalance() {
        double distance = distanceBtwPoints(getAllRegionsCenter(), getImgCenter());
        double vb =  Math.exp(-1 * Math.pow(distance, 2) / (2 * 0.2));
        return vb;
    }
}
