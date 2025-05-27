package edu.esiea.inventorymanager.dao.bddimp;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Category;

public class CategoriesDaoBdd implements ICategoriesDao {

	private final DaoBddHelper bdd;

	public CategoriesDaoBdd() throws DaoException {
		this.bdd = DaoBddHelper.getInstance();
	}

	@Override
	public Category createCategory(final Category cat) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().persist(cat);
			this.bdd.commitTransaction();
			return cat;
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de créer la catégorie.", e);
		}
	}

	@Override
	public List<Category> getAllCategories() throws DaoException {
		final TypedQuery<Category> query = this.bdd.getEntityManager().createNamedQuery("Category.findAll",
				Category.class);
		return query.getResultList();
	}

	@Override
	public Category getCategoryById(final int id) throws DaoException {
		final TypedQuery<Category> query = this.bdd.getEntityManager().createNamedQuery("Category.findById",
				Category.class);
		query.setParameter("id", id);
		if (query.getResultList().size() > 0) {
			return query.getResultList().getFirst();
		}
		return null;
	}

	@Override
	public void updateCategory(final Category cat) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().merge(cat);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de modifier la catégorie.", e);
		}
	}

	@Override
	public void deleteCategory(final Category cat) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().remove(cat);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de supprimer la catégorie", e);
		}
	}
}