package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;

public class Matchdata implements Serializable {
		private String value;
		private String tableName;
		
		private String value2;
		private String tableName2;
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getTableName2() {
			return tableName2;
		}
		public void setTableName2(String tableName2) {
			this.tableName2 = tableName2;
		}
		public String getValue2() {
			return value2;
		}
		public void setValue2(String value2) {
			this.value2 = value2;
		}
		
	}