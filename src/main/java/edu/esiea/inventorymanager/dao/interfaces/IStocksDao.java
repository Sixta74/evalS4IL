package edu.esiea.inventorymanager.dao.interfaces;

import java.util.List;

import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.InOut;
import edu.esiea.inventorymanager.model.Stock;

public interface IStocksDao {

	/**
	 * Stores given Stock in the persistence layer and returns it with its id set.
	 *
	 * @param sto the stock to be stored
	 * @return stored stock with its Id set.
	 * @throws DaoException in case of error.
	 */
	Stock createStock(Stock sto) throws DaoException;

	/**
	 * Reads all Stocks in the persistence layer and returns them.
	 *
	 * @return All stored Stocks in a {@link List}. This List can be empty but it
	 *         <u><b>can't</b></u> be <code>null</code>.
	 * @throws DaoException in case of error.
	 */
	List<Stock> getAllStocks() throws DaoException;

	/**
	 * Returns stored stock whose id corresponds to the given one.
	 *
	 * @param id Id of the Stock to get.
	 * @return Stored Stock if found, <code>null</code> if no Stock matches the
	 *         given Id.
	 * @throws DaoException in case of error.
	 */
	Stock getStockById(int id) throws DaoException;

	/**
	 * Returns stored Stocks that are linked to an {@link Article} whose id
	 * corresponds to the given one.
	 *
	 * @param id Id of the {@link Article} associated with the Stock to get.
	 * @return List of Stocks linked to the given Article.
	 * @throws DaoException in case of error.
	 */
	List<Stock> getStocksByArticleId(int id) throws DaoException;

	/**
	 * Returns all Stocks filtered by their TransferType.
	 *
	 * @param transferType The type of transfer.
	 * @return List of Stocks matching the given TransferType.
	 * @throws DaoException in case of error.
	 */
	List<Stock> getStocksByTransferType(InOut transferType) throws DaoException;

	/**
	 * Updates the given Stock in the persistence layer.
	 *
	 * @param sto the Stock to be updated.
	 * @throws DaoException in case of error.
	 */
	void updateStock(Stock sto) throws DaoException;

	/**
	 * Deletes the stored Stock whose id corresponds to the given one.
	 *
	 * @param sto Stock to delete.
	 * @throws DaoException in case of error.
	 */
	void deleteStock(Stock sto) throws DaoException;
}