package org.apache.contrib.ftp.spring;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.config.spring.SpringUtil;
import org.apache.ftpserver.ipfilter.IpFilterType;
import org.apache.ftpserver.ipfilter.RemoteIpFilter;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.net.UnknownHostException;

public class HdfsListenerBeanDefinitionParser extends
        AbstractSingleBeanDefinitionParser {
    private final Logger LOG = LoggerFactory.getLogger(HdfsListenerBeanDefinitionParser.class);

    private String ftpserverNameSpace;

    public HdfsListenerBeanDefinitionParser(String ftpserverNameSpace) {
        this.ftpserverNameSpace = ftpserverNameSpace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getBeanClass(final Element element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParse(final Element element,
                           final ParserContext parserContext,
                           final BeanDefinitionBuilder builder) {

        BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(ListenerFactory.class);

        if (StringUtils.hasText(element.getAttribute("port"))) {
            factoryBuilder.addPropertyValue("port", Integer.valueOf(element
                    .getAttribute("port")));
        }

        SslConfiguration ssl = parseSsl(element);
        if (ssl != null) {
            factoryBuilder.addPropertyValue("sslConfiguration", ssl);
        }

        Element dataConElm = SpringUtil.getChildElement(element,
                ftpserverNameSpace, "data-connection");
        DataConnectionConfiguration dc = parseDataConnection(dataConElm, ssl);
        factoryBuilder.addPropertyValue("dataConnectionConfiguration", dc);

        if (StringUtils.hasText(element.getAttribute("idle-timeout"))) {
            factoryBuilder.addPropertyValue("idleTimeout", SpringUtil.parseInt(
                    element, "idle-timeout", 300));
        }

        String localAddress = SpringUtil.parseStringFromInetAddress(element,
                "local-address");
        if (localAddress != null) {
            factoryBuilder.addPropertyValue("serverAddress", localAddress);
        }
        factoryBuilder.addPropertyValue("implicitSsl", SpringUtil.parseBoolean(
                element, "implicit-ssl", false));

        Element blacklistElm = SpringUtil.getChildElement(element,
                ftpserverNameSpace, "blacklist");
        if (blacklistElm != null) {
            LOG
                    .warn("Element 'blacklist' is deprecated, and may be removed in a future release. Please use 'remote-ip-filter' instead. ");
            try {
                RemoteIpFilter remoteIpFilter = new RemoteIpFilter(IpFilterType.DENY,
                        blacklistElm.getTextContent());
                factoryBuilder.addPropertyValue("sessionFilter", remoteIpFilter);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(
                        "Invalid IP address or subnet in the 'blacklist' element",
                        e);
            }
        }

        Element remoteIpFilterElement = SpringUtil.getChildElement(element,
                ftpserverNameSpace, "remote-ip-filter");
        if (remoteIpFilterElement != null) {
            if (blacklistElm != null) {
                throw new FtpServerConfigurationException(
                        "Element 'remote-ip-filter' may not be used when 'blacklist' element is specified. ");
            }
            String filterType = remoteIpFilterElement.getAttribute("type");
            try {
                RemoteIpFilter remoteIpFilter = new RemoteIpFilter(IpFilterType
                        .parse(filterType), remoteIpFilterElement
                        .getTextContent());
                factoryBuilder
                        .addPropertyValue("sessionFilter", remoteIpFilter);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(
                        "Invalid IP address or subnet in the 'remote-ip-filter' element");
            }
        }

        BeanDefinition factoryDefinition = factoryBuilder.getBeanDefinition();

        String listenerFactoryName = parserContext.getReaderContext().generateBeanName(factoryDefinition);

        BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(factoryDefinition, listenerFactoryName);
        registerBeanDefinition(factoryHolder, parserContext.getRegistry());

        // set the factory on the listener bean
        builder.getRawBeanDefinition().setFactoryBeanName(listenerFactoryName);
        builder.getRawBeanDefinition().setFactoryMethodName("createListener");
    }

    private SslConfiguration parseSsl(final Element parent) {
        Element sslElm = SpringUtil.getChildElement(parent,
                ftpserverNameSpace, "ssl");

        if (sslElm != null) {
            SslConfigurationFactory ssl = new SslConfigurationFactory();

            Element keyStoreElm = SpringUtil.getChildElement(sslElm,
                    ftpserverNameSpace, "keystore");
            if (keyStoreElm != null) {
                ssl.setKeystoreFile(SpringUtil.parseFile(keyStoreElm, "file"));
                ssl.setKeystorePassword(SpringUtil.parseString(keyStoreElm,
                        "password"));

                String type = SpringUtil.parseString(keyStoreElm, "type");
                if (type != null) {
                    ssl.setKeystoreType(type);
                }

                String keyAlias = SpringUtil.parseString(keyStoreElm,
                        "key-alias");
                if (keyAlias != null) {
                    ssl.setKeyAlias(keyAlias);
                }

                String keyPassword = SpringUtil.parseString(keyStoreElm,
                        "key-password");
                if (keyPassword != null) {
                    ssl.setKeyPassword(keyPassword);
                }

                String algorithm = SpringUtil.parseString(keyStoreElm,
                        "algorithm");
                if (algorithm != null) {
                    ssl.setKeystoreAlgorithm(algorithm);
                }
            }

            Element trustStoreElm = SpringUtil.getChildElement(sslElm,
                    ftpserverNameSpace, "truststore");
            if (trustStoreElm != null) {
                ssl.setTruststoreFile(SpringUtil.parseFile(trustStoreElm,
                        "file"));
                ssl.setTruststorePassword(SpringUtil.parseString(trustStoreElm,
                        "password"));

                String type = SpringUtil.parseString(trustStoreElm, "type");
                if (type != null) {
                    ssl.setTruststoreType(type);
                }

                String algorithm = SpringUtil.parseString(trustStoreElm,
                        "algorithm");
                if (algorithm != null) {
                    ssl.setTruststoreAlgorithm(algorithm);
                }
            }

            String clientAuthStr = SpringUtil.parseString(sslElm,
                    "client-authentication");
            if (clientAuthStr != null) {
                ssl.setClientAuthentication(clientAuthStr);
            }

            String enabledCiphersuites = SpringUtil.parseString(sslElm,
                    "enabled-ciphersuites");
            if (enabledCiphersuites != null) {
                ssl.setEnabledCipherSuites(enabledCiphersuites.split(" "));
            }

            String protocol = SpringUtil.parseString(sslElm, "protocol");
            if (protocol != null) {
                ssl.setSslProtocol(protocol);
            }

            return ssl.createSslConfiguration();
        } else {
            return null;
        }

    }

    private DataConnectionConfiguration parseDataConnection(
            final Element element,
            final SslConfiguration listenerSslConfiguration) {
        DataConnectionConfigurationFactory dc = new DataConnectionConfigurationFactory();

        if (element != null) {

            dc.setImplicitSsl(SpringUtil.parseBoolean(element, "implicit-ssl", false));

            // data con config element available
            SslConfiguration ssl = parseSsl(element);

            if (ssl != null) {
                LOG.debug("SSL configuration found for the data connection");
                dc.setSslConfiguration(ssl);
            }

            dc.setIdleTime(SpringUtil.parseInt(element, "idle-timeout", dc.getIdleTime()));

            Element activeElm = SpringUtil.getChildElement(element,
                    ftpserverNameSpace, "active");
            if (activeElm != null) {
                dc.setActiveEnabled(SpringUtil.parseBoolean(activeElm, "enabled",
                        true));
                dc.setActiveIpCheck(SpringUtil.parseBoolean(activeElm,
                        "ip-check", false));
                dc.setActiveLocalPort(SpringUtil.parseInt(activeElm,
                        "local-port", 0));

                String localAddress = SpringUtil.parseStringFromInetAddress(
                        activeElm, "local-address");
                if (localAddress != null) {
                    dc.setActiveLocalAddress(localAddress);
                }
            }

            Element passiveElm = SpringUtil.getChildElement(element,
                    ftpserverNameSpace, "passive");
            if (passiveElm != null) {
                String address = SpringUtil.parseStringFromInetAddress(passiveElm,
                        "address");
                if (address != null) {
                    dc.setPassiveAddress(address);
                }

                String externalAddress = SpringUtil.parseStringFromInetAddress(
                        passiveElm, "external-address");
                if (externalAddress != null) {
                    dc.setPassiveExternalAddress(externalAddress);
                }

                String ports = SpringUtil.parseString(passiveElm, "ports");
                if (ports != null) {
                    dc.setPassivePorts(ports);
                }
                dc.setPassiveIpCheck(SpringUtil.parseBoolean(passiveElm,
                        "ip-check", false));
            }
        } else {
            // no data conn config element, do we still have SSL config from the
            // parent?
            if (listenerSslConfiguration != null) {
                LOG
                        .debug("SSL configuration found for the listener, falling back for that for the data connection");
                dc.setSslConfiguration(listenerSslConfiguration);
            }
        }

        return dc.createDataConnectionConfiguration();
    }
}
