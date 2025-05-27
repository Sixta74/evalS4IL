package edu.esiea.inventorymanager.dao.bddimp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import edu.esiea.inventorymanager.exception.DaoException;

public class DaoBddHelper {
	private static DaoBddHelper instance;
	private final EntityManager entityManager;

	public static DaoBddHelper getInstance() throws DaoException {
		if (instance == null) {
			instance = new DaoBddHelper("evalbdds4");
		}
		return instance;
	}

	/**
	 * This method allow to set the persistance target as the test one
	 * 
	 * @return the instance of this class initialized with test persistance unit
	 * @throws DaoException in case of error
	 */

	public static DaoBddHelper forceTestInstance() throws DaoException {
		instance = new DaoBddHelper("evalbdds4Test");
		return instance;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	private DaoBddHelper(final String persistanceUnitName) throws DaoException {
		try {
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistanceUnitName);
			this.entityManager = emf.createEntityManager();
			final org.eclipse.persistence.sessions.Session session = this.entityManager
					.unwrap(org.eclipse.persistence.sessions.Session.class);
			System.out.println("Entity manager créé : " + session.getDatasourcePlatform().toString());
		} catch (final Exception e) {
			throw new DaoException("Impossible de créer l'Entity Manager", e);
		}
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
