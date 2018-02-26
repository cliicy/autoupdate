package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class BooleanSPParameter extends StoredProcedureParameter<Boolean> {
	public static final int JDBCTYPE = Types.BOOLEAN;
	public BooleanSPParameter(Boolean value) {
		super(JDBCTYPE,-1,value,true);
	}
	public BooleanSPParameter(Boolean value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
