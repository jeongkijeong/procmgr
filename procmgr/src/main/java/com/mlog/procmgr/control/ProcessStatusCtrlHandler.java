package com.mlog.procmgr.control;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.command.RemoteCommandManager;
import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.DataHandler;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.dataaccess.H2MDataAccessObject;

@SuppressWarnings("unchecked")
public class ProcessStatusCtrlHandler extends DataHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, Object> commandsMap = null;

	private H2MDataAccessObject H2M_dataAccessObject = null;
	private SqlSession H2M_sqlSession = null;

	public ProcessStatusCtrlHandler() {
		super();

		H2M_dataAccessObject = (H2MDataAccessObject) DataAccessManager.getInstance().getDataAccessObject(DS_H2M);
		
		if (H2M_sqlSession == null) {
			H2M_sqlSession = H2M_dataAccessObject.getSqlSession();
		}
	}

	@Override
	public void handler(Object object) {
		if (object == null) {
			return;
		}

		// 프로세스 제어요청 수행
		int retv = 0;
		try {
			Map<String, Object> param = Utils.jsonStrToObject((String) object);
			String type = (String) param.get(DATA_TYPE);

			switch (type) {
			case PROCESS_RESTART:
				retv = processClose(param);
				retv = processStart(param);

				break;
			case PROCESS_START:
				retv = processStart(param);

				break;
			case PROCESS_CLOSE:
				retv = processClose(param);

				break;
			case PROCESS_KILL9:
				retv = processShutdown(param);

				break;
			default:
				break;
			}

			if (retv == FAILURE) {
				logger.debug("failure process control request type : {}", type);
			} else {
				logger.debug("success process control request type : {}", type);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/*
	 * 프로세스 실행 명령어 수행
	 * */
	private int processStart(Map<String, Object> param) {
		int retv = FAILURE;
		
		try {
			List<Map<String, Object>> processList = (List<Map<String, Object>>) param.get(PROC_LIST);
			if (processList == null || processList.size() == 0) {
				return SUCCESS;
			}
			
			for (Map<String, Object> process : processList) {
				Map<String, Object> target = H2M_dataAccessObject.selectTargetProcessInfo(H2M_sqlSession, process);

				String command = null;
				if ((command = getProcessControlCommand(PROCESS_START, target)) == null) {
					continue;
				}

				target.put(COMMAND, command);
				delivery(target);
			}

			H2M_sqlSession.commit();

			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	/*
	 * 프로세스 종료 명령어 수행
	 * */
	private int processClose(Map<String, Object> param) {
		int retv = FAILURE;

		try {
			List<Map<String, Object>> processList = (List<Map<String, Object>>) param.get(PROC_LIST);
			if (processList == null || processList.size() == 0) {
				return SUCCESS;
			}

			for (Map<String, Object> process : processList) {
				Map<String, Object> target = H2M_dataAccessObject.selectTargetProcessInfo(H2M_sqlSession, process);

				String command = null;
				if ((command = getProcessControlCommand(PROCESS_CLOSE, target)) == null) {
					continue;
				}

				target.put(COMMAND, command);
				delivery(target);
			}

			retv = SUCCESS;
			Thread.sleep(5000);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	/*
	 * 프로세스 강제종료 명령어 수행
	 * */
	private int processShutdown(Map<String, Object> param) {
		int retv = FAILURE;

		try {
			List<Map<String, Object>> processList = (List<Map<String, Object>>) param.get(PROC_LIST);
			if (processList == null || processList.size() == 0) {
				return SUCCESS;
			}

			for (Map<String, Object> process : processList) {
				Map<String, Object> target = H2M_dataAccessObject.selectTargetProcessInfo(H2M_sqlSession, process);

				String command = null;
				if ((command = getProcessControlCommand(PROCESS_KILL9, target)) == null) {
					continue;
				}

				target.put(COMMAND, command);
				delivery(target);
			}
			
			retv = SUCCESS;
			Thread.sleep(3000);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	
	private String getProcessControlCommand(String type, Map<String, Object> param) {
		String command = null;

		try {
			if (commandsMap == null) {
				String path = Utils.readFile(Utils.getProperty(Constant.COMMANDS_INFO_PATH));
				if (path != null) {
					commandsMap = Utils.jsonStrToObject(path);
				}
			}

			Object object = commandsMap.get((String) param.get(PROC_CODE));
			if (object != null) {
				command = ((Map<String, String>) object).get(type);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}

		return command;
	}

	private void delivery(Map<String, Object> param) {
		try {
			RemoteCommandManager.getInstance().address(Utils.jsonStrToObject(Utils.objectToJsonStr(param)));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
