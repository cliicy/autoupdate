package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class PagingBrowseWindow extends Window {
	public static final int WINDOW_WIDTH = 750;
	public static final int WINDOW_HEIGHT = 500;

	private Window window;
	private PagingContext pContext;
	private PagingBrowsePanel pagingPanel;
	private boolean isCancelled = false;

	public PagingBrowseWindow(PagingContext context) {
		pContext = context;
		this.window = this;
		String fullPath = "";
		if (pContext != null && pContext.getParent() != null) {
			fullPath = pContext.getParent().getPath();
			if (fullPath == null || fullPath.trim().length() == 0) {
				fullPath = pContext.getParent().getDisplayName();
			} else {
				if (!fullPath.endsWith("\\")) {
					fullPath += "\\";
				}
				fullPath += pContext.getParent().getDisplayName();
			}
		}
		String path = UIContext.Messages.browse(fullPath);
		this.setHeadingHtml(path);
		ToolTipConfig tooltip=new ToolTipConfig(new Html("<pre style=\"word-wrap:break-word\">"+path+"</pre>").getHtml());
		tooltip.setMaxWidth(WINDOW_WIDTH);
		this.getHeader().setToolTip(tooltip);
		this.getHeader().setStyleAttribute("text-overflow","ellipsis");
		this.getHeader().setStyleAttribute("white-space", "nowrap");

		this.setResizable(false);
		this.setClosable(true);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);

		LayoutContainer con = new LayoutContainer();
		con.setStyleAttribute("padding", "6px");
		con.setLayout(new FitLayout());

		pagingPanel = new PagingBrowsePanel(pContext);

		con.add(pagingPanel);

		Button okBtn = new Button(UIContext.Constants.ok());
		okBtn.ensureDebugId("AD2BDAB4-228C-4610-9A85-3D5A2AB37882");
		okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});

		this.addButton(okBtn);

		Button cancelBtn = new Button(UIContext.Constants.cancel());
		cancelBtn.ensureDebugId("6CC9ACFE-50B6-4e2c-AC30-D4600663A8FA");
		cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				setCancelled(true);
				window.hide();
			}
		});
		this.addButton(cancelBtn);

		this.add(con);
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public boolean isCancelled() {
		return isCancelled;
	}
}
