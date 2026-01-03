package ch.supsi.fscli.backend.service.model;

import java.util.Map;

public abstract class INode implements INodeOperations {
    private final long id;
    private final INodeType type;
    private transient DirectoryINode parent;

    public INode(long id, INodeType type, DirectoryINode parent) {
        this.id = id;
        this.type = type;
        this.parent = parent;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public INodeType getType() {
        return type;
    }

    @Override
    public DirectoryINode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryINode parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        for (Map.Entry<String, INode> entry : parent.getChildren().entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }

}
