package com.hcsc.workflow.util;


import java.util.ArrayList;
import java.util.Hashtable;


import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import com.interwoven.cssdk.access.CSAuthorizationException;
import com.interwoven.cssdk.access.CSExpiredSessionException;
import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.common.CSIterator;
import com.interwoven.cssdk.common.CSObjectNotFoundException;
import com.interwoven.cssdk.common.CSRemoteException;
import com.interwoven.cssdk.common.query.CSComparisonConstraint;
import com.interwoven.cssdk.common.query.CSOperator;
import com.interwoven.cssdk.common.query.CSQuery;
import com.interwoven.cssdk.common.query.CSSelector;
import com.interwoven.cssdk.common.query.CSSelectorAttribute;
import com.interwoven.cssdk.filesys.CSAreaRelativePath;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSVPath;
import com.interwoven.cssdk.workflow.CSExternalTask;
import com.interwoven.cssdk.workflow.CSURLExternalTask;



public class attachDepend implements CSURLExternalTask{

	/**
	 * @param args
	 */
	
	private static final Logger LOGGER = Logger.getLogger(attachDepend.class);
	
	public void execute(CSClient client, CSExternalTask task, Hashtable params) {
		
		//public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			String attachDependVal = task.getVariable("attachDependencies");
			String vpath = task.getArea().getVPath().toString();
			
			
			LOGGER.debug("Attach Depend Value " + attachDependVal);
			
			if(attachDependVal.equals("yes")){
				
				
				task.chooseTransition("Start Attach Dependencies", "Start Attach Dependencies");
				
			}
			else if(attachDependVal.equals("no")){
				
				

			
			CSAreaRelativePath[] filePaths = task.getFiles();
			
			for(CSAreaRelativePath filePath: filePaths){
				
				LOGGER.debug("File " + filePath.getName());
				
				CSFile file = client.getFile(new CSVPath(vpath + "/" + filePath));
				
				LOGGER.debug("File Type " + file.getKind());
				
				if(file.getKind()== 2){
					
					LOGGER.debug("I am a directory");
					
					CSDir directory = (CSDir)file;
					
					CSSelector attributeSelector = new CSSelector(null);
				    attributeSelector.addAttribute(new CSSelectorAttribute("vpath", null, null, -1));
				    CSComparisonConstraint fileKindConstraint = new CSComparisonConstraint("kind", null, null, null, null, CSOperator.EQUALS, String.valueOf(1));
				    CSQuery csQuery = new CSQuery(attributeSelector, fileKindConstraint, null);
				    CSIterator directoryIterator = directory.getFiles(csQuery.toString(), 0, -1, true);
				    
				    LOGGER.debug(directoryIterator.getTotalSize());
				    
				    CSAreaRelativePath[] paths = new CSAreaRelativePath[(int) directoryIterator.getTotalSize()];
				    ArrayList<CSFile> dirFilePaths = new ArrayList<CSFile>();
				    
				    LOGGER.debug("Size before while loop " + dirFilePaths.size());
				    
				    while(directoryIterator.hasNext()){
				    	
				    	CSFile dirFile = (CSFile)directoryIterator.next();
				    	
				    	LOGGER.debug("File Name " + dirFile.getName());
				    	
				    	
				    	dirFilePaths.add(dirFile);
				    	
				    	LOGGER.debug("Size in while loop "+ dirFilePaths.size());
				    	
				    	LOGGER.debug("1");
				    	
				    	//dirFilePaths.add(dirFile.getVPath().getAreaRelativePath());
				    	int i=0;
				    	
				    	for (CSFile dirFilePath : dirFilePaths)
				    	    {
				    	      CSAreaRelativePath path = dirFilePath.getVPath().getAreaRelativePath();
				    	      paths[(i++)] = path;
				    	      
				    	    }
				    	
				    	
				    	
				    	
				    	
				    	
				    	
				    }
					
			    	LOGGER.debug("Before attachfiles");

				    
				    task.attachFiles(paths);
				    
				    LOGGER.debug("After attachfiles");
				}
				
			}
			task.chooseTransition("Start Attach Deployment Target", "Start Attach Deployment Target");
			
			}
			
			//System.out.println(task.getFiles().toString());
			
			
			
			
			
		} catch (CSAuthorizationException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		} catch (CSRemoteException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		} catch (CSObjectNotFoundException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		} catch (CSExpiredSessionException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		} catch (CSException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		}
	
		
		
	}
	

}
