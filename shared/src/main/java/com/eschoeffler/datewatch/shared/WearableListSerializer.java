package com.eschoeffler.datewatch.shared;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eschoeffler on 11/9/14.
 */
public abstract class WearableListSerializer<T extends WearableSerializable> extends WearableSerializer<List<T>> {
  public WearableListSerializer(String uniqueName) {
    super(uniqueName);
  }

  @Override
  protected void serializeToDataMap(DataMap dataMap, List<T> listOfSerializable) {
    ArrayList<DataMap> dataMapList = new ArrayList<DataMap>();
    for (T serializable : listOfSerializable) {
      dataMapList.add(serializable.serialize());
    }
    dataMap.putDataMapArrayList(getKey(), dataMapList);
  }

  @Override
  protected List<T> deserializeFromDataMap(DataMap dataMap) {
    ArrayList<DataMap> dataMapList = dataMap.getDataMapArrayList(getKey());
    ArrayList<T> listOfSerializable = new ArrayList<T>();
    for (DataMap serializableAsDataMap : dataMapList) {
      listOfSerializable.add(fromDataMap(serializableAsDataMap));
    }
    return listOfSerializable;
  }

  protected abstract T fromDataMap(DataMap dataMap);
}
