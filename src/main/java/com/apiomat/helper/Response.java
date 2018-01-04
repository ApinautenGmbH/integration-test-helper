/* Copyright (c) 2012 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten GmbH, Hainstrasse 4, 04109 Leipzig, Germany
 *
 * 02.01.2018
 * andreas */
package com.apiomat.helper;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * Data transfer object between {@link AomHttpClient} and calling methods
 *
 * @author andreas
 */
public class Response
{
	private final StatusLine status;
	private byte[ ] entityContent = null;
	private Header[ ] headers = null;

	/**
	 * Constructor
	 *
	 * @param response the http response
	 */
	public Response( final HttpResponse response )
	{
		this.status = response.getStatusLine( );
		this.headers = response.getAllHeaders( );

		try
		{
			this.entityContent = EntityUtils.toByteArray( response.getEntity( ) );
			// EntityUtils.consume( response.getEntity( ) );
		}
		catch ( IllegalArgumentException | IOException e )
		{
			// ok
		}
	}

	/**
	 * @return the statusCode
	 */
	public StatusLine getStatusLine( )
	{
		return this.status;
	}

	/**
	 * @return the entityContent
	 */
	public byte[ ] getEntityContent( )
	{
		return this.entityContent;
	}

	/**
	 * returns an empty string if the response has no content
	 *
	 * @return an empty string if the response has no content
	 */
	public String getEntityContentAsString( )
	{
		final byte[ ] content = getEntityContent( );
		return content == null ? "" : new String( content );
	}

	public String getHeader( final String name )
	{
		if ( this.headers == null )
		{
			return null;
		}
		for ( Header h : this.headers )
		{
			if ( h.getName( ).equalsIgnoreCase( name ) )
			{
				return h.getValue( );
			}
		}
		return null;
	}
}
