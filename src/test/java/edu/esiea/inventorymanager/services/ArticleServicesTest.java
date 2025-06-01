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
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Category;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticleServicesTest extends JerseyTest {

	private static final String PARAM_NAME_1 = "ArticleTest1";
	private static final String PARAM_EAN13_1 = "1234567890123";
	private static final String PARAM_BRAND_1 = "BrandTest1";
	private static final String PARAM_PICTURE_1 = "image1.jpg";
	private static final String PARAM_PRICE_1 = "15.99";
	private static final String PARAM_DESCRIPTION_1 = "Description article 1";
	private static Category category = new Category("Category1", "The first category of my categories");

	private static int httpStatus;
	private static Article article;

	@Override
	protected Application configure() {
		return new ResourceConfig(ArticleServices.class);
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

	private void callAddService(final String name, final String ean, final String brand, final String picture,
			final String price, final String description) {
		final Form formulaire = new Form();
		if (name != null) {
			formulaire.param(ArticleServices.PARAM_ART_NAME, name);
		}
		if (ean != null) {
			formulaire.param(ArticleServices.PARAM_ART_EAN13, ean);
		}
		if (brand != null) {
			formulaire.param(ArticleServices.PARAM_ART_BRAND, brand);
		}
		if (picture != null) {
			formulaire.param(ArticleServices.PARAM_ART_PICTURE, picture);
		}
		if (price != null) {
			formulaire.param(ArticleServices.PARAM_ART_PRICE, price);
		}
		if (description != null) {
			formulaire.param(ArticleServices.PARAM_ART_DESCRIPTION, description);
		}

		final Response response = target("/article/add").request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.CREATED.getStatusCode()) {
			try {
				article = response.readEntity(Article.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers un Article", e);
			}
		}
	}

	@Test
	@Order(1)
	void testAddArticle() {
		callAddService(PARAM_NAME_1, PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1, PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.CREATED.getStatusCode(), httpStatus,
				"Le status de la réponse devrait être created.");
		assertNotNull(article, "Echec du mappage réponse article.");
		assertTrue(article.getId() > 0, "L'article créé n'a pas l'id adéquat.");
		assertEquals(PARAM_NAME_1, article.getName(), "Le nom du premier article n'est pas bon.");
		assertEquals(PARAM_EAN13_1, article.getEAN13(), "Le code EAN du premier article n'est pas bon.");
		assertEquals(PARAM_BRAND_1, article.getBrand(), "Le code EAN du premier article n'est pas bon.");
		assertEquals(PARAM_PICTURE_1, article.getPicture_URL(), "L'image du premier article n'est pas bonne.");
		float expectedPrice = Float.parseFloat(PARAM_PRICE_1);
		assertEquals(expectedPrice, article.getPrice(), 0.0001, "Le prix du premier article n'est pas bon.");
		// assertEquals(PARAM_PRICE_1, article.getPrice(), "Le prix du premier article
		// n'est pas bon.");
		assertEquals(PARAM_DESCRIPTION_1, article.getDescription(), "La description du premier article n'est pas bon.");

		callAddService("", PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1, PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);

		callAddService(null, PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1, PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);

		callAddService(PARAM_NAME_1, PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, "Not numeric", PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(2)
	void testGetArticleById() {
		Response response = target("/article/".concat(Integer.toString(article.getId()))).request()
				.accept(MediaType.APPLICATION_JSON).get();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		Article fetchedArticle = response.readEntity(Article.class);
		assertEquals(article.getName(), fetchedArticle.getName());

		response = target("/article/-1").request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	@Test
	@Order(3)
	void testUpdateArticle() {
		final String strId = Integer.toString(article.getId());
		callUpdateService(strId, "UpdatedName", PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1,
				PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.OK.getStatusCode(), httpStatus);
		assertNotNull(article);
		assertEquals("UpdatedName", article.getName());

		callUpdateService("-1", "UpdatedName", PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1,
				PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), httpStatus);

		callUpdateService("NotNumeric", "UpdatedName", PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1,
				PARAM_DESCRIPTION_1);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), httpStatus);
	}

	@Test
	@Order(4)
	void testDeleteArticle() {
		Response response = target("/article/delete/" + article.getId()).request().delete();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		response = target("/article/" + article.getId()).request().get();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	private void callUpdateService(final String id, final String name, final String ean, final String brand,
			final String picture, final String price, final String description) {
		final Form formulaire = new Form();
		if (id != null) {
			formulaire.param(ArticleServices.PARAM_ART_ID, id);
		}
		if (name != null) {
			formulaire.param(ArticleServices.PARAM_ART_NAME, name);
		}
		if (ean != null) {
			formulaire.param(ArticleServices.PARAM_ART_EAN13, ean);
		}
		if (brand != null) {
			formulaire.param(ArticleServices.PARAM_ART_BRAND, brand);
		}
		if (picture != null) {
			formulaire.param(ArticleServices.PARAM_ART_PICTURE, picture);
		}
		if (price != null) {
			formulaire.param(ArticleServices.PARAM_ART_PRICE, price);
		}
		if (description != null) {
			formulaire.param(ArticleServices.PARAM_ART_DESCRIPTION, description);
		}

		final Response response = target("/article/update").request().accept(MediaType.APPLICATION_JSON)
				.put(Entity.form(formulaire));
		httpStatus = response.getStatus();
		if (httpStatus == Response.Status.OK.getStatusCode()) {
			try {
				article = response.readEntity(Article.class);
			} catch (final Exception e) {
				fail("Impossible de mapper la réponse vers un Article", e);
			}
		}
	}
}