package com.clarifai.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.http.client.ClientProtocolException;

import com.clarifai.ClarifaiClient;
import com.clarifai.ClarifaiTagging;
import com.clarifai.TokenExpiredException;

/**
 * Tests for the Clarifai API Client
 * 
 * @author Nico Hezel
 *
 */
public class ClarifaiTest {

	public static void main(String[] args) throws ClientProtocolException, TokenExpiredException, IOException {
		
		String clientId = "";
		String clientSecret = "";
		
		// connect to Clarifai server
		ClarifaiClient clarifai = new ClarifaiClient(clientId, clientSecret);
		
		
		
		System.out.println("Tag image via image URL");
		String imageURL = "http://www.clarifai.com/img/metro-north.jpg";
		
		// tag an image
		ClarifaiTagging imageUrlTags = clarifai.tag(imageURL);
		
		// print the image tags
		for (String tag : imageUrlTags.getTags())
			System.out.println(tag);
		System.out.println();
		
		
		
		System.out.println("Tag image via image file");
		Path imageFile = Paths.get("metro-north.jpg");
		
		// tag an image
		ClarifaiTagging imageFileTags = clarifai.tag(imageFile);
		
		// print the image tags
		for (String tag : imageFileTags.getTags())
			System.out.println(tag);
		System.out.println();
		
		
		
		System.out.println("Tag image via BufferedImage");
		BufferedImage image = ImageIO.read(imageFile.toFile());
		
		// tag an image
		ClarifaiTagging imageTags = clarifai.tag(image);
		
		// print the image tags
		for (String tag : imageTags.getTags())
			System.out.println(tag);
	}
}
