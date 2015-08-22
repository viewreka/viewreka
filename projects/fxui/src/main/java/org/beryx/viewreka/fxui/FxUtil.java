package org.beryx.viewreka.fxui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * A class providing static JavaFX utility methods.
 */
public class FxUtil {
    private static final Logger log = LoggerFactory.getLogger(FxUtil.class);

    /**
     * Applies one of the styles from a set of mutually exclusive styles and removes the remaining ones.
     * @param baseStyleName the base name used as prefix for all styles in the set
     * @param suffixes the set of suffixes used to generate the names of the mutually exclusive styles by appending them to the {@code baseStyleName}
     * @param appliedSuffix the suffix of the style to be applied. (The styles corresponding to the remaining suffixes will be removed.)
     * @param fxNode the FX node to which the specified style has to be applied (and the remaining ones removed)
     */
    synchronized public static void applyStyle(final String baseStyleName, final Iterable<String> suffixes, final String appliedSuffix, final Node fxNode) {
        if(fxNode == null) return;
        final ObservableList<String> styleClass = fxNode.getStyleClass();
        final String appliedStyle = baseStyleName + "-" + appliedSuffix;
        for(final String suffix : suffixes) {
            if(suffix.equals(appliedSuffix)) continue;
            final String removedStyle = baseStyleName + "-" + suffix;
            styleClass.remove(removedStyle);
        }
        if(!styleClass.contains(appliedStyle)) {
            styleClass.add(appliedStyle);
        }
        log.debug("Resulting style for {}: {}", fxNode.getId(), styleClass);

    }

    /**
     * Applies one of the styles in a pair of complementary styles, while removing the other one.
     * The names of complementary styles are obtained by appending the suffixes {@code "-on"} and {@code "-off"} to a base style name.
     * @param baseStyleName the base style name, to which the suffixes {@code "-on"} and {@code "-off"} are appended in order to obtain the pair of complementary styles
     * @param enabled true, if the style with the suffix {@code "-on"} should be applied; false for the {@code "-off"} suffix.
     * @param fxNode the FX node to which the specified style has to be applied (and the complementary one removed)
     * @return true, if the desired suffix has been successfully applied
     */
    synchronized public static boolean applyStyle(final String baseStyleName, final boolean enabled, final Node fxNode) {
        boolean applied = false;
        final String styleNameAdd = baseStyleName + (enabled ? "-on" : "-off");
        final String styleNameRemove = baseStyleName + (enabled ? "-off" : "-on");
        if(fxNode != null) {
            final ObservableList<String> styleClass = fxNode.getStyleClass();
            final boolean removed = styleClass.remove(styleNameRemove);
            final boolean added = !fxNode.getStyleClass().contains(styleNameAdd) && styleClass.add(styleNameAdd);
            applied = enabled ? added : removed;
        }
        return applied;
    }

    /**
     * A convenience method that calls {@link #applyStyle(String, boolean, Node)} with {@code enabled = true}.
     * This means that the style with the {@code "-on"} suffix will be applied and the style with the {@code "-off"} suffix removed.
     * @param baseStyleName the base style name, to which the suffixes {@code "-on"} and {@code "-off"} are appended in order to obtain the pair of complementary styles
     * @param fxNode the FX node to which the specified style has to be applied (and the complementary one removed)
     * @return true, if the style with the {@code "-on"} suffix has been successfully applied and the style with the {@code "-off"} suffix removed
     */
    public static boolean addStyle(final String baseStyleName, final Node fxNode) {
        return applyStyle(baseStyleName, true, fxNode);
    }

    /**
     * A convenience method that calls {@link #applyStyle(String, boolean, Node)} with {@code enabled = false}.
     * This means that the style with the {@code "-off"} suffix will be applied and the style with the {@code "-on"} suffix removed.
     * @param baseStyleName the base style name, to which the suffixes {@code "-on"} and {@code "-off"} are appended in order to obtain the pair of complementary styles
     * @param fxNode the FX node to which the specified style has to be applied (and the complementary one removed)
     * @return true, if the style with the {@code "-off"} suffix has been successfully applied and the style with the {@code "-on"} suffix removed
     */
    public static boolean removeStyle(final String baseStyleName, final Node fxNode) {
        return applyStyle(baseStyleName, false, fxNode);
    }

}
