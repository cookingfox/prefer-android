package com.cookingfox.android.prefer.impl.pref.typed;

import android.preference.Preference;

import com.cookingfox.android.prefer.api.exception.InvalidPrefValueException;
import com.cookingfox.android.prefer.api.prefer.Prefer;
import com.cookingfox.android.prefer.impl.pref.AbstractAndroidPref;
import com.cookingfox.android.prefer.impl.pref.PreferenceModifier;
import com.cookingfox.android.prefer.impl.prefer.SharedPreferencesPrefer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import fixtures.FixtureSharedPreferences;
import fixtures.example.Key;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AndroidStringPref} and {@link AbstractAndroidPref}.
 */
public class AndroidStringPrefTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private Prefer prefer;
    private AndroidStringPref<Key> pref;

    @Before
    public void setUp() throws Exception {
        prefer = new SharedPreferencesPrefer(new FixtureSharedPreferences());

        // add `OnSharedPreferenceChangeListener`
        prefer.initializePrefer();

        pref = new AndroidStringPref<>(prefer, Key.Username, "");
    }

    @After
    public void tearDown() throws Exception {
        // remove `OnSharedPreferenceChangeListener`
        prefer.disposePrefer();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_prefer_null() throws Exception {
        new AndroidStringPref<>(null, Key.IsEnabled, "");
    }

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_key_null() throws Exception {
        new AndroidStringPref<Key>(prefer, null, "");
    }

    @Test(expected = InvalidPrefValueException.class)
    public void constructor_should_throw_if_default_value_null() throws Exception {
        new AndroidStringPref<>(prefer, Key.IsEnabled, null);
    }

    @Test
    public void constructor_should_validate_default_value() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        new AndroidStringPref<Key>(prefer, Key.IsEnabled, "") {
            @Override
            public boolean validate(String value) throws Exception {
                called.set(true);

                return super.validate(value);
            }
        };

        assertTrue(called.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addListener_should_throw_if_listener_null() throws Exception {
        pref.addListener(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: setPreferenceModifier
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void setPreferenceModifier_should_throw_if_modifier_null() throws Exception {
        pref.setPreferenceModifier(null);
    }

    @Test
    public void setPreferenceModifier_should_set_the_modifier() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        pref.setPreferenceModifier(new PreferenceModifier() {
            @Override
            public Preference modifyPreference(Preference generated) {
                called.set(true);
                return generated;
            }
        });

        pref.modifyPreference(null);

        assertTrue(called.get());
    }

}
