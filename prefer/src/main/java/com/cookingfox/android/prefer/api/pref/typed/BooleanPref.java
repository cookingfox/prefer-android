package com.cookingfox.android.prefer.api.pref.typed;

import com.cookingfox.android.prefer.api.pref.Pref;

/**
 * Created by abeldebeer on 10/05/16.
 */
public interface BooleanPref<K extends Enum<K>> extends Pref<K, Boolean> {
}
