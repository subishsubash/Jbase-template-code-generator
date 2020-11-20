package com.temenos.template.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TemplateMaster {

		@Id
		Integer id;
		
		String appProperties;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getAppProperties() {
			return appProperties;
		}

		public void setAppProperties(String appProperties) {
			this.appProperties = appProperties;
		}
}


