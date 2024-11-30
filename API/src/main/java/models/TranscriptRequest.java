package models;
public class TranscriptRequest {
    private String fileName;
    private String fileContent;
    private String aiProviderName;
    private String aiProviderKey;

    public TranscriptRequest(){ }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getAiProviderName() {
        return aiProviderName;
    }

    public void setAiProviderName(String aiProviderName) {
        this.aiProviderName = aiProviderName;
    }

    public String getAiProviderKey() {
        return aiProviderKey;
    }

    public void setAiProviderKey(String aiProviderKey) {
        this.aiProviderKey = aiProviderKey;
    }
}
