package org.jigsaw.payment.rpc.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析当前服务器的IP地址
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月4日
 */
public class IpPortResolver {
    private static final Logger LOG = LoggerFactory.getLogger(IpPortResolver.class);

    private static final int MAX_PORT_RETRY_COUNT = 100;

    /**
     * 获取机器上可用的ipv4地址.
     * Note: 1 当有多个符合条件的ip地址时,只返回第一个ip地址
     *       2 192.168的地址会被过滤,不回返回
     * @return
     */
    public String getIpV4Address() {
        String ip = null;
        // 1. 首先获取eth0地址.
        try {
            NetworkInterface ni = NetworkInterface.getByName("eth0");
            if (ni != null) {
                ip = getFirstIpV4Address(ni.getInetAddresses());
            }
        } catch (SocketException e) {
            LOG.warn("Failed to directly get ip by name of \"ech0\"", e);
        }

        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        // 2.遍历机器上的所有网卡,取得ip地址.
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface networkInterface = en.nextElement();
                if (networkInterface == null) {
                    continue;
                }

                ip = getFirstIpV4Address(networkInterface.getInetAddresses());
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            LOG.error("Failed to get ip", e);
        }

        return ip;
    }

    /**
     * 判断指定的端口是否可用,并返回可用的端口
     * @param ip
     * @param port
     * @return
     */
    public int getAvailablePort(String ip, int port) {
        int availablePort = port;

        int retryCount = 0;
        while (retryCount ++ < MAX_PORT_RETRY_COUNT) {
            if (isPortAvailable("0.0.0.0", availablePort)
                    && isPortAvailable(ip, availablePort)) {
                break;
            }

            availablePort += 1;
        }

        return availablePort;
    }

    /**
     * 判断指定的端口是否可用.
     * @param ip
     * @param port
     * @return
     */
    private boolean isPortAvailable(String ip, int port) {
        boolean isOk = true;

        Socket socket = new Socket();
        try {
            socket.bind(new InetSocketAddress(ip, port));
            socket.close();
        } catch (IOException e) {
            LOG.warn("Failed to bind port in the machine: ", e);
            isOk = false;
        }

        return isOk;
    }

    /**
     * 遍历当前机器上的各个网卡信息，返回符合条件的ip地址.
     * Note: 1 当有多个符合条件的ip地址时,只返回第一个ip地址
     *       2 192.168的地址会被过滤
     * @param ias
     * @return
     */
    private String getFirstIpV4Address(Enumeration<InetAddress> ias) {
        String ip = null;
        if (ias == null) {
            return ip;
        }

        while (ias.hasMoreElements()) {
            InetAddress ia = ias.nextElement();
            String host = getIpV4Address(ia);
            if (StringUtils.isNotEmpty(host)
                    && !StringUtils.startsWith(host, "192.168")) {
                ip = host;
                break;
            }
        }

        return ip;
    }

    /**
     * 获取指定网卡上的ipv4地址
     * @param inetAddress
     * @return
     */
    private String getIpV4Address(InetAddress inetAddress) {
        if (inetAddress == null
                || inetAddress.isLoopbackAddress()
                || inetAddress instanceof Inet6Address) {
            return null;
        }

        if (inetAddress instanceof Inet4Address) {
            return inetAddress.getHostAddress();
        }

        return null;
    }
}
