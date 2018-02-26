package com.ca.arcflash.ui.client.notifications;

import java.util.HashMap;
import java.util.Map.Entry;

import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.homepage.EntitlementRegistrationWindow;
import com.ca.arcflash.ui.client.notifications.events.NotificationEventHandler;
import com.ca.arcflash.ui.client.notifications.events.NotificationRefreshEvent;
import com.ca.arcflash.ui.client.notifications.events.NotificationRefreshEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

public class MessagesWidget extends Composite implements NotificationRefreshEventHandler{

	private static final NotificationServiceAsync notificationService = (NotificationServiceAsync) GWT.create(NotificationService.class);
	private FlashUIMessages commonMessages = GWT.create(FlashUIMessages.class);
	//final Menu messagesMenu = new Menu();
	final TextButton button = new TextButton(commonMessages.messages(0));
	private Label notificationLabel;
	private Label emailLabel;
	 
	public Widget getNotifications()
	{
		button.setIcon(UIContext.IconBundle.logMsg());
		button.ensureDebugId("cf267831-e480-48d3-934a-9babdb08655c");
		button.addStyleName("ca-tertiary");
		button.addStyleName("logonUserItem");
		NotificationEventHandler.getInstance().registerEventHandler(NotificationRefreshEvent.TYPE, this);
	
		
		notificationService.getNotifications(new AsyncCallback<HashMap<String,String>>() {
						@Override
			public void onSuccess(HashMap<String,String> notifications) {
				Menu messagesMenu = new Menu();
				if(notifications != null)
				{
					String notificationLabelStr = "";
					String emailLabelStr = "";
					MenuItem regMenuItem = null;
					int notificationsCount = notifications.size();
					for(Entry<String, String> notification : notifications.entrySet())
					{
						if(notification.getKey().equals("AERP"))
						{
							String value = notification.getValue();
							if(value.equalsIgnoreCase("ISACTIVATED_INACTIVE"))
							{
								if(notifications.containsKey("emailID"))
								{
									notificationLabelStr = commonMessages.messagesUserNotActivatedNotificationForEmail(notifications.get("emailID"));
									notificationsCount = notificationsCount -1;
								}
								else
								{
									notificationLabelStr = commonMessages.messagesUserNotActivatedNotification();
								}
								emailLabelStr = commonMessages.messagesSendNewRegistrationEmail();
							}
							else
							{
								notificationLabelStr = commonMessages.messagesUserNotRegisteredNotification();
								emailLabelStr = commonMessages.messagesRegister();
							}
							//regMenuItem = new MenuItem(){
								//@Override
								///protected void onClick(NativeEvent be) {
									//super.onClick(be);
									//new EntitlementRegistrationWindow();
									//EntitlementRegistrationWindow window = new EntitlementRegistrationWindow();
									//window.ensureDebugId("60565b28-6339-4dcf-8cd4-afe579c35253");
									//window.setModal(true);
									//window.show();
								//}
						//	};
							regMenuItem = new MenuItem();
							//regMenuItem.setHTML(notificationLabel);
							//regMenuItem.setIcon(ComponentNode.getImagebundles().VMStatusWarning());
							VerticalLayoutContainer vContainer = new VerticalLayoutContainer();
							notificationLabel =  new Label(notificationLabelStr, true);
							notificationLabel.setWidth("250px");
							vContainer.add(notificationLabel);
							if(emailLabelStr != null && !emailLabelStr.isEmpty())
							{
								emailLabel = new Label(emailLabelStr, true);
								emailLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
								emailLabel.setWidth("250px");
								emailLabel.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
								emailLabel.addClickHandler(new ClickHandler() {

								    @Override
								    public void onClick(ClickEvent cEv) {
								    	new EntitlementRegistrationWindow();
								    }
								});
								vContainer.add(emailLabel);
							}
							HBoxLayoutContainer hBox = new HBoxLayoutContainer();
							hBox.add(new Image(UIContext.IconBundle.logMsg()), new BoxLayoutData(new Margins(5, 0, 2, 0)));
							hBox.add(vContainer, new BoxLayoutData(new Margins(5, 0, 0, 4)));
							regMenuItem.setWidget(hBox);
							messagesMenu.add(regMenuItem);
							//messagesMenu.add(regMenuItem);
							messagesMenu.add(new SeparatorMenuItem());
						}
					}
					if(notificationsCount > 0)
					{
						button.setText(commonMessages.messages(notificationsCount));
						button.setIcon(UIContext.IconBundle.logMsg());
						button.setMenu(messagesMenu);
					}
					else
					{
						button.setText(commonMessages.messages(notificationsCount));
						button.setIcon(UIContext.IconBundle.logMsg());
						button.clearSizeCache();
						//button.setMenu(messagesMenu);
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});	
		return button;
	}
	
	@Override
	public void refreshNotifications(NotificationRefreshEvent event) {
		button.getMenu().clear();
		getNotifications();
	}
}
