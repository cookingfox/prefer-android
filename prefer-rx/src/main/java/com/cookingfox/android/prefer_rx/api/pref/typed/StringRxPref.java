package com.cookingfox.android.prefer_rx.api.pref.typed;

import com.cookingfox.android.prefer.api.pref.Pref;
import com.cookingfox.android.prefer_rx.api.pref.RxPref;

/**
 * Rx observable {@link Pref} implementation with a String value.
 *
 * @param <K> References the enum class for this Pref's key.
 */
public interface StringRxPref<K extends Enum<K>> extends RxPref<K, String> {
}
