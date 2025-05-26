package edu.esiea.inventorymanager.services;

import java.util.List;

import edu.esiea.inventorymanager.dao.DaoFactory;
import edu.esiea.inventorymanager.dao.interfaces.IArticlesDao;
import edu.esiea.inventorymanager.exception.DaoException;
import edu.esiea.inventorymanager.model.Article;
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

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addArticle(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_ART_NAME) == null || formParams.get(PARAM_ART_EAN13) == null
				|| formParams.get(PARAM_ART_BRAND) == null || formParams.get(PARAM_ART_PICTURE) == null
				|| formParams.get(PARAM_ART_PRICE) == null || formParams.get(PARAM_ART_DESCRIPTION) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String name = formParams.get(PARAM_ART_NAME).getFirst();
		final String EAN13 = formParams.get(PARAM_ART_EAN13).getFirst();
		final String brand = formParams.get(PARAM_ART_BRAND).getFirst();
		final String picture = formParams.get(PARAM_ART_PICTURE).getFirst();
		final String priceStr = formParams.get(PARAM_ART_PRICE).getFirst();
		final String description = formParams.get(PARAM_ART_DESCRIPTION).getFirst();

		if (name == null || name.isBlank() || EAN13 == null || EAN13.isBlank() || brand == null || brand.isBlank()
				|| picture == null || picture.isBlank() || priceStr == null || priceStr.isBlank() || description == null
				|| description.isBlank()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires est vide ou incorrect.").build();
		}

		float price;
		try {
			price = Float.parseFloat(priceStr);
		} catch (final NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Le prix fourni n'est pas un nombre valide.")
					.build();
		}

		Article article = new Article(name, EAN13, brand, picture, price, description);
		try {
			article = DaoFactory.getInstance().getArticlesDao().createArticle(article);
			final GenericEntity<Article> json = new GenericEntity<>(article) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArticle(@PathParam("id") final int idArticle) {
		final IArticlesDao dao = DaoFactory.getInstance().getArticlesDao();
		try {
			final Article article = dao.getArticleById(idArticle);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article avec l'id [" + idArticle + "] n'a été trouvé.").build();
			}
			dao.deleteArticle(article);
			return Response.ok().entity("Supprimé").build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArticles() {
		try {
			final List<Article> ret = DaoFactory.getInstance().getArticlesDao().getAllArticles();
			final GenericEntity<List<Article>> json = new GenericEntity<>(ret) {
			};
			return Response.ok().entity(json).build();
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticleResponse(@PathParam("id") final int idArticle) {
		try {
			Article article = DaoFactory.getInstance().getArticlesDao().getArticleById(idArticle);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article avec l'id [" + idArticle + "] n'a été trouvé.").build();
			}
			final GenericEntity<Article> json = new GenericEntity<>(article) {
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
	public Response updateArticle(final MultivaluedMap<String, String> formParams) {
		if (formParams.get(PARAM_ART_ID) == null || formParams.get(PARAM_ART_NAME) == null
				|| formParams.get(PARAM_ART_EAN13) == null || formParams.get(PARAM_ART_BRAND) == null
				|| formParams.get(PARAM_ART_PICTURE) == null || formParams.get(PARAM_ART_PRICE) == null
				|| formParams.get(PARAM_ART_DESCRIPTION) == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un des paramètres obligatoires n'est pas fourni.").build();
		}

		final String idStr = formParams.get(PARAM_ART_ID).getFirst();
		final String name = formParams.get(PARAM_ART_NAME).getFirst();
		final String EAN13 = formParams.get(PARAM_ART_EAN13).getFirst();
		final String brand = formParams.get(PARAM_ART_BRAND).getFirst();
		final String picture = formParams.get(PARAM_ART_PICTURE).getFirst();
		final String priceStr = formParams.get(PARAM_ART_PRICE).getFirst();
		final String description = formParams.get(PARAM_ART_DESCRIPTION).getFirst();

		int id;
		try {
			id = Integer.parseInt(idStr);
		} catch (final NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("L'id fourni n'est pas un nombre entier.")
					.build();
		}

		float price;
		try {
			price = Float.parseFloat(priceStr);
		} catch (final NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Le prix fourni n'est pas un nombre valide.")
					.build();
		}

		Article article;
		try {
			article = DaoFactory.getInstance().getArticlesDao().getArticleById(id);
			if (article == null) {
				return Response.status(Response.Status.NOT_FOUND).entity("Aucun article avec l'identifiant " + idStr)
						.build();
			}
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		try {
			article.setName(name);
			article.setEAN13(EAN13);
			article.setBrand(brand);
			article.setPicture_URL(picture);
			article.setPrice(price);
			article.setDescription(description);
			DaoFactory.getInstance().getArticlesDao().updateArticle(article);

			final GenericEntity<Article> json = new GenericEntity<>(article) {
			};
			return Response.ok().entity(json).build();
		} catch (final DaoException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}