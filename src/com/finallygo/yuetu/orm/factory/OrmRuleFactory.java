package com.finallygo.yuetu.orm.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.finallygo.yuetu.orm.rule.IOrmRule;

public class OrmRuleFactory {
	public static ResourceBundle rb=ResourceBundle.getBundle("orm");
	public static String ruleClassName=rb.getString("OrmRuleClass");
	public static Map ormRuleClassCache=new HashMap();
	
	public static IOrmRule getRuleObject(){
		//先从缓存中取
		IOrmRule rule=(IOrmRule) ormRuleClassCache.get(ruleClassName);
		if(rule!=null){//如果找到了
			return rule;
		}
		//如果没有
		try {
			Class clazz=Class.forName(ruleClassName);
			Object obj=clazz.newInstance();
			if (obj instanceof IOrmRule) {
				rule = (IOrmRule) obj;
				ormRuleClassCache.put(ruleClassName, rule);
				return rule;
			}else{
				throw new RuntimeException("映射规则类必须实现IOrmRule接口");
			}
		} catch(ClassNotFoundException e){
			throw new RuntimeException("找不到类:"+ruleClassName,e);
		} catch (Exception e) {
			throw new RuntimeException("获取映射规则类的时候出错",e);
		}
	}
}
