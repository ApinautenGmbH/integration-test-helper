/* Copyright (c) 2011 - 2017 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten GmbH, Hainstrasse 4, 04109 Leipzig, Germany
 *
 * Oct 16, 2017
 * thum */
package com.apiomat.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Base class for interacting with ApiOmat programmatically.
 *
 * @author thum
 */
public class AomHttpClient
{
	private final HttpClient client;

	private final String yambasHost;
	private final String yambasBase;
	private final AOMSystem system;

	private String customerName;

	private String userName;
	private String password;
	private String accessToken;

	private String apiKey = "";
	private String appName;
	private String sdkVersion = "1.0";

	/**
	 * Creates a new AomHttpClient
	 *
	 * @param host "http://localhost:8080" for example
	 * @param system the {@link AOMSystem} to use for further requests
	 */
	public AomHttpClient( String host, AOMSystem system )
	{
		this.yambasHost = host == null ? System.getProperty( "yambasHost", "http://localhost:8080" ) : host;
		this.yambasBase = this.yambasHost + "/yambas/rest/";
		this.system = system;
		this.client = HttpClientBuilder.create( ).build( );
	}

	/* ================== Getters & Setters ===================================== */

	/**
	 * @return the underlying httpclient
	 */
	public HttpClient getHttpClient( )
	{
		return this.client;
	}

	/**
	 * @return the userName
	 */
	public String getUserName( )
	{
		return this.userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword( )
	{
		return this.password;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken( )
	{
		return this.accessToken;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey( )
	{
		return this.apiKey;
	}

	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey( String apiKey )
	{
		this.apiKey = apiKey;
	}

	/**
	 * @return the appName
	 */
	public String getAppName( )
	{
		return this.appName;
	}

	/**
	 * @param appName
	 *        the appName to set
	 */
	public void setAppName( String appName )
	{
		this.appName = appName;
	}

	/**
	 * @return the system
	 */
	public AOMSystem getSystem( )
	{
		return this.system;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName( )
	{
		return this.customerName;
	}

	/**
	 * @param customerName
	 *        the customerName to set
	 */
	public void setCustomerName( String customerName )
	{
		this.customerName = customerName;
	}

	/**
	 * @return the yambasHost
	 */
	public String getYambasHost( )
	{
		return this.yambasHost;
	}

	/**
	 * @return the yambasBase
	 */
	public String getYambasBase( )
	{
		return this.yambasBase;
	}

	/**
	 * @return the sdkVersion
	 */
	public String getSdkVersion( )
	{
		return this.sdkVersion;
	}

	/**
	 * @param sdkVersion the sdkVersion to set
	 */
	public void setSdkVersion( String sdkVersion )
	{
		this.sdkVersion = sdkVersion;
	}

	/* ================== Methods ===================================== */

	/**
	 * Set the credentials used for authentication
	 * Alternatively, use {@link #setCredentials(String)}
	 *
	 * @param name email of user or account
	 * @param password password of user or account
	 */
	public void setCredentials( String name, String password )
	{
		this.accessToken = null;
		this.userName = name;
		this.password = password;
	}

	/**
	 * Set the access token used for authentication
	 * Alternatively, use {@link #setCredentials(String, String)}
	 *
	 * @param accessToken access token of user or account
	 */
	public void setCredentials( String accessToken )
	{
		this.userName = null;
		this.password = null;
		this.accessToken = accessToken;
	}

	/**
	 * Fetches an OAuth2 token for the configured credentials from yambas
	 *
	 * @return Token map containing an OAuth2 access token and other info
	 */
	public String getOauth2Token( )
	{
		final HttpPost request = new HttpPost( this.yambasHost + "/yambas/oauth/token" );
		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "grant_type", "aom_user" ) );
		data.add( new BasicNameValuePair( "client_id", this.getAppName( ) ) );
		data.add( new BasicNameValuePair( "client_secret", this.getApiKey( ) ) );
		data.add( new BasicNameValuePair( "scope", "read write" ) );
		data.add( new BasicNameValuePair( "username", this.getUserName( ) ) );
		data.add( new BasicNameValuePair( "app", this.getAppName( ) ) );
		data.add( new BasicNameValuePair( "password", this.getPassword( ) ) );
		data.add( new BasicNameValuePair( "system", this.getSystem( ).toString( ) ) );

		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );
			request.releaseConnection( );
			return AomHelper.getStringFromStream( response.getEntity( ).getContent( ) );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Revokes an OAuth2 token. Requires this client to be configured with a valid access token.
	 */
	public void revokeOAuth2Token( )
	{
		HttpPost request = new HttpPost( this.yambasHost + "/yambas/oauth/users/revoke" );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-apikey", getApiKey( ) );
		try
		{
			this.client.execute( request );
			request.releaseConnection( );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Refreshes the OAuth2 token with a refresh token.
	 *
	 * @param refreshToken the refresh token
	 * @return Token map containing an OAuth2 access token and other info
	 */
	public String refreshOauth2Token( String refreshToken )
	{
		final HttpPost request = new HttpPost( this.yambasHost + "/yambas/oauth/token" );
		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "grant_type", "refresh_token" ) );
		data.add( new BasicNameValuePair( "client_id", this.getAppName( ) ) );
		data.add( new BasicNameValuePair( "client_secret", this.getApiKey( ) ) );
		data.add( new BasicNameValuePair( "refresh_token", refreshToken ) );
		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );
			final String ret = EntityUtils.toString( response.getEntity( ) );
			request.releaseConnection( );
			return ret;
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Creates the authentication header value
	 *
	 * @return the authentication header value
	 */
	public String getAuthenticationHeaderBasic( )
	{
		if ( getUserName( ) == null || getPassword( ) == null )
		{
			return null;
		}

		try
		{
			String credentials = getUserName( ) + ":" + getPassword( );
			String encoded = new String( Base64.encodeBase64( credentials.getBytes( "UTF-8" ) ), "UTF-8" );
			return "Basic " + encoded;
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Returns authentication header with OAuth2 token
	 *
	 * @return authentication header with OAuth2 token
	 */
	public String getAuthenticationHeaderBearer( )
	{
		if ( getAccessToken( ) == null )
		{
			return null;
		}

		String result = "Bearer " + getAccessToken( );
		return result;
	}

	/**
	 * Creates the customer and sets it automatically as customerName ({@link #getCustomerName()})
	 *
	 * @param customerName customers unique name
	 * @param email email of the customer
	 * @param password password of the customer
	 * @return request object to check status codes and return values
	 */
	public Response createCustomer( String customerName, String email, String password )
	{
		final HttpPost request = new HttpPost( this.yambasBase + "customers" );
		setAuthorizationHeader( request );

		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "name", customerName ) );
		data.add( new BasicNameValuePair( "email", email ) );
		data.add( new BasicNameValuePair( "password", password ) );

		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );

			this.customerName = customerName;
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Deletes the customer and sets customerName to null ({@link #getCustomerName()})
	 *
	 * @param customerName unique name of the customer
	 * @return request object to check status codes and return values
	 */
	public Response deleteCustomer( String customerName )
	{
		HttpDelete request = new HttpDelete( this.yambasBase + "customers/" + customerName );
		setAuthorizationHeader( request );
		try
		{
			final HttpResponse response = this.client.execute( request );

			this.customerName = null;
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Creates the app for the currently set customer (see {@link #setCustomerName(String)})
	 *
	 * @param appName the name of the app to create
	 * @return request object to check status codes and return values
	 */
	public Response createApp( String appName )
	{
		return createApp( this.customerName, appName );
	}

	/**
	 * Creates an app for a specific customer
	 *
	 * @param customerName the name of the customer
	 * @param appName the name of the app to create
	 * @return request object to check status codes and return values
	 */
	public Response createApp( String customerName, String appName )
	{
		final HttpPost request = new HttpPost( this.yambasBase + "customers/" + customerName + "/apps" );
		setAuthorizationHeader( request );

		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "name", appName ) );

		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );

			this.appName = appName;
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Creates a module for a specific customer
	 *
	 * @param customerName the name of the customer
	 * @param moduleName the name of the module to create
	 * @return request object to check status codes and return values
	 */
	public Response createModule( String customerName, String moduleName )
	{
		final HttpPost request = new HttpPost( this.yambasBase + "modules" );
		setAuthorizationHeader( request );

		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "moduleName", moduleName ) );
		data.add( new BasicNameValuePair( "customerName", customerName ) );

		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Adds the module to the app and customer which were set
	 *
	 * @param moduleName name of the module to add to the current app
	 * @return request object to check status codes and return values
	 */
	public Response addModuleToApp( String moduleName )
	{
		return addModuleToApp( this.customerName, this.appName, moduleName );
	}

	/**
	 * Adds the module to the app
	 *
	 * @param customerName
	 *        the name of the customer which owns the app
	 * @param appName
	 *        the name of the app
	 * @param moduleName
	 *        the name of the module to add
	 * @return request object to check status codes and return values
	 */
	public Response addModuleToApp( String customerName, String appName, String moduleName )
	{
		HttpPost request = new HttpPost(
			this.yambasBase + "customers/" + customerName + "/apps/" + appName + "/usedmodules" );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );

		final List<NameValuePair> data = new ArrayList<NameValuePair>( );
		data.add( new BasicNameValuePair( "moduleName", moduleName ) );

		try
		{
			request.setEntity( new UrlEncodedFormEntity( data ) );
			final HttpResponse response = this.client.execute( request );

			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Deletes a module
	 *
	 * @param moduleName
	 *        the name of the module to delete
	 * @param deleteCompletely
	 *        if set to false, the module is only deleted from the current system and wil still exist in database
	 * @return request object to check status codes and return values
	 */
	public Response deleteModule( String moduleName, boolean deleteCompletely )
	{
		HttpDelete request = new HttpDelete(
			this.yambasBase + "modules/" + moduleName + "?deleteCompletely=" + String.valueOf( deleteCompletely ) );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Adds an auth class in a module to the app
	 *
	 * @param customerName the name of the customer which owns the app
	 * @param appName the name of the app
	 * @param moduleName the name of the module containing the auth class
	 * @param className the name of the auth class
	 * @return request object to check status codes and return values
	 * @throws UnsupportedEncodingException exc
	 */
	public Response addAuthModuleToApp( String customerName, String appName, String moduleName,
		String className )
		throws UnsupportedEncodingException
	{
		final HttpPut request =
			new HttpPut( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );

		final String data =
			"{ \"authClassesMap\" : { \"" + this.system.toString( ) + "\": { \"1\":\"Basics$User\", \"2\": \"" +
				moduleName + "$" + className +
				"\"}}}";

		request.setEntity( new StringEntity( data, ContentType.APPLICATION_JSON ) );

		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Updates the module
	 *
	 * @param moduleName
	 *        the name of the module to add
	 * @param objectToUpdate
	 *        JSON containing the key/value pais to use for update
	 * @return request object to check status codes and return values
	 */
	public Response updateModule( String moduleName, JSONObject objectToUpdate )
	{
		HttpPut request = new HttpPut( this.yambasBase + "modules/" + moduleName );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.setEntity( new StringEntity( objectToUpdate.toString( ), ContentType.APPLICATION_JSON ) );

		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Sets the app to active state
	 *
	 * @return request object to check status codes and return values
	 */
	public Response deployApp( )
	{
		return deployApp( this.customerName, this.appName );
	}

	/**
	 * Sets the app to active state
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app
	 * @return request object to check status codes and return values
	 */
	public Response deployApp( String customerName, String appName )
	{
		HttpPut request = new HttpPut( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpEntity requestEntity = new StringEntity(
				"{\"applicationStatus\":{\"" + this.system + "\":\"ACTIVE\"}, \"applicationName\":\"" +
					appName + "\"}",
				ContentType.APPLICATION_JSON );
			request.setEntity( requestEntity );

			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Updates a modules config for the given app
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app
	 * @param moduleName
	 *        the name of the module to update config for
	 * @param key
	 *        config key
	 * @param value
	 *        value of the config
	 * @return request object to check status codes and return values
	 */
	public Response updateConfig( String customerName, String appName, String moduleName, String key,
		String value )
	{
		final HttpPut request = new HttpPut( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpEntity requestEntity = new StringEntity(
				"{\"configuration\":" + "	{\"" + this.system.toString( ).toLowerCase( ) + "Config\": {\"" +
					moduleName + "\":{\"" + key + "\":\"" + value + "\"}}}, \"applicationName\":\"" + appName + "\"}",
				ContentType.APPLICATION_JSON );
			request.setEntity( requestEntity );

			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Returns the ApIOmat version string
	 *
	 * @return version string
	 */
	public String getVersion( )
	{
		final HttpGet request = new HttpGet( this.yambasBase );
		try
		{
			request.addHeader( "Accept", "application/json" );
			final HttpResponse resp = this.client.execute( request );
			final JSONObject json = new JSONObject( EntityUtils.toString( resp.getEntity( ) ) );
			request.releaseConnection( );
			return json.getString( "version" );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Returns the app which is currently set
	 *
	 * @return request object to check status codes and return values
	 */
	public Response getApp( )
	{
		return getApp( this.customerName, this.appName, this.system );
	}

	/**
	 * Returns the specific app
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app
	 * @param system
	 *        the used system
	 * @return trequest object to check status codes and return values
	 */
	public Response getApp( String customerName, String appName, AOMSystem system )
	{
		final HttpGet request = new HttpGet( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		try
		{
			final HttpResponse resp = this.client.execute( request );
			final JSONObject json = new JSONObject( EntityUtils.toString( resp.getEntity( ) ) );
			final JSONObject keysObj = json.getJSONObject( "apiKeys" );

			this.apiKey = keysObj.getString( system.toString( ).toLowerCase( ) + "ApiKey" );
			return new Response( resp );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Deletes the app which is currently set
	 *
	 * @return request object to check status codes and return values
	 */
	public Response deleteApp( )
	{
		return deleteApp( this.customerName, this.appName );
	}

	/**
	 * Deletes the specified app
	 *
	 * @param customerName
	 *        the name of the customer which owns the app
	 * @param appName
	 *        the name of the app to delete
	 * @return request object to check status codes and return values
	 */
	public Response deleteApp( String customerName, String appName )
	{
		final HttpDelete request = new HttpDelete( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Drops all data contained in the currently set app
	 *
	 * @return request object to check status codes and return values
	 */
	public Response dropData( )
	{
		final HttpDelete request = new HttpDelete( this.yambasBase + "apps/" + this.appName + "/models" );
		setAuthorizationHeader( request );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Returns all MetaModels (classes) of a module
	 *
	 * @param moduleName name of the module
	 * @return request object to check status codes and return values
	 */
	public Response getMetaModels( final String moduleName )
	{
		final HttpGet request = new HttpGet( this.yambasBase + "modules/" + moduleName + "/metamodels" );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "x-apiomat-sdkVersion", this.sdkVersion );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Creates an object of the given dataModelName and moduleName
	 * The app name which is set in this client will be used
	 *
	 * @param moduleName
	 *        the name of the module
	 * @param dataModelName
	 *        the name of the class
	 * @return request object to check status codes and return values
	 */
	public Response createObject( String moduleName, String dataModelName )
	{
		return createObject( moduleName, dataModelName, new JSONObject( ) );
	}

	/**
	 * Creates an object of the given dataModelName and moduleName
	 * the appname which is set in this object will be used
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param otherFieldsObject
	 *        the other fields to set as JSONObject (the @type field will be added automatically)
	 * @return request object to check status codes and return values
	 */
	public Response createObject( String moduleName, String dataModelName, JSONObject otherFieldsObject )
	{
		final HttpPost request = new HttpPost(
			this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" + dataModelName );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "x-apiomat-sdkVersion", this.sdkVersion );
		try
		{
			otherFieldsObject.put( "@type", moduleName + '$' + dataModelName );
			final HttpEntity requestEntity =
				new StringEntity( otherFieldsObject.toString( ), ContentType.APPLICATION_JSON );
			request.setEntity( requestEntity );
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Get the specified object for a class
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodel
	 * @param dataModelId
	 *        the ID of the object to return
	 * @return request object to check status codes and return values
	 */
	public Response getObject( String moduleName, String dataModelName, String dataModelId )
	{
		final HttpGet request = new HttpGet( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
			dataModelName + "/" + dataModelId );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "x-apiomat-sdkVersion", this.sdkVersion );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Get a list of objects for the given query
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param query
	 *        ApiOmat query string, may be null to append no query
	 * @return request object to check status codes and return values
	 */
	public Response getObjects( String moduleName, String dataModelName, String query )
	{
		return getObjects( moduleName, dataModelName, query, null );
	}

	/**
	 * Get a list of objects for the given query
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param query
	 *        ApiOmat query string, may be null to append no query
	 * @param additionalRequestHeaders
	 *        additional headers to be set
	 * @return request object to check status codes and return values
	 */
	public Response getObjects( String moduleName, String dataModelName, String query,
		Map<String, String> additionalRequestHeaders )
	{
		final HttpGet request = new HttpGet(
			this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" + dataModelName +
				( query == null ? "" : "?q=" + query ) );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "x-apiomat-sdkVersion", this.sdkVersion );
		if ( additionalRequestHeaders != null )
		{
			additionalRequestHeaders.forEach( ( name, value ) -> request.addHeader( name, value ) );
		}
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Updates an object of the given dataModelName and moduleName
	 * the appname which is set in this object will be used
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param dataModelId
	 *        the id of the datamodel
	 * @param fullUpdate
	 *        indicates whether the fullupdate flag should be set to true or false
	 * @param objectToUpdate
	 *        JSON containing the key/value pais to use for update
	 * @return request object to check status codes and return values
	 */
	public Response updateObject( String moduleName, String dataModelName, String dataModelId,
		boolean fullUpdate,
		JSONObject objectToUpdate )
	{
		final HttpPut request =
			new HttpPut( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + '/' +
				dataModelName + '/' + dataModelId );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "x-apiomat-sdkVersion", this.sdkVersion );
		request.addHeader( "X-apiomat-fullupdate", String.valueOf( fullUpdate ) );
		objectToUpdate.put( "@type", moduleName + "$" + dataModelName );

		try
		{
			final HttpEntity requestEntity =
				new StringEntity( objectToUpdate.toString( ), ContentType.APPLICATION_JSON );
			request.setEntity( requestEntity );

			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Adds a reference to an object
	 *
	 * @param moduleName
	 *        the name of the module
	 * @param dataModelName
	 *        the name of the datamodel
	 * @param dataModelId
	 *        the id of the datamodel
	 * @param attributeName
	 *        the name of the (reference) attribute
	 * @param refId
	 *        the reference id
	 * @param isTransientRef
	 *        indicates whether the referenced class is transient or not (needed to specify whether to set foreignId
	 *        or id
	 * @param refClassModule
	 *        the module name of the referenced class
	 * @param refClassName
	 *        the name of the referenced class
	 * @return request object to check status codes and return values
	 */
	public Response addReference( String moduleName, String dataModelName, String dataModelId,
		String attributeName,
		String refId, boolean isTransientRef, String refClassModule, String refClassName )
	{
		final HttpPost request =
			new HttpPost( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
				dataModelName + "/" + dataModelId + "/" + attributeName );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final String data = "{ \"@type\":\"" + refClassModule + "$" + refClassName + "\",\"" +
				( isTransientRef ? "foreignId" : "id" ) + "\":\"" + refId + "\"}";
			final HttpEntity requestEntity = new StringEntity( data, ContentType.APPLICATION_JSON );
			request.setEntity( requestEntity );

			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Get the references for a specified object
	 *
	 * @param moduleName
	 *        the name of the module
	 * @param dataModelName
	 *        the name of the datamodel
	 * @param dataModelId
	 *        the id of the datamodel
	 * @param refAttributeName
	 *        the name of the (reference) attribute
	 * @return request object to check status codes and return values
	 */
	public Response getReference( String moduleName, String dataModelName, String dataModelId,
		String refAttributeName )
	{
		final HttpGet request = new HttpGet( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
			dataModelName + "/" + dataModelId + "/" + refAttributeName );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Delete the references for a specified object
	 *
	 * @param moduleName
	 *        the name of the module
	 * @param dataModelName
	 *        the name of the datamodel
	 * @param dataModelId
	 *        the datamodel-id
	 * @param refAttributeName
	 *        the attribute-name of the reference
	 * @param refId
	 *        the reference id
	 * @return request object to check status codes and return values
	 */
	public Response deleteReference( String moduleName, String dataModelName, String dataModelId,
		String refAttributeName, String refId )
	{
		final HttpDelete request = new HttpDelete( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName +
			"/" + dataModelName + "/" + dataModelId + "/" + refAttributeName + "/" + refId );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Deletes an object
	 *
	 * @param moduleName
	 *        the name of the module
	 * @param dataModelName
	 *        the name of the datamodel
	 * @param dataModelId
	 *        the datamodel-id
	 * @return request object to check status codes and return values
	 */
	public Response deleteObject( String moduleName, String dataModelName, String dataModelId )
	{
		final HttpDelete request = new HttpDelete( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName +
			"/" + dataModelName + "/" + dataModelId );
		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Posts static data, either as image or as file
	 *
	 * @param content the content
	 * @param isImage indicates whether this is an image or a file
	 * @return request object to check status codes and return values
	 */
	public Response postStaticData( final byte[ ] content, final boolean isImage )
	{
		final HttpPost request =
			new HttpPost( this.yambasBase + "apps/" + this.appName + "/data/" + ( isImage ? "images/" : "files/" ) );
		request.setEntity( EntityBuilder.create( ).setBinary( content ).build( ) );
		request.addHeader( "Content-Type", "application/octet-stream" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Deletes static data, either image or file
	 *
	 * @param id the file id
	 * @param isImage indicates whether this is an image or a file
	 * @return request object to check status codes and return values
	 */
	public Response deleteStaticData( String id, final boolean isImage )
	{
		final HttpDelete request =
			new HttpDelete(
				this.yambasBase + "apps/" + this.appName + "/data/" + ( isImage ? "images/" : "files/" ) + id );
		request.addHeader( "Content-Type", "application/octet-stream" );
		request.addHeader( "x-apiomat-apikey", this.apiKey );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Sends a request to yambas base URL + path. yambas base URL is: yambasHost + "/yambas/rest/"
	 *
	 * @param path
	 *        the path
	 * @param contentTypes content types ("application/json" used if not provided)
	 * @return request object to check status codes and return values
	 */
	public Response getRequestRestEndpoint( String path, String... contentTypes )
	{
		final HttpGet request = new HttpGet( this.yambasBase + path );
		setAuthorizationHeader( request );
		if ( contentTypes.length == 0 )
		{
			request.addHeader( "ContentType", "application/json" );
		}
		else
		{
			request.addHeader( "ContentType", String.join( ",", contentTypes ) );
		}
		request.addHeader( "x-apiomat-apikey", getApiKey( ) );
		request.addHeader( "x-apiomat-system", getSystem( ).toString( ) );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Sends a request to yambas base URL + path. yambas base URL is: yambasHost + "/yambas/rest/"
	 *
	 * @param path
	 *        the path
	 * @param payLoad
	 *        the payload as input stream
	 * @return request object to check status codes and return values
	 */
	public Response postRequestRestEndpoint( String path, InputStream payLoad )
	{
		final HttpPost request = new HttpPost( this.yambasBase + path );

		setAuthorizationHeader( request );
		request.addHeader( "ContentType", "application/json" );
		request.addHeader( "x-apiomat-apikey", getApiKey( ) );
		request.addHeader( "x-apiomat-system", getSystem( ).toString( ) );

		try
		{
			request.setEntity( EntityBuilder.create( ).setStream( payLoad ).build( ) );
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Sends a binary request to yambas base URL + path. yambas base URL is: yambasHost + "/yambas/rest/"
	 *
	 * @param path
	 *        the path
	 * @param fieldName
	 *        name of the binary data field
	 * @param entityPayload
	 *        the binary data as input stream
	 * @return request object to check status codes and return values
	 */
	public Response postRequestRestEndpoint( String path, final String fieldName, InputStream entityPayload )
	{
		Response response = null;
		final HttpPost request = new HttpPost( this.yambasBase + path );

		setAuthorizationHeader( request );
		request.addHeader( "ContentType", ContentType.APPLICATION_OCTET_STREAM.getMimeType( ) );
		request.addHeader( "x-apiomat-apikey", getApiKey( ) );
		request.addHeader( "x-apiomat-system", getSystem( ).toString( ) );

		try
		{
			EntityBuilder builder = EntityBuilder.create( );
			builder.setContentType( ContentType.APPLICATION_OCTET_STREAM );
			builder.setStream( entityPayload );

			final HttpEntity entity = builder.build( );
			request.setEntity( entity );
			final HttpResponse responseIntern = this.client.execute( request );
			response = new Response( responseIntern );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return response;
	}

	/**
	 * Dumps an apps data to csv-format
	 *
	 * @param appName
	 *        the AppName
	 * @return request object to check status codes and return values
	 */
	public Response exportAppDataToCSV( final String appName )
	{
		return getRequestRestEndpoint( "modules/csv/spec/" + appName );
	}

	/**
	 * Imports a CSV dump into an existing app
	 *
	 * @param appName
	 *        the Appname
	 * @param data
	 *        payload of CSV-zip
	 *
	 * @return request object to check status codes and return values
	 */
	public Response importCSVToApp( String appName, InputStream data )
	{
		final HttpPost request = new HttpPost( this.yambasBase + "modules/csv/spec/" + appName );
		setAuthorizationHeader( request );
		request.addHeader( "x-apiomat-system", this.system.toString( ) );
		request.addHeader( "Content-Type", "application/octet-stream" );
		request.addHeader( "X-apiomat-apikey", this.apiKey );

		HttpEntity entity = EntityBuilder.create( ).setStream( data ).build( );
		request.setEntity( entity );
		try
		{
			final HttpResponse response = this.client.execute( request );
			return new Response( response );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Downloads a module and unzips it to path
	 *
	 * @param moduleName
	 *        name of the Module
	 * @param targetPath
	 *        extract path
	 * @return request object to check status codes and return values
	 * @throws IOException exc
	 */
	public Response downloadNM( final String moduleName, final String targetPath ) throws IOException
	{
		final Response response = getRequestRestEndpoint( "modules/" + moduleName + "/sdk" );
		AomHelper.unzip( response.getEntityContent( ), targetPath );
		return response;
	}

	private void setAuthorizationHeader( HttpRequest requestMethod )
	{
		/* First try basic auth */
		String authHeader = getAuthenticationHeaderBasic( );
		/* If null, try Oauth */
		if ( authHeader == null )
		{
			authHeader = getAuthenticationHeaderBearer( );
		}
		/* Set header if not null, otherwise don't set header */
		if ( authHeader != null )
		{
			requestMethod.addHeader( "Authorization", authHeader );
		}
	}

	/**
	 * Upload the given module jar
	 *
	 * @param moduleName
	 *        the name of the module to add
	 * @param update
	 *        "true" to update classes and module, "overwrite" to update and overwrite changes done in dashboard
	 *        in the meantime
	 * @param jarStream
	 *        Stream of packaged module jar
	 * @return request object to check status codes and return values
	 */
	public Response uploadModule( String moduleName, final String update, final InputStream jarStream )
	{
		return postRequestRestEndpoint( "modules/" + moduleName + "/sdk?update=" + update, jarStream );
	}

	/**
	 * Downloads a SDK in given language and return the zipped file in response
	 *
	 * @param language
	 *        language of SDK that should be downloaded
	 * @return request object to check status codes and return values
	 * @throws IOException exc
	 */
	public Response downloadSDK( final SDKLanguage language ) throws IOException
	{
		return downloadSDK( language, null );
	}

	/**
	 * Downloads a SDK in given language and extract it to given target path
	 *
	 * @param language
	 *        language of SDK that should be downloaded
	 * @param targetPath
	 *        extract path
	 * @return request object to check status codes and return values
	 * @throws IOException exc
	 */
	public Response downloadSDK( final SDKLanguage language, final String targetPath ) throws IOException
	{
		String path =
			String.format( "customers/%s/apps/%s/sdk/%s", getCustomerName( ), getAppName( ),
				language.toString( ).toLowerCase( ) );
		final Response response = getRequestRestEndpoint( path );
		if ( targetPath != null && targetPath.isEmpty( ) == false )
		{
			AomHelper.unzip( response.getEntityContent( ), targetPath );
		}
		return response;
	}
}
