package edu.esiea.inventorymanager.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;
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

@Path("/command")
public class CommandServices {

	public static final String PARAM_COM_ID = "CommandId";
	public static final String PARAM_COM_DATE = "CommandDate";
	public static final String PARAM_COM_COMMENT = "CommandComment";

	private static final Logger logger = Logger.getLogger(CommandServices.class);

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCommand(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_COM_DATE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_COM_COMMENT))) {

			logger.warn("Paramètres manquants lors de l'ajout d'une commande.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_COM_DATE));
			String comment = formParams.getFirst(PARAM_COM_COMMENT);

			Command command = new Command(date, new ArrayList<>(), comment);
			command = DaoFactory.getInstance().getCommandsDao().createCommand(command);

			logger.info("Commande ajoutée avec succès : " + command.getId());
			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(command) {
			}).build();
		} catch (Exception e) {
			logger.error("Erreur interne lors de la création d'une commande : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCommand(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_COM_ID)) || isNullOrEmpty(formParams.getFirst(PARAM_COM_DATE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_COM_COMMENT))) {

			logger.warn("Paramètres manquants pour la mise à jour d'une commande.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int id = Integer.parseInt(formParams.getFirst(PARAM_COM_ID));
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_COM_DATE));
			String comment = formParams.getFirst(PARAM_COM_COMMENT);

			ICommandsDao dao = DaoFactory.getInstance().getCommandsDao();
			Command command = dao.getCommandById(id);

			if (command == null) {
				logger.warn("Aucune commande trouvée avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + id + "] n'a été trouvée.").build();
			}

			command.setDate(date);
			command.setComment(comment);

			if (!isNullOrEmpty(formParams.getFirst("StockIds"))) {
				String[] stockIds = formParams.getFirst("StockIds").split(",");
				List<Stock> updatedStocks = new ArrayList<>();
				for (String stockIdStr : stockIds) {
					int stockId = Integer.parseInt(stockIdStr);
					Stock stock = DaoFactory.getInstance().getStocksDao().getStockById(stockId);
					if (stock != null) {
						updatedStocks.add(stock);
					}
				}
				command.setStocks(updatedStocks);
			}

			dao.updateCommand(command);
			logger.info("Commande mise à jour avec succès : " + command.getId());

			return Response.ok().entity(new GenericEntity<>(command) {
			}).build();
		} catch (NumberFormatException e) {
			logger.warn("Format invalide pour les paramètres lors de la mise à jour.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		} catch (Exception e) {
			logger.error("Erreur interne lors de la mise à jour d'une commande : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCommands() {
		try {
			List<Command> list = DaoFactory.getInstance().getCommandsDao().getAllCommands();
			logger.info("Liste des commandes récupérée avec succès.");
			return Response.ok().entity(new GenericEntity<>(list) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération des commandes : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommandById(@PathParam("id") final int id) {
		try {
			Command command = DaoFactory.getInstance().getCommandsDao().getCommandById(id);
			if (command == null) {
				logger.warn("Aucune commande trouvée avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + id + "] n'a été trouvée.").build();
			}
			logger.info("Commande récupérée avec succès : " + command.getId());
			return Response.ok().entity(new GenericEntity<>(command) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération d'une commande : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCommand(@PathParam("id") final int id) {
		try {
			ICommandsDao dao = DaoFactory.getInstance().getCommandsDao();
			Command command = dao.getCommandById(id);
			if (command == null) {
				logger.warn("Aucune commande trouvée à supprimer avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + id + "] n'a été trouvée.").build();
			}

			dao.deleteCommand(command);
			logger.info("Commande supprimée avec succès : " + command.getId());
			return Response.ok().entity("Commande supprimée avec succès.").build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la suppression d'une commande : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}