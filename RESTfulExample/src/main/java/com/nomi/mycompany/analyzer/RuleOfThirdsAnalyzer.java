/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.analyzer;

import com.nomi.mycompany.linedetection.Line;
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
public class RuleOfThirdsAnalyzer {
    private Mat img; 
    private List<Rect> objectList;
    private List<Rect> faceList;
    private List<Line> lineList;
    
    //metric variables
    
    private List<Point> powerPoints;
    private List<Line> thirdLines;
    private List<Line> frameLines;
    
    private int frameX;
    private int frameY;
    private int frameWidth;
    private int frameHeight;
    
    public RuleOfThirdsAnalyzer(Mat image, List<Rect> oList, List<Rect> fList, List<Line> lList) {
        img = image;
        objectList = oList;
        faceList = fList;
        lineList = lList;
        
        powerPoints = new ArrayList();
        thirdLines = new ArrayList();
        frameLines =  new ArrayList();
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
     
    
    public void calculatePowerPoints() {
        powerPoints = new ArrayList();
        int width = frameWidth;
        int height = frameHeight;
        powerPoints.add(new Point(frameX + (width-1)/3, frameY + (height-1)/3));
        powerPoints.add(new Point(frameX + (width-1)/3 * 2, frameY + (height-1)/3));
        powerPoints.add(new Point(frameX + (width-1)/3, frameY + (height-1)/3 * 2));
        powerPoints.add(new Point(frameX + (width-1)/3 * 2, frameY + (height-1)/3 * 2));
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
        return xDiff / frameWidth + yDiff / frameHeight;
    }
    
    private double calcEPoint() {
        double imgArea = frameWidth * frameHeight;
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
            return (1/ePoint * sum);
        }
        else {
            return 0;
        }
    }
    
    private void calculateThirdLines() {
        thirdLines = new ArrayList();
        int width = frameWidth - 1;
        int height = frameHeight - 1;
        thirdLines.add(new Line(frameX + width/3, frameY, width/3, frameY + height));
        thirdLines.add(new Line(frameX + 2*width/3, frameY, 2*width/3, frameY + height));
        thirdLines.add(new Line(frameX, height/3, frameY + width, frameY + height/3));
        thirdLines.add(new Line(frameX, 2*height/3, frameY + width, frameY + 2*height/3));  
    }
    
    private double minDistToThirdLines(Line line) {
        int width = frameWidth - 1;
        int height = frameHeight - 1;
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
        if (lineList.size() > 0) {
            for (Line line : lineList) {
                sumOfLines += line.getLength();
            }
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
            return (1/eLine * sum);
        }
        else {
            return 0;
        }
    }
    
    public double calcERuleOfThirds() {
        calculatePowerPoints();    
        calculateThirdLines();
        calculateFrameLines();
        actualizeLineList();
        actualizeObjectList();
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
    
    private Point intersPointOfTwoLines(Line l1, Line l2) {
        double x1 = l1.getX1();
        double x2 = l1.getX2();
        double y1 = l1.getY1();
        double y2 = l1.getY2();
        
        double x3 = l2.getX1();
        double x4 = l2.getX2();
        double y3 = l2.getY1();
        double y4 = l2.getY2();
        
        double d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
        if (d == 0) {
            return null;
        }

        double xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
        double yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;
        
        if (!isPointInFrame(new Point(xi,yi))) {
            return null;
        }
        return new Point(xi,yi);
    }
    
    private void calculateFrameLines() {
        frameLines = new ArrayList();
        Line line = new Line(frameX, frameY, frameX+frameWidth, frameY);
        frameLines.add(line);
        line = new Line(frameX, frameY, frameX, frameY+frameHeight);
        frameLines.add(line);
        line = new Line(frameX+frameWidth, frameY, frameX+frameWidth, frameY+frameHeight);
        frameLines.add(line);
        line = new Line(frameX, frameY+frameHeight, frameX+frameWidth, frameY+frameHeight);
        frameLines.add(line);
    }
    
    private boolean isPointInFrame(Point p) {
        if (p.x >= frameX-1 && p.x <= frameX + frameWidth+1 &&
                p.y >= frameY-1 && p.y <= frameY + frameHeight+1) {
            return true;
        }
        return false;
    }
    
    private Line getLineSegmentInFrame(Line originalLine) {
        ArrayList<Point> points = new ArrayList();
        Point intersect;
        int nullNr = 0;
        
        for (Line line : frameLines) {
            intersect = intersPointOfTwoLines(line, originalLine);
            if (intersect != null) {
                if (isPointOnLine(intersect, originalLine)) {
                    if (intersect.x == 0 && intersect.y == 0 && nullNr == 0) {
                        points.add(intersect);
                        nullNr++;
                    }
                    else if (intersect.x == 0 && intersect.y == 0) {
                    }
                    else {
                        points.add(intersect);
                    }
                }
            }
            
            if (points.size() == 0) {
                Point startPoint = new Point(originalLine.getX1(), originalLine.getY1());
                Point endPoint = new Point(originalLine.getX2(), originalLine.getY2());
                if (isPointInFrame(startPoint) && isPointInFrame(endPoint)) {
                    return originalLine;
                }
            }
            else if (points.size() == 1) {
                Point inters = points.get(0);
                Point second = new Point(originalLine.getX1(), originalLine.getY1());
                if (!isPointInFrame(second)) {
                    second = new Point(originalLine.getX2(), originalLine.getY2());
                }
                return new Line(inters.x, inters.y, second.x, second.y);
            }
            else if (isPointInFrame(points.get(0)) && isPointInFrame(points.get(1))){
                return new Line(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
            }
        }
        return null;
    }
    
    private boolean isPointOnLine(Point point, Line line) {
        Point p1 = new Point(line.getX1(), line.getY1());
        Point p2 = new Point(line.getX2(), line.getY2());
        return normalDistBtwPoints(p1,point) + normalDistBtwPoints(p2, point) == normalDistBtwPoints(p1, p2);
    }
    
    private double normalDistBtwPoints(Point p1, Point p2) {
        return (Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2)));
    }
    
    private void actualizeLineList() {
        List<Line> actualLines = new ArrayList();
        for (Line line : lineList) {
            Line lineInFrame = getLineSegmentInFrame(line);
            if (lineInFrame != null) {
                actualLines.add(lineInFrame);
            }
        }
        lineList = actualLines;
    }
    
    private Rect intersection(Rect r2) {
        Rectangle awtRect1 = new Rectangle(frameX, frameY, frameX + frameWidth+1,frameY + frameHeight+1);
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
