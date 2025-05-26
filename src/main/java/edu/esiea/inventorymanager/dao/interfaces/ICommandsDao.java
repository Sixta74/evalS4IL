package edu.esiea.inventorymanager.dao.interfaces;

import java.util.List;

import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;
import edu.esiea.inventorymanager.model.Stock;

public interface ICommandsDao {

	/**
	 * Stores given Command in the persistence layer and returns it with its id set.
	 *
	 * @param com the command to be stored
	 * @return stored command with its Id set.
	 * @throws DaoException in case of error.
	 */
	Command createCommand(Command com) throws DaoException;

	/**
	 * Reads all Commands in the persistence layer and returns them.
	 *
	 * @return All stored Commands in a {@link List}. This List can be empty but it
	 *         <u><b>can't</b></u> be <code>null</code>.
	 * @throws DaoException in case of error.
	 */
	List<Command> getAllCommands() throws DaoException;

	/**
	 * Returns stored command whose id corresponds to the given one.
	 *
	 * @param id Id of the Command to get.
	 * @return Stored Command if found, <code>null</code> if no Command matches the
	 *         given Id.
	 * @throws DaoException in case of error.
	 */
	Command getCommandById(int id) throws DaoException;

	/**
	 * Returns stored Commands that contain a {@link Stock} whose id corresponds to
	 * the given one.
	 *
	 * @param id Id of the {@link Stock} associated with the Command to get.
	 * @return Stored Command containing the given Stock if found, <code>null</code>
	 *         otherwise.
	 * @throws DaoException in case of error.
	 */
	Command getCommandByStockId(int id) throws DaoException;

	/**
	 * Updates the given Command in the persistence layer.
	 *
	 * @param com the Command to be updated.
	 * @throws DaoException in case of error.
	 */
	void updateCommand(Command com) throws DaoException;

	/**
	 * Deletes the stored Command whose id corresponds to the given one.
	 *
	 * @param com Command to delete.
	 * @throws DaoException in case of error.
	 */
	void deleteCommand(Command com) throws DaoException;
}