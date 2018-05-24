package me.itaot.write.database.oracle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import me.itaot.database.tools.ConfigTools;
import me.itaot.database.tools.DBTools;
import me.itaot.write.word.WriteWordFile;

public class OracleTableExport {
	
	private static final Logger log = Logger.getLogger(OracleTableExport.class);

	private static final String SQL_TABLE_STRUCT = "SELECT a.column_name AS column_name,a.data_type AS data_type,a.data_length as data_length,a.nullable AS nullable,comments  FROM user_tab_columns a LEFT JOIN user_col_comments b ON (a.table_name = b.table_name AND a.column_name = b.column_name) WHERE a.table_name = ?  ORDER BY column_id";

	private static final String SQL_QUERY_ALL_TABLES = "select a.table_name as table_name,b.comments as comments from user_tables a,user_tab_comments b where a.table_name = b.table_name and  a.table_name not like '%$%' and a.table_name not like '%BAK_BWZL_ZBM%' order by table_name";

	public static void main(String args[]) throws SQLException, IOException {
		WriteWordFile wordFile = new WriteWordFile(ConfigTools.get("file"));
		write(wordFile);
	}

	public static void write(WriteWordFile wordFile) throws SQLException, IOException {

		Connection connection = DBTools.getConnFromPool();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(SQL_QUERY_ALL_TABLES);
		while (rs.next()) {
			String tableName = rs.getString("table_name");
			String tableComments = rs.getString("comments");
			log.info("表名：" + tableName);
			long startTime = System.currentTimeMillis();
			
			Connection conn2 = DBTools.getConnFromPool();
			PreparedStatement pmtm = conn2.prepareStatement(SQL_TABLE_STRUCT);
			pmtm.setString(1, tableName);
			ResultSet tableStructRs = pmtm.executeQuery();
			
			wordFile.writeTableStruct(tableName, tableComments, tableStructRs);
			tableStructRs.close();
			pmtm.close();
			conn2.close();
			
			long endTime = System.currentTimeMillis();
			log.info("写入完成，耗时" + (endTime - startTime) + "ms");
		}
		DBTools.releaseResource();
		WriteWordFile.flush();
	}

}
