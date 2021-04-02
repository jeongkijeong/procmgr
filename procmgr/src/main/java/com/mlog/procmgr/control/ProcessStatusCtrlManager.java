package com.mlog.procmgr.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.context.DataHandler;
import com.mlog.procmgr.main.ProcessManager;

public class ProcessStatusCtrlManager implements ProcessManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ProcessStatusCtrlManager instance = null;

	private int worker_count = 1;
	private int active_index = 0;

	private List<DataHandler> handlerList = null;

	public static ProcessStatusCtrlManager getInstance() {
		if (instance == null) {
			instance = new ProcessStatusCtrlManager();
		}

		return instance;
	}

	@Override
	public void start() {
		try {
			handlerList = new ArrayList<DataHandler>();
			DataHandler handler = null;
			
			for (int i = 0; i < worker_count; i++) {
				Thread thread = new Thread(handler = new ProcessStatusCtrlHandler());
				thread.start();
				
				handlerList.add(handler);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info("start [{}] / [{}]", getClass().getSimpleName(), worker_count);
	}

	@Override
	public void close() {
		Constant.RUN = false;
	}

	@Override
	public void address(Object object) {
		if (handlerList != null && handlerList.size() >= active_index) {
			handlerList.get(active_index++).put(object);
		} else {
			return;
		}

		if (active_index % worker_count == 0) {
			active_index = 0;
		}
	}
	
	
}
