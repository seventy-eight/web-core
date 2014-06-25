package org.seventyeight.web.model;

import com.google.gson.JsonObject;

/**
 * @author cwolfgang
 */
public interface CoreRequest extends ParameterRequest {
    public static final String SESSION_USER = "sessionUser";
    public JsonObject getJson();
}
