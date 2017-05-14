/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomi.mycompany.model;

/**
 *
 * @author Nomi
 */
public class CropInfo {
    private double originalScore;
    private int x;
    private int y;
    private int width;
    private int height;
    private double bestScore;
    
//    public CropInfo(double originalScore, int x, int y, int width, int height, double bestScore) {
//        this.originalScore = originalScore;
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//        this.bestScore = bestScore;
//    }
    
    public double getOriginalScore() {
        return originalScore;
    }
    
    public void setOriginalScore(double originalScore) {
        this.originalScore = originalScore;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.x = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public double getBestScore() {
        return bestScore;
    }
    
    public void setBestScore(double bestScore) {
        this.bestScore = bestScore;
    }
    
    @Override
	public String toString() {
		return "CropInfo [originalScore=" + originalScore + ", "
                        + "x=" + x + ", y=" + y + ", width=" + width + ", "
                        + "height=" + height + ", bestScore=" + bestScore + "]";
	}
}
