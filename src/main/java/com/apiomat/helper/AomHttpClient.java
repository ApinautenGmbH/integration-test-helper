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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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

	private String apiKey;
	private String appName;

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
		this.client = new HttpClient( );
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
		PostMethod request = new PostMethod( this.yambasHost + "/yambas/oauth/token" );
		NameValuePair[ ] data = { new NameValuePair( "grant_type", "aom_user" ),
			new NameValuePair( "client_id", this.getAppName( ) ),
			new NameValuePair( "client_secret", this.getApiKey( ) ),
			new NameValuePair( "scope", "read write" ), new NameValuePair( "username", this.getUserName( ) ),
			new NameValuePair( "app", this.getAppName( ) ), new NameValuePair( "password", this.getPassword( ) ),
			new NameValuePair( "system", this.getSystem( ).toString( ) ) };
		request.setRequestBody( data );
		try
		{
			this.client.executeMethod( request );
			return request.getResponseBodyAsString( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Revokes an OAuth2 token. Requires this client to be configured with a valid access token.
	 *
	 * @return request object to check status codes and return values
	 */
	public HttpMethod revokeOAuth2Token( )
	{
		PostMethod request = new PostMethod( this.yambasHost + "/yambas/oauth/users/revoke" );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-apikey", getApiKey( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Refreshes the OAuth2 token with a refresh token.
	 *
	 * @param refreshToken the refresh token
	 * @return Token map containing an OAuth2 access token and other info
	 */
	public String refreshOauth2Token( String refreshToken )
	{
		PostMethod request = new PostMethod( this.yambasHost + "/yambas/oauth/token" );
		NameValuePair[ ] data = { new NameValuePair( "grant_type", "refresh_token" ),
			new NameValuePair( "client_id", this.getAppName( ) ),
			new NameValuePair( "client_secret", this.getApiKey( ) ),
			new NameValuePair( "refresh_token", refreshToken ) };
		request.setRequestBody( data );
		try
		{
			this.client.executeMethod( request );
			return request.getResponseBodyAsString( );
		}
		catch ( IOException e )
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
			System.out.println( e );
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
	public HttpMethod createCustomer( String customerName, String email, String password )
	{
		PostMethod request = new PostMethod( this.yambasBase + "customers" );
		setAuthorizationHeader( request );
		NameValuePair[ ] data = { new NameValuePair( "name", customerName ), new NameValuePair( "email", email ),
			new NameValuePair( "password", password ) };
		request.setRequestBody( data );
		try
		{
			this.client.executeMethod( request );
			this.customerName = customerName;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Deletes the customer and sets customerName to null ({@link #getCustomerName()})
	 *
	 * @param customerName unique name of the customer
	 * @return request object to check status codes and return values
	 */
	public HttpMethod deleteCustomer( String customerName )
	{
		DeleteMethod request = new DeleteMethod( this.yambasBase + "customers/" + customerName );
		setAuthorizationHeader( request );
		try
		{
			this.client.executeMethod( request );
			this.customerName = null;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Creates the app for the currently set customer (see {@link #setCustomerName(String)})
	 *
	 * @param appName the name of the app to create
	 * @return request object to check status codes and return values
	 */
	public HttpMethod createApp( String appName )
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
	public HttpMethod createApp( String customerName, String appName )
	{
		PostMethod request = new PostMethod( this.yambasBase + "customers/" + customerName + "/apps" );
		setAuthorizationHeader( request );
		NameValuePair[ ] data = { new NameValuePair( "name", appName ), };
		request.setRequestBody( data );
		try
		{
			this.client.executeMethod( request );
			this.appName = appName;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Adds the module to the app and customer which were set
	 *
	 * @param moduleName name of the module to add to the current app
	 * @return request object to check status codes and return values
	 */
	public HttpMethod addModuleToApp( String moduleName )
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
	public HttpMethod addModuleToApp( String customerName, String appName, String moduleName )
	{
		PostMethod request = new PostMethod(
			this.yambasBase + "customers/" + customerName + "/apps/" + appName + "/usedmodules" );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		NameValuePair[ ] data = { new NameValuePair( "moduleName", moduleName ), };
		request.setRequestBody( data );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod deleteModule( String moduleName, boolean deleteCompletely )
	{
		DeleteMethod request = new DeleteMethod(
			this.yambasBase + "modules/" + moduleName + "?deleteCompletely=" + String.valueOf( deleteCompletely ) );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod addAuthModuleToApp( String customerName, String appName, String moduleName, String className )
		throws UnsupportedEncodingException
	{
		final PutMethod request =
			new PutMethod( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );

		final String data =
			"{ \"authClassesMap\" : { \"" + this.system.toString( ) + "\": { \"1\":\"Basics$User\", \"2\": \"" +
				moduleName + "$" + className +
				"\"}}}";
		final StringRequestEntity requestEntity = new StringRequestEntity( data, "application/json", "UTF-8" );
		request.setRequestEntity( requestEntity );

		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Updates the module
	 *
	 * @param moduleName
	 *        the name of the module to add
	 * @param nvps
	 *        Name/Value pairs to be sent in the update JSON
	 * @return request object to check status codes and return values
	 */
	public HttpMethod updateModule( String moduleName, NameValuePair... nvps )
	{
		PutMethod request = new PutMethod( this.yambasBase + "modules/" + moduleName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			StringBuilder sb = new StringBuilder( );
			sb.append( "{" );
			for ( NameValuePair nvp : nvps )
			{
				sb.append( "\"" );
				sb.append( nvp.getName( ) );
				sb.append( "\":\"" );
				sb.append( nvp.getValue( ) );
				sb.append( "\"," );
			}
			sb.delete( sb.length( ) - 1, sb.length( ) );
			sb.append( "}" );

			StringRequestEntity requestEntity = new StringRequestEntity( sb.toString( ), "application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Sets the app to active state
	 *
	 * @return request object to check status codes and return values
	 */
	public HttpMethod deployApp( )
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
	public HttpMethod deployApp( String customerName, String appName )
	{
		PutMethod request = new PutMethod( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			StringRequestEntity requestEntity = new StringRequestEntity(
				"{\"applicationStatus\":{\"" + this.system + "\":\"ACTIVE\"}, \"applicationName\":\"" + appName + "\"}",
				"application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod updateConfig( String customerName, String appName, String moduleName, String key, String value )
	{
		PutMethod request = new PutMethod( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			StringRequestEntity requestEntity = new StringRequestEntity(
				"{\"configuration\":" + "	{\"" + this.system.toString( ).toLowerCase( ) + "Config\": {\"" +
					moduleName + "\":{\"" + key + "\":\"" + value + "\"}}}, \"applicationName\":\"" + appName + "\"}",
				"application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Returns the ApIOmat version string
	 *
	 * @return version string
	 */
	public String getVersion( )
	{
		GetMethod request = new GetMethod( this.yambasBase );
		try
		{
			request.setRequestHeader( "Accept", "application/json" );
			this.client.executeMethod( request );
			final JSONObject json =
				new JSONObject( AomHelper.getStringFromStream( request.getResponseBodyAsStream( ) ) );
			return json.getString( "version" );
		}
		catch ( IOException e )
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
	public HttpMethod getApp( )
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
	public HttpMethod getApp( String customerName, String appName, AOMSystem system )
	{
		GetMethod request = new GetMethod( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		try
		{
			this.client.executeMethod( request );
			final JSONObject json =
				new JSONObject( AomHelper.getStringFromStream( request.getResponseBodyAsStream( ) ) );
			final JSONObject keysObj = json.getJSONObject( "apiKeys" );

			this.apiKey = keysObj.getString( system.toString( ).toLowerCase( ) + "ApiKey" );

		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Deletes the app which is currently set
	 *
	 * @return request object to check status codes and return values
	 */
	public HttpMethod deleteApp( )
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
	public HttpMethod deleteApp( String customerName, String appName )
	{
		DeleteMethod request = new DeleteMethod( this.yambasBase + "customers/" + customerName + "/apps/" + appName );
		setAuthorizationHeader( request );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Drops all data contained in the currently set app
	 *
	 * @return request object to check status codes and return values
	 */
	public HttpMethod dropData( )
	{
		DeleteMethod request = new DeleteMethod( this.yambasBase + "apps/" + this.appName + "/models" );
		setAuthorizationHeader( request );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Returns all MetaModels (classes) of a module
	 *
	 * @param moduleName name of the module
	 * @return request object to check status codes and return values
	 */
	public HttpMethod getMetaModels( final String moduleName )
	{
		GetMethod request = new GetMethod( this.yambasBase + "modules/" + moduleName + "/metamodels" );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod createObject( String moduleName, String dataModelName )
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
	public HttpMethod createObject( String moduleName, String dataModelName, JSONObject otherFieldsObject )
	{
		final PostMethod request = new PostMethod(
			this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" + dataModelName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		try
		{

			otherFieldsObject.put( "@type", moduleName + '$' + dataModelName );
			StringRequestEntity requestEntity = new StringRequestEntity( otherFieldsObject.toString( ),
				"application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );
			System.out.println( otherFieldsObject.toString( ) );
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod getObject( String moduleName, String dataModelName, String dataModelId )
	{
		GetMethod request = new GetMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
			dataModelName + "/" + dataModelId );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod getObjects( String moduleName, String dataModelName, String query )
	{
		GetMethod request = new GetMethod(
			this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" + dataModelName +
				( query == null ? "" : "?q=" + query ) );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	 * @param otherFields
	 *        the other fields to set as NameValuePairs
	 * @return request object to check status codes and return values
	 */
	public HttpMethod updateObject( String moduleName, String dataModelName, String dataModelId, boolean fullUpdate,
		NameValuePair... otherFields )
	{
		PutMethod request = new PutMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + '/' +
			dataModelName + '/' + dataModelId );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		request.setRequestHeader( "X-apiomat-fullupdate", String.valueOf( fullUpdate ) );
		try
		{
			StringBuilder sb = new StringBuilder( );
			sb.append( "{ \"@type\":\"" );
			sb.append( moduleName );
			sb.append( '$' );
			sb.append( dataModelName );
			sb.append( "\"," );
			for ( NameValuePair nvp : otherFields )
			{
				sb.append( "\"" );
				sb.append( nvp.getName( ) );
				sb.append( "\":" );
				if ( nvp.getValue( ).startsWith( "[" ) == false )
				{
					sb.append( "\"" );
				}
				sb.append( nvp.getValue( ) );
				if ( nvp.getValue( ).startsWith( "[" ) == false )
				{
					sb.append( "\"" );
				}
				sb.append( "," );
			}
			sb.delete( sb.length( ) - 1, sb.length( ) );
			sb.append( "}" );

			StringRequestEntity requestEntity = new StringRequestEntity( sb.toString( ), "application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod updateObject( String moduleName, String dataModelName, String dataModelId, boolean fullUpdate,
		JSONObject objectToUpdate )
	{
		final PutMethod request =
			new PutMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + '/' +
				dataModelName + '/' + dataModelId );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "x-apiomat-sdkVersion", "1.0" );
		request.setRequestHeader( "X-apiomat-fullupdate", String.valueOf( fullUpdate ) );
		objectToUpdate.put( "@type", moduleName + "$" + dataModelName );

		try
		{
			final StringRequestEntity requestEntity =
				new StringRequestEntity( objectToUpdate.toString( ), "application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod addReference( String moduleName, String dataModelName, String dataModelId, String attributeName,
		String refId, boolean isTransientRef, String refClassModule, String refClassName )
	{
		PostMethod request = new PostMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
			dataModelName + "/" + dataModelId + "/" + attributeName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			String data = "{ \"@type\":\"" + refClassModule + "$" + refClassName + "\",\"" +
				( isTransientRef ? "foreignId" : "id" ) + "\":\"" + refId + "\"}";
			StringRequestEntity requestEntity = new StringRequestEntity( data, "application/json", "UTF-8" );
			request.setRequestEntity( requestEntity );

			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod getReference( String moduleName, String dataModelName, String dataModelId,
		String refAttributeName )
	{
		GetMethod request = new GetMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" +
			dataModelName + "/" + dataModelId + "/" + refAttributeName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod deleteReference( String moduleName, String dataModelName, String dataModelId,
		String refAttributeName, String refId )
	{
		DeleteMethod request = new DeleteMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName +
			"/" + dataModelName + "/" + dataModelId + "/" + refAttributeName + "/" + refId );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod deleteObject( String moduleName, String dataModelName, String dataModelId )
	{
		DeleteMethod request = new DeleteMethod( this.yambasBase + "apps/" + this.appName + "/models/" + moduleName +
			"/" + dataModelName + "/" + dataModelId );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Posts static data, either as image or as file
	 *
	 * @param content the content
	 * @param isImage indicates whether this is an image or a file
	 * @return request object to check status codes and return values
	 */
	public HttpMethod postStaticData( final byte[ ] content, final boolean isImage )
	{
		final PostMethod request =
			new PostMethod( this.yambasBase + "apps/" + this.appName + "/data/" + ( isImage ? "images/" : "files/" ) );
		request.setRequestEntity( new ByteArrayRequestEntity( content ) );
		request.setRequestHeader( "Content-Type", "application/octet-stream" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Deletes static data, either image or file
	 *
	 * @param id the file id
	 * @param isImage indicates whether this is an image or a file
	 * @return request object to check status codes and return values
	 */
	public HttpMethod deleteStaticData( String id, final boolean isImage )
	{
		final DeleteMethod request =
			new DeleteMethod(
				this.yambasBase + "apps/" + this.appName + "/data/" + ( isImage ? "images/" : "files/" ) + id );
		request.setRequestHeader( "Content-Type", "application/octet-stream" );
		request.setRequestHeader( "x-apiomat-apikey", this.apiKey );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( final IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Sends a request to yambas base URL + path. yambas base URL is: yambasHost + "/yambas/rest/"
	 *
	 * @param path
	 *        the path
	 * @param contentTypes content types ("application/json" used if not provided)
	 * @return request object to check status codes and return values
	 */
	public HttpMethod getRequestRestEndpoint( String path, String... contentTypes )
	{
		GetMethod request = new GetMethod( this.yambasBase + path );
		setAuthorizationHeader( request );
		if ( contentTypes.length == 0 )
		{
			request.setRequestHeader( "ContentType", "application/json" );
		}
		else
		{
			request.setRequestHeader( "ContentType", String.join( ",", contentTypes ) );
		}
		request.setRequestHeader( "x-apiomat-apikey", getApiKey( ) );
		request.setRequestHeader( "x-apiomat-system", getSystem( ).toString( ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod postRequestRestEndpoint( String path, InputStream payLoad )
	{
		String url = this.yambasBase + path;
		PostMethod request = new PostMethod( url );

		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
		request.setRequestHeader( "x-apiomat-apikey", getApiKey( ) );
		request.setRequestHeader( "x-apiomat-system", getSystem( ).toString( ) );

		request.setRequestEntity( new InputStreamRequestEntity( payLoad ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
	}

	/**
	 * Dumps an apps data to csv-format
	 *
	 * @param appName
	 *        the AppName
	 * @return request object to check status codes and return values
	 */
	public HttpMethod exportAppDataToCSV( final String appName )
	{
		return getRequestRestEndpoint( "modules/csv/spec/" + appName );
	}

	/**
	 * Imports a CSV dump into an existing app
	 *
	 * @param appName
	 *        the Appname
	 * @param buf
	 *        byte array buffer of CSV-zip
	 *
	 * @return request object to check status codes and return values
	 */
	public HttpMethod importCSVToApp( String appName, byte[ ] buf )
	{
		PostMethod request = new PostMethod( this.yambasBase + "modules/csv/spec/" + appName );
		setAuthorizationHeader( request );
		request.setRequestHeader( "x-apiomat-system", this.system.toString( ) );
		request.setRequestHeader( "Content-Type", "application/octet-stream" );
		request.setRequestHeader( "X-apiomat-apikey", this.apiKey );
		request.setRequestEntity( new ByteArrayRequestEntity( buf ) );
		try
		{
			this.client.executeMethod( request );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return request;
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
	public HttpMethod downloadNM( final String moduleName, final String targetPath ) throws IOException
	{
		HttpMethod response = getRequestRestEndpoint( "modules/" + moduleName + "/sdk" );
		AomHelper.unzip( response.getResponseBody( ), targetPath );
		return response;
	}

	private void setAuthorizationHeader( HttpMethodBase requestMethod )
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
			requestMethod.setRequestHeader( "Authorization", authHeader );
		}
	}
}
