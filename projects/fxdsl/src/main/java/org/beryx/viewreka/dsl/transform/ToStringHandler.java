package org.beryx.viewreka.dsl.transform;


/**
 * This is a special handler interface, which does not honor the standard semantic of an {@link AliasHandler}.
 * The values of map entries associated with this handler will be converted to String instead of being replaced with a corresponding aliasClass
 */
public interface ToStringHandler extends AliasHandler<Void> {
	// No additional methods
}
