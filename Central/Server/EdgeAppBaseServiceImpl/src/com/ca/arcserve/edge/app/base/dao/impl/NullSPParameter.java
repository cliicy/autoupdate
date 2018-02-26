package com.ca.arcserve.edge.app.base.dao.impl;

import java.sql.Types;

public class NullSPParameter extends StoredProcedureParameter<String> {
	public static final int JDBCTYPE = Types.NVARCHAR;

	public NullSPParameter(int jdbcType) {
		super(jdbcType,-1,null,true);
	}
}
