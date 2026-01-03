package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.CreateFilesystemService;

public final class CreateFilesystemController {

    private CreateFilesystemService createFilesystemService;

    public CreateFilesystemController(CreateFilesystemService createFileSystemService) {
        this.createFilesystemService = createFileSystemService;
    }
    /**
     * Delegates the creation of a new {@link Filesystem} to the underlying
     * {@link CreateFilesystemService}.
     * <p>
     * This method serves as the interface between the GUI layer and the service
     * layer, constructing a filesystem identified by the given name and returning
     * the initialized instance to the caller.
     *
     * @param name the human-readable name used to identify the newly created filesystem
     * @return the fully initialized {@link Filesystem} instance produced by the service
     * @see CreateFilesystemService#createFilesystem(String)
     * @see Filesystem#Filesystem(String)
     */
    public Filesystem createFilesystem(String name) {
       return createFilesystemService.createFilesystem(name);
    }

    public boolean isFilesystemInitialized(){
        return createFilesystemService.getFilesystem() != null;
    }
}
