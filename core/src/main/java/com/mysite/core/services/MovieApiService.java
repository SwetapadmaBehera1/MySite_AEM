/**
 *
 */
package com.mysite.core.services;

import com.mysite.core.utils.ApiResponse;

public interface MovieApiService {
	public ApiResponse getImdbApiResult(String searchParam);

	public ApiResponse getYoutubeApiResult(String searchParam);

}
