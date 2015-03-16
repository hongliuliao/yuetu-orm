/*
 * Created on 2010-04-23
 *
 */
package com.finallygo.collect.test.pojo;


import java.sql.Date;
import java.sql.Timestamp;


/**
 * @author denlly-BeanGenerator
 *
 */
public class Riddle{

	private Integer riddleId;
	private String riddleContent;
	private String riddleAnswer;
	private String answerPeople;
	private Date readDate;
	private Timestamp updateDt;

	public Integer getRiddleId(){
		return this.riddleId;
	}
	public void setRiddleId(Integer riddleId){
		this.riddleId=riddleId;
	}

	public String getRiddleContent(){
		return this.riddleContent;
	}
	public void setRiddleContent(String riddleContent){
		this.riddleContent=riddleContent;
	}

	public String getRiddleAnswer(){
		return this.riddleAnswer;
	}
	public void setRiddleAnswer(String riddleAnswer){
		this.riddleAnswer=riddleAnswer;
	}

	public Date getReadDate(){
		return this.readDate;
	}
	public void setReadDate(Date readDate){
		this.readDate=readDate;
	}

	public Timestamp getUpdateDt(){
		return this.updateDt;
	}
	public void setUpdateDt(Timestamp updateDt){
		this.updateDt=updateDt;
	}
	public String getAnswerPeople() {
		return answerPeople;
	}
	public void setAnswerPeople(String answerPeople) {
		this.answerPeople = answerPeople;
	}

}
