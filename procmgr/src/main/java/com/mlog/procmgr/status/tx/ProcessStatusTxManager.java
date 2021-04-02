package com.mlog.procmgr.status.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medialog.meerkat.handler.MeerKat;
import com.medialog.meerkat.server.tcp.TcpServer;
import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.main.ProcessManager;

public class ProcessStatusTxManager implements ProcessManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static ProcessStatusTxManager instance = null;

	private TcpServer meerkatServer = null;
	private MeerKat meerKat = null;

	public static ProcessStatusTxManager getInstance() {
		if (instance == null) {
			instance = new ProcessStatusTxManager();
		}

		return instance;
	}

	@Override
	public void start() {
		String port = Utils.getProperty(Constant.PROCMON_TCP_PORT);

		if (meerkatServer == null) {
			meerkatServer = new TcpServer(port, meerKat = new ProcessStatusTxHandler(10));

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
