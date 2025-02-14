package org.acme.DTO;

public class UploadProgressDTO {
    private String fileName;
    private long uploadedParts;
    private long totalParts;
    private double percentage;

    public UploadProgressDTO() {

    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getUploadedParts() {
        return uploadedParts;
    }

    public void setUploadedParts(long uploadedParts) {
        this.uploadedParts = uploadedParts;
    }

    public long getTotalParts() {
        return totalParts;
    }

    public void setTotalParts(long totalParts) {
        this.totalParts = totalParts;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
