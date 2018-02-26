package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;

public class CatalogModelType {
	public static final int OT_VSS_FILESYSTEM_WRITER	=	1;
	public static final int Folder	=	6;
	public static final int File	=	7;
	public static final int MSSQLDB	= 	73;
	
	public static final int OT_VSS_SQL_WRITER = 10;
	public static final int OT_VSS_SQL_COMPONENT = 11;
	public static final int OT_VSS_SQL_COMPONENT_SELECTABLE = 12;
	public static final int OT_VSS_SQL_NODE = 13;
	public static final int OT_VSS_SQL_LOGICALPATH = 14;
	
	public static final int OT_VSS_EXCH_WRITER = 20; //EXCH writer
	public static final int OT_VSS_EXCH_COMPONENT = 21; //EXCH Component                                                                        
	public static final int OT_VSS_EXCH_COMPONENT_SELECTABLE = 22; //EXCH Selectable Component
	public static final int OT_VSS_EXCH_NODE = 23; //EXCH Machine name
	public static final int OT_VSS_EXCH_LOGICALPATH = 24; //EXCH Logical Path
	public static final int OT_VSS_EXCH_SERVER = 25;//EXCH Server name
	public static final int OT_VSS_EXCH_INFOSTORE = 26;//EXCH Information Store
	public static final int OT_VSS_EXCH_COMPONENT_PUBLIC = 27;  //EXCH public folder or public database
	public static final int OT_VSS_EXCH_REPLICA = 28; //EXCH Replica of exchange Replica writer 2007/2010.  writer/server/infostore/replica

	public static final int OT_GRT_EXCH_MBSDB 			= 255; // EDb
	public static final int OT_GRT_EXCH_CALENDAR 		= 9004; // Calendar folder
	public static final int OT_GRT_EXCH_CONTACTS 		= 9005; // Contract folder
	public static final int OT_GRT_EXCH_CALENDAR_ITEM	= OT_GRT_EXCH_CALENDAR + 100; // Calendar item
	public static final int OT_GRT_EXCH_CONTACTS_ITEM	= OT_GRT_EXCH_CONTACTS + 100; // Contract item
	public static final int OT_GRT_EXCH_CONTACTS_GROUP	= 9024; // Contract group
	public static final int OT_GRT_EXCH_DRAFT 			= 9006; // Draft folder 
	public static final int OT_GRT_EXCH_JOURNAL 		= 9007; // Journal folder 
	public static final int OT_GRT_EXCH_NOTES 			= 9008; // Notes folder
	public static final int OT_GRT_EXCH_TASKS 			= 9009; // Tasks folder
	public static final int OT_GRT_EXCH_PUBLIC_FOLDERS 	= 254; // Root Public folder
	public static final int OT_GRT_EXCH_MAILBOX 		= 9013; // Mailbox
	public static final int OT_GRT_EXCH_DELETED_ITEMS 	= 9014; // Deleted Item folder
	public static final int OT_GRT_EXCH_INBOX 			= 9015; // Inbox folder
	public static final int OT_GRT_EXCH_OUTBOX 			= 9016; // Outbox folder
	public static final int OT_GRT_EXCH_SENT_ITEMS 		= 9017; // Sent items folder
	public static final int OT_GRT_EXCH_FOLDER 			= 9018; // Folder created by user
	public static final int OT_GRT_EXCH_MESSAGE 		= 9023; // Email	
	
	public static final int OT_GRT_SP_DB = 10000;
	public static final int OT_GRT_SP_SITE= 10001;
	public static final int OT_GRT_SP_WEB= 10002; 
	public static final int OT_GRT_SP_LIST= 10003; 
	public static final int OT_GRT_SP_FOLDER= 10004; 
	public static final int OT_GRT_SP_FILE= 10005;
	public static final int OT_GRT_SP_VERSION = 10006;
	
	public static final int ActiveDirectory	= 8000;
	public static final int AD_GENERAL		= 8010;
	public static final int AD_USERS		= 8011;
	public static final int AD_USER			= 8012;
	public static final int AD_COMPUTER		= 8013;
	public static final int AD_OU			= 8014;
	public static final int AD_ATTRIBUTE	= 8100;
	
	public final static ArrayList<Integer> allActiveDirectoryTypes = new ArrayList<Integer>();
	static {
		allActiveDirectoryTypes.add(CatalogModelType.ActiveDirectory);
		allActiveDirectoryTypes.add(CatalogModelType.AD_GENERAL);
		allActiveDirectoryTypes.add(CatalogModelType.AD_USERS);
		allActiveDirectoryTypes.add(CatalogModelType.AD_USER);
		allActiveDirectoryTypes.add(CatalogModelType.AD_COMPUTER);
		allActiveDirectoryTypes.add(CatalogModelType.AD_OU);
		allActiveDirectoryTypes.add(CatalogModelType.AD_ATTRIBUTE);
	}
	
	public final static ArrayList<Integer> allExchangeTypes = new ArrayList<Integer>();
	public final static ArrayList<Integer> NonSelectExchangeTypes = new ArrayList<Integer>();
	static {
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_WRITER);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_COMPONENT);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_COMPONENT_SELECTABLE);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_NODE);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_LOGICALPATH);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_SERVER);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_INFOSTORE);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC);
		allExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_REPLICA);
		
		allExchangeTypes.add(OT_GRT_EXCH_MBSDB);	
		allExchangeTypes.add(OT_GRT_EXCH_PUBLIC_FOLDERS);
	}
	
	static {		
		NonSelectExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_NODE);	
		NonSelectExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_SERVER);
		NonSelectExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_INFOSTORE);
		NonSelectExchangeTypes.add(CatalogModelType.OT_VSS_EXCH_REPLICA);
	}
	// types for SharePoint GRT
	public final static ArrayList<Integer> allGRTSPTypes = new ArrayList<Integer>();
	static {
		allGRTSPTypes.add(OT_GRT_SP_DB);	
		allGRTSPTypes.add(OT_GRT_SP_SITE);	
		allGRTSPTypes.add(OT_GRT_SP_WEB);	
		allGRTSPTypes.add(OT_GRT_SP_LIST);	
		allGRTSPTypes.add(OT_GRT_SP_FOLDER);	
		allGRTSPTypes.add(OT_GRT_SP_FILE);	
		allGRTSPTypes.add(OT_GRT_SP_VERSION);	
	}

	// types for Exchange GRT
	public final static ArrayList<Integer> allGRTExchangeTypes = new ArrayList<Integer>();
	static {
		//allGRTExchangeTypes.add(OT_GRT_EXCH_MBSDB);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_CALENDAR);
		allGRTExchangeTypes.add(OT_GRT_EXCH_CALENDAR_ITEM);
		allGRTExchangeTypes.add(OT_GRT_EXCH_CONTACTS);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_CONTACTS_ITEM);
		allGRTExchangeTypes.add(OT_GRT_EXCH_CONTACTS_GROUP);
		allGRTExchangeTypes.add(OT_GRT_EXCH_DRAFT);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_JOURNAL);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_NOTES);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_TASKS);	
		//allGRTExchangeTypes.add(OT_GRT_EXCH_PUBLIC_FOLDERS);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_MAILBOX);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_DELETED_ITEMS);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_INBOX);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_OUTBOX);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_SENT_ITEMS);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_FOLDER);	
		allGRTExchangeTypes.add(OT_GRT_EXCH_MESSAGE);	
	}
	
	public final static ArrayList<Integer> rootGRTExchangeTypes = new ArrayList<Integer>();
	static {
		//rootGRTExchangeTypes.add(OT_VSS_EXCH_COMPONENT_SELECTABLE);	
		//rootGRTExchangeTypes.add(OT_VSS_EXCH_COMPONENT_PUBLIC);		
		rootGRTExchangeTypes.add(OT_GRT_EXCH_MBSDB);	
		rootGRTExchangeTypes.add(OT_GRT_EXCH_PUBLIC_FOLDERS);	
	}
	
	public final static ArrayList<Integer> exchSubItemType_mailboxes = new ArrayList<Integer>();
	static {
		exchSubItemType_mailboxes.add(OT_GRT_EXCH_MAILBOX);		
	}

	public final static ArrayList<Integer> exchSubItemType_folders = new ArrayList<Integer>();
	static {
		exchSubItemType_folders.add(OT_GRT_EXCH_CALENDAR);	
		exchSubItemType_folders.add(OT_GRT_EXCH_CONTACTS);	
		exchSubItemType_folders.add(OT_GRT_EXCH_DRAFT);	
		exchSubItemType_folders.add(OT_GRT_EXCH_JOURNAL);	
		exchSubItemType_folders.add(OT_GRT_EXCH_NOTES);	
		exchSubItemType_folders.add(OT_GRT_EXCH_DELETED_ITEMS);	
		exchSubItemType_folders.add(OT_GRT_EXCH_INBOX);	
		exchSubItemType_folders.add(OT_GRT_EXCH_OUTBOX);	
		exchSubItemType_folders.add(OT_GRT_EXCH_SENT_ITEMS);	
		exchSubItemType_folders.add(OT_GRT_EXCH_FOLDER);
	}
	
	public final static ArrayList<Integer> exchSubItemType_calendar_contact_item = new ArrayList<Integer>();
	static {
		exchSubItemType_calendar_contact_item.add(OT_GRT_EXCH_CALENDAR_ITEM);	
		exchSubItemType_calendar_contact_item.add(OT_GRT_EXCH_CONTACTS_ITEM);
		exchSubItemType_calendar_contact_item.add(OT_GRT_EXCH_CONTACTS_GROUP);
	}
	
	public final static ArrayList<Integer> exchSubItemType_non_email_item = new ArrayList<Integer>();
	static {
		exchSubItemType_non_email_item.add(OT_GRT_EXCH_CALENDAR);	
		exchSubItemType_non_email_item.add(OT_GRT_EXCH_CONTACTS);
		exchSubItemType_non_email_item.add(OT_GRT_EXCH_CALENDAR_ITEM);	
		exchSubItemType_non_email_item.add(OT_GRT_EXCH_CONTACTS_ITEM);
		exchSubItemType_non_email_item.add(OT_GRT_EXCH_CONTACTS_GROUP);
	}
	
	public final static ArrayList<Integer> exchSubItemType_messages = new ArrayList<Integer>();
	static {
		exchSubItemType_messages.add(OT_GRT_EXCH_MESSAGE);		
	}

}
