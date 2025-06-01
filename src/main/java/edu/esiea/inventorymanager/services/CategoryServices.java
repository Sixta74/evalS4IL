package edu.esiea.inventorymanager.services;

import java.util.List;

import org.apache.log4j.Logger;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.ICategoriesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Category;
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

@Path("/category")
public class CategoryServices {

	public static final String PARAM_CAT_ID = "CategoryId";
	public static final String PARAM_CAT_NAME = "CategoryName";
	public static final String PARAM_CAT_DESCRIPTION = "CategoryDescription";

	private static final Logger logger = Logger.getLogger(CategoryServices.class);

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCategory(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_CAT_NAME))
				|| isNullOrEmpty(formParams.getFirst(PARAM_CAT_DESCRIPTION))) {

			logger.warn("Paramètres manquants lors de l'ajout d'une catégorie.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			Category category = new Category(formParams.getFirst(PARAM_CAT_NAME),
					formParams.getFirst(PARAM_CAT_DESCRIPTION));

			category = DaoFactory.getInstance().getCategoriesDao().createCategory(category);
			logger.info("Catégorie ajoutée avec succès : " + category.getName());
			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(category) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la création d'une catégorie : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Consumes("application/x-www-form-urlencoded")
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCategory(final MultivaluedMap<String, String> formParams) {
		if (isNullOrEmpty(formParams.getFirst(PARAM_CAT_ID)) || isNullOrEmpty(formParams.getFirst(PARAM_CAT_NAME))
				|| isNullOrEmpty(formParams.getFirst(PARAM_CAT_DESCRIPTION))) {

			logger.warn("Paramètres manquants pour la mise à jour d'une catégorie.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int id = Integer.parseInt(formParams.getFirst(PARAM_CAT_ID));
			ICategoriesDao dao = DaoFactory.getInstance().getCategoriesDao();
			Category category = dao.getCategoryById(id);

			if (category == null) {
				logger.warn("Aucune catégorie trouvée pour la mise à jour avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'id [" + id + "] n'a été trouvée.").build();
			}

			category.setName(formParams.getFirst(PARAM_CAT_NAME));
			category.setDescription(formParams.getFirst(PARAM_CAT_DESCRIPTION));
			dao.updateCategory(category);

			logger.info("Catégorie mise à jour avec succès : " + category.getName());
			return Response.ok().entity(new GenericEntity<>(category) {
			}).build();
		} catch (NumberFormatException e) {
			logger.warn("Format invalide pour ID lors de la mise à jour.");
			return Response.status(Response.Status.BAD_REQUEST).entity("L'id fourni n'est pas un nombre entier.")
					.build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la mise à jour d'une catégorie : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCategories() {
		try {
			List<Category> list = DaoFactory.getInstance().getCategoriesDao().getAllCategories();
			logger.info("Liste des catégories récupérée avec succès.");
			return Response.ok().entity(new GenericEntity<>(list) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération des catégories : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategoryById(@PathParam("id") final int id) {
		try {
			Category category = DaoFactory.getInstance().getCategoriesDao().getCategoryById(id);
			if (category == null) {
				logger.warn("Aucune catégorie trouvée avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'id [" + id + "] n'a été trouvée.").build();
			}
			logger.info("Catégorie récupérée avec succès : " + category.getName());
			return Response.ok().entity(new GenericEntity<>(category) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération d'une catégorie : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCategory(@PathParam("id") final int id) {
		try {
			ICategoriesDao dao = DaoFactory.getInstance().getCategoriesDao();
			Category category = dao.getCategoryById(id);
			if (category == null) {
				logger.warn("Aucune catégorie trouvée à supprimer avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'id [" + id + "] n'a été trouvée.").build();
			}

			dao.deleteCategory(category);
			logger.info("Catégorie supprimée avec succès : " + category.getName());
			return Response.ok().entity("Catégorie supprimée avec succès.").build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la suppression d'une catégorie : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}