package ch.supsi.fscli.backend.repository;

public interface Serializable {
    boolean isSaved();
    String getFileFullPathName();
    void setFileFullPathName(String fileFullPathName);
}
