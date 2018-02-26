package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;

public class TrustedHostWindow extends Window {

	final HomepageServiceAsync homepageService = GWT
			.create(HomepageService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	private TrustedHostWindow window;
	private ListStore<TrustHostModel> store;

	public TrustedHostWindow() {
		this.setResizable(false);
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.trustedHostWindowTitle());
		this.setClosable(false);
		this.setSize(400, 300);

		GridCellRenderer<TrustHostModel> buttonRenderer = new GridCellRenderer<TrustHostModel>() {

			public Object render(final TrustHostModel model, String property,
					ColumnData config, final int rowIndex, final int colIndex,
					ListStore<TrustHostModel> store, Grid<TrustHostModel> grid) {
				if (model.getType() == 0)
					return "";
				if( model.isSelected() )
					return "";
				Label label = new Label();
				label.setStyleName("text_AddServer");
				label.setText(UIContext.Constants.trustedHostWindowDelete());
				label.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						
						final Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {

								if (be.getButtonClicked().getItemId().equals(Dialog.YES))
									commonService.removeTrustedHost(model, new BaseAsyncCallback<Void>() {
										@Override
										public void onSuccess(Void result) {
											refreshTrustHost();
										}
									}
								);
							}
						};

						MessageBox mb = new MessageBox();
						mb.setIcon(MessageBox.WARNING);
						mb.setButtons(MessageBox.YESNO);
						mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
						mb.setMessage(UIContext.Messages
								.trustedHostWindowDeleteAlert(model
										.getHostName()));
						mb.addCallback(messageBoxHandler);
						Utils.setMessageBoxDebugId(mb);
						mb.show();
					}
				});

				return label;
			}
		};

		TableLayout windowLayout = new TableLayout();
		windowLayout.setCellPadding(2);
		windowLayout.setCellSpacing(2);
		windowLayout.setWidth("100%");
		windowLayout.setColumns(1);
		window.setLayout(windowLayout);

		Label addServer = new Label();
		addServer.ensureDebugId("2d1f0b74-058b-4e13-a348-264c152877ca");
		addServer.setStyleName("text_AddServer");
		addServer.setText(UIContext.Constants.addTrustedHostAddText());
		addServer.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddTrustedHostWindow window = new AddTrustedHostWindow();
				window.addListener(Events.Hide, new Listener<WindowEvent>() {
					@Override
					public void handleEvent(WindowEvent be) {
						refreshTrustHost();
					}

				});
				window.setModal(true);
				window.show();

			}

		});

		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		this.add(addServer, tableData);

		Button okButton = new Button();
		okButton.ensureDebugId("8b6d0462-c811-4f09-8439-42bbb536f4ad");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});

		this.addButton(okButton);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("hostName");
		column.setHeaderHtml(UIContext.Constants.trustedHostWindowColumnName());
		column.setWidth(150);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("hostName");
		column.setHeaderHtml(UIContext.Constants.trustedHostWindowColumnAction());
		column.setWidth(100);
		column.setRenderer(buttonRenderer);
		column.setMenuDisabled(true);
		configs.add(column);

		store = new ListStore<TrustHostModel>();
		ColumnModel cm = new ColumnModel(configs);

		Grid<TrustHostModel> grid = new Grid<TrustHostModel>(store, cm);
		grid.setBorders(false);
		grid.setAutoExpandColumn("hostName");
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setHeight(200);

		ContentPanel panel = new ContentPanel();
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.add(grid);
		panel.setHeight(200);

		add(panel);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		refreshTrustHost();
	}

	private void refreshTrustHost() {
		homepageService
				.getTrustHosts(new BaseAsyncCallback<TrustHostModel[]>() {

					@Override
					public void onSuccess(TrustHostModel[] result) {
						store.removeAll();
						if (result != null) {
							for (int i = 0; i < result.length; i++) {
								store.add(result[i]);
							}
						}
					}

				});
	}
}
