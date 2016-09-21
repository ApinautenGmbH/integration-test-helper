/* Copyright (c) 2011 - 2015 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten GmbH, Hainstrasse 4, 04109 Leipzig, Germany
 *
 * Oct 19, 2015
 * thum */
package com.apiomat.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

/**
 * @author thum
 */
public class AomHelper
{

	/**
	 * cuts the id off the url
	 *
	 * @param inputUrl the input url
	 *
	 * @return the id
	 */
	public static String getIdFromUrl( final String inputUrl )
	{
		if ( inputUrl == null )
		{
			return "";
		}
		int idx = inputUrl.lastIndexOf( '/' );
		if ( idx == -1 )
		{
			return "";
		}
		return inputUrl.substring( idx + 1 );
	}

	/**
	 * @param is the inputstream
	 * @return the string from id
	 */
	public static String getStringFromStream( InputStream is )
	{
		StringWriter writer = new StringWriter( );
		try
		{
			IOUtils.copy( is, writer, "UTF-8" );
		}
		catch ( IOException e )
		{
			return null;
		}
		return writer.toString( );
	}
}
