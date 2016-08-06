package com.wuxing.bean;

import java.util.List;

public class UserBean {
	private List<Data> data;
	private Status status;

	public void setData(List<Data> data) {
		this.data = data;
	}

	public List<Data> getData() {
		return data;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
	
	public class Data {


	    private String user_name;
	    private String headimg;
	    private String alias;
	    

	    public String getUser_name() {
			return user_name;
		}
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		public void setHeadimg(String headimg) {
	         this.headimg = headimg;
	     }
	     public String getHeadimg() {
	         return headimg;
	     }

	    public void setAlias(String alias) {
	         this.alias = alias;
	     }
	     public String getAlias() {
	         return alias;
	     }

	}
	
	public class Status {

	    private int succeed;
	    public void setSucceed(int succeed) {
	         this.succeed = succeed;
	     }
	     public int getSucceed() {
	         return succeed;
	     }

	}

}
