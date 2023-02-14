

/*
 This ruleset exports database entities into XML format.  The downloadable export file will
 be a binary zip file suitable for import using the SYSTEM IMPORT ruleset.  Input into this
 rule is a config file containing two sections:
 Queries in the form:
 <ClassName>#<QueryString> 
 The following are two sample queries:
 oracle.communications.inventory.api.entity.TelephoneNumberSpecification#o.name LIKE 'Sample'
 oracle.communications.inventory.api.entity.TelephoneNumberSpecification#o.lastModifiedUser='inventory'
 And instructions in the form:
 commitSize=<NumericValue> 
 duplicateAction=<ActionValue> 
 relationshipsToInclude=<IncludeValue>
 Where <NumericValue> is any valid number greater than 1 and up to a reasonable export size.
 And <ActionValue> is equal to Error, Update, Ignore
 And <IncludeValue> is equal to None, Meta, Data, All
 The following are the contents of a sample export config that will pull all Specifications
 whose name begins with 'Sample' and where the maximum export size is set at 1000 entities.
 If any duplicates are found in the target store an error is raised and no action is taken. 
 Relationships to metadata such as related specs, extension points, rulesets et. al.  
 are exported as well.
 oracle.communications.inventory.api.entity.Specification#o.name LIKE 'Sample'
 duplicateAction=Error
 relationshipsToInclude=Meta
 */
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import oracle.communications.inventory.api.common.XmlExtractor;
import oracle.communications.inventory.api.common.container.ImportInventoryContainer;
import oracle.communications.inventory.api.common.container.ImportInventoryResult;
import oracle.communications.inventory.api.common.impl.XmlExtractorImpl;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.impl.FeedbackProviderImpl;
import oracle.communications.platform.persistence.PersistenceHelper;


log.debug ("", "System Export");
if(checkFileType(importInventoryContainer,log)){
	ImportInventoryResult result = exportToXml( importInventoryContainer, log );
	return result;
}


def ImportInventoryResult exportToXml( ImportInventoryContainer container, Log log)
{
	ImportInventoryResult result = null;
	UserTransaction ut = null;
	try
	{
		ut = PersistenceHelper.makePersistenceManager().getTransaction();
		ut.begin();
		XmlExtractor extractor = new XmlExtractorImpl( );
		result = extractor.performExport( container );
		if (FeedbackProviderImpl.hasErrors()){
			ut.rollback();
		}else{
			ut.commit();
		}
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
		log.error("import.noUpload",".txt" );
		return false;
	}else if(!fileName.contains(".txt")){
		log.error("import.fileUploadTextType");
		return false;
	}
	return true;
}

