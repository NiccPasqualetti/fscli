package ch.supsi.fscli.backend.service.model;

public interface INodeOperations {
    long getId();

    INodeType getType();

    DirectoryINode getParent();

    void setParent(DirectoryINode parent);

    String getName();
}
