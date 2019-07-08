/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.appcenter.reactnative.data;

import android.app.Application;

import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.data.Data;
import com.microsoft.appcenter.data.TimeToLive;
import com.microsoft.appcenter.data.exception.DataException;
import com.microsoft.appcenter.data.models.DocumentWrapper;
import com.microsoft.appcenter.data.models.ReadOptions;
import com.microsoft.appcenter.data.models.WriteOptions;
import com.microsoft.appcenter.reactnative.shared.AppCenterReactNativeShared;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;

public class AppCenterReactNativeDataModule extends BaseJavaModule {

    private static final String DESERIALIZED_VALUE_KEY = "deserializedValue";

    private static final String JSON_VALUE_KEY = "jsonValue";

    private static final String ETAG_KEY = "eTag";

    private static final String LAST_UPDATED_DATE_KEY = "lastUpdatedDate";

    private static final String IS_FROM_DEVICE_CACHE_KEY = "isFromDeviceCache";

    private static final String ID_KEY = "id";

    private static final String PARTITION_KEY = "partition";

    private static final String TIME_TO_LIVE_KEY = "timeToLive";

    public AppCenterReactNativeDataModule(Application application) {
        AppCenterReactNativeShared.configureAppCenter(application);
        if (AppCenter.isConfigured()) {
            AppCenter.start(Data.class);
        }
    }

    @Override
    public String getName() {
        return "AppCenterReactNativeData";
    }

    @ReactMethod
    public void read(String documentId, String partition, ReadableMap readOptionsMap, final Promise promise) {
        ReadOptions readOptions = getReadOptions(readOptionsMap);
        Data.read(documentId, JsonElement.class, partition, readOptions).thenAccept(new Consumer<JsonElement>("Read failed", promise));
    }

    @ReactMethod
    public void create(String documentId, String partition, ReadableMap documentMap, ReadableMap writeOptionsMap, final Promise promise) {
        WriteOptions writeOptions = getWriteOptions(writeOptionsMap);
        JsonObject jsonObject = AppCenterReactNativeDataUtils.convertReadableMapToJsonObject(documentMap);
        Data.create(documentId, jsonObject, JsonElement.class, partition, writeOptions).thenAccept(new Consumer<JsonElement>("Create failed", promise));
    }

    @ReactMethod
    public void remove(String documentId, String partition, ReadableMap writeOptionsMap, final Promise promise) {
        WriteOptions writeOptions = getWriteOptions(writeOptionsMap);
        Data.delete(documentId, partition, writeOptions).thenAccept(new Consumer<Void>("Delete failed", promise));
    }

    @ReactMethod
    public void replace(String documentId, String partition, ReadableMap documentMap, ReadableMap writeOptionsMap, final Promise promise) {
        WriteOptions writeOptions = getWriteOptions(writeOptionsMap);
        JsonObject jsonObject = AppCenterReactNativeDataUtils.convertReadableMapToJsonObject(documentMap);
        Data.replace(documentId, jsonObject, JsonElement.class, partition, writeOptions).thenAccept(new Consumer<JsonElement>("Replace failed", promise));
    }

    private class Consumer<T> implements AppCenterConsumer<DocumentWrapper<T>> {

        private Promise mPromise;

        private String mErrorCode;

        private Consumer(String errorCode, Promise promise) {
            mPromise = promise;
            mErrorCode = errorCode;
        }

        @Override
        public void accept(DocumentWrapper<T> documentWrapper) {
            WritableMap jsDocumentWrapper = new WritableNativeMap();
            jsDocumentWrapper.putString(ETAG_KEY, documentWrapper.getETag());
            jsDocumentWrapper.putString(ID_KEY, documentWrapper.getId());
            jsDocumentWrapper.putString(PARTITION_KEY, documentWrapper.getPartition());

            /* Pass milliseconds back to JS object since `WritableMap` does not support `Date` as values. */
            jsDocumentWrapper.putDouble(LAST_UPDATED_DATE_KEY, documentWrapper.getLastUpdatedDate().getTime());
            jsDocumentWrapper.putBoolean(IS_FROM_DEVICE_CACHE_KEY, documentWrapper.isFromDeviceCache());
            jsDocumentWrapper.putString(JSON_VALUE_KEY, documentWrapper.getJsonValue());
            if (documentWrapper.getError() != null) {
                DataException dataException = documentWrapper.getError();
                mPromise.reject(mErrorCode, dataException.getMessage(), dataException, jsDocumentWrapper);
                return;
            }
            if (!(documentWrapper.getDeserializedValue() instanceof JsonElement)) {
                mPromise.resolve(jsDocumentWrapper);
                return;
            }
            JsonElement deserializedValue = (JsonElement) documentWrapper.getDeserializedValue();
            if (deserializedValue.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = deserializedValue.getAsJsonPrimitive();
                if (jsonPrimitive.isString()) {
                    jsDocumentWrapper.putString(DESERIALIZED_VALUE_KEY, jsonPrimitive.getAsString());
                } else if (jsonPrimitive.isNumber()) {
                    jsDocumentWrapper.putDouble(DESERIALIZED_VALUE_KEY, jsonPrimitive.getAsDouble());
                } else if (jsonPrimitive.isBoolean()) {
                    jsDocumentWrapper.putBoolean(DESERIALIZED_VALUE_KEY, jsonPrimitive.getAsBoolean());
                }
            } else if (deserializedValue.isJsonObject()) {
                JsonObject jsonObject = deserializedValue.getAsJsonObject();
                WritableMap writableMap = AppCenterReactNativeDataUtils.convertJsonObjectToWritableMap(jsonObject);
                jsDocumentWrapper.putMap(DESERIALIZED_VALUE_KEY, writableMap);
            } else if (deserializedValue.isJsonArray()) {
                JsonArray jsonArray = deserializedValue.getAsJsonArray();
                WritableArray writableArray = AppCenterReactNativeDataUtils.convertJsonArrayToWritableArray(jsonArray);
                jsDocumentWrapper.putArray(DESERIALIZED_VALUE_KEY, writableArray);
            } else {
                jsDocumentWrapper.putNull(DESERIALIZED_VALUE_KEY);
            }
            mPromise.resolve(jsDocumentWrapper);
        }
    }

    private ReadOptions getReadOptions(ReadableMap readOptionsMap) {
        ReadOptions readOptions;
        if (readOptionsMap != null && readOptionsMap.hasKey(TIME_TO_LIVE_KEY)) {
            readOptions = new ReadOptions(readOptionsMap.getInt(TIME_TO_LIVE_KEY));
        } else {
            readOptions = new ReadOptions();
        }
        return readOptions;
    }

    private WriteOptions getWriteOptions(ReadableMap writeOptionsMap) {
        WriteOptions writeOptions;
        if (writeOptionsMap != null && writeOptionsMap.hasKey(TIME_TO_LIVE_KEY)) {
            writeOptions = new WriteOptions(writeOptionsMap.getInt(TIME_TO_LIVE_KEY));
        } else {
            writeOptions = new WriteOptions();
        }
        return writeOptions;
    }
}