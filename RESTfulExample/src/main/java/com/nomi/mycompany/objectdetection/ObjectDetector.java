/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.objectdetection;

import static com.nomi.mycompany.analyzer.Constants.APERTURE_SIZE;
import static com.nomi.mycompany.analyzer.Constants.BLOCK_SIZE;
import static com.nomi.mycompany.analyzer.Constants.COLOR_WINDOW_RADIUS;
import static com.nomi.mycompany.analyzer.Constants.CONSTANT_OF_MEAN;
import static com.nomi.mycompany.analyzer.Constants.COUNT_VALUE;
import static com.nomi.mycompany.analyzer.Constants.EPS_VALUE;
import static com.nomi.mycompany.analyzer.Constants.MAX_LEVEL;
import static com.nomi.mycompany.analyzer.Constants.MAX_VALUE;
import static com.nomi.mycompany.analyzer.Constants.THRESHOLD1;
import static com.nomi.mycompany.analyzer.Constants.THRESHOLD2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import static org.opencv.core.TermCriteria.COUNT;
import static org.opencv.core.TermCriteria.EPS;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

/**
 *
 * @author Nomi
 */
public class ObjectDetector {
    private Mat img;
    private Mat imgOut;
    private Mat imgMeanShifted;
    private Mat imgGrayscale;
    private Mat imgCanny;
    private List<MatOfPoint> contours;
    private List<Rect> objList;
    private List<Rect> mainObjects;
    private Rect mainRect;
    private Mat mRgba;
    
    public ObjectDetector(String fileName) {
        img = Imgcodecs.imread(fileName);
        imgOut = Imgcodecs.imread(fileName);
        contours = new ArrayList();
        imgMeanShifted = new Mat();
        imgGrayscale = new Mat();
        imgCanny = new Mat();
        objList = new ArrayList();
        mainObjects = new ArrayList();
        mainRect = null;
        mRgba = new Mat();
    }
    
    public Mat getImg() {
        return img; 
    }
    
    public Mat getImgMeanShifted() {
        return imgMeanShifted; 
    }
    
    public Mat getImgGrayscale() {
        return imgGrayscale; 
    }
    
    public Mat getImgCanny() {
        return imgGrayscale; 
    }

    public Mat getImgOut() {
        return imgOut; 
    }
    
    public List<Rect> getMainObjects() {
        return mainObjects; 
    }
    
    public void preProcessImg() {

        int spatialWindowRadius = ((img.width() + img.height() ) / 2);
        spatialWindowRadius = spatialWindowRadius * 3/100;
        //System.out.println(spatialWindowRadius);
        TermCriteria termCriteria = new TermCriteria(COUNT + EPS, COUNT_VALUE, EPS_VALUE);
        Imgproc.pyrMeanShiftFiltering(img, imgMeanShifted, spatialWindowRadius,
                COLOR_WINDOW_RADIUS, MAX_LEVEL, termCriteria);
    }
    
    public void toGrayScale(Mat m) {
        Imgproc.cvtColor(m, imgGrayscale, COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(imgGrayscale, imgGrayscale, MAX_VALUE,
        ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, BLOCK_SIZE, CONSTANT_OF_MEAN);
  
    }
    
    public void detectEdges(Mat m) {
        Imgproc.Canny(m, imgCanny, THRESHOLD1, 
                THRESHOLD2, APERTURE_SIZE, true); 
    }
    
    public void findObjects() {
        
//        Imgproc.cvtColor(img, imgGrayscale, Imgproc.COLOR_RGBA2GRAY, 1); 
//        Core.convertScaleAbs(img, imgGrayscale);
//        Core.normalize(imgGrayscale, imgMeanShifted, 0.0, 1.0, NORM_MINMAX);
        preProcessImg();

        toGrayScale(imgMeanShifted);
        detectEdges(imgGrayscale);
        Imgproc.findContours(imgCanny, contours, imgCanny, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        objList = new ArrayList();
        
        for ( MatOfPoint mop : contours) {
            MatOfPoint2f m2p;
            m2p = new MatOfPoint2f( mop.toArray() );
            Double peri = Imgproc.arcLength(m2p, true);
            Imgproc.approxPolyDP(m2p, m2p, 0.02*peri, true);
            Imgproc.drawContours(imgOut, contours, -1, new Scalar(0, 0, 255), 2);

            float area = img.width() * img.height();
            Rect rect = Imgproc.boundingRect(mop);
            objList.add(rect);
            Imgproc.rectangle(imgOut, rect.tl(), rect.br(), new Scalar(255, 0, 0));
        }
        
        Collections.sort(objList, new Comparator<Rect>() {
            @Override public int compare(Rect r1, Rect r2) {
                return (int)(r2.area() - r1.area());
            }

        });
        
        List<Rect> arr = objList;
        
        while (arr.size() > 0) {
            //System.out.println("---->" + arr);
            Rect bigRect = arr.get(0);
            arr.remove(0);
            Rect bigRect2 = new Rect();

            while(!equals(bigRect, bigRect2)) {
                bigRect2 = bigRect;
                for (int i=0; i < arr.size(); ++i) {
                   // System.out.println("elotte"+arr.get(i));
                    if (doOverlap(bigRect, arr.get(i))) {
                        //System.out.println("utana"+arr.get(i));
                        bigRect = union(bigRect, arr.get(i));
                        arr.remove(i);
                        break;
                    }
                }

            }

            mainRect = bigRect;
            
            if (objList.size() > 5 && mainRect.area() >= img.width() * img.height() * 3/100 ) {
                Imgproc.rectangle(imgOut, bigRect.tl(), bigRect.br(), new Scalar(255, 255, 0));
                mainObjects.add(mainRect);
            }
            else if (objList.size() <= 5) {
                mainObjects.add(mainRect);
            }
        }
        
    }
    
    public boolean doOverlap(Rect r1, Rect r2) {
        if(r1.tl().x > r2.br().x || 
                r1.br().x < r2.tl().x ||
                r1.tl().y > r2.br().y ||
                r1.br().y < r2.tl().y) {
            return false;
        }
        else {
            return true;
        }
    }
    
    public boolean equals(Rect r1, Rect r2) {
        if (r1.tl().x == r2.tl().x && r1.br().x == r2.br().x &&
                r1.tl().y == r2.tl().y && r1.br().y == r2.br().y) {
            return true;
        }
        return false;
    }
    
    public Rect union(Rect r1, Rect r2) {
        Point[] rects_pts = new Point[4];
        rects_pts[0] =  r1.tl();
        rects_pts[1] =  r1.br();
        rects_pts[2] =  r2.tl();
        rects_pts[3] =  r2.br();

        MatOfPoint mof = new MatOfPoint();
        mof.fromArray(rects_pts);

        Rect union = Imgproc.boundingRect(mof);
        return union;
    }
    
    public List<Rect> getObjList() {
        return objList;
    }
    
    public Rect getMainObject() {
        return mainRect;
    }
}
