package com.finallygo.collect.data;

import java.util.HashMap;
import java.util.Map;

import com.finallygo.build.pojo.MyTable;
import com.finallygo.collect.inter.impl.BuildMyTableForCommon;
/**
 * �����߰������Ѽ�������Ϣ
 * @author finallygo
 *
 */
public class CollectData {
	public static Map tableMap=new HashMap();//�洢�����еı�,keyΪ����,valueΪ�ҷ�װ�ı����
	public static Map foreignMap=new HashMap();//�洢�����е������Ϣ
	public static Map insertFieldsCache=new HashMap();//����һ�ű���Ҫ������ֶ�
	public static Map updateFieldsCache=new HashMap();//����һ�ű���Ҫ���µ��ֶ�(������)
	public static Map deleteFieldsCache=new HashMap();//����һ�ű���Ҫɾ�����ֶ�(������)
	public static Map selectFieldsCache=new HashMap();//����ѡ��һ�ű���Ҫ���ֶ�(������)
//	public static ResourceBundle rb=ResourceBundle.getBundle("collect");
//	public static boolean isLazy=Boolean.valueOf(rb.getString("Lazy")).booleanValue();//�Ƿ���������
	public static boolean isLazy=true;
//	public static IBuildMyTable builder=(IBuildMyTable) MethodTimeProxy.createProxy(BuildMyTableForMySql.class);
	public static BuildMyTableForCommon builder=new BuildMyTableForCommon();
	
	static{
		if(!isLazy){//�������������,��һ���԰�����������Ϣ���ص��ڴ���
			MyTable[] tables=builder.getMyTables();
			for (int i = 0; i < tables.length; i++) {
				tableMap.put(tables[i].getTableName().toUpperCase(), tables[i]);
			}
		}
		foreignMap=builder.getForeignKeys();//�Ѽ������Ϣ
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
		//�ж��Ƿ���������
		if(!isLazy){//�������������,���Ҳ����ͱ���
			throw new RuntimeException("�Ҳ����ñ�����Ӧ�ı�:"+tableName);
		}else{//�������������ȡ��ǰMyTable
			table=builder.getMyTable(tableName);
			if(table!=null){
				tableMap.put(table.getTableName().toUpperCase(), table);//���뻺��
				return table;
			}
			throw new RuntimeException("�Ҳ����ñ�����Ӧ�ı�:"+tableName);
		}
		
	}
}
