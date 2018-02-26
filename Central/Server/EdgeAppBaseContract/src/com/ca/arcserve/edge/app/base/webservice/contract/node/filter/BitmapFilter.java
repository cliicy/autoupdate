package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

public class BitmapFilter extends NodeFilter {

	private static final long serialVersionUID = 5607562662415992061L;
	
	private int bitmap;
	
	public BitmapFilter() {
		this(NodeFilterType.JobStatus, 0);
	}
	
	public BitmapFilter(NodeFilterType type, int bitmap) {
		super(type);
		this.bitmap = bitmap;
	}
	
	@Override
	public boolean isEnabled() {
		return bitmap > 0;
	}

	public int getBitmap() {
		return bitmap;
	}

	public void setBitmap(int bitmap) {
		this.bitmap = bitmap;
	}

}
