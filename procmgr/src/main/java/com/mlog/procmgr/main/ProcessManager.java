package com.mlog.procmgr.main;

import com.mlog.procmgr.common.CommonStr;

public interface ProcessManager extends CommonStr {
	public void start();

	public void close();

	public void address(Object object);
}