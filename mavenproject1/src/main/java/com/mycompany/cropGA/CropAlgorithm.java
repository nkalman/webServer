/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cropGA;

import com.mycompany.analyzer.Analyzer;
import java.util.Random;

/**
 *
 * @author Nomi
 */
public class CropAlgorithm {
    /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.3;
    private static final int tournamentSize = 20;
    private static final boolean elitism = true;
    
    private int originalWidth;
    private int originalHeight;
    
    private Analyzer analyzer;

    /* Public methods */
    
    public CropAlgorithm(Analyzer an) {
        analyzer = an;
    }
    
    // Evolve a population
    public Population evolvePopulation(Population pop, int width, int height) {
        originalWidth = width;
        originalHeight = height;
        Population newPopulation = new Population(pop.size(), false, width, height, analyzer);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        for (int i = elitismOffset; i < pop.size(); i++) {
            Individual newIndiv = new Individual(analyzer);
            newIndiv.setX(1000);
            newIndiv.setY(1000);
            newIndiv.setWidth(originalWidth);
            newIndiv.setWidth(originalHeight);
            while (newIndiv.getX() + newIndiv.getWidth() > originalWidth ||
                    newIndiv.getY() + newIndiv.getHeight() > originalHeight) {
                Individual indiv1 = tournamentSelection(pop);
                Individual indiv2 = tournamentSelection(pop);
                newIndiv = crossover(indiv1, indiv2);
            }
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover individuals
    private Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual(analyzer);
        // Set geneX
        if (Math.random() <= uniformRate) {
            newSol.setX(indiv1.getX());
        } 
        else {
            newSol.setX(indiv2.getX());
        }
        //Set geneY
        if (Math.random() <= uniformRate) {
            newSol.setY(indiv1.getY());
        } 
        else {
            newSol.setY(indiv2.getY());
        }
        //Set geneWidth
        if (Math.random() <= uniformRate) {
            newSol.setWidth(indiv1.getWidth());
            newSol.setHeight(indiv1.getHeight());
        } 
        else {
            newSol.setWidth(indiv2.getWidth());
            newSol.setHeight(indiv2.getHeight());
        }
//        if (newSol.getX() + newSol.getWidth() > originalWidth) {
//            newSol.setX(originalWidth - newSol.getWidth());
//        }
        
//        //Set geneHeight
//        if (Math.random() <= uniformRate) {
//            newSol.setHeight(indiv1.getHeight());
//        } 
//        else {
//            newSol.setHeight(indiv2.getHeight());
//        }
//        if (newSol.getY() + newSol.getHeight() > originalHeight) {
//            newSol.setY(originalHeight - newSol.getHeight());
//        }
        
        return newSol;
    }

    // Mutate an individual
    private void mutate(Individual indiv) {
       do {
        
             // Create a random geneX
            Random rand;
            if (Math.random() <= mutationRate) {
                rand = new Random();
                double geneX = rand.nextInt(originalWidth/2 + 1);
                indiv.setX(geneX);
            }
            // Create a random geneY
            if (Math.random() <= mutationRate) {
                rand = new Random();
                double geneY = rand.nextInt(originalHeight/2 + 1);
                indiv.setY(geneY);
            }
            // Create a random geneWidth
            if (Math.random() <= mutationRate) {
                rand = new Random();
                double geneWidth = rand.nextInt(originalWidth - originalWidth/2 + 1)
                        + originalWidth/2;
                indiv.setWidth(geneWidth);
                double frameRatio =  (double)originalHeight / (double)originalWidth;
                double geneHeight = frameRatio * geneWidth;
                indiv.setHeight((int)geneHeight);
            }
        } while (indiv.getX() + indiv.getWidth() <= originalWidth ||
                    indiv.getY() + indiv.getHeight() <= originalHeight);
    }

    // Select individuals for crossover
    private Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, originalWidth, originalHeight, analyzer);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        Individual fittest = tournament.getFittest();
        return fittest;
    }
}
