package edu.esiea.inventorymanager.dao.bddimp;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;

public class CommandsDaoBdd implements ICommandsDao {

	private final DaoBddHelper bdd = DaoBddHelper.getInstance();

	@Override
	public Command createCommand(final Command com) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().persist(com);
			this.bdd.commitTransaction();
			return com;
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de créer la commande.", e);
		}
	}

	@Override
	public List<Command> getAllCommands() throws DaoException {
		final TypedQuery<Command> query = this.bdd.getEntityManager().createNamedQuery("Command.findAll",
				Command.class);
		return query.getResultList();
	}

	@Override
	public Command getCommandById(final int id) throws DaoException {
		final TypedQuery<Command> query = this.bdd.getEntityManager().createNamedQuery("Command.findById",
				Command.class);
		query.setParameter("id", id);
		return query.getResultList().isEmpty() ? null : query.getResultList().getFirst();
	}

	@Override
	public Command getCommandByStockId(final int id) throws DaoException {
		final TypedQuery<Command> query = this.bdd.getEntityManager().createNamedQuery("Command.findByStockId",
				Command.class);
		query.setParameter("id", id);
		return query.getResultList().isEmpty() ? null : query.getResultList().getFirst();
	}

	@Override
	public void updateCommand(final Command com) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().merge(com);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de modifier la commande.", e);
		}
	}

	@Override
	public void deleteCommand(final Command com) throws DaoException {
		try {
			this.bdd.beginTransaction();
			this.bdd.getEntityManager().remove(com);
			this.bdd.commitTransaction();
		} catch (final PersistenceException e) {
			this.bdd.rollBackTransaction();
			throw new DaoException("Impossible de supprimer la commande.", e);
		}
	}
}