/**
 *
 */
package com.mysite.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.compress.utils.Lists;
import org.apache.jackrabbit.vault.util.MimeTypes;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class AssetListImplTest {

	private static final String ASSET4 = "/content/asset4";

	private static final AemContext ctx = new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
			.build();
	private static final String CURRENT_RESOURCE = "/content/resource";
	private static final String JSON_PATH = "/com/asset-list.json";
	private static final String NOASSETPATH_RESOURCE = "/content/resource_noAssetPath";
	private static final String NOFILTERTAG_RESOURCE = "/content/resource_noFilterTag";
	private static final String TEST_ROOT_PAGE = "/content";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpAll() throws Exception {
		ctx.addModelsForClasses(AssetListImpl.class);
		ctx.load().json(JSON_PATH, TEST_ROOT_PAGE);
	}

	private Asset asset1;
	private Asset asset2;
	private Asset asset3;

	@Mock
	private Hit hit1;

	@Mock
	private Hit hit2;

	@Mock
	private Hit hit3;

	@Mock
	private Hit hit4;

	@Mock
	private final List<Hit> mockListHit = Lists.newArrayList();

	private AssetListImpl model;

	@Mock
	private Query query;

	@Mock
	private QueryBuilder queryBuilder;

	@Mock
	private SearchResult result;

	private void createAsset1() {
		final Map<String, Object> props = new HashMap<>();
		props.put("dc:title", "PDF OVerview");
		props.put("dam:size", 122325);

		this.asset1 = ctx.create().asset("/content/dam/zeiss/pdf1", 10, 10, MimeTypes.getMimeType("json"), props);
	}

	private void createAsset2() {
		final Map<String, Object> props = new HashMap<>();
		props.put("jcr:title", "PDF OVerview");
		props.put("dc:title", "");
		props.put("dam:size", 1073741824);

		this.asset2 = ctx.create().asset("/content/dam/zeiss/pdf2", 10, 10, MimeTypes.getMimeType("json"), props);
	}

	private void createAsset3() {
		final Map<String, Object> props = new HashMap<>();
		props.put("dc:title", "PDF OVerview");
		props.put("dam:size", 3860843);

		this.asset3 = ctx.create().asset("/content/dam/zeiss/pdf3", 10, 10, MimeTypes.getMimeType("json"), props);
	}

	void init() {
		ctx.registerService(QueryBuilder.class, this.queryBuilder);
		ctx.registerAdapter(ResourceResolver.class, Query.class, this.query);

		lenient().when(this.queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class)))
				.thenReturn(this.query);
		lenient().when(this.query.getResult()).thenReturn(this.result);
	}

	@Test
	void testAssetList_noAssetPath() throws RepositoryException {

		init();

		final Resource resource = ctx.currentResource(NOASSETPATH_RESOURCE);

		lenient().when(this.result.getHits()).thenReturn(this.mockListHit);
		lenient().when(this.mockListHit.isEmpty()).thenReturn(true);

		this.model = resource.adaptTo(AssetListImpl.class);
	}

	@Test
	void testAssetList_noFilterTag() throws RepositoryException {

		init();

		final Resource resource = ctx.currentResource(NOFILTERTAG_RESOURCE);

		this.model = resource.adaptTo(AssetListImpl.class);
		assertNotNull(this.model.getPdfList());
	}

	@Test
	void testAssetListSuccess() throws RepositoryException {

		init();
		createAsset1();
		createAsset2();
		createAsset3();

		final Resource resource = ctx.currentResource(CURRENT_RESOURCE);
		final List<Hit> listHit = Lists.newArrayList();
		listHit.add(this.hit1);
		listHit.add(this.hit2);
		listHit.add(this.hit3);
		listHit.add(this.hit4);

		lenient().when(this.result.getHits()).thenReturn(listHit);
		lenient().when(this.hit1.getResource()).thenReturn(ctx.resourceResolver().getResource(this.asset1.getPath()));
		lenient().when(this.hit2.getResource()).thenReturn(ctx.resourceResolver().getResource(this.asset2.getPath()));
		lenient().when(this.hit3.getResource()).thenReturn(ctx.resourceResolver().getResource(this.asset3.getPath()));
		lenient().when(this.hit4.getResource()).thenReturn(ctx.resourceResolver().getResource(ASSET4));

		this.model = resource.adaptTo(AssetListImpl.class);
		assertNotNull(this.model.getPdfList());

		lenient().when(this.hit2.getResource()).thenThrow(RepositoryException.class);
	}

}
