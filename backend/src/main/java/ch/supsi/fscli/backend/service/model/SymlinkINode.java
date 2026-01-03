package ch.supsi.fscli.backend.service.model;

public class SymlinkINode extends INode implements IHardLinkable {

    private long referenceCount = 1;
    private final INode destination;

    public SymlinkINode(long id, DirectoryINode parent, INode destination) {
        super(id, INodeType.SYM_LINK, parent);
        this.destination = destination;
    }

    public INode getDestination() {
        return destination;
    }

    @Override
    public long getReferenceCount() {
        return referenceCount;
    }

    @Override
    public void increaseReferenceCount() {
        referenceCount++;
    }

    @Override
    public void decreaseReferenceCount() {
        referenceCount--;
    }
}
