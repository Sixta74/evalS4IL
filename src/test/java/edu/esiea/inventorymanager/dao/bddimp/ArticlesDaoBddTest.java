package edu.esiea.inventorymanager.dao.bddimp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Category;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticlesDaoBddTest {

	private static Article article1;

	private static final String PARAM_NAME_1 = "Smartphone";
	private static final String PARAM_EAN13_1 = "1234567890123";
	private static final String PARAM_BRAND_1 = "Samsung";
	private static final String PARAM_PICTURE_1 = "image.jpg";
	private static final float PARAM_PRICE_1 = 999.99f;
	private static final String PARAM_DESCRIPTION_1 = "Un smartphone puissant";
	private static Category category;

	private static IArticlesDao dao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		category = new Category("Électronique", "Articles tech et accessoires");
		article1 = new Article(PARAM_NAME_1, PARAM_EAN13_1, PARAM_BRAND_1, PARAM_PICTURE_1, PARAM_PRICE_1,
				PARAM_DESCRIPTION_1);
		DaoFactory.getInstance().getCategoriesDao().createCategory(category);
		article1.setCategory(category);

		DaoBddHelper.forceTestInstance();
		dao = DaoFactory.getInstance().getArticlesDao();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		article1 = null;
		category = null;
		final EntityManager m = DaoBddHelper.getInstance().getEntityManager();
		if (m.isOpen()) {
			m.clear();
		}
	}

	@Test
	@Order(1)
	void testCreateArticle() {
		try {
			article1 = dao.createArticle(article1);
		} catch (final DaoException e) {
			fail("Impossible de créer l'article");
		}
		assertNotNull(article1, "L'article n'a pas été créé !");
		assertTrue(article1.getId() > 0, "L'ID de l'article est invalide !");

		assertEquals(PARAM_NAME_1, article1.getName(), "Nom de l'article incorrect.");
		assertEquals(PARAM_EAN13_1, article1.getEAN13(), "Code EAN13 incorrect.");
		assertEquals(PARAM_BRAND_1, article1.getBrand(), "Marque incorrecte.");
		assertEquals(PARAM_PICTURE_1, article1.getPicture_URL(), "URL de l'image incorrecte.");
		assertEquals(PARAM_PRICE_1, article1.getPrice(), "Prix incorrect.");
		assertEquals(PARAM_DESCRIPTION_1, article1.getDescription(), "Description incorrecte.");
		assertEquals(category.getId(), article1.getCategory().getId(), "La catégorie de l'article ne correspond pas.");

		category = article1.getCategory();
	}

	@Test
	@Order(2)
	void testGetAllArticles() throws DaoException {
		List<Article> articles = dao.getAllArticles();
		assertNotNull(articles, "La liste des articles est NULL !");
		assertTrue(articles.size() > 0, "Aucun article trouvé !");
		assertTrue(articles.contains(article1), "L'article créé avant n'est pas retourné !");
	}

	@Test
	@Order(3)
	void testGetArticleById() throws DaoException {
		Article fetchedArticle = dao.getArticleById(article1.getId());

		assertNotNull(fetchedArticle, "L'article récupéré est NULL !");
		assertEquals(PARAM_NAME_1, fetchedArticle.getName(), "Le nom de l'article est incorrect !");
		assertEquals(PARAM_EAN13_1, fetchedArticle.getEAN13(), "Code EAN13 incorrect !");
		assertEquals(PARAM_BRAND_1, fetchedArticle.getBrand(), "Marque incorrecte !");
		assertEquals(PARAM_PICTURE_1, fetchedArticle.getPicture_URL(), "URL de l'image incorrecte !");
		assertEquals(PARAM_PRICE_1, fetchedArticle.getPrice(), "Prix incorrect !");
		assertEquals(PARAM_DESCRIPTION_1, fetchedArticle.getDescription(), "Description incorrecte !");
		assertEquals(category.getId(), fetchedArticle.getCategory().getId(), "La catégorie ne correspond pas !");
	}

	@Test
	@Order(4)
	void testUpdateArticle() throws DaoException {
		article1.setPrice(799.99f);
		dao.updateArticle(article1);

		Article updatedArticle = dao.getArticleById(article1.getId());

		assertNotNull(updatedArticle, "L'article mis à jour est NULL !");
		assertEquals(799.99f, updatedArticle.getPrice(), "Le prix n'a pas été mis à jour !");
	}

	@Test
	@Order(5)
	void testDeleteArticle() throws DaoException {
		dao.deleteArticle(article1);

		Article deletedArticle = dao.getArticleById(article1.getId());
		assertNull(deletedArticle, "L'article n'a pas été supprimé !");
	}
}