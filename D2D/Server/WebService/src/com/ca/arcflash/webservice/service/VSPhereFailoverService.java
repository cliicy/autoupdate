package com.ca.arcflash.webservice.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.powerState;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.job.HAJobStatus;
import com.ca.arcflash.jobscript.base.JobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.heartbeat.ConcreteHeartBeatCommand;
import com.ca.arcflash.webservice.jni.NativeFacade;

public class VSPhereFailoverService {

	private static final Logger logger = Logger
			.getLogger(VSPhereFailoverService.class);

	private static VSPhereFailoverService service = new VSPhereFailoverService();
	private static final Lock lock = new ReentrantLock();

	private Set<String> monitoringThreads = new HashSet<String>();
	private boolean pOnQueueStarted = false;
	private boolean pOffQueueStarted = false;

	private VSPhereFailoverService() {
	}

	public static VSPhereFailoverService getInstance() {

		return service;

	}

	private void startMonitorVMPowerOffThread() {
		if (pOffQueueStarted) {
			return;
		}
		Thread t = new MonitorVMPowerOffThread();
		t.start();
		pOffQueueStarted = true;
	}

	private void startMonitorVMPowerOnThread() {
		if (pOnQueueStarted) {
			return;
		}
		Thread t = new MonitorVMPowerOnThread();
		t.start();
		pOnQueueStarted = true;
	}

	private void startMonitoringThread(BackupVM backupVM) {
		Thread t = new MonitoringThread(backupVM);
		t.start();
	}

	public void monitorVM(String afGuid) throws Exception {

		BackupVM backupVM = getBackupVM(afGuid);
		if (backupVM == null) {
			throw new Exception(
					"vm backup configuration does not contain backup vm.");
		}
		
		if (backupVM.getVmType() == BackupVM.Type.HyperV.ordinal() ||
			backupVM.getVmType() == BackupVM.Type.HyperV_Cluster.ordinal()) // Hyper-v VM
			return;

		lock.lock();

		try {

			if (isESXServerMonitoredInCache(backupVM.getEsxServerName())) {
				return;
			}

			startMonitoringThread(backupVM);

			addThread2Cache(backupVM.getEsxServerName());

			// start monitoring thread
			startMonitorVMPowerOffThread();
			startMonitorVMPowerOnThread();

		} finally {
			lock.unlock();
		}
	}

	public void startAllMoniteeHeartBeat(Collection<AFJob> jobs) {

		Thread t = new CheckMoniteeStateThread(jobs);
		t.start();

	}

	private BackupVM getBackupVM(String vminstanceUUID) throws ServiceException {

		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vminstanceUUID);
		VMBackupConfiguration vmBackup = VSphereService.getInstance()
				.getVMBackupConfiguration(vm);
		if (vmBackup == null) {
			return null;
		}
		return vmBackup.getBackupVM();

	}

	private void addThread2Cache(String esxName) {

		monitoringThreads.add(esxName);

	}

	private void removeThread2Cache(String esxName) {

		lock.lock();
		try {
			monitoringThreads.remove(esxName);
		} finally {
			lock.unlock();
		}
	}

	private boolean isESXServerMonitoredInCache(String esxName) {

		return monitoringThreads.contains(esxName);

	}

	private class MonitoringThread extends Thread {

		private BackupVM backupVM;

		MonitoringThread(BackupVM backupVM) {
			this.backupVM = backupVM;
		}

		@Override
		public void run() {
			logger.info("Thread for Monitoring esx "
					+ backupVM.getEsxServerName() + " starts.");
			CAVirtualInfrastructureManager vmwareManager = null;
			try {

				vmwareManager = CAVMwareInfrastructureManagerFactory
						.getCAVMwareVirtualInfrastructureManager2(
								backupVM.getEsxServerName(),
								backupVM.getEsxUsername(),
								backupVM.getEsxPassword(),
								backupVM.getProtocol(), true,
								backupVM.getPort());

				vmwareManager.monitorVMHealth();

			} catch (Exception e) {

				logger.error(e.getMessage(), e);
				lock.lock();
				try {
					removeThread2Cache(backupVM.getEsxServerName());
				} finally {
					lock.unlock();
				}

			}
			finally {
				if (vmwareManager != null) {
					try {
						vmwareManager.close();
					} catch (Exception ex) {
					}
				}
			}

		}

	}

	private class MonitorVMPowerOffThread extends Thread {

		@Override
		public void run() {
			logger.info("MonitorVMPowerOffThread starts.");
			while (true) {
				try {
					String vminstanceUUID = CAVirtualInfrastructureManager.pOffqueue
							.take();
					AFJob heartBeatJob = HAService.getInstance()
							.getHeartBeatJob(vminstanceUUID);
					if (heartBeatJob != null) {
						// emulate heartbeat does not send
						heartBeatJob.unschedule();
						// WebServiceClientProxy clientProxy =
						// MonitorWebClientManager.getMonitorWebClientProxy(vminstanceUUID);
						// clientProxy.getServiceV2().startFailover(vminstanceUUID,
						// new VMSnapshotsInfo());
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					return;
				}
			}
		}
	}

	private class MonitorVMPowerOnThread extends Thread {

		@Override
		public void run() {
			logger.info("MonitorVMPowerOnThread starts.");
			while (true) {
				try {
					String vminstanceUUID = CAVirtualInfrastructureManager.pOnqueue
							.take();
					AFJob heartBeatJob = HAService.getInstance()
							.getHeartBeatJob(vminstanceUUID);
					if (heartBeatJob != null) {
						// emulate heartbeat does not send
						heartBeatJob.schedule();
					}
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					return;
				}
			}
		}
	}

	private class CheckMoniteeStateThread extends Thread {

		private Collection<AFJob> heatBeatJobs;

		CheckMoniteeStateThread(Collection<AFJob> heatBeatJobs) {
			this.heatBeatJobs = heatBeatJobs;
		}

		@Override
		public void run() {

			try {

				Iterator<AFJob> iterator = heatBeatJobs.iterator();
				while (iterator.hasNext()) {
					AFJob afJob = (AFJob) iterator.next();
					JobScript jobScript = afJob.getJobScript();
					if (jobScript != null
							&& jobScript instanceof HeartBeatJobScript) {
						HeartBeatJobScript heartB = (HeartBeatJobScript) jobScript;
						heartB.setHeartBeatCommand(new ConcreteHeartBeatCommand());
						if (afJob.getJobStatus().getStatus() == HAJobStatus.Status.Active
								|| afJob.getJobStatus().getStatus() == HAJobStatus.Status.Failed) {

							if (HACommon.isTargetPhysicalMachine(heartB.getAFGuid())) {
								HAService.getInstance().startHeartBeat(heartB.getAFGuid());
							} else {

								BackupVM backupVM = getBackupVM(heartB
										.getAFGuid());
								if (backupVM == null) {
									continue;
								}

								monitorVM(heartB.getAFGuid());
								
								if (backupVM.getVmType() == BackupVM.Type.HyperV.ordinal() ||
									backupVM.getVmType() == BackupVM.Type.HyperV_Cluster.ordinal()) {
									NativeFacade facade = HAService.getInstance().getNativeFacade();
									long handle = 0;
									try {
										handle = facade.OpenHypervHandle(backupVM.getEsxServerName(), backupVM.getEsxUsername(), backupVM.getEsxPassword());
										if (facade.GetHyperVVmState(handle, backupVM.getInstanceUUID()) == 2) {
											logger.info("start hyper-v managed vm heart beat.");
											HAService.getInstance().startHeartBeat(heartB.getAFGuid());
										}
									} catch (Exception e) {
										logger.error(e.getMessage());
									}finally{
										try {
											if (handle != 0)
												facade.CloseHypervHandle(handle);
										} catch (ServiceException e) {
											logger.error("Failed to close hyperv manager handle." + e.getMessage());
										}
									}
								}
								else {
									String esxHostName = backupVM
											.getEsxServerName();
									String esxUserName = backupVM.getEsxUsername();
									String esxPassword = backupVM.getEsxPassword();
									String exsProtocol = backupVM.getProtocol();
									int esxPort = backupVM.getPort();
									String vmuuid = backupVM.getInstanceUUID();
									String vmname = backupVM.getVmName();
	
									logger.info("esxHostName: " + esxHostName);
									logger.info("esxUserName: " + esxUserName);
									logger.info("exsProtocol: " + exsProtocol);
									logger.info("esxPort: " + esxPort);
									logger.info("vmuuid: " + vmuuid);
									logger.info("vmname: " + vmname);
	
									CAVirtualInfrastructureManager vmwaremanager = CAVMwareInfrastructureManagerFactory
											.getCAVMwareVirtualInfrastructureManager(
													esxHostName, esxUserName,
													esxPassword, exsProtocol, true,
													esxPort);
									powerState vmstate = powerState.errorFault;
									try {
										vmstate = vmwaremanager.getVMPowerstate(vmname, vmuuid);
									}
									catch (Exception e) {
										logger.error("Fail to get vm power state");
									}
									
									try {
										vmwaremanager.close();
									} catch (Exception ex) {
									}
									
									if (vmstate == powerState.poweredOn) {
										logger.info("start vsphere managed vm heart beat.");
										HAService.getInstance().startHeartBeat(heartB.getAFGuid());
									}
								}
							}

						} else if (afJob.getJobStatus().getStatus() == HAJobStatus.Status.Canceled) {
							String upgradeFlag = CommonUtil.getD2DUpgradeFlag();
							if ("true".equals(upgradeFlag)) {
								int result = HAService.getInstance().resumeHeartBeatForVcmUpgrade(heartB.getAFGuid());
								logger.info("upgradeFlag is true. resume heartbeat. result = " + result);
								CommonUtil.setD2DUpgradeFlag("");
							}
						}

					}
				}

			} catch (Throwable e) {
				logger.error("Error starting heart beat in context listener."
						+ e.getMessage(), e);
			}

		}
	}

	public static void main(String[] args) {

	}

}
