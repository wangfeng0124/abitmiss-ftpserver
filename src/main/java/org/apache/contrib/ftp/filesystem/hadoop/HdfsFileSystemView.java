package org.apache.contrib.ftp.filesystem.hadoop;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.contrib.ftp.utils.StringUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * hadoop file system view
 * @author wangfeng0124
 */
public class HdfsFileSystemView implements FileSystemView {

    private final FileSystem fileSystem;

    // the root directory will never end with '/'
    private String rootDir = "";
    // the first character will always be "/"
    // the last character will never be "/"
    // It is always with respect to the root directory

    // must start with '/'
    private String currDir = "/";
    private final User user;
    private final boolean caseInsensitive;
    /**
     * Constructor - set the user object.
     */
    protected HdfsFileSystemView(FileSystem fileSystem, User user, boolean caseInsensitive)
            throws FtpException {

        this.fileSystem = fileSystem;
        if (user == null) {
            throw new IllegalArgumentException("user can not be null");
        }
        if (fileSystem.getHomeDirectory() == null) {
            throw new IllegalArgumentException(
                    "User home directory can not be null");
        }

        this.rootDir = StringUtil.getHdfsPrefixStr(fileSystem.getHomeDirectory().toString())
                        + StringUtil.getCusHomeDir(user.getHomeDirectory());
        this.user = user;
        this.caseInsensitive = caseInsensitive;
        currDir = "/";
    }

    @Override
    public FtpFile getHomeDirectory() throws FtpException {
        return new HdfsFtpFile(this.rootDir, fileSystem, user, rootDir);
    }

    @Override
    public FtpFile getWorkingDirectory() throws FtpException {
        return new HdfsFtpFile(this.rootDir + currDir, fileSystem, user, rootDir);
    }

    /**
     * maintian used to cd command
     * @param filePath
     * @return
     * @throws FtpException
     */
    @Override
    public boolean changeWorkingDirectory(String filePath) throws FtpException {
        // not a directory - return false
        String physicalFilePath = StringUtil.getPhysicalFilePath(rootDir, currDir, filePath, caseInsensitive);
        try {
            if (! this.fileSystem.isDirectory(new Path(physicalFilePath))) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // change current directory
        physicalFilePath = physicalFilePath.substring(rootDir.length());
        this.currDir = StringUtil.trimTrailingSlash(physicalFilePath) + "/";

        return true;
    }

    /**
     * get a ftpfile object
     * @param filePath
     * @return
     * @throws FtpException
     */
    @Override
    public FtpFile getFile(String filePath) throws FtpException {
        String physicalName = StringUtil.getPhysicalFilePath(rootDir,
                currDir, filePath, caseInsensitive);
        return new HdfsFtpFile(physicalName, fileSystem, user, rootDir);
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return true;
    }

    @Override
    public void dispose() {

    }

}