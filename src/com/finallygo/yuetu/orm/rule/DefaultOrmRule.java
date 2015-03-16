package com.finallygo.yuetu.orm.rule;

import com.finallygo.common.utils.CommonUtils;

public class DefaultOrmRule implements IOrmRule {

	public Class dbType2JavaTypeForMySql(String dbType){
		if ("int".equals(dbType)) {
			return Integer.class;
		} else if ("varchar".equals(dbType)) {
			return String.class;
		} else if ("date".equals(dbType)) {
			return java.util.Date.class;
		} else if ("datetime".equals(dbType)) {
			return java.sql.Timestamp.class;
		} else if ("double".equals(dbType)) {
			return java.lang.Double.class;
		} else if ("bit".equals(dbType)) {
			return java.lang.Boolean.class;
		}
		throw new RuntimeException("不支持的数据类型");
	}
	public String class2TableName(String className){
		StringBuffer name=new StringBuffer();
		for(int i=0;i<className.length();i++){
			char c1=className.charAt(i);
			if(Character.isUpperCase(c1)){
				StringBuffer sb=new StringBuffer();
				sb.append(c1);
				for(int j=i+1;j<className.length();j++){
					char c2=className.charAt(j);
					if(Character.isUpperCase(c2)){
						break;
					}
					sb.append(c2);
				}
				name.append(sb+"_");
			}
		}
		return name.toString().substring(0,name.length()-1).toUpperCase();
	}
	public String tableName2ClassName(String tableName){
		//按_分割之后让首字母大写
		String[] names=tableName.toLowerCase().split("_");
		StringBuffer className=new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			className.append(CommonUtils.getFirstUpper(names[i]));
		}
		return className.toString();
	}
	public String javaField2DbField(String fieldName){
		StringBuffer name=new StringBuffer(fieldName);
		for(int i=0;i<name.length();i++){
			char c1=name.charAt(i);
			if(Character.isUpperCase(c1)){
				name.insert(i, "_");
				i++;
			}
		}
		return name.toString().toUpperCase();
	}
	public String dbField2JavaField(String dbField){
//		按"_"分割之后让首字母大写(除了第一个单词)
		String[] names=dbField.toLowerCase().split("_");
		StringBuffer javaField=new StringBuffer(names[0]);
		for(int i=1;i<names.length;i++){
			javaField.append(CommonUtils.getFirstUpper(names[i]));
		}
		return javaField.toString();
	}

}
