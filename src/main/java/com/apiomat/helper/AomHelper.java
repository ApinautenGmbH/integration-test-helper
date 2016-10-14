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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

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

	/**
	 * @param target - ant-target
	 * @param path - system-path of nm
	 * @param userPassword - the customer´s password
	 * @return success-string
	 * @throws Exception
	 */
	public static String runAnttask( final String target, final String path, final String userPassword )
		throws Exception
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
			final ProcessBuilder pb =
				new ProcessBuilder( antBin, "-Dant.build.javac.target=1.8", "-Dant.build.javac.source=1.8", "-f",
					"build.xml", target );
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
}
