package com.broadband.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	public static int ROW_LOCK_PERIOD = 30; //seconds
	
	/* CUSTOM SEQUENCE */
	public static final String SEQUENCE_RESERVATION = "RESERVATION_SEQ";
	public static final String SEQUENCE_SERVICE = "Sequence_Service"; 
	public static final String CHAR_ENCODING = "UTF-8";
	
	/* DATE FORMAT */
	// public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	
	// public static final String TFLSYSTEMCONFIGPROPERTIES = "TFLSystemConfig.properties";
	
	/* DEBUG */	
	public static final String SERVICE_DETAILS = "Service Details ";
	public static final String COLON_DELIMITTER = ":";
	public static final String SPACE_DELIMITTER = " ";
	public static final String EXCEPTION_DELIMITTER = "Exception :";
	
	
	
	/* SERVICE ACTIONS */
	public static final String SA_CREATE = "Create";
	public static final String SA_CHANGE = "Change";
	public static final String SA_SUSPEND = "Suspend";
	public static final String SA_RESUME = "Resume";
	public static final String SA_DISCONNECT = "Disconnect";
	public static final String SA_INVALID_ACTION = "invalid_action";
	public static final String SA_NULL_ACTION = "null_action";
	public static final String SA_NO_ACTION = "no_action";
	public static final String SA_CHANGECPE = "ChangeCPE";
	public static final String SA_CHANGEUPLOADSPEED = "ChangeUploadSpeed";

	public static final String SA_CHANGEMEDIUM = "changeMedium";
	public static final String SA_CHANGEPAYTYPE = "PrepaidToPostpaid";
	public static final String SA_CHANGEUSERCREDENTIALS = "changeUserCredentials";
	public static final String SA_CHANGEOWNERSHIP = "changeOwnership";
	public static final String SA_CHANGETECHNOLOGY = "changeTechnology";
	public static final String SA_CHANGEUSERNAME = "changeUsername";
	public static final String SA_CHANGEMAPPING = "changeMapping";
	public static final String SA_RELOCATION_INTERNAL = "relocationInternal";
	public static final String SA_RELOCATION_EXTERNAL = "relocationExternal";
	public static final String SA_CHANGE_VAS = "changeVAS";
	public static final String SA_ADD_EXTENSION = "addExtension";
	public static final String SA_DELETE_EXTENSION = "deleteExtension";
	public static final String SA_RELOCATE_EXTENSION = "relocateExtension";	
	public static final String SA_CHANGE_SIM = "changeSIM";
	public static final String SA_CHANGE_CPE = "changeCPE";
	public static final String SA_CHANGEPILOT = "changePilot";
	public static final String SA_CHANGE_BANDWIDTH = "changeBandwidth";
	public static final String SA_CHANGE_CHANNELS = "changeChannels";
	public static final String SA_CHANGE_PLAN= "changePlanName";
	public static final String SA_CHANGE_SPEEDNONCOPPER = "changeSpeedNonCopper";
	public static final String SA_CHANGE_PLANCOPPER = "changePlanNameCopper";
	public static final String SA_CHANGE_MISC= "changeMisc";
	public static final String SA_STEPBARRING = "stepBarring"; 
	public static final String SA_OCB = "ocb"; 
	public static final String SA_TOS = "tos"; 
	public static final String SA_SUSPENDWITHCONFIGURATION = "suspendWithConfiguration";
	public static final String SA_RESUMEWITHCONFIGURATION = "resumeWithConfiguration";
	public static final String SA_ADDDIDBLOCK = "addDIDBlock";
	public static final String SA_REMOVEDIDBLOCK = "removeDIDBlock";
	public static final String SA_ADDAUXILLARYNUMBER = "addAuxillaryNumber";
	public static final String SA_REMOVEAUXILLARYNUMBER = "removeAuxillaryNumber";
	public static final String SA_CHANGE_EMAIL = "changeEmail";
	public static final String SA_CHANGE_DOMAIN = "changeDomain";
	public static final String SA_CHANGE_EMAIL_DOMAIN = "changeEmailDomain";

	// Configuration Item Names
	public static final String RFS_CI = "RFS_CI";
	
	/* PARTY SPECIFICATIONS */
	public static final String SPEC_PARTY_CUSTOMER = "Customer";
	public static final String PARAM_PARTY = "Party";


	/* COPPER DEVICES SPECIFICATION */
	public static final String SPEC_DP = "DP";
	public static final String SPEC_TB = "TB";
	public static final String SPEC_NETWORK_PROFILE = "Network_Profile";
	public static final String SPEC_CVLAN = "CVLAN";
	public static final String SPEC_SVLAN = "SVLAN";
	
	public static final String SPEC_CAB = "Cabinet";
	public static final String SPEC_CAB_D_PORT = "CAB_DPort";
	public static final String SPEC_CAB_E_PORT = "CAB_EPort";

	public static final String SPEC_MDF = "MDF";
	public static final String SPEC_MDF_PORT = "MDF_Port";

	public static final String SPEC_MSAN = "MSAN";
	public static final String SPEC_ISAM = "ISAM";
	public static final String SPEC_DSLAM = "DSLAM";
	public static final String SPEC_UMG = "UMG";
	
	/* Fibre DEVICES  SPECIFICATION */
	public static final String SPEC_ODF = "ODF";
	public static final String SPEC_SPLICING_BOARD = "SplicingBoard";
	public static final String SPEC_ODF_PORT = "ODF_Port";
	public static final String SPEC_FDT_PORT = "FDT_Port";
	

	public static final String SPEC_AGG = "AGG";
	
	
	/* RESOURCE SPECIFICATION */
	public static final String SPEC_FIXEDLINENUMBER = "FixedLineNumber";
	public static final String SPEC_DIDBLOCK = "DIDBlock";
	public static final String SPEC_MSISDN = "MSISDN";
	public static final String SPEC_LTE_SIM= "LTE_SIM";
	public static final String SPEC_CPE = "CPE";
	public static final String SPEC_ENTERPRISE_CPE = "Enterprise_CPE";
	public static final String SPEC_SHORTCODE = "ShortCode";
	public static final String SPEC_PREMIUMNUMBER = "PremiumNumber";
	public static final String SPEC_SYSTEMCONFIG = "SystemConfig";
	public static final String SPEC_PORTRESERVATION= "PortReservation";
	
	
	/* PIPE SPECIFICATION */
	public static final String SPEC_DEVICE_CABLE = "Device_Cable";
	public static final String SPEC_DEVICE_FOC = "Device_FOC";
	
	/* CFS SPECIFICATION */
	public static final String SPEC_FIXEDVOICE_CFS = "FixedVoice_CFS";
	public static final String SPEC_FIXEDVOICE_RFS = "FixedVoice_RFS";
	public static final String SPEC_PREMIUMNUMBER_CFS = "PremiumNumber_CFS";
	public static final String SPEC_PREMIUMNUMBER_RFS = "PremiumNumber_RFS";
	public static final String SPEC_ISDN_CFS = "ISDN_CFS";
	public static final String SPEC_ISDN_RFS = "ISDN_RFS";
	public static final String SPEC_SIPTRUNK_CFS = "SIPTrunk_CFS";
	public static final String SPEC_SIPTRUNK_RFS = "SIPTrunk_RFS";
	public static final String SPEC_BROADBAND_CFS = "Broadband_CFS";
	public static final String SPEC_BROADBAND_CFS_SC = "Broadband_CFS_SC";
	public static final String SPEC_FIXEDVOICE_CFS_SC = "FixedVoice_CFS_SC";
	public static final String SPEC_CLOUDPABX_CFS_SC = "Cloud_PABX_CFS_SC";
	public static final String SPEC_MOBILEBROADBAND_CFS_SC = "MobileBroadband_CFS_SC";
	public static final String SPEC_DIA_CFS_SC = "DIA_CFS_SC";
	public static final String SPEC_SATELLITEDATA_CFS_SC = "SatelliteData_CFS_SC";
	public static final String SPEC_SIPTRUNK_CFS_SC = "SIPTrunk_CFS_SC";
	public static final String SPEC_PABX_CFS_SC = "PABX_CFS_SC";
	public static final String SPEC_ISDN_CFS_SC = "ISDN_CFS_SC";
	public static final String SPEC_IPVPN_CFS_SC = "IPVPN_CFS_SC";
	public static final String SPEC_PREMIUMNUMBER_CFS_SC = "PremiumNumber_CFS_SC";
	public static final String SPEC_BROADBAND_RFS = "Broadband_RFS";
	public static final String SPEC_MOBILEBROADBAND_CFS = "MobileBroadband_CFS";
	public static final String SPEC_MOBILEBROADBAND_RFS = "MobileBroadband_RFS";
	public static final String SPEC_SATELLITEVOICE_CFS = "SatelliteVoice_CFS";
	public static final String SPEC_SATELLITEVOICE_RFS = "SatelliteVoice_RFS";
	public static final String SPEC_SATELLITEDATA_CFS = "SatelliteData_CFS";
	public static final String SPEC_SATELLITEDATA_RFS = "SatelliteData_RFS";
	public static final String SPEC_CLOUDPABX_CFS = "Cloud_PABX_CFS";
	public static final String SPEC_CLOUDPABX_RFS = "Cloud_PABX_RFS";
	public static final String SPEC_PABX_CFS = "PABX_CFS";
	public static final String SPEC_PABX_RFS = "PABX_RFS";
	public static final String SPEC_ODX_CFS = "ODX_CFS";
	public static final String SPEC_ODX_RFS = "ODX_RFS";
	public static final String SPEC_IPVPN_CFS = "IPVPN_CFS";
	public static final String SPEC_IPVPN_RFS = "IPVPN_RFS";
	public static final String SPEC_IPLC_CFS = "IPLC_CFS";
	public static final String SPEC_IPLC_RFS = "IPLC_RFS";
	public static final String SPEC_EPL_CFS = "EPL_CFS";
	public static final String SPEC_EPL_RFS = "EPL_RFS"; 	
	public static final String SPEC_DIA_CFS = "DIA_CFS";
	public static final String SPEC_DIA_RFS = "DIA_RFS";
	public static final String SPEC_DIA_RFS_SC = "DIA_RFS_SC";
	public static final String SPEC_WEBHOSTINGDNS_CFS = "WebHosting_DNS_CFS";
	public static final String SPEC_WEBHOSTINGDNS_RFS = "WebHosting_DNS_RFS";
	public static final String SPEC_WEBHOSTINGDNS_CFS_SC = "WebHosting_DNS_CFS_SC";
	public static final String SPEC_WEBHOSTINGDNS_RFS_SC = "WebHosting_DNS_RFS_SC";
	public static final String SPEC_EMAIL_CFS = "Email_CFS";
	public static final String SPEC_EMAIL_RFS = "Email_RFS";
	public static final String SPEC_WIFIADINJECTION_CFS = "WifiAdInjection_CFS";
	public static final String SPEC_WIFIADINJECTION_RFS = "WifiAdInjection_RFS";
	public static final String SPEC_TFL_ORDER = "TFL_Order";
	public static final String SPEC_DNS_CFS = "DNS_CFS";
	public static final String SPEC_DNS_RFS = "DNS_RFS";

	
	/* CUSTOM OBJECT SPECIFICATION */
	public static final String SPEC_CARDTYPE = "CardType";
	public static final String SPEC_SERVICETYPE = "ServiceType";
	public static final String SPEC_DEVICESITE = "DeviceSite";
	public static final String SPEC_DOMAINNAME = "DomainName";
	
	/* LOGICAL DEVICE SPECIFICATION */
	public static final String SPEC_FDT = "FDT";
	public static final String SPEC_FAT = "FAT";
	public static final String SPEC_TRAY = "Tray";
	public static final String SPEC_CARD = "Card";
	
	/* DEVICEINTERFACE SPECIFICATION */
	public static final String SPEC_DP_PORT = "DP_Port";
	public static final String SPEC_TB_PORT = "TB_Port";
	public static final String SPEC_DOWNLINK_PORT = "Downlink_Port";
	public static final String SPEC_VIRTUAL_PORT = "Virtual_Port";
	public static final String SPEC_INLET_PORT = "Inlet_Port";
	public static final String SPEC_OUTLET_PORT = "Outlet_Port";
	public static final String SPEC_DOWNLINK = "Downlink";
	public static final String SPEC_UPLINK = "Uplink";
	
	// FixedVoice
	public static final String CI_SUBSCRIBER_PORT = "Subscriber_Port_CI";
	public static final String CI_SERVICE_PORT = "Service_Port_CI";
	public static final String CI_COMBO_PORT = "Combo_Port_CI";
	public static final String CI_AGGREGATION_PORT = "Aggregation_CI";
	public static final String CI_COMBO_PORTS = "Combo_Ports_CI";
	
	/* PLACE SPECIFICATION */
	public static final String SPEC_DEVICEADDRESS = "Device_Address"; //Address
	public static final String SPEC_SERVICEADDRESS = "Service_Address"; //Address
	public static final String SPEC_ADDRESS = "Address"; //Address
	
	/* PLACE Constants */
	public static final String COUNTRY_FIJI = "Fiji"; // Address

	/* DELIMITERS */
	public static final String DELIMITER_DEVICE_PORT = "#";
	public static final String DELIMITER_NAD = "#";
	public static final String DELIMITER_PORT_SEGMENT = "/";
	public static final String DELIMITER_PIPE = ">>";
	public static final String DELIMITER_PIPE_SEPARATOR = "|";
	public static final String DELIMITER_PRIVATE_IPADDRESS_POOL = "#PE2CE";
	public static final String DELIMITER_GLOBALNPE_IPADDRESS_POOL = "GlobalNPE#PE2CE";
	public static final String DELIMITER_MANAGEMENT_IPADDRESS_POOL = "#ManagementIP";
	public static final String DELIMITER_SVLAN = "#SVLAN#";
	public static final String DELIMITER_CVLAN = "#CVLAN#";
	
	
	/* MISC */
	public static final String SPEC_BI = "BI_Order";
	public static final String SPEC_ONTID = "ONTId";
	public static final String CI_RFS_HOLDER = "RFS_CI";
	public static final String CFS = "CFS";
	public static final String RFS = "RFS";
	public static final String SUFFIX_CONFIG_SPEC = "_SC";
	public static final String SIDE_EXCHANGE = "E";
	public static final String SIDE_DISTRIBUTION = "D";
	public static final String SUFFIX_D_PORT = "_D_Port";
	public static final String SUFFIX_E_PORT = "_E_Port";
	public static final String SUFFIX_PANEL = "_Panel";//Used only for Fibre
	public static final String UNDERSCORE = "_";
	public static final String HYPHEN = "-";
	public static final String COMMA = ",";
	public static final String SLASH = "/";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String ANYDPPORTS = "DP";
	public static final String ADD = "Add";
	public static final String DELETE = "Delete";
	public static final String RELOCATE = "Relocate";
	public static final String RELOCATED = "Relocated";
	public static final String LTE = "LTE";
	public static final String GLOBAL_DIA = "Global DIA";
	public static final String GLOBAL_SIP = "Global SIP";
	public static final String SIP = "SIP";
	public static final String BUSINESS = "Business";
	public static final String POSTPAY = "Post-pay";
	public static final String PREPAY = "Pre-pay";
	
	public static final String FAIL = "FAIL";
	public static final String SUCCESS = "SUCCESS";
	public static final String STARTS_WITH = "STARTS_WITH";
	public static final String CONTAINS = "CONTAINS";
	public static final String ENDS_WITH = "ENDS_WITH";
	public static final String EMPTY = "";
	public static final String L2 = "L2";
	public static final String L3 = "L3";
	public static final String BSP_EFTPOS = "BSP EFTPOS";
	public static final String COPPER = "Copper";
	public static final String MICROWAVE = "Microwave";
	public static final String FIBER = "Fibre";
	public static final String VIRTUAL = "Virtual";
	public static final String STANDARD = "Standard";
	public static final String COMMUNITY = "Community";
	public static final String PUBLIC = "Public";
	
	/* COMMON Service PARAMS */
	public static final String PARAM_SERVICE_ACTION = "ServiceAction";
	public static final String PARAM_TYPE = "Type";
	public static final String PARAM_ADDITIONALIPADDRESS = "AdditionalIPAddress";
	public static final String PARAM_IPADDRESS = "IPAddress";
	public static final String PARAM_TFLPROVIDED= "TFLProvided";
	public static final String PARAM_TFLOWNED= "TFLOwned";
	public static final String PARAM_CREDIT_CONTROL_BARRING = "Credit_Control_Action";
	public static final String PARAM_BANDWIDTH = "Bandwidth";
	public static final String PARAM_PLAN_NAME = "PlanName";
	public static final String PARAM_CUSTOMER_NAME = "CustomerName";
	public static final String PARAM_MISC_ORDER_TYPE = "MiscOrderType";
	public static final String PARAM_EMAIL = "Email";
	
	public static final String PARAM_SUFFIX = "Suffix";
	public static final String PARAM_PENAME = "PEName";
	public static final String SERVICE_ID = "ServiceId";
	public static final String ACCOUNT_ID = "AccountId";
	public static final String PARAM_ACCESS_TECHNOLOGY = "AccessTechnology";
	public static final String PARAM_ALIAS_NAME = "Alias_Name";
	public static final String PARAM_TEMPLATE_VERSION = "Template_Version";
	public static final String PARAM_SERVICENUMBER = "ServiceNumber";
	public static final String PARAM_PARENT_SERVICE_ID = "Parent_Service_ID";
	public static final String PARAM_HUB_ID = "Hub_Id";
	public static final String PARAM_SITETYPE = "SiteType";
	public static final String SPOKE = "Spoke";
	public static final String HUB = "Hub";
	public static final String PARAM_LINKTYPE = "LinkType";
	public static final String PARAM_PROGRAMMING_DATE = "ProgrammingDate";
	public static final String PARAM_SERVICE_ACTION_DATE = "ServiceActionDate";
	public static final String PARAM_PARENT_VLAN_IDPlanName = "ParentVLANId";
	public static final String PARAM_VPNTYPE = "VpnType";
	public static final String PARAM_TEMPLATE_NAME = "Template_Name";
	public static final String PARAM_LINKED_SERVICE_ID = "Linked_Service_ID";
	public static final String PARAM_SERVICEPROFILENAME = "ServiceProfileName";
	public static final String PARAM_SPECTRUMPROFILENAME = "SpectrumProfileName";
	public static final String PARAM_NETWORKCUSTOMERID = "Network_CustomerId";
	public static final String PARAM_VLANCUSTOMERID = "VLAN_CustomerId";
	public static final String PARAM_PAYTYPE = "PayType";
	public static final String PARAM_CLASSIFICATION = "Classification";
	public static final String PARAM_NORMAL = "Normal";
	public static final String PARAM_VODAFONE = "VODAFONE";
	public static final String PARAM_FINTEL = "FINTEL";
	
	public static final String PARAM_CUSTOMERSEGMENT= "CustomerSegment";
	public static final String PARAM_MEDIUM = "Medium";
	public static final String PARAM_MIXEDMEDIUM = "MixedMedium";
	public static final String PARAM_TOPOLOGY = "Topology";
	public static final String PARAM_WIRELESS_CPE = "WirelessCPE";
	public static final String PARAM_CUSTOMERTYPE= "CustomerType";
	public static final String PARAM_CARDTYPE= "CardType";
	public static final String PARAM_SLOTNUMBER = "SlotNumber";
	public static final String PARAM_SUBSCRIBER_TELEPHONE_NUMBER = "Subscriber_Telephone_Number";
	public static final String PARAM_SUBSCRIBER_CATEGOIRY = "Subscriber_Category";
	public static final String PARAM_EMERGANCY_CALL_CLASS = "Emergency_Call_Class";
	public static final String PARAM_SERVICE_PROFILE_INDEX1 = "Service_Profile_Index_1";
	public static final String PARAM_SERVICE_PROFILE_INDEX2 = "Service_Profile_Index_2";
	public static final String PARAM_SERVICE_PROFILE_INDEX3 = "Service_Profile_Index_3";
	public static final String PARAM_ISAM_NAME = "ISAM_Name";
	public static final String PARAM_VOICE_TEMPLATE = "Voice_Template";
	public static final String PARAM_COSID = "Cos_ID";
	public static final String PARAM_PORTNUMBER = "portNumber";
	public static final String PARAM_FRAMENUMBER = "FrameNumber";
	public static final String PARAM_APSID = "APSId";
	public static final String PARAM_SOFTSWITCH_ID = "Softswitch_ID";
	public static final String PARAM_SSID = "SSId";
	public static final String PARAM_SOFTSWITCH_IP = "SoftSwitchIP";
	public static final String PARAM_EID = "EID";
	public static final String PARAM_MODULENUMBER = "ModuleNumber";
	public static final String PARAM_DIRECTORY_NUMBER = "Directory_Number";
	public static final String PARAM_DEFAULT_GATEWAY = "DefaultGateway";
	public static final String PARAM_VOICEIP = "VoiceIP";
	public static final String PARAM_DATAIP = "DataIP";
	public static final String PARAM_DEFAULT_MASK = "DefaultMask";
	public static final String PARAM_DEVICE_IPADDRESS = "Device_IPAddress";
	public static final String PARAM_DEVICE_MASK = "Device_Mask";
	public static final String PARAM_TECHNOLOGY = "Technology";
	public static final String PARAM_CARD_TECHNOLOGY = "CardTechnology";
	
	public static final String PARAM_DEVICETYPE = "DeviceType";
	public static final String PARAM_DEVICEIPADDRESS = "Device_IPAddress";
	public static final String PARAM_TERMINALID = "TerminalId";
	public static final String PARAM_PLANNAME = "PlanName";
	public static final String PARAM_MISCORDERTYPE = "MiscOrderType";
	public static final String PARAM_NUMBEROFCHANNELS = "NumberOfChannels";
	public static final String PARAM_OPERATIONAL_STATUS = "OperationalStatus";
	public static final String PARAM_ISAM_PORT = "ISAM_Port";
	public static final String PARAM_ISAM_IPADDRESS = "ISAM_IPAddress";
	public static final String PARAM_VIRTUAL_PATH_IDENTIFIER = "VIRTUAL_PATH_IDENTIFIER";
	public static final String PARAM_VIRTUAL_CHANNEL_IDENTIFIER = "VIRTUAL_CHANNEL_IDENTIFIER";
	public static final String PARAM_VPI = "VPI";
	public static final String PARAM_VCI = "VCI";
	public static final String PARAM_EFTPOS = "EFTPOS";
	public static final String PARAM_STATICIP = "StaticIP";
	
	public static final String PARAM_PORT_CAPACITY = "Port_Capacity";
	public static final String PARAM_SLOT_CAPACITY = "Slot_Capacity";
	public static final String PARAM_LICENSE_CAPACITY = "License_Capacity";
	public static final String PARAM_SOFTWARE_VERSION = "Software_Version";
	public static final String PARAM_IDUDATA_PORT = "IDU_Data_Port";
	public static final String PARAM_ATA_VOICE_PORT = "ATA_Voice_Port";
	
	public static final String PARAM_UPSTREAM_TRAFFIC_PROFILE = "Upstream_Traffic_Profile";
	public static final String PARAM_DOWNSTREAM_TRAFFIC_PROFILE = "Downstream_Traffic_Profile";
	
	public static final String PARAM_DOWNLOADSPEED = "DownloadSpeed";
	public static final String PARAM_UPLOADSPEED = "UploadSpeed";

	public static final String ACTIVE = "Active";
	public static final String INTACT = "Intact";
	public static final String FAULTY = "Faulty";
	
	
	/* COMMON CI REQUEST PARAMS */
	public static final String PARAM_VLAN_ID = "VLAN_ID";
	public static final String PARAM_LATITUDE = "Latitude";
	public static final String PARAM_LONGITUDE = "Longitude";
	public static final String PARAM_VILLAGE_NAME = "Street_VillageName";
	public static final String PARAM_STREET_HOUSE_NUMBER= "Street_HouseNumber";
	public static final String PARAM_BUILDING_NAME= "BuildingName";
	public static final String PARAM_FDIVISION = "FDivision";
	/* PSTN CI REQUEST PARAMS */
	public static final String PARAM_FIXEDLINENUMBER_CI = "FixedLineNumber_CI";
	public static final String PARAM_FIXEDLINENUMBERS_CI = "FixedLineNumbers_CI";
	public static final String PARAM_MSISDN_CI = "MSISDN_CI";
	public static final String PARAM_SIM_CI = "SIM_CI";
	public static final String PARAM_CPE_CI = "CPE_CI";
	public static final String PARAM_USERCREDENTIALS_CI = "UserCredentials_CI";
	//public static final String PARAM_PROPERTIES_CI = "Properties_CI";
	public static final String PARAM_PREMIUMNUMBER_CI = "PremiumNumber_CI";
	public static final String PARAM_CVLAN_CI = "CVLAN_CI";
	public static final String PARAM_SVLAN_CI = "SVLAN_CI";
	public static final String PARAM_NETWORK_PROFILE_CI = "NetworkProfile_CI";
	public static final String PARAM_EFTPOS_CI = "EFTPOS_CI";
	public static final String PARAM_SERVICE_ADDRESS_CI = "Service_Address_CI";
	public static final String PARAM_FOREIGN_ADDRESS_CI = "Foreign_Address_CI";
	public static final String PARAM_IP_ADDRESS_CI = "IP_Address_CI";
	public static final String PARAM_ADDITIONAL_IP_ADDRESS_CI = "Additional_IP_Address_CI";
	public static final String PARAM_ADDITIONAL_IP_ADDRESSES_CI = "Additional_IP_Addresses_CI"; 
	public static final String PARAM_VAS_CI = "VAS_CI";
	public static final String PARAM_DNS_CI = "DNS_CI";
	public static final String PARAM_MANAGEMENT_IPADDRESS_CI = "ManagementIPAddress_CI";
	public static final String PARAM_PROPERTIES = "Properties";
	public static final String PARAM_USER_CREDENTIALS = "User_Credentials";


	public static final String PARAM_CODECONTROLBARRINGALL_CI = "CodeControlBarringAll_CI";
	public static final String PARAM_SMARTLINK_CI = "Smartlink_CI";
	public static final String PARAM_PBX_PILOT_CI = "PBX_Pilot_CI";
	public static final String PARAM_PBX_AUXILIARY_CI = "PBX_Auxiliary_CI";
	public static final String PARAM_HOTLINESERVICEINSTANT_CI = "HotlineServiceInstant_CI";
	public static final String PARAM_HOTLINESERVICEDELAY_CI = "HotlineServiceDelay_CI";
	public static final String PARAM_CODECONTROLBARRINGDIGICELMOBILE_CI  = "CodeControlBarringDigicelMobile_CI";
	public static final String PARAM_CHANGECODECONTROLBARRINGPIN_CI  = "ChangeCodeControlBarringPin_CI";
	public static final String PARAM_CODECONTROLBARRINGFINTELMOBILE_CI = "CodeControlBarringFintelMobile_CI";
	public static final String PARAM_CODECONTROLBARRINGIDD_CI = "CodeControlBarringIDD_CI";
	public static final String PARAM_CODECONTROLBARRINGMOBIDD_CI = "CodeControlBarringMOBIDD_CI";
	public static final String PARAM_CODECONTROLBARRINGALLMOBILES_CI = "CodeControlBarringAllMobiles_CI";
	public static final String PARAM_CODECONTROLBARRINGMVNOMOBILE_CI = "CodeControlBarringMVNOMobile_CI";
	public static final String PARAM_CODECONTROLBARRINGNATIONALCE_CI = "CodeControlBarringNationalCE_CI";
	public static final String PARAM_CODECONTROLBARRINGNATIONALWE_CI = "CodeControlBarringNationalWE_CI";
	public static final String PARAM_CODECONTROLBARRINGNATIONALNO_CI = "CodeControlBarringNationalNO_CI";
	public static final String PARAM_CODECONTROLBARRINGSTDANDIDD_CI = "CodeControlBarringSTDAndIDD_CI";
	public static final String PARAM_CODECONTROLBARRINGVODAFONEMOBILE_CI = "CodeControlBarringVodafoneMobile_CI";
	public static final String PARAM_VOICEMAIL_CI = "VoiceMail_CI";
	public static final String PARAM_MEETMECONFERENCE_CI = "MeetMeconference_CI";
	public static final String PARAM_INTERNATIONALTOLLFREE_CI = "InternationalTollFree_CI";
	public static final String PARAM_LOCALTOLLFREE_CI = "LocalTollFree_CI";
	public static final String PARAM_CALLFORWARDDUETOFAULT_CI = "CallForwardDueToFault_CI"; 
	public static final String PARAM_CALLFORWARDIMMEDIATE_CI = "CallForwardImmediate_CI";
	public static final String PARAM_CALLFORWARDIMMEDIATEINTERNATIONAL_CI = "CallForwardImmediateInternational_CI";
	public static final String PARAM_CALLFORWARDIMMEDIATEMOBILE_CI = "CallForwardImmediateMobile_CI";
	public static final String PARAM_CALLFORWARDSMARTLINKSERVICE_CI  = "CallForwardSmartlinkService_CI";
	public static final String PARAM_CALLTRANSFERVARIABLENUMBER_CI = "CallTransferVariableNumber_CI";
	public static final String PARAM_CALLWAITINGCUSTOMERCONTROL_CI = "CallWaitingCustomerControl_CI";
	public static final String PARAM_CALLWAITINGOPERATOR_CI = "CallWaitingOperator_CI";
	public static final String PARAM_CALLMINDERONVMS_CI = "CallMinderonVMS_CI";
	public static final String PARAM_SMARTLINKADDITIONALSERVICE_CI = "SmartlinkAdditionalService_CI";
	public static final String PARAM_VOICEMAILP_CI = "VoicemailPassword_CI";
	public static final String PARAM_VOICEMAILEMAILADDRESSAMEND_CI = "VoiceMailEmailAddressAmend_CI";
	public static final String PARAM_VOICEMAILVIRTUAL_CI = "VoiceMailVirtual_CI";
	public static final String PARAM_VOICEMAILTOEMAIL_CI = "VoiceMailToEmail_CI";
	public static final String PARAM_ABBREVIATEDDIALINGOPERATORCONTROL_CI = "AbbreviatedDialingOperatorControl_CI"; 
	public static final String PARAM_CALLTRANSFERBUSYFIXEDNUMBER_CI = "CallTransferBusyFixedNumber_CI";
	public static final String PARAM_CALLTRANSFERIMMEDIATE_CI = "CallTransferImmediate_CI";
	public static final String PARAM_CALLFORWARDAFTER5RINGS_CI = "CallForwardAfter5Rings_CI";
	public static final String PARAM_VOICEMAILPWD_CI = "VoicemailPassword_CI";
	
	public static final String PARAM_STATICIP_CI = "Static_IP_CI";
	public static final String PARAM_IP_ADDRESSES_CI = "IP_Addresses_CI";
	
	public static final String PARAM_FIXEDLINENUMBER = "FixedLineNumber";
	public static final String PARAM_DIVISION = "Division";
	public static final String PARAM_DIDBLOCK = "DIDBlock";
	public static final String PARAM_EXTENSION = "Extension";
	public static final String PARAM_SERVICEID = "ServiceId";
	public static final String PARAM_SLOTNUMBER_SERIALNUMBER = "SlotNo_SerialNumber";
	public static final String PARAM_MSISDN = "MSISDN";
	public static final String PARAM_ICCID = "ICCID";
	public static final String PARAM_IMSI = "IMSI";
	public static final String PARAM_IMEI = "IMEI";	
	public static final String PARAM_SERIAL_NUMBER = "SerialNumber";

	public static final String PARAM_USERNAME = "Username";
	public static final String PARAM_PASSWORD = "Password";
	public static final String PARAM_CUSTOMERNAME = "CustomerName";
	public static final String PARAM_DATACIRCUITID = "DataCircuitId";
	public static final String PARAM_EMAILID = "EmailId";
	public static final String PARAM_REGION = "Region";
	public static final String PARAM_RELOCTAIONINTERNALDATE = "relocationInternalDate";
	
	//SIP Trunk Request 
	public static final String PARAM_PILOTNUMBER_CI = "PilotNumber_CI";
	public static final String PARAM_AUXILLARYNUMBER_CI = "AuxillaryNumber_CI";
	public static final String PARAM_AUXILLARYNUMBERS_CI = "AuxillaryNumbers_CI";
	public static final String PARAM_AGGREGATION_CI = "Aggregation_CI";
	public static final String PARAM_RADIO_CI = "Radio_CI";
	public static final String PARAM_DIDBLOCK_CI ="DIDBlock_CI";
	public static final String PARAM_DIDBLOCKS_CI ="DIDBlocks_CI";
	public static final String PARAM_TELEPHONENUMBER = "TelephoneNumber";
	public static final String PARAM_ACTION = "Action";
	public static final String PARAM_MEMBER_NUMBER = "Member_Number";
	public static final String PARAM_AUXILIARY_NUMBER = "Auxiliary_Number";
	public static final String PARAM_ACTIVE_DEVICE_NAME = "Active_Device_Name";
	public static final String PARAM_AGGREGATION_NODE_NAME = "AggregationNodeName";
	public static final String PARAM_AGGREGATION_PORT = "AggregationPort";
	public static final String PARAM_AGGREGATION_NODE_TYPE = "AggregationNodeType";
	public static final String PARAM_AGGREGATION_TYPE = "AggregationType";
	public static final String PARAM_PORT_NAME = "Portname";
	public static final String PARAM_PARENT_DEVICE = "ParentDevice";
	public static final String PARAM_DELETE = "Delete";
	public static final String PARAM_DELETED = "Deleted";
	public static final String PARAM_ADD = "Add";
	public static final String PARAM_ADDED = "Added";
	public static final String PARAM_EXISTING = "Existing";
	public static final String PARAM_MODIFY = "Modify";
	public static final String PARAM_DOMAIN_NAME = "DomainName";
	
	public static final String PARAM_TERMINATE_PILOTNUMBER= "TerminatePilotNumber";
	public static final String PARAM_PILOTNUMBER= "PilotNumber";
	
	//VLAN Parameters that will come in CI Request for Fibre
	public static final String PARAM_PEROUTER = "PERouter";
	
	/* COMMON CONFIG ITEMS */
	public static final String CI_ACCESS = "Access_CI";
	public static final String CI_TARGET = "Target_CI";
	public static final String CI_SUBSCRIBERPORT = "SubscriberPort_CI";
	public static final String CI_SERVICEPORT = "ServicePort_CI";
	public static final String CI_CPEPORT = "CPEPort_CI";
	public static final String CI_VIRTUALPORT = "VirtualPort_CI";
	//public static final String CI_PROPERTIES = "Properties_CI";
	public static final String CI_TAVARIABLES = "TAVariables_CI";
	public static final String CI_USERACCOUNT = "UserAccount_CI";
	public static final String CI_IPADDRESS = "IPAddress_CI";
	public static final String CI_SVLAN = "SVLAN_CI";
	public static final String CI_CVLAN = "CVLAN_CI";
	public static final String CI_DOMAIN_NAME = "DomainName_CI";
	public static final String CI_EMAIL = "Email_CI";

	
	/* SERVICE & RESOURCE CHARACTERISTICS */
	public static final String PARAM_SMARTLINK = "SmartLink";
	public static final String PARAM_SMARTLINKADDITIONALSERVICE = "SmartLinkAdditionalService";
	public static final String PARAM_SHORTCODE = "ShortCode";
	public static final String PARAM_PREMIUMNUMBER = "PremiumNumber";
	public static final String PARAM_HOTLINESERVICEINSTANT = "HotlineServiceInstant";
	public static final String PARAM_HOTLINESERVICEDELAY = "HotlineServiceDelay";
	public static final String PARAM_CODECONTROLBARRINGALL = "CodeControlBarringAll";
	public static final String PARAM_CODECONTROLBARRINGDIGICELMOBILE = "CodeControlBarringDigicelMobile";
	public static final String PARAM_CODECONTROLBARRINGFINTELMOBILE = "CodeControlBarringFintelMobile";
	public static final String PARAM_CODECONTROLBARRINGIDD = "CodeControlBarringIDD";
	public static final String PARAM_CODECONTROLBARRINGMOBIDD = "CodeControlBarringMOBIDD";
	public static final String PARAM_CODECONTROLBARRINGALLMOBILES = "CodeControlBarringAllMobiles";
	public static final String PARAM_CODECONTROLBARRINGMVNOMOBILE = "CodeControlBarringMVNOMobile";
	public static final String PARAM_CODECONTROLBARRINGNATIONALCE = "CodeControlBarringNationalCE";
	public static final String PARAM_CODECONTROLBARRINGNATIONALWE = "CodeControlBarringNationalWE";
	public static final String PARAM_CODECONTROLBARRINGNATIONALNO = "CodeControlBarringNationalNO";
	public static final String PARAM_CODECONTROLBARRINGSTDANDIDD = "CodeControlBarringSTDAndIDD";
	public static final String PARAM_CODECONTROLBARRINGCODAFONEMOBILE = "CodeControlBarringVodafoneMobile";
	public static final String PARAM_VOICEMAIL = "VoiceMail";
	public static final String PARAM_MEETMECONFERENCE = "MeetMeconference";
	public static final String PARAM_CALLFORWARDIMMEDIATE = "CallForwardImmediate";
	public static final String PARAM_CALLFORWARDIMMEDIATEINTERNATIONAL = "CallForwardImmediateInternational";
	public static final String PARAM_CALLFORWARDIMMEDIATEMOBILE = "CallForwardImmediateMobile";
	public static final String PARAM_CALLFORWARDSMARTLINKSERVICE = "CallForwardSmartlinkService";
	public static final String PARAM_CALLTRANSFERVARIABLENUMBER = "CallTransferVariableNumber";
	public static final String PARAM_CALLWAITINGCUSTOMERCONTROL = "CallWaitingCustomerControl";
	public static final String PARAM_CALLWAITINGOPERATOR = "CallWaitingOperator";
	public static final String PARAM_CALLMINDERONVMS = "CallMinderonVMS";
	public static final String PARAM_VOICEMAILP = "VoicemailPassword";
	public static final String PARAM_VOICEMAILEMAILADDRESSAMEND = "VoiceMailEmailAddressAmend";
	public static final String PARAM_VOICEMAILVIRTUAL = "VoiceMailVirtual";
	public static final String PARAM_VOICEMAILTOEMAIL = "VoiceMailToEmail";
	public static final String PARAM_RADIONAME = "RadioName";
	public static final String PARAM_RADIOPORT = "RadioPort";
	public static final String PARAM_FIBRESUBSCRIBERPORT = "FibreSubscriberPort";
	public static final String RESERVATION_ID = "ReservationId";
	public static final String CHAR_ACCESS_TECHNOLOGY = "AccessTechnology";
	public static final String CHAR_PARENTDEVICE = "ParentDevice";
	public static final String CHAR_DPTYPE = "DPType";
	public static final String CHAR_DIVISION = "Division";
	
	public static final String CHAR_ISSUBSCRIBERPORT = "IsSubscriberPort";
	public static final String CHAR_RESERVATIONID = "ReservationId";
	public static final String CHAR_MEDIUM = "Medium";
	public static final String CHAR_SERVICEID = "ServiceId";
	public static final String CHAR_DI_RESERVEDFOR ="ReservedFor";
	public static final String CHAR_OPERATIONALSTATUS = "OperationalStatus";
	public static final String CHAR_SERVICEACTION = "ServiceAction";
	public static final String CHAR_DID = "DID";
	public static final String CHAR_DEVICESITE = "DeviceSite";
	public static final String CHAR_PLANTNO = "PlantNo";
	public static final String CHAR_CARDTYPE = "CardType";
	public static final String CHAR_TOTAL_PORTS = "TotalPorts";
	public static final String CHAR_STARTING_TID = "StartingTID";
	public static final String CHAR_DEVICE_TYPE = "DeviceType";
	public static final String CHAR_DECOMMISSIONED = "Decommissioned";
	public static final String CHAR_SUPPORTEDSERVICES = "SupportedServices";
	public static final String CHAR_TERMINALID = "TerminalId";
	public static final String CHAR_ROUTER = "Router";
	public static final String CHAR_ISORIGINATING = "IsOriginating";
	public static final String CHAR_STATICIP = "StaticIP";
	public static final String CHAR_AVAILUPLOADBW = "AvailableUploadBandwidth";
	public static final String CHAR_MAXUPLOADBW = "MaximumUploadBandwidth";
	public static final String CHAR_AVAILDOWNLOADBW = "AvailableDownloadBandwidth";
	public static final String CHAR_MAXDOWNLOADBW = "MaximumDownloadBandwidth";
	public static final String CHAR_DOWNLOADBW = "DownloadBandwidth";
	public static final String CHAR_UPLOADBW = "UploadBandwidth";
	public static final String CHAR_ACTION = "Action";
	
	/* PLACE CHARACTERISTICS */
	public static final String CHAR_COUNTRY = "Country";
	public static final String CHAR_ATOLL = "Atoll";
	public static final String CHAR_ISLAND = "Island";
	public static final String CHAR_BUILDING = "Building";
	public static final String CHAR_DISTRICT = "District";
	public static final String CHAR_BLOCKNO= "BlockNo";
	public static final String CHAR_APARTMENT = "Apartment";
	public static final String CHAR_POSTALCODE = "PostalCode";
	public static final String CHAR_STREET = "Street";
	public static final String CHAR_SITE = "Site";
	
	/* SYSTEM CONFIG CHARACTERISTICS */
	public static final String CHAR_RESERVEDBANDWIDTH = "ReservedBandwidth"; //20% bandwidth reserved on OLT port for speed upgrade plans
	public static final String CHAR_PORTRESERVATIONEXPIRY = "PortReservationExpiry"; //180 days
	public static final String CHAR_FIXEDLINENUMBERRESERVATIONEXPIRY = "FixedLineNumberReservationExpiry"; //365 days
	public static final String CHAR_FIXEDLINENUMBERAGINGPERIOD = "FixedlineNumberAgingPeriod"; //180 days
	public static final String CHAR_MOBILENUMBERAGINGPERIOD = "MobileNumberAgingPeriod"; //90 days also applicable for Mobile Short Codes
	
	public static final String MBPS = "Mbps";
	public static final String KBPS = "Kbps";
	public static final String CAB = "CAB";
	public static final String CARD_NPOT_B = "NPOT-B";
	public static final String CARD_SPOT_C = "SPOT-C";
	
	public static final String CHAR_PORT_NUMBER = "portNumber";
	public static final String CHAR_PARENT_CABLE = "ParentCable";
	
	/* Bulk Rules */
	public static final String SPEC_PARTY = "PartySpecification";
	
	//IP Pool Constants
	public static final String IPADDRESS_POOL = "IPAddressPool";
	public static final String IP_DOMAIN = "IPDomainType";
	public static final String CIDR = "CIDR";
	public static final String MAIN_RANGE = "MainRange";
	public static final String IP_DOMAIN_PRIVATE = "Private";
	public static final String IP_DOMAIN_PUBLIC = "Global IP Address Domain";
	public static final String IP_DOMAIN_CHAR = "IPDomain";
	
	//VLANPool Constants
	public static final String SERVICE_TYPE = "Servicetype";
	public static final String PARAM_SERVICE_TYPE = "Service_Type";
	public static final String SVLAN = "SVLAN";
	public static final String CVLAN = "CVLAN";
	public static final String VLAN_DOMAIN_SPEC = "VLANDomain";
	public static final String VLAN_POOL_SPEC = "VLANPool";
	public static final String PE_ROUTER = "Router";
	public static final String ADSL = "ADSL";
	public static final String VDSL = "VDSL";
	public static final String GPON = "GPON";
	public static final String SHDSL = "SHDSL";
	public static final String GHDSL = "GHDSL";
	
	// Bulk constants
	public static final String UIM_HOME = "uim.home";
	public static final String STATUS = "SERVICESTATUS";
	public static final String RS_PASS = "Pass";
	public static final String PARAM_MSANPORTNAME = "NewMSANPortName";
	public static final String FILENAME_TIMESTAMP_FORMAT = "dd-MM-yyyy_HH.mm.ss"; //Date Time Format to be used in File Name
	public static final String PARAM_TOSWITCH = "ToSwitch";
	public static final String PARAM_FROMSWITCH = "FromSwitch";
	
	public static final String SITECODE="SiteCode";
	public static final String DPCODE="DPCode";
	public static final String TBCODE="TBCode";

	public static final String SPARE="Spare";
	public static final String PORT="Port";
	
	public static final String DPADDRESS = "DPAddress";
	
	public static final String DIVISION="Division";
	public static final String COUNTRY="Country";
	public static final String CITY="City_Town_Province";
	public static final String SUBURB="Suburb_Tikina_Island";
	public static final String LOTDPNUMBER="LOT_DPNumber";
	public static final String STREETNUMBER="Street_HouseNumber";
	public static final String ESTATENAME="EstateName";
	public static final String BUILDINGNAME="BuildingName";
	public static final String STREETNAME="Street_VillageName";
	
	public static final String CPESERIALNUMBER = "SerialNumber";
	public static final String CPEDEVICETYPE = "DeviceType";
	public static final String CPEMACADDRESS = "MACAddress";
	public static final String CPEMODEL= "Model";
	public static final String CPEVENDOR= "Vendor";
	
	public static final String IMSI = "IMSI";
	public static final String SERVICENUMBER = "ServiceNumber";

	public static final String MSISDN = "MSISDN";

	public static final String SIM_NAME = "IMSI";

	public static final String ICCID = "ICCID";

	public static final String AVAILABLE = "Available";
	public static final String STARTNUMBER = "StartNumber";
	public static final String ENDNUMBER = "EndNumber";	  
	public static final String DIDBLOCK_START = "DIDBlockStart";
	public static final String DIDBLOCK_END = "DIDBlockEnd";
	public static final String SPECIFICATION = "Specification";
	public static final String NAME = "Name";
	public static final String DID = "DID";
	public static final String DESCRIPTION = "Description";
	public static final String PARAM_VOICESHORTCODE_CI = "VoiceShortCode_CI";
	public static final String PARAM_VTSATCALLFORWARDINGIFBUSY_CI="VTSATCallForwardingIfBusy_CI";
	public static final String PARAM_VTSATCALLFORWARDINGIFBUSYANDNOREPLY_CI="VTSATCallForwardingIfBusyAndNoReply_CI";
	public static final String PARAM_VTSATCALLFORWARDINGIFNOREPLY_CI="VTSATCallForwardingIfNoReply_CI";
	public static final String PARAM_VTSATCALLFORWARDINGIFUNCONDITIONAL_CI="VTSATCallForwardingIfUnconditional_CI";
	public static final String PARAM_CODECONTROLBARIDDANDVODA_CI="CodeControlBarIDDAndVODA_CI";
	public static final String PARAM_VTSATBARMOBILEANDIDDCALLS_CI="VTSATBarMobileAndIDDCalls_CI";
	public static final String PARAM_VTSATBARMOBILEANDIDDCALLSALLOWLOCALCALLS_CI="VTSATBarMobileAndIDDCallsAllowLocalCalls_CI";
	public static final String PARAM_ATA_CI="ATA_CI";
	public static final String PARAM_CPE = "CPE";
	public static final String PARAM_EXTENSIONS_CI = "Extensions_CI";
	public static final String PARAM_EXTENSION_CI = "Extension_CI";
	public static final String PARAM_LOCALTOLLFREEGROUP_CI = "LocalTollFreeGroup_CI";
	public static final String PARAM_PBXAUXILIARYNUMBERS_CI = "PBX_AuxiliaryNumbers_CI";
	public static final String PARAM_PBXAUXILIARY_CI = "PBX_Auxillary_CI";
	public static final String PARAM_ODX_CI = "ODX_CI";
	public static final String NUMBER_TYPE = "Number_Type";
	public static final String CHAR_CPE_TYPE = "CPEType";
	public static final String CHAR_SERIAL_NUMBER = "SerialNumber";
	public static final String PARAM_DATE_INSTALLED="DateInstalled";
	public static final String CHAR_IP_ADDRESS = "IPAddress";
	public static final String CHAR_SLOT_NUMBER = "SlotNumber";
	public static final String CHAR_CARD_TYPE = "CardType";
	public static final String CHAR_SERVICE_PORT = "ServicePort";
	public static final String CHAR_IFBN = "IFBN";
	public static final String CHAR_CARD_DESCRIPTION = "CardDescription";
	public static final String CHAR_FUNCTION = "Function";
	public static final String CHAR_ONU_PORT_NUMBER = "onuPortNumber";
	public static final String CHAR_FRAME_NUMBER = "FrameNumber";
	
	public static final String PARAM_USED_PORTS="UsedPorts";
	public static final String PARAM_AVAILABLE_PORTS="AvailablePorts";
	
	public static final String PARAM_PARTYNAME = "PartyName";
	
	public static final String SN_FIXEDVOICE = "FixedVoice";
	public static final String SN_BROADBAND = "Broadband";
	public static final String SN_SIPTRUNK = "SIPTrunk";
	public static final String SN_IPLC = "IPLC";
	public static final String SN_EPL = "EPL";
	public static final String SN_DIA = "DIA";
	public static final String SN_ISDN = "ISDN";
	public static final String SN_IPVPN = "IPVPN";
	
	public static final String OPERATOR_EQUALS = "Equals";

	public enum EnumOperationalStatus {	
		Faulty, Active, Blocked, Spare, Reserved
	}
	
	public enum EnumMediumType {	
		Copper, Fibre;
		
		public static boolean contains(String mediumType){
			for(EnumMediumType enumMediumType: EnumMediumType.values()){
				if(enumMediumType.toString().equals(mediumType)){
					return true;
				}
			}
			return false;
		}
	}	

	public enum EnumCopperDevices {	
		DP, CABINET, MDF, MSAN, ISAM, DSLAM;
		
		public static boolean contains(String deviceSpec){
			for(EnumCopperDevices enumCopperDevices: EnumCopperDevices.values()){
				if(enumCopperDevices.toString().equals(deviceSpec)){
					return true;
				}
			}
			return false;
		}
	}	
	
		
	/**	 
	 * These are 5 order types for FAR.
	 *
	 */
	public enum EnumOrderType {	
		ChangeTechnology, NewInstall, ExternalRelocation, RelocationChangeTechnology, ChangeSpeed, TroubleTicket;
		
		public static boolean contains(String orderType){
			for(EnumOrderType enumOrderType: EnumOrderType.values()){
				if(enumOrderType.toString().equalsIgnoreCase(orderType)){
					return true;
				}
			}
			return false;
		}
	}
	
	

	public enum EnumConnectivityUpdateActions {	
		Add, Delete;
	}
	
	/**
	 * This method is used to call common code in BaseDesiger.processAccessCI 
	 *
	 */
	public enum EnumAccessServices {	
		FixedVoice, Broadband, SIPTrunk, IPLC, DIA, ISDN, IPVPN, EPL, ODX;
		
		public static boolean contains(String serviceType){
			for(EnumAccessServices enumAccessServices: EnumAccessServices.values()){
				if(enumAccessServices.toString().equals(serviceType)){
					return true;
				}
			}
			return false;
		}
	}
	
	
	//This map is only used for Service Number generation via OAP
	public static final Map<String,String> MAPSERVICETYPE = new HashMap<>();
	static{
		MAPSERVICETYPE.put(SN_FIXEDVOICE,"FV");
		MAPSERVICETYPE.put(SN_BROADBAND,"BB");
		MAPSERVICETYPE.put("MobileBroadband","MB");
		MAPSERVICETYPE.put(SN_SIPTRUNK,"ST");
		MAPSERVICETYPE.put(SN_IPVPN,"IV");
		MAPSERVICETYPE.put("PABX","PA");
		MAPSERVICETYPE.put(SN_DIA,"DI");
		MAPSERVICETYPE.put("CloudPABX","CP");
		MAPSERVICETYPE.put(SN_ISDN,"IS");
		MAPSERVICETYPE.put(SN_IPLC,"IP");
		MAPSERVICETYPE.put(SN_EPL,"EP");
		MAPSERVICETYPE.put("Premium","PN");
		MAPSERVICETYPE.put("WebHosting/DomainName","WD");		
		MAPSERVICETYPE.put("Email","EM");
		MAPSERVICETYPE.put("Wifi-Ad Injection","WA");
		MAPSERVICETYPE.put("SatelliteData","SD");
		MAPSERVICETYPE.put("SatelliteVoice","SV");
		MAPSERVICETYPE.put("ODX","OD");
		MAPSERVICETYPE.put("Pilot","PI");
		MAPSERVICETYPE.put("Auxiliary","AU");
		
	}
	
		
	// Error codes 
	public static final String ERR_SERVICE_MISSING_PARAMETER =  "Service.missingParameter";	
	public static final String ERR_SERVICE_CONFIGURATION_IS_NULL = "Service.configurationIsNull";
	public static final String ERR_SERVICE_IS_NULL = "Service.serviceIsNull";
	public static final String ERR_SERVICE_MISSING_RESOURCES = "Service.missingResources";
	public static final String ERR_INTERNAL_ERROR = "ws.internalError";
	public static final String ERR_INVALID_PARAMETER = "Service.invalidParameter";
	public static final String ERR_FIXEDLINENUMBER_NOTFOUND="FixedLineNumber.numberNotFound";
	
	public static final String ERR_MISSING_INPUT_PARAMETER="ws.missingInputParameter";
	public static final String ERR_MISSING_INPUT_PARAMETER_ID="ws.missingInputParameter.id";
	
	public static final String ERR_INVALID_INPUT_PARAMETER="ws.invalidInputParameter";
	public static final String ERR_INVALID_INPUT_PARAMETER_ID="ws.invalidInputParameter.id";
	
	
	
	
	
	// Custom Web Service Names
	public static final String WS_UPDATEROUTE = "updateRoute";
	public static final String WS_UPDATECPE = "updateCPE";
	public static final String WS_GETCPEUSERNAME = "GetCPEUserName";
	
	public static final String WS_CREATED = "Created";
}
