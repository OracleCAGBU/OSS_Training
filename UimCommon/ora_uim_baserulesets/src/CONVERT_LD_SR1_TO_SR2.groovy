//-------------------------------------------------------------------------------------------------
// IMPORTS
//-------------------------------------------------------------------------------------------------

import javax.transaction.UserTransaction;

import oracle.communications.inventory.api.common.container.ImportInventoryContainer;
import oracle.communications.inventory.api.common.container.ImportInventoryResult;
import oracle.communications.inventory.api.entity.DeviceInterface;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.logicaldevice.impl.LogicalDeviceCreator;
import oracle.communications.inventory.api.logicaldevice.impl.LogicalDeviceUpdater;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import com.opencsv.CSVReader;

//-------------------------------------------------------------------------------------------------
// GLOBALS
//-------------------------------------------------------------------------------------------------

log.debug("", "CONVERT LD SR1 TO SR2 rule");
// set the following boolean variable to false if delete is to be truly executed
if(checkFileType(importInventoryContainer,log)){
	boolean displayOnly = true;
	ImportInventoryResult result = convertLogicalDevices(importInventoryContainer.getAttachment(), log, displayOnly);
	log.debug("", "Returned from CONVERT LD rule");
	//log.info("", new String [] {"Executed and converted " + result.getImportedCount() + " Logical Device Specifications"});
	//drools.insert(result);
	return result;
}

//-------------------------------------------------------------------------------------------------
// FUNCTIONS
//-------------------------------------------------------------------------------------------------

def ImportInventoryResult convertLogicalDevices(Object attachment, Log log, boolean displayOnly) {
	int specCount = 0;
	int failedCount = 0;
	UserTransaction ut = null;
	try {
		List records = null;
		CSVReader cvs = new CSVReader(new StringReader(attachment.toString()), '#'.toCharacter());
		records = cvs.readAll();

		List ldList = new ArrayList();
		for (int r=0; r<records.size(); r++) {
			String[] record = (String[]) records.get(r);
			ldList.add(record[0]);
		}

		if (Utils.isEmpty(ldList)) {
			log.info("", "No Logical Device Specification found");
		}

		Iterator ldSpecListItr = ldList.iterator();
		while (ldSpecListItr.hasNext()) {
			try {
				ut = PersistenceHelper.makePersistenceManager().getTransaction();
				ut.begin();

				String specName = (String) ldSpecListItr.next();

				log.debug("", "Processing the spec " + specName);

				Finder f = null;
				f = PersistenceHelper.makeFinder();
				LogicalDeviceManager ldMgr = PersistenceHelper.makeLogicalDeviceManager();
				Collection<LogicalDeviceSpecification> specList = f.findByName(LogicalDeviceSpecification.class, specName);
				if (specList.size() > 0) {
					LogicalDeviceSearchCriteria criteria = ldMgr.makeLogicalDeviceSearchCriteria();
					criteria.setLogicalDeviceSpecs(specList.toArray(new LogicalDeviceSpecification[specList.size()]));
					Collection<LogicalDevice> ldListResult = ldMgr.findLogicalDevice(criteria);
					log.debug("", "  Found " + ldListResult.size() + " logical devices");
					LogicalDeviceCreator ldc = new LogicalDeviceCreator();
					LogicalDeviceUpdater ldu = new LogicalDeviceUpdater();
					int i = 1;
					for (LogicalDevice ld : ldListResult) {
						log.debug("", "    " + i + ") Converting Logical device " + ld.getId());
						Set <DeviceInterface> deviceInterfaceList = ld.getAllDeviceInterfaces();
						int j = 1;
						for ( DeviceInterface deviceInterface : deviceInterfaceList ) {
							log.debug("", "      " + j + ") Converting Device Interface " + deviceInterface.getId());
							/*if (deviceInterface.getRateCode() != null ) {
							 log.debug("", new String [] {"             Device Interface Rate Code BEFORE = " + deviceInterface.getRateCode().getName()});
							 }
							 else {
							 log.debug("", new String [] {"             Device Interface Rate Code BEFORE = -"});
							 }
							 */
							ldc.setRateCodeOnDeviceInterface(deviceInterface);
							//log.debug("", new String [] {"             Device Interface Rate Code AFTER = " + deviceInterface.getRateCode().getName()});
							j++;
						}
						i++;
					}
				}
				f.reset();
				specCount++;
				ut.commit();
			}
			catch (Throwable t) {
				failedCount++;
				if (ut != null) {
					try {
						ut.rollback();
					} catch (Exception e){}
				}
				log.exception("", new Exception(), "convert LD from SR1 to SR2 error" + t.getLocalizedMessage());
			}
			finally {
				log.debug("", "In Finally");
			}
		}
	} catch(Exception e) {
		log.exception("", e, "Error " + e.getLocalizedMessage());
	}
	log.debug("", "Before return");
	ImportInventoryResult result = new ImportInventoryResult();
	result.setImportedCount(specCount);
	return result;
}

def boolean checkFileType(ImportInventoryContainer container,Log log)
{
	String fileName =container.getUploadedFilename();
	if(fileName==null){
		log.error("import.noUpload", ".txt" );
		return false;
	}else if(!fileName.contains(".txt")){
		log.error("import.fileUploadTextType");
		return false;
	}
	return true;
}

