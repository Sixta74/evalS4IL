package edu.esiea.inventorymanager.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Article")
@NamedQueries({ @NamedQuery(name = "Article.findById", query = "SELECT art FROM Article art WHERE art.id = :id"),
		@NamedQuery(name = "Article.findAll", query = "SELECT art FROM Article art"),
		@NamedQuery(name = "Article.findByStockId", query = "SELECT art FROM Article art JOIN art.stocks s WHERE s.id = :id"),
		@NamedQuery(name = "Article.findAllByCategoryId", query = "SELECT art FROM Article art JOIN art.category cat WHERE cat.id = :id") })
public class Article {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "Name", nullable = false, length = 30)
	private String name;
	@Column(name = "EAN13", nullable = false, length = 30)
	private String EAN13;
	@Column(name = "Brand", nullable = false, length = 30)
	private String brand;
	@Column(name = "Picture_URL", nullable = false, length = 30)
	private String picture_URL;
	@Column(name = "Price", nullable = false, length = 100)
	private float price;
	@Column(name = "Description", nullable = false, length = 30)
	private String description;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "Category", referencedColumnName = "Id")
	private Category category;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "ArticleId", referencedColumnName = "Id")
	private List<Stock> stocks;

	public Article(String name, String eAN13, String brand, String picture_URL, float price, String description) {
		this.name = name;
		this.EAN13 = eAN13;
		this.brand = brand;
		this.picture_URL = picture_URL;
		this.price = price;
		this.description = description;
	}

	public Article() {
	}

	public int getId() {
		return id;
	}

	// Obligation de laisser setId pour que les classes de test service focntionne
	// du à l'utilisation d'une persistence RAM
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Stock> getStock() {
		return stocks;
	}

	public void setStock(List<Stock> stocks) {
		this.stocks = stocks;
	}

}
