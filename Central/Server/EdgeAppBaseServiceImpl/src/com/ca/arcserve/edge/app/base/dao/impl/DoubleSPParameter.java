package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class DoubleSPParameter extends StoredProcedureParameter<Double> {
	public static final int JDBCTYPE = Types.DOUBLE;
	public DoubleSPParameter(Double value) {
		super(JDBCTYPE,-1,value,true);
	}
	public DoubleSPParameter(Double value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
