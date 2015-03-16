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
			//将include中的也加进来
			Document[] docs=getOtherSqlDocs(doc);
			for(int i=0;i<docs.length;i++){
				getSqlXmlObjects(docs[i]);
			}
		} catch (Exception e) {
			throw new RuntimeException("解析sql文件出现异常!",e);
		}
	}
	public static Map getSqlXmlObjects(Document doc){
		NodeList nodeList=doc.getElementsByTagName("sql");//得到所有标签为sql的node
		
		for(int i=0;i<nodeList.getLength();i++){//遍历所有sqlnode
			SqlXmlObject sqlObject = new SqlXmlObject();//创建一个对象
			Node node=nodeList.item(i);//得到其中的一个
			NamedNodeMap nodeMap=node.getAttributes();
			String sqlId=nodeMap.getNamedItem("id").getNodeValue();//得到id属性
			
			NodeList list=node.getChildNodes();//得到sql node的子节点
			String sqlContent=list.item(0).getNodeValue();//得到其中的sql
			if(sqlContent==null||"".equals(sqlContent.trim())){
				if(list.getLength()>0){
					sqlContent=list.item(1).getNodeValue();;
				}
				if(sqlContent==null||"".equals(sqlContent.trim())){
					throw new RuntimeException("在sqlId为"+sqlId+"中没有找到有效的sql语句!");
				}
			}
			String[] sqls=sqlContent.trim().split("\n");//按换行进行分割
			StringBuffer newSql=new StringBuffer();//定义一个新的字符串来存储修改后的sql语句
			for(int j=0;j<sqls.length;j++){
				newSql.append(fiterSql(sqls[j]));
			}
			sqlObject.setSqlId(sqlId);
			sqlObject.setSqlContent(newSql.toString().trim());
			sqlMap.put(sqlId, sqlObject);//添加到map中
		}
		return sqlMap;
	}
	private static Document[] getOtherSqlDocs(Document doc){
		NodeList nodeList=doc.getElementsByTagName("include");//得到所有标签为sql的node
		Document[] docs=new Document[nodeList.getLength()];
		for(int i=0;i<nodeList.getLength();i++){
			Node node=nodeList.item(i);//得到其中的一个
			NamedNodeMap nodeMap=node.getAttributes();
			String fileName=nodeMap.getNamedItem("file").getNodeValue();//得到file属性
			try {
				builder=factory.newDocumentBuilder();
				URL url=classLoader.getResource(fileName);
				docs[i]= builder.parse(url.toString());
			} catch (Exception e) {
				throw new RuntimeException("在解析文件:"+fileName+"出现异常",e);
			}
		}
		return docs;
	}
	/**
	 * 过滤sql语句,主要是去掉注释
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
			throw new RuntimeException("没有找到sql编号为:"+sqlId+"的sql语句");
		}
		return sqlObj.getSqlContent();
	}
	public static void main(String[] args) {
		String sql=AnalyzeSqlXml.getSql("selectBooks");
		System.out.println(sql);
	}

}
