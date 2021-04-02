package com.mlog.procmgr.datasource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlog.procmgr.common.CommonStr;
import com.mlog.procmgr.common.Utils;

public class MyBasicConnection implements CommonStr{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private SqlSessionFactory sessionFactory = null;
	
	private String DS_NAME = null;

	public MyBasicConnection() {
		super();
	}

	public MyBasicConnection(String dataSource) {
		super();
		this.DS_NAME = dataSource;
	}

	public SqlSessionFactory init() {
		try {
			String configPath = null;

			switch (DS_NAME) {
			case DS_H2M:
				configPath = "db/config/H2M_mybiats_cfg.xml";
				break;
			case DS_MDM:
				configPath = "db/config/MDM_mybiats_cfg.xml";
				break;
			case DS_SMS:
				configPath = "db/config/SMS_mybiats_cfg.xml";
				break;
			default:
				break;
			}

			sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(configPath),
					Utils.getDataBaseProperties(DS_NAME));
		} catch (Exception e) {
			logger.error("", e);
		}

		return sessionFactory;
	}

	public SqlSession openSession() {
		SqlSession sqlSession = null;

		if (sessionFactory == null) {
			sessionFactory = init();
		}

		try {
			sqlSession = sessionFactory.openSession();
		} catch (Exception e) {
			logger.error("", e);
		}

		return sqlSession;
	}
}
