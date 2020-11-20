package com.temenos.template.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ComponentMaster {

	@Id
	Integer id;
	
	String componentProperties;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComponentProperties() {
		return componentProperties;
	}

	public void setComponentProperties(String componentProperties) {
		this.componentProperties = componentProperties;
	}	
}
