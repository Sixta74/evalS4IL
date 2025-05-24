package edu.esiea.inventorymanager.model;

public class Article {
	private int id;
	private String name;
	private String EAN13;
	private String brand;
	private String picture_URL;
	private float price;
	private String description;
	private String Stock;

	public Article(String name, String eAN13, String brand, String picture_URL, float price, String description,
			String stock) {
		super();
		this.name = name;
		EAN13 = eAN13;
		this.brand = brand;
		this.picture_URL = picture_URL;
		this.price = price;
		this.description = description;
		Stock = stock;
	}

	public Article() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEAN13() {
		return EAN13;
	}

	public void setEAN13(String eAN13) {
		EAN13 = eAN13;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPicture_URL() {
		return picture_URL;
	}

	public void setPicture_URL(String picture_URL) {
		this.picture_URL = picture_URL;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStock() {
		return Stock;
	}

	public void setStock(String stock) {
		Stock = stock;
	}

}
