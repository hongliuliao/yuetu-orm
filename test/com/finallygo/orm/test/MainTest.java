package com.finallygo.orm.test;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.finallygo.build.pojo.MyField;
import com.finallygo.build.pojo.MyTable;
import com.finallygo.collect.data.CollectData;
import com.finallygo.collect.inter.impl.BuildMyTableForCommon;
import com.finallygo.collect.test.pojo.BookType;
import com.finallygo.collect.test.pojo.EduinfoThreads;
import com.finallygo.collect.test.pojo.Riddle;
import com.finallygo.collect.test.pojo.TbBook;
import com.finallygo.db.utils.ConnectHelper;
import com.finallygo.db.utils.DataBaseHelper;
import com.finallygo.db.utils.JdbcTemplate;
import com.finallygo.yuetu.orm.sql.AnalyzeSqlXml;
import com.finallygo.yuetu.orm.utils.OrmUtils;
import com.finallygo.yuetu.orm.utils.MySession;

public class MainTest {

	public static void testCollectTable(){
		Connection conn=ConnectHelper.getConn();
		try {
			JdbcTemplate jdbcTemplate=new JdbcTemplate(conn,false);
			BuildMyTableForCommon b=new BuildMyTableForCommon();
			MyField[] fields=b.getTableField("eduinfo_threads");
//			MyTable[] tables=b.getMyTables();
			String sql=b.buildDeleteSql("riddle",fields);
			System.out.println(sql);
			List list=jdbcTemplate.executeQuery("select * from eduinfo_news");
			System.out.println(list.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DataBaseHelper.closeConn(conn);
		}
		
	}
	public static void testCollectSession(){
		MyTable table=CollectData.getTableByTableName("eduinfo_threads");
		String[] fields=OrmUtils.buildUpdateFields(EduinfoThreads.class, table.getFields());
		for (int i = 0; i < fields.length; i++) {
			System.out.println(fields[i]);
		}
	}

	public static void testMySessionSelect(){
		MySession session=new MySession();
//		Riddle riddle=(Riddle) session.getObject(Riddle.class, new Long(1));
//		Riddle riddle2=(Riddle) session.getObject(Riddle.class, new Long(5));
//		System.out.println(riddle.getRiddleAnswer());
//		System.out.println(riddle2.getRiddleAnswer());
		TbBook book=(TbBook) session.getObject(TbBook.class, new Long(1));
		System.out.println(book.getBookType().getTypeId());
	}
	public static void testMySessionUpdate(){
		MySession session=new MySession();
		Riddle riddle=(Riddle) session.getObject(Riddle.class, new Long(1));
		riddle.setUpdateDt(new java.sql.Timestamp(new java.util.Date().getTime()));
		session.update(riddle);
		Riddle riddle2=(Riddle) session.getObject(Riddle.class, new Long(1));
		System.out.println(riddle2.getUpdateDt());
	}
	public static void testMySessionInsert(){
		TbBook book=new TbBook();
		BookType bookType=new BookType();
		bookType.setTypeId(new Integer(1));
		book.setBookName("java aop");
		book.setBookAmount(new Float(200));
		book.setBookAuthor("finallygo");
		book.setBookPrice(new Float(10));
		book.setBookType(bookType);
		book.setPublishDate(new Date());
		
		//bookType.setTypeName("javaÏà¹Ø");
		MySession session=new MySession();
//		session.save(bookType);
		session.save(book);
	}
	public static void testJdbcTemplateSelect(){
		JdbcTemplate jdbcTemplate=new JdbcTemplate(ConnectHelper.getConn(),true);
//		Map map=(Map) jdbcTemplate.executeQuery("select * from tb_book t1 left join book_type t2 on t1.book_type=t2.type_id").get(0);
		String sql=AnalyzeSqlXml.getSql("selectBooks3");
		Map map=(Map) jdbcTemplate.executeQuery(sql,new Object[]{new Long(3)}).get(0);
		TbBook book=(TbBook) OrmUtils.map2ObjectForJdbcTemplate(map, TbBook.class);
		System.out.println(book.getBookName());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		testCollectTable();
//		testCollectSession();
//		testMySessionSelect();
//		testMySessionUpdate();
//		testMySessionInsert();
//		testConn();
		testJdbcTemplateSelect();
	}

}
