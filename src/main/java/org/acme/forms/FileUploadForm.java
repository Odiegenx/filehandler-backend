package org.acme.forms;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;

import java.io.InputStream;

public class FileUploadForm {

    @FormParam("fileName")
    private String fileName;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream data;

    @FormParam("fileSize")
    private long fileSize;

    @HeaderParam("Content-Type")
    private String contentType;


    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
