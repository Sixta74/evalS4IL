package edu.esiea.inventorymanager.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.IStocksDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
import edu.esiea.inventorymanager.model.Command;
import edu.esiea.inventorymanager.model.InOut;
import edu.esiea.inventorymanager.model.Stock;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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

	private static final Logger logger = Logger.getLogger(StockServices.class);

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
				|| isNullOrEmpty(formParams.getFirst(PARAM_STOCK_COMMENT))
				|| isNullOrEmpty(formParams.getFirst("CommandId"))) {

			logger.warn("Paramètres manquants lors de l'ajout d'un stock.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int commandId = Integer.parseInt(formParams.getFirst("CommandId"));
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_STOCK_DATE));
			int articleId = Integer.parseInt(formParams.getFirst(PARAM_STOCK_ARTICLE_ID));
			int quantity = Integer.parseInt(formParams.getFirst(PARAM_STOCK_QUANTITY));
			InOut transferType = InOut.valueOf(formParams.getFirst(PARAM_STOCK_TRANSFER_TYPE));
			String comment = formParams.getFirst(PARAM_STOCK_COMMENT);

			Article article = DaoFactory.getInstance().getArticlesDao().getArticleById(articleId);
			if (article == null) {
				logger.warn("Article invalide lors de l'ajout d'un stock.");
				return Response.status(Response.Status.BAD_REQUEST).entity("Article invalide.").build();
			}

			Command command = DaoFactory.getInstance().getCommandsDao().getCommandById(commandId);
			if (command == null) {
				logger.warn("Commande invalide lors de l'ajout d'un stock.");
				return Response.status(Response.Status.BAD_REQUEST).entity("Commande invalide.").build();
			}

			Stock stock = new Stock(date, article, quantity, transferType, comment);
			stock = DaoFactory.getInstance().getStocksDao().createStock(stock);

			command.getStocks().add(stock);
			DaoFactory.getInstance().getCommandsDao().updateCommand(command);

			logger.info("Stock ajouté avec succès : " + stock.getId());
			logger.info("Commande mise à jour avec le stock : " + command.getId());

			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(stock) {
			}).build();
		} catch (NumberFormatException e) {
			logger.warn("Format invalide pour les paramètres lors de l'ajout d'un stock.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la création d'un stock : " + e.getMessage());
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
				logger.warn("Aucun stock trouvé avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + id + "] n'a été trouvé.").build();
			}
			logger.info("Stock récupéré avec succès : " + stock.getId());
			return Response.ok().entity(new GenericEntity<>(stock) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération d'un stock : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllStocks() {
		try {
			List<Stock> stocks = DaoFactory.getInstance().getStocksDao().getAllStocks();

			if (stocks.isEmpty()) {
				logger.warn("Aucun stock disponible en base.");
				return Response.status(Response.Status.NO_CONTENT).entity("Aucun stock trouvé.").build();
			}

			logger.info("Liste des stocks récupérée avec succès : " + stocks.size() + " éléments.");
			return Response.ok().entity(new GenericEntity<>(stocks) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération des stocks : " + e.getMessage());
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
				logger.warn("Aucun stock trouvé à supprimer avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun stock avec l'id [" + id + "] n'a été trouvé.").build();
			}

			dao.deleteStock(stock);
			logger.info("Stock supprimé avec succès : " + stock.getId());
			return Response.ok().entity("Stock supprimé avec succès.").build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la suppression d'un stock : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}