package com.finallygo.yuetu.orm.rule;

public interface IOrmRule {
	/**
	 * ����ת����
	 * @param className ����
	 * @return ����
	 */
	public String class2TableName(String className);
	/**
	 * ����ת����
	 * @param tableName ����
	 * @return ����
	 */
	public String tableName2ClassName(String tableName);
	/**
	 * java������ת���ݿ�������
	 * @param javaField java��������
	 * @return ���ݿ�������
	 */
	public String javaField2DbField(String javaField);
	/**
	 * ���ݿ��е�����תjava�е�������
	 * @param dbField ���ݿ��е�����
	 * @return java�е�������
	 */
	public String dbField2JavaField(String dbField);
}
