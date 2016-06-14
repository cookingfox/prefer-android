package com.cookingfox.android.prefer.impl.prefer;

import com.cookingfox.android.prefer.api.exception.GroupAlreadyAddedException;
import com.cookingfox.android.prefer.api.exception.PreferNotInitializedException;
import com.cookingfox.android.prefer.api.pref.OnValueChanged;
import com.cookingfox.android.prefer.api.pref.PrefGroup;
import com.cookingfox.android.prefer.api.pref.typed.BooleanPref;
import com.cookingfox.android.prefer.impl.pref.AndroidPrefGroup;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidBooleanPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidIntegerPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidStringPref;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import fixtures.FixtureSharedPreferences;
import fixtures.example.Key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link SharedPreferencesPrefer} and {@link AndroidPrefer}.
 */
public class SharedPreferencesPreferTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private SharedPreferencesPrefer prefer;

    @Before
    public void setUp() throws Exception {
        prefer = new SharedPreferencesPrefer(new FixtureSharedPreferences());
        prefer.initializePrefer();
    }

    @After
    public void tearDown() throws Exception {
        prefer.disposePrefer();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_preferences_null() throws Exception {
        new SharedPreferencesPrefer(null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: subscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void subscribe_should_throw_if_null_pref() throws Exception {
        // noinspection unchecked
        prefer.subscribe(null, new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignored
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void subscribe_should_throw_if_null_subscriber() throws Exception {
        prefer.subscribe(createBooleanPref(true), null);
    }

    @Test
    public void subscribe_should_call_subscriber_on_value_changed() throws Exception {
        BooleanPref<Key> booleanPref = createBooleanPref(false);
        final AtomicBoolean called = new AtomicBoolean(false);

        prefer.subscribe(booleanPref, new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                called.set(true);
            }
        });

        booleanPref.setValue(true);

        assertTrue(called.get());
        assertTrue(booleanPref.getValue());
    }

    @Test(expected = PreferNotInitializedException.class)
    public void subscribe_should_throw_if_not_initialized() throws Exception {
        SharedPreferencesPrefer prefer = new SharedPreferencesPrefer(new FixtureSharedPreferences());
        AndroidBooleanPref<Key> pref = prefer.newBoolean(Key.IsEnabled, true);

        prefer.subscribe(pref, new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignore
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: unsubscribe
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void unsubscribe_should_throw_if_null_prefer() throws Exception {
        // noinspection unchecked
        prefer.unsubscribe(null, new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignored
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void unsubscribe_should_throw_if_null_subscriber() throws Exception {
        prefer.unsubscribe(createBooleanPref(true), null);
    }

    @Test
    public void unsubscribe_should_not_call_subscriber_on_value_changed() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);

        BooleanPref<Key> booleanPref = createBooleanPref(false);
        OnValueChanged<Boolean> subscriber = new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                called.set(true);
            }
        };

        prefer.subscribe(booleanPref, subscriber);
        prefer.unsubscribe(booleanPref, subscriber);

        booleanPref.setValue(true);

        assertFalse(called.get());
        assertTrue(booleanPref.getValue());
    }

    @Test(expected = PreferNotInitializedException.class)
    public void unsubscribe_should_throw_if_not_initialized() throws Exception {
        SharedPreferencesPrefer prefer = new SharedPreferencesPrefer(new FixtureSharedPreferences());
        AndroidBooleanPref<Key> pref = prefer.newBoolean(Key.IsEnabled, true);

        prefer.unsubscribe(pref, new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignore
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: disposePrefer
    //----------------------------------------------------------------------------------------------

    @Test
    public void disposePrefer_should_clear_all_pref_groups() throws Exception {
        AndroidPrefGroup<Key> group = prefer.addNewGroup(Key.class);

        assertTrue(prefer.groups.containsValue(group));

        prefer.disposePrefer();

        assertFalse(prefer.groups.containsValue(group));
    }

    @Test
    public void disposePrefer_should_clear_all_pref_subscribers() throws Exception {
        AndroidBooleanPref<Key> pref = prefer.newBoolean(Key.IsEnabled, true);

        OnValueChanged<Boolean> subscriber = new OnValueChanged<Boolean>() {
            @Override
            public void onValueChanged(Boolean value) {
                // ignore
            }
        };

        prefer.subscribe(pref, subscriber);

        assertTrue(prefer.prefSubscribers.containsKey(pref));

        prefer.disposePrefer();

        assertFalse(prefer.prefSubscribers.containsKey(pref));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getFromString
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void getFromString_should_throw_if_key_null() throws Exception {
        prefer.getFromString(null, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: putFromString
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void putFromString_should_throw_if_key_null() throws Exception {
        prefer.putFromString(null, null);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getBoolean & putBoolean
    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("all")
    @Test
    public void getBoolean_should_return_stored_value() throws Exception {
        boolean stored = false;

        prefer.putBoolean(Key.IsEnabled, stored);

        boolean result = prefer.getBoolean(Key.IsEnabled, true);

        assertEquals(stored, result);
    }

    @SuppressWarnings("all")
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
        // noinspection unchecked
        prefer.addGroup(null);
    }

    @Test
    public void addGroup_should_create_and_add_new_pref_group() throws Exception {
        assertTrue(prefer.groups.size() == 0);

        AndroidPrefGroup<Key> group = new AndroidPrefGroup<>(prefer, Key.class);
        prefer.addGroup(group);

        assertTrue(prefer.groups.size() == 1);
        assertTrue(prefer.groups.containsValue(group));
    }

    @Test(expected = GroupAlreadyAddedException.class)
    public void addGroup_should_throw_if_already_contains_key_class() throws Exception {
        AndroidPrefGroup<Key> group = new AndroidPrefGroup<>(prefer, Key.class);
        prefer.addGroup(group);
        prefer.addGroup(group);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addNewGroup
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addNewGroup_should_throw_if_enum_class_null() throws Exception {
        // noinspection unchecked
        prefer.addNewGroup(null);
    }

    @Test
    public void addNewGroup_should_create_and_add_new_pref_group() throws Exception {
        assertTrue(prefer.groups.size() == 0);

        AndroidPrefGroup<Key> group = prefer.addNewGroup(Key.class);

        assertTrue(prefer.groups.size() == 1);
        assertTrue(prefer.groups.containsValue(group));
    }

    @Test(expected = GroupAlreadyAddedException.class)
    public void addNewGroup_should_throw_if_already_contains_key_class() throws Exception {
        prefer.addNewGroup(Key.class);
        prefer.addNewGroup(Key.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: findGroup
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void findGroup_should_throw_if_key_class_null() throws Exception {
        // noinspection unchecked
        prefer.findGroup(null);
    }

    @Test
    public void findGroup_should_not_throw_if_not_found() throws Exception {
        prefer.findGroup(Key.class);
    }

    @Test
    public void findGroup_should_return_group() throws Exception {
        AndroidPrefGroup<Key> group = prefer.addNewGroup(Key.class);

        PrefGroup<Key> result = prefer.findGroup(Key.class);

        assertSame(group, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getGroups
    //----------------------------------------------------------------------------------------------

    @Test
    public void getGroups_should_return_empty_set_if_no_groups_added() throws Exception {
        Set<PrefGroup<? extends Enum>> groups = prefer.getGroups();

        assertNotNull(groups);
        assertTrue(groups.isEmpty());
    }

    @Test
    public void getGroups_should_return_groups() throws Exception {
        AndroidPrefGroup<Key> first = prefer.addNewGroup(Key.class);
        AndroidPrefGroup<Thread.State> second = prefer.addNewGroup(Thread.State.class);

        Set<PrefGroup<? extends Enum>> groups = prefer.getGroups();

        assertNotNull(groups);
        assertTrue(groups.contains(first));
        assertTrue(groups.contains(second));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: onChangeListener
    //----------------------------------------------------------------------------------------------

//    // NOTE: this test is for checking the error print only
//    @Test
//    public void onChangeListener_should_throw_if_key_cannot_be_deserialized() throws Exception {
//        prefer.onChangeListener.onSharedPreferenceChanged(null, "foo");
//    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newBoolean
    //----------------------------------------------------------------------------------------------

    @Test
    public void newBoolean_should_create_and_add_boolean_pref() throws Exception {
        AndroidBooleanPref<Key> pref = prefer.newBoolean(Key.IsEnabled, true);

        assertNotNull(pref);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newInteger
    //----------------------------------------------------------------------------------------------

    @Test
    public void newInteger_should_create_and_add_integer_pref() throws Exception {
        AndroidIntegerPref<Key> pref = prefer.newInteger(Key.IntervalMs, 123);

        assertNotNull(pref);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: newString
    //----------------------------------------------------------------------------------------------

    @Test
    public void newString_should_create_and_add_string_pref() throws Exception {
        AndroidStringPref<Key> pref = prefer.newString(Key.Username, "foo");

        assertNotNull(pref);
    }

    //----------------------------------------------------------------------------------------------
    // HELPER METHODS
    //----------------------------------------------------------------------------------------------

    private BooleanPref<Key> createBooleanPref(boolean defaultValue) {
        return prefer.newBoolean(Key.IsEnabled, defaultValue);
    }

}
