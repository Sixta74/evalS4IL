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
import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Category;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesDaoBddTest {

	private static Category category;

	private static final String PARAM_NAME = "Électronique";
	private static final String PARAM_DESCRIPTION = "Articles tech et accessoires";

	private static ICategoriesDao dao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		category = new Category(PARAM_NAME, PARAM_DESCRIPTION);
		DaoBddHelper.forceTestInstance();
		dao = DaoFactory.getInstance().getCategoriesDao();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		category = null;
		EntityManager m = DaoBddHelper.getInstance().getEntityManager();
		if (m.isOpen()) {
			m.clear();
		}
	}

	@Test
	@Order(1)
	void testCreateCategory() {
		try {
			category = dao.createCategory(category);
		} catch (final DaoException e) {
			e.printStackTrace();
			fail("Impossible de créer la catégorie");
		}
	}

	@Test
	@Order(3)
	void testGetAllCategories() throws DaoException {

		List<Category> categories = dao.getAllCategories();

		assertNotNull(categories, "La liste des catégories est NULL !");
		assertTrue(categories.size() > 0, "Aucune catégorie trouvée !");
		assertTrue(categories.contains(category), "La catégorie créée avant n'est pas retournée !");
	}

	@Test
	@Order(2)
	void testGetCategoryById() throws DaoException {
		Category fetchedCategory = dao.getCategoryById(category.getId());
		assertNotNull(fetchedCategory, "La catégorie récupérée est NULL !");
		assertEquals(PARAM_NAME, fetchedCategory.getName(), "Le nom de la catégorie est incorrect !");
		assertEquals(PARAM_DESCRIPTION, fetchedCategory.getDescription(), "La description est incorrecte !");
	}

	@Test
	@Order(4)
	void testUpdateCategory() throws DaoException {
		category.setName("Informatique");
		dao.updateCategory(category);

		Category updatedCategory = dao.getCategoryById(category.getId());
		assertNotNull(updatedCategory, "La catégorie mise à jour est NULL !");
		assertEquals("Informatique", updatedCategory.getName(), "Le nom n'a pas été mis à jour !");
	}

	@Test
	@Order(5)
	void testDeleteCategory() throws DaoException {
		dao.deleteCategory(category);
		Category deletedCategory = dao.getCategoryById(category.getId());
		assertNull(deletedCategory, "La catégorie n'a pas été supprimée !");
	}
}