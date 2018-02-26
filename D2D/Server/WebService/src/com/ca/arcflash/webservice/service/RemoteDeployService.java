package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployModel;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployStatus;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTargetDetail;
import com.ca.arcflash.webservice.jni.model.IRemoteDeployCallback;
import com.ca.arcflash.webservice.jni.model.JDeployStatus;

public class RemoteDeployService extends BaseService {

	private static final String XML_MODEL = "/RemoteDeploy.xml";

	private static RemoteDeployService instance = null;

	private RemoteDeployModel model = null;

	private AtomicBoolean deploying = new AtomicBoolean(false);
	private final int trustType = 1;

	private static Logger log = Logger.getLogger(RemoteDeployService.class);

	private RemoteDeployService() {
		if (model == null) {
			FileInputStream fis = null;
			try {
				File file = new File(ServiceContext.getInstance()
						.getDataFolderPath()
						+ XML_MODEL);
				if (file.exists()) {
					fis = new FileInputStream(file);
					model = JAXB.unmarshal(fis, RemoteDeployModel.class);
					if (model.getRemoteDeployTargets() != null) {
						Collection<RemoteDeployTargetDetail> values = model
								.getRemoteDeployTargets().values();
						if (values != null && values.size() > 0) {
							for (RemoteDeployTargetDetail detail : values) {
								String pwd = detail.getPassword();
								String uuid = detail.getUuid();
								if (pwd != null) {
									detail.setPassword(getNativeFacade()
											.decrypt(pwd));
								}
								if (uuid != null) {
									detail.setUuid(getNativeFacade().decrypt(
											uuid));
								}
							}
						}
					}
					if(model.getLocalPassword() != null)
					{
						model.setLocalPassword(getNativeFacade().decrypt(model.getLocalPassword()));
					}
				} else {
					log
							.debug("Cann't found remote deploy module from xml: "
									+ XML_MODEL
									+ ". Initialize remote deploy module with empty content.");
					model = new RemoteDeployModel();
				}
			} catch (Throwable t) {
				log.error("Failed to read remote deploy model from xml: "
						+ XML_MODEL, t);
				model = new RemoteDeployModel();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Throwable t) {
						log.error(t.getMessage(), t);
					}
				}
			}
		}

		goOnLastDeploy();
	}

	private void goOnLastDeploy() {
		log.debug("goOnLastDeploy - begin");
		Map<String, RemoteDeployTargetDetail> map = model
				.getRemoteDeployTargets();
		List<String> list = new ArrayList<String>();

		for (RemoteDeployTargetDetail rd : map.values()) {
			if (rd.isSelected()) {
				if (rd.getStatus() != JDeployStatus.DEPLOY_SUCCESS && rd.getStatus() != JDeployStatus.DEPLOY_FAILED) {
					log.debug("Server:" + rd.getServerName() +", status:" + rd.getStatus() );
					list.add(rd.getServerName());
				}
			}
		}

		if (!list.isEmpty()) {
			this.startRemoteDeploy(model.getLocalDomain(),
					model.getLocalUser(), model.getLocalPassword(), list
							.toArray(new String[0]), true);
		}
		log.debug("goOnLastDeploy - end");
	}

	public static synchronized RemoteDeployService getInstance() {
		if (instance == null) {
			instance = new RemoteDeployService();
		}
		return instance;
	}

	private synchronized void store() {

		storeTrustHost();

		try {
			String filePath = ServiceContext.getInstance().getDataFolderPath()
					+ XML_MODEL;
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				RemoteDeployModel copyModel = new RemoteDeployModel();
				
				String localPwd = model.getLocalPassword();
				
				if(localPwd != null)
				{
					copyModel.setLocalPassword(getNativeFacade().encrypt((localPwd)));
				}
				copyModel.setLocalDomain(model.getLocalDomain());
				copyModel.setLocalUser(model.getLocalUser());
				
				Map<String, RemoteDeployTargetDetail> map = copyModel
						.getRemoteDeployTargets();
				if (model.getRemoteDeployTargets() != null
						&& model.getRemoteDeployTargets().values() != null) {
					RemoteDeployTargetDetail[] arr = model
							.getRemoteDeployTargets().values().toArray(
									new RemoteDeployTargetDetail[0]);
					for (RemoteDeployTargetDetail detail : arr) {
						map.put(detail.getServerName(), detail.clone());
					}
				}

				if (copyModel.getRemoteDeployTargets() != null) {
					Collection<RemoteDeployTargetDetail> values = copyModel
							.getRemoteDeployTargets().values();
					if (values != null && values.size() > 0) {
						for (RemoteDeployTargetDetail detail : values) {
							String pwd = detail.getPassword();
							String uuid = detail.getUuid();
							if (pwd != null) {
								detail.setPassword(getNativeFacade().encrypt(
										(pwd)));
							}
							if (uuid != null) {
								detail.setUuid(getNativeFacade().encrypt(uuid));
							}
						}
					}
				}
				JAXB.marshal(copyModel, fos);
			} catch (Exception e) {
				log.error("Failed to store remote deploy model from xml: "
						+ XML_MODEL, e);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		} catch (Throwable t) {
			log.error("Failed to store remote deploy model from xml: "
					+ XML_MODEL, t);
		}
	}

	private synchronized void storeTrustHost() {
		try {
			Collection<RemoteDeployTargetDetail> coll = model
					.getRemoteDeployTargets().values();
			TrustedHost[] trustHosts = CommonService.getInstance()
					.getTrustedHosts();

			if (trustHosts != null) {
				for (TrustedHost host : trustHosts) {
					if (host.getType() == trustType) {
						CommonService.getInstance().removeTrustedHost(host);
					}
				}
			}

			for (RemoteDeployTargetDetail detail : coll) {
				if (RemoteDeployStatus.DEPLOY_SUCCESS.value() == detail
						.getStatus()) {
					TrustedHost host = new TrustedHost();
					host.setName(detail.getServerName());
					host.setType(trustType);
					if(detail.isUseHttps())
						host.setProtocol("https:");
					else
						host.setProtocol("http:");
					host.setPort(detail.getPort());
					host.setUuid(detail.getUuid());
					host.setUserName(detail.getUsername());
					host.setPassword(detail.getPassword());
					host.setD2dVersion(Integer.parseInt(CommonService
							.getInstance().getVersionInfoInternal()
							.getMajorVersion()));
					CommonService.getInstance().addTrustedHost(host);
				}
			}

		} catch (Throwable t) {
			log.error("Failed to add trust host", t);
		}
	}

	public synchronized RemoteDeployTargetDetail[] getRemoteDeployTargets(
			String[] serverNames) {
		if (serverNames == null || serverNames.length == 0) {
			return model.getRemoteDeployTargets().values().toArray(
					new RemoteDeployTargetDetail[0]);
		} else {
			List<RemoteDeployTargetDetail> list = new ArrayList<RemoteDeployTargetDetail>();
			for (String key : serverNames) {
				list.add(model.getRemoteDeployTargets().get(key));
			}
			return list.toArray(new RemoteDeployTargetDetail[0]);
		}

	}

	public synchronized void setRemoteDeployTargets(
			RemoteDeployTarget[] remoteDeployTargets) {
		List<String> targetKeys = new ArrayList<String>();
		for (RemoteDeployTarget target : remoteDeployTargets) {

			targetKeys.add(target.getServerName());

			if (model.getRemoteDeployTargets().containsKey(
					target.getServerName())) {
				RemoteDeployTargetDetail storedTarget = model
						.getRemoteDeployTargets().get(target.getServerName());
				storedTarget.setServerName(target.getServerName());
				storedTarget.setUsername(target.getUsername());
				storedTarget.setPassword(target.getPassword());
				storedTarget.setPort(target.getPort());
				storedTarget.setInstallDirectory(target.getInstallDirectory());
				storedTarget.setReboot(target.isReboot());
				storedTarget.setInstallDriver(target.isIntallDriver());
				storedTarget.setAutoStartRRService(target.isAutoStartRRService());
				storedTarget.setUseHttps(target.isUseHttps());
			} else {
				RemoteDeployTargetDetail detail = new RemoteDeployTargetDetail();
				detail.setUuid(UUID.randomUUID().toString());
				detail.setServerName(target.getServerName());
				detail.setUsername(target.getUsername());
				detail.setPassword(target.getPassword());
				detail.setPort(target.getPort());
				detail.setInstallDirectory(target.getInstallDirectory());
				detail.setReboot(target.isReboot());
				detail.setInstallDriver(target.isIntallDriver());
				detail.setAutoStartRRService(target.isAutoStartRRService());
				detail.setUseHttps(target.isUseHttps());
				model.getRemoteDeployTargets().put(detail.getServerName(),
						detail);
			}

		}

		model.getRemoteDeployTargets().keySet().retainAll(targetKeys);
		store();
	}

	public synchronized void startRemoteDeploy(final String localDomain,
			final String localUser, final String localPassword,
			final String[] serverNames) {
		startRemoteDeploy(localDomain, localUser, localPassword, serverNames,
				false);
	}

	private synchronized void startRemoteDeploy(final String localDomain,
			final String localUser, final String localPassword,
			final String[] serverNames, boolean isRestart) {
		if (deploying.compareAndSet(false, true)) {	
			log.debug("isRestart:" + isRestart);
			if (!isRestart) {
				try {
					boolean hasSelection = false;
					if (model.getRemoteDeployTargets() != null) {
						Iterator<String> itr = model.getRemoteDeployTargets()
								.keySet().iterator();
						while (itr.hasNext()) {
							String sn = itr.next();
							boolean isSelectedToDeploy = false;
							for (String serverName : serverNames) {
								if (sn.equalsIgnoreCase(serverName)) {
									isSelectedToDeploy = true;
									break;
								}
							}						

							RemoteDeployTargetDetail detail = model
									.getRemoteDeployTargets().get(sn);
							if (isSelectedToDeploy) {
								detail.setStatus(-1);// Pending for deploy...
								detail.setPercentage(0);
								detail.setMsgCode(0);
								detail.setSelected(true);
								hasSelection = true;
							} else {
								detail.setSelected(false);
							}
						}

						if (hasSelection) {
							model.setLocalDomain(localDomain);
							model.setLocalUser(localUser);
							model.setLocalPassword(localPassword);
						}

						store();
					}
				} catch (Throwable t) {
					log.error("Throwable occured:", t);
				}
			}

			log.debug("Start Remote Deploy.");
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						for (String serverName : serverNames) {
							final RemoteDeployTargetDetail detail = model
									.getRemoteDeployTargets().get(serverName);

							String tempLocalDomain = localDomain;
							String tempLocalUser = localUser;
							if (tempLocalDomain == null
									|| tempLocalDomain.isEmpty()) {
								if (tempLocalUser != null
										&& !tempLocalUser.isEmpty()) {
									String[] domainAndUser = tempLocalUser
											.split("\\\\+");
									if (domainAndUser != null
											&& domainAndUser.length > 1) {
										tempLocalDomain = domainAndUser[0];
										tempLocalUser = domainAndUser[1];
									}
								}
								log
										.debug("No local domain and local user information.");
							}

							if (tempLocalDomain == null
									|| tempLocalDomain.isEmpty()
									|| "localhost"
											.equalsIgnoreCase(tempLocalDomain)) {
								tempLocalDomain = InetAddress.getLocalHost()
										.getHostName();
							}

							boolean bResumedAndCheck = false;
							int curStatus = detail.getStatus();
							if (curStatus == JDeployStatus.DEPLOY_COPYING_IMAGE
									|| curStatus == JDeployStatus.DEPLOY_IN_PROGRESS
									|| curStatus == JDeployStatus.DEPLOY_NOT_STARTED
									|| curStatus == JDeployStatus.DEPLOY_THIRD_PARTY
									|| curStatus == JDeployStatus.DEPLOY_WAITING) {
								bResumedAndCheck = true;
							}

							log.debug("curStatus:" + curStatus);

							String uuid = getNativeFacade().encrypt(detail.getUuid());
							log.debug("pre uuid:" + uuid);
							RemoteDeployTarget rdtarget = new RemoteDeployTarget();
							rdtarget.setAutoStartRRService(detail.isAutoStartRRService());
							rdtarget.setInstallDirectory(detail.getInstallDirectory());
							rdtarget.setPassword(detail.getPassword());
							rdtarget.setPort(detail.getPort());
							rdtarget.setReboot(detail.isReboot());
							rdtarget.setIntallDriver(detail.isInstallDriver());
							rdtarget.setServerName(serverName);
							rdtarget.setUsername(detail.getUsername());
							rdtarget.setUseHttps(detail.isUseHttps());
							
							log.debug(StringUtil.convertObject2String(rdtarget));
							
							DeployUpgradeInfo info = instance.validRemoteDeploy(tempLocalDomain, tempLocalUser, localPassword, rdtarget);
							
							log.debug(StringUtil.convertObject2String(info));
							
							if(info != null && info.getUuid()!= null && info.getUuid().length() > 0)	{
								uuid = info.getUuid();
								log.debug("uuid:" + uuid);
								detail.setUuid(getNativeFacade().decrypt(uuid));
							}
							log.debug("now uuid:" + detail.getUuid());
							
							getNativeFacade().StartToDeploy(tempLocalDomain,
									tempLocalUser, localPassword,
									uuid, detail.getServerName(),
									detail.getUsername(), detail.getPassword(),
									detail.getPort(),
									detail.getInstallDirectory(),
									detail.isAutoStartRRService(),
									detail.isReboot(),
									detail.isInstallDriver(),
									detail.isUseHttps(),
									bResumedAndCheck,
									new IRemoteDeployCallback() {

										@Override
										public void update(JDeployStatus status) {
											log
													.debug("Callback From ADT received.");
											log.debug("Status: "
													+ status.getStatus());
											log.debug("Percentage: "
													+ status.getPercentage());
											log.debug("Message: "
													+ status.getMessage());
											log.debug("msgCode: "
													+ status.getMsgCode());
											detail.setStatus(status.getStatus());
											detail.setPercentage(status.getPercentage());
											detail.setProgressMessage(status.getMessage());
											detail.setMsgCode(status.getMsgCode());
											try {
												store();
											} catch (Throwable t) {
												log
														.debug(
																"Failed to store the data received from ADT callback.",
																t);
											}
										}
									});

						}
					} catch (Throwable t) {
						log.error("Error occurs during remote deploy.", t);
					}
					deploying.set(false);
					log.debug("Remote Deploy Completed.");

				}
			});
			thread.start();
		}
	}

	public DeployUpgradeInfo validRemoteDeploy(String localDomain, String localUser,
			String localPassword, RemoteDeployTarget remoteTarget)
			throws ServiceException {
		log.debug("validRemoteDeploy() - start");
		DeployUpgradeInfo deployUpgrade = new DeployUpgradeInfo();
		try {
			log.debug("validRemoteDeploy() - invoke JNI");
			if (localDomain == null || localDomain.isEmpty()
					|| "localhost".equalsIgnoreCase(localDomain)) {
				localDomain = InetAddress.getLocalHost().getHostName();
			}
			deployUpgrade = this.getNativeFacade().validRemoteDeploy(localDomain,
					localUser, localPassword, remoteTarget);
		} catch (Throwable e) {
			log.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}
		log.debug("JNI return:" + deployUpgrade.getDwRet());
		log.debug("validRemoteDeploy - end");
		return deployUpgrade;
	}
}
