package com.msb.ibs.corp.cross.exchange.domain.integration.input;

public class TtrDocument {
    private Integer documentId;
    private Integer fileId;
    private String fileName;
    private String pathFile;
    private String docName;
    private String status;
    private String rejectContent;
    private String engDocName;

    public Integer getDocumentId() {
        return documentId;
    }
    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }
    public Integer getFileId() {
        return fileId;
    }
    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getPathFile() {
        return pathFile;
    }
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
    public String getDocName() {
        return docName;
    }
    public void setDocName(String docName) {
        this.docName = docName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRejectContent() {
        return rejectContent;
    }
    public void setRejectContent(String rejectContent) {
        this.rejectContent = rejectContent;
    }

    public String getEngDocName() {
        return engDocName;
    }

    public void setEngDocName(String engDocName) {
        this.engDocName = engDocName;
    }
}
