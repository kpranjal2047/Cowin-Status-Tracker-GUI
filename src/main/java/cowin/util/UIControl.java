package cowin.util;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
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

  /**
   * Utility method to clear text from nodes
   *
   * @param nodes Nodes to clear text from
   */
  public void clearTextFromNodes(final Node @NonNull ... nodes) {
    for (final Node node : nodes) {
      if (node instanceof TextInputControl) {
        ((TextInputControl) node).setText("");
      } else if (node instanceof Labeled) {
        ((Labeled) node).setText(null);
      }
    }
  }
}
