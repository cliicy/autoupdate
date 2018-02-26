package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class FloatSPParameter extends StoredProcedureParameter<Float> {
	public static final int JDBCTYPE = Types.FLOAT;
	public FloatSPParameter(Float value) {
		super(JDBCTYPE,-1,value,true);
	}
	public FloatSPParameter(Float value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
