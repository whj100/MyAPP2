package com.wuxing.bean;

import java.util.List;

public class GroupBean {
	private List<Data> data;

	private Status status;

	public void setData(List<Data> data) {
		this.data = data;
	}

	public List<Data> getData() {
		return this.data;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return this.status;
	}

	public class Data {
		private String groupid;

		private String groupname;
		
		private String headImg;
		

		public String getHeadImg() {
			return headImg;
		}

		public void setHeadImg(String headImg) {
			this.headImg = headImg;
		}

		public void setGroupid(String groupid) {
			this.groupid = groupid;
		}

		public String getGroupid() {
			return this.groupid;
		}

		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		public String getGroupname() {
			return this.groupname;
		}

	}

	public class Status {
		private int succeed;

		public void setSucceed(int succeed) {
			this.succeed = succeed;
		}

		public int getSucceed() {
			return this.succeed;
		}

	}

	
}
