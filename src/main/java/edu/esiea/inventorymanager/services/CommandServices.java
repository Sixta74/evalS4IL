package edu.esiea.inventorymanager.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			LocalDate date = LocalDate.parse(formParams.getFirst(PARAM_COM_DATE));
			String comment = formParams.getFirst(PARAM_COM_COMMENT);

			Command command = new Command(date, new ArrayList<>(), comment);
			command = DaoFactory.getInstance().getCommandsDao().createCommand(command);

			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(command) {
			}).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCommand(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_COM_ID)) || isNullOrEmpty(formParams.getFirst(PARAM_COM_DATE))
				|| isNullOrEmpty(formParams.getFirst(PARAM_COM_COMMENT))) {
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

			return Response.ok().entity(new GenericEntity<>(command) {
			}).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCommands() {
		try {
			final List<Command> list = DaoFactory.getInstance().getCommandsDao().getAllCommands();
			return Response.ok().entity(new GenericEntity<>(list) {
			}).build();
		} catch (DaoException e) {
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
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + id + "] n'a été trouvée.").build();
			}
			List<Stock> stocks = DaoFactory.getInstance().getStocksDao().getStocksByCommandId(id);
			command.setStocks(stocks);

			return Response.ok().entity(new GenericEntity<>(command) {
			}).build();
		} catch (DaoException e) {
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
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + id + "] n'a été trouvée.").build();
			}
			dao.deleteCommand(command);
			return Response.ok().entity("Commande supprimée avec succès.").build();
		} catch (DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}