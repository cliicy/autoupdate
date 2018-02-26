package com.ca.arcserve.edge.app.base.webservice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import com.arcserve.asbu.webservice.archive2tape.udp.ASBUInfo;
import com.arcserve.asbu.webservice.archive2tape.udp.ASBUStatus;
import com.arcserve.edge.common.annotation.NonSecured;
import com.arcserve.edge.rbac.model.Permission;
import com.arcserve.edge.rbac.model.Role;
import com.arcserve.edge.rbac.webservice.IRBACService;
import com.arcserve.edge.rbac.webservice.RBACServiceImpl;
import com.ca.arcflash.common.IDirectWebServiceImpl;
import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.listener.service.event.ActivityLogEvent;
import com.ca.arcflash.listener.service.event.DataSyncEvent;
import com.ca.arcflash.listener.service.event.DatastoreStatusChangeEvent;
import com.ca.arcflash.listener.service.event.JobEvent;
import com.ca.arcflash.listener.service.event.JobHistoryEvent;
import com.ca.arcflash.rps.webservice.data.ManualFilecopyItem;
import com.ca.arcflash.rps.webservice.data.RecoveryPointWithNodeInfo;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.ds.HashRoleEnvInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.rps.webservice.replication.ManualMergeItem;
import com.ca.arcflash.rps.webservice.replication.ManualReplicationItem;
import com.ca.arcflash.webservice.data.ApplicationStatus;
import com.ca.arcflash.webservice.data.ConnectionInfo;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.JobMonitor;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DInfo;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.vsphere.ResourcePool;
import com.ca.arcflash.webservice.data.vsphere.VirtualCenter;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.edge.data.notify.NotifyMessage;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcflash.webservice.toedge.ID2DChangeProtocolNotify;
import com.ca.arcflash.webservice.toedge.IEdgeD2DJobService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.IEdgeLicense;
import com.ca.arcflash.webservice.toedge.IEdgeNotify;
import com.ca.arcflash.webservice.toedge.IEdgeVSphereService;
import com.ca.arcserve.edge.app.asbu.webservice.ASBUServiceImpl;
import com.ca.arcserve.edge.app.asbu.webservice.IASBUService;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.abintegration.ABFuncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.ActionCenter;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.IActionCenter;
import com.ca.arcserve.edge.app.base.webservice.aerp.AERPWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.client.IBaseService;
import com.ca.arcserve.edge.app.base.webservice.cloudaccount.CloudAccountServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItem;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItemId;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.ApmResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.BIPatchInfoEdge;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.PatchInfoEdge;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUDeviceInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaGroupInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPoolSet;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUSyncResult;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.DeleteASBUBackupServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBBranchInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Account;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeAppInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgePreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeRegistryInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ExternalLinks;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ShowEULAModule;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SimpleSortPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.CmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DeployD2DSettings;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.PreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DMergeJobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.RepJobMonitor4Edge;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.RecoveryPointDataItem;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryItem;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.FilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayHostHeartbeatResponse2;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayLoginInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayRegistrationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUnregistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdateStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayUpdatesInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GenerateGatewayRegStrParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.HypervisorWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVHDOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.InstantVMPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.ProtectedNodeWithRecoveryPoints;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryPointInfoForInstantVM;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.RecoveryServerResult;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StartInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVHDOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.StopInstantVMOperation;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter4Dashboard;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobTypeForGroupByPlan;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResultWithMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.CSVObject;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ESXServer;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHostBackupStats;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ExportNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDeleteSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeInfoList4VM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterSavingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenario;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RPSSourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResultForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SRMSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SourceMachineNetworkAdapterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.StandbyVMNetworkInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilterGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.AssignPolicyCheckResultCode;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.BackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ParsedBackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlolicyPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResource;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.DeployStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImagesInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.MachineConfigure;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.contract.reportdashboard.BackupStatusByGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceValidationResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.ProxyConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VMInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAParameters;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAResult;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.PatchManager;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.d2dresync.EdgeD2DReSyncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.dataSync.RecoveryPointHandler;
import com.ca.arcserve.edge.app.base.webservice.destinationmanagement.ShareFolderManageServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.jobhistory.JobHistoryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImplWrapper;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail;
import com.ca.arcserve.edge.app.base.webservice.node.LinuxNodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.notify.EdgeNotifyServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.LinuxPlanManagmentImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductDeployServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.recoverypoints.RecoveryPointServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.srm.NodeDeleteServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.srm.SrmServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.storageappliance.StorageApplianceServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.CheckSyncProperties;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncArcserveServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncArcserveServiceMgr;
import com.ca.arcserve.edge.app.base.webservice.vSphere.VSphereServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vcm.VCMServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.IMspCustomerService;
import com.ca.arcserve.edge.app.msp.webservice.IMspNodeService;
import com.ca.arcserve.edge.app.msp.webservice.IMspPlanService;
import com.ca.arcserve.edge.app.msp.webservice.MspCustomerServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.MspNodeServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.MspPlanServiceImpl;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingResult;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingResult;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.datastore.DataSeedingJobScript;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.filecopyJob.ManualFilecopyParam;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.job.replicationJob.ManualReplicationRPSParam;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.AddRpsNodesResult;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsConnectionInfo;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.rps.IEdgeRPSRegService;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSDataStoreService;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSNodeService;
import com.ca.arcserve.edge.app.rps.webservice.rpsReg.EdgeRPSRegServiceImpl;

/**From jaxws spec 4.2.4
 * <pre>
 * All methods of an SEI can throw javax.xml.ws.WebServiceException and zero or more service specific exceptions
 * 1. Conformance (Remote Exceptions): If an error occurs during a remote operation invocation, an implementation
 * MUST throw a service specific exception if possible. If the error cannot be mapped to a service specific
 * exception, an implementation MUST throw a ProtocolException or one of its subclasses, as appropriate for
 * the binding in use. See section 6.4.1 for more details.
 * 2. Conformance (Other Exceptions): For all other errors, i.e. all those that don’t occur as part of a remote
 * invocation, an implementation MUST throw a WebServiXML
 * ceException whose cause is the original local exception
 *  that was thrown, if any.
 *  For instance, an error in the configuration of a proxy instance may result in a WebServiceException whose
 *  cause is a java.lang.IllegalArgumentException thrown by some implementation code.
 *  </pre>
 *  <pre>
 *  6.4.1 Protocol Specific Exception Handling
 *  1. Conformance (Protocol specific fault generation): When throwing an exception as the result of a protocol
 *  level fault, an implementation MUST ensure that the exception is an instance of the appropriate ProtocolException
 *   subclass. For SOAP the appropriate ProtocolException subclass is SOAPFaultException,
 *   for XML/HTTP is is HTTPException.
 *   2.Conformance (Protocol specific fault consumption): When an implementation catches an exception thrown
 *   by a service endpoint implementation and the cause of that exception is an instance of the appropriate
 *   ProtocolException subclass for the protocol in use, an implementation MUST reflect the information
 *   contained in the ProtocolException subclass within the generated protocol level fault.
 *   </pre>
 *   <pre>
 *   6.4.2 One-way Operations
 *   1. Conformance (One-way operations): When sending a one-way message, implementations MUST throw
 *   a WebServiceException if any error is detected when sending the message.
 *   </pre>
 * @author gonro07
 *
 */
@WebService(endpointInterface="com.ca.arcserve.edge.app.base.webservice.IEdgeService")
public class EdgeWebServiceImpl implements IEdgeService, IDirectWebServiceImpl, IServiceSecure {

	private static final Logger logger = Logger.getLogger(EdgeWebServiceImpl.class);

	public static final  String DBConfigFilePath = EdgeCommonUtil.EdgeInstallPath+EdgeCommonUtil.EdgeCONFIGURATION_DIR + Configuration.DBCONFIGURATION_FILE;
	public static String cmConfigurationFilePath = "";

	@Resource
	private WebServiceContext context;

	private INodeService nodeService = EdgeWebServiceProxyFactory.createProxy4D2D(new NodeServiceImpl(this), INodeService.class, this);
	private IASBUService asbuService = EdgeWebServiceProxyFactory.createASBUProxy(new ASBUServiceImpl(this), IASBUService.class, this);
	private IRBACService rbacService = EdgeWebServiceProxyFactory.createRBACProxy(new RBACServiceImpl(this), IRBACService.class, this);
	private IEdgeLinuxNodeService linuxNodeService = EdgeWebServiceProxyFactory.createProxy4LinuxD2D(new LinuxNodeServiceImpl(), IEdgeLinuxNodeService.class, this);
	private IEdgeLinuxPlanService linuxPlanService = EdgeWebServiceProxyFactory.createProxy4LinuxD2D(new LinuxPlanManagmentImpl(), IEdgeLinuxPlanService.class, this);
	private IActivityLogService logService = EdgeWebServiceProxyFactory.createProxy4D2D(new ActivityLogServiceImpl(), IActivityLogService.class, this);
	private IJobHistroryService jobHistoryService = EdgeWebServiceProxyFactory.createProxy4D2D(new JobHistoryServiceImpl(), IJobHistroryService.class, this);
	private IEdgeConfigurationService configService = new ConfigurationServiceImpl(DBConfigFilePath, cmConfigurationFilePath, (IBaseService)this);
	private IEdgeLicenseService licenseServiceNew = EdgeWebServiceProxyFactory.createProxy4D2D(new LicenseServiceImplWrapper(), IEdgeLicenseService.class, this);
	private AERPWebServiceImpl aerpWebServiceImpl = new AERPWebServiceImpl();
	private IEdgeCommonService commonService = EdgeWebServiceProxyFactory.createProxy4D2D(new EdgeCommonServiceImpl(), IEdgeCommonService.class, this);
	
	private IVSphereService vSphereService = EdgeWebServiceProxyFactory.createProxy4D2D(new VSphereServiceImpl(), IVSphereService.class, this);
	private IRPSNodeService rpsNodeService= EdgeWebServiceProxyFactory.createProxy4RPS(new RPSNodeServiceImpl(this), IRPSNodeService.class, this);
	private IEdgeRPSRegService rpsRegService = EdgeWebServiceProxyFactory.createProxy4RPS(new EdgeRPSRegServiceImpl(), IEdgeRPSRegService.class, this);
	private IRPSDataStoreService rpsDataStoreService = EdgeWebServiceProxyFactory.createProxy4RPS(new RPSDataStoreServiceImpl(), IRPSDataStoreService.class, this);
	private IProductDeployService deployService = ProductDeployServiceImpl.getInstance();
	// MSP support
	private IMspCustomerService mspCustomerService = EdgeWebServiceProxyFactory.createProxy(new MspCustomerServiceImpl(), IMspCustomerService.class, this);
	private IMspPlanService mspPlanService = EdgeWebServiceProxyFactory.createProxy(new MspPlanServiceImpl(), IMspPlanService.class, this);
	private IMspNodeService mspNodeService = EdgeWebServiceProxyFactory.createProxy4RPS(new MspNodeServiceImpl(), IMspNodeService.class, this);
	private boolean localCheckSession = false;

	// APM support
	private IEdgeApmForD2D apm4D2DService = EdgeWebServiceProxyFactory.createProxy(PatchManager.getInstance(), IEdgeApmForD2D.class, this);
	private IEdgeApmForEdge apm4EdgeService = EdgeWebServiceProxyFactory.createProxy(PatchManager.getInstance(), IEdgeApmForEdge.class, this);

	//InstantVM
	private IInstantVMService instantVMService = EdgeWebServiceProxyFactory.createProxy4D2D(new InstantVMServiceImpl(this), IInstantVMService.class, this);

	//recovery point manager;
	private IRecoveryPointService recoveryPointService = EdgeWebServiceProxyFactory.createProxy4D2D( new RecoveryPointServiceImpl(), IRecoveryPointService.class, this );

	//share folder management 
	private IShareFolderManagementService shareFolderService = EdgeWebServiceProxyFactory.createProxy4D2D( new ShareFolderManageServiceImpl( recoveryPointService ), IShareFolderManagementService.class, this );

	// edge service for D2D
	private IEdgeD2DService d2dService = EdgeWebServiceProxyFactory.createProxy(new D2DEdgeServiceImpl(), IEdgeD2DService.class);
	private IEdgeNotify d2dNotifyService = EdgeWebServiceProxyFactory.createProxy(new EdgeNotifyServiceImpl(), IEdgeNotify.class);
	private ID2DChangeProtocolNotify d2dChangeProtocolService = EdgeWebServiceProxyFactory.createProxy(new EdgeNotifyServiceImpl(), ID2DChangeProtocolNotify.class);
	private IEdgeD2DJobService d2dJobService = EdgeWebServiceProxyFactory.createProxy(new D2DEdgeServiceImpl(), IEdgeD2DJobService.class);
	private IEdgeLicense d2dLicenseService = EdgeWebServiceProxyFactory.createProxy(new LicenseServiceImplWrapper(), IEdgeLicense.class);
	private IEdgeVSphereService d2dVSphereService = EdgeWebServiceProxyFactory.createProxy(new VSphereServiceImpl(), IEdgeVSphereService.class);
	
	private IDiscoveryService discoveryService = new DiscoveryServiceImpl();
	private IStorageApplianceService infrastructureService = new StorageApplianceServiceImpl(); 
	private IEdgeGatewayService gatewayService = EdgeWebServiceProxyFactory.createProxy(
			new EdgeGatewayBean(), IEdgeGatewayService.class, null);
	
	private IEdgeVCMService edgeVCMService = EdgeWebServiceProxyFactory.createProxy4D2D(new VCMServiceImpl(), IEdgeVCMService.class, this);
	private IPolicyManagementService policyManagementService = EdgeWebServiceProxyFactory.createProxy4D2D(new PolicyManagementServiceImpl(), IPolicyManagementService.class, this);
	private ICloudAccountService cloudService = new CloudAccountServiceImpl();
	
	private IActionCenter actionCenter = EdgeWebServiceProxyFactory.createProxy( ActionCenter.getInstance(), IActionCenter.class, null );
	
	public EdgeWebServiceImpl() {}

	
    public synchronized void setWebServiceContext( WebServiceContext context )
    {
    	this.context = context;
    }

	public void setD2dService(IEdgeD2DService d2dService) {
		this.d2dService = d2dService;
	}

	public void setD2dNotifyService(IEdgeNotify d2dNotifyService) {
		this.d2dNotifyService = d2dNotifyService;
	}

	public void setD2dChangeProtocolService(ID2DChangeProtocolNotify d2dChangeProtocolService) {
		this.d2dChangeProtocolService = d2dChangeProtocolService;
	}

	public void setD2dJobService(IEdgeD2DJobService d2dJobService) {
		this.d2dJobService = d2dJobService;
	}

	public void setD2dLicenseService(IEdgeLicense d2dLicenseService) {
		this.d2dLicenseService = d2dLicenseService;
	}

	public void setD2dVSphereService(IEdgeVSphereService d2dVSphereService) {
		this.d2dVSphereService = d2dVSphereService;
	}
	
	public void setProductDeployService( IProductDeployService productDeployService )
	{
		this.deployService = productDeployService;
	}
	
	//only used in EdgeWebUI invocation, where it does not use WebService
	private volatile HttpSession session;
    
	@NonSecured
	@NotPrintAttribute
    public HttpSession getSession() {
		if(session != null) return session;
		else if(context!=null){
    		Object requestProperty = context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
    		if (requestProperty != null	&& requestProperty instanceof HttpServletRequest) {
    			HttpServletRequest request = (HttpServletRequest) requestProperty;
    			return request.getSession(true);
    		}
    		return null;
    	}
		return null;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}
	
	@Override
	public void checkSession() throws EdgeServiceFault {
		if(localCheckSession)
			return;
		HttpSession session = this.getSession();
		
		if (session == null || (session.getAttribute(CommonUtil.STRING_SESSION_USERNAME) == null && session.getAttribute(CommonUtil.STRING_SESSION_UUID) == null))
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_NOT_LOGIN, "Not login");
	}
	
	@Override
	@NonSecured
	public int validateUser(String username, @NotPrintAttribute String password, String domain)
			throws EdgeServiceFault {
		if (StringUtil.isEmptyOrNull(username)) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_UsernameRequired, "Username required");
		}
		
		if(StringUtil.isEmptyOrNull(domain)) {
			try {
				domain = InetAddress.getLocalHost().getHostName().toUpperCase();
			} catch (UnknownHostException e) {
				domain = "localhost";
			}
		} else {
			// regexpress valid userName
			String pattenStr = "^[^\\\\/\"\\[\\]<>\\+=;,?*@]+$";
			Pattern userPattern = Pattern.compile(pattenStr);		
			if (!userPattern.matcher(username).matches()) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Login_WrongCredential, "Invalid user credentials !");
			}
		}
		
		NativeFacade nativeCode = new NativeFacadeImpl();
		int res = nativeCode.validateUser(username, password, domain);
		
		HttpSession session = this.getSession();
		
		session.setAttribute(CommonUtil.STRING_SESSION_USERNAME, username);
		session.setAttribute(CommonUtil.STRING_SESSION_PASSWORD, password);
		session.setAttribute(CommonUtil.STRING_SESSION_DOMAIN, domain);
		
		DeployTargetDetail.localAdmin = username;
		DeployTargetDetail.localAdminPassword = password;
		DeployTargetDetail.localDomain = domain;
		
		return res;
	}

	@Override
	public boolean InvokeGetSrmInfo(int hostID, String protocol,
			String host, int port, int command) throws EdgeServiceFault {
		return SrmServiceImpl.InvokeGetSrmInfo(hostID, protocol,host,port,command);
	}

	@Override
	public boolean IsSrmProbeDone() {
		return SrmServiceImpl.IsProbeDone();
	}

	@Override
	public void SrmProbeNow() {
		SrmServiceImpl.ProbeNow();
	}

	@Override
	public void SrmProbeNodes(List<Integer> nodesIDList) {
		SrmServiceImpl.ProbeNodes(nodesIDList);
	}

	@Override
	public void NodeDeleteProbeNodes(List<Integer> nodesIDList) {
		NodeDeleteServiceImpl.deleteNodes(nodesIDList);
	}

	@Override
	public int D2DSyncXML(long edgeTaskId, String initFileName, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault
	{
		//return d2dService.D2DSyncXML(edgeTaskId, initFileName, uuid, cleanFlag);
		return -1;
	}

	@Override
	public int D2DSyncActiveLogXML(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault
	{
		//return d2dService.D2DSyncActiveLogXML(edgeTaskId, xmlContent, uuid, cleanFlag);
		return -1;
	}

	@Override
	public void EdgeD2DReSync(int[] d2dHostId) throws EdgeServiceFault	{
		EdgeD2DReSyncServiceImpl resync = new EdgeD2DReSyncServiceImpl();

		//resync.EdgeD2DReSync(d2dHostId);
	}

	@NonSecured
	@Override
	public String getBackupConfigurationXML(@NotPrintAttribute String afguid, String buildNumber,
			String majorVersion, String minorVersion) throws EdgeServiceFault {
		//return d2dService.getBackupConfigurationXML(afguid, buildNumber, majorVersion, minorVersion);
		return null;
	}
	@NonSecured
	@Override
	public int reportBackupConfigurationXML(@NotPrintAttribute String afguid, String buildNumber,
			String majorVersion, String minorVersion,
			String backupConfigurationXML) throws EdgeServiceFault {
		//return d2dService.reportBackupConfigurationXML(afguid, buildNumber, majorVersion, minorVersion, backupConfigurationXML);
		return -1;
	}

	@Override
	public ASBUSetting getASBUSetting(int branchID) throws EdgeServiceFault {
		return nodeService.getASBUSetting(branchID);
	}

	@Override
	public D2DSetting getD2DSetting(int branchID) throws EdgeServiceFault {
		return nodeService.getD2DSetting(branchID);
	}

	@Override
	public ASBUSetting getGlobalASBUSetting() throws EdgeServiceFault{
		return nodeService.getGlobalASBUSetting();
	}

	@Override
	public D2DSetting getGlobalD2DSetting() throws EdgeServiceFault {
		return nodeService.getGlobalD2DSetting();
	}

	@Override
	public SRMSetting getGlobalSRMSetting() throws EdgeServiceFault {
		return nodeService.getGlobalSRMSetting();
	}

	@Override
	public NodeDeleteSetting getGlobalNodeDeleteSetting() throws EdgeServiceFault {
		return nodeService.getGlobalNodeDeleteSetting();
	}

	@Override
	public List<ServerInfo> getServers() {
		//return nodeService.getServers();
		return null;
	}

	@Override
	public void saveASBUSetting(int branchID, ASBUSetting setting) throws EdgeServiceFault {
		nodeService.saveASBUSetting(branchID, setting);
	}

	@Override
	public void saveD2DSetting(int branchID, D2DSetting setting) throws EdgeServiceFault {
		nodeService.saveD2DSetting(branchID, setting);
	}

	@Override
	public void saveGlobalASBUSetting(ASBUSetting setting) throws EdgeServiceFault {
		nodeService.saveGlobalASBUSetting(setting);
	}

	@Override
	public void saveGlobalD2DSetting(D2DSetting setting) throws EdgeServiceFault {
		nodeService.saveGlobalD2DSetting(setting);
	}

	@Override
	public void saveGlobalSRMSetting(SRMSetting setting) throws EdgeServiceFault {
		nodeService.saveGlobalSRMSetting(setting);
	}

	@Override
	public void saveGlobalNodeDeleteSetting (NodeDeleteSetting setting) throws EdgeServiceFault {
		nodeService.saveGlobalNodeDeleteSetting(setting);
	}

	@Override
	public String discoverNodesFromAD(DiscoveryOption[] options) throws EdgeServiceFault{
		return nodeService.discoverNodesFromAD(options);
	}

	@Override
	public void addADSource(DiscoveryOption option) throws EdgeServiceFault {
		nodeService.addADSource(option);
	}
	
	@Override
	public int addADSourceforWizard(DiscoveryOption option) throws EdgeServiceFault {
		return nodeService.addADSourceforWizard(option);
	}

	@Override
	public void deleteADSource(int id) throws EdgeServiceFault {
		nodeService.deleteADSource(id);
	}

	@Override
	public List<DiscoveryOption> getADSourceList() throws EdgeServiceFault {
		return nodeService.getADSourceList();
	}

	@Override
	public void updateADSource(DiscoveryOption option) throws EdgeServiceFault {
		nodeService.updateADSource(option);
	}

	@Override
	public void cancelDiscovery() throws EdgeServiceFault{
		nodeService.cancelDiscovery();
	}

	@Override
	@NotPrintAttribute
	public DiscoveryMonitor getDiscoveryMonitor() throws EdgeServiceFault {
		return nodeService.getDiscoveryMonitor();
	}

	@Override
	public AdminAccountValidationResult validateAdminAccount(GatewayId gatewayId, String computerName, String userName,@NotPrintAttribute String password) throws EdgeServiceFault {
		return nodeService.validateAdminAccount(gatewayId, computerName, userName, password);
	}

	@Override
	public List<AdminAccountValidationResultWithMessage> validateAdminAccountList(
			List<Node> nodeList) throws EdgeServiceFault {
		return nodeService.validateAdminAccountList(nodeList);
	}
	
	@Override
	public String ConnectARCserve(GatewayEntity gateway, String strARCServer, String strUser,
			String strPassword, ABFuncAuthMode mode, int port, Protocol  protocol) throws EdgeServiceFault
    {
		String sessionNo = null;
		synchronized(ABFuncServiceImpl.class)
		{
			ABFuncServiceImpl funcimpl = new ABFuncServiceImpl(strARCServer, port);
			sessionNo = funcimpl.ConnectARCserve(gateway, strUser, strPassword, mode);
			EdgeCommonUtil.saveARCserveSessionNo( this.getSession(), sessionNo, funcimpl);
		}

		return sessionNo;
	}
	
	@NonSecured
	public String ConnectARCserveForDeleteNodeDataJob(GatewayEntity gateway, String strARCServer, String strUser,
			String strPassword, ABFuncAuthMode mode, int port, Protocol  protocol) throws EdgeServiceFault
    {
		String sessionNo = null;
		synchronized(ABFuncServiceImpl.class)
		{
			ABFuncServiceImpl funcimpl = new ABFuncServiceImpl(strARCServer, port);
			sessionNo = funcimpl.ConnectARCserve(gateway, strUser, strPassword, mode);
			EdgeCommonUtil.saveARCserveSessionNo( this.getSession(), sessionNo, funcimpl);
		}

		return sessionNo;
	}

	@Override
	public ABFuncServerType GetServerType(String strSessionNo) throws EdgeServiceFault
	{
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.GetServerType(strSessionNo);
		}
		else
		{
			return null;
		}

	}

	@Override
	public String MarkArcserveManageStatus(String strSessionNo, String strEdgeServerName, Boolean bOverwrite, ABFuncManageStatus status) throws EdgeServiceFault
	{
		String arcserveId = "";
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			arcserveId = funcimpl.MarkArcserveManageStatus(strSessionNo, strEdgeServerName, bOverwrite, status);
		}
		return arcserveId;
	}
	
	@NonSecured
	public String MarkArcserveManageStatusForDeleteNodeDataJob(String strSessionNo, String strEdgeServerName, Boolean bOverwrite, ABFuncManageStatus status) throws EdgeServiceFault
	{
		String arcserveId = "";
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			arcserveId = funcimpl.MarkArcserveManageStatus(strSessionNo, strEdgeServerName, bOverwrite, status);
		}
		return arcserveId;
	}

	@Override
	public ABFuncManageStatus GetArcserveManageStatus(String strSessionNo, String strEdgeServerName) throws EdgeServiceFault
	{
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.GetArcserveManageStatus(strSessionNo, strEdgeServerName);
		}
		else
		{
			return null;
		}

	}
	
	@Override
	public String getGDBServer(String strSessionNo) throws EdgeServiceFault
	{
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.getGDBServer(strSessionNo);
		}
		else
		{
			return null;
		}
	}

	@Override
	public ArrayOfstring getArcserveVersionInfo(String strSessionNo)
			throws EdgeServiceFault {
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.getArcserveVersionInfo(strSessionNo);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Boolean IsArcserveBranch(String strSessionNo)
			throws EdgeServiceFault {
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.IsArcserveBranch(strSessionNo);
		}
		else
		{
			return null;
		}
	}
	@Override
	public String GetManagedEdgeServer(String strSessionNo)
	throws EdgeServiceFault {
		ABFuncServiceImpl funcimpl = EdgeCommonUtil.getARCserveSessionNo( this.getSession(),strSessionNo);
		if(null != funcimpl)
		{
			return funcimpl.GetManagedEdgeServer(strSessionNo);
		}
		else
		{
			return null;
		}
	}

	@Override
	public long addLog(ActivityLog log) throws EdgeServiceFault {
		return logService.addLog(log);
	}

	@Override
	public DBConfigInfo getDatabaseConfiguration() throws EdgeServiceFault {
		return configService.getDatabaseConfiguration();
	}
	
	@Override
	public PreferenceConfigInfo getPreferenceConfiguration() throws EdgeServiceFault {
		return configService.getPreferenceConfiguration();
	}

	@Override
	public void setDatabaseConfiguration(DBConfigInfo dbConfig)
			throws EdgeServiceFault	{
		configService.setDatabaseConfiguration(dbConfig);
	}

	@Override
	public void setPreferenceConfiguration(PreferenceConfigInfo pfConfig)
			throws EdgeServiceFault	{
		configService.setPreferenceConfiguration(pfConfig);
	}


	@Override
	public Boolean testSQLServer(String serverName, String instance, int port,
			String userName, @NotPrintAttribute String password) throws EdgeServiceFault {
		return configService.testSQLServer(serverName, instance, port,
				userName, password);
	}



	@Override
	public void setNodesAsManaged(int[] idArray) throws EdgeServiceFault {
		nodeService.setNodesAsManaged(idArray);
	}

	// ////////////////////////////////////////////////////////////////////////
	// ARCserve Sync Service
	@Override
	public Boolean InvokeFullARCserveSync(int[] rhostID) throws EdgeServiceFault {
		SyncArcserveServiceMgr syncMgr = new SyncArcserveServiceMgr();
		return syncMgr.InvokeSync(rhostID, true, false);
	}

	@Override
	public Boolean InvokeIncARCserveSync(int[] rhostID, boolean bIsAutoConvert) throws EdgeServiceFault {
		SyncArcserveServiceMgr syncMgr = new SyncArcserveServiceMgr();
		return syncMgr.InvokeSync(rhostID, false, bIsAutoConvert);
	}

	@Override
	public Boolean unRegisterBranchServer(int rhostID, String serverName)
			throws EdgeServiceFault {
		return SyncArcserveServiceImpl.UnRegisterBranchServer(rhostID, serverName);
	}

	@Override
	public List<GDBBranchInfo> enumBranchServer(int rhostID) throws EdgeServiceFault {
		return SyncArcserveServiceImpl.enumBranchServer(rhostID);
	}

	@Override
	public Integer[] GetInvalidHost(int[] rhostID) throws EdgeServiceFault
	{
		CheckSyncProperties syncProperty = new CheckSyncProperties();
		return syncProperty.CheckSyncStatus(rhostID);
	}
	// End ARCserve Sync Service
	//////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public List<NodeGroup> getNodeGroups() throws EdgeServiceFault {
		return nodeService.getNodeGroups();
	}

	@Override
	public List<Node> getNodesByGroup(int gatewayId, int groupID, int groupType) throws EdgeServiceFault {
		return nodeService.getNodesByGroup(gatewayId, groupID, groupType);
	}
	
	@Override
	public List<Node> getHBBUProxy(int gatewayId) throws EdgeServiceFault {
		return nodeService.getHBBUProxy(gatewayId);
	}

	//////////////////////////////////////////////////////////////////////////
	// Policy Management

	@Override
	public List<ItemOperationResult> deleteUnifiedPolicies( List<Integer> idList )
		throws EdgeServiceFault
	{
		return policyManagementService.deleteUnifiedPolicies( idList );
	}
	
	@Override
	public ParsedBackupPolicy getParsedBackupPolicy( int policyId )
		throws EdgeServiceFault
	{
		return policyManagementService.getParsedBackupPolicy( policyId );
	}

	@Override
	public List<Node> getNodesByPolicy( int policyId ) throws EdgeServiceFault
	{
		return policyManagementService.getNodesByPolicy( policyId );
	}

	// End of Policy Management
	//////////////////////////////////////////////////////////////////////////


	@Override
	public void UpdateRegInfoToD2D(ConnectionContext connectionContext, int d2dHostId, boolean forceFlag)throws EdgeServiceFault {
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();

		regService.UpdateRegInfoToD2D(connectionContext, d2dHostId, forceFlag);
	}

	@Override
	public RemoteNodeInfo queryRemoteNodeInfo(GatewayId gatewayId, int nodeId, String hostname, String username,
			@NotPrintAttribute String password, String protocol, int port) throws EdgeServiceFault {
		return nodeService.queryRemoteNodeInfo(gatewayId, nodeId, hostname, username, password, protocol, port);
	}
	
	@Override
	public RemoteNodeInfo updateRemoteNodeInfo(int hostId, String hostname, String username,
			@NotPrintAttribute String password) throws EdgeServiceFault {
		return nodeService.updateRemoteNodeInfo(hostId, hostname, username, password);
	}

	@Override
	public int createNewNodeGroup(GatewayId gatewayId, NodeGroup group, int[] assigedNodes) throws EdgeServiceFault{
		return nodeService.createNewNodeGroup(gatewayId, group, assigedNodes);
	}
	
	@Override
	public void deleteNode(int id, boolean keepCurrentSettings) throws EdgeServiceFault {
		nodeService.deleteNode(id, keepCurrentSettings);
	}

	@Override
	public void deleteNodeGroup(int groupID) throws EdgeServiceFault {
		nodeService.deleteNodeGroup(groupID);
	}

	@Override
	public void updateNewNodeGroup(NodeGroup group, int[] assigedNodes) throws EdgeServiceFault{
		nodeService.updateNewNodeGroup(group, assigedNodes);
	}

	@Override
	public void RemoveRegInfoFromD2D(int d2dHostId, boolean forceFlag)throws EdgeServiceFault {
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();

		regService.RemoveRegInfoFromD2D(d2dHostId, forceFlag);
	}

	@NonSecured
	public void RemoveRegInfoFromD2DForDeleteNodeDataJob(int d2dHostId, boolean forceFlag)throws EdgeServiceFault {
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();

		regService.RemoveRegInfoFromD2D(d2dHostId, forceFlag);
	}

	@Override
	public AutoDiscoverySetting getAutoDiscoverySettings(AutoDiscoverySetting.SettingType settingType)
			throws EdgeServiceFault {
		return nodeService.getAutoDiscoverySettings(settingType);
	}

	@Override
	public void saveAutoDiscoverySettings(AutoDiscoverySetting settings, AutoDiscoverySetting.SettingType settingType)
			throws EdgeServiceFault {
		nodeService.saveAutoDiscoverySettings(settings, settingType);

	}

	@Override
	public int D2DSyncJobStatus(String initFileName, @NotPrintAttribute String uuid) throws EdgeServiceFault {
		return d2dService.D2DSyncJobStatus(initFileName, uuid);
	}

	/* Deploy */
	@Override
	public boolean isLocalHost(String host) throws EdgeServiceFault {
		return nodeService.isLocalHost(host);
	}

	@Override
	public void submitRemoteDeploy( List<DeployTargetDetail> deployTargets )
			throws EdgeServiceFault {
		deployService.submitRemoteDeploy(deployTargets);
	}

	@Override
	public String getLicenseText()throws EdgeServiceFault{
		return nodeService.getLicenseText();
	}

	/* End of Deploy */
	
	@Override
	public void importNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault {
		nodeService.importNodes(nodes, type);
	}

	@Override
	public void importVMs(DiscoveryESXOption esxOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault {
		nodeService.importVMs(esxOption, vms, type, addEsxToADList);
	}

	@Override
	public int promoteEmailToEdge(List<CommonEmailInformation> infoList) throws EdgeServiceFault {
		return d2dService.promoteEmailToEdge(infoList);
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(DiscoveryESXOption option)
			throws EdgeServiceFault {
		return nodeService.getVMVirtualMachineList(option);
	}

	@Override
	@NonSecured
	public String getDefaultUser() throws EdgeServiceFault{

		logger.debug("EdgeWebServiceImpl.getDefaultUser() - start");
		
		EdgeRegistryInfo regInfo = CommonUtil.getApplicationRegistryInfo();
		String defaultUser = regInfo.getAdminUser();
		if(defaultUser == null || defaultUser.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Open the Windows Registry Failed");
		} else {
			logger.debug("EdgeWebServiceImpl.getDefaultUser() - end");
			return defaultUser;
		}
	}

	@Override
	public NodeDetail getNodeDetailInformation(int hostID) throws EdgeServiceFault {
		return this.nodeService.getNodeDetailInformation(hostID);
	}

	@Override
	public void markNodeAsManaged(NodeRegistrationInfo nodeInfo, boolean overwrite) throws EdgeServiceFault{
		nodeService.markNodeAsManaged(nodeInfo, overwrite);
	}

	@Override
	public int markMultiNodesAsManaged(List<Integer> nodeIds, boolean overWrite)
			throws EdgeServiceFault {
		return nodeService.markMultiNodesAsManaged(nodeIds, overWrite);
	}
	
	@Override
	public NodeManageResult queryRpsManagedStatus(
			NodeRegistrationInfo nodeRegistrationInfo) throws EdgeServiceFault {
		return rpsNodeService.queryRpsManagedStatus(nodeRegistrationInfo);
	}
	
	@Override
	public void markRpsNodeAsManagedById(int rpsNodeId, boolean overwrite) throws EdgeServiceFault{
		nodeService.markRpsNodeAsManagedById(rpsNodeId, overwrite);
	}

	@Override
	public Node[] getNodesByGroupAndType(int groupID, int[] type)
			throws EdgeServiceFault {
		return nodeService.getNodesByGroupAndType(groupID, type);
	}

	@Override
	public List<Node> getNodesByGDBId(int GDBId) throws EdgeServiceFault {
		return nodeService.getNodesByGDBId(GDBId);
	}

	@Override
	public List<Node> getDeletedNodes() throws EdgeServiceFault {
		return nodeService.getDeletedNodes();
	}

	// APM integration ----- start -------
	@Override
	public AutoUpdateSettings GetUpdateSettings() throws EdgeServiceFault {
		return apm4EdgeService.GetEdgeUpdateSettings();
	}
	@Override
	public void SetUpdateSettings(AutoUpdateSettings in_UpdateConfig) throws EdgeServiceFault{
		apm4EdgeService.SetEdgeUpdateSettings(in_UpdateConfig);
	}
	@Override
	public VSphereProxyInfo getUUIDforD2DLogin(int hostid) throws EdgeServiceFault {
		return nodeService.getUUIDforD2DLogin(hostid);
	}
	
	@Override
	public VSphereProxyInfo getRps4RemoteNode(int nodeId) throws EdgeServiceFault {
		return nodeService.getRps4RemoteNode(nodeId);
	}

	@Override
	public AutoUpdateSettings GetEdgeUpdateSettings() throws EdgeServiceFault {
		return apm4EdgeService.GetEdgeUpdateSettings();
	}

	@Override
	public void SetEdgeUpdateSettings(AutoUpdateSettings updateConfig)
			throws EdgeServiceFault {
		apm4EdgeService.SetEdgeUpdateSettings(updateConfig);
	}

	@Override
	public AutoUpdateSettings testDownloadServerConnnectionEdge(
			AutoUpdateSettings updateSettings) throws EdgeServiceFault {
		return apm4EdgeService.testDownloadServerConnnectionEdge(updateSettings);
	}

	//added by cliicy.luo to add Hotfix menu-item
	@Override
	public AutoUpdateSettings testDownloadBIServerConnnectionEdge(
			AutoUpdateSettings updateSettings) throws EdgeServiceFault {
		return apm4EdgeService.testDownloadBIServerConnnectionEdge(updateSettings);
	}
	
	@Override
	public void SetEdgeHotfixSettings(AutoUpdateSettings updateConfig)
			throws EdgeServiceFault {
		apm4EdgeService.SetEdgeHotfixSettings(updateConfig);
	}
	
	@Override
	public PatchInfoEdge[] getHotfixInfoesEdge() throws EdgeServiceFault {
		return apm4EdgeService.getHotfixInfoesEdge();
	}
	
	@Override
	public PatchInfoEdge[] getHotfix_Edgine() throws EdgeServiceFault {
		return apm4EdgeService.getHotfix_Edgine();
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	
	@Override
	public PMResponse SubmitAPMRequestD2D(int RequestType)
			throws EdgeServiceFault{
		logger.debug("oooo [EdgeWebServiceImpl] will return apm4D2DService.SubmitAPMRequestD2D.");
		return apm4D2DService.SubmitAPMRequestD2D(RequestType);
	}

	@Override
	public int getPatchManagerStatusEdge() throws EdgeServiceFault {
		return apm4EdgeService.getPatchManagerStatusEdge();
	}

	@Override
	public PatchInfoEdge[] getPatchInfoesEdge() throws EdgeServiceFault {
		return apm4EdgeService.getPatchInfoesEdge();
	}


	@Override
	public int installPatchEdge() throws EdgeServiceFault {
		return apm4EdgeService.installPatchEdge();
	}

	// APM integration -----  end  -------

	@Override
	public RemoteNodeInfo tryConnectD2D(GatewayId gatewayId, String d2dProtocol, String d2dHost,
			int d2dPort, String d2dUserName, String d2dPassword)
			throws EdgeServiceFault {
		return nodeService.tryConnectD2D(gatewayId, d2dProtocol, d2dHost, d2dPort, d2dUserName, d2dPassword);
	}

	@Override
	public RegistrationNodeResult registerNode(boolean failedReadRemoteRegistry,
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {

		return nodeService.registerNode(failedReadRemoteRegistry, registrationNodeInfo);
	}

	@Override
	public String[] updateNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo nodeInfo) throws EdgeServiceFault {
		return nodeService.updateNode(failedReadRemoteRegistry,nodeInfo);
	}

	@Override
	@NonSecured
	public EdgeVersionInfo getVersionInformation() throws EdgeServiceFault {
		//return commonService.getVersionInformation();
		return new EdgeCommonServiceImpl().getVersionInformation();
	}

	@Override
	public int D2DEndSync(boolean fullSyncMode, long edgeTaskId, @NotPrintAttribute String uuid, boolean result) throws EdgeServiceFault {
		//return d2dService.D2DEndSync(fullSyncMode, edgeTaskId, uuid, result);
		return -1;
	}

	@Override
	public long D2DStartSync(boolean fullSyncMode, @NotPrintAttribute String uuid) throws EdgeServiceFault {
		//return d2dService.D2DStartSync(fullSyncMode, uuid);
		return -1;
	}

	@Override
	public CmInfo getCmConfiguration() throws EdgeServiceFault {
		return configService.getCmConfiguration();
	}

	@Override
	public int setCmConfiguration(CmInfo cmInfo) throws EdgeServiceFault {
		configService.setCmConfiguration(cmInfo);
		return 0;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<ItemOperationResult> redeployPolicyToNodes(
		List<Integer> nodeIdList, int policyType, int policyId )
		throws EdgeServiceFault
	{
		return policyManagementService.redeployPolicyToNodes(
			nodeIdList, policyType, policyId );
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void redeployPolicies( int policyType, List<Integer> policyIdList )
		throws EdgeServiceFault
	{
		policyManagementService.redeployPolicies( policyType, policyIdList );
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void doPolicyDeploymentNow() throws EdgeServiceFault
	{
		policyManagementService.doPolicyDeploymentNow();
	}

	@Override
	public int D2DSyncVMInfo(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault {
		//return d2dService.D2DSyncVMInfo(edgeTaskId, xmlContent, uuid, cleanFlag);
		return -1;
	}
	
	@Override
	public int D2DSyncTempVMHost(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean isFullsync) throws EdgeServiceFault {
		//return d2dService.D2DSyncTempVMHost(edgeTaskId, xmlContent, uuid, isFullsync);
		return -1;
	}
	
	@Override
	public List<NodeGroup> getNodeESXGroups(int gatewayId) throws EdgeServiceFault {
		return nodeService.getNodeESXGroups(gatewayId);
	}
	
	@Override
	public List<NodeGroup> getNodeGroupsByLayer(int gatewayid, NodeGroup parentGroup) throws EdgeServiceFault {
		return nodeService.getNodeGroupsByLayer(gatewayid, parentGroup);
	}

	@Override
	public Node[] getNodesESXByGroupAndType(int groupID, int[] types, int grouptype)
			throws EdgeServiceFault {
		return nodeService.getNodesESXByGroupAndType(groupID, types,grouptype);
	}

	@Override
	public void deleteNodeESXGroup(int groupID, int type)
			throws EdgeServiceFault {
		nodeService.deleteNodeESXGroup(groupID, type);
	}

	@Override
	public void deleteNodeESXGroupAndNodes(int groupID, int type)
			throws EdgeServiceFault {
		nodeService.deleteNodeESXGroupAndNodes(groupID, type);
	}

	@Override
	public PagingResult<DiscoveredNode> getDiscoveredNodes(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return nodeService.getDiscoveredNodes(filter, config);
	}

	@Override
	public void hideDiscoverdNodes(int[] nodeIds) throws EdgeServiceFault {
		nodeService.hideDiscoverdNodes(nodeIds);
	}

	@Override
	public int setDatabaseConfigurationWithSchemaCreation(
			DBConfigInfo dbConfig, boolean needCreateDbSchema)
			throws EdgeServiceFault {
		configService.setDatabaseConfigurationWithSchemaCreation(dbConfig,
				needCreateDbSchema);
		return 0;
	}

	@Override
	public int D2DSyncArchive(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault {
		return d2dService.D2DSyncArchive(edgeTaskId, xmlContent, uuid, cleanFlag);
	}

	@Override
	public void addEsxSource(DiscoveryESXOption esxOption)
			throws EdgeServiceFault {
		nodeService.addEsxSource(esxOption);
	}

	@Override
	public void cancelEsxDiscovery() throws EdgeServiceFault {
		nodeService.cancelEsxDiscovery();
	}

	@Override
	public void deleteEsxSource(int id) throws EdgeServiceFault {
		nodeService.deleteEsxSource(id);
	}

	@Override
	public String discoverNodesFromESX(DiscoveryESXOption[] esxOptions)
			throws EdgeServiceFault {
		return nodeService.discoverNodesFromESX(esxOptions);
	}
	
	@Override
	public PagingResult<DiscoveredVM> getDiscoveredVMs(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return nodeService.getDiscoveredVMs(filter, config);
	}

	@Override
	public DiscoveryMonitor getEsxDiscoveryMonitor() throws EdgeServiceFault {
		return nodeService.getEsxDiscoveryMonitor();
	}

	@Override
	public List<DiscoveryESXOption> getEsxSourceList() throws EdgeServiceFault {
		return nodeService.getEsxSourceList();
	}

	@Override
	public void hideDiscoverdVMs(int[] vmIds) throws EdgeServiceFault {
		nodeService.hideDiscoverdVMs(vmIds);
	}

	@Override
	public void updateEsxSource(DiscoveryESXOption esxOption)
			throws EdgeServiceFault {
		nodeService.updateEsxSource(esxOption);
	}

	@Override
	public VMInfo getVMNodesFromVSphere() throws EdgeServiceFault {
		return vSphereService.getVMNodesFromVSphere();
	}

	@Override
	public int D2DSyncVCM(long edgeTaskId, String xmlContent, @NotPrintAttribute String uuid, boolean cleanFlag) throws EdgeServiceFault {
		return d2dService.D2DSyncVCM(edgeTaskId, xmlContent, uuid, cleanFlag);
	}

	@Override
	public EmailServerSetting getEmailServerSetting() throws EdgeServiceFault{
		return configService.getEmailServerSetting();
	}
	
	@Override
	public void saveEmailServerTemplateSetting(EmailServerSetting setting) throws EdgeServiceFault{
		
		configService.saveEmailServerTemplateSetting(setting);
		
	}
	
	@Override
	public void deleteEmailServerTemplateSetting(int featureId) throws EdgeServiceFault{
		
		configService.deleteEmailServerTemplateSetting(featureId);
		
	}

	@Override
	public void saveEmailServerSetting(EmailServerSetting serverSetting) throws EdgeServiceFault {
		configService.saveEmailServerSetting(serverSetting);
	}
	
	@Override
	public boolean testEmailServerTemplateSetting(EmailServerSetting serverSetting)
			throws EdgeServiceFault {
		return configService.testEmailServerTemplateSetting(serverSetting);
	}

	@Override
	public EmailServerSetting getEmailServerTemplateSetting(int featureId)
			throws EdgeServiceFault {
		return configService.getEmailServerTemplateSetting(featureId);
	}

	@Override
	public EmailTemplateSetting getEmailTemplateSetting(int featureId)
			throws EdgeServiceFault {
		return configService.getEmailTemplateSetting(featureId);
	}

	@Override
	public void saveEmailTemplateSetting(EmailTemplateSetting templateSetting)
			throws EdgeServiceFault {
		configService.saveEmailTemplateSetting(templateSetting);
	}

	@Override
	public NodeInfoList4VM getNodesWhoIsUsingPolicy4VM(int policyId)
			throws EdgeServiceFault {
		return policyManagementService.getNodesWhoIsUsingPolicy4VM(policyId);
	}
	
	@Override
	@NonSecured
	public int flashChangeToProtocol(@NotPrintAttribute String flashUUID, int toProtocol) throws EdgeServiceFault {
		return d2dChangeProtocolService.flashChangeToProtocol(flashUUID, toProtocol);
	}

	@Override
	public DiscoveryESXOption getVMNodeESXSettings(int hostID)
			throws EdgeServiceFault {
		return nodeService.getVMNodeESXSettings(hostID);
	}
	
	@Override
	public DiscoveryHyperVOption getVMNodeHyperVSettings(int hostID)
			throws EdgeServiceFault {
		return nodeService.getVMNodeHyperVSettings(hostID);
	}

	@Override
	public void saveVMNodeESXSettings(int hostID, DiscoveryESXOption esxSetting)
			throws EdgeServiceFault {
		nodeService.saveVMNodeESXSettings(hostID, esxSetting);
	}

	@Override
	@NonSecured
	public void setVMApplicationStatus(String vmInstanceUuid, ApplicationStatus appStatus) throws EdgeServiceFault {
		d2dVSphereService.setVMApplicationStatus(vmInstanceUuid, appStatus);
	}

	@Override
	public List<ESXServer> getEsxNodeList(DiscoveryESXOption esxOption)
			throws EdgeServiceFault {
		return nodeService.getEsxNodeList(esxOption);
	}

	@Override
	public EdgeAppInfo getAppInformation() throws EdgeServiceFault {
		return new EdgeCommonServiceImpl().getAppInformation();
	}

	@Override
	public boolean checkARCserveNodeManageStatus(String guid)
			throws EdgeServiceFault {
		SyncArcserveServiceMgr sasm = new SyncArcserveServiceMgr();
		return sasm.checkNodeManageStatus(guid);
	}

	@NonSecured
	@Override
	public int validateUserByUUID(String uuid) throws EdgeServiceFault {
		if (uuid == null || uuid.isEmpty())
			throw new EdgeServiceFault("UUID is required", new EdgeServiceFaultBean(EdgeServiceErrorCode.Login_UUIDRequired, "UUID is required"));

		String appUUID = CommonUtil.retrieveCurrentAppUUID();
		if (!uuid.equals(appUUID))
			throw new EdgeServiceFault("Wrong UUID", new EdgeServiceFaultBean(EdgeServiceErrorCode.Login_WrongUUID, "Wrong UUID"));

		setUuid(uuid);

		return 0;
	}
	
	protected void setUuid(String uuid) {
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(CommonUtil.STRING_SESSION_UUID, uuid);
		}
	}
	
	protected String getUuid() {
		HttpSession session = getSession();
		if (session == null) {
			return "";
		}
		
		Object uuidObject = session.getAttribute(CommonUtil.STRING_SESSION_UUID);
		if (!(uuidObject instanceof String)) {
			return "";
		}
		
		return (String) uuidObject;
	}

	@Override
	public VSphereProxyInfo getVSphereProxyInfoByHostId(int hostId) throws EdgeServiceFault {
		return this.nodeService.getVSphereProxyInfoByHostId(hostId);
	}


	@Override
	public EdgePreferenceConfigInfo getPreferenceInformation()
			throws EdgeServiceFault {
		return commonService.getPreferenceInformation();
	}

	@Override
	public List<String> getManagedVMbyProxy(String uuid) throws EdgeServiceFault {
		return d2dVSphereService.getManagedVMbyProxy(uuid);
	}

	@Override
	public void UpdateRegInfoToProxy(ProxyConnectInfo proxyConnectInfo,
			boolean forceFlag) throws EdgeServiceFault {
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();

		regService.UpdateRegInfoToProxy(proxyConnectInfo, forceFlag);	
		
	}

	@Override
	public void regEdgeToProxy(int policyType, int policyId, int hostId, boolean forceFlag)
			throws EdgeServiceFault {
		policyManagementService.regEdgeToProxy(policyType, policyId, hostId, forceFlag);
		
	}

	@Override
	public boolean isManagedByEdge(String uuid)	throws EdgeServiceFault {
		return d2dService.isManagedByEdge(uuid);
	}

	@Override
	public List<Boolean> getEULAStatus(ShowEULAModule module) throws EdgeServiceFault{
		return nodeService.getEULAStatus(module);
	}

	@Override
	public void setEULAStatus(ShowEULAModule module, List<Boolean> list) throws EdgeServiceFault{
		nodeService.setEULAStatus(module, list);
	}

	@Override
	public Account getEdgeAccount() throws EdgeServiceFault {
		return configService.getEdgeAccount();
	}

	@Override
	public void saveEdgeAccount(@NotPrintAttribute Account account) throws EdgeServiceFault {
		configService.saveEdgeAccount(account);
	}

	@Override
	public DeployD2DSettings getDeployD2DSettings() throws EdgeServiceFault
	{
		return configService.getDeployD2DSettings();
	}

	@Override
	public void saveDeployD2DSettings( DeployD2DSettings settings )
		throws EdgeServiceFault
	{
		configService.saveDeployD2DSettings( settings );
	}

	@Override
	public ApmResponse[] checkUpdateEdge() throws EdgeServiceFault {
		return apm4EdgeService.checkUpdateEdge();
	}
	
	//added by cliicy.luo to add Hotfix menu-item
	@Override
	public ApmResponse[] checkHotfixEdge() throws EdgeServiceFault {
		return apm4EdgeService.checkHotfixEdge();
	}
	//added by cliicy.luo to add Hotfix menu-item
	
	@Override
	public int checkPolicyStatus(String d2dUuid, String policyUuid, boolean justcheck)throws EdgeServiceFault{
		return d2dService.checkPolicyStatus(d2dUuid, policyUuid, justcheck);
	}

	@Override
	public void redeployPolicy2RightNodes(int policyType, int policyId,
			int hostId) throws EdgeServiceFault {
		policyManagementService.redeployPolicy2RightNodes(policyType, policyId, hostId);
		
	}

	@Override
	public NodePagingResult getNodesESXByGroupAndTypePaging(int groupID,
			int grouptype, EdgeNodeFilter nodeFilter, NodePagingConfig np)
			throws EdgeServiceFault {
		return nodeService.getNodesESXByGroupAndTypePaging(groupID, grouptype, nodeFilter, np);
	}

	@Override
	public List<ExportNode> getExportNodeList(int groupID, int grouptype,
			EdgeNodeFilter nodeFilter) throws EdgeServiceFault {
		return nodeService.getExportNodeList(groupID, grouptype, nodeFilter);
	}
	
	@Override
	public String generateExportNodeFile(List<Integer> nodeIds)
			throws EdgeServiceFault {
		return nodeService.generateExportNodeFile(nodeIds);
	}
	
	@Override
	public String generateExportNodeFileForGroup(int gatewayId, int groupType, int groupId)
			throws EdgeServiceFault {
		return nodeService.generateExportNodeFileForGroup(gatewayId, groupType, groupId);
	}
	
	@Override
	public int importNodesFromFile(String filePath) throws EdgeServiceFault {
		return nodeService.importNodesFromFile(filePath);
	}
	
	@Override
		public void backupForEDGE(int id, String hostname,
			int backupType, String value, boolean convert) throws EdgeServiceFault {
		nodeService.backupForEDGE(id, hostname, backupType, value, convert);
	}

	@Override
	public D2DBackupJobStatusInfo getBackupJobStatusById(int nodeId)  throws EdgeServiceFault{
		return nodeService.getBackupJobStatusById(nodeId);
	}

	@Override
	public List<D2DBackupJobStatusInfo> getBackupJobStatusAll(
			List<String> nodeIdList) throws EdgeServiceFault {
		return nodeService.getBackupJobStatusAll(nodeIdList);
	}
	
	@Override
	public List<FlashJobMonitor> getJobStatusInfoList(String jobStatusKey)
			throws EdgeServiceFault {
		return nodeService.getJobStatusInfoList(jobStatusKey);
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorForDashboard(int productType, int nodeId, int rpsNodeId, long jobType, long jobId, String jobUUID)
			throws EdgeServiceFault {
		return nodeService.getJobMonitorForDashboard(productType, nodeId, rpsNodeId, jobType, jobId, jobUUID);
	}

	@Override
	public boolean cancelJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault {
		return nodeService.cancelJob(nodeId, hostName, jobId);
	}
	
	@Override
	public int cancelJobById(int nodeId, String hostName, long jobId, long jobType,
			String d2dUuid, String vmInstanceUuid, boolean isCancelJobFromRPS)
			throws EdgeServiceFault {
		return nodeService.cancelJobById(nodeId, hostName, jobId, jobType, d2dUuid, vmInstanceUuid, isCancelJobFromRPS);
	}
	
	@Override
	public void cancelJobByGroup(int gatewayId, int groupId, int groupType) throws EdgeServiceFault {
		nodeService.cancelJobByGroup(gatewayId, groupId, groupType);
	}
	
	@Override
	public boolean cancelVMJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault {
		return nodeService.cancelVMJob(nodeId, hostName, jobId);
	}
	
	@Override
	public boolean cancelWaitingJob(Node node, String vmInstanceUuid) throws EdgeServiceFault {
		return nodeService.cancelWaitingJob(node, vmInstanceUuid);
	}
	
	@Override
	public void backupVM(int nodeID, int backupType, String jobName)
			throws EdgeServiceFault {
		nodeService.backupVM(nodeID, backupType, jobName);
	}
	
	@Override
	public void backupVMWithFlag(int nodeID,int backupType, String jobName,boolean convertForBackupSet)
			throws EdgeServiceFault {
		nodeService.backupVMWithFlag(nodeID, backupType, jobName, convertForBackupSet);
	}

	@Override
	public int syncBackupJobsStatus(String vmUuid, JobMonitor jobMonitor) throws EdgeServiceFault {
		return d2dService.syncBackupJobsStatus(vmUuid, jobMonitor);
	}
	
	@Override
	public int syncBackupJobsStatusAll(List<String> vmUuid, List<JobMonitor> jobMonitor)
			throws EdgeServiceFault {
		for (int i = 0; i < vmUuid.size(); i++) {
			String uuid = vmUuid.get(i);
			syncBackupJobsStatus(uuid, jobMonitor.get(i));
		}
		return 0;
	}

	@Override
	public int submitBackupJob(int gatewayId, int groupID, int groupType, int backupType, String jobName) throws EdgeServiceFault {
		return nodeService.submitBackupJob(gatewayId, groupID, groupType, backupType, jobName);
	}

	@Override
	public void deleteNodes(int[] ids, boolean keepCurrentSettings ) throws EdgeServiceFault {
		nodeService.deleteNodes(ids, keepCurrentSettings);
	}
	
	@Override
	public int backupNodesForEDGE(int[] ids, int backupType, String value)
			throws EdgeServiceFault {
		return nodeService.backupNodesForEDGE(ids, backupType, value);
	}

	@Override
	public void backupVMs(int[] nodeIDs, int backupType, String jobName)
			throws EdgeServiceFault {
		nodeService.backupVMs(nodeIDs, backupType, jobName);
	}
	
	@Override
	public void verifyVMs(int[] nodeIDs) throws EdgeServiceFault {
		nodeService.verifyVMs(nodeIDs);
	}
	
	@Override
	public void verifyVMsByInstanceUUID(List<String> instanceUUIDs) throws EdgeServiceFault {
		d2dVSphereService.verifyVMsByInstanceUUID(instanceUUIDs);
	}

	@Override
	public String queryVMHostName(int hostID) throws EdgeServiceFault {
		return nodeService.queryVMHostName(hostID);
	}

	@Override
	public void submitVerifyVMJobForGroup(int gatewayId, int groupID, int groupType)
			throws EdgeServiceFault {
		nodeService.submitVerifyVMJobForGroup(gatewayId, groupID, groupType);
	}

	@Override
	public void submitARCserveFullSyncForGroup(int groupID, int groupType)
			throws EdgeServiceFault {
		SyncArcserveServiceMgr syncMgr = new SyncArcserveServiceMgr();
		syncMgr.setServiceImpl(this);
		syncMgr.submitARCserveFullSyncForGroup(groupID, groupType);
	}

	@Override
	public void submitARCserveIncrementalSyncForGroup(int groupID, int groupType)
			throws EdgeServiceFault {
		SyncArcserveServiceMgr syncMgr = new SyncArcserveServiceMgr();
		syncMgr.setServiceImpl(this);
		syncMgr.submitARCserveIncrementalSyncForGroup(groupID, groupType);
	}

	@Override
	public void submitD2DSyncForGroup(int groupID, int groupType)
			throws EdgeServiceFault {
		EdgeD2DReSyncServiceImpl resync = new EdgeD2DReSyncServiceImpl();
		resync.setServiceImpl(this);
		resync.submitD2DSyncForGroup(groupID, groupType);
		
	}

	@Override
	public int syncConversionJobInfo(String uuid, RepJobMonitor repJobMonitor, D2DStatusInfo d2dStatusInfo) throws EdgeServiceFault {
		return d2dService.syncConversionJobInfo(uuid, repJobMonitor, d2dStatusInfo);
	}

	@Override
	public boolean cancelReplication(int nodeId, String hostName, String vmInstanceUUID) throws EdgeServiceFault {
		return nodeService.cancelReplication(nodeId, hostName, vmInstanceUUID);
	}

	@Override
	public List<RepJobMonitor4Edge> getConversionJobStatusAll(
			List<String> nodeIdList) throws EdgeServiceFault {
		return nodeService.getConversionJobStatusAll(nodeIdList);
	}

	@Override
	public RepJobMonitor4Edge getRepJobMonitorById(int nodeId)
			throws EdgeServiceFault {
		return nodeService.getRepJobMonitorById(nodeId);
	}

	@Override
	public void changeHeartBeatStatus(int[] nodeID, boolean enabled)
			throws EdgeServiceFault {
		nodeService.changeHeartBeatStatus(nodeID, enabled);
	}

	@Override
	public void changeAutoOfflieCopyStatus(int[] nodeID, boolean enabled, boolean forceSmartCopy)
			throws EdgeServiceFault {
		nodeService.changeAutoOfflieCopyStatus(nodeID, enabled, forceSmartCopy);
	}

	@Override
	public void changeHeartBeatStatusForGroup(int groupID, int groupType,
			boolean enabled) throws EdgeServiceFault {
		nodeService.changeHeartBeatStatusForGroup(groupID, groupType, enabled);
	}

	@Override
	public void changeAutoOfflieCopyStatusForGroup(int groupID, int groupType,
			boolean enabled) throws EdgeServiceFault {
		nodeService.changeAutoOfflieCopyStatusForGroup(groupID, groupType, enabled);
	}

	@Override
	public DiscoveryESXOption getESXInformation(int id) throws EdgeServiceFault {
		return nodeService.getESXInformation(id);
	}

	@Override
	public void redeployPolicyByESX(int esxID) throws EdgeServiceFault {
		nodeService.redeployPolicyByESX(esxID);
	}

	@Override
	public String getEdgePolicyName(int policyId) throws EdgeServiceFault {
		return policyManagementService.getEdgePolicyName(policyId);
	}

	@Override
	public int copyEdgePolicy(int policyId, String newPolicyName) throws EdgeServiceFault {
		return policyManagementService.copyEdgePolicy(policyId, newPolicyName);
	}

	@Override
	public int getPolicyIdByName(String policyName) throws EdgeServiceFault {
		return policyManagementService.getPolicyIdByName(policyName);
	}

	@Override
	public VMVerifyStatus getVMVerifyStatus(int id) throws EdgeServiceFault {
		return nodeService.getVMVerifyStatus(id);
	}
	
	@Override
	public List<ESXServer> getDiscoveryEsxServers(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return nodeService.getDiscoveryEsxServers(esxOption);
	}

	@Override
	public List<DiscoveryVirtualMachineInfo> getVmList(DiscoveryESXOption esxOption, ESXServer esxServer) throws EdgeServiceFault {
		return nodeService.getVmList(esxOption, esxServer);
	}

	@Override
	public void notify(NotifyMessage message) throws EdgeServiceFault {
		d2dNotifyService.notify(message);
	}

	@Override
	public int syncD2DStatusInfo(String uuid, ApplicationType appType, D2DStatusInfo d2dStatusInfo) throws EdgeServiceFault {
		return d2dService.syncD2DStatusInfo(uuid, appType, d2dStatusInfo);
	}

	@Override
	public int syncVSphereStatusAll(List<D2DStatusInfo> statusInfoList) throws EdgeServiceFault {
		return d2dService.syncVSphereStatusAll(statusInfoList);
	}

	@Override
	public int syncVCMStatusAll(List<D2DStatusInfo> statusInfoList) throws EdgeServiceFault {
		return d2dService.syncVCMStatusAll(statusInfoList);
	}
	
	@Override
	public VMInfo getVMNodesFromVSphere2(VCMConnectionInfo vcmConnection) throws EdgeServiceFault {
		return this.vSphereService.getVMNodesFromVSphere2(vcmConnection);
	}
	
	@Override
	public boolean redeployPoliciesEx(int policyType) throws EdgeServiceFault {
		return policyManagementService.redeployPoliciesEx( policyType);
	}

	@Override
	public AssignPolicyCheckResultCode canNodesBeAssignedWithPolicy(
			List<Integer> nodeIdList, int policyType, int policyId)
			throws EdgeServiceFault {
		return policyManagementService.canNodesBeAssignedWithPolicy(
				nodeIdList, policyType, policyId );
	}

	@Override
	public AssignPolicyCheckResultCode canNodesOfGroupsBeAssignedWithPolicy(
			List<Integer> groupIdList, int policyType, int policyId)
			throws EdgeServiceFault {
		return policyManagementService.canNodesOfGroupsBeAssignedWithPolicy(
				groupIdList, policyType, policyId );
	}
	
	@Override
	public int updateMultipleNodeByIds(int[] nodeID, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential)
			throws EdgeServiceFault {
		return nodeService.updateMultipleNodeByIds(nodeID, globalUsername, globalPassword, forceManaged, usingOrignalCredential);
	}

	@Override
	public int updateMultipleNodeForGroup(int gatewayId, int groupID, int groupType, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential)
			throws EdgeServiceFault {
		return nodeService.updateMultipleNodeForGroup(gatewayId, groupID, groupType, globalUsername, globalPassword, forceManaged, usingOrignalCredential);
	}

	@Override
	public VMSnapshotsInfo[] getVMSnapshots(Node node) throws EdgeServiceFault {
		return nodeService.getVMSnapshots(node);
	}

	@Override
	public int shutDownVM(Node node) throws EdgeServiceFault {
		return nodeService.shutDownVM(node);
	}

	@Override
	public String getCurrentRunningSnapshot(Node node) throws EdgeServiceFault {
		return nodeService.getCurrentRunningSnapshot(node);
	}

	@Override
	public void startFailover(Node node, VMSnapshotsInfo vmSnapInfo)
			throws EdgeServiceFault {
		nodeService.startFailover(node, vmSnapInfo);
	}

	@Override
	public boolean isFailoverJobFinish(Node node) throws EdgeServiceFault {
		return nodeService.isFailoverJobFinish(node);
	}
	
	@Override
	public PatchInfo getD2DPatchInformation() throws EdgeServiceFault {
		return commonService.getD2DPatchInformation();
	}

	@Override
	public ARCFlashNode getARCFlashNodeInfo(Node node) throws EdgeServiceFault {
		return nodeService.getARCFlashNodeInfo(node);
	}

	@Override
	public List<Node> getVMRunningList(List<Node> nodeList)
			throws EdgeServiceFault {
		return nodeService.getVMRunningList(nodeList);
	}
	
	@Override
	public String getInstalldHbbuServer() throws EdgeServiceFault {
		return nodeService.getInstalldHbbuServer();
	}
	@Override
	public void sendDiscoveryNodesAlertToCPM(String sendHost, String subject,
			String content, Date date) throws EdgeServiceFault {
		
		configService.sendDiscoveryNodesAlertToCPM(sendHost, subject, content, date);		
	}
	
	@Override
	public List<RHAScenario> getScenarioList(RHAControlService controlService) throws EdgeServiceFault {
		return nodeService.getScenarioList(controlService);
	}

	@Override
	public List<RHASourceNode> getSourceNodeList(RHAControlService controlService) throws EdgeServiceFault {
		return nodeService.getSourceNodeList(controlService);
	}
	
	@Override
	public ImportNodeFromRHAResult importNodeFromRHA(ImportNodeFromRHAParameters parameters) throws EdgeServiceFault {
		return nodeService.importNodeFromRHA(parameters);
	}

	@Override
	public List<RPSSourceNode> importNodeFromRpsServer(RpsNode rpsNode) throws EdgeServiceFault {
		return rpsNodeService.importNodeFromRpsServer(rpsNode);
	}
	
	@Override
	public List<OffsiteVCMConverterInfo> getOffsiteVCMConverters(
		List<Integer> specificConverters
		) throws EdgeServiceFault
	{
		return this.nodeService.getOffsiteVCMConverters( specificConverters );
	}

	@Override
	public List<OffsiteVCMConverterSavingStatus> updateOffsiteVCMConverters(
		List<OffsiteVCMConverterInfo> converterInfoList )
		throws EdgeServiceFault
	{
		return this.nodeService.updateOffsiteVCMConverters( converterInfoList );
	}

	@Override
	public void cancelUpdatingOffsiteVCMConverters() throws EdgeServiceFault
	{
		this.nodeService.cancelUpdatingOffsiteVCMConverters();
	}

	@Override
	public String updateOffsiteVCMConvertersAsync(
		List<OffsiteVCMConverterInfo> converterInfoList )
		throws EdgeServiceFault
	{
		return this.nodeService.updateOffsiteVCMConvertersAsync( converterInfoList );
	}

	@Override
	public List<OffsiteVCMConverterSavingStatus> getOffsiteVCMConverterUpdatingStatus(
		String savingSessionId ) throws EdgeServiceFault
	{
		return this.nodeService.getOffsiteVCMConverterUpdatingStatus( savingSessionId );
	}

	@Override
	public void deleteOffsiteVCMConverterUpdatingSession( String savingSessionId )
		throws EdgeServiceFault
	{
		this.nodeService.deleteOffsiteVCMConverterUpdatingSession( savingSessionId );
	}

	@Override
	public List<RHAControlService> getControlServiceList(String serverNamePrefix) throws EdgeServiceFault {
		return nodeService.getControlServiceList(serverNamePrefix);
	}

	@Override
	public BackupPolicy getPolicyInfo( int policyId, boolean needDetails )
		throws EdgeServiceFault
	{
		return this.policyManagementService.getPolicyInfo( policyId, needDetails );
	}

	@Override
	public OffsiteVCMConverterInfo getOffsiteVCMConverterInfoByHostId(int hostId) throws EdgeServiceFault {
		return nodeService.getOffsiteVCMConverterInfoByHostId(hostId);
	}
	@Override
	public List<SourceMachineNetworkAdapterInfo> getSourceMachineNetworkAdapterInfoList(Node node)
			throws EdgeServiceFault {
		return nodeService.getSourceMachineNetworkAdapterInfoList(node);
	}

	@Override
	public void saveSourceMachineNetworkAdapterInfo(Node node,
			List<SourceMachineNetworkAdapterInfo> networkAdapterList)
			throws EdgeServiceFault {
		nodeService.saveSourceMachineNetworkAdapterInfo(node, networkAdapterList);
	}

	@Override
	public int syncADRConfigureToVCM(String uuid, ADRConfigure adrConfigInfo) throws EdgeServiceFault {
		return d2dService.syncADRConfigureToVCM(uuid, adrConfigInfo);
	}

	@Override
	public int getSourceMachineNetworkAdapterSize(Node node)
			throws EdgeServiceFault {
		return nodeService.getSourceMachineNetworkAdapterSize(node);
	}

	@Override
	public List<IPSetting> getIPSettingFromVCM(String uuid) throws EdgeServiceFault {
		return d2dService.getIPSettingFromVCM(uuid);
	}

	@Override
	public StandbyVMNetworkInfo getStandbyVMNetworkInfo(Node node)
			throws EdgeServiceFault {
		return nodeService.getStandbyVMNetworkInfo(node);
	}

	@Override
	public void saveStandbyVMNetworkInfo(Node node,
			StandbyVMNetworkInfo standbyVMNetworkInfo) throws EdgeServiceFault {
		nodeService.saveStandbyVMNetworkInfo(node, standbyVMNetworkInfo);
	}

	@Override
	public void syncMergeJob(MergeStatus[] statusArray) throws EdgeServiceFault {
		d2dJobService.syncMergeJob(statusArray);
	}

	@Override
	public List<D2DMergeJobStatus> getMergeJobStatus(List<Integer> nodeIds) throws EdgeServiceFault {
		return nodeService.getMergeJobStatus(nodeIds);
	}

	@Override
	public D2DMergeJobStatus getMergeJobStatusById(int nodeId) throws EdgeServiceFault {
		return nodeService.getMergeJobStatusById(nodeId);
	}

	@Override
	public int pauseMergeJob(int nodeId) throws EdgeServiceFault {
		return nodeService.pauseMergeJob(nodeId);
	}

	@Override
	public int resumeMergeJob(int nodeId) throws EdgeServiceFault {
		return nodeService.resumeMergeJob(nodeId);
	}

	@Override
	public void pauseMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault {
		nodeService.pauseMultipleMergeJob(nodeIds);
	}

	@Override
	public void resumeMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault {
		nodeService.resumeMultipleMergeJob(nodeIds);
	}

	@Override
	public int pauseVMMergeJob(int vmHostId) throws EdgeServiceFault {
		return nodeService.pauseVMMergeJob(vmHostId);
	}

	@Override
	public int resumeVMMergeJob(int vmHostId) throws EdgeServiceFault {
		return nodeService.resumeVMMergeJob(vmHostId);
	}

	@Override
	public void pauseMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault {
		nodeService.pauseMultipleVMMergeJob(vmHostIds);
	}

	@Override
	public void resumeMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault {
		nodeService.resumeMultipleVMMergeJob(vmHostIds);
	}

	@Override
	public void specifyESXServerForRVCM(int hostID, DiscoveryESXOption option,
			boolean isForceSave) throws EdgeServiceFault {
		nodeService.specifyESXServerForRVCM(hostID, option, isForceSave);		
	}

	@Override
	public List<SessionPassword> getSessionPasswordForHost(int hostId) throws EdgeServiceFault {
		return nodeService.getSessionPasswordForHost(hostId);
	}

	@Override
	public void saveSessionPassword(List<Integer> hostIdList, List<SessionPassword> passwordList,
			boolean override) throws EdgeServiceFault {
		nodeService.saveSessionPassword(hostIdList, passwordList, override);
	}

	@Override
	@NonSecured
	public String validateUserByUser(String username, String password, String domain) throws EdgeServiceFault {
		NativeFacade nativeCode = new NativeFacadeImpl();
		nativeCode.validateUser(username, password, domain);
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(CommonUtil.STRING_SESSION_USERNAME, username);
			session.setAttribute(CommonUtil.STRING_SESSION_PASSWORD, password);
			session.setAttribute(CommonUtil.STRING_SESSION_DOMAIN, domain);
		}
		return CommonUtil.retrieveCurrentAppUUID();
	}
	
	@Override
	public List<Node> getSRMNodes() throws EdgeServiceFault {
		return nodeService.getSRMNodes();
	}
	
	@Override
	public List<RpsNode> getRpsNodesByGroup(int gateway, int groupID) throws EdgeServiceFault {
		return rpsNodeService.getRpsNodesByGroup(gateway, groupID);
	}
	
	@Override
	public RegistrationNodeResult registerRpsNode(boolean failedReadRemoteRegistry,
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return rpsNodeService.registerRpsNode(failedReadRemoteRegistry,registrationNodeInfo);
	}
	@Override
	public String[] updateRpsNode(boolean failedReadRemoteRegistry,NodeRegistrationInfo nodeInfo, boolean overwrite)
			throws EdgeServiceFault {
		return rpsNodeService.updateRpsNode(failedReadRemoteRegistry,nodeInfo, overwrite);
	}
	@Override
	public void deleteRpsNodeOnly(int id) throws EdgeServiceFault {
		rpsNodeService.deleteRpsNodeOnly(id);
	}
	@Override
	public void deleteRpsNode(int id, boolean keepCurrentSettings)
			throws EdgeServiceFault {
		rpsNodeService.deleteRpsNode(id, keepCurrentSettings);
	}
	@Override
	public RpsNode getRpsNodeDetailInformation(int hostID) throws EdgeServiceFault {
		return rpsNodeService.getRpsNodeDetailInformation(hostID);
	}
	
	@Override
	public void UpdateRegInfoToRpsServer(ConnectionContext context, int nodeId, boolean forceFlag)
			throws EdgeServiceFault {
		rpsRegService.UpdateRegInfoToRpsServer(context,nodeId, forceFlag);
	}
	@Override
	public void RemoveRegInfoFromRpsServer(RpsNode node,RpsConnectionInfo conInfo, boolean forceFlag)
			throws EdgeServiceFault {
		rpsRegService.RemoveRegInfoFromRpsServer(node,conInfo, forceFlag);
	}
	
	@Override
	public void markRpsNodeAsManaged(NodeRegistrationInfo nodeInfo, boolean overwrite) throws EdgeServiceFault {
		rpsNodeService.markRpsNodeAsManaged(nodeInfo, overwrite);
	}
	
	@Override
	public NodeManageResult queryNodeManagedStatus(NodeRegistrationInfo info) 
			throws EdgeServiceFault{
		return nodeService.queryNodeManagedStatus(info);
	}
	
	@Override
	public AddRpsNodesResult importRpsNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault {
		return rpsNodeService.importRpsNodes(nodes, type);
	}
	
	@Override
	public List<DataStoreSettingInfo> getDataStoreListByNode(int nodeId) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreListByNode(nodeId);
	}
	
	@Override
	public DataStoreSettingInfo getDataStoreById(int dedupId) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreById(dedupId);
	}
	
	@Override
	public HashRoleEnvInfo getHashRoleEnvInfo(int nodeID) throws EdgeServiceFault{
		return rpsDataStoreService.getHashRoleEnvInfo(nodeID);
	}
	
	@Override
	public void createFolder(int nodeID,  NodeRegistrationInfo rpsInfo, String parentPath, String subDir, String username, String password) throws EdgeServiceFault{
		rpsDataStoreService.createFolder(nodeID, rpsInfo, parentPath, subDir, username, password);
	}
	
	@Override
	public FileFolderItem getFileItems(int nodeID, NodeRegistrationInfo rpsInfo,String inputFolder,String username,String password, boolean bIncludeFiles,int browseClient) throws EdgeServiceFault{
		return rpsDataStoreService.getFileItems(nodeID, rpsInfo, inputFolder,username,password, bIncludeFiles, browseClient);
		
	}
	
	@Override
	public Volume[] getVolumes(int nodeID, NodeRegistrationInfo rpsInfo, int browseClient) throws EdgeServiceFault{
		return rpsDataStoreService.getVolumes(nodeID,rpsInfo, browseClient);
	}
	
	@Override
	public void cutAllRemoteConnections(int nodeID, NodeRegistrationInfo rpsInfo) throws EdgeServiceFault{
		rpsDataStoreService.cutAllRemoteConnections(nodeID, rpsInfo);
	}

	@Override
	public long validateDestOnly(int nodeID, NodeRegistrationInfo rpsInfo,String path, String domain, String user,
			String pwd, int mode) throws EdgeServiceFault{
		
		return rpsDataStoreService.validateDestOnly(nodeID,rpsInfo, path, domain, user, pwd, mode);
		
	}
	@Override
	public String saveDataStoreSetting(DataStoreSettingInfo settingInfo) throws EdgeServiceFault {
		return rpsDataStoreService.saveDataStoreSetting(settingInfo);
	}
	
	@Override
	public boolean checkDataStoreDuplicate(DataStoreSettingInfo settingInfo) throws EdgeServiceFault{
		return rpsDataStoreService.checkDataStoreDuplicate(settingInfo);
	}
	
	@Override
	public DataStoreSettingInfo getDataStoreByGuid(int nodeId, String guid) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreByGuid(nodeId, guid);
	}
	
	@Override
	public List<DataStoreSettingInfo> getDataStoreHistoryByGuid(int nodeId,
			String guid, Date timeStamp) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreHistoryByGuid(nodeId, guid, timeStamp);
	}

	@Override
	public List<DataStoreStatusListElem> getDataStoreSummariesByNode(int nodeId) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreSummariesByNode(nodeId);
	}
	
	@Override
	public DataStoreStatusListElem getDataStoreSummary(int nodeId, String guid) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreSummary(nodeId, guid);
	}
	
	@Override
	public int handleJobEvent(JobEvent event) {
		return d2dJobService.handleJobEvent(event);
	}

	@Override
	public int handleActivityLogEvent(ActivityLogEvent event) {
		return d2dJobService.handleActivityLogEvent(event);
	}
	
	@Override
	public int handleJobHistoryEvent(JobHistoryEvent event) {
		return d2dJobService.handleJobHistoryEvent(event);
	}
	
	@Override
	public int handleDatastoreStatusChangeEvent(DatastoreStatusChangeEvent event) {
		return d2dJobService.handleDatastoreStatusChangeEvent(event);
	}

	@Override
	@NonSecured
	public int checkUserByUUID(String uuid) {
		if (uuid == null || uuid.isEmpty())
			return 1; //UUID is required

		String appUUID = CommonUtil.retrieveCurrentAppUUID();
		if (!uuid.equals(appUUID))
			return 2; //Wrong UUID

		setUuid(uuid);

		return 0;
	}

	@Override
	public void startDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		rpsDataStoreService.startDataStoreInstance(nodeId, dataStoreUuid);
	}

	@Override
	public void stopDataStoreInstance(int nodeId, String dataStoreUuid)
			throws EdgeServiceFault {
		rpsDataStoreService.stopDataStoreInstance(nodeId, dataStoreUuid);		
	}

	@Override
	public void deleteDataStoreById(int nodeId, String dedupId) throws EdgeServiceFault {
		rpsNodeService.deleteDataStoreById(nodeId, dedupId);
	}

	@Override
	public JobHistoryPagingResult getD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return jobHistoryService.getD2DJobHistoryList(nodeId, config, filter);
	}
	
	@Override
	public JobHistoryPagingResult getASBUJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return jobHistoryService.getASBUJobHistoryList(nodeId, config, filter);
	}

	@Override
	public void deleteAllD2DJobHistorys(int nodeId) throws EdgeServiceFault {
		jobHistoryService.deleteAllD2DJobHistorys(nodeId);
	}

	@Override
	public void deleteOldD2DJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault {
		jobHistoryService.deleteOldD2DJobHistorys(nodeId, serverDate);
	}
	
	@Override
	public JobHistoryPagingResult getRpsJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return jobHistoryService.getRpsJobHistoryList(nodeId, config, filter);
	}

	@Override
	public void deleteAllRpsJobHistorys(int nodeId) throws EdgeServiceFault {
		jobHistoryService.deleteAllRpsJobHistorys(nodeId);
	}

	@Override
	public void deleteOldRpsJobHistorys(int nodeId, ServerDate serverDate) throws EdgeServiceFault {
		jobHistoryService.deleteOldRpsJobHistorys(nodeId, serverDate);
	}

	@Override
	public void importHyperVVMs(DiscoveryHyperVOption hyperVOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault {
		nodeService.importHyperVVMs(hyperVOption, vms, type, addEsxToADList);
	}
	

	@Override
	public HypervProtectionType getHyperVProtectionType(DiscoveryHyperVOption hyperVOption)
			throws EdgeServiceFault {
		return nodeService.getHyperVProtectionType(hyperVOption);
	}
	
	@Override
	public List<DiscoveryVirtualMachineInfo> getHypervVMList(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		return nodeService.getHypervVMList(hyperVOption);
	}
	
	@Override
	public List<Task> getTaskList() throws EdgeServiceFault {
		return nodeService.getTaskList();
	}

	@Override
	public int saveNodeFilters(NodeFilterGroup filterGroup) throws EdgeServiceFault {
		return nodeService.saveNodeFilters(filterGroup);
	}
	
	@Override
	public void deleteFilter(int id) throws EdgeServiceFault {
		nodeService.deleteFilter(id);
	}

	@Override
	public void deleteTask(Integer taskID) throws EdgeServiceFault {
		nodeService.deleteTask(taskID);
		
	}

	@Override
	public List<PolicyInfo> getHostPolicies(int hostId) {
		return policyManagementService.getHostPolicies(hostId);
	}

	@Override
	public HostConnectInfo getVCMConverterByHostId(int hostId) throws EdgeServiceFault {
		return nodeService.getVCMConverterByHostId(hostId);
	}
	

	@Override
	public HostConnectInfo getMonitorConnectInfoByHostId(int hostId)
			throws EdgeServiceFault {
		return nodeService.getMonitorConnectInfoByHostId(hostId);
	}

	@Override
	public int createUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault {
		return policyManagementService.createUnifiedPolicy(policy);
	}

	@Override
	public List<PolicyInfo> getPlanList() throws EdgeServiceFault {
		return policyManagementService.getPlanList();
	}
	
	@Override
	public PolicyPagingResult getPlanListByPaging(PlolicyPagingConfig config) throws EdgeServiceFault {
		return policyManagementService.getPlanListByPaging(config);
	}

	@Override
	public List<JobHistory> getLatestJobHistoriesByNodeId(int nodeId)
			throws EdgeServiceFault {
		return nodeService.getLatestJobHistoriesByNodeId(nodeId);
	}

	@Override
	public UnifiedPolicy loadUnifiedPolicyById(int planId)
			throws EdgeServiceFault {
		return policyManagementService.loadUnifiedPolicyById(planId);
	}

	@Override
	public RegistrationNodeResult registerLinuxNode(
			NodeRegistrationInfo registrationNodeInfo,boolean isForce) throws EdgeServiceFault {
		return linuxNodeService.registerLinuxNode(registrationNodeInfo,isForce);
	}

	@Override
	public RegistrationNodeResult registerLinuxD2DServer(
			NodeRegistrationInfo registrationNodeInfo,boolean isForce,boolean isClearExistingData) throws EdgeServiceFault {
		return linuxNodeService.registerLinuxD2DServer(registrationNodeInfo,isForce,isClearExistingData);
	}

	@Override
	public AddNodeResult addNodes(List<NodeRegistrationInfo> nodeList) throws EdgeServiceFault {
		return nodeService.addNodes(nodeList);
	}

	@Override
	public LogPagingResult getUnifiedLogs(LogPagingConfig config, LogFilter filter) throws EdgeServiceFault {
		return logService.getUnifiedLogs(config, filter);
	}
	
	@Override
	public void addUnifiedLog(LogAddEntity entity) throws EdgeServiceFault {
		logService.addUnifiedLog(entity);
	}

	@Override
	public void deleteUnifiedLogs(LogFilter filter) throws EdgeServiceFault {
		logService.deleteUnifiedLogs(filter);
	}

	@Override
	public void updateUnifiedPolicy(UnifiedPolicy policy) throws EdgeServiceFault {
		logger.info("EdgeWebServiceImpl.updateUnifiedPolicy(), start" + policy.toString());
		policyManagementService.updateUnifiedPolicy(policy);
		logger.info("EdgeWebServiceImpl.updateUnifiedPolicy(), end");
	}
	
	@Override
	public JobHistoryPagingResult getDashboardJobHistoryList(JobHistoryPagingConfig config, JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		return jobHistoryService.getDashboardJobHistoryList(config, filter);
	}

	@Override
	public List<String> getPrepostScriptList(int linuxD2DServerId)
			throws EdgeServiceFault {
		return linuxPlanService.getPrepostScriptList(linuxD2DServerId);
	}

	@Override
	public boolean validateBackupLocation(int linuxD2DServerId,
			LinuxBackupLocationInfo locationInfo) throws EdgeServiceFault {
		return linuxPlanService.validateBackupLocation(linuxD2DServerId, locationInfo);
	}

	@Override
	public List<DataStoreStatusListElem> getDataStoreSummariesByNodefromCache(
			int nodeId ) throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreSummariesByNodefromCache(nodeId );
	}

	@Override
	public void triggerDataStoreSummarySync() throws EdgeServiceFault {
		this.rpsDataStoreService.triggerDataStoreSummarySync();
		
	}

	@Override
	public List<Node> getNodeListByIDs(List<Integer> ids) throws EdgeServiceFault{
		return nodeService.getNodeListByIDs(ids);
	}
	
	@Override
	public PagingResult<Node> getNodePagingListByIDs(List<Integer> ids, PagingConfig config) throws EdgeServiceFault{
		return nodeService.getNodePagingListByIDs(ids, config);
	}

	@Override
	public List<PolicyInfo> getPlansByNodeNameIp(int gatewayid, String name, String ip)
			throws EdgeServiceFault {
		return  this.policyManagementService.getPlansByNodeNameIp(gatewayid, name, ip);
	}

	@Override
	public RegistrationNodeResult validateLinuxNode(NodeRegistrationInfo d2dServer,
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return linuxNodeService.validateLinuxNode(d2dServer,registrationNodeInfo);
	}

	@Override
	public RegistrationNodeResultForLinux validateLinuxD2DServer(
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return linuxNodeService.validateLinuxD2DServer(registrationNodeInfo);
	}

	@Override
	public int validateManaged(
			NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return linuxNodeService.validateManaged(registrationNodeInfo);
	}

	@Override
	public int getCountOfHostWithVSBTask() throws EdgeServiceFault {
		return nodeService.getCountOfHostWithVSBTask();
	}

	@Override
	public List<PolicyInfo> getPlans(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		return jobHistoryService.getPlans(filter);
	}

	@Override
	public List<JobTypeForGroupByPlan> getJobTypes(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		return jobHistoryService.getJobTypes(filter);
	}

	@Override
	public JobHistoryPagingResult getJobHistories(JobHistoryFilter4Dashboard filter) throws EdgeServiceFault {
		return jobHistoryService.getJobHistories(filter);
	}
	
	@Override
	public int backupLinuxNode(int nodeId,String nodeName, int backupType)
			throws EdgeServiceFault {
		return linuxNodeService.backupLinuxNode(nodeId,nodeName, backupType);
	}
	
	@Override
	public long testConnectionToCloud(ArchiveCloudDestInfo in_cloudInfo)
		throws EdgeServiceFault
	{
		return this.policyManagementService.testConnectionToCloud(in_cloudInfo);
	}
	
	@Override
	public String GetArchiveDNSHostName() throws EdgeServiceFault{
		return this.policyManagementService.GetArchiveDNSHostName();
	}

	@Override
	public List<CloudProviderInfo> getCloudProviderInfos()
			throws EdgeServiceFault {
		
		return this.policyManagementService.getCloudProviderInfos();
	}

	@Override
	public int getReplicationQueueSize(int nodeId) throws EdgeServiceFault {
		return nodeService.getReplicationQueueSize(nodeId);
	}
	
	@Override
	public void doManualDiscovery() throws EdgeServiceFault {
		nodeService.doManualDiscovery();
	}
	
	@Override
	public void doAutoDiscovery() throws EdgeServiceFault {
		nodeService.doAutoDiscovery();
	}

	@Override
	public UnifiedPolicy loadUnifiedPolicyByUuid(String uuid)
			throws EdgeServiceFault {
		return policyManagementService.loadUnifiedPolicyByUuid(uuid);
	}

	@Override
	public CustomerPagingResult getCustomers(CustomerPagingConfig config) throws EdgeServiceFault {
		return mspCustomerService.getCustomers(config);
	}

	@Override
	public int addCustomer(Customer customer) throws EdgeServiceFault {
		return mspCustomerService.addCustomer(customer);
	}
	
	@Override
	public int modeifyCustomer(Customer customer) throws EdgeServiceFault {
		return mspCustomerService.modeifyCustomer(customer);
	}

	@Override
	public void deleteCustomers(List<Integer> customerIds) throws EdgeServiceFault {
		mspCustomerService.deleteCustomers(customerIds);
	}

	@Override
	public int saveFilters(BaseFilter filter)
			throws EdgeServiceFault {
		return nodeService.saveFilters(filter);
	}

	@Override
	public List<BaseFilter> getFilters(FilterType filterType) throws EdgeServiceFault {
		return nodeService.getFilters(filterType);
	}
	
	@Override
	public JobHistoryPagingResult getLinuxD2DJobHistoryList(int nodeId, JobHistoryPagingConfig config, JobHistoryFilter filter) throws EdgeServiceFault {
		return jobHistoryService.getLinuxD2DJobHistoryList(nodeId, config, filter);
	}

	@Override
	public int cancelLinuxJob(int nodeId, String jobUUID)
			throws EdgeServiceFault {
		return linuxNodeService.cancelLinuxJob(nodeId, jobUUID);
	}

	@Override
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(
			DiscoveryESXOption esxOption,
			boolean recursive) throws EdgeServiceFault {
		return nodeService.getVmwareTreeRootEntity(esxOption, recursive);
	}

	@Override
	public HostConnectInfo getLinuxD2DServerInfoByHostId(int hostId)
			throws EdgeServiceFault {
		return linuxNodeService.getLinuxD2DServerInfoByHostId(hostId);
	}

	@Override
	public List<MspReplicationDestination> getMspReplicationDestinations(String localFQDNName, RpsHost mspServer, HttpProxy clientHttpProxy) throws EdgeServiceFault {
		return policyManagementService.getMspReplicationDestinations(localFQDNName, mspServer, clientHttpProxy);
	}

	@Override
	public PlanPagingResult getAvailableMspPlans(int customerId, PlanPagingConfig config) throws EdgeServiceFault {
		return mspPlanService.getAvailableMspPlans(customerId, config);
	}

	@Override
	public void assignMspPlans(int customerId, List<Integer> mspPlanIds) throws EdgeServiceFault {
		mspPlanService.assignMspPlans(customerId, mspPlanIds);
	}

	@Override
	public void importNodesFromRPS(int rpsHostId) throws EdgeServiceFault {
		mspNodeService.importNodesFromRPS(rpsHostId);
	}
	
	@Override
	public DiscoveryESXOption getVMNodeESXSettingsFromDB(int hostID) throws EdgeServiceFault {
		return nodeService.getVMNodeESXSettingsFromDB(hostID);
	}

	@Override
	public DataStoreSettingInfo importDataStoreInstance(
			int nodeID, DataStoreSettingInfo storeSettings, boolean bOverWrite,
			boolean bForceTakeOwnership) throws EdgeServiceFault {
		return rpsDataStoreService.importDataStoreInstance(nodeID, storeSettings, bOverWrite, bForceTakeOwnership);
	}

	@Override
	public DataStoreSettingInfo getDataStoreInfoFromDisk(int nodeID, String strPath,
			String strUser, String strPassword, String strDataStorePassword)
			throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreInfoFromDisk(nodeID, strPath, strUser, strPassword, strDataStorePassword);
	}
	
	@Override
	public List<NetworkPath> getMappedNetworkPath(int nodeID)
			throws EdgeServiceFault {
		return rpsNodeService.getMappedNetworkPath(nodeID);
	}
	
	@Override
	public int resumeMergeJob4RPS(int rpsNodeId, String uuid)
			throws EdgeServiceFault {
		return nodeService.resumeMergeJob4RPS(rpsNodeId, uuid);
	}
	
	@Override
	public int handleDataSyncEvent(DataSyncEvent event) {
		return d2dJobService.handleDataSyncEvent(event);
	}

	@Override
	public void updateEdgeConnectionCredential(int hostID, String userName,
			@NotPrintAttribute String password) {
		nodeService.updateEdgeConnectionCredential(hostID, userName, password);
		
	}

	@Override
	public long getDataStoreDedupeRequiredMinMemSizeByte(int nodeId, String dataStoreId)
			throws EdgeServiceFault {
		return rpsDataStoreService.getDataStoreDedupeRequiredMinMemSizeByte(nodeId, dataStoreId);
	}
	
	@Override
	public List<Integer> getNodesNeedRemoteDeploy(List<Integer> nodeIds)
			throws EdgeServiceFault {
		return nodeService.getNodesNeedRemoteDeploy(nodeIds);
	}

	@Override
	public NodeDetail getNodeDetailInformationByVMID(String vmInstanceUUID) throws EdgeServiceFault {
		return nodeService.getNodeDetailInformationByVMID(vmInstanceUUID);
	}

	@Override
	public boolean isConverterSameForNode(String converUuid, String nodeUuid) throws EdgeServiceFault {
		return d2dService.isConverterSameForNode(converUuid, nodeUuid);
	}

	@Override
	public String getNodeAuthUuid(String uuid) throws EdgeServiceFault {
		return nodeService.getNodeAuthUuid(uuid);
	}

	@Override
	public List<String> getWindowsLocalUsers() throws EdgeServiceFault {
		return mspCustomerService.getWindowsLocalUsers();
	}

	@Override
	public D2DStatus checkD2DStatus(D2DInfo d2dInfo) throws EdgeServiceFault {
		return d2dService.checkD2DStatus(d2dInfo);
	}

	@Override
	public ExternalLinks getExternalLinksForInternal(String language,
			String country) throws EdgeServiceFault {
		return commonService.getExternalLinksForInternal(language, country);
	}

	@Override
	public boolean checkLinuxD2DServerCanBeDeleted(int[] node) {
		return linuxNodeService.checkLinuxD2DServerCanBeDeleted(node);
	}
	
	@NonSecured
	public void setLocalCheckSession(boolean localCheckSession) {
		this.localCheckSession = localCheckSession;
	}

	@NonSecured
	public boolean isLocalCheckSession() {
		return localCheckSession;
	}

	@Override
	public void enablePolicies(boolean value, List<Integer> policyIdList
			,RebootType nodeInstallType ,Date nodeInstallTime)
			throws EdgeServiceFault {
		policyManagementService.enablePolicies(value, policyIdList,nodeInstallType,nodeInstallTime);
	}
	
	@Override
	public void generateExportLogFile(LogPagingConfig config,
			LogFilter filter, String logExportIdentifier ) throws EdgeServiceFault {
		
		this.logService.generateExportLogFile(config, filter, logExportIdentifier );

	}
	@Override
	public LogExportMessage logExportCommunicate( LogExportMessage request, String exportIdentifier )
			throws EdgeServiceFault {
		return this.logService.logExportCommunicate(request, exportIdentifier);
	}
	
	@Override
	public DiscoveryHyperVOption getHyperVInformation(int id) throws EdgeServiceFault {
		return nodeService.getHyperVInformation(id);
	}

	@Override
	public void updateHyperVSource(DiscoveryHyperVOption hypervOption) throws EdgeServiceFault {
		nodeService.updateHyperVSource(hypervOption);
	}
	
	@Override
	public void redeployPolicyByHyperV(int hyperVID) throws EdgeServiceFault {
		nodeService.redeployPolicyByHyperV(hyperVID);
	}

	@Override
	public com.ca.arcflash.webservice.edge.license.LicenseCheckResult checkLicense(UDP_CLIENT_TYPE type,
			MachineInfo machine, long required_feature) throws EdgeServiceFault{
		logger.info("EdgeWebServiceImpl.checkLicense() - start");
		return d2dLicenseService.checkLicense(type, machine, required_feature);
	}

	@Override
	public int addLicenseKeyNew(String license) throws EdgeServiceFault {
		return licenseServiceNew.addLicenseKeyNew(license);
	}

	@Override
	public List<LicenseInformation> getLicenses() throws EdgeServiceFault {
		return licenseServiceNew.getLicenses();
	}

	@Override
	public LicenseInformation getLicenseInfo(String key)
			throws EdgeServiceFault {
		return licenseServiceNew.getLicenseInfo(key);
	}

	@Override
	public List<ProtectedNodeInDestination> getNodesDetailFromDataStore(int rpsNodeId , List<ProtectedNodeInDestination> originalNodeList) throws EdgeServiceFault {
		return rpsDataStoreService.getNodesDetailFromDataStore(rpsNodeId ,originalNodeList);
	}
	
	@Override
	public List<RecoveryPoint> getRecoveryPointsByTimePeriod(
			int rpsNodeId, ProtectedNodeInDestination node, Date beginTime, Date endTime)
			throws EdgeServiceFault {
		return recoveryPointService.getRecoveryPointsByTimePeriod(rpsNodeId, node, beginTime, endTime);
	}
	@Override
	public RecoveryPointItem[] getRecoveryPointItems(RecoveryPoint recoveryPoint, int rpsNodeId, ProtectedNodeInDestination node) throws EdgeServiceFault {
		return recoveryPointService.getRecoveryPointItems(recoveryPoint, rpsNodeId, node);
	}
	@Override
	public boolean validateRecoveryPointPassword(
			RecoveryPointInformationForCPM recoveryPoint)
			throws EdgeServiceFault {
		return recoveryPointService.validateRecoveryPointPassword(recoveryPoint);
	}
	@Override
	public MachineConfigure getRecoveryPointMachineConfig( RecoveryPointInformationForCPM recoveryPoint ) throws EdgeServiceFault {
		return recoveryPointService.getRecoveryPointMachineConfig(recoveryPoint);
	}
	@Override
	public List<ProtectedNodeInDestination> getDataSeedingNodes(int sourceRpsNodeId, String sourceDataStoreUuid) throws EdgeServiceFault {
		return rpsDataStoreService.getDataSeedingNodes(sourceRpsNodeId, sourceDataStoreUuid);
	}
	
	@Override
	public void submitDataSeedingJob(DataSeedingJobScript script) throws EdgeServiceFault {
		rpsDataStoreService.submitDataSeedingJob(script);
	}

	@Override
	public List<LicensedNodeInfo> getLicensedNodeList(BundledLicense licenseId) throws EdgeServiceFault {
		return licenseServiceNew.getLicensedNodeList(licenseId);
	}

	@Override
	public List<LicensedNodeInfo> getUnLicensedNodeList(BundledLicense licenseId) throws EdgeServiceFault {
		return licenseServiceNew.getUnLicensedNodeList(licenseId);
	}

	@Override
	public void releaseNodeFromLicenseNew(BundledLicense licenseId,
			List<LicensedNodeInfo> nodeList) throws EdgeServiceFault {
		licenseServiceNew.releaseNodeFromLicenseNew(licenseId, nodeList);
	}

	@Override
	public List<EsxVSphere> getEsxInfoList(int gatewayid, List<VsphereEntityType> types)
			throws EdgeServiceFault {
		return nodeService.getEsxInfoList(gatewayid, types);
	}
	
	@Override
	public List<EsxVSphere> getHyperVInfoList(int gatewayid)
			throws EdgeServiceFault {
		return nodeService.getHyperVInfoList(gatewayid);
	}

	@Override
	public HostConnectInfo getVsbMonitorByHostId(int hostId) throws EdgeServiceFault {
		return nodeService.getVsbMonitorByHostId(hostId);
	}

	@Override
	public List<Integer> getPlanIds() throws EdgeServiceFault {
		return policyManagementService.getPlanIds();
	}
	
	@Override
	public boolean specifyHypervisor(LicenseMachineType machineType, Hypervisor hypervisor, List<Integer> nodeIds) throws EdgeServiceFault { 
		return nodeService.specifyHypervisor(machineType, hypervisor, nodeIds);
	}
	public void resyncRecoveryPointSummaryInfo() throws EdgeServiceFault {
		RecoveryPointHandler.getInstance().syncRecoveryPointTotalSize();
	}

	@Override
	public Hypervisor getSpecifiedHypervisor(int hostId) throws EdgeServiceFault {
		return nodeService.getSpecifiedHypervisor(hostId);
	}

	@Override
	public CSVObject<ExportNode> getHostNodeCSVObject(int groupID, int grouptype,
			EdgeNodeFilter nodeFilter) throws EdgeServiceFault {
		return nodeService.getHostNodeCSVObject(groupID, grouptype, nodeFilter);
	}
	
	@Override
	public  InstantVMOperationResult startInstantVM( StartInstantVMOperation operationPara )  throws EdgeServiceFault {
		return instantVMService.startInstantVM(operationPara);
	}

	@Override
	public  InstantVMOperationResult stopInstantVM( StopInstantVMOperation operationPara ) throws EdgeServiceFault{
		return instantVMService.stopInstantVM(operationPara);
	}
	
	@Override
	public int getHostIdByHostNameOrIP(int gatewayid, String name, String ip, int isVisible) {
		return nodeService.getHostIdByHostNameOrIP(gatewayid, name, ip, isVisible);
	}
	
	@Override
	public InstantVHDOperationResult startInstantVHD(StartInstantVHDOperation para) throws EdgeServiceFault {
		return instantVMService.startInstantVHD(para);
	}
	
	@Override
	public InstantVHDOperationResult stopInstantVHD(StopInstantVHDOperation para) throws EdgeServiceFault {
		return instantVMService.stopInstantVHD(para);
	}
	

	@Override
	public InstantVMPagingResult getInstantVMPagingNodes(InstantVMPagingConfig config, InstantVMFilter filter) {
		return instantVMService.getInstantVMPagingNodes(config, filter);
	}
	
	@Override
	public long powerOnIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault{
		return instantVMService.powerOnIVM(instantVMJobUUID, ivmUUID);
	}

	@Override
	public long powerOffIVM(String instantVMJobUUID, String ivmUUID) throws EdgeServiceFault{
		return instantVMService.powerOffIVM(instantVMJobUUID, ivmUUID);
	}
	
	@Override
	public ASBUSyncResult createOrUpdateASBUServers(ArcserveConnectInfo connectInfo, String hostName)throws EdgeServiceFault {
		return asbuService.createOrUpdateASBUServers(connectInfo, hostName);
	}

	@Override
	public List<DeleteASBUBackupServerResult> deleteASBUDomain(int domainId) throws EdgeServiceFault {
		return asbuService.deleteASBUDomain(domainId);
	}

	@Override
	public List<ASBUServerInfo> getASBUServerList(GatewayId gatewayId, int domainId)throws EdgeServiceFault{
		return asbuService.getASBUServerList(gatewayId, domainId);
	}
	
	@Override
	public List<ASBUServerInfo> getASBUServerListWithoutGroup(GatewayId gatewayId, int domainId)
			throws EdgeServiceFault {
		return asbuService.getASBUServerListWithoutGroup(gatewayId, domainId);
	}

	@Override
	public List<ASBUMediaGroupInfo> getASBUMediaGroupList(int serverId) throws EdgeServiceFault{
		return asbuService.getASBUMediaGroupList(serverId);
	}

	@Override
	public List<ASBUMediaInfo> getASBUMediaList(int serverId, int groupNum)
		throws EdgeServiceFault{
		return asbuService.getASBUMediaList(serverId, groupNum);
	}
	
	@Override
	public ASBUServerStatusInfo getAsbuServerStatus(int serverId)
			throws EdgeServiceFault {
		return asbuService.getAsbuServerStatus(serverId);
	}

	@Override
	public VsphereEntity getVcloudResource(VsphereEntity vcloudEntity)
			throws EdgeServiceFault {
		return nodeService.getVcloudResource(vcloudEntity);
	}

	@Override
	public List<ProtectedResource> getProtectedResources(List<ProtectedResourceIdentifier> resourceIds)
			throws EdgeServiceFault {
		return nodeService.getProtectedResources(resourceIds);
	}

	@Override
	public PagingResult<NodeEntity> getPagingNodes(NodeGroup group, List<NodeFilter> nodeFilters, SortablePagingConfig<NodeSortCol> config) throws EdgeServiceFault {
		return nodeService.getPagingNodes(group, nodeFilters, config);
	}

    public PagingResult<ShareFolderDestinationInfo> getSharedFolderDestinationList( SimpleSortPagingConfig shareFolderPagingConfig, GatewayId gatewayId) throws EdgeServiceFault{
        return shareFolderService.getSharedFolderDestinationList(shareFolderPagingConfig, gatewayId);
    }

    @Override
    public List<RecoveryPointWithNodeInfo> getGroupedRecoveryPointsFromSharedFolder(
            SharedFolderBrowseParam param, Date beginTime, Date endTime)
            throws EdgeServiceFault {
        return recoveryPointService.getGroupedRecoveryPointsFromSharedFolder(param, beginTime, endTime); 
    }

    @Override
    public List<PlanInDestination> getPlansFromSharedFolder(
            SharedFolderBrowseParam param) throws EdgeServiceFault {
        return shareFolderService.getPlansFromSharedFolder(param); 
    }
    public List<ProtectedNodeInDestination> getNodesDetailFromSharedFolder( SharedFolderBrowseParam browseParam, 
			List<ProtectedNodeInDestination> needToUpdates ) throws EdgeServiceFault {
		 return shareFolderService.getNodesDetailFromSharedFolder( browseParam, needToUpdates );
	}

	@Override
	public List<NodeVcloudSummary> getVcloudPropertiesByNodeIds(
			List<Integer> nodeIds) throws EdgeServiceFault {
		return nodeService.getVcloudPropertiesByNodeIds(nodeIds);
	}

	@Override
	public List<ASBUMediaPoolSet> getASBUMediaPoolSet(int serverId, String groupName) throws EdgeServiceFault {
		return asbuService.getASBUMediaPoolSet(serverId, groupName);
	}

	@Override
	public List<RecoveryPoint> getLinuxRecoveryPoints(
			SharedFolderBrowseParam param, Date startDate, Date endDate)
			throws EdgeServiceFault {
		return  recoveryPointService.getLinuxRecoveryPoints(param, startDate, endDate);
	}

	@Override
	public ASBUStatus checkASBUStatus(ASBUInfo info) throws EdgeServiceFault {
		return asbuService.checkASBUStatus(info);
	}

	@Override
	public int checkPlanStatus(String d2dUuid, String policyUuid,
			boolean justcheck) throws EdgeServiceFault {
		return checkPolicyStatus(d2dUuid, policyUuid, justcheck);
	}

	

	@Override
	public void deleteRemoteDeploy(List<DeployTargetDetail> targets)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDeployProcess(DeployTargetDetail target)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DeployStatusInfo getDeployStatus(DeployTargetDetail target)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDeployProcessExitValue(DeployTargetDetail target)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<ASBUDeviceInformation> getASBUDeviceList(int serverId, int groupNum) throws EdgeServiceFault {
		return asbuService.getASBUDeviceList(serverId, groupNum);
	}

	@Override
	public EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return nodeService.getEsxServerInformation(esxOption);
	}

	@Override
	public EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, VWWareESXNode esxNode) throws EdgeServiceFault {
		return nodeService.getEsxHostInformation(esxOption, esxNode);
	}
	
	@Override
	public List<com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor> findRemoteJobMonitor(JobDetail jobDetail) throws EdgeServiceFault {
		return jobHistoryService.findRemoteJobMonitor(jobDetail);
	}

	@Override
	public List<com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor> findJobMonitors(JobDetail jobDetail) throws EdgeServiceFault {
		return jobHistoryService.findJobMonitors(jobDetail);
	}

	@Override
	public void createJobMonitor(com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor jobMonitor) throws EdgeServiceFault {
		jobHistoryService.createJobMonitor(jobMonitor);
	}

	@Override
	public void deleteJobMonitor(com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor jobMonitor) throws EdgeServiceFault {
		jobHistoryService.deleteJobMonitor(jobMonitor);
	}
	
	@Override
	public void deleteRecoveryPointsFromDataStore(int rpsNodeId, String dataStoreUUID,
			List<String> nodeUUIDList) throws EdgeServiceFault {
		rpsDataStoreService.deleteRecoveryPointsFromDataStore(rpsNodeId, dataStoreUUID, nodeUUIDList);
	}
	
	@Override
	public List<DiscoveryItem> getDiscoveryItemList() throws EdgeServiceFault {
		return discoveryService.getDiscoveryItemList();
	}
	@Override
	public void saveDiscoverySetting(DiscoverySetting setting) throws EdgeServiceFault {
		discoveryService.saveDiscoverySetting(setting);
	}
	@Override
	public void deleteDiscoverySetting(List<DiscoverySetting> settings) throws EdgeServiceFault {
		discoveryService.deleteDiscoverySetting(settings);
	}
	@Override
	public void runDiscoveryJob(List<DiscoverySetting> settings) throws EdgeServiceFault {
		discoveryService.runDiscoveryJob(settings);
	}

	@Override
	public LicenseCheckResult checkLicenseNCE(MachineInfo machine, boolean isVM)
			throws EdgeServiceFault {
		return d2dLicenseService.checkLicenseNCE(machine, isVM);
	}

	@Override
	public List<LicenseInformation> getNceLicenseList() throws EdgeServiceFault {
		return licenseServiceNew.getNceLicenseList();
	}

	@Override
	public List<LicensedNodeInfo> getNodeListOfNceLicense()
			throws EdgeServiceFault {
		return licenseServiceNew.getNodeListOfNceLicense();
	}

	@Deprecated
	@Override
	public List<DeployTargetDetail> getDeployTargets( List<Integer> hostIds )
		throws EdgeServiceFault
	{
		return ProductDeployServiceImpl.getInstance().getDeployTargets( hostIds );
	}

	@Override
	public GatewayRegistrationResult addOrUpdateGateway(GatewayEntity entity) throws EdgeServiceFault {
		return gatewayService.addOrUpdateGateway(entity);
	}

	@Override
	public void deleteGateway(List<Integer> gatewayIds) throws EdgeServiceFault {
		gatewayService.deleteGateway(gatewayIds);
	}

	@Override
	public PagingResult<GatewayEntity> getPagingGateway(PagingConfig config) throws EdgeServiceFault {
		return gatewayService.getPagingGateway(config);
	}

	@Override
	public List<GatewayEntity> getAllGateways() throws EdgeServiceFault {
		return gatewayService.getAllGateways();
	}

	@Override
	public List<GatewayEntity> getAllValidGateways() throws EdgeServiceFault {
		return gatewayService.getAllValidGateways();
	}
	
	@Override
	public List<Node> cancelRemoteDeploy(List<Node> sourceNodes,
			String errorMessage) throws EdgeServiceFault {
		return ProductDeployServiceImpl.getInstance().cancelRemoteDeploy(sourceNodes, errorMessage);
	}

	@Override
	public boolean cancelJobForEdge(JobDetail jobDetail)
			throws EdgeServiceFault {
		return jobHistoryService.cancelJobForEdge(jobDetail);
	}


	@Override
	public ConnectionContext getASBUConnectInfo(int nodeId)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<PlanInDestination> getNodesFromDataStroe(int rpsNodeId,
			String DataStoreUUID, boolean filterNullClientUuid) throws EdgeServiceFault {
		return rpsDataStoreService.getNodesFromDataStroe(rpsNodeId, DataStoreUUID, filterNullClientUuid);
	}
	@Override
	public List<ProtectedNodeInDestination> getNodesFromShareFolder(int rpsNodeId,
			ConnectionInfo connectionInfo) throws EdgeServiceFault {
		return rpsDataStoreService.getNodesFromShareFolder(rpsNodeId, connectionInfo);
	}
	
	@Override
	public List<EdgeHostBackupStats> getBackupStats(int offSet)
			throws EdgeServiceFault {
		return nodeService.getBackupStats(offSet);
	}

	@Override
	public List<LicensedVmInfo> getVmListByHypervisor(String hypervisor)
			throws EdgeServiceFault {
		return licenseServiceNew.getVmListByHypervisor(hypervisor);
	}
	
		public void AddStorageAppliance(StorageApplianceInfo info, GatewayId gatewayId)
			throws EdgeServiceFault {
		infrastructureService.AddStorageAppliance(info, gatewayId);
	}

	@Override
	public StorageAppliancePagingResult getInfrastructureListByPaging(
			StorageAppliancePagingConfig config) throws EdgeServiceFault {
		return infrastructureService.getInfrastructureListByPaging(config);
	}

	@Override
	public void deleteInfrastructures(int[] infrastructuresIds, GatewayId gateway)
			throws EdgeServiceFault {
		infrastructureService.deleteInfrastructures(infrastructuresIds, gateway);
		
	}
	// for AQA
	@Override
	public StorageApplianceInfo getInfrastructureByHostnames(String serverIp, String dataIp)
			throws EdgeServiceFault {
		return infrastructureService.getInfrastructureByHostnames(serverIp, dataIp);
	}

	@Override
	public StorageApplianceInfo getInfrastructureById(int infraId)
			throws EdgeServiceFault {
		return infrastructureService.getInfrastructureById(infraId);
	}
	
	@Override
	public FileFolderItem getFileFolderWithCredentials(GatewayId gateway,String path,String user, String pwd) throws EdgeServiceFault{
		
		return shareFolderService.getFileFolderWithCredentials(gateway, path, user, pwd);
		
	}
	
	@Override
	public boolean createFolderOnDestination(GatewayId gateway,String parentPath, String subDir)throws EdgeServiceFault{
		
		return shareFolderService.createFolderOnDestination(gateway, parentPath, subDir);
	}
	
	@Override
	public NetworkPath[] getMappedNetworkPathOnDestination(GatewayId gateway, String userName) throws EdgeServiceFault{
		
		return shareFolderService.getMappedNetworkPathOnDestination(gateway, userName);
	}
	
	@Override
	public long getDestDriveType(GatewayId gateway, String path) throws EdgeServiceFault{
		
		return shareFolderService.getDestDriveType(gateway, path);
	
	}
	
	@Override
	public Volume[] getVolumesFromDestination(GatewayId gateway) throws EdgeServiceFault{
		return shareFolderService.getVolumesFromDestination(gateway);
	}
	
	@Override
	public String getMntPathFromVolumeGUID(GatewayId gateway, String strGUID) throws EdgeServiceFault{
		
		return shareFolderService.getMntPathFromVolumeGUID(gateway, strGUID);
	}
	
	@Override
	public long validateDest(GatewayId gateway, String path, String domain, String user, String pwd) throws EdgeServiceFault{
		
		return shareFolderService.validateDest(gateway, path, domain, user, pwd);
	}
	
	@Override
	public long validateDestForMode(GatewayId gateway, String path, String domain, String user, String pwd,int mode)throws EdgeServiceFault{
		return shareFolderService.validateDestForMode(gateway, path, domain, user, pwd, mode);
	}

	@Override
	public HostConnectInfo getD2DConnectionInfo() throws EdgeServiceFault {
	  return nodeService.getD2DConnectionInfo();
	}

	@Override
	public void testMonitorConnection(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		edgeVCMService.testMonitorConnection(monitorInfo);
	}

	@Override
	public MonitorHyperVInfo getMonitorHyperVInfo(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		return edgeVCMService.getMonitorHyperVInfo(monitorInfo);
	}

	@Override
	public void validateSource(HostConnectInfo monitorInfo, String path, String domain, String user,
			String pwd, boolean isNeedCreateFolder) throws EdgeServiceFault {
		edgeVCMService.validateSource(monitorInfo, path, domain, user, pwd, isNeedCreateFolder);
	}

	@Override
	public Volume[] getMonitorVolumes(HostConnectInfo monitorInfo) throws EdgeServiceFault {
		return edgeVCMService.getMonitorVolumes(monitorInfo);
	}
	//Jan sprint
	@Override
	public StorageApplianceValidationResponse validateNASServer(GatewayId gatewayId, StorageApplianceInfo info)
			throws EdgeServiceFault {
		return infrastructureService.validateNASServer(gatewayId, info);
	}

	@Override
	public int validateProxyInfo( GatewayId gatewayId, String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet ) throws EdgeServiceFault
	{
		return nodeService.validateProxyInfo( gatewayId,
			hostName, protocol, port, userName, password, isUseTimeRange, isUseBackupSet );
	}

	@Override
	public ResourcePool[] getResourcePool( GatewayId gatewayId,
		VirtualCenter vc, com.ca.arcflash.webservice.data.vsphere.ESXServer esxServer,
		ResourcePool parentResourcePool ) throws EdgeServiceFault
	{
		return this.policyManagementService.getResourcePool(
			gatewayId, vc, esxServer, parentResourcePool );
	}

	@Override
	public FileFolderItem getFiles(HostConnectInfo hostInfo,
			String parentFolder, String user, String password) throws EdgeServiceFault {
		return edgeVCMService.getFiles(hostInfo, parentFolder, user, password);
	}

	@Override
	public Volume[] getVolumesByHostConnect(HostConnectInfo hostInfo) throws EdgeServiceFault {
		return edgeVCMService.getVolumesByHostConnect(hostInfo);
	}

	@Override
	public boolean createFolderByHostConnect(HostConnectInfo hostInfo, String parentFolder, String folderName) throws EdgeServiceFault {
		return edgeVCMService.createFolderByHostConnect(hostInfo, parentFolder, folderName);
	}

	@Override
	public SiteId createSite( SiteInfo siteInfo ) throws EdgeServiceFault
	{
		return gatewayService.createSite( siteInfo );
	}

	@Override
	public void updateSite( SiteId siteId, SiteInfo siteInfo )
		throws EdgeServiceFault
	{
		gatewayService.updateSite( siteId, siteInfo );
	}

	@Override
	public void deleteSite( SiteId siteId ) throws EdgeServiceFault
	{
		gatewayService.deleteSite( siteId );
	}

	@Override
	public SiteInfo getSite( SiteId siteId ) throws EdgeServiceFault
	{
		return gatewayService.getSite( siteId );
	}

	@Override
	public List<SiteInfo> querySites( SiteFilter filter )
		throws EdgeServiceFault
	{
		return gatewayService.querySites( filter );
	}

	@Override
	public String generateGatewayRegistrationString(
		GenerateGatewayRegStrParam param ) throws EdgeServiceFault
	{
		return gatewayService.generateGatewayRegistrationString( param );
	}

	@Override
	public GatewayRegistrationResult registerGatewayHost(
		GatewayRegistrationInfo regInfo ) throws EdgeServiceFault
	{
		return gatewayService.registerGatewayHost( regInfo );
	}

	@Override
	public void unregisterGatewayHost( GatewayUnregistrationInfo unregInfo )
		throws EdgeServiceFault
	{
		gatewayService.unregisterGatewayHost( unregInfo );
	}
	
	@Override
	@NonSecured
	public GatewayConnectInfo gatewayLogin( GatewayLoginInfo loginInfo )
		throws EdgeServiceFault
	{
		return gatewayService.gatewayLogin( loginInfo );
	}

	@Override
	public GatewayEntity getGatewayById( GatewayId gatewayId )
		throws EdgeServiceFault
	{
		return gatewayService.getGatewayById( gatewayId );
	}

	@Override
	public void onGatewayHostRegistered( GatewayRegistrationResult regResult )
		throws EdgeServiceFault
	{
		gatewayService.onGatewayHostRegistered( regResult );
	}

	@Override
	public GatewayHostHeartbeatResponse2 gatewayHostHeartbeat( GatewayHostHeartbeatParam param )
		throws EdgeServiceFault
	{
		return gatewayService.gatewayHostHeartbeat( param );
	}

	@Override
	public void announceGatewayHostReady( String gatewayUuid,
		String gatewayHostUuid ) throws EdgeServiceFault
	{
		gatewayService.announceGatewayHostReady( gatewayUuid, gatewayHostUuid );
	}

	@Override
	public void doGatewayUpdate( List<GatewayId> gatewayIdList ) throws EdgeServiceFault
	{
		gatewayService.doGatewayUpdate( gatewayIdList );
	}

	@Override
	public GatewayUpdatesInfo getGatewayUpdateInfo( Version gatewayVersion )
		throws EdgeServiceFault
	{
		return gatewayService.getGatewayUpdateInfo( gatewayVersion );
	}

	@Override
	public void reportGatewayUpdateStatus( GatewayId gatewayId, GatewayUpdateStatus statusInfo )
		throws EdgeServiceFault
	{
		gatewayService.reportGatewayUpdateStatus( gatewayId, statusInfo );
	}

	@Override
	public GatewayId getGatewayIdByUuid( String gatewayUuid )
		throws EdgeServiceFault
	{
		return gatewayService.getGatewayIdByUuid( gatewayUuid );
	}

	@Override
	public PagingResult<DiscoveredNode> getDiscoveryADResult(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return nodeService.getDiscoveryADResult(filter, config);
	}
	
	@Override
	public List<RecoveryPointDataItem> getRecoveryPointData()  throws EdgeServiceFault{
		return nodeService.getRecoveryPointData();
	}
	
	@Override
	public List<RecoveryPointDataItem> getD2DBackupData()  throws EdgeServiceFault{
		return nodeService.getD2DBackupData();
	}

	@Override
	public long getRPSDatastoreVolumeMaxSize() throws EdgeServiceFault{
		return nodeService.getRPSDatastoreVolumeMaxSize();
	}
	
	@Override
	public Boolean IsApplianceNotConfig(String domain, String userName,
			String password) throws EdgeServiceFault {
		return commonService.IsApplianceNotConfig(domain, userName, password);
	}
	
/*	@Override
	public List<JobHistory> getJobMonitorsFromDB(int agentId,int serverId,String dataStoreUUID) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsFromDB(agentId,serverId,dataStoreUUID);
	}*/

/*	@Override
	public List<FlashJobMonitor> getJobMonitors(int nodeId,int serverId,Long jobType,Long jobId) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitors(nodeId, serverId, jobType, jobId);
	}*/

	@Override
	public List<FlashJobMonitor> getJobMonitorsForDashBoard(JobHistory jobHistory) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsForDashBoard(jobHistory);
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorsForDashBoardHistorys(List<JobHistory> historys) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsForDashBoardHistorys(historys);
	}

	@Override
	public List<FlashJobMonitor> getJobMonitorsForNodeView(int nodeId) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsForNodeView(nodeId);
	}

	@Override
	public List<FlashJobMonitor> getJobMonitorsForRpsView(int serverId, String dataStoreUUID) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsForRpsView(serverId, dataStoreUUID);
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorsForAsbuView(int serverId) throws EdgeServiceFault {
		return jobHistoryService.getJobMonitorsForAsbuView(serverId);
	}
	
	@Override
	public List<Integer> getLowVersionNodeIdsByPlanIds(List<Integer> planIds)
			throws EdgeServiceFault {
		return policyManagementService.getLowVersionNodeIdsByPlanIds(planIds);
	}
	
	@Override
	public void changeNodesCredentials(List<Integer> nodeIds, String userName,
			@NotPrintAttribute String password) throws EdgeServiceFault {
		nodeService.changeNodesCredentials(nodeIds, userName, password);
	}

    @Override
	public BackupStatusByGroup getLastBackupStatusByGroup(int groupType,
			int groupId) throws EdgeServiceFault {
		return nodeService.getLastBackupStatusByGroup(groupType, groupId);
	}

	@Override
	public String getTargetUUID(DeployTargetDetail target)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValuePair<Integer, Integer>> getPlanIdsWithTheSameNodeIds(UnifiedPolicy policy)
			throws EdgeServiceFault {
		return policyManagementService.getPlanIdsWithTheSameNodeIds(policy);
	}
	
	@Override
	public MonitorHyperVInfo getAdapterForInstantVM(HypervisorWrapper hw) throws EdgeServiceFault{
		// TODO Auto-generated method stub
		return nodeService.getAdapterForInstantVM(hw);
	}
	
	@Override
	public NodeDetail getNodetailByIpOrHostName(HypervisorWrapper hw)
			throws EdgeServiceFault {
		return nodeService.getNodetailByIpOrHostName(hw);
		
	}

	@Override
	public MonitorHyperVInfo getMonitorHyperVInfoFromConsole(
			HostConnectInfo monitorInfo) throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return instantVMService.getMonitorHyperVInfoFromConsole(monitorInfo);
	}

	@Override
	public void cutAllRemoteConnections4VM(ConnectionContext context) throws EdgeServiceFault {
		nodeService.cutAllRemoteConnections4VM(context);
		
	}

	@Override
	public FileFolderItem getFileItems4VM(ConnectionContext context, String inputFolder,
			String username, String password, boolean bIncludeFiles,
			int browseClient) throws EdgeServiceFault {
		return nodeService.getFileItems4VM(context, inputFolder, username, password, bIncludeFiles, browseClient);
	}

	@Override
	public void createFolder4VM(ConnectionContext context, String parentPath, String subDir)
			throws EdgeServiceFault {
		nodeService.createFolder4VM(context, parentPath, subDir);
	}

	@Override
	public Volume[] getVolumes4VM(ConnectionContext context) throws EdgeServiceFault {
		return nodeService.getVolumes4VM(context);
	}

	@Override
	public List<Permission> getUserAllPermissions(String username) throws EdgeServiceFault {
		return rbacService.getUserAllPermissions(username);
	}
	
	@Override
	public List<Role> getUserAllRoles(String username) throws EdgeServiceFault {
		return rbacService.getUserAllRoles(username);
	}
	//May sprint
	@Override
	public int triggerCollectDiagnosticData(Node node, DiagInfoCollectorConfiguration diagObj)
			throws EdgeServiceFault {
		
		if(node==null){
			logger.info("Node is null");
		}
		
		if(nodeService==null){
			logger.info("node service is null");
		}
		
		if(diagObj ==null){
			logger.info("diagobj is null");
		}
		return nodeService.triggerCollectDiagnosticData(node, diagObj);
		
	}

	@Override
	public int triggerCollectDiagnosticDataForLinuxNode(Node node, DiagInfoCollectorConfiguration diagObj)
			throws EdgeServiceFault {
		return nodeService.triggerCollectDiagnosticDataForLinuxNode(node, diagObj);
		
	}

	@Override
	public int triggerCollectDiagnosticDataForNodes(List<Node> nodes, DiagInfoCollectorConfiguration diagObj)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		return nodeService.triggerCollectDiagnosticDataForNodes(nodes, diagObj);
		
	}

	@Override
	public ProductImagesInfo getProductImagesInfo() throws EdgeServiceFault
	{
		return this.deployService.getProductImagesInfo();
	}
	
	@Override
	public long addD2dLog(String version, int productType, Date utcTime,
			Date localTime, int severity, long jobId, int jobType,
			int serverHostId, int targetHostId, int sourceRpsHostId,
			int targetRpsHostId, String sourceDataStoreUUID,
			String targetDataStoreUUID, String planUUID, String targetPlanUUID,
			String message) throws EdgeServiceFault {
		return logService.addD2dLog(version, productType, utcTime, localTime, severity, jobId, jobType, serverHostId, 
				targetHostId, sourceRpsHostId, targetRpsHostId, sourceDataStoreUUID, targetDataStoreUUID, planUUID, targetPlanUUID, message);
	}

    @Override
	public LogPagingResult getUnifiedLogsById(List<Long> logIds,
			LogPagingConfig config) throws EdgeServiceFault {
		return logService.getUnifiedLogsById(logIds, config);
	}

	@Override
	public SitePagingResult pageQuerySites(SiteFilter filter,
			SitePagingConfig loadConfig) throws EdgeServiceFault {
		return gatewayService.pageQuerySites(filter, loadConfig);
	}
	
	@Override
	public Boolean sendRegistrationEmail(SiteInfo siteInfo,
			EmailServerSetting setting) throws EdgeServiceFault {
		return gatewayService.sendRegistrationEmail(siteInfo, setting);
	}
	
	@Override
	public Integer sendRegistrationEmails(List<SiteId> siteParams, String consoleURL)
			throws EdgeServiceFault {
		return gatewayService.sendRegistrationEmails(siteParams, consoleURL);
	}
	
	@Override
	public int cancelMultipleJobs(List<CancelJobParameter> parameters)
			throws EdgeServiceFault {
		return jobHistoryService.cancelMultipleJobs(parameters);
	}
	
	
	@Override
	public ArchiveCloudDestInfo saveCloudAccount(ArchiveCloudDestInfo cloudInfo, GatewayId gatewayId)
			throws EdgeServiceFault {
		return cloudService.saveCloudAccount(cloudInfo, gatewayId);
		
	}

	@Override
	public Set<String> deleteCloudAccounts(int[] cloudAccountIds)
			throws EdgeServiceFault {
		return cloudService.deleteCloudAccounts(cloudAccountIds);
		
	}


	@Override
	public List<ArchiveCloudDestInfo> getCloudAccountById(int cloudAccountId)
			throws EdgeServiceFault {
		return cloudService.getCloudAccountById(cloudAccountId);
	}
	
	@Override
	public List<ArchiveCloudDestInfo> getCloudAccountsForDetails(String accountName, int id)
			throws EdgeServiceFault {
		return cloudService.getCloudAccountsForDetails(accountName, id);
	}
	
	@Override
	public String getConfigurationParam(int paramId)
			throws EdgeServiceFault {
		return configService.getConfigurationParam(paramId);
	}


	@Override
	public void inserOrUpdateConfigurationParam(int paramId, String key, String value)
			throws EdgeServiceFault {
		configService.inserOrUpdateConfigurationParam(paramId, key, value);		
	}


	@Override
	public void deleteConfigurationParam(int paramId) throws EdgeServiceFault {
		configService.deleteConfigurationParam(paramId);
	}
	
	@Override
	public void ApplianceFactoryReset(boolean preserve, boolean autoReboot) throws EdgeServiceFault {
		commonService.ApplianceFactoryReset(preserve, autoReboot);
	}

	@Override
	public String getConsoleHostName(){
		return commonService.getConsoleHostName();
	}
	
	@Override
	public int collectDiagnosticInfo(DiagInfoCollectorConfiguration diagObj,
			int hostid, boolean isLinuxBackupServer, String authUUID,
			String hostname) {
		// TODO Auto-generated method stub
		return linuxNodeService.collectDiagnosticInfo(diagObj, hostid, isLinuxBackupServer, authUUID, hostname);
	}
	
	@Override
	public int triggerCollectDiagnosticDataForConsoleNode(
			DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault {
		return nodeService.triggerCollectDiagnosticDataForConsoleNode(diagObj);
	}

	@Override
	public List<SessionPassword> validateSessionPassword(
			int rpsNodeId, List<SessionPassword> list)
			throws EdgeServiceFault {
		return rpsDataStoreService.validateSessionPassword(rpsNodeId, list);
	}


	@Override
	public void deleteActionItem( ActionItemId id ) throws EdgeServiceFault
	{
		actionCenter.deleteActionItem( id );
	}


	@Override
	public List<ActionItem> getAllActionItems() throws EdgeServiceFault
	{
		try
		{
			return actionCenter.getAllActionItems();
		}
		catch (Exception e)
		{
			logger.error( "Error getting all action items.", e );
			throw EdgeServiceFault.getFault( EdgeServiceErrorCode.Common_Service_General, e.toString() );
		}
	}
	
	@Override
	public int getASBUPlanCount(int serverId, String mediaGroupName)
			throws EdgeServiceFault {
		return asbuService.getASBUPlanCount(serverId, mediaGroupName);
	}


	@Override
	public RecoveryPointInfoForInstantVM getRecoveryPointInfo(
			int rpsNodeId, RecoveryPointInformationForCPM rp)
			throws EdgeServiceFault {
		return instantVMService.getRecoveryPointInfo(rpsNodeId, rp);
	}

	@Override
	public List<String> validateProtectedResource(String serverUUID,
			String policyUUID, String policyGlobalUUID, String archiveUUID)
			throws EdgeServiceFault {
		return d2dService.validateProtectedResource(serverUUID, policyUUID, policyGlobalUUID, archiveUUID);
	}

	@Override
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfiguationSummary(
			String planUUID) {
		return d2dService.getRpsArchiveConfiguationSummary(planUUID);
	}

	@Override
	public ProtectedNodeWithRecoveryPoints getRecoveryPointsByNode(
			DestinationBrowser browser, ProtectedNodeInDestination node,
			Date beginTime, Date endTime) throws EdgeServiceFault {
		return instantVMService.getRecoveryPointsByNode(browser, node, beginTime, endTime);
	}

	@Override
	public GatewayEntity getLocalGateway() throws EdgeServiceFault {
		return gatewayService.getLocalGateway();
	}
	
	@Override
	public String[] registerEntitlementDetails(String name, String company,
			String contactNumber, String emailID, String netSuiteId) {
		try {
			checkSession();
		} catch (EdgeServiceFault e) {
			logger.error("Exception Invoking registerEntitlementDetails " + e.getMessage());
		}
		return aerpWebServiceImpl.registerEntitlementDetails(name, company, contactNumber, emailID, netSuiteId);
	}

	@Override
	public String submitAERPJob() {
		try {
			checkSession();
		} catch (EdgeServiceFault e) {
			logger.error("Exception Invoking submitAERPJob Scheduler Service " + e.getMessage());
		}
		return aerpWebServiceImpl.submitAERPJob();
	}

	@Override
	public String isActivated() {
		try {
			checkSession();
		} catch (EdgeServiceFault e) {
			logger.error("Exception Invoking isActivated Service " + e.getMessage());
		}
		return aerpWebServiceImpl.isActivated();
	}
	
	@Override
	public String cancelRegistration(String name, String company, String contactNumber, String emailID, String netSuiteId) {
			try {
				checkSession();
			} catch (EdgeServiceFault e) {
				logger.error("Exception Invoking cancelRegistration Service " + e.getMessage());
			}
			return aerpWebServiceImpl.cancelRegistration(name, company, contactNumber, emailID, netSuiteId);
	}
	
	@Override
	public int queryEdgeMgrStatusForNode(int nodeID) throws EdgeServiceFault {
		return instantVMService.queryEdgeMgrStatusForNode(nodeID);
	}

	@Override
	public int saveVMWareInfoToDB(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return instantVMService.saveVMWareInfoToDB(esxOption);
	}

	@Override
	public int saveHyperVInfoToDB(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		return instantVMService.saveHyperVInfoToDB(hyperVOption);
	}


//	@Override
//	public PrecheckResult checkRecoveryServer(HypervisorType type, NodeRegistrationInfo regInfo) throws EdgeServiceFault {
//		return instantVMService.checkRecoveryServer(type, regInfo);
//	}


	@Override
	public RecoveryServerResult validateRecoveryServerConnectAndManage(
			HypervisorType type, Node agent,
			GatewayEntity gateway, boolean isLinux, boolean isRps,
			boolean isHyperV) throws EdgeServiceFault {

		return instantVMService.validateRecoveryServerConnectAndManage(type, agent, gateway, isLinux, isRps, isHyperV);
	}


	@Override
	public RecoveryServerResult validateRecoveryServerAddRPSAndHypervNode(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux, boolean isRps)
			throws EdgeServiceFault {

		return instantVMService.validateRecoveryServerAddRPSAndHypervNode(type, detail, gateway, isLinux, isRps);
	}


	@Override
	public RecoveryServerResult validateRecoveryServerUpdateNode(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault {

		return instantVMService.validateRecoveryServerUpdateNode(type, detail, gateway, isLinux);
	}


	@Override
	public RecoveryServerResult validateRecoveryServerVersionAndInstall(
			HypervisorType type, NodeDetail detail,
			GatewayEntity gateway, boolean isLinux) throws EdgeServiceFault {

		return instantVMService.validateRecoveryServerVersionAndInstall(type, detail, gateway, isLinux);
	}
	
	@Override
	public RecoveryServerResult serverAndNFScheck(NodeRegistrationInfo regInfo,
			HypervisorType type, boolean addNode) throws EdgeServiceFault{
		return instantVMService.serverAndNFScheck(regInfo, type, addNode);
	}
	
	
	@Override
	public void startReplicationNow(int rpsNodeId,
			List<ManualReplicationItem> replicationitems)
			throws EdgeServiceFault {
		rpsDataStoreService.startReplicationNow(rpsNodeId ,replicationitems);
	}
	
	@Override
	public void deleteShareFolderByid(int destinationId) throws EdgeServiceFault {
		shareFolderService.deleteShareFolderByid(destinationId);
	}


	@Override
	public void setWarnningAcknowledged(List<Integer> nodeIds) throws EdgeServiceFault {
		nodeService.setWarnningAcknowledged(nodeIds);
	}


	@Override
	public List<ManualReplicationRPSParam> getReplicationRpsParamsByPolicyName(
			String policyName) throws EdgeServiceFault {
		return policyManagementService.getReplicationRpsParamsByPolicyName(policyName);
	}
	
	@Override
	public int isASBUAgentInstalled(int nodeId) throws EdgeServiceFault {
		return nodeService.isASBUAgentInstalled(nodeId);
	}
	
	@Override
	public Integer backupNodesByPolicyIdList(List<Integer> policyIdList,
			int backupType, String jobName) throws EdgeServiceFault {
		return policyManagementService.backupNodesByPolicyIdList(policyIdList, backupType, jobName);		
	}


	@Override
	public DiscoveryHyperVEntityInfo getHyperVTreeRootEntity(
			DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		return nodeService.getHyperVTreeRootEntity(hyperVOption);
	}


	@Override
	public void startMergeNow(int rpsNodeId, List<ManualMergeItem> mergeItems)
			throws EdgeServiceFault {
		rpsNodeService.startMergeNow(rpsNodeId, mergeItems);		
	}


	@Override
	public List<RecoveryPoint> getRecoveryPointsByNodeList(int rpsNodeId,
			List<ProtectedNodeInDestination> nodeList, Date beginTime,
			Date endTime) throws EdgeServiceFault {
		return recoveryPointService.getRecoveryPointsByNodeList(rpsNodeId, nodeList, beginTime, endTime);
	}


	@Override
	public void verifyFCPFC(List<Integer> nodes) throws EdgeServiceFault {
		nodeService.verifyFCPFC(nodes);
	}


	@Override
	public List<RpsArchiveConfiguationWrapper> getRpsArchiveConfigSummary(
			String planUUID, boolean isNeedToEncr) {
		return policyManagementService.getRpsArchiveConfigSummary(planUUID, isNeedToEncr);
	}
	
	@Override
	public GatewayEntity getGatewayByHostId(int nodeId) throws EdgeServiceFault {
		return nodeService.getGatewayByHostId(nodeId);
	}


	@Override
	public int checkNodeExist(int gatewayId, String hostName)
			throws EdgeServiceFault {
		return nodeService.checkNodeExist(gatewayId, hostName);
	}


	@Override
	public String getLinuxVersionInfo(int nodeId) throws EdgeServiceFault {
		return linuxNodeService.getLinuxVersionInfo(nodeId);
	}
	
	@Override
	public ShareFolderDestinationInfo getSharedFolderWithpassword(
			int destinationId) throws EdgeServiceFault {
		return shareFolderService.getSharedFolderWithpassword(destinationId);
	}
	
	@Override
	public void updateSharedFolder(String destination, String username,
			String password, GatewayId gatewayId) throws EdgeServiceFault {
		shareFolderService.updateSharedFolder(destination, username, password, gatewayId);
	}


	@Override
	public PagingResult<ArchiveCloudDestInfo> getAllCloudAccountsByPaging(
			PagingConfig config, int gatewayId) throws EdgeServiceFault {
		return cloudService.getAllCloudAccountsByPaging(config, gatewayId);
	}


	@Override
	public List<ArchiveCloudDestInfo> getAllCloudAccounts(int cloudType,
			int cloudSubType, List<Integer> existingCloudAccountIDs,
			int currentGatewayId) throws EdgeServiceFault {
		return cloudService.getAllCloudAccounts(cloudType,cloudSubType, existingCloudAccountIDs, currentGatewayId);
	}

	@Override
	public List<Integer> checkRPSVersion(List<Integer> policyIdList)
			throws EdgeServiceFault {
		return policyManagementService.checkRPSVersion(policyIdList);
	}


	@Override
	public List<Node> getLinuxBackupserverList(int gatewayId)
			throws EdgeServiceFault {
		return nodeService.getLinuxBackupserverList(gatewayId);
	}

	@Override
	public List<ManualFilecopyParam> getFilecopyParamsByPolicyId(
			long policyId) throws EdgeServiceFault {
		return policyManagementService.getFilecopyParamsByPolicyId(policyId);
	}


	@Override
	public void startFilecopyNow(int rpsNodeId,
			List<ManualFilecopyItem> filecopyItems)
			throws EdgeServiceFault {
		rpsNodeService.startFilecopyNow(rpsNodeId, filecopyItems);
		
	}


	@Override
	public void startAgentFilecopyNow(List<Integer> nodeIdList)
			throws EdgeServiceFault {
		nodeService.startAgentFilecopyNow(nodeIdList);
		
	}


	@Override
	public List<ManualFilecopyParam> getFileArchiveParamsByPolicyId(
			long policyId) throws EdgeServiceFault {
		return policyManagementService.getFileArchiveParamsByPolicyId(policyId);
	}
	
	@Override
	public void startFileArchiveNow(int rpsNodeId,
			List<ManualFilecopyItem> filearchiveItems) throws EdgeServiceFault {
		rpsNodeService.startFileArchiveNow(rpsNodeId, filearchiveItems);
	}


	@Override
	public void startAgentFileArchiveNow(List<Integer> nodeIdList)
			throws EdgeServiceFault {
		nodeService.startAgentFileArchiveNow(nodeIdList);
		
	}
	
	@Override
	public void bindPolicyD2DRole(int policyId, int hostId, D2DRole d2dRole)
			throws EdgeServiceFault {
		nodeService.bindPolicyD2DRole(policyId, hostId, d2dRole);
	}
	
	@Override
	public void updateRegInfoOfExistingNode(GatewayId gatewayId, int nodeId)
			throws EdgeServiceFault {
		gatewayService.updateRegInfoOfExistingNode(gatewayId, nodeId);
		
	}
}