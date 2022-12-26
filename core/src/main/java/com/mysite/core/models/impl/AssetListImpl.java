/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mysite.core.models.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.google.common.collect.Lists;
import com.mysite.core.constants.AssetConstants;
import com.mysite.core.models.AssetList;
import com.mysite.core.models.AssetResource;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sweta
 *
 */
@Slf4j
@Model(adaptables = { Resource.class }, adapters = { AssetList.class, ComponentExporter.class }, resourceType = {
		AssetListImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetListImpl implements AssetList {

	protected static final String RESOURCE_TYPE = "zeiss/components/pdflist";

	@ValueMapValue
	private String[] filterTag;

	@Getter
	private List<AssetResource> pdfList;

	@Inject
	private QueryBuilder queryBuilder;

	@ValueMapValue(name = "pdfPath")
	private String queryPath;

	@SlingObject
	private ResourceResolver resourceResolver;

	private Map<String, String> getQueryMap() {

		final Map<String, String> queryMap = new HashMap<>();

		queryMap.put(AssetConstants.PATH, this.queryPath);
		queryMap.put(AssetConstants.TYPE, AssetConstants.DAM_ASSET);
		queryMap.put(AssetConstants.PROPERTY1, AssetConstants.JCRCONTENT_METADATA_DCFORMAT);
		queryMap.put(AssetConstants.PROPERTY_VALUE1, AssetConstants.APPLICATION_PDF);
		if (this.filterTag != null) {
			int count = 1;
			for (final String tag : this.filterTag) {
				queryMap.put(AssetConstants.PROPERTY2, AssetConstants.JCRCONTENT_METADATA_CQTAGS);
				queryMap.put(StringUtils.join(AssetConstants.PROPERTY2, count + AssetConstants.VALUE), tag);
				queryMap.put(StringUtils.join(AssetConstants.PROPERTY2, AssetConstants.OPERATION),
						AssetConstants.EQUALS);
				count++;
			}
		}
		queryMap.put(AssetConstants.P_LIMIT, "-1");

		return queryMap;

	}

	@PostConstruct
	protected void init() {

		if (StringUtils.isNotBlank(this.queryPath)) {
			this.pdfList = Lists.newArrayList();
			final Query query = this.queryBuilder.createQuery(PredicateGroup.create(getQueryMap()),
					this.resourceResolver.adaptTo(Session.class));
			log.debug("Query executed {}", query);
			final List<Hit> hits = query.getResult().getHits();

			for (final Hit hit : hits) {
				try {
					final AssetResource pdfResource = hit.getResource().adaptTo(AssetResource.class);
					log.debug("Title ..Path ..Size ..{}..{}..{}", pdfResource.getTitle(), pdfResource.getPath(),
							pdfResource.getSize());
					this.pdfList.add(pdfResource);

				} catch (final RepositoryException e) {
					// TODO Auto-generated catch block
					log.error("Repository Exception {}", e.getMessage());
				}
			}

		}
	}

}
