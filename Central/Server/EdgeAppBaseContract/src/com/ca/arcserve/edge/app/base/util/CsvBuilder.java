/**
 * 
 */
package com.ca.arcserve.edge.app.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijwe02
 * 
 */
public class CsvBuilder {
	private ArrayList<ArrayList<String>> csvLines = new ArrayList<ArrayList<String>>();
	private ArrayList<String> currentLine;

	private StringBuffer instantBuffer;
	public void newLine() {
		currentLine = new ArrayList<String>();
		csvLines.add(currentLine);
	}

	public void addLine(String... columns) {
		if (currentLine == null || currentLine.size() > 0) {
			currentLine = new ArrayList<String>();
			csvLines.add(currentLine);
		}
		for (String column : columns) {
			currentLine.add(column);
		}
		currentLine = new ArrayList<String>();
		csvLines.add(currentLine);
	}

	public void addLine(List<String> columnList) {
		if (columnList != null) {
			String[] columns = columnList.toArray(new String[0]);
			addLine(columns);
		}
	}

	public void addColumn(String column) {
		if (StringUtil.isEmptyOrNull(column)) {
			column = "";
		}
		if (currentLine == null) {
			currentLine = new ArrayList<String>();
			csvLines.add(currentLine);
		}
		currentLine.add(column);
	}

	public static String escape(String content) {
		if (StringUtil.isEmptyOrNull(content)) {
			content = "";
		}
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("\"").append(content.replaceAll("\"", "\"\"")).append("\"");
		return strBuf.toString();
	}

	public String getCsvContent() {
		if(  instantBuffer !=null ) {
			return instantBuffer.toString();
		}
		else {
			StringBuffer strBuf = new StringBuffer();
			for (ArrayList<String> line : csvLines) {
				for (int column = 0; column < line.size(); column++) {
					String columnValue = line.get(column);
					if (column != 0) {
						strBuf.append(",");
					}
					strBuf.append(escape(columnValue));
				}
				strBuf.append("\n");
			}
			return strBuf.toString();
		}

	}
	public void addStringToBufferInstantly( String buffer ) {
		if( instantBuffer == null ) {
			instantBuffer = new StringBuffer(); 
		}
		if( buffer != null ) {
			instantBuffer.append( buffer );
		}
	}
	public void addLineToBufferInstantly( List<String> columnList ){
		if( instantBuffer == null ) {
			instantBuffer = new StringBuffer(); 
		}
		if( columnList == null ) {
			instantBuffer.append("\n");
		}
		else {
			boolean first = true;
			for ( String col : columnList ) {
				if ( first ) {
					first = false;
				}
				else {
					instantBuffer.append(",");
				}
				instantBuffer.append( escape(col) );
			}
			instantBuffer.append("\n");
		}
	}
}
