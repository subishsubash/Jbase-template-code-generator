package com.temenos.template.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FieldMaster {

	@Id
	Integer id;
	
	String fieldProperties;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFieldProperties() {
		return fieldProperties;
	}

	public void setFieldProperties(String fieldProperties) {
		this.fieldProperties = fieldProperties;
	}

}
