package com.finallygo.collect.data;

import java.util.HashMap;
import java.util.Map;

import com.finallygo.build.pojo.MyTable;
import com.finallygo.collect.inter.impl.BuildMyTableForCommon;
/**
 * 本工具包所有搜集到的信息
 * @author finallygo
 *
 */
public class CollectData {
	public static Map tableMap=new HashMap();//存储了所有的表,key为表名,value为我封装的表对象
	public static Map foreignMap=new HashMap();//存储了所有的外键信息
	public static Map insertFieldsCache=new HashMap();//缓存一张表需要插入的字段
	public static Map updateFieldsCache=new HashMap();//缓存一张表需要更新的字段(以主键)
	public static Map deleteFieldsCache=new HashMap();//缓存一张表需要删除的字段(以主键)
	public static Map selectFieldsCache=new HashMap();//缓存选择一张表需要的字段(以主键)
//	public static ResourceBundle rb=ResourceBundle.getBundle("collect");
//	public static boolean isLazy=Boolean.valueOf(rb.getString("Lazy")).booleanValue();//是否是懒加载
	public static boolean isLazy=true;
//	public static IBuildMyTable builder=(IBuildMyTable) MethodTimeProxy.createProxy(BuildMyTableForMySql.class);
	public static BuildMyTableForCommon builder=new BuildMyTableForCommon();
	
	static{
		if(!isLazy){//如果不是懒加载,就一次性把所有数据信息加载到内存中
			MyTable[] tables=builder.getMyTables();
			for (int i = 0; i < tables.length; i++) {
				tableMap.put(tables[i].getTableName().toUpperCase(), tables[i]);
			}
		}
		foreignMap=builder.getForeignKeys();//搜集外键信息
	}
	public static Map getForeignMap(){
		return foreignMap;
	}
	public void initMyTables(){
		
	}
	public static MyTable getTableByTableName(String tableName){
		MyTable table=(MyTable) tableMap.get(tableName.toUpperCase());
		if(table!=null){
			return table;
		}
		//判断是否是懒加载
		if(!isLazy){//如果不是懒加载,又找不到就报错
			throw new RuntimeException("找不到该表名对应的表:"+tableName);
		}else{//如果是懒加载则取当前MyTable
			table=builder.getMyTable(tableName);
			if(table!=null){
				tableMap.put(table.getTableName().toUpperCase(), table);//放入缓存
				return table;
			}
			throw new RuntimeException("找不到该表名对应的表:"+tableName);
		}
		
	}
}
