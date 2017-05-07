/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.linedetection;

import static com.mycompany.analyzer.Constants.DIAGONAL_TRESHOLD;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Nomi
 */
public class LineDetector {
    private Mat img;
    private Mat edgeDetectedImg;
    private List<Line> lineList;
    private double diagAngle1;
    private double diagAngle2;
    private List<Line> diagonalLineList;
    private Line horizontalLine;
    
    public LineDetector(String fileName) {
        img = Imgcodecs.imread(fileName);
        edgeDetectedImg = new Mat();
        lineList = new ArrayList();
        diagonalLineList = new ArrayList();
        
        horizontalLine = new Line(0,0,img.width()-1,0);
        Line l2 = new Line(0,0,img.width()-1,img.height()-1);
        Line l3 = new Line(img.width()-1,0,0,img.height()-1);
        
        diagAngle1 = horizontalLine.getAngle(l2);
        diagAngle2 = horizontalLine.getAngle(l3);
    }
    
    public Mat getImg() {
        return img;
    }
    
    public Mat getEdgeDetectedImg() {
        return edgeDetectedImg;
    }
    
    public List<Line> getLineList() {
        return lineList;
    }

    public List<Line> getDiagonalLineList() {
        return lineList;
    }
    
    public void findLines() {
        Imgproc.Canny(img, edgeDetectedImg, 100, 200, 3, true); 
        Mat lines = new Mat();

        int width = img.width();
        int height = img.height();
        double diagonal = Math.sqrt(width*width + height*height);
        int minOfWidthHeight = (width<height)?width:height;
        
        Imgproc.HoughLinesP(edgeDetectedImg, lines, 1, Math.PI/180, minOfWidthHeight*10/100, diagonal*25/100, diagonal*4/100);

        int firstN = (lines.rows() < 5)?lines.rows():5;

        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x,0 );
            double[] vec1 = lines.get(x,1 );
            double x1 = vec[0], 
                   y1 = vec[1],
                   x2 = vec[2],
                   y2 = vec[3];
            Point startPoint = new Point(x1, y1);
            Point endPoint = new Point(x2, y2);

            
            double angle_inv = horizontalLine.getAngle(new Line(x1,y1,x2,y2));
            double angle = horizontalLine.getAngle(new Line(x2,y2,x1,y1));
            //System.out.print(angle);
            if ((angle >= diagAngle1 - DIAGONAL_TRESHOLD && angle <= diagAngle1 + DIAGONAL_TRESHOLD) ||
                    (angle >= diagAngle2 - DIAGONAL_TRESHOLD && angle <= diagAngle2 + DIAGONAL_TRESHOLD) ||
                    (angle_inv >= diagAngle1 - DIAGONAL_TRESHOLD && angle_inv <= diagAngle1 + DIAGONAL_TRESHOLD) ||
                    (angle_inv >= diagAngle2 - DIAGONAL_TRESHOLD && angle_inv <= diagAngle2 + DIAGONAL_TRESHOLD)){
                diagonalLineList.add(new Line(x1,y1,x2,y2));
                Imgproc.line(img, startPoint, endPoint, new Scalar(255,255,0), 4);
            }
            else {
                lineList.add(new Line(x1,y1,x2,y2));
                //Imgproc.line(img, startPoint, endPoint, new Scalar(255,0,0), 1);
            }

        }

          
        Collections.sort(lineList, new Comparator<Line>() {
             @Override public int compare(Line l1, Line l2) {
                 return (int)(l2.getLength() - l1.getLength());
             }

        });

        ArrayList arr = new ArrayList<Line>();
           
        for (int i=0; i<firstN+1; i++) {
            if (lineList.size() >= firstN+1) {
               double x1 = lineList.get(i).getX1(), 
                      y1 = lineList.get(i).getY1(),
                      x2 = lineList.get(i).getX2(),
                      y2 = lineList.get(i).getY2();
               Point startPoint = new Point(x1, y1);
               Point endPoint = new Point(x2, y2);
               arr.add(lineList.get(i));
               
               Imgproc.line(img, startPoint, endPoint, new Scalar(0,0,255), 1);
            }
        } 
        lineList = arr;
    }
}
