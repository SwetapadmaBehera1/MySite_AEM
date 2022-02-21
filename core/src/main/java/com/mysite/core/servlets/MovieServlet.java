/**
 *
 */
package com.mysite.core.servlets;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.api.MoviesResultsApi;
import com.mysite.core.api.movieResults.model.MovieResponse;
import com.mysite.core.constants.MysiteConstants;
import com.mysite.core.services.MovieApiService;

@Component(service = Servlet.class, property = { SLING_SERVLET_PATHS + "=/bin/mysite/movie-results",
		SLING_SERVLET_METHODS + "=HttpConstants.METHOD_GET", SLING_SERVLET_EXTENSIONS + "=json" })
public class MovieServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	@Reference
	private MovieApiService movieService;
	String searchParam;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		this.searchParam = request.getParameter(MysiteConstants.FULLTEXT);
		final ObjectMapper mapper = new ObjectMapper();
		MovieResponse movieResponse = new MovieResponse();

		final MoviesResultsApi moviesApi = new MoviesResultsApi(movieResponse, mapper, this.movieService,
				this.searchParam);
		movieResponse = moviesApi.getMovieResults();

		final String jsonString = mapper.writer().writeValueAsString(movieResponse);
		response.setHeader(MysiteConstants.CACHE_CONTROL, MysiteConstants.MAX_AGE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MysiteConstants.APPLICATION_JSON);
		response.getWriter().write(jsonString);

	}

}
