package com.mlog.procmgr.dataaccess;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.datasource.MyBasicConnection;

public class H2MDataAccessObject implements DataAccessObject {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MyBasicConnection connection = new MyBasicConnection(DS_H2M);

	public SqlSession getSqlSession() {
		SqlSession sqlSession = connection.openSession();
		return sqlSession;
	}

	public List<Map<String, Object>> selectSystemLogFault(SqlSession session, Map<String, Object> param) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectSystemLogFault(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}

	public int insertSystemLogFault(SqlSession session, Map<String, Object> param) {
		int retv = 0;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.insertSystemLogFault(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public int insertProcessProperty(SqlSession session, Map<String, Object> param) {
		int retv = 0;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.insertProcessProperty(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public List<Map<String, Object>> selectProcessProperty(SqlSession session) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectProcessProperty();
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}
	
	public List<Map<String, Object>> selectSMSPernUserInfo(SqlSession session, Map<String, Object> param) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectSMSPernUserInfo(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}
	

	public List<Map<String, Object>> selectSMSAuthUserInfo(SqlSession session, Map<String, Object> param) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectSMSAuthUserInfo(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}
	
	
	public int insertProcessStatusData(SqlSession session, Map<String, Object> param) {
		int retv = FAILURE;

		if (param == null) {
			return retv;
		}

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.insertProcessStatusData(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public int insertTargetProcessInfo(SqlSession session, Map<String, Object> param) {
		int retv = FAILURE;

		if (param == null) {
			return retv;
		}

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.insertTargetProcessInfo(param);
			logger.debug("insertTargetProcessInfo");

			// insert mmi process info.
			if (param.get("WEIGHT") != null) {
				retv = mapper.insertMmiStatus(param);
				logger.debug("insertMmiStatus");
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public Map<String, Object> selectTargetProcessInfo(SqlSession session, Map<String, Object> param) {
		Map<String, Object> map = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			map = mapper.selectTargetProcessInfo(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return map;
	}

	public List<Map<String, Object>> selectTargetConnectInfo(SqlSession session) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectTargetConnectInfo();
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}

	public List<Map<String, Object>> selectProcessStatusData(SqlSession session) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectProcessStatusData();
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}

	public List<Map<String, Object>> selectDownStatusProcess(SqlSession session) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectDownStatusProcess();
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return list;
	}

	public List<Map<String, Object>> selectHangStatusProcess(SqlSession session, String max_defunct_cnt) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectHangStatusProcess(max_defunct_cnt);
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}

	public int updateDownStatusProcess(SqlSession session) {
		int retv = FAILURE;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.updateDownStatusProcess();
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}
	
	public int updateMMIStatus(SqlSession session, Map<String, Object> param) {
		int retv = FAILURE;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.updateMmiStatus(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public int insertMMIStatus(SqlSession session, Map<String, Object> param) {
		int retv = FAILURE;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			retv = mapper.insertMmiStatus(param);
		} catch (Exception e) {
			logger.error("", e);
		}

		return retv;
	}

	public List<Map<String, Object>> selectMMIStatus(SqlSession session) {
		List<Map<String, Object>> list = null;

		try {
			H2MDataAccessMapper mapper = session.getMapper(H2MDataAccessMapper.class);
			list = mapper.selectMmiStatus();
		} catch (Exception e) {
			logger.error("", e);
		}

		return list;
	}
	
}
