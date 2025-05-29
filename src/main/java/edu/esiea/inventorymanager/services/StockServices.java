package edu.esiea.inventorymanager.services;

import java.time.LocalDate;
import java.util.List;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.InOut;
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

@Path("/stock")
public class StockServices {

	public static final String PARAM_STOCK_ID = "StockId";
	public static final String PARAM_STOCK_DATE = "StockDate";
	public static final String PARAM_STOCK_ARTICLE_ID = "ArticleId";
	public static final String PARAM_STOCK_QUANTITY = "StockQuantity";
	public static final String PARAM_STOCK_TRANSFER_TYPE = "StockTransferType";
	public static final String PARAM_STOCK_COMMENT = "StockComment";

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStock(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_STOCK_DATE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_ARTICLE_ID))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_QUANTITY))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_TRANSFER_TYPE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_COMMENT))) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_STOCK_DATE));
			int articleId = Integer.parseInt(formParams.getFirst(PARAM_STOCK_ARTICLE_ID));
			int quantity = Integer.parseInt(formParams.getFirst(PARAM_STOCK_QUANTITY));
			InOut transferType = InOut.valueOf(formParams.getFirst(PARAM_STOCK_TRANSFER_TYPE));
			String comment = formParams.getFirst(PARAM_STOCK_COMMENT);

			Stock stock = new Stock(date, DaoFactory.getInstance().getArticlesDao().getArticleById(articleId), quantity,
					transferType, comment);
			stock = DaoFactory.getInstance().getStocksDao().createStock(stock);

			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(stock) {
			}).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStock(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_STOCK_ID)) || isNullOrEmpty(formParams.getFirst(PARAM_STOCK_DATE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_ARTICLE_ID))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_QUANTITY))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_TRANSFER_TYPE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_COMMENT))) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int id = Integer.parseInt(formParams.getFirst(PARAM_STOCK_ID));
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_STOCK_DATE));
			int articleId = Integer.parseInt(formParams.getFirst(PARAM_STOCK_ARTICLE_ID));
			int quantity = Integer.parseInt(formParams.getFirst(PARAM_STOCK_QUANTITY));
			InOut transferType = InOut.valueOf(formParams.getFirst(PARAM_STOCK_TRANSFER_TYPE));
			String comment = formParams.getFirst(PARAM_STOCK_COMMENT);

			IStocksDao dao = DaoFactory.getInstance().getStocksDao();
			Stock stock = dao.getStockById(id);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + id + "] n'a été trouvé.").build();
			}

			stock.setDate(date);
			stock.setArticle(DaoFactory.getInstance().getArticlesDao().getArticleById(articleId));
			stock.setQuantity(quantity);
			stock.setTransferType(transferType);
			stock.setComment(comment);
			dao.updateStock(stock);

			return Response.ok().entity(new GenericEntity<>(stock) {
			}).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllStocks() {
		try {
			final List<Stock> list = DaoFactory.getInstance().getStocksDao().getAllStocks();
			return Response.ok().entity(new GenericEntity<>(list) {
			}).build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStockById(@PathParam("id") final int id) {
		try {
			Stock stock = DaoFactory.getInstance().getStocksDao().getStockById(id);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + id + "] n'a été trouvé.").build();
			}
			return Response.ok().entity(new GenericEntity<>(stock) {
			}).build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStock(@PathParam("id") final int id) {
		try {
			IStocksDao dao = DaoFactory.getInstance().getStocksDao();
			Stock stock = dao.getStockById(id);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + id + "] n'a été trouvé.").build();
			}
			dao.deleteStock(stock);
			return Response.ok().entity("Stock supprimé avec succès.").build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}