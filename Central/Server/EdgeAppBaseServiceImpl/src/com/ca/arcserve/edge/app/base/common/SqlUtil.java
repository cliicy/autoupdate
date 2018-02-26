package com.ca.arcserve.edge.app.base.common;

import java.util.List;

public class SqlUtil {

	public static String convertWildcard(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}

		value = value.replace("%", "\\%").replace("_", "\\_");
		value = value.replace('*', '%').replace('?', '_');

		if (!value.endsWith("%")) {
			value = value + "%";
		}

		return value;
	}

	public static String marshal(List<Integer> ids) {
		StringBuilder builder = new StringBuilder();

		for (Integer id : ids) {
			builder.append(id).append(" ");
		}

		return builder.toString();
	}

}
