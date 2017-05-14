package com.nomi.mycompany.webserver;

import com.nomi.mycompany.analyzer.Analyzer;
import com.nomi.mycompany.cropGA.CropAlgorithm;
import com.nomi.mycompany.cropGA.Individual;
import com.nomi.mycompany.cropGA.Population;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.nomi.mycompany.model.CropInfo;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

@Path("/file")
public class UploadFileService {
    
        static {
            ToolClassToLoadLibrary.loadNativeLibrary();
        }
    
        @GET
	@Path("/{param}")
	public Response printMessage(@PathParam("param") String msg) {

//		String result = "Restful example myy: " + msg;
//
//		return Response.status(200).entity(result).build();

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
        double originalScore = analyzer.calcCombinedAestheticScore();
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
        
        
        CropInfo bestCrop = new CropInfo();
        bestCrop.setOriginalScore(originalScore);
        bestCrop.setX(x);
        bestCrop.setY(y);
        bestCrop.setWidth(w);
        bestCrop.setHeight(h);
        bestCrop.setBestScore(fittest.getAestheticScore());
        
        String result = "Record entered: "+ bestCrop;

        return Response.status(201).entity(result).build();


	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String uploadedFileLocation = "d://uploaded/"
				+ fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		Mat img;
                img = Imgcodecs.imread(uploadedFileLocation);
                int width = img.width();
                int height = img.height();
                System.out.println("w= " + width + "  h= " + height);

                Analyzer analyzer = new Analyzer(uploadedFileLocation);
                analyzer.setFrame(0, 0, width, height);

                Individual ind = new Individual(analyzer);
                ind.generateIndividual(width, height);
                double originalScore = analyzer.calcCombinedAestheticScore();
                System.out.println("\n\nTHE RESULT: " + analyzer.calcCombinedAestheticScore());

                Population myPop = new Population(200, true, width, height, analyzer);

                long startTime = System.currentTimeMillis();

                int generationCount = 0;
                //500 volt eredetileeg a hatar
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

                Imgcodecs.imwrite("D:\\1Downloads\\Firefox downloads\\cropped\\" +  fileDetail.getFileName(),cropped);

                long estimatedTime = System.currentTimeMillis() - startTime;
                System.out.println("IDOOO==== " + estimatedTime );


                CropInfo bestCrop = new CropInfo();
                bestCrop.setOriginalScore(originalScore);
                bestCrop.setX(x);
                bestCrop.setY(y);
                bestCrop.setWidth(w);
                bestCrop.setHeight(h);
                bestCrop.setBestScore(fittest.getAestheticScore());

                String result = "Scores: "+ bestCrop;

                return Response.status(201).entity(result).build();

	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}