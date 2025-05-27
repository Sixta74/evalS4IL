package edu.esiea.inventorymanager.services;

import java.time.LocalDate;
import java.util.List;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.ICommandsDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Command;
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

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCommand(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_COM_DATE) == null || formParams.get(PARAM_COM_COMMENT) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String dateStr = formParams.get(PARAM_COM_DATE).getFirst();
		final String comment = formParams.get(PARAM_COM_COMMENT).getFirst();

		if (dateStr.isBlank() || dateStr == null || comment.isBlank() || comment == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires est vide ou incorrect.").build();
		}

		LocalDate date;
		try {
			date = LocalDate.parse(dateStr);
		} catch (final Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		}

		Command command = new Command(date, null, comment);
		try {
			command = DaoFactory.getInstance().getCommandsDao().createCommand(command);
			final GenericEntity<Command> json = new GenericEntity<>(command) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCommand(@PathParam("id") final int idCommand) {
		try {
			final ICommandsDao dao = DaoFactory.getInstance().getCommandsDao();
			final Command command = dao.getCommandById(idCommand);
			if (command == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + idCommand + "] n'a été trouvée.").build();
			}
			dao.deleteCommand(command);
			return Response.ok().entity("Supprimé").build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCommands() {
		try {
			final List<Command> ret = DaoFactory.getInstance().getCommandsDao().getAllCommands();
			final GenericEntity<List<Command>> json = new GenericEntity<>(ret) {
			};
			return Response.ok().entity(json).build();
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommandResponse(@PathParam("id") final int idCommand) {
		try {
			Command command = DaoFactory.getInstance().getCommandsDao().getCommandById(idCommand);
			if (command == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'id [" + idCommand + "] n'a été trouvée.").build();
			}
			final GenericEntity<Command> json = new GenericEntity<>(command) {
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
	public Response updateCommand(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_COM_ID) == null || formParams.get(PARAM_COM_DATE) == null
				|| formParams.get(PARAM_COM_COMMENT) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String idStr = formParams.get(PARAM_COM_ID).getFirst();
		final String dateStr = formParams.get(PARAM_COM_DATE).getFirst();
		final String comment = formParams.get(PARAM_COM_COMMENT).getFirst();

		int id;
		LocalDate date;

		try {
			id = Integer.parseInt(idStr);
			date = LocalDate.parse(dateStr);
		} catch (final Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Erreur dans le format des paramètres fournis.")
					.build();
		}

		Command command = null;
		try {
			command = DaoFactory.getInstance().getCommandsDao().getCommandById(id);
			if (command == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune commande avec l'identifiant " + idStr + " n'a été trouvée.").build();
			}
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		try {
			command.setDate(date);
			command.setComment(comment);
			DaoFactory.getInstance().getCommandsDao().updateCommand(command);

			final GenericEntity<Command> json = new GenericEntity<>(command) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}