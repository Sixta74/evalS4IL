package edu.esiea.inventorymanager.services;

import java.util.ArrayList;
import java.util.List;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Category;
import edu.esiea.inventorymanager.model.Stock;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

@Path("/article")
public class ArticleServices {

	public static final String PARAM_ART_ID = "ArticleId";
	public static final String PARAM_ART_NAME = "ArticleName";
	public static final String PARAM_ART_EAN13 = "ArticleEAN13";
	public static final String PARAM_ART_BRAND = "ArticleBrand";
	public static final String PARAM_ART_PICTURE = "ArticlePicture";
	public static final String PARAM_ART_PRICE = "ArticlePrice";
	public static final String PARAM_ART_DESCRIPTION = "ArticleDescription";
	public static final String PARAM_ART_CATEGORY_ID = "CategoryId";
	public static final String PARAM_ART_STOCK_IDS = "StockIds";

	@POST
	@Path("/add")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addArticle(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_ART_NAME)) || isNullOrEmpty(formParams.getFirst(PARAM_ART_EAN13))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_BRAND))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_PICTURE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_PRICE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_DESCRIPTION))) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			String name = formParams.getFirst(PARAM_ART_NAME);
			String ean13 = formParams.getFirst(PARAM_ART_EAN13);
			String brand = formParams.getFirst(PARAM_ART_BRAND);
			String picture = formParams.getFirst(PARAM_ART_PICTURE);
			float price = Float.parseFloat(formParams.getFirst(PARAM_ART_PRICE));
			String description = formParams.getFirst(PARAM_ART_DESCRIPTION);

			ICategoriesDao categoryDao = DaoFactory.getInstance().getCategoriesDao();
			Category category = null;

			String categoryIdStr = formParams.getFirst(PARAM_ART_CATEGORY_ID);
			if (categoryIdStr != null && !categoryIdStr.isBlank()) {
				int categoryId = Integer.parseInt(categoryIdStr);
				category = categoryDao.getCategoryById(categoryId);
			}

			Article article = new Article(name, ean13, brand, picture, price, description);

			if (category != null) {
				article.setCategory(category);
			}

			article = DaoFactory.getInstance().getArticlesDao().createArticle(article);

			final GenericEntity<Article> json = new GenericEntity<>(article) {
			};
			return Response.status(Response.Status.CREATED).entity(json).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("ID ou prix invalide.").build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateArticle(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_ART_ID)) || isNullOrEmpty(formParams.getFirst(PARAM_ART_NAME))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_EAN13))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_BRAND))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_PICTURE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_PRICE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_ART_DESCRIPTION))) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int id = Integer.parseInt(formParams.getFirst(PARAM_ART_ID));
			String name = formParams.getFirst(PARAM_ART_NAME);
			String ean13 = formParams.getFirst(PARAM_ART_EAN13);
			String brand = formParams.getFirst(PARAM_ART_BRAND);
			String picture = formParams.getFirst(PARAM_ART_PICTURE);
			float price = Float.parseFloat(formParams.getFirst(PARAM_ART_PRICE));
			String description = formParams.getFirst(PARAM_ART_DESCRIPTION);

			ICategoriesDao categoryDao = DaoFactory.getInstance().getCategoriesDao();
			IStocksDao stocksDao = DaoFactory.getInstance().getStocksDao();
			IArticlesDao articlesDao = DaoFactory.getInstance().getArticlesDao();

			Article article = articlesDao.getArticleById(id);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article trouvé avec l'identifiant : " + id).build();
			}

			Category category = null;
			List<Stock> updatedStocks = null;

			String categoryIdStr = formParams.getFirst(PARAM_ART_CATEGORY_ID);
			if (categoryIdStr != null && !categoryIdStr.isBlank()) {
				int categoryId = Integer.parseInt(categoryIdStr);
				category = categoryDao.getCategoryById(categoryId);
			}

			if (formParams.get(PARAM_ART_STOCK_IDS) != null) {
				updatedStocks = new ArrayList<>();

				for (String stockIdStr : formParams.get(PARAM_ART_STOCK_IDS)) {
					int stockId = Integer.parseInt(stockIdStr);
					Stock stock = stocksDao.getStockById(stockId);
					if (stock != null) {
						updatedStocks.add(stock);
					}
				}
				article.setStock(updatedStocks);
			}

			article.setName(name);
			article.setEAN13(ean13);
			article.setBrand(brand);
			article.setPicture_URL(picture);
			article.setPrice(price);
			article.setDescription(description);

			if (category != null) {
				article.setCategory(category);
			}

			articlesDao.updateArticle(article);

			final GenericEntity<Article> json = new GenericEntity<>(article) {
			};
			return Response.ok().entity(json).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("ID ou prix invalide.").build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArticles() {
		try {
			final List<Article> list = DaoFactory.getInstance().getArticlesDao().getAllArticles();
			final GenericEntity<List<Article>> json = new GenericEntity<>(list) {
			};
			return Response.ok().entity(json).build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticleById(@PathParam("id") final int id) {
		Article article = null;
		try {
			article = DaoFactory.getInstance().getArticlesDao().getArticleById(id);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article avec l'id [".concat(Integer.toString(id)).concat("] n'a été trouvé."))
						.build();
			}
			final GenericEntity<Article> json = new GenericEntity<>(article) {
			};
			return Response.ok().entity(json).build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArticle(@PathParam("id") final int id) {
		try {
			IArticlesDao dao = DaoFactory.getInstance().getArticlesDao();
			Article article = dao.getArticleById(id);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article trouvé avec l'identifiant : " + id).build();
			}
			dao.deleteArticle(article);
			return Response.ok().entity("Article supprimé avec succès.").build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}