package com.baima.commonlibrary.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baima.commonlibrary.BuildConfig;
import com.baima.commonlibrary.http.parser.GsonParser;
import com.baima.commonlibrary.http.parser.Parser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * okhttp请求的封装类
 * Created by cai.jia on 2016/7/11 0011.
 */
public class HttpRequestManager {

    public static final String SERVER_ERROR = "服务器错误";
    public static final String NETWORK_ERROR = "网络无法访问，请先确认手机网络是否正常，再重新登录";
    public static final String PARSER_ERROR = "解析错误";

    private OkHttpClient client;

    private ExecutorService executorService;

    private static volatile HttpRequestManager manager;

    private Map<Object, Set<AsyncHttpRequest>> tagRequest;

    private Parser parser;

    private  Context context;

    private HttpRequestManager(Context context) {
        File file =new File(Environment.getExternalStorageDirectory()+"/cache");
        this.context = context.getApplicationContext();
        if(!file.exists()){
            file.mkdirs();
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
//        CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        executorService = Executors.newCachedThreadPool();
        tagRequest = new ConcurrentHashMap<>();
        parser = new GsonParser();
        client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();
        OkHttpUtils.initClient(client);


    }
    //单例模式

    public static HttpRequestManager getInstance(Context context) {
        if (manager == null) {
            synchronized (HttpRequestManager.class) {
                manager = new HttpRequestManager(context);
            }
        }
        return manager;
    }

    @NonNull
    private <T> AsyncHttpResponseHandler getHandler(final long requestId, final Type type,
                                                    final HttpResponseCallback<T> callback) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(Call call, String result) {
                if (callback == null) {
                    return;
                }

                try {
                    if (BuildConfig.DEBUG) {
                        Log.v("response", result);
                    }
                    if (type == null) {
                        callback.onSuccess(requestId, (T) result, result);

                    } else {
                        T t = parser.parser(result, type);
                        if (t != null) {
                            callback.onSuccess(requestId, t, result);

                        } else {
                            callback.onFailure(requestId, PARSER_ERROR, result);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure(requestId, PARSER_ERROR, result);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }

                if (callback == null) {
                    return;
                }
                callback.onFailure(requestId, SERVER_ERROR, SERVER_ERROR);
            }
        };
    }

    public <T> void get(Context context, long requestId, Object tag, String url,
                        RequestParams params, Type type, HttpResponseCallback<T> callback) {
        RequestConfig config = new RequestConfig.Builder()
                .requestId(requestId)
                .context(context)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(getUrlWithQueryString(url, params))
                .tag(tag)
                .build();
        sendRequest(config, request, type, callback);

        if (BuildConfig.DEBUG) {
            Log.v("request", getUrlWithQueryString(url, params));
        }

    }

    public <T> void post(Context context, long requestId, Object tag, String url,
                         RequestParams params, Type type, HttpResponseCallback<T> callback) {
        RequestConfig config = new RequestConfig.Builder()
                .requestId(requestId)
                .context(context)
                .build();

        Request request = new Request.Builder()
                .post(params.getPostBody())
                .url(url)
                .tag(tag)
                .build();
        sendRequest(config, request, type, callback);
        Log.v("request", getUrlWithQueryString(url, params));
        if (BuildConfig.DEBUG) {
            Log.v("request", getUrlWithQueryString(url, params));
        }
    }

    public <T> void sendRequest(RequestConfig config, Request request, Type type,
                                HttpResponseCallback<T> callback) {
        if (config != null && config.context() != null) {
            boolean hasNetwork = isConnected(config.context());
            if (!hasNetwork) {
                callback.onFailure(config.requestId(), NETWORK_ERROR, NETWORK_ERROR);
                return;
            }
        }

        AsyncHttpResponseHandler handler = getHandler(config != null ? config.requestId() : -1,
                type, callback);
        sendRequest(request, handler);
    }

    public void sendRequest(final Request request, AsyncHttpResponseHandler handler) {
        final AsyncHttpRequest asyncRequest = new AsyncHttpRequest(client, request, handler);

        if (request != null && request.tag() != null) {
            Set<AsyncHttpRequest> requestList = tagRequest.get(request.tag());
            if (requestList == null) {
                requestList = new HashSet<>();
            }
            requestList.add(asyncRequest);
            tagRequest.put(request.tag(), requestList);
        }

        executorService.submit(new FutureTask<Boolean>(asyncRequest) {

            @Override
            protected void done() {
                if (request != null && request.tag() != null) {
                    Set<AsyncHttpRequest> requestList = tagRequest.get(request.tag());
                    if (requestList != null && !requestList.isEmpty()) {
                        requestList.remove(asyncRequest);
                    }
                }
            }
        });
    }

    public void cancel(Object tag) {
        if (tag == null) {
            return;
        }

        Set<AsyncHttpRequest> requestList = tagRequest.get(tag);
        if (requestList != null && !requestList.isEmpty()) {
            for (AsyncHttpRequest request : requestList) {
                request.cancel(tag);
            }
        }
    }

    public static String getUrlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            String paramString = params.getParamString();
            if (!url.contains("?")) {
                url += "?" + paramString;
            } else {
                url += "&" + paramString;
            }
        }

        return url;
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
