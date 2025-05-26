package edu.esiea.inventorymanager.dao.interfaces;

import java.util.List;

import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Stock;

public interface IArticlesDao {

	/**
	 * Stores given Article in the persitency Layer and returns it with its id set.
	 *
	 * @param a the system to be stored
	 * @return stored system with its Id set.
	 * @throws DaoException in case of error.
	 */
	Article createArticle(Article a) throws DaoException;

	/**
	 * Reads all Article in the persitency Layer and returns them.
	 *
	 * @return All stored Article in a {@link List}. This List can be empty but it
	 *         <u><b>can't</b></u> be <code>null</code>.
	 * @throws DaoException in case of error.
	 */
	List<Article> getAllArticles() throws DaoException;

	/**
	 * Return stored system which id correspond to the one given.
	 *
	 * @param id Id of Article to get
	 * @return Stored Article which id correspond to the one given if found.
	 *         <code>null</code> if no Article has been found with given Id.
	 * @throws DaoException in case of error.
	 */
	Article getArticleById(int id) throws DaoException;

	/**
	 * Return stored Article that contains the {@link Stock} which id correspond to
	 * the one given.
	 *
	 * @param id Id of the {@link Stock} of the System to get
	 * @return Stored system which {@link Stock}'s id correspond to the one given if
	 *         found. <code>null</code> if no Article has been found with given
	 *         {@link Stock}.
	 * @throws DaoException in case of error.
	 */
	Article getArticleByStockId(int id) throws DaoException;

	/**
	 * Updates given Article in the persitency Layer.
	 *
	 * @param a the Article to be updated
	 * @throws DaoException in case of error.
	 */
	void updateArticle(Article a) throws DaoException;

	/**
	 * Delete stored Article which id correspond to the one given.
	 *
	 * @param a Article to delete
	 * @throws DaoException in case of error.
	 */
	void deleteArticle(Article a) throws DaoException;

}
