package edu.esiea.inventorymanager.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import edu.esiea.inventorymanager.model.Category;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServicesTest extends JerseyTest {

	private static final String PARAM_NAME = "TestCategory";
	private static final String PARAM_DESCRIPTION = "Description de la catégorie";

	private static int httpStatus;
	private static Category category;

	@Override
	protected Application configure() {
		return new ResourceConfig(CategoryServices.class);
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

	private void callAddService(final String name, final String description) {
		final Form formulaire = new Form();
		if (name != null) {
			formulaire.param(CategoryServices.PARAM_CAT_NAME, name);
		}
		if (description != null) {
			formulaire.param(CategoryServices.PARAM_CAT_DESCRIPTION, description);
		}

		final Response response = target("/category/add").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.CREATED.getStatusCode()) {
			try {
				category = response.readEntity(Category.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers une Category", e);
			}
		}
	}

	@Test
	@Order(1)
	void testAddCategory() {
		callAddService(PARAM_NAME, PARAM_DESCRIPTION);
		assertEquals(Response.Status.CREATED.getStatusCode(), httpStatus, "Le status devrait être CREATED.");
		assertNotNull(category, "Erreur de création de la catégorie.");
		assertTrue(category.getId() > 0, "L'ID de la catégorie créée est incorrect.");
		assertEquals(PARAM_NAME, category.getName(), "Le nom de la catégorie ne correspond pas.");
		assertEquals(PARAM_DESCRIPTION, category.getDescription(), "La description de la catégorie ne correspond pas.");

		callAddService("", PARAM_DESCRIPTION);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);

		callAddService(null, PARAM_DESCRIPTION);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(2)
	void testGetCategoryById() {
		Response response = target("/category/" + category.getId()).request().accept(MediaType.APPLICATION_JSON).get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Category fetchedCategory = response.readEntity(Category.class);
		assertEquals(category.getName(), fetchedCategory.getName());

		response = target("/category/-1").request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	@Test
	@Order(3)
	void testUpdateCategory() {
		final String strId = Integer.toString(category.getId());
		callUpdateService(strId, "UpdatedCategoryName", "Nouvelle description");
		assertEquals(Response.Status.OK.getStatusCode(), httpStatus);
		assertNotNull(category);
		assertEquals("UpdatedCategoryName", category.getName());

		callUpdateService("-1", "UpdatedCategoryName", "Nouvelle description");
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), httpStatus);

		callUpdateService("NotNumeric", "UpdatedCategoryName", "Nouvelle description");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(4)
	void testDeleteCategory() {
		Response response = target("/category/delete/" + category.getId()).request().delete();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		response = target("/category/" + category.getId()).request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	private void callUpdateService(final String id, final String name, final String description) {
		final Form formulaire = new Form();
		if (id != null) {
			formulaire.param(CategoryServices.PARAM_CAT_ID, id);
		}
		if (name != null) {
			formulaire.param(CategoryServices.PARAM_CAT_NAME, name);
		}
		if (description != null) {
			formulaire.param(CategoryServices.PARAM_CAT_DESCRIPTION, description);
		}

		final Response response = target("/category/update").request().accept(MediaType.APPLICATION_JSON)
				.put(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.OK.getStatusCode()) {
			try {
				category = response.readEntity(Category.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers une Category", e);
			}
		}
	}
}