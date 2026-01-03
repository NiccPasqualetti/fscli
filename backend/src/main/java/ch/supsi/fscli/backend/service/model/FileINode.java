package ch.supsi.fscli.backend.service.model;

public class FileINode extends INode implements IHardLinkable {

    private long referenceCount = 1;

    public FileINode(long id, DirectoryINode parent) {
        super(id, INodeType.FILE, parent);
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
