/**
 *
 */
package com.mysite.core.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.jackrabbit.vault.util.MimeTypes;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.api.youtube.model.YoutubeResponse;
import com.mysite.core.services.MovieApiService;
import com.mysite.core.utils.ApiResponse;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class MovieServletTest {

	private static final AemContext context = new AemContext();
	@Mock
	private MovieApiService movieService;
	@InjectMocks
	private final MovieServlet movieServlet = new MovieServlet();

	@Mock
	private SlingHttpServletRequest request;

	@Mock
	private ResourceResolver resolver;

	@Mock
	private SlingHttpServletResponse response;
	@Mock
	private PrintWriter writer;

	private void initImdbObjects() throws IOException {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		final InputStream imdbResponseJsonStream = classLoader.getResourceAsStream("api/imdb-input.json");

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode node = mapper.readValue(imdbResponseJsonStream, JsonNode.class);

		final String imdbJsonStr = mapper.writeValueAsString(node);
		final ApiResponse apiResponse = new ApiResponse(200, imdbJsonStr);
		lenient().when(this.movieService.getImdbApiResult(any())).thenReturn(apiResponse);
	}

	private void initMovieService() {
		context.registerService(this.movieService);
	}

	private void initYoutubeObjects() throws IOException {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		final InputStream youtubeResponseJsonStream = classLoader.getResourceAsStream("api/youtube-input.json");

		final ObjectMapper mapper = new ObjectMapper();
		final YoutubeResponse youtubeResponse = mapper.readValue(youtubeResponseJsonStream, YoutubeResponse.class);

		final String youtubeJsonStr = mapper.writeValueAsString(youtubeResponse);
		final ApiResponse apiResponse = new ApiResponse(200, youtubeJsonStr);
		when(this.movieService.getYoutubeApiResult(any())).thenReturn(apiResponse);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {

		initMovieService();

		lenient().when(this.response.getWriter()).thenReturn(this.writer);
	}

	@Test
	void testforExpectedJson() throws ServletException, IOException {

		initYoutubeObjects();
		initImdbObjects();

		this.movieServlet.doGet(this.request, this.response);

		verify(this.response, times(1)).setContentType(MimeTypes.getMimeType("json"));
	}
}
