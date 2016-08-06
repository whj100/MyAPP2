package com.wuxing.bean;

public class LoginBean {
	private Data data;
	private Status status;

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public class Data {

		private Session session;

		public void setSession(Session session) {
			this.session = session;
		}

		public Session getSession() {
			return session;
		}

		public class Session {

			private String user_id;
			private String hxusername;
			private String hxpassword;

			public String getUser_id() {
				return user_id;
			}

			public void setUser_id(String user_id) {
				this.user_id = user_id;
			}

			public void setHxusername(String hxusername) {
				this.hxusername = hxusername;
			}

			public String getHxusername() {
				return hxusername;
			}

			public void setHxpassword(String hxpassword) {
				this.hxpassword = hxpassword;
			}

			public String getHxpassword() {
				return hxpassword;
			}

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