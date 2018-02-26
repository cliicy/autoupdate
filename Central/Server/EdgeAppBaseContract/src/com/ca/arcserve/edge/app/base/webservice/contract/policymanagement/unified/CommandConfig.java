package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CommandConfig implements Serializable {

	private static final long serialVersionUID = 898058007334970140L;
	  private boolean enableBeforeJob;
	  private String beforeJobCommand;
	  private boolean isOnExitCode;
	  private long exitCode;
	  private BeforeAfterJobOption beforeJobOption;
	  private boolean enableAfterJob;
	  private String afterJobCommand;
	  private Set<BeforeAfterJobOption> afterJobOption = new HashSet<BeforeAfterJobOption>();
	  private String username;
	  private String password;
	public boolean isEnableBeforeJob() {
		return enableBeforeJob;
	}
	public void setEnableBeforeJob(boolean enableBeforeJob) {
		this.enableBeforeJob = enableBeforeJob;
	}
	public String getBeforeJobCommand() {
		return beforeJobCommand;
	}
	public void setBeforeJobCommand(String beforeJobCommand) {
		this.beforeJobCommand = beforeJobCommand;
	}
	public boolean isOnExitCode() {
		return isOnExitCode;
	}
	public void setOnExitCode(boolean isOnExitCode) {
		this.isOnExitCode = isOnExitCode;
	}
	public long getExitCode() {
		return exitCode;
	}
	public void setExitCode(long exitCode) {
		this.exitCode = exitCode;
	}
	public BeforeAfterJobOption getBeforeJobOption() {
		return beforeJobOption;
	}
	public void setBeforeJobOption(BeforeAfterJobOption beforeJobOption) {
		this.beforeJobOption = beforeJobOption;
	}
	public boolean isEnableAfterJob() {
		return enableAfterJob;
	}
	public void setEnableAfterJob(boolean enableAfterJob) {
		this.enableAfterJob = enableAfterJob;
	}
	public String getAfterJobCommand() {
		return afterJobCommand;
	}
	public void setAfterJobCommand(String afterJobCommand) {
		this.afterJobCommand = afterJobCommand;
	}
	public Set<BeforeAfterJobOption> getAfterJobOption() {
		return afterJobOption;
	}
	public void setAfterJobOption(Set<BeforeAfterJobOption> afterJobOption) {
		this.afterJobOption = afterJobOption;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	  
	  
}
