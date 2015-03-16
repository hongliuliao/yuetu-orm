package com.finallygo.yuetu.orm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.finallygo.build.pojo.MyField;
import com.finallygo.build.pojo.MyTable;
import com.finallygo.collect.data.CollectData;
import com.finallygo.common.utils.CommonUtils;
import com.finallygo.db.utils.ConnectHelper;
import com.finallygo.db.utils.JdbcTemplate;
import com.finallygo.yuetu.orm.factory.OrmRuleFactory;
import com.finallygo.yuetu.orm.rule.IOrmRule;
/**
 * ģ��hibernateʵ���Լ���session
 * @author user
 *
 */
public class MySession {
	
	private IOrmRule rule=OrmRuleFactory.getRuleObject();
	
	/**
	 * �������
	 * @param obj Ҫ����Ķ���
	 */
	public void save(Object obj){
		String className=obj.getClass().getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		MyField[] fields=table.getFields();
		
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		jdbcTemplate.executeUpate(table.getInsertSql(), OrmUtils.getInsertValues(obj, fields));
	}
	/**
	 * ����������
	 * @param objList ���󼯺�
	 */
	public void saveAll(List objList){
		if(objList==null||objList.size()==0){
			throw new RuntimeException("Ҫ����Ķ��󼯺ϲ���Ϊ��!");
		}
		String className=objList.get(0).getClass().getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		MyField[] fields=table.getFields();
		String[] fieldNames=OrmUtils.buildInsertFields(objList.get(0).getClass(), fields);
		Object[][] values=new Object[objList.size()][fieldNames.length];
		
		for(int i=0;i<objList.size();i++){
			for(int j=0;j<fieldNames.length;j++){
				values[i][j]=CommonUtils.getProperty(objList.get(i), fieldNames[j]);
			}
		}
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		jdbcTemplate.executeBatch(table.getInsertSql(), values);
	}
	/**
	 * ���¶���,������������
	 * @param obj Ҫ���µĶ���
	 */
	public void update(Object obj){
		String className=obj.getClass().getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		MyField[] fields=table.getFields();
		
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		jdbcTemplate.executeUpate(table.getUpdateSql(), OrmUtils.getUpdateValues(obj, fields));
	}
	/**
	 * ɾ������,��������ɾ��
	 * @param obj Ҫɾ���Ķ���
	 */
	public void delete(Object obj){
		String className=obj.getClass().getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		MyField[] fields=table.getFields();
		
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		jdbcTemplate.executeUpate(table.getDeleteSql(), OrmUtils.getDeleteValues(obj, fields));
	}
	/**
	 * ����������ѯ����
	 * @param clazz ��ѯ�Ķ�������
	 * @param id ���������
	 * @return ��ѯ�����Ķ���
	 */
	public Object getObject(Class clazz,Object id){
		String className=clazz.getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		List values=new ArrayList();
		MyField[] fields=table.getFields();
		String[] fieldNames=OrmUtils.buildSelectFields(clazz, fields);
		for (int i = 0; i < fieldNames.length; i++) {
			if(CommonUtils.constType.contains(id.getClass())){//�����һ��������
				values.add(id);
			}else{//���������������
				Object value=CommonUtils.getProperty(id, fieldNames[i]);
				values.add(value);
			}
		}
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		List list=jdbcTemplate.executeQuery(table.getSelectSql(),values.toArray());
		if(list.size()==0){
			return null;
		}
		if(list.size()>1){
			throw new RuntimeException("����������ѯ����������¼,��ȷ�������Ƿ���ȷ");
		}
		Map map=(Map) list.get(0);
		return OrmUtils.map2ObjectForJdbcTemplate(map, clazz);
	} 
}
