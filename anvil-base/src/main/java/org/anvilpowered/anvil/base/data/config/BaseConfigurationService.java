/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.base.data.config;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.key.Key;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.base.data.registry.BaseRegistry;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Service to load and save data from a config file
 *
 * @author Cableguy20
 */
@Singleton
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class BaseConfigurationService extends BaseRegistry implements ConfigurationService {

    protected ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode rootConfigurationNode;

    /**
     * Maps Keys to their verification function
     */
    private final Map<Key<?>, Map<Predicate<Object>, Function<Object, Object>>> verificationMap;

    /**
     * Maps ConfigKeys to configuration node names
     */
    private final Map<Key<?>, String> nodeNameMap;

    /**
     * Maps ConfigKeys to configuration node descriptions
     */
    private final Map<Key<?>, String> nodeDescriptionMap;

    private boolean configValuesEdited;

    @Inject
    public BaseConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;

        verificationMap = new HashMap<>();
        initVerificationMap();

        nodeNameMap = new HashMap<>();
        initNodeNameMap();

        nodeDescriptionMap = new HashMap<>();
        initNodeDescriptionMap();
    }

    protected void initVerificationMap() {
    }

    protected void initNodeNameMap() {
        setName(Keys.SERVER_NAME, "server.name");
        setName(Keys.USE_SHARED_ENVIRONMENT, "datastore.useSharedEnvironment");
        setName(Keys.USE_SHARED_CREDENTIALS, "datastore.useSharedCredentials");
        setName(Keys.DATA_DIRECTORY, "datastore.dataDirectory");
        setName(Keys.DATA_STORE_NAME, "datastore.dataStoreName");
        setName(Keys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString");
        setName(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname");
        setName(Keys.MONGODB_PORT, "datastore.mongodb.port");
        setName(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname");
        setName(Keys.MONGODB_USERNAME, "datastore.mongodb.username");
        setName(Keys.MONGODB_PASSWORD, "datastore.mongodb.password");
        setName(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb");
        setName(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth");
        setName(Keys.MONGODB_USE_SRV, "datastore.mongodb.useSrv");
        setName(Keys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString");
        setName(Keys.PROXY_MODE, "proxy.mode");
    }

    protected void initNodeDescriptionMap() {
        setDescription(Keys.SERVER_NAME, "\nServer name");
        setDescription(Keys.USE_SHARED_ENVIRONMENT, "\nWhether to use Anvil shared environment." +
            "\nThis will use hostname and port from Anvil");
        setDescription(Keys.USE_SHARED_CREDENTIALS, "\nWhether to use Anvil credentials. (Requires useSharedEnvironment)" +
            "\nThis will use (additionally) username, password, authDb and useAuth from Anvil");
        setDescription(Keys.DATA_DIRECTORY, "\nDirectory for extra data" +
            "\nPlease note that it is not recommended to change this value from the original");
        setDescription(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use");
        setDescription(Keys.MONGODB_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nCustom MongoDB connection string that will used instead of the connection info and credentials below" +
            "\nWill only be used if useConnectionString=true");
        setDescription(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        setDescription(Keys.MONGODB_PORT, "\nMongoDB port");
        setDescription(Keys.MONGODB_DBNAME, "\nMongoDB database name");
        setDescription(Keys.MONGODB_USERNAME, "\nMongoDB username");
        setDescription(Keys.MONGODB_PASSWORD, "\nMongoDB password");
        setDescription(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication");
        setDescription(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
        setDescription(Keys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record");
        setDescription(Keys.MONGODB_USE_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nWhether to use the connection string provided instead of the normal connection info and credentials" +
            "\nOnly use this if you know what you are doing!" +
            "\nPlease note that this plugin will inherit both useConnectionString and connectionString from" +
            "\nAnvil if and only if useSharedEnvironment and useSharedCredentials are both true");
        setDescription(Keys.PROXY_MODE, "\nEnable this if you plan to run your servers on a proxy. \nFailure to do so will cause issues with your database.");
    }

    protected <T> void setVerification(Key<T> key,
                                       Map<Predicate<T>, Function<T, T>> verification) {
        verificationMap.put(key,
            (Map<Predicate<Object>, Function<Object, Object>>) (Object) verification);
    }

    protected void setName(Key<?> key, String name) {
        nodeNameMap.put(key, name);
    }

    protected void setDescription(Key<?> key, String description) {
        nodeDescriptionMap.put(key, description);
    }

    @Override
    public <T> void set(Key<T> key, T value) {
        super.set(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T> void remove(Key<T> key) {
        super.remove(key);
        configValuesEdited = true;
    }

    @Override
    public <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer) {
        super.transform(key, transformer);
        configValuesEdited = true;
    }

    @Override
    public <T> void transform(Key<T> key, Function<? super T, ? extends T> transformer) {
        super.transform(key, transformer);
        configValuesEdited = true;
    }

    @Override
    public <T> void addToCollection(Key<? extends Collection<T>> key, T value) {
        super.addToCollection(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T> void removeFromCollection(Key<? extends Collection<T>> key, T value) {
        super.removeFromCollection(key, value);
        configValuesEdited = true;
    }

    @Override
    public <K, T> void putInMap(Key<? extends Map<K, T>> key, K mapKey, T mapValue) {
        super.putInMap(key, mapKey, mapValue);
        configValuesEdited = true;
    }

    @Override
    public <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey) {
        super.removeFromMap(key, mapKey);
        configValuesEdited = true;
    }

    @Override
    public void load() {
        initConfigMaps();
        super.load();
    }

    @Override
    public boolean save() {
        if (configValuesEdited) {
            for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
                CommentedConfigurationNode node = fromString(entry.getValue());
                node.setValue(getUnsafe(entry.getKey()));
            }
            try {
                configLoader.save(rootConfigurationNode);
                configValuesEdited = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private CommentedConfigurationNode fromString(String name) {
        String[] path = name.split("[.]");
        CommentedConfigurationNode node = rootConfigurationNode;
        for (String s : path) {
            node = node.getNode(s);
        }
        return node;
    }

    private <T> void setNodeDefault(CommentedConfigurationNode node, Key<T> key)
        throws ObjectMappingException {
        node.setValue(key, getDefault(key));
    }

    private void initConfigMaps() {

        try {
            rootConfigurationNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int updatedCount = 0;
        for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
            Key<?> key = entry.getKey();
            CommentedConfigurationNode node = fromString(entry.getValue());
            if (node.isVirtual()) {
                try {
                    setNodeDefault(node, key);
                    updatedCount++;
                } catch (ObjectMappingException e) {
                    e.printStackTrace();
                }
            } else {
                boolean[] modified = {false};
                initConfigValue(key, node, modified);
                if (modified[0]) {
                    updatedCount++;
                }
            }

            if (node.isVirtual() || !node.getComment().isPresent()) {
                node.setComment(nodeDescriptionMap.get(key));
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
            try {
                configLoader.save(rootConfigurationNode);
                configValuesEdited = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param typeToken {@link TypeToken} of node to parse. Pass a {@link Key} to save that value to the registry
     * @param node      {@link CommentedConfigurationNode} to get value from
     */
    private <T> T initConfigValue(TypeToken<T> typeToken, CommentedConfigurationNode node, boolean[] modified) {

        // it ain't pretty but it works

        if (typeToken instanceof Key && typeToken.isSubtypeOf(List.class)) {

            // *** unwrap list *** //
            try {

                Method getMethod = List.class.getMethod("get", int.class);
                Invokable<? extends T, ?> invokable = typeToken.method(getMethod);
                T list = (T) verify(verificationMap.get(typeToken), node.getList(invokable.getReturnType()), node, modified);

                set((Key<T>) typeToken, list);

                return list;

            } catch (NoSuchMethodException | IllegalArgumentException | ObjectMappingException e) {
                e.printStackTrace();
                return null;
            }

        } else if (typeToken.isSubtypeOf(Map.class)) {

            // *** unwrap map *** //
            try {

                Method getMethod = Map.class.getMethod("get", Object.class);
                Invokable<?, ?> invokable = typeToken.method(getMethod);
                TypeToken<?> subType = invokable.getReturnType();

                Map<Object, Object> result = new HashMap<>();

                for (Map.Entry<?, ? extends CommentedConfigurationNode> entry : node.getChildrenMap().entrySet()) {
                    // here comes the recursion
                    result.put(entry.getValue().getKey(), initConfigValue(subType, entry.getValue(), modified));
                }

                if (typeToken instanceof Key) {
                    Key<T> key = (Key<T>) typeToken;
                    T map = (T) verify(verificationMap.get(key), result, node, modified);
                    set(key, map);
                }

                return (T) result;

            } catch (NoSuchMethodException | IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
        } else if (typeToken instanceof Key) {
            try {
                T value = node.getValue(typeToken);
                Key<T> key = (Key<T>) typeToken;
                set(key, (T) verify(verificationMap.get(key), value, node, modified));
                return value;
            } catch (ClassCastException | ObjectMappingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return node.getValue(typeToken);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private <T> T verify(Map<Predicate<T>, Function<T, T>> verificationMap, T value, CommentedConfigurationNode node, boolean[] modified) {
        if (verificationMap == null) return value; // if there is no verification function defined
        T result = value;
        for (Map.Entry<Predicate<T>, Function<T, T>> entry : verificationMap.entrySet()) {
            if (entry.getKey().test(result)) {
                modified[0] = true;
                result = entry.getValue().apply(result);
            }
        }
        if (modified[0]) {
            node.setValue(result);
        }
        return result;
    }
}
