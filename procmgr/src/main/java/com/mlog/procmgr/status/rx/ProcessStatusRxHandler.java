package com.mlog.procmgr.status.rx;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medialog.meerkat.handler.MeerKat;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.dataaccess.H2MDataAccessObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

public class ProcessStatusRxHandler extends TimeHandler implements MeerKat {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<ChannelId, ChannelHandlerContext> connectdChannel = null;
	
	private H2MDataAccessObject H2M_dataAccessObject = null;
	private SqlSession H2M_sqlSession = null;

	public ProcessStatusRxHandler() {
		super();
		connectdChannel = new HashMap<ChannelId, ChannelHandlerContext>();

		H2M_dataAccessObject = (H2MDataAccessObject) DataAccessManager.getInstance().getDataAccessObject(DS_H2M);
		
		if (H2M_sqlSession == null) {
			H2M_sqlSession = H2M_dataAccessObject.getSqlSession();
		}
	}

	@Override
	public void handler(Object object) {
		if (object == null) {
			sendMessage("");
		}
	}

	@Override
	public void channelActived(ChannelHandlerContext ctx) {
		connectdChannel.put(ctx.channel().id(), ctx);
	}

	@Override
	public void channelRemoved(ChannelHandlerContext ctx) {
		connectdChannel.remove(ctx.channel().id());
	}

	/**
	 * 감시대상 프로세스 상태정보 수신 및 저장
	 */
	@Override
	public synchronized void recvMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}

		if (H2M_sqlSession == null) {
			return;
		}

		try {
			Map<String, Object> param = Utils.jsonStrToObject(msg);
			if (param == null || param.size() == 0) {
				return;
			}

			int retv = 0;
/*			if (param.get("PID_INFO") != null) {
				retv = H2M_dataAccessObject.insertProcessStatusData(H2M_sqlSession, param);
			} else {
				retv = H2M_dataAccessObject.updateMMIStatus(H2M_sqlSession, param);
			}
*/

			// S : 프로세스 상태, H : MMI 행
			if ("S".equalsIgnoreCase((String) param.get("GBN"))) {
				retv = H2M_dataAccessObject.insertProcessStatusData(H2M_sqlSession, param);
			} else {
				retv = H2M_dataAccessObject.updateMMIStatus(H2M_sqlSession, param);
			}

			Thread.sleep(100);
			if (retv == FAILURE) {
				logger.error("failure insert process status : {}", msg);
			} else {
				logger.debug("success insert process status : {}", msg);
			}

			H2M_sqlSession.commit();

			// Thread.sleep(100);
		} catch (Exception e) {
			H2M_sqlSession.rollback();
			logger.error("", e);
		}
	}

	@Override
	public void recvMessage(ChannelHandlerContext ctx, Object msg) {
		logger.info("recv message : {}", msg);
		recvMessage(msg.toString());
	}

	@Override
	public void sendMessage(String msg) {
		for (Entry<ChannelId, ChannelHandlerContext> entry : connectdChannel.entrySet()) {
			ChannelHandlerContext context = entry.getValue();

			if (context != null) {
				context.writeAndFlush(msg);
			}
		}
	}

}
