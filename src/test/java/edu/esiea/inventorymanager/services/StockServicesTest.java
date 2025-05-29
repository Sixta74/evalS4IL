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
import edu.esiea.inventorymanager.model.Stock;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StockServicesTest extends JerseyTest {

	private static final String PARAM_DATE = LocalDate.now().toString();
	private static final String PARAM_ARTICLE_ID = "1"; // ID d'un article fictif
	private static final String PARAM_QUANTITY = "10";
	private static final String PARAM_TRANSFER_TYPE = "IN"; // Valeur fictive : IN ou OUT
	private static final String PARAM_COMMENT = "Stock Test";

	private static int httpStatus;
	private static Stock stock;

	@Override
	protected Application configure() {
		return new ResourceConfig(StockServices.class);
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

	private void callAddService(final String date, final String articleId, final String quantity,
			final String transferType, final String comment) {
		final Form formulaire = new Form();
		if (date != null) {
			formulaire.param(StockServices.PARAM_STOCK_DATE, date);
		}
		if (articleId != null) {
			formulaire.param(StockServices.PARAM_STOCK_ARTICLE_ID, articleId);
		}
		if (quantity != null) {
			formulaire.param(StockServices.PARAM_STOCK_QUANTITY, quantity);
		}
		if (transferType != null) {
			formulaire.param(StockServices.PARAM_STOCK_TRANSFER_TYPE, transferType);
		}
		if (comment != null) {
			formulaire.param(StockServices.PARAM_STOCK_COMMENT, comment);
		}

		final Response response = target("/stock/add").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.CREATED.getStatusCode()) {
			try {
				stock = response.readEntity(Stock.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers un Stock", e);
			}
		}
	}

	@Test
	@Order(1)
	void testAddStock() {
		callAddService(PARAM_DATE, PARAM_ARTICLE_ID, PARAM_QUANTITY, PARAM_TRANSFER_TYPE, PARAM_COMMENT);
		assertEquals(Response.Status.CREATED.getStatusCode(), httpStatus, "Le status devrait être CREATED.");
		assertNotNull(stock, "Erreur de création du stock.");
		assertTrue(stock.getId() > 0, "L'ID du stock créé est incorrect.");
		assertEquals(PARAM_DATE, stock.getDate().toString(), "La date du stock ne correspond pas.");
		assertEquals(PARAM_COMMENT, stock.getComment(), "Le commentaire du stock ne correspond pas.");

		callAddService("", PARAM_ARTICLE_ID, PARAM_QUANTITY, PARAM_TRANSFER_TYPE, PARAM_COMMENT);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);

		callAddService(null, PARAM_ARTICLE_ID, PARAM_QUANTITY, PARAM_TRANSFER_TYPE, PARAM_COMMENT);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(2)
	void testGetStockById() {
		Response response = target("/stock/" + stock.getId()).request().accept(MediaType.APPLICATION_JSON).get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Stock fetchedStock = response.readEntity(Stock.class);
		assertEquals(stock.getComment(), fetchedStock.getComment());

		response = target("/stock/-1").request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	@Test
	@Order(3)
	void testUpdateStock() {
		final String strId = Integer.toString(stock.getId());
		callUpdateService(strId, LocalDate.now().plusDays(1).toString(), PARAM_ARTICLE_ID, "20", "OUT",
				"Updated Stock Comment");
		assertEquals(Response.Status.OK.getStatusCode(), httpStatus);
		assertNotNull(stock);
		assertEquals("Updated Stock Comment", stock.getComment());

		callUpdateService("-1", LocalDate.now().plusDays(1).toString(), PARAM_ARTICLE_ID, "20", "OUT",
				"Updated Stock Comment");
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), httpStatus);

		callUpdateService("NotNumeric", LocalDate.now().plusDays(1).toString(), PARAM_ARTICLE_ID, "20", "OUT",
				"Updated Stock Comment");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(4)
	void testDeleteStock() {
		Response response = target("/stock/delete/" + stock.getId()).request().delete();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		response = target("/stock/" + stock.getId()).request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	private void callUpdateService(final String id, final String date, final String articleId, final String quantity,
			final String transferType, final String comment) {
		final Form formulaire = new Form();
		if (id != null) {
			formulaire.param(StockServices.PARAM_STOCK_ID, id);
		}
		if (date != null) {
			formulaire.param(StockServices.PARAM_STOCK_DATE, date);
		}
		if (articleId != null) {
			formulaire.param(StockServices.PARAM_STOCK_ARTICLE_ID, articleId);
		}
		if (quantity != null) {
			formulaire.param(StockServices.PARAM_STOCK_QUANTITY, quantity);
		}
		if (transferType != null) {
			formulaire.param(StockServices.PARAM_STOCK_TRANSFER_TYPE, transferType);
		}
		if (comment != null) {
			formulaire.param(StockServices.PARAM_STOCK_COMMENT, comment);
		}

		final Response response = target("/stock/update").request().accept(MediaType.APPLICATION_JSON)
				.put(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.OK.getStatusCode()) {
			try {
				stock = response.readEntity(Stock.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers un Stock", e);
			}
		}
	}
}