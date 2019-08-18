package org.apache.contrib.ftp.spring;

import org.apache.ftpserver.config.spring.CommandFactoryBeanDefinitionParser;
import org.apache.ftpserver.config.spring.ServerBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HdfsFtpServerNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * The FtpServer Spring config namespace
     */
//    public final String FTPSERVER_NS = "http://mina.apache.org/ftpserver/spring/v2";
    public final String ftpserverNameSpace = "http://ftpserver.abitmiss.org/hdfs/spring/v1";
    /**
     * Register the necessary element names with the appropriate bean definition
     * parser
     */
    public HdfsFtpServerNamespaceHandler() {
        registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        registerBeanDefinitionParser("nio-listener",
                new HdfsListenerBeanDefinitionParser(this.ftpserverNameSpace));
        registerBeanDefinitionParser("file-user-manager",
                new HdfsUserManagerBeanDefinitionParser(this.ftpserverNameSpace));
        registerBeanDefinitionParser("db-user-manager",
                new HdfsUserManagerBeanDefinitionParser(this.ftpserverNameSpace));
        registerBeanDefinitionParser("hdfs-filesystem",
                new HdfsFileSystemBeanDefinitionParser());
        registerBeanDefinitionParser("commands",
                new CommandFactoryBeanDefinitionParser());

    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        // do nothing
    }
}
