

/*
 This ruleset imports XML formatted data into the system.
 For C4 this will consist of a zip bundle that was previously
 extracted using the SYSTEM EXPORT rule.
 Last Updated 8-30-2007.
 */
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import oracle.communications.inventory.api.common.XmlIngestor;
import oracle.communications.inventory.api.common.container.ImportInventoryContainer;
import oracle.communications.inventory.api.common.container.ImportInventoryResult;
import oracle.communications.inventory.api.common.impl.XmlIngestorImpl;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.platform.persistence.PersistenceHelper;


log.debug ("", "System Import");
if(checkFileType(importInventoryContainer,log)){
	ImportInventoryResult result = importFromXml( importInventoryContainer, log );
	return result;
}


def ImportInventoryResult importFromXml( ImportInventoryContainer container, Log log)
{
	ImportInventoryResult result = null;
	UserTransaction ut = null;
	try
	{
		ut = PersistenceHelper.makePersistenceManager().getTransaction();
		ut.begin();
		XmlIngestor ingestor = new XmlIngestorImpl( );
		result = ingestor.performImport( container );
		if (FeedbackProviderImpl.hasErrors()){
			ut.rollback();
		}else{
			ut.commit();
		}
	}
	catch (FileNotFoundException e) {
		if (ut != null) {
			try {
				ut.rollback();
			} catch (SystemException f) { }
		}
		log.error("");
	}
	catch (Exception e) {
		if (ut != null) {
			try {
				ut.rollback();
			} catch (SystemException f) { }
		}
		log.error("import.generalError", e, "Error" );
	}
	finally {
	}
	return result;
}

def boolean checkFileType(ImportInventoryContainer container,Log log)
{
	String fileName =container.getUploadedFilename();
	if(fileName==null){
		log.error("import.noUpload", ".zip" );
		return false;
	}else if(!fileName.contains(".zip")){
		log.error("import.fileUploadZipType");
		return false;
	}
	return true;
}

