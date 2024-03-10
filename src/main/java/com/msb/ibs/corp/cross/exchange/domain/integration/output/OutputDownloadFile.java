package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class OutputDownloadFile {

    private String fileName;
    private byte[] file;


    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
}
