/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.analyzer;

import com.mycompany.linedetection.Line;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 *
 * @author Nomi
 */
public class RuleOfThirdsAnalyzer {
    private Mat img; 
    private List<Rect> objectList;
    private List<Rect> faceList;
    private List<Line> lineList;
    
    //metric variables
    
    private List<Point> powerPoints;
    private List<Line> thirdLines;
    
    public RuleOfThirdsAnalyzer(Mat image, List<Rect> oList, List<Rect> fList, List<Line> lList) {
        img = image;
        objectList = oList;
        faceList = fList;
        lineList = lList;
        
        powerPoints = new ArrayList();
        thirdLines = new ArrayList();
        calculatePowerPoints();    
        calculateThirdLines();
        
        //System.out.println("RULE OF THIRDS: " + calcERuleOfThirds());
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
     
    private void calculatePowerPoints() {
        int width = img.width();
        int height = img.height();
        powerPoints.add(new Point((width-1)/3, (height-1)/3));
        powerPoints.add(new Point((width-1)/3 * 2, (height-1)/3));
        powerPoints.add(new Point((width-1)/3, (height-1)/3 * 2));
        powerPoints.add(new Point((width-1)/3 * 2, (height-1)/3 * 2));
    }
    
    private double minDistToPowerPoints(Rect rect) {
        Point center = new Point(rect.x + (rect.width - 1) / 2, rect.y + (rect.height - 1) / 2);
        double min = 100000000;
        double actualDist = 0;
        for (Point p : powerPoints) {
            actualDist = distanceBtwPoints(center, p);
            if (actualDist < min) {
                min = actualDist;
            }
        }
        return min;
    }
    
    private double distanceBtwPoints(Point a, Point b) {
        double xDiff = Math.abs(a.x - b.x);
        double yDiff = Math.abs(a.y - b.y);
        return xDiff / img.width() + yDiff / img.height();
    }
    
    private double calcEPoint() {
        double imgArea = img.width() * img.height();
        double weight = imgArea * 3/100;
        if (objectList.size() + faceList.size() > 0) {
            double ePoint = calcSumOfMass();
            double sum = 0;
            for (Rect rect : objectList) {
                sum = sum +  (rect.area() * Math.exp((-1 * Math.pow(minDistToPowerPoints(rect), 2)) / (2 * 0.17)));
            }
            for (Rect rect : faceList) {
                sum = sum +  ((rect.area()+ weight) * Math.exp((-1 * Math.pow(minDistToPowerPoints(rect), 2)) / (2 * 0.17)));
            }
            System.out.println("\n" + "epoint: " + 1/ePoint * sum);
            return (1/ePoint * sum);
        }
        else {
            return 0;
        }
    }
    
    private void calculateThirdLines() {
        int width = img.width() - 1;
        int height = img.height() - 1;
        thirdLines.add(new Line(width/3, 0, width/3, height));
        thirdLines.add(new Line(2*width/3, 0, 2*width/3, height));
        thirdLines.add(new Line(0, height/3, width, height/3));
        thirdLines.add(new Line(0, 2*height/3, width, 2*height/3));  
    }
    
    private double minDistToThirdLines(Line line) {
        int width = img.width() - 1;
        int height = img.height() - 1;
        Point p1 = new Point(line.getX1(), line.getY1());
        Point p2 = new Point(line.getX2(), line.getY2());
        
        ArrayList<Double> distances = new ArrayList();
        double dist = 0;
        
        for (Line thLine : thirdLines) {
            Point closestPoint = getClosestPointOnSegment(thLine, p1);
            dist = distanceBtwPoints(closestPoint, p1); 
            closestPoint = getClosestPointOnSegment(thLine, p2);
            dist += distanceBtwPoints(closestPoint, p2);
            distances.add(dist);
 
        }
               
        double min = distances.get(0);
        for (int i = 1; i < distances.size(); ++i) {
            if (distances.get(i) < min) {
                min = distances.get(i);
            }
        }
        
        return min;
    }
    
    private double calcSumOfLines() {
        double sumOfLines = 0;
        for (Line line : lineList) {
            sumOfLines += line.getLength();
        }
        return sumOfLines;
    }
    
    private double calcELine() {
        if (lineList.size() > 0) {
            double eLine = calcSumOfLines();
            double sum = 0;
            for (Line line : lineList) {
                sum = sum +  (line.getLength() * Math.exp((-1 * Math.pow(minDistToThirdLines(line), 2)) / (2 * 0.17)));
            }
            System.out.println("eline: " + 1/eLine * sum);
            return (1/eLine * sum);
        }
        else {
            return 0;
        }
    }
    
    public double calcERuleOfThirds() {
        return (calcEPoint()* 1/3 + calcELine() * 2/3);
    }
    
//    public BufferedImage mat2BufferedImage(Mat m){
//        int type = BufferedImage.TYPE_BYTE_GRAY;
//        if ( m.channels() > 1 ) {
//            type = BufferedImage.TYPE_3BYTE_BGR;
//        }
//        int bufferSize = m.channels()*m.cols()*m.rows();
//        byte [] b = new byte[bufferSize];
//        m.get(0,0,b); // get all the pixels
//        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
//        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        System.arraycopy(b, 0, targetPixels, 0, b.length);  
//        return image;
//    }
    
//    public void showImage(BufferedImage img) {
//        ImageIcon icon=new ImageIcon(img);
//        JFrame frame=new JFrame();
//        frame.setLayout(new FlowLayout());        
//        frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
//        JLabel lbl=new JLabel();
//        lbl.setIcon(icon);
//        frame.add(lbl);
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
    
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
