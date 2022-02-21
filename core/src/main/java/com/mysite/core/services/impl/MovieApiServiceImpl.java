package com.mysite.core.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysite.core.constants.MysiteConstants;
import com.mysite.core.services.MovieApiService;
import com.mysite.core.services.config.MovieApiConfig;
import com.mysite.core.utils.ApiResponse;

@Component(service = MovieApiService.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = MovieApiConfig.class)
public class MovieApiServiceImpl implements MovieApiService {

	private static final Logger LOG = LoggerFactory.getLogger(MovieApiServiceImpl.class);

	private String imdbApiKey;
	private String imdbApiUrl;
	private String youtubeApiKey;

	private String youtubeApiUrl;

	@Activate
	public void activate(MovieApiConfig apiConfig) {
		this.imdbApiUrl = apiConfig.imdbApiUrl();
		this.imdbApiKey = apiConfig.apiKey();
		this.youtubeApiUrl = apiConfig.youtubeApiUrl();
		this.youtubeApiKey = apiConfig.key();
	}

	private ApiResponse callApi(String url) {
		try {
			LOG.debug("URL ..{}", url);
			final CloseableHttpClient httpClient = HttpClients.createDefault();
			final HttpGet getRequest = new HttpGet(url);
			final CloseableHttpResponse response = httpClient.execute(getRequest);

			if (response != null) {
				LOG.debug("Response Code ..{}", response.getStatusLine().getStatusCode());
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					final String responseEntity = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
					return new ApiResponse(response.getStatusLine().getStatusCode(), responseEntity);
				}
			}
		} catch (final IOException e) {
			return new ApiResponse(MysiteConstants.STATUS_CODE_FAIL, e.getMessage());
		}
		return new ApiResponse(MysiteConstants.STATUS_CODE_FAIL, "Response entity is null");

	}

	@Override
	public ApiResponse getImdbApiResult(String searchParam) {

		final String[] urlParts = { this.imdbApiUrl, this.imdbApiKey, searchParam };
		final String url = StringUtils.join(urlParts, '/');

		return callApi(url);
	}

	@Override
	public ApiResponse getYoutubeApiResult(String searchParam) {

		String url = this.youtubeApiUrl + "?" + MysiteConstants.KEY + this.youtubeApiKey;
		final String[] urlParts = { url, MysiteConstants.Q_PARAM + searchParam, MysiteConstants.PART_SNIPPET };
		url = StringUtils.join(urlParts, '&');

		return callApi(url);
	}

}
