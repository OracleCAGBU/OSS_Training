package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
public class Parameter {

	private String name;
	private String parentBiParameterName;
	private String defaultValue;
	private String childSpecification;
	private String childEntityType;
	private String parentSpecification;
	private String parentParameterName;
	private String parentParameterConfigItemName;
	
	
	public Parameter(){
		
	}
	
	public Parameter( String parentSpecification,String childSpecification, String name, String parentBiParameterName, String defaultValue, String parentParameterName, String parentParameterConfigItemName) {
		this.name = name;
		this.parentBiParameterName = parentBiParameterName;
		this.defaultValue  = defaultValue;
		this.childSpecification  = childSpecification;
		this.parentSpecification  = parentSpecification;
		this.parentParameterName = parentParameterName;
		this.parentParameterConfigItemName = parentParameterConfigItemName;
	}
	
	public Parameter( String parentSpecification,String childSpecification, String childEntityType, String name, String parentBiParameterName, String defaultValue, String parentParameterName, String parentParameterConfigItemName) {
		this.name = name;
		this.parentBiParameterName = parentBiParameterName;
		this.defaultValue  = defaultValue;
		this.childSpecification  = childSpecification;
		this.childEntityType  = childEntityType;
		this.parentSpecification  = parentSpecification;
		this.parentParameterName = parentParameterName;
		this.parentParameterConfigItemName = parentParameterConfigItemName;
	}
	
	public Parameter(String name, String parentBiParameterName, String defaultValue, String parentParameterName, String parentParameterConfigItemName) {
		this.name = name;
		this.parentBiParameterName = parentBiParameterName;
		this.defaultValue  = defaultValue;
		this.parentParameterName = parentParameterName;
		this.parentParameterConfigItemName = parentParameterConfigItemName;
	}
	

	
	public String getChildSpecification() {
		return childSpecification;
	}

	public void setChildSpecification(String childSpecification) {
		this.childSpecification = childSpecification;
	}
	
	public String getChildEntityType() {
		return childEntityType;
	}

	public void setChildEntityType(String childEntityType) {
		this.childEntityType = childEntityType;
	}
	
	public String getParentSpecification() {
		return parentSpecification;
	}

	public void setParentSpecification(String parentSpecification) {
		this.parentSpecification = parentSpecification;
	}
	
	public String getParentParameterName() {
		return parentParameterName;
	}

	public void setParentParameterName(String parentParameterName) {
		this.parentParameterName = parentParameterName;
	}
	
	public String getParentParameterConfigItemName() {
		return parentParameterConfigItemName;
	}

	public void setParentParameterConfigItemName(String parentParameterConfigItemName) {
		this.parentParameterConfigItemName = parentParameterConfigItemName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}	
	
	public String getParentBiParameterName() {
		return parentBiParameterName;
	}

	public void setParentBiParameterName(String parentBiParameterName) {
		this.parentBiParameterName = parentBiParameterName;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Parameter Details - ");
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("DefaultValue:" + getDefaultValue());
		sb.append(", ");
		sb.append("ParentBiParameterName:" + getParentBiParameterName());
		sb.append(", ");
		sb.append("ParentSpecification:" + getParentSpecification());
		sb.append(", ");
		sb.append("ChildSpecification:" + getChildSpecification());
		sb.append(".");
		sb.append("ChildEntityType:" + getChildEntityType());
		sb.append(".");
		sb.append("ParentParameterConfigItemName:" + getParentParameterConfigItemName());
		sb.append(", ");
		sb.append("ParentParameterName:" + getParentParameterName());
		sb.append(".");		
		return sb.toString();
	}
}
