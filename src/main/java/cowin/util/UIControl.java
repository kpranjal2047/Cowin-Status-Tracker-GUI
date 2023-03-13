package cowin.util;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/** Utility class providing methods for controlling UI elements */
@UtilityClass
public class UIControl {

  /**
   * Utility method to disable nodes
   *
   * @param nodes Nodes to disable
   */
  public void disableNodes(final Node @NonNull ... nodes) {
    for (final Node node : nodes) {
      if (node.getClass().equals(MFXTextField.class)) {
        ((MFXTextField) node).setText("");
      } else if (node.getClass().equals(Label.class)) {
        ((Label) node).setText(null);
      }
      node.setDisable(true);
    }
  }

  /**
   * Utility method to enable nodes
   *
   * @param nodes Nodes to enable
   */
  public void enableNodes(final Node @NonNull ... nodes) {
    for (final Node node : nodes) {
      node.setDisable(false);
    }
  }
}
