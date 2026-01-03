package ch.supsi.fscli.backend.service.commands;

import ch.supsi.fscli.backend.exceptions.FileSystemException;
import ch.supsi.fscli.backend.exceptions.TooManyArgumentsException;
import ch.supsi.fscli.backend.service.CreateFilesystemService;
import ch.supsi.fscli.backend.service.model.DirectoryINode;
import ch.supsi.fscli.backend.service.model.Filesystem;
import ch.supsi.fscli.backend.service.model.INode;

import java.util.List;
import java.util.Map;

public class PwdService extends AbstractCommand {
    private final CreateFilesystemService createFilesystemService;

    public PwdService(String description, CreateFilesystemService createFilesystemService) {
        super("pwd", description);
        this.createFilesystemService = createFilesystemService;
    }


    /**
     * Return the current path of the {@link Filesystem}
     * @param operands
     * @param flags
     * @return the path as a string
     * @throws TooManyArgumentsException
     */
    @Override
    public String execute(List<String> operands, List<String> flags) throws FileSystemException {
        if(!flags.isEmpty() || !operands.isEmpty())
            throw new TooManyArgumentsException();

        Filesystem filesystem = createFilesystemService.getFilesystem();
        DirectoryINode parent = filesystem.getCurrentDir();
        DirectoryINode root = filesystem.getRoot();
        if(parent == root){
            return "/";
        }else{
            StringBuilder result = new StringBuilder("/");
            while(parent != null){
                String name = getParentFolderName(parent);
                if(name != null && !name.isEmpty()){
                    result.insert(0, name);
                    result.insert(0, "/");
                }
                parent = parent.getParent();
            }
            return result.toString();
        }
    }

    private String getParentFolderName(DirectoryINode directory){
        DirectoryINode parent = directory.getParent();
        if(parent == null)
            return "";
        for (Map.Entry<String, INode> entry : parent.getChildren().entrySet()) {
            if (entry.getValue() == directory) {
                return entry.getKey();
            }
        }
        return null;
    }
}
