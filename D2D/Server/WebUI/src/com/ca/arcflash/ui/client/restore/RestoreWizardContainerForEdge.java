package com.ca.arcflash.ui.client.restore;

public class RestoreWizardContainerForEdge extends RestoreWizardContainer{

	public RestoreWizardContainerForEdge(int nPageIndex) {
		super(nPageIndex);
		prevButton.enable();
	}

	@Override
	public void SetPage(int page) {
		super.SetPage(page);
		
		switch (page) {
		case RestoreWizardContainer.PAGE_RECOVERY:
		case RestoreWizardContainer.PAGE_SEARCH:
		case RestoreWizardContainer.PAGE_VM_RECOVERY:
		case RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY:
		case RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY:
			prevButton.setVisible(false);
			break;
		default:
			prevButton.setVisible(true);
			break;
		}
		
		nextButton.setVisible(page != RestoreWizardContainer.PAGE_INTRO);
	}	
}
