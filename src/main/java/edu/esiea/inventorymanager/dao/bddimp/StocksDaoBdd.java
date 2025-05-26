package edu.esiea.inventorymanager.dao.bddimp;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.InOut;
import edu.esiea.inventorymanager.model.Stock;

public class StocksDaoBdd implements IStocksDao {

	private final DaoBddHelper bdd = DaoBddHelper.getInstance();

	@Override
	public Stock createStock(final Stock sto) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().persist(sto);
			this.bdd.commitTransaction();
			return sto;
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de créer le stock.", e);
		}
	}

	@Override
	public List<Stock> getAllStocks() throws DaoException {
		final TypedQuery<Stock> query = this.bdd.getEntityManager().createNamedQuery("Stock.findAll", Stock.class);
		return query.getResultList();
	}

	@Override
	public Stock getStockById(final int id) throws DaoException {
		final TypedQuery<Stock> query = this.bdd.getEntityManager().createNamedQuery("Stock.findById", Stock.class);
		query.setParameter("id", id);
		return query.getResultList().isEmpty() ? null : query.getResultList().getFirst();
	}

	@Override
	public List<Stock> getStocksByArticleId(final int id) throws DaoException {
		final TypedQuery<Stock> query = this.bdd.getEntityManager().createNamedQuery("Stock.findByArticleId",
				Stock.class);
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public List<Stock> getStocksByTransferType(final InOut transferType) throws DaoException {
		final TypedQuery<Stock> query = this.bdd.getEntityManager().createNamedQuery("Stock.findAllbyTransferType",
				Stock.class);
		query.setParameter("transferType", transferType);
		return query.getResultList();
	}

	@Override
	public void updateStock(final Stock sto) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().merge(sto);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de modifier le stock.", e);
		}
	}

	@Override
	public void deleteStock(final Stock sto) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().remove(sto);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de supprimer le stock.", e);
		}
	}
}