package com.mlog.procmgr.status.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medialog.meerkat.handler.MeerKat;
import com.medialog.meerkat.server.tcp.TcpServer;
import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.main.ProcessManager;

public class ProcessStatusRxManager implements ProcessManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ProcessStatusRxManager instance = null;

	private TcpServer meerkatServer = null;
	private MeerKat meerKat = null;

	public static ProcessStatusRxManager getInstance() {
		if (instance == null) {
			instance = new ProcessStatusRxManager();
		}

		return instance;
	}

	@Override
	public void start() {
		String port = Utils.getProperty(Constant.PROCMGR_TCP_PORT);

		if (meerkatServer == null) {
			meerkatServer = new TcpServer(port, meerKat = new ProcessStatusRxHandler());

			Thread meerkatThread = new Thread(meerkatServer);
			meerkatThread.start();

			Thread handlerThread = new Thread((TimeHandler) meerKat);
			handlerThread.start();
		}
		
		logger.info("start [{}] / [{}]", getClass().getSimpleName(), port);
	}

	@Override
	public void close() {
		if (meerkatServer != null) {
			meerkatServer.close();
		}
	}

	@Override
	public void address(Object object) {
		if (meerKat != null) {
			((TimeHandler) meerKat).put(object);
		}
	}
}
