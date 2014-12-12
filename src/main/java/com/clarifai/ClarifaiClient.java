package com.clarifai;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gives Access to the Clarifai API
 * 
 * @author Nico Hezel
 *
 */
public class ClarifaiClient {

	public static final String TOKEN_URL = "https://api.clarifai.com/v1/token/";
	public static final String TAGGING_URL = "https://api.clarifai.com/v1/tag/";
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	protected HttpClient client;
	
	protected ClarifaiToken token;
	protected String clientId;
	protected String clientSecret;
	
	/**
	 * Clarifai
	 * 
	 * @param clientId
	 * @param clientSecret
	 */
	public ClarifaiClient(String clientId, String clientSecret) {
		this.client = HttpClientBuilder.create().build();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	/**
	 * Returns a Clarifai API authentication token.
	 * Creates a new one if non is existing or the old is expired.
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected ClarifaiToken getToken() throws ClientProtocolException, IOException {
		if(token == null || token.isExpired())
			token = ClarifaiToken.request(client, clientId, clientSecret);
		return token;
	}
	
	/**
	 * Send a tagging-request to the Clarifai API.
	 * Specify a BufferedImage 
	 * 
	 * @param image
	 * @param maxSize
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public ClarifaiTagging tag(BufferedImage image, Path ... jsonFiles) throws TokenExpiredException, ClientProtocolException, IOException {
		return ClarifaiTagging.tag(client, getToken(), image, jsonFiles);	
	}
	
	
	/**
	 * Send a tagging-request to the Clarifai API.
	 * Specify a URL to an image.
	 * 
	 * @param url
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public ClarifaiTagging tag(String url, Path ... jsonFiles) throws TokenExpiredException, ClientProtocolException, IOException {
		return ClarifaiTagging.tag(client, getToken(), url, jsonFiles);	
	}
	
	/**
	 * Send a tagging-request to the Clarifai API.
	 * Specify an image file.
	 * 
	 * @param imageFile (only supported images formats)
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public ClarifaiTagging tag(Path imageFile, Path ... jsonFiles)  throws TokenExpiredException, ClientProtocolException, IOException {
		return ClarifaiTagging.tag(client, getToken(), imageFile, jsonFiles);	
	}
}
