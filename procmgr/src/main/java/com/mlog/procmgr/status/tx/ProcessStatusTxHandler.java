package com.mlog.procmgr.status.tx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medialog.meerkat.handler.MeerKat;
import com.mlog.procmgr.common.Utils;
import com.mlog.procmgr.context.TimeHandler;
import com.mlog.procmgr.control.ProcessStatusCtrlManager;
import com.mlog.procmgr.dataaccess.DataAccessManager;
import com.mlog.procmgr.dataaccess.H2MDataAccessObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

public class ProcessStatusTxHandler extends TimeHandler implements MeerKat {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<ChannelId, ChannelHandlerContext> connectdChannel = null;
	
	private H2MDataAccessObject H2M_dataAccessObject = null;
	
	private SqlSession H2M_sqlSession = null;
	
	private String bufferedData = null;

	public ProcessStatusTxHandler(int timeout) {
		super();
		
		setTimeout(timeout);
		connectdChannel = new HashMap<ChannelId, ChannelHandlerContext>();

		H2M_dataAccessObject = (H2MDataAccessObject) DataAccessManager.getInstance().getDataAccessObject(DS_H2M);
		
		if (H2M_sqlSession == null) {
			H2M_sqlSession = H2M_dataAccessObject.getSqlSession();
		}
	}

	/**
	 * 프로세스 상태조회 후 접속 클라이언트에 전송.
	 */
	@Override
	public void handler(Object object) {
		if (H2M_sqlSession == null) {
			return;
		}

		try {
			String message = null;
			if (object == null) {
				List<Map<String, Object>> list = H2M_dataAccessObject.selectProcessStatusData(H2M_sqlSession);
				if (list != null && list.size() > 0) {
					message = Utils.objectToJsonStr(list);
					bufferedData = message;
				}
			} else {
				message = (String) object;
			}

			sendMessage(message);

			H2M_sqlSession.commit();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void channelActived(ChannelHandlerContext ctx) {
		connectdChannel.put(ctx.channel().id(), ctx);

		if (bufferedData != null) {
			sendMessage(ctx, bufferedData);
		}
	}

	@Override
	public void channelRemoved(ChannelHandlerContext ctx) {
		connectdChannel.remove(ctx.channel().id());
	}

	@Override
	public synchronized void recvMessage(String msg) {
		ProcessStatusCtrlManager.getInstance().address(msg);
	}

	@Override
	public void recvMessage(ChannelHandlerContext ctx, Object msg) {
		logger.info("recv message : {}", msg);
		recvMessage(msg.toString());
	}

	@Override
	public void sendMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}

		if (connectdChannel.size() == 0) {
			return;
		}

		for (Entry<ChannelId, ChannelHandlerContext> entry : connectdChannel.entrySet()) {
			ChannelHandlerContext context = entry.getValue();

			if (context != null) {
				context.writeAndFlush(msg);
				
				logger.info("send message : {}", msg);
			}
		}
	}

	public void sendMessage(ChannelHandlerContext context, String msg) {
		if (context != null) {
			context.writeAndFlush(msg);

			logger.info("send message : {}", msg);
		}
	}
}
