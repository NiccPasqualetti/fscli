package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.model.AbstractModel;
import ch.supsi.fscli.frontend.controller.EventHandler;

public interface ControlledView extends DataView{
    void initialize(EventHandler eventHandler, AbstractModel model);

}
