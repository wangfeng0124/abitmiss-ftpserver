package org.apache.contrib.ftp.embeded;

import org.apache.contrib.ftp.utils.PropLoadUtil;

import java.io.File;

/**
 * custom configuration class
 * @author wangfeng0124
 */
public class CusFtpConfiguration {
    /**
     * config property file name
     */
    public static final String CONFIG_FILE_NAME = "config.properties";
    /**
     * check if the file in the list is in the classpath.(except config property file)
     * separator by comma
     */
    public static final String CHECK_LIST_IN_CLASSPATH = "CHECK_LIST_IN_CLASSPATH";

    /**
     * user property file name
     */
    public static final File USER_PROP_FILE = new File(PropLoadUtil.getValue("USER_PROP_FILE_NAME"));

    /**
     * ftp port
     */
    public static final int FTP_PORT = Integer.parseInt(PropLoadUtil.getValue("FTP_PORT"));

    /**
     * passive port set
     */
    public static final String PASSIVE_PORT_STR = PropLoadUtil.getValue("PASSIVE_PORT_STR");

}
