package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.CommandResult;
import ch.supsi.fscli.backend.controller.CreateFilesystemController;
import ch.supsi.fscli.backend.controller.CommandController;
import ch.supsi.fscli.backend.controller.LanguageController;
import ch.supsi.fscli.backend.controller.SerialController;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class FilesystemModel extends AbstractModel {
    private final SerialController serialController;
    private final CreateFilesystemController createFilesystemController;
    private final CommandController commandController;
    private final LanguageController languageController;

    private String tempCommandOutput;
    private String feedback;
    private boolean needToClearOutput;

    public FilesystemModel(SerialController serialController,
                           CreateFilesystemController createFilesystemController,
                           CommandController commandController,
                           LanguageController languageController) {
        this.serialController = serialController;
        this.createFilesystemController = createFilesystemController;
        this.commandController = commandController;
        this.languageController = languageController;
        feedback = tempCommandOutput = "";
        needToClearOutput = false;
    }

    public String getFeedback(){
        String t = feedback;
        feedback = "";
        return t;
    }

    public String getTempCommandOutput(){
        String t = tempCommandOutput;
        tempCommandOutput = "";
        return t;
    }

    public boolean needToClearOutput(){
        if(needToClearOutput){
            needToClearOutput = false;
            return true;
        }
        return false;
    }


    public void save() {
        try{
            this.serialController.save();
            feedback = languageController.translate("savedFs");
        }catch(IOException e){
            feedback = languageController.translate("savedFsError");
        }
    }

    public void saveAs(File file) {
        try{
            this.serialController.saveAs(file);
            feedback = languageController.translate("savedFs");
        } catch (IOException e) {
            feedback = languageController.translate("savedFsError");
        }
    }

    public void open(File file) {
        try{
            this.serialController.open(file);
            feedback = languageController.translate("openedFs");
        }catch(IOException e){
            feedback = languageController.translate("openedFsError");
        }
    }

    public void createFileSystem() {
        this.createFilesystemController.createFilesystem("FS1");
        feedback = languageController.translate("createdNewFs");
    }


    public void executeCommand(String input) {
        StringTokenizer st = new StringTokenizer(input);
        if(!st.hasMoreTokens()){
            feedback = languageController.translate("command.invalid");
            return;
        }

        String command = st.nextToken();
        String[] args = new  String[st.countTokens()];
        for(int i = 0; i < args.length; i++)
            args[i] = st.nextToken();

        feedback = languageController.translate("command.executed") + ": " + command;

        if(command.equals("clear")){
            needToClearOutput = true;
        }else{
            CommandResult result = commandController.parseSafe(command, args);
            if (result.isSuccess()) {
                tempCommandOutput = result.getOutput();
            } else if (result.getOutput() != null) {
                feedback = languageController.translate(result.getOutput());
            } else if (result.getFeedbackMessageKey() != null) {
                feedback = languageController.translate(result.getFeedbackMessageKey());
            } else {
                feedback = languageController.translate("unknownError");
            }
        }
    }


    public boolean isFilesystemInitialized(){
        return this.createFilesystemController.isFilesystemInitialized();
    }

    public boolean isAlreadySaved() {
        return serialController.isAlreadySaved();
    }
}
