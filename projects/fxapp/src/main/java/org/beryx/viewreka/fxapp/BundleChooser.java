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
package org.beryx.viewreka.fxapp;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.beryx.viewreka.bundle.repo.CatalogRepo;
import org.beryx.viewreka.bundle.repo.BundleInfo;
import org.beryx.viewreka.core.Version;
import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.settings.FxPropsAwareWindow;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * The dialog used to choose Viewreka bundles.
 */
public class BundleChooser extends BorderPane implements FXMLNode, FxPropsAwareWindow {
    private static final Logger log = LoggerFactory.getLogger(BundleChooser.class);

    public static final String PROP_CATALOG_URLS = "repo.catalog.urls";
    public static final String DEFAULT_CATALOG_URL  = "http://viewreka-bundles.beryx.org/json";

    @FXML TreeTableView<BundleInfo> ttvBundles;
    @FXML TreeTableColumn<BundleInfo, BundleInfo> ttColName;
    @FXML TreeTableColumn<BundleInfo, String> ttColVersion;
    @FXML TreeTableColumn<BundleInfo, String> ttColId;
    @FXML TreeTableColumn<BundleInfo, String> ttColDescription;

    private final SettingsManager<GuiSettings> guiSettingsManager;
    private final List<BundleInfo> initialInfoEntries;
    private final List<Pair<String, Version>> existingBundles;

    private final Set<String> catalogUrls = new LinkedHashSet<>();

    private final Map<Pair<String,Version>, BundleInfo> selectedBundlesMap = new LinkedHashMap<>();

    private static class NullBundleInfo implements BundleInfo {
        private final String category;

        NullBundleInfo(String category) {
            this.category = category;
        }
        @Override public String getName() { return category; }
        @Override public List<String> getCategories() { return Arrays.asList(category); }
        @Override public String getBundleClass() { return null; }
        @Override public int getViewrekaVersionMajor() { return 0; }
        @Override public int getViewrekaVersionMinor() { return 0; }
        @Override public int getViewrekaVersionPatch() { return 0; }
        @Override public String getId() { return null; }
        @Override public Version getVersion() { return new Version(0, 0, 0, null, false); }
        @Override public String getDescription() { return null; }
        @Override public String getUrl() { return null; }
        @Override public String getHomePage() { return null; }
    }

    private static class BundleInfoTreeItem extends CheckBoxTreeItem<BundleInfo> {
        private final BooleanProperty disabled = new SimpleBooleanProperty(this, "disabled", false) {
            @Override protected void invalidated() {
                super.invalidated();
                Event evt = new CheckBoxTreeItem.TreeModificationEvent<BundleInfo>(checkBoxSelectionChangedEvent(), BundleInfoTreeItem.this, true);
                Event.fireEvent(BundleInfoTreeItem.this, evt);
            }
        };
        public final void setDisabled(boolean value) { disabledProperty().setValue(value); }
        public final boolean isDisabled() { return disabledProperty().getValue(); }
        public final BooleanProperty disabledProperty() { return disabled; }

        BundleInfoTreeItem(BundleInfo bundleInfo, boolean existing) {
            super(bundleInfo);
            setIndeterminate(existing);
            setDisabled(existing);
        }
    }

    private class BundleInfoTreeTableRow extends CheckBoxTreeTableRow<BundleInfo> {
        @Override
        protected BooleanProperty getDisableProperty(CheckBoxTreeItem<BundleInfo> cbti) {
            return ((BundleInfoTreeItem)cbti).disabledProperty();
        }
    }

    public static BundleChooser createWith(SettingsManager<GuiSettings> guiSettingsManager, List<BundleInfo> initialInfoEntries, List<Pair<String, Version>> existingBundles) {
        return new BundleChooser(guiSettingsManager, initialInfoEntries, existingBundles).load();
    }

    private BundleChooser(SettingsManager<GuiSettings> guiSettingsManager, List<BundleInfo> initialInfoEntries, List<Pair<String, Version>> existingBundles) {
        this.guiSettingsManager = guiSettingsManager;
        this.initialInfoEntries = initialInfoEntries;
        this.existingBundles = existingBundles;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("ttvBundles", ttvBundles);
        check("ttColName", ttColName);
        check("ttColVersion", ttColVersion);
        check("ttColId", ttColId);
        check("ttColDescription", ttColDescription);

        ttvBundles.getColumns().forEach(col -> col.impl_setReorderable(false));

        GuiSettings settings = guiSettingsManager.getSettings();
        String[] urlArray = settings.getProperty(PROP_CATALOG_URLS, new String[]{DEFAULT_CATALOG_URL}, false);
        catalogUrls.addAll(Arrays.asList(urlArray));
        settings.setProperty(PROP_CATALOG_URLS, urlArray);

        Map<String, Map<Pair<String, Version>, BundleInfo>> categoryMap = new TreeMap<>();
        catalogUrls.forEach(url -> {
            CatalogRepo repo = new CatalogRepo(url);
            List<BundleInfo> infoEntries = null;
            try {
                infoEntries = repo.getEntries();
            } catch (Exception e) {
                Dialogs.error("Cannot read from repository " + repo.getCatalogUrl(), e.getMessage(), e);
            }
            infoEntries.forEach(entry ->
                entry.getCategories().forEach(category -> {
                    Map<Pair<String, Version>, BundleInfo> idMap = categoryMap.get(category);
                    if(idMap == null) {
                        idMap = new TreeMap<>();
                        categoryMap.put(category, idMap);
                    }
                    String bundleId = entry.getId();
                    Version version = entry.getVersion();
                    Pair<String, Version> infoPair = new ImmutablePair<>(bundleId, version);
                    BundleInfo oldEntry = idMap.get(infoPair);
                    if(oldEntry == null) {
                        idMap.put(infoPair, entry);
                    } else if(entry.getUrl().equals(oldEntry.getUrl())) {
                        log.warn("Different URLs for " + entry.getName() + " " + version + ": " + entry.getUrl() + " and " + oldEntry.getUrl());
                    }
                })
            );
        });

        ttvBundles.setRowFactory(item -> new BundleInfoTreeTableRow());
        ttColName.setCellFactory(p -> new BundleInfoNameTreeTableCell<>());

        ttColName.setCellValueFactory(param -> new ReadOnlyObjectWrapper<BundleInfo>(param.getValue().getValue()));
        ttColVersion.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getVersion().toString()));
        ttColId.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getId()));
        ttColDescription.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getDescription()));

        TreeItem<BundleInfo> root = new TreeItem<>(new NullBundleInfo("Bundles"));
        root.setExpanded(true);
        ttvBundles.setRoot(root);
        ttvBundles.setShowRoot(false);
        categoryMap.entrySet().forEach(categoryEntry -> {
            String category = categoryEntry.getKey();
            TreeItem<BundleInfo> categoryItem = new TreeItem<>(new NullBundleInfo(category));
            categoryItem.setExpanded(true);
            root.getChildren().add(categoryItem);

            List<BundleInfo> bInfo = new ArrayList<BundleInfo>(categoryEntry.getValue().values());
            bInfo.sort((b1, b2) -> {
                int res = b1.getName().compareTo(b2.getName());
                if(res == 0) {
                    res = b1.getVersion().compareTo(b2.getVersion());
                }
                return res;
            });
            bInfo.forEach(bundleInfo -> {
                boolean existing = existingBundles.stream().anyMatch(pair -> bundleInfo.getId().equals(pair.getKey()) && bundleInfo.getVersion().equals(pair.getValue()));
                BundleInfoTreeItem bundleItem = new BundleInfoTreeItem(bundleInfo, existing);

                bundleItem.setIndependent(true);
                Pair<String, Version> infoPair = new ImmutablePair<>(bundleInfo.getId(), bundleInfo.getVersion());
                bundleItem.setSelected(initialInfoEntries.stream().anyMatch(entry -> new ImmutablePair<>(entry.getId(), entry.getVersion()).equals(infoPair)));
                bundleItem.selectedProperty().addListener((obs, oldVal, newVal) -> updateSelection(bundleInfo, newVal));
                categoryItem.getChildren().add(bundleItem);
            });
        });
    }

    private void updateSelection(BundleInfo bundleInfo, Boolean selected) {
        Pair<String, Version> infoPair = new ImmutablePair<>(bundleInfo.getId(), bundleInfo.getVersion());
        ttvBundles.getRoot().getChildren().forEach(categoryItem -> {
            categoryItem.getChildren().forEach(item -> {
                BundleInfo entry = item.getValue();
                if(new ImmutablePair<>(entry.getId(), entry.getVersion()).equals(infoPair)) {
                    ((BundleInfoTreeItem)item).setSelected(selected);
                }
            });
        });
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        FxPropsManager manager = new FxPropsManager(guiSettingsManager.getSettings(), "bundleChooser");
        manager.register(ttColName.prefWidthProperty(), ttColName::getWidth, "ttColName");
        manager.register(ttColVersion.prefWidthProperty(), ttColVersion::getWidth, "ttColVersion");
        manager.register(ttColId.prefWidthProperty(), ttColId::getWidth, "ttColId");
        manager.register(ttColDescription.prefWidthProperty(), ttColDescription::getWidth, "ttColDescription");
        return manager;
    }

    public void chooseBundles() {
        selectedBundlesMap.clear();

        Set<String> selectedOrDisabledIds = new HashSet<>();
        Set<String> duplicateIds = new HashSet<>();
        ttvBundles.getRoot().getChildren().forEach(categoryItem -> {
            categoryItem.getChildren().forEach(it -> {
                BundleInfoTreeItem item = (BundleInfoTreeItem) it;
                if(item.isDisabled()) {
                    selectedOrDisabledIds.add(item.getValue().getId());
                }
            });
        });
        ttvBundles.getRoot().getChildren().forEach(categoryItem -> {
            categoryItem.getChildren().forEach(it -> {
                BundleInfoTreeItem item = (BundleInfoTreeItem) it;
                BundleInfo bundleInfo = item.getValue();
                if(item.isSelected()) {
                    String id = bundleInfo.getId();
                    BundleInfo oldInfo = selectedBundlesMap.put(new ImmutablePair<>(id, bundleInfo.getVersion()), bundleInfo);
                    if(oldInfo == null) {
                        if(selectedOrDisabledIds.contains(id)) {
                            duplicateIds.add(id);
                        } else {
                            selectedOrDisabledIds.add(id);
                        }
                    }
                }
            });
        });
        if(!duplicateIds.isEmpty()) {
            boolean keep = Dialogs.confirmYesNo("Duplicate bundle",
                    "You have selected two or more bundles with the same ID but different versions.\n" +
                    "This may lead to unexpected results.",
                    "Are you sure you want to include all selected bundles?");
            if(!keep) return;
        }
        ((Stage)getScene().getWindow()).close();
    }

    public void cancelBundles() {
        ((Stage)getScene().getWindow()).close();
    }

    public Map<Pair<String,Version>, BundleInfo> getSelectedBundlesMap() {
        return selectedBundlesMap;
    }
}
