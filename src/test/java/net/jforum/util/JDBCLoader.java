/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * Runs a batch of sql statements
 * @author Rafael Steil
 */
public class JDBCLoader {
	private static final Logger logger = Logger.getLogger(JDBCLoader.class);
	private Session session;

	public JDBCLoader(Session session) {
		this.session = session;
	}

	/**
	 * The sql file to load, relative to the classpath
	 * @param sqlfile
	 */
	public void run(String sqlfile) {
		BufferedReader reader = null;
		FileReader fileReader = null;

		try {
			fileReader = new FileReader(this.getClass().getResource(sqlfile).getFile());
			reader = new BufferedReader(fileReader);

			String line = null;

			while ((line = reader.readLine()) != null) {
				if (!StringUtils.isEmpty(line)) {
					logger.debug("JDBCLoader: [Running] " + line);
					this.runStatement(line);
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (fileReader != null) {
				try { fileReader.close(); }
				catch (Exception e) {}
			}

			if (reader != null) {
				try { reader.close(); }
				catch (Exception e) {}
			}
		}
	}

	private void runStatement(String sql) throws SQLException {
		SQLQuery query = session.createSQLQuery(sql);
		query.executeUpdate();
	}
}
