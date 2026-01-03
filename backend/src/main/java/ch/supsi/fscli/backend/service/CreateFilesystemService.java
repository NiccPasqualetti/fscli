package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.service.model.Filesystem;

public final class CreateFilesystemService {
    private Filesystem filesystem;
    /**
     * Creates and initializes a new {@link Filesystem} instance.
     * <p>
     * This method constructs a fresh in-memory filesystem identified by the
     * provided name. The resulting instance is fully initialized and ready for
     * use by higher-level controllers or GUI components.
     *
     * @param name the human-readable name used to identify this filesystem instance
     * @return a newly constructed and initialized {@link Filesystem}
     * @see Filesystem#Filesystem(String)
     */
    public Filesystem createFilesystem(String name) {
        filesystem = new Filesystem(name);
        return filesystem;
    }

    public Filesystem getFilesystem() {
        return filesystem;
    }

    public void setFilesystem(Filesystem filesystem) {
        this.filesystem = filesystem;
    }
}
