package com.time_em.model;

/**
 * Created by minakshi on 08/06/16.
 */
public class MultipartDataModel {
    public static int FILE_TYPE = 0, STRING_TYPE = 1;
    public String key, value;
    public int dataType;

    public MultipartDataModel(String key, String value, int dataType) {
        this.key = key;
        this.value = value;
        this.dataType = dataType;
    }
}
