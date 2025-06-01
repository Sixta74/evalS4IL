package edu.esiea.inventorymanager.services;

import java.util.List;

import org.apache.log4j.Logger;

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
	public static final String PARAM_ART_CATEGORY_ID = "CategoryId";
	public static final String PARAM_ART_STOCK_IDS = "StockIds";

	private static final Logger logger = Logger.getLogger(ArticleServices.class);

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

			logger.warn("Paramètres manquants lors de l'ajout d'un article.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			Article article = new Article(formParams.getFirst(PARAM_ART_NAME), formParams.getFirst(PARAM_ART_EAN13),
					formParams.getFirst(PARAM_ART_BRAND), formParams.getFirst(PARAM_ART_PICTURE),
					Float.parseFloat(formParams.getFirst(PARAM_ART_PRICE)), formParams.getFirst(PARAM_ART_DESCRIPTION));

			article = DaoFactory.getInstance().getArticlesDao().createArticle(article);
			logger.info("Article ajouté avec succès : " + article.getName());
			return Response.status(Response.Status.CREATED).entity(new GenericEntity<>(article) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la création d'un article : " + e.getMessage());
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

			logger.warn("Paramètres manquants pour la mise à jour d'un article.");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Un ou plusieurs paramètres obligatoires sont manquants.").build();
		}

		try {
			int id = Integer.parseInt(formParams.getFirst(PARAM_ART_ID));
			IArticlesDao articlesDao = DaoFactory.getInstance().getArticlesDao();
			Article article = articlesDao.getArticleById(id);
			if (article == null) {
				logger.warn("Aucun article trouvé pour la mise à jour avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article trouvé avec l'identifiant : " + id).build();
			}

			article.setName(formParams.getFirst(PARAM_ART_NAME));
			article.setEAN13(formParams.getFirst(PARAM_ART_EAN13));
			article.setBrand(formParams.getFirst(PARAM_ART_BRAND));
			article.setPicture_URL(formParams.getFirst(PARAM_ART_PICTURE));
			article.setPrice(Float.parseFloat(formParams.getFirst(PARAM_ART_PRICE)));
			article.setDescription(formParams.getFirst(PARAM_ART_DESCRIPTION));

			articlesDao.updateArticle(article);
			logger.info("Article mis à jour avec succès : " + article.getName());
			return Response.ok().entity(new GenericEntity<>(article) {
			}).build();
		} catch (NumberFormatException e) {
			logger.warn("Format invalide pour ID ou prix lors de la mise à jour.");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID ou prix invalide.").build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la mise à jour d'un article : " + e.getMessage());
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
		try {
			Article article = DaoFactory.getInstance().getArticlesDao().getArticleById(id);
			if (article == null) {
				logger.warn("Aucun article trouvé avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article avec l'id [" + id + "] n'a été trouvé.").build();
			}
			logger.info("Article récupéré : " + article.getName());
			return Response.ok().entity(new GenericEntity<>(article) {
			}).build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la récupération d'un article : " + e.getMessage());
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
				logger.warn("Aucun article trouvé à supprimer avec l'ID : " + id);
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Aucun article trouvé avec l'identifiant : " + id).build();
			}

			dao.deleteArticle(article);
			logger.info("Article supprimé avec succès : " + article.getName());
			return Response.ok().entity("Article supprimé avec succès.").build();
		} catch (DaoException e) {
			logger.error("Erreur interne lors de la suppression d'un article : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}