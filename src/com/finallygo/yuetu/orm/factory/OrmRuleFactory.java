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
		//�ȴӻ�����ȡ
		IOrmRule rule=(IOrmRule) ormRuleClassCache.get(ruleClassName);
		if(rule!=null){//����ҵ���
			return rule;
		}
		//���û��
		try {
			Class clazz=Class.forName(ruleClassName);
			Object obj=clazz.newInstance();
			if (obj instanceof IOrmRule) {
				rule = (IOrmRule) obj;
				ormRuleClassCache.put(ruleClassName, rule);
				return rule;
			}else{
				throw new RuntimeException("ӳ����������ʵ��IOrmRule�ӿ�");
			}
		} catch(ClassNotFoundException e){
			throw new RuntimeException("�Ҳ�����:"+ruleClassName,e);
		} catch (Exception e) {
			throw new RuntimeException("��ȡӳ��������ʱ�����",e);
		}
	}
}
