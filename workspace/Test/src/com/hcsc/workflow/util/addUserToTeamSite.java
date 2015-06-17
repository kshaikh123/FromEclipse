//package fileConnection;
package com.hcsc.workflow.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.*;

import com.interwoven.cssdk.access.CSAuthorizationException;
import com.interwoven.cssdk.access.CSExpiredSessionException;
import com.interwoven.cssdk.access.CSGroup;
import com.interwoven.cssdk.access.CSLDAPDatabase;
import com.interwoven.cssdk.access.CSNewUser;
import com.interwoven.cssdk.access.CSPrincipalManager;
import com.interwoven.cssdk.access.CSUser;
import com.interwoven.cssdk.access.CSUserDatabase;
import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.common.CSIterator;
import com.interwoven.cssdk.common.CSObjectNotFoundException;
import com.interwoven.cssdk.common.CSRemoteException;
import com.interwoven.cssdk.filesys.CSAreaRelativePath;
import com.interwoven.cssdk.filesys.CSBranch;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSObjectAlreadyExistsException;
import com.interwoven.cssdk.workflow.CSExternalTask;
import com.interwoven.cssdk.workflow.CSTask;
import com.interwoven.cssdk.workflow.CSTransitionableTask;
import com.interwoven.cssdk.workflow.CSURLExternalTask;
import com.interwoven.cssdk.workflow.CSWorkflowEvent;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;


public class addUserToTeamSite implements CSURLExternalTask{

	/**
	 * @param args
	 */
	
	private static final transient Logger LOGGER = Logger.getLogger(addUserToTeamSite.class);
	
	String ErrorString = "";
	
	public void execute(CSClient client, CSExternalTask task, Hashtable params) {
	
		
		
		try {
			
			CSUserDatabase[] ldap = client.getPrincipalManager().searchDatabases(CSUserDatabase.DB_LDAP);
			
			String ldapId = "";
			
			String[] groupsArray = null;
			
			CSUser[] addUserToGroup = null;
			
			String userId = "";
			
			
			
		for(int i=0;i<ldap.length;i++){
			
			ldapId = ldap[i].getLDAPDatabaseDetails().getName();
			
			LOGGER.debug(ldapId);
			
			}
			
			userId = task.getVariable("userId");
			
		    Runtime rt = Runtime.getRuntime();
		    Process pr = rt.exec("/interwoven/TeamSite/bin/iwuseradm add-user " + userId + " -db LDAP_User_DB");
		    getErrorString(pr,"Add User");

		    int exitVal = pr.waitFor();
		    LOGGER.debug("Exited with error code "+exitVal);
		    
		    if(exitVal == 0){
		    	
		    	 String groups = task.getVariable("groups");
		    	 
		    	 LOGGER.debug("Groups " + groups);
		    	 
		    	 if(!groups.isEmpty()){
		    		 
		    		 LOGGER.debug("Entered if loop for groups");
		    		 
		    		String delimiter = ",";
		 		    
		 		    int delimPosition = groups.indexOf(delimiter);
		 			
		 			groupsArray = groups.split(delimiter);
		 			
		 			LOGGER.debug(groups.split(delimiter).length);
		 			
		 			for(int i =0; i<groupsArray.length;i++){
		 				
		 			    Runtime rtGroup = Runtime.getRuntime();
		 			    Process prGroup = rtGroup.exec("iwgroup add-member -u " + userId + " " + groupsArray[i]);
		 			    
		 			    LOGGER.debug("The member was added to " + groupsArray[i] );

		 			    int exitValGroup = prGroup.waitFor();
		 			    LOGGER.debug("Exited with error code "+exitValGroup);
		 				
		 			} 
		 			task.chooseTransition("User Addition Success Email", "User with user ID " +  userId + " was successfully added to TeamSite and added to groups "+ groups);
		 			
		    	 }
		    	 
		    	 task.chooseTransition("User Addition Success Email", "User with user ID " +  userId + " was successfully added to TeamSite");

		    	
		    }
		    
		    else{
		    
		    	 task.chooseTransition("User Addition Failure Email", "User with user ID " +  userId + " was not added to TeamSite due to the following error: " + ErrorString);
		    }
		
		    
		  
		
		}catch(Exception e) {
		
		    //System.out.println(e.toString());
		    //e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		
	}
	

	public void getErrorString(Process processExec, String command){ 
		
		try{
		
			StringWriter writer = new StringWriter();
			IOUtils.copy(processExec.getErrorStream(), writer, "UTF-8");
			ErrorString = writer.toString();
		
			int exitValLock = processExec.waitFor();
			LOGGER.debug(command + " exited with error code " +exitValLock);
			LOGGER.debug(command + " error " + ErrorString);
			
			//Get Error Message
			if(ErrorString.contains(":")){
				
				String[] error = ErrorString.split(":");
				
				for(int i=0; i < error.length; i++){
					
					LOGGER.debug(error[i]);
					
					if(i == (error.length - 1)){
						
						ErrorString = error[i].toLowerCase();
						LOGGER.debug("Final Error " + error[i]);
						
					}
				}
			}
			
			
		}
		catch (InterruptedException e) {
	    	// TODO Auto-generated catch block
	    	LOGGER.error(e.getMessage());
	    	}
		catch (IOException e){//Catch exception if any
			  LOGGER.error("Error: " + e.getMessage());
		}
	 
		
	}


}
