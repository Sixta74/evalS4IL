package edu.esiea.inventorymanager.dao.bddimp;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;

public class ArticlesDaoBdd implements IArticlesDao {

	private final DaoBddHelper bdd = DaoBddHelper.getInstance();

	@Override
	public Article createArticle(final Article a) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().persist(a);
			this.bdd.commitTransaction();
			return a;
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de créer l'article.", e);
		}
	}

	@Override
	public List<Article> getAllArticles() throws DaoException {
		final TypedQuery<Article> query = this.bdd.getEntityManager().createNamedQuery("Article.findAll",
				Article.class);
		return query.getResultList();
	}

	@Override
	public Article getArticleById(final int id) throws DaoException {
		final TypedQuery<Article> query = this.bdd.getEntityManager().createNamedQuery("Article.findById",
				Article.class);
		query.setParameter("id", id);
		if (query.getResultList().size() > 0) {
			return query.getResultList().getFirst();
		}
		return null;
	}

	@Override
	public Article getArticleByStockId(final int id) throws DaoException {
		final TypedQuery<Article> query = this.bdd.getEntityManager().createNamedQuery("Article.findByStockId",
				Article.class);
		query.setParameter("id", id);
		if (query.getResultList().size() > 0) {
			return query.getResultList().getFirst();
		}
		return null;
	}

	@Override
	public List<Article> getAllArticlesByCategoryId(final int categoryId) throws DaoException {
		final TypedQuery<Article> query = this.bdd.getEntityManager().createNamedQuery("Article.findByCategoryId",
				Article.class);
		query.setParameter("id", categoryId);
		return query.getResultList();
	}

	@Override
	public void updateArticle(final Article a) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().merge(a);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de modifier l'article.", e);
		}
	}

	@Override
	public void deleteArticle(final Article a) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().remove(a);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de supprimer l'article", e);
		}
	}
}
