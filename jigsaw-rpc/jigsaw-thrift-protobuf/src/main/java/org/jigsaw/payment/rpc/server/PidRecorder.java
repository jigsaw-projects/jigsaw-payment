package org.jigsaw.payment.rpc.server;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月5日
 */
public class PidRecorder {
	private static final Logger LOG = LoggerFactory
			.getLogger(PidRecorder.class);

	/**
	 * Get pid.
	 * 
	 * @return
	 */
	public Long getPID() {
		String processName = ManagementFactory.getRuntimeMXBean().getName();
		return fetchPidFromPidInfo(processName.split("@")[0]);
	}

	/**
	 * Fetch pid from the info string that contains the pid, but the pid info
	 * string maybe is not the same. For example: the pidInfo maybe is in one of
	 * those situations: 1: GNU libgcj 4.4.7 20120313 (Red Hat 4.4.7-3) [10600
	 * 2: 32199
	 * 
	 * @param pidInfo
	 * @return
	 */
	private Long fetchPidFromPidInfo(String pidInfo) {
		if (pidInfo == null || pidInfo.length() < 1) {
			return 0L;
		}

		// Judge whether it is a purely digital string.
		int index = findLastNonNumericCharIndexOfPidInfo(pidInfo);
		if (index == -1) {
			// It only contains the digital string.
			return Long.parseLong(pidInfo);
		}

		// It contains other non-numeric string.
		String pid = pidInfo.substring(index + 1);
		return Long.parseLong(pid);
	}

	/**
	 * Find the index of last non numeric char in the pid info string.
	 * 
	 * @param pidInfo
	 * @return the index of the last occurrence of the non-numeric character in
	 *         the character sequence represented by this object, or -1 if the
	 *         character does not occur.
	 */
	private int findLastNonNumericCharIndexOfPidInfo(String pidInfo) {
		int index = -1;
		for (int i = pidInfo.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(pidInfo.charAt(i))) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Write pid to pid file.
	 * 
	 * @param pidFile
	 * @return
	 */
	public boolean writePidToFile(String pidFile) {
		FileWriter pidFileWriter = null;

		try {
			pidFileWriter = new FileWriter(pidFile);
			pidFileWriter.write(getPID().toString());
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false;
		} finally {
			try {
				pidFileWriter.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
				return false;
			}
		}
		return true;
	}
}
