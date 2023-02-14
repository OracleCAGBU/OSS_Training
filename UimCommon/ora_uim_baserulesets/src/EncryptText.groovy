/*==============================================================================================================
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 *
 * This sample file, which has been provided by Oracle Corporation as part of an Oracle® product for use
 * ONLY by licensed users of the product, includes CONFIDENTIAL and PROPRIETARY information of Oracle
 * Corporation.
 *
 * This material is the confidential property of Oracle Corporation or its licensors and may be used,
 * reproduced, stored, or transmitted only in accordance with a valid Oracle license or sublicense
 * agreement.
 * 
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF THE LICENSE
 * AGREEMENT AND LIMITED WARRANTY FURNISHED WITH THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD ORACLE CORPORATION, ITS RELATED COMPANIES AND ITS SUPPLIERS,
 * HARMLESS FROM AND AGAINST ANY CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR DISTRIBUTION
 * OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES ARISING OUT OF OR RESULTING FROM THE USE,
 * MODIFICATION, OR DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR DERIVED FROM THIS
 * SAMPLE SOURCE CODE FILE.
 * =============================================================================================================*/

import oracle.communications.platform.security.impl.JCEEncrypt;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;
import oracle.communications.inventory.api.common.container.ImportInventoryContainer;
import oracle.communications.inventory.api.framework.logging.Log;


log.debug ("", "System Export");
if(checkFileType(importInventoryContainer,log)){
	JCEEncrypt encrypt = new JCEEncrypt();
	BufferedReader br = null;
	try {
	File file = importInventoryContainer.getFile();	
	if(file != null) {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = "";
		String text = new String();
		while( (line = br.readLine())!= null ){
		       text += line;
		}
		log.info ("","Encrypted text is : "+encrypt.encryptObject(text));
	} else {
		br = new BufferedReader(new StringReader(importInventoryContainer.getAttachment().toString()));
		String line = "";
		String text = new String();
		while( (line = br.readLine())!= null ){
			text += line;
		}
		log.info ("","Encrypted text is : "+encrypt.encryptObject(text));
	}
	
	} catch(Exception exception) {
	throw exception;
	} finally {
	br.close();
	}
}


def boolean checkFileType(ImportInventoryContainer container,Log log)
{
	String fileName =container.getUploadedFilename();
	if(fileName==null){
		log.error("import.noUpload",".txt" );
		return false;
	}else if(!fileName.contains(".txt")){
		log.error("import.fileUploadTextType");
		return false;
	}
	return true;
}




