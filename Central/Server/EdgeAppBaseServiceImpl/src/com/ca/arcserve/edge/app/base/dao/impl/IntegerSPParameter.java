package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class IntegerSPParameter extends StoredProcedureParameter<Integer> {
	public static final int JDBCTYPE = Types.INTEGER;
	public IntegerSPParameter(Integer value) {
		super(JDBCTYPE,-1,value,true);
	}
	public IntegerSPParameter(Integer value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
