package com.mlog.procmgr.status.down;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.control.ProcessStatusCtrlManager;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.dataaccess.H2MDataAccessObject;

public class ProcessStatusDownHandler extends TimeHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private H2MDataAccessObject H2M_dataAccessObject = null;
	private SqlSession H2M_sqlSession = null;

	public ProcessStatusDownHandler(int timeout) {
		super();

		setTimeout(timeout);

		H2M_dataAccessObject = (H2MDataAccessObject) DataAccessManager.getInstance().getDataAccessObject(DS_H2M);

		if (H2M_sqlSession == null) {
			H2M_sqlSession = H2M_dataAccessObject.getSqlSession();
		}

		updateTargetProcessInfo();
	}

	@Override
	public void handler(Object object) {
		if (object == null) {
			selectDownStatusProcess(); // 다운 프로세스 감시. 5분동안 데이터 수집 안될 경우, 프로세스 상태가 다운일 경우
			selectHangStatusProcess(); // 좀비 프로세스 감시.
		}
	}

	/**
	 * 감시대상 프로세스 초기화.
	 */
	@SuppressWarnings("unchecked")
	private void updateTargetProcessInfo() {

		try {
			String procinfo = Utils.readFile(Utils.getProperty(Constant.WATCH_PROCESS_PATH));
			
			List<HashMap<String, Object>> procInfoList = (List<HashMap<String, Object>>) Utils.jsonStrToObject(procinfo)
					.get("PROCESSES");
			
			for (HashMap<String, Object> procInfo : procInfoList) {
				H2M_dataAccessObject.insertTargetProcessInfo(H2M_sqlSession, procInfo);
			}

			H2M_sqlSession.commit();
		} catch (Exception e) {
			H2M_sqlSession.rollback();
			logger.error("", e);
		}
	}

	/**
	 * 다운 프로세스 감시.
	 */
	private int selectDownStatusProcess() {
		int retv = FAILURE;
		
		try {
			List<Map<String, Object>> downProcessList = H2M_dataAccessObject.selectDownStatusProcess(H2M_sqlSession);
			if (downProcessList != null && downProcessList.size() > 0) {
				processContorl(downProcessList);
			}

			H2M_sqlSession.commit();
			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return retv;
	}

	/**
	 * 좀비 프로세스 감시.
	 */
	private int selectHangStatusProcess() {
		int retv = FAILURE;

		try {
			List<Map<String, Object>> hangProcessList = H2M_dataAccessObject.selectMMIStatus(H2M_sqlSession);
			if (hangProcessList != null && hangProcessList.size() > 0) {
				processContorl(hangProcessList);
			}

			H2M_sqlSession.commit();
			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	private void processContorl(List<Map<String, Object>> processList) {
		List<Map<String, Object>> ctrlProcessList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> downProcess : processList) {
			if (downProcess == null) {
				continue;
			}

			if ("on".equals(downProcess.get(WATCH_YN)) || "ON".equals(downProcess.get(WATCH_YN))) {
				ctrlProcessList.add(downProcess);
			}
		}

		Map<String, Object> control = new HashMap<String, Object>();
		control.put(DATA_TYPE, PROCESS_RESTART);
		control.put(PROC_LIST, ctrlProcessList);

		if (control.size() > 0) {
			ProcessStatusCtrlManager.getInstance().address(Utils.objectToJsonStr(control));
		}
	}

}
