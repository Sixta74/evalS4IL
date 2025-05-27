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
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Command;
import edu.esiea.inventorymanager.model.InOut;
import edu.esiea.inventorymanager.model.Stock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StockDaoBddTest {

	private static Stock stock;
	private static Article article;
	private static Command command;

	private static final LocalDate PARAM_DATE = LocalDate.of(2025, 5, 27);
	private static final int PARAM_QUANTITY = 100;
	private static final String PARAM_COMMENT = "Stock initial";
	private static final InOut PARAM_TRANSFER_TYPE = InOut.IN;

	private static IStocksDao dao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		article = new Article("Smartphone", "1234567890123", "Samsung", "image.jpg", 999.99f, "Un smartphone puissant");
		command = new Command(PARAM_DATE, null, "Commande test");

		DaoFactory.getInstance().getArticlesDao().createArticle(article);
		DaoFactory.getInstance().getCommandsDao().createCommand(command);

		stock = new Stock(PARAM_DATE, article, PARAM_QUANTITY, PARAM_TRANSFER_TYPE, PARAM_COMMENT);

		DaoBddHelper.forceTestInstance();
		dao = DaoFactory.getInstance().getStocksDao();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		stock = null;
		article = null;
		command = null;

		EntityManager m = DaoBddHelper.getInstance().getEntityManager();
		if (m.isOpen()) {
			m.clear();
		}
	}

	@Test
	@Order(1)
	void testCreateStock() {
		try {
			stock = dao.createStock(stock);
		} catch (final DaoException e) {
			fail("Impossible de créer le stock");
		}

		assertNotNull(stock, "Le stock n'a pas été créé !");
		assertTrue(stock.getId() > 0, "L'ID du stock est invalide !");
		assertEquals(PARAM_DATE, stock.getDate(), "Date incorrecte.");
		assertEquals(PARAM_QUANTITY, stock.getQuantity(), "Quantité incorrecte.");
		assertEquals(PARAM_TRANSFER_TYPE, stock.getTransferType(), "Type de transfert incorrect.");
		assertEquals(PARAM_COMMENT, stock.getComment(), "Commentaire incorrect.");
		assertEquals(article.getId(), stock.getArticle().getId(), "L'article du stock ne correspond pas.");
		assertEquals(command.getId(), stock.getCommand().getId(), "La commande du stock ne correspond pas.");
	}

	@Test
	@Order(2)
	void testGetAllStocks() throws DaoException {
		List<Stock> stocks = dao.getAllStocks();
		assertNotNull(stocks, "La liste des stocks est NULL !");
		assertTrue(stocks.size() > 0, "Aucun stock trouvé !");
		assertTrue(stocks.contains(stock), "Le stock créé avant n'est pas retourné !");
	}

	@Test
	@Order(3)
	void testGetStockById() throws DaoException {
		Stock fetchedStock = dao.getStockById(stock.getId());
		assertNotNull(fetchedStock, "Le stock récupéré est NULL !");
		assertEquals(PARAM_DATE, fetchedStock.getDate(), "La date du stock est incorrecte !");
		assertEquals(PARAM_QUANTITY, fetchedStock.getQuantity(), "La quantité du stock est incorrecte !");
		assertEquals(PARAM_TRANSFER_TYPE, fetchedStock.getTransferType(), "Le type de transfert est incorrect !");
		assertEquals(PARAM_COMMENT, fetchedStock.getComment(), "Le commentaire est incorrect !");
	}

	@Test
	@Order(4)
	void testUpdateStock() throws DaoException {
		stock.setQuantity(150);
		dao.updateStock(stock);

		Stock updatedStock = dao.getStockById(stock.getId());

		assertNotNull(updatedStock, "Le stock mis à jour est NULL !");
		assertEquals(150, updatedStock.getQuantity(), "La quantité n'a pas été mise à jour !");
	}

	@Test
	@Order(5)
	void testDeleteStock() throws DaoException {
		dao.deleteStock(stock);
		Stock deletedStock = dao.getStockById(stock.getId());
		assertNull(deletedStock, "Le stock n'a pas été supprimé !");
	}
}