package com.mlog.procmgr.dataaccess;

import org.apache.ibatis.session.SqlSession;

import com.mlog.procmgr.common.CommonStr;

public interface DataAccessObject extends CommonStr {
	public SqlSession getSqlSession();
}
