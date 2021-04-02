package com.mlog.procmgr.context;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.command.RemoteCommandManager;
import com.mlog.procmgr.common.CommonStr;
import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.control.ProcessStatusCtrlManager;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.h2db.H2DatabaseManager;
import com.mlog.procmgr.main.ProcessManager;
import com.mlog.procmgr.status.down.ProcessStatusDownManager;
import com.mlog.procmgr.status.rx.ProcessStatusRxManager;
import com.mlog.procmgr.status.tx.ProcessStatusTxManager;

public class ContextManager implements CommonStr {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<ProcessManager> managerList = null;
	
	public static ContextManager instance = null;

	public static ContextManager getInstance() {
		if (instance == null) {
			instance = new ContextManager();
		}

		return instance;
	}

	public ContextManager() {
		super();

		managerList = new ArrayList<ProcessManager>();

		managerList.add(DataAccessManager.getInstance());
		managerList.add(H2DatabaseManager.getInstance());

		managerList.add(ProcessStatusTxManager.getInstance());   // 프로세스 상태전송 및 제어요청 수신.
		managerList.add(ProcessStatusRxManager.getInstance());   // 프로세스 상태수신.

		managerList.add(ProcessStatusDownManager.getInstance()); // 프로세스 다운감시.
		managerList.add(ProcessStatusCtrlManager.getInstance()); // 프로세스 제어요청.
		managerList.add(RemoteCommandManager.getInstance());
	}

	/**
	 * 컨텍스트 매니저 시작.
	 */
	public int startManager() {
		logger.info(this.getClass().getSimpleName() + " start");

		Constant.RUN = true;
		try {
			for (ProcessManager manager : managerList) {
				manager.start();
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info(this.getClass().getSimpleName() + " start completed");
		
		return -1;
	}

	/**
	 * 컨텍스트 매니저 종료.
	 */
	public int closeManager() {
		logger.info(this.getClass().getSimpleName() + " close");

		Constant.RUN = false;
		
		try {
			for (ProcessManager manager : managerList) {
				manager.close();
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info(this.getClass().getSimpleName() + " close completed");
		System.exit(0);
		
		return -1;
	}
}
