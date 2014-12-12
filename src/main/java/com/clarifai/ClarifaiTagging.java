package com.clarifai;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The result of a tagged image.
 * 
 * @author Nico Hezel
 *
 */
public class ClarifaiTagging {


	@SerializedName("status_code")
	@Expose
	private String statusCode;
	
	@SerializedName("status_msg")
	@Expose
	private String statusMsg;
	
	@Expose
	private Meta meta;
	
	@Expose
	private List<Result> results = new ArrayList<Result>();
	
	public List<String> getTags() {
		return results.get(0).result.tag.classes;
	}
	
	public void print() {
		System.out.println("status_code: "+statusCode);
		System.out.println("status_msg: "+statusMsg);
		if(meta != null) {
			System.out.println("meta: ");
			meta.print();
		}
		System.out.println("results: ");
		for (Result result : results)
			result.print();
	}
	
	private static class Meta {
		@Expose
		private Tag tag;

		public void print() {
			System.out.println(" Tag: ");
			tag.print();
		}
	}
	
	private static class Result {
		@SerializedName("status_code")
		@Expose
		private String statusCode;
		
		@SerializedName("status_msg")
		@Expose
		private String statusMsg;
		
		@SerializedName("local_id")
		@Expose
		private String localId;
		
		@Expose
		private Double docid;
		
		@Expose
		private Result_ result;

		
		
		public void print() {
			System.out.println(" status_code: "+statusCode);
			System.out.println(" status_msg: "+statusMsg);
			System.out.println(" local_id: "+localId);
			System.out.println(" docid: "+docid);
			System.out.println(" result: ");
			result.print();			
		}
	}
	
		 
	private static class Result_ {
		@Expose
		private Tag_ tag;
		
		@Expose
		private String error;

		public void print() {
			if(tag != null) {
				System.out.println("  tag: ");
				tag.print();
			} 
			
			if(error != null)
				System.out.println("  error: "+error);
		}
	}
	
	private static class Tag {
		@Expose
		private Double timestamp;
		
		@Expose
		private String model;
		
		@Expose
		private String config;
		
		@SerializedName("status_code")
		@Expose
		private String statusCode;
			 
		public void print() {
			System.out.println(" timestamp: "+timestamp);
			System.out.println(" model: "+model);
			System.out.println(" config: "+config);
			System.out.println(" status_code: "+statusCode);
		}
	}

	private static class Tag_ {
		@Expose
		private List<String> classes = new ArrayList<String>();
		
		@Expose
		private List<Double> probs = new ArrayList<Double>();

		public void print() {
			System.out.println("    class <-> prob");
			for (int i = 0; i < classes.size(); i++) {
				System.out.println("    "+(i+1)+". "+classes.get(i)+" <-> "+probs.get(i));
			}
		}
	}
	
	/**
	 * Parse the json response from the REST API.
	 * 
	 * @param json
	 * @return
	 */
	public static ClarifaiTagging parse(String json) {
		
		ClarifaiTagging tag = ClarifaiClient.gson.fromJson(json, ClarifaiTagging.class);
		return tag;
	}
	
	/**
	 * Send a tagging-request to the clarifai api.
	 * Specify a BufferedImage 
	 * 
	 * @param client
	 * @param token
	 * @param file
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static ClarifaiTagging tag(HttpClient client, ClarifaiToken token, BufferedImage image, Path ... jsonFiles)  throws TokenExpiredException, ClientProtocolException, IOException {
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		// write the thumbnail in the output stream
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", output);
		output.flush();
		byte[] imageInByte = output.toByteArray();
		output.close();
		
		// write the bytes of the output stream into the request body
		builder.addBinaryBody("encoded_data", imageInByte, ContentType.APPLICATION_OCTET_STREAM, "image");
		return tag(client, token, builder.build(), jsonFiles);	
	}
	

	
	/**
	 * Send a tagging-request to the clarifai api.
	 * Specify a URL to an image.
	 * 
	 * @param client
	 * @param token
	 * @param url
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static ClarifaiTagging tag(HttpClient client, ClarifaiToken token, String url, Path ... jsonFiles)  throws TokenExpiredException, ClientProtocolException, IOException {

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("url", url));
		return tag(client, token, new UrlEncodedFormEntity(urlParameters), jsonFiles);	
	}
	
	/**
	 * Send a tagging-request to the clarifai api.
	 * Specify an image file.
	 * 
	 * @param client
	 * @param token
	 * @param imageFile (only supported images formats)
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static ClarifaiTagging tag(HttpClient client, ClarifaiToken token, Path imageFile, Path ... jsonFiles)  throws TokenExpiredException, ClientProtocolException, IOException {

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("encoded_data", imageFile.toFile());
		return tag(client, token, builder.build(), jsonFiles);		
	}
	
	/**
	 * Send a tagging-request to the clarifai api 
	 * 
	 * @param client
	 * @param token
	 * @param entry
	 * @param jsonFiles (optional)
	 * @return
	 * @throws TokenExpiredException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	protected static ClarifaiTagging tag(HttpClient client, ClarifaiToken token, HttpEntity entry, Path ... jsonFiles) throws TokenExpiredException, ClientProtocolException, IOException  {
		
		HttpPost request = new HttpPost(ClarifaiClient.TAGGING_URL);
		 
		// add request header
		request.addHeader(token.getHeader());
		
		// add POST data	
		request.setEntity(entry);
		
		// send request and wait for the response
		HttpResponse response = client.execute(request);
		
		// get the body of the response
		String json = EntityUtils.toString(response.getEntity());

		// save the json response to a file
		if(jsonFiles.length > 0) {
			String prettyJson = toPrettyFormat(json);
			for (Path jsonFile : jsonFiles) 
				Files.write(jsonFile, prettyJson.getBytes());
		}
	
		// create a java object from the response 	
		return ClarifaiTagging.parse(json);
	}
	
	/**
     * Convert a JSON string to a pretty printed version
     * 
     * @param jsonString
     * @return
     */
    public static String toPrettyFormat(String jsonString) 
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();
        return ClarifaiClient.gson.toJson(json);
    }
}
