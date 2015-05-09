package org.beryx.viewreka.fxui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class FxUtil {
	private static final Logger log = LoggerFactory.getLogger(FxUtil.class);

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

	public static boolean addStyle(final String baseStyleName, final Node fxNode) {
		return applyStyle(baseStyleName, true, fxNode);
	}

	public static boolean removeStyle(final String baseStyleName, final Node fxNode) {
		return applyStyle(baseStyleName, false, fxNode);
	}

}
