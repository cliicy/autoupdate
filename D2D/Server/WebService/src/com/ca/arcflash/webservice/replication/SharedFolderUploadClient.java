package com.ca.arcflash.webservice.replication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.service.ServiceException;


public class SharedFolderUploadClient implements IUpload {
	private static Logger logger = Logger.getLogger(SharedFolderUploadClient.class);

	private ReplicationSource src;
	private ReplicationDestination dest;
	private static boolean isUploading = false;
	private static Object lock = new Object();
	private static int MaxRetryCount = 3;

	private boolean isSrcValid = true;
	private boolean isDestValid = true;

	private boolean isSrcConned = true;
	private boolean isDestConned = true;
	
	private static NativeFacade nativeFacade = new NativeFacadeImpl();

	public static final int NS_CANNOTCONN = 0x01;
	public static final int NS_CANNOTWRITE = 0x02;
	public static final int NS_CANNOTREAD = 0x04;
	public static final int FS_ISNOTDIRECTORY = 0x08;
	public static final int ERROR_ALREADY_ASSIGNED = 85;
	public static final int ERROR_LOGON_FAILURE = 1326;
	public static final int ERROR_SESSION_CREDENTIAL_CONFLICT = 1219;
	public static final int ERROR_NO_NET_OR_BAD_PATH = 1203;

	static {
		System.loadLibrary("NativeFacade");
	}

	public SharedFolderUploadClient(ReplicationSource src,
			ReplicationDestination dest) {
		this.src = src;
		this.dest = dest;
	}

	public void doUpload() {
		synchronized (lock) {
			preUpload();
			isUploading = true;
			upload();
			isUploading = false;
			postUpload();
		}
	}

	private void postUpload() {
		if (isRemote(src.getBaseDir()) && isSrcConned) {
			try {
				nativeFacade.NetCancel(src.getBaseDir(), false);
			} catch (ServiceException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
		if (isRemote(dest.getDestPath()) && isDestConned) {
			try {
				nativeFacade.NetCancel(dest.getDestPath(), false);
			} catch (ServiceException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
	}

	private void preUpload() {
		checkSrc();
		checkDest();
	}

	private void checkDest() {
		File destF = new File(dest.getDestPath());
		if (checkWritable(destF))
			return;

		int ret = verifyNetConn(dest.getUserName(), dest.getPwd(), dest
				.getDestPath(), true, true);

		if (ret == NS_CANNOTCONN) {
			isDestConned = false;
		} else if (ret == FS_ISNOTDIRECTORY) {
			// log(dest is not a directory)
		}
		isDestValid = ret == 0;
	}

	private static boolean checkWritable(File folder) {
		UUID uuid = UUID.randomUUID();
		File tempF = new File(folder, uuid.toString());
		if (folder.canWrite()) {
			try {
				if (tempF.createNewFile()) {
					tempF.delete();
					return true;
				}
			} catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
		return false;
	}

	private void checkSrc() {
		File srcF = new File(src.getBaseDir());
		if (srcF.canRead()) {
			return;
		}

		int ret = verifyNetConn(src.getUserName(), src.getPwd(), src
				.getBaseDir(), false, false);

		if (ret == NS_CANNOTCONN) {
			isSrcConned = false;
		}

		isSrcValid = ret == 0;
	}

	private void upload() {
		if (isSrcValid && isDestValid) {
			copyToUNC();
		}
	}

	private int verifyNetConn(String userName, String pwd, String netPath,
			boolean checkWritable, boolean checkIsDirctory) {
		boolean isNetConnSucc = false;
		boolean isRemote = isRemote(netPath);
		if (isRemote) {
			isNetConnSucc = NetConn(userName, pwd, netPath);
		}

		if (isRemote && !isNetConnSucc) {
			// log(failed to connect to path with username);
			logger.debug(MessageFormatEx.format(
					"Failed to connect to path {0} with username {1}.",
					netPath, userName));
			return NS_CANNOTCONN;
		}

		File f = new File(netPath);
		if (!f.canRead()) {
			// log(path is not readable);
			logger.debug(MessageFormatEx.format("path {0} is not readable.", f
					.getPath()));
			return NS_CANNOTREAD;
		}
		if (checkIsDirctory) {
			if (!f.isDirectory()) {
				logger.debug(MessageFormatEx.format(
						"path {0} is not a directroy.", f.getPath()));
				return FS_ISNOTDIRECTORY;
			}
		}

		if (checkWritable) {
			if (!checkWritable(f)) {
				logger.debug(MessageFormatEx.format("path {0} is not writable.",
						f.getPath()));
				return NS_CANNOTWRITE;
			}
		}

		return 0;
	}

	private boolean NetConn(String user, String pwd, String netPath) {

		int retryCount = 0;

		while (true) {

			int ret0=0;
			try {
				ret0 = (int) nativeFacade.NetConn(user, pwd, netPath);
			} catch (ServiceException e1) {
				logger.error(e1.getMessage() == null ? e1 : e1.getMessage());
			}

			if (ret0 == 0) {
				logger.debug("Logon Successful.");
				return true;
			} else if (ret0 == 1326) {
				// ERROR_LOGON_FAILURE 1326
				logger
						.debug("Logon failure: unknown user name or bad password");

			} else if (ret0 == 1219) {
				// ERROR_SESSION_CREDENTIAL_CONFLICT 1219
				if (!isUploading && retryCount < MaxRetryCount) {
					try {
						nativeFacade.NetCancel(netPath, false);
					} catch (ServiceException e) {
						logger.error(e.getMessage() == null ? e : e.getMessage());
					}
					retryCount++;
					continue;
				}
				logger
						.debug("Multiple connections to a server or shared resource by the same user, using more than one user name, are not allowed. Disconnect all previous connections to the server or shared resource and try again");

			} else if (ret0 == 1203) {
				// ERROR_NO_NET_OR_BAD_PATH 1203
				logger
						.debug("The network path was either typed incorrectly, does not exist, or the network provider is not currently available. Please try retyping the path or contact your network administrator.");

			}
			break;
		}

		return false;
	}

	private void copyToUNC() {
		String mname = "copyToUNC()";
		if (src.hasFilesOrDirsRelativeToBase()) {
			for (String s : src.getFileOrDirsRelativeToBase()) {
				File srcF = new File(src.getBaseDir(), s);
				File remoteF = new File(dest.getDestPath(), s);
				if (!srcF.exists())
					continue;

				if (srcF.isFile()) {
					try {
						copyFileToUNC(srcF, remoteF);
					} catch (IOException e) {
						logger
								.debug(MessageFormat
										.format(
												"[{0}], failed to copy from {1} to {2}, ERROR MSG:{3}",
												mname, srcF.getPath(), remoteF
														.getPath(), e
														.getMessage()));
					}
				} else {// isDirectory
					if (!remoteF.exists()) {
						boolean isCreated = remoteF.mkdirs();
						if (!isCreated) {
							logger.debug(MessageFormatEx.format(
									"Failed to create dir:{0}", remoteF
											.getPath()));
						}
					}
					copyDirToUNC(srcF.getPath(), remoteF.getPath());
				}
			}
		} else {
			copyDirToUNC(src.getBaseDir(), dest.getDestPath());
		}
	}

	private void copyDirToUNC(String srcPath, String destPath) {
		String mname = "copyDirToUNC(String srcPath, String destPath)";
		File srcDir = new File(srcPath);
		File destDir = new File(destPath);
		File[] fs = srcDir.listFiles();
		if (fs != null && fs.length > 0) {
			for (File srcF : fs) {
				File remoteF = new File(destDir, srcF.getName());
				if (srcF.isFile()) {
					try {
						copyFileToUNC(srcF, remoteF);
					} catch (IOException e) {
						logger
								.debug(MessageFormat
										.format(
												"[{0}], failed to copy from {1} to {2}, ERROR MSG:{3}",
												mname, srcF.getPath(), remoteF
														.getPath(), e
														.getMessage()));
					}
				} else {// isDirectory
					if (!remoteF.exists()) {
						boolean isCreated = remoteF.mkdir();
						if (!isCreated) {
							logger.debug(MessageFormatEx.format(
									"Failed to create dir:{0}", remoteF
											.getPath()));
						}
					}
					copyDirToUNC(srcF.getPath(), remoteF.getPath());
				}
			}
		}
	}

	private void copyFileToUNC(File srcFile, File remoteFile)
			throws IOException {
		if (!srcFile.exists() || !srcFile.canRead())
			return;

		File p = remoteFile.getParentFile();
		if (!p.exists()) {
			boolean isCreated = p.mkdirs();
			if (!isCreated) {
				logger.debug(MessageFormatEx.format("Failed to create dir:{0}", p
						.getPath()));
			}
		}
		if (!remoteFile.exists()) {
			boolean isCreated = remoteFile.createNewFile();
			if (!isCreated) {
				logger.debug(MessageFormatEx.format("Failed to create file:{0}",
						remoteFile.getPath()));
			}
		} else {
			remoteFile.setWritable(true);
		}

		InputStream source = new FileInputStream(srcFile);
		OutputStream dest = new FileOutputStream(remoteFile);
		copy(source, dest);
		close(source);
		close(dest);
	}

	private void close(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Throwable ex) {
		}
	}

	private void close(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (Throwable ex) {
		}
	}

	public static long copy(InputStream source, OutputStream dest)
			throws IOException {
		return copy(source, dest, false);
	}

	public static long copy(InputStream source, OutputStream dest, boolean flush)
			throws IOException {
		byte[] buffer = new byte[64 * 1024];
		long total = 0;
		int readBytes;
		OutputStream buffedDest = new BufferedOutputStream(dest, buffer.length);
		InputStream buffedSrc = new BufferedInputStream(source, buffer.length);

		while ((readBytes = buffedSrc.read(buffer)) != -1) {
			if (readBytes == 0) {
				readBytes = buffedSrc.read();
				if (readBytes < 0)
					break;
				buffedDest.write(readBytes);
				if (flush)
					buffedDest.flush();
				total++;
			} else {

				buffedDest.write(buffer, 0, readBytes);
				if (flush)
					buffedDest.flush();
				total += readBytes;
			}
		}
		return total;
	}

	private static boolean isRemote(String path) {
		if (path != null) {
			if (path.startsWith("\\\\?\\")) {
				String pattenStr = "^[\\\\]{2}[?]{1}[\\\\]{1}[A-Za-z]{1}[:]{1}.*";
				Pattern localP = Pattern.compile(pattenStr);
				// Pattern remoteP = Pattern
				// .compile("^[\\]{2}[?][\\]{1}U|uN|nC|c\\[^:`~!@#\\$\\^&*()=+[]{}\|;\\'\\\\",<>/?]+[\\]{1}");
				// // "\\?\UNC\server\share"
				// "\\?\D:\<long path>"
				Matcher m = localP.matcher(path);
				if (!m.matches()) {
					return true;
				}
			} else if (path.startsWith("\\\\")) {
				return true;
			}

		}
		return false;
	}

	public static int validateOnly(String userName, String pwd, String path,
			boolean checkWritable, boolean checkIsDirctory) {
		boolean isNetConnSucc = false;
		boolean isRemote = isRemote(path);
		String localdl = "";
		if (isRemote) {
			try {
				localdl = NetConnWithLocal(userName, pwd, path);
			} catch (NetConnException ne) {
				return ne.getCode();
			}
			isNetConnSucc = localdl != null && localdl.trim().length() > 0;
		}

		if (isRemote && !isNetConnSucc) {
			// log(failed to connect to path with username);
			return NS_CANNOTCONN;
		}

		File f = new File(path);
		if (!f.canRead()) {
			// log(path is not readable);
			return NS_CANNOTREAD;
		}
		if (checkWritable) {
			if (!f.canWrite()) {
				// log(path is not writable);
				return NS_CANNOTWRITE;
			}
		}
		if (checkIsDirctory) {
			if (!checkWritable(f)) {
				// log(path is not writable);
				return FS_ISNOTDIRECTORY;
			}
		}
		if (isNetConnSucc) {
			NetCancelWithLocal(localdl, true);
		}

		return 0;
	}

	private static void NetCancelWithLocal(String localdl, boolean isForce) {
		try {
			nativeFacade.NetCancel(localdl, isForce);
		} catch (ServiceException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}

	private static class NetConnException extends Exception {
		private static final long serialVersionUID = 3631205305892172213L;
		private int code;

		public NetConnException(int code, String msg) {
			super(msg);
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
	}

	private static String NetConnWithLocal(String userName, String pwd,
			String netPath) throws NetConnException {
		String local = "";
		char drvStartChar = 'Z';
		char drvEndChar = 'C';
		for (char dl = drvStartChar; dl >= drvEndChar; dl--) {
			local = dl + ":";
			
			int ret = nativeFacade.NetConnWithLocal(userName, pwd, local, netPath);
			if (ret == ERROR_ALREADY_ASSIGNED) {
				// System.out.println("The local device name is already in use.");
				continue;
			} else if (ret == 0) {
				break;
			} else if (ret == ERROR_LOGON_FAILURE) {
				throw new NetConnException(ret,
						"Logon failure: unknown user name or bad password");

			} else if (ret == ERROR_SESSION_CREDENTIAL_CONFLICT) {
				throw new NetConnException(
						ret,
						"Multiple connections to a server or shared resource by the same user, using more than one user name, are not allowed. Disconnect all previous connections to the server or shared resource and try again");

			} else if (ret == ERROR_NO_NET_OR_BAD_PATH) {
				throw new NetConnException(
						ret,
						"The network path was either typed incorrectly, does not exist, or the network provider is not currently available. Please try retyping the path or contact your network administrator.");

			} else {
				break;
			}
		}
		return local;
	}
}
