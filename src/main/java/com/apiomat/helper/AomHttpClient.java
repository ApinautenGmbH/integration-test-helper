/* Copyright (c) 2011 - 2015 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten GmbH, Hainstrasse 4, 04109 Leipzig, Germany
 *
 * Oct 16, 2015
 * thum */
package com.apiomat.helper;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Assert;

/**
 * @author thum
 */
public class AomHttpClient
{
	private final String yambasHost;

	private final String yambasBase;

	private String userName;

	private String password;

	private String accessToken;

	private final HttpClient client;

	private String apiKey;

	private String appName;

	private String customerName;

	private final AOMSystem system;

	/**
	 * Creates a new AomHttpClient
	 *
	 * @param host
	 */
	public AomHttpClient( String host, AOMSystem system )
	{
		this.yambasHost = host == null ? System.getProperty( "yambasHost", "http://localhost:8080" ) : host;
		this.yambasBase = this.yambasHost + "/yambas/rest/";
		this.system = system;
		this.client = new HttpClient( );
	}

	/**
	 *
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
	 * @param apiKey
	 *        the apiKey to set
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
	 * set the credentials used for auth
	 *
	 * @param username
	 * @param password
	 */
	public void setCredentials( String username, String password )
	{
		this.accessToken = null;
		this.userName = username;
		this.password = password;
	}

	/**
	 * set the credentials used for auth
	 *
	 * @param accessToken
	 */
	public void setCredentials( String accessToken )
	{
		this.userName = null;
		this.password = null;
		this.accessToken = accessToken;
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
	 * creates the customer and sets it automatically as customerName ({@link #getCustomerName()})
	 *
	 * @param customerName
	 * @param email
	 * @param password
	 * @return the statuscode
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
	 * creates the app for the currently set customer
	 *
	 * @param appName
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod createApp( String appName )
	{
		return createApp( this.customerName, appName );
	}

	/**
	 * creates an app for a specific customer
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app to create
	 * @return the {@link HttpMethod} object after executing the request
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
	 * adds the module to the app and customer which were set
	 *
	 * @param moduleName
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod addModuleToApp( String moduleName )
	{
		return addModuleToApp( this.customerName, this.appName, moduleName );
	}

	/**
	 * adds the module to the app
	 *
	 * @param customerName
	 *        the name of the customer which owns the app
	 * @param appName
	 *        the name of the app
	 * @param moduleName
	 *        the name of the module to add
	 * @return the {@link HttpMethod} object after executing the request
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
	 * adds the module to the app
	 *
	 * @param customerName the name of the customer which owns the app
	 * @param appName the name of the app
	 * @param moduleName the name of the module to add
	 * @param className
	 * @return the {@link HttpMethod} object after executing the request
	 * @throws UnsupportedEncodingException
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
	 * updates the module
	 *
	 * @param moduleName
	 *        the name of the module to add
	 * @return the {@link HttpMethod} object after executing the request
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
	 * deploys the app
	 *
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod deployApp( )
	{
		return deployApp( this.customerName, this.appName );
	}

	/**
	 * deploys the app
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app
	 * @return the {@link HttpMethod} object after executing the request
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
	 * updates a single app config field
	 *
	 * @param customerName
	 * @param appName
	 * @param moduleName
	 * @param key
	 * @param value
	 * @return
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
	 * requests to get the app
	 *
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod getApp( )
	{
		return getApp( this.customerName, this.appName, this.system );
	}

	/**
	 * requests to get the app
	 *
	 * @param customerName
	 *        the name of the customer
	 * @param appName
	 *        the name of the app
	 * @param system
	 *        the used system
	 * @return the {@link HttpMethod} object after executing the request
	 */
	@SuppressWarnings( "unchecked" )
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
	 * deletes the app which is currently set
	 *
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod deleteApp( )
	{
		return deleteApp( this.customerName, this.appName );
	}

	/**
	 * deletes the specified app
	 *
	 * @param customerName
	 *        the name of the customer which owns the app
	 * @param appName
	 *        the name of the app to delete
	 * @return the {@link HttpMethod} object after executing the request
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
	 * drops all data contained in the currently set app
	 *
	 * @return the {@link HttpMethod} object after executing the request
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
	 * creates an object of the given dataModelName and moduleName <br/>
	 * the appname which is set in this object will be used<br/>
	 *
	 * This method is deprecated, use {@link #createObject(String, String, JSONObject)} instead
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param otherFields
	 *        the other fields to set as NameValuePairs
	 * @return the {@link HttpMethod} object after executing the request
	 */
	@Deprecated
	public HttpMethod createObject( String moduleName, String dataModelName, NameValuePair... otherFields )
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
			StringBuilder sb = new StringBuilder( );
			sb.append( "{ \"@type\":\"" );
			sb.append( moduleName );
			sb.append( '$' );
			sb.append( dataModelName );
			sb.append( "\"," );
			for ( NameValuePair nvp : otherFields )
			{
				sb.append( '"' ).append( nvp.getName( ) ).append( "\":\"" ).append( nvp.getValue( ) ).append( "\"," );
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
	 * creates an object of the given dataModelName and moduleName <br/>
	 * the appname which is set in this object will be used
	 *
	 * @param moduleName
	 *        the modulenname
	 * @param dataModelName
	 *        the name of the datamodels
	 * @param otherFieldsObject
	 *        the other fields to set as JSONObject (the @type field will be added automatically)
	 * @return the {@link HttpMethod} object after executing the request
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
	 * @param dataModelName
	 * @param dataModelId
	 * @return the {@link HttpMethod} object after executing the request
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
	 * @param dataModelName
	 * @param query
	 * @return
	 */
	public HttpMethod getObjects( String moduleName, String dataModelName, String query )
	{
		GetMethod request = new GetMethod(
			this.yambasBase + "apps/" + this.appName + "/models/" + moduleName + "/" + dataModelName + "?q=" + query );
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
	 * updates an object of the given dataModelName and moduleName <br/>
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
	 * @return the {@link HttpMethod} object after executing the request
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
	 * adds a reference
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
	 * @return the {@link HttpMethod} object after executing the request
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
	 * Get the references for a specified class
	 *
	 * @param moduleName
	 * @param dataModelName
	 * @param dataModelId
	 * @param refAttributeName
	 * @return the {@link HttpMethod} object after executing the request
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
	 * Delete the references for a specified class
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
	 * @return the {@link HttpMethod} object after executing the request
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
	 * Get the references for a specified class
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
	 * @return the {@link HttpMethod} object after executing the request
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
	 * Sends a request to yambas base URL + path. yambas base URL is: yambasHost + "/yambas/rest/"
	 *
	 * @param path
	 *        the path
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod getRequestRestEndpoint( String path )
	{
		GetMethod request = new GetMethod( this.yambasBase + path );
		setAuthorizationHeader( request );
		request.setRequestHeader( "ContentType", "application/json" );
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
	 * @return the {@link HttpMethod} object after executing the request
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
	 * Revoke an OAuth2 token. Requires this client to be configured with a valid access token.
	 *
	 * @return the {@link HttpMethod} object after executing the request
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
	 * @param refreshToken
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
	 * creates the authentication header value
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
	 * Return authentication header with OAuth2 token
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
	 * Dumps an app�s data to csv-format
	 *
	 * @param appName
	 *        the AppName
	 * @return the {@link HttpMethod} object after executing the request
	 */
	public HttpMethod exportAppDataToCSV( final String appName )
	{
		return getRequestRestEndpoint( "modules/csv/spec/" + appName );
	}

	/**
	 * imports a CSV-dump into an existing app
	 *
	 * @param appName
	 *        - the Appname
	 * @param buf
	 *        - byte-Array-buffer of CSV-zip
	 *
	 * @return the {@link HttpMethod} object after executing the request
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
	 * @param data
	 * @param targetPath
	 * @return number of files having been unzipped
	 */
	public static long unzip( final byte[ ] data, final String targetPath )
	{
		final InputStream input = new ByteArrayInputStream( data );
		final byte[ ] buffer = new byte[ 1024 ];
		final ZipInputStream zip = new ZipInputStream( input );
		long unzippedFiles = 0;
		try
		{
			int len = 0;
			ZipEntry e = zip.getNextEntry( );

			while ( e != null )
			{
				if ( !e.isDirectory( ) )
				{
					final ByteArrayOutputStream baos = new ByteArrayOutputStream( );
					while ( ( len = zip.read( buffer ) ) != -1 )
					{
						baos.write( buffer, 0, len );
					}
					baos.close( );

					final String filename = e.getName( );
					final byte[ ] fileData = baos.toByteArray( );
					FileUtils.writeByteArrayToFile( new File( targetPath + "/" + filename ), fileData );
					unzippedFiles++;
				}
				e = zip.getNextEntry( );
			}
			zip.close( );
		}
		catch ( IOException e )
		{
			Assert.fail( );
		}
		return unzippedFiles;
	}

	/**
	 * Downloads a module and unzips it to path
	 *
	 * @param moduleName
	 *        - name of the Module
	 * @param targetPath
	 *        - extract path
	 * @return the {@link HttpMethod} object after executing the request
	 * @throws IOException
	 */
	public HttpMethod downloadNM( final String moduleName, final String targetPath ) throws IOException
	{
		HttpMethod response = getRequestRestEndpoint( "modules/" + moduleName + "/sdk" );
		unzip( response.getResponseBody( ), targetPath );
		return response;
	}

	/**
	 * @param target
	 *        - ant-target
	 * @param path
	 *        - system-path of nm
	 * @return success-string
	 * @throws Exception
	 */
	public String runAnttask( final String target, final String path, final String userPassword ) throws Exception
	{
		String result = "";
		final Process process;
		try
		{
			/* set system in properties file and upload */
			final Properties sdkProp = new Properties( );
			sdkProp.load( new FileInputStream( new File( path + "/sdk.properties" ) ) );
			sdkProp.setProperty( "password", userPassword );
			sdkProp.store( new FileOutputStream( new File( path + "/sdk.properties" ) ), null );
			String antBin = "ant";
			if ( System.getenv( "ANT_BIN" ) != null )
			{
				antBin = System.getenv( "ANT_BIN" );
			}

			/* if running this test on a local windows-system, set the ANT_BIN env var. */
			final ProcessBuilder pb = new ProcessBuilder( antBin, "-Dant.build.javac.target=1.8",
				"-Dant.build.javac.source=1.8", "-f", "build.xml", target );
			if ( pb.environment( ).containsKey( "JAVA_HOME" ) == false )
			{
				/* For executing the testJava8 test, we sometimes need to set the java home to a java 8 JDK; if
				 * necessary, extend this on windows systems */
				pb.environment( ).put( "JAVA_HOME", "/opt/jdk" );
			}
			pb.directory( new File( path ) );
			pb.redirectErrorStream( true );
			process = pb.start( );

			final InputStream is = process.getInputStream( );
			final InputStreamReader isr = new InputStreamReader( is );
			final BufferedReader br = new BufferedReader( isr );
			String line;
			boolean successfull = false;
			boolean isNextResultLine = false;
			while ( ( line = br.readLine( ) ) != null )
			{
				System.out.println( line );
				if ( isNextResultLine )
				{ /* store the line after "BUILD FAILED" to get the correct error message */
					result = line;
					isNextResultLine = false;
				}
				if ( line.contains( "BUILD SUCCESSFUL" ) )
				{
					successfull = true;
					result = line;
				}
				else if ( line.contains( "BUILD FAILED" ) )
				{
					isNextResultLine = true;
				}

			}
			assertTrue( successfull );
			br.close( );

		}
		catch ( final Exception e )
		{
			e.printStackTrace( );
			Assert.fail( );
		}

		return result;
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
