package com.ca.arcserve.edge.app.base.webservice.node;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.EsxHostInformation;
import com.ca.arcflash.ha.model.EsxServerInformation;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.jobscript.failover.DNSUpdaterParameters;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.Gateway;
import com.ca.arcflash.jobscript.failover.IPAddressInfo;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.rps.webservice.data.RpsArchiveConfiguationWrapper;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.data.MachineType;
import com.ca.arcflash.webservice.data.VWWareESXNode;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.data.d2dstatus.VMPowerStatus;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.internal.DiagInfoCollectorConfigurationXMLDAO;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcserve.edge.app.base.appdaos.AuthUuidWrapper;
import com.ca.arcserve.edge.app.base.appdaos.EdgeASDataSyncSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DDataSyncSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDiscoverySetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDiscoverySettingModel;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeFilter;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperVVerifyStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeIntegerValue;
import com.ca.arcserve.edge.app.base.appdaos.EdgeJobHistory;
import com.ca.arcserve.edge.app.base.appdaos.EdgeNetworkConfiguration;
import com.ca.arcserve.edge.app.base.appdaos.EdgeNodeDeleteProbingSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicyDeployWarningErrorMessage;
import com.ca.arcserve.edge.app.base.appdaos.EdgeProtectedResource;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSRMProbingSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSourceGroup;
import com.ca.arcserve.edge.app.base.appdaos.EdgeStandbyVMNetworkInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeStringValue;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSyncStatus;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMStorage;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeD2DSyncDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVCMDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSBDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeVSphereDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.common.SqlUtil;
import com.ca.arcserve.edge.app.base.common.connection.ConverterConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.MonitorConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.NodeConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.resources.messages.WebServiceFaultMessageRetriever;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.AutoDiscoveryJob;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.schedulers.NodeDeleteJob;
import com.ca.arcserve.edge.app.base.schedulers.SchedulerHelp;
import com.ca.arcserve.edge.app.base.schedulers.SrmJob;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.HostInfoCache;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan.VSBTaskDeployment;
import com.ca.arcserve.edge.app.base.serviceexception.D2DServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeService;
import com.ca.arcserve.edge.app.base.webservice.INodeAdService;
import com.ca.arcserve.edge.app.base.webservice.INodeEsxService;
import com.ca.arcserve.edge.app.base.webservice.INodeRHAService;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.ISessionPasswordService;
import com.ca.arcserve.edge.app.base.webservice.action.ActionTaskManager;
import com.ca.arcserve.edge.app.base.webservice.arcserve.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceClientProxy;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.action.BackupNowTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ManageMultiNodesParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.action.UpdateMultiNodesParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncManageStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.CollectionUtils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.common.DeployCommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeRegistryInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.NodeStatusUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ServiceState;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ShowEULAModule;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DBackupType;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DEstimatedValue;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DJobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DMergeJobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.D2DStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.RepJobMonitor4Edge;
import com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus.VCMStorage;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.RecoveryPointDataItem;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySettingForHyperV;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.BaseFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.filter.FilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.instantvm.HypervisorWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting.ASBUSettingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult.AddNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult.NodeManagedStatusByConsole;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AdminAccountValidationResultWithMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.CSVObject;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DBackupJobStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.D2DSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DNSForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVmwareEntityInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ESXServer;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHostBackupStats;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ExportNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.GatewayForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.IPAddressInfoForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.IPSettingForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NetworkDiffInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NetworkInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeBkpStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDeleteSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManageResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfoForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfoForRHA;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfoForVcloud;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSyncStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterEditingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterSavingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenario;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResultForLinux;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RestorableNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SRMSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SessionPassword;
import com.ca.arcserve.edge.app.base.webservice.contract.node.SourceMachineNetworkAdapterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.StandbyVMNetworkInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VCMConverterType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.VMVerifyStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.WinsForADR;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ArcserveInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ConverterSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.D2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.GatewaySummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.JobSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.LinuxD2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.PlanSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ProxyInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.RemoteDeployInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.VmInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.VsbSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.BitmapFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilterGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter.NodeFilterType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ParsedBackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployFlags;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicySortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResource;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ConversionTask;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanEnableStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.PlanTaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.reportdashboard.BackupStatusByGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.Task;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.EsxVSphere;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VSphereProxyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityRelationType;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.VsphereEntityType;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAParameters;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAResult;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DAllJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DConversionJobsStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DJobsStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.D2DMergeJobStatusCache;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.d2dreg.EdgeD2DRegServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMManager;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMServiceUtil;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseService;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.monitor.ImportNodesJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorManager;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryManager;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryService;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.HyperVManagerAdapter;
import com.ca.arcserve.edge.app.base.webservice.node.exportimport.NodeExporter;
import com.ca.arcserve.edge.app.base.webservice.node.exportimport.NodeImporter;
import com.ca.arcserve.edge.app.base.webservice.node.filter.NodeFilterLoader;
import com.ca.arcserve.edge.app.base.webservice.node.filter.NodeFilterResult;
import com.ca.arcserve.edge.app.base.webservice.node.filter.NodeGroupFilterLoader;
import com.ca.arcserve.edge.app.base.webservice.node.filter.NodeSortLoader;
import com.ca.arcserve.edge.app.base.webservice.node.hypervisor.HypervisorSpecifier;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetPolicyUuidException;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductDeployServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.storageappliance.HBBUProxy;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncArcserveIncJob;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;
import com.ca.arcserve.edge.app.base.webservice.vcm.VCMServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsConnectionInfoDao;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class NodeServiceImpl implements INodeService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(NodeServiceImpl.class);
	
	//private IEdgeSchedulerDao scheduleDao = DaoFactory.getDao(IEdgeSchedulerDao.class);
	IEdgeSettingDao settingDao = DaoFactory.getDao(IEdgeSettingDao.class);
	IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	IEdgeJobHistoryDao historyDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	IEdgeD2DSyncDao syncDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
	IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	IEdgeVCMDao vcmDao = DaoFactory.getDao(IEdgeVCMDao.class);
	IEdgeVSBDao vsbDao = DaoFactory.getDao(IEdgeVSBDao.class);
	IEdgeGatewayDao gatewayDao = DaoFactory.getDao(IEdgeGatewayDao.class);

	private static IEdgeConnectInfoDao staticConnectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private static IEdgeD2DSyncDao staticSyncDao = DaoFactory.getDao(IEdgeD2DSyncDao.class);
	private static IEdgeHypervisorDao hypervisorDao = DaoFactory.getDao(IEdgeHypervisorDao.class);

	EdgeWebServiceImpl serviceImpl;
	private INodeAdService adService = new NodeAdServiceImpl();
	private INodeEsxService esxService = new NodeEsxServiceImpl();
	private INodeRHAService rhaService = new NodeRHAServiceImpl();
	private LinuxNodeServiceImpl linuxNodeService = new LinuxNodeServiceImpl();
	private ILicenseService licenseService;
	private ISessionPasswordService sessionPasswordService;
	private ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	private EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();
	private static final String htmlSpecialCharsRegx = "[<>\\\\/&]";
	Pattern htmlPattern = Pattern.compile(htmlSpecialCharsRegx, Pattern.UNICODE_CASE);
	VCMServiceImpl vcmService;
	NativeFacade  nativeFacade = new NativeFacadeImpl();
    private IEdgeCommonService commonService = new EdgeCommonServiceImpl();
	private static final String SEMICOLON = ";";
	private static final String COLON = ":";

	private DiscoveryManager discoveryMgr = DiscoveryManager.getInstance();
	
	private static final String TYPE_HYPERV = "HYPERV";
	private static final String TYPE_ESX = "ESX";
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	private RPSNodeServiceImpl rpsNodeService = new RPSNodeServiceImpl();
	
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
	private static final String logCollectorUtilityPath = EdgeCommonUtil.EdgeInstallPath + "\\" + CommonUtil.BaseEdgeBIN_DIR + "DiagnosticUtility";
	private static final String logCollectorConfigXMLPath = logCollectorUtilityPath + "\\DiagInfoCollectorConfig.xml";
	
	private static IEdgeExternalLinks edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	
	public void setNativeFacade(NativeFacade nativeFacadeImpl)
	{
		this.nativeFacade = nativeFacadeImpl;
	}
	public NodeServiceImpl(){
		this(null);
	}

	private boolean checkHTMLChars(String input){
		Matcher matcher = htmlPattern.matcher(input);
		return matcher.find();
	}

	public NodeServiceImpl(EdgeWebServiceImpl serviceImpl){
		this.serviceImpl = serviceImpl;
		
		this.vcmService = new VCMServiceImpl();
		this.licenseService = new LicenseServiceImpl();
	}

	@Override
	public List<ServerInfo> getServers() {
		return null;
	}

	@Override
	public ASBUSetting getASBUSetting(int branchID) throws EdgeServiceFault {
		ASBUSetting setting = new ASBUSetting();
		setting.setBranchID(branchID);
		List<EdgeASDataSyncSetting> edgeDataSyncSettings = new ArrayList<EdgeASDataSyncSetting>();
		settingDao.as_edge_asdatasync_setting_get(branchID, edgeDataSyncSettings);

		if (!edgeDataSyncSettings.isEmpty()) {
			EdgeASDataSyncSetting edgeDataSyncSetting = edgeDataSyncSettings
					.get(0);

			setting.setSchedule(getASBUSchedule(edgeDataSyncSetting));
			setting.setRetryInterval(edgeDataSyncSetting.getRetryInterval());
			setting.setRetryTimes(edgeDataSyncSetting.getRetryTimes());
			setting.setSyncFilePath(parseFilePath(edgeDataSyncSetting.getSyncFilepath()));
			setting.setStatus(ASBUSetting.ASBUSettingStatus.parseInt(edgeDataSyncSetting.getStatus()));
		}
		else {
			setting.setSyncFilePath(parseFilePath(setting.getSyncFilePath()));
		}

		return setting;
	}

	@Override
	public ASBUSetting getGlobalASBUSetting() throws EdgeServiceFault {
		return getASBUSetting(0);
	}
	
	@Override
	public void backupForEDGE(int id, String hostname, int backupType, String value, boolean convert) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(id)) {
			logger.info("[NodeServiceImpl] submit a backup job for the node: "+hostname+", the node id is: "+id);
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
			if (regStatus == 2) {
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();				
				if (edgeInfo != null) {				
					String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
					if(!StringUtil.isEmptyOrNull(consoleName))
						CurRegisteredEdgeHostName = consoleName;
				}
				EdgeServiceFaultBean b = new EdgeServiceFaultBean(EdgeServiceErrorCode.Backup_NodeManagedByOthers, CurRegisteredEdgeHostName);
				b.setMessageParameters(new String[]{CurRegisteredEdgeHostName});
				throw new EdgeServiceFault(CurRegisteredEdgeHostName,b);
			}
			
			connection.getService().backupWithFlag(backupType, value, convert);
		}
	}
	
	@Override
	public D2DSetting getD2DSetting(int branchID) throws EdgeServiceFault {
		D2DSetting setting = new D2DSetting();
		List<EdgeD2DDataSyncSetting> edgeDataSyncSettings = new ArrayList<EdgeD2DDataSyncSetting>();
		settingDao.as_edge_d2ddatasync_setting_get(branchID, edgeDataSyncSettings);

		if (!edgeDataSyncSettings.isEmpty()) {
			EdgeD2DDataSyncSetting edgeDataSyncSetting = edgeDataSyncSettings
					.get(0);
			setting.setRetryInterval(edgeDataSyncSetting.getRetryInterval());
			setting.setRetryTimes(edgeDataSyncSetting.getRetryTimes());
		}

		return setting;
	}

	@Override
	public D2DSetting getGlobalD2DSetting() throws EdgeServiceFault {
		return getD2DSetting(0);
	}

	@Override
	public SRMSetting getGlobalSRMSetting() throws EdgeServiceFault {
		SRMSetting setting = new SRMSetting();
		List<EdgeSRMProbingSetting> edgeSRMProbingSettings = new ArrayList<EdgeSRMProbingSetting>();
		settingDao.as_edge_srmprobing_setting_get(edgeSRMProbingSettings);

		if (!edgeSRMProbingSettings.isEmpty()) {
			EdgeSRMProbingSetting edgeSRMProbingSetting = edgeSRMProbingSettings
					.get(0);

			setting.setConcurrentThreadCount(edgeSRMProbingSetting
					.getThreadCount());
			setting.setRetryInterval(edgeSRMProbingSetting.getRetryInterval());
			setting.setRetryTimes(edgeSRMProbingSetting.getRetryTimes());
			setting.setTimeOut(edgeSRMProbingSetting.getTimeout());
			setting.setStatus(SRMSetting.SRMSettingStatus.parseInt(edgeSRMProbingSetting.getStatus()));

			ScheduleData schedule = getSRMSchedule(edgeSRMProbingSetting);
			setting.setSchedule(schedule);
		}

		return setting;
	}

	@Override
	public NodeDeleteSetting getGlobalNodeDeleteSetting() throws EdgeServiceFault {
		NodeDeleteSetting setting = new NodeDeleteSetting();
		List<EdgeNodeDeleteProbingSetting> edgeNodeDeleteProbingSettings = new ArrayList<EdgeNodeDeleteProbingSetting>();
		settingDao.as_edge_nodedeleteprobing_setting_get(edgeNodeDeleteProbingSettings);

		if (!edgeNodeDeleteProbingSettings.isEmpty()) {
			EdgeNodeDeleteProbingSetting edgeNodeDeleteProbingSetting = edgeNodeDeleteProbingSettings
					.get(0);

			setting.setConcurrentThreadCount(edgeNodeDeleteProbingSetting
					.getThreadCount());
			setting.setRetryInterval(edgeNodeDeleteProbingSetting.getRetryInterval());
			setting.setRetryTimes(edgeNodeDeleteProbingSetting.getRetryTimes());
			setting.setTimeOut(edgeNodeDeleteProbingSetting.getTimeout());
			setting.setStatus(NodeDeleteSetting.NodeDeleteSettingStatus.parseInt(edgeNodeDeleteProbingSetting.getStatus()));

			ScheduleData schedule = getNodeDeleteSchedule(edgeNodeDeleteProbingSetting);
			setting.setSchedule(schedule);
		}

		return setting;
	}

	@Override
	public void saveASBUSetting(int branchID, ASBUSetting setting) throws EdgeServiceFault {
		if (setting.getStatus() == ASBUSetting.ASBUSettingStatus.enabled) {
			if (!isSyncFolderValid(setting.getSyncFilePath())) {
				throw EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Configuration_InvalidASBUSyncFolder,"");
			}
		}

		EdgeASDataSyncSetting edgeASDataSyncSetting = new EdgeASDataSyncSetting();
		ScheduleData scheduleData = setting.getSchedule();
		scheduleData.setStartFromDate(generateRepeatFromDateByScheduleTime(scheduleData.getScheduleTime()));
		String repeatMethodParameter = SchedulerHelp.createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = SchedulerHelp.createRepeatUntilParameter(scheduleData);

		edgeASDataSyncSetting.setActionTime(scheduleData.getScheduleTime());
		edgeASDataSyncSetting.setActionType(2);
		edgeASDataSyncSetting.setBranchid(setting.getBranchID());
		edgeASDataSyncSetting.setRepeatFrom(scheduleData.getStartFromDate());
		edgeASDataSyncSetting.setRepeatParam(repeatUntilParameter);
		edgeASDataSyncSetting.setRepeatType(scheduleData.getRepeatUntilType().getValue());
		edgeASDataSyncSetting.setRetryInterval(setting.getRetryInterval());
		edgeASDataSyncSetting.setRetryTimes(setting.getRetryTimes());
		edgeASDataSyncSetting.setScheduleDescription(scheduleData.getScheduleDescription());
		edgeASDataSyncSetting.setScheduleID(scheduleData.getScheduleID());
		edgeASDataSyncSetting.setScheduleName(scheduleData.getScheduleName());
		edgeASDataSyncSetting.setScheduleParam(repeatMethodParameter);
		edgeASDataSyncSetting.setScheduleType(scheduleData.getRepeatMethodData().getRepeatMethodType().getValue());
		edgeASDataSyncSetting.setStatus((setting.getStatus()).getValue());
		edgeASDataSyncSetting.setSyncFilepath(setting.getSyncFilePath());

		settingDao.as_edge_asdatasync_setting_set(edgeASDataSyncSetting.getSyncFilepath(),
				edgeASDataSyncSetting.getRetryTimes(),
				edgeASDataSyncSetting.getRetryInterval(),
				edgeASDataSyncSetting.getStatus(),
				edgeASDataSyncSetting.getScheduleID(),
				edgeASDataSyncSetting.getScheduleName(),
				edgeASDataSyncSetting.getScheduleDescription(),
				2,
				edgeASDataSyncSetting.getScheduleType(),
				edgeASDataSyncSetting.getScheduleParam(),
				edgeASDataSyncSetting.getActionTime(),
				edgeASDataSyncSetting.getRepeatFrom(),
				edgeASDataSyncSetting.getRepeatType(),
				edgeASDataSyncSetting.getRepeatParam(),
				edgeASDataSyncSetting.getBranchid());

		if (scheduleData.getScheduleID() <= 0) {
			ASBUSetting asbuSetting = getASBUSetting(edgeASDataSyncSetting.getBranchid());
			scheduleData = asbuSetting.getSchedule();
		}

		if (setting.getStatus() == ASBUSetting.ASBUSettingStatus.enabled) {
			try {
				SchedulerUtilsImpl.getInstance().updateScheduler(SyncArcserveIncJob.getInstance(),
						scheduleData);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
		else {
			try {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(scheduleData.getScheduleID());
				SchedulerUtilsImpl.getInstance().removeIDs(SyncArcserveIncJob.getInstance(),
						ids);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void saveGlobalASBUSetting(ASBUSetting setting) throws EdgeServiceFault {
		saveASBUSetting(0, setting);
	}

	@Override
	public void saveD2DSetting(int branchID, D2DSetting setting) throws EdgeServiceFault {
		settingDao.as_edge_d2ddatasync_setting_set(setting.getRetryTimes(),
				setting.getRetryInterval(), 0, branchID);
	}

	@Override
	public void saveGlobalD2DSetting(D2DSetting setting) throws EdgeServiceFault {
		saveD2DSetting(0, setting);
	}

	@Override
	public void saveGlobalSRMSetting(SRMSetting setting) throws EdgeServiceFault {
		EdgeSRMProbingSetting edgeSRMSetting = new EdgeSRMProbingSetting();
		ScheduleData scheduleData = setting.getSchedule();
		scheduleData.setStartFromDate(generateRepeatFromDateByScheduleTime(scheduleData.getScheduleTime()));
		String repeatMethodParameter = SchedulerHelp.createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = SchedulerHelp.createRepeatUntilParameter(scheduleData);

		edgeSRMSetting.setActionTime(scheduleData.getScheduleTime());
		edgeSRMSetting.setActionType(3);
		edgeSRMSetting.setRepeatFrom(scheduleData.getStartFromDate());
		edgeSRMSetting.setRepeatParam(repeatUntilParameter);
		edgeSRMSetting.setRepeatType(scheduleData.getRepeatUntilType().getValue());
		edgeSRMSetting.setRetryInterval(setting.getRetryInterval());
		edgeSRMSetting.setRetryTimes(setting.getRetryTimes());
		edgeSRMSetting.setScheduleDescription(scheduleData.getScheduleDescription());
		edgeSRMSetting.setScheduleID(scheduleData.getScheduleID());
		edgeSRMSetting.setScheduleName(scheduleData.getScheduleName());
		edgeSRMSetting.setScheduleParam(repeatMethodParameter);
		edgeSRMSetting.setScheduleType(scheduleData.getRepeatMethodData().getRepeatMethodType().getValue());
		edgeSRMSetting.setStatus((setting.getStatus()).getValue());
		edgeSRMSetting.setThreadCount(setting.getConcurrentThreadCount());
		edgeSRMSetting.setTimeout(setting.getTimeOut());
		edgeSRMSetting.setProbeFilter("");

		settingDao.as_edge_srmprobing_setting_set(
				edgeSRMSetting.getProbeFilter(),
				edgeSRMSetting.getThreadCount(),
				edgeSRMSetting.getTimeout(),
				edgeSRMSetting.getRetryTimes(),
				edgeSRMSetting.getRetryInterval(),
				edgeSRMSetting.getStatus(),
				edgeSRMSetting.getScheduleID(),
				edgeSRMSetting.getScheduleName(),
				edgeSRMSetting.getScheduleDescription(),
				edgeSRMSetting.getActionType(),
				edgeSRMSetting.getScheduleType(),
				edgeSRMSetting.getScheduleParam(),
				edgeSRMSetting.getActionTime(),
				edgeSRMSetting.getRepeatFrom(),
				edgeSRMSetting.getRepeatType(),
				edgeSRMSetting.getRepeatParam());

		if (setting.getStatus() == SRMSetting.SRMSettingStatus.enabled) {
			try {
				SchedulerUtilsImpl.getInstance().updateScheduler(SrmJob.getInstance(),
						scheduleData);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
		else {
			try {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(scheduleData.getScheduleID());
				SchedulerUtilsImpl.getInstance().removeIDs(SrmJob.getInstance(),
						ids);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void saveGlobalNodeDeleteSetting(NodeDeleteSetting setting) throws EdgeServiceFault {
		EdgeNodeDeleteProbingSetting edgeNodeDeleteSetting = new EdgeNodeDeleteProbingSetting();
		ScheduleData scheduleData = setting.getSchedule();
		scheduleData.setStartFromDate(generateRepeatFromDateByScheduleTime(scheduleData.getScheduleTime()));
		String repeatMethodParameter = SchedulerHelp.createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = SchedulerHelp.createRepeatUntilParameter(scheduleData);

		edgeNodeDeleteSetting.setActionTime(scheduleData.getScheduleTime());
		edgeNodeDeleteSetting.setActionType(2);
		edgeNodeDeleteSetting.setRepeatFrom(scheduleData.getStartFromDate());
		edgeNodeDeleteSetting.setRepeatParam(repeatUntilParameter);
		edgeNodeDeleteSetting.setRepeatType(scheduleData.getRepeatUntilType().getValue());
		edgeNodeDeleteSetting.setRetryInterval(setting.getRetryInterval());
		edgeNodeDeleteSetting.setRetryTimes(setting.getRetryTimes());
		edgeNodeDeleteSetting.setScheduleDescription(scheduleData.getScheduleDescription());
		edgeNodeDeleteSetting.setScheduleID(scheduleData.getScheduleID());
		edgeNodeDeleteSetting.setScheduleName(scheduleData.getScheduleName());
		edgeNodeDeleteSetting.setScheduleParam(repeatMethodParameter);
		edgeNodeDeleteSetting.setScheduleType(scheduleData.getRepeatMethodData().getRepeatMethodType().getValue());
		edgeNodeDeleteSetting.setStatus((setting.getStatus()).getValue());
		edgeNodeDeleteSetting.setThreadCount(setting.getConcurrentThreadCount());
		edgeNodeDeleteSetting.setTimeout(setting.getTimeOut());
		edgeNodeDeleteSetting.setProbeFilter("");

		settingDao.as_edge_nodeDelete_setting_set(
				edgeNodeDeleteSetting.getProbeFilter(),
				edgeNodeDeleteSetting.getThreadCount(),
				edgeNodeDeleteSetting.getTimeout(),
				edgeNodeDeleteSetting.getRetryTimes(),
				edgeNodeDeleteSetting.getRetryInterval(),
				edgeNodeDeleteSetting.getStatus(),
				edgeNodeDeleteSetting.getScheduleID(),
				edgeNodeDeleteSetting.getScheduleName(),
				edgeNodeDeleteSetting.getScheduleDescription(),
				edgeNodeDeleteSetting.getActionType(),
				edgeNodeDeleteSetting.getScheduleType(),
				edgeNodeDeleteSetting.getScheduleParam(),
				edgeNodeDeleteSetting.getActionTime(),
				edgeNodeDeleteSetting.getRepeatFrom(),
				edgeNodeDeleteSetting.getRepeatType(),
				edgeNodeDeleteSetting.getRepeatParam());

		if (setting.getStatus() == NodeDeleteSetting.NodeDeleteSettingStatus.enabled) {
			try {
				SchedulerUtilsImpl.getInstance().updateScheduler(NodeDeleteJob.getInstance(),
						scheduleData);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
		else {
			try {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(scheduleData.getScheduleID());
				SchedulerUtilsImpl.getInstance().removeIDs(NodeDeleteJob.getInstance(),
						ids);
			} catch (EdgeSchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
		NodeDeleteJob.init();
	}



	private ScheduleData getASBUSchedule(EdgeASDataSyncSetting edgeDataSyncSetting) {
		ScheduleData scheduleData = new ScheduleData();

		scheduleData.setScheduleID(edgeDataSyncSetting.getScheduleID());
		scheduleData.setScheduleName(edgeDataSyncSetting.getScheduleName());
		scheduleData.setScheduleDescription(edgeDataSyncSetting.getScheduleDescription());
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(edgeDataSyncSetting.getScheduleType()));
		SchedulerHelp.setRepeartData(edgeDataSyncSetting.getScheduleParam(), scheduleData);
		scheduleData.setScheduleTime(edgeDataSyncSetting.getActionTime());
		scheduleData.setStartFromDate(edgeDataSyncSetting.getRepeatFrom());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType.parseInt(edgeDataSyncSetting.getRepeatType()));
		SchedulerHelp.setRepeatUntilParameterRelatedValues(edgeDataSyncSetting.getRepeatParam(), scheduleData);

		return scheduleData;
	}

	private ScheduleData getSRMSchedule(EdgeSRMProbingSetting edgeDataSyncSetting) {
		ScheduleData scheduleData = new ScheduleData();

		scheduleData.setScheduleID(edgeDataSyncSetting.getScheduleID());
		scheduleData.setScheduleName(edgeDataSyncSetting.getScheduleName());
		scheduleData.setScheduleDescription(edgeDataSyncSetting.getScheduleDescription());
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(edgeDataSyncSetting.getScheduleType()));
		SchedulerHelp.setRepeartData(edgeDataSyncSetting.getScheduleParam(), scheduleData);
		scheduleData.setScheduleTime(edgeDataSyncSetting.getActionTime());
		scheduleData.setStartFromDate(edgeDataSyncSetting.getRepeatFrom());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType.parseInt(edgeDataSyncSetting.getRepeatType()));
		SchedulerHelp.setRepeatUntilParameterRelatedValues(edgeDataSyncSetting.getRepeatParam(), scheduleData);

		return scheduleData;
	}

	private ScheduleData getNodeDeleteSchedule(EdgeNodeDeleteProbingSetting edgeDataSyncSetting) {
		ScheduleData scheduleData = new ScheduleData();

		scheduleData.setScheduleID(edgeDataSyncSetting.getScheduleID());
		scheduleData.setScheduleName(edgeDataSyncSetting.getScheduleName());
		scheduleData.setScheduleDescription(edgeDataSyncSetting.getScheduleDescription());
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(edgeDataSyncSetting.getScheduleType()));
		SchedulerHelp.setRepeartData(edgeDataSyncSetting.getScheduleParam(), scheduleData);
		scheduleData.setScheduleTime(edgeDataSyncSetting.getActionTime());
		scheduleData.setStartFromDate(edgeDataSyncSetting.getRepeatFrom());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType.parseInt(edgeDataSyncSetting.getRepeatType()));
		SchedulerHelp.setRepeatUntilParameterRelatedValues(edgeDataSyncSetting.getRepeatParam(), scheduleData);

		return scheduleData;
	}

	/*private ScheduleData getSchedule(int scheduleID) {
		ScheduleData scheduleData = new ScheduleData();
		List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
		scheduleDao.as_edge_schedule_list(scheduleID, schedules);
		EdgeScheduler_Schedule schedule = schedules.get(0);

		scheduleData.setScheduleID(schedule.getID());
		scheduleData.setScheduleName(schedule.getName());
		scheduleData.setScheduleDescription(schedule.getDescription());
		scheduleData.getRepeatMethodData().setRepeatMethodType(
				ScheduleData.RepeatMethodType.parseInt(schedule
						.getScheduleType()));
		setRepeatMethodParameterRelatedValues(schedule.getScheduleParam(), scheduleData);
		scheduleData.setScheduleTime(schedule.getActionTime().getTime());
		scheduleData.setStartFromDate(schedule.getRepeatFrom().getTime());
		scheduleData.setRepeatUntilType(ScheduleData.RepeatUnitlType
				.parseInt(schedule.getRepeatType()));
		setRepeatUntilParameterRelatedValues(schedule.getRepeatParam(), scheduleData);

		return scheduleData;
	}

	private void saveSchedule(ScheduleData scheduleData) {
		String repeatMethodParameter = createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = createRepeatUntilParameter(scheduleData);
		scheduleDao.as_edge_schedule_update_for_ui(
				scheduleData.getScheduleID(), scheduleData.getScheduleName(),
				scheduleData.getScheduleDescription(), 2,
				scheduleData.getRepeatMethodData().getRepeatMethodType().getValue(),
				repeatMethodParameter,
				new Date(scheduleData.getScheduleTime()), new Date(scheduleData
						.getStartFromDate()),
				scheduleData.getRepeatUntilType().getValue(), repeatUntilParameter);
	}*/


	private boolean isSyncFolderValid(String folderPath) {
		if (!folderPath.matches("^([a-zA-Z]:\\\\?)|([a-zA-Z]:\\\\([^\\\\/:\\*\\?\\|\"<>]+\\\\)*[^\\\\/:\\*\\?\\|\"<>]+\\\\?)$")) {
			return false;
		}

		File file = new File(folderPath);
		if (!file.isDirectory()) {
			if (!file.mkdirs()) {
				return false;
			}
		}
		return true;
	}

	private String parseFilePath(String path) {
		String parsedPath = path;
		String edgeHomeString = "<EdgeHome>";
		if (parsedPath.contains(edgeHomeString)) {
			String edgeHomeFolder = getEdgeHomeFolder();
			edgeHomeFolder = edgeHomeFolder.replace("\\", "\\\\");
			parsedPath = parsedPath.replaceFirst(edgeHomeString, edgeHomeFolder);
		}

		return parsedPath;
	}

	private String getEdgeHomeFolder() {
		String edgeHomePath = "";
		try {
			edgeHomePath = CommonUtil.getApplicationRegistryInfo().getAppPath();
			if (edgeHomePath.endsWith("\\")) {
				edgeHomePath = edgeHomePath.substring(0, edgeHomePath.length() - 1);
			}		}
		catch(Exception e) {
			String currentdir = System.getProperty("user.dir");
			if (!currentdir.endsWith("\\")) {
				currentdir += "\\";
			}
			edgeHomePath = currentdir + "ARCserve Edge";
		}
	    return edgeHomePath;
	}

//	@Override
//	public void startDiscoveryService() throws EdgeServiceFault {
//		DiscoveryService.getInstance().start();
//	}
//
//	@Override
//	public void stopDiscoveryService() throws EdgeServiceFault {
//		DiscoveryService.getInstance().stop();
//	}
//
//	@Override
//	public DiscoveryServiceStatus queryDiscoveryServiceStatus() throws EdgeServiceFault {
//		return DiscoveryService.getInstance().queryStatus();
//	}
//
//	@Override
//	public List<String> getDomainControllerList(String domainName) throws EdgeServiceFault {
//		return DiscoveryService.getInstance().getDomainControllerList(domainName);
//	}

	@Override
	public String discoverNodesFromAD(DiscoveryOption[] options) throws EdgeServiceFault {
		return adService.discoverNodesFromAD(options);
	}

	@Override
	public void addADSource(DiscoveryOption option) throws EdgeServiceFault {
		adService.addADSource(option);
	}
	
	@Override
	public int addADSourceforWizard(DiscoveryOption option) throws EdgeServiceFault {
		return adService.addADSourceforWizard(option);
	}

	@Override
	public void updateADSource(DiscoveryOption option) throws EdgeServiceFault {
		adService.updateADSource(option);
	}

	@Override
	public void deleteADSource(int id) throws EdgeServiceFault {
		adService.deleteADSource(id);
	}

	@Override
	public List<DiscoveryOption> getADSourceList() throws EdgeServiceFault {
		return adService.getADSourceList();
	}

/*	@Override
	public void probeNode(DiscoveryOption option) throws EdgeServiceFault {
		DiscoveryService.getInstance().probeNode(option);
	}
*/
	@Override
	public void cancelDiscovery() throws EdgeServiceFault {
		adService.cancelDiscovery();
	}

	@Override
	@NotPrintAttribute
	public DiscoveryMonitor getDiscoveryMonitor() throws EdgeServiceFault {
		return adService.getDiscoveryMonitor();
	}

	@Override
	public AdminAccountValidationResult validateAdminAccount(GatewayId gatewayId, String computerName, String userName, @NotPrintAttribute String password) throws EdgeServiceFault {
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		AdminAccountValidationResult result = null;
		try {
			result = nativeFacade.validateAdminAccount(computerName, userName, password);
		} catch (EdgeServiceFault e) {
			if(e.getFaultInfo().getCode().equals(EdgeServiceErrorCode.Login_WrongNode)){//check whether is admin$ problem or the network problem
				boolean isNetworkIsavailable = false;
				try {
					isNetworkIsavailable = nativeFacade.isHostReachble(computerName);
				} catch (Exception ex) {
					logger.error("[NodeserviceImpl]:validateAdminAccount() Test host: "+computerName+" reachble failed.",e);
				}
				String msg;
				EdgeServiceFaultBean bean;
				//hard code this url now, because gateway have not EdgeLink.properties file
				//This method should be refactored and should exist only in console , not in gateway
				if(isNetworkIsavailable){
					msg = String.format("Cannot connect to the specified server. Please check the share \\\\%s\\ADMIN$", computerName);
					bean = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_CantConnect_Admin$Disable, msg);
					bean.setMessageParameters(new Object[]{computerName,edgeExternalLinks.cannotConnectD2DWebService()});
				}else {
					msg = String.format("Cannot connect to the specified server. Please check the network or DNS problems.", computerName);
					String messageSubject = EdgeCMWebServiceMessages.getMessage("node");
					bean = new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_CantConnect_NetWorkNotAvailable, msg);
					bean.setMessageParameters(new Object[]{messageSubject,computerName,messageSubject,edgeExternalLinks.cannotConnectD2DWebService()});
				}
				EdgeServiceFault esf = new EdgeServiceFault(msg, bean);
				throw esf;
			}else if(e.getFaultInfo().getCode().equals(EdgeServiceErrorCode.Login_NotAdministrator)){
				e.getFaultInfo().setMessageParameters(new String[]{edgeExternalLinks.addNodeUsingNonBuiltInAdministrator()});
				throw e;
			}else {
				throw e;
			}
		}
		return result;
	}

	@Override
	public List<AdminAccountValidationResultWithMessage> validateAdminAccountList(
			List<Node> nodeList) throws EdgeServiceFault {
		//add linux backup server
		List<NodeRegistrationInfo> linuxBackupServers = new ArrayList<NodeRegistrationInfo>();
		for(Node node:nodeList){
			if(node.getProtectionTypeBitmap() == ProtectionType.LINUX_D2D_SERVER.getValue()){
				NodeRegistrationInfo serverInfo = new NodeRegistrationInfo();;
				bindLinuxBackupServerInfo(serverInfo, node.getHostname(),node.getUsername(),node.getPassword(),Integer.valueOf(node.getD2dPort()),Integer.valueOf(node.getD2dProtocol()),node.getNodeDescription());
				addLinuxD2DServerNodeToList(serverInfo,linuxBackupServers);
			}
		}
		CompletionService<AdminAccountValidationResultWithMessage> completionService = new ExecutorCompletionService<AdminAccountValidationResultWithMessage>(EdgeExecutors.getFixedPool());
		Map<Future<AdminAccountValidationResultWithMessage>, Integer> resultMap = new HashMap<Future<AdminAccountValidationResultWithMessage>, Integer>();
		AdminAccountValidationResultWithMessage[] resultList = new AdminAccountValidationResultWithMessage[nodeList.size()]; 
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			NodeValidator validator = new NodeValidator(node);
			resultMap.put(completionService.submit(validator), i);
        }
		for (int i = 0; i < nodeList.size(); i++) {
			try {
                Future<AdminAccountValidationResultWithMessage> future = completionService.take();
                resultList[resultMap.get(future)] = future.get();                
            } catch (InterruptedException e) {
            	logger.error(e.getMessage());
                break;
            } catch (ExecutionException e) {
            	logger.error(e.getMessage());
                break;
            }
		}
		
		return Arrays.asList(resultList);
	}
	private void bindLinuxBackupServerInfo(NodeRegistrationInfo serverInfo,String hostName,String username,String password,int port,int protocol, String description) {
		serverInfo.setNodeName(hostName);
		serverInfo.setUsername(username);
		serverInfo.setPassword(password);
		serverInfo.setD2dPort(port);
		serverInfo.setD2dProtocol(Protocol.parse(protocol));
		serverInfo.setNodeDescription(description);
		serverInfo.setProtectionType(ProtectionType.LINUX_D2D_SERVER);
	}
	
	class NodeValidator implements Callable<AdminAccountValidationResultWithMessage> {
		private Logger validatorLogger = Logger.getLogger(NodeValidator.class);
        private Node node;

        public NodeValidator(Node n) {
        	this.node = n;
        }
        
        public AdminAccountValidationResultWithMessage call() throws Exception {
        	AdminAccountValidationResultWithMessage result = new AdminAccountValidationResultWithMessage();
        	try {
        		
        		if (node.getId() > 0)
        		{
	        		GatewayEntity gateway = gatewayService.getGatewayByHostId( node.getId() );
	        		node.setGatewayId( gateway.getId() );
        		}
        		
        		if ((node.getGatewayId() == null) || !node.getGatewayId().isValid())
        			logger.warn( "NodeValidator.call(): node.getGatewayId() is null or invalid." );
        		
        		if(HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue() == node.getNodeType()){
        			getVmwareTreeRootEntity(node.getDiscoveryESXOption(),true);
        			result.setValidationResult(AdminAccountValidationResult.Succeed);
    			}else if(HostType.EDGE_NODE_LINUX.getValue() == node.getNodeType()){
    				List<EdgeHost> linuxD2DList = new LinkedList<EdgeHost>();
    				hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, linuxD2DList);
    				NodeRegistrationInfo serverInfo = null;
    				if(CollectionUtils.isNotEmpty(linuxD2DList)){
    					serverInfo = new NodeRegistrationInfo();
    					EdgeHost edgeHost = linuxD2DList.get(0);
    					bindLinuxBackupServerInfo(serverInfo, edgeHost.getRhostname(),edgeHost.getUsername(),edgeHost.getPassword(),Integer.valueOf(edgeHost.getD2dPort()),Integer.valueOf(edgeHost.getD2dProtocol()),edgeHost.getNodeDescription());
    				}
    				NodeRegistrationInfo nodeInfo = new NodeRegistrationInfo();
    				nodeInfo.setNodeName(node.getHostname());
    				nodeInfo.setUsername(node.getUsername());
    				nodeInfo.setPassword(node.getPassword());
    				nodeInfo.setNodeDescription(node.getNodeDescription());
    				nodeInfo.setLinux(true);
    				try{
    					RegistrationNodeResult linuxNodeValidateResult = linuxNodeService.validateLinuxNode(nodeInfo,nodeInfo);
    					String[] resultCodes = linuxNodeValidateResult.getErrorCodes();
        				if(resultCodes[0] == null){
        					result.setValidationResult(AdminAccountValidationResult.Succeed);
        				}else {
							result.setValidationMessage(resultCodes[0]);
						}
    				}catch(EdgeServiceFault e){
						validatorLogger.error("[NodeValidator] validate linux node failed.",e);
						if(e.getFaultInfo().getCode() == EdgeServiceErrorCode.Node_AlreadyExist){
							result.setValidationResult(AdminAccountValidationResult.Succeed);
						}else {
							String errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),e.getFaultInfo());
							result.setValidationMessage(errorMessage);
						}
					}
    				
    			}else if(ProtectionType.LINUX_D2D_SERVER.getValue() == node.getProtectionTypeBitmap()){
    				NodeRegistrationInfo serverInfo = new NodeRegistrationInfo();
    				bindLinuxBackupServerInfo(serverInfo, node.getHostname(),node.getUsername(),node.getPassword(),Integer.valueOf(node.getD2dPort()),Integer.valueOf(node.getD2dProtocol()),node.getNodeDescription());
					try{
						RegistrationNodeResultForLinux linuxBackupServerValidateResult = linuxNodeService.validateLinuxD2DServer(serverInfo);
						if(linuxBackupServerValidateResult.isExistLinuxBackupServer()){
							result.setValidationResult(AdminAccountValidationResult.Succeed);
	    				}
					}catch(EdgeServiceFault e){
						validatorLogger.error("[NodeValidator] validate linux backup server failed.",e);
						if(e.getFaultInfo().getCode() == EdgeServiceErrorCode.Node_AlreadyExist){
							result.setValidationResult(AdminAccountValidationResult.Succeed);
						}else {
							String errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),e.getFaultInfo());
							result.setValidationMessage(errorMessage);
						}
					}
    				
    			}else{
    				AdminAccountValidationResult validationResult = validateAdminAccount(node.getGatewayId() ,node.getHostname(), node.getUsername(), node.getPassword());
        			result.setValidationResult(validationResult);
    			}
			} catch (Exception e) {
				validatorLogger.error("[NodeValidator] validate admin account failed.",e);
				String errorMessage;
				if(e instanceof EdgeServiceFault){
					errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),((EdgeServiceFault)e).getFaultInfo());
				}else {
					EdgeServiceFaultBean bean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General,null);
					errorMessage = WebServiceFaultMessageRetriever.getErrorMessage( DataFormatUtil.getServerLocale(),bean);
				}
				result.setValidationMessage(errorMessage);
				result.setValidationResult(AdminAccountValidationResult.InvalidCredential);
			}
            return result;
        }
    }
	
	@Override
	public List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(
			DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return esxService.getVMVirtualMachineList(esxOption);
	}

//	private String convertoSQLStr(String str) {
//		if (StringUtil.isEmptyOrNull(str)) {
//			return "";
//		}
//		str = str.trim();
//		str = str.replaceAll("[*]", "%");
//		str = str.replaceAll("[?]", "_");
//		return str;
//	}

	private static NodeDetail commonConvertEdgeHost2Node(EdgeHost daoHost){


		NodeDetail host = new NodeDetail();
		host.setId(daoHost.getRhostid());
		host.setAppStatus(daoHost.getAppStatus());
		host.setHostname(daoHost.getRhostname());
		host.setIpaddress(daoHost.getIpaddress());
		host.setLastupdated(daoHost.getLastupdated());
		host.setServerPrincipalName(daoHost.getServerPrincipalName());
		host.setOsDescription(daoHost.getOsdesc());
		host.setOsType(daoHost.getOstype());//issue 143314 <zhaji22>
		host.setSqlServerInstalled(ApplicationUtil.isSQLInstalled(daoHost.getAppStatus()));
		host.setExchangeInstalled(ApplicationUtil.isExchangeInstalled(daoHost.getAppStatus()));
		host.setD2dInstalled(ApplicationUtil.isD2DInstalled(daoHost.getAppStatus()));
		host.setLinuxD2DInstalled(ApplicationUtil.isLinuxD2DInstalled(daoHost.getAppStatus()));
		host.setD2dOnDInstalled(ApplicationUtil.isD2DODInstalled(daoHost.getAppStatus()));
		host.setArcserveInstalled(ApplicationUtil.isArcserveInstalled(daoHost.getAppStatus()));
		host.setArcserveBackupVersion(daoHost.getArcserveBackupVersion());
		host.setD2DMajorversion(daoHost.getD2DMajorversion());
		host.setD2dMinorversion(daoHost.getD2dMinorversion());
		host.setD2dBuildnumber(daoHost.getD2dBuildnumber());
		host.setD2dUpdateversionnumber(daoHost.getD2dUpdateversionnumber());
		host.setD2dProtocol(daoHost.getD2dProtocol());
		host.setArcserveProtocol(daoHost.getArcserveProtocol());
		host.setArcservePort(daoHost.getArcservePort());
		host.setD2dPort(daoHost.getD2dPort());
		host.setAsbuLastUpdateTime(daoHost.getAsbuLastUpdateTime());
		host.setD2dLastUpdateTime(daoHost.getD2dLastUpdateTime());
		host.setUsername(daoHost.getUsername());
		host.setPassword(daoHost.getPassword());
		host.setNodeDescription(daoHost.getNodeDescription());
		int hostType = daoHost.getRhostType();
		host.setRhostType(hostType);
		host.setPhysicalMachine(HostTypeUtil.isPhysicsMachine(hostType));
		host.setVMwareMachine(HostTypeUtil.isVMWareVirtualMachine(hostType));
		host.setHyperVMachine(HostTypeUtil.isHyperVVirtualMachine(hostType));
		host.setHasVCMMonitorFlag(HostTypeUtil.isVCMMonitor(hostType));
		host.setVCMMonitee(HostTypeUtil.isVCMMonitee(hostType));
		host.setVMImportFromVSphere(HostTypeUtil.isVMImportFromVSphere(hostType));
		host.setLinuxNode(HostTypeUtil.isLinuxNode(hostType));
		host.setImportedFromRHA(HostTypeUtil.isNodeImportFromRHA(hostType));
		host.setImportedFromRHAWithHBBU(HostTypeUtil.isNodeImportFromRHAWithHBBU(hostType));
		host.setImportedFromRPS(HostTypeUtil.isNodeImportFromRPS(hostType));
		host.setImportedFromRPSReplication(HostTypeUtil.isNodeImportFromRPSReplica(hostType));
		host.setHyperVVmAsPhysicalMachine(HostTypeUtil.isHyperVVmAsPhysicalMachine(hostType));
		host.setIsVisible(daoHost.getIsVisible());
		host.setVmName(daoHost.getVmname());
		host.setD2DUUID(daoHost.getD2DUUID());
		host.setSiteName(daoHost.getSiteName());
		host.setGatewayId(new GatewayId(daoHost.getGatewayId()));
		host.setSiteName(daoHost.getSiteName());
		
		if (daoHost.getARCserveType() == ABFuncServerType.BRANCH_PRIMARY)
			host.setArcserveType(ABFuncServerType.BRANCH_PRIMARY);
		else if (daoHost.getARCserveType() == ABFuncServerType.GDB_PRIMARY_SERVER)
			host.setArcserveType(ABFuncServerType.GDB_PRIMARY_SERVER);
		else if (daoHost.getARCserveType() == ABFuncServerType.NORNAML_SERVER)
			host.setArcserveType(ABFuncServerType.NORNAML_SERVER);
		else if (daoHost.getARCserveType() == ABFuncServerType.ARCSERVE_MEMBER)
			host.setArcserveType(ABFuncServerType.ARCSERVE_MEMBER);
		else
			host.setArcserveType(ABFuncServerType.UN_KNOWN);

		if (daoHost.getArcserveManagedStatus() == NodeManagedStatus.Managed.ordinal())
			host.setArcserveManaged(NodeManagedStatus.Managed);
		else if (daoHost.getArcserveManagedStatus() == NodeManagedStatus.Unmanaged.ordinal())
			host.setArcserveManaged(NodeManagedStatus.Unmanaged);
		else
			host.setArcserveManaged(NodeManagedStatus.Unknown);

		if (daoHost.getD2dManagedStatus() == NodeManagedStatus.Managed.ordinal())
			host.setD2dManaged(NodeManagedStatus.Managed);
		else if (daoHost.getD2dManagedStatus() == NodeManagedStatus.Unmanaged.ordinal())
			host.setD2dManaged(NodeManagedStatus.Unmanaged);
		else
			host.setD2dManaged(NodeManagedStatus.Unknown);
		if (daoHost.getRpsManagedStatus() == NodeManagedStatus.Managed
				.ordinal())
			host.setRpsManagedStatus(NodeManagedStatus.Managed);
		else if (daoHost.getRpsManagedStatus() == NodeManagedStatus.Unmanaged
				.ordinal())
			host.setRpsManagedStatus(NodeManagedStatus.Unmanaged);
		else
			host.setRpsManagedStatus(NodeManagedStatus.Unknown);
		
 	        host.setD2dLastBackupJobStatus(dbValueToJobStatus(daoHost.getD2dLastBackupJobStatus()));
		host.setD2dLastBackupStartTime(daoHost.getD2dLastBackupStartTime());
		
		populateVSBStatus(host,daoHost);
		populateNodeConverterInfo(host, daoHost);
		
		host.setProtectionTypeBitmap(daoHost.getProtectionTypeBitmap());
		
		//set rps info for node
		host.setRpsServer(daoHost.getRpsName());
		
		host.setVmWindowsOS(!HostTypeUtil.isVMNonWindowsOS(hostType));
		host.setVmStatus(daoHost.getVmStatus());
		host.setTimezone(daoHost.getTimezone());
		host.setMachineType(LicenseMachineType.parseValue(daoHost.getMachineType()));
		boolean isconsoleappinstalled = ApplicationUtil.isConsoleInstalled(daoHost.getAppStatus());
	//	logger.info("*******inside commonConvertEdgeHost2Node, isConsoleInstalled: " + isconsoleappinstalled );
		host.setConsoleInstalled(ApplicationUtil.isConsoleInstalled(daoHost.getAppStatus()));
		
		return host;
	}
	
	private static void populateNodeConverterInfo(Node node, EdgeHost daoHost) {
		if (EdgeWebServiceContext.getApplicationType() != EdgeApplicationType.vShpereManager) {
			node.setConverterId(daoHost.getConverterId());
			node.setConverter(daoHost.getConverter());
			node.setRecoveryPointFolder(daoHost.getRecoveryPointFolder());
			node.setConverterUsername(daoHost.getConverterUsername());
			node.setConverterPassword(daoHost.getConverterPassword());
			node.setConverterPort(daoHost.getConverterPort());
			node.setConverterProtocol(daoHost.getConverterProtocol());
		}
	}
	
	private static void populateVSBStatus(Node node,EdgeHost daoHost){
		D2DStatusInfo vsbStatusInfo = null;
		if (daoHost.getHasVSBStatusInfo() == 0) // doesn't has vsb status info
		{
			vsbStatusInfo = D2DStatusInfo.NullObject;
		}
		else // has d2d status info
		{
			vsbStatusInfo = new D2DStatusInfo();
			
			vsbStatusInfo.setLastBackupStartTime( daoHost.getD2dLastVSBStartTime());
			vsbStatusInfo.setLastBackupType( dbValueToD2DBackupType( daoHost.getD2dLastVSBType() ) );
			vsbStatusInfo.setLastBackupJobStatus( dbValueToD2DJobStatus( daoHost.getD2dLastVSBJobStatus() ) );
			vsbStatusInfo.setLastBackupStatus( dbValueToD2DStatus( daoHost.getD2dLastVSBStatus() ) );

			vsbStatusInfo.setVmName(daoHost.getStandbyVMName());
			vsbStatusInfo.setStandbyVMRecentSnapshot(daoHost.getStandbyVMRecentSnapshot());
			vsbStatusInfo.setSnapshotTimeZoneOffset(daoHost.getSnapshotTimeZoneOffset());
			vsbStatusInfo.setCurrentRunningSnapshot(daoHost.getCurrentRunningSnapshot());
			
			vsbStatusInfo.setVmPowerStatus(VMPowerStatus.values()[daoHost.getVmPowerStatus()]);
			
			vsbStatusInfo.setRecoveryPointRetentionCount( daoHost.getD2dSnapShotRetentionCount() );
			vsbStatusInfo.setRecoveryPointCount( daoHost.getD2dSnapShotCount());
			vsbStatusInfo.setRecoveryPointStatus( dbValueToD2DStatus( daoHost.getD2dRecPointStatus()));
			
			vsbStatusInfo.setDestinationFreeSpace( daoHost.getVsbDestFreeSpace() );
			vsbStatusInfo.setDestinationStatus( dbValueToD2DStatus( daoHost.getVsbDestStatus()) );
			
			vsbStatusInfo.setAutoOfflieCopyStatus(daoHost.getAutoOfflieCopyStatus());
			vsbStatusInfo.setHeartbeatStatus(daoHost.getHeartbeatStatus());
			
			vsbStatusInfo.setOverallStatus( dbValueToD2DStatus( daoHost.getVsbOverallStatus()));
			
			vsbStatusInfo.setLastUpdateTimeDiffSeconds(daoHost.getLastUpdateTimeDiffSeconds());
		}
		node.setVsbSatusInfo(vsbStatusInfo);
	}
	
	public static D2DEstimatedValue dbValueToD2DEstimatedValue (int dbValue) {
		for (D2DEstimatedValue enumValue : D2DEstimatedValue.values()) {
			if (enumValue.ordinal() == dbValue) {
				return enumValue;
			}
		}
		return D2DEstimatedValue.Unknown;
	}
	
	public static D2DBackupType dbValueToD2DBackupType( int dbValue )
	{
		for (D2DBackupType enumValue : D2DBackupType.values())
		{
			if (enumValue.ordinal() == dbValue)
				return enumValue;
		}
		
		return D2DBackupType.Unknown;
	}
	
	public static D2DJobStatus dbValueToD2DJobStatus( int dbValue )
	{
		for (D2DJobStatus enumValue : D2DJobStatus.values())
		{
			if (enumValue.ordinal() == dbValue)
				return enumValue;
		}
		
		return D2DJobStatus.Unknown;
	}
	
	public static D2DStatus dbValueToD2DStatus( int dbValue )
	{
		for (D2DStatus enumValue : D2DStatus.values())
		{
			if (enumValue.ordinal() == dbValue)
				return enumValue;
		}
		
		return D2DStatus.Unknown;
	}
	
	public static JobStatus dbValueToJobStatus( int dbValue )
	{
		for (JobStatus enumValue : JobStatus.values())
		{
			if (enumValue.getValue() == dbValue)
				return enumValue;
		}
		
		return JobStatus.All;
	}
	
	public static Node convertDaoNode2ContractNodeWithFullInfo(EdgeHost daoHost){
		NodeDetail host = commonConvertEdgeHost2Node(daoHost);
		EdgeSyncStatus daoSyncStatus = new EdgeSyncStatus();
		daoSyncStatus.setStatus(daoHost.getArcSyncStatus());
		daoSyncStatus.setChange_status(daoHost.getArcSyncChangeStatus());
		host.setSyncStatus(convertDaoSyncStatus2Contract(daoSyncStatus));
		host.setBkpStatus(convertDaoNodeStatus2Contract(daoHost.getD2dStatus()));
		host.setUsername(daoHost.getUsername());
		host.setAuthUUID(daoHost.getAuthUUID());
		host.setPassword(daoHost.getPassword());
		host.setPolicyContentFlag(daoHost.getPolicyContentFlag());
		return host;
	}
	public static Node convertDaoNode2ContractNode(EdgeHost daoHost){
		try
		{
			NodeDetail host = commonConvertEdgeHost2Node(daoHost);
	
			List<EdgeSyncStatus> syncList = new ArrayList<EdgeSyncStatus>();
			List<EdgeConnectInfo> nodeStatusList = new ArrayList<EdgeConnectInfo>();
	
			try {
				syncList.clear();
				staticSyncDao.as_edge_Get_Sync_Status(EdgeSyncComponents.ARCserve_Backup.getValue(), daoHost.getRhostid(), syncList);
				if (syncList != null && !syncList.isEmpty()) {
					host.setSyncStatus(convertDaoSyncStatus2Contract(syncList.get(0)));
				}
			} catch (Exception e) {
				logger.error("as_edge_Get_Sync_Status", e);
			}
	
			try {
				nodeStatusList.clear();
				staticConnectionInfoDao.as_edge_connect_info_list(daoHost.getRhostid(), nodeStatusList);
				if (nodeStatusList != null && !nodeStatusList.isEmpty()) {
					host.setBkpStatus(convertDaoNodeStatus2Contract(nodeStatusList.get(0).getStatus()));
				}
			} catch (Exception e) {
				logger.error("as_edge_Get_ABNode_Status", e);
			}
	
			return host;
		}
		catch (Exception e)
		{
			logger.error( "Error converting DAO node to contract node.", e );
			throw e;
		}
	}

	@Override
	public void setNodesAsManaged(int[] idArray) throws EdgeServiceFault {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		for (int i=1;i<=idArray.length;i++){
			buffer.append(idArray[i-1]);
			if (i<idArray.length)
				buffer.append(",");
		}
		buffer.append(")");
		hostMgrDao.as_edge_host_set_managed(buffer.toString(), 1);
	}

	@Override
	public List<NodeGroup> getNodeGroups() throws EdgeServiceFault {
		List<EdgeSourceGroup> groups = new ArrayList<EdgeSourceGroup>();
		hostMgrDao.as_edge_group_list(0, groups);

		List<NodeGroup> result = new ArrayList<NodeGroup>();
		for (EdgeSourceGroup daoSourceGroup : groups) {
			NodeGroup sourceGroup = new NodeGroup();
			sourceGroup.setType(NodeGroup.UNESX);
			sourceGroup.setId(daoSourceGroup.getId());
			sourceGroup.setName(daoSourceGroup.getName());
			result.add(sourceGroup);
		}

		List<EdgeHost> ungroupHosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(0, 1, ungroupHosts);
		if (ungroupHosts!=null && ungroupHosts.size()>0){
			NodeGroup sourceGroup = new NodeGroup();
			sourceGroup.setType(NodeGroup.Default);
			sourceGroup.setId(NodeGroup.UNGROUP);
			result.add(sourceGroup);
		}

		return result;
	}

	@Override
	public List<NodeGroup> getNodeESXGroups(int gatewayId) throws EdgeServiceFault {
		List<EdgeSourceGroup> groups = new ArrayList<EdgeSourceGroup>();
		hostMgrDao.as_edge_group_get_all_list(gatewayId, groups);
		List<NodeGroup> result = new ArrayList<NodeGroup>();
		Map<String, List<NodeGroup>> scenarioGroupMap = new Hashtable<String, List<NodeGroup>>();
		for (EdgeSourceGroup daoSourceGroup : groups) {
			NodeGroup sourceGroup;
			
			if (daoSourceGroup.getGroupType() == NodeGroup.NodeFilterGroupType) {
				String filterXml = daoSourceGroup.getComments();
				EdgeNodeFilter nodeFilter;
				
				try {
					nodeFilter = CommonUtil.unmarshal(filterXml, EdgeNodeFilter.class);
				} catch (JAXBException e) {
					logger.error("unmarshal node filter failed, error message = " + e.getMessage());
					nodeFilter = new EdgeNodeFilter();
				}
				
				sourceGroup = new NodeFilterGroup(nodeFilter);
			} else {
				sourceGroup = new NodeGroup();
			}
			
			sourceGroup.setId(daoSourceGroup.getId());
			String groupName = daoSourceGroup.getName();
			sourceGroup.setName(groupName);

			sourceGroup.setType(daoSourceGroup.getGroupType());

			if (daoSourceGroup.getGroupType() == NodeGroup.RHAScenarioGroupType) {
				sourceGroup.setComments(daoSourceGroup.getComments());
				List<NodeGroup> groupList;
				if (scenarioGroupMap.containsKey(groupName)) {
					groupList = scenarioGroupMap.get(groupName);
				} else {
					groupList = new ArrayList<NodeGroup>();
				}
				groupList.add(sourceGroup);
				scenarioGroupMap.put(groupName, groupList);
			}
			result.add(sourceGroup);
		}
		for (List<NodeGroup> groupList : scenarioGroupMap.values()) {
			if (groupList.size() > 1) {
				for (NodeGroup nodeGroup : groupList) {
					String groupName = nodeGroup.getName();
					String controlServiceName = nodeGroup.getComments();
					if (!StringUtil.isEmptyOrNull(groupName) && !StringUtil.isEmptyOrNull(controlServiceName)) {
						nodeGroup.setName(groupName + "@" + controlServiceName);
					}
				}
			}
		}

		return result;
	}
	
	@Override
	public List<NodeGroup> getNodeGroupsByLayer(int gatewayid, NodeGroup parentGroup) throws EdgeServiceFault {
		if(parentGroup == null){
			//get the node root group
			List<NodeGroup> nodeGroups = new ArrayList<>();
			//All nodes
			NodeGroup allNodeGroup = new NodeGroup(NodeGroup.ALLGROUP,EdgeCMWebServiceMessages.getMessage("allNodesGroup"),NodeGroup.Default,"",false);
			nodeGroups.add(allNodeGroup);
			//without plan nodes 
			NodeGroup unAssignedPlanGroup = new NodeGroup(NodeGroup.UnassignedPolicyGroup,
					EdgeCMWebServiceMessages.getMessage("WithoutPlanGroup"),NodeGroup.Default,"",false);
			nodeGroups.add(unAssignedPlanGroup);
			//other groups
			List<EdgeIntegerValue> nodeGroupTypes = new ArrayList<>();
			hostMgrDao.as_edge_group_get_type_list(gatewayid, nodeGroupTypes);
			for(EdgeIntegerValue groupType : nodeGroupTypes){
				NodeGroup group = null;
				int groupTypeValue = groupType.getValue();
				if(groupTypeValue == NodeGroup.UNESX){
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("customGroup"), NodeGroup.UNESX, "",true);
				}else if (groupTypeValue == NodeGroup.ESX) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("esxGroup"), NodeGroup.ESX, "",true);
				}else if (groupTypeValue == NodeGroup.VCLOUD) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("vCloudGroup"), NodeGroup.VCLOUD, "",true);
				}else if (groupTypeValue == NodeGroup.HYPERV) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("hypervGroup"), NodeGroup.HYPERV, "",true);
				}else if (groupTypeValue == NodeGroup.WinProxyGroupType) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("winProxyGroup"), NodeGroup.WinProxyGroupType, "",true);
				}else if (groupTypeValue == NodeGroup.LinuxD2D) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("linuxD2DGroup"), NodeGroup.LinuxD2D, "",true);
				}else if (groupTypeValue == NodeGroup.D2DPolicyGroupType) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("planGroup"), NodeGroup.D2DPolicyGroupType, "",true);
				}else if (groupTypeValue == NodeGroup.GDB) {
					group = new NodeGroup(0, EdgeCMWebServiceMessages.getMessage("gdbGroup"), NodeGroup.GDB, "",true);
				}

				if(group != null)
					nodeGroups.add(group);
			}
			return nodeGroups;
		}
		
		switch (parentGroup.getType()) {
		case NodeGroup.UNESX:
			return getCustomeGroups(gatewayid);
		case NodeGroup.ESX:
			return getEsxGroups(gatewayid);
		case NodeGroup.VCLOUD:
			return getVcloudGroups(gatewayid);
		case NodeGroup.HYPERV:
			return getHypervGroups(gatewayid);
		case NodeGroup.WinProxyGroupType:
			return getWinProxyGroups(gatewayid);
		case NodeGroup.LinuxD2D:
			return getLinuxGroups(gatewayid);
		case NodeGroup.D2DPolicyGroupType:
			return getPlanGroups(gatewayid);
		case NodeGroup.GDB:
			return getGDBGroups();
		case NodeGroup.NodeFilterGroupType:
			return getNodeFilterGroups();
		default:
			return null;
		}
		//This original code is for RHAScenarioGroupType, but original get group method can't return RHAScenarioGroupType
		//So comment it out
//			if (daoSourceGroup.getGroupType() == NodeGroup.RHAScenarioGroupType) {
//				sourceGroup.setComments(daoSourceGroup.getComments());
//				List<NodeGroup> groupList;
//				if (scenarioGroupMap.containsKey(groupName)) {
//					groupList = scenarioGroupMap.get(groupName);
//				} else {
//					groupList = new ArrayList<NodeGroup>();
//				}
//				groupList.add(sourceGroup);
//				scenarioGroupMap.put(groupName, groupList);
//			}
//			result.add(sourceGroup);
//		}
//		for (List<NodeGroup> groupList : scenarioGroupMap.values()) {
//			if (groupList.size() > 1) {
//				for (NodeGroup nodeGroup : groupList) {
//					String groupName = nodeGroup.getName();
//					String controlServiceName = nodeGroup.getComments();
//					if (!StringUtil.isEmptyOrNull(groupName) && !StringUtil.isEmptyOrNull(controlServiceName)) {
//						nodeGroup.setName(groupName + "@" + controlServiceName);
//					}
//				}
//			}
//		}
//
//		return result;
	}
	
	private List<NodeGroup> getCustomeGroups(int gatewayId){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeSourceGroup> groups = new ArrayList<>();
		hostMgrDao.as_edge_group_list(gatewayId, groups);
		for (EdgeSourceGroup srcGroup : groups) {
			NodeGroup customGroup = new NodeGroup(srcGroup.getId(),srcGroup.getName(),NodeGroup.UNESX,srcGroup.getComments(),false);
			result.add(customGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getEsxGroups(int gatewayid)throws EdgeServiceFault{
		List<NodeGroup> result = new ArrayList<>();
		List<VsphereEntityType> types = new ArrayList<>();
		types.add(VsphereEntityType.UNKNOWN);
		types.add(VsphereEntityType.esxServer);
		types.add(VsphereEntityType.vCenter);
		List<EsxVSphere> esxList = getEsxInfoList(gatewayid, types);
		for (EsxVSphere esx : esxList) {
			NodeGroup esxGroup = new NodeGroup(esx.getId(),esx.getHostname(),NodeGroup.ESX,null,false);
			result.add(esxGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getVcloudGroups(int gatewayid)throws EdgeServiceFault{
		List<NodeGroup> result = new ArrayList<>();
		List<VsphereEntityType> types = new ArrayList<>();
		types.add(VsphereEntityType.vCloudDirector);
		List<EsxVSphere> vCloudList = getEsxInfoList(gatewayid, types);
		for (EsxVSphere vCloud : vCloudList) {
			NodeGroup vCloudGroup = new NodeGroup(vCloud.getId(),vCloud.getHostname(),NodeGroup.VCLOUD,null,false);
			result.add(vCloudGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getHypervGroups(int gatewayId){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeSourceGroup> groups = new ArrayList<>();
		hostMgrDao.as_edge_hyperv_group_list(gatewayId, groups);
		for (EdgeSourceGroup srcGroup : groups) {
			NodeGroup hyperVGroup = new NodeGroup(srcGroup.getId(),srcGroup.getName(),NodeGroup.HYPERV,null,false);
			result.add(hyperVGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getWinProxyGroups(int gatewayId){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeSourceGroup> groups = new ArrayList<>();
		hostMgrDao.as_edge_winProxy_group_list(gatewayId,groups);
		for (EdgeSourceGroup srcGroup : groups) {
			NodeGroup winProxyVGroup = new NodeGroup(srcGroup.getId(),srcGroup.getName(),NodeGroup.WinProxyGroupType,srcGroup.getComments(),false);
			result.add(winProxyVGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getLinuxGroups(int gatewayid){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeSourceGroup> groups = new ArrayList<>();
		hostMgrDao.as_edge_linuxServer_group_list(gatewayid,groups);
		for (EdgeSourceGroup srcGroup : groups) {
			NodeGroup linuxServerGroup = new NodeGroup(srcGroup.getId(),srcGroup.getName(),NodeGroup.LinuxD2D,srcGroup.getComments(),false);
			result.add(linuxServerGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getPlanGroups(int gatewayid){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgePolicy> planList = new ArrayList<EdgePolicy>();
		policyDao.as_edge_policy_list_bytype( 6, 0, planList );
		if(gatewayid==0){
			for (EdgePolicy plan : planList) {
				NodeGroup planGroup = new NodeGroup(plan.getId(),plan.getName(),NodeGroup.D2DPolicyGroupType,null,false);
				result.add(planGroup);
			}
		}else{
			List<PolicyInfo> policyList = new ArrayList<PolicyInfo>(); 
			int[] totalCount = new int[1];
			policyDao.as_edge_plan_getPlanList_by_paging(Integer.MAX_VALUE,0,
					EdgeSortOrder.ASC.value(),PolicySortCol.planName.value(),gatewayid,totalCount,policyList);
			for (EdgePolicy plan : planList) {
				for (PolicyInfo info:policyList) {
					if(plan.getId()==info.getPolicyId()){						
						NodeGroup planGroup = new NodeGroup(plan.getId(),plan.getName(),NodeGroup.D2DPolicyGroupType,null,false);
						result.add(planGroup);
						break;
					}
				}
			}
		}		
		return result;
	}
	
	private List<NodeGroup> getGDBGroups(){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeSourceGroup> groups = new ArrayList<>();
		hostMgrDao.as_edge_gdb_group_list(groups);
		for (EdgeSourceGroup srcGroup : groups) {
			NodeGroup gdbGroup = new NodeGroup(srcGroup.getId(),srcGroup.getName(),NodeGroup.HYPERV,null,false);
			result.add(gdbGroup);
		}
		return result;
	}
	
	private List<NodeGroup> getNodeFilterGroups(){
		List<NodeGroup> result = new ArrayList<>();
		List<EdgeFilter> filters = new ArrayList<EdgeFilter>();
		hostMgrDao.as_edge_filter_select(1, filters);
		for (EdgeFilter filter : filters) {
			NodeGroup nodeGroup = null;
			EdgeNodeFilter nodeFilter=null;
			String filterXml = filter.getFilterXml();
			try {
				nodeFilter = CommonUtil.unmarshal(filterXml, EdgeNodeFilter.class);
			} catch (Exception e) {
				logger.error("unmarshal node filter failed, error message = " + e.getMessage());
				nodeFilter = new EdgeNodeFilter();
			}
			nodeGroup= new NodeFilterGroup(nodeFilter);
			nodeGroup.setId(filter.getId());
			nodeGroup.setName(filter.getName());
			nodeGroup.setType(NodeGroup.NodeFilterGroupType);
			result.add(nodeGroup);
		}
		return result;
	}
	
	@Override
	public List<Node> getNodesByGroup(int gatewayId, int groupId, int groupType) throws EdgeServiceFault {
		List<Integer> hostIds = getNodeIdsByGroup(gatewayId, groupId, groupType);
		return getNodeListByIDs(hostIds);
//		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
//		if (groupID == NodeGroup.UNGROUP)
//			hostMgrDao.as_edge_host_list_bygroupid(0, hosts);
//		else if (groupID == NodeGroup.EXCHANGE || groupID == NodeGroup.SQLSERVER || groupID == NodeGroup.ALLGROUP)
//			hostMgrDao.as_edge_host_list(0, 1, hosts);
///*chie*/else if(groupID == NodeGroup.D2D)
//			hostMgrDao.as_edge_host_list(NodeGroup.D2D, 1, hosts);
//		else if(groupID == NodeGroup.D2DOD)	// (*D2DOD*)
//			hostMgrDao.as_edge_host_list(NodeGroup.D2DOD, 1, hosts);// (*D2DOD*)
//		else if(groupID == NodeGroup.ASBU)
//			hostMgrDao.as_edge_host_list(NodeGroup.ASBU, 1, hosts);
//		else if(groupID == NodeGroup.LinuxD2D)
//			hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, hosts);
//		else if(groupID == NodeGroup.LinuxNode)
//			hostMgrDao.as_edge_host_list(NodeGroup.LinuxNode, 1, hosts);
//		else
//			hostMgrDao.as_edge_host_list_bygroupid(groupID, hosts);
//
//		List<Node> nodes = new LinkedList<Node>();
//		for (EdgeHost host : hosts){
//			if(groupID == NodeGroup.EXCHANGE){
//				if (ApplicationUtil.isExchangeInstalled(host.getAppStatus()))
//					nodes.add(convertDaoNode2ContractNode(host));
//				else
//					continue;
//			}else if(groupID == NodeGroup.SQLSERVER){
//				if (ApplicationUtil.isSQLInstalled(host.getAppStatus()))
//					nodes.add(convertDaoNode2ContractNode(host));
//				else
//					continue;
//			}else
//				nodes.add(convertDaoNode2ContractNode(host));
//
//			try{
//				
//				// !!! NEED TO CHECK !!!
//				//
//				// There seems to be some problem here. In fact, instead of policy ID,
//				// as_edge_policy_by_hostID() gets the policy name. And the real problem
//				// is the DAO function only gets the policy of type 1 (CPM policy).
//				//
//				// By checking further, this part of data was not used, so from the build,
//				// we cannot see any thing wrong. This was found by reading code when fixing
//				// another issue, and we need to do some further investigation on it. The
//				// time is very close to GM, so for now, we just keep the original behavior.
//				//
//				// Pang, Bo (panbo01)
//				// 2013-01-25
//				
//				Node newNode = nodes.get(nodes.size()-1);
//				String[] policyID = new String[1];
//				policyDao.as_edge_policy_by_hostID(host.getRhostid(), policyID);
//				newNode.setPolicyName(policyID[0]);
//				
//				// In r16.5, there may be several policy types in one application, but in
//				// this release, a node cannot have both type of policies in one application,
//				// so we just try to find if it has one, and we can stop searching once we
//				// found one.
//				//
//				// Pang, Bo (panbo01)
//				// 2013-01-25
//				
//				EdgeApplicationType appType = EdgeWebServiceContext.getApplicationType();
//				List<Integer> policyTypeList = getPolicyTypeByAppType( appType );
//				EdgeHostPolicyMap hostPolicyMap = null;
//				for (int policyType : policyTypeList)
//				{
//					hostPolicyMap = getHostPolicyMapByHostId( host.getRhostid(), policyType );
//					if (hostPolicyMap != null)
//					{
//						newNode.setPolicyDeployStatus( hostPolicyMap.getDeployStatus() );
//						newNode.setPolicyDeployReason(hostPolicyMap.getDeployReason()); 
//						newNode.setLastSuccessfulPolicyDeploy( hostPolicyMap.getLastSuccDeploy() );
//						break;
//					}
//				}
//			}catch(Exception e){
//
//			}
//		}
//		
//		// set vm info
//		populateVMInformation(nodes);
//		
//		return nodes;
	}
	
	@Override
	public List<Node> getHBBUProxy(int gatewayId) throws EdgeServiceFault {
		List<Node> nodes = new LinkedList<Node>();
		List<HBBUProxy> proxys = new ArrayList<HBBUProxy>(); 
		vcmDao.as_edge_vsphere_proxy_Info_list(gatewayId, proxys );
		
		Map<String, String> proxyMap = new HashMap<String, String>();
		if(proxys !=null && !proxys.isEmpty()){
			for( HBBUProxy proxy : proxys ) {
				if(!proxyMap.containsKey(proxy.getUuid())){
					proxyMap.put(proxy.getUuid(), proxy.getUuid());
					Node hbbuProxyNode = new Node();
					hbbuProxyNode.setId(proxy.getId());
					hbbuProxyNode.setHostname(proxy.getHostname());
					hbbuProxyNode.setUsername(proxy.getUsername());
					hbbuProxyNode.setD2DUUID(proxy.getUuid());
					hbbuProxyNode.setAuthUUID(proxy.getAuthUuid());
					hbbuProxyNode.setPassword(proxy.getPassword());
					hbbuProxyNode.setD2dPort(String.valueOf(proxy.getPort()));
					hbbuProxyNode.setD2dProtocol(proxy.getProtocol());
					nodes.add(hbbuProxyNode);
				}
			}
		}

		// rpsNode can be show in ProxyList
		List<RpsNode> rpsList = rpsNodeService.getRpsNodesByGroup(gatewayId, -1);
		for (RpsNode rps:rpsList) {
			if(!proxyMap.containsKey(rps.getUuid())){
				proxyMap.put(rps.getUuid(), rps.getUuid());
				Node hbbuProxyNode = new Node();
				//this flag used to this is a RPSNode				
				hbbuProxyNode.setId(rps.getNode_id());
				hbbuProxyNode.setD2DUUID(rps.getUuid());
				hbbuProxyNode.setHostname(rps.getNode_name());
				hbbuProxyNode.setD2dProtocol(rps.getProtocol());	
				hbbuProxyNode.setD2dPort(String.valueOf(rps.getPort()));
				hbbuProxyNode.setUsername(rps.getUsername());
				hbbuProxyNode.setPassword(rps.getPassword());
				// add rps 's D2D Information
				List<EdgeD2DHost> hostList = new ArrayList<EdgeD2DHost>();		
				hostList.clear();
				hostMgrDao.getHostByUUID(rps.getUuid(), hostList);
				if(hostList.isEmpty()){
					hbbuProxyNode.setRpsManagedStatus(NodeManagedStatus.Managed); 
				} else {
					hbbuProxyNode.setD2dInstalled(true);
					EdgeD2DHost host = hostList.get(0);
					//Node d2d = new Node();		
					hbbuProxyNode.setId(host.getRhostid());
					hbbuProxyNode.setD2DUUID(host.getUuid());
					hbbuProxyNode.setHostname(host.getRhostname());
					hbbuProxyNode.setD2dProtocol(host.getProtocol());	
					hbbuProxyNode.setD2dPort(String.valueOf(host.getPort()));
					hbbuProxyNode.setUsername(rps.getUsername());
					hbbuProxyNode.setPassword(rps.getPassword());	
				}
				nodes.add(hbbuProxyNode);
			}
		}
		return nodes;
	}
	
	
	private List<Integer> getPolicyTypeByAppType( EdgeApplicationType appType )
	{
		List<Integer> policyTypeList = new ArrayList<Integer>();
		switch (appType)
		{
		case CentralManagement:
//			policyTypeList.add( PolicyTypes.BackupAndArchiving );
//			policyTypeList.add( PolicyTypes.VMBackup );
			policyTypeList.add( PolicyTypes.Unified );
			break;
			
		case VirtualConversionManager:
			policyTypeList.add( PolicyTypes.VCM );
			policyTypeList.add( PolicyTypes.RemoteVCM );
			break;
			
		case vShpereManager:
			policyTypeList.add( PolicyTypes.VMBackup );
			break;
			
		default:
			policyTypeList.add( PolicyTypes.BackupAndArchiving );
		}
		return policyTypeList;
	}
	
	private EdgeHostPolicyMap getHostPolicyMapByHostId( int hostId, int policyType )
	{
		List<EdgeHostPolicyMap> mapList = new LinkedList<EdgeHostPolicyMap>();
		this.policyDao.getHostPolicyMap( hostId, policyType, mapList );
		if (mapList.size() == 0)
			return null;
		
		return mapList.get( 0 );
	}


	@Override
	public List<Node> getDeletedNodes() throws EdgeServiceFault {

		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_deletedhost_list(hosts);
		List<Node> nodes = new LinkedList<Node>();
		for (EdgeHost host : hosts)
			nodes.add(convertDaoNode2ContractNode(host));
		return nodes;
	}
	
	/**
	 * Via scanremotenode(registry) and tryconnectd2d(web service) to get the node information
	 */
	@Override
	public RemoteNodeInfo queryRemoteNodeInfo(GatewayId gatewayId, int hostId, String hostname, String username,
			@NotPrintAttribute String password, String protocol, int port) throws EdgeServiceFault {
		if(hostId > 0){
			GatewayEntity gateway = this.gatewayService.getGatewayByHostId( hostId);
			gatewayId = gateway.getId();
		}
		//validate admin account
		try{
			validateAdminAccount(gatewayId, hostname, username, password);
		}catch(EdgeServiceFault edgeServiceFault){
			logger.error("[NodeServiceImpl] queryRemoteNodeInfo() invoke validateAdminAccount() failed."+edgeServiceFault.getMessage()); 
			if(EdgeServiceErrorCode.Node_CantConnect_Admin$Disable.equals(edgeServiceFault.getFaultInfo().getCode())){
				logger.info("[NodeServiceImpl] queryRemoteNodeInfo() invoke validateAdminAccount() failed. "
						+ "because can't connect admin$, so invoke tryConnectD2DUseDefaultConfig()");
				if(haveAgentService(gatewayId, hostname, username, password)){
					return tryConnectD2DUseDefaultConfig(gatewayId,hostname,username,password,protocol,port);
				}else {
					RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
					nodeInfo.setD2DInstalled(false);
					return nodeInfo;
				}
			}else {
				logger.info("[NodeServiceImpl] queryRemoteNodeInfo() invoke validateAdminAccount() failed. "
						+ "throw the error to UI.");
				throw edgeServiceFault;
			}
		}
		
		//scan remote node (registry)
		String edgeUser = null;
		String edgeDomain = null;
		String edgePassword = null;
		if ((serviceImpl != null) && (serviceImpl.getSession() != null)){
			edgeUser =(String) serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_USERNAME);
			edgeDomain =(String) serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_DOMAIN);
			edgePassword = (String)serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_PASSWORD);
		}else{
			EdgeAccount ea = new EdgeAccount();
			BaseWSJNI.getEdgeAccount(ea);
			edgeUser = ea.getUserName();
			edgeDomain = ea.getDomain();
			edgePassword = ea.getPassword();
		}
		try{
			return DiscoveryService.getInstance().scanRemoteNode(gatewayId, edgeUser, edgeDomain, edgePassword, hostname, username, password);
		}catch(EdgeServiceFault e){
			//check agent service is running or not
			if(!haveAgentService(gatewayId, hostname, username, password)){
				RemoteNodeInfo nodeInfo = new RemoteNodeInfo();
				nodeInfo.setD2DInstalled(false);
				return nodeInfo;
			}
			
			logger.debug("[NodeServiceImpl] queryRemoteNodeInfo() -> scanRemoteNode() failed.",e);
			if (EdgeServiceErrorCode.Node_RemoteRegistry_WrongCredential.equals(e.getFaultInfo().getCode())) {				
				throw e;
			} else {
				//If scan remote node failed then use web service to get remote node info
				return tryConnectD2DUseDefaultConfig(gatewayId, hostname, username, password, protocol, port);
			}
		}
	}
	
	private boolean haveAgentService(GatewayId gatewayId, String hostname, String username,
			@NotPrintAttribute String password){
		try{
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
			String realServiceName = com.ca.arcserve.edge.app.base.serviceinfo.ServiceInfoConstants.AGENT_SERVICE_NAME;
			ServiceState agentServiceState = nativeFacade.checkServiceIsRunning(hostname, realServiceName, username, password);
			if(agentServiceState == ServiceState.NotExist){
				return false;
			}
		}catch(Exception ex){
			logger.error("[NodeServiceImpl] queryRemoteNodeInfo() invoke "
					+ "checkServiceIsRunning() failed."+ex.getMessage()+"continue to invoke scanRemoteNode().");
		}
		return true;
	}
	
	private RemoteNodeInfo tryConnectD2DUseDefaultConfig(GatewayId gatewayId, String d2dHost, String d2dUserName, String d2dPassword,String protocol, int port)throws EdgeServiceFault{
		logger.debug("[NodeServiceImpl] tryConnectD2DUseDefaultConfig() protocol is "+protocol+" port is "+port);
		if(protocol.equalsIgnoreCase("http") && port == 8014){
			try{
				return tryConnectD2D(gatewayId, "http", d2dHost, 8014, d2dUserName, d2dPassword);
			}catch(EdgeServiceFault e){
				if(EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort.equals(e.getFaultInfo().getCode())){
					return tryConnectD2D(gatewayId, "https", d2dHost, 8014, d2dUserName, d2dPassword);
				}else {
					throw e;
				}
			}
		}else if (protocol.equalsIgnoreCase("https") && port == 8014) {
			try{
				return tryConnectD2D(gatewayId, "https", d2dHost, 8014, d2dUserName, d2dPassword);
			}catch(EdgeServiceFault e){
				if(EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort.equals(e.getFaultInfo().getCode())){
					return tryConnectD2D(gatewayId, "http", d2dHost, 8014, d2dUserName, d2dPassword);
				}else {
					throw e;
				}
			}
		}else {
			try{
				return tryConnectD2D(gatewayId, protocol, d2dHost, port, d2dUserName, d2dPassword);
			}catch(EdgeServiceFault e){
				if(EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort.equals(e.getFaultInfo().getCode())){
					try{
						return tryConnectD2D(gatewayId, "http", d2dHost, port, d2dUserName, d2dPassword);
					}catch(EdgeServiceFault e1){
						if(EdgeServiceErrorCode.Node_CantConnect_WrongProtocolOrPort.equals(e1.getFaultInfo().getCode())){
							return tryConnectD2D(gatewayId, "https", d2dHost, port, d2dUserName, d2dPassword);
						}else {
							throw e1;
						}
					}
				}else {
					throw e;
				}
			}
		}
		
	}

	@Override
	public RemoteNodeInfo updateRemoteNodeInfo(int hostId, String hostname, String username,
			@NotPrintAttribute String password) throws EdgeServiceFault {
		
		GatewayEntity gateway = gatewayService.getGatewayByHostId(hostId);

		RemoteNodeInfo returnRemoteNodeInfo =  queryRemoteNodeInfo(gateway.getId(), hostId, hostname, username, password,"",0);
		if (returnRemoteNodeInfo == null || !returnRemoteNodeInfo.isRPSInstalled()) {
			return returnRemoteNodeInfo;
		}
		
		//for the case alone rps server without any plan
		logger.debug("validateUserAndUpdateIfNeeded. hostname: "+hostname+"username: "+username);
		
		String[] usernameParts = parseDomainUsername(username);
        String domain = usernameParts[0];
        String user = usernameParts[1];
        
		String protocol = returnRemoteNodeInfo.getD2DProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, hostname, returnRemoteNodeInfo.getD2DPortNumber());
		context.buildCredential(user, password, domain);
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.setConnectTimeout(30 * 1000);
			connection.setRequestTimeout(30 * 1000);
			connection.connect();
			
			connection.getService().validateUserAndUpdateIfNeeded(user, password, domain);
			logger.debug("update rps node return: " + connection.getNodeUuid());
		}
        
		return returnRemoteNodeInfo;
	}
	
	@Override
	public int createNewNodeGroup(GatewayId gatewayId, NodeGroup group, int[] assigedNodes) throws EdgeServiceFault{
		if (this.checkHTMLChars(group.getName()))
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ContainsHTMLChars, "");
		if (group.getName()!=null&&"".equals(group.getName().trim()))
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NodeNameIsSpace, "");
		int[] output = new int[1];
		hostMgrDao.as_edge_group_isexistedByGroupName(gatewayId.getRecordId(), group.getName(), output);

		if (output[0] != 0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NodeGroupAlreadyExist, "");

		hostMgrDao.as_edge_group_update(0, group.getName(), group.getComments(), 1, output);
		gatewayService.bindEntity(gatewayId, output[0], EntityType.CustomerGroup);
		for (int nodeID:assigedNodes){
			hostMgrDao.as_edge_group_assign(output[0], nodeID);
		}

		return output[0];
	}
	
	@Override
	public void deleteNode(final int id, boolean keepCurrentSettings) throws EdgeServiceFault {

		deleteNodeInternal(id, 1, keepCurrentSettings);
	}
	
	protected void deleteNodeInternal(final int id, int isVisible, boolean keepCurrentSettings) throws EdgeServiceFault {
		final List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(id, isVisible, hosts);

		if (hosts == null || hosts.size()==0)
			return;
		
		//generate begin log
		EdgeHost hostToDelete = hosts.get(0);
		logger.info("[NodeserviceImpl]deleteNodeInternal() Begin to delete the node: "+hostToDelete.getRhostname()+" the node id is: "+id);
		Node generateLogNode = new Node();
		generateLogNode.setHostname(hostToDelete.getRhostname());
		//not set nodeid for delete node activity log, because 
		//delete node will delete the log, we not want to delete this kind log when delete log
		//generateLogNode.setId(hostToDelete.getRhostid());
		String beginLog = EdgeCMWebServiceMessages.getMessage("deleteNodeStart",hostToDelete.getRhostname());
		generateLog(Severity.Information, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", beginLog), Module.DeleteNode);
		
		// Check weather node is recovery server of Instant VM
        if(InstantVMManager.getInstance().isRecoveryServer(id)){
        	hostMgrDao.as_edge_host_set_visible(id, 1);
        	String message = MessageReader.getMessage(
					"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
					EdgeServiceErrorCode.Node_Delete_Node_Is_Recovery_Server, hostToDelete.getRhostname());
			String failedMessageString = EdgeCMWebServiceMessages.getMessage("deleteNodeFailed", hostToDelete.getRhostname(),message);
			logger.error("[NodeserviceImpl] deleteNodeInternal() Delete node failed, because the node is used as recovery server.");
			generateLog(Severity.Error, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", failedMessageString), Module.DeleteNode);
			
			EdgeServiceFaultBean serviceFault = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Delete_Node_Is_Recovery_Server, "");
			serviceFault.setMessageParameters(new String[]{hostToDelete.getRhostname()});
			throw new EdgeServiceFault(failedMessageString, serviceFault);  
        };
        
		// Check weather node is converter or monitor or proxy
		List<EdgeHost> hostLst = new ArrayList<EdgeHost>();
		String ids = "(" + id + ")";		
		hostMgrDao.as_edge_hosts_list(ids, hostLst);
		EdgeConnectInfo connectInfo = getEdgeConnectInfoById(id);
		String uuid = (connectInfo == null) ? null : connectInfo.getUuid();
		if (hostLst.size() != 0 && uuid != null && !"".equals(uuid)) {
//			String uuid = hostLst.get(0).getD2DUUID();
			List<HostConnectInfo> converterList = new ArrayList<HostConnectInfo>();
			vsbDao.as_edge_vsb_is_converter(uuid, converterList);
			if (converterList.size() != 0) {
				int converterId = converterList.get(0).getId();
				List<EdgeHost> hostList = new ArrayList<EdgeHost>();
				vsbDao.as_edge_host_list_byConverterId(converterId, hostList);
				if (hostList.size() == 1 && hostList.get(0).getRhostid() == id) {
					logger.info("This node is a converter, and the node to be convert by it is the node itself, so it can be deleted.");
				} else {
					hostMgrDao.as_edge_host_set_visible(id, 1);
					
					String message = MessageReader.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							EdgeServiceErrorCode.Node_Delete_Node_Is_Converter);
					String failedMessageString = EdgeCMWebServiceMessages.getMessage("deleteNodeFailed", hostToDelete.getRhostname(),message);
					logger.error("[NodeserviceImpl] deleteNodeInternal() Delete node failed, because the node is used as converter.");
					generateLog(Severity.Error, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", failedMessageString), Module.DeleteNode);
					
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_Delete_Node_Is_Converter, ""));
				}
			}
			List<HostConnectInfo> monitorList = new ArrayList<HostConnectInfo>();
			vsbDao.as_edge_vsb_is_monitor(uuid, monitorList);
			if (monitorList.size() != 0) {
				int monitorId = monitorList.get(0).getId();
				List<EdgeHost> hostList = new ArrayList<EdgeHost>();
				vsbDao.as_edge_host_list_byMonitorId(monitorId, hostList);
				if (hostList.size() == 1 && hostList.get(0).getRhostid() == id) {
					logger.info("This node is a monitor, and the node monited by it is the node itself, so it can be deleted.");
				} else {
					hostMgrDao.as_edge_host_set_visible(id, 1);
					
					String message = MessageReader.getMessage(
							"com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",
							EdgeServiceErrorCode.Node_Delete_Node_Is_Monitor);
					String failedMessageString = EdgeCMWebServiceMessages.getMessage("deleteNodeFailed", hostToDelete.getRhostname(),message);
					logger.error("[NodeserviceImpl] deleteNodeInternal() Delete node failed, because the node is used as monitor.");
					generateLog(Severity.Error, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", failedMessageString), Module.DeleteNode);
					
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(
							EdgeServiceErrorCode.Node_Delete_Node_Is_Monitor, ""));
				}
			}
		}
		
		List<EdgeHost> lstProxy = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list_proxy(lstProxy);
		for (EdgeHost host : lstProxy) {
			if (host.getRhostid() == id) {
				hostMgrDao.as_edge_host_set_visible(id, 1);
				
				String message = MessageReader.getMessage("com.ca.arcserve.edge.app.base.resources.messages.ErrorMessages",EdgeServiceErrorCode.Node_Delete_Node_Is_Proxy);;
				String failedMessageString = EdgeCMWebServiceMessages.getMessage("deleteNodeFailed", hostToDelete.getRhostname(),message);
				logger.error("[NodeserviceImpl] deleteNodeInternal() Delete node failed, because the node is used as monitor.");
				generateLog(Severity.Error, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", failedMessageString), Module.DeleteNode);
				
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_Delete_Node_Is_Proxy , ""));
			}
		}
		
		//check linux d2d server
		if (ProtectionType.LINUX_D2D_SERVER.getValue() == hosts.get(0).getProtectionTypeBitmap()){
			linuxNodeService.checkLinuxD2DCanBeDeleted(id);
		}
		
		// We just set node invisible here for UI not display , and will remove records later. 
		// If node has policy , do remove records in policy unassign task by UnregisterNodeAfterUnassign flag
		hostMgrDao.as_edge_host_set_visible(id, -1);
		EdgeHost host = hosts.get(0);
		
		if(HostTypeUtil.isLinuxNode(host.getRhostType())){
			linuxNodeService.deleteLinuxNode(id, keepCurrentSettings);
			// try release license
			licenseService.deleteLicenseByMachine(host.getRhostname(), LicenseDef.UDP_CLIENT_TYPE.UDP_LINUX_AGENT);
		}else if (ProtectionType.LINUX_D2D_SERVER.getValue() == host.getProtectionTypeBitmap()){
			linuxNodeService.deleteLinuxD2DServer(id);
		}else{
			Node node = convertDaoNode2ContractNode(hosts.get(0));
			unregisterARCserveBackup(id, host, node);
			//unregisterD2D(id, node);
			
			// In r16.5, there may be several policy types in one application. So we
			// need to check if the node has policy of all these types, and try to
			// unassign the policy if the node has.
			//
			// But original design only support one policy type, i.e. once we finish
			// undeploying policy, we can unregister the node. If the node has multiple
			// types of policies, this design may cause problem. In r16.5, it's OK
			// since in this release, a node can only have one type of policy in one
			// application, though the application may have several types of policy.
			//
			// Since the time is very close to GM, after discussed with Zhenghua, we
			// will not do big change at this time. In later release, if we want to
			// support node have several policies in on application, we need to note
			// here.
			//
			// Pang, Bo (panbo01)
			// 2013-01-25
			
			if (HostTypeUtil.isVMWareVirtualMachine(host.getRhostType()) || HostTypeUtil.isHyperVVirtualMachine(host.getRhostType())){
				try{
					int[] totalCount = new int[1];
					hostMgrDao.as_edge_host_list_vms_by_samehyervisor(id, totalCount);
					if (totalCount[0]==0){
						List<Node> nodeList = new LinkedList<Node>();
						nodeList.add(node);
						this.populateVMInformation(nodeList);
						if (HostTypeUtil.isHyperVVirtualMachine(host.getRhostType())){
							licenseService.deleteLicenseByMachine(node.getHyperVisor(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
						}else{
							List<EdgeEsxVmInfo> vmList = new LinkedList<>();
							esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
							if(!vmList.isEmpty()){
								List<EdgeEsx> esxs = new LinkedList<>();
								esxDao.as_edge_esx_getHypervisorByHostId(node.getId(),esxs);
								if(!esxs.isEmpty()){
									if(esxs.get(0).getServertype()==VsphereEntityType.esxServer.getValue()){
										licenseService.deleteLicenseByMachine(vmList.get(0).getEsxHost(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
									}else {
										licenseService.deleteLicenseByMachine(node.getHyperVisor(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
									}
								}
							}
						}
					}
					
					//if it is vApp , then delete all the member vms
					if(HostTypeUtil.isVapp(host.getRhostType())){
						List<IntegerId> vmids = new LinkedList<IntegerId>();
						hostMgrDao.as_edge_host_getIdsByGroup(-40,id, vmids);
						for(IntegerId vmId : vmids){
							deleteNodeInternal(vmId.getId(), 2, false);
						}
					}
				}catch(Exception e){
					logger.error("error when try to release HBBU license", e);
				}
			}
			
			EdgeApplicationType appType = EdgeWebServiceContext.getApplicationType();
			List<Integer> policyTypeList = this.getPolicyTypeByAppType( appType );
			
			boolean hasPolicy = false;
			for (int policyType : policyTypeList)
			{
				if (this.doesNodeHavePolicy( id, policyType ))
				{
					try
					{
						PolicyManagementServiceImpl policyManagementServiceImpl =
							PolicyManagementServiceImpl.getInstance();
						
						int flags = 0;
						// defect 218413 VM node should set DeployFlag as UnregisterNodeAfterUnassign
						//if(node.isD2dInstalled() && node.getD2dManaged()==NodeManagedStatus.Managed)
							flags |= PolicyDeployFlags.UnregisterNodeAfterUnassign;
						if (keepCurrentSettings)
							flags |= PolicyDeployFlags.KeepCurrentSettingsWhenUnassin;
						
						policyManagementServiceImpl.removePolicyFromNodeImmedately(
							id, policyType, flags );
						
						hasPolicy = true;
					}
					catch (Exception e)
					{
						continue;
					}
				}
			}
	
			if (!hasPolicy) // the node doesn't have a policy
			{
				unregisterD2D( id, node );
				hostMgrDao.as_edge_host_remove(id);
				logger.info("NodeServiceImpl.deleteNodeInternal(): delete node, nodeId:" + id);
//			}else{
//				//defect 207788, work around
//				hostMgrDao.removeHistoryAndJobs(id); // this is not correct here, this log should be deleted in as_edge_host_remove
			}
			
			//try to remove branch server
			try{
				if (hosts.get(0).getARCserveType() != ABFuncServerType.GDB_PRIMARY_SERVER){
					
					//Add log for delete end
					logger.info("[NodeserviceImpl]deleteNodeInternal() Delete the node: "+hostToDelete.getRhostname()+" finished, the node id is: "+id);
					String endLog = EdgeCMWebServiceMessages.getMessage("deleteNodeFinished",hostToDelete.getRhostname());
					generateLog(Severity.Information, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", endLog), Module.DeleteNode);
					
					return;
				}
	
				List<EdgeArcserveConnectInfo> connectionList = new LinkedList<EdgeArcserveConnectInfo>();
				connectionInfoDao.as_edge_arcserve_connInfo_list_by_gdbbranchid(hosts.get(0).getRhostid(), connectionList);
	
				for(EdgeArcserveConnectInfo arcserveConnectInfo : connectionList){
					try{
						List<EdgeHost> branchServer = new LinkedList<EdgeHost>();
						hostMgrDao.as_edge_host_list(arcserveConnectInfo.getHostid(), 1, branchServer);
						if(branchServer.size()==0)
							continue;
	
						if (branchServer.get(0).getD2dManagedStatus() == NodeManagedStatus.Managed.ordinal()){
	
						}else{
							hostMgrDao.as_edge_host_remove(branchServer.get(0).getRhostid());
							logger.info("NodeServiceImpl.deleteNodeInternal(): delete node, nodeId:" + branchServer.get(0).getRhostid());
						}
					}catch(Exception e){
						logger.error(e);
					}
				}
			}catch(Exception e){
				logger.error(e);
			}
		}
		
		//Add log for delete end
		logger.info("[NodeserviceImpl]deleteNodeInternal() Delete the node: "+hostToDelete.getRhostname()+" finished, the node id is: "+id);
		String endLog = EdgeCMWebServiceMessages.getMessage("deleteNodeFinished",hostToDelete.getRhostname());
		generateLog(Severity.Information, generateLogNode, EdgeCMWebServiceMessages.getMessage("deleteNodePrefix", endLog), Module.DeleteNode);
		
	}
	private void unregisterARCserveBackup(final int id, EdgeHost host, Node node) {
		try {
			if (node.getArcserveManaged() == NodeManagedStatus.Managed){
				String uuid = CommonUtil.retrieveCurrentAppUUID();
				try {
				    ASBUSetting setting = getASBUSetting(id);
				    List<Integer> ids = new ArrayList<Integer>();
					ids.add(setting.getSchedule().getScheduleID());
					SchedulerUtilsImpl.getInstance().removeIDs(SyncArcserveIncJob.getInstance(),ids);
				}catch (EdgeSchedulerException e) {
					logger.error("$Thread.run() - exception ignored", e); //$NON-NLS-1$
				}

				List<EdgeArcserveConnectInfo> output = new LinkedList<EdgeArcserveConnectInfo>();
				connectionInfoDao.as_edge_arcserve_connect_info_list(id, output);

				if (output !=null && output.size()>0){
					GatewayEntity gateway = gatewayService.getGatewayByHostId(host.getRhostid());
					String sessionID = serviceImpl.ConnectARCserve(gateway, host.getRhostname(),
							output.get(0).getCauser(),
							output.get(0).getCapasswd(),
							ABFuncAuthMode.values()[output.get(0).getAuthmode()],
							output.get(0).getPort(),
							Protocol.values()[(output.get(0).getProtocol())]);
					serviceImpl.MarkArcserveManageStatus(sessionID, uuid, false, ABFuncManageStatus.UN_MANAGED);
				}
				connectionInfoDao.as_edge_arcserve_connect_remove(id);
			}else
				connectionInfoDao.as_edge_arcserve_connect_remove(id);
		} catch (EdgeServiceFault e){
			if (EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer.equals(e.getFaultInfo().getCode()))
				connectionInfoDao.as_edge_arcserve_connect_remove(id);
			//else
				//connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		} catch (Exception e1) {
			logger.error("$Thread.run() - exception ignored", e1); //$NON-NLS-1$
			//connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		}
	}
	private void unregisterD2D(final int id, Node node) {
		// unbind the license from License Module
		try{
			if(EdgeWebServiceContext.getApplicationType()==EdgeApplicationType.CentralManagement)
				licenseService.deleteLicenseByMachine(node.getHostname(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
		}catch (Exception e) {
			logger.error("Remove license failed.", e); //$NON-NLS-1$
		}
		
		try {
			if (node.getD2dManaged() == NodeManagedStatus.Managed){
				this.regService.RemoveRegInfoFromD2D(id, true);
				connectionInfoDao.as_edge_connect_remove(id);
			}else
				connectionInfoDao.as_edge_connect_remove(id);
		} catch (EdgeServiceFault e){
			if (EdgeServiceErrorCode.Node_D2D_UnReg_Not_Owner.equals(e.getFaultInfo().getCode()))
				connectionInfoDao.as_edge_connect_remove(id);
			//else
				//connectionInfoDao.as_edge_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		} catch (Exception e) {
			logger.error("$Thread.run() - exception ignored", e); //$NON-NLS-1$
			//connectionInfoDao.as_edge_connect_update_managedStatus(id, NodeManagedStatus.Unmanaged.ordinal());
		}
	}
	
	private boolean doesNodeHavePolicy( int nodeId, int policyType )
	{
		try
		{
			List<EdgeHostPolicyMap> mapList = new LinkedList<EdgeHostPolicyMap>();
			this.policyDao.getHostPolicyMap( nodeId, policyType, mapList );
			return (mapList.size() > 0);
		}
		catch (Exception e)
		{
			logger.error( "doesNodeHavePolicy() failed.", e );
			return false;
		}
	}
	
	public void unregisterD2D( int nodeId ) throws Exception
	{
		final List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list( nodeId, -1, hosts );

		if ((hosts == null) || (hosts.size() == 0))
			return;

		Node node = convertDaoNode2ContractNode( hosts.get( 0 ) );
		if (!node.isVMImportFromVSphere())
			unregisterD2D( nodeId, node );
	}

	@Override
	public void deleteNodeGroup(int groupID) throws EdgeServiceFault {
		hostMgrDao.as_edge_group_remove(groupID);
	}
	@Override
	public void deleteNodeESXGroup(int groupID,int type) throws EdgeServiceFault {
		if(type == NodeGroup.HYPERV) {
			hostMgrDao.as_edge_group_hyperv_remove(groupID,type);
			this.gatewayService.unbindEntity( groupID, EntityType.HyperVServer );
		} else {
			hostMgrDao.as_edge_group_esx_remove(groupID,type);
			if(type==NodeGroup.UNESX)
				this.gatewayService.unbindEntity( groupID, EntityType.CustomerGroup );
			else 
				this.gatewayService.unbindEntity( groupID, EntityType.VSphereEntity );			
		}
		
	}

	@Override
	public void deleteNodeESXGroupAndNodes(int groupID, int type) throws EdgeServiceFault {
		//delete nodes in this ESX group
		List<EdgeHost> nodes = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_esx_list_by_group_type_appstatus_prodType(
				groupID, 0, 0, type, -1, nodes);
		
		String hypervisor = null;
		List<EdgeStringValue> esxIdList = new LinkedList<EdgeStringValue>();
		if (type == NodeGroup.ESX)
			this.hostMgrDao.as_edge_esx_list_hostnames(groupID, esxIdList);
		else if (type == NodeGroup.HYPERV)
			if (nodes.size()>0)
				hypervisor = nodes.get(0).getHypervisorHostName();
		
		for(EdgeHost node : nodes){
			this.deleteNode(node.getRhostid(), false);	
		}
		//delete the ESX group
		this.deleteNodeESXGroup(groupID, type);
		try{
			if (type == NodeGroup.ESX)
				for (EdgeStringValue esxHypervisor:esxIdList){
					logger.info("release license for hypervisor:"+esxHypervisor.getValue());
					licenseService.deleteLicenseByMachine(esxHypervisor.getValue(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
				}
			else if (type == NodeGroup.HYPERV && hypervisor!=null)
				licenseService.deleteLicenseByMachine(hypervisor, LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
			
		}catch(Exception e){
			logger.error("failed to release license for hypervisor", e);
		}
	}

	@Override
	public void updateNewNodeGroup(NodeGroup group, int[] assigedNodes) throws EdgeServiceFault{
		if (this.checkHTMLChars(group.getName()))
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ContainsHTMLChars, "");

		int[] output = new int[1];

		hostMgrDao.as_edge_group_isexisted(group.getId(), group.getName(), output);

		if (output[0] != 0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NodeGroupAlreadyExist, "");

		hostMgrDao.as_edge_group_unassignall(group.getId());
		hostMgrDao.as_edge_group_update(group.getId(), group.getName(), group.getComments(), 1, output);
		for (int nodeID:assigedNodes){
			hostMgrDao.as_edge_group_assign(group.getId(), nodeID);
		}
	}

	@Override
	public boolean isLocalHost(String host) throws EdgeServiceFault {
		logger.debug("isLocalHost begin, host:" + host);
		boolean isLocal = CommonUtil.isLocalHost(host);
		logger.debug("isLocalHost end, isLocal:" + isLocal);
		return isLocal;
	}
	
	@Override
	public String getLicenseText()throws EdgeServiceFault{
		return ProductDeployServiceImpl.getInstance().getLicenseText();
	}
	/* End of Deploy */
	@Override
	public AutoDiscoverySetting getAutoDiscoverySettings(AutoDiscoverySetting.SettingType settingType)
			throws EdgeServiceFault {

		return EdgeDiscoverySettingModel.getUISetting(settingType);
	}

	@Override
	public void saveAutoDiscoverySettings(AutoDiscoverySetting settings, AutoDiscoverySetting.SettingType settingType)
			throws EdgeServiceFault {
		ScheduleData scheduleData = settings.getSchedule();
		scheduleData.setStartFromDate(generateRepeatFromDateByScheduleTime(scheduleData.getScheduleTime()));
		EdgeDiscoverySettingModel model = EdgeDiscoverySettingModel
				.getModel(settings);
		String xml;
		try {
			xml = EdgeDiscoverySetting.getString(model);
		} catch (JAXBException e) {
			throw EdgeServiceFault
					.getFault(EdgeServiceErrorCode.Common_Service_General, e
							.getMessage());
		}
		String repeatMethodParameter = SchedulerHelp.createRepeatMethodParameter(scheduleData);
		String repeatUntilParameter = SchedulerHelp.createRepeatUntilParameter(scheduleData);
		int scheduleid = settingDao.as_edge_discovery_setting_set(xml, scheduleData
				.getScheduleName(), scheduleData.getScheduleDescription(), 2,
				scheduleData.getRepeatMethodData().getRepeatMethodType()
						.getValue(), repeatMethodParameter, scheduleData
						.getScheduleTime(), scheduleData.getStartFromDate(),
				scheduleData.getRepeatUntilType().getValue(),
				repeatUntilParameter, settingType.ordinal());

		try {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(scheduleData.getScheduleID());
			SchedulerUtilsImpl.getInstance().clearIDs(
					AutoDiscoveryJob.getInstance());
		} catch (EdgeSchedulerException e) {
			logger.error(e.getMessage(), e);
		}
		scheduleData.setScheduleID(scheduleid);

		if (model.getStatus() == ASBUSettingStatus.enabled
				&& (model.getaDStatus() == ASBUSettingStatus.enabled || model
						.getuDPStatus() == ASBUSettingStatus.enabled)) {
			try {
				SchedulerUtilsImpl.getInstance().updateScheduler(
						AutoDiscoveryJob.getInstance(), scheduleData);
			} catch (EdgeSchedulerException e) {
				throw EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Common_Service_General, e
								.getMessage());
			}
		}
	}

/*
	@Override
	public String[] registerNode(NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		EdgeHost node = populateEdgeHost(registrationNodeInfo);

		//check ARCserve caroot username/password
		if (registrationNodeInfo.getNodeInfo().isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup()){
			ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[registrationNodeInfo.getAbAuthMode()];
			serviceImpl.ConnectARCserve(node.getRhostname(), registrationNodeInfo.getCarootUsername(),
					registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(), arcserveAuthMode, registrationNodeInfo.getArcservePort(), registrationNodeInfo.getArcserveProtocol());
		}

		//check exists
		if (isNodeExists(node.getRhostname(), node.getIpaddress()))
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_AlreadyExist , ""));

		//insert Node
		int[] output = new int[1];
		hostMgrDao.as_edge_host_update(0, 0, 0, 0, 0, node.getLastupdated(), node.getRhostname(),
					"", node.getIpaddress(), node.getOsdesc(), "", "",	0, 0, 0, 0, 0, 0, 1, node.getAppStatus(), "", output);
		node.setRhostid(output[0]);
		connectionInfoDao.as_edge_connect_info_update(output[0], registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(),
				nodeInfo.getD2DUUID(), nodeInfo.getD2DProtocol().ordinal(), nodeInfo.getD2DPortNumber(), 0,
				nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getD2DBuildNumber(), NodeManagedStatus.Unknown.ordinal());
		if (nodeInfo.isARCserveBackInstalled())
			connectionInfoDao.as_edge_arcserve_connect_info_update_gdb(output[0], registrationNodeInfo.getCarootUsername(), registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(),
					registrationNodeInfo.getAbAuthMode(), nodeInfo.getARCserveProtocol().ordinal(), nodeInfo.getARCservePortNumber(), nodeInfo.getARCserveType().ordinal(), nodeInfo.getARCserveVersion(),0, NodeManagedStatus.Unknown.ordinal());

		String[] errorCodes = new String[2];
		errorCodes[0] = tryMarkD2DAsManaged(registrationNodeInfo, node, false);
		errorCodes[1] = tryMarkARCserveAsManaged(registrationNodeInfo, node, false);

		return errorCodes;
	}
*/	@Override
	public RegistrationNodeResult registerNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		return registerNode(failedReadRemoteRegistry, registrationNodeInfo, false);
	}

	private void connectD2D(NodeRegistrationInfo registrationNodeInfo, String[] nodeUuid, String[] authUuid) throws EdgeServiceFault {
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		String protocol = nodeInfo.getD2DProtocol() == Protocol.Https ? "https" : "http";
		ConnectionContext context = new ConnectionContext(protocol, registrationNodeInfo.getNodeName(), nodeInfo.getD2DPortNumber());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		
		try {
			GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
			context.setGateway(gateway);
		} catch (EdgeServiceFault e) {
			logger.error("Failed to get gateway by id " + registrationNodeInfo.getGatewayId() + ". " + e.getFaultInfo().getMessage());
		}
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			nodeUuid[0] = connection.getNodeUuid();
			authUuid[0] = connection.getAuthUuid();
		}
	}

	public RegistrationNodeResult registerNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo, boolean overwrightManage) throws EdgeServiceFault {
//		this.validateAdminAccount(registrationNodeInfo.getNodeName(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());
		EdgeHost node = populateEdgeHost(registrationNodeInfo);
//		List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(node.getRhostname());
		List<String> fqdnNameList = new ArrayList<String>();
		if(node.getGatewayId() != 0){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( new GatewayId(node.getGatewayId()));
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(node.getRhostname());
			} catch (Exception e) {
				logger.error("[NodeServiceImpl] registerNode() get fqdn name failed.",e);
			}
		}
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		//check exists
		if (getHostIdByName(node.getGatewayId(), node.getRhostname(), node.getIpaddress(),fqdnNameList,1)>0)
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_AlreadyExist , ""));
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		String strSessionNoToGetABVersion = null;
		if (registrationNodeInfo==null||StringUtil.isEmptyOrNull(registrationNodeInfo.getUsername())
				||StringUtil.isEmptyOrNull(registrationNodeInfo.getPassword())) {
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_InvalidUser , ""));
		}
		//check ARCserve caroot username/password
		if (nodeInfo.isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup()){
			GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
			ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[registrationNodeInfo.getAbAuthMode().ordinal()];
			strSessionNoToGetABVersion = serviceImpl.ConnectARCserve(gateway,registrationNodeInfo.getNodeName(), registrationNodeInfo.getCarootUsername(),
					registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(), arcserveAuthMode, registrationNodeInfo.getArcservePort(), registrationNodeInfo.getArcserveProtocol());
		}

		if(failedReadRemoteRegistry)
		{
			RemoteNodeInfo d2dRemoteNodeInfo = null;
			if(nodeInfo.isD2DInstalled()||nodeInfo.isD2DODInstalled() /*&& registrationNodeInfo.isRegisterD2D()*/)
			{
				try {
					d2dRemoteNodeInfo = tryConnectD2D(registrationNodeInfo.getGatewayId(), (registrationNodeInfo.getD2dProtocol() == Protocol.Https)?"https":"http", registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());
				} catch (EdgeServiceFault e) {
					// If failed, try to connect web service with another protocol
					d2dRemoteNodeInfo = tryConnectD2D(registrationNodeInfo.getGatewayId(), (registrationNodeInfo.getD2dProtocol() == Protocol.Https)?"http":"https", registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());
				}
				if(d2dRemoteNodeInfo == null)
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_D2D_Reg_connection_refuse , ""));

				nodeInfo.setD2DMajorVersion(d2dRemoteNodeInfo.getD2DMajorVersion());
				nodeInfo.setD2DMinorVersion(d2dRemoteNodeInfo.getD2DMinorVersion());
				nodeInfo.setUpdateVersionNumber(d2dRemoteNodeInfo.getUpdateVersionNumber());
				nodeInfo.setD2DUUID(d2dRemoteNodeInfo.getD2DUUID());
				nodeInfo.setD2DBuildNumber(d2dRemoteNodeInfo.getD2DBuildNumber());
				nodeInfo.setHostEdgeServer(d2dRemoteNodeInfo.getHostEdgeServer());
				registrationNodeInfo.getNodeInfo().setD2DInstalled(d2dRemoteNodeInfo.isD2DInstalled());
				registrationNodeInfo.getNodeInfo().setD2DODInstalled(d2dRemoteNodeInfo.isD2DODInstalled());
				registrationNodeInfo.getNodeInfo().setOsDescription(d2dRemoteNodeInfo.getOsDescription());
				registrationNodeInfo.getNodeInfo().setExchangeInstalled(d2dRemoteNodeInfo.isExchangeInstalled());
				registrationNodeInfo.getNodeInfo().setSQLServerInstalled(d2dRemoteNodeInfo.isSQLServerInstalled());
			}
			if (nodeInfo.isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup())
			{
				if(strSessionNoToGetABVersion != null && !strSessionNoToGetABVersion.isEmpty())
				{
					ArrayOfstring abVersionInfoArray = serviceImpl.getArcserveVersionInfo(strSessionNoToGetABVersion);
					if(abVersionInfoArray != null)
					{
						List<String> abVersionInfoList = abVersionInfoArray.getString();
						if(abVersionInfoList != null)
						{
							nodeInfo.setARCserveVersion(abVersionInfoList.get(0)+"."+abVersionInfoList.get(1));
						}
					}
					ABFuncServerType arcserveBackupType = serviceImpl.GetServerType(strSessionNoToGetABVersion);
					nodeInfo.setARCserveType(arcserveBackupType);
					if(arcserveBackupType == ABFuncServerType.BRANCH_PRIMARY || arcserveBackupType == ABFuncServerType.ARCSERVE_MEMBER || arcserveBackupType == ABFuncServerType.UN_KNOWN)
					{
						registrationNodeInfo.setRegisterARCserveBackup(false);
					}
				}
			}
		}

		node = populateEdgeHost(registrationNodeInfo);
		
		String nodeUuid = nodeInfo.getD2DUUID();
		String authUuid = null;
		if (nodeInfo.isD2DInstalled()) {
			String[] outNodeUuid = new String[1];
			String[] outAuthUuid = new String[1];
			connectD2D(registrationNodeInfo, outNodeUuid, outAuthUuid);
			nodeUuid = outNodeUuid[0];
			authUuid = outAuthUuid[0];
		}

		//insert Node
		int[] output = new int[1];
		
		String hostName = node.getRhostname();
		if(!StringUtil.isEmptyOrNull(hostName))
			hostName = hostName.toLowerCase();
		
		hostMgrDao.as_edge_host_update(-1, node.getLastupdated(), hostName,node.getNodeDescription(),
					node.getIpaddress(), node.getOsdesc(),node.getOstype(),1, node.getAppStatus(),
					"",node.getRhostType(), registrationNodeInfo.getProtectionType().getValue(),fqdnNames, output);
		node.setRhostid(output[0]);
		connectionInfoDao.as_edge_connect_info_update(output[0], registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(),
				nodeUuid, nodeInfo.getD2DProtocol().ordinal(), nodeInfo.getD2DPortNumber(), 0,
				nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getUpdateVersionNumber(), nodeInfo.getD2DBuildNumber(), NodeManagedStatus.Unknown.ordinal());
		
		//bind gateway
		bindEntityToGateway(node.getRhostid(), registrationNodeInfo.getGatewayId(), EntityType.Node);
		
		if (authUuid != null) {
			connectionInfoDao.as_edge_connect_info_setAuthUuid(nodeUuid, authUuid);
		}
		
		if (nodeInfo.isARCserveBackInstalled())
			connectionInfoDao.as_edge_arcserve_connect_info_update_gdb(output[0], registrationNodeInfo.getCarootUsername(), registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(),
					registrationNodeInfo.getAbAuthMode().ordinal(), nodeInfo.getARCserveProtocol().ordinal(), nodeInfo.getARCservePortNumber(), nodeInfo.getARCserveType().ordinal(), nodeInfo.getARCserveVersion(),0, NodeManagedStatus.Unknown.ordinal());

		if (registrationNodeInfo.isVMWareVM()) {
			hostMgrDao.as_edge_host_updateMachineType(output[0], LicenseMachineType.VSHPERE_VM.getValue());
		} else if (registrationNodeInfo.isHyperVVM()) {
			hostMgrDao.as_edge_host_updateMachineType(output[0], LicenseMachineType.HYPER_V_VM.getValue());
		} else {
			tryDetectMachineType(output[0], registrationNodeInfo.getNodeName(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());
		}
		
		RegistrationNodeResult result = new RegistrationNodeResult();
		String[] errorCodes = new String[2];
		errorCodes[0] = tryMarkD2DAsManaged(registrationNodeInfo, node, overwrightManage);
		errorCodes[1] = tryMarkARCserveAsManaged(registrationNodeInfo, node, overwrightManage);
//		if (nodeInfo.isD2DInstalled()) {		
//			D2DStatusCommonService.getD2DStatusCommonServiceInstance().updateD2DStatus(node, nodeInfo.getD2DProtocol(), nodeInfo.getD2DPortNumber(), nodeInfo.getD2DUUID());
//		} else {
//			D2DStatusCommonService.getD2DStatusCommonServiceInstance().saveD2DStatusInfo(node.getRhostid(), new com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo());
//		}
		if((!nodeInfo.isARCserveBackInstalled()) && (nodeInfo.isD2DInstalled()||nodeInfo.isD2DODInstalled())) {
			if(errorCodes[0] == EdgeServiceErrorCode.Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE) {
				logger.debug("D2D cannot access Edge Web Service, rollback adding the D2D node!");
				rollbackAddedD2DNode(output[0]);
				EdgeServiceFaultBean faultBean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE , "");
				faultBean.setMessageParameters(new String[]{EdgeCommonUtil.getGatewayHostNameByGateWayId(registrationNodeInfo.getGatewayId())});
				throw new EdgeServiceFault("", faultBean);
			}
		}
		
		result.setErrorCodes(errorCodes);
		result.setHostID(output[0]);
		return result;
	}

	public void rollbackAddedD2DNode(int d2dHostId) {
		try {
			connectionInfoDao.as_edge_connect_remove(d2dHostId);
			hostMgrDao.as_edge_host_remove(d2dHostId);
			logger.info("NodeServiceImpl.rollbackAddedD2DNode(): delete node, nodeId:" + d2dHostId);
		}catch(Throwable t) {
			logger.error(t.toString());
		}
	}

	public String tryMarkARCserveAsManaged(NodeRegistrationInfo registrationNodeInfo, EdgeHost host, boolean overwrite) throws EdgeServiceFault{
		logger.debug("tryMarkARCserveAsManaged(NodeRegistrationInfo, EdgeHost) - start"); //$NON-NLS-1$

		if (!registrationNodeInfo.getNodeInfo().isARCserveBackInstalled() || !registrationNodeInfo.isRegisterARCserveBackup() ||
				registrationNodeInfo.getNodeInfo().getARCserveType() == ABFuncServerType.ARCSERVE_MEMBER)
			return null;

		try{
			ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[registrationNodeInfo.getAbAuthMode().ordinal()];
			GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
			String sessionID = serviceImpl.ConnectARCserve(gateway, host.getRhostname(), registrationNodeInfo.getCarootUsername(),
					registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(), arcserveAuthMode, registrationNodeInfo.getArcservePort(), registrationNodeInfo.getArcserveProtocol());

			//check whether it can be marked as managed
			String uuid = CommonUtil.retrieveCurrentAppUUID();

			ABFuncManageStatus managedStatus = serviceImpl.GetArcserveManageStatus(sessionID, uuid);
			if (managedStatus == ABFuncManageStatus.MANAGED){
				//throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_AlreadyManagedByMyself , ""));
				logger.debug("This ARCserve has been marked by myself, igore backend error");
			}
			
			if (!overwrite && managedStatus == ABFuncManageStatus.MANAGED_BY_OTHER_SERVER) {
				String managedEdgeHost = serviceImpl.GetManagedEdgeServer(sessionID);
				
				// For fixing uninstall/reinstall Edge service, then add same node will popup 
				// warning dialog case.
				InetAddress inetAddr = InetAddress.getLocalHost();
				if (!inetAddr.getHostName().equalsIgnoreCase(managedEdgeHost)) {
					registrationNodeInfo.getNodeInfo().setHostEdgeServer(
							managedEdgeHost);
					throw new EdgeServiceFault(
							"",
							new EdgeServiceFaultBean(
									EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer,
									""));
				} else {
					overwrite = !overwrite;
				}
			}
			
			ABFuncServerType arcserveBackupType = serviceImpl.GetServerType(sessionID);
			if(arcserveBackupType != ABFuncServerType.BRANCH_PRIMARY && arcserveBackupType != ABFuncServerType.ARCSERVE_MEMBER && arcserveBackupType != ABFuncServerType.UN_KNOWN)
			{
				String arcserveUUID = serviceImpl.MarkArcserveManageStatus(sessionID, uuid, overwrite, ABFuncManageStatus.MANAGED);
				connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(host.getRhostid(), NodeManagedStatus.Managed.ordinal(), arcserveUUID);
				logger.debug("tryMarkARCserveAsManaged(NodeRegistrationInfo, EdgeHost) - end"); //$NON-NLS-1$
			}
			else
			{
				logger.debug("tryMarkARCserveAsManaged(NodeRegistrationInfo, EdgeHost) - failed because its type can't be managed.");
			}
			return null;
		}catch(EdgeServiceFault e){
			logger.error("tryMarkARCserveAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
			connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(host.getRhostid(), NodeManagedStatus.Unmanaged.ordinal(), null);
			return e.getFaultInfo().getCode();
		}catch(Exception e){
			logger.error("tryMarkARCserveAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
			connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(host.getRhostid(), NodeManagedStatus.Unmanaged.ordinal(), null);
			return EdgeServiceErrorCode.Common_Service_General;
		}
	}
	
	public String tryMarkD2DAsManaged(NodeRegistrationInfo registrationNodeInfo, EdgeHost host, boolean overwrite) throws EdgeServiceFault{
		return tryMarkD2DAsManaged(registrationNodeInfo, host, overwrite, false);
	}

	public String tryMarkD2DAsManaged(NodeRegistrationInfo registrationNodeInfo, EdgeHost host, boolean overwrite, boolean isUpdateNode) throws EdgeServiceFault{
		ConnectionContext context = new ConnectionContext(registrationNodeInfo.getD2dProtocol(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		context.setGateway(gateway);
		
		logger.info("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) - start"); //$NON-NLS-1$
		
		if (!(registrationNodeInfo.getNodeInfo().isD2DInstalled())
				|| !registrationNodeInfo.isRegisterD2D())
			return null;
		
		IEdgeD2DRegService regService = null;
		
		try{
			regService = EdgeD2DRegServiceFactory.create();
			if(host.getRhostid() != 0){
				EdgeCommonUtil.changeNodeManagedStatus(host.getRhostid(), NodeManagedStatus.Managed);
			}
			regService.UpdateRegInfoToD2D(context, host.getRhostid(), overwrite);			
			logger.info("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) - end"); //$NON-NLS-1$
			return null;
			
		}catch(EdgeServiceFault e){
			
			if(EdgeServiceErrorCode.Node_D2D_Reg_Again.equalsIgnoreCase(e.getFaultInfo().getCode())) //Agent managed successfully
			{
				logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) Node_D2D_Reg_Again", e);
				if(host.getRhostid() != 0){
					EdgeCommonUtil.changeNodeManagedStatus(host.getRhostid(), NodeManagedStatus.Managed);
				}
				return null;
				
			}else {//Agent managed failed
				logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
				if(host.getRhostid() != 0){
					EdgeCommonUtil.changeNodeManagedStatus(host.getRhostid(), NodeManagedStatus.Unmanaged);
				}
				
				if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equalsIgnoreCase(e.getFaultInfo().getCode())){
					String message = e.getMessage();
					String hostEdgeNameOfD2D = message.substring(message.indexOf('^')+1);
					registrationNodeInfo.getNodeInfo().setHostEdgeServer(hostEdgeNameOfD2D);
					return e.getFaultInfo().getCode();
				}else {
					throw e;
				}
			}
			
		}catch(Exception e){
			
			logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
			if(host.getRhostid() != 0){
				EdgeCommonUtil.changeNodeManagedStatus(host.getRhostid(), NodeManagedStatus.Unmanaged);
			}
			return EdgeServiceErrorCode.Common_Service_General;
			
		}	
		
	}

	/*
	public String tryMarkD2DAsManaged(NodeRegistrationInfo registrationNodeInfo, EdgeHost host, boolean overwrite, boolean isUpdateNode) throws EdgeServiceFault{
		ConnectionContext context = new ConnectionContext(registrationNodeInfo.getD2dProtocol(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		context.setGateway(gateway);
		
		logger.info("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) - start"); //$NON-NLS-1$
		
		if (!(registrationNodeInfo.getNodeInfo().isD2DInstalled())
				|| !registrationNodeInfo.isRegisterD2D())
			return null;
		
		MarkD2DAsManagedResult result = tryMarkD2DAsManaged( context, host.getRhostid(), overwrite, isUpdateNode );
		
		if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equalsIgnoreCase( result.getErrorCode() ))
			registrationNodeInfo.getNodeInfo().setHostEdgeServer( result.getHostName() );
		
		return result.getErrorCode();
	}
	
	public static class MarkD2DAsManagedResult
	{
		private String errorCode = null;
		private String hostName = "";
		
		public String getErrorCode()
		{
			return errorCode;
		}
		
		public void setErrorCode( String errorCode )
		{
			this.errorCode = errorCode;
		}
		
		public String getHostName()
		{
			return hostName;
		}
		
		public void setHostName( String hostName )
		{
			this.hostName = hostName;
		}
	}

	public MarkD2DAsManagedResult tryMarkD2DAsManaged(
		ConnectionContext context, int hostId, boolean overwrite, boolean isUpdateNode ) throws EdgeServiceFault
	{
		MarkD2DAsManagedResult result = new MarkD2DAsManagedResult();
		
		IEdgeD2DRegService regService = null;
		
		try{
			regService = EdgeD2DRegServiceFactory.create();
			if(hostId != 0){
				EdgeCommonUtil.changeNodeManagedStatus(hostId, NodeManagedStatus.Managed);
			}
			regService.UpdateRegInfoToD2D(context, hostId, overwrite);			
			logger.info("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) - end"); //$NON-NLS-1$
			result.setErrorCode( null );
			return result;
			
		}catch(EdgeServiceFault e){
			
			if(EdgeServiceErrorCode.Node_D2D_Reg_Again.equalsIgnoreCase(e.getFaultInfo().getCode())) //Agent managed successfully
			{
				logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost) Node_D2D_Reg_Again", e);
				if(hostId != 0){
					EdgeCommonUtil.changeNodeManagedStatus(hostId, NodeManagedStatus.Managed);
				}
				result.setErrorCode( null );
				return result;
				
			}else {//Agent managed failed
				logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
				if(hostId != 0){
					EdgeCommonUtil.changeNodeManagedStatus(hostId, NodeManagedStatus.Unmanaged);
				}
				
				if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equalsIgnoreCase(e.getFaultInfo().getCode())){
					String message = e.getMessage();
					String hostEdgeNameOfD2D = message.substring(message.indexOf('^')+1);
					result.setErrorCode( e.getFaultInfo().getCode() );
					result.setHostName( hostEdgeNameOfD2D );
					return result;
				}else {
					throw e;
				}
			}
			
		}catch(Exception e){
			
			logger.debug("tryMarkD2DAsManaged(NodeRegistrationInfo, EdgeHost)", e); //$NON-NLS-1$
			if(hostId != 0){
				EdgeCommonUtil.changeNodeManagedStatus(hostId, NodeManagedStatus.Unmanaged);
			}
			result.setErrorCode( EdgeServiceErrorCode.Common_Service_General );
			return result;
			
		}	
		
	}
	*/

	@Override
	public void markNodeAsManaged(NodeRegistrationInfo nodeInfo,
			boolean overwrite) throws EdgeServiceFault {
		try{
			if(nodeInfo.getId() != 0){
				GatewayEntity gateway = this.gatewayService.getGatewayByHostId( nodeInfo.getId());
				nodeInfo.setGatewayId(gateway.getId());
			}
			markNodeAsManagedNoActivityLog(nodeInfo, overwrite);
			//generate successfull activitylog
			String message = EdgeCMWebServiceMessages.getMessage("ManageNodeSuccessful", nodeInfo.getNodeName());
			String manageNodeLog = EdgeCMWebServiceMessages.getMessage("ManagedNode_Log",message);
			generateLogForRegInfo(Severity.Information, nodeInfo, 
					manageNodeLog, Module.ManageMultipleNodes);
		}catch(Exception e){
			//Error ActivityLog
			NodeExceptionUtil.generateActivityLogByExceptionForRegInfo(Module.ManageMultipleNodes, nodeInfo, "ManagedNode_Log", e);
			throw e;
		}
	}
	
	public void markNodeAsManagedNoActivityLog(NodeRegistrationInfo nodeInfo,
			boolean overwrite) throws EdgeServiceFault{
		EdgeHost node = populateEdgeHost(nodeInfo);
		//It is not right to getid by hostname and ip, because the db may conatins two nodes which have same name and one is physical
		//Another is vm
		//node.setRhostid(this.getHostIdByHostNameOrIP(node.getRhostname(), node.getIpaddress()));

		String errCode = tryMarkD2DAsManaged(nodeInfo, node, overwrite);
		if (errCode != null) {
			throw EdgeServiceFault.getFault(errCode,
					"Error ocurred during mark D2D as managed ");
		}
		
		errCode = tryMarkARCserveAsManaged(nodeInfo, node, overwrite);
		if (errCode != null) {
			throw EdgeServiceFault.getFault(errCode,
					"Error ocurred during mark ARCserve as managed ");
		}
		
		if(overwrite){
			//Try to redeploy plan
			PolicyManagementServiceImpl policyManagementServiceImpl =
					PolicyManagementServiceImpl.getInstance();
			logger.info("Do deploy agent plan for node: "+nodeInfo.getNodeName());
			policyManagementServiceImpl.deployPlanByNodeId(nodeInfo.getId(),PolicyDeployReasons.PolicyContentChanged);
			
			//try to redeploy plan when this node is HBBU proxy
			logger.info("Do deploy hbbu plan by proxy: "+nodeInfo.getNodeName());
			policyManagementServiceImpl.deployHBBUPlanByProxyNodeId(nodeInfo.getId());
		}
	}
	
	@Override
	public int markMultiNodesAsManaged(List<Integer> nodeIds, boolean overWrite)
			throws EdgeServiceFault {
		ManageMultiNodesParameter parameter = new ManageMultiNodesParameter();
		parameter.setModule(Module.ManageMultipleNodes);
		for(Integer id : nodeIds){
			parameter.getEntityIds().add(id);
		}
		ActionTaskManager<Integer> manager = new ActionTaskManager<Integer>(parameter,serviceImpl);
		parameter.setForceManage(overWrite);
		return manager.doAction();
	}
	
	@Override
	public void markRpsNodeAsManagedById(int rpsNodeId,
			boolean overwrite) throws EdgeServiceFault {
		rpsNodeService.markRpsNodeAsManagedById(rpsNodeId, overwrite);
	}
	
	@Override
	public NodeManageResult queryNodeManagedStatus(NodeRegistrationInfo registrationNodeInfo) 
			throws EdgeServiceFault{
		NodeManageResult result = new NodeManageResult();
		ConnectionContext context = new ConnectionContext(registrationNodeInfo.getD2dProtocol(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getD2dPort());
		context.buildCredential(registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(registrationNodeInfo.getGatewayId());
		context.setGateway(gateway);
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context)))
		{
			connection.connect();
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
			NodeManagedStatusByConsole status = NodeManagedStatusByConsole.parse(regStatus);
			result.setManagedStatus(status);
			if (NodeManagedStatusByConsole.ManagedByAnotherConsole == status) {
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();	
				if (edgeInfo != null) {				
					String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
					if(CommonUtil.isSameHost(consoleName, EdgeCommonUtil.getLocalFqdnName())){
						result.setManagedStatus(NodeManagedStatusByConsole.ManagedByCurrentConsle);
					}
					if(!StringUtil.isEmptyOrNull(consoleName))
						CurRegisteredEdgeHostName = consoleName;
				}
				result.setMnanagedConsoleName(CurRegisteredEdgeHostName);
			}
		}
		return result;
	}
	
	public int getHostIdByName(int gatewayid, String name, String ip, List<String> fqdnNameList, int isVisible){
		int[] output = new int[1];
		hostMgrDao.as_edge_host_getIdByHostnameIp(gatewayid, name, ip, isVisible, output);
		int hostId = output[0];
		
		// check duplication again with DNS resolved host name or IP address
		if(hostId <= 0){
			InetAddress[] addrs = null;
			try {
				addrs = InetAddress.getAllByName(name);
			} catch (UnknownHostException e) {
				addrs = null;
			}
			if (addrs != null) {
				for (InetAddress addr : addrs) {
					hostId = getHostIdByHostNameOrIP(gatewayid, addr.getHostName(), addr.getHostAddress(),isVisible);
					if (hostId > 0) {
						break;
					}
				}
			}
		}
		// check fqdn name
		if(hostId <= 0 && fqdnNameList != null){
			//check duplication with fqdn name
			for(String fqdnName : fqdnNameList){
				hostId = getHostIdByFqdnName(gatewayid, fqdnName);
				if (hostId > 0) {
					break;
				}
			}
		}
		
		return hostId;
	}

	/**
	 * This function is used in adding node process to check if a node was
	 * already added. Originally, it will try to match host name OR IP address.
	 * Since IP of a host may change and same IP may be used in different host
	 * as time goes by, checking IP address will block adding the other host.
	 * So we make IP address to be ignored by changed SQL.
	 * 
	 * Pang, Bo (panbo01)
	 * 2014-09-24
	 * 
	 * @param	name
	 * 			Host name of the host.
	 * @param	ip
	 * 			IP address of the host.
	 * @return	Host ID of the matched host, or 0 if no node matches.
	 */
	@Override
	public int getHostIdByHostNameOrIP(int gatewayid, String name, String ip, int isVisible) {
		int[] output = new int[1];
		hostMgrDao.as_edge_host_getIdByHostnameIp(gatewayid, name, ip, isVisible, output);
		return output[0];
	}
	
	public int getHostIdByFqdnName(int gatewayid, String fqdnName) {
		int[] output = new int[1];
		hostMgrDao.as_edge_host_getIdByFqdnName(gatewayid, fqdnName, output);
		return output[0];
	}
	
	public EdgeHost populateEdgeHost(NodeRegistrationInfo registrationNodeInfo){
		return populateEdgeHost(registrationNodeInfo, true);
	}

	public EdgeHost populateEdgeHost(NodeRegistrationInfo registrationNodeInfo, boolean updateRhostType){
		
		EdgeHost host = new EdgeHost();
		host.setRhostid(registrationNodeInfo.getId());
		host.setRhostname(registrationNodeInfo.getNodeName());
		host.setNodeDescription(registrationNodeInfo.getNodeDescription());
		host.setGatewayId(registrationNodeInfo.getGatewayId().getRecordId());
		if (registrationNodeInfo.getNodeName()==null || registrationNodeInfo.getNodeName().isEmpty())
			host.setIpaddress("");
		else{
			try
			{
				IRemoteNativeFacade nativeFacade =
					remoteNativeFacadeFactory.createRemoteNativeFacade( registrationNodeInfo.getGatewayId() );
				String ipAddress = nativeFacade.getIpByHostName( registrationNodeInfo.getNodeName() );
				host.setIpaddress(ipAddress);
			} catch (UnknownHostException e) {
				logger.error("populateEdgeHost(String, RemoteNodeInfo)", e); //$NON-NLS-1$
				host.setIpaddress("");
			}
		}
	
		int appStatus = 0;
		appStatus = registrationNodeInfo.getNodeInfo().isARCserveBackInstalled()?ApplicationUtil.setArcserveInstalled(appStatus):appStatus;
		appStatus = registrationNodeInfo.getNodeInfo().isD2DInstalled()?ApplicationUtil.setD2DInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isSQLServerInstalled()?ApplicationUtil.setSQLInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isExchangeInstalled()?ApplicationUtil.setExchangeInstalled(appStatus):appStatus;
	    appStatus = registrationNodeInfo.getNodeInfo().isD2DODInstalled()?ApplicationUtil.setD2DODInstalled(appStatus):appStatus;
		appStatus = registrationNodeInfo.getNodeInfo().isRPSInstalled()?ApplicationUtil.setRPSInstalled(appStatus):appStatus;
		appStatus = registrationNodeInfo.getNodeInfo().isConsoleInstalled()?ApplicationUtil.setConsoleInstalled(appStatus):appStatus;
	    host.setAppStatus(appStatus);

	    if (updateRhostType) {
		    int hostType = 0;
		    hostType = registrationNodeInfo.isPhysicsMachine()?HostTypeUtil.setPhysicsMachine(hostType):hostType;
		    hostType = registrationNodeInfo.isHyperVVM()?HostTypeUtil.setHyperVVirtualsMachine(hostType):hostType;
		    hostType = registrationNodeInfo.isHyperVClusterVM()?HostTypeUtil.setHyperVClusterVirtualsMachine(hostType):hostType;
		    hostType = registrationNodeInfo.isVMWareVM()?HostTypeUtil.setVMWareVirtualsMachine(hostType):hostType;
		    hostType = registrationNodeInfo.isVCMMonitor()?HostTypeUtil.setVCMMonitor( hostType ):hostType;
		    hostType = registrationNodeInfo.isVCMMonitee()?HostTypeUtil.setVCMMonitee( hostType ):hostType;
		    host.setRhostType(hostType);
	    }
	    host.setLastupdated(new Date());
	    host.setOsdesc(registrationNodeInfo.getNodeInfo().getOsDescription()==null?"":registrationNodeInfo.getNodeInfo().getOsDescription());
	    host.setOstype(registrationNodeInfo.getNodeInfo().getOsType()==null?"":registrationNodeInfo.getNodeInfo().getOsType());
	    return host;
	}

	@Override
	public void importNodes(NodeRegistrationInfo[] nodes, ImportNodeType type) throws EdgeServiceFault {
		
		String jobID = java.util.UUID.randomUUID().toString();
		try {
			ImportNodesJob job = null; 
			if(type == ImportNodeType.File){
				job = new ImportNodeFromFileJob();
			}else if (type == ImportNodeType.AutoDiscovery_VMWare) {
				job = new ImportVMFromDiscoveryJob();
			}else{
				job = new ImportNodesJob();
			}
			job.setId(jobID);
			job.schedule(job.createJobDetail(nodes, type, this));
			
		} catch (Throwable e) {
			logger.error("importNodes()", e);
		}
	}

	private void importLinuxNodes(List<NodeRegistrationInfo> nodes){
		String jobID = java.util.UUID.randomUUID().toString();
		try {
			ImportLinuxNodesJob job = new ImportLinuxNodesJob(); 
			job.setId(jobID);
			job.schedule(job.createJobDetail(nodes, this));
			
		} catch (Throwable e) {
			logger.error("importNodes()", e);
		}
	}
	
	@Override
	public void importVMs(DiscoveryESXOption esxOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault {
		
		String jobID = java.util.UUID.randomUUID().toString();
		
		try {
			ImportVMsJob job = new ImportVMsJob();
			job.setId(jobID);
			job.schedule(job.createJobDetail(esxOption, vms, type, addEsxToADList, this));
			
		} catch (Throwable e) {
			logger.error("importVMs()", e);
		}
	}
	
	public void addActivityLogForImportNodes(String nodeName, Severity severity, ImportNodeType type, String message){
		ActivityLog log = new ActivityLog();
		log.setNodeName(nodeName != null ? nodeName : "");
		log.setMessage(message);
		log.setSeverity(severity);
		log.setTime(new Date());
		if (type == ImportNodeType.AutoDiscovery_AD || type == ImportNodeType.AutoDiscovery_VMWare){
			log.setModule(Module.ImportNodesFromAD);
			log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_AutoDiscovery_Log", log.getMessage()));
		}else if (type == ImportNodeType.HyperV || type == ImportNodeType.WMWare){
			log.setModule(Module.ImportNodesFromHypervisor);
			log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_HyperVisor_Log", log.getMessage()));
		}else if (type == ImportNodeType.File){
			log.setModule(Module.ImportNodesFromFile);
			log.setMessage(EdgeCMWebServiceMessages.getMessage("importNodes_File_Log", log.getMessage()));
		}

		try {
			logService.addLog(log);
		} catch (EdgeServiceFault e) {
			logger.error("Error occurs during add activity log", e);
		}
	}

	public void addActivityLogForImportNodes(Severity severity, ImportNodeType type, String message){
		addActivityLogForImportNodes(null, severity, type, message);
	}


	@SuppressWarnings("static-access")
	@Override
	public NodeDetail getNodeDetailInformation(int hostID) throws EdgeServiceFault {
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		hostMgrDao.as_edge_host_list(hostID, 1, hosts);

		if (hosts.size()>0){
			NodeDetail node = (NodeDetail)this.convertDaoNode2ContractNode(hosts.get(0));

			List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
			connectionInfoDao.as_edge_connect_info_list(hostID, connInfoLst);
			if (connInfoLst.size()>0)
				node.setD2dConnectInfo(convertD2DConnectionInfo(connInfoLst.get(0)));

			ArrayList<EdgeArcserveConnectInfo> infos = new ArrayList<EdgeArcserveConnectInfo>();
			connectionInfoDao.as_edge_arcserve_connect_info_list(hostID, infos);
			if (infos.size() > 0) {
				node.setArcserveConnectInfo(convertARCserveConnectionInfo(infos.get(0)));
				// BUG 753540
				// add start
				// set the value of Class Node's member: asbuWebServiceConnectInfo
				node.setArcserveProtocol(infos.get(0).getProtocol());
				node.setArcservePort(String.valueOf(infos.get(0).getPort()));
				// add end
			}

			GatewayEntity gateway = gatewayService.getGatewayByHostId(hostID);
			node.setGatewayId(gateway.getId());
			
			return node;
		}
		return null;
	}

	@Override
	public void updateEdgeConnectionCredential(int hostID, String userName, @NotPrintAttribute String password) {
		connectionInfoDao.as_edge_connect_info_update_credential(hostID, userName, password);
	}
	
	private ArcserveConnectInfo convertARCserveConnectionInfo(EdgeArcserveConnectInfo edgeArcserveConnectInfo) {
		ArcserveConnectInfo result = new ArcserveConnectInfo();
		result.setCauser(edgeArcserveConnectInfo.getCauser());
		result.setCapasswd(edgeArcserveConnectInfo.getCapasswd());
		result.setGdb_branchid(edgeArcserveConnectInfo.getGdb_branchid());
		result.setPort(edgeArcserveConnectInfo.getPort());
		result.setVersion(edgeArcserveConnectInfo.getVersion());

		if (edgeArcserveConnectInfo.getManaged() == NodeManagedStatus.Managed.ordinal())
			result.setManaged(NodeManagedStatus.Managed);
		else if (edgeArcserveConnectInfo.getManaged() == NodeManagedStatus.Unmanaged.ordinal())
			result.setManaged(NodeManagedStatus.Unmanaged);
		else
			result.setManaged(NodeManagedStatus.Unknown);

		if (edgeArcserveConnectInfo.getType() == ABFuncServerType.BRANCH_PRIMARY.ordinal())
			result.setType(ABFuncServerType.BRANCH_PRIMARY);
		else if (edgeArcserveConnectInfo.getType() == ABFuncServerType.GDB_PRIMARY_SERVER.ordinal())
			result.setType(ABFuncServerType.GDB_PRIMARY_SERVER);
		else if (edgeArcserveConnectInfo.getType() == ABFuncServerType.NORNAML_SERVER.ordinal())
			result.setType(ABFuncServerType.NORNAML_SERVER);
		else if (edgeArcserveConnectInfo.getType() == ABFuncServerType.STANDALONE_SERVER.ordinal())
			result.setType(ABFuncServerType.STANDALONE_SERVER);
		else if (edgeArcserveConnectInfo.getType() == ABFuncServerType.ARCSERVE_MEMBER.ordinal())
			result.setType(ABFuncServerType.ARCSERVE_MEMBER);
		else
			result.setType(ABFuncServerType.UN_KNOWN);

		if (edgeArcserveConnectInfo.getAuthmode() == ABFuncAuthMode.AR_CSERVE.ordinal())
			result.setAuthmode(ABFuncAuthMode.AR_CSERVE);
		else if (edgeArcserveConnectInfo.getAuthmode() == ABFuncAuthMode.WINDOWS.ordinal())
			result.setAuthmode(ABFuncAuthMode.WINDOWS);
		else if (edgeArcserveConnectInfo.getAuthmode() == ABFuncAuthMode.CURRENT_PROCESS.ordinal())
			result.setAuthmode(ABFuncAuthMode.CURRENT_PROCESS);

		if (edgeArcserveConnectInfo.getProtocol() == Protocol.Http.ordinal())
			result.setProtocol(Protocol.Http);
		else if (edgeArcserveConnectInfo.getProtocol() == Protocol.Https.ordinal())
			result.setProtocol(Protocol.Https);
		else
			result.setProtocol(Protocol.UnKnown);

		return result;
	}
	private D2DConnectInfo convertD2DConnectionInfo(EdgeConnectInfo edgeConnectInfo) {
		D2DConnectInfo result = new D2DConnectInfo();
		result.setUsername(edgeConnectInfo.getUsername());
		result.setPassword(edgeConnectInfo.getPassword());
		result.setUuid(edgeConnectInfo.getUuid());
		result.setType(edgeConnectInfo.getType());
		result.setPort(edgeConnectInfo.getPort());
		result.setMajorversion(edgeConnectInfo.getMajorversion());
		result.setMinorversion(edgeConnectInfo.getMinorversion());
		result.setUpdateversionnumber(edgeConnectInfo.getUpdateversionnumber());
		result.setBuildnumber(edgeConnectInfo.getBuildnumber());

		if (edgeConnectInfo.getManaged() == NodeManagedStatus.Managed.ordinal())
			result.setManaged(NodeManagedStatus.Managed);
		else if (edgeConnectInfo.getManaged() == NodeManagedStatus.Unmanaged.ordinal())
			result.setManaged(NodeManagedStatus.Unmanaged);
		else
			result.setManaged(NodeManagedStatus.Unknown);

		if (edgeConnectInfo.getProtocol() == Protocol.Http.ordinal())
			result.setProtocol(Protocol.Http);
		else if (edgeConnectInfo.getProtocol() == Protocol.Https.ordinal())
			result.setProtocol(Protocol.Https);
		else
			result.setProtocol(Protocol.UnKnown);

		return result;
	}
/*	@Override
	public String[] updateNode(NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		this.validateAdminAccount(registrationNodeInfo.getNodeName(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());

		NodeDetail nodeDetail = getNodeDetailInformation(registrationNodeInfo.getId());
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		EdgeHost node = populateEdgeHost(registrationNodeInfo);
		node.setRhostid(registrationNodeInfo.getId());

		//check ARCserve caroot username/password
		if (registrationNodeInfo.getNodeInfo().isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup()){
			ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[registrationNodeInfo.getAbAuthMode()];
			serviceImpl.ConnectARCserve(node.getRhostname(), registrationNodeInfo.getCarootUsername(),
					registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(), arcserveAuthMode, registrationNodeInfo.getArcservePort(), registrationNodeInfo.getArcserveProtocol());
		}

		if (nodeDetail.getD2dConnectInfo()==null){
			nodeDetail.setD2dConnectInfo(new D2DConnectInfo());
			nodeDetail.getD2dConnectInfo().setManaged(NodeManagedStatus.Unknown);
		}

		//insert Node
		int[] output = new int[1];
		hostMgrDao.as_edge_host_update(node.getRhostid(), 0, 0, 0, 0, node.getLastupdated(), node.getRhostname(),
					"", node.getIpaddress(), node.getOsdesc(), "", "",	0, 0, 0, 0, 0, 0, 1, node.getAppStatus(), "", output);
		connectionInfoDao.as_edge_connect_info_update(node.getRhostid(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(),
				nodeInfo.getD2DUUID(), nodeInfo.getD2DProtocol().ordinal(), nodeInfo.getD2DPortNumber(), 0,
				nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getD2DBuildNumber(), NodeManagedStatus.Unknown.ordinal());
		if (nodeInfo.isARCserveBackInstalled())
			connectionInfoDao.as_edge_arcserve_connect_info_update(node.getRhostid(), registrationNodeInfo.getCarootUsername(), registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(),
					registrationNodeInfo.getAbAuthMode(), nodeInfo.getARCserveProtocol().ordinal(), nodeInfo.getARCservePortNumber(), nodeInfo.getARCserveType().ordinal(), nodeInfo.getARCserveVersion(),NodeManagedStatus.Unknown.ordinal());

		connectionInfoDao.as_edge_connect_update_managedStatus(registrationNodeInfo.getId(), nodeDetail.getD2dConnectInfo().getManaged().ordinal());
		if (nodeDetail.getArcserveConnectInfo()!=null)
			connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(registrationNodeInfo.getId(), nodeDetail.getArcserveConnectInfo().getManaged().ordinal());

		String[] errorCodes = new String[2];
		if (nodeDetail.getD2dConnectInfo().getManaged()!=NodeManagedStatus.Managed && registrationNodeInfo.isRegisterD2D())
			errorCodes[0] = tryMarkD2DAsManaged(registrationNodeInfo, node, false);
		if ((nodeDetail.getArcserveConnectInfo()==null || nodeDetail.getArcserveConnectInfo().getManaged()!=NodeManagedStatus.Managed) && registrationNodeInfo.isRegisterARCserveBackup())
			errorCodes[1] = tryMarkARCserveAsManaged(registrationNodeInfo, node, false);

		//remove D2D
		if (nodeDetail.getD2dConnectInfo().getManaged()==NodeManagedStatus.Managed && !registrationNodeInfo.isRegisterD2D()){
			try {
				serviceImpl.RemoveRegInfoFromD2D(registrationNodeInfo.getId(), true);
			} catch (Exception e){
				logger.error("unregister D2D", e);
			} finally {
				connectionInfoDao.as_edge_connect_update_managedStatus(registrationNodeInfo.getId(), NodeManagedStatus.Unmanaged.ordinal());
			}
		}

		//remove ARCserve
		if ((nodeDetail.getArcserveConnectInfo()!=null && nodeDetail.getArcserveConnectInfo().getManaged()==NodeManagedStatus.Managed) && !registrationNodeInfo.isRegisterARCserveBackup()){
			unregisterARCserveBackup(nodeDetail.getId(), node, nodeDetail);
			if (nodeInfo.isARCserveBackInstalled())
				connectionInfoDao.as_edge_arcserve_connect_info_update_gdb(node.getRhostid(), registrationNodeInfo.getCarootUsername(), registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(),
						registrationNodeInfo.getAbAuthMode(), nodeInfo.getARCserveProtocol().ordinal(), nodeInfo.getARCservePortNumber(), nodeInfo.getARCserveType().ordinal(), nodeInfo.getARCserveVersion(),0, NodeManagedStatus.Unknown.ordinal());
		}

		return errorCodes;
	}
*/
	/**
	 * Have printed activity logs in this update node API
	 * If you don't need update node activity log, please invoke other update node API
	 */
	@Override
	public String[] updateNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo) throws EdgeServiceFault {
		String message = "";
		try{
			RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
			String d2dUUID = nodeInfo.getD2DUUID();
			if(d2dUUID == null || d2dUUID.equals("")){
				EdgeConnectInfo connectInfo = getEdgeConnectInfoById(registrationNodeInfo.getId());
				nodeInfo.setD2DUUID(connectInfo.getUuid());
			}
			String[] errors = updateNode(failedReadRemoteRegistry, registrationNodeInfo, false, false);
			//successful
			if(errors[0]==null && errors[1]==null){
				message = EdgeCMWebServiceMessages.getMessage("updateNodeSuccessful", registrationNodeInfo.getNodeName());
				String updateLog = EdgeCMWebServiceMessages.getMessage("updateNode_Log",message);
				generateLogForRegInfo(Severity.Information, registrationNodeInfo, 
						updateLog, Module.UpdateNode);
			}
			
			//warnning
			if (errors[0] != null) {
				if (EdgeServiceErrorCode.Node_D2D_Reg_Duplicate.equals(errors[0]) ) {
					message = EdgeCMWebServiceMessages.getMessage("failedToManageD2DByAnotherServe", 
							registrationNodeInfo.getNodeName(),registrationNodeInfo.getNodeInfo().getHostEdgeServer());
				}else {
					message = MessageReader.getErrorMessage(errors[0]);
					if( message.equals( EdgeCMWebServiceMessages.getResource( "unknownError" ) ) ) {
						message = EdgeCMWebServiceMessages.getMessage("failedToManageD2D", registrationNodeInfo.getNodeName());
						logger.error(" unable to manage d2d node "+registrationNodeInfo.getNodeName() +" , error code is " + errors[0] );
					}
				}
				String updateLog = EdgeCMWebServiceMessages.getMessage("updateNode_Log",message);
				generateLogForRegInfo(Severity.Warning, registrationNodeInfo, updateLog, Module.UpdateNode);
			}
			
			if(errors[1] != null){
				if (EdgeServiceErrorCode.ABFunc_HaveManagedByAnotherServer == errors[1]) {
					message = EdgeCMWebServiceMessages.getMessage("failedToManageARCServerBackupByAnotherServe", 
							registrationNodeInfo.getNodeName(), registrationNodeInfo.getNodeInfo().getHostEdgeServer());
				} else {
					message = MessageReader.getErrorMessage(errors[1]);
					if( message.equals( EdgeCMWebServiceMessages.getResource( "unknownError" ) ) ) {
						message = EdgeCMWebServiceMessages.getMessage("failedToManageARCServerBackup", registrationNodeInfo.getNodeName() ) ;
						logger.error(" Unable to manage CA ARCserve Backup node " + registrationNodeInfo.getNodeName() +" , error code is " + errors[1] );
					}
				}
				String updateLog = EdgeCMWebServiceMessages.getMessage("updateNode_Log",message);
				generateLogForRegInfo(Severity.Warning, registrationNodeInfo, updateLog , Module.UpdateNode);
			}
			
			return errors;
		}catch(Exception exception){
			//Error
			NodeExceptionUtil.generateActivityLogByExceptionForRegInfo(Module.UpdateNode,registrationNodeInfo,"updateNode_Log", exception);
			throw exception;
		}
	}
	
	public String[] updateNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo,
			boolean overwrite, boolean updateMultipleNode) throws EdgeServiceFault {
		return updateNode(failedReadRemoteRegistry, registrationNodeInfo, overwrite, updateMultipleNode, false);//because have PFC check, so not to verify vm when update node
	}
	
	public String[] updateNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo, boolean overwrite, boolean updateMultipleNode, boolean verify) throws EdgeServiceFault {
		return updateNode(failedReadRemoteRegistry, registrationNodeInfo, overwrite, updateMultipleNode, verify, PolicyDeployReasons.PolicyContentChanged, true);
	}
	
	public String[] updateNode(boolean failedReadRemoteRegistry, NodeRegistrationInfo registrationNodeInfo, boolean overwrite, boolean updateMultipleNode, boolean verify, int planDeployReason, boolean deployPlan) throws EdgeServiceFault {
		GatewayEntity gateway = this.gatewayService.getGatewayByHostId( registrationNodeInfo.getId() );
		registrationNodeInfo.setGatewayId( gateway.getId() );
		
		//Defect 761117
		//If UI update node, admin account have been validated by native code or web service, so this code have no use now
		//this.validateAdminAccount(registrationNodeInfo.getGatewayId(), registrationNodeInfo.getNodeName(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());

		logger.info("update node input parameters: failedReadRemoteRegistry=" + failedReadRemoteRegistry);
		logger.info("update node input parameters: isRegisterD2D=" + registrationNodeInfo.isRegisterD2D());
		logger.info("update node input parameters: isD2DInstalled=" + (registrationNodeInfo.getNodeInfo() == null ? "" : registrationNodeInfo.getNodeInfo().isD2DInstalled()));
		
		registrationNodeInfo.setNodeName( normalizeHostName( registrationNodeInfo.getNodeName() ) );
		registrationNodeInfo.setUsername( trim( registrationNodeInfo.getUsername() ) );
		
		NodeDetail nodeDetailFromDB = getNodeDetailInformation(registrationNodeInfo.getId());
		retrieveNodePolicyDetails(nodeDetailFromDB);
		if (registrationNodeInfo.getNodeInfo() == null)
			registrationNodeInfo.setNodeInfo( new RemoteNodeInfo() );
		RemoteNodeInfo nodeInfo = registrationNodeInfo.getNodeInfo();
		
		boolean hasHBBUPlan = Utils.hasBit(nodeDetailFromDB.getPolicyContentFlag(), PlanTaskType.WindowsVMBackup);

		if(failedReadRemoteRegistry)
		{
			if (registrationNodeInfo.isRegisterD2D()) {
				RemoteNodeInfo d2dRemoteNodeInfo = null;
				Protocol protocol = registrationNodeInfo.getD2dProtocol() != null ? registrationNodeInfo.getD2dProtocol():nodeDetailFromDB.getD2dConnectInfo().getProtocol();
				String userName = !"".equals(registrationNodeInfo.getUsername()) ? registrationNodeInfo.getUsername() : nodeDetailFromDB.getD2dConnectInfo().getUsername();
				String pwd = !"".equals(registrationNodeInfo.getPassword()) ? registrationNodeInfo.getPassword() : nodeDetailFromDB.getD2dConnectInfo().getPassword();
				int port = registrationNodeInfo.getD2dPort() != 0 ? registrationNodeInfo.getD2dPort() : nodeDetailFromDB.getD2dConnectInfo().getPort();
				logger.info("Update node with params userName:" + userName + ";port:" + port + ";protocol:" + protocol + ";host:" + nodeDetailFromDB.getHostname());
				d2dRemoteNodeInfo = null;
				try {
					d2dRemoteNodeInfo = tryConnectD2D(registrationNodeInfo.getGatewayId(), (protocol == Protocol.Https)?"https":"http", nodeDetailFromDB.getHostname(), port, userName, pwd);
				}
				catch( Exception e  ){ /*ignore */ }
				
				if( d2dRemoteNodeInfo !=null ) {
					nodeInfo.setD2DMajorVersion(d2dRemoteNodeInfo.getD2DMajorVersion());
					nodeInfo.setD2DMinorVersion(d2dRemoteNodeInfo.getD2DMinorVersion());
					nodeInfo.setUpdateVersionNumber(d2dRemoteNodeInfo.getUpdateVersionNumber());
					nodeInfo.setD2DUUID(d2dRemoteNodeInfo.getD2DUUID());
					nodeInfo.setD2DBuildNumber(d2dRemoteNodeInfo.getD2DBuildNumber());
					nodeInfo.setHostEdgeServer(d2dRemoteNodeInfo.getHostEdgeServer());
					nodeInfo.setOsDescription(d2dRemoteNodeInfo.getOsDescription());
					registrationNodeInfo.getNodeInfo().setD2DInstalled(d2dRemoteNodeInfo.isD2DInstalled());
					registrationNodeInfo.getNodeInfo().setD2DODInstalled(d2dRemoteNodeInfo.isD2DODInstalled());
					registrationNodeInfo.getNodeInfo().setOsDescription(d2dRemoteNodeInfo.getOsDescription());
					registrationNodeInfo.getNodeInfo().setExchangeInstalled(d2dRemoteNodeInfo.isExchangeInstalled());
					registrationNodeInfo.getNodeInfo().setSQLServerInstalled(d2dRemoteNodeInfo.isSQLServerInstalled());
				}
				else if(d2dRemoteNodeInfo == null)//only phy machine return error; for imported vm node, continue 
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_D2D_Reg_connection_refuse, ""));
			}
			
			if (nodeInfo.isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup())
			{
				ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[registrationNodeInfo.getAbAuthMode().ordinal()];
				String strSessionNoToGetABVersion = serviceImpl.ConnectARCserve(gateway, registrationNodeInfo.getNodeName(), registrationNodeInfo.getCarootUsername(),
					registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(), arcserveAuthMode, registrationNodeInfo.getArcservePort(), registrationNodeInfo.getArcserveProtocol());
				if(strSessionNoToGetABVersion != null && !strSessionNoToGetABVersion.isEmpty())
				{
					ArrayOfstring abVersionInfoArray = serviceImpl.getArcserveVersionInfo(strSessionNoToGetABVersion);
					if(abVersionInfoArray != null)
					{
						List<String> abVersionInfoList = abVersionInfoArray.getString();
						if(abVersionInfoList != null)
						{
							nodeInfo.setARCserveVersion(abVersionInfoList.get(0)+"."+abVersionInfoList.get(1));
						}
					}
					ABFuncServerType arcserveBackupType = serviceImpl.GetServerType(strSessionNoToGetABVersion);
					nodeInfo.setARCserveType(arcserveBackupType);
					if(arcserveBackupType == ABFuncServerType.BRANCH_PRIMARY || arcserveBackupType == ABFuncServerType.ARCSERVE_MEMBER || arcserveBackupType == ABFuncServerType.UN_KNOWN)
					{
						registrationNodeInfo.setRegisterARCserveBackup(false);
					}
				}
			}
		}
		String[] errorCodes = new String[2];
		EdgeHost node = populateEdgeHost(registrationNodeInfo, false);
		if(StringUtil.isEmptyOrNull(node.getOsdesc())){
			//if remote scan cannot get the OS description, use it in database
			node.setOsdesc(nodeDetailFromDB.getOsDescription() == null ? "" : nodeDetailFromDB.getOsDescription());
		}
		if(StringUtil.isEmptyOrNull(node.getOstype())){
			//if remote scan cannot get the OS description, use it in database
			//incase remote scan cannot get os and ostype info and reuse them which is already in DB
			node.setOstype(nodeDetailFromDB.getOsType() == null ? "" : nodeDetailFromDB.getOsType());
		}
		
		node.setRhostType(nodeDetailFromDB.getRhostType());
		node.setProtectionTypeBitmap(nodeDetailFromDB.getProtectionTypeBitmap());
		
		String nodeUuid = nodeInfo.getD2DUUID();
		String authUuid = null;
		
		//if (!hasHBBUPlan) {
			if (nodeDetailFromDB.getD2dConnectInfo()==null){
				nodeDetailFromDB.setD2dConnectInfo(new D2DConnectInfo());
				nodeDetailFromDB.getD2dConnectInfo().setManaged(NodeManagedStatus.Managed);
			} 
			
//			if(nodeInfo.isD2DInstalled()) {
//				policyDao.deleteHostPolicyMap(node.getRhostid(), getPolicyTypeByApplicationType());			
//			}
			
			if (nodeInfo.isD2DInstalled()) {
				String[] outNodeUuid = new String[1];
				String[] outAuthUuid = new String[1];
				connectD2D(registrationNodeInfo, outNodeUuid, outAuthUuid);
				nodeUuid = outNodeUuid[0];
				authUuid = outAuthUuid[0];
			}
						
			if (authUuid != null) {
				//There must be existing record when this method is called
				connectionInfoDao.as_edge_connect_info_setAuthUuid(nodeUuid, authUuid);
			}
			
			if (nodeInfo.isARCserveBackInstalled() && registrationNodeInfo.isRegisterARCserveBackup())
				connectionInfoDao.as_edge_arcserve_connect_info_update(node.getRhostid(), registrationNodeInfo.getCarootUsername(), registrationNodeInfo.getCarootPassword()==null?"":registrationNodeInfo.getCarootPassword(),
						registrationNodeInfo.getAbAuthMode().ordinal(), nodeInfo.getARCserveProtocol().ordinal(), nodeInfo.getARCservePortNumber(), nodeInfo.getARCserveType().ordinal(), nodeInfo.getARCserveVersion(),NodeManagedStatus.Unknown.ordinal());

			if (nodeDetailFromDB.getArcserveConnectInfo()!=null)
				connectionInfoDao.as_edge_arcserve_connect_update_managedStatus(registrationNodeInfo.getId(), nodeDetailFromDB.getArcserveConnectInfo().getManaged().ordinal(), null);
			
			
		//}
		
		//insert Node
		int[] output = new int[1];
		
		String hostName = node.getRhostname();
		if(!StringUtil.isEmptyOrNull(hostName))
			hostName = hostName.toLowerCase();
		
//		List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
		List<String> fqdnNameList = new ArrayList<String>();
		if(gateway.getId() != null && gateway.getId().isValid()){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade(gateway.getId());
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
			} catch (Exception e) {
				logger.error("[NodeServiceImpl] updateNode() get fqdn name failed.",e);
			}
		}
		String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
		
		//check node uuid
		if(StringUtil.isEmptyOrNull(nodeUuid)){
			nodeUuid = nodeDetailFromDB.getD2DUUID();
			if(StringUtil.isEmptyOrNull(nodeUuid)){
				nodeUuid = UUID.randomUUID().toString();
				nodeInfo.setD2DUUID(nodeUuid);
				logger.info("[NodeServiceImpl] update node: the node uuid of "+hostName+ " is empty or null. so generate one for it. the generated uuid is: "+nodeUuid);
			}
		}
		
		hostMgrDao.as_edge_host_update(node.getRhostid(), node.getLastupdated(), hostName,node.getNodeDescription(),
				node.getIpaddress(), node.getOsdesc(),node.getOstype(),1, node.getAppStatus(), 
				"",node.getRhostType(), node.getProtectionTypeBitmap(), fqdnNames, output);
		connectionInfoDao.as_edge_connect_info_update(node.getRhostid(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword(),
				nodeUuid, nodeInfo.getD2DProtocol().ordinal(), nodeInfo.getD2DPortNumber(), 0,
				nodeInfo.getD2DMajorVersion(), nodeInfo.getD2DMinorVersion(), nodeInfo.getUpdateVersionNumber(), nodeInfo.getD2DBuildNumber(), NodeManagedStatus.Managed.ordinal());
			
		
		boolean isProtectedByHBBU = false;
		if (!nodeDetailFromDB.isPhysicalMachine() 
			&& (nodeDetailFromDB.isVMwareMachine() ||nodeDetailFromDB.isHyperVMachine()) 
			&& (!nodeInfo.isD2DInstalled() || hasHBBUPlan)){
			
			isProtectedByHBBU = true;
			
		}
		
		boolean isProxy = isVsphereProxy(node.getRhostid());
		
		if (registrationNodeInfo.isRegisterD2D() && (!isProtectedByHBBU || isProxy))
			errorCodes[0] = tryMarkD2DAsManaged(registrationNodeInfo, node, overwrite, true);
		if (registrationNodeInfo.isRegisterARCserveBackup() && (!isProtectedByHBBU || isProxy))
			errorCodes[1] = tryMarkARCserveAsManaged(registrationNodeInfo, node, overwrite);

		
		if ((nodeDetailFromDB.isVMwareMachine() || nodeDetailFromDB.isHyperVMachine())) {
			try{				
				List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(1);
				policyDao.getHostPolicyMap(node.getRhostid(), PolicyTypes.Unified, mapList);
				
				if (mapList.size()>0){
					int policyId = mapList.get(0).getPolicyId();
					if (hasHBBUPlan) {
						serviceImpl.redeployPolicyToNodes(Arrays.asList(node.getRhostid()), PolicyTypes.Unified, policyId);
						//serviceImpl.redeployPolicy2RightNodes(PolicyTypes.VMBackup, mapList.get(0).getPolicyId(), node.getRhostid());
					}
				}
			}catch(Exception e){
				logger.error(e);
			}
			
			if (verify) {
				// verify VM
				try {
					this.verifyVMs(new int[] { node.getRhostid() });
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		
		if (EdgeApplicationType.CentralManagement == EdgeWebServiceContext.getApplicationType()){
			try{
				hostMgrDao.as_edge_deploy_target_update_credential(registrationNodeInfo.getNodeName(), registrationNodeInfo.getUsername(), registrationNodeInfo.getPassword());
			}catch(Exception e){
				logger.error(e);
			}
				
		}
		
		if(deployPlan){
			//Try to redeploy plan
			PolicyManagementServiceImpl policyManagementServiceImpl =
					PolicyManagementServiceImpl.getInstance();
			logger.info("Do deploy agent plan for node: "+registrationNodeInfo.getNodeName());
			policyManagementServiceImpl.deployPlanByNodeId(node.getRhostid(),planDeployReason);
			
			//try to redeploy plan when this node is HBBU proxy
			logger.info("Do deploy hbbu plan by proxy: "+registrationNodeInfo.getNodeName());
			policyManagementServiceImpl.deployHBBUPlanByProxyNodeId(node.getRhostid());
		}
		
		//update agent credential
		String[] policyID = new String[1];
		policyDao.as_edge_policy_by_hostID(node.getRhostid(), policyID);
		if(policyID[0] == null && nodeInfo.isD2DInstalled()){  //the node has no plan and connection is not null
					
			int majorNumber = parseInt(registrationNodeInfo.getNodeInfo().getD2DMajorVersion(), 0);
			int minorNumber = parseInt(registrationNodeInfo.getNodeInfo().getD2DMinorVersion(), 0);
			int updateVersionNumber = parseInt(registrationNodeInfo.getNodeInfo().getUpdateVersionNumber(), 0);
							
			// if the node version is higher then 5.0.update 4
			if(!((majorNumber<5) ||(majorNumber==5 && minorNumber==0 && updateVersionNumber<4))){
				try(D2DConnection connection = connectionFactory.createD2DConnection(registrationNodeInfo.getId())) {
					connection.connect();	
					logger.info("The node credential is updated. NodeName:"+ registrationNodeInfo.getNodeName());
					connection.getService().updateAdminAccount(registrationNodeInfo.getUsername(), BackupService.getInstance().getNativeFacade().encrypt(registrationNodeInfo.getPassword()));
				} catch(Exception e){
					logger.error(e);
				}
			}
		}
		
		return errorCodes;
	
	}
	
	private int parseInt(String strValue, int defaultValue){
		if (strValue == null){
			return defaultValue;
		}
		try {
			int value = Integer.parseInt(strValue);
			return value;
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
			return defaultValue;
		}
	}

	@Override
	public List<Node> getNodesByGDBId(int GDBId) throws EdgeServiceFault {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		// changed by lijwe02 on 2010-10-21 for 19752789 D2D CAN'T MANAGED UPDATE BRANCH and 19753156 WRONG VERSION FOR BRANCH SERVE
		// hostMgrDao.as_edge_host_list_by_gdb_id(GDBId, hosts);
		hostMgrDao.as_edge_host_list_display_info_by_gdb_id(GDBId, hosts);
		// end of changing by lijwe02

		List<Node> nodes = new LinkedList<Node>();
		//for (EdgeHost host : hosts) {
		addEdgeHostToNodeList(hosts, nodes, false, false, false, false);
			//nodes.add(convertDaoNode2ContractNode(host));
		//}

		// added by lijwe02 on 2010-10-21
		for (Node node:nodes){
			node.setGDBId(GDBId); // can delete
		}
		// end of adding by lijwe02

		return nodes;
	}

	@Override
	public Node[] getNodesByGroupAndType(int groupID, int[] types)
			throws EdgeServiceFault {
		List<Node> nodes = new LinkedList<Node>();
		if (types != null && types.length > 0) {
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			boolean isExch = groupID == NodeGroup.EXCHANGE;
			boolean isSQL = groupID == NodeGroup.SQLSERVER;
			boolean isD2D = (groupID == NodeGroup.D2D);
			boolean isD2DOD = (groupID == NodeGroup.D2DOD);
			int appStatus = 0;
			if (isExch) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH
						.getValue();
			} else if (isSQL) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL
						.getValue();
			}
			for (int i = 0; i < types.length; i++) {
				hosts.clear();
				hostMgrDao.as_edge_host_list_by_group_type_appstatus_prodType(
						groupID, types[i], appStatus, hosts);
				addEdgeHostToNodeList(hosts, nodes, isExch, isSQL, isD2D, isD2DOD);
			}
		}

		return nodes.toArray(new Node[0]);
	}

	@Override
	public Node[] getNodesESXByGroupAndType(int groupID, int[] types, int grouptype)
			throws EdgeServiceFault {
		List<Node> nodes = new LinkedList<Node>();
		if (types != null && types.length > 0) {
			List<EdgeHost> hosts = new ArrayList<EdgeHost>();
			boolean isExch = groupID == NodeGroup.EXCHANGE;
			boolean isSQL = groupID == NodeGroup.SQLSERVER;
			boolean isD2D = (groupID == NodeGroup.D2D);
			boolean isD2DOD = (groupID == NodeGroup.D2DOD);
			int appStatus = 0;
			if (isExch) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH
						.getValue();
			} else if (isSQL) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL
						.getValue();
			} else if (isD2D) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue();
			} else if (isD2DOD) {
				appStatus = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue();
			}
			for (int i = 0; i < types.length; i++) {
				hosts.clear();
				hostMgrDao.as_edge_host_esx_list_by_group_type_appstatus_prodType(
						groupID, types[i], appStatus, grouptype, 1, hosts);
				addEdgeHostToNodeList(hosts, nodes, isExch, isSQL, isD2D, isD2DOD);
				populateVMInformation(nodes);
			}
		}

		return nodes.toArray(new Node[0]);
	}
	
	public void populateVMInformation(List<Node> nodes) throws EdgeServiceFault {
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager){
			for (Node node : nodes){
				vmList.clear();
				esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
				if (vmList.size()>0){
					node.setVmName(vmList.get(0).getVmName());
					node.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
					node.setHyperVisor(getHyperVisorInfo(vmList.get(0)));
					// BUG 762401
					// 2016/1/6  ADD
					node.setEsxName(getVcenterInfo(vmList.get(0)));
					// END
					VMVerifyStatus pfcStatus = esxService.getVMVerifyStatus(node.getId());
					node.setVerifyStatus(pfcStatus.getStatus());
				}
			}
		}
		else 
		{
			for (Node node : nodes){
				if(node.isVMImportFromVSphere() || node.isVMwareMachine()){// for VCM and CM 
					vmList.clear();
					esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
					if (vmList.size()>0){
						node.setVmName(vmList.get(0).getVmName());
						node.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
						node.setHyperVisor(getHyperVisorInfo(vmList.get(0)));
						// BUG 762401
						// 2016/1/6  ADD
						node.setEsxName(getVcenterInfo(vmList.get(0)));
						// END
					}					
				}
				
				if (node.isHyperVMachine()){
					List<EdgeHyperVHostMapInfo> hypervHostMap = new LinkedList<EdgeHyperVHostMapInfo>();
					this.hyperVDao.as_edge_hyperv_host_map_getById(node.getId(), hypervHostMap);
					if (hypervHostMap.size()>0){
						node.setVmName(hypervHostMap.get(0).getVmName());
						node.setVmInstanceUUID(hypervHostMap.get(0).getVmInstanceUuid());
						node.setHyperVisor(getHyperVisorInfo(hypervHostMap.get(0)));
					}
				}
			}
		}
	}
	
	public String getHyperVisorInfo(EdgeEsxVmInfo vmInfo){
		
		// get connect info
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		esxDao.as_edge_esx_getHypervisorByHostId(vmInfo.getHostId(), esxList);
		String result = esxList.size() > 0?esxList.get(0).getHostname():"";
		
		// if this is vCenter type , we need to add the ESX host info		
		if(!esxList.isEmpty() && (esxList.get(0).getServertype() ==  2)){
			result = EdgeCMWebServiceMessages.getMessage("importNodes_HyperVisorInfo", 
					result, vmInfo.getEsxHost()); // vCenterName(ESXName)
		}
		
		return result;
	}
	
   public String getVcenterInfo(EdgeEsxVmInfo vmInfo){
		
		// get connect info
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		esxDao.as_edge_esx_getHypervisorByHostId(vmInfo.getHostId(), esxList);
		String result = esxList.size() > 0?esxList.get(0).getHostname():"";
		return result;
	}
	
	private String getHyperVisorInfo(EdgeHyperVHostMapInfo hypervHostMap){
		
		// get connect info
		List<EdgeHyperV> hypervList = new LinkedList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getById(hypervHostMap.getHyperVId(), hypervList);
		String result = hypervList.size() > 0?hypervList.get(0).getHostname():"";
		return result;
	}
	
	@Override
	public VSphereProxyInfo getUUIDforD2DLogin(int hostid) throws EdgeServiceFault
	{
		IEdgeConnectInfoDao edao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
		edao.as_edge_connect_info_list(hostid, infos);
		EdgeConnectInfo daoProxyInfo = new EdgeConnectInfo();
    	if(infos!=null&&infos.size()>0){
    		daoProxyInfo = infos.get(0);
    		VSphereProxyInfo proxyInfo = new VSphereProxyInfo();
			proxyInfo.setVSphereProxyName( daoProxyInfo.getRhostname() );
			proxyInfo.setVSphereProxyUsername( daoProxyInfo.getUsername() );
			proxyInfo.setVSphereProxyPassword( daoProxyInfo.getPassword());
			proxyInfo.setVSphereProxyProtocol(Protocol.parse(daoProxyInfo.getProtocol()));
			proxyInfo.setVSphereProxyPort( daoProxyInfo.getPort() );
			proxyInfo.setVSphereProxyUuid( daoProxyInfo.getUuid() );

			return proxyInfo;
    	}else{
    		return null;
    	}
	}
	
	@Override
	public VSphereProxyInfo getRps4RemoteNode(int nodeId) throws EdgeServiceFault {
		List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
		policyDao.getHostPolicyMap(nodeId, PolicyTypes.Unified, maps);
		if (maps.isEmpty()) {
			return null;
		}
		
		UnifiedPolicy policy = PolicyManagementServiceImpl.getInstance().getUnifiedPolicyById(maps.get(0).getPolicyId());
		if (policy.getMspServerReplicationSettings() == null) {
			return null;
		}
		
		VSphereProxyInfo info = new VSphereProxyInfo();
		
		info.setVmHostID(policy.getMspServerReplicationSettings().getHostId());
		info.setVSphereProxyName(policy.getMspServerReplicationSettings().getHostName());
		info.setVSphereProxyProtocol(policy.getMspServerReplicationSettings().getProtocol() == 0 ? Protocol.Http : Protocol.Https);
		info.setVSphereProxyPort(policy.getMspServerReplicationSettings().getPort());
		info.setVSphereProxyUsername(policy.getMspServerReplicationSettings().getUserName());
		info.setVSphereProxyPassword(policy.getMspServerReplicationSettings().getPassword());
		
		if (policy.getMspServerReplicationSettings().getUuid() != null) {
			IRpsConnectionInfoDao rpsConnectionInfoDao = DaoFactory.getDao(IRpsConnectionInfoDao.class);
			List<AuthUuidWrapper> wrappers = new ArrayList<AuthUuidWrapper>();
			rpsConnectionInfoDao.as_edge_rps_connection_info_getAuthUuid(policy.getMspServerReplicationSettings().getUuid(), wrappers);
			if (!wrappers.isEmpty()) {
				info.setVSphereProxyUuid(wrappers.get(0).getAuthUuid());
			}
		}
		
		return info;
	}
	
	/*
	 * Use to replace function addEdgeHostToNodeList
	 * Because original function is too ugly
	 */
	private void addHostToNodeListWithFullInfo(List<EdgeHost> hosts,
			List<Node> nodes) {
		if(hosts == null || hosts.isEmpty()) 
			return;
		
		for(EdgeHost host : hosts){
			
			Node node = convertDaoNode2ContractNodeWithFullInfo(host);
			nodes.add(node);
			//for security don't return node with password
			node.setPassword(host.getPassword());
			
			if(node != null){
				try {
					node.setPolicyName(host.getPolicyName());
					node.setPolicyContentFlag(host.getPolicyContentFlag());
					node.setPolicyDeployStatus(host.getDeploystatus());
                    node.setPolicyDeployReason(host.getDeployReason()); 
                    node.setPolicyType(host.getPolicytype());
					List<EdgePolicyDeployWarningErrorMessage> warningErrorList = new ArrayList<EdgePolicyDeployWarningErrorMessage>();
					policyDao.getPolicyDeployWarningErrorMessage(host.getRhostid(), getPolicyTypeByApplicationType(), warningErrorList);
					if(warningErrorList != null && warningErrorList.size() > 0)
					{
						node.setWarning(warningErrorList.get(0).getWarning());
						node.setError(warningErrorList.get(0).getError());
					} 
					if (host.getDeploystatus() == PolicyDeployStatus.CreateRPSPolicy_Failed
							|| host.getDeploystatus() == PolicyDeployStatus.CreateASBUPolicy_Failed) {
						List<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
						IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
						edgePolicyDao.as_edge_plan_getPlanList(policyList);												
						for (PolicyInfo info : policyList) {
							if (info.getPolicyId() == host.getPolicyId()) {
								node.setError(info.getDeployErrorMessage());
							}
						}
					}
					node.setLastSuccessfulPolicyDeploy(host.getLastsuccdeploy());
					node.setPolicyIDForEsx(host.getPolicyId());
//					if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.VirtualConversionManager) {
//						ParsedBackupPolicy policy = serviceImpl
//								.getParsedBackupPolicy(host.getPolicyId());
//						node.setVcmSettings(policy.getVcmSettings());
					fillVCMStorageInfo( node );
					//}
					node.setInstallationType(host.getInstallationType());
					node.setRemoteDeployStatus(host.getRemoteDeployStatus());//remote deploy status
					node.setDeployTaskStatus(host.getDeployTaskStatus());//task status
					node.setRemoteDeployTime(host.getRemoteDeployTime());
				} catch (Exception e) {

				}
				
				populateNodeConverterInfo(node, host);
				
				if(EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager 
						|| node.isVMImportFromVSphere() || node.isVMwareMachine() 
						|| EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.CentralManagement){
					node.setVmName(host.getVmname());
					node.setVmInstanceUUID(host.getVmInstanceUuid());
					node.setHyperVisor(getHyperInfo(host));
					node.setVerifyStatus(host.getVerifyStatus());
					node.setVmStatus(host.getVmStatus());
					node.setVmWindowsOS(!HostTypeUtil.isVMNonWindowsOS(host.getRhostType()));
				}
				// Set enable status
				node.setEnableStatus(host.getEnableStatus());
				// Find proxy info
				List<EdgeConnectInfo> lstProxy = new ArrayList<EdgeConnectInfo>();
				hostMgrDao.as_edge_proxy_by_vmhostid(node.getId(), lstProxy);
				if (lstProxy != null && lstProxy.size() != 0) {
					Node proxyNode = new Node();
					proxyNode.setHostname(lstProxy.get(0).getRhostname());
					node.setProxyNode(proxyNode);
				}
				
			}
		}
	}
	
	private void fillVCMStorageInfo( Node node )
	{
		try
		{
			if (node == null)
				return;
			
			D2DStatusInfo d2dStatusInfo = node.getVsbSatusInfo();
			if (d2dStatusInfo == null)
				return;
			
			List<EdgeVCMStorage> daoStorages = new LinkedList<EdgeVCMStorage>();
			hostMgrDao.getVCMStorages( node.getId(), daoStorages );
			
			List<VCMStorage> vcmStorageList = new LinkedList<VCMStorage>();
			for (EdgeVCMStorage daoStorage : daoStorages)
			{
				VCMStorage storage = new VCMStorage();
				storage.setName( daoStorage.getStorageName() );
				storage.setFreeSize( daoStorage.getFreeSize() );
				storage.setColdStandySize(daoStorage.getColdStandySize());
				storage.setOtherSize(daoStorage.getOtherSize());
				storage.setTotalSize(daoStorage.getTotalSize());
				vcmStorageList.add( storage );
			}
			d2dStatusInfo.setDestinationVCMStorages( vcmStorageList.toArray( new VCMStorage[0] ) );
		}
		catch (Exception e)
		{
			logger.error( "Error getting VCM storage for node '" + node.getHostname() + "' (id: " + node.getId() + ").", e );
		}
	}
		
	private String getHyperInfo(EdgeHost host){
		String result = host.getEsxName();
		
		// if this is vCenter type , we need to add the ESX host info		
		if (host.getEsxType() == 2)
			result = EdgeCMWebServiceMessages.getMessage(
					"importNodes_HyperVisorInfo", result,
					host.getEsxHost()); // vCenterName(ESXName)
		
		return result;
	}
	/*
	 * Too many query from database
	 * should be replaced by function addHostToNodeListWithFullInfo
	 */
	private void addEdgeHostToNodeList(List<EdgeHost> hosts, List<Node> nodes,
			boolean isExch, boolean isSQL, boolean isD2D, boolean isD2DOD) {
		if(hosts == null || hosts.isEmpty()) return;

		List<EdgeSyncStatus> syncList = new ArrayList<EdgeSyncStatus>();
		List<EdgeConnectInfo> nodeStatusList = new ArrayList<EdgeConnectInfo>();
		for (EdgeHost host : hosts) {
			Node node = null;
			if (isExch) {
				if (ApplicationUtil.isExchangeInstalled(host.getAppStatus())) {
					node = convertDaoNode2ContractNode(host);
					nodes.add(node);
				} else
					continue;
			} else if (isSQL) {
				if (ApplicationUtil.isSQLInstalled(host.getAppStatus())) {
					node = convertDaoNode2ContractNode(host);
					nodes.add(node);
				} else
					continue;
			} else if (isD2D){
				if (ApplicationUtil.isD2DInstalled(host.getAppStatus())){
					node = convertDaoNode2ContractNode(host);
					nodes.add(node);
				}
			} else if (isD2DOD){
				if (ApplicationUtil.isD2DODInstalled(host.getAppStatus())){
					node = convertDaoNode2ContractNode(host);
					nodes.add(node);
				}
			}else {
				node = convertDaoNode2ContractNode(host);
				nodes.add(node);
			}

			if (node != null) {
				try {
					syncList.clear();
					syncDao.as_edge_Get_Sync_Status(
							EdgeSyncComponents.ARCserve_Backup.getValue(), host
									.getRhostid(), syncList);
					if (syncList != null && !syncList.isEmpty()) {
						node
								.setSyncStatus(convertDaoSyncStatus2Contract(syncList
										.get(0)));
					}
				} catch (Exception e) {
					logger.error("as_edge_Get_Sync_Status", e);
				}

//				try {
//					nodeStatusList.clear();
//					syncDao.as_edge_Get_ABNode_Status(host.getRhostid(),host.getRhostname(), nodeStatusList);
//					if (nodeStatusList != null && !nodeStatusList.isEmpty()) {
//						node.setBkpStatus(convertDaoNodeStatus2Contract(nodeStatusList.get(0)));
//					}
//				} catch (Exception e) {
//					logger.error("as_edge_Get_ABNode_Status", e);
//				}
//
//				try {
//					nodeStatusList.clear();
//					syncDao.as_edge_Get_D2DNode_Status(host.getRhostid(), nodeStatusList);
//					if (nodeStatusList != null && !nodeStatusList.isEmpty()) {
//						node.setBkpStatus(convertDaoNodeStatus2Contract(nodeStatusList.get(0)));
//					}
//				} catch (Exception e) {
//					logger.error("as_edge_Get_D2DNode_Status", e);
//				}


				try {
					nodeStatusList.clear();
					connectionInfoDao.as_edge_connect_info_list(host.getRhostid(), nodeStatusList);
					if (nodeStatusList != null && !nodeStatusList.isEmpty()) {
						node.setBkpStatus(convertDaoNodeStatus2Contract(nodeStatusList.get(0).getStatus()));
						node.setUsername(nodeStatusList.get(0).getUsername());
						node.setPassword(nodeStatusList.get(0).getPassword());
					}
				} catch (Exception e) {
					logger.error("as_edge_Get_ABNode_Status", e);
				}

				try{
					List<EdgeHostPolicyMap> map = new  LinkedList<EdgeHostPolicyMap>();
					if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.VirtualConversionManager)
						policyDao.getHostPolicyMap(node.getId(), PolicyTypes.VCM, map);
					else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.CentralManagement)
						policyDao.getHostPolicyMap(node.getId(), PolicyTypes.BackupAndArchiving, map);
					else if (EdgeWebServiceContext.getApplicationType() == EdgeApplicationType.vShpereManager)
						policyDao.getHostPolicyMap(node.getId(), PolicyTypes.VMBackup, map);



						if (map.size()>0){
							EdgeHostPolicyMap firstMap = map.get(0);
							ParsedBackupPolicy policy = serviceImpl.getParsedBackupPolicy(firstMap.getPolicyId());
							node.setPolicyName(policy.getGeneralInfo().getName());
							node.setPolicyDeployStatus( firstMap.getDeployStatus() );
							node.setLastSuccessfulPolicyDeploy( firstMap.getLastSuccDeploy() );
							node.setPolicyIDForEsx(firstMap.getPolicyId());
						}

				}catch(Exception e){

				}
			}
		}
	}

	private static NodeBkpStatus convertDaoNodeStatus2Contract(
			int daoStatus) {
		NodeBkpStatus bkpStatus = new NodeBkpStatus();
		bkpStatus.setD2dStatus(daoStatus);
		return bkpStatus;
	}
	private static NodeSyncStatus convertDaoSyncStatus2Contract(
			EdgeSyncStatus edgeSyncStatus) {
		NodeSyncStatus syncStatus = new NodeSyncStatus();
		syncStatus.setChangeStatus(edgeSyncStatus.getChange_status());
		syncStatus.setStatus(edgeSyncStatus.getStatus());
		return syncStatus;
	}
	
	public RemoteNodeInfo tryConnectD2D(ConnectionContext context) throws EdgeServiceFault {
		if (logger.isDebugEnabled()) {
			logger.debug(context.toString());
		}
		
		EdgeD2DRegServiceImpl regService = new EdgeD2DRegServiceImpl();
		EdgeConnectInfo d2dConnectInfo = regService.tryConnectD2D(context);

		RemoteNodeInfo nodeInfoForD2D = new RemoteNodeInfo();
		nodeInfoForD2D.setD2DBuildNumber(d2dConnectInfo.getBuildnumber());
		nodeInfoForD2D.setD2DMajorVersion(d2dConnectInfo.getMajorversion());
		nodeInfoForD2D.setD2DMinorVersion(d2dConnectInfo.getMinorversion());
		nodeInfoForD2D.setUpdateVersionNumber(d2dConnectInfo.getUpdateversionnumber());
		nodeInfoForD2D.setD2DUUID(d2dConnectInfo.getUuid());
		nodeInfoForD2D.setHostEdgeServer(d2dConnectInfo.getRhostname());
		nodeInfoForD2D.setD2DODInstalled(d2dConnectInfo.isD2DODInstalled());
		nodeInfoForD2D.setOsDescription(d2dConnectInfo.getOsName());
		nodeInfoForD2D.setD2DInstalled(d2dConnectInfo.isD2DInstalled());
		nodeInfoForD2D.setSQLServerInstalled(d2dConnectInfo.isSqlServerByReg());
		nodeInfoForD2D.setExchangeInstalled(d2dConnectInfo.isMsExchangeByReg());
		nodeInfoForD2D.setD2DPortNumber(d2dConnectInfo.getPort());
		nodeInfoForD2D.setD2DProtocol(d2dConnectInfo.getProtocol() == Protocol.Http.ordinal() ? Protocol.Http : Protocol.Https );
		nodeInfoForD2D.setRPSInstalled( d2dConnectInfo.isRpsInstalledByReg() );
		
		return nodeInfoForD2D;
	}

	@Override
	public RemoteNodeInfo tryConnectD2D(GatewayId gatewayId, String d2dProtocol, String d2dHost, int d2dPort,String d2dUserName, String d2dPassword) throws EdgeServiceFault {
		assert gatewayId!=null;
		assert gatewayId.getRecordId()!=0;
		ConnectionContext context = new ConnectionContext(d2dProtocol, d2dHost, d2dPort);
		context.buildCredential(d2dUserName, d2dPassword, "");
		GatewayEntity gateway = gatewayService.getGatewayById(gatewayId);
		context.setGateway(gateway);
		
		return tryConnectD2D(context);
	}

	public void setHostMgrDao(IEdgeHostMgrDao hostMgrDao) {
		this.hostMgrDao = hostMgrDao;
	}

	@Override
	public PagingResult<DiscoveredNode> getDiscoveredNodes(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return adService.getDiscoveredNodes(filter, config);
	}

	@Override
	public void hideDiscoverdNodes(int[] nodeIds) throws EdgeServiceFault {
		adService.hideDiscoverdNodes(nodeIds);
	}

	@Override
	public void addEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		esxService.addEsxSource(esxOption);
	}

	@Override
	public void cancelEsxDiscovery() throws EdgeServiceFault {
		esxService.cancelEsxDiscovery();
	}

	@Override
	public void deleteEsxSource(int id) throws EdgeServiceFault {
		esxService.deleteEsxSource(id);
	}

	@Override
	public String discoverNodesFromESX(DiscoveryESXOption[] esxOptions) throws EdgeServiceFault {
		return esxService.discoverNodesFromESX(esxOptions);
	}
	@Override
	public PagingResult<DiscoveredVM> getDiscoveredVMs(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return esxService.getDiscoveredVMs(filter, config);
	}

	@Override
	public DiscoveryMonitor getEsxDiscoveryMonitor() throws EdgeServiceFault {
		return esxService.getEsxDiscoveryMonitor();
	}

	@Override
	public List<DiscoveryESXOption> getEsxSourceList() throws EdgeServiceFault {
		return esxService.getEsxSourceList();
	}

	@Override
	public void hideDiscoverdVMs(int[] vmIds) throws EdgeServiceFault {
		esxService.hideDiscoverdVMs(vmIds);
	}

	@Override
	public void updateEsxSource(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		esxService.updateEsxSource(esxOption);
	}

	private List<NodeGroup> getReastorableNodeGroups(D2DServerInfo host){
		List<NodeGroup> restoreNodeGroup = new ArrayList<NodeGroup>();
		List<EdgeIntegerValue> intValueList = new ArrayList<EdgeIntegerValue>();
		this.hostMgrDao.as_edge_host_get_node_groups( host.getId(), intValueList );
		for (EdgeIntegerValue intValue : intValueList) {
			NodeGroup nodeGroup = new NodeGroup();
			nodeGroup.setId(intValue.getValue());
			nodeGroup.setType(NodeGroup.UNESX);
			restoreNodeGroup.add(nodeGroup);
		}	
		//There are no ESX groups in VCM. 
		if (EdgeWebServiceContext.getApplicationType() != EdgeApplicationType.VirtualConversionManager) {
			intValueList = new ArrayList<EdgeIntegerValue>();
			this.hostMgrDao.as_edge_host_get_node_esx_groups( host.getId(), intValueList );
			for (EdgeIntegerValue intValue : intValueList) {
				NodeGroup nodeGroup = new NodeGroup();
				nodeGroup.setId(intValue.getValue());
				nodeGroup.setType(NodeGroup.ESX);
				restoreNodeGroup.add(nodeGroup);
			}
		}
		return restoreNodeGroup;
	}
	private void setRestorableNodeRegularInfo(RestorableNode node, D2DServerInfo host, List<EdgeEsxVmInfo> vmList ){
		node.setName(host.getName());
		node.setExchangeInstalled(host.isExchangeInstalled());
		node.setSqlServerInstalled(host.isSqlServerInstalled());
		node.setVmInstalled(host.isVmInstalled());
//		node.setGroupIds(host.getGroupIds());
		node.setRestoreNodeGroup(getReastorableNodeGroups(host));
		node.setBackupType(host.getBackupType());
		node.setManagedStatus(NodeManagedStatus.Managed);
		node.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
		node.setVmName(vmList.get(0).getVmName());
		node.setHyperVisor(getHyperVisorInfo(vmList.get(0)));
	}

	protected void setHyperVVmAsPhysicalMachine(int hostID) {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		this.hostMgrDao.as_edge_hosts_list("(" + hostID + ")", hosts);

		if (hosts.size() > 0) {
			EdgeHost host = hosts.get(0);
			host.setRhostType(host.getRhostType() | HostType.EDGE_NODE_HYPERV_VM_AS_PHYSICAL_MACHINE.getValue());

			String hostName = host.getRhostname();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			try {
				GatewayEntity gateway = gatewayService.getGatewayByEntityId(hostID, EntityType.Node);
				if(gateway.getId() != null && gateway.getId().isValid()){
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gateway.getId() );
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				}
			} catch (Exception e) {
					logger.error("[NodeServiceImpl] setHyperVVmAsPhysicalMachine() get fqdn name failed.",e);
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			hostMgrDao.as_edge_host_update(host.getRhostid(), host.getLastupdated(), hostName,
					host.getNodeDescription(), host.getIpaddress(), host.getOsdesc(), host.getOstype(),
					host.getIsVisible(), host.getAppStatus(), host.getServerPrincipalName(), host.getRhostType(),
					host.getProtectionTypeBitmap(),fqdnNames, new int[1]);
		}
	}

	@Override
	public DiscoveryESXOption getVMNodeESXSettings(int hostID) throws EdgeServiceFault {
		MachineDetail machineDetail;
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(hostID)) {
			connection.connect();
			// Get the esx server information from the d2d host
			machineDetail = connection.getService().getMachineDetail(null);
		}
		
		if (machineDetail == null) {
			return null;
		}
		
		String hypervisorHostName = machineDetail.getHypervisorHostName();
		String hypervisorUserName = machineDetail.getHypervisorUserName();
		int hypervisorPort = machineDetail.getHypervisorPort();
		if (StringUtil.isEmptyOrNull(hypervisorHostName)
				|| StringUtil.isEmptyOrNull(hypervisorUserName) 
				|| hypervisorPort == 0) {
			return null;
		}
		
		DiscoveryESXOption result = new DiscoveryESXOption();
		// result.setId(esxList.get(0).getId());
		result.setEsxServerName(hypervisorHostName);
		result.setEsxUserName(hypervisorUserName);
		result.setEsxPassword(machineDetail.getHypervisorPassword());
		result.setIgnoreCertificate(true);
		result.setProtocol(Protocol.parse(machineDetail.getHypervisorProtocol()));
		result.setPort(hypervisorPort);
		result.setEsxHost(machineDetail.getESXHostName());
		
		return result;
	}
	
	@Override
	public DiscoveryHyperVOption getVMNodeHyperVSettings(int hostID) throws EdgeServiceFault {
		List<EdgeHyperVHostMapInfo> hostMapInfo = new LinkedList<EdgeHyperVHostMapInfo>();
		hyperVDao.as_edge_hyperv_host_map_getById(hostID, hostMapInfo);
		if (hostMapInfo.size()>0 && hostMapInfo.get(0).getHyperVId()>0){
			List<EdgeHyperV> hypervList = new LinkedList<EdgeHyperV>();
			hyperVDao.as_edge_hyperv_getById(hostMapInfo.get(0).getHyperVId(), hypervList);
			if (hypervList.size()>0){
				DiscoveryHyperVOption result = new DiscoveryHyperVOption();
				EdgeHyperV hyperV = hypervList.get(0);
				
				result.setId(hyperV.getId());
				result.setServerName(hyperV.getHostname());
				result.setUsername(hyperV.getUsername());
				result.setPassword(hyperV.getPassword());
				result.setCluster(hyperV.getType() == HypervProtectionType.CLUSTER.getValue());
				
				GatewayEntity gateway = this.gatewayService.getGatewayByEntityId(
					hyperV.getId(), EntityType.HyperVServer );
				result.setGatewayId( gateway.getId() );
				
				return result;
			}
		}
		return null;
	}
	
	@SuppressWarnings( "serial" )
	public class ConnectD2DException extends Exception {}

	@Override
	public void saveVMNodeESXSettings(int hostID, DiscoveryESXOption esxSetting) throws EdgeServiceFault {
		ConnectionContext context = new NodeConnectionContextProvider(hostID).create();
		
		MachineDetail machineDetail = new MachineDetail();
		machineDetail.setMachineType(MachineType.ESX_VM);
		machineDetail.setHostName(context.getHost());
		machineDetail.setHypervisorHostName(esxSetting.getEsxServerName());
		machineDetail.setHypervisorUserName(esxSetting.getEsxUserName());
		machineDetail.setHypervisorPassword(esxSetting.getEsxPassword());
		machineDetail.setHypervisorProtocol(esxSetting.getProtocol().toString());
		machineDetail.setHypervisorPort(esxSetting.getPort());
		machineDetail.setESXHostName(esxSetting.getEsxHost());
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			connection.getService().updateESXDetails(machineDetail);
		} catch (SOAPFaultException e) {
			SOAPFaultException exception = (SOAPFaultException) e;
			if (FlashServiceErrorCode.VCM_MACHINE_ISNOT_ESXVM.equals(exception.getFault().getFaultCodeAsQName()
					.getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_MACHINE_ISNOT_ESXVM, ""));
			} else if (FlashServiceErrorCode.VCM_VM_DOESNOT_EXIST_ON_ESX.equals(exception.getFault()
					.getFaultCodeAsQName().getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_VM_DOESNOT_EXIST_ON_ESX, ""));
			} else if (FlashServiceErrorCode.VCM_MACHINE_VMWARE_TOOLS_NOT_INSTALLED.equals(exception.getFault()
					.getFaultCodeAsQName().getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_VM_TOOLS_NOT_INSTALLED, ""));
			} else if (FlashServiceErrorCode.VCM_MACHINE_VMWARE_TOOLS_NOT_RUNNING.equals(exception.getFault()
					.getFaultCodeAsQName().getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_VM_TOOLS_NOT_RUNNING, ""));
			} else if (FlashServiceErrorCode.VCM_VC_ESX_INVALID_CREDENTIALS.equals(exception.getFault()
					.getFaultCodeAsQName().getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_VC_ESX_INVALID_CREDENTIALS, ""));
			} else if (FlashServiceErrorCode.VCM_VC_ESX_CONNECT_ERROR.equals(exception.getFault().getFaultCodeAsQName()
					.getLocalPart())) {
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(
						EdgeServiceErrorCode.Node_VCM_VC_ESX_CONNECT_ERROR, ""));
			} else
				throw new EdgeServiceFault("",
						new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General, ""));
		} catch (WebServiceException e) {
			if (e.getCause() != null && e.getCause() instanceof SocketTimeoutException) {
				EdgeServiceFault fault = EdgeServiceFault.getFault(
						EdgeServiceErrorCode.Node_UPDATE_VMWARED2D_ESX_TIMEOUT, "call d2d webservice timeout");
				throw fault;
			} else
				throw e;
		}
	}

	@Override
	public List<ESXServer> getEsxNodeList(DiscoveryESXOption esxOption)
			throws EdgeServiceFault {
		return this.esxService.getEsxNodeList(esxOption);
	}
	
	@Override
	public VSphereProxyInfo getVSphereProxyInfoByHostId(int hostId) throws EdgeServiceFault {
		return vcmService.getVSphereProxyInfoByHostId(hostId);
	}
	
	private int getPolicyTypeByApplicationType()
	{
		return PolicyManagementServiceImpl.getPolicyTypeByApplicationType();

	}

	@Override
	public List<Boolean> getEULAStatus(ShowEULAModule module)  throws EdgeServiceFault{
		List<Boolean> list = new ArrayList<Boolean>();
		if (module == ShowEULAModule.DeployModule) {
			list = this.getFlagFromRegistry(list,
					WindowsRegistry.VALUE_NAME_DEPLOY_AGREE_FLAG);
			list = this.getFlagFromRegistry(list,
					WindowsRegistry.VALUE_NAME_DEPLOY_SHOWLICENSE_FLAG);
		}
		if (module == ShowEULAModule.PolicyModule) {
			list = this.getFlagFromRegistry(list,
					WindowsRegistry.VALUE_NAME_POLICY_AGREE_FLAG);
			list = this.getFlagFromRegistry(list,
					WindowsRegistry.VALUE_NAME_POLICY_SHOWLICENSE_FLAG);
		}
		return list;
	}

	private List<Boolean> getFlagFromRegistry(List<Boolean> list, String key) {
		String flag = CommonUtil.getApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM, key);
		if (!StringUtil.isEmptyOrNull(flag)) {
			list.add(Boolean.valueOf(flag));
		} else {
			list.add(false);
		}
		return list;
	}

	@Override
	public void setEULAStatus(ShowEULAModule module, List<Boolean> list)  throws EdgeServiceFault{
		if (module == ShowEULAModule.DeployModule) {
			this.setFlagToRegistry(list.get(0),
					WindowsRegistry.VALUE_NAME_DEPLOY_AGREE_FLAG);
			this.setFlagToRegistry(list.get(1),
					WindowsRegistry.VALUE_NAME_DEPLOY_SHOWLICENSE_FLAG);
		}
		if (module == ShowEULAModule.PolicyModule) {
			this.setFlagToRegistry(list.get(0),
					WindowsRegistry.VALUE_NAME_POLICY_AGREE_FLAG);
			this.setFlagToRegistry(list.get(1),
					WindowsRegistry.VALUE_NAME_POLICY_SHOWLICENSE_FLAG);
		}
	}

	private void setFlagToRegistry(Boolean flag, String key) {
		CommonUtil.setApplicationExtentionKey(WindowsRegistry.KEY_NAME_ROOT_CM,
				key, flag.toString());
	}
		
	private Date generateRepeatFromDateByScheduleTime(Date scheduleTime) {
		Calendar scheduleCalendar = Calendar.getInstance();
		scheduleCalendar.setTime(scheduleTime);
		
		Calendar repeatFromCalendar = Calendar.getInstance();
		repeatFromCalendar.clear();
		
		int[] fields = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DATE };
		for (int field : fields) {
			repeatFromCalendar.set(field, scheduleCalendar.get(field));
		}
		
		return repeatFromCalendar.getTime();
	}
	
	private void copyHypervisorInfo(EdgeHost from,EdgeHost to){
		to.setVmGuestOS(from.getVmGuestOS());
		to.setVmUUID(from.getVmUUID());
		to.setVmXPath(from.getVmXPath());
		to.setHypervisorHostName(from.getHypervisorHostName());
		to.setHypervisorUsername(from.getHypervisorUsername());
		to.setHypervisorPassword(from.getHypervisorPassword());
		to.setHypervisorProtocol(from.getHypervisorProtocol());
		to.setHypervisorPort(from.getHypervisorPort());
		to.setHypervisorServerType(from.getHypervisorServerType());
		to.setHypervisorSocketCount(from.getHypervisorSocketCount());
		to.setHypervisorEssential(from.getHypervisorEssential());
		to.setHypervisorVisible(from.getHypervisorVisible());
	}
	
	@Override
	public List<ExportNode> getExportNodeList(int groupID, int grouptype,
			EdgeNodeFilter nodeFilter) throws EdgeServiceFault {
		List<ExportNode> exportNodes = new ArrayList<ExportNode>();
		
		NodePagingConfig np = new NodePagingConfig();
		np.setOrderCol(NodeSortCol.hostname);
		np.setOrderType(EdgeSortOrder.ASC);
		np.setStartpos(0);
		np.setPagesize(Integer.MAX_VALUE);
		int[] totalCount = new int[1];
		List<EdgeHost> hosts = getEdgeHostForPaging(groupID, grouptype, nodeFilter, np, totalCount);
		
		//adjuge need export linux backup server node
		List<EdgeHost> linuxBackupServer = null;
		boolean isNeedLinuxBackupServer = false;
		for(EdgeHost h : hosts){
			if(h.getRhostType() == HostType.EDGE_NODE_LINUX.getValue()){
				linuxBackupServer = getEdgeHostForPaging(0, NodeGroup.LinuxD2D, nodeFilter, np, totalCount);
				isNeedLinuxBackupServer=true;
				break;
			}
		}
		if(isNeedLinuxBackupServer){
			if(!hosts.contains(linuxBackupServer)){
				hosts.addAll(linuxBackupServer);
			}
		}
		
		//get hypervisor information
		StringBuilder sb = new StringBuilder();
		for(EdgeHost host : hosts){
			sb.append(host.getRhostid()).append(" ");
		}
		List<EdgeHost> hypervisorHosts = new ArrayList<EdgeHost>();
		List<EdgeHost> hypervHosts = new ArrayList<EdgeHost>();
		esxDao.as_edge_esx_vm_list_by_ids(sb.toString(),hypervisorHosts);
		hyperVDao.as_edge_hyperv_vm_list_by_ids(sb.toString(),hypervHosts);
		hypervisorHosts.addAll(hypervHosts);
		for(EdgeHost h : hosts){
			final String rHostname = h.getRhostname();
			final String vmName = h.getVmname();
			if (StringUtil.isEmptyOrNull(vmName) && StringUtil.isEmptyOrNull(rHostname)) {
				continue;
			}
			//fill in hypervisor host info
			for(EdgeHost hypervisorHost:hypervisorHosts){
				final String hypervisorHostName = h.getEsxName();
				if(StringUtil.isNotEmpty(hypervisorHostName) && hypervisorHostName.equals(hypervisorHost.getHypervisorHostName())){
					copyHypervisorInfo(hypervisorHost,h);
					break;
				}
			}
			ExportNode node = new ExportNode();
			node.setNodeDescription(h.getNodeDescription());
			node.setHostName(rHostname);
			node.setIpAddress(h.getIpaddress());
			node.setOsDesc(h.getOsdesc());
			node.setOsType(h.getOstype());
			node.setAppStatus(String.valueOf(h.getAppStatus()));
			node.setServerPrincipalName(h.getServerPrincipalName());
			node.setHostType(String.valueOf(h.getRhostType()));
			node.setTimezone(String.valueOf(h.getTimezone()));
			node.setProtectionType(String.valueOf(h.getProtectionTypeBitmap()));
			node.setMachineType(String.valueOf(h.getMachineType()));
			//VM information
			node.setVmName(h.getVmname());
			node.setVmInstanceUUID(h.getVmInstanceUuid());
			node.setVmStatus(String.valueOf(h.getVmStatus()));
			node.setVmHost(h.getEsxHost());
			node.setVmGuestOS(h.getVmGuestOS());
			node.setVmUUID(h.getVmUUID());
			node.setVmXPath(h.getVmXPath());
			//Hypervisor
			node.setHypervisorHostName(h.getHypervisorHostName());
			node.setHypervisorUsername(h.getHypervisorUsername());
			node.setHypervisorPassword(h.getHypervisorPassword());
			node.setHypervisorPort(String.valueOf(h.getHypervisorPort()));
			node.setHypervisorProtocol(String.valueOf(h.getHypervisorProtocol()));
			node.setHypervisorSocketCount(String.valueOf(h.getHypervisorSocketCount()));
			node.setHypervisorServerType(String.valueOf(h.getHypervisorServerType()));
			node.setHypervisorVisible(String.valueOf(h.getHypervisorVisible()));
			node.setHypervisorEssential(String.valueOf(h.getHypervisorEssential()));
			
			node.setNodeName(StringUtil.isNotEmpty(rHostname)?rHostname:vmName);
			node.setUsername(h.getUsername());
			node.setPassword(WSJNI.AFEncryptString(h.getPassword()));
			node.setPort(h.getD2dPort());
			node.setProtocol(String.valueOf(h.getD2dProtocol()));
			exportNodes.add(node);
		}
		return exportNodes;
	}
	
	@Override
	public String generateExportNodeFile(List<Integer> nodeIds)
			throws EdgeServiceFault {
		NodeExporter exporter = new NodeExporter(nodeIds);
		return exporter.run();
	}
	
	@Override
	public String generateExportNodeFileForGroup(int gatewayId, int groupType, int groupId)
			throws EdgeServiceFault {
		List<Integer> ids = getNodeIdsByGroup(gatewayId, groupId, groupType);
		NodeExporter exporter = new NodeExporter(ids);
		return exporter.run();
	}
	
	@Override
	public int importNodesFromFile(String filePath) throws EdgeServiceFault {
		NodeImporter nodeImporter = new NodeImporter(filePath, this);
		EdgeExecutors.getCachedPool().submit(nodeImporter);
		return nodeImporter.getTaskId();
	}
	
	@Override
	public NodePagingResult getNodesESXByGroupAndTypePaging(int groupID,
			int grouptype, EdgeNodeFilter nodeFilter, NodePagingConfig np) throws EdgeServiceFault

	{

		NodePagingResult nodePagingResult = new NodePagingResult();
		List<Node> nodes = new LinkedList<Node>();
		
		int[] totalCount = new int[1];
		List<EdgeHost> hosts = getEdgeHostForPaging(groupID, grouptype, nodeFilter, np, totalCount);

		addHostToNodeListWithFullInfo(hosts, nodes);
		
		if (grouptype == NodeGroup.GDB) {
			for (Node node : nodes) {
				// For GDB group, EDGE will return the branch server list.
				// And the GDB id is equal to group id.
				node.setGDBId(groupID);
			}
		}
		
		//set node's job running status
		setJobRunningNode(nodes);
		
		for (Node node : nodes) {
			node.setLstJobHistory(getLatestJobHistoriesByNodeId(node.getId()));
		}
		
		nodePagingResult.setData(nodes);
		nodePagingResult.setStartIndex(np.getStartpos());
		nodePagingResult.setTotalCount(totalCount[0]);

		return nodePagingResult;
	}
	
	private void setJobRunningNode(List<Node> nodes ){
		if(nodes == null || nodes.isEmpty()) 
			return;
		// change getJobRunningStaus From DB, not From Cache
/*		for (Node node : nodes) {
			List<FlashJobMonitor> jobMonitors = D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(getJobMonitorKey(node));
			for (FlashJobMonitor flashJobMonitor : jobMonitors) {
				if(flashJobMonitor.isPendingJobMonitor()) {
					node.setWaitingJobRunning(true);
				} else {
					node.setJobRunning(true);
					break;
				}
			}
		}*/
		
		// Get all jobMonitor form DB
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		historyDao.as_edge_d2dJobHistory_monitor_getJobMonitor(-1, -1, "", lstJobHistory);
		if(lstJobHistory==null||lstJobHistory.isEmpty())
			return;
		Map<String, Long> statusMap = new HashMap<String, Long>();
		for(EdgeJobHistory monitor:lstJobHistory){
			if(statusMap.containsKey(monitor.getAgentId())){
				long oldStatus = statusMap.get(monitor.getAgentId());
				if(oldStatus == JobStatus.Waiting.getValue() 
						&& monitor.getJobStatus() != oldStatus){
					statusMap.put(monitor.getAgentId(), monitor.getJobStatus());	
				}
			} else {
				statusMap.put(monitor.getAgentId(), monitor.getJobStatus());			
			}
		}
		for (Node node : nodes) {
			String key = ""+node.getId();			
			if(!statusMap.containsKey(key))
				continue;
			long status = statusMap.get(key);
			if(status == JobStatus.Waiting.getValue()){
				node.setWaitingJobRunning(true);
			} else {
				node.setJobRunning(true);
			}
		}
				
	}
	
	private String getJobMonitorKey(Node node) {
		if (node.isLinuxNode()) {
			return "LinuxD2D" + "-" + node.getId() + "-";
		} else {
			return "D2D" + "-" + node.getId() + "-";
		}
	}
	
	private List<EdgeHost> getEdgeHostForPaging(int groupID,
			int grouptype, EdgeNodeFilter nodeFilter, NodePagingConfig np, int[] totalCount){
		List<EdgeHost> hosts = new ArrayList<EdgeHost>();
		
		String col2 = null;
		if (np.getOrderCol() == NodeSortCol.vcenter)
			col2 = NodeSortCol.vcenter.value();
		else if (np.getOrderCol() == NodeSortCol.policy)
			col2 = NodeSortCol.policyStatus.value();

		if (grouptype == NodeGroup.GDB) {
			hostMgrDao.as_edge_host_list_display_info_by_gdb_id_paging(groupID,
					PolicyTypes.BackupAndArchiving, nodeFilter.getNodeName(), np
							.getStartpos(), np.getPagesize(), np.getOrderType()
							.value(), np.getOrderCol().value(), col2,
					totalCount, hosts);
		} else {
			hostMgrDao.as_edge_GetFilteredPagingNodeList(groupID, grouptype, nodeFilter.getHostTypeBitmap(),nodeFilter.getNodeVisibleLevel(),nodeFilter.getVappId(),
					nodeFilter.getNodeName(), nodeFilter.getGatewayId(), nodeFilter.getApplicationBitmap(), nodeFilter.getJobStatusBitmap(), 
					nodeFilter.getProtectionTypeBitmap(), nodeFilter.getNodeStatusBitmap(), nodeFilter.getOsBitmap(), nodeFilter.getRemoteDeployBitmap(),nodeFilter.getLastBackupStatusBitmap(),
					getRemoteDeployParam( nodeFilter.getRemoteDeployBitmap() ), nodeFilter.getNotnullfieldBitmap(), np.getStartpos(), np.getPagesize(), np.getOrderType().value(), np.getOrderCol().value(), col2, 
					totalCount, hosts);
		}
		
		return hosts;
	}
	public String getRemoteDeployParam( int deployBitmap ){
		String result = "";
		if( deployBitmap != 0 ) {
			try {
				//major + minor + build +update patch
				EdgeVersionInfo edgeVersionInfo = commonService.getVersionInformation();
				return (edgeVersionInfo.getVersionString()+"."+(StringUtil.isEmptyOrNull(edgeVersionInfo.getUpdateNumber())?"0":edgeVersionInfo.getUpdateNumber()));
			} catch (EdgeServiceFault e) {
				logger.error("generate patch info for remote deploy failed:", e);
			}
		}
		return result;
	}
	@Override
	public D2DBackupJobStatusInfo getBackupJobStatusById(int nodeId)
			throws EdgeServiceFault {
		return D2DJobsStatusCache.getD2DBackupJobsStatusCache().get(String.valueOf(nodeId));
	}
	@Override
	public List<D2DBackupJobStatusInfo> getBackupJobStatusAll(
			List<String> nodeIdList) throws EdgeServiceFault {
		return D2DJobsStatusCache.getD2DBackupJobsStatusCache().getD2DBackupJobStatusInfoList(nodeIdList);
	}
	
	@Deprecated
	@Override
	public List<FlashJobMonitor> getJobStatusInfoList(String jobStatusKey)
			throws EdgeServiceFault {
		return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(jobStatusKey);
	}
	
	@Override
	public List<FlashJobMonitor> getJobMonitorForDashboard(int productType, int nodeId, int rpsNodeId, long jobType
			, long jobId, String jobUUID) throws EdgeServiceFault {
		return D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobMonitorForDashboard(productType, nodeId, rpsNodeId, jobType, jobId, jobUUID);
	}
	
	@Override
	public boolean cancelJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(nodeId)) {
			connection.connect();
			
			connection.getService().cancelJob(jobId);
		} catch (SOAPFaultException e) {
			throw  EdgeServiceFault.getFault(FlashServiceErrorCode.Common_CancelJobFailed, "");
		} catch (WebServiceException e){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, "");
		}
		
		return true;
	}
	
	@Override
	public int cancelJobById(int nodeId, String hostName, long jobId, long jobType, String d2dUuid, String vmInstanceUuid, boolean isCancelJobFromRPS)
			throws EdgeServiceFault {
		EdgeHost host = HostInfoCache.getInstance().getHostInfo(nodeId);
		if (isCancelJobFromRPS) {
			try (RPSConnection connection = connectionFactory.createRPSConnection(nodeId)) {
				connection.connect();
				
				logger.debug("cancelJobById: jobId = " + jobId + ", jobType = " + jobType);
				
				int value;
				if (jobType == JobType.JOBTYPE_RPS_MERGE) {				
					value = connection.getService().pauseMerge(MergeAPISource.MANUALLY, StringUtil.isEmptyOrNull(vmInstanceUuid) ? d2dUuid : vmInstanceUuid);
				} else {				
					value = (int) connection.getService().cancelJob(StringUtil.isEmptyOrNull(vmInstanceUuid) ? d2dUuid : vmInstanceUuid, jobId, jobType);
				}
				
				logger.debug("cancelJobById: returnValue = " + value);
				return value;
			}
		} else if (JobType.JOBTYPE_CONVERSION == jobType) {
			// cancel conversion job
			cancelReplication(nodeId, hostName, vmInstanceUuid);
			return 0;	// Cancel succeed, return 0, else, it will throw exception
		} else {
			if (!StringUtil.isEmptyOrNull(vmInstanceUuid)) {
				try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(nodeId))) {
					connection.connect();
					
					if (jobType == JobType.JOBTYPE_VM_BACKUP && jobId == 0) { // For vShpere waiting job
						return connection.getService().cancelWaitingJob(vmInstanceUuid);
					} else {		
						if(HostTypeUtil.isVapp(host.getRhostType())){
							return cancelvAppJob(nodeId, vmInstanceUuid, jobId,jobType,connection);
						}else {
							return connection.getService().cancelJob(jobId);
						}
					}
				}
			} else {				
				try (D2DConnection connection = connectionFactory.createD2DConnection(nodeId)) {
					connection.connect();
					return connection.getService().cancelJob(jobId);
				}
			}
		}
	}
	
	@Override
	public void cancelJobByGroup(int gatewayId, int groupId, int groupType) throws EdgeServiceFault {
		logger.debug("[NodeServiceImpl] cancelJobByGroup() gateway ID: "+gatewayId);
		logger.debug("[NodeServiceImpl] cancelJobByGroup() group ID: "+groupId);
		logger.debug("[NodeServiceImpl] cancelJobByGroup() group Type: "+groupType);
		
		BackupNowJob cancelJob = new BackupNowJob(gatewayId, groupId, groupType);
		cancelJob.cancelBackupByGroup();
	}
	
	@Override
	public boolean cancelVMJob(int nodeId, String hostName, long jobId) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(nodeId))) {
			connection.connect();
			connection.getService().cancelJob(jobId);
		} catch (SOAPFaultException e) {
			throw  EdgeServiceFault.getFault(FlashServiceErrorCode.Common_CancelJobFailed, "");
		} catch (WebServiceException e){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, "");
		}
		
		return true;
	}
	
	public int cancelvAppJob(int nodeId,String instanceUUID,long jobId, long jobType, D2DConnection connection) throws EdgeServiceFault {
		connection.getService().cancelvAppChildVMJob(instanceUUID, jobType);
		boolean cancelChildSuccess = false;
		int retryCount = 60;
		do {
			if(connection.getService().isAllvAppChildJobCanceled(instanceUUID, jobType)) { 
				cancelChildSuccess = true;
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} while (--retryCount > 0);
		if(cancelChildSuccess){
			connection.getService().cancelJob(jobId);
		}
		else {
			logger.error("[NodeServiceImpl]: cancel vapp child job timeout.");
			return 1;
		}
		return 0;
	}


	@Override
	public boolean cancelWaitingJob(Node node, String vmInstanceUuid) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(node.getId()))) {
			connection.connect();
			connection.getService().cancelWaitingJob(vmInstanceUuid);
			generateLog(Severity.Warning, node, EdgeCMWebServiceMessages.getMessage("CancelVMBackupJob_Success"), Module.SubmitD2DJob);
		} catch (SOAPFaultException e) {
			generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("CancelVMBackupWaitingJobFailed"), Module.SubmitD2DJob);
			logger.error(e.getMessage(), e);
			throw  EdgeServiceFault.getFault(EdgeServiceErrorCode.CancelVMBackupWaitingJobFailed, "");
		} catch (WebServiceException e){
			generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("CancelVMBackupJob_CantConnect2Proxy"), Module.SubmitD2DJob);
			logger.error(e.getMessage(), e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, "");
		}
		return true;
	}
	
	public EdgeConnectInfo getEdgeConnectInfoById(int nodeId) {		
		List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(nodeId, infos);
		EdgeConnectInfo edgeConnectInfo = new EdgeConnectInfo();
		if (infos != null && !infos.isEmpty()) {
			edgeConnectInfo = infos.get(0);
		}
		return edgeConnectInfo;
	}

	@Override
	public void backupVM(int nodeID, int backupType, String jobName) throws EdgeServiceFault {
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(nodeID, vmList);
		
		if(vmList.isEmpty()){
			logger.error("[NodeServiceImpl] backupVM can not find the vm according to the nodeId: "+nodeID);
			return;
		}
			
		VirtualMachine vm = new VirtualMachine();
		vm.setVmName(vmList.get(0).getVmName());
		vm.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
		vm.setVmUUID(vmList.get(0).getVmUuid());
		
		ConnectionContext context = new VMConnectionContextProvider(nodeID).create();
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.vShpereManager, EdgeCommonUtil.getLocalFqdnName());
			if (1 != regStatus){
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Backup_ProxyManagedByOthers, "");
			}
			
			connection.getService().backupVM(backupType, jobName, vm);
		} catch (SOAPFaultException e) {
			logger.error(e);
			EdgeServiceFault fault = D2DServiceFault.getFault(e.getFault().getFaultCodeAsQName().getLocalPart(), "");
			fault.getFaultInfo().setMessageParameters(new Object[]{e.getMessage()});
			throw fault;
		} catch (WebServiceException e){
			logger.error(e.getMessage(), e);
			EdgeServiceFault fault = EdgeServiceFault.getFault(EdgeServiceErrorCode.Backup_CantConnect2Proxy , "");
			fault.getFaultInfo().setMessageParameters(new String[]{context.getHost()});
			throw fault;
		}
	}
	
	private VirtualMachine getVirtualMachine(int nodeID) {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(nodeID, 1, hosts);
		
		VirtualMachine vm = new VirtualMachine();
		
		if (HostTypeUtil.isVMWareVirtualMachine(hosts.get(0).getRhostType())) {
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(nodeID, vmList);
			if(!vmList.isEmpty()){
				vm.setVmName(vmList.get(0).getVmName());
				vm.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
				vm.setVmUUID(vmList.get(0).getVmUuid());
			}else {
				logger.error("[NodeServiceImpl] getVirtualMachine() Can not find the vm info according to nodeId "+nodeID);
			}
		} else {
			List<EdgeHyperVHostMapInfo> hypervHostMap = new LinkedList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_getById(nodeID, hypervHostMap);
		
			vm.setVmName(hypervHostMap.get(0).getVmName());
			vm.setVmInstanceUUID(hypervHostMap.get(0).getVmInstanceUuid());
			vm.setVmUUID(hypervHostMap.get(0).getVmUuid());
		}
		
		return vm;
	}
	
	public void backupVMWithFlag(int nodeID, int backupType, String jobName, boolean convertForBackupSet) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(nodeID))) {
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.vShpereManager,EdgeCommonUtil.getLocalFqdnName());
			if (1 != regStatus) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Backup_ProxyManagedByOthers, "");
			}
			
			VirtualMachine vm = getVirtualMachine(nodeID);
			connection.getService().backupVMWithFlag(backupType, jobName, vm, convertForBackupSet);
		}
	}
	
	@Override
	public int submitBackupJob(int gatewayId, int groupID, int groupType, int backupType, String jobName) throws EdgeServiceFault {
		logger.debug("submit backup job: gatewayId: "+gatewayId+"group ID: "+groupID+", groupType: "+groupType+", backupType: "+backupType+", jobName: "+jobName);
		List<Integer> nodeIds = getNodeIdsByGroup(gatewayId, groupID, groupType);
		BackupNowTaskParameter parameter = new BackupNowTaskParameter();
		parameter.setModule(Module.SubmitD2DJob);
		parameter.setBackupType(backupType);
		parameter.setJobName(jobName);
		parameter.getEntityIds().addAll(nodeIds);
		ActionTaskManager<Integer> actionTaskManager = new ActionTaskManager<Integer>(parameter);
		return actionTaskManager.doAction();
	}
	@Override
	public void deleteNodes(final int[] ids, final boolean keepCurrentSettings) throws EdgeServiceFault {
		try{
			if (ids.length == 0)
				return;
			
			String nodeIDString = int2String(ids);
			this.hostMgrDao.markHostAsRemoved(nodeIDString);
			Runnable deleteNodeTask = new Runnable(){

				@Override
				public void run() {
					for (int id : ids){
						try {
							deleteNodeInternal(id, -1, keepCurrentSettings);
						} catch (EdgeServiceFault e) {
							logger.error("error when delete nodes", e);
						}
					}
				}
			};
			
			EdgeExecutors.getCachedPool().submit(deleteNodeTask);
		}catch(Exception e){
			logger.error("error when delete nodes", e);
		}
	}
	
	@Override
	public int backupNodesForEDGE(int[] ids, int backupType, String jobName)
			throws EdgeServiceFault {
		BackupNowTaskParameter parameter = new BackupNowTaskParameter();
		parameter.setModule(Module.SubmitD2DJob);
		parameter.setBackupType(backupType);
		parameter.setJobName(jobName);
		for (int i = 0; i < ids.length; i++) {
			parameter.getEntityIds().add(ids[i]);
		}
		ActionTaskManager<Integer> actionTaskManager = new ActionTaskManager<Integer>(parameter);
		return actionTaskManager.doAction();
	}
	@Override
	public void backupVMs(int[] nodeIDs, int backupType, String jobName)
			throws EdgeServiceFault {
		BackupNowVMJob.BackupNowVMJobMultipleNodes backupJobs = new BackupNowVMJob.BackupNowVMJobMultipleNodes(nodeIDs, backupType, jobName);
		backupJobs.submitBackupJobs();
	}
	
	@Override
	public void verifyVMs(int[] nodeIDs) throws EdgeServiceFault {
		try {
			Map<String, List<Integer>> splitedNodes = splitNodesByHypervisorType(nodeIDs);
			
			List<Integer> hypervNodes = splitedNodes.get(TYPE_HYPERV);
			if (hypervNodes !=null && hypervNodes.size() > 0) {
				String jobID = java.util.UUID.randomUUID().toString();
				VerifyHypervVMsJob job = new VerifyHypervVMsJob();
				job.setId(jobID);
				
				int[] tempNodes = new int[hypervNodes.size()];
				int index = 0;
				for (Integer id : hypervNodes) {
					tempNodes[index++] = id;
				}
				job.schedule(job.createJobDetail(tempNodes));
			}
			
			List<Integer> esxNodes = splitedNodes.get(TYPE_ESX);
			if (esxNodes !=null && esxNodes.size() > 0) {
				String jobID = java.util.UUID.randomUUID().toString();
				VerifyVMsJob job = new VerifyVMsJob();
				job.setId(jobID);
				
				int[] tempNodes = new int[esxNodes.size()];
				int index = 0;
				for (Integer id : esxNodes) {
					tempNodes[index++] = id;
				}
				job.schedule(job.createJobDetail(tempNodes));
			}
		} catch (Throwable e) {
			logger.error("verifyVMs()", e);
		}
	}
	
	@Override
	public String queryVMHostName(int hostID) throws EdgeServiceFault {
		NodeDetail nodeDetail = getNodeDetailInformation(hostID);
		if(nodeDetail.isHyperVMachine()){
			return queryHypervVMHostName(hostID);
		}else {
			return queryVmwareVMHostName(hostID);
		}
	}
	
	private String queryHypervVMHostName( int nodeId)throws EdgeServiceFault {
		List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>(1);
		hyperVDao.as_edge_hyperv_host_map_getById(nodeId, hostMapInfo);
		
		if (hostMapInfo.size()==0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND , "");
			
		EdgeHyperVHostMapInfo hostMap = hostMapInfo.get(0);
		
		List<EdgeHyperV> hypervList = new ArrayList<EdgeHyperV>(1);
		hyperVDao.as_edge_hyperv_getById(hostMap.getHyperVId(), hypervList);
		
		if (hypervList.size()==0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_NOTFOUND , "");
		EdgeHyperV hyperV = hypervList.get(0);
		GatewayId gateWayId = gatewayService.getGatewayByEntityId(hyperV.getId(), EntityType.HyperVServer).getId();
		JHypervVMInfo vmInfo = HyperVManagerAdapter.getInstance().getHypervVMInfo(gateWayId, hyperV.getHostname(), hyperV.getUsername(),
				hyperV.getPassword(),hostMapInfo.get(0).getVmInstanceUuid());
		String vmHostName = vmInfo==null?"":vmInfo.getVmHostName(); 
		if (StringUtil.isEmptyOrNull(vmHostName))
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CANT_QUERY_VM_HOSTNAME, "");
		
		return vmHostName;
	}
	
	private String queryVmwareVMHostName(int hostID) throws EdgeServiceFault {
		List<EdgeEsx> esxList = new LinkedList<>();
		esxDao.as_edge_esx_getHypervisorByHostId(hostID,esxList);
		
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(hostID, vmList);
		
		if (vmList.size()==0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_NOTFOUND , "");
			
		if (esxList.size()==0)
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_ESX_NOTFOUND , "");
			
		DiscoveryESXOption esxOption = new DiscoveryESXOption();
		esxOption.setEsxHost(esxList.get(0).getHostname());
		esxOption.setEsxServerName(esxList.get(0).getHostname());
		esxOption.setEsxUserName(esxList.get(0).getUsername());
		esxOption.setEsxPassword(esxList.get(0).getPassword());
		esxOption.setIgnoreCertificate(true);
		esxOption.setPort(esxList.get(0).getPort());
		esxOption.setProtocol(Protocol.parse(esxList.get(0).getProtocol()));
		
		GatewayEntity gateway = this.gatewayService.getGatewayByHostId( hostID );
		esxOption.setGatewayId( gateway.getId() );
		
		String hostName = null;
		
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		hostName = vmwareService.getVMHostName(esxOption,vmList.get(0).getVmName(), vmList.get(0).getVmInstanceUuid());
		vmwareService.close();
		
		if (hostName == null || hostName.isEmpty())
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CANT_QUERY_VM_HOSTNAME, "");
		
		return hostName;
	}
	
	
	@Override
	public void submitVerifyVMJobForGroup(int gatewayId, int groupID, int groupType) throws EdgeServiceFault {
		try{
			List<Integer> nodeList = getNodeIdsByGroup(gatewayId, groupID, groupType);
			int[] idList = new int[nodeList.size()];
			for (int i=0;i<nodeList.size();i++){
				idList[i] = nodeList.get(i);
			}
			this.verifyVMs(idList);
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	private String int2String(int[] source) {
		if (source == null)
			return "null";
		int iMax = source.length - 1;
		if (iMax == -1)
			return "()";

		StringBuilder b = new StringBuilder();
		b.append('(');
		for (int i = 0;; i++) {
			b.append(source[i]);
			if (i == iMax)
				return b.append(')').toString();
			b.append(", ");
		}
	}
	
	@Override
	public boolean cancelReplication(int nodeId, String hostName, String vmInstanceUUID) throws EdgeServiceFault {
		try {
			try (D2DConnection connection = connectionFactory.createD2DConnection(new ConverterConnectionContextProvider(nodeId, this))) {
				connection.connect();
				
				String uuid = vmInstanceUUID;
				if (StringUtil.isEmptyOrNull(uuid)) {
					EdgeConnectInfo edgeConnectInfo = getEdgeConnectInfoById(nodeId);
					uuid = edgeConnectInfo.getUuid();
				}
				
				if (!StringUtil.isEmptyOrNull(uuid)) {
					connection.getService().cancelReplication(uuid);
					return true;
				} else {
					logger.error("UUID for cancel replication job is empty.");
				}
			}
			
			if (StringUtil.isEmptyOrNull(vmInstanceUUID)) {
				try (D2DConnection connection = connectionFactory.createD2DConnection(nodeId)) {
					connection.connect();
					
					EdgeConnectInfo edgeConnectInfo = getEdgeConnectInfoById(nodeId);
					connection.getService().cancelReplication(edgeConnectInfo.getUuid());
				}
			} else {
				try (D2DConnection connection = connectionFactory.createD2DConnection(new VMConnectionContextProvider(nodeId))) {
					connection.connect();
					
					connection.getService().cancelReplication(vmInstanceUUID);
				}
			}
		} catch (SOAPFaultException e) {
			throw  EdgeServiceFault.getFault(FlashServiceErrorCode.Common_CancelJobFailed, "");
		} catch (WebServiceException e){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, "");
		}
		
		return true;
	}
	@Override
	public List<RepJobMonitor4Edge> getConversionJobStatusAll(List<String> nodeIdList)
			throws EdgeServiceFault {
		return D2DConversionJobsStatusCache.getJobsStatusCache().getD2DConversionJobStatusInfoList(nodeIdList);
	}
	@Override
	public RepJobMonitor4Edge getRepJobMonitorById(int nodeId)
			throws EdgeServiceFault {
		return D2DConversionJobsStatusCache.getJobsStatusCache().get(String.valueOf(nodeId));
	}
	@Override
	public void changeHeartBeatStatus(int[] nodeID, boolean enabled)
			throws EdgeServiceFault {
		if (nodeID == null || nodeID.length==0)
			return;
		
		if (nodeID.length>1){
			HeartBeatJob.HeartBeatJobForNodes job = new HeartBeatJob.HeartBeatJobForNodes(this, nodeID);
			job.changeHeartBeatStatus(enabled);
			return;
		}
		
		NodeDetail nodeDetail = getNodeDetailInformation(nodeID[0]);
		VCMServiceManager.getInstance().changeHeartBeatStatus(nodeDetail, enabled);
		
	}
	@Override
	public void changeAutoOfflieCopyStatus(int[] nodeID, boolean enabled, boolean forceSmartCopy)
			throws EdgeServiceFault {
		if (nodeID == null || nodeID.length==0)
			return;
		
		if (nodeID.length>1){
			AutoOfflineCopyJob.AutoOfflineCopyJobForNodes job = new AutoOfflineCopyJob.AutoOfflineCopyJobForNodes(this, nodeID, forceSmartCopy);
			job.changeAutoOfflineCopyStatus(enabled);
			return;
		}
		
		NodeDetail nodeDetail = getNodeDetailInformation(nodeID[0]);
		VCMServiceManager.getInstance().changeAutoOfflieCopyStatus(nodeDetail, enabled, forceSmartCopy);
		
	}
	@Override
	public void changeHeartBeatStatusForGroup(int groupID, int groupType,
			boolean enabled) throws EdgeServiceFault {
		HeartBeatJob.HeartBeatJobForGroup job = new HeartBeatJob.HeartBeatJobForGroup(this, groupID, groupType);
		job.changeHeartBeatStatus(enabled);
	}
	@Override
	public void changeAutoOfflieCopyStatusForGroup(int groupID, int groupType,
			boolean enabled) throws EdgeServiceFault {
		AutoOfflineCopyJob.AutoOfflineCopyJobForGroup job = new AutoOfflineCopyJob.AutoOfflineCopyJobForGroup(this, groupID, groupType);
		job.changeAutoOfflineCopyStatus(enabled);
	}
	
	@Override
	public DiscoveryESXOption getESXInformation(int id) throws EdgeServiceFault {
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
		esxDao.as_edge_esx_getById(id, esxList);
		
		if (esxList.size()>0){
			DiscoveryESXOption result = new DiscoveryESXOption();
			result.setId(esxList.get(0).getId());
			result.setEsxServerName(esxList.get(0).getHostname());
			result.setEsxUserName(esxList.get(0).getUsername());
			result.setEsxPassword(esxList.get(0).getPassword());
			result.setIgnoreCertificate(true);
			result.setProtocol(Protocol.parse(esxList.get(0).getProtocol()));
			result.setPort(esxList.get(0).getPort());
			return result;
		}else
			return null;
	}
	@Override
	public void redeployPolicyByESX(int esxID) throws EdgeServiceFault {
		try{
			EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
			List<Node> nodeList = null;
			NodePagingConfig pagingConfig = new NodePagingConfig();
			
			pagingConfig.setOrderCol(NodeSortCol.hostname);
			pagingConfig.setOrderType(EdgeSortOrder.ASC);
			pagingConfig.setPagesize(Integer.MAX_VALUE);
			pagingConfig.setStartpos(0);
			
			NodePagingResult result = getNodesESXByGroupAndTypePaging(esxID, NodeGroup.ESX, nodeFilter, pagingConfig);
			nodeList = result.getData();
			
			for (int i=0;i<nodeList.size();i++){
				Node node = nodeList.get(i);
				try{
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(1);
					policyDao.getHostPolicyMapByHostAndPlanTaskType(node.getId(), PolicyTypes.VMBackup, mapList);
					
					if (mapList.size()>0){
						ActivityLog log = new ActivityLog();
						log.setModule(Module.PolicyManagement);
						log.setSeverity(Severity.Information);
						log.setNodeName(node.getHostname());
						log.setMessage(EdgeCMWebServiceMessages.getResource("policyDeployment_Redeploy_VCenterESXChanged"));
						
						logService.addLog(log);
						serviceImpl.redeployPolicyToNodes(Arrays.asList(node.getId()), PolicyTypes.Unified, mapList.get(0).getPolicyId());
					}
				}catch(Exception e){
					logger.error(e);
				}
			}
			
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	@Override
	public VMVerifyStatus getVMVerifyStatus(int id) throws EdgeServiceFault {
		VMVerifyStatus result = new VMVerifyStatus();
		
		NodeDetail nodeDetail = getNodeDetailInformation(id);
		if (nodeDetail != null && nodeDetail.isHyperVMachine()) {
			List<EdgeHyperVVerifyStatus> hypervVerifyStatusList = new LinkedList<EdgeHyperVVerifyStatus>();
			hyperVDao.as_edge_hyperv_verify_status_getById(id,hypervVerifyStatusList);
			
			try {
				if(hypervVerifyStatusList.size()>0){
					int status = hypervVerifyStatusList.get(0).getStatus();
					String detail = hypervVerifyStatusList.get(0).getDetail();
					result.setStatus(status);
					if(hypervVerifyStatusList.get(0).getDetail()!=null){
						VMVerifyStatus verifyStatus = CommonUtil.unmarshal(detail, VMVerifyStatus.class);
						if(verifyStatus != null){
							result.setDetails(verifyStatus.getDetails());	
						}
					}
				}
				
			} catch (Exception e) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
			}
		} else if (nodeDetail != null && nodeDetail.isVMwareMachine()) {
			result = this.esxService.getVMVerifyStatus(id);
		}
		
		return result;
	}
	@Override
	public List<ESXServer> getDiscoveryEsxServers(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		return esxService.getDiscoveryEsxServers(esxOption);
	}
	@Override
	public List<DiscoveryVirtualMachineInfo> getVmList(DiscoveryESXOption esxOption, ESXServer esxServer) throws EdgeServiceFault {
		return esxService.getVmList(esxOption, esxServer);
	}
	@Override
	public int updateMultipleNodeByIds(int[] nodeID, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential)
			throws EdgeServiceFault {
		UpdateMultiNodesParameter<Integer> parameter = new UpdateMultiNodesParameter<Integer>();
		parameter.setModule(Module.UpdateMutipleNode);
		for(int i=0 ; i< nodeID.length ; i++){
			parameter.getEntityIds().add(nodeID[i]);
		}
		parameter.setGlobalUsername(globalUsername);
		parameter.setGlobalPassword(globalPassword);
		parameter.setForceManaged(forceManaged);
		parameter.setUsingOrignalCredential(usingOrignalCredential);
		ActionTaskManager<Integer> manager = new ActionTaskManager<Integer>(parameter,serviceImpl);
		return manager.doAction();
	}
	@Override
	public int updateMultipleNodeForGroup(int gatewayId, int groupId, int groupType, String globalUsername, String globalPassword, boolean forceManaged, boolean usingOrignalCredential)
			throws EdgeServiceFault {
		List<Integer> nodeIds = getNodeIdsByGroup(gatewayId, groupId, groupType);
		UpdateMultiNodesParameter<Integer> parameter = new UpdateMultiNodesParameter<Integer>();
		parameter.setModule(Module.UpdateMutipleNode);
		parameter.getEntityIds().addAll(nodeIds);
		parameter.setGlobalUsername(globalUsername);
		parameter.setGlobalPassword(globalPassword);
		parameter.setForceManaged(forceManaged);
		parameter.setUsingOrignalCredential(usingOrignalCredential);
		ActionTaskManager<Integer> manager = new ActionTaskManager<Integer>(parameter,serviceImpl);
		return manager.doAction();
	}

	@Override
	public VMSnapshotsInfo[] getVMSnapshots(Node node) throws EdgeServiceFault {
		// Query the database to get the snapshot list
		List<VMSnapshotsInfo> snapshotList = new ArrayList<VMSnapshotsInfo>();
		vsbDao.as_edge_host_vsb_snapshot_getByHostId(node.getId(), snapshotList);
		for (VMSnapshotsInfo info : snapshotList) {
			if (info.getTimestamp() > 0) {
				info.setTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(new Date(info.getTimestamp())));
			}
		}
		return snapshotList.toArray(new VMSnapshotsInfo[0]);
	}
	
	@Override
	public int shutDownVM(Node node) throws EdgeServiceFault {
		ID2D4EdgeService monitorService = null;
		String moniteeUUID = null;
		try (D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
			connection.connect();
			monitorService = connection.getService();
			moniteeUUID = getMoniteeUUID(node);
			int returnValue = monitorService.shutdownVM(moniteeUUID);
			if (returnValue == 0) {
				generateLogForVCMRecoverPointSnapshots(Severity.Information, node, EdgeCMWebServiceMessages.getMessage("shutDownVMSuccessful", node.getHostname()));
			} else {
				generateLogForVCMRecoverPointSnapshots(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("shutDownVMFailed", node.getHostname()));
			}
			return returnValue;
		} catch (Exception e){
			generateLogForVCMRecoverPointSnapshots(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("shutDownVMFailed", node.getHostname()));
			throw EdgeServiceFault.getFault("", "shutDownVMFailed");
		}
	}

	@Override
	public String getCurrentRunningSnapshot(Node node) throws EdgeServiceFault {
		List<D2DStatusInfo> vsbStatusList = new ArrayList<D2DStatusInfo>();
		vsbDao.as_edge_host_vsb_status_getByHostId(node.getId(), vsbStatusList);
		if (vsbStatusList.size() == 0) {
			return null;
		}
		return vsbStatusList.get(0).getCurrentRunningSnapshot();
	}
	
	@Override
	public void startFailover(Node node, VMSnapshotsInfo vmSnapInfo) throws EdgeServiceFault {
		ID2D4EdgeService monitorService = null;
		String moniteeUUID = null;
		try (D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
			connection.connect();
			monitorService = connection.getService();
			moniteeUUID = getMoniteeUUID(node);
			if (vmSnapInfo.isPowerOnWithIPSettings()) {				
				List<DNSUpdaterParameters> dnsParameters = new ArrayList<DNSUpdaterParameters>();
				List<IPSetting> ipSettings = getIPSettingFromVCM(node, dnsParameters);
				vmSnapInfo.setIpSettings(ipSettings);
				vmSnapInfo.setDnsParameters(dnsParameters);
			}
			monitorService.startFailover(moniteeUUID, vmSnapInfo);
			generateLogForVCMRecoverPointSnapshots(Severity.Information, node, EdgeCMWebServiceMessages.getMessage("startFailoverSuccessful", node.getHostname()));
		} catch (Exception e) {
			logger.error("Failed to start failover.", e);
			generateLogForVCMRecoverPointSnapshots(Severity.Error, node, EdgeCMWebServiceMessages.getMessage("startFailoverFailed", node.getHostname()));
			throw EdgeServiceFault.getFault("", "startFailoverFailed");
		}
	}
	
	public List<IPSetting> getIPSettingFromVCM(Node node, List<DNSUpdaterParameters> dnsParameters) throws EdgeServiceFault {
		List<IPSetting> ipSettings = new ArrayList<IPSetting>();
		List<EdgeNetworkConfiguration> sourceMachineAdapterList = getEdgeNetworkConfigurationListFromDBOrPlan(node);
		if (sourceMachineAdapterList.size() > 0) {
			NetworkInfo networkInfo = getNetworkInfo(node);
			List<EdgeNetworkConfiguration> vmNetworkConfigurationList = new ArrayList<EdgeNetworkConfiguration>();
			hostMgrDao.as_edge_vcm_networkConfiguration_selectById(node.getId(), vmNetworkConfigurationList);
			if (vmNetworkConfigurationList.size() > 0) {
				List<EdgeStandbyVMNetworkInfo> edgeStandbyVMNetworkInfoList = new ArrayList<EdgeStandbyVMNetworkInfo>();
				hostMgrDao.as_edge_vcm_dnsRedirectionSetting_selectById(node.getId(), edgeStandbyVMNetworkInfoList);
				EdgeStandbyVMNetworkInfo dnsRedirectionSetting = null;
				if (edgeStandbyVMNetworkInfoList.size() > 0) {
					dnsRedirectionSetting = edgeStandbyVMNetworkInfoList.get(0);
				}
				int i=0;
				for (;i<vmNetworkConfigurationList.size();i++) {
					// Fix issue 86163: Failed to start the selected recovery point snapshot.
					// Because source machine adapter list is less than vm network configurations, so it will throw ArrayIndexOutOfBound exception
					if (i >= sourceMachineAdapterList.size()) {
						break;
					}
					EdgeNetworkConfiguration sourceConfiguration = sourceMachineAdapterList.get(i);
					EdgeNetworkConfiguration networkConfiguration = vmNetworkConfigurationList.get(i);
					if (networkConfiguration.getIsKeepWithBackup() == 0) {
						networkConfiguration.setIsDHCP(sourceConfiguration.getIsDHCP());
						networkConfiguration.setIpStr(sourceConfiguration.getIpStr());
						networkConfiguration.setGatewayStr(sourceConfiguration.getGatewayStr());
						networkConfiguration.setDnsStr(sourceConfiguration.getDnsStr());
						networkConfiguration.setWinsStr(sourceConfiguration.getWinsStr());
					}
					IPSetting ipSetting = getIPSetting(networkConfiguration);
					
					// DNS Redirection
					if (networkConfiguration.getIsKeepWithBackup() != 0 && networkConfiguration.getIsDHCP() != 0 && dnsParameters != null) {
						String ipStr = ipSetting.getIPAddressToString();
						for (String dns : ipSetting.getDnses()) {
							DNSUpdaterParameters dnsUpdaterParameter = new DNSUpdaterParameters();
							dnsUpdaterParameter.setDns(dns);
							dnsUpdaterParameter.setHostIp(ipStr);
							if (dnsRedirectionSetting != null) {
								dnsUpdaterParameter.setTtl(dnsRedirectionSetting.getTtl());
								dnsUpdaterParameter.setDnsServerType(dnsRedirectionSetting.getDnsServerType());
								if (dnsRedirectionSetting.getDnsServerType() == 0) {									
									dnsUpdaterParameter.setUsername(dnsRedirectionSetting.getDnsUsername());
									if (dnsRedirectionSetting.getDnsPassword() != null) {										
										// AspectJ from D2D side will decrypt all attributes like "password" or "pwd"
										// We don't need this behavior here, so we change "password" to "credential" for avoiding decryption
										// Defect 166142
										dnsUpdaterParameter.setCredential(DaoFactory.getEncrypt().encryptString(dnsRedirectionSetting.getDnsPassword()));
									}
								} else {
									dnsUpdaterParameter.setKeyFile(dnsRedirectionSetting.getKeyFile());
								}
							}
							dnsParameters.add(dnsUpdaterParameter);
						}
					}
					
					if (networkInfo.isConfiguredSameNetwork()) {
						ipSetting.setVirtualNetwork(networkConfiguration.getIsVirtualNameFromPolicy() == 0 ? networkInfo.getNetworkName() : networkConfiguration.getVirtualNetworkName());
						ipSetting.setNicType(networkConfiguration.getIsNICTypeFromPolicy() == 0 ? networkInfo.getNetworkType() : networkConfiguration.getNicTypeName());
					} else {
						if (networkConfiguration.getIsVirtualNameFromPolicy() == 0 && i < networkInfo.getNetworkDiffList().size()) {
							ipSetting.setVirtualNetwork(networkInfo.getNetworkDiffList().get(i).getDiffNetworkName());
						} else {
							ipSetting.setVirtualNetwork(networkConfiguration.getVirtualNetworkName());
						}
						if (networkConfiguration.getIsNICTypeFromPolicy() == 0 && i < networkInfo.getNetworkDiffList().size()) {
							ipSetting.setNicType(networkInfo.getNetworkDiffList().get(i).getDiffNetworkType());
						} else {
							ipSetting.setNicType(networkConfiguration.getNicTypeName());
						}
					}
					ipSettings.add(ipSetting);
				}
				if (!networkInfo.isConfiguredSameNetwork()) {
					for (;i < networkInfo.getNetworkDiffList().size();i++) {
						IPSetting ipSetting = new IPSetting();
						ipSetting.setDhcp(true);
						ipSetting.setVirtualNetwork(networkInfo.getNetworkDiffList().get(i).getDiffNetworkName());
						ipSetting.setNicType(networkInfo.getNetworkDiffList().get(i).getDiffNetworkType());
						ipSettings.add(ipSetting);
					}
				}
			} 
			/*
			 * If user has not configured the VM network setting, we keep the powered on VM behavior as before,
			 * not refresh the IP setting with the source machine adapter info.
			 */
//			else {
//				if (networkInfo.isConfiguredSameNetwork()) {					
//					for (EdgeNetworkConfiguration sourceMachineNetworkAdapterInfo : sourceMachineAdapterList) {
//						IPSetting ip = getIPSetting(sourceMachineNetworkAdapterInfo);
//						ip.setVirtualNetwork(networkInfo.getNetworkName());
//						ip.setNicType(networkInfo.getNetworkType());
//						ipSettings.add(ip);
//					}
//				} else {
//					int diffNetwork = 0;
//					for (EdgeNetworkConfiguration sourceMachineNetworkAdapterInfo : sourceMachineAdapterList) {
//						IPSetting ip = getIPSetting(sourceMachineNetworkAdapterInfo);
//						if (diffNetwork<=sourceMachineAdapterList.size()-1) {
//							ip.setVirtualNetwork(networkInfo.getNetworkDiffList().get(diffNetwork).getDiffNetworkName());
//							ip.setNicType(networkInfo.getNetworkDiffList().get(diffNetwork).getDiffNetworkType());
//						}
//						ipSettings.add(ip);
//						diffNetwork++;
//					}
//				}
//			}
		}
		return ipSettings;
	}
	
	private IPSetting getIPSetting(EdgeNetworkConfiguration adapterInfo) {
		IPSetting ipSetting = new IPSetting();
		ipSetting.setDhcp(adapterInfo.getIsDHCP()==0?true:false);
		List<IPAddressInfo> ipList = new ArrayList<IPAddressInfo>();
		List<Gateway> gatewayList = new ArrayList<Gateway>();
		List<String> dnsList = new ArrayList<String>();
		List<String> winsList = new ArrayList<String>();
		if (!StringUtil.isEmptyOrNull(adapterInfo.getIpStr())) {			
			for (String ipStr : adapterInfo.getIpStr().split(SEMICOLON)) {
				IPAddressInfo ip = new IPAddressInfo();
				String[] ipAddr = ipStr.split(COLON);
				ip.setIp(ipAddr[0]);
				ip.setSubnet(ipAddr[1]);
				ipList.add(ip);
			}
		}
		if (!StringUtil.isEmptyOrNull(adapterInfo.getGatewayStr())) {
			for (String gatewayStr : adapterInfo.getGatewayStr().split(SEMICOLON)) {						
				Gateway gateway = new Gateway();
				gateway.setGatewayAddress(gatewayStr);
				gatewayList.add(gateway);
			}
		}
		if (!StringUtil.isEmptyOrNull(adapterInfo.getDnsStr())) {
			for (String dns : adapterInfo.getDnsStr().split(SEMICOLON)) {
				dnsList.add(dns);
			}
		}
		if (!StringUtil.isEmptyOrNull(adapterInfo.getWinsStr())) {
			for (String win : adapterInfo.getWinsStr().split(SEMICOLON)) {
				winsList.add(win);
			}
		}
		ipSetting.setIpAddresses(ipList);
		ipSetting.setGateways(gatewayList);
		ipSetting.setDnses(dnsList);
		ipSetting.setWins(winsList);
		return ipSetting;
	}
	@Override
	public boolean isFailoverJobFinish(Node node) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
			connection.connect();
			ID2D4EdgeService monitorService = connection.getService();
			String moniteeUUID = getMoniteeUUID(node);
			return monitorService.isFailoverJobFinish(moniteeUUID);
		} catch (Exception e) {
			return false;
		}
	}
	
	private String getMoniteeUUID(Node node) {
		if (node.isVMwareMachine() || node.isHyperVMachine()) {
			List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
			vsbDao.as_edge_vsb_converter_getByHostId(node.getId(), converterList);
			if (converterList.size() == 1) {
				EdgeVCMConnectInfo converter = converterList.get(0);
				if (VCMConverterType.isHbbuConverter(converter.getConverterType())) {
					return node.getVmInstanceUUID();
				}
			}
		}
		String moniteeUuid = getMoniteeUuidByHostId(node.getId());
		if (moniteeUuid != null) {
			return moniteeUuid;
		}
		return node.getD2DUUID();
	}


	@Override
	public ARCFlashNode getARCFlashNodeInfo(Node node) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
			ARCFlashNodesSummary summary = connection.getService().getARCFlashNodesSummary();
			for(ARCFlashNode flashNode : summary.getNodes()) {
				if (node.getD2DUUID().equalsIgnoreCase(flashNode.getUuid())) {
					flashNode.setSelectedServerAccessible(getServerStatus(summary, flashNode));
					return flashNode;
				}
			}
		} catch (Exception e) {
			throw EdgeServiceFault.getFault("", "getARCFlashNodeInfoFailed");
		}
		
		return null;
	}
	
	private void generateLogForVCMRecoverPointSnapshots(Severity severity, Node node, String message) {
		generateLog(severity, node, message, Module.VCMRecoverPointSnapshots);
	}
	
	private long generateLog(Severity severity, Node node, String message, Module module) {
		if(StringUtil.isEmptyOrNull(message))
			return 0;
		String nodeName = (node==null?"":node.getHostname());
		int nodeId = (node == null ? 0 : node.getId());
		ActivityLog log = new ActivityLog();
		log.setNodeName(nodeName);
		log.setHostId(nodeId);
		if(module != null){
			log.setModule(module);
		}
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		
		try {
			return logService.addLog(log);
		} catch (Exception e) {
			logger.error("Error occurs during add activity log",e);
		}
		return 0;
	}
	
	private long generateLogForRegInfo(Severity severity, NodeRegistrationInfo nodeRegInfo, String message, Module module) {
		if(nodeRegInfo == null){
			return generateLog(severity, null, message, module);
		}else {
			Node tempNode = new Node();
			tempNode.setId(nodeRegInfo.getId());
			tempNode.setHostname(nodeRegInfo.getNodeName());
			return generateLog(severity, tempNode, message, module);
		}
	}
	
	private boolean getServerStatus(ARCFlashNodesSummary nodesSummary, ARCFlashNode node) {
		long value = nodesSummary.getServerTime() - node.getLastUpdate(); 
		boolean health = value<(node.getHeartBeatFailoverTimeoutInSecond()*1000) || node.isPaused();
		return health;
	}
	@Override
	public List<Node> getVMRunningList(List<Node> nodeList)
			throws EdgeServiceFault {
		for (Node node : nodeList) {
			ID2D4EdgeService monitorService = null;
			String moniteeUUID = null;
			String currentRunningVMUUID = null;
			try(D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
				connection.connect();
				moniteeUUID = getMoniteeUUID(node);
				monitorService = connection.getService();
				currentRunningVMUUID = monitorService.getCurrentRunningSnapShotGuid(moniteeUUID);
				if (currentRunningVMUUID != null) {
					String failoverJobScriptString =  monitorService.getFailoverJobScript(moniteeUUID);
					FailoverJobScript failoverJobScript = CommonUtil.unmarshal(failoverJobScriptString, FailoverJobScript.class);
					if (failoverJobScript.getFailoverMechanism().get(0) != null) {						
						String runningVMName = failoverJobScript.getFailoverMechanism().get(0).getVirtualMachineDisplayName();
						node.setRunningVMName(runningVMName);
					}
				}
			} catch (Exception e) {
				currentRunningVMUUID = null;
			}
			node.setVMRunning(currentRunningVMUUID != null ? true : false);
		}
		return nodeList;
	}
	
	@Override
	public String getInstalldHbbuServer() throws EdgeServiceFault {
		String localName = "localhost";
		InetAddress localHost;
		try{
			try {
				localHost = InetAddress.getLocalHost();
				localName = localHost.getHostName();
			} catch (UnknownHostException e) {
				logger.error(e);
			}
			
			EdgeRegistryInfo regInfo = CommonUtil.getApplicationRegistryInfo(EdgeApplicationType.vShpereManager);
			
			if (regInfo!=null && regInfo.getAppPath() != null){
				return localName;
			}
		}catch(Exception e){
			logger.error(e);
		}
		return null;
	}

	@Override
	public List<RHAScenario> getScenarioList(RHAControlService controlService) throws EdgeServiceFault {
		return rhaService.getScenarioList(controlService);
	}

	@Override
	public List<RHASourceNode> getSourceNodeList(RHAControlService controlService) throws EdgeServiceFault {
		return rhaService.getSourceNodeList(controlService);
	}

	@Override
	public ImportNodeFromRHAResult importNodeFromRHA(ImportNodeFromRHAParameters parameters) throws EdgeServiceFault {
		return rhaService.importNodeFromRHA(parameters);
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	//   Configure Off-site VCM Converters
	//
	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<OffsiteVCMConverterInfo> getOffsiteVCMConverters(
		List<Integer> specificConverters
		) throws EdgeServiceFault
	{
		try
		{
			List<OffsiteVCMConverterInfo> converterList = new LinkedList<OffsiteVCMConverterInfo>();
			
			List<EdgeVCMConnectInfo> daoInfoList = new LinkedList<EdgeVCMConnectInfo>();
			if ((specificConverters != null) && (specificConverters.size() > 0))
			{
				for (int id : specificConverters)
				{
					daoInfoList.clear();
					this.vsbDao.as_edge_vsb_converter_getById(id, daoInfoList);
					if (daoInfoList.size() > 0)
					{
						EdgeVCMConnectInfo daoInfo = daoInfoList.get( 0 );
						OffsiteVCMConverterInfo info = daoConverterInfoToContractConverterInfo( daoInfo );
						converterList.add( info );
					}
				}
			}
			else // no specific converters
			{
				// Get converters for nodes imported from RHA
				vsbDao.as_edge_vsb_converter_forNodesimportedFromRHA(daoInfoList);
				for (EdgeVCMConnectInfo daoInfo : daoInfoList)
				{
					OffsiteVCMConverterInfo info = daoConverterInfoToContractConverterInfo( daoInfo );
					converterList.add( info );
				}
			}
			
			return converterList;
		}
		catch (Exception e)
		{
			logger.error( "getOffsiteVCMConverters() failed.", e );

			throw EdgeServiceFault.getFault(
				EdgeServiceErrorCode.Common_Service_General, "" );
		}
	}
	
	public static OffsiteVCMConverterInfo daoConverterInfoToContractConverterInfo(EdgeVCMConnectInfo daoInfo) {
		OffsiteVCMConverterInfo info = new OffsiteVCMConverterInfo();
		info.setId(daoInfo.getId());
		info.setHostname(daoInfo.getHostName());
		info.setPort(daoInfo.getPort());
		info.setProtocol(daoInfo.getProtocol());
		info.setUsername(daoInfo.getUserName());
		info.setPassword(daoInfo.getPassword());
		info.setUuid(daoInfo.getUuid());
		return info;
	}
	
	class ConverterUpdateControlInfo
	{
		private boolean isUpdating;
		private boolean shouldBeCancelled;
		
		public boolean isUpdating()
		{
			return isUpdating;
		}
		
		public void setUpdating( boolean isUpdating )
		{
			this.isUpdating = isUpdating;
		}
		
		public boolean isShouldBeCancelled()
		{
			return shouldBeCancelled;
		}
		
		public void setShouldBeCancelled( boolean shouldBeCancelled )
		{
			this.shouldBeCancelled = shouldBeCancelled;
		}
	}
	
	private ConverterUpdateControlInfo GetConverterUpdateControlInfo()
	{
		ConverterUpdateControlInfo controlInfo = (ConverterUpdateControlInfo)
			this.serviceImpl.getSession().getAttribute( CommonUtil.STRING_SESSION_POLICYEDITSESSION );
		if (controlInfo == null)
		{
			controlInfo = new ConverterUpdateControlInfo();
			controlInfo.setUpdating( false );
			controlInfo.setShouldBeCancelled( false );
			this.serviceImpl.getSession().setAttribute( CommonUtil.STRING_SESSION_POLICYEDITSESSION, controlInfo );
		}
		
		return controlInfo;
	}
	
	@Override
	public List<OffsiteVCMConverterSavingStatus> updateOffsiteVCMConverters(
		List<OffsiteVCMConverterInfo> converterInfoList )
		throws EdgeServiceFault
	{
		ConverterUpdateControlInfo controlInfo = GetConverterUpdateControlInfo();
		try
		{
			synchronized (controlInfo)
			{
				controlInfo.setUpdating( true );
				controlInfo.setShouldBeCancelled( false );
			}
			
			List<OffsiteVCMConverterSavingStatus> statusList = new LinkedList<OffsiteVCMConverterSavingStatus>();
			
			for (OffsiteVCMConverterInfo converterInfo : converterInfoList)
			{
				OffsiteVCMConverterSavingStatus status = new OffsiteVCMConverterSavingStatus();
				status.setConverterId( converterInfo.getId() );
				status.setStatus( OffsiteVCMConverterEditingStatus.Updating );
				statusList.add( status );
			}
			
			int i;
			boolean shouldBeCancelled = false;
			boolean isCancelled = false;
			
			for (i = 0; i < converterInfoList.size(); i ++)
			{
				synchronized (controlInfo)
				{
					shouldBeCancelled = controlInfo.isShouldBeCancelled();
				}
				if (shouldBeCancelled)
				{
					isCancelled = true;
					break;
				}
				
				OffsiteVCMConverterInfo converterInfo = converterInfoList.get( i );
				
				OffsiteVCMConverterSavingStatus status =
					getSpecificOffsiteVCMConverterUpdatingStatus( statusList, converterInfo.getId() );
				
				VerifyOffsiteVCMConverterInfoResult result = verifyOffsiteVCMConverterInfo( converterInfo );
				
				if (result.getResult() == VerifyOffsiteVCMConverterInfoResult.Result.OK)
				{
					int[] id = new int[1];
					int[] insert = new int[1];
					vsbDao.as_edge_vsb_converter_cu(0, 0, converterInfo.getHostname(), converterInfo.getPort(),
							converterInfo.getProtocol().ordinal(), converterInfo.getUsername(),
							converterInfo.getPassword(), result.getUuid(), result.getAuthUuid(), id, insert);
					// this.hostMgrDao.udpateOffsiteVCMConverter(
					// converterInfo.getId(),
					// converterInfo.getHostname(),
					// converterInfo.getPort(),
					// converterInfo.getProtocol().ordinal(),
					// converterInfo.getUsername(),
					// converterInfo.getPassword(),
					// result.getUuid() );

					status.setStatus( OffsiteVCMConverterEditingStatus.UpdatedOK );
				}
				else if (result.getResult() == VerifyOffsiteVCMConverterInfoResult.Result.FailedToConnect)
				{
					status.setStatus( OffsiteVCMConverterEditingStatus.UpdatedFailed_Unreachable );
				}
				else if (result.getResult() == VerifyOffsiteVCMConverterInfoResult.Result.UnsatisfiedVersion)
				{
					status.setStatus( OffsiteVCMConverterEditingStatus.UpdatedFailed_UnsatisfiedVersion );
				}
				else if (result.getResult() == VerifyOffsiteVCMConverterInfoResult.Result.FailedToLogin)
				{
					status.setStatus( OffsiteVCMConverterEditingStatus.UpdatedFailed_LoginFailed );
				}
			}
			
			if (isCancelled)
			{
				for (; i < converterInfoList.size(); i ++)
				{
					OffsiteVCMConverterInfo converterInfo = converterInfoList.get( i );
					OffsiteVCMConverterSavingStatus status =
						getSpecificOffsiteVCMConverterUpdatingStatus( statusList, converterInfo.getId() );
					status.setStatus( OffsiteVCMConverterEditingStatus.Cancelled );
				}
			}
			
			return statusList;
		}
		finally
		{
			controlInfo.setUpdating( false );
		}
	}
	
	private OffsiteVCMConverterSavingStatus getSpecificOffsiteVCMConverterUpdatingStatus(
		List<OffsiteVCMConverterSavingStatus> statusList, int converterId )
	{
		for (OffsiteVCMConverterSavingStatus status : statusList)
		{
			if (status.getConverterId() == converterId)
				return status;
		}
		return null;
	}
	
	static class VerifyOffsiteVCMConverterInfoResult
	{
		public enum Result
		{
			OK,
			FailedToConnect,
			UnsatisfiedVersion,
			FailedToLogin,
		}
		
		private Result result = Result.OK;
		private String uuid = "";
		private String authUuid = "";
		
		public Result getResult()
		{
			return result;
		}
		
		public void setResult( Result result )
		{
			this.result = result;
		}
		
		public String getUuid()
		{
			return uuid;
		}
		
		public void setUuid( String uuid )
		{
			this.uuid = uuid;
		}

		public String getAuthUuid() {
			return authUuid;
		}

		public void setAuthUuid(String authUuid) {
			this.authUuid = authUuid;
		}
	}
	
	private VerifyOffsiteVCMConverterInfoResult verifyOffsiteVCMConverterInfo( OffsiteVCMConverterInfo converterInfo ) throws EdgeServiceFault
	{
		int connectTimeout = 30 * 1000; // 30 seconds
		int requestTimeout = 30 * 1000; // 30 seconds
		
		VerifyOffsiteVCMConverterInfoResult result = new VerifyOffsiteVCMConverterInfoResult();
		result.setResult( VerifyOffsiteVCMConverterInfoResult.Result.OK );
		result.setUuid( "" );
		
		// connect D2D
		String protocolString = converterInfo.getProtocol() == Protocol.Http ? "HTTP" : "HTTPS";
		ConnectionContext context = new ConnectionContext(protocolString, converterInfo.getHostname(), converterInfo.getPort());
		context.buildCredential(converterInfo.getUsername(), converterInfo.getPassword(), "");
		GatewayEntity gateway = gatewayService.getGatewayById(converterInfo.getGatewayId());
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context)))
		{
			connection.setConnectTimeout(connectTimeout);
			connection.setRequestTimeout(requestTimeout);
			connection.connect();
			
			VersionInfo d2dVersionInfo = connection.getService().getVersionInfo();
			int d2dMajorVersion = Integer.parseInt( d2dVersionInfo.getMajorVersion() );
			int d2dMinorVersion = Integer.parseInt( d2dVersionInfo.getMinorVersion() );
			int versionNumber = d2dMajorVersion * 10 + d2dMinorVersion;
			if (versionNumber < 50 || versionNumber == 165 || versionNumber == 170)
				throw new Exception( "D2D version is " + d2dMajorVersion + "." + d2dMinorVersion );
			
			String[] usernameParts = parseDomainUsername( converterInfo.getUsername() );
			String domain = usernameParts[0];
			String username = usernameParts[1];
			connection.getService().validateUserAndUpdateIfNeeded(username, converterInfo.getPassword(), domain);
			
			result.setUuid(connection.getNodeUuid());
			result.setAuthUuid(connection.getAuthUuid());
		}
		catch (WebServiceException e)
		{
			logger.error("failed to connect to converter.", e);
			result.setResult( VerifyOffsiteVCMConverterInfoResult.Result.FailedToConnect );
			return result;
		}
		catch (Exception e)
		{
			result.setResult( VerifyOffsiteVCMConverterInfoResult.Result.UnsatisfiedVersion );
			return result;
		}
		
		return result;
	}
	
	// Parse user name into domain name and user name.
	// I.e. parse domain\\username to domain name and user name.
	// Parts[0] is the domain name, and parts[1] is the user name.
	private String[] parseDomainUsername( String username )
	{
		String[] parts = new String[2];
		
		int index = username.indexOf( "\\" );
		if (index != -1)
		{
			parts[0] = username.substring( 0, index );
			parts[1] = username.substring( index + 1 );
		}
		else // no domain name
		{
			parts[0] = "";
			parts[1] = username;
		}
		
		return parts;
	}
	
	@Override
	public void cancelUpdatingOffsiteVCMConverters() throws EdgeServiceFault
	{
		ConverterUpdateControlInfo controlInfo = GetConverterUpdateControlInfo();
		synchronized (controlInfo)
		{
			controlInfo.setShouldBeCancelled( true );
		}
	}
	
	// Following three methods are reserved for update off-site VCM converters
	// asynchronous. Currently, we just use the simple way, the synchronous
	// way to converters. If we'll no longer use the asynchronous way, just
	// delete them.
	//
	// Pang, Bo (panbo01)
	// 2012-07-02

	@Override
	public String updateOffsiteVCMConvertersAsync(
		List<OffsiteVCMConverterInfo> converterInfoList
		) throws EdgeServiceFault
	{
		return null;
	}
	
	@Override
	public List<OffsiteVCMConverterSavingStatus> getOffsiteVCMConverterUpdatingStatus(
		String savingSessionId
		) throws EdgeServiceFault
	{
		return null;
	}
	
	@Override
	public void deleteOffsiteVCMConverterUpdatingSession(
		String savingSessionId
		) throws EdgeServiceFault
	{
	}

	@Override
	public List<RHAControlService> getControlServiceList(String serverNamePrefix) throws EdgeServiceFault {
		try {
			return rhaService.getControlServiceList(serverNamePrefix);
		} catch (Exception e) {
			logger.error("Failed to get control service lsit.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
	}

	@Override
	public OffsiteVCMConverterInfo getOffsiteVCMConverterInfoByHostId(int hostId) throws EdgeServiceFault {
		try {
			return rhaService.getOffsiteVCMConverterInfoByHostId(hostId);
		} catch (Exception e) {
			logger.error("Failed to get the converter information for hostId:" + hostId, e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
	}
	
	@Override
	public List<SourceMachineNetworkAdapterInfo> getSourceMachineNetworkAdapterInfoList(
			Node node) throws EdgeServiceFault {
		List<SourceMachineNetworkAdapterInfo> list = getSourceMachineNetworkAdapterListFromDB(node);
		
		NetworkInfo networkInfo = getNetworkInfo(node);
		
		String[] virtualNetworkArray = null;
		String[] networkTypeArray = null;

		VirtualizationType virtualizationType = networkInfo.getVirtualizationInfo().getVirtualizationType();
		if(virtualizationType == VirtualizationType.HyperV) {
			try (D2DConnection connection = connectionFactory.createD2DConnection(new MonitorConnectionContextProvider(node.getId()))) {
				connection.connect();
				ID2D4EdgeService monitorService = connection.getService();
				virtualNetworkArray = monitorService.getHypervNetworksFromMonitor("", "", "");
				networkTypeArray = monitorService.getHypervNetworkAdapterTypes();
			}
				
		} else {
			DiscoveryESXOption discoveryESXOption;
			ESXNode eNode = new ESXNode();
			
			// should use Monitor's Gateway not host's gateway
			//GatewayEntity gateway = this.gatewayService.getGatewayByHostId( node.getId() );
			GatewayEntity gateway = new GatewayEntity();			
			HostConnectInfo monitor = this.getMonitorConnectInfoByHostId(node.getId());
			if(monitor!=null){
				gateway.setId(monitor.getGatewayId());
			} else {
				logger.error("NodeServiceImpl getSourceMachineNetworkAdapterInfoList cannot getMonitorConnectInfoByHostId hostid="+node.getId());
				gateway = this.gatewayService.getGatewayByHostId( node.getId() );
			}
			
			if(virtualizationType == VirtualizationType.VMwareESX){
				VMwareESX esxServerInfo = (VMwareESX)networkInfo.getVirtualizationInfo();
				discoveryESXOption = getDiscoveryESXOption(gateway.getId(), esxServerInfo.getHostName(),esxServerInfo.getUserName()
											, esxServerInfo.getPassword(),true,esxServerInfo.getPort(),esxServerInfo.getProtocol());
				//get virtual networkList
				eNode.setDataCenter(esxServerInfo.getDataCenter());
				eNode.setEsxName(esxServerInfo.getEsxName());

			} else {
				VMwareVirtualCenter vCenterInfo = (VMwareVirtualCenter)networkInfo.getVirtualizationInfo();
				discoveryESXOption = getDiscoveryESXOption(gateway.getId(), vCenterInfo.getHostName(),vCenterInfo.getUserName()
									, vCenterInfo.getPassword(),true,vCenterInfo.getPort(),vCenterInfo.getProtocol());
				
				//get virtual networkList
				eNode.setDataCenter(vCenterInfo.getDataCenter());
				eNode.setEsxName(vCenterInfo.getEsxName());
			}
			
			discoveryESXOption.setGatewayId( gateway.getId() );
			
			String[] EMPTY = new String[0];
			IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
			IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( discoveryESXOption.getGatewayId() );
			ArrayList<String> networkList = vmwareService.getVirtualNetworkList(discoveryESXOption, eNode);
			vmwareService.close();
				
			if(networkList == null || networkList.size()== 0){
				virtualNetworkArray = EMPTY;
			}
			else {
				virtualNetworkArray = networkList.toArray(EMPTY);
			}	
			//get network adapter
			networkTypeArray = getNetworkAdapterTypeByOs(gateway.getId(), networkInfo.getVirtualizationInfo(), node.getOsDescription(),node.getOsType());
		}
		
		// get saved data, merge with policy network adapter
		List<EdgeNetworkConfiguration> networkConfigurationList = new ArrayList<EdgeNetworkConfiguration>();
		hostMgrDao.as_edge_vcm_networkConfiguration_selectById(node.getId(), networkConfigurationList);
		
		if (networkInfo.isConfiguredSameNetwork()) {
			
			for (int i=0; i<list.size(); i++) {
				SourceMachineNetworkAdapterInfo adapterInfo = list.get(i);
				adapterInfo.setVirtualNetworkList(Arrays.asList(virtualNetworkArray));
				adapterInfo.setNetworkTypeList(Arrays.asList(networkTypeArray));
				adapterInfo.setPolicyVirtualName(networkInfo.getNetworkName());
				adapterInfo.setPolicyNICType(networkInfo.getNetworkType());
				List<IPSettingForADR> savedIpSettings = new ArrayList<IPSettingForADR>();
				IPSettingForADR ipSetting = new IPSettingForADR();
				List<IPAddressInfoForADR> ipList = new ArrayList<IPAddressInfoForADR>();
				List<GatewayForADR> gatewayList = new ArrayList<GatewayForADR>();
				List<WinsForADR> winsList = new ArrayList<WinsForADR>();
				List<DNSForADR> dnsList = new ArrayList<DNSForADR>();
				if (i < networkConfigurationList.size()) {
					adapterInfo.setKeepWithBackup(networkConfigurationList.get(i).getIsKeepWithBackup()==0?true:false);
					adapterInfo.setDefaultVirtualNetwork(networkConfigurationList.get(i).getVirtualNetworkName());
					adapterInfo.setDefaultNetworkType(networkConfigurationList.get(i).getNicTypeName());
					adapterInfo.setVirtualNameFromPolicy(networkConfigurationList.get(i).getIsVirtualNameFromPolicy()==0?true:false);
					adapterInfo.setNICTypeFromPolicy(networkConfigurationList.get(i).getIsNICTypeFromPolicy()==0?true:false);
					
					ipSetting.setDhcp(networkConfigurationList.get(i).getIsDHCP()==0?true:false);
					if (networkConfigurationList.get(i).getDnsStr() != null) {
						for (String dns : networkConfigurationList.get(i).getDnsStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(dns)) {								
								DNSForADR dnsADR = new DNSForADR();
								dnsADR.setDns(dns);
								dnsList.add(dnsADR);
							}
						}
					}
					if (networkConfigurationList.get(i).getGatewayStr() != null) {
						for (String gatewayStr : networkConfigurationList.get(i).getGatewayStr().split(SEMICOLON)) {						
							if (!StringUtil.isEmptyOrNull(gatewayStr)) {									
								GatewayForADR gateway = new GatewayForADR();
								gateway.setGatewayAddress(gatewayStr);
								gatewayList.add(gateway);
							}
						}
					}
					if (networkConfigurationList.get(i).getIpStr() != null) {
						for (String ipStr : networkConfigurationList.get(i).getIpStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(ipStr)) {								
								IPAddressInfoForADR ip = new IPAddressInfoForADR();
								String[] ipAddr = ipStr.split(COLON);
								ip.setIp(ipAddr[0]);
								ip.setSubnet(ipAddr[1]);
								ipList.add(ip);
							}
						}
					}
					if (networkConfigurationList.get(i).getWinsStr() != null) {
						for (String win : networkConfigurationList.get(i).getWinsStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(win)) {								
								WinsForADR winADR = new WinsForADR();
								winADR.setWins(win);
								winsList.add(winADR);
							}
						}
					}
				} else {
					adapterInfo.setDefaultVirtualNetwork(networkInfo.getNetworkName());
					adapterInfo.setDefaultNetworkType(networkInfo.getNetworkType());
					adapterInfo.setVirtualNameFromPolicy(true);
					adapterInfo.setNICTypeFromPolicy(true);
				}
				ipSetting.setIps(ipList);
				ipSetting.setGateways(gatewayList);
				ipSetting.setDnses(dnsList);
				ipSetting.setWins(winsList);
				savedIpSettings.add(ipSetting);
				adapterInfo.setSavedIpSettings(savedIpSettings);
			}
		} else {
			for (int i=0; i<list.size(); i++) {
				SourceMachineNetworkAdapterInfo adapterInfo = list.get(i);
				adapterInfo.setVirtualNetworkList(Arrays.asList(virtualNetworkArray));
				adapterInfo.setNetworkTypeList(Arrays.asList(networkTypeArray));
				List<IPSettingForADR> savedIpSettings = new ArrayList<IPSettingForADR>();
				IPSettingForADR ipSetting = new IPSettingForADR();
				List<IPAddressInfoForADR> ipList = new ArrayList<IPAddressInfoForADR>();
				List<GatewayForADR> gatewayList = new ArrayList<GatewayForADR>();
				List<DNSForADR> dnsList = new ArrayList<DNSForADR>();
				List<WinsForADR> winsList = new ArrayList<WinsForADR>();
				
				if (i < networkInfo.getNetworkDiffList().size()) {
					adapterInfo.setPolicyVirtualName(networkInfo.getNetworkDiffList().get(i).getDiffNetworkName());
					adapterInfo.setPolicyNICType(networkInfo.getNetworkDiffList().get(i).getDiffNetworkType());
				} 

				if (i < networkConfigurationList.size()) {
					adapterInfo.setKeepWithBackup(networkConfigurationList.get(i).getIsKeepWithBackup()==0?true:false);
					adapterInfo.setDefaultVirtualNetwork(networkConfigurationList.get(i).getVirtualNetworkName());
					adapterInfo.setDefaultNetworkType(networkConfigurationList.get(i).getNicTypeName());
					adapterInfo.setVirtualNameFromPolicy(networkConfigurationList.get(i).getIsVirtualNameFromPolicy()==0?true:false);
					adapterInfo.setNICTypeFromPolicy(networkConfigurationList.get(i).getIsNICTypeFromPolicy()==0?true:false);
					ipSetting.setDhcp(networkConfigurationList.get(i).getIsDHCP()==0?true:false);
					if (networkConfigurationList.get(i).getDnsStr() != null) {
						for (String dns : networkConfigurationList.get(i).getDnsStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(dns)) {									
								DNSForADR dnsADR = new DNSForADR();
								dnsADR.setDns(dns);
								dnsList.add(dnsADR);
							}
						}
					}
					if (networkConfigurationList.get(i).getGatewayStr() != null) {
						for (String gatewayStr : networkConfigurationList.get(i).getGatewayStr().split(SEMICOLON)) {		
							if (!StringUtil.isEmptyOrNull(gatewayStr)) {									
								GatewayForADR gateway = new GatewayForADR();
								gateway.setGatewayAddress(gatewayStr);
								gatewayList.add(gateway);
							}
						}
					}
					if (networkConfigurationList.get(i).getIpStr() != null) {							
						for (String ipStr : networkConfigurationList.get(i).getIpStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(ipStr)) {										
								IPAddressInfoForADR ip = new IPAddressInfoForADR();
								String[] ipAddr = ipStr.split(COLON);
								ip.setIp(ipAddr[0]);
								ip.setSubnet(ipAddr[1]);
								ipList.add(ip);
							}
						}
					}
					if (networkConfigurationList.get(i).getWinsStr() != null) {
						for (String win : networkConfigurationList.get(i).getWinsStr().split(SEMICOLON)) {
							if (!StringUtil.isEmptyOrNull(win)) {									
								WinsForADR winADR = new WinsForADR();
								winADR.setWins(win);
								winsList.add(winADR);
							}
						}
					}
				} else {
					if (i < networkInfo.getNetworkDiffList().size()) {						
						adapterInfo.setDefaultVirtualNetwork(networkInfo.getNetworkDiffList().get(i).getDiffNetworkName());
						adapterInfo.setDefaultNetworkType(networkInfo.getNetworkDiffList().get(i).getDiffNetworkType());
					} 
					adapterInfo.setVirtualNameFromPolicy(true);
					adapterInfo.setNICTypeFromPolicy(true);
				}

				ipSetting.setIps(ipList);
				ipSetting.setGateways(gatewayList);
				ipSetting.setDnses(dnsList);
				ipSetting.setWins(winsList);
				savedIpSettings.add(ipSetting);
				adapterInfo.setSavedIpSettings(savedIpSettings);
			}
		}
		return list;
	}
	
	private DiscoveryESXOption getDiscoveryESXOption( GatewayId gatewayId, String hostName,String userName,String password , Boolean ignoreCertificate ,
			int port,String protocal) {
		DiscoveryESXOption eCenterOption = new DiscoveryESXOption();
		eCenterOption.setEsxHost(hostName);
		eCenterOption.setEsxServerName(hostName);
		eCenterOption.setEsxUserName(userName);
		eCenterOption.setEsxPassword(password);
		eCenterOption.setIgnoreCertificate(true);
		eCenterOption.setPort(port);
		eCenterOption.setProtocol(protocal.equalsIgnoreCase("Http")?Protocol.Http:Protocol.Https);
		eCenterOption.setGatewayId( gatewayId );
		
		return eCenterOption;
	}
	
//	public CAVirtualInfrastructureManager getVmwareManager(String hostName,String userName,String password , Boolean ignoreCertificate ,
//			int port,String protocal) throws EdgeServiceFault{
//		DiscoveryESXOption eCenterOption = new DiscoveryESXOption();
//		eCenterOption.setEsxHost(hostName);
//		eCenterOption.setEsxServerName(hostName);
//		eCenterOption.setEsxUserName(userName);
//		eCenterOption.setEsxPassword(password);
//		eCenterOption.setIgnoreCertificate(true);
//		eCenterOption.setPort(port);
//		eCenterOption.setProtocol(protocal.equalsIgnoreCase("Http")?Protocol.Http:Protocol.Https);
//		return VMwareManagerAdapter.getInstance().createVMWareManager(eCenterOption);
//	}
	
	public String[] getNetworkAdapterTypeByOs(GatewayId gatewayId, Virtualization virtualizationInfo , String osDesc , String osType)throws EdgeServiceFault{
		ESXNode eNode = new ESXNode();
		DiscoveryESXOption dEsxOption = null;

		if(virtualizationInfo.getVirtualizationType()==VirtualizationType.VMwareESX){
			VMwareESX esxServerInfo = (VMwareESX)virtualizationInfo;
				
			dEsxOption = getDiscoveryESXOption(gatewayId, esxServerInfo.getHostName(),esxServerInfo.getUserName(),esxServerInfo.getPassword(), true,esxServerInfo.getPort(),esxServerInfo.getProtocol());
				
			eNode.setDataCenter(esxServerInfo.getDataCenter());
			eNode.setEsxName(esxServerInfo.getEsxName());
			
		}else if(virtualizationInfo.getVirtualizationType()==VirtualizationType.VMwareVirtualCenter){
			VMwareVirtualCenter vCenterInfo = (VMwareVirtualCenter)virtualizationInfo;
				
			dEsxOption = getDiscoveryESXOption(gatewayId, vCenterInfo.getHostName(),vCenterInfo.getUserName(), vCenterInfo.getPassword(),true,vCenterInfo.getPort(),vCenterInfo.getProtocol());
				
			eNode.setDataCenter(vCenterInfo.getDataCenter());
			eNode.setEsxName(vCenterInfo.getEsxName());
		}
			
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( dEsxOption.getGatewayId() );
		List<String> os_nicTypeList = vmwareService.getNetworkAdapterTypeByOs(dEsxOption,eNode);
		vmwareService.close();
		
		String[] result = null;
		result = getNicListByOsFromMap(os_nicTypeList , osDesc,osType);
		if(result==null || result.length==0){
			result = getAllNicListFromMap(os_nicTypeList); //when osdesc or ostype is empty , then return all the nic type.
		}
		return result;	
	}
	
	private String[] getNicListByOsFromMap(List<String>os_nicTypeList,String osDesc , String osType){
		String[] EMPTY = new String[0];
		if(os_nicTypeList==null || os_nicTypeList.size()==0)
			return EMPTY;
		
		List<String> result = new ArrayList<String>();
		for(String os_nic : os_nicTypeList){
			if(os_nic.contains("Microsoft")){
				String os_name;
				String os_type;
				int indexBit = os_nic.indexOf("(");
				if(indexBit == -1){ //some os have no ostype
					os_name = os_nic.substring(0,os_nic.indexOf("+"));
					if(osDesc!=null){
						if(osDesc.contains(os_name)){
							String[] tempNics = os_nic.substring(os_nic.indexOf("+")+1).split(",");
							for(String tempNic : tempNics){
								result.add(tempNic.substring(tempNic.indexOf("Virtual")+7).toUpperCase().trim());
							}
							break;
						}
					}
				}else {
					os_name = os_nic.substring(os_nic.indexOf("Microsoft")+10,indexBit).trim(); //os_name : Windows Server 2012
					os_type = os_nic.substring(indexBit+1,os_nic.indexOf(")")); //os_type :64-bit
					if(osDesc!=null && osType!=null){
						if(osDesc.contains(os_name)&& os_type.contains(osType)){
							String[] tempNics = os_nic.substring(os_nic.indexOf("+")+1).split(",");
							for(String tempNic : tempNics){
								result.add(tempNic.substring(tempNic.indexOf("Virtual")+7).toUpperCase().trim());
							}
							break;
						}
					}
				}		
			}
		}
		// The Flexible network adapter identifies itself as a Vlance(PCNET32) adapter 
		//when a virtual machine boots, but initializes itself and functions as either a Vlance or a VMXNET adapter
		if(result.contains("PCNET32")||result.contains("VMXNET")){
			result.remove("PCNET32");
			result.remove("VMXNET");
			result.add("Flexible");
		}
		return result.toArray(EMPTY);
	}
	
	private String[] getAllNicListFromMap(List<String>os_NicTypesMap){
		List<String> result = new ArrayList<String>();
		if(os_NicTypesMap!=null){
			for(String os_nic : os_NicTypesMap){
				String[] tempNic = os_nic.substring(os_nic.indexOf("+")+1).split(",");
				for(String nic : tempNic){
					//nic like: VirtualE1000e , should be E1000E
					String nicType = nic.substring(nic.indexOf("Virtual")+7).toUpperCase().trim();
					if(!result.contains(nicType))
						result.add(nicType);
				}
			}
		}
		if(result.contains("PCNET32")||result.contains("VMXNET")){
			result.remove("PCNET32");
			result.remove("VMXNET");
			result.add("Flexible");
		}
		return result.toArray(new String[result.size()]);
	}
	
	private NetworkInfo getNetworkInfo(Node node) throws EdgeServiceFault {
		ConversionTask conversionTask = getConversionTaskForNode(node);
		if (conversionTask == null) {
			throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.NODE_VCM_CANNOT_READ_POLICY,
					"Failed to read vsb task"));
		}
		NetworkInfo networkInfo = new NetworkInfo();
		JobScriptCombo jobScript = conversionTask.getConversionJobScript();
		List<Virtualization> virtualizationList = jobScript.getFailoverJobScript().getFailoverMechanism();
		networkInfo.setConfiguredSameNetwork(jobScript.getFailoverJobScript().isConfiguredSameNetwork());
		for (Virtualization virtualization : virtualizationList) {
			networkInfo.setVirtualizationInfo(virtualization);
			List<NetworkDiffInfo> networkDiffInfoList = new ArrayList<NetworkDiffInfo>();
			for (NetworkAdapter networkAdapter : virtualization.getNetworkAdapters()) {
				if (jobScript.getFailoverJobScript().isConfiguredSameNetwork()) {
					networkInfo.setNetworkName(networkAdapter.getNetworkLabel());
					networkInfo.setNetworkType(networkAdapter.getAdapterType());
				} else {
					NetworkDiffInfo networkDiffInfo = new NetworkDiffInfo();
					networkDiffInfo.setDiffNetworkName(networkAdapter.getNetworkLabel());
					networkDiffInfo.setDiffNetworkType(networkAdapter.getAdapterType());
					networkDiffInfoList.add(networkDiffInfo);
				}
			}
			networkInfo.setNetworkDiffList(networkDiffInfoList);
		}
		return networkInfo;
	}
	
	private ConversionTask getConversionTaskForNode(Node node) throws EdgeServiceFault {
		if (node == null) {
			return null;
		}
		long planId = node.getPolicyIDForEsx();
		if (planId <= 0) {
			logger.error("The ndoe has no plan asigned.");
			return null;
		}
		PolicyManagementServiceImpl policyService = PolicyManagementServiceImpl.getInstance();
		UnifiedPolicy plan = policyService.loadUnifiedPolicyById((int) planId);
		if (plan == null) {
			logger.error("Failed to load plan, the plan object is null.");
			return null;
		}
		return plan.getConversionConfiguration();
	}

	private List<EdgeNetworkConfiguration> getEdgeNetworkConfigurationListFromDBOrPlan(Node node) throws EdgeServiceFault {
		//TODO: we need consider both Bug 751385 and Zendesk 28913
		// load source machine adapter from VCM database
		List<EdgeNetworkConfiguration> sourceMachineAdapter = new ArrayList<EdgeNetworkConfiguration>();
		this.hostMgrDao.as_edge_vcm_sourceMachineAdapter_selectById(node.getId(), sourceMachineAdapter);
		if (sourceMachineAdapter.size() == 0) {
			logger.info("The source machine adapter doesn't exists in database, we try to get it from policy.");
			ConversionTask conversionTask = getConversionTaskForNode(node);
			if (conversionTask != null) {
				JobScriptCombo jobScript = conversionTask.getConversionJobScript();
				if (jobScript != null) {
					FailoverJobScript failoverJobScript = jobScript.getFailoverJobScript();
					List<Virtualization> virtualList = failoverJobScript.getFailoverMechanism();
					ADRConfigure adrConfigInfo = new ADRConfigure();
					SortedSet<NetworkAdapter> netadapters = new TreeSet<NetworkAdapter>();
					if (virtualList != null) {
						for (Virtualization virtualization : virtualList) {
							List<NetworkAdapter> networkAdapter = virtualization.getNetworkAdapters();
							if (networkAdapter != null) {
								for (NetworkAdapter adapter : networkAdapter) {
									netadapters.add(adapter);
								}
							}
						}
					}
					adrConfigInfo.setNetadapters(netadapters);
					adrConfigInfo.setNetworkAdapterInfoFromPolicy(true);
					sourceMachineAdapter = NetworkAdapterInfoUtil
							.getEdgeNetworkConfigurationFromADRConfigure(adrConfigInfo);
				}
				if (sourceMachineAdapter.size() == 0) {
					throw new EdgeServiceFault("", new EdgeServiceFaultBean(
							EdgeServiceErrorCode.NODE_VCM_SOURCE_MACHINE_ADAPTER_NOT_EXIST, "Can't fetch ADRconfigue"));
				}
			}
		}
		return sourceMachineAdapter;
	}

	private List<SourceMachineNetworkAdapterInfo> getSourceMachineNetworkAdapterListFromDB(Node node)
			throws EdgeServiceFault {
		List<SourceMachineNetworkAdapterInfo> list = new ArrayList<SourceMachineNetworkAdapterInfo>();
		// load source machine adapter from VCM database or plan
		List<EdgeNetworkConfiguration> sourceMachineAdapter = getEdgeNetworkConfigurationListFromDBOrPlan(node);
		for (EdgeNetworkConfiguration adapter : sourceMachineAdapter) {
			list.add(NetworkAdapterInfoUtil.convertEdgeNetworkConfigurationToSourceMachineNetworkAdapterInfo(adapter));
		}
		return list;
	}

	@Override
	public void saveSourceMachineNetworkAdapterInfo(Node node,
			List<SourceMachineNetworkAdapterInfo> networkAdapterList)
			throws EdgeServiceFault {
		hostMgrDao.as_edge_vcm_networkConfiguration_deleteByHostId(node.getId());
		for (SourceMachineNetworkAdapterInfo adapterInfo : networkAdapterList) {
			StringBuilder ipStr = new StringBuilder();
			StringBuilder gatewayStr = new StringBuilder();
			StringBuilder dnsStr = new StringBuilder();
			StringBuilder winsStr = new StringBuilder();
			int ipSize = 0;
			if (!adapterInfo.isKeepWithBackup()) {
				ipSize = adapterInfo.getSavedIpSettings().get(0).getIps().size();
				for (int i=0;i<ipSize;i++) {
					IPAddressInfoForADR ip = adapterInfo.getSavedIpSettings().get(0).getIps().get(i);
					ipStr.append(ip.getIp()).append(COLON).append(ip.getSubnet());
					if (i<ipSize-1) {
						ipStr.append(SEMICOLON);
					}
				}
				int gatewaySize = adapterInfo.getSavedIpSettings().get(0).getGateways().size();
				for (int j=0;j<gatewaySize;j++) {
					GatewayForADR gateway = adapterInfo.getSavedIpSettings().get(0).getGateways().get(j);
					gatewayStr.append(gateway.getGatewayAddress());
					if (j<gatewaySize-1) {
						gatewayStr.append(SEMICOLON);
					}
				}
				int dnsSize = adapterInfo.getSavedIpSettings().get(0).getDnses().size();
				for (int m=0;m<dnsSize;m++) {
					DNSForADR dns = adapterInfo.getSavedIpSettings().get(0).getDnses().get(m);
					dnsStr.append(dns.getDns());
					if (m<dnsSize-1) {
						dnsStr.append(SEMICOLON);
					}
				}
				int winsSize = adapterInfo.getSavedIpSettings().get(0).getWins().size();
				for (int n=0;n<winsSize;n++) {
					WinsForADR wins = adapterInfo.getSavedIpSettings().get(0).getWins().get(n);
					winsStr.append(wins.getWins());
					if (n<winsSize-1) {
						winsStr.append(SEMICOLON);
					}
				}
			}
			hostMgrDao.as_edge_vcm_networkConfiguration_saveOrUpdate(
					node.getId(), adapterInfo.getMacAddress(),
					adapterInfo.getDefaultVirtualNetwork(),
					adapterInfo.isVirtualNameFromPolicy() ? 0 : 1,
					adapterInfo.getDefaultNetworkType(),
					adapterInfo.isNICTypeFromPolicy() ? 0 : 1,
					ipSize == 0 ? 0 : 1, // no IP means DHCP enabled.
					adapterInfo.isKeepWithBackup() ? 0 : 1,
					ipStr.toString(), gatewayStr.toString(), dnsStr.toString(), winsStr.toString());
		}
	}
	@Override
	public int getSourceMachineNetworkAdapterSize(Node node)
			throws EdgeServiceFault {
		List<EdgeNetworkConfiguration> networkConfigurationList = new ArrayList<EdgeNetworkConfiguration>();
		hostMgrDao.as_edge_vcm_networkConfiguration_selectById(node.getId(), networkConfigurationList);
//		for (EdgeNetworkConfiguration edgeNetworkConfiguration : networkConfigurationList) {
//			if (edgeNetworkConfiguration.getIsKeepWithBackup() == 1 || edgeNetworkConfiguration.getIsNICTypeFromPolicy() == 1
//					|| edgeNetworkConfiguration.getIsVirtualNameFromPolicy() == 1)
//				return 1;
//		}
//		return 0;
		return networkConfigurationList.size();
	}
	
	@Override
	public StandbyVMNetworkInfo getStandbyVMNetworkInfo(Node node) throws EdgeServiceFault {
		List<SourceMachineNetworkAdapterInfo> sourceMachineNetworkAdapterInfo = this.getSourceMachineNetworkAdapterInfoList(node);
		StandbyVMNetworkInfo standbyVMNetworkInfo = new StandbyVMNetworkInfo();
		standbyVMNetworkInfo.setSourceMachineNetworkAdapterInfo(sourceMachineNetworkAdapterInfo);
		List<EdgeStandbyVMNetworkInfo> edgeStandbyVMNetworkInfoList = new ArrayList<EdgeStandbyVMNetworkInfo>();
		hostMgrDao.as_edge_vcm_dnsRedirectionSetting_selectById(node.getId(), edgeStandbyVMNetworkInfoList);
		if (edgeStandbyVMNetworkInfoList.size() > 0) {
			EdgeStandbyVMNetworkInfo dnsRedirectionSetting = edgeStandbyVMNetworkInfoList.get(0);
			standbyVMNetworkInfo.setTtl(dnsRedirectionSetting.getTtl());
			standbyVMNetworkInfo.setDnsServerType(dnsRedirectionSetting.getDnsServerType());
			standbyVMNetworkInfo.setDnsUsername(dnsRedirectionSetting.getDnsUsername());
			standbyVMNetworkInfo.setDnsPassword(dnsRedirectionSetting.getDnsPassword());
			standbyVMNetworkInfo.setKeyFile(dnsRedirectionSetting.getKeyFile());
		}
		return standbyVMNetworkInfo;
	}
	@Override
	public void saveStandbyVMNetworkInfo(Node node, StandbyVMNetworkInfo standbyVMNetworkInfo) throws EdgeServiceFault {
		this.saveSourceMachineNetworkAdapterInfo(node, standbyVMNetworkInfo.getSourceMachineNetworkAdapterInfo());
		hostMgrDao.as_edge_vcm_dnsRedirectionSetting_save(node.getId(), standbyVMNetworkInfo.getTtl(), standbyVMNetworkInfo.getDnsServerType(),
				standbyVMNetworkInfo.getDnsUsername(), standbyVMNetworkInfo.getDnsPassword(), standbyVMNetworkInfo.getKeyFile());
		// Update standby vm network information on converter
		List<Integer> hostIdList = new ArrayList<Integer>();
		hostIdList.add(node.getId());
		VSBTaskDeployment vsbTaskDeployer = new VSBTaskDeployment();
		vsbTaskDeployer.updateIPSettingsToConverter(hostIdList);
	}

	@Override
	public List<D2DMergeJobStatus> getMergeJobStatus(List<Integer> nodeIds) throws EdgeServiceFault {
		List<D2DMergeJobStatus> statusList = new ArrayList<D2DMergeJobStatus>();
		if (nodeIds == null) {
			return statusList;
		}
		
		for (Integer id : nodeIds) {
			MergeStatus cachedStatus = D2DMergeJobStatusCache.getInstance().get(id);
			if (cachedStatus == null) {
				continue;
			}
			
			D2DMergeJobStatus status = new D2DMergeJobStatus();
			status.setNodeId(id);
			status.setStatus(cachedStatus);
			statusList.add(status);
		}
		
		return statusList;
	}
	
	@Override
	public D2DMergeJobStatus getMergeJobStatusById(int nodeId) throws EdgeServiceFault {
		D2DMergeJobStatus status = new D2DMergeJobStatus();
		
		status.setNodeId(nodeId);
		status.setStatus(D2DMergeJobStatusCache.getInstance().get(nodeId));
		
		return status;
	}
	
	@Override
	public int pauseMergeJob(int nodeId) throws EdgeServiceFault {
		return MergeJobManager.getInstance().changeMergeJob(nodeId, false);
	}
	
	@Override
	public int resumeMergeJob(int nodeId) throws EdgeServiceFault {
		return MergeJobManager.getInstance().changeMergeJob(nodeId, true);
	}
	
	@Override
	public void pauseMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault {
		MergeJobManager.getInstance().pauseMultipleMergeJob(nodeIds);
	}
	
	@Override
	public void resumeMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault {
		MergeJobManager.getInstance().resumeMultipleMergeJob(nodeIds);
	}
	
	@Override
	public int pauseVMMergeJob(int vmHostId) throws EdgeServiceFault {
		return MergeJobManager.getInstance().changeVMMergeJob(vmHostId, false);
	}
	
	@Override
	public int resumeVMMergeJob(int vmHostId) throws EdgeServiceFault {
		return MergeJobManager.getInstance().changeVMMergeJob(vmHostId, true);
	}
	
	@Override
	public void pauseMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault {
		MergeJobManager.getInstance().pauseMultipleVMMergeJob(vmHostIds);
	}
	
	@Override
	public void resumeMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault {
		MergeJobManager.getInstance().resumeMultipleVMMergeJob(vmHostIds);
	}
	
	@Override
	public void specifyESXServerForRVCM(int hostID, DiscoveryESXOption esxSetting, boolean isForceSave) throws EdgeServiceFault {
		if (EdgeWebServiceContext.getApplicationType() != EdgeApplicationType.VirtualConversionManager){
			logger.error("specifyESXServerForRVCM mothed is only valid for VCM");
			return;
		}
		
		NodeDetail nodeDetail = this.getNodeDetailInformation(hostID);
		if (nodeDetail == null || ! nodeDetail.isImportedFromRHA()) {
			logger.error("get node info failed or it is not RVCM node.");
			return;
		}
		
		MachineDetail machineDetail = new MachineDetail();
		machineDetail.setMachineType(MachineType.ESX_VM);
		machineDetail.setHostName(nodeDetail.getHostname());
		machineDetail.setHypervisorHostName(esxSetting.getEsxServerName());
		machineDetail.setHypervisorUserName(esxSetting.getEsxUserName());
		machineDetail.setHypervisorPassword(esxSetting.getEsxPassword());
		machineDetail.setHypervisorProtocol(esxSetting.getProtocol().toString());
		machineDetail.setHypervisorPort(esxSetting.getPort());
		machineDetail.setESXHostName(esxSetting.getEsxHost());
		machineDetail.setInstanceUuid(nodeDetail.getD2DUUID());
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(hostID)) {
			connection.connect();
			connection.getService().specifyESXServerForRVCM(machineDetail, nodeDetail.getRecoveryPointFolder(), isForceSave);
		} catch (SOAPFaultException e) {
			logger.error("fail to save esx server setting for RVCM node",e);
			SOAPFaultException exception = (SOAPFaultException) e;
			if (FlashServiceErrorCode.VCM_MACHINE_ISNOT_ESXVM.equals(exception.getFault().getFaultCodeAsQName().getLocalPart())){
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(FlashServiceErrorCode.VCM_MACHINE_ISNOT_ESXVM , ""));
			}else if (FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO.equals(exception.getFault().getFaultCodeAsQName().getLocalPart())){
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(FlashServiceErrorCode.VCM_FAILED_GET_VM_BACKUPINFO , ""));
			}else
				throw new EdgeServiceFault("", new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_General , ""));
		} catch (WebServiceException e) {
			logger.error("fail to save esx server setting for RVCM node",e);
			if(e.getCause()!=null &&  e.getCause() instanceof SocketTimeoutException){
				EdgeServiceFault fault = EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_UPDATE_VMWARED2D_ESX_TIMEOUT, "call d2d webservice timeout");
				throw fault;
			}else
				throw e;
		}
		
		// save to db
		int[] output = new int[1];
		esxDao.as_edge_esx_update(esxSetting.getId(), esxSetting.getEsxServerName(), "","", 
				esxSetting.getProtocol().ordinal(), esxSetting.getPort(),0, 0,"","", output);		
		saveVMToDB(esxSetting.getGatewayId().getRecordId(), output[0], hostID,machineDetail.getInstanceUuid(),machineDetail.getHostName(),""
				,UUID.randomUUID().toString(),"","","","","",0,0,0,"",true);
	}
	
	@Override
	public List<SessionPassword> getSessionPasswordForHost(int hostId) throws EdgeServiceFault {
		return getSessionPasswordService().getSessionPasswordForHost(hostId);
	}

	@Override
	public void saveSessionPassword(List<Integer> hostIdList, List<SessionPassword> passwordList,
			boolean override) throws EdgeServiceFault {
		getSessionPasswordService().saveSessionPassword(hostIdList, passwordList, override);
	}

	public ISessionPasswordService getSessionPasswordService() {
		if (sessionPasswordService == null) {
			sessionPasswordService = new SessionPasswordServiceImpl();
		}
		return sessionPasswordService;
	}

	@Override
	public List<Node> getSRMNodes() throws EdgeServiceFault {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		List<Node> nodes = new LinkedList<Node>();
		
		hostMgrDao.as_edge_host_list_for_srm(0, hosts);
		for(EdgeHost h : hosts){
			nodes.add(convertDaoNode2ContractNode(h));
		}
		return nodes;
	}
	
	@Override
	public void importHyperVVMs(DiscoveryHyperVOption hyperVOption, VMRegistrationInfo[] vms, ImportNodeType type, boolean addEsxToADList) throws EdgeServiceFault {
		String jobID = java.util.UUID.randomUUID().toString();
		JobMonitor monitor = JobMonitorManager.getInstance().getJobMonitor(jobID, ImportNodesJobMonitor.class);
		if(monitor == null){
			logger.error("Job monitor is null in NodeServiceImpl.");
			return;
		}
		synchronized (monitor) {
			try {
				ImportVMsHyperVJob job = new ImportVMsHyperVJob();
				job.setId(jobID);
				job.schedule(job.createJobDetail(hyperVOption, vms, type, addEsxToADList, this));
			} catch (Throwable e) {
				logger.error("importVMsFromHperV()", e);
			}
		}
	}
	
	private GatewayId getValidateHypervGateWayId(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault{
		//invalid gateway;
		GatewayId gateWayId = null;
		if(!hyperVOption.getGatewayId().isValid()){
			int id = hyperVOption.getId();
			gateWayId = gatewayService.getGatewayByEntityId(id, EntityType.HyperVServer).getId();  ////hyperv use entitytype = 3;! 
		}
		else {
			gateWayId = hyperVOption.getGatewayId();
		}
		return gateWayId;
	}
	@Override
	public HypervProtectionType getHyperVProtectionType(DiscoveryHyperVOption hyperVOption)throws EdgeServiceFault {
		return HyperVManagerAdapter.getInstance().getHyperVProtectionType( getValidateHypervGateWayId(hyperVOption), hyperVOption.getServerName(), hyperVOption.getUsername(),
				hyperVOption.getPassword() );
	}
	
	@Override
	public List<DiscoveryVirtualMachineInfo> getHypervVMList(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		return HyperVManagerAdapter.getInstance().getHypervVMList( getValidateHypervGateWayId( hyperVOption ),
				hyperVOption.getServerName(), hyperVOption.getUsername(), hyperVOption.getPassword(),hyperVOption.getHypervProtectionType());
	}
	
	@Override
	public List<Task> getTaskList() throws EdgeServiceFault {
		return TaskMonitor.getTaskList();
	}

	@Override
	public void deleteTask( Integer taskID ) throws EdgeServiceFault {
		TaskMonitor.deleteTask( taskID );
	}
	
	@Override
	public int saveNodeFilters(NodeFilterGroup filterGroup) throws EdgeServiceFault {
		if (filterGroup.getName() == null || filterGroup.getName().isEmpty() || filterGroup.getNodeFilter() == null || filterGroup.getName().length() > 64) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters");
		}
		
		int[] exist = new int[1];
		hostMgrDao.as_edge_node_filter_exist(filterGroup.getId(), filterGroup.getName(), exist);
		if (exist[0] == 1) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_Filter_NameExist, "The node filter has already existed.");
		}
		
		String filterXml = null;
		
		try {
			filterXml = CommonUtil.marshal(filterGroup.getNodeFilter());
		} catch (JAXBException e) {
			logger.error("marshal node filter failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "marshal node filter failed.");
		}
		
		int[] newFilterId = new int[1];
		hostMgrDao.as_edge_node_filter_update(filterGroup.getId(), filterGroup.getName(), filterXml, newFilterId);
		return filterGroup.getId() == 0 ? newFilterId[0] : filterGroup.getId();
	}
	
	@Override
	public void deleteFilter(int id) throws EdgeServiceFault {
		hostMgrDao.as_edge_filter_delete(id);
	}

	@Override
	public HostConnectInfo getVCMConverterByHostId(int hostId) throws EdgeServiceFault {
		List<EdgeVCMConnectInfo> converterList = new ArrayList<EdgeVCMConnectInfo>();
		vsbDao.as_edge_vsb_converter_getByHostId(hostId, converterList);
		if (converterList.size() > 0) {
			EdgeVCMConnectInfo converter = converterList.get(0);
			if (converter.getHostId() > 0) {
				List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
				connectionInfoDao.as_edge_connect_info_list(converter.getHostId(), infos);
				if (infos.size() > 0) {
					HostConnectInfo newConverter = new HostConnectInfo();
					EdgeConnectInfo info = infos.get(0);
					newConverter.setId(converter.getId());
					newConverter.setHostId(converter.getHostId());
					newConverter.setHostName(converter.getHostName());
					newConverter.setPort(info.getPort());
					newConverter.setProtocol(info.getProtocol() == Protocol.Https.ordinal() ? Protocol.Https
							: Protocol.Http);
					newConverter.setUserName(info.getUsername());
					newConverter.setPassword(info.getPassword());
					newConverter.setUuid(info.getUuid());
					newConverter.setAuthUuid(info.getAuthUuid());
					newConverter.setConverterType(converter.getConverterType());
					return newConverter;
				}
			}
			return converter.toHostConnectInfo();
		}
		return null;
	}

	private String getMoniteeUuidByHostId(int hostId) {
		try {
			NodeDetail nodeDetail = getNodeDetailInformation(hostId);
			D2DConnectInfo connectInfo = nodeDetail.getD2dConnectInfo();
			if (connectInfo != null) {
				return connectInfo.getUuid();
			}
		} catch (EdgeServiceFault e) {
			logger.error("Failed to get monitee uuid by host id:" + hostId);
		}
		return null;
	}
	
	@Override
	public HostConnectInfo getMonitorConnectInfoByHostId(int hostId) throws EdgeServiceFault{
		// Get monitor information from database
		HostConnectInfo info = null;
		
		List<HostConnectInfo> monitorList = new ArrayList<HostConnectInfo>();
		vsbDao.as_edge_vsb_monitor_getByHostId(hostId, monitorList);
		
		if (monitorList.size() == 1) {
			info = monitorList.get(0);
		} else if (monitorList.size() > 0) {
			logger.error("Thre are many monitor records on the database.");
			info = monitorList.get(0);
		}
		
		if (info != null) {			
			// this should get Monitor 's gateway, not host's geteway
			//GatewayEntity gateway = gatewayService.getGatewayByHostId(hostId);
			//info.setGatewayId(gateway.getId());
			List<EdgeD2DHost> hostList = new ArrayList<EdgeD2DHost>();
			hostMgrDao.getHostByUUID(info.getUuid(), hostList);
			if(hostList!=null && (!hostList.isEmpty())){
				GatewayEntity gateway = gatewayService.getGatewayByHostId(hostList.get(0).getRhostid());
				info.setGatewayId(gateway.getId());
			}
		}
		
		return info;
	}

	@Override
	public List<JobHistory> getLatestJobHistoriesByNodeId(int nodeId) throws EdgeServiceFault {
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		hostMgrDao.as_edge_node_latest_job_status(nodeId, lstJobHistory);
		
		List<JobHistory> retval = new ArrayList<JobHistory>();
		for (EdgeJobHistory jobHistory : lstJobHistory) {
			JobHistory jh = new JobHistory();

			jh.setVersion(jobHistory.getVersion());
			jh.setProductType(jobHistory.getProductType());
			jh.setJobId(jobHistory.getJobId());
			jh.setJobMethod(jobHistory.getJobMethod());
			jh.setJobType(jobHistory.getJobType());
			jh.setJobStatus(JobStatus.parse(jobHistory.getJobStatus()));
			jh.setJobUTCStartDate(jobHistory.getJobUTCStartTime());
			jh.setJobLocalStartDate(jobHistory.getJobLocalStartTime());
			jh.setJobUTCEndDate(jobHistory.getJobUTCEndTime());
			jh.setJobLocalEndDate(jobHistory.getJobLocalEndTime());
			jh.setServerId(jobHistory.getServerId());
			jh.setAgentId(jobHistory.getAgentId());
			jh.setSourceRPSId(jobHistory.getSourceRPSId());
			jh.setTargetRPSId(jobHistory.getTargetRPSId());
			
			retval.add(jh);
		}
		return retval;
	}
	
	private static String trim( String string )
	{
		if (string == null)
			return string;
		
		return string.trim();
	}
	
	private String normalizeHostName(String hostname){
		return hostname.trim().toLowerCase();
	}

	@Override
	public AddNodeResult addNodes(List<NodeRegistrationInfo> nodeList) throws EdgeServiceFault {
		if (nodeList.isEmpty()) {
			return new AddNodeResult();
		}
		
		List<NodeRegistrationInfo> d2dNodeList = new ArrayList<NodeRegistrationInfo>();
		List<NodeRegistrationInfo> linuxNodeList = new ArrayList<NodeRegistrationInfo>();
		Map<String, List<NodeRegistrationInfo>> mapEsx = new HashMap<String, List<NodeRegistrationInfo>>();
		Map<String, List<NodeRegistrationInfo>> mapHyperv = new HashMap<String, List<NodeRegistrationInfo>>();
		List<Integer> vMwareNodesForVerify = new ArrayList<Integer>();
		List<Integer> hyperVNodesForVerify = new ArrayList<Integer>();
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
		List<EdgeHyperV> hypervList = new ArrayList<EdgeHyperV>();
		AddNodeResult addResult = new AddNodeResult();
		List<AddNodeInfo> nodeIdList = new ArrayList<AddNodeInfo>();
		AddNodeInfo info_win=new AddNodeInfo(AddNodeResult.NodeEnum.Windows);
		AddNodeInfo info_linux=new AddNodeInfo(AddNodeResult.NodeEnum.LinuxBackupServer);
		AddNodeInfo info_linuxBkServer=new AddNodeInfo(AddNodeResult.NodeEnum.Linux);
		AddNodeInfo info_vmware=new AddNodeInfo(AddNodeResult.NodeEnum.VM_Vmware);
		AddNodeInfo info_hyperv=new AddNodeInfo(AddNodeResult.NodeEnum.VM_HyperV);
		AddNodeInfo info_rha=new AddNodeInfo(AddNodeResult.NodeEnum.RHA);

		for (NodeRegistrationInfo node : nodeList) {
			
			node.setNodeName( normalizeHostName( node.getNodeName() ) );
			node.setUsername( trim( node.getUsername() ) );
			
			if (node instanceof NodeRegistrationInfoForRHA) {
				NodeRegistrationInfoForRHA nodeInfoForRHA = (NodeRegistrationInfoForRHA) node;
				ImportNodeFromRHAParameters param = new ImportNodeFromRHAParameters();
				param.setControlService(nodeInfoForRHA.getControlService());
				param.addRHASourceNode(nodeInfoForRHA.getSourceNode());
				ImportNodeFromRHAResult result = importNodeFromRHA(param);
				addResult.addAllConverterIds(result.getConverterIdList());
				info_rha.getNodeIds().addAll(result.getImportedNodes());
				
				bindEntityToGateway( result.getConverterIdList(), node.getGatewayId(), EntityType.Converter );
				bindEntityToGateway( result.getImportedNodes(), node.getGatewayId(), EntityType.Node );
				
				continue;
			}
			if(node instanceof NodeRegistrationInfoForVcloud){
				VcloudNodeImporter importer = new VcloudNodeImporter((NodeRegistrationInfoForVcloud)node, this);
				int id = importer.ImportVcloudNode();
				addResult.getvCloudGroupIds().add(id);
				
				bindEntityToGateway( id, node.getGatewayId(), EntityType.VSphereEntity );
				
				continue;
			}
			
			String hostName = node.getNodeName();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[NodeServiceImpl] addNodes() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			if (node.isPhysicsMachine()) {
				//check exists
				int hostId = getHostIdByName(node.getGatewayId().getRecordId(), node.getNodeName(), "",fqdnNameList,0);
				
				//check exists by uuid
				if(hostId ==0 && node.getNodeInfo() != null && !StringUtil.isEmptyOrNull(node.getNodeInfo().getD2DUUID())){
					int[] hostIds = new int[1];
					hostMgrDao.as_edge_host_getHostIdByUuid(node.getNodeInfo().getD2DUUID(), ProtectionType.WIN_D2D.getValue(),
							hostIds);
					if(hostIds[0] != 0)
						hostId = hostIds[0];
				}
				
				if (hostId > 0) {
					String message = EdgeCMWebServiceMessages.getMessage("importNodes_NodeAlreadyExist", node.getNodeName());
//					String message = "Node " + node.getNodeName() + " already exists in the database.";
					logger.debug(message);
					addActivityLogForImportNodes(Severity.Warning, ImportNodeType.File, message);
					node.setId(hostId);
					
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					connectionInfoDao.as_edge_connect_info_list(hostId, connInfoLst);
					if(connInfoLst.size() == 0){
						connectionInfoDao.as_edge_connect_info_update(hostId, node.getUsername(), node.getPassword(),
								"", 0, 0, 0,
								"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
					}
					hostMgrDao.as_edge_host_set_visible(hostId, 1); //issue 763963
				} else {
					int[] output = new int[1];
					hostMgrDao.as_edge_host_update(-1, new Date(), hostName,node.getNodeDescription(),
							null, null,null, 1, 0, "",HostType.EDGE_NODE_PHYSICS_MACHINE.getValue(), 1, fqdnNames, output);
					node.setId(output[0]);
					connectionInfoDao.as_edge_connect_info_update(output[0], node.getUsername(), node.getPassword(),
							"", 0, 0, 0,
							"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
				}
				d2dNodeList.add(node);
				info_win.getNodeIds().add(node.getId());
				
				bindEntityToGateway( node.getId(), node.getGatewayId(), EntityType.Node );
			}
			if (node.isVMWareVM()) {
				node.getDiscoveryESXOption().setGatewayId(node.getGatewayId());
				
				String esxServer = node.getDiscoveryESXOption().getEsxServerName();
				List<NodeRegistrationInfo> esxNodeList;
				if (mapEsx.containsKey(esxServer)) {
					esxNodeList = mapEsx.get(esxServer);
				} else {
					esxNodeList = new ArrayList<NodeRegistrationInfo>();
					mapEsx.put(esxServer, esxNodeList);
				}
				
				if (node.getVmRegistrationInfo().getNodeInfo() == null) {
					node.getVmRegistrationInfo().setNodeInfo(new NodeRegistrationInfo());
				}
				
				DiscoveryVirtualMachineInfo vmInfo = node.getVmRegistrationInfo().getVmInfo();
		
				//save vm to host table
				int[] ids = new int[1];
				int[] output = new int[1];
				int nodeID = -1;
				esxDao.as_edge_host_getHostByInstanceUUID(node.getGatewayId().getRecordId(), vmInfo.getVmInstanceUuid(), ids);
				if(ids[0] > 0){
					nodeID = ids[0];
					String message = EdgeCMWebServiceMessages.getMessage("importNodes_NodeAlreadyExist", vmInfo.getVmName());
					logger.debug(message);
					addActivityLogForImportNodes(Severity.Warning, ImportNodeType.WMWare, message);
					
					bindEntityToGateway( nodeID, node.getGatewayId(), EntityType.Node );
					
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					connectionInfoDao.as_edge_connect_info_list(nodeID, connInfoLst);
					if(connInfoLst.size() == 0){
						connectionInfoDao.as_edge_connect_info_update(nodeID, node.getVmRegistrationInfo().getNodeInfo().getUsername(), node.getVmRegistrationInfo().getNodeInfo().getPassword(),
								null, 0, 0, 0,
								"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
					}
					hostMgrDao.as_edge_host_set_visible(nodeID, 1);
				}else{
					hostMgrDao.as_edge_host_update(-1, new Date(), hostName, node.getNodeDescription(),
							null, null, null, 1, 0, "", HostType.EDGE_NODE_VMWARE_VIRTUALMACHINE.getValue(), 1, fqdnNames, output);
					nodeID = output[0];
					
					bindEntityToGateway( nodeID, node.getGatewayId(), EntityType.Node );
				}				
					
				//save vm to connectinfo table
				connectionInfoDao.as_edge_connect_info_update(nodeID, node.getVmRegistrationInfo().getNodeInfo().getUsername(), node.getVmRegistrationInfo().getNodeInfo().getPassword(),
						null, 0, 0, 0,
						"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());

				node.setId(nodeID);
				node.getVmRegistrationInfo().getNodeInfo().setId(nodeID);
				node.getVmRegistrationInfo().getNodeInfo().setGatewayId(node.getGatewayId());
				
				//save esx to esx db table
				esxList.clear();
				esxDao.as_edge_esx_getByName(node.getGatewayId().getRecordId(), esxServer, esxList); //we need a uuid for esx sever/vcenter physical machine, then change the implementation
				int esxID = esxList.isEmpty() ? 0 : esxList.get(0).getId();
				output[0] = 0;
				esxDao.as_edge_esx_update(esxID, 
						esxServer, 
						node.getDiscoveryESXOption().getEsxUserName(), 
						node.getDiscoveryESXOption().getEsxPassword(), 
						node.getDiscoveryESXOption().getProtocol().ordinal(), 
						node.getDiscoveryESXOption().getPort(),
						0,
						0,
						"",
						"",
						output);
				
				if (esxID == 0) {
					esxID = output[0];
				}
				
				bindEntityToGateway( esxID, node.getGatewayId(), EntityType.VSphereEntity );
				
				//save vm to esx, vm_detali, vmentity_esxEntity_map, vmHost_vmEntity_map
				saveVMToDB(node.getGatewayId().getRecordId(), esxID, nodeID, vmInfo.getVmInstanceUuid(), vmInfo.getVmHostName(), vmInfo.getVmName(), vmInfo.getVmUuid()
						,vmInfo.getVmEsxHost(),vmInfo.getVmXPath(),vmInfo.getVmGuestOS(),vmInfo.getUserName()
						,vmInfo.getPassword(), 0,0,0,"",true);

				esxDao.as_edge_esx_updateLicenseInfo(nodeID, vmInfo.isVmEsxEssential() ? 1 : 0, vmInfo.getVmEsxSocketCount());
				node.getDiscoveryESXOption().setId(esxID);
				esxNodeList.add(node);
				vMwareNodesForVerify.add(nodeID);
				info_vmware.getNodeIds().add(nodeID);
			}
			if (node.isHyperVVM()) {
				DiscoveryVirtualMachineInfo vmInfo = node.getVmRegistrationInfo().getVmInfo();
				String hypervServer = vmInfo.getVmEsxHost();
				// No longer use 0 and 1, it's confused, because in HypervProtectionType, we define 1 as standalone and 2 as cluster
				// So we should follow this. 1 means stand alone and 2 means cluster
				int hypervType = 1; //1 stand alone hyperv server , 2 Cluster virtual node
				if(vmInfo.getVmType() == HypervEntityType.HypervStandAloneVMINCluster.getValue()){ //cluster vm, we just storage the cluster virtual node
					hypervServer = vmInfo.getClusterVirtualName();
					hypervType = 2;
				}
				
				List<NodeRegistrationInfo> hypervNodeList;
				if (mapHyperv.containsKey(hypervServer)) {
					hypervNodeList = mapHyperv.get(hypervServer);
				} else {
					hypervNodeList = new ArrayList<NodeRegistrationInfo>();
					mapHyperv.put(hypervServer, hypervNodeList);
				}
				
				int[] ids = new int[1];
				int[] output = new int[1];
				int nodeID = -1;
				
				if (node.getVmRegistrationInfo().getNodeInfo() == null) {
					node.getVmRegistrationInfo().setNodeInfo(new NodeRegistrationInfo());
				}
				
				hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(node.getGatewayId().getRecordId(), vmInfo.getVmInstanceUuid(), ids);
				if(ids[0] > 0){
					nodeID = ids[0];
					String message = EdgeCMWebServiceMessages.getMessage("importNodes_NodeAlreadyExist", vmInfo.getVmName());
					logger.debug(message);
					addActivityLogForImportNodes(Severity.Warning, ImportNodeType.HyperV, message);
					
					bindEntityToGateway( nodeID, node.getGatewayId(), EntityType.Node );
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					connectionInfoDao.as_edge_connect_info_list(nodeID, connInfoLst);
					if(connInfoLst.size() == 0){
						connectionInfoDao.as_edge_connect_info_update(nodeID, node.getVmRegistrationInfo().getNodeInfo().getUsername(), node.getVmRegistrationInfo().getNodeInfo().getPassword(),
								null, 0, 0, 0,
								"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());
					}
					hostMgrDao.as_edge_host_set_visible(nodeID, 1);
				} else {
					int hostType = HostType.EDGE_NODE_HYPERV_VIRTUALMACHINE.getValue();
					if(node.isHyperVClusterVM()){
						hostType = HostTypeUtil.setHyperVClusterVirtualsMachine(hostType);
					}
					hostMgrDao.as_edge_host_update(-1, new Date(), hostName,null,
							null, null,null, 1, 0, "",hostType, 1, fqdnNames, output);
					nodeID = output[0];
				}
					
				connectionInfoDao.as_edge_connect_info_update(nodeID, node.getVmRegistrationInfo().getNodeInfo().getUsername(), node.getVmRegistrationInfo().getNodeInfo().getPassword(),
						null, 0, 0, 0,
						"", "", "", "", NodeManagedStatus.Unmanaged.ordinal());

				node.setId(nodeID);
				node.getVmRegistrationInfo().getNodeInfo().setId(nodeID);
				node.getVmRegistrationInfo().getNodeInfo().setGatewayId(node.getGatewayId());
				
				hypervList.clear();
				//add hyperv
				hyperVDao.as_edge_hyperv_getByName(node.getGatewayId().getRecordId(), hypervServer, hypervList);
				int hypervServerId = hypervList.isEmpty() ? 0 : hypervList.get(0).getId();
				
				output[0] = 0;
				hyperVDao.as_edge_hyperv_update(hypervServerId, 
						hypervServer, 
						node.getDiscoveryESXOption().getEsxUserName(), 
						node.getDiscoveryESXOption().getEsxPassword(), 
						node.getDiscoveryESXOption().getProtocol().ordinal(), 
						node.getDiscoveryESXOption().getPort(),
						0,hypervType,
						output);
				if (hypervServerId == 0)
					this.gatewayService.bindEntity( node.getGatewayId(), output[0], EntityType.HyperVServer );
				hypervServerId = output[0];
				
				List<EdgeHyperVHostMapInfo> hostMapInfo = new LinkedList<EdgeHyperVHostMapInfo>();
				hyperVDao.as_edge_hyperv_host_map_getById(nodeID, hostMapInfo);
				if (hostMapInfo != null && !hostMapInfo.isEmpty()) {
					hyperVDao.as_edge_hyperv_host_map_delete(hostMapInfo.get(0).getHyperVId(), nodeID, 0);
				}

				hyperVDao.as_edge_hyperv_host_map_add(hypervServerId, nodeID, IEdgeHyperVDao.HYPERV_HOST_STATUS_VISIBLE,
						vmInfo.getVmName(), vmInfo.getVmUuid(), vmInfo.getVmInstanceUuid(), vmInfo.getVmEsxHost(), vmInfo.getVmGuestOS());//if it is cluster resource, then map store the cluster name
				logger.info("[NodeServiceImpl]:addNodes() insert one item to as_edge_hyperv_host_map, "
						+ "the nodeId is "+nodeID +"the vminstanceuuid is "+vmInfo.getVmInstanceUuid()+" the hypervid is "+hypervServerId);

//				int esxSocketCount = VSphereLicenseCheck.getHyperVCPUSocketCount(vmInfo.getVmEsxHost(), node.getDiscoveryESXOption().getEsxUserName(), node.getDiscoveryESXOption().getEsxPassword());
//				hyperVDao.as_edge_hyperv_updateLicenseInfo(nodeID, esxSocketCount);
				hyperVDao.as_edge_hyperv_updateLicenseInfo(nodeID, vmInfo.getVmEsxSocketCount());
			
				hypervNodeList.add(node);
				hyperVNodesForVerify.add(nodeID);
				info_hyperv.getNodeIds().add(nodeID);
				
				bindEntityToGateway( hypervServerId, node.getGatewayId(), EntityType.HyperVServer );
				bindEntityToGateway( nodeID, node.getGatewayId(), EntityType.Node );
			}
			if(node.getProtectionType() == ProtectionType.LINUX_D2D_SERVER){
				int serverId = addLinuxD2DServerNodeToList(node,linuxNodeList);
				info_linuxBkServer.getNodeIds().add( serverId );
//				int nodeId = addLinuxNodeToList(node,linuxNodeList,true);
//				info_linux.getNodeIds().add( nodeId );
				bindEntityToGateway( serverId, node.getGatewayId(), EntityType.Node );
			}
			if(node.isLinux()){
				int nodeId = addLinuxNodeToList(node,linuxNodeList,false);
				info_linux.getNodeIds().add( nodeId );
				bindEntityToGateway( nodeId, node.getGatewayId(), EntityType.Node );
			}
			
			if (node.getNodeInfo() !=null && node.getNodeInfo().isARCserveBackInstalled()){
				connectionInfoDao.as_edge_arcserve_connect_info_update(node.getId(), 
						node.getCarootUsername()==null?"":node.getCarootUsername(), 
								node.getCarootPassword()==null?"":node.getCarootPassword(),
						node.getAbAuthMode().ordinal(), node.getNodeInfo().getARCserveProtocol().ordinal(),
						node.getNodeInfo().getARCservePortNumber(), node.getNodeInfo().getARCserveType().ordinal(), 
						node.getNodeInfo().getARCserveVersion(),NodeManagedStatus.Unknown.ordinal());
			}
		}
		
		startVerifyVMJob(vMwareNodesForVerify, ImportNodeType.WMWare);
		startVerifyVMJob(hyperVNodesForVerify, ImportNodeType.HyperV);
		
		if (d2dNodeList.size() != 0) {
			NodeRegistrationInfo[] nodeRegistrationInfo = new NodeRegistrationInfo[d2dNodeList.size()];  
			importNodes(d2dNodeList.toArray(nodeRegistrationInfo), ImportNodeType.File);
		}
		if(linuxNodeList.size() !=0){
			importLinuxNodes(linuxNodeList);
		}
		
		Iterator<Entry<String, List<NodeRegistrationInfo>>> iterator = mapEsx.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<NodeRegistrationInfo>> entry = iterator.next();
			List<NodeRegistrationInfo> oneEsxNodeList = entry.getValue();
			if (oneEsxNodeList == null || oneEsxNodeList.isEmpty()) {
				continue;
			}
			DiscoveryESXOption esxOption = oneEsxNodeList.get(0).getDiscoveryESXOption();
			esxOption.setGatewayId(oneEsxNodeList.get(0).getGatewayId());
			VMRegistrationInfo[] vms = new VMRegistrationInfo[oneEsxNodeList.size()];
			for (int i = 0 ; i < oneEsxNodeList.size() ; i++) {
				vms[i] = oneEsxNodeList.get(i).getVmRegistrationInfo();
			}
			updateEsxServerType(esxOption, esxOption.getId());
			importVMs(esxOption, vms, ImportNodeType.WMWare, esxOption.isAddEsxToADList());
		}	
		
		iterator = mapHyperv.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, List<NodeRegistrationInfo>> entry = iterator.next();
			List<NodeRegistrationInfo> oneHyperVNodeList = entry.getValue();
			if (oneHyperVNodeList == null || oneHyperVNodeList.isEmpty()) {
				continue;
			}
			DiscoveryESXOption esxOption = oneHyperVNodeList.get(0).getDiscoveryESXOption();
			DiscoveryHyperVOption hypervOption = new DiscoveryHyperVOption();
			//hypervOption.setServerName(esxOption.getEsxServerName());
			hypervOption.setServerName(entry.getKey());
			hypervOption.setUsername(esxOption.getEsxUserName());
			hypervOption.setPassword(esxOption.getEsxPassword());
			hypervOption.setHypervProtectionType(HypervProtectionType.STANDALONE);
			hypervOption.setGatewayId(oneHyperVNodeList.get(0).getGatewayId());
			if(oneHyperVNodeList.get(0).getVmRegistrationInfo().getVmInfo().getVmType()==2)
				hypervOption.setHypervProtectionType(HypervProtectionType.CLUSTER);
			boolean addEsxToADList = esxOption.isAddEsxToADList();
			VMRegistrationInfo[] vms = new VMRegistrationInfo[oneHyperVNodeList.size()];
			for (int i = 0 ; i < oneHyperVNodeList.size() ; i++) {
				vms[i] = oneHyperVNodeList.get(i).getVmRegistrationInfo();
			}
			importHyperVVMs(hypervOption, vms, ImportNodeType.HyperV, addEsxToADList);
		}
		
		if(info_win.getNodeIds().size()>0)
			nodeIdList.add(info_win);
		if(info_linux.getNodeIds().size()>0)
			nodeIdList.add(info_linux);
		if(info_linuxBkServer.getNodeIds().size()>0)
			nodeIdList.add(info_linuxBkServer);
		if(info_vmware.getNodeIds().size()>0)
			nodeIdList.add(info_vmware);
		if(info_hyperv.getNodeIds().size()>0)
			nodeIdList.add(info_hyperv);
		if(info_rha.getNodeIds().size()>0)
			nodeIdList.add(info_rha);
		addResult.setNodeIdList(nodeIdList);
		return addResult;
	}
	
	private void bindEntityToGateway(
		Collection<Integer> entityIdList, GatewayId gatewayId, EntityType entityType ) throws EdgeServiceFault
	{
		for (int entityId : entityIdList)
			this.gatewayService.bindEntity( gatewayId, entityId, entityType );
	}
	
	private void bindEntityToGateway(
		int entityId, GatewayId gatewayId, EntityType entityType ) throws EdgeServiceFault
	{
		this.gatewayService.bindEntity( gatewayId, entityId, entityType );
	}
	
	private int addLinuxD2DServerNodeToList(NodeRegistrationInfo node,List<NodeRegistrationInfo> nodeList){
		int nodeid=linuxNodeService.getNodeId(node.getNodeName(), null, 2);
		if(nodeid>0){
			String message = "Linux D2D server" + node.getNodeName() + " already exists in the database.";
			logger.debug(message);
			addActivityLogForImportNodes(Severity.Warning, ImportNodeType.File, message);
		}else{
			int[] output = new int[1];
			
			String hostName = node.getNodeName();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[NodeserviceImpl] addLinuxD2DServerNodeToList() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			hostMgrDao.as_edge_host_update(
				0,											// rhostid
				new Date(),									// lastupdated
				hostName,							// rhostname
				node.getNodeDescription(),					// nodeDescription
				null,										// ipaddress
				null,										// osdesc
				"",											// ostype
				1,											// IsVisible
				ApplicationUtil.setLinuxD2DInstalled(0),	// appStatus
				"",											// ServerPrincipalName
				0,											// rhostType
				ProtectionType.LINUX_D2D_SERVER.getValue(),	// protectionType
				fqdnNames,                                  // fqdn name
				output										// [out] id
				);
			nodeid=output[0];
			connectionInfoDao.as_edge_connect_info_update(
				output[0],								// hostid
				node.getUsername(),						// username
				node.getPassword(),						// password
				"",										// uuid
				node.getD2dProtocol().ordinal(),		// protocol
				node.getD2dPort(),						// port
				0,										// type
				"",										// majorversion
				"",										// minorversion
				"",										// updateversionnumber
				"",										// buildnumber
				NodeManagedStatus.Unmanaged.ordinal()	// managed
				);
		}
		node.setId(nodeid);
		node.setLinux( false );
		nodeList.add(node);
		return nodeid;
	}
	
	private int addLinuxNodeToList(NodeRegistrationInfo node,List<NodeRegistrationInfo> nodeList,boolean isAddForLinuxD2DServer){
		int nodeid=linuxNodeService.getNodeId(node.getNodeName(), null, 1);
		if(nodeid>0){
			if(!isAddForLinuxD2DServer){
				String message = "Linux Node " + node.getNodeName() + " already exists in the database.";
				logger.debug(message);
				addActivityLogForImportNodes(Severity.Warning, ImportNodeType.File, message);
			}
		}else{
			int[] output = new int[1];
			String nodeUUID = null;
			if(node instanceof NodeRegistrationInfoForLinux){
				nodeUUID = ((NodeRegistrationInfoForLinux)node).getNodeUUID();
			}
			String hostName = node.getNodeName();
			if(!StringUtil.isEmptyOrNull(hostName))
				hostName = hostName.toLowerCase();
			
//			List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
			List<String> fqdnNameList = new ArrayList<String>();
			if(node.getGatewayId() != null && node.getGatewayId().isValid()){
				try {
					IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( node.getGatewayId());
					fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
				} catch (Exception e) {
					logger.error("[Nodeserviceimpl] addLinuxNodeToList() get fqdn name failed.",e);
				}
			}
			String fqdnNames = CommonUtil.listToCommaString(fqdnNameList);
			
			hostMgrDao.as_edge_host_update(
				-1,									// rhostid
				new Date(),							// lastupdated
				hostName,					// rhostname
				node.getNodeDescription(),			// nodeDescription
				null,								// ipaddress
				null,								// osdesc
				"",									// ostype
				1,									// IsVisible
				isAddForLinuxD2DServer ? ApplicationUtil.setLinuxD2DInstalled(0) : 0,	// appStatus
				"",									// ServerPrincipalName
				HostTypeUtil.setLinuxNode(0),		// rhostType
				ProtectionType.WIN_D2D.getValue(),	// protectionType
				fqdnNames,                          // fqdn name
				output								// [out] id
				);
			nodeid=output[0];
			connectionInfoDao.as_edge_connect_info_update(
				output[0],								// hostid
				node.getUsername(),						// username
				node.getPassword(),						// password
				nodeUUID,								// uuid
				0,										// protocol
				0,										// port
				0,										// type
				"",										// majorversion
				"",										// minorversion
				"",										// updateversionnumber
				"",										// buildnumber
				NodeManagedStatus.Unmanaged.ordinal()	// managed
				);

			if (isAddForLinuxD2DServer)
				node = cloneObject( node );
						
			node.setId( nodeid );
			node.setLinux( true ); // should be even the node is added for Linux D2D server
			
			if (!isAddForLinuxD2DServer)
				nodeList.add( node );
		}
		
		return nodeid;
	}
	
	/**
	 * Make a copy of the specified object by copying deeply, which means every
	 * field of the new object is independent to the original object.
	 * 
	 * @param	object
	 * 			The object to be cloned.
	 * @return	The new copy of the specified object.
	 */
	@SuppressWarnings("unchecked")
	private <E extends Serializable> E cloneObject( E object )
	{
		if (object == null)
			return null;
		
		try
		{
			ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
			ObjectOutputStream output = new ObjectOutputStream( outputBytes );
			output.writeObject( object );
			output.close();
			
			ByteArrayInputStream inputBytes = new ByteArrayInputStream( outputBytes.toByteArray() );
			ObjectInputStream input = new ObjectInputStream( inputBytes );
			Object newObject = input.readObject();
			
			return (E) newObject;
		}
		catch (Exception e)
		{
			logger.error( "Error cloning object. Object type: " + object.getClass().getName(), e );
			return null;
		}
	}
	
	@Override
	public List<Node> getNodeListByIDs(List<Integer> ids) throws EdgeServiceFault{
		List<Node> nodes = new LinkedList<Node>();
		
		List<EdgeHost> hosts = getEdgeHostByIDs(ids, null);
		
		
		addHostToNodeListWithFullInfo(hosts, nodes);

		for (Node node : nodes) {
			node.setLstJobHistory(getLatestJobHistoriesByNodeId(node.getId()));
		}
		
		populateVMInformation(nodes);
		
		return nodes;
	}
	
	@Override
	public PagingResult<Node> getNodePagingListByIDs(List<Integer> ids, PagingConfig config) throws EdgeServiceFault{
		logger.info(System.currentTimeMillis()+ " begin getNodePagingListByIDs");
		PagingResult<Node> pagingResult = new PagingResult<Node>();
		List<Node> nodes = new LinkedList<Node>();
		List<EdgeHost> hosts = getEdgeHostByIDs(ids, config);
		addHostToNodeListWithFullInfo(hosts, nodes);
		for (Node node : nodes) {
			node.setLstJobHistory(getLatestJobHistoriesByNodeId(node.getId()));
		}
		populateVMInformation(nodes);
		pagingResult.setStartIndex(config.getStartIndex());
		pagingResult.setTotalCount(ids.size());
		pagingResult.setData(nodes);
		logger.info(System.currentTimeMillis()+ " end getNodePagingListByIDs");
		return pagingResult;
	}
	
	public List<EdgeHost> getEdgeHostByIDs(List<Integer> ids, PagingConfig config) {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		StringBuilder sb=new StringBuilder();
		for(int id:ids){
			sb.append(id).append(" ");
		}
		int[] total=new int[1];
		if(config!=null)
			hostMgrDao.as_edge_host_list_by_ids_paging(sb.toString(), config.getStartIndex(), config.getCount(),
				total,hosts);
		else
			hostMgrDao.as_edge_host_list_by_ids(sb.toString(), hosts);
		return hosts;
	}

	@Override
	public int getCountOfHostWithVSBTask() throws EdgeServiceFault {
		int[] countOfHosts = new int[1];
		vsbDao.as_edge_host_countWithVSBTask(countOfHosts);
		return countOfHosts[0];
	}

	@Override
	public int getReplicationQueueSize(int nodeId) throws EdgeServiceFault {
		try {
			NodeDetail nodeDetail = getNodeDetailInformation(nodeId);
			return VCMServiceManager.getInstance().getReplicationQueueSize(nodeDetail);
		} catch (SOAPFaultException e) {
			logger.error(e);
			EdgeServiceFault fault = D2DServiceFault.getFault(e.getFault().getFaultCodeAsQName().getLocalPart(), "");
			throw fault;
		} catch (WebServiceException e) {
			logger.error(e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_CantConnectRemoteD2D, "");
		}
	}
	
	@Override
	public void doManualDiscovery() throws EdgeServiceFault {
		discoveryMgr.doManualDiscovery();
	}
	
	@Override
	public void doAutoDiscovery() throws EdgeServiceFault {
		discoveryMgr.doAutoDiscovery();
	}
	
	@Override
	public int saveFilters(BaseFilter filter)
			throws EdgeServiceFault {
		String filterXml = null;
		
		try {
			filterXml = CommonUtil.marshal(filter);
		} catch (JAXBException e) {
			logger.error("marshal filter failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "marshal filter failed.");
		}
		
		int[] newFilterId = new int[1];
		hostMgrDao.as_edge_filter_update(filter.getId(), filterXml, filter.getFilterType().getValue(), newFilterId);
		return filter.getId() == 0 ? newFilterId[0] : filter.getId();
	}
	
	@Override
	public List<BaseFilter> getFilters(FilterType filterType) throws EdgeServiceFault {
		List<EdgeFilter> filters = new ArrayList<EdgeFilter>();
		hostMgrDao.as_edge_filter_select(filterType.getValue(), filters);
		List<BaseFilter> dbFilterList = new ArrayList<BaseFilter>();
		// Create default filter
		if (filterType == FilterType.DashboardFilter) {			
			BaseFilter mostRecentFilter = new BaseFilter();
			mostRecentFilter.setType(1);
			mostRecentFilter.setDefaultFilter(true);
			dbFilterList.add(mostRecentFilter);
		} else if (filterType == FilterType.LogTimeFilter) {
			BaseFilter allTimeFilter = new BaseFilter();
			allTimeFilter.setType(4);
			allTimeFilter.setDefaultFilter(true);
			dbFilterList.add(allTimeFilter);
		}
		BaseFilter last24HoursFilter = new BaseFilter();
		last24HoursFilter.setType(2);
		last24HoursFilter.setAmount(24);
		last24HoursFilter.setUnit(2);
		last24HoursFilter.setDefaultFilter(true);
		dbFilterList.add(last24HoursFilter);
		BaseFilter last7DaysFilter = new BaseFilter();
		last7DaysFilter.setType(2);
		last7DaysFilter.setAmount(7);
		last7DaysFilter.setUnit(3);
		last7DaysFilter.setDefaultFilter(true);
		dbFilterList.add(last7DaysFilter);
		
		for (EdgeFilter edgeFilter : filters) {
			BaseFilter resultFilter;
			try {
				resultFilter = CommonUtil.unmarshal(edgeFilter.getFilterXml(), BaseFilter.class);
			} catch (JAXBException e) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Failed to unmarshal dashboard filter");
			} 
			resultFilter.setId(edgeFilter.getId());
			dbFilterList.add(resultFilter);
		}
		
		return dbFilterList;
	}
	
	private Map<String, List<Integer>> splitNodesByHypervisorType(int[] nodeIDs) throws EdgeServiceFault {
		 Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		if (nodeIDs == null || nodeIDs.length == 0) {
			return result;
		}
		
		for (int hostID : nodeIDs) {
			NodeDetail detail = getNodeDetailInformation(hostID);
			if (detail.isHyperVMachine()) {
				if (result.containsKey(TYPE_HYPERV)) {
					result.get(TYPE_HYPERV).add(hostID);
				} else {
					 List<Integer> list = new ArrayList<>();
					 list.add(hostID);
					 result.put(TYPE_HYPERV, list);
				}
			} else if (detail.isVMwareMachine()) {
				if (result.containsKey(TYPE_ESX)) {
					result.get(TYPE_ESX).add(hostID);
				} else {
					 List<Integer> list = new ArrayList<>();
					 list.add(hostID);
					 result.put(TYPE_ESX, list);
				}
			}
		}
		
		return result;
	}

	@Override
	public DiscoveryVmwareEntityInfo getVmwareTreeRootEntity(DiscoveryESXOption esxOption, boolean recursive) throws EdgeServiceFault {
		return esxService.getVmwareTreeRootEntity(esxOption, recursive);
	}

	@Override
	public DiscoveryESXOption getVMNodeESXSettingsFromDB(int hostID) throws EdgeServiceFault {
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(hostID, vmList);
		
		List<EdgeEsx> esxList = new LinkedList<>();
		esxDao.as_edge_esx_getHypervisorByHostId(hostID,esxList);
		
		if(vmList.isEmpty() || esxList.isEmpty())
			return null;
		
		DiscoveryESXOption result = new DiscoveryESXOption();
		result.setId(esxList.get(0).getId());
		result.setEsxServerName(esxList.get(0).getHostname());
		result.setEsxUserName(esxList.get(0).getUsername());
		result.setEsxPassword(esxList.get(0).getPassword());
		result.setIgnoreCertificate(true);
		result.setProtocol(Protocol.parse(esxList.get(0).getProtocol()));
		result.setPort(esxList.get(0).getPort());
		result.setEsxHost(vmList.get(0).getEsxHost());
		
		GatewayEntity gateway = gatewayService.getGatewayByEntityId(result.getId(), EntityType.VSphereEntity);
		result.setGatewayId(gateway.getId());
		
		return result;
	}
	
	public void retrieveNodePolicyDetails(NodeDetail nodeDetail) {
		List<EdgeHostPolicyMap> maps = new ArrayList<EdgeHostPolicyMap>();
		policyDao.getHostPolicyMap(nodeDetail.getId(), PolicyTypes.Unified, maps);

		if (!maps.isEmpty()) {
			List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
			policyDao.as_edge_policy_list(maps.get(0).getPolicyId(), 0, policyList);

			if (!policyList.isEmpty()) {
				nodeDetail.setPolicyType(policyList.get(0).getType());
				nodeDetail.setPolicyContentFlag(policyList.get(0).getContentflag());
			}
		}
	}
	@Override
	public int resumeMergeJob4RPS(int rpsNodeId, String uuid)
			throws EdgeServiceFault {
		try(RPSConnection conn=EdgeCommonUtil.getRPSServerProxyByNodeId(rpsNodeId)){
			return conn.getService().resumeMerge(MergeAPISource.MANUALLY, uuid);
		}
	}
	
	@Override
	public List<Integer> getNodesNeedRemoteDeploy(List<Integer> nodeIds)
			throws EdgeServiceFault {
		List<Integer> nodesNeedInstall = new ArrayList<Integer>();
		List<Node> nodes = getNodeListByIDs(nodeIds);
		if(nodes!=null){
			String version = getRemoteDeployParam(1);
			for (Node node : nodes) {
				if(DeployCommonUtil.isFreshOrOldVersionD2D(node.getD2DMajorversion(), node.getD2dMinorversion(), node.getD2dUpdateversionnumber(),node.getD2dBuildnumber(), version)
						&& !HostTypeUtil.isNodeImportFromRHA(node.getRhostType())
						&& !HostTypeUtil.isNodeImportFromRHAWithHBBU(node.getRhostType())
						&& !HostTypeUtil.isNodeImportFromRPS(node.getRhostType())
						&& !HostTypeUtil.isNodeImportFromRPSReplica(node.getRhostType())){
					nodesNeedInstall.add(node.getId());
				}
			}
		}
		return nodesNeedInstall;
	}
	
	@Override
	public NodeDetail getNodeDetailInformationByVMID(String vmInstanceUUID) throws EdgeServiceFault {
		int[] ids = new int[1];
		hostMgrDao.as_edge_host_vm_by_instanceUUID(vmInstanceUUID, ids);
		
		if (ids[0]>0){
			return this.getNodeDetailInformation(ids[0]);
		}
		
		return null;
	}
	
	@Override
	public String getNodeAuthUuid(String uuid) throws EdgeServiceFault {
		List<AuthUuidWrapper> authUuids = new ArrayList<AuthUuidWrapper>();
		connectionInfoDao.as_edge_connect_info_getAuthUuid(uuid, authUuids);
		if (authUuids.isEmpty()) {
			return null;
		}
		
		AuthUuidWrapper wrapper = authUuids.get(0);
		if (wrapper.getAuthUuid() == null || wrapper.getAuthUuid().isEmpty()) {
			return null;
		}
		
		return wrapper.getAuthUuid();
	}
	
	@Override
	public DiscoveryHyperVOption getHyperVInformation(int id) throws EdgeServiceFault {
		List<EdgeHyperV> hypervList = new LinkedList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getById(id, hypervList);
		
		if (hypervList.size()>0){
			DiscoveryHyperVOption result = new DiscoveryHyperVOption();
			result.setId(hypervList.get(0).getId());
			result.setServerName(hypervList.get(0).getHostname());
			result.setUsername(hypervList.get(0).getUsername());
			result.setPassword(hypervList.get(0).getPassword());
			result.setHypervProtectionType(HypervProtectionType.parse(hypervList.get(0).getType()));
			return result;
		}else
			return null;
	}
	
	@Override
	public void updateHyperVSource(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		if (hyperVOption == null || hyperVOption.getServerName() == null || hyperVOption.getServerName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		List<EdgeHyperV> hyperVList = new ArrayList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getById(hyperVOption.getId(), hyperVList);
		if (hyperVList.isEmpty()) {
			return;
		}
		
		EdgeHyperV hyperV = hyperVList.get(0);
		if (!hyperV.getHostname().equalsIgnoreCase(hyperVOption.getServerName())) {
			List<EdgeHyperV> hyperVNameList = new LinkedList<EdgeHyperV>();
			
			hyperVDao.as_edge_hyperv_getByName(hyperVOption.getGatewayId().getRecordId(), hyperVOption.getServerName(), hyperVNameList);
			if (!hyperVNameList.isEmpty() && hyperVNameList.get(0).getVisible() != -1) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_EsxSourceExist, "");
			}
		}
		
		hyperVOption.setGatewayId(gatewayService.getGatewayByEntityId(hyperVOption.getId(), EntityType.HyperVServer).getId());
		
		DiscoveryService.getInstance().validateHyperVAccount(hyperVOption);
		
		int[] output = new int[1];
		hyperVDao.as_edge_hyperv_update(hyperVOption.getId(),
		hyperVOption.getServerName(),
		hyperVOption.getUsername(),
		hyperVOption.getPassword(),
		0, 
		0,
		1,
		hyperVOption.getHypervProtectionType().getValue() == HypervProtectionType.CLUSTER.getValue() ? HypervProtectionType.CLUSTER.getValue() : HypervProtectionType.STANDALONE.getValue(),
		output);
		
		if (hyperVOption.getId() == 0)
			this.gatewayService.bindEntity( hyperVOption.getGatewayId(), output[0], EntityType.HyperVServer );
		
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( hyperVOption.getGatewayId() );
		int esxSocketCount = nativeFacade.getHyperVCPUSocketCount(hyperVOption.getServerName(), hyperVOption.getUsername(), hyperVOption.getPassword());
		hyperVDao.as_edge_hyperv_updateLicenseInfo(output[0], esxSocketCount);
	}
	
	@Override
	public void redeployPolicyByHyperV(int hyperVID) throws EdgeServiceFault {
		try{
			EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
			List<Node> nodeList = null;
			NodePagingConfig pagingConfig = new NodePagingConfig();
			
			pagingConfig.setOrderCol(NodeSortCol.hostname);
			pagingConfig.setOrderType(EdgeSortOrder.ASC);
			pagingConfig.setPagesize(Integer.MAX_VALUE);  
			pagingConfig.setStartpos(0);
			
			NodePagingResult result = getNodesESXByGroupAndTypePaging(hyperVID, NodeGroup.HYPERV, nodeFilter, pagingConfig);
			nodeList = result.getData();
			
			for (int i=0;i<nodeList.size();i++){
				Node node = nodeList.get(i);
				try{
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(1);
					policyDao.getHostPolicyMapByHostAndPlanTaskType(node.getId(), PolicyTypes.VMBackup, mapList);
					
					if (mapList.size()>0){
						ActivityLog log = new ActivityLog();
						log.setModule(Module.PolicyManagement);
						log.setSeverity(Severity.Information);
						log.setNodeName(node.getHostname());
						log.setMessage(EdgeCMWebServiceMessages.getResource("policyDeployment_Redeploy_HyperVChanged"));
						
						logService.addLog(log);
						serviceImpl.redeployPolicyToNodes(Arrays.asList(node.getId()), PolicyTypes.Unified, mapList.get(0).getPolicyId());
					}
				}catch(Exception e){
					logger.error(e);
				}
			}
			
		}catch(Exception e){
			logger.error(e);
		}
	
	}
	
	public String getVMInstanceUUIDById(int vmId) {
		List<EdgeHost> hosts = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(vmId, 1, hosts);
		if (hosts.size() == 0) {
			return null;
		}
		
		if (HostTypeUtil.isVMWareVirtualMachine(hosts.get(0).getRhostType())) { // VMware
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmId, vmList);
			if(!vmList.isEmpty()){
				return vmList.get(0).getVmInstanceUuid();
			}
			return null;
		} else { // Hyper-V
			List<EdgeHyperVHostMapInfo> hostMapInfo = new ArrayList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_getById(vmId, hostMapInfo);
			if (hostMapInfo != null && hostMapInfo.size() > 0) {
				return hostMapInfo.get(0).getVmInstanceUuid();
			}
			return null;
		}
	}
	@Override
	public List<EsxVSphere> getEsxInfoList(int gateway, List<VsphereEntityType> types)
			throws EdgeServiceFault {
		List<EsxVSphere> lstResult = new ArrayList<EsxVSphere>();
		IEdgeVSphereDao dao = DaoFactory.getDao(IEdgeVSphereDao.class);
		String esxTypes = "()";
		if(types!=null && !types.isEmpty()){
			StringBuilder sBuilder = new StringBuilder("(");
			int iMax = types.size()-1;
			for (int i=0 ; i<= iMax ; i++) {
				sBuilder.append(types.get(i).getValue());
				if(i<iMax){
					sBuilder.append(",");
				}else {
					sBuilder.append(")");
				}
			}
			esxTypes = sBuilder.toString();
		}
		dao.as_edge_vsphere_vmESXInfolist(gateway, esxTypes,lstResult);
		return  lstResult;
	}
	
	@Override
	public List<EsxVSphere> getHyperVInfoList(int gatewayid)
			throws EdgeServiceFault {
		List<EsxVSphere> lstResult = new ArrayList<EsxVSphere>();
		IEdgeVSphereDao dao = DaoFactory.getDao(IEdgeVSphereDao.class);		
		dao.as_edge_vsphere_vmHyperVInfolist(gatewayid,lstResult);
		return  lstResult;
	}

	@Override
	public HostConnectInfo getVsbMonitorByHostId(int hostId) throws EdgeServiceFault {
		List<HostConnectInfo> monitorList = new ArrayList<HostConnectInfo>();
		vsbDao.as_edge_vsb_monitor_getByHostId(hostId, monitorList);
		if (monitorList != null && monitorList.size() > 0) {
			return monitorList.get(0);
		}
		return null;
	}
	
	public LicenseMachineType tryDetectMachineType(int nodeId, String nodeName, String username, String password) {
		if (StringUtil.isEmptyOrNull(nodeName) || StringUtil.isEmptyOrNull(username)) {
			return LicenseMachineType.Undetected;
		}
		
		GatewayEntity gateway = null;
		
		try
		{
			IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
			gateway = gatewayService.getGatewayByHostId( nodeId );
		}
		catch (Exception e)
		{
			logger.error( "tryDetectMachineType(): Error getting gateway information.", e );
		}
		
		try {
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gateway.getId() );
			LicenseMachineType machineType = nativeFacade.getLicenseMachineType(nodeName, username, password);
			if (machineType != LicenseMachineType.Undetected) {
				hostMgrDao.as_edge_host_updateMachineType(nodeId, machineType.getValue());
			}
			
			return machineType;
		} catch (EdgeServiceFault e) {
			logger.debug("ImportNodesJob - try to detect machine type failed.", e);
			return LicenseMachineType.Undetected;
		}
	}
	
	@Override
	public boolean specifyHypervisor(LicenseMachineType machineType, Hypervisor hypervisor, List<Integer> nodeIds) throws EdgeServiceFault {
		if (hypervisor == null || StringUtil.isEmptyOrNull(hypervisor.getServerName()) || nodeIds == null || nodeIds.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters.");
		}
		
		HypervisorSpecifier specifier = HypervisorSpecifier.createSpecifier(machineType);
		
		if (nodeIds.size() > 1) {
			specifier.specifyNodes(hypervisor, nodeIds);
			return true;
		} else {
			specifier.specifySingle(hypervisor, nodeIds.get(0));
			return false;
		}
	}
	
	private void startVerifyVMJob(List<Integer> nodesForVerify, ImportNodeType type) {
		if (nodesForVerify.isEmpty()) {
			return;
		}
		
		int size = nodesForVerify.size();
		int[] nodeIDs = new int[size];
		for (int i = 0; i < size; i++) {
			nodeIDs[i] = nodesForVerify.get(i);
		}
		try {
			verifyVMs(nodeIDs);
		} catch (EdgeServiceFault e) {
			String message = e.getFaultInfo().getMessage();
			logger.error(message, e);
			addActivityLogForImportNodes(Severity.Error, type, message);
			return;
		}
	}
	
	@Override
	public Hypervisor getSpecifiedHypervisor(int hostId) throws EdgeServiceFault {
		List<EdgeHost> hosts = getEdgeHostByIDs(Arrays.asList(hostId), null);
		if (hosts.isEmpty()) {
			return null;
		}
		
		EdgeHost host = hosts.get(0);
		
		LicenseMachineType machineType = LicenseMachineType.parseValue(host.getMachineType());
		if (machineType == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "cannot parse the machine type to enum object.");
		}
		
		switch (machineType) {
		case VSHPERE_VM: return getSpecifiedEsx(host.getGatewayId(), hostId);
		case HYPER_V_VM: return getSpecifiedHyperV(host.getGatewayId(), hostId);
		case Other: return getSpecifiedOtherHypervisor(hostId);
		default: break;
		}
		
		
		if (HostTypeUtil.isLinuxNode(host.getRhostType()) || Utils.hasBit(host.getProtectionTypeBitmap(), ProtectionType.LINUX_D2D_SERVER)) {
			return tryGetLinuxHypervisor(host.getGatewayId(), hostId);
		}
		
		return null;
	}
	
	private Hypervisor tryGetLinuxHypervisor(int gatewayid, int hostId) throws EdgeServiceFault {
		Hypervisor hypervisor = getSpecifiedEsx(gatewayid, hostId);
		if (hypervisor != null) {
			return hypervisor;
		}
		
		hypervisor = getSpecifiedHyperV(gatewayid, hostId);
		if (hypervisor != null) {
			return hypervisor;
		}
		
		return getSpecifiedOtherHypervisor(hostId);
	}
	
	private Hypervisor getSpecifiedEsx(int gatewayid, int hostId) throws EdgeServiceFault {
		String[] serverName = new String[1];
		int[] esxid=new int[1];
		esxDao.as_edge_esx_getLicenseInfo(hostId, esxid, serverName, new int[1], new int[1]);
		if (serverName[0] == null) {
			return null;
		}
		
		List<EdgeEsx> servers = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getById(esxid[0], servers);
		if (servers.isEmpty()) {
			return null;
		}
		
		EdgeEsx server = servers.get(0);
		return new Hypervisor(new GatewayId(gatewayid), server.getHostname(), server.getUsername(), server.getPassword(), Protocol.parse(server.getProtocol()), server.getPort());
	}
	
	private Hypervisor getSpecifiedHyperV(int gatewayid, int hostId) throws EdgeServiceFault {
		String[] serverName = new String[1];
		int[] hypervid=new int[1];
		hyperVDao.as_edge_hyperv_getLicenseInfo(hostId, hypervid, serverName, new int[1]);
		if (serverName[0] == null) {
			return null;
		}
		
		List<EdgeHyperV> servers = new ArrayList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getById(hypervid[0], servers);
		if (servers.isEmpty()) {
			return null;
		}
		
		EdgeHyperV server = servers.get(0);
		return new Hypervisor(new GatewayId(gatewayid), serverName[0], server.getUsername(), server.getPassword());
	}
	
	private Hypervisor getSpecifiedOtherHypervisor(int hostId) throws EdgeServiceFault {
		String[] hypervisorName = new String[1];
		int[] socketCount = new int[1];
		hypervisorDao.as_edge_hypervisor_vm_getLicenseInfo(hostId, hypervisorName, socketCount);
		if (hypervisorName[0] == null) {
			return null;
		}
		
		GatewayEntity gateway = this.gatewayService.getGatewayByHostId( hostId );
		Hypervisor hypervisor = new Hypervisor(gateway.getId(), hypervisorName[0]);
		hypervisor.setSocketCount(socketCount[0]);
		return hypervisor;
	}
	
	@Override
	public CSVObject<ExportNode> getHostNodeCSVObject(int groupID, int grouptype,
			EdgeNodeFilter nodeFilter) throws EdgeServiceFault {
		CSVObject<ExportNode> csvObject = new CSVObject<ExportNode>();
		csvObject.setHeaders(new String[]{
				EdgeCMWebServiceMessages.getMessage("export_host_node_name"),
				EdgeCMWebServiceMessages.getMessage("export_host_username"),
				EdgeCMWebServiceMessages.getMessage("export_host_password"),
				EdgeCMWebServiceMessages.getMessage("export_host_encrypt"),
				EdgeCMWebServiceMessages.getMessage("export_host_node_dscription"),
				EdgeCMWebServiceMessages.getMessage("export_host_host_name"),
				EdgeCMWebServiceMessages.getMessage("export_host_ip_address"),
				EdgeCMWebServiceMessages.getMessage("export_host_os_dscription"),
				EdgeCMWebServiceMessages.getMessage("export_host_os_type"),
				EdgeCMWebServiceMessages.getMessage("export_host_app_status"),
				EdgeCMWebServiceMessages.getMessage("export_host_server_principal_name"),
				EdgeCMWebServiceMessages.getMessage("export_host_host_type"),
				EdgeCMWebServiceMessages.getMessage("export_host_timezone"),
				EdgeCMWebServiceMessages.getMessage("export_host_protection_type"),
				EdgeCMWebServiceMessages.getMessage("export_host_machine_type"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_name"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_instance_uuid"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_status"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_host"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_guest_os"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_xpath"),
				EdgeCMWebServiceMessages.getMessage("export_host_vm_uuid"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_host_name"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_username"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_password"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_protocol"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_port"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_server_type"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_scoket_count"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_essential"),
				EdgeCMWebServiceMessages.getMessage("export_host_hypervisor_visible"),
				EdgeCMWebServiceMessages.getMessage("export_host_port"),
				EdgeCMWebServiceMessages.getMessage("export_host_protocol")
		});
		csvObject.setCSVNodes(getExportNodeList(groupID,grouptype,nodeFilter));
		return csvObject;
	}
	
	@Override
	public VsphereEntity getVcloudResource(VsphereEntity vcloudEntity)
			throws EdgeServiceFault {
		return esxService.getVcloudResource(vcloudEntity);
	}
	
	@Override
	public List<ProtectedResource> getProtectedResources(List<ProtectedResourceIdentifier> resourceIds)
			throws EdgeServiceFault {
		List<EdgeProtectedResource> resources = new LinkedList<EdgeProtectedResource>();
		StringBuilder nodeIds=new StringBuilder();
		StringBuilder groupIds = new StringBuilder();
		for(ProtectedResourceIdentifier identifier : resourceIds){
			if(identifier.getType() == ProtectedResourceType.node){
				nodeIds.append(identifier.getId()).append(" ");
			}else if (identifier.getType() == ProtectedResourceType.group_esx) {
				groupIds.append(identifier.getId()).append(" ");
			}
		}
		hostMgrDao.as_edge_protected_resource_list_by_ids(nodeIds.toString(),groupIds.toString(), resources);
		
		List<ProtectedResource> result = new ArrayList<ProtectedResource>();
		for(EdgeProtectedResource resource : resources){
			ProtectedResource protectedResource = convertResourceFromDao(resource);
			result.add(protectedResource);
		}
		
		return result;
	}
	
	private ProtectedResource convertResourceFromDao(EdgeProtectedResource daoData){
		ProtectedResource resource = new ProtectedResource();
		ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
		identifier.setId(daoData.getId());
		if(daoData.getResourceType() == -1)
			identifier.setType(ProtectedResourceType.node);
		else{
			identifier.setType(ProtectedResourceType.group_esx);
		}
		resource.setIdentifier(identifier);
		resource.setName(daoData.getName());
		resource.setVmName(daoData.getVmName());
		resource.setHypervisor(daoData.getHypervisor());
		resource.setPlanName(daoData.getPlanName());
		resource.setSiteName(daoData.getSiteName());
		
		if(daoData.getDescription() == null)
			resource.setDescription("");
		else
			resource.setDescription(daoData.getDescription());
		return resource;
	}
	
	@Override
	public PagingResult<NodeEntity> getPagingNodes(NodeGroup group, List<NodeFilter> nodeFilters, SortablePagingConfig<NodeSortCol> config) throws EdgeServiceFault {
		// node group
		NodeFilterResult filterResult = NodeGroupFilterLoader.create(group).load(group);
		
		// all node filters
		if (nodeFilters != null) {
			for (NodeFilter filter : nodeFilters) {
				NodeFilterResult result = NodeFilterLoader.create(filter.getType()).load(filter);
				filterResult.intersect(result);
			}
		}
		
		// sort and merge filtered result
		List<Integer> sortedHostIds = NodeSortLoader.create(config.getSortColumn()).load(config.isAsc());
		if (filterResult.isFiltered()) {
			Iterator<Integer> iterator = sortedHostIds.iterator();
			while (iterator.hasNext()) {
				if (!filterResult.getFilteredHostIds().contains(iterator.next())) {
					iterator.remove();
				}
			}
		}
		
		PagingResult<NodeEntity> pagingResult = new PagingResult<NodeEntity>();
		pagingResult.setStartIndex(config.getStartIndex());
		pagingResult.setTotalCount(sortedHostIds.size());

		if (config.getCount() < 1) {
			return pagingResult;
		}
		
		if (config.getStartIndex() < 0) {
			config.setStartIndex(0);
		}
		
		int endIndex = config.getStartIndex() + config.getCount();
		if (endIndex > pagingResult.getTotalCount()) {
			endIndex = pagingResult.getTotalCount();
		}
		
		// paging data
		List<Integer> pagingHostIds = sortedHostIds.subList(config.getStartIndex(), endIndex);
		Map<Integer, NodeSummary> nodeSummaries = getNodeSummaries(pagingHostIds);
		Map<Integer, VmInfoSummary> vmInfoSummaries = getVmInfoSummaries(pagingHostIds);
		Map<Integer, ArcserveInfoSummary> arcserveInfoSummaries = getArcserveInfoSummaries(pagingHostIds,group.getType(),group.getId());
		Map<Integer, D2DInfoSummary> d2dInfoSummaries = getD2DInfoSummaries(pagingHostIds);
		Map<Integer, PlanSummary> planSummaries = getPlanSummaries(pagingHostIds);
		Map<Integer, JobSummary> jobSummaries = getJobSummaries(pagingHostIds);
		Map<Integer, RemoteDeployInfoSummary> remoteDeployInfoSummaries = getRemoteDeployInfoSummaries(pagingHostIds);
		Map<Integer, ConverterSummary> converterSummaries = getConverterSummaries(pagingHostIds);
		Map<Integer, GatewaySummary> gatewaySummaries = getGatewaySummaries(pagingHostIds);
		Map<Integer, D2DStatusInfo> vsbStatusMap = getVsbStatus(pagingHostIds);
		Map<Integer, ProxyInfoSummary> proxyInfoMap = getProxyInfoSummaries(pagingHostIds);
		Map<Integer, LinuxD2DInfoSummary> linuxD2DInfoMap = getLinuxD2DInfoSummaries(pagingHostIds);
		
		for (Integer id : pagingHostIds) {
			NodeEntity nodeEntity = new NodeEntity();
			if (nodeSummaries.containsKey(id)) {
				nodeEntity.setNodeSummary(nodeSummaries.get(id));
			}
			if(vmInfoSummaries.containsKey(id)){
				nodeEntity.setVmInfoSummary(vmInfoSummaries.get(id));
			}
			if(arcserveInfoSummaries.containsKey(id)){
				nodeEntity.setArcserveInfoSummary(arcserveInfoSummaries.get(id));
			}
			if(d2dInfoSummaries.containsKey(id)){
				nodeEntity.setD2dInfoSummary(d2dInfoSummaries.get(id));
			}
			if(planSummaries.containsKey(id)){
				nodeEntity.setPlanSummary(planSummaries.get(id));
			}
			if(jobSummaries.containsKey(id)){
				nodeEntity.setJobSummary(jobSummaries.get(id));
			}
			if(remoteDeployInfoSummaries.containsKey(id)){
				nodeEntity.setRemoteDeployInfoSummary(remoteDeployInfoSummaries.get(id));
			}
			if(converterSummaries.containsKey(id)){
				nodeEntity.setConverterSummary(converterSummaries.get(id));
			}
			if(gatewaySummaries.containsKey(id)){
				nodeEntity.setGatewaySummary(gatewaySummaries.get(id));
			}
			if(vsbStatusMap.containsKey(id) && vsbStatusMap.get(id).getHasVSBStatusInfo() != 0){
				VsbSummary summary = new VsbSummary();
				summary.setVsbSatusInfo(vsbStatusMap.get(id));
				nodeEntity.setVsbSummary(summary);
			}
			if(proxyInfoMap.containsKey(id)){
				nodeEntity.setProxyInfoSummary(proxyInfoMap.get(id));
			}
			
			if(linuxD2DInfoMap.containsKey(id)){
				nodeEntity.setLinuxD2DInfoSummary(linuxD2DInfoMap.get(id));
			}
			
			pagingResult.getData().add(nodeEntity);
		}
		
		return pagingResult;
	}
	
	private Map<Integer, NodeSummary> getNodeSummaries(List<Integer> pagingHostIds) throws EdgeServiceFault{
		List<NodeSummary> summaries = new ArrayList<NodeSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getNodeSummaries(hostIdArray, summaries);
		
		Map<Integer, NodeSummary> result = new HashMap<Integer, NodeSummary>();
		for (NodeSummary summary : summaries) {
			// banar05
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String CurrentConsoleHostName = "";
			String CurrentConsoleHostIp = "";
			if(ip!=null)
			{
				CurrentConsoleHostName = ip.getHostName();
				CurrentConsoleHostIp = ip.getHostAddress();
			}
			summary.setCurrentConsoleMachineNameForCollectDiag(CurrentConsoleHostName);
			summary.setCurrentConsoleIPForCollectDiag(CurrentConsoleHostIp);
			result.put(summary.getId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, VmInfoSummary> getVmInfoSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<VmInfoSummary> summaries = new ArrayList<VmInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getVmInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, VmInfoSummary> result = new HashMap<Integer, VmInfoSummary>();
		for (VmInfoSummary summary : summaries) {
			summary.setEsxName(summary.getHypervisor());
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(summary.getHostId(), vmList);
			if (vmList.size()>0){
				String newHyperVisor = getHyperVisorInfo(vmList.get(0));
				if(!StringUtil.isEmptyOrNull(newHyperVisor)){
					summary.setHypervisor(newHyperVisor);
					summary.setEsxName(getVcenterInfo(vmList.get(0)));
				}
			}
			
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, ProxyInfoSummary> getProxyInfoSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<ProxyInfoSummary> summaries = new ArrayList<ProxyInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getProxyInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, ProxyInfoSummary> result = new HashMap<Integer, ProxyInfoSummary>();
		for (ProxyInfoSummary summary : summaries) {
			result.put(summary.getVmHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, LinuxD2DInfoSummary> getLinuxD2DInfoSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<LinuxD2DInfoSummary> summaries = new ArrayList<LinuxD2DInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getLinuxD2DInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, LinuxD2DInfoSummary> result = new HashMap<Integer, LinuxD2DInfoSummary>();
		for (LinuxD2DInfoSummary summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, ArcserveInfoSummary> getArcserveInfoSummaries(List<Integer> pagingHostIds,int groupType,int groupId)throws EdgeServiceFault{
		List<ArcserveInfoSummary> summaries = new ArrayList<ArcserveInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getArcserveInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, ArcserveInfoSummary> result = new HashMap<Integer, ArcserveInfoSummary>();
		for (ArcserveInfoSummary summary : summaries) {
			if (groupType == NodeGroup.GDB) {
				summary.setGdbId(groupId);
			}
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, D2DInfoSummary> getD2DInfoSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<D2DInfoSummary> summaries = new ArrayList<D2DInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getD2DInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, D2DInfoSummary> result = new HashMap<Integer, D2DInfoSummary>();
		for (D2DInfoSummary summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, PlanSummary> getPlanSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<PlanSummary> summaries = new ArrayList<PlanSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getPlanSummaries(hostIdArray, summaries);
		
		Map<Integer, UnifiedPolicy> policMap = new HashMap<Integer, UnifiedPolicy>();
		
		Map<Integer, PlanSummary> result = new HashMap<Integer, PlanSummary>();
		for (PlanSummary summary : summaries) {
			// check whether has CrossSiteVsb
			if(Utils.hasBit(summary.getContentFlag(), PlanTaskType.LocalConversion)
					&&Utils.hasBit(summary.getContentFlag(), PlanTaskType.Replication)){
				UnifiedPolicy policy = null;
				if(policMap.containsKey(summary.getId())){
					policy = policMap.get(summary.getId());
				} else {
					policy = PolicyManagementServiceImpl.getInstance().loadUnifiedPolicyById(summary.getId());
				}
				if(policy != null && policy.getConversionConfiguration() != null){
					ConversionTask task = policy.getConversionConfiguration();
					summary.setHasCrossSiteVsb(task.isSourceTaskRemoteReplicate());
				}
			}
			result.put(summary.getHostId(), summary);
		}
		policMap.clear();
		
		return result;
	}
	
	private void setJobRunningForNodes(List<JobSummary> summaries){
		if(summaries == null || summaries.isEmpty()) 
			return;		
		// Get all jobMonitor form DB
		List<EdgeJobHistory> lstJobHistory = new ArrayList<EdgeJobHistory>();
		historyDao.as_edge_d2dJobHistory_monitor_getJobMonitor(-1, -1, "", lstJobHistory);
		if(lstJobHistory==null||lstJobHistory.isEmpty())
			return;
		Map<String, Long> statusMap = new HashMap<String, Long>();
		for(EdgeJobHistory monitor:lstJobHistory){
			if(statusMap.containsKey(monitor.getAgentId())){
				long oldStatus = statusMap.get(monitor.getAgentId());
				if(oldStatus == JobStatus.Waiting.getValue() 
						&& monitor.getJobStatus() != oldStatus){
					statusMap.put(monitor.getAgentId(), monitor.getJobStatus());	
				}
			} else {
				statusMap.put(monitor.getAgentId(), monitor.getJobStatus());			
			}
		}
		for (JobSummary node : summaries) {
			String key = ""+node.getHostId();			
			if(!statusMap.containsKey(key))
				continue;
			long status = statusMap.get(key);
			if(status == JobStatus.Waiting.getValue()){
				node.setWaittingJobToRun(true);
			} else {
				node.setJobRunning(true);
			}
		}
				
	}
	
	private Map<Integer, JobSummary> getJobSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<JobSummary> summaries = new ArrayList<JobSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getJobSummaries(hostIdArray, summaries);
		
		Map<Integer, JobSummary> result = new HashMap<Integer, JobSummary>();
		
		// set jobRunFlag not use D2DAllJobStatusCache,should use DB table as_edge_d2dJobhistory_jobMonitor
		setJobRunningForNodes(summaries);
			
		for (JobSummary summary : summaries) {
			/* // set jobRunFlag not use  D2DAllJobStatusCache,should use DB table as_edge_d2dJobhistory_jobMonitor
			List<FlashJobMonitor> jobMonitors = D2DAllJobStatusCache.getD2DAllJobStatusCache().getJobStatusInfoList(summary.getJobMonitorKey());
			for (FlashJobMonitor flashJobMonitor : jobMonitors) {
				if(flashJobMonitor.isPendingJobMonitor()) {
					summary.setWaittingJobToRun(true);
				} else {
					summary.setJobRunning(true);
					break;
				}
			}*/
			summary.setLatestJobHistories(getLatestJobHistoriesByNodeId(summary.getHostId()));
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	@Override
	public List<EdgeHostBackupStats> getBackupStats(int offSet) throws EdgeServiceFault {
		List<EdgeHostBackupStats> result = new ArrayList<EdgeHostBackupStats>();
		historyDao.as_edge_get_backup_stats(offSet, result);
		return result;
	}
	
	private Map<Integer, RemoteDeployInfoSummary> getRemoteDeployInfoSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<RemoteDeployInfoSummary> summaries = new ArrayList<RemoteDeployInfoSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getRemoteDeployInfoSummaries(hostIdArray, summaries);
		
		Map<Integer, RemoteDeployInfoSummary> result = new HashMap<Integer, RemoteDeployInfoSummary>();
		for (RemoteDeployInfoSummary summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		
		return result;
	}
	
	private Map<Integer, ConverterSummary> getConverterSummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<ConverterSummary> summaries = new ArrayList<ConverterSummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getConverterSummaries(hostIdArray, summaries);
		
		Map<Integer, ConverterSummary> result = new HashMap<Integer, ConverterSummary>();
		for (ConverterSummary summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		return result;
	}
	
	private Map<Integer, GatewaySummary> getGatewaySummaries(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<GatewaySummary> summaries = new ArrayList<GatewaySummary>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getGatewaySummaries(hostIdArray, summaries);
		
		Map<Integer, GatewaySummary> result = new HashMap<Integer, GatewaySummary>();
		for (GatewaySummary summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		return result;
	}
	
	private Map<Integer, D2DStatusInfo> getVsbStatus(List<Integer> pagingHostIds)throws EdgeServiceFault{
		List<D2DStatusInfo> summaries = new ArrayList<D2DStatusInfo>();
		String hostIdArray = SqlUtil.marshal(pagingHostIds);
		hostMgrDao.as_edge_host_getVsbStatus(hostIdArray, summaries);
		
		Map<Integer, D2DStatusInfo> result = new HashMap<Integer, D2DStatusInfo>();
		for (D2DStatusInfo summary : summaries) {
			result.put(summary.getHostId(), summary);
		}
		return result;
	}
	
	public void saveVMToDB(int gatewayid, int esxServerId,int vmHostId, String vmInstanceUUID, String vmHostName, String vmName, 
			String vmUUID, String vmEsxHost, String vmVMX, String vmGuestOs,String vmUserName,String vmPassword,
			int vmProtocol,int vmPort,int isVisble,String description,boolean changeHypervisor){
		logger.debug("[NodeserviceImpl] saveVMToDB gatewayid is : "+ gatewayid +" esxServerId is: "
			+esxServerId +" vmHostId is: "+vmHostId +" vmInstanceUUID is: "+vmInstanceUUID
			+" vmHostName is: "+vmHostName+" vmName is: "+vmName +" vmUUID is: "+vmUUID +" vmEsxHost is: "+vmEsxHost
			+" vmVMX is: "+vmVMX +" vmGuestOs is: "+vmGuestOs + " Change hypervisor: "+changeHypervisor);
		//save to esx table
		List<EdgeEsx> resourceList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getVsphereEntityByUuidAndName(gatewayid, vmInstanceUUID,vmHostName,resourceList);
		int entityId = resourceList.isEmpty()?0:resourceList.get(0).getId();
		int[] entityOutput = new int[1];
		esxDao.as_edge_esx_update(entityId, 
				vmHostName,
				vmUserName, 
				vmPassword,
				vmProtocol,
				vmPort,
				VsphereEntityType.vm.getValue(),
				isVisble,
				description,
				vmInstanceUUID,
				entityOutput);
		if(entityId==0){
			entityId = entityOutput[0];
			gatewayService.bindEntity(new GatewayId(gatewayid), entityId, EntityType.VSphereEntity);
		}
		
		int[] dbEsxId = new int[1];
		esxDao.as_edge_esx_getESXIdByVMUUID(vmInstanceUUID, dbEsxId);
		int originalEsxServerId = dbEsxId[0];
		
		List<EdgeEsxVmInfo> vmList = new LinkedList<>();
		esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(vmHostId, vmList);
		
		int deleteFlag = IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE;
		if(!vmList.isEmpty() && !StringUtil.isEmptyOrNull(vmList.get(0).getVmInstanceUuid())){
			deleteFlag = vmList.get(0).getStatus();
		}
		//If vm exsit, just only its parent esxServer can update vm_detail
		//issue 762808
		if(originalEsxServerId == esxServerId || changeHypervisor){
			deleteFlag = IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE;
		}
		
		//save to vm detail table
		esxDao.as_edge_vsphere_vm_detail_update(entityId, deleteFlag, vmName, 
				vmUUID,vmEsxHost, vmVMX, vmGuestOs);
		
		//save to , esx_vmEntity_map table
		//add node can change hypervisor, but autodiscovery cann't change hypervisor
		if(entityId!=0 && esxServerId != 0){
			if(changeHypervisor){
				esxDao.as_edge_vsphere_entity_map_update(entityId, esxServerId,  VsphereEntityRelationType.child_parent.ordinal());
			}else {
				esxDao.as_edge_vsphere_entity_map_insert(entityId, esxServerId,  VsphereEntityRelationType.child_parent.ordinal());
			}
		}
		
		//save to entity_host_map table
		if(entityId!=0 && vmHostId!=0)
			esxDao.as_edge_vsphere_entity_host_map_update(vmHostId,entityId);
	}
	
	public void saveVMToDB_NoUuid(int gatewayid, int esxServerId,int vmHostId, String vmInstanceUUID, String vmHostName, String vmName, 
			String vmUUID, String vmEsxHost, String vmVMX, String vmGuestOs,String vmUserName,String vmPassword,
			int vmProtocol,int vmPort,int isVisble,String description){
		//save to esx table
		List<EdgeEsx> resourceList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getEntityByHostId(vmHostId,resourceList);
		int entityId = resourceList.isEmpty()?0:resourceList.get(0).getId();
		int[] entityOutput = new int[1];
		esxDao.as_edge_esx_update(entityId, 
				null,
				vmUserName, 
				vmPassword,
				vmProtocol,
				vmPort,
				VsphereEntityType.vm.getValue(),
				isVisble,
				description,
				null,
				entityOutput);
		if(entityId==0){
			entityId = entityOutput[0];
			gatewayService.bindEntity(new GatewayId(gatewayid), entityId, EntityType.VSphereEntity);
		}
		//save to vm detail table
		if(entityId !=0)
			esxDao.as_edge_vsphere_vm_detail_update(entityId, IEdgeEsxDao.ESX_HOST_STATUS_VISIBLE, null, 
					null,vmEsxHost, vmVMX, vmGuestOs);
				
		//save to , esx_vmEntity_map table
		if(entityId!=0 && esxServerId != 0)
			esxDao.as_edge_vsphere_entity_map_insert(entityId, esxServerId,  VsphereEntityRelationType.child_parent.ordinal());
				
		//save to entity_host_map table
		if(entityId!=0 && vmHostId!=0)
			esxDao.as_edge_vsphere_entity_host_map_update(vmHostId,entityId);
	}
	
	@Override
	public List<NodeVcloudSummary> getVcloudPropertiesByNodeIds(
			List<Integer> nodeIds) throws EdgeServiceFault {
		List<NodeVcloudSummary> vcloudSummaries = new ArrayList<NodeVcloudSummary>();
		String hostIdArray = SqlUtil.marshal(nodeIds);
		hostMgrDao.as_edge_host_getVcloudSummaries(hostIdArray, vcloudSummaries);
		return vcloudSummaries;
	}
	
	@Override
	public EsxServerInformation getEsxServerInformation(DiscoveryESXOption esxOption) throws EdgeServiceFault {
//		return esxService.getEsxServerInformation(esxOption);
		logger.debug("DiscoveryESXOption: "+InstantVMServiceUtil.printObject(esxOption));
		EsxServerInformation info = esxService.getEsxServerInformation(esxOption);
		logger.debug("getEsxServerInformation() return: "+InstantVMServiceUtil.printObject(info));
		return info;
	}
	
	@Override
	public EsxHostInformation getEsxHostInformation(DiscoveryESXOption esxOption, VWWareESXNode esxNode) throws EdgeServiceFault {
//		return esxService.getEsxHostInformation(esxOption, esxNode);
		logger.debug("DiscoveryESXOption: "+InstantVMServiceUtil.printObject(esxOption));
		logger.debug("VWWareESXNode: "+InstantVMServiceUtil.parseObjectToXmlString(esxNode));
		EsxHostInformation info = esxService.getEsxHostInformation(esxOption, esxNode);
		logger.debug("getEsxHostInformation() return: "+InstantVMServiceUtil.parseObjectToXmlString(info));
		return info;
	}
	
	public void addOrUpdateDiscoveryForHyperV(DiscoverySettingForHyperV setting) throws EdgeServiceFault {
		if (setting == null || setting.getHostname() == null || setting.getHostname().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "");
		}
		
		if (setting.getId() == 0) { // Add
			List<EdgeHyperV> hyperVNameList = new LinkedList<EdgeHyperV>();
			hyperVDao.as_edge_hyperv_getByName(setting.getGatewayId().getRecordId(), setting.getHostname(), hyperVNameList);
			if (!hyperVNameList.isEmpty() && hyperVNameList.get(0).getVisible() != -1) {
				if(hyperVNameList.get(0).getIsAutoDiscovery()==0){
					setAutoDiscoveryForHyperv(hyperVNameList.get(0).getId(), 1);
					setting.setId(hyperVNameList.get(0).getId());
				}else {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HyperV_Host_Exist, "");
				}
			}
		} else { // Update
			List<EdgeHyperV> hyperVList = new ArrayList<EdgeHyperV>();
			hyperVDao.as_edge_hyperv_getById(setting.getId(), hyperVList);
			EdgeHyperV hyperV = hyperVList.get(0);
			if (!hyperV.getHostname().equalsIgnoreCase(setting.getHostname())) {
				List<EdgeHyperV> hyperVNameList = new LinkedList<EdgeHyperV>();
				hyperVDao.as_edge_hyperv_getByName(setting.getGatewayId().getRecordId(), setting.getHostname(), hyperVNameList);
				if (!hyperVNameList.isEmpty() && hyperVNameList.get(0).getVisible() != -1) {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_HyperV_Host_Exist, "");
				}
			}
		} 
		DiscoveryHyperVOption hyperVOption = new DiscoveryHyperVOption();
		hyperVOption.setServerName(setting.getHostname());
		hyperVOption.setUsername(setting.getUsername());
		hyperVOption.setPassword(setting.getPassword());
		hyperVOption.setHypervProtectionType(setting.getHypervType());
		hyperVOption.setCluster(setting.getHypervType().getValue() == HypervProtectionType.CLUSTER.getValue());
		hyperVOption.setGatewayId(setting.getGatewayId());
		DiscoveryService.getInstance().validateHyperVAccount(hyperVOption);
		int[] output = new int[1];
		hyperVDao.as_edge_hyperv_update(setting.getId(), 
				setting.getHostname(), 
				setting.getUsername(), 
				setting.getPassword(), 
				0, 
				0,
				1,
				setting.getHypervType().getValue() == HypervProtectionType.CLUSTER.getValue() ? HypervProtectionType.CLUSTER.getValue() : HypervProtectionType.STANDALONE.getValue(),
				output);
		if (setting.getId() == 0)
			this.gatewayService.bindEntity( setting.getGatewayId(), output[0], EntityType.HyperVServer );
	}
	
	@Override
	public HostConnectInfo getD2DConnectionInfo() throws EdgeServiceFault {
		HostConnectInfo connectInfo = new HostConnectInfo();
		List<EdgeHost> linuxD2DList = new LinkedList<EdgeHost>();
		hostMgrDao.as_edge_host_list(NodeGroup.LinuxD2D, 1, linuxD2DList);
		if (linuxD2DList.size() < 1) {
			throw EdgeServiceFault
					.getFault(
							EdgeServiceErrorCode.Node_Linux_No_Available_D2D_Server,
							"");
		}
		EdgeHost edgeHost = linuxD2DList.get(0);
		List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
		connectionInfoDao.as_edge_connect_info_list(edgeHost.getRhostid(),
				connInfoLst);
		if (connInfoLst.size() < 1) {
			throw EdgeServiceFault
					.getFault(
							EdgeServiceErrorCode.Node_Linux_No_Available_D2D_Server,
							"");
		}
		EdgeConnectInfo edgeConnectInfo = connInfoLst.get(0);
		connectInfo.setUuid(edgeConnectInfo.getAuthUuid());
		connectInfo.setHostName(edgeConnectInfo.getRhostname());
		connectInfo.setPort(edgeConnectInfo.getPort());
		connectInfo.setProtocol(Protocol.parse(edgeConnectInfo.getProtocol()));
		return connectInfo;
	}
	
	@Override
	public int validateProxyInfo( GatewayId gatewayId, String hostName, String protocol, int port,
		String userName, String password, boolean isUseTimeRange,
		boolean isUseBackupSet ) throws EdgeServiceFault
	{
		IRemoteNodeServiceFactory serviceFactory = EdgeFactory.getBean( IRemoteNodeServiceFactory.class );
		IRemoteNodeService remoteService = serviceFactory.createRemoteNodeService( gatewayId );
		return remoteService.validateProxyInfo(
			hostName, protocol, port, userName, password, isUseTimeRange, isUseBackupSet );
	}
	
	@Override
	public PagingResult<DiscoveredNode> getDiscoveryADResult(
			DiscoveredNodeFilter filter, SortablePagingConfig<Integer> config)
			throws EdgeServiceFault {
		return adService.getDiscoveryADResult(filter, config);
	}
	
	@Override
	public List<RecoveryPointDataItem> getRecoveryPointData() throws EdgeServiceFault {
		List<RecoveryPointDataItem>  datas = new ArrayList<RecoveryPointDataItem>(); 
		Date serverDate = new Date();
		Date startDate = new Date(serverDate.getYear(), serverDate.getMonth(), serverDate.getDate() - 6);
		Date endDate = new Date(serverDate.getYear(), serverDate.getMonth(), serverDate.getDate() + 1);

		historyDao.getRecoveryPointDatas(startDate, endDate, TimeZone.getDefault().getRawOffset(), datas);
		
		for(RecoveryPointDataItem item: datas){
			item.setServerDate(CommonUtil.fromServerDate(item.getExecDate()));
		}		

		return datas;
	}
	
	@Override
	public List<RecoveryPointDataItem> getD2DBackupData() throws EdgeServiceFault {
		List<RecoveryPointDataItem>  datas = new ArrayList<RecoveryPointDataItem>(); 
		historyDao.getD2DBackupData(TimeZone.getDefault().getRawOffset(), datas);
		for(RecoveryPointDataItem item: datas){
			item.setServerDate(CommonUtil.fromServerDate(item.getExecDate()));
		}		
		return datas;
	}
	
	@Override
	public long getRPSDatastoreVolumeMaxSize() throws EdgeServiceFault{
		long[] maxSize = new long[1];
		historyDao.as_edge_getRpsVolume_MaxSize(maxSize);
		
		return maxSize[0];
	}
	
	@Override
	public void changeNodesCredentials(List<Integer> nodeIds, String userName,
			@NotPrintAttribute String password) throws EdgeServiceFault {
		for (int hostId : nodeIds) {
			if(hostId == 0)
				continue;
			connectionInfoDao.as_edge_connect_info_update_credential(hostId, userName, password);//change credential to DB
			NodeDetail nodeDetailFromDB = getNodeDetailInformation(hostId);
			retrieveNodePolicyDetails(nodeDetailFromDB);
			boolean hasHBBUPlan = Utils.hasBit(nodeDetailFromDB.getPolicyContentFlag(), PlanTaskType.WindowsVMBackup);
			if ((nodeDetailFromDB.isVMwareMachine() || nodeDetailFromDB.isHyperVMachine())) {
				try{				
					List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>(1);
					policyDao.getHostPolicyMap(hostId, PolicyTypes.Unified, mapList);
					
					if (mapList.size()>0){
						int policyId = mapList.get(0).getPolicyId();
						if (hasHBBUPlan) {
							serviceImpl.redeployPolicyToNodes(Arrays.asList(hostId), PolicyTypes.Unified, policyId);
						}
					}
				}catch(Exception e){
					logger.error("changeNodesCredentials, redeploy hbbu plan failed.",e);
				}
			}
		}
	}
	
	@Override
	public BackupStatusByGroup getLastBackupStatusByGroup(int groupType, int groupId) throws EdgeServiceFault {
		BackupStatusByGroup result= null ;
		NodePagingConfig pagingConfig = new NodePagingConfig();
		pagingConfig.setPagesize(2500);
		pagingConfig.setStartpos(0);
		pagingConfig.setOrderType(EdgeSortOrder.ASC);
		pagingConfig.setOrderCol(NodeSortCol.lastBackupResult);
		NodePagingResult pagingResult = this.getNodesESXByGroupAndTypePaging(groupId, groupType, new EdgeNodeFilter(), pagingConfig);
		List<Node> allNodes = pagingResult.getData();
		
		if(allNodes.size()>0){
			result = new BackupStatusByGroup();
			for(Node node: allNodes){
//				String policyName = node.getPolicyName();
//				if(StringUtil.isEmptyOrNull(policyName)){
//					result.addNotAssignedNumber();
//				} else { 
					if (node.getLstJobHistory() == null || node.getLstJobHistory().isEmpty()) {
						result.addNoBkpJobHistoryNumber();
					} else {
						JobHistory item = NodeStatusUtil.getBackupJobHistory(node.getLstJobHistory());
						if(item == null){
							result.addNoBkpJobHistoryNumber();
						} else {
							if(item.getJobStatus() == JobStatus.Finished || item.getJobStatus() == JobStatus.BackupJob_PROC_EXIT){  
								result.addSuccessfulNumber();
							} else {
								if(item.getJobStatus() == JobStatus.Missed || item.getJobStatus() == JobStatus.Skipped){
									result.addMissedNumber();
								} else {
									if(item.getJobStatus() == JobStatus.Canceled){
										result.addCanceledNumber();
									} else {
										if(item.getJobStatus() == JobStatus.Failed || item.getJobStatus() == JobStatus.Crash ||
												item.getJobStatus() == JobStatus.LicenseFailed){
											result.addFailedNumber();
										} else {
											logger.info("the backup Job Status is not in these 5 types.");
										}
									}
								}
							}
						}
					}
//				}
			}
		}
		return result;
	}
	
	
	@Override
	public NodeDetail getNodetailByIpOrHostName(HypervisorWrapper hw)  throws EdgeServiceFault{
		String hostName="";
		String ip="";
		Pattern pattern = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

		if(pattern.matcher(hw.getHyperVisor().getServerName()).find()){
			ip = hw.getHyperVisor().getServerName();
		}else{
			hostName = hw.getHyperVisor().getServerName();
		}
		
		//get host id by hostname or ip
		int[] output = new int[1];
		hostMgrDao.as_edge_host_getIdByHostnameOrIp(hostName, ip,  output);
		int hostId = output[0];
		
		if(hostId!=0){
			NodeDetail nodeDetail = new NodeDetail();
			try {
				
				// get node detail by host id
				nodeDetail = getNodeDetailInformation(hostId);
				return nodeDetail;
			} catch (EdgeServiceFault e1) {
				// TODO Auto-generated catch block
				throw EdgeServiceFault.getFault("", "get node detail error");
			}
		}
		return null;
	}
	
	@Override
	public MonitorHyperVInfo getAdapterForInstantVM(HypervisorWrapper hw) throws EdgeServiceFault{
		NodeDetail nodeDetail = new NodeDetail();
		nodeDetail = getNodetailByIpOrHostName(hw);
		if(nodeDetail!=null){
			//if already get the node detail ,we don't need to validate account?
//			{
//				try {
//					validateAdminAccount(hw.getHyperVisor().getGatewayId(), hw.getHyperVisor().getServerName(), 
//							hw.getHyperVisor().getUsername(), hw.getHyperVisor().getPassword());
//				} catch (EdgeServiceFault e2) {
//					if("8589934594".equals(e2.getFaultInfo().getCode()) ||
//							"8589934593".equals(e2.getFaultInfo().getCode())){
//						logger.error("require admin or invalid password.");
//					}
//					return null;
//				}
//				
//				tryConnectD2D(gatewayId, d2dProtocol, d2dHost, d2dPort, d2dUserName, d2dPassword);
//			}
			
			
			
			HostConnectInfo hypervConnIfo = new HostConnectInfo();
			hypervConnIfo.setHostId(Integer.valueOf(nodeDetail.getNodeIdString()));
			hypervConnIfo.setHostName(nodeDetail.getHostname());
			hypervConnIfo.setUserName(nodeDetail.getUsername());
			hypervConnIfo.setPassword(nodeDetail.getPassword());
			hypervConnIfo.setProtocol(nodeDetail.getD2dConnectInfo().getProtocol());
			hypervConnIfo.setPort(nodeDetail.getD2dConnectInfo().getPort());
			try {
				
				// test agent connection
				vcmService.testMonitorConnection(hypervConnIfo);
			} catch (EdgeServiceFault e) {
				logger.error("connect to hyperV agent error.");
				throw EdgeServiceFault.getFault("", "connect to hyperV agent error.");
			}
			
			try {
				
				// get result through agent service
				MonitorHyperVInfo result = vcmService.getMonitorHyperVInfo(hypervConnIfo);
				return result;
			} catch (EdgeServiceFault e) {
				// TODO Auto-generated catch block
				throw EdgeServiceFault.getFault("", "get hyper-v adaptor error");
			}
			
		}else{
			
			throw EdgeServiceFault.getFault("", "don't install hyper-v");
		}
		
	}
	
	@Override
	public Volume[] getVolumes4VM(ConnectionContext context) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			return connection.getService().getVolumes();
		}
	}
	
	@Override
	public void cutAllRemoteConnections4VM(ConnectionContext context) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			connection.getService().cutAllRemoteConnections();
		}
	}
	
	@Override
	public FileFolderItem getFileItems4VM(ConnectionContext context, String inputFolder, String username, String password, boolean bIncludeFiles, int browseClient) throws EdgeServiceFault{
		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}
		
		if (inputFolder != null && inputFolder.endsWith("\\") && !inputFolder.endsWith("\\\\"))
			inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("\\"));
			
		if (inputFolder != null && inputFolder.endsWith("/"))
			inputFolder = inputFolder.substring(0, inputFolder.lastIndexOf("/"));
			
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
//			EdgeRpsNode rpsNode= EdgeCommonUtil.getRPSNodeInfo(nodeID, rpsNodeDao);
			
//			if (browseClient == 2) {
//				username = rpsNode.getUsername();
//				password = rpsNode.getPassword();
//			}
			
			FileFolderItem item = connection.getService().getFileFolderWithCredentials(inputFolder, username, password);
//			FileFolderItem item = connection.getService().getFileFolder(inputFolder);
			if(item.getFolders()!=null && (item.getFolders()).length!=0){
				for(int i=0; i<item.getFolders().length; i++){
					(item.getFolders())[i].setPath((item.getFolders())[i].getPath().replace(":\\\\", ":\\"));
				}
			}
			return item;
		}
	}
	
	//delete parameter:,String username, String password
	@Override
	public void createFolder4VM(ConnectionContext context, String parentPath, String subDir) throws EdgeServiceFault {
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
//			connection.getService().createFolderWithDetails(parentPath, subDir, username, password);
			connection.getService().createFolder(parentPath, subDir);
		}
	}
	// May sprint
	@Override
	public int triggerCollectDiagnosticData(Node node, DiagInfoCollectorConfiguration diagObj)
			throws EdgeServiceFault {
		logger.info("coming to nodeserviceimpl, node id is: " + node.getId());
		if(node.isD2dInstalled() && (Integer.parseInt(node.getD2DMajorversion()) >= 6)){
			logger.info("Agent is installed. Hence triggering data collection for the Agent");
			
			//save settings to console xml even it is agent or VM
			if(diagObj!=null){
				//save the password as encrypted
				/*String originalPwd = diagObj.getPassword();
				if(originalPwd!=null && !originalPwd.isEmpty()){
					diagObj.setPassword(Base64.encode(originalPwd));
				}*/
				DiagInfoCollectorConfigurationXMLDAO xmlDao;
				try {
					xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
					logger.info("save diag settings in console xml - single windows node");
					xmlDao.save(logCollectorConfigXMLPath, diagObj);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			
			try (D2DConnection connection = connectionFactory.createD2DConnection(node.getId())) {
				connection.connect();
				int status =  connection.getService().collectDiagnosticInfo(diagObj);
				
			} catch (SOAPFaultException e) {
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(node.getHostname());
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				logService.addLog(log);
				logger.error("fail to trigger diagnostic data collection",e);
			} catch (WebServiceException e) {
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(node.getHostname());
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				//logService.addLog(log);
				//logger.error("webservice has timeout",e);
			} catch (EdgeServiceFault e) {
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(node.getHostname());
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				logService.addLog(log);
				logger.error("fail to trigger diagnostic data collection",e);
			}	
		}
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String CurrentHostName = "";
		String CurrentHostIp = "";
		if(ip!=null)
		{
			CurrentHostName = ip.getHostName();
			CurrentHostIp = ip.getHostAddress();
		}
		String hostnameFromNode = node.getHostname();
		if(node.isConsoleInstalled() || hostnameFromNode.equalsIgnoreCase(CurrentHostName) || hostnameFromNode.equalsIgnoreCase(CurrentHostIp)){
			logger.info("Console is installed. Hence triggering data collection for the console");
			
			/*String protocol = node.getConsoleProtocol() == 1 ? "http" : "https";
			ConnectionContext context = new ConnectionContext(protocol, node.getHostname(), Integer.parseInt( node.getConsolePort()));
			context.buildCredential(node.getUsername(), node.getPassword(), node.getDomainName());*/
			
			
			/*GatewayEntity gateway = this.gatewayService.getGatewayByHostId( node.getId());
			GatewayId gatewayId = gateway.getId();
			
			
			//scan remote node (registry)
			String edgeUser = null;
			String edgeDomain = null;
			String edgePassword = null;
			if ((serviceImpl != null) && (serviceImpl.getSession() != null)){
				edgeUser =(String) serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_USERNAME);
				edgeDomain =(String) serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_DOMAIN);
				edgePassword = (String)serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_PASSWORD);
			}else{
				EdgeAccount ea = new EdgeAccount();
				BaseWSJNI.getEdgeAccount(ea);
				edgeUser = ea.getUserName();
				edgeDomain = ea.getDomain();
				edgePassword = ea.getPassword();
			}
			
			
			logger.info("*************** calling  scanRemoteNode************************");
			RemoteNodeInfo remoteNodeInfo =  DiscoveryService.getInstance().scanRemoteNode(gatewayId, edgeUser, edgeDomain, edgePassword, node.getHostname(), node.getUsername(), node.getPassword());
			logger.info("***Remote Node d2d uuid is: " + remoteNodeInfo.getD2DUUID() );
			logger.info("console installed is: " + remoteNodeInfo.isConsoleInstalled());
			logger.info(remoteNodeInfo.getConsolePortNumber());
			logger.info(remoteNodeInfo.getConsoleProtocol());
			
			ConnectionContext context = new ConnectionContext(remoteNodeInfo.getConsoleProtocol(), node.getHostname(), remoteNodeInfo.getConsolePortNumber());
			context.buildCredential(node.getUsername(), node.getPassword(), node.getDomainName());
			context.setGateway(gateway);
			
			ConsoleConnection connection = connectionFactory.createConsoleConnection(context);
			connection.connect();
			
			BaseWebServiceClientProxy proxy = getConsoleConnection(context);
			
			IEdgeService edgeService = getService(proxy);
			
			edgeService.validateUserByUser(node.getUsername(), node.getPassword(), node.getDomainName());
			
			String msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecStart"));
			generateLog(Severity.Information, node, msg, null);*/
			
			String msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecStart"));
			generateLog(Severity.Information, node, msg, null);
			
			int status  = triggerCollectDiagnosticDataForConsoleNode(diagObj);
			
			logger.info("Return value of triggerCollectDiagnosticDataForConsoleNode is: " + status);
			
			if(status==0){
				msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecSuccess", diagObj.getUploadDestination()));
				generateLog(Severity.Information, node, msg, null);
			}
			else if(status==2)
			{
				msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecSuccessButFailedToCopyToNWshare", diagObj.getUploadDestination()));
				generateLog(Severity.Warning, node, msg, null);
			}
			else{
				msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFail", diagObj.getUploadDestination()));
				generateLog(Severity.Error, node, msg, null);
			}
	    	//return status;
		}
		
		
		return 0;
	}
	
	public BaseWebServiceClientProxy getConsoleConnection( ConnectionContext context )
	{
		/*BaseWebServiceClientProxy webService;
		BaseWebServiceFactory serviceFactory = new BaseWebServiceFactory();
		try {
			webService = serviceFactory.getEdgeWebService(
					context.getProtocol(), context.getHost(), context.getPort(), IEdgeService.class);
		}catch (Exception e) {
			logger.error(e);
			return null;
		}
		return webService;*/
		
		//Commented since due to TFS issue: 753616
		/*WebServiceFactory.setCONTEXT_PATH(CommonUtil.CENTRAL_MANAGER_CONTEXT_PATH);
        WebServiceFactory.setEndPointName("EdgeServiceConsoleImpl");
        BaseWebServiceFactory.setSubStaticWebServiceFactory(new WebServiceFactory());

        if(context.getProtocol().equalsIgnoreCase("https"))
        {
            try {
                CommonUtil.prepareTrustAllSSLEnv();
            } catch (KeyManagementException e) {
                logger.error(e);
            } catch (NoSuchAlgorithmException e) {
            	logger.error(e);
            } catch (KeyStoreException e) {
            	logger.error(e);
            }
        }
        BaseWebServiceClientProxy clientProxy = BaseWebServiceFactory.getService(context.getProtocol(), context.getHost(), context.getPort());
        
        return clientProxy;*/
		
		BaseWebServiceFactory serviceFactory = new BaseWebServiceFactory();
        
		BaseWebServiceClientProxy clientProxy = serviceFactory.getEdgeWebService(
				context.getProtocol(), context.getHost(), context.getPort(), IEdgeService.class);
		
		return clientProxy;

    }

	
	protected IEdgeService getService( BaseWebServiceClientProxy clientProxy )
	{
		return (IEdgeService) clientProxy.getService();
	}

	
	@Override
	public int triggerCollectDiagnosticDataForConsoleNode(DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault 
	{
		logger.info("Entered into triggerCollectDiagnosticDataForConsoleNode by connecting webservice");
		
		/*String msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecStart"));
		addActivityLog(CommonUtil.getLocalHost(), Severity.Information, msg);*/
		
		if(diagObj!=null){
			//save the password as encrypted
			/*String originalPwd = diagObj.getPassword();
			if(originalPwd!=null && !originalPwd.isEmpty()){
				diagObj.setPassword(Base64.encode(originalPwd));
			}*/
			DiagInfoCollectorConfigurationXMLDAO xmlDao;
			try {
				xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
				xmlDao.save(logCollectorConfigXMLPath, diagObj);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		
		String logCollectorUtilityBatch = logCollectorUtilityPath + "\\arcserveConsoleSupport.bat";
		logCollectorUtilityBatch = "\"" + logCollectorUtilityBatch + "\"";
		if(diagObj!=null){
			logger.warn("The config is not null hence sending xml as argument");
			logCollectorUtilityBatch = logCollectorUtilityBatch +  " -xmlConfig "  + "\"" + logCollectorConfigXMLPath + "\"";
			logCollectorUtilityBatch = "\"" + logCollectorUtilityBatch + "\"";
		}
		
		//org code: String cmd= "cmd /c \"" + logCollectorUtilityBatch + "\"";
		
		String cmd= "cmd /c " + logCollectorUtilityBatch ;
		
		File f = new File(logCollectorUtilityPath);
		
		int iResult=-1;
		try {
			iResult = executeCmd(cmd,null,f,"Y", true);
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
        }
		
    	//Step3: Activity log message with the return value. 
		
		/*if(iResult==0){
			msg = String.format(EdgeCMWebServiceMessages.getMessage("DiagUtilityExecSuccess"));
			addActivityLog(CommonUtil.getLocalHost(), Severity.Information, msg);
		}
		
		else{
			logger.info("The return value of running the arcserveAgentSupport.bat file is: " + iResult);
			msg = String.format(EdgeCMWebServiceMessages.getMessage("DiagUtilityExecFail"));
			addActivityLog(CommonUtil.getLocalHost(), Severity.Error, msg);
		}
    	*/
		logger.info("Collecting the console diag info is finished using the web service");
		return iResult;
	}
	
	private int executeCmd(String cmd, String[] envp, File dir, String input, boolean ignoreInputErrStreams) throws IOException, InterruptedException 
	{
		Runtime rn = Runtime.getRuntime();   
		Process process = null;   
	    
    	logger.info("ExeCMD: "+ cmd + ", dir: " + dir.getAbsolutePath());
    	
    	process = rn.exec(cmd, envp, dir );
    	
    	OutputStream objOutput = process.getOutputStream();
	    
		if(input!=null && !input.isEmpty()){
		     objOutput.write(input.getBytes());
		     objOutput.flush();
		     objOutput.close();   
		}
		
		BufferedReader cmdInput = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		
		BufferedReader cmdError = new BufferedReader(new InputStreamReader(process
				.getErrorStream()));
		
		if(ignoreInputErrStreams){
			try{
				cmdInput.close();
				cmdError.close();
			}catch(IOException e){
				logger.error("Exception while closing the process input, error streams");
				logger.error(e);
			}
		}
		
		else{

			StringBuffer cmdOutputbuffer = new StringBuffer();
			StringBuffer cmdErrorbuffer = new StringBuffer();
			
			String cmdout = "";
	
			try {
	
				// Read command output and storing into string buffer
				while ((cmdout = cmdInput.readLine()) != null) {
					cmdOutputbuffer.append(cmdout);
					cmdOutputbuffer.append("\n");
				}
	
			} catch (Exception err) {
				logger.error("some exception in reading cmd output" + err);
				//return null;
			}
	
			try {
				// Read command output and storing into string buffer
				while ((cmdout = cmdError.readLine()) != null) {
					cmdErrorbuffer.append(cmdout);
					cmdErrorbuffer.append("\n");
				}
			} catch (Exception error) {
				logger.error("some exception in reading cmd output error"
						+ error);
			}
		}
		
		process.waitFor();
    	int iResult = process.exitValue();
    	logger.info("cmd: " + cmd + "exit code=" + iResult);
    	
    	process.destroy();
    	process=null;
    	return iResult;
    }
	
	
	@Override
	public int triggerCollectDiagnosticDataForLinuxNode(Node node, DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault 
	{
		
		//save settings to console xml even it is agent or VM
		if(diagObj!=null){
			//save the password as encrypted
			/*String originalPwd = diagObj.getPassword();
			if(originalPwd!=null && !originalPwd.isEmpty()){
				diagObj.setPassword(Base64.encode(originalPwd));
			}*/
			DiagInfoCollectorConfigurationXMLDAO xmlDao;
			try {
				xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
				logger.info("save diag settings in console xml - single Linux node");
				xmlDao.save(logCollectorConfigXMLPath, diagObj);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		boolean isLinuxBackupServer = false;
		if((node.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue()) == ProtectionType.LINUX_D2D_SERVER.getValue()){
			isLinuxBackupServer = true;
		}
		linuxNodeService.collectDiagnosticInfo(diagObj, node.getId(), isLinuxBackupServer , node.getAuthUUID(), node.getHostname());
		logger.info("Collecting diag info from linux node id: " + node.getId() + ", node name: " + node.getHostname());
		return 0;
	}
	@Override
		public int triggerCollectDiagnosticDataForNodes(List<Node> nodes, DiagInfoCollectorConfiguration diagObj) throws EdgeServiceFault {
		
		//save settings to console xml even it is agent or VM
		if(diagObj!=null){
			//save the password as encrypted
			/*String originalPwd = diagObj.getPassword();
			if(originalPwd!=null && !originalPwd.isEmpty()){
				diagObj.setPassword(Base64.encode(originalPwd));
			}*/
			DiagInfoCollectorConfigurationXMLDAO xmlDao;
			try {
				xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
				logger.info("save diag settings in console xml - for multiple nodes");
				xmlDao.save(logCollectorConfigXMLPath, diagObj);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String CurrentHostName = "";
		String CurrentHostIp = "";
		if(ip!=null)
		{
			CurrentHostName = ip.getHostName();
			CurrentHostIp = ip.getHostAddress();
		}
		
		for(Node node :nodes)
		{
			logger.info("node id: " + node.getId());
		}
		SortedSet<Integer> nodeIds = new TreeSet(); 
		
		for(Node node :nodes)
		{
			//TODO
			//if the node is linux log collection
			if((node.isLinuxNode() && node.getLinuxD2DInfoSummary()!=null && node.getLinuxD2DInfoSummary().getMajorversion()!=null && Integer.parseInt(node.getLinuxD2DInfoSummary().getMajorversion()) >=6) || (node.isLinuxD2DInstalled() && Integer.parseInt(node.getD2DMajorversion())>=6)){
				boolean isLinuxBackupServer = false;
				if((node.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue() ) == ProtectionType.LINUX_D2D_SERVER.getValue()){
					isLinuxBackupServer = true;
				}
				linuxNodeService.collectDiagnosticInfo(diagObj, node.getId(), isLinuxBackupServer , node.getAuthUUID(), node.getHostname());
				logger.info("Collecting diag info from linux node id: " + node.getId() + ", node name: " + node.getHostname());
			}
			
			//if the node is d2d
			else if(node.isD2dInstalled()&& Integer.parseInt(node.getD2DMajorversion())>=6){
				nodeIds.add(node.getId());
			}
			
			//if the node is agent less vm then collect from the proxy
			else if (com.ca.arcserve.edge.app.base.webservice.contract.common.Utils.hasBit(node.getPolicyContentFlag(), PlanTaskType.WindowsVMBackup) && Integer.parseInt(node.getProxyInfos().getMajorversion()) >=6){
				VSphereProxyInfo proxyInfo = getVSphereProxyInfoByHostId(node.getId());
				if(proxyInfo==null){
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Error);
					log.setNodeName(node.getHostname());
					log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailVM", node.getVmInstanceUUID()));
					logService.addLog(log);
				}
				
				logger.info("The proxy id is: " + proxyInfo.getvSphereProxyId());
				
				nodeIds.add(proxyInfo.getvSphereProxyId());
			}
			
			//TODO: if console only installed.
			
			String hostnameFromNode = node.getHostname();
			if((node.isConsoleInstalled() || hostnameFromNode.equalsIgnoreCase(CurrentHostName) || hostnameFromNode.equalsIgnoreCase(CurrentHostIp))/* && !(node.isD2dInstalled() && Integer.parseInt(node.getD2DMajorversion())>=6)*/){
				logger.info("Console is installed. Hence triggering data collection for the console");
				
				String msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecStart"));
				generateLog(Severity.Information, node, msg, null);
				
				int status  = triggerCollectDiagnosticDataForConsoleNode(diagObj);
				
				logger.info("Return value of triggerCollectDiagnosticDataForConsoleNode is: " + status);
				
				if(status==0){
					msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecSuccess", diagObj.getUploadDestination()));
					generateLog(Severity.Information, node, msg, null);
				}
				else if(status==2)
				{
					msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecSuccessButFailedToCopyToNWshare", diagObj.getUploadDestination()));
					generateLog(Severity.Warning, node, msg, null);
				}
				else{
					msg = String.format(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFail", diagObj.getUploadDestination()));
					generateLog(Severity.Error, node, msg, null);
				}
			}
		}
		
		logger.info("the node ids to submit diag utility are : " + nodeIds.toString());
		
		for(int id: nodeIds){
			
			String nodeName = "";
			for(Node node: nodes){
				if(node.getId() ==id)
					nodeName = node.getHostname();
			}
			
			try (D2DConnection connection = connectionFactory.createD2DConnection(id)) {
				connection.connect();
				connection.getService().collectDiagnosticInfo(diagObj);
			} catch (SOAPFaultException e) {
				logger.error("fail to trigger diagnostic data collection for id:" + id, e);
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(nodeName);
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				logService.addLog(log);
				return 0;
			} catch (WebServiceException e) {
				logger.error("fail to trigger diagnostic data collection",e);
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(nodeName);
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				logService.addLog(log);
				return 0;
			}	catch (EdgeServiceFault e) {
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Error);
				log.setNodeName(nodeName);
				log.setMessage(EdgeCMWebServiceMessages.getResource("DiagUtilityExecFailNode"));
				logService.addLog(log);
				logger.error("fail to trigger diagnostic data collection",e);
			}
			
		}
		return 1;
		
	}
	
	
	//If get nodeid by group directly by DB, it may more quickly
	public List<Integer> getNodeIdsByGroup(int gatewayId, int groupId, int groupType)throws EdgeServiceFault{
		// node group
		List<Integer> result = new ArrayList<Integer>();
		
		NodeGroup group = new NodeGroup();
		group.setId(groupId);
		group.setType(groupType);
		
		NodeFilterResult filterResult = NodeGroupFilterLoader.create(group).load(group);
		
		BitmapFilter gatewayNodeFilter = new BitmapFilter();
		gatewayNodeFilter.setBitmap(gatewayId);
		gatewayNodeFilter.setType(NodeFilterType.GateWay);
		
		NodeFilterResult gatewayResult = NodeFilterLoader.create(NodeFilterType.GateWay).load(gatewayNodeFilter);
		filterResult.intersect(gatewayResult);
		
		result.addAll(filterResult.getFilteredHostIds());
		return result;
	}
	
	public boolean isVsphereProxy(int hostId){
		List<EdgeConnectInfo> proxy = new ArrayList<EdgeConnectInfo>();
		hostMgrDao.as_edge_proxy_by_proxyhostid(hostId, proxy);
		if(!proxy.isEmpty())
			return true;
		return false;
	}
	
	public void updateEsxServerType(DiscoveryESXOption esxOption, int esxId) {
		try {
			IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
			IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
			int type = vmwareService.updateEsxServerType(esxOption);
			vmwareService.close();
			esxDao.as_edge_esx_update_type(esxId, type);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
		
	@Override
	public void setWarnningAcknowledged(List<Integer> nodeIds) throws EdgeServiceFault {
		for (Integer nodeId : nodeIds) {
			policyDao.as_edge_policy_warning_updateAcknowledge(nodeId, 1);
		}
	}
	
	@Override
	public int isASBUAgentInstalled(int nodeId) throws EdgeServiceFault{
		IRPSService4CPM service;
		try (RPSConnection connection = connectionFactory
				.createRPSConnection(nodeId)) {
			connection.connect();
			service = connection.getService();
			try{
				boolean isInstalled = service.isASBUAgentInstalled();
				logger.debug("NodeServiceImpl, isASBUAgentInstalled : " + isInstalled + ", nodeId : " + nodeId);
				if(isInstalled)
					return 1;
				else
					return 0;
			}catch(SOAPFaultException e){
				String errorMessage = e.getFault().getFaultString();
				if (errorMessage != null && errorMessage.startsWith("Cannot find dispatch method")) {
					logger.error("NodeServiceImpl, isASBUAgentInstalled failed, " + e + ", nodeId : " + nodeId);
					return 2;
				}else{
					throw e;
				}
			}
		}
	}
	
	@Override
	public DiscoveryHyperVEntityInfo getHyperVTreeRootEntity(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault{
		return HyperVManagerAdapter.getInstance().getHyperVTreeRootEntity( getValidateHypervGateWayId( hyperVOption ),
				hyperVOption.getServerName(), hyperVOption.getUsername(), hyperVOption.getPassword(),hyperVOption.getHypervProtectionType());
	}
	
	public void verifyFCPFC(List<Integer> nodeIds) throws EdgeServiceFault 
	{
		logger.info("coming to nodeserviceimpl for verifyFCPFC");
		 
		if (nodeIds == null || nodeIds.size() == 0) {
			return;
		}
		List<Node> nodes = getNodeListByIDs(nodeIds);
		
		for (Node node: nodes) {
			try{
				long planId = node.getPolicyIDForEsx();
				
				if(planId<=0){
					logger.error("It seems the node: "  + node.getHostname() + " does not have associated with any plan");
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Warning);
					log.setNodeName(node.getHostname());
					log.setHostId(node.getId());
					log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcNotStart"));
					logService.addLog(log);
					continue;
				}
				
				String policyUUID;
				try {
					policyUUID = PolicyManagementServiceImpl.getInstance().getPolicyUuid((int)planId);
				} catch (GetPolicyUuidException e1) {
					logger.error(e1);
					logger.error("Could not get the policy UUID for the plan with ID: " + planId + " for the node: " + node.getHostname());
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Error);
					log.setNodeName(node.getHostname());
					log.setHostId(node.getId());
					log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcNotStart"));
					logService.addLog(log);
					continue;
				}
				UnifiedPolicy policy = serviceImpl.loadUnifiedPolicyByUuid(policyUUID);
				
				if(!policy.getTaskList().contains(TaskType.FILE_ARCHIVE) && !policy.getTaskList().contains(TaskType.FileCopy)){
					logger.warn("No filecopy/filearchive tasks are available inside the plan for the node: " + node.getHostname());
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Warning);
					log.setNodeName(node.getHostname());
					log.setHostId(node.getId());
					log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcNotStart"));
					logService.addLog(log);
					continue;
				}
				if(policy.getBackupConfiguration().isD2dOrRPSDestType()){
					logger.info("coming to nodeserviceimpl for verifyFCPFC, node is: " + node.getHostname());
					try (D2DConnection d2dConnection = connectionFactory.createD2DConnection(node.getId())) {
						d2dConnection.connect();
						if(policy.getTaskList().contains(TaskType.FILE_ARCHIVE)){
							long fcCheckStatus = d2dConnection.getService().validateArchiveConfiguration(policy.getFileArchiveConfiguration());
						}
						if(policy.getTaskList().contains(TaskType.FileCopy)){
							long fcCheckStatus = d2dConnection.getService().validateArchiveConfiguration(policy.getFileCopySettingsWrapper().get(0).getArchiveConfiguration());
						}
						d2dConnection.close();
					}catch(Exception e){
						logger.error("Filecopy pre flight check is failed for the node: " + node.getHostname());
						ActivityLog log = new ActivityLog();
						log.setModule(Module.All);
						log.setSeverity(Severity.Error);
						log.setNodeName(node.getHostname());
						log.setHostId(node.getId());
						log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcFailed", e.getMessage()));
						logService.addLog(log);
						
						continue;
					}
				}
				else{
					D2DConnection d2dConnection = null;
					try{
						//connect to D2D for getting the full destination path. This is needed to construct the FC destination path.
						d2dConnection = connectionFactory.createD2DConnection(node.getId()); 
						d2dConnection.connect();
					}catch(EdgeServiceFault e){
						String errorMessage = WebServiceFaultMessageRetriever.
								getErrorMessage( DataFormatUtil.getServerLocale(),(e.getFaultInfo()));
						logger.error("It seems the node: "  + node.getHostname() + " does not have Agent installed or agent service not running");
						ActivityLog log = new ActivityLog();
						log.setModule(Module.All);
						log.setSeverity(Severity.Warning);
						log.setNodeName(node.getHostname());
						log.setHostId(node.getId());
						log.setMessage(errorMessage);
						logService.addLog(log);
						continue;
					}
					BackupConfiguration backupConfig = d2dConnection.getService().getBackupConfiguration();
					if(backupConfig==null){
						logger.error("It seems the node: "  + node.getHostname() + " does not have backup configuration");
						ActivityLog log = new ActivityLog();
						log.setModule(Module.All);
						log.setSeverity(Severity.Warning);
						log.setNodeName(node.getHostname());
						log.setHostId(node.getId());
						log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcNotStart"));
						logService.addLog(log);
						continue;
					}
					String agentBackupDest = backupConfig.getDestination();
					String agentHostName = d2dConnection.getService().GetArchiveDNSHostName();
					d2dConnection.close();
					List<RpsArchiveConfiguationWrapper> rpsFCWrappers = serviceImpl.getRpsArchiveConfigSummary(policyUUID, false);
					for(RpsArchiveConfiguationWrapper fcConfig: rpsFCWrappers){
						RpsHost rpsHostInfo = fcConfig.getHost();
						RPSConnection rpsConnection = connectionFactory.createRPSConnection(rpsHostInfo.getRhostId());
						rpsConnection.connect();
						try{
							long result = rpsConnection.getService().validateFCAndFASettings(fcConfig, agentHostName, agentBackupDest);
						}catch(Exception e){
							logger.error("Filecopy pre flight check is failed for the node: " + node.getHostname());
							ActivityLog log = new ActivityLog();
							log.setModule(Module.All);
							log.setSeverity(Severity.Error);
							log.setNodeName(rpsHostInfo.getRhostname());
							log.setHostId(rpsHostInfo.getRhostId());
							
							
							log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcFailed", e.getMessage()));
							logService.addLog(log);
						}
						rpsConnection.close();
					}
				}
				logger.info("Filecopy pre flight check is done for the node: " + node.getHostname());
				ActivityLog log = new ActivityLog();
				log.setModule(Module.All);
				log.setSeverity(Severity.Information);
				log.setNodeName(node.getHostname());
				log.setHostId(node.getId());
				log.setMessage(EdgeCMWebServiceMessages.getResource("fcPfcComplete"));
				logService.addLog(log);
			}catch(EdgeServiceFault e){
					String errorMessage = WebServiceFaultMessageRetriever.
							getErrorMessage( DataFormatUtil.getServerLocale(),(e.getFaultInfo()));
					logger.error("exception while trying filecopy PFC for the node: "  + node.getHostname());
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Error);
					log.setNodeName(node.getHostname());
					log.setHostId(node.getId());
					log.setMessage(errorMessage);
					logService.addLog(log);
					continue;
			}
		}
	}
	
	@Override
	public GatewayEntity getGatewayByHostId(int nodeId) throws EdgeServiceFault {
		try {
			if(nodeId<=0){				
				logger.error("NodeServiceImpl getGatewayByHostId : " + nodeId);
				return null;
			}
			return gatewayService.getGatewayByHostId(nodeId);		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * If node exist, return node id, else return 0;
	 */
	@Override
	public int checkNodeExist(int gatewayId, String hostName)
			throws EdgeServiceFault {
//		List<String> fqdnNameList = CommonUtil.getFqdnNamebyHostNameOrIp(hostName);
		List<String> fqdnNameList = new ArrayList<String>();
		if(gatewayId != 0){
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( new GatewayId(gatewayId));
				fqdnNameList = nativeFacade.getFqdnNamebyHostNameOrIp(hostName);
			} catch (Exception e) {
				logger.error("[NodeServiceImpl] checkNodeExist() get fqdn name failed.",e);
			}
		}
		return getHostIdByName(gatewayId, 
				hostName,"",fqdnNameList,1);
	}
	
	@Override
	public List<Node> getLinuxBackupserverList(int gatewayId)
			throws EdgeServiceFault {
		return getNodesByGroup(gatewayId, 0, NodeGroup.LinuxD2D);
	}
	
	public void setAutoDiscoveryForHyperv(int id, int isAutoDiscovery){
		hyperVDao.as_edge_hyperv_update_auto_discovery_flag(id, isAutoDiscovery);
	}
	
	@Override
	public void startAgentFilecopyNow(List<Integer> nodeIdList) throws EdgeServiceFault 
	{
		List<Node> nodes = getNodeListByIDs(nodeIdList);
		for(Node node: nodes){
			try (D2DConnection connection = connectionFactory.createD2DConnection(node.getId())) {
				logger.info("[NodeServiceImpl] submit a filecopy job for the node: " + node.getHostname());
				connection.connect();
				
				//Handle the case: when node is currently managed by some other console or not
				int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
				if (regStatus == 2) {
					EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
					String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();				
					if (edgeInfo != null) {				
						String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
						if(!StringUtil.isEmptyOrNull(consoleName))
							CurRegisteredEdgeHostName = consoleName;
					}
					//Note: reusing the existing message as the resource strings freezed
					String message = EdgeCMWebServiceMessages.getMessage("failedToManageD2DByAnotherServe", 
							node.getHostname(),CurRegisteredEdgeHostName);
					
					logger.error("startAgentFilecopyNow: It seems the node: "  + node.getHostname() + " is not managed by the local console: " + EdgeCommonUtil.getLocalFqdnName() + ", but its managed by: " + CurRegisteredEdgeHostName);
				
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Error);
					log.setNodeName(node.getHostname());
					log.setMessage(message);
					log.setHostId(node.getId());
					logService.addLog(log);
					continue;
				}
				connection.getService().startFilecopyNow();
			}
		}
	}
	
	@Override
	public void startAgentFileArchiveNow(List<Integer> nodeIdList) throws EdgeServiceFault 
	{
		List<Node> nodes = getNodeListByIDs(nodeIdList);
		for(Node node: nodes){
			try (D2DConnection connection = connectionFactory.createD2DConnection(node.getId())) {
				logger.info("[NodeServiceImpl] submit a file archive job for the node: "+ node.getHostname());
				connection.connect();
				
				//Handle the case: when node is currently managed by some other console or not
				int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.CentralManagement, EdgeCommonUtil.getLocalFqdnName());
				if (regStatus == 2) {
					EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
					String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();				
					if (edgeInfo != null) {				
						String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
						if(!StringUtil.isEmptyOrNull(consoleName))
							CurRegisteredEdgeHostName = consoleName;
					}
					//Note: reusing the existing message as the resource strings freezed
					String message = EdgeCMWebServiceMessages.getMessage("failedToManageD2DByAnotherServe", 
							node.getHostname(),CurRegisteredEdgeHostName);
					
					logger.error("startAgentFileArchiveNow: It seems the node: "  + node.getHostname() + " is not managed by the local console: " + EdgeCommonUtil.getLocalFqdnName() + ", but its managed by: " + CurRegisteredEdgeHostName);
				
					ActivityLog log = new ActivityLog();
					log.setModule(Module.All);
					log.setSeverity(Severity.Error);
					log.setNodeName(node.getHostname());
					log.setMessage(message);
					log.setHostId(node.getId());
					logService.addLog(log);
					continue;
				}
				
				connection.getService().startFileArchiveNow();
			}
		}
	}
	
	@Override
	public void bindPolicyD2DRole(int policyId, int hostId, D2DRole d2dRole)
			throws EdgeServiceFault {
		policyDao.as_edge_policy_AddD2DRole(policyId, hostId, d2dRole);
	}
	
}
