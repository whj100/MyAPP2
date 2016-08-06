package com.wuxing.bean;

import java.util.List;

public class MemberBean {
	private Data data;

	private Status status;

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return this.data;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return this.status;
	}

	public class Data {
		private List<Member> member;

		private String owner;

		public void setMember(List<Member> member) {
			this.member = member;
		}

		public List<Member> getMember() {
			return this.member;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getOwner() {
			return this.owner;
		}

		public class Member {
			private String user_name;

			private String headimg;

			private String alias;

			public void setUser_name(String user_name) {
				this.user_name = user_name;
			}

			public String getUser_name() {
				return this.user_name;
			}

			public void setHeadimg(String headimg) {
				this.headimg = headimg;
			}

			public String getHeadimg() {
				return this.headimg;
			}

			public void setAlias(String alias) {
				this.alias = alias;
			}

			public String getAlias() {
				return this.alias;
			}

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
