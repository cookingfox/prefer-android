package com.cookingfox.android.prefer.impl.prefer;

import com.cookingfox.android.prefer.api.exception.GroupAlreadyAddedException;
import com.cookingfox.android.prefer.api.pref.PrefListener;
import com.cookingfox.android.prefer.api.pref.typed.BooleanPref;
import com.cookingfox.android.prefer.impl.pref.AndroidPrefGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import fixtures.FixtureSharedPreferences;
import fixtures.example.Key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link SharedPreferencesPrefer}.
 */
public class SharedPreferencesPreferTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private SharedPreferencesPrefer prefer;

    @Before
    public void setUp() throws Exception {
        prefer = new SharedPreferencesPrefer(new FixtureSharedPreferences());

        // add `OnSharedPreferenceChangeListener`
        prefer.initializePrefer();
    }

    @After
    public void tearDown() throws Exception {
        // remove `OnSharedPreferenceChangeListener`
        prefer.disposePrefer();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addListener_should_throw_if_null_pref() throws Exception {
        // noinspection unchecked
        prefer.addListener(null, new PrefListener<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignored
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void addListener_should_throw_if_null_listener() throws Exception {
        prefer.addListener(createBooleanPref(true), null);
    }

    @Test
    public void addListener_should_call_listener_on_value_changed() throws Exception {
        BooleanPref<Key> booleanPref = createBooleanPref(false);
        final AtomicBoolean called = new AtomicBoolean(false);

        prefer.addListener(booleanPref, new PrefListener<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                called.set(true);
            }
        });

        booleanPref.setValue(true);

        assertTrue(called.get());
        assertTrue(booleanPref.getValue());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeListener_should_throw_if_null_prefer() throws Exception {
        // noinspection unchecked
        prefer.removeListener(null, new PrefListener<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignored
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void removeListener_should_throw_if_null_listener() throws Exception {
        prefer.removeListener(createBooleanPref(true), null);
    }

    @Test
    public void removeListener_should_not_call_listener_on_value_changed() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        BooleanPref<Key> booleanPref = createBooleanPref(false);
        PrefListener<Boolean> listener = new PrefListener<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                called.set(true);
            }
        };

        prefer.addListener(booleanPref, listener);
        prefer.removeListener(booleanPref, listener);

        booleanPref.setValue(true);

        assertFalse(called.get());
        assertTrue(booleanPref.getValue());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getBoolean & putBoolean
    //----------------------------------------------------------------------------------------------

    @Test
    public void getBoolean_should_return_stored_value() throws Exception {
        boolean stored = false;

        prefer.putBoolean(Key.IsEnabled, stored);

        boolean result = prefer.getBoolean(Key.IsEnabled, true);

        assertEquals(stored, result);
    }

    @Test
    public void getBoolean_should_return_default_value_for_non_stored() throws Exception {
        boolean defaultValue = true;

        boolean result = prefer.getBoolean(Key.IsEnabled, defaultValue);

        assertEquals(defaultValue, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getInteger & putInteger
    //----------------------------------------------------------------------------------------------

    @Test
    public void getInteger_should_return_stored_value() throws Exception {
        int stored = 1;

        prefer.putInteger(Key.IsEnabled, stored);

        int result = prefer.getInteger(Key.IsEnabled, 2);

        assertEquals(stored, result);
    }

    @Test
    public void getInteger_should_return_default_value_for_non_stored() throws Exception {
        int defaultValue = 1;

        int result = prefer.getInteger(Key.IsEnabled, defaultValue);

        assertEquals(defaultValue, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getString & putString
    //----------------------------------------------------------------------------------------------

    @Test
    public void getString_should_return_stored_value() throws Exception {
        String stored = "foo";

        prefer.putString(Key.IsEnabled, stored);

        String result = prefer.getString(Key.IsEnabled, "bar");

        assertEquals(stored, result);
    }

    @Test
    public void getString_should_return_default_value_for_non_stored() throws Exception {
        String defaultValue = "foo";

        String result = prefer.getString(Key.IsEnabled, defaultValue);

        assertEquals(defaultValue, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addGroup
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addGroup_should_throw_if_group_null() throws Exception {
        prefer.addGroup(null);
    }

    @Test
    public void addGroup_should_create_and_add_new_pref_group() throws Exception {
        assertTrue(prefer.groups.size() == 0);

        AndroidPrefGroup<Key> group = new AndroidPrefGroup<>(Key.class);
        prefer.addGroup(group);

        assertTrue(prefer.groups.size() == 1);
        assertTrue(prefer.groups.contains(group));
    }

    @Test(expected = GroupAlreadyAddedException.class)
    public void addGroup_should_throw_if_already_contains_key_class() throws Exception {
        AndroidPrefGroup<Key> group = new AndroidPrefGroup<>(Key.class);
        prefer.addGroup(group);
        prefer.addGroup(group);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addNewGroup
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addNewGroup_should_throw_if_enum_class_null() throws Exception {
        prefer.addNewGroup(null);
    }

    @Test
    public void addNewGroup_should_create_and_add_new_pref_group() throws Exception {
        assertTrue(prefer.groups.size() == 0);

        AndroidPrefGroup<Key> group = prefer.addNewGroup(Key.class);

        assertTrue(prefer.groups.size() == 1);
        assertTrue(prefer.groups.contains(group));
    }

    @Test(expected = GroupAlreadyAddedException.class)
    public void addNewGroup_should_throw_if_already_contains_key_class() throws Exception {
        prefer.addNewGroup(Key.class);
        prefer.addNewGroup(Key.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newBoolean
    //----------------------------------------------------------------------------------------------

    @Test
    public void newBoolean_should_create_new_boolean_pref() throws Exception {
//        prefer.newBoolean(null,null);
    }

    //----------------------------------------------------------------------------------------------
    // HELPER METHODS
    //----------------------------------------------------------------------------------------------

    private BooleanPref<Key> createBooleanPref(boolean defaultValue) {
        return prefer.newBoolean(Key.IsEnabled, defaultValue);
    }

}
