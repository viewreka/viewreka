/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Code adapted from http://stackoverflow.com/questions/29300551/javafx-add-checkboxtreeitem-in-treetable

package org.beryx.viewreka.fxapp;

import com.sun.javafx.scene.control.skin.TreeTableRowSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Support custom graphic for Tree/TableRow. Here in particular a checkBox.
 * http://stackoverflow.com/q/29300551/203657
 * <p>
 * Basic idea: implement custom TreeTableRow that set's its graphic to the
 * graphic/checkBox. Doesn't work: layout is broken, graphic appears
 * over the text. All fine if we set the graphic to the TreeItem that's
 * shown. Possible as long as the treeItem doesn't have a graphic of
 * its own.
 * <p>
 * Basic problem:
 * <li> TableRowSkinBase seems to be able to cope: has protected method
 *   graphicsProperty that should be implemented to return the graphic
 *   if any. That graphic is added to the children list and sized/located
 *   in layoutChildren.
 * <li> are added the graphic/disclosureNode as needed before
 *   calling super.layoutChildren,
 * <li> graphic/disclosure are placed inside the leftPadding of the tableCell
 *   that is the treeColumn
 * <li> TreeTableCellSkin must cooperate in taking into account the graphic/disclosure
 *   when calculating its leftPadding
 * <li> cellSkin is hard-coded to use the TreeItem's graphic (vs. the rowCell's)
 *
 * PENDING JW:
 * <li>- would expect to not alter the scenegraph during layout (might lead to
 *   endless loops or not) but done frequently in core code
 * <p>
 *
 * Outline of the solution as implemented:
 * <li> need a TreeTableCell with a custom skin
 * <li> override leftPadding in skin to add row graphic if available
 * <li> need CheckBoxTreeTableRow that sets its graphic to checkBox (or a combination
 *   of checkBox and treeItem's)
 * <li> need custom rowSkin that implements graphicProperty to return the row graphic
 *
 * @author Jeanette Winzenburg, Berlin
 *
 * @see BundleInfoNameTreeTableCell
 * @see BundleInfoNameTreeTableCell.DefaultTreeTableCellSkin
 *
 */
public class CheckBoxTreeTableRow<T> extends TreeTableRow<T> {

    private CheckBox checkBox;

    private ObservableValue<Boolean> booleanProperty;

    private BooleanProperty indeterminateProperty;
    private BooleanProperty disableProperty;

    public CheckBoxTreeTableRow() {
        this(item -> {
            if (item instanceof CheckBoxTreeItem<?>) {
                return ((CheckBoxTreeItem<?>)item).selectedProperty();
            }
            return null;
        });
    }

    public CheckBoxTreeTableRow(
            final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedProperty) {
        this.getStyleClass().add("check-box-tree-cell");
        setSelectedStateCallback(getSelectedProperty);
        checkBox = new CheckBox();
        checkBox.setAlignment(Pos.TOP_LEFT);
    }

    // --- selected state callback property
    private ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>>
            selectedStateCallback = new SimpleObjectProperty<>(this, "selectedStateCallback");

    /**
     * Property representing the {@link Callback} that is bound to by the
     * CheckBox shown on screen.
     */
    public final ObjectProperty<Callback<TreeItem<T>, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
        return selectedStateCallback;
    }

    /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<TreeItem<T>, ObservableValue<Boolean>> value) {
        selectedStateCallbackProperty().set(value);
    }

    /**
     * Returns the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final Callback<TreeItem<T>, ObservableValue<Boolean>> getSelectedStateCallback() {
        return selectedStateCallbackProperty().get();
    }

    /** {@inheritDoc} */
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            TreeItem<T> treeItem = getTreeItem();
            if (treeItem instanceof CheckBoxTreeItem) {

                checkBox.setGraphic(treeItem == null ? null : treeItem.getGraphic());
                setGraphic(checkBox);
                // uninstall bindings
                if (booleanProperty != null) {
                    checkBox.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
                }
                if (disableProperty != null) {
                    checkBox.disableProperty().unbindBidirectional(disableProperty);
                }
                if (indeterminateProperty != null) {
                    checkBox.indeterminateProperty().unbindBidirectional(indeterminateProperty);
                }
                // install new bindings.
                CheckBoxTreeItem<T> cbti = (CheckBoxTreeItem<T>) treeItem;
                booleanProperty = cbti.selectedProperty();
                checkBox.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);

                indeterminateProperty = cbti.indeterminateProperty();
                checkBox.indeterminateProperty().bindBidirectional(indeterminateProperty);

                disableProperty = getDisableProperty(cbti);
                if(disableProperty != null) {
                    checkBox.disableProperty().bindBidirectional(disableProperty);
                }
            }
        }
    }

    /**
     * Retrieves the disableProperty of a {@link CheckBoxTreeItem}. This default implementation, which always returns null, can be overridden by subclasses.
     * @param cbti the CheckBoxTreeItem whose disableProperty should be retrieved
     * @return the disableProperty associated with {@code cbti}
     */
    protected BooleanProperty getDisableProperty(CheckBoxTreeItem<T> cbti) {
        return null;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CheckBoxTreeTableRowSkin<>(this);
    }

    public static class CheckBoxTreeTableRowSkin<S> extends TreeTableRowSkin<S> {
        protected ObjectProperty<Node> checkGraphic;

        /**
         * @param control
         */
        public CheckBoxTreeTableRowSkin(TreeTableRow<S> control) {
            super(control);
        }

        /**
         * Note: this is implicitly called from the constructor of LabeledSkinBase.
         * At that time, checkGraphic is not yet instantiated. So we do it here,
         * still having to create it at least twice. That'll be a problem if
         * anybody would listen to changes ...
         */
        @Override
        protected ObjectProperty<Node> graphicProperty() {
            if (checkGraphic == null) {
                checkGraphic = new SimpleObjectProperty<Node>(this, "checkGraphic");
            }
            CheckBoxTreeTableRow<S> treeTableRow = getTableRow();
            if (treeTableRow.getTreeItem() == null) {
                checkGraphic.set(null);
            } else {
                checkGraphic.set(treeTableRow.getGraphic());
            }
            return checkGraphic;
        }

        protected CheckBoxTreeTableRow<S> getTableRow() {
            return (CheckBoxTreeTableRow<S>) super.getSkinnable();
        }
    }
}
