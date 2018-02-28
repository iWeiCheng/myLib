package com.baima.commonlibrary.http.parser;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson解析器
 * Created by cai.jia on 2016/7/13 0013.
 */
public class GsonParser implements Parser {

    private Gson gson;

    public GsonParser() {
        gson = new Gson();
    }

    @Override
    public <T> T parser(String result, Type type) throws Exception {
        return gson.fromJson(result, type);
    }
}
