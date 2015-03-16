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
	 * �����ṩ����������ָ�������н��в���,Ŀǰֻ֧�����Ϊ2�Ĳ���
	 * @param clazz Ҫ���ҵ���
	 * @param db2fieldName ���ݿ��е��ֶ�ͨ���Զ���orm����ת���ɵ�������
	 * @return ���ֶ��ڸ����е�λ��
	 */
	private static String getFieldStringBySearch(Class clazz,String db2fieldName){
		List list=CommonUtils.getFieldNameList(clazz);
		
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String fieldName = (String) iter.next();
			//���������ȫ�����е�һ��,��ȥ���༶��ϵ��һ��,�ͷ�����Ӧ����,�����и�������Ϊ�����п����ж����������һ��,����Ϊ���ٶ��Ȳ�������
			//2010-06-22��������
			if(fieldName.equals(db2fieldName)){
				return fieldName;
			}
//			��������Ҳ����͸�������еĹ�ϵ������
			String className=clazz.getSimpleName();
			String tableName=rule.class2TableName(className);
			String dbFieldName=rule.javaField2DbField(db2fieldName);
			String key=tableName+"."+dbFieldName;//���������key,ͨ������ҵ���Ӧ������ֶ�,��һ���õ���Ӧ����
			String foreignInfo=(String) CollectData.foreignMap.get(key.toUpperCase());//�õ������Ϣ
			if(foreignInfo!=null){
				String foreignTableName=foreignInfo.split("\\.")[0];//�����
				String foreignFieldName=foreignInfo.split("\\.")[1];//�����
				String foreignClassName=rule.tableName2ClassName(foreignTableName);//���ת��֮��ı���
				String foreignJavaFieldName=rule.dbField2JavaField(foreignFieldName);
				/*
				 * �����뵽����ʵ��,
				 * һ����ͨ����������ֵõ�������,֮��������������
				 * һ����ͨ������ı�����������������������ƥ��
				 * ���������ֶ�������ȫ�������,��Ϊ�ڵ�һ����һ����������ж������,����Щ�����������ͬ��������
				 * �ڶ���Ҳ����,��Ϊjava���а��ĸ���,�����ݿ���û��,Ҳ��˵,java�п�����ͬ������,�����ݿ��в�����ͬ���ı�
				 * ���������ô�����?
				 * ������ʱ�����õڶ��ַ�ʽ��ʵ��
				 *
				 * �����������õڶ��ַ�ʽ��ʵ���ǿ��Ե�,��Ϊ����������Ҫ��orm�Ĺ����,����������Լ���������ǵ�
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
		//�������û���ҵ������Կ��ǲ�����������
		for(int i=0;i<list.size();i++){
			String fieldName =(String) list.get(i);
			if(CommonUtils.getSimpleName(fieldName).equals(db2fieldName)){
				return fieldName;
			}
		}
		//��������Ҳ��������쳣
		throw new RuntimeException("����"+clazz+"���Ҳ�������:"+db2fieldName);
	}
	/**
	 * ������������漰���ֶ�
	 * @param clazz ָ������
	 * @param fields ���ݿ��е��ֶ�
	 * @return ���е��ֶ�
	 */
	public static String[] buildInsertFields(Class clazz,com.finallygo.build.pojo.MyField[] fields){
		//��ȥ�����в���
		String[] javaFields=(String[]) CollectData.insertFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
		//����Ҳ���
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
		//��ȥ�����в���
		String[] javaFields=(String[]) CollectData.updateFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
		//����Ҳ���
		List fieldList=new ArrayList();
		for (int i = 0; i < fields.length; i++) {
			if(!fields[i].isKey()){//�ȼӷ�������ֵ
				String fieldName=fields[i].getFieldName();
				String javaField=rule.dbField2JavaField(fieldName);
				String field=OrmUtils.getFieldStringBySearch(clazz, javaField);
				fieldList.add(field);
			}
		}
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){//�������������ֵ
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
//		��ȥ�����в���
		String[] javaFields=(String[]) CollectData.deleteFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
//		����Ҳ���
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
//		��ȥ�����в���
		String[] javaFields=(String[]) CollectData.selectFieldsCache.get(clazz);
		if(javaFields!=null){
			return javaFields;
		}
//		����Ҳ���
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
	 * ���ݴ�����������õ���Ӧ������ֵ
	 * @param obj Ҫȡֵ�Ķ���
	 * @param fieldNames ������
	 * @return �õ���ֵ
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
	 * ��map�е�ֵ��䵽������
	 * @param map ��mapΪ��jdbcTemplate�в�ѯ������List�е�Map
	 * @param clazz Ҫ���Ķ������
	 * @return ���õĶ���
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
//			�ж��Ƿ��".",����б�ʾ�ж༶�Ĺ�ϵ,���������һ������������,һ�����������������
			if(fieldName.indexOf(".")!=-1){
//				���ж��Ƿ������
				String columnName=rule.javaField2DbField(CommonUtils.getFirstName(fieldName));
				String key=tableName+"."+columnName;
				String foreignKeyInfo=(String) CollectData.getForeignMap().get(key);
				String foreignKey=rule.javaField2DbField(CommonUtils.getSimpleName(fieldName));
				if(foreignKeyInfo!=null){
					if(!foreignKey.equals(CommonUtils.getSimpleName(foreignKeyInfo))){
						continue;
					}
//					����������ͨ��������ȡֵ
					value=map.get(columnName);
				}else{//�������������,��ȡ����Ĳ���
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
