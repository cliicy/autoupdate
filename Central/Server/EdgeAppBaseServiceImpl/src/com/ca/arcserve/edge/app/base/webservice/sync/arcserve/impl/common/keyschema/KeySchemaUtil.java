package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.XMLMarshalUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchema.Tables;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchema.Tables.Table;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchema.Tables.Table.Columns;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchema.Tables.Table.Columns.Column;

public class KeySchemaUtil {

	private KeySchemaUtil() {
		throw new UnsupportedOperationException();
	}

	public static KeySchema getUnmarshalKeySchema() {
		Package pack = KeySchema.class.getPackage();
		String contextPath = pack.getName();

		InputStream xmlStream = null;
		xmlStream = KeySchemaUtil.class.getResourceAsStream("KeySchema.xml");

		KeySchema key = null;
		try {
			key = (KeySchema) XMLMarshalUtil.unmarshal(contextPath, xmlStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		} finally {
			try {
				xmlStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ConfigurationOperator.errorMessage(e.getMessage(), e);
			}
		}

		return key;
	}

	public static ArrayList<String> getUniqueColumn(KeySchema keyschema,
			String objname) {
		Table t = getTableByName(keyschema, objname);

		return getKeyColumns(t);

	}

	protected static Table getTableByName(KeySchema keyschema, String objname) {
		Tables tables = keyschema.getTables();
		List<Table> tableList = tables.getTable();
		Iterator<Table> it = tableList.iterator();

		Table t = null;
		while (it.hasNext()) {
			t = it.next();
			if (t.getName().compareToIgnoreCase(objname) == 0)
				return t;
		}

		return null;

	}

	protected static ArrayList<String> getKeyColumns(Table t) {
		ArrayList<String> columns = new ArrayList<String>();

		if (t != null) {
			Columns cs = t.getColumns();
			List<Column> cl = cs.getColumn();

			Iterator<Column> it = cl.iterator();
			Column c;
			while (it.hasNext()) {
				c = it.next();
				if (c.getIsKey().compareToIgnoreCase("True") == 0)
					columns.add(c.getName());
			}
		}
		return columns;
	}
}
