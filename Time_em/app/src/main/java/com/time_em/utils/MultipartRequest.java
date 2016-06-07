package com.time_em.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.time_em.dashboard.HomeActivity;


import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//import cz.msebera.android.httpclient.entity.ContentType;
//import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
//import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
//import cz.msebera.android.httpclient.entity.mime.content.FileBody;
//import cz.msebera.android.httpclient.entity.mime.content.StringBody;

public class MultipartRequest extends Request<NetworkResponse> {

    private MultipartEntity entity = new MultipartEntity();

//    private MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    private final Response.Listener<NetworkResponse> mListener;
    private HashMap<String, String> mParams;


    public MultipartRequest(String url,String userId, String subject, String message, String notTypeId, String notifyTo,String attachmentPath, Response.ErrorListener errorListener, Response.Listener<NetworkResponse> listener)
    {
        super(Method.POST, url, errorListener);
        Log.e("MultipartRequest","Constructor called");
        mListener = listener;

        buildMultipartEntity(userId, subject, message, notTypeId, notifyTo,attachmentPath);
    }

    private void buildMultipartEntity(String userId, String subject, String message, String notTypeId, String notifyTo, String attachmentPath)
    {
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//        FileBody fileBody = new FileBody(new File(attachmentPath)); //image should be a String
//        builder.addPart("profile_picture", fileBody);

        try
        {
            entity.addPart("profile_picture", new FileBody(new File(attachmentPath)));
            entity.addPart("UserId", new StringBody(userId, ContentType.TEXT_PLAIN));
            entity.addPart("Subject", new StringBody(subject, ContentType.TEXT_PLAIN));
            entity.addPart("Message", new StringBody(message, ContentType.TEXT_PLAIN));
            entity.addPart("NotificationTypeId", new StringBody(notTypeId, ContentType.TEXT_PLAIN));
            entity.addPart("notifyto", new StringBody(notifyTo, ContentType.TEXT_PLAIN));
        }
        catch (Exception e)
        {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

/*public class MultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;
    private final String mMimeType;
    private final byte[] mMultipartBody;

    public MultipartRequest(String url, Map<String, String> headers, String mimeType, byte[] multipartBody, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers;
        this.mMimeType = mimeType;
        this.mMultipartBody = multipartBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return mMimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mMultipartBody;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }*/
}