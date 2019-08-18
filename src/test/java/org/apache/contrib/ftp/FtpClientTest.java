package org.apache.contrib.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;

public class FtpClientTest {
    public static void main(String[] args) {
        String tmpStr = "~/";
        if(tmpStr.startsWith("~/")){
            tmpStr = tmpStr.substring(2);
        }
        System.out.println(tmpStr.equals(""));
    }
}
