package ch.supsi.fscli.backend.service.model;

public interface IHardLinkable extends INodeOperations {

    /**
     * Return the number of references (hardlinks) pointing to this object
     */
    long getReferenceCount();

    /**
     * Increases by one every time a hardlink pointing to the object is created
     */
    void increaseReferenceCount();

    /**
     * Decreases by one every time a hardlink pointing to the object is removed
     */
    void decreaseReferenceCount();
}
