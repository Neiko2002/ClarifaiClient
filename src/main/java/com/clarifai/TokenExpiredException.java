package com.clarifai;

/**
 * This exception gets thrown if the token that was used for the request was expired.
 * 
 * @author Nico Hezel
 *
 */
public class TokenExpiredException extends Exception {

	private static final long serialVersionUID = 6740484388282843195L;
	
	protected ClarifaiToken clarifaiToken;
	
	public TokenExpiredException(ClarifaiToken clarifaiToken) {
		this.clarifaiToken = clarifaiToken;
	}

	@Override
	public String getMessage() {
		return "The Clarifai token "+clarifaiToken.getAccessToken()+" is expired";
	}
}
