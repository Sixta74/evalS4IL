package edu.esiea.inventorymanager.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import edu.esiea.inventorymanager.dao.bddimp.DaoBddHelper;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommandServicesTest extends JerseyTest {

	private static final String PARAM_DATE = LocalDate.now().toString();
	private static final String PARAM_COMMENT = "Test Command";

	private static int httpStatus;
	private static Command command;

	@Override
	protected Application configure() {
		return new ResourceConfig(CommandServices.class);
	}

	@BeforeAll
	static void setUpBeforeClass() throws DaoException {
		DaoBddHelper.forceTestInstance();
	}

	@AfterAll
	static void tearDownAfterClass() throws DaoException {
		EntityManager m = DaoBddHelper.getInstance().getEntityManager();
		if (m.isOpen()) {
			m.clear();
		}
	}

	private void callAddService(final String date, final String comment) {
		final Form formulaire = new Form();
		if (date != null) {
			formulaire.param(CommandServices.PARAM_COM_DATE, date);
		}
		if (comment != null) {
			formulaire.param(CommandServices.PARAM_COM_COMMENT, comment);
		}

		final Response response = target("/command/add").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.CREATED.getStatusCode()) {
			try {
				command = response.readEntity(Command.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers une Command", e);
			}
		}
	}

	@Test
	@Order(1)
	void testAddCommand() {
		callAddService(PARAM_DATE, PARAM_COMMENT);
		assertEquals(Response.Status.CREATED.getStatusCode(), httpStatus, "Le status devrait être CREATED.");
		assertNotNull(command, "Erreur de création de la commande.");
		assertTrue(command.getId() > 0, "L'ID de la commande créée est incorrect.");
		assertEquals(PARAM_DATE, command.getDate().toString(), "La date de la commande ne correspond pas.");
		assertEquals(PARAM_COMMENT, command.getComment(), "Le commentaire de la commande ne correspond pas.");

		callAddService("", PARAM_COMMENT);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);

		callAddService(null, PARAM_COMMENT);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(2)
	void testGetCommandById() {
		Response response = target("/command/" + command.getId()).request().accept(MediaType.APPLICATION_JSON).get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Command fetchedCommand = response.readEntity(Command.class);
		assertEquals(command.getComment(), fetchedCommand.getComment());

		response = target("/command/-1").request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	@Test
	@Order(3)
	void testUpdateCommand() {
		final String strId = Integer.toString(command.getId());
		callUpdateService(strId, LocalDate.now().plusDays(1).toString(), "Updated Command Comment");
		assertEquals(Response.Status.OK.getStatusCode(), httpStatus);
		assertNotNull(command);
		assertEquals("Updated Command Comment", command.getComment());

		callUpdateService("-1", LocalDate.now().plusDays(1).toString(), "Updated Command Comment");
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), httpStatus);

		callUpdateService("NotNumeric", LocalDate.now().plusDays(1).toString(), "Updated Command Comment");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(4)
	void testDeleteCommand() {
		Response response = target("/command/delete/" + command.getId()).request().delete();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		response = target("/command/" + command.getId()).request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	private void callUpdateService(final String id, final String date, final String comment) {
		final Form formulaire = new Form();
		if (id != null) {
			formulaire.param(CommandServices.PARAM_COM_ID, id);
		}
		if (date != null) {
			formulaire.param(CommandServices.PARAM_COM_DATE, date);
		}
		if (comment != null) {
			formulaire.param(CommandServices.PARAM_COM_COMMENT, comment);
		}

		final Response response = target("/command/update").request().accept(MediaType.APPLICATION_JSON)
				.put(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.OK.getStatusCode()) {
			try {
				command = response.readEntity(Command.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers une Command", e);
			}
		}
	}
}