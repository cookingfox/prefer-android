package com.cookingfox.android.prefer_rx.api.pref;

import com.cookingfox.android.prefer.api.pref.Prefs;

/**
 * Wraps a Pref group. Extend this interface so you can provide access to the separate Pref objects
 * and the group they are contained in.
 *
 * @param <K> References the concrete enum key class.
 */
public interface RxPrefs<K extends Enum<K>> extends Prefs<K> {

    /**
     * Returns Pref group that is associated with {@link K}.
     *
     * @return The Pref group.
     */
    RxPrefGroup<K> getPrefGroup();

}
