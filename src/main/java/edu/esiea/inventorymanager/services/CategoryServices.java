package edu.esiea.inventorymanager.services;

import java.util.List;

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

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCategory(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_CAT_NAME) == null || formParams.get(PARAM_CAT_DESCRIPTION) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String name = formParams.get(PARAM_CAT_NAME).getFirst();
		final String description = formParams.get(PARAM_CAT_DESCRIPTION).getFirst();

		if (name.isBlank() || name == null || description.isBlank() || description == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires est vide ou incorrect.").build();
		}

		Category category = new Category(name, description);
		try {
			category = DaoFactory.getInstance().getCategoriesDao().createCategory(category);
			final GenericEntity<Category> json = new GenericEntity<>(category) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCategory(@PathParam("id") final int idCategory) {
		final ICategoriesDao dao = DaoFactory.getInstance().getCategoriesDao();
		try {
			final Category category = dao.getCategoryById(idCategory);
			if (category == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'id [" + idCategory + "] n'a été trouvée.").build();
			}
			dao.deleteCategory(category);
			return Response.ok().entity("Supprimé").build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCategories() {
		try {
			final List<Category> ret = DaoFactory.getInstance().getCategoriesDao().getAllCategories();
			final GenericEntity<List<Category>> json = new GenericEntity<>(ret) {
			};
			return Response.ok().entity(json).build();
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategoryResponse(@PathParam("id") final int idCategory) {
		try {
			Category category = DaoFactory.getInstance().getCategoriesDao().getCategoryById(idCategory);
			if (category == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'id [" + idCategory + "] n'a été trouvée.").build();
			}
			final GenericEntity<Category> json = new GenericEntity<>(category) {
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
	public Response updateCategory(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_CAT_ID) == null || formParams.get(PARAM_CAT_NAME) == null
				|| formParams.get(PARAM_CAT_DESCRIPTION) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String idStr = formParams.get(PARAM_CAT_ID).getFirst();
		final String name = formParams.get(PARAM_CAT_NAME).getFirst();
		final String description = formParams.get(PARAM_CAT_DESCRIPTION).getFirst();

		int id;
		try {
			id = Integer.parseInt(idStr);
		} catch (final NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("L'id fourni n'est pas un nombre entier.")
					.build();
		}

		Category category = null;
		try {
			category = DaoFactory.getInstance().getCategoriesDao().getCategoryById(id);
			if (category == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucune catégorie avec l'identifiant " + idStr + " n'a été trouvée.").build();
			}
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		try {
			category.setName(name);
			category.setDescription(description);
			DaoFactory.getInstance().getCategoriesDao().updateCategory(category);

			final GenericEntity<Category> json = new GenericEntity<>(category) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}