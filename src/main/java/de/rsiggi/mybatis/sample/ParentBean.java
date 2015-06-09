package de.rsiggi.mybatis.sample;

public class ParentBean {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChildBean getClient() {
		return client;
	}

	public void setClient(ChildBean client) {
		this.client = client;
	}

	private ChildBean client;

	@Override
	public String toString() {
		return "ParentBean [name=" + name + ", client=" + client + "]";
	}


}
