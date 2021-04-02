package com.mlog.procmgr.common;

public interface CommonStr {
	public final String DEFAULT_LOG_FILE_PATH = "./conf/logback.xml";
	public final String DEFAULT_CFG_FILE_PATH = "./conf/server.properties";

	public final String DS_SMS = "SMS";
	public final String DS_MDM = "MDM";
	public final String DS_H2M = "H2M";

	public final static int SUCCESS = +0;
	public final static int FAILURE = -1;

	public final String TO_H2DB = "TO_H2DB";
	public final String TO_FILE = "TO_FILE";

	public final String NAME = "NAME";
	public final String TYPE = "TYPE";

	public final String DATA_TYPE_USER = "DATA_TYPE_USER";
	public final String DATA_TYPE_PROP = "DATA_TYPE_PROP";

	public final String PROCESS_START    = "start";
	public final String PROCESS_CLOSE    = "close";
	public final String PROCESS_KILL9    = "kill9";
	public final String PROCESS_RESTART  = "restart";

	public final String PROC_LIST = "PROC_LIST";
	public final String PROC_NAME = "PROC_NAME";
	public final String PROC_CODE = "PROC_CODE";
	public final String PROC_INDX = "PROC_INDX";
	public final String DATA_TYPE = "TYPE";
	public final String PROC_STAT = "PROC_STAT";
	public final String WATCH_YN  = "WATCH_YN";

	public final String COMMAND = "COMMAND";

	public final String HOST_IP = "HOST_IP";
	public final String HOST_ID = "HOST_ID";
	public final String HOST_PW = "HOST_PW";
	public final String PROMPT  = "PROMPT";

//	public final String LS  = "ls";
//	public final String PWD = "pwd";
}
