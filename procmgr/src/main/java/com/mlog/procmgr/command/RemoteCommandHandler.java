package com.mlog.procmgr.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.medialog.rcommander.RCommander;
import com.medialog.rcommander.RCommander.Protocol;
import com.mlog.procmgr.context.TimeHandler;

public class RemoteCommandHandler extends TimeHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, Object> remote = null;
	private RCommander rcommander = null;

	public RemoteCommandHandler(Map<String, Object> remote, int timeout) {
		super();

		this.remote = remote;
		connect();

		this.setTimeout(timeout);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handler(Object object) {
		String command = null;

		if (object == null) {
			command = "ls";
		} else {
			command = (String) ((Map<String, Object>) object).get(COMMAND);
		}

		try {
			execute(command);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public int connect() {
		int retv = FAILURE;

		try {
			if (remote.get(HOST_IP) == null || "127.0.0.1".equals((String) remote.get(HOST_IP))) {
				return SUCCESS;
			}

			if (rcommander == null) {
				rcommander = new RCommander(Protocol.SSH);
			}

			// 1: success, 0 : failure
			retv = rcommander.connect((String) remote.get(HOST_IP), (String) remote.get(HOST_ID),
					(String) remote.get(HOST_PW), (String) remote.get(PROMPT));

			if (retv > 0) {
				retv = SUCCESS;
				logger.debug("success connect to [{}]", remote.get(HOST_IP));
			} else {
				rcommander = null;
				retv = FAILURE;
				logger.error("failure connect to [{}]", remote.get(HOST_IP));
			}

			logger.debug("{} / {} / {} / {}", (String) remote.get(HOST_IP), (String) remote.get(HOST_ID),
					(String) remote.get(HOST_PW), (String) remote.get(PROMPT));
			
		} catch (Exception e) {
			rcommander.disconnect();
			rcommander = null;

			logger.error("", e);
		}
		
		return retv;
	}

	public int execute(String command) {
		int retv = FAILURE;
		String result = "";
		
		try {
			logger.debug("execute command : [{}]", command);

			// skip remote command in local host.
			if (remote.get(HOST_IP) == null || "127.0.0.1".equals((String) remote.get(HOST_IP))) {
				Process process = Runtime.getRuntime().exec(command);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((result = br.readLine()) != null) {
					result += result;
				}

				br.close();
			} else {
				// remote control.
				if (rcommander == null || rcommander.isConnect() == false) {
					connect();
				}
	
				if (rcommander != null) {
					result = rcommander.sendCommand(command);
				}
			}

			logger.debug("execute command : [{}] / [{}]", command, result);
			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return retv;
	}

	public int disconnect() {
		int retv = FAILURE;

		try {
			if (remote.get(HOST_IP) == null || "127.0.0.1".equals((String) remote.get(HOST_IP))) {
				return SUCCESS;
			}

			if (rcommander != null) {
				rcommander.disconnect();
			}
			
			retv = SUCCESS;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return retv;
	}
	
}
