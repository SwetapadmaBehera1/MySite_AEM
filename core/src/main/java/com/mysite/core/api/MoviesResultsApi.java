/**
 *
 */
package com.mysite.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.api.movieResults.ApiApi;
import com.mysite.core.api.movieResults.model.MovieResponse;
import com.mysite.core.api.movieResults.model.MovieResponseResponseInfo;
import com.mysite.core.api.movieResults.model.Movies;
import com.mysite.core.api.movieResults.model.Results;
import com.mysite.core.api.youtube.model.Details;
import com.mysite.core.api.youtube.model.DetailsSnippet;
import com.mysite.core.api.youtube.model.DetailsSnippetThumbnails;
import com.mysite.core.api.youtube.model.DetailsSnippetThumbnailsDefault;
import com.mysite.core.api.youtube.model.Items;
import com.mysite.core.api.youtube.model.YoutubeResponse;
import com.mysite.core.constants.MysiteConstants;
import com.mysite.core.services.MovieApiService;
import com.mysite.core.utils.ApiResponse;

public class MoviesResultsApi implements ApiApi {

	private static final Logger LOG = LoggerFactory.getLogger(MoviesResultsApi.class);

	private final ObjectMapper mapper;
	private final MovieResponse movieResponse;

	private final MovieApiService movieService;
	String searchParam;

	public MoviesResultsApi(MovieResponse movieResponse, ObjectMapper mapper, MovieApiService movieService,
			String searchParam) {
		this.movieResponse = movieResponse;
		this.mapper = mapper;
		this.movieService = movieService;
		this.searchParam = searchParam;

	}

	private Movies getImdbResponse(MovieResponse movieResponse, ObjectMapper mapper, Movies movieList) {

		final ApiResponse movieApiResponse = this.movieService.getImdbApiResult(this.searchParam);
		LOG.debug("IMDB response....{}", movieApiResponse.getStatusCode());
		try {
			if (movieApiResponse.getStatusCode() == MysiteConstants.STATUC_CODE_SUCCESS) {

				final JsonNode node = mapper.readValue(movieApiResponse.getResponseBody(), JsonNode.class);
				final JsonNode array = node.get(MysiteConstants.RESULTS);
				for (final JsonNode imdbResult : array) {
					final Results results = new Results();
					results.setImage(imdbResult.get(MysiteConstants.IMAGE).textValue());
					results.setTitle(imdbResult.get(MysiteConstants.TITLE).textValue());
					results.setDescription(imdbResult.get(MysiteConstants.DESCRIPTION).textValue());
					results.setResultType(MysiteConstants.RESULTTYPE_IMDB);
					movieList.add(results);
				}
			}
			movieResponse.setResponseInfo(
					getResponseInfo(movieApiResponse.getStatusCode(), movieApiResponse.getResponseBody()));
		} catch (final JsonProcessingException e) {
			movieResponse.setResponseInfo(getResponseInfo(MysiteConstants.STATUS_CODE_FAIL, e.getMessage()));
		}
		return movieList;
	}

	@Override
	public MovieResponse getMovieResults() {
		final Movies movieList = getYoutubeResponse(this.movieResponse, this.mapper);
		this.movieResponse.setResults(getImdbResponse(this.movieResponse, this.mapper, movieList));
		return this.movieResponse;
	}

	private MovieResponseResponseInfo getResponseInfo(int statusCode, String message) {

		final MovieResponseResponseInfo responseInfo = new MovieResponseResponseInfo();
		responseInfo.setStatus(statusCode);
		responseInfo.setMessage(message);
		return responseInfo;

	}

	private Movies getYoutubeResponse(MovieResponse movieResponse, ObjectMapper mapper) {

		final ApiResponse apiResponse = this.movieService.getYoutubeApiResult(this.searchParam);
		final Movies movies = new Movies();

		YoutubeResponse youtubeResponse = new YoutubeResponse();
		try {
			if (apiResponse.getStatusCode() == MysiteConstants.STATUC_CODE_SUCCESS) {

				youtubeResponse = mapper.readValue(apiResponse.getResponseBody(), YoutubeResponse.class);

				final Items items = youtubeResponse.getItems();
				for (final Details detail : items) {
					final DetailsSnippet detailsSnippet = detail.getSnippet();
					final DetailsSnippetThumbnails detailThumbnails = detailsSnippet.getThumbnails();
					final DetailsSnippetThumbnailsDefault defaultThumbnail = detailThumbnails.getDefault();
					final Results results = new Results();

					results.setTitle(detailsSnippet.getTitle());
					results.setDescription(detailsSnippet.getDescription());
					results.setImage(defaultThumbnail.getUrl());
					results.setResultType(MysiteConstants.RESULTTYPE_YOUTUBE);

					movies.add(results);
				}
				movieResponse
						.setResponseInfo(getResponseInfo(apiResponse.getStatusCode(), apiResponse.getResponseBody()));
			}
		} catch (final JsonProcessingException e) {
			movieResponse.setResponseInfo(getResponseInfo(MysiteConstants.STATUS_CODE_FAIL, e.getMessage()));
		}

		return movies;
	}
}
