/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.linedetection;

/**
 *
 * @author Nomi
 */
public class Line {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double length;
    
    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.length = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    
    public double getX1() {
        return x1;
    }
    
    public double getY1() {
        return y1;
    }
    
    public double getX2() {
        return x2;
    }
    
    public double getY2() {
        return y2;
    }
    
    public double getLength() {
        return length;
    }
    
    public double getAngle(Line ln2) {
        double angle1 = Math.atan2(getY1() - getY2(), getX1() - getX2());
        double angle2 = Math.atan2(ln2.getY1() - ln2.getY2(), ln2.getX1() - ln2.getX2());
        return angle1 - angle2;
    }
    
    
    
    public String toString() { 
        return "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + " length:" + length;
    }
}
