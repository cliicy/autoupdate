package com.ca.arcflash.failover;

import java.util.Locale;
import java.util.ResourceBundle;

import com.ca.arcflash.common.DataFormatUtil;

public class FailoverMessage {

	public static final String FAILOVER_BEGIN = "FAILOVER_BEGIN";
	public static final String FAILOVER_BEGIN_HYPERV_VIRTUAL_NEEDED = "FAILOVER_BEGIN_HYPERV_VIRTUAL_NEEDED";
	public static final String FAILOVER_Exit_HYPERV_With_Live_HeartBeat = "FAILOVER_Exit_HYPERV_With_Live_HeartBeat";
	public static final String FAILOVER_Error_HYPERV_VMGUESTIMG = "FAILOVER_Error_HYPERV_VMGUESTIMG";
	public static final String FAILOVER_warn_HYPERV_VM_TOOL_NOT_EXIST = "FAILOVER_warn_HYPERV_VM_TOOL_NOT_EXIST";
	public static final String FAILOVER_Error_HYPERV_Handler = "FAILOVER_Error_HYPERV_Handler";
	public static final String FAILOVER_Error_HYPERV_Without_VM = "FAILOVER_Error_HYPERV_Without_VM";
//	public static final String FAILOVER_Error_HYPERV_VM_POWEROFF = "FAILOVER_Error_HYPERV_VM_POWEROFF";
//	public static final String FAILOVER_Error_HYPERV_VM_RUNNING = "FAILOVER_Error_HYPERV_VM_RUNNING";
	public static final String FAILOVER_Error_HYPERV_Without_Snapshot_for_session = "FAILOVER_Error_HYPERV_Without_Snapshot_for_session";
	public static final String FAILOVER_Error_HYPERV_Without_HyperV_ExternaL = "FAILOVER_Error_HYPERV_Without_HyperV_ExternaL";
	public static final String FAILOVER_Error_HYPERV_Without_BootVolume = "FAILOVER_Error_HYPERV_Without_BootVolume";
	public static final String FAILOVER_Error_HYPERV_Mount = "FAILOVER_Error_HYPERV_Mount";
	public static final String FAILOVER_Error_HYPERV_Mount_Dynamic_Disk = "FAILOVER_Error_HYPERV_Mount_Dynamic_Disk";
	public static final String FAILOVER_Error_HYPERV_Mount_Same_Disk = "FAILOVER_Error_HYPERV_Mount_Same_Disk";
	public static final String FAILOVER_Error_HYPERV_InjectDriver = "FAILOVER_Error_HYPERV_InjectDriver";
	public static final String FAILOVER_Error_HYPERV_Attach_VMGUESTISO = "FAILOVER_Error_HYPERV_Attach_VMGUESTISO";
	public static final String FAILOVER_Error_HYPERV_Get_VirtualNetWorks = "FAILOVER_Error_HYPERV_Get_VirtualNetWorks";
	public static final String FAILOVER_Error_HYPERV_Add_NetWorkAdapter = "FAILOVER_Error_HYPERV_Add_NetWorkAdapter";
	public static final String FAILOVER_WARN_HYPERV_CHANGE_ADAPTER_TYPE = "FAILOVER_WARN_HYPERV_CHANGE_ADAPTER_TYPE";
	public static final String FAILOVER_Error_HYPERV_RervertSession = "FAILOVER_Error_HYPERV_RervertSession";
	public static final String FAILOVER_Error_HYPERV_VM_RECONFIGURE = "FAILOVER_Error_HYPERV_VM_RECONFIGURE";
//	public static final String FAILOVER_Error_HYPERV_PowserON = "FAILOVER_Error_HYPERV_PowserON";
	public static final String FAILOVER_Error_HYPERV_REMOVE_ALL_ADAPTERS = "FAILOVER_Error_HYPERV_REMOVE_ALL_ADAPTERS";
	public static final String FAILOVER_Error_VIRTUAL_NETWORK_NOTAVAILABLE = "FAILOVER_Error_VIRTUAL_NETWORK_NOTAVAILABLE";
	public static final String FIALOVER_Error_GET_BOOT_DRIVER_LETTER = "FIALOVER_Error_GET_BOOT_DRIVER_LETTER";
	public static final String FIALOVER_Error_GET_SYS_DRIVER_LETTER = "FIALOVER_Error_GET_SYS_DRIVER_LETTER";
	
//	public static final String FAILOVER_Process_HYPERV_PowserON = "FAILOVER_Process_HYPERV_PowserON";
	public static final String FAILOVER_Process_HYPERV_Exit = "FAILOVER_Process_HYPERV_Exit";
	public static final String FAILOVER_Process_HYPERV_Reconfigure = "FAILOVER_Process_HYPERV_Reconfigure";
	public static final String FAILOVER_Process_HYPERV_Position_Snapshot = "FAILOVER_Process_HYPERV_Position_Snapshot";
	public static final String FAILOVER_Process_HYPERV_Configure_Network = "FAILOVER_Process_HYPERV_Configure_Network";
	public static final String FAILOVER_Process_HYPERV_Apply_SnapShot = "FAILOVER_Process_HYPERV_Apply_SnapShot";
	public static final String FAILOVER_Process_HYPERV_Process_BootVolumeDisk = "FAILOVER_Process_HYPERV_Process_BootVolumeDisk";
	public static final String FAILOVER_Process_HYPERV_Attach_VMGUESTISO = "FAILOVER_Process_HYPERV_Attach_VMGUESTISO";
	public static final String FAILOVER_Process_HYPERV_Check_VM = "FAILOVER_Process_HYPERV_Check_VM";
	public static final String FAILOVER_Process_HYPERV_Returne_MESSAGE = "FAILOVER_Process_HYPERV_RETURN_MESSAGE";
	
	public static final String FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION="FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION";
	public static final String FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_BEGIN="FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_BEGIN";
	public static final String FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_FAIL="FAILOVER_PROCESS_VMWARE_DRIVER_INJECTION_FAIL";
	public static final String FAILOVER_PROCESS_VMWARE_OBTAIN_ADRCONFIG="FAILOVER_PROCESS_VMWARE_OBTAIN_ADRCONFIG";
	public static final String FAILOVER_PROCESS_VMWARE_FIND_BOOT_VOLUME="FAILOVER_PROCESS_VMWARE_FIND_BOOT_VOLUME";
	public static final String FAILOVER_PROCESS_VMWARE_FIND_VMDK_URL="FAILOVER_PROCESS_VMWARE_FIND_VMDK_URL";
	public static final String FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_BEGIN="FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_BEGIN";
	public static final String FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_FAIL="FAILOVER_PROCESS_VMWARE_REVERT_SNAPSHOT_FAIL";
//	public static final String FAILOVER_PROCESS_VMWARE_POWER_ON_BEGIN="FAILOVER_PROCESS_VMWARE_POWER_ON_BEGIN";
//	public static final String FAILOVER_PROCESS_VMWARE_POWER_ON_FAIL="FAILOVER_PROCESS_VMWARE_POWER_ON_FAIL";
	public static final String FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_BEGIN="FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_BEGIN";
	public static final String FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_FAIL="FAILOVER_PROCESS_VMWARE_MOUNT_VM_TOOLS_FAIL";
//	public static final String FAILOVER_PROCESS_VMWARE_POWER_OFF="FAILOVER_PROCESS_VMWARE_POWER_OFF";
	public static final String FAILOVER_ERROR_VMWARE_INVALID_DISKSIGNATURE="FAILOVER_ERROR_VMWARE_INVALID_DISKSIGNATURE";
	public static final String FAILOVER_ERROR_VMWARE_INVALID_SYS_BOOT_DISKSIGNATURE="FAILOVER_ERROR_VMWARE_INVALID_SYS_BOOT_DISKSIGNATURE";
	public static final String FAILOVER_PROCESS_VMWARE_TOOL_OLD_VERSION="FAILOVER_PROCESS_VMWARE_TOOL_OLD_VERSION";
	public static final String FAILOVER_PROCESS_VMWARE_TOOL_QUERY_FAILED="FAILOVER_PROCESS_VMWARE_TOOL_QUERY_FAILED";
//	public static final String FAILOVER_PROCESS_SHUTDOWN_VMWARE_FAILED="FAILOVER_PROCESS_SHUTDOWN_VMWARE_FAILED";
	
	public static final String FAILOVER_PROCESS_BEGIN_CONFIGURE_BOOTABLE_SESSION="FAILOVER_PROCESS_BEGIN_CONFIGURE_BOOTABLE_SESSION";
	public static final String FAILOVER_PROCESS_FINISH_CONFIGURE_BOOTABLE_SESSION="FAILOVER_PROCESS_FINISH_CONFIGURE_BOOTABLE_SESSION";
	public static final String FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT="FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT";
	public static final String FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT_VM_POWER_ON="FAILOVER_PROCESS_FAILED_CREATE_BOOTABLE_SNAHSOT_VM_POWER_ON";

	public static final String FAILOVER_PROCESS_VMI_FAIL_TAKE_SNAPSHOT="FAILOVER_PROCESS_VMI_FAIL_TAKE_SNAPSHOT";
	public static final String FAILOVER_PROCESS_VMI_FAIL_REVERT_SNAPSHOT="FAILOVER_PROCESS_VMI_FAIL_REVERT_SNAPSHOT";

	public static final String FAILOVER_ERROR_ESX_FAILOVER_FAIL="FAILOVER_ERROR_ESX_FAILOVER_FAIL";
	public static final String FAILOVER_Process_ESX_RETURN_MESSAGE="FAILOVER_Process_ESX_RETURN_MESSAGE";
	
	public static final String SYNC_ADRCONFIGURE_TO_VCM_SUCCESSFUL = "SYNC_ADRCONFIGURE_TO_VCM_SUCCESSFUL";
	public static final String SYNC_ADRCONFIGURE_TO_VCM_FAILED = "SYNC_ADRCONFIGURE_TO_VCM_FAILED";
	public static final String SYNC_ADRCONFIGURE_TO_VCM_FAILED_DESTINATION = "SYNC_ADRCONFIGURE_TO_VCM_FAILED_DESTINATION";
	
	public static final String FAILOVER_PROCESS_VM_SHUTDOWN = "FAILOVER_PROCESS_VM_SHUTDOWN";
	public static final String FAILOVER_PROCESS_VM_SHUTDOWN_FAILED = "FAILOVER_PROCESS_VM_SHUTDOWN_FAILED";
	public static final String FAILOVER_PROCESS_VM_POWER_ON="FAILOVER_PROCESS_VM_POWER_ON";
	public static final String FAILOVER_PROCESS_VM_POWER_ON_FAIL="FAILOVER_PROCESS_VM_POWER_ON_FAIL";
	
	public static final String FAILOVER_RET_MSG="FAILOVER_RET_MSG";

	private static ResourceBundle labels;
	static {
	 
		labels = ResourceBundle.getBundle("com.ca.arcflash.failover.failoverRes", DataFormatUtil.getServerLocale() );
	}
	
	public static String getResource(String key){
		return labels.getString(key);
	}
	public static String getResource(String key,String... pars){
		
		String labelValue= labels.getString(key);
		return String.format(labelValue, (Object[])pars);
		
	}
}
