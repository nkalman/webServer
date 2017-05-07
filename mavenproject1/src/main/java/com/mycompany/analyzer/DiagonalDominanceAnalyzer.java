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

/**
 *
 * @author Nomi
 */
public class DiagonalDominanceAnalyzer {
    private Mat img; 
    private List<Line> diagonalLineList;
    
    private List<Line> frameLines;
    
    private int frameX;
    private int frameY;
    private int frameWidth;
    private int frameHeight;
    
    public DiagonalDominanceAnalyzer(Mat image, List dList) {
        img = image;
        diagonalLineList = dList;
        frameLines =  new ArrayList();
    }
    
    private double minDistToDiagonal(Line line) {
        Point p1 = new Point(line.getX1(), line.getY1());
        Point p2 = new Point(line.getX2(), line.getY2());
        
        Line diag1 = new Line(frameX, frameY, frameX + frameWidth - 1, frameY + frameHeight - 1);
        Line diag2 = new Line(frameX + frameWidth - 1, 0, 0, frameY + frameHeight - 1);
        double dist1 = 0;
        Point closestPoint = getClosestPointOnSegment(diag1, p1);
        dist1 += distanceBtwPoints(closestPoint, p1);

        closestPoint = getClosestPointOnSegment(diag1, p2);
        dist1 += distanceBtwPoints(closestPoint, p2);
        dist1 =  dist1 / 2;

        double dist2 = 0;
        closestPoint = getClosestPointOnSegment(diag2, p1);
        dist2 += distanceBtwPoints(closestPoint, p1);
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
    
    public void setFrame(int x, int y, int width, int height) {
        this.frameX = x;
        this.frameY = y;
        this.frameWidth = width;
        this.frameHeight = height;
    }
    
    private double calcSumOfLines() {
        double sumOfLines = 0;
        for (Line line : diagonalLineList) {
            sumOfLines += line.getLength();
        }
        return sumOfLines;
    }
    
    public double calcEDiagonalDominance() {
        calculateFrameLines();
        actualizeLineList();
        if (diagonalLineList.size() > 0) {
            double eLine = calcSumOfLines();
            double sum = 0;
            for (Line line : diagonalLineList) {
                sum = sum +  (line.getLength() * Math.exp((-1 * Math.pow(minDistToDiagonal(line), 2)) / (2 * 0.17)));
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
        Line line = new Line(frameX, frameY, frameX+frameWidth-1, frameY);
        frameLines.add(line);
        line = new Line(frameX, frameY, frameX, frameY+frameHeight-1);
        frameLines.add(line);
        line = new Line(frameX+frameWidth-1, frameY, frameX+frameWidth-1, frameY+frameHeight-1);
        frameLines.add(line);
        line = new Line(frameX, frameY+frameHeight-1, frameX+frameWidth-1, frameY+frameHeight-1);
        frameLines.add(line);
    }
    
    private boolean isPointInFrame(Point p) {
        if (p.x >= frameX-1 && p.x <= frameX+frameWidth+1 &&
                p.y >= frameY-1 && p.y <= frameY+frameHeight+1) {
            return true;
        }
        return false;
    }
    
   private Line getLineSegmentInFrame(Line originalLine) {
        ArrayList<Point> points = new ArrayList();
        Point intersect;
        int nullNr = 0;
        
        for (Line line : frameLines) {
            //System.out.println("-----------");
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
        for (Line line : diagonalLineList) {
            Line lineInFrame = getLineSegmentInFrame(line);
            if (lineInFrame != null) {
                actualLines.add(lineInFrame);
            }
        }
        diagonalLineList = actualLines;
    }
}
