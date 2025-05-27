package edu.esiea.inventorymanager.dao.bddimp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommandsDaoBddTest {

	private static Command command;

	private static final LocalDate PARAM_DATE = LocalDate.of(2025, 5, 27);
	private static final String PARAM_COMMENT = "Commande test";

	private static ICommandsDao dao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		command = new Command(PARAM_DATE, null, PARAM_COMMENT); // Pas de stocks associés
		DaoBddHelper.forceTestInstance();
		dao = DaoFactory.getInstance().getCommandsDao();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		command = null;
		EntityManager m = DaoBddHelper.getInstance().getEntityManager();
		if (m.isOpen()) {
			m.clear();
		}
	}

	@Test
	@Order(1)
	void testCreateCommand() {
		try {
			command = dao.createCommand(command);
		} catch (final DaoException e) {
			e.printStackTrace();
			fail("Impossible de créer la commande");
		}

		assertNotNull(command, "La commande n'a pas été créée !");
		assertTrue(command.getId() > 0, "L'ID de la commande est invalide !");
		assertEquals(PARAM_DATE, command.getDate(), "Date incorrecte.");
		assertEquals(PARAM_COMMENT, command.getComment(), "Commentaire incorrect.");
	}

	@Test
	@Order(2)
	void testGetAllCommands() throws DaoException {

		List<Command> commands = dao.getAllCommands();

		assertNotNull(commands, "La liste des commandes est NULL !");
		assertTrue(commands.size() > 0, "Aucune commande trouvée !");
		assertTrue(commands.contains(command), "La commande créée avant n'est pas retournée !");
	}

	@Test
	@Order(3)
	void testGetCommandById() throws DaoException {
		Command fetchedCommand = dao.getCommandById(command.getId());

		assertNotNull(fetchedCommand, "La commande récupérée est NULL !");
		assertEquals(PARAM_DATE, fetchedCommand.getDate(), "La date de la commande est incorrecte !");
		assertEquals(PARAM_COMMENT, fetchedCommand.getComment(), "Le commentaire de la commande est incorrect !");
	}

	@Test
	@Order(4)
	void testUpdateCommand() throws DaoException {
		command.setComment("Commande modifiée");
		dao.updateCommand(command);

		Command updatedCommand = dao.getCommandById(command.getId());

		assertNotNull(updatedCommand, "La commande mise à jour est NULL !");
		assertEquals("Commande modifiée", updatedCommand.getComment(), "Le commentaire n'a pas été mis à jour !");
	}

	@Test
	@Order(5)
	void testDeleteCommand() throws DaoException {
		dao.deleteCommand(command);
		Command deletedCommand = dao.getCommandById(command.getId());
		assertNull(deletedCommand, "La commande n'a pas été supprimée !");
	}
}