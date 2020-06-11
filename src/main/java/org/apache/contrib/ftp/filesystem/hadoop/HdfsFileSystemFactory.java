package org.apache.contrib.ftp.filesystem.hadoop;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.hadoop.conf.Configuration;
import org.apache.contrib.ftp.utils.StringUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * hadoop file system factory
 * @author wangfeng0124
 */
public class HdfsFileSystemFactory implements FileSystemFactory, InitializingBean {
    private final Logger LOGGER = LoggerFactory.getLogger(HdfsFileSystemFactory.class);
    private boolean createHome = true;
    private boolean caseInsensitive = false;
    private List<String> confFileStrList;
    private boolean checkConfFileListFlag = true;

    public HdfsFileSystemFactory() {
        super();
    }

    public HdfsFileSystemFactory(List<String> confFileStrList) {
        this(confFileStrList, true, true);
    }

    public HdfsFileSystemFactory(List<String> confFileStrList, boolean caseInsensitive, boolean createHome) {
        this.confFileStrList = confFileStrList;
        this.caseInsensitive = caseInsensitive;
        this.createHome = createHome;
    }

    /**
     * create FileSystemView object
     * @param user
     * @return
     * @throws FtpException
     */
    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        synchronized (user) {
            // ---------- set hadoop file system ----------
            Configuration configuration = new Configuration();
            configuration.setBoolean("fs.hdfs.impl.disable.cache", true);
            FileSystem fileSystem = null;
            /*for(String confFileStr:this.confFileStrList){
                System.out.println(confFileStr);
                configuration.addResource(confFileStr);
            }*/
            try {
                fileSystem = FileSystem.newInstance(configuration);
            } catch(IOException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            if(fileSystem == null){
                System.out.println("hadoop file system create error, please check your config.");
                System.exit(1);
            }

            // create home if does not exist
            LOGGER.info("create home if it does not exist");
            if (createHome) {
                String homeDirStr = StringUtil.getHdfsPrefixStr(fileSystem.getHomeDirectory().toString()) + StringUtil.getCusHomeDir(user.getHomeDirectory());// + "/user/" + user.getName();
                try {
                    Path homeDirPath = new Path(homeDirStr);
                    if (fileSystem.isFile(homeDirPath)) {
                        LOGGER.warn("Not a directory :: " + homeDirStr);
                        throw new FtpException("Not a directory :: " + homeDirStr);
                    }
                    if((!fileSystem.exists(homeDirPath)) && (!fileSystem.mkdirs(homeDirPath))) {
                        LOGGER.warn("Cannot create user home :: " + homeDirStr);
                        throw new FtpException("Cannot create user home :: "
                                + homeDirStr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            FileSystemView fsView = new HdfsFileSystemView(fileSystem, user, caseInsensitive);
            return fsView;
        }
    }

    /**
     * check if the file in the list is in the classpath
     */
    public void checkListInClasspath() {
        if(!this.checkConfFileListFlag || this.confFileStrList.size() == 0){
            return ;
        }
        URL url = Thread.currentThread().getContextClassLoader().getResource("./");
        File file = null;
        Set<String> fileNameSet = new HashSet<String>();
        try {
            file = new File(url.toURI());
            File[] fileArr = file.listFiles();

            for (File tmpFile : fileArr) {
                fileNameSet.add(tmpFile.getName());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String tmpFileName : this.confFileStrList) {
            if (!fileNameSet.contains(tmpFileName)) {
                System.err.println("hadoop config file " + tmpFileName + " not found in the classpath.");
                System.exit(3);
            }
        }
    }
    public boolean isCreateHome() {
        return createHome;
    }

    public void setCreateHome(boolean createHome) {
        this.createHome = createHome;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public List<String> getConfFileStrList() {
        return confFileStrList;
    }

    public void setConfFileStrList(List<String> confFileStrList) {
        this.confFileStrList = confFileStrList;
    }

    public boolean isCheckConfFileListFlag() {
        return checkConfFileListFlag;
    }

    public void setCheckConfFileListFlag(boolean checkConfFileListFlag) {
        this.checkConfFileListFlag = checkConfFileListFlag;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkListInClasspath();
    }
}
