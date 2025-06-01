package edu.esiea.inventorymanager.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Stock")
@NamedQueries({ @NamedQuery(name = "Stock.findById", query = "SELECT sto FROM Stock sto WHERE sto.id = :id"),
		@NamedQuery(name = "Stock.findAll", query = "SELECT sto FROM Stock sto"),
		@NamedQuery(name = "Stock.findByArticleId", query = "SELECT sto FROM Stock sto JOIN sto.article art WHERE art.id = :id"),
		@NamedQuery(name = "Stock.findAllbyTransferType", query = "SELECT sto FROM Stock sto WHERE sto.transferType = :transferType") })
public class Stock {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "Date", nullable = false, length = 30)
	private LocalDate date;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Article", referencedColumnName = "Id")
	private Article article;
	@Column(name = "Quantity", nullable = false, length = 30)
	private int quantity;
	@Enumerated(EnumType.STRING)
	@Column(name = "TransferType", nullable = false, length = 3)
	private InOut transferType;
	@Column(name = "Comment", nullable = false, length = 30)
	private String comment;

	public Stock(LocalDate date, Article article, int quantity, InOut transferType, String comment) {
		this.date = date;
		this.article = article;
		this.quantity = quantity;
		this.transferType = transferType;
		this.comment = comment;
	}

	public Stock() {
	}

	public int getId() {
		return id;
	}

	// Obligation de laisser setId pour que les classes de test service focntionne
	// du à l'utilisation d'une persistence RAM
	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public InOut getTransferType() {
		return transferType;
	}

	public void setTransferType(InOut transferType) {
		this.transferType = transferType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
