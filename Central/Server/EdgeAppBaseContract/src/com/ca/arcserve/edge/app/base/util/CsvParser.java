package com.ca.arcserve.edge.app.base.util;

import java.util.ArrayList;
import java.util.List;

public class CsvParser {
	
	private String csv;
	
	public CsvParser(String csv) {
		this.csv = csv;
	}
	
	public List<String> getNextRow() {
		List<String> retval = new ArrayList<String>();
		
		boolean end = false;
		while (!end && csv != null) {
			int charNum = startWithNewLine();
			if (charNum > 0) {
				skip(charNum);
				end = true;
			} else if (csv.startsWith("\"")) {
				String field  = this.getEscapedField();
				retval.add(field);
			} else {
				String field  = this.getNormalField();
				retval.add(field);
			}
		}
		
		return retval;
	}
	
	private int startWithNewLine() {
		if (csv.startsWith("\n")) {
			return 1;
		} else if (csv.startsWith("\r\n")) {
			return 2;
		} else {
			return 0;
		}
	}
	
	private void skip(int i) {
		if (i < csv.length()) {
			csv = csv.substring(i);
		} else {
			csv = null;
		}
	}
	
	private String getNormalField() {
		int index = 0;
		boolean isLastField = false;
		
		for (; index < csv.length(); ++index) {
			char ch = csv.charAt(index);
			
			if (ch == ',') {
				break;
			}
			
			if (ch == '\n') {
				isLastField = true;
				break;
			}
		}
		
		String retval = csv.substring(0, index).trim();	// ignore space
		
		if (isLastField) {
			skip(index);
		} else {
			skip(index + 1);
		}
		
		return retval;
	}
	
	private String getEscapedField() {
		skip(1);	// escape "
		
		StringBuilder builder = new StringBuilder();
		boolean end = false;
		
		while (!end && csv != null) {
			int index = csv.indexOf("\"");
			if (index == -1) {
				builder.append(csv);
				csv = null;
				break;
			}
			
			builder.append(csv.substring(0, index));
			csv = csv.substring(index + 1);
			
			if (startWithNewLine() > 0) {
				end = true;
			} else if (csv.startsWith(",")) {
				end = true;
				skip(1);
			} else if (csv.startsWith("\"")) {
				builder.append("\"");
				skip(1);
			}
		}
		
		return builder.toString();
	}

}
