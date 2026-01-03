package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.Filesystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateFilesystemControllerTest {

    private CreateFilesystemService service;
    private CreateFilesystemController controller;

    @BeforeEach
    void setUp() {
        service = new CreateFilesystemService();
        controller = new CreateFilesystemController(service);
    }

    @Test
    void testIsFilesystemInitializedInitiallyFalse() {
        assertFalse(controller.isFilesystemInitialized());
        assertNull(service.getFilesystem());
    }

    @Test
    void testCreateFilesystemReturnsInstanceAndInitializesService() {
        Filesystem fs = controller.createFilesystem("myFS");

        assertNotNull(fs);
        assertSame(service.getFilesystem(), fs);
        assertTrue(controller.isFilesystemInitialized());
    }

    @Test
    void testSubsequentCreatesReplacePreviousFilesystem() {
        Filesystem first = controller.createFilesystem("first");
        Filesystem second = controller.createFilesystem("second");

        assertNotNull(first);
        assertNotNull(second);
        assertSame(service.getFilesystem(), second);
        assertNotSame(first, second);
    }
}
