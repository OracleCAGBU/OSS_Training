/*
 This ruleset takes a input file(config.txt) and performs the following on the input file.
 1.  Creates an instance of telephone number if it does not exists in the system. 
 2.  Creates an instance of logical device account if it does not exists in the system. 
 3.  Creates an instance of equipment if it does not exists in the system. 
 4.  Validates and creates a custom involvement between the telephone number and logical device account. 
 5.  Validates and creates a custom involvement between the logical device account and equipment. 
 The format of the input file, config.txt, file should be as follows:
 TN,LDA,Equipment 
 <TelephoneNumber id>,<Logical device account id>,<Equipment id>
 Before executing the ruleset the following should be replaced in config.txt file.
 <TelephoneNumber id> is the ID (Existing/New) for telephone number.
 <Logical device account id> is the ID( Existing/New) for Logical device account.
 <Equipment id>  is the ID (Existing/New) for Equipment
 Steps to execute the ruleset:
 1.  Create Telephone number, Logical device account & Equipmentspecifications in SCE and 
 install the cartridge into UIM. 
 2.  Modify the hardcoded spec names(SampleEquipmentSpec, SampleLDASpec & SampleTelephoneNumberSpec) 
 in the rule set to the above created spec names. 
 3.  Install the baserulset cartridge into UIM application. 
 4.  Click on the "Import inventory" link in left navigation bar. 
 5.  Select the IMPORT_INVENTORY ruleset and browse for the input file. 
 6.  Click on the proceed.   
 Result: A custom involvement will be created between the supplied Telephone 
 number and Logical device account, Logical device account and equipment. 
 */

import javax.transaction.UserTransaction;
import oracle.communications.inventory.api.common.AttachmentManager;
import oracle.communications.inventory.api.common.CustomInvolvementSearchCriteria;
import oracle.communications.inventory.api.common.container.ImportInventoryContainer;
import oracle.communications.inventory.api.common.container.ImportInventoryResult;
import oracle.communications.inventory.api.entity.*;
import oracle.communications.inventory.api.entity.common.CustomInvolvement;
import oracle.communications.inventory.api.equipment.EquipmentManager;
import oracle.communications.inventory.api.equipment.EquipmentSearchCriteria;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.logicaldevice.account.LogicalDeviceAccountManager;
import oracle.communications.inventory.api.logicaldevice.account.LogicalDeviceAccountSearchCriteria;
import oracle.communications.inventory.api.number.TelephoneNumberManager;
import oracle.communications.inventory.api.number.TelephoneNumberSearchCriteria;
import oracle.communications.inventory.api.number.container.TelephoneNumberData;
import oracle.communications.inventory.api.number.container.TelephoneNumberResult;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;
import com.opencsv.CSVReader;


log.debug ("", "Import Inventory");
if(checkFileType(importInventoryContainer,log)){
	ImportInventoryResult result = importInventoryAttachment(importInventoryContainer.getAttachment(), log);
	return result;
}


def boolean validateAttachment(AttachmentManager am, Persistent from, Persistent to) {
	boolean ok = false;
	if (from == null || to == null) return false;
	CustomInvolvementSearchCriteria criteria = am.makeCustomInvolvementSearchCriteria();
	AttachmentType[] atArray = [AttachmentType.PRECONFIGURED];
	criteria.setAttachmentType(atArray);
	criteria.setFromEntity(from);
	criteria.setRange( 0, 1 );
	try {
		List col = am.findCustomInvolvement( criteria );
		ok = col.size() <= 0;
	} catch(Exception e) {
		ok = false;
	}
	if (ok) {
		criteria.setToEntity(to);
		try {
			Collection col = am.findCustomInvolvement( criteria );
			ok = col.size() <= 0;
		} catch(Exception e) {
			ok = false;
		}
	}
	return ok;
}

def Equipment getSIM(String sim, Log log) {
	Equipment returnEquipment = null;
	Equipment equipment =null;
	Finder f=null;
	EquipmentSpecification eqpSpec=null;
	boolean found = false;
	EquipmentManager manager = PersistenceHelper.makeEquipmentManager();
	EquipmentSearchCriteria criteria = manager.makeEquipmentSearchCriteria();
	CriteriaItem id = criteria.makeCriteriaItem();
	id.setValue(sim);
	id.setOperator(CriteriaOperator.EQUALS);
	criteria.setId(id);
	equipment = manager.makeEquipment();
	equipment.setId(sim);
	try {
		Collection<Equipment> equipList = manager.findEquipment(criteria);
		if (equipList != null && !equipList.isEmpty()) {
			found = true;
			returnEquipment = equipList.iterator().next();
			log.debug("","Found existing equipment="+sim);
		}
	} catch(Exception e) {
		found = false;
	}
	if (!found) {
		UserTransaction ut = null;
		try {
			ut = PersistenceHelper.makePersistenceManager().getTransaction();
			ut.begin();

			Collection<Specification> specList =f.findByName(Specification.class, "SampleEquipmentSpec");
			eqpSpec=(EquipmentSpecification)specList.iterator().next();
			equipment.setSpecification(eqpSpec);
			Collection<Equipment> list = new ArrayList<Equipment>();
			list.add(equipment);
			manager.createEquipment(list);
			ut.commit();
			log.debug("","Created new Equipment="+sim);
			Collection<Equipment> equipList = manager.findEquipment(criteria);
			if (equipList != null && !equipList.isEmpty()) {
				found = true;
				returnEquipment = equipList.iterator().next();
			}
		} catch (Throwable t) {
			if (ut != null) {
				try {
					found = false;
					ut.rollback();
				} catch (Exception e){}
			}
			try {
				log.exception("", new Exception(), "getSIM " + t.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		finally {
		}
		if (!found) {
			returnEquipment = null;
		}
	}
	return returnEquipment;
}

def TelephoneNumber getTN(String tn, Log log) {
	TelephoneNumber telephoneNumber = null;
		
	TelephoneNumberManager tnMaintainer = PersistenceHelper.makeTelephoneNumberManager();
	List l = new ArrayList();
	TelephoneNumberSearchCriteria tnCriteria = tnMaintainer.makeTelephoneNumberSearchCriteria();
	CriteriaItem rangeFromCi = tnCriteria.makeCriteriaItem();
	rangeFromCi.setName("id");
	rangeFromCi.setOperator(CriteriaOperator.EQUALS);
	rangeFromCi.setValue(tn);
	l.add(rangeFromCi);
	tnCriteria.addCriteriaItems(l);

	List entity = null;
	TelephoneNumberManager tnManager=null;
	try {
		tnManager = PersistenceHelper.makeTelephoneNumberManager();
		entity=tnManager.findTelephoneNumbers(tnCriteria);
	} catch(Exception e) {
		e.printStackTrace();
	}
	if(entity != null && entity.size() > 0) {
		log.debug("","found existing tn for id = " + tn);
		telephoneNumber = (TelephoneNumber)entity.toArray()[0];
	} else {
		UserTransaction ut = null;
		ut = PersistenceHelper.makePersistenceManager().getTransaction();
		ut.begin();
		log.debug("","creating new tn for id = " + tn);
		TelephoneNumberResult tnResult=null;
		try {
			TelephoneNumberData tnData =  new TelephoneNumberData();
			tnData.setRangeFrom(tn);
			tnData.setTelephoneNumberSpecName("SampleTelephoneNumberSpec");
			tnResult = tnManager.createTelephoneNumbers(tnData);
			ut.commit();
		} catch (Exception e) {
			if (ut != null) {
				try {
					found = false;
					ut.rollback();
				} catch (Exception e1){}
			}
			e.printStackTrace();
		}
		if (tnResult != null) {
			int tnCount = tnResult.getNumberOfCreatedTelephoneNumbers();
			if (tnCount > 0) {
				oracle.communications.inventory.api.number.container.TelephoneNumber[] tns = tnResult.getTelephoneNumbers();
				if (tns!= null) {
					try {
						String[] stringArray = [
							tns[0].getUnformattedPhoneNumber()
						];
						entity = tnManager.findTelephoneNumbers(stringArray,false);
						telephoneNumber = (TelephoneNumber)entity.toArray()[0];
					} catch(Exception e) {
						log.debug("","unable to find tn");
					}
					log.debug("","returned 1 tn!");
				} else {
					log.debug("","although 1 was created, nothing was returned!");
				}
			}
		} else {
			log.debug("","the TN result was null!!!");
		}
	}
	return telephoneNumber;
}

def LogicalDeviceAccount getPSID(String psid, Log log)  {
	LogicalDeviceAccountManager mgr = PersistenceHelper.makeLogicalDeviceAccountManager();
	LogicalDeviceAccount account = null;
	boolean found = false;
	try {
		LogicalDeviceAccountSearchCriteria criteria = mgr.makeLogicalDeviceAccountSearchCriteria();
		CriteriaItem item = criteria.makeCriteriaItem();
		item.setName("id");
		item.setOperator(CriteriaOperator.EQUALS);
		item.setValue(psid);
		criteria.setId(item);
		Collection accounts = mgr.findLogicalDeviceAccounts(criteria);
		if (accounts != null && accounts.size() > 0) {
			account = (LogicalDeviceAccount)accounts.toArray()[0];
			found = true;
		}
	}
	catch (Throwable t) {
		found = false;
	}
	if (!found) {
		UserTransaction ut = null;
		try {
			List specs = new ArrayList(mgr.getAllLogicalDeviceAccountSpecifications());
			LogicalDeviceAccountSpecification nes = null;
			for(int i=0;i<specs.size();i++) {
				LogicalDeviceAccountSpecification spec = (LogicalDeviceAccountSpecification)specs.get(i);
				if ("SampleLDASpec".equals(spec.getName())) {
					nes = spec;
					break;
				}
			}
			ut = PersistenceHelper.makePersistenceManager().getTransaction();
			ut.begin();
			account = mgr.makeLogicalDeviceAccount();
			account.setAdminState(InventoryState.INSTALLED);
			account.setId(psid);
			account.setSpecification(nes);
			List<LogicalDeviceAccount> ldaList = new ArrayList<LogicalDeviceAccount> (1);
			ldaList.add(account);
			List<LogicalDeviceAccount> list = mgr.createLogicalDeviceAccounts(ldaList);
			if(!list.isEmpty()){
				account = list.get(0);
			}
			ut.commit();
		} catch (Throwable t) {
			if (ut != null) {
				try {
					found = false;
					ut.rollback();
				} catch (Exception e){}
			}
			log.exception("", new Exception(), "getPSID " + t.getLocalizedMessage());
		}
		finally {
		}
	}
	return account;
}



def boolean isEmpty(String string) {
	if (string == null || "".equals(string))
		return true;
	return false;
}



def ImportInventoryResult importInventoryAttachment(Object attachment, Log log) {
	int importedCount = 0;

	try {
		List records = null; //r.readAll();
		BufferedReader checker = new BufferedReader(new StringReader(attachment.toString()));
		String importSpec = checker.readLine();
		checker.close();
		String[] fields = importSpec.split(",");
		int tnIndex = -1, simIndex = -1, psidIndex = -1;
		for(int i=0;i<fields.length;i++) {
			if ("TN".equals(fields[i])) {
				tnIndex = i;
			} else if ("Equipment".equals(fields[i])) {
				simIndex = i;
			} else if ("LDA".equals(fields[i])) {
				psidIndex = i;
			}
		}
		records = new ArrayList();
		if (fields.length > 1) {

			// The uploaded file is saved using a charset.  This is usually the default charset of the client that
			// creates this file.  This charset must be used to decode the String returned by
			// ImportInventoryContainer.getAttachment().
			// Use the following to decode the ImportInventoryContainer.getAttachment() String:
			//     CSVReader cvs = new CSVReader(new StringReader(new String(container.getAttachment().getBytes(), "<uploaded file charset>")));
			// If charset is GB2312,
			//     CSVReader cvs = new CSVReader(new StringReader(new String(container.getAttachment().getBytes(), "GB2312")));
			// If charset is UTF-8,
			//     CSVReader cvs = new CSVReader(new StringReader(new String(container.getAttachment().getBytes(), "UTF-8")));
			// So, you must advise user to save file in a pre-defined charset in order to decode the file correctly.
			CSVReader cvs = new CSVReader(new StringReader(attachment.toString()));

			records = cvs.readAll();
		} else {
			BufferedReader lineReader = new BufferedReader(new StringReader(attachment.toString()));
			String line = null;
			while((line = lineReader.readLine()) != null) {
				String [] stringArray = [line];
				records.add(stringArray);
			}
		}

		//System.out.println("records.size()=" + records.size());
		// start at 2nd record, since 1st is format of file
		for (int r=1;r<records.size();r++) {
			//System.out.println("record=" + r);
			String[] record = (String[])records.get(r);
			if (record == null) continue;
			// calls to apis
			String tn = null;
			if (tnIndex >= 0 && record.length > tnIndex) tn = record[tnIndex];
			String sim = null;
			if (simIndex >= 0 && record.length > simIndex) sim = record[simIndex];
			String psid = null;
			if (psidIndex >= 0 && record.length > psidIndex) psid = record[psidIndex];
			if (isEmpty(tn) && isEmpty(sim) && isEmpty(psid))
				continue;

			TelephoneNumber telephoneNumber = null;
			if (tnIndex >= 0) {
				telephoneNumber = getTN(tn,log);
			}
			LogicalDeviceAccount account = null;
			if (psidIndex >= 0) account = getPSID(psid,log);
			Equipment equipment = null;
			if (simIndex >= 0) equipment = getSIM(sim,log);
			importedCount ++;
			UserTransaction ut = null;
			Finder f = PersistenceHelper.makeFinder();
			try {
				ut = PersistenceHelper.makePersistenceManager().getTransaction();
				ut.begin();
				Collection<CustomInvolvementSpecification> customInvolvementSpecs =
                f.findByName(CustomInvolvementSpecification.class,
                             "PreconfigureSpec");
                if(customInvolvementSpecs == null || customInvolvementSpecs.size == 0) {
                	log.error("custom.involvementSpecificationNotFound","PreconfigureSpec" );
                	continue;
                }	
                CustomInvolvementSpecification customInvolvementSpec = customInvolvementSpecs.iterator().next();              
				AttachmentManager am = PersistenceHelper.makeAttachmentManager();
				boolean tnToPsidAttach = validateAttachment(am,telephoneNumber,account);
				boolean psidToEquipmentAttach = validateAttachment(am,account,equipment);
				Collection <CustomInvolvement> list = new ArrayList <CustomInvolvement> (2);
				if (tnToPsidAttach) {
					CustomInvolvement tnToPsid = am.makeCustomInvolvement();
					tnToPsid.setAttachmentType(AttachmentType.PRECONFIGURED);
					tnToPsid.setFromEntity(telephoneNumber);
					tnToPsid.setToEntity(account);
					tnToPsid.setSpecification(customInvolvementSpec);
					list.add (tnToPsid);					
				}
				if (psidToEquipmentAttach) {
					CustomInvolvement psidToEquipment = am.makeCustomInvolvement();
					psidToEquipment.setAttachmentType(AttachmentType.PRECONFIGURED);
					psidToEquipment.setFromEntity(account);
					psidToEquipment.setToEntity(equipment);
					psidToEquipment.setSpecification(customInvolvementSpec);
					list.add (psidToEquipment);					
				}
				am.createCustomInvolvement(list);
				if (tnToPsidAttach || psidToEquipmentAttach) {
					ut.commit();					
				} else {
					ut.rollback();
				}
			}
			catch (Throwable t) {
				if (ut != null) {
					try {
						ut.rollback();
					} catch (Exception e){e.printStackTrace();}
				}
				log.exception("", new Exception(), "importInventory " + t.getLocalizedMessage());
			}
			finally {
			}

			// validate & make associations
		}
	} catch(IOException io) {
		log.error("",io,"Error");
	}
	ImportInventoryResult result = new ImportInventoryResult();
	result.setImportedCount(importedCount);


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


