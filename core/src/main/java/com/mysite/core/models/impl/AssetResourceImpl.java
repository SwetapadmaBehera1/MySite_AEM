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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.mysite.core.models.AssetResource;

import lombok.Getter;

/**
 * @author sweta
 *
 */
@Model(adaptables = { Resource.class }, adapters = {
		AssetResource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetResourceImpl implements AssetResource {

	@Getter
	private String path;

	@ValueMapValue(name = "./jcr:content/metadata/dam:size")
	private long pdfSize;

	@Self
	Resource resource;

	@Getter
	private String size;

	@Getter
	@ValueMapValue(name = "./jcr:content/metadata/dc:title")
	private String title;

	private void convertSize() {

		final long units = this.pdfSize / 1024;
		this.size = this.pdfSize < 1024 ? StringUtils.join(this.pdfSize, " B")
				: (units < 1024 ? StringUtils.join(units, "KB")
						: (units / 1024 < 1024 ? StringUtils.join(units / 1024, "MB")
								: StringUtils.join((units / 1024) / 1024, "GB")));

	}

	@PostConstruct
	protected void init() {

		this.title = StringUtils.isEmpty(this.title) ? this.resource.getName() : this.title;
		this.path = this.resource.getPath();
		convertSize();

	}

}
