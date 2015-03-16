package com.finallygo.yuetu.orm.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.finallygo.collect.data.CollectData;
import com.finallygo.common.utils.CommonUtils;
import com.finallygo.db.utils.ConnectHelper;
import com.finallygo.db.utils.DataBaseHelper;
import com.finallygo.db.utils.JdbcTemplate;
import com.finallygo.yuetu.orm.factory.OrmRuleFactory;
import com.finallygo.yuetu.orm.rule.IOrmRule;

public class OrmUtils {
	public static final SimpleDateFormat dateFormater=new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat timeFormater=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static IOrmRule rule=OrmRuleFactory.getRuleObject();

	/**
	 * 根据提供的属性名在指定的类中进行查找,目前只支持深度为2的查找
	 * @param clazz 要查找的类
	 * @param db2fieldName 数据库中的字段通过自定义orm规则转换成的属性名
	 * @return 该字段在该类中的位置
	 */
	private static String getFieldStringBySearch(Class clazz,String db2fieldName){
		List list=CommonUtils.getFieldNameList(clazz);
		
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String fieldName = (String) iter.next();
			//如果属性完全和类中的一致,或去除多级关系后一致,就返回相应属性,这里有个隐患因为属性中可能有对象的属性名一样,现在为了速度先不考虑了
			//2010-06-22消除隐患
			if(fieldName.equals(db2fieldName)){
				return fieldName;
			}
//			如果还是找不到就根据外键中的关系来查找
			String className=clazz.getSimpleName();
			String tableName=rule.class2TableName(className);
			String dbFieldName=rule.javaField2DbField(db2fieldName);
			String key=tableName+"."+dbFieldName;//外键集合中key,通过这个找到对应的外表字段,进一步得到相应属性
			String foreignInfo=(String) CollectData.foreignMap.get(key.toUpperCase());//得到外键信息
			if(foreignInfo!=null){
				String foreignTableName=foreignInfo.split("\\.")[0];//外表名
				String foreignFieldName=foreignInfo.split("\\.")[1];//外键名
				String foreignClassName=rule.tableName2ClassName(foreignTableName);//外键转换之后的表名
				String foreignJavaFieldName=rule.dbField2JavaField(foreignFieldName);
				/*
				 * 这里想到两种实现,
				 * 一种是通过外键的名字得到属性名,之后在这里类里找
				 * 一种是通过外键的表名与这个类里的属性名进行匹配
				 * 但是这两种都不能完全解决问题,因为在第一个中一个类里可能有多个对象,而这些对象可以有相同的属性名
				 * 第二种也不能,因为java中有包的概念,而数据库中没有,也就说,java中可以有同名的类,而数据库中不能有同名的表
				 * 这个问题怎么解决呢?
				 * 现在暂时考虑用第二种方式来实现
				 *
				 * 后来想了下用第二种方式来实现是可以的,因为我这里是有要求orm的规则的,这个规则就制约了我所顾虑的
				 * 
				 */
				Field[] fields=clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					Class cla=fields[i].getType();
					if(!CommonUtils.constType.contains(cla)){
						if(cla.getSimpleName().equals(foreignClassName)){
							return fields[i].getName()+"."+foreignJavaFieldName;
						}
					}
				}
			}
		}
		//如果还是没有找到就试试看是不是联合主键
		for(int i=0;i<list.size();i++){
			String fieldName =(String) list.get(i);
			if(CommonUtils.getSimpleName(fieldName).equals(db2fieldName)){
				return fieldName;
			}
		}
		//如果还是找不到就抛异常
		throw new RuntimeException("在类"+clazz+"里找不到属性:"+db2fieldName);
	}
	/**
	 * 创建插入操作涉及的字段
	 * @param clazz 指定的类
	 * @param fields 数据库中的字段
	 * @return 类中的字段
	 */
	public static String[] buildInsertFields(Class clazz,com.finallygo.build.pojo.MyField[] fields){
		//先去缓存中查找
		String[] javaFields=(String[]) CollectData.insertFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
		//如果找不到
		javaFields=new String[fields.length];
		for(int i=0;i<fields.length;i++){
			String fieldName=fields[i].getFieldName();
			String javaField=rule.dbField2JavaField(fieldName);
			String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
			javaFields[i]=field;
		}
		CollectData.insertFieldsCache.put(clazz, javaFields);
		return javaFields;
	}
	public static String[] buildUpdateFields(Class clazz,com.finallygo.build.pojo.MyField[] fields){
		//先去缓存中查找
		String[] javaFields=(String[]) CollectData.updateFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
		//如果找不到
		List fieldList=new ArrayList();
		for (int i = 0; i < fields.length; i++) {
			if(!fields[i].isKey()){//先加非主键的值
				String fieldName=fields[i].getFieldName();
				String javaField=rule.dbField2JavaField(fieldName);
				String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
				fieldList.add(field);
			}
		}
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){//再添加是主键的值
				String fieldName=fields[i].getFieldName();
				String javaField=rule.dbField2JavaField(fieldName);
				String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
				fieldList.add(field);
			}
		}
		javaFields=new String[fieldList.size()];
		for(int i=0;i<fieldList.size();i++){
			javaFields[i]=(String) fieldList.get(i);
		}
		CollectData.updateFieldsCache.put(clazz, javaFields);
		return javaFields;
	}
	public static String[] buildDeleteFields(Class clazz,com.finallygo.build.pojo.MyField[] fields){
//		先去缓存中查找
		String[] javaFields=(String[]) CollectData.deleteFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
//		如果找不到
		List list=new ArrayList();
		
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				String fieldName=fields[i].getFieldName();
				String javaField=rule.dbField2JavaField(fieldName);
				String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
				list.add(field);
			}
		}
		javaFields=new String[list.size()];
		for(int i=0;i<list.size();i++){
			javaFields[i]=(String) list.get(i);
		}
		CollectData.deleteFieldsCache.put(clazz, javaFields);
		return javaFields;
	}
	public static String[] buildSelectFields(Class clazz,com.finallygo.build.pojo.MyField[] fields){
//		先去缓存中查找
		String[] javaFields=(String[]) CollectData.selectFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
//		如果找不到
		List list=new ArrayList();
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				String fieldName=fields[i].getFieldName();
				String javaField=rule.dbField2JavaField(fieldName);
				String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
				list.add(field);
			}
		}
		javaFields=new String[list.size()];
		for(int i=0;i<list.size();i++){
			javaFields[i]=(String) list.get(i);
		}
		CollectData.selectFieldsCache.put(clazz, javaFields);
		return javaFields;
	}
	
	public static String getJdbcTemplateMapString(Object obj){
		if(obj==null){
			return "";
		}
		if (obj instanceof java.sql.Date) {
			java.sql.Date date = (java.sql.Date) obj;
			return dateFormater.format(date);
		}else if (obj instanceof  java.sql.Timestamp) {
			java.sql.Timestamp time = (java.sql.Timestamp) obj;
			return timeFormater.format(time);
		}else{
			return obj.toString();
		}
	}
	public static Object[] getInsertValues(Object obj,com.finallygo.build.pojo.MyField[] fields){
		String[] fieldNames=buildInsertFields(obj.getClass(), fields);
		return getValue(obj,fieldNames);
	}
	public static Object[] getUpdateValues(Object obj,com.finallygo.build.pojo.MyField[] fields){
		String[] fieldNames=buildUpdateFields(obj.getClass(), fields);
		return getValue(obj,fieldNames);
	}
	
	public static Object[] getDeleteValues(Object obj,com.finallygo.build.pojo.MyField[] fields){
		String[] fieldNames=buildDeleteFields(obj.getClass(), fields);
		return getValue(obj,fieldNames);
	}
	public static Object[] getSelectValues(Object obj,com.finallygo.build.pojo.MyField[] fields){
		String[] fieldNames=buildSelectFields(obj.getClass(), fields);
		return getValue(obj,fieldNames);
	}
	/**
	 * 根据传入的属性名得到相应的属性值
	 * @param obj 要取值的对象
	 * @param fieldNames 属性名
	 * @return 得到的值
	 */
	public static Object[] getValue(Object obj,String[] fieldNames){
		List values=new ArrayList();
		for(int i=0;i<fieldNames.length;i++){
			Object value=CommonUtils.getProperty(obj, fieldNames[i]);
			values.add(value);
		}
		return values.toArray();
	}
	/**
	 * 将map中的值填充到对象中
	 * @param map 此map为从jdbcTemplate中查询出来的List中的Map
	 * @param clazz 要填充的对象的类
	 * @return 填充好的对象
	 */
	public static Object map2ObjectForJdbcTemplate(Map map,Class clazz){
		return map2ObjectForJdbcTemplate(map,clazz,rule);
	}
	public static Object map2ObjectForJdbcTemplate(Map map,Class clazz,IOrmRule rule){
		Object obj=CommonUtils.getNewObject(clazz);
		Map fieldNameMap=CommonUtils.getFieldNameMap(clazz);
		Iterator i=fieldNameMap.keySet().iterator();
		String tableName=rule.class2TableName(clazz.getSimpleName());
		while(i.hasNext()){
			String fieldName=(String) i.next();
			Class type=(Class) fieldNameMap.get(fieldName);
			String dbFieldName=rule.javaField2DbField(fieldName).toUpperCase();
			Object value=null;
//			判断是否带".",如果有表示有多级的关系,这里分两种一种是外键的情况,一种是联合主键的情况
			if(fieldName.indexOf(".")!=-1){
//				先判断是否是外键
				String columnName=rule.javaField2DbField(CommonUtils.getFirstName(fieldName));
				String key=tableName+"."+columnName;
				String foreignKeyInfo=(String) CollectData.getForeignMap().get(key);
				String foreignKey=rule.javaField2DbField(CommonUtils.getSimpleName(fieldName));
				if(foreignKeyInfo!=null){
					if(!foreignKey.equals(CommonUtils.getSimpleName(foreignKeyInfo))){
						continue;
					}
//					如果是外键就通过列名来取值
					value=map.get(columnName);
				}else{//如果是联合主键,就取后面的部分
					columnName=rule.javaField2DbField(CommonUtils.getSimpleName(fieldName));
					value=map.get(columnName);
				}
			}else{
				value = map.get(dbFieldName);
			}
			
			if(value!=null){
				value=CommonUtils.convert(value, type);
				CommonUtils.setProperty(obj, fieldName, value);
			}
		}
		return obj;
	}
	public static List getListForDwrCondition(Map condition,Class clazz){
		List list=new ArrayList();
		Object obj= CommonUtils.map2ObjectForQuery(condition, clazz);
		obj= SimpleQueryer.setStringLike(obj);
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
		
		String sql=SimpleQueryer.getSelectSql(obj);
		int start=Integer.parseInt(condition.get("start").toString());
		int limit=Integer.parseInt(condition.get("limit").toString());
		String limitSql=DataBaseHelper.addPageForMySql(sql, start, limit);
		Object[] values=SimpleQueryer.getValues(obj);
		List mapList=jdbcTemplate.executeQuery(limitSql, values);
		for(int i=0;i<mapList.size();i++){
			Map map=(Map) mapList.get(i);
			Object o=map2ObjectForJdbcTemplate(map, obj.getClass());
			list.add(o);
		}
		return list;
	}
	public static void main(String[] args) {
		System.out.println(getJdbcTemplateMapString(new java.sql.Timestamp(new java.util.Date().getTime())));
	}
}
