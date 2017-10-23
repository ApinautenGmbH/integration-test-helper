/* Copyright (c) 2011 - 2017, Apinauten GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY IT. */
package com.apiomat.helper;

/**
 * ApiOmat status codes
 */
public enum Status
{
	SCRIPT_ERROR( 701, "Script error!" ),
	APPLICATION_NOT_ACTIVE( 702, "Application is deactivated!" ),
	BAD_IMAGE( 703, "Image format seems to be corrupted!" ),
	BAD_ID( 704, "ID format is wrong!" ),
	CONCURRENT_ACCESS( 705, "Concurrent access forbidden!" ),
	APPLICATION_SANDBOX( 706, "Application is in sandbox mode!" ),
	MODEL_NOT_DEPLOYED( 707, "Class is not deployed!" ),
	WRONG_REF_TYPE( 709, "Wrong reference type!" ),
	ATTRIBUTE_NOT_SET( 710, "Attribute not set!" ),
	OPERATION_NOT_POSSIBLE( 711, "CRUD operation not possible on this class!" ),
	APPLICATION_NAME_MISMATCH( 712, "Application name does not match the one defined in the class!" ),
	WRONG_AUTH_HEADER( 713, "Wrong authentication header format, must be 'username:password'" ),
	MODEL_STILL_USED( 714, "Class is still used by other attributes, scripts or subclasses!'" ),
	COLLECTION_NOT_ALLOWED( 715, "Collection is not supported for this class type!" ),
	FB_NO_VALID_MEMBER( 716, "Request send from no valid member" ),
	FB_NO_OAUTH_TOKEN( 717, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
	FB_POST_ID_MISSING( 718, "Facebook post id has to be set!" ),
	RESTORE_NO_DUMPS_FOUND( 719, "No dumps for app on this date exist!" ),
	TW_NO_VALID_MEMBER( 720, "Request send from no valid member" ),
	TW_NO_OAUTH_TOKEN( 721, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
	IMEXPORT_WRONG_ENCODING( 722, "Wrong Encoding" ),
	IMEXPORT_WRONG_CONTENT( 723, "Wrong Filecontent" ),
	PUSH_PAYLOAD_EXCEEDED( 724, "Payload size exceeded!" ),
	PUSH_ERROR( 725, "Error in push request!" ),
	BAD_EMAIL( 726, "eMail format is wrong!" ),
	BAD_PROMOTIONCODE_DISCOUNT( 727, "Discount value is wrong!" ),
	BAD_PROMOTIONCODE_CODE( 728, "Code is invalid" ),
	PLAN_PRICE( 729, "Plan price must be >= 0!" ),
	PLAN_NO_SYSTEMS( 730, "Plan must have at least one system!" ),
	SCRIPT_TIME_ERROR( 731, "Script was interrupted, execution took too long." ),
	INVALID_NAME( 732, "Name must start with a letter, followed only by letters or numbers." ),
	ATTRIBUTE_IN_SUPERCLASS( 733, "Attribute is already defined in superclass." ),
	JSON_TYPE_ERROR( 734, "The @type is not correctly defined in your JSON (must be: MODULENAME$CLASSNAME" ),
	TBLR_NO_VALID_MEMBER( 735, "Request send from no valid member" ),
	TBLR_NO_OAUTH_TOKEN( 736, "Requesting member has no oAuth token, please authenticate! See http://doc.apiomat.com" ),
	TBLR_POST_ID_MISSING( 737, "Tumblr post id has to be set!" ),
	LOCATION_INVALID( 738, "Location data is invalid (latitude or longitude missing)!" ),
	SCRIPT_EXCEPTION( 739, "Exception was raised in external code!" ),
	BAD_ACCOUNTNAME( 740, "Account name must contain only characters A-Z,a-z or 0-9!" ),
	BAD_IMAGE_ALPHA( 746, "alpha is wrong (must be a double value between 0.0 and 1.0)" ),
	BAD_IMAGE_BGCOLOR( 747, "bgcolor is wrong (must be an RGB hex value without #, like 'FF0000' for red)" ),
	BAD_IMAGE_FORMAT( 748, "format is wrong (can only be png, gif, bmp or jpg/jpeg)" ),
	QUERY_ERROR( 708, "Query could not be parsed!" ),
	BAD_TYPE_IN_QUERY( 741, "The query contains a value with the wrong type" ),
	UNKNOWN_CLASS_IN_QUERY( 742, "The definition of the class couldn't be found" ),
	WRONG_NUM_FORMAT_IN_QUERY( 743, "A number was supplied in the wrong format" ),
	QUERY_PARSE_ERROR( 744, "The query couldn't be parsed" ),
	UNKNOWN_ATTRIBUTE_IN_QUERY( 745, "An attribute that was used in the query doesn't exist in the class" ),
	FOREIGNID_NOT_SET( 749, "Foreign ID must be set to a unique value for this class!" ),
	CLASSES_MISSING( 750,
		"Not all classes for this module are contained in the jar! This will lead to compile errors." ),
	INVALID_ATTRIBUTE_TYPE( 751, "Attributes type is invalid" ),
	TOKEN_VALUE_EXISTS( 752, "The token value already exists" ),
	JSON_FORMAT_ERROR( 753, "JSON could not be parsed" ),
	MODULE_DEPLOYMENT( 754, "Module is currently deploying. Please try again later." ),
	BAD_USERNAME( 755, "User name must not contain a ':'." ),
	CSV_ZIP_FORMAT( 756, "CSV import only accepts a single .zip file!" ),
	VERIFICATION( 757, "Verification error" ),
	MODULE_STILL_USED( 758, "Module is still used by other modules of this app!'" ),
	CLASS_NOT_IN_MODULE( 759, "Model name not found for this module!" ),
	ORDER_TRANSACTION_TIMESTAMP_OUTDATED( 760, "Transaction outdated!" ),
	ORDER_TRANSACTION_ID_INVALID( 761, "Transaction ID invalid!" ),
	ORDER_TRANSACTION_SECRET_INVALID( 762, "Transaction secret invalid!" ),
	MANDATORY_FIELD( 763, "Mandatory fields must not be empty or null!" ),
	INVALID_PASSWD_LENGTH( 764, "Password must have a length of at least 6 characters!" ),
	BAD_PROMOTIONCODE_VALID( 765, "Valid from/to of Code is null" ),
	BAD_CLASS_NAME_SAME_AS_MODULE( 766, "Class name must not be the same as the module name!" ),
	NO_ORG_MEMBER( 767, "Customer is not a member of the organization" ),
	MODULE_CLASS_NOT_CONTAINED( 768,
		"Module main class is not contained in the uploaded file! Probably wrong module uploaded?" ),
	BAD_GROUP_NAME( 769, "Account name must contain only characters A-Z,a-z or 0-9!" ),
	INVISIBLE_CLASS( 770, "Class is not visible to REST!" ),
	MODULE_TYPE_NOT_ALLOWED( 771, "The action is not allowed for this module type" ),
	MAX_FILE_SIZE( 772, "File is larger than maximum file size!" ),
	BAD_RESOURCE( 773, "Resource seems to be corrupted or is unexpectedly null or empty." ),
	APPLICATION_NOT_FOUND( 801, "Application was not found!" ),
	CUSTOMER_NOT_FOUND( 802, "Customer was not found!" ),
	ID_NOT_FOUND( 803, "ID was not found!" ),
	MODEL_NOT_FOUND( 804, "Class was not found!" ),
	MODULE_NOT_FOUND( 805, "Module was not found!" ),
	PLAN_NOT_FOUND( 807, "Plan was not found!" ),
	PROMOCODE_NOT_FOUND( 808, "Promotion code not valid!" ),
	DEMOAPP_NOT_FOUND( 809, "This language has no demo content" ),
	ORGANIZATION_NOT_FOUND( 810, "Organization was not found!" ),
	GROUP_NOT_FOUND( 811, "Group was not found!" ),
	ACCOUNT_NOT_FOUND( 812, "Account was not found!" ),
	DEFAULT_MODULE_NOT_FOUND( 813, "Default module was not found for the given account" ),
	MODULE_USE_FORBIDDEN( 820, "Required module is not attached to app" ),
	PUSH_ERROR_APIKEY( 821, "No API Key defined for Push service!" ),
	PUSH_ERROR_CERTIFICATE( 822, "No certificate defined for Push service!" ),
	SAME_NAME_USED_IN_SUPERCLASS( 823, "Same name is already used in a superclass." ),
	PAYMENT_MAX_MODULE( 824, "Maximum number of used modules exceeded for this plan." ),
	PAYMENT_SYSTEM( 825, "Selected system use is not allowed for this plan." ),
	PAYMENT_DOWNGRADE( 826, "Up/Downgrading plans is only allowed for super admins." ),
	SAVE_REFERENECE_BEFORE_REFERENCING( 827,
		"Object you are trying to reference is not on the server. Please save it first." ),
	PAYMENT_DB_SIZE( 828, "Used database size exceeds plan" ),
	ENDPOINT_PATH_NOT_ALLOWED( 829, "Endpoint not allowed for app; please add path to your app's config." ),
	PAYMENT_NO_CRON( 1820, "Cronjobs are not allowed for this plan." ),
	PAYMENT_MODULE_NOT_FREE( 1821, "This module is not available for free plan." ),
	NATIVEMODULE_DEACTIVATED( 1822, "Native Module feature is not activated for this installation." ),
	LICENSE_INVALID( 1823, "Your license does not allow this action." ),
	PAYMENT_NO_CUSTOMERROLES( 1824, "Customer role usage is not available for free plan." ),
	WHITELABEL( 1825, "Only available for whitelabel installations." ),
	WHITELABEL_NOT( 1826, "Not available for whitelabel installations." ),
	MODULE_CONFIG_NO_DOT( 1827, "No dot allowed in module config key." ),
	PLAN_FALLBACK( 1828, "Application cannot be activated without valid plan." ),
	PLAN_INACTIVE( 1829, "Plan is not selectable!" ),
	ENTERPRISE( 1830, "Only available for enterprise installations." ),
	ACCOUNT_UNACCEPTED_CONTRACTS( 1831, "Account has unaccepted Contracts" ),
	DELETE_MANDATORY_DEFAULT_MODULE( 1832, "It is not allowed to remove this default module" ),
	ID_EXISTS( 830, "ID exists!" ),
	NAME_RESERVED( 831, "Name is reserved!" ),
	CIRCULAR_DEPENDENCY( 832, "You can't set circular inheritance!" ),
	NAME_EXISTS( 833, "Name is already used!" ),
	EMAIL_EXISTS( 834, "E-mail is already used!" ),
	CUSTOMER_IN_ORG( 835, "Customer is already member of an organization" ),
	PUSH_ERROR_PROXY( 836, "Proxy configuration caused an error for Push service!" ),
	UNAUTHORIZED( 840, "Authorization failed!" ),
	WRONG_APIKEY( 841, "API Key was not correct!" ),
	EVALANCHE_UNAUTH( 842, "Authorization failed! Maybe username/password was not set for evelanche configuration?" ),
	PW_CHANGE_W_TOKEN( 843, "Not authorized to change a user's password when authenticating with a token." ),
	TOKEN_AUTH_ERROR( 844, "The token could not be authenticated" ),
	TOKEN_READ_ONLY( 845, "The token can only be used for read requests." ),
	AUTHENTICATION_REJECTED( 846, "Authentication with username/password was rejected by third-party-system." ),
	CRUD_ERROR( 901, "Internal error during CRUD operation" ),
	IMEXPORT_ERROR( 902, "Error during im/export!" ),
	COMPILE_ERROR( 903, "Classes could not be compiled!" ),
	REFERENCE_ERROR( 904, "Error in class reference!" ),
	PUSH_PAYLOAD_ERROR( 905, "Failed to create payload!" ),
	PUSH_SEND_ERROR( 906, "Failed to send message(s)!" ),
	PUSH_INIT_FAILED( 907, "Failed to initialize push service!" ),
	FACEBOOK_ERROR( 908, "An error occured while communicating with facebook!" ),
	FACEBOOK_OAUTH_ERROR( 910, "facebook throws oAuth error! Please show login dialog again" ),
	FACEBOOK_OAUTH_ERROR2( 917, "Received OAuth2 error from Facebook" ),
	MEMBER_NOT_FOUND( 911, "Can't find member with this id/username" ),
	WORDPRESS_FETCH_DATA_ERROR( 912, "Can't fetch data for wordpress blog" ),
	TUMBLR_OAUTH_ERROR( 913, "tumblr threw oAuth error! Please show login dialog again" ),
	TUMBLR_ERROR( 914, "Error communicating with tumblr!" ),
	EXECUTE_METHOD_ERROR_PRIMITIVE( 915, "Only primitive types are allowed" ),
	EXECUTE_METHOD_ERROR( 916, "Execute method failed" ),
	OAUTH_TOKEN_REQUEST_ERROR( 918, "An error occured during requesting an ApiOmat OAuth2 token" ),
	FINDING_RESOURCE_ERROR( 919, "An error occured while trying to find the resource" ),
	NATIVEMODULE_DEPLOY( 920, "Executing onDeploy failed" ),
	TOKEN_SEARCH_ERROR( 921, "An error occured while searching for tokens" ),
	MODULE_CONFIG_MISSING( 922, "Your module seems not to be configured properly" ),
	NATIVEMODULE_INIT( 923, "Could not initialize git repository" ),
	NATIVEMODULE_PULL( 924, "Could not pull git repository" ),
	NATIVEMODULE_PUSH( 925, "Could not push git repository" ),
	NO_DOGET_RETURN( 926, "The module's doGet didn't return a model" ),
	CUSTOMER_TWO_ORGS( 927, "The customer was found in two organizations" ),
	NATIVEMODULE_HOOKS_NOT_FOUND( 928, "Annotated hook class not found" ),
	ANALYTICS_ERROR( 929, "The analytics instance couldn't process the request correctly" ),
	EMAIL_ERROR( 930, "Error during sending email" ),
	HREF_NOT_FOUND( 601, "Class has no HREF; please save it first!" ),
	WRONG_URI_SYNTAX( 602, "URI syntax is wrong" ),
	WRONG_CLIENT_PROTOCOL( 603, "Client protocol is wrong" ),
	IO_EXCEPTION( 604, "IOException was thrown" ),
	UNSUPPORTED_ENCODING( 605, "Encoding is not supported" ),
	INSTANTIATE_EXCEPTION( 606, "Error on class instantiation" ),
	IN_PERSISTING_PROCESS( 607, "Object is in persisting process. Please try again later" ),
	VERIFY_SOCIALMEDIA( 608, "Can't verify against social media provider" ),
	TOO_MANY_LOCALIDS( 609, "Can't create more localIDs. Please try again later" ),
	MAX_CACHE_SIZE_REACHED( 610, "The maximum cache size is reached." ),
	CANT_WRITE_IN_CACHE( 611, "Can't persist data to cache." ),
	BAD_DATASTORE_CONFIG( 612,
		"For requesting a session token without a refresh token, the Datastore must be configured with a username and password" ),
	NO_TOKEN_RECEIVED( 613, "The response didn't contain a token" ),
	NO_NETWORK( 614, "No network connection available" ),
	ID_NOT_FOUND_OFFLINE( 615, "ID was not found in offline storage" ),
	ATTACHED_HREF_MISSING( 616, "HREF of attached image / file / reference is missing" ),
	REQUEST_TIMEOUT( 617, "The request timed out during connecting or reading the response" ),
	ASYNC_WAIT_ERROR( 618, "An error occured during waiting for an async task to finish" ),
	IN_DELETING_PROCESS( 619, "Object is in deleting process. Please try again later" ),
	SSO_REDIRECT( 620, "The request was redirected to an SSO Identity Provider" ),
	MANUAL_CONCURRENT_WRITE_FAILED( 621, "Concurrent write to own concurrent data type failed" ),
	SAVE_FAILED( 622, "Load not executed because save already failed" ),
	SSL_ERROR( 623, "An error occurred during establishing a secure connection" ),
	MAX_FILE_SIZE_OFFLINE_EXCEEDED( 624, "The max file size for offline saving is exceeded" ),
	SQL_CONSTRAINT( 625, "An error occurred because of an SQL constraint (for example unique ForeignID" ),
	MALICIOUS_MEMBER( 950, "Malicious use of member detected!" ),
	NULL( 9999, "" ); // placeholder

	private final int code;
	private final String reason;

	Status( final int statusCode, final String reasonPhrase )
	{
		this.code = statusCode;
		this.reason = reasonPhrase;
	}

	public int getStatusCode( )
	{
		return this.code;
	}

	public String getReasonPhrase( )
	{
		return this.reason;
	}

	public static Status getStatusForCode( int code )
	{
		for ( Status s : Status.values( ) )
		{
			if ( s.getStatusCode( ) == code )
			{
				return s;
			}
		}
		return NULL;
	}
}
