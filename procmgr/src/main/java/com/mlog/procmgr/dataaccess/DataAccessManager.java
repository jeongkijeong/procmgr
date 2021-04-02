package com.mlog.procmgr.dataaccess;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.main.ProcessManager;

public class DataAccessManager implements ProcessManager{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static DataAccessManager instance = null;

	private Map<String, DataAccessObject> dataAccessObjectFactory = null;

	public static DataAccessManager getInstance() {
		if (instance == null) {
			instance = new DataAccessManager();
		}

		return instance;
	}

	@Override
	public void start() {
		dataAccessObjectFactory = new HashMap<String, DataAccessObject>();

		dataAccessObjectFactory.put(DS_H2M, new H2MDataAccessObject());
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void address(Object object) {
		// TODO Auto-generated method stub
	}
	
	public DataAccessObject getDataAccessObject(String dataSource) {
		DataAccessObject dataAccessObject = null;

		if (dataAccessObjectFactory != null) {
			dataAccessObject = dataAccessObjectFactory.get(dataSource);
		}

		if (dataAccessObject != null) {
			logger.debug("success get dataAccessObject for {} DB", dataSource);
		} else {
			logger.error("failure get dataAccessObject for {} DB", dataSource);
		}

		return dataAccessObject;
	}
}
