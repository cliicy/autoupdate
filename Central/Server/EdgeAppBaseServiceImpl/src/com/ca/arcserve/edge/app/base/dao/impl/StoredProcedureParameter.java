package com.ca.arcserve.edge.app.base.dao.impl;
/**
 * A class for CallableStatement parameters. For output parameter, the value is available by {@link #getValue} method
 * @author gonro07
 *
 * @param <T>
 */
class StoredProcedureParameter <T>{
	private int jdbcType;
	private int scale;
	private T value;
	private boolean inPara = true;
	public int getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	/**
	 *
	 * @return -1 means no scalse
	 */
	public int getScale() {
		return scale;
	}
//	public void setScale(int scale) {
//		this.scale = scale;
//	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public StoredProcedureParameter(int jdbcType, int scale,
			T value, boolean inPara) {
		super();
		this.jdbcType = jdbcType;
		this.scale = scale;
		this.value = value;
		this.inPara = inPara;
	}
//	public StoredProcedureParameter(int jdbcType, boolean inPara
//			) {
//		this(jdbcType,-1,null,inPara);
//	}

	public boolean isInPara() {
		return inPara;
	}
	public void setInPara(boolean inPara) {
		this.inPara = inPara;
	}

}
