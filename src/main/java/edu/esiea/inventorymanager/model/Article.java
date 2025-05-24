package edu.esiea.inventorymanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Article")
public class Article {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "Name", nullable = false, length = 30)
	private String name;
	@Column(name = "EAN13", nullable = false, length = 30)
	private String EAN13;
	@Column(name = "brand", nullable = false, length = 30)
	private String brand;
	@Column(name = "picture_URL", nullable = false, length = 30)
	private String picture_URL;
	@Column(name = "price", nullable = false, length = 100)
	private float price;
	@Column(name = "description", nullable = false, length = 30)
	private String description;
	@Column(name = "Stock", nullable = false)
	private String Stock;

	public Article(String name, String eAN13, String brand, String picture_URL, float price, String description,
			String stock) {
		this.name = name;
		this.EAN13 = eAN13;
		this.brand = brand;
		this.picture_URL = picture_URL;
		this.price = price;
		this.description = description;
		this.Stock = stock;
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
