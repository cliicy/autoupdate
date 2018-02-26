package com.ca.arcserve.edge.app.rps.appdaos.model;

public class EdgeRpsDeployMessage {
	private int msg_type;
	private String errcode;
	private String msg;
	public int getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
}
