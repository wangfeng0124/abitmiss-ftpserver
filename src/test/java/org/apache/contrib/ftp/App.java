package org.apache.contrib.ftp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Pattern filePathPattern = Pattern.compile("(.*?)[/]+$");
        String tmpStr = "/ajlkfds/dfjls////";
        Matcher macher = filePathPattern.matcher(tmpStr);
        if(macher.find()){
            System.out.println(macher.group(1));
        }
        System.out.println( "Hello World!" );
        String hdfsPrefixPatternStr = "hdfs://[^/]+";
        String tempStr = "hdfs://test02-bigdata02:8020/user/test/abc";
        Pattern hdfsPrefixPattern = Pattern.compile(hdfsPrefixPatternStr);
        Matcher hdfsPrefixMatcher = hdfsPrefixPattern.matcher(tempStr);
        if(hdfsPrefixMatcher.find()){
            System.out.println(hdfsPrefixMatcher.group(0));
        }

        System.out.println(tempStr.replaceFirst(hdfsPrefixPatternStr, ""));

        System.out.println("hdfs://test02-bigdata02:8020/user/test/abc".replace("hdfs://test02-bigdata02:8020/user/test", ""));
    }
}
