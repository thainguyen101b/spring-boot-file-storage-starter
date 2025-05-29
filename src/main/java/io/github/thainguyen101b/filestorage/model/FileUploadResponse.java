package io.github.thainguyen101b.filestorage.model;

public class FileUploadResponse {
    private String fileId;
    private String fileName;
    private String downloadUrl;
    private long size;
    private String contentType;

    public FileUploadResponse(String fileId, String fileName, String downloadUrl, 
                            long size, String contentType) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.size = size;
        this.contentType = contentType;
    }

    // Getters and setters
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}