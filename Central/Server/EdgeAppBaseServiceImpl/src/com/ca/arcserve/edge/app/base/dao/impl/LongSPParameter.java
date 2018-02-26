package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;


class LongSPParameter extends StoredProcedureParameter<Long> {
	public static final int JDBCTYPE = Types.BIGINT;
	public LongSPParameter(Long value) {
		super(JDBCTYPE,-1,value,true);
	}
	public LongSPParameter(Long value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
