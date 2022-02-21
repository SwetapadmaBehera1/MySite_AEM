package com.mysite.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Movie API Configuration", description = "Containd configuration for Movie API")
public @interface MovieApiConfig {
	@AttributeDefinition(name = "Movie API key IMDB", type = AttributeType.STRING)
	String apiKey() default "";

	@AttributeDefinition(name = "Movie API endpoint for IMDB", type = AttributeType.STRING)
	String imdbApiUrl() default "https://imdb-api.com/en/API/SearchMovie/";

	@AttributeDefinition(name = "Movie API key Youtube", type = AttributeType.STRING)
	String key() default "";

	@AttributeDefinition(name = "Movie API endpoint for Youtube", type = AttributeType.STRING)
	String youtubeApiUrl() default "https://content-youtube.googleapis.com/youtube/v3/search";

}
