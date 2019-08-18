package org.apache.contrib.ftp.spring;

import org.apache.contrib.ftp.filesystem.hadoop.HdfsFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class HdfsFileSystemBeanDefinitionParser extends
        AbstractSingleBeanDefinitionParser {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends FileSystemFactory> getBeanClass(
            final Element element) {
        return HdfsFileSystemFactory.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParse(final Element element,
                           final ParserContext parserContext,
                           final BeanDefinitionBuilder builder) {
        if (StringUtils.hasText(element.getAttribute("file-system"))) {
            builder.addPropertyValue("fileSystem", Boolean
                    .valueOf(element.getAttribute("file-system")));
        }

        if (StringUtils.hasText(element.getAttribute("case-insensitive"))) {
            builder.addPropertyValue("caseInsensitive", Boolean
                    .valueOf(element.getAttribute("case-insensitive")));
        }
        if (StringUtils.hasText(element.getAttribute("create-home"))) {
            builder.addPropertyValue("createHome", Boolean
                    .valueOf(element.getAttribute("create-home")));
        }
    }
}
