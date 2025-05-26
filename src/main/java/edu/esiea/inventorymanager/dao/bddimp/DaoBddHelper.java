package edu.esiea.inventorymanager.dao.bddimp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class DaoBddHelper {

	private static DaoBddHelper instance;
	private final EntityManager entityManager;

	public static DaoBddHelper getInstance() {
		if (instance == null) {
			instance = new DaoBddHelper();
		}
		return instance;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public DaoBddHelper() {
		try {
			Class.forName("org.eclipse.persistence.jpa.PersistenceProvider");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("evalbdds4");
		entityManager = emf.createEntityManager();
	}

	public void beginTransaction() {
		this.entityManager.getTransaction().begin();
	}

	public void commitTransaction() {
		final EntityTransaction trans = this.entityManager.getTransaction();
		if (trans.isActive()) {
			trans.commit();
		}
	}

	public void rollBackTransaction() {
		final EntityTransaction trans = this.entityManager.getTransaction();
		if (trans.isActive()) {
			trans.rollback();
		}
	}

}
