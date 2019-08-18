package org.apache.contrib.ftp.embeded;

import org.apache.contrib.ftp.filesystem.hadoop.HdfsFileSystemFactory;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.contrib.ftp.utils.PropLoadUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FtpServerEmbeded {
    public void test01(){


        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

        // ---------- set data connect ----------
        DataConnectionConfigurationFactory dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory();
        dataConnectionConfigurationFactory.setPassivePorts(CusFtpConfiguration.PASSIVE_PORT_STR);
        dataConnectionConfigurationFactory.setActiveEnabled(true);
//        dataConnectionConfigurationFactory.setActiveLocalAddress("0.0.0.0");
        dataConnectionConfigurationFactory.setActiveLocalPort(12221);
//        dataConnectionConfigurationFactory.setActiveIpCheck(true);
        DataConnectionConfiguration dataConnectionConfig = dataConnectionConfigurationFactory.createDataConnectionConfiguration();
        factory.setDataConnectionConfiguration(dataConnectionConfig);

        // ---------- set the port of the listener ----------
        factory.setPort(CusFtpConfiguration.FTP_PORT);
        // replace the default listener
        serverFactory.addListener("default", factory.createListener());

        // ---------- set user manager ----------
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//        userManagerFactory.setFile(new File("users.properties"));
        userManagerFactory.setFile(CusFtpConfiguration.USER_PROP_FILE);
        serverFactory.setUserManager(userManagerFactory.createUserManager());

        // ---------- set hadoop file system ----------
        List<String> confFileStrList = new LinkedList<String>();
        confFileStrList.add("core-site.xml");
        confFileStrList.add("hdfs-site.xml");

        serverFactory.setFileSystem(new HdfsFileSystemFactory(confFileStrList));

        // ---------- start the server ----------
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
            this.addShutdownHook(server);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
    public void test02(){
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(2221);
        // define SSL configuration
        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
        ssl.setKeystorePassword("password");
        // set the SSL configuration for the listener
        factory.setSslConfiguration(ssl.createSslConfiguration());
        factory.setImplicitSsl(true);
        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("myusers.properties"));
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        // start the server
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add shutdown hook.
     */
    private void addShutdownHook(final FtpServer engine) {

        // create shutdown hook
        Runnable shutdownHook = new Runnable() {
            public void run() {
                System.out.println("Stopping server...");
                engine.stop();
            }
        };
        // add shutdown hook
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }

    public static void main(String[] args) {
        PropLoadUtil.PROP_LOAD_FLAG = true;
        FtpServerEmbeded ftpServerEmbeded = new FtpServerEmbeded();
    }
}
