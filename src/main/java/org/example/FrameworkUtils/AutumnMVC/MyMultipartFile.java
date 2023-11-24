package org.example.FrameworkUtils.AutumnMVC;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@Data
public class MyMultipartFile {
    private String name;
    private String originalFilename;
    private String contentType;
    private byte[] fileContent;


    public byte[] getBytes() throws IOException {
        return this.fileContent;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.fileContent);
    }
}
