package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class ShortSPParameter extends StoredProcedureParameter<Short> {
	public static final int JDBCTYPE = Types.SMALLINT;
	public ShortSPParameter(Short value) {
		super(JDBCTYPE,-1,value,true);
	}
	public ShortSPParameter(Short value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
