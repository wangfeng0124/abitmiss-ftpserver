package org.apache.contrib.ftp;

import org.apache.contrib.ftp.utils.PropLoadUtil;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FtpServerStartUp {
    public void usingXmlTest(String fileName){
        FtpServer server = null;
        System.out.println("Using XML configuration file " + fileName + "...");

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(fileName);

        if (ctx.containsBean("server")) {
            server = (FtpServer) ctx.getBean("server");
        } else {
            String[] beanNames = ctx.getBeanNamesForType(FtpServer.class);
            if (beanNames.length == 1) {
                server = (FtpServer) ctx.getBean(beanNames[0]);
            } else if (beanNames.length > 1) {
                System.out.println("Using the first server defined in the configuration, named " + beanNames[0]);
                server = (FtpServer) ctx.getBean(beanNames[0]);
            } else {
                System.err.println("XML configuration does not contain a server configuration");
            }
        }

        try {
            // start the server
            server.start();
            // add shutdown hook if possible
            this.addShutdownHook(server);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        System.out.println("FtpServer started");


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
        if(args.length != 1){
            System.out.println("param must only have one, it's a custom xml config file name.");
            System.out.println("this file must in classpath");
            System.exit(1);
        }

        PropLoadUtil.checkListInClasspath(args);

        FtpServerStartUp ftpServerStartUp = new FtpServerStartUp();
        ftpServerStartUp.usingXmlTest(args[0]);
    }
}
