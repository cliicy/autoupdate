package com.ca.arcflash.webservice.jni.model;

import java.util.ArrayList;

public class JSearchResult {
	private long found = 0;
	private long current = 0 ;
	private ArrayList<JCatalogDetail> detail = new ArrayList<JCatalogDetail>(0);
	private ArrayList<JMsgSearchRec> msgDetail = new ArrayList<JMsgSearchRec>(0);

	public long getFound() {
		return found;
	}

	public void setFound(long found) {
		this.found = found;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public ArrayList<JCatalogDetail> getDetail() {
		return detail;
	}

	public void setDetail(ArrayList<JCatalogDetail> detail) {
		this.detail = detail;
	}

	public boolean hasNext() {
		return !(current == -1);
	}

	public void setMsgDetail(ArrayList<JMsgSearchRec> msgDetail) {
		this.msgDetail = msgDetail;
	}

	public ArrayList<JMsgSearchRec> getMsgDetail() {
		return msgDetail;
	}
}
