package com.sample.model;

public class HelloModel {

	private Integer id;
	private String description;

	public HelloModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HelloModel(Integer id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
