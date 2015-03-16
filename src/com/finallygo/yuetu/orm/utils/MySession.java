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
 * 模仿hibernate实现自己的session
 * @author user
 *
 */
public class MySession {
	
	private IOrmRule rule=OrmRuleFactory.getRuleObject();
	
	/**
	 * 保存对象
	 * @param obj 要保存的对象
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
	 * 保存多个对象
	 * @param objList 对象集合
	 */
	public void saveAll(List objList){
		if(objList==null||objList.size()==0){
			throw new RuntimeException("要保存的对象集合不能为空!");
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
	 * 更新对象,根据主键更新
	 * @param obj 要更新的对象
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
	 * 删除对象,根据主键删除
	 * @param obj 要删除的对象
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
	 * 根据主键查询对象
	 * @param clazz 查询的对象类型
	 * @param id 对象的主键
	 * @return 查询出来的对象
	 */
	public Object getObject(Class clazz,Object id){
		String className=clazz.getSimpleName();
		String tableName=rule.class2TableName(className);
		MyTable table=CollectData.getTableByTableName(tableName);
		List values=new ArrayList();
		MyField[] fields=table.getFields();
		String[] fieldNames=OrmUtils.buildSelectFields(clazz, fields);
		for (int i = 0; i < fieldNames.length; i++) {
			if(CommonUtils.constType.contains(id.getClass())){//如果是一个主键的
				values.add(id);
			}else{//如果是联合主键的
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
			throw new RuntimeException("根据主键查询出了两条记录,请确认主键是否正确");
		}
		Map map=(Map) list.get(0);
		return OrmUtils.map2ObjectForJdbcTemplate(map, clazz);
	} 
}
