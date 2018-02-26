package com.ca.arcflash.ui.client.coldstandby;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanelImages;

@SuppressWarnings("deprecation")
public interface DisclourePanelImageBundles extends DisclosurePanelImages{

	@Resource("com/ca/arcflash/ui/client/common/icons/arrow_state_grey_right.png")
	public AbstractImagePrototype disclosurePanelClosed();

	@Resource("com/ca/arcflash/ui/client/common/icons/arrow_state_grey_expanded.png")
	public AbstractImagePrototype disclosurePanelOpen();

}
