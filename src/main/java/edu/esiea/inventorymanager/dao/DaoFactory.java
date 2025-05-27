package edu.esiea.inventorymanager.dao;

import edu.esiea.inventorymanager.dao.bddimp.ArticlesDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.CategoriesDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.CommandsDaoBdd;
import edu.esiea.inventorymanager.dao.bddimp.StocksDaoBdd;
import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;

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

	public IArticlesDao getArticlesDao() throws DaoException {
		if (this.articlesDao == null) {
			this.articlesDao = new ArticlesDaoBdd();
		}
		return this.articlesDao;

	}

	public ICategoriesDao getCategoriesDao() throws DaoException {
		if (this.categoriesDao == null) {
			this.categoriesDao = new CategoriesDaoBdd();
		}
		return this.categoriesDao;

	}

	public ICommandsDao getCommandsDao() throws DaoException {
		if (this.commandsDao == null) {
			this.commandsDao = new CommandsDaoBdd();
		}
		return this.commandsDao;

	}

	public IStocksDao getStocksDao() throws DaoException {
		if (this.stocksDao == null) {
			this.stocksDao = new StocksDaoBdd();
		}
		return this.stocksDao;

	}
}