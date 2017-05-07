/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

import com.mycompany.linedetection.Line;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 *
 * @author Nomi
 */
public class DiagonalDominanceAnalyzer {
    private Mat img; 
    private List<Line> diagonalLineList;
    
    public DiagonalDominanceAnalyzer(Mat image, List dList) {
        img = image;
        diagonalLineList = dList;
    }
    
    private double minDistToThirdLines(Line line) {
        int width = img.width() - 1;
        int height = img.height() - 1;
        Point p1 = new Point(line.getX1(), line.getY1());
        Point p2 = new Point(line.getX2(), line.getY2());
        
        Line diag1 = new Line(0, 0, width, height);
        Line diag2 = new Line(width, 0, 0, height);
        
        double dist1 = 0;
        Point closestPoint = getClosestPointOnSegment(diag1, p1);
        dist1 += distanceBtwPoints(closestPoint, p1);
        
        closestPoint = getClosestPointOnSegment(diag1, p2);
        dist1 += distanceBtwPoints(closestPoint, p2);
        dist1 =  dist1 / 2;
        
        double dist2 = 0;
        closestPoint = getClosestPointOnSegment(diag2, p1);
        dist2 += distanceBtwPoints(closestPoint, p1);
        
        //Imgproc.line(img, closestPoint,p1, new Scalar(255,255,255), 1);
        closestPoint = getClosestPointOnSegment(diag2, p2);
        dist2 += distanceBtwPoints(closestPoint, p2);
        dist2 =  dist2 / 2;
        
        
        if (dist1 < dist2) {
            return dist1;
        }
        else {
            return dist2;
        
        }
    }
    
    private double calcSumOfLines() {
        double sumOfLines = 0;
        for (Line line : diagonalLineList) {
            sumOfLines += line.getLength();
        }
        return sumOfLines;
    }
    
    public double calcEDiagonalDominance() {
        if (diagonalLineList.size() > 0) {
            double eLine = calcSumOfLines();
            double sum = 0;
            for (Line line : diagonalLineList) {
                sum = sum +  (line.getLength() * Math.exp((-1 * Math.pow(minDistToThirdLines(line), 2)) / (2 * 0.17)));
            }
            System.out.println("ediagline: " + 1/eLine * sum);
            return (1/eLine * sum);
        }
        else {
            return 0;
        }
    }
    
    private double distanceBtwPoints(Point a, Point b) {
        double xDiff = Math.abs(a.x - b.x);
        double yDiff = Math.abs(a.y - b.y);
        return xDiff / img.width() + yDiff / img.height();
    }
    
    public Point getClosestPointOnSegment(Line line, Point p) {
        double xDelta = line.getX2() - line.getX1();
        double yDelta = line.getY2() - line.getY1();

        double u = ((p.x - line.getX1()) * xDelta + (p.y- line.getY1()) * yDelta) 
                / (xDelta * xDelta + yDelta * yDelta);

        Point closestPoint;
        if (u < 0) {
          closestPoint = new Point(line.getX1(), line.getY1());
        }
        else if (u > 1) {
          closestPoint = new Point(line.getX2(), line.getY2());
        }
        else {
          closestPoint = new Point((int) Math.round(line.getX1() + u * xDelta), 
                  (int) Math.round(line.getY1()+ u * yDelta));
        }

        return closestPoint;
    }
    
    
    
}
