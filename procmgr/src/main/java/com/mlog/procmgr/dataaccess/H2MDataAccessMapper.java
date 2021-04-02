package com.mlog.procmgr.dataaccess;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.mlog.procmgr.datasource.Mapper;

public interface H2MDataAccessMapper extends Mapper {

	public int insertSystemLogFault(Map<String, Object> param);

	public List<Map<String, Object>> selectSystemLogFault(Map<String, Object> param);
	
	public int insertProcessProperty(Map<String, Object> param);

	public List<Map<String, Object>> selectProcessProperty();
	
	public List<Map<String, Object>> selectSMSPernUserInfo(Map<String, Object> param);

	public List<Map<String, Object>> selectSMSAuthUserInfo(Map<String, Object> param);

	public int insertProcessStatusData(Map<String, Object> param);

	public Map<String, Object> selectTargetProcessInfo(Map<String, Object> param);

	public List<Map<String, Object>> selectTargetConnectInfo();
	
	public int insertTargetProcessInfo(Map<String, Object> param);

	public int insertMmiStatus(Map<String, Object> param);

	public int updateMmiStatus(Map<String, Object> param);

	public List<Map<String, Object>> selectMmiStatus();

	public int updateDownStatusProcess();

	public List<Map<String, Object>> selectProcessStatusData();

	public List<Map<String, Object>> selectDownStatusProcess();

	public List<Map<String, Object>> selectHangStatusProcess(@Param("MAX_DEFUNCT_CNT") String max_defunct_cnt);

}
