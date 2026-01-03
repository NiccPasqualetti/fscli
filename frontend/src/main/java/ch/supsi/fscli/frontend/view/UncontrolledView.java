package ch.supsi.fscli.frontend.view;
import ch.supsi.fscli.frontend.model.AbstractModel;

public interface UncontrolledView extends DataView{
    void initialize(AbstractModel model);
}
