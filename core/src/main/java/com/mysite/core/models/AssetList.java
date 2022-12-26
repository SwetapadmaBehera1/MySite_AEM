/**
 *
 */
package com.mysite.core.models;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;

/**
 * @author sweta
 *
 */
@ConsumerType
public interface AssetList extends Component {

	List<AssetResource> getPdfList();
}
