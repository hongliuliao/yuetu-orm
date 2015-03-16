package com.finallygo.collect.inter.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.finallygo.build.pojo.MyField;
import com.finallygo.db.utils.ConnectHelper;
import com.finallygo.db.utils.DataBaseHelper;

public class BaseBuildMyTable {
	/**
	 * �����������
	 * @param tableName ����
	 * @param fields �ֶ�
	 * @return insert���
	 */
	public String buildInsertSql(String tableName,MyField[] fields){
		String sql="insert into "+tableName+" values(";
		//Field[] fields=this.getTableField(tableName);
		for(int i=0;i<fields.length;i++){
			sql=sql+"?";
			if(i!=fields.length-1){
				sql=sql+",";
			}
		}
		sql=sql+")";
		return sql;
	}
	/**
	 * �����������µ�update���
	 * @param tableName ����
	 * @param fields �ֶ�
	 * @return update���
	 */
	public String buildUpdateSql(String tableName,MyField[] fields){
		String sql="update "+tableName+" set ";
//		Field[] fields=this.getTableField(tableName);
		String sql1="";//������ƴ�ӵ�sql
		String sql2=" where 1=1 ";//����ƴ�ӵ�sql
		for(int i=0;i<fields.length;i++){
			if(!fields[i].isKey()){
				sql1=sql1+fields[i].getFieldName()+"=?,";
			}else{
				sql2=sql2+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		if(sql1.length()>1){
			sql1=sql1.substring(0, sql1.length()-1);//��ȡ���һ������
		}
		sql=sql+sql1+sql2;
		return sql;
	}
	/**
	 * ��������ɾ����delete���
	 * @param tableName ����
	 * @param fields �ֶ�
	 * @return delete���
	 */
	public String buildDeleteSql(String tableName,MyField[] fields){
		String sql="delete from "+tableName+" where 1=1 ";
//		Field[] fields=this.getTableField(tableName);
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				sql=sql+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		return sql;
	}
	public String buildSelectSql(String tableName,MyField[] fields){ 
		String sql="select * from "+tableName+" where 1=1 ";
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				sql=sql+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		return sql;
	}
	public Map getForeignKey(String tableName) {
		Connection conn=ConnectHelper.getConn();
		Map foreignMap=new HashMap();
		ResultSet rs=null;
		try {
			DatabaseMetaData metaData=conn.getMetaData();
			rs=metaData.getImportedKeys(conn.getCatalog(), metaData.getUserName(), tableName);
			
			while(rs.next()){
				String pkTableName=rs.getString("PKTABLE_NAME");
				String pkColumnName=rs.getString("PKCOLUMN_NAME");
				String fkTableName=rs.getString("FKTABLE_NAME");
				String fkColumnName=rs.getString("FKCOLUMN_NAME");
				foreignMap.put(fkTableName+"."+fkColumnName, pkTableName+"."+pkColumnName);
			}
			
		} catch (SQLException e) {
			//������ֵ��쳣��ô����?
		} finally{
			DataBaseHelper.closeRs(rs);
			DataBaseHelper.closeConn(conn);
		}
		return foreignMap; 
	}
}
