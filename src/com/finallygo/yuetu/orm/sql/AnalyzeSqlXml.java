package com.finallygo.yuetu.orm.sql;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AnalyzeSqlXml {
	private static Map sqlMap=new LinkedHashMap();
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder builder;
	private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	static{
		initSql();
	}
	public static void initSql(){
		try {
			builder=factory.newDocumentBuilder();
			URL url=classLoader.getResource("sql.xml");
			Document doc=builder.parse(url.toString());
			getSqlXmlObjects(doc);
			//��include�е�Ҳ�ӽ���
			Document[] docs=getOtherSqlDocs(doc);
			for(int i=0;i<docs.length;i++){
				getSqlXmlObjects(docs[i]);
			}
		} catch (Exception e) {
			throw new RuntimeException("����sql�ļ������쳣!",e);
		}
	}
	public static Map getSqlXmlObjects(Document doc){
		NodeList nodeList=doc.getElementsByTagName("sql");//�õ����б�ǩΪsql��node
		
		for(int i=0;i<nodeList.getLength();i++){//��������sqlnode
			SqlXmlObject sqlObject = new SqlXmlObject();//����һ������
			Node node=nodeList.item(i);//�õ����е�һ��
			NamedNodeMap nodeMap=node.getAttributes();
			String sqlId=nodeMap.getNamedItem("id").getNodeValue();//�õ�id����
			
			NodeList list=node.getChildNodes();//�õ�sql node���ӽڵ�
			String sqlContent=list.item(0).getNodeValue();//�õ����е�sql
			if(sqlContent==null||"".equals(sqlContent.trim())){
				if(list.getLength()>0){
					sqlContent=list.item(1).getNodeValue();;
				}
				if(sqlContent==null||"".equals(sqlContent.trim())){
					throw new RuntimeException("��sqlIdΪ"+sqlId+"��û���ҵ���Ч��sql���!");
				}
			}
			String[] sqls=sqlContent.trim().split("\n");//�����н��зָ�
			StringBuffer newSql=new StringBuffer();//����һ���µ��ַ������洢�޸ĺ��sql���
			for(int j=0;j<sqls.length;j++){
				newSql.append(fiterSql(sqls[j]));
			}
			sqlObject.setSqlId(sqlId);
			sqlObject.setSqlContent(newSql.toString().trim());
			sqlMap.put(sqlId, sqlObject);//��ӵ�map��
		}
		return sqlMap;
	}
	private static Document[] getOtherSqlDocs(Document doc){
		NodeList nodeList=doc.getElementsByTagName("include");//�õ����б�ǩΪsql��node
		Document[] docs=new Document[nodeList.getLength()];
		for(int i=0;i<nodeList.getLength();i++){
			Node node=nodeList.item(i);//�õ����е�һ��
			NamedNodeMap nodeMap=node.getAttributes();
			String fileName=nodeMap.getNamedItem("file").getNodeValue();//�õ�file����
			try {
				builder=factory.newDocumentBuilder();
				URL url=classLoader.getResource(fileName);
				docs[i]= builder.parse(url.toString());
			} catch (Exception e) {
				throw new RuntimeException("�ڽ����ļ�:"+fileName+"�����쳣",e);
			}
		}
		return docs;
	}
	/**
	 * ����sql���,��Ҫ��ȥ��ע��
	 * @param sql
	 * @return
	 */
	public static String fiterSql(String sql){
		String newSql=sql;
		if(sql.indexOf("--")!=-1){
			newSql=sql.substring(0,sql.indexOf("--"));
		}
		return newSql;
	}
	public static Map getSqlMap(){
		return sqlMap;
	}
	public static String getSql(String sqlId){
		SqlXmlObject sqlObj= (SqlXmlObject) sqlMap.get(sqlId);
		if(sqlObj==null){
			throw new RuntimeException("û���ҵ�sql���Ϊ:"+sqlId+"��sql���");
		}
		return sqlObj.getSqlContent();
	}
	public static void main(String[] args) {
		String sql=AnalyzeSqlXml.getSql("selectBooks");
		System.out.println(sql);
	}

}
