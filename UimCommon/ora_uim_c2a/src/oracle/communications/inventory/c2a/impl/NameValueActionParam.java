package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
public class NameValueActionParam {
	private String name;
	private String value;
	private String action;
	private Integer valueInt;
	
public NameValueActionParam(){
		
	}

public NameValueActionParam(String name, String value, String action, Integer valueInt){
	this.name=name;
	this.value=value;
	this.action=action;
	this.valueInt=valueInt;
}

public NameValueActionParam(String name, String value, String action){
	this.name=name;
	this.value=value;
	this.action=action;	
	this.valueInt=null;
}

public NameValueActionParam(String name, String value){
	this.name=name;
	this.value=value;
	this.action=null;	
	this.valueInt=null;
}

public NameValueActionParam(String name, String value, String action, String valueIntString){
	this.name=name;
	this.value=value;
	this.action=action;	
	this.valueInt=Integer.parseInt(valueIntString);
}

public String getName(){
	return name;
}
public void setName(String name){
	this.name=name;
}
public String getValue(){
	return value;
}
public void setValue(String value){
	this.value=value;
}

public Integer getValueInt(){
	return valueInt;
}
public void setValueInt(String value){
	this.valueInt=Integer.parseInt(value);
}
public void setValueInt(Integer valueInt){
	this.valueInt=valueInt;
}

public String getAction(){
	return action;
}
public void setAction(String action){
	this.action=action;
}
}
