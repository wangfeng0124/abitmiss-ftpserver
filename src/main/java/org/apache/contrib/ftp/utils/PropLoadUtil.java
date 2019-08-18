package org.apache.contrib.ftp.utils;

import org.apache.contrib.ftp.embeded.CusFtpConfiguration;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * property file load util
 * @author wangfeng0124
 */
public class PropLoadUtil {

    public static boolean PROP_LOAD_FLAG = false;
    private static Properties PROP = PROP_LOAD_FLAG?loadPropFile(CusFtpConfiguration.CONFIG_FILE_NAME):null;

    public static void loadConfigOnStart(){

    }
    /**
     * load property file
     * @param propFilePath
     * @return
     */
    public static Properties loadPropFile(String propFilePath) {
        Properties tmpProp = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFilePath);

        if (inputStream != null) {
            try {
                tmpProp.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Property file " + propFilePath + " load error, Please check!");
                System.exit(2);
            }
        } else {
            System.err.println("Property file " + propFilePath + " not found in the classpath.");
            System.exit(1);
        }
        checkListInClasspath(tmpProp);
        return tmpProp;
    }

    /**
     * check if the file in the list is in the classpath
     */
    public static void checkListInClasspath(Properties prop){
        String[] fileNameArr = prop.getProperty(CusFtpConfiguration.CHECK_LIST_IN_CLASSPATH).split(SeparatorConsts.SEPARATOR_COMMA, -1);
        checkListInClasspath(fileNameArr);
    }

    /**
     * check if the file in the list is in the classpath
     */
    public static void checkListInClasspath(String[] fileNameArr){
        for(String tmpFileName:fileNameArr){
            System.out.println(tmpFileName);

            URL url = Thread.currentThread().getContextClassLoader().getResource(tmpFileName);
            if(url == null){
                System.err.println("Property file " + tmpFileName + " not found in the classpath.");
                System.exit(3);
            }
        }
    }

    /**
     * get value from PROP
     * @param key
     * @return
     */
    public static String getValue(String key){
        return PROP.getProperty(key);
    }

}
