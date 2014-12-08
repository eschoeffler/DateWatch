package com.eschoeffler.datewatch.shared;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;

/**
 * Created by eschoeffler on 11/9/14.
 */
public abstract class WearableMapSerializer<T extends WearableSerializable> extends WearableSerializer<T> {

  public WearableMapSerializer(String uniqueName) {
    super(uniqueName);
  }

  @Override
  public void serializeToDataMap(DataMap dataMap, T serializable) {
    dataMap.putDataMap(getKey(), serializable.serialize());
  }

  @Override
  protected T deserializeFromDataMap(DataMap dataMap) {
    return fromDataMap(dataMap.getDataMap(getKey()));
  }

  protected abstract T fromDataMap(DataMap dataMap);
}
