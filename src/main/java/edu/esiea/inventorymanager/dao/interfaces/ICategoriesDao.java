package edu.esiea.inventorymanager.dao.interfaces;

import java.util.List;

import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Category;

public interface ICategoriesDao {

	/**
	 * Stores given Category in the persistence layer and returns it with its id
	 * set.
	 *
	 * @param cat the category to be stored
	 * @return stored category with its Id set.
	 * @throws DaoException in case of error.
	 */
	Category createCategory(Category cat) throws DaoException;

	/**
	 * Reads all Categories in the persistence layer and returns them.
	 *
	 * @return All stored Categories in a {@link List}. This List can be empty but
	 *         it <u><b>can't</b></u> be <code>null</code>.
	 * @throws DaoException in case of error.
	 */
	List<Category> getAllCategories() throws DaoException;

	/**
	 * Return stored category which id corresponds to the given one.
	 *
	 * @param id Id of Category to get
	 * @return Stored Category which id corresponds to the one given if found.
	 *         <code>null</code> if no Category has been found with the given Id.
	 * @throws DaoException in case of error.
	 */
	Category getCategoryById(int id) throws DaoException;

	/**
	 * Updates the given Category in the persistence layer.
	 *
	 * @param cat the Category to be updated
	 * @throws DaoException in case of error.
	 */
	void updateCategory(Category cat) throws DaoException;

	/**
	 * Deletes stored Category which id corresponds to the given one.
	 *
	 * @param cat Category to delete
	 * @throws DaoException in case of error.
	 */
	void deleteCategory(Category cat) throws DaoException;
}