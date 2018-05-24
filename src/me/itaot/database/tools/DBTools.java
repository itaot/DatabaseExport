package me.itaot.database.tools;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

public class DBTools {

	private static DruidDataSource dataSource = new DruidDataSource();

	static {
		dataSource.setDriverClassName(ConfigTools.get("driver"));
		dataSource.setUrl(ConfigTools.get("url"));
		dataSource.setUsername(ConfigTools.get("username"));
		dataSource.setPassword(ConfigTools.get("password"));
		dataSource.setMaxActive(5);
	}

	public DBTools() {

	}

	public static Connection getConnFromPool() throws SQLException {
		return dataSource.getConnection();
	}

	public static void releaseResource() {
		dataSource.close();
	}

}
