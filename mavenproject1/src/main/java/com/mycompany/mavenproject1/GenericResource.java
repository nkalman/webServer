/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import com.mycompany.analyzer.Analyzer;
import com.mycompany.cropGA.CropAlgorithm;
import com.mycompany.cropGA.Individual;
import com.mycompany.cropGA.Population;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import toolclasstoloadlibrary.ToolClassToLoadLibrary;

/**
 * REST Web Service
 *
 * @author Nomi
 */
@Path("generic")
public class GenericResource {
    
    static {
        ToolClassToLoadLibrary.loadNativeLibrary();
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }
    

    /**
     * Retrieves representation of an instance of com.mycompany.mavenproject1.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getXml() {
        //TODO return proper representation object
        JsonBuilderFactory factory = Json.createBuilderFactory(null);

        String filename = "D:\\1Downloads\\Firefox downloads\\stock.jpg";
        Mat img;
        img = Imgcodecs.imread(filename);
        int width = img.width();
        int height = img.height();
        System.out.println("w= " + width + "  h= " + height);
        
        Analyzer analyzer = new Analyzer(filename);
        analyzer.setFrame(0, 0, width, height);
        
        Individual ind = new Individual(analyzer);
        ind.generateIndividual(width, height);
        System.out.println("\n\nTHE RESULT: " + analyzer.calcCombinedAestheticScore());
        
        Population myPop = new Population(200, true, width, height, analyzer);
        
        long startTime = System.currentTimeMillis();
        
        int generationCount = 0;
        while (generationCount < 500) {
            generationCount++;
            System.out.println("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getAestheticScore());
            //myPop = CropAlgorithm.evolvePopulation(myPop);
            CropAlgorithm cropAlg = new CropAlgorithm(analyzer);
            myPop = cropAlg.evolvePopulation(myPop, width, height);
            System.out.println("-------------------------------------------------ACTUAL BEST:  " + myPop.getFittest().getAestheticScore() + "    " + myPop.getFittest().toString());
        }
        System.out.println("Solution found!");
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        Individual fittest =  myPop.getFittest();
        System.out.println(fittest);
        double score = fittest.getAestheticScore();
        System.out.println(fittest.getAestheticScore());
        
        
        
        int x = (int)fittest.getX();
        int y = (int)fittest.getY();
        int w = (int)fittest.getWidth();
        int h = (int)fittest.getHeight();
        
        Rect roi = new Rect(x, y, w, h);
        Mat cropped = new Mat(img, roi);
        
        Imgcodecs.imwrite("D:\\1Downloads\\Firefox downloads\\cropped\\torocko.jpg",cropped);
        
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("IDOOO==== " + estimatedTime );
        
        JsonObject value = factory.createObjectBuilder()
            .add("value", score)
            .add("x", x)
            .add("y", y)
            .add("width", w)
            .add("height", h)
            .build();

        System.out.println("-------------------------------");
   
        return value;
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
