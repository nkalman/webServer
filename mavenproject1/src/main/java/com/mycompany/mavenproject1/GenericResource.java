/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import com.mycompany.analyzer.Analyzer;
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
import toolclasstoloadlibrary.ToolClassToLoadLibrary;

/**
 * REST Web Service
 *
 * @author Nomi
 */
@Path("generic")
public class GenericResource {

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
        JsonObject value = factory.createObjectBuilder()
            .add("firstName", "John")
            .add("lastName", "Smith")
            .build();
        

        String filename = "D:\\1Downloads\\Firefox downloads\\stock.jpg";

        Analyzer analyzer = new Analyzer(filename);
        System.out.println("\n\nTHE RESULT: " + analyzer.calcCombinedAestheticScore());

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
