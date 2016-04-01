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

import com.sun.javafx.scene.control.skin.TreeTableCellSkin;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.beryx.viewreka.bundle.repo.BundleInfo;

/**
 * TreeTableCell actually showing something. This is copied from TreeTableColumn plus
 * installs DefaultTreeTableCellSkin which handles row graphic width.
 */
public class BundleInfoNameTreeTableCell<S, T> extends TreeTableCell<S, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        if (item == getItem()) return;

        super.updateItem(item, empty);

        if (item == null) {
            super.setText(null);
            super.setGraphic(null);
        } else if (item instanceof Node) {
            super.setText(null);
            super.setGraphic((Node)item);
        } else if (item instanceof BundleInfo) {
            BundleInfo bundleInfo = (BundleInfo)item;
            super.setText(((BundleInfo) item).getName());
            super.setGraphic(null);
            if(bundleInfo.getBundleClass() == null) { // NullBundleInfo
                super.getStyleClass().add("category");
            }
        } else {
            super.setText(item.toString());
            super.setGraphic(null);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DefaultTreeTableCellSkin<>(this);
    }

    /**
     * TreeTableCellSkin that handles row graphic in its leftPadding, if
     * it is in the treeColumn of the associated TreeTableView.
     * <p>
     * It assumes that per-row graphics - including the graphic of the TreeItem, if any -
     * is folded into the TreeTableRow graphic and patches its leftLabelPadding
     * to account for the graphic width.
     * <p>
     *
     * Note: TableRowSkinBase seems to be designed to cope with variations of row
     * graphic - it has a method <code>graphicProperty()</code> that's always used
     * internally when calculating offsets in the treeColumn.
     * Subclasses override as needed, the layout code remains constant. The real
     * problem is the TreeTableCell hard-codes the TreeItem as the only graphic
     * owner.
     *
     */
    public static class DefaultTreeTableCellSkin<S, T> extends TreeTableCellSkin<S, T> {

        /**
         * @param treeTableCell
         */
        public DefaultTreeTableCellSkin(TreeTableCell<S, T> treeTableCell) {
            super(treeTableCell);
        }

        /**
         * Overridden to adjust the padding returned by super for row graphic.
         */
        @Override
        protected double leftLabelPadding() {
            double padding = super.leftLabelPadding();
            padding += getRowGraphicPatch();
            return padding;
        }

        /**
         * Returns the patch for leftPadding if the tableRow has a graphic of
         * its own.<p>
         *
         * Note: this implemenation is a bit whacky as it relies on super's
         * handling of treeItems graphics offset. A cleaner
         * implementation would override leftLabelPadding from scratch.
         * <p>
         * PENDING JW: doooooo it!
         *
         * @return
         */
        protected double getRowGraphicPatch() {
            if (!isTreeColumn()) return 0;
            Node graphic = getSkinnable().getTreeTableRow().getGraphic();
            if (graphic != null) {
                double height = getCellSize();
                // start with row's graphic
                double patch = graphic.prefWidth(height);
                // correct for super's having added treeItem's graphic
                TreeItem<S> item = getSkinnable().getTreeTableRow().getTreeItem();
                if (item.getGraphic() != null) {
                    double correct = item.getGraphic().prefWidth(height);
                    patch -= correct;
                }
                return patch;
            }
            return 0;
        }

        /**
         * Checks and returns whether our cell is attached to a treeTableView/column
         * and actually has a TreeItem.
         * @return
         */
        protected boolean isTreeColumn() {
            if (getSkinnable().isEmpty()) return false;
            TreeTableColumn<S, T> column = getSkinnable().getTableColumn();
            TreeTableView<S> view = getSkinnable().getTreeTableView();
            if (column.equals(view.getTreeColumn())) return true;
            return view.getVisibleLeafColumns().indexOf(column) == 0;
        }

    }
}

