package org.apache.contrib.ftp.utils;

import java.io.File;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String util
 * @author wangfeng0124
 */
public class StringUtil {
    private static String HDFS_PREFIX_PATTERN_STR = "hdfs://[^/]+";
    private static Pattern HDFS_PREFIX_PATTERN = Pattern.compile(HDFS_PREFIX_PATTERN_STR);
    private static Pattern TRIM_TRAIL_SLASH_PATTERN = Pattern.compile("(.*?)[/]+$");

    /**
     * get hdfs prefix, such as:
     * "hdfs:${hostname}:${port}/${cusdir}" --> "hdfs:${hostname}:${port}"
     * @param hdfsHomeDir
     * @return
     */
    public static String getHdfsPrefixStr(String hdfsHomeDir){

        Matcher tmpMatcher = HDFS_PREFIX_PATTERN.matcher(hdfsHomeDir);
        if(tmpMatcher.find()){
            return tmpMatcher.group(0);
        }

        return "";
    }

    /**
     * get custom directory path string, such as
     * "hdfs:${hostname}:${port}/${cusdir}" --> "/${cusdir}"
     * @param cusHomeDir
     * @return
     */
    public static String getCusHomeDir(String cusHomeDir){

        return trimTrailingSlash(cusHomeDir).replaceFirst(HDFS_PREFIX_PATTERN_STR,"");
    }

    /**
     * Normalize separate character. replace all \ and / to /
     */
    public static String normalizeSeparateChar(final String pathName) {
        return pathName.replace(File.separator, SeparatorConsts.SEPARATOR_SLASH)
                                            .replaceAll("\\\\", SeparatorConsts.SEPARATOR_SLASH);
    }

    /**
     * trime the tail /,
     * such as "/abc/test/aa/////" ----> "/abc/test/aa", all '/' deleted
     * @param path
     * @return
     */
    public static String trimTrailingSlash(String path) {
        Matcher matcher = TRIM_TRAIL_SLASH_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return path;
    }

    /**
     * get physical file path, a true path
     * @param rootDir rootDir is "hdfs://${hostname}:${port}/${cusRootDir}" or "${cusRootDir}"
     * @param currDir
     * @param filePath
     * @return
     */
    public static String getPhysicalFilePath(final String rootDir,
                                         final String currDir, final String filePath, final boolean caseInsensitive) {

        // normalize file name
        String normalizedFileName = normalizeSeparateChar(filePath);
        String result;

        // if file name is relative, set resArg to root dir + curr dir
        // if file name is absolute, set resArg to root dir
        if (normalizedFileName.charAt(0) != '/') {
            // file name is relative
            result = rootDir + StringUtil.trimTrailingSlash(currDir);
        } else {
            result = rootDir;
        }

        if (normalizedFileName.startsWith("~/")) {
            normalizedFileName = normalizedFileName.substring(2);
        }

        // replace .and ..
        StringTokenizer st = new StringTokenizer(normalizedFileName, "/");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();

            // . => current directory
            if (tok.equals(".") || tok.equals("")) {
                // ignore and move on
            } else if (tok.equals("..")) {
                // .. => parent directory (if not root)
                if (result.startsWith(rootDir + "/")) {
                    int slashIndex = result.lastIndexOf('/');
                    if (slashIndex != -1) {
                        result = result.substring(0, slashIndex);
                    }
                }
            } else {
                // token is normal directory name
                result = result + '/' + tok;
            }
        }
        if(caseInsensitive){
            return result.toLowerCase();
        } else{
            return result;
        }

    }

    public static void main(String[] args) {
        String tmpStr = "hdfs://test02-bigdata02:8020/user/test/abc";
        System.out.println(getHdfsPrefixStr(tmpStr));
        System.out.println(getCusHomeDir(tmpStr));

        System.out.println("12233\\goodmo\\rning".replace('\\', '/'));
        System.out.println("12233\\goodmor\\ning".replaceAll("\\\\", SeparatorConsts.SEPARATOR_SLASH));
        System.out.println("\\\\");
        System.out.println("12233\\goodmor\\ning");
        System.out.println(trimTrailingSlash("/a/abjfkl/ bmjkj/j////"));
        System.out.println(getPhysicalFilePath("hdfs://test02-bigdata02:8020/user/test/abc", "/sss/ddDDd/eee/jjlkl", "../../../../../", false));
        System.out.println(getPhysicalFilePath("hdfs://test02-bigdata02:8020/user/test/abc", "/sss/dSSSdd/eee/jjlkl", "jklfjdlks/jsjdlfd", true));
        System.out.println(getPhysicalFilePath("hdfs://test02-bigdata02:8020/user/test/abc", "/sss/ddd/eee/jjlkl", "/", false));
        System.out.println(getPhysicalFilePath("hdfs://test02-bigdata02:8020/user/test/abc", "/", "jfdlkslkfds/jlkfdlkdslls", false));
        System.out.println("hdfs://test02-bigdata02:8020/user/test/abc/sss/ddd/eee/jjlkl".substring("hdfs://test02-bigdata02:8020/user/test/abc".length()));
        String fileUri = "hdfs://test02-bigdata02:8020/user";
        String rootDir = "hdfs://test02-bigdata02:8020";
        String parentName;
        int slashIndex = fileUri.lastIndexOf('/');
        if (slashIndex >= rootDir.length()) {
            parentName = fileUri.substring(0, slashIndex);
            System.out.println("succ:" + parentName);
        } else {
            System.out.println("failed");
        }

    }
}
