package com.eschoeffler.datewatch.shared;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eschoeffler on 11/9/14.
 */
public abstract class WearableSerializer<T> {
  private String mPath;
  private String mKey;
  public WearableSerializer(String uniqueName) {
    mPath = "/" + uniqueName;
    mKey = uniqueName;
  }

  public boolean matchesType(DataItem item) {
    return item.getUri().getPath().equals(mPath);
  }

  public String getKey() {
    return mKey;
  }

  public String getPath() {
    return mPath;
  }

  public PutDataRequest serialize(T serializable) {
    PutDataMapRequest request = PutDataMapRequest.create(mPath);
    serializeToDataMap(request.getDataMap(), serializable);
    return request.asPutDataRequest();
  }

  public T deserialize(DataItem dataItem) {
    if (!matchesType(dataItem)) {
      throw new IllegalArgumentException("dataItem has the wrong path." +
          " Expected " + mPath + " but got " + dataItem.getUri().getPath());
    }
    return deserializeFromDataMap(DataMapItem.fromDataItem(dataItem).getDataMap());
  }

  protected abstract void serializeToDataMap(DataMap dataMap, T serializable);
  protected abstract T deserializeFromDataMap(DataMap dataMap);
}
