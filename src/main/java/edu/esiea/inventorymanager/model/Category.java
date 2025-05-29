package edu.esiea.inventorymanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Category")
@NamedQueries({ @NamedQuery(name = "Category.findById", query = "SELECT cat FROM Category cat WHERE cat.id = :id"),
		@NamedQuery(name = "Category.findAll", query = "SELECT cat FROM Category cat") })
public class Category {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "Name", nullable = false, length = 30)
	private String name;
	@Column(name = "Description", nullable = false, length = 30)
	private String description;

	public Category(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Category() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
