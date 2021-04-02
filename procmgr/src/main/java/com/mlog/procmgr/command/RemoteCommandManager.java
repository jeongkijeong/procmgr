package com.mlog.procmgr.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.dataaccess.H2MDataAccessObject;
import com.mlog.procmgr.main.ProcessManager;

public class RemoteCommandManager implements ProcessManager {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static RemoteCommandManager instance = null;
	
	private H2MDataAccessObject H2M_dataAccessObject = null;
	private SqlSession H2M_sqlSession = null;

	private Map<String, TimeHandler> handlerMap = null;

	public static RemoteCommandManager getInstance() {
		if (instance == null) {
			instance = new RemoteCommandManager();
		}

		return instance;
	}

	public RemoteCommandManager() {
		super();
	}

	@Override
	public void start() {
		H2M_dataAccessObject = (H2MDataAccessObject) DataAccessManager.getInstance().getDataAccessObject(DS_H2M);

		if (H2M_sqlSession == null) {
			H2M_sqlSession = H2M_dataAccessObject.getSqlSession();
		}

		try {
			List<Map<String, Object>> remoteList = H2M_dataAccessObject.selectTargetConnectInfo(H2M_sqlSession);
			if (remoteList == null || remoteList.size() == 0) {
				return;
			}

			handlerMap = new HashMap<String, TimeHandler>();

			TimeHandler handler = null;
			for (Map<String, Object> remote : remoteList) {
				Thread thread = new Thread(handler = new RemoteCommandHandler(remote, 30));
				thread.start();

				handlerMap.put((String) remote.get(HOST_IP), handler);
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			H2M_sqlSession.close();
		}

		logger.info("start [{}] / [{}]", getClass().getSimpleName(), handlerMap.size());
	}

	@Override
	public void close() {
		Constant.RUN = false;

		try {
			for (String key : handlerMap.keySet()) {
				((RemoteCommandHandler) handlerMap.get(key)).disconnect();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void address(Object object) {
		try {
			if (handlerMap == null || handlerMap.size() == 0) {
				return;
			}

			String key = (String) ((Map<String, Object>) object).get(HOST_IP);
			if (key != null) {
				handlerMap.get(key).put(object);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
