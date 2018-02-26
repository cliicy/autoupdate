package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;
import java.util.Date;


class DateSPParameter extends StoredProcedureParameter<Date> {
	public static final int JDBCTYPE = Types.TIMESTAMP;
	public DateSPParameter(Date value) {
		super(JDBCTYPE,-1,value,true);
	}
	public DateSPParameter(Date value,int jdbcType) {
		super(jdbcType,-1,value,true);
	}
}
