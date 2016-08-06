package com.wuxing.bean;

import java.util.List;

public class PositionBean {
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

		private String user_id;
		private String user_name;
		private String lonandlat;

		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public String getUser_name() {
			return user_name;
		}

		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}

		public void setLonandlat(String lonandlat) {
			this.lonandlat = lonandlat;
		}

		public String getLonandlat() {
			return lonandlat;
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