package org.jigsaw.payment.rpc.register;

import org.apache.curator.framework.AuthInfo;

import java.util.Arrays;

/**
 * A widget class for cofiguring digest auth.
 *
 * @author shamphone@gmail.com
 * @version 1.0.0  5/16/16
 **/
public class DigestAuthInfo extends AuthInfo{
    private static final String SCHEMA = "digest";

    private String username;
    private String password;

    /**
     * @param username
     * @param password
     */
    public DigestAuthInfo(String username, String password) {
        super(SCHEMA, (username+":"+password).getBytes());
        this.username = username;
        this.password = password;
    }


    public DigestAuthInfo(){
        super(SCHEMA, null);
    }


    @Override
    public String getScheme() {
       return SCHEMA;
    }


    @Override
    public byte[] getAuth() {
        return (username+":"+password).getBytes();
    }


    @Override
    public String toString() {
        return "AuthInfo{" +
                "scheme='" + SCHEMA + '\'' +
                ", auth=" + Arrays.toString(this.getAuth()) +
                '}';
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }

}
