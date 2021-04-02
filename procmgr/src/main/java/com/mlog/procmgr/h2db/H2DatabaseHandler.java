package com.mlog.procmgr.h2db;

import java.io.File;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.Constant;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.DataHandler;
import com.mlog.procmgr.dataaccess.DataAccessManager;


public class H2DatabaseHandler extends DataHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private HashMap<String, Object> tableMap = new HashMap<>();

	public H2DatabaseHandler() {
		super();
		
		Map<String, String> user = new HashMap<String, String>();
		user.put("SMS_PERN_USER_INFO", "FALT_GBN_CD, USER_ID, USER_TEL_NO");
		tableMap.put(DATA_TYPE_USER, user);

		Map<String, String> prop = new HashMap<String, String>();
		prop.put("PROCESS_PROPERTY", "PROPERTY_KEY, PROPERTY_VAL");
		tableMap.put(DATA_TYPE_PROP, prop);

		csvFileToDbTable(DATA_TYPE_PROP);
		csvFileToDbTable(DATA_TYPE_USER);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handler(Object object) {
		Map<String, String> map = (Map<String, String>)object;
		
		String type = map.get(TYPE);
		String name = map.get(NAME);

		switch (type) {
		case TO_H2DB:
			csvFileToDbTable(name);

			break;
		case TO_FILE:
			dbTableToCsvFile(name);

			break;
		default:
			break;
		}
	}

	public int dbTableToCsvFile(String key) {
		int retv = FAILURE;

		SqlSession sqlSession = DataAccessManager.getInstance().getDataAccessObject(DS_H2M).getSqlSession();
		if (sqlSession == null) {
			return retv;
		}
		
		String baseDirectory = getBaseDirectory(key);
		
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) tableMap.get(key);
			
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String name = entry.getKey();
				String cols = entry.getValue();
				
				Statement stmt = sqlSession.getConnection().createStatement();
				String srcPath = baseDirectory + "/" + name + ".CSV";

				String sql = String.format("CALL CSVWRITE('%s', 'SELECT %s FROM %s', 'UTF-8', '||')", srcPath, cols,
						name);
				stmt.execute(sql);
				logger.debug(sql);
			}

			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}

		return retv;
	}

	public int csvFileToDbTable(String key) {
		int retv = FAILURE;

		SqlSession sqlSession = DataAccessManager.getInstance().getDataAccessObject(DS_H2M).getSqlSession();
		if (sqlSession == null) {
			return retv;
		}
		
		String baseDirectory = getBaseDirectory(key);

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) tableMap.get(key);
			
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String name = entry.getKey();
				String cols = entry.getValue();

				String targetPath = baseDirectory + "/" + name + ".CSV";

				File file = new File(targetPath);
				if (file.exists() == false) {
					return retv;
				}
	
				String path = file.getAbsolutePath();
				if (path == null) {
					return retv;
				}
				
				String delete = String.format("DELETE FROM %s", name);
				
				String insert = String.format(
						"INSERT INTO %s (%s) SELECT %s FROM CSVREAD('%s', null, 'charset=UTF-8 fieldSeparator=||')",
						name, cols, cols, path);

				Statement stmt = sqlSession.getConnection().createStatement();
				stmt.execute(delete);
				stmt.execute(insert);
				logger.debug(insert);
			}
			
			sqlSession.commit();
			
			return SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}

		return retv;
	}
	
	private String getBaseDirectory(String key) {
		String baseDirectory = null;

		switch (key) {
		case DATA_TYPE_PROP:
			baseDirectory = Utils.getProperty(Constant.H2_BACKUP_PATH_PROP);

			break;
		case DATA_TYPE_USER:
			baseDirectory = Utils.getProperty(Constant.H2_BACKUP_PATH_USER);

			break;
		default:
			break;
		}

		return baseDirectory;
	}
}
