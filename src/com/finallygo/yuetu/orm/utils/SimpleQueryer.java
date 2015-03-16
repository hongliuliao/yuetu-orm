package com.finallygo.yuetu.orm.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.finallygo.common.utils.CommonUtils;
import com.finallygo.yuetu.orm.factory.OrmRuleFactory;

public class SimpleQueryer {
	
	public static String getSelectSql(Object obj){
		StringBuffer sql=new StringBuffer();
		Class clazz=obj.getClass();
		String tableName=OrmRuleFactory.getRuleObject().class2TableName(CommonUtils.getSimpleName(clazz.getName())).toLowerCase();
		sql.append("select * from "+tableName+" where 1=1 ");
		Map valuesMap=CommonUtils.Object2Map(obj);
		Set entrySet=valuesMap.entrySet();
		for(Iterator i=entrySet.iterator();i.hasNext();){
			Entry entry=(Entry) i.next();
			String dbFieldName=OrmRuleFactory.getRuleObject().javaField2DbField((String) entry.getKey());
			Object value=entry.getValue();
			if(value.getClass()==String.class){
				sql.append(" and "+dbFieldName+" like ? ");
			}else{
				sql.append(" and "+dbFieldName+" = ? ");
			}
		}
		return sql.toString();
	}
	public static Object[] getValues(Object obj){
		Map valuesMap=CommonUtils.Object2Map(obj);
		Set entrySet=valuesMap.entrySet();
		Object[] values=new Object[valuesMap.size()];
		int num=0;
		for(Iterator i=entrySet.iterator();i.hasNext();){
			Entry entry=(Entry) i.next();
			values[num]=entry.getValue();
			num++;
		}
		return values;
	}
	/**
	 * 给对象中的String类型添加%value%,即sql中的like查询
	 * @param obj 要修改的对象
	 * @return 返回的对象
	 */
	public static Object setStringLike(Object obj){
		Map fieldNameMap=CommonUtils.getFieldNameMap(obj.getClass());
		Iterator i=fieldNameMap.keySet().iterator();
		while(i.hasNext()){
			String fieldName=(String) i.next();
			Class type=(Class) fieldNameMap.get(fieldName);
			if(type==String.class){
				Object value=CommonUtils.getPropertyString(obj, fieldName);
				if(value!=null){
					CommonUtils.setProperty(obj, fieldName,"%"+value+"%" );	
				}
			}
		}
		return obj;
	}
	public static String getCountSql(Object obj){
		String sql=getSelectSql(obj);
		sql="select count(*) from ("+sql+")";
		return sql;
	}
}
