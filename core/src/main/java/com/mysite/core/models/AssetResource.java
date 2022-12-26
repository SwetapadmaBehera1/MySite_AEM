/**
 *
 */
package com.mysite.core.models;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;

/**
 * @author sweta
 *
 */
@ConsumerType
public interface AssetResource extends Component {

	String getPath();

	String getSize();

	String getTitle();
}
