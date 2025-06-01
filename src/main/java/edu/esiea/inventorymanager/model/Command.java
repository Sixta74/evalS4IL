package edu.esiea.inventorymanager.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Command")
@NamedQueries({ @NamedQuery(name = "Command.findById", query = "SELECT com FROM Command com WHERE com.id = :id"),
		@NamedQuery(name = "Command.findAll", query = "SELECT com FROM Command com"),
		@NamedQuery(name = "Command.findByStockId", query = "SELECT com FROM Command com JOIN com.stocks s WHERE s.id = :id") })
public class Command {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "Date", nullable = false, length = 30)
	private LocalDate date;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "Command", referencedColumnName = "Id")
	private List<Stock> stocks;
	@Column(name = "Comment", nullable = false, length = 30)
	private String comment;

	public Command(LocalDate date, List<Stock> stocks, String comment) {
		this.date = date;
		this.stocks = stocks;
		this.comment = comment;
	}

	public Command() {
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

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
