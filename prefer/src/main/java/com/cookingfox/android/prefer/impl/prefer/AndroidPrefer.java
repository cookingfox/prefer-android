package com.cookingfox.android.prefer.impl.prefer;

import com.cookingfox.android.prefer.api.exception.GroupAlreadyAddedException;
import com.cookingfox.android.prefer.api.exception.PreferNotInitializedException;
import com.cookingfox.android.prefer.api.pref.OnGroupValueChanged;
import com.cookingfox.android.prefer.api.pref.OnValueChanged;
import com.cookingfox.android.prefer.api.pref.Pref;
import com.cookingfox.android.prefer.api.pref.PrefGroup;
import com.cookingfox.android.prefer.api.prefer.Prefer;
import com.cookingfox.android.prefer.impl.pref.AndroidPrefGroup;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidBooleanPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidFloatPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidIntegerPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidLongPref;
import com.cookingfox.android.prefer.impl.pref.typed.AndroidStringPref;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.cookingfox.guava_preconditions.Preconditions.checkNotNull;

/**
 * Base implementation of {@link Prefer}.
 */
public abstract class AndroidPrefer implements Prefer {

    /**
     * Pref groups by key class.
     */
    protected final Map<Class, PrefGroup<? extends Enum>> groups = new LinkedHashMap<>();

    /**
     * Helper class.
     */
    protected PreferHelper helper;

    /**
     * Whether this Prefer is initialized: call {@link #initializePrefer()} first.
     */
    protected boolean initialized = false;

    /**
     * Value changed listeners per Pref.
     */
    protected final Map<Pref, Set<OnValueChanged>> prefValueChangedListeners = new LinkedHashMap<>();

    /**
     * Group value changed listeners per group.
     */
    protected final Map<PrefGroup, Set<OnGroupValueChanged>> prefGroupValueChangedListeners = new LinkedHashMap<>();

    //----------------------------------------------------------------------------------------------
    // IMPLEMENTATION: PreferLifecycle
    //----------------------------------------------------------------------------------------------

    @Override
    public void initializePrefer() {
        if (initialized) {
            return;
        }

        getHelper().initializePrefer();

        initialized = true;
    }

    @Override
    public void disposePrefer() {
        if (!initialized) {
            return;
        }

        getHelper().disposePrefer();

        groups.clear();
        prefValueChangedListeners.clear();
        prefGroupValueChangedListeners.clear();

        initialized = false;
    }

    //----------------------------------------------------------------------------------------------
    // BOOLEAN
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean getBoolean(Enum key, boolean defaultValue) {
        return getHelper().getBoolean(key, defaultValue);
    }

    @Override
    public void putBoolean(Enum key, boolean value) {
        getHelper().putBoolean(key, value);
    }

    //----------------------------------------------------------------------------------------------
    // FLOAT
    //----------------------------------------------------------------------------------------------

    @Override
    public float getFloat(Enum key, float defaultValue) {
        return getHelper().getFloat(key, defaultValue);
    }

    @Override
    public void putFloat(Enum key, float value) {
        getHelper().putFloat(key, value);
    }

    //----------------------------------------------------------------------------------------------
    // INTEGER
    //----------------------------------------------------------------------------------------------

    @Override
    public int getInteger(Enum key, int defaultValue) {
        return getHelper().getInteger(key, defaultValue);
    }

    @Override
    public void putInteger(Enum key, int value) {
        getHelper().putInteger(key, value);
    }

    //----------------------------------------------------------------------------------------------
    // LONG
    //----------------------------------------------------------------------------------------------

    @Override
    public long getLong(Enum key, long defaultValue) {
        return getHelper().getLong(key, defaultValue);
    }

    @Override
    public void putLong(Enum key, long value) {
        getHelper().putLong(key, value);
    }

    //----------------------------------------------------------------------------------------------
    // STRING
    //----------------------------------------------------------------------------------------------

    @Override
    public String getString(Enum key, String defaultValue) {
        return getHelper().getString(key, defaultValue);
    }

    @Override
    public void putString(Enum key, String value) {
        getHelper().putString(key, value);
    }

    //----------------------------------------------------------------------------------------------
    // PREF LISTENERS
    //----------------------------------------------------------------------------------------------

    @Override
    public <K extends Enum<K>, V> void addValueChangedListener(Pref<K, V> pref, OnValueChanged<V> listener) {
        checkNotNull(pref, "Pref can not be null");
        checkNotNull(listener, "Listener can not be null");

        if (!initialized) {
            throw new PreferNotInitializedException("Can not add listener");
        }

        Set<OnValueChanged> listeners = this.prefValueChangedListeners.get(pref);

        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            prefValueChangedListeners.put(pref, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public <K extends Enum<K>, V> void removeValueChangedListener(Pref<K, V> pref, OnValueChanged<V> listener) {
        checkNotNull(pref, "Pref can not be null");
        checkNotNull(listener, "Listener can not be null");

        if (!initialized) {
            throw new PreferNotInitializedException("Can not add listener");
        }

        final Set<OnValueChanged> listeners = this.prefValueChangedListeners.get(pref);

        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    //----------------------------------------------------------------------------------------------
    // GROUPS
    //----------------------------------------------------------------------------------------------

    @Override
    public <K extends Enum<K>> void addGroup(PrefGroup<K> group) {
        checkNotNull(group, "Group can not be null");

        Class<K> keyClass = group.getKeyClass();

        if (groups.containsKey(keyClass)) {
            throw new GroupAlreadyAddedException(group);
        }

        groups.put(keyClass, group);
    }

    @Override
    public <K extends Enum<K>> void addGroupValueChangedListener(PrefGroup<K> group, OnGroupValueChanged<K> listener) {
        checkNotNull(group, "Pref can not be null");
        checkNotNull(listener, "Listener can not be null");

        if (!initialized) {
            throw new PreferNotInitializedException("Can not add group listener");
        }

        Set<OnGroupValueChanged> listeners = this.prefGroupValueChangedListeners.get(group);

        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            prefGroupValueChangedListeners.put(group, listeners);
        }

        listeners.add(listener);
    }

    @Override
    public <K extends Enum<K>> PrefGroup<K> findGroup(Class<K> keyClass) {
        // noinspection unchecked
        return (PrefGroup<K>) groups.get(checkNotNull(keyClass, "Key class can not be null"));
    }

    @Override
    public Set<PrefGroup<? extends Enum>> getGroups() {
        return new LinkedHashSet<>(groups.values());
    }

    @Override
    public <K extends Enum<K>> void removeGroupValueChangedListener(PrefGroup<K> group, OnGroupValueChanged<K> listener) {
        checkNotNull(group, "Pref can not be null");
        checkNotNull(listener, "Listener can not be null");

        if (!initialized) {
            throw new PreferNotInitializedException("Can not add listener");
        }

        final Set<OnGroupValueChanged> listeners = this.prefGroupValueChangedListeners.get(group);

        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    //----------------------------------------------------------------------------------------------
    // ADDITIONAL PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates and adds a new Pref group for this key class.
     *
     * @param keyClass The enum key class for which to create a Pref group.
     * @param <K>      References the concrete enum key class.
     * @return The newly created group.
     * @see #addGroup(PrefGroup)
     */
    public <K extends Enum<K>> AndroidPrefGroup<K> addNewGroup(Class<K> keyClass) {
        AndroidPrefGroup<K> group = newGroup(keyClass);

        addGroup(group);

        return group;
    }

    /**
     * Creates a new Pref group for this key class.
     *
     * @param keyClass The enum key class for which to create a Pref group.
     * @param <K>      References the concrete enum key class.
     * @return The newly created group.
     * @see #addGroup(PrefGroup)
     */
    public <K extends Enum<K>> AndroidPrefGroup<K> newGroup(Class<K> keyClass) {
        return new AndroidPrefGroup<>(this, keyClass);
    }

    /**
     * Creates a new Pref with the provided key and default value.
     *
     * @param key          The Pref's unique key.
     * @param defaultValue The Pref's default value.
     * @param <K>          References the enum class for this Pref's key.
     * @return The newly created Pref.
     */
    public <K extends Enum<K>> AndroidBooleanPref<K> newBoolean(K key, boolean defaultValue) {
        return new AndroidBooleanPref<>(this, key, defaultValue);
    }

    /**
     * Creates a new Pref with the provided key and default value.
     *
     * @param key          The Pref's unique key.
     * @param defaultValue The Pref's default value.
     * @param <K>          References the enum class for this Pref's key.
     * @return The newly created Pref.
     */
    public <K extends Enum<K>> AndroidFloatPref<K> newFloat(K key, float defaultValue) {
        return new AndroidFloatPref<>(this, key, defaultValue);
    }

    /**
     * Creates a new Pref with the provided key and default value.
     *
     * @param key          The Pref's unique key.
     * @param defaultValue The Pref's default value.
     * @param <K>          References the enum class for this Pref's key.
     * @return The newly created Pref.
     */
    public <K extends Enum<K>> AndroidIntegerPref<K> newInteger(K key, int defaultValue) {
        return new AndroidIntegerPref<>(this, key, defaultValue);
    }

    /**
     * Creates a new Pref with the provided key and default value.
     *
     * @param key          The Pref's unique key.
     * @param defaultValue The Pref's default value.
     * @param <K>          References the enum class for this Pref's key.
     * @return The newly created Pref.
     */
    public <K extends Enum<K>> AndroidLongPref<K> newLong(K key, long defaultValue) {
        return new AndroidLongPref<>(this, key, defaultValue);
    }

    /**
     * Creates a new Pref with the provided key and default value.
     *
     * @param key          The Pref's unique key.
     * @param defaultValue The Pref's default value.
     * @param <K>          References the enum class for this Pref's key.
     * @return The new ly created Pref.
     */
    public <K extends Enum<K>> AndroidStringPref<K> newString(K key, String defaultValue) {
        return new AndroidStringPref<>(this, key, defaultValue);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Create a new Prefer helper instance.
     *
     * @return The create Prefer helper instance.
     */
    protected abstract PreferHelper createHelper();

    /**
     * Returns the helper instance. Creates a new helper using {@link #createHelper()} if it doesn't
     * exist yet.
     *
     * @return The Prefer helper instance.
     */
    protected PreferHelper getHelper() {
        if (helper == null) {
            helper = checkNotNull(createHelper(), "`createHelper` returned null");
        }

        return helper;
    }

    /**
     * Notify listeners of a changed Pref value.
     *
     * @param serializedKey The serialized Pref key.
     */
    @SuppressWarnings("unchecked")
    protected void handlePrefChanged(String serializedKey) {
        Enum key;

        try {
            key = PreferKeySerializer.deserializeKey(serializedKey);
        } catch (ClassNotFoundException error) {
            // ignore unknown key
            return;
        }

        // notify pref value changed listeners
        for (Map.Entry<Pref, Set<OnValueChanged>> entry : prefValueChangedListeners.entrySet()) {
            Pref pref = entry.getKey();

            // match pref by key
            if (pref.getKey().equals(key)) {
                Object value = pref.getValue();

                // pass new value to listeners
                for (OnValueChanged listener : entry.getValue()) {
                    listener.onValueChanged(value);
                }

                break;
            }
        }

        // key class for groups
        final Class<? extends Enum> keyClass = key.getClass();

        // notify pref group value changed listeners
        for (Map.Entry<PrefGroup, Set<OnGroupValueChanged>> entry : prefGroupValueChangedListeners.entrySet()) {
            PrefGroup group = entry.getKey();

            // match group by key class
            if (group.getKeyClass().equals(keyClass)) {
                Pref pref = group.findPref(key);

                // pass pref with new value to listeners
                for (OnGroupValueChanged listener : entry.getValue()) {
                    listener.onGroupValueChanged(pref);
                }

                break;
            }
        }
    }

}
