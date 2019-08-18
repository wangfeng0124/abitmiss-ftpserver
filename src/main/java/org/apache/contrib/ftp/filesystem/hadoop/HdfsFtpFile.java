package org.apache.contrib.ftp.filesystem.hadoop;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.contrib.ftp.utils.StringUtil;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * hadoop ftp file object
 * @author wangfeng0124
 */
public class HdfsFtpFile implements FtpFile  {
    private final Logger LOG = LoggerFactory.getLogger(HdfsFtpFile.class);
    // the file name with respect to the user root.
    // The path separator character will be '/' and
    // it will always begin with '/'.
    private final String fileName;

    private final FileSystem fileSystem;
    private final User user;
//    private String groupName;
    private final String rootDir;

    /**
     * Constructor, internal do not use directly.
     */
    protected HdfsFtpFile(final String fileName, final FileSystem fileSystem,
                            final User user, final String rootDir) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }
        if (fileSystem == null) {
            throw new IllegalArgumentException("hdfs file system can not be null");
        }
        if (fileName.length() == 0) {
            throw new IllegalArgumentException("fileName can not be empty");
        }

        this.fileName = fileName;
        this.fileSystem = fileSystem;
        this.user = user;
        this.rootDir = rootDir;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAbsolutePath() {
        return getName();
    }

    /**
     * get file path in ftp, need remove root directory which configured in property file.
     * @return
     */
    @Override
    public String getName() {
        return this.fileName.replaceFirst(rootDir, "");
    }

    /**
     * obtain parent file Uri
     * @param fileUri
     * @return
     */
    public String getParentUri(String fileUri) {
        // trim all trail '/'
        fileUri = StringUtil.trimTrailingSlash(fileUri);

        // root - the short name will be '/'
        if (fileUri.equals(rootDir)) {
            return fileUri;
        }

        // return from the last '/'
        String parentName;
        int slashIndex = fileUri.lastIndexOf('/');
        if (slashIndex >= rootDir.length()) {
            parentName = fileUri.substring(0, slashIndex);
            return parentName;
        } else {
            return null;
        }
    }

    /**
     * get first parent uri
     * @param fileUri
     * @return
     */
    public String getParentFirstExist(String fileUri) {
        String parentFirstUri = fileUri;
        do{
            parentFirstUri = getParentUri(parentFirstUri);
        } while(! parentFirstUri.equals(rootDir) && ! doesExist(parentFirstUri));

        if(parentFirstUri == null) {
            return null;
        }

        return parentFirstUri;
    }

    public boolean doesExist(String fileUri) {
        try {
            return fileSystem.exists(new Path(fileUri));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // obtain status of parent file uri which exists first
    public FileStatus getDirStatus(String fileUri) {
        String parentFirstUri = getParentFirstExist(fileUri);
        if(parentFirstUri != null){
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(parentFirstUri));
                if(fileStatus.getOwner().equals(this.user.getName())){
                    return fileStatus;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean isHidden() {
        return ! doesExist();
    }

    @Override
    public boolean isDirectory() {
        try {
            return fileSystem.isDirectory(new Path(this.fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isFile() {
        try {
            return fileSystem.isFile(new Path(this.fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doesExist() {
        try {
            return fileSystem.exists(new Path(this.fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isReadable() {
/*        if(! doesExist()){
            return false;
        }*/
        try {
            FileStatus fileStatus;// = fileSystem.getFileStatus(new Path(this.fileName));
            if(doesExist()){
                fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
            } else{
                fileStatus = fileSystem.getFileStatus(new Path(getParentUri(this.fileName)));
            }
            FsPermission fsPermission = fileStatus.getPermission();
//            FsAction groupFsAction = fsPermission.getGroupAction();
            FsAction userFsAction = fsPermission.getUserAction();
            FsAction otherFsAction = fsPermission.getOtherAction();
            if(fileStatus.getOwner().equals(this.user.getName()) &&
                    (userFsAction.and(FsAction.READ).equals(FsAction.READ)) || otherFsAction.and(FsAction.READ).equals(FsAction.READ)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isWritable() {
/*        if(! doesExist()){
            return false;
        }*/
        try {
            FileStatus fileStatus;// = fileSystem.getFileStatus(new Path(this.fileName));
            if(doesExist()){
                fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
            } else{
                fileStatus = fileSystem.getFileStatus(new Path(getParentUri(this.fileName)));
            }
            FsPermission fsPermission = fileStatus.getPermission();
//            FsAction groupFsAction = fsPermission.getGroupAction();
            FsAction userFsAction = fsPermission.getUserAction();
            FsAction otherFsAction = fsPermission.getOtherAction();

            if(fileStatus.getOwner().equals(this.user.getName()) &&
                    (userFsAction.and(FsAction.WRITE).equals(FsAction.WRITE)) || otherFsAction.and(FsAction.WRITE).equals(FsAction.WRITE)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRemovable() {
        // root cannot be deleted
        if(StringUtil.trimTrailingSlash(fileName).equals(rootDir)){
            return false;
        }

        return isWritable();
    }

    @Override
    public String getOwnerName() {
        if(doesExist()) {
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
                return fileStatus.getOwner();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getGroupName() {
        if(doesExist()){
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
                return fileStatus.getGroup();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int getLinkCount() {
        return 0;
    }

    @Override
    public long getLastModified() {
        if(isReadable()){
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
                return fileStatus.getModificationTime();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public boolean setLastModified(long time) {
        if(isWritable()) {
            try {
                fileSystem.setTimes(new Path(this.fileName), time, time);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public long getSize() {
        if(isReadable()){
            try {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
                return fileStatus.getBlockSize();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public FileStatus getPhysicalFile() {
        if(isReadable()){
            try {
                return fileSystem.getFileStatus(new Path(this.fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean mkdir() {
        FileStatus fileStatus = getDirStatus(this.fileName);
        if(fileStatus != null){
            FsPermission fsPermission = new FsPermission(FsAction.ALL, FsAction.READ, FsAction.READ);
            try {
                fileSystem.mkdirs(new Path(this.fileName), fsPermission);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean delete() {
        if(isWritable()){
            try {
                fileSystem.delete(new Path(this.fileName), true);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean move(FtpFile destination) {
        if(isWritable() && destination.isWritable()){
            try{
                fileSystem.rename(new Path(this.fileName), new Path(destination.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<? extends FtpFile> listFiles() {
        if(! isReadable()){
            return null;
        }
        List<HdfsFtpFile> hdfsFtpFiles = new LinkedList<HdfsFtpFile>();
        try {
            if(isDirectory()){
                FileStatus[] fileStatusArr = fileSystem.listStatus(new Path(this.fileName));
                for(FileStatus fileStatus:fileStatusArr){
                    hdfsFtpFiles.add(new HdfsFtpFile(fileStatus.getPath().toString(), fileSystem, user, rootDir));
                }
            } else {
                FileStatus fileStatus = fileSystem.getFileStatus(new Path(this.fileName));
                hdfsFtpFiles.add(new HdfsFtpFile(fileStatus.getPath().toString(), fileSystem, user, rootDir));
            }
            return hdfsFtpFiles;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        if(! isWritable()){
            throw new IOException("No write permission : " + fileName);
        }
        try {
            FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(fileName));
            return fsDataOutputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        if (!isReadable()) {
            throw new IOException("No read permission : " + fileName);
        }
        try {
            FSDataInputStream fsDataInputStream = fileSystem.open(new Path(fileName));
            return fsDataInputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
