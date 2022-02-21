/**
 *
 */
package com.mysite.core.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.mysite.core.services.config.MovieApiConfig;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class MovieApiServiceImplTest {

	private static final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);
	private static final String MOVIE_NAME = "avengers";
	@Mock
	private HttpClientBuilderFactory httpClienBuilderFactory;

	@Mock
	private HttpClientBuilder httpClientBuilder;

	@Mock
	private MovieApiConfig movieApiConfig;
	@Mock
	private MovieApiServiceImpl movieApiService;

	private InputStream responseJsonStream;

	private StatusLine statusLine;

	private void initHttpObjects() throws UnsupportedOperationException, IOException {

		final ClassLoader classLoader = this.getClass().getClassLoader();
		this.responseJsonStream = classLoader.getResourceAsStream("api/youtube-input.json");

		final HttpEntity httpEntity = mock(HttpEntity.class);
		final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		final CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
		this.statusLine = mock(StatusLine.class);

		lenient().when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientContext.class)))
				.thenReturn(httpResponse);
		lenient().when(httpEntity.getContent()).thenReturn(this.responseJsonStream);
		lenient().when(httpResponse.getEntity()).thenReturn(httpEntity);
		lenient().when(httpResponse.getStatusLine()).thenReturn(this.statusLine);

		lenient().when(this.statusLine.getStatusCode()).thenReturn(200);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.movieApiService = ctx.registerInjectActivateService(MovieApiServiceImpl.class);

		initHttpObjects();

		when(this.movieApiConfig.youtubeApiUrl())
				.thenReturn("https://content-youtube.googleapis.com/youtube/v3/search");
		when(this.movieApiConfig.key()).thenReturn("ewefv");
		when(this.movieApiConfig.imdbApiUrl()).thenReturn("https://imdb-api.com/en/API/SearchMovie/");
		when(this.movieApiConfig.apiKey()).thenReturn("werwer");

		this.movieApiService.activate(this.movieApiConfig);
	}

	@Test
	void testJsonError() throws StreamReadException, DatabindException, IOException {

		assertEquals(400, this.movieApiService.getYoutubeApiResult(null).getStatusCode());
	}

	@Test
	void testJsonSuccess() throws StreamReadException, DatabindException, IOException {

		assertNotNull(this.movieApiService.getImdbApiResult(MOVIE_NAME));
	}

}
