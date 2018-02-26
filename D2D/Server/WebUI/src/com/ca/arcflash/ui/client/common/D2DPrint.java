package com.ca.arcflash.ui.client.common;

public class D2DPrint {

	public static native void print()/*-{
			$wnd.print();
	}-*/;


	public static native void print(String html)/*-{
		var frame = $doc.getElementById("__gwt_historyFrame");
		if (!frame){
			return;
		}
		
		frame = frame.contentWindow;
		var doc = frame.document;
		doc.open();
		doc.write(html);
		doc.close();
		frame.focus();
		frame.print();
	}-*/;
}
