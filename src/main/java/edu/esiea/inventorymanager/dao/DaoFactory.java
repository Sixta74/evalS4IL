package edu.esiea.inventorymanager.dao;

import edu.esiea.inventorymanager.dao.bddimp.ArticlesDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.CategoriesDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.CommandsDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.StocksDaoBdd;
import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;

public class DaoFactory {
	private static DaoFactory instance;
	private IArticlesDao articlesDao;
	private ICategoriesDao categoriesDao;
	private ICommandsDao commandsDao;
	private IStocksDao stocksDao;

	public static DaoFactory getInstance() {
		if (instance == null) {
			instance = new DaoFactory();
		}
		return instance;
	}

	private DaoFactory() {
	}

	public IArticlesDao getArticlesDao() {
		if (articlesDao == null) {
			articlesDao = new ArticlesDaoBdd();
		}
		return articlesDao;

	}

	public ICategoriesDao getCategoriesDao() {
		if (categoriesDao == null) {
			categoriesDao = new CategoriesDaoBdd();
		}
		return categoriesDao;

	}

	public ICommandsDao getCommandsDao() {
		if (commandsDao == null) {
			commandsDao = new CommandsDaoBdd();
		}
		return commandsDao;

	}

	public IStocksDao getStocksDao() {
		if (stocksDao == null) {
			stocksDao = new StocksDaoBdd();
		}
		return stocksDao;

	}
}