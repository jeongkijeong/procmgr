package com.mlog.procmgr.h2db;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.DataHandler;
import com.mlog.procmgr.main.ProcessManager;

public class H2DatabaseManager implements ProcessManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static H2DatabaseManager instance = null;

	private H2Database h2Database = null;

	private int active_index = 0;
	private int worker_count = 1;

	private List<DataHandler> handlerList = null;
	
	public static synchronized H2DatabaseManager getInstance() {
		if (instance == null) {
			instance = new H2DatabaseManager();
		}

		return instance;
	}

	public H2DatabaseManager() {
		super();
	}

	@Override
	public void start() {
		logger.info("Start H2DataBaseManager");

		if (h2Database == null) {
			h2Database = new H2Database();

			String path = Utils.getProperty(Constant.H2_SCHEMA_PATH);
			String port = Utils.getProperty(Constant.H2_SERVER_PORT);

			if (path != null && port != null) {
				logger.info("schema path {} / server prot {}", path, port);
			} else {
				return;
			}

			// H2 embedded mode start.
			h2Database.start(path, port);
		}
		
		handlerList = new ArrayList<DataHandler>();

		DataHandler handler = null;
		for (int i = 0; i < worker_count; i++) {
			Thread thread = new Thread(handler = new H2DatabaseHandler());
			thread.start();

			handlerList.add(handler);
		}

		String classname = String.format("%-23s", getClass().getSimpleName());
		logger.info("start [{}] / [{}]", classname, worker_count);
	}

	@Override
	public void close() {
		logger.info("Close H2DataBaseManager");

		if (h2Database != null) {
			h2Database.shutdown();
		}
	}

	@Override
	public void address(Object object) {
		handlerList.get(active_index++).put(object);

		if (active_index % worker_count == 0) {
			active_index = 0;
		}
	}
}
