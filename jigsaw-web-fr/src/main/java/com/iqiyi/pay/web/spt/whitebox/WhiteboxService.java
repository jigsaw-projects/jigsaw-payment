package com.iqiyi.pay.web.spt.whitebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 白盒加密 服务类
 * Created by lihengjun on 2016/4/15.
 */
@Component
public class WhiteboxService {
    public final static Logger LOGGER = LoggerFactory.getLogger(WhiteboxService.class);
    
    /**
     * AES 加密密钥文件名称
     */
    public final static String AES_ENCRYP_KEY_FILE_NAME = "aes_encrypt_key";
    /**
     * AES 解密密钥文件名称
     */
    public final static String AES_DECRYP_KEY_FILE_NAME = "aes_decrypt_key";
    /**
     * RSA 服务端证书
     */
    public final static String RSA_SERVER_PRI = "rsa_server_pri.der";
    /**
     * RSA 移动端证书
     */
    public final static String RSA_CLIENT_PRI = "rsa_client_pri.der";
    
    
    
    /**
     * 白盒加解密文件路径
     * /data/wallet.conf/whitebox/1.0/
     */
    
    @Value("${whitebox.path:/data/config/whitebox/}")
    public String whiteBoxPath;;

    @Value("${secret.pc.platform}")
    private String platform;
    

    /**
     * 获取AES 解密私钥
     *
     * @param version
     * @return
     */
    @Cacheable(value = "whiteboxCache", key = "'getDecryptKey'+#version")
    public String getAESDecryptKey(String version, String platform) throws IOException {
        if(platform.equals(this.platform)){
            platform = "pcw";
        }
        File file = this.newFileByVersionAndName(version, platform, AES_DECRYP_KEY_FILE_NAME);
        return FileUtils.readFileToString(file);
    }

    /**
     * 获取AES 加密私钥
     *
     * @param version
     * @return
     */
    @Cacheable(value = "whiteboxCache", key = "'getEncryptKey'+#version")
    public String getAESEncryptKey(String version, String platform) throws IOException {
        if(this.platform.equals(platform)){
            platform = "pcw";
        }
        File file = this.newFileByVersionAndName(version, platform, AES_ENCRYP_KEY_FILE_NAME);
        return FileUtils.readFileToString(file);
    }

    /**
     * 获取服务端私钥证书 文件
     *
     * @param version
     * @return
     */
    @Cacheable(value = "whiteboxCache", key = "'getRsaServerPriFile'+#version")
    public File getRsaServerPriFile(String version, String platform) throws FileNotFoundException {
        if(this.platform.equals(platform)){
            platform = "pcw";
        }
        return this.newFileByVersionAndName(version, platform, RSA_SERVER_PRI);
    }

    /**
     * 获取客户端私钥证书文件
     *
     * @param version
     * @return
     */
    @Cacheable(value = "whiteboxCache", key = "'getRsaClientPriFile'+#version")
    public File getRsaClientPriFile(String version, String platform) throws FileNotFoundException {
       return this.newFileByVersionAndName(version, platform, RSA_CLIENT_PRI);
    }

    /**
     * 根据版本号 文件查找文件
     * /data/wallet.config/whitebox/$version/$filename
     * @param version
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    private File newFileByVersionAndName(String version, String platform, String filename) throws FileNotFoundException {
        StringBuilder fullFilename = new StringBuilder(whiteBoxPath);
        File file = null;
        //文件路径为：/data/wallet.conf/whitebox/1.0/
        if (StringUtils.isBlank(platform)) {
        	 fullFilename.append(version).append(File.separatorChar).append(filename);
        } else {
        	 fullFilename.append(platform).append(File.separatorChar).append(version).append(File.separatorChar).append(filename);
        }
       
        file = new File(fullFilename.toString());
        if (!file.exists()) {
            throw new FileNotFoundException("file not found:"+fullFilename.toString());
        }

        return file;
    }

}
