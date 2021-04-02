package com.mlog.procmgr.h2db;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;

import org.h2.tools.RunScript;
import org.h2.tools.Server;

import com.mlog.procmgr.common.CommonStr;
import com.mlog.procmgr.dataaccess.DataAccessManager;

public class H2Database {
	private Server server = null;

	public H2Database() {
		super();
	}

	public void start(String path, String port) {
		try {
			server = null;
			
			if (port != null && port.length() > 0) {
				server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", port).start();
			} else {
				server = Server.createTcpServer("-tcpAllowOthers").start();
			}

			System.out.println("Server started and connection is open.");
			System.out.println("URL: jdbc:h2:" + server.getURL());

			Connection conn = DataAccessManager.getInstance().getDataAccessObject(CommonStr.DS_H2M).getSqlSession()
					.getConnection();

			if (path != null && path.length() > 0) {
				File file = new File(path);
				if (file.exists() == true) {
					RunScript.execute(conn, new FileReader(file));
				}
			}

			conn.close();

		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}
	
	public void shutdown() {
		try {
			if (server != null) {
				server.stop();
				server.shutdown();
			}
		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}

}
