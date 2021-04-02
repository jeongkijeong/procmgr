package com.mlog.procmgr.status.down;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.main.ProcessManager;

public class ProcessStatusDownManager implements ProcessManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ProcessStatusDownManager instance = null;
	
	private int worker_count = 1;
	private int active_index = 0;

	private List<TimeHandler> handlerList = null;

	public static ProcessStatusDownManager getInstance() {
		if (instance == null) {
			instance = new ProcessStatusDownManager();
		}

		return instance;
	}

	@Override
	public void start() {
		handlerList = new ArrayList<TimeHandler>();

		TimeHandler handler = null;
		for (int i = 0; i < worker_count; i++) {
			Thread thread = new Thread(handler = new ProcessStatusDownHandler(60));
			thread.start();

			handlerList.add(handler);
		}

		logger.info("start [{}]", getClass().getSimpleName());
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
