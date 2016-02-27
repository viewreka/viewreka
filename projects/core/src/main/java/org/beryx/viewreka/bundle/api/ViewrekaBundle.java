package org.beryx.viewreka.bundle.api;

import org.beryx.viewreka.core.Version;

import java.util.List;

public interface ViewrekaBundle<GUI> {
    List<String> getCategories();
    String getId();
    String getName();
    Version getVersion();
    String getDescription();
    List<CodeTemplate> getTemplates();
    void addTo(GUI gui);
    void removeFrom(GUI gui);

    default CodeTemplate getTemplate(String name) {
        return getTemplates().stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
    }
}
