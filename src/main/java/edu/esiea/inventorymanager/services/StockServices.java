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

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStock(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_STOCK_DATE) == null || formParams.get(PARAM_STOCK_ARTICLE_ID) == null
				|| formParams.get(PARAM_STOCK_QUANTITY) == null || formParams.get(PARAM_STOCK_TRANSFER_TYPE) == null
				|| formParams.get(PARAM_STOCK_COMMENT) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String dateStr = formParams.get(PARAM_STOCK_DATE).getFirst();
		final String articleIdStr = formParams.get(PARAM_STOCK_ARTICLE_ID).getFirst();
		final String quantityStr = formParams.get(PARAM_STOCK_QUANTITY).getFirst();
		final String transferTypeStr = formParams.get(PARAM_STOCK_TRANSFER_TYPE).getFirst();
		final String comment = formParams.get(PARAM_STOCK_COMMENT).getFirst();

		if (dateStr == null || dateStr.isBlank() || articleIdStr == null || articleIdStr.isBlank()
				|| quantityStr == null || quantityStr.isBlank() || transferTypeStr == null || transferTypeStr.isBlank()
				|| comment == null || comment.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires est vide ou incorrect.").build();
		}

		LocalDate date;
		int articleId;
		int quantity;
		InOut transferType;

		try {
			date = LocalDate.parse(dateStr);
			articleId = Integer.parseInt(articleIdStr);
			quantity = Integer.parseInt(quantityStr);
			transferType = InOut.valueOf(transferTypeStr);
		} catch (final Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		}

		Stock stock;
		try {
			stock = new Stock(date, DaoFactory.getInstance().getArticlesDao().getArticleById(articleId), quantity,
					transferType, comment);
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		try {
			stock = DaoFactory.getInstance().getStocksDao().createStock(stock);
			final GenericEntity<Stock> json = new GenericEntity<>(stock) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStock(@PathParam("id") final int idStock) {
		final IStocksDao dao = DaoFactory.getInstance().getStocksDao();
		try {
			final Stock stock = dao.getStockById(idStock);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + idStock + "] n'a été trouvé.").build();
			}
			dao.deleteStock(stock);
			return Response.ok().entity("Supprimé").build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllStocks() {
		try {
			final List<Stock> ret = DaoFactory.getInstance().getStocksDao().getAllStocks();
			final GenericEntity<List<Stock>> json = new GenericEntity<>(ret) {
			};
			return Response.ok().entity(json).build();
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStockResponse(@PathParam("id") final int idStock) {
		try {
			Stock stock = DaoFactory.getInstance().getStocksDao().getStockById(idStock);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + idStock + "] n'a été trouvé.").build();
			}
			final GenericEntity<Stock> json = new GenericEntity<>(stock) {
			};
			return Response.ok().entity(json).build();
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStock(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_STOCK_ID) == null || formParams.get(PARAM_STOCK_DATE) == null
				|| formParams.get(PARAM_STOCK_ARTICLE_ID) == null || formParams.get(PARAM_STOCK_QUANTITY) == null
				|| formParams.get(PARAM_STOCK_TRANSFER_TYPE) == null || formParams.get(PARAM_STOCK_COMMENT) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String idStr = formParams.get(PARAM_STOCK_ID).getFirst();
		final String dateStr = formParams.get(PARAM_STOCK_DATE).getFirst();
		final String articleIdStr = formParams.get(PARAM_STOCK_ARTICLE_ID).getFirst();
		final String quantityStr = formParams.get(PARAM_STOCK_QUANTITY).getFirst();
		final String transferTypeStr = formParams.get(PARAM_STOCK_TRANSFER_TYPE).getFirst();
		final String comment = formParams.get(PARAM_STOCK_COMMENT).getFirst();

		int id, articleId, quantity;
		LocalDate date;
		InOut transferType;

		try {
			id = Integer.parseInt(idStr);
			date = LocalDate.parse(dateStr);
			articleId = Integer.parseInt(articleIdStr);
			quantity = Integer.parseInt(quantityStr);
			transferType = InOut.valueOf(transferTypeStr);
		} catch (final Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		}
		Stock stock = null;
		try {
			stock = DaoFactory.getInstance().getStocksDao().getStockById(id);
			if (stock == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun barème avec l'identifiant".concat(idStr)).build();
			}
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		try {
			stock.setDate(date);
			stock.setArticle(DaoFactory.getInstance().getArticlesDao().getArticleById(articleId));
			stock.setQuantity(quantity);
			stock.setTransferType(transferType);
			stock.setComment(comment);
			DaoFactory.getInstance().getStocksDao().updateStock(stock);

			final GenericEntity<Stock> json = new GenericEntity<>(stock) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}