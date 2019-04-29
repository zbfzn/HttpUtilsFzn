package top.lyfzn.httputilsfzn;
/**
 * 需添加OKHttp，fastjson依赖，errorCode:{-1(链接不合法)}{-401(添加Header出错：数组超限)}{-4001(网络资源解析出错)}{-4002（网络请求出错）}{ -402（添加Header出错：空指针）}{-403(数据解析异常)}{-404(表单数据解析异常)}
 */

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtilFzn{//erro方法最后调用,failed返回“”空字符串或接口报错信息
    public static final String UA_PHONE="User-Agent: Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Mobile Safari/537.36",
            UA_PC="user-agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36";

     private  OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();
     private List<HttpTask> tasks;
     public HttpUtilFzn(){
            tasks=new ArrayList<>();
     }
    public void requestPost(final String url, final String[] header_strs, final FznData fznData, final RequestCallBack rcall, final RequestErrorManager errorManager){//json、text、form
        if(url.contains("http://")||url.contains("https://")){
            HttpTask httpTask=excuteTask(new TaskListener() {
                @Override
                public JSONObject Do() {
                    Request.Builder r_b=new Request.Builder();
                    r_b.url(url);
                    if(header_strs!=null){//add headers
                        if(header_strs.length>0){
                            String[][] headerss=praseDataToStrings(errorManager,header_strs);
                            for (String[] header:headerss) {
                                try{
                                    r_b.addHeader(header[0],header[1]);
                                }catch (IndexOutOfBoundsException ioe){
                                    Log.e("haerder_error_out","添加Header出错：数组超限");
                                    errorManager.addErrorMes("errorCode:-401   "+ioe.getMessage());
                                }catch (NullPointerException np){
                                    Log.e("header_error_nullpoint","添加Header出错：空指针");
                                    errorManager.addErrorMes("errorCode:-402   "+np.getMessage());
                                }
                            }
                        }
                    }
                    if(fznData!=null){
                        fznData.create(errorManager);
                        if(fznData.params!=null){//添加data
                            r_b.post((RequestBody) fznData.params);
                        }else{
                            r_b.post(FormBody.create(MediaType.parse("text/plain"),""));
                        }
                    }


                    Request request=r_b.build();
                    Call call=okHttpClient.newCall(request);
                    String data;
                    JSONObject all=new JSONObject();
                    try{
                        Response ce=call.execute();
                        if(ce.isSuccessful()){
                            data= Objects.requireNonNull(ce.body()).string();
                            all.put("code",0);
                            all.put("content",data);
                        }else {
                            data="{}";
                            all.put("code",-1);
                            all.put("content",data);
                            Log.e("network_error","网络请求出错");
                            errorManager.addErrorMes("errorCode:-4001(网络请求出错)");
                        }

                    }catch (Exception e){
                        data="{}";
                        all.put("code",-1);
                        all.put("content",data);
                        Log.e("network_error","网络请求出错");
                        errorManager.addErrorMes("errorCode:-4002(网络请求出错)");
                    }
                    return all;
                }
                @Override
                public void Done(JSONObject data){
                    rcall.beforeResult();

                    if(data.getIntValue("code")==0){
                        rcall.success(data.getString("content"));
                    }else{
                        rcall.fialed(errorManager,data.getString("content"));
                    }
                    errorManager.freshErrMessages();

                    rcall.afterResult();
                }
            });
            tasks.add(httpTask);
        }else{
            rcall.fialed(errorManager,"errorCode:-1");
            errorManager.addErrorMes("请求链接不合法："+url);
            errorManager.freshErrMessages();
        }

    }
    public void requestGet(final String url, final String[] header_strs, final RequestCallBack rcall, final RequestErrorManager errorManager){
         if(url.contains("http://")||url.contains("https://")){
             HttpTask httpTask=excuteTask(new TaskListener() {
                 @Override
                 public JSONObject Do() {
                     Request.Builder r_b=new Request.Builder();
                     r_b.get();
                     r_b.url(url);
                     if(header_strs!=null){//add headers
                         if(header_strs.length>0){
                             String[][] headerss=praseDataToStrings(errorManager,header_strs);
                             for (String[] header:headerss) {
                                 try{
                                     r_b.addHeader(header[0],header[1]);
                                 }catch (IndexOutOfBoundsException ioe){
                                     Log.e("haerder_error_out","添加Header出错：数组超限");
                                     errorManager.addErrorMes("errorCode:-401   "+ioe.getMessage());
                                 }catch (NullPointerException np){
                                     Log.e("header_error_nullpoint","添加Header出错：空指针");
                                     errorManager.addErrorMes("errorCode:-402   "+np.getMessage());
                                 }
                             }
                         }
                     }

                     Request request=r_b.build();
                     Call call=okHttpClient.newCall(request);
                     String data=null;
                     JSONObject all=new JSONObject();
                     try{
                         Response ce=call.execute();
                         if(ce.isSuccessful()){
                             data= Objects.requireNonNull(ce.body()).string();
                             all.put("code",0);
                             all.put("content",data);
                         }else {
                             data="{}";
                             all.put("code",-1);
                             all.put("content",data);
                             Log.e("network_error","网络请求出错");
                             errorManager.addErrorMes("errorCode:-4001(网络请求出错)");
                         }

                     }catch (Exception e){
                         data="{}";
                         all.put("code",-1);
                         all.put("content",data);
                         Log.e("network_error","网络请求出错");
                         errorManager.addErrorMes("errorCode:-4002(网络请求出错)");
                     }
                     return all;
                 }
                 @Override
                 public void Done(JSONObject data){
                     rcall.beforeResult();

                     if(data.getIntValue("code")==0){
                         rcall.success(data.getString("content"));
                     }else{
                         rcall.fialed(errorManager,data.getString("content"));
                     }
                     errorManager.freshErrMessages();

                     rcall.afterResult();
                 }
             });
             tasks.add(httpTask);
         }else{
             rcall.fialed(errorManager,"errorCode:-1");
             errorManager.addErrorMes("请求链接不合法："+url);
             errorManager.freshErrMessages();
         }

    }
    public static String[][] praseDataToStrings(RequestErrorManager errorManager,String[] strings_strs){
        String[][] strings=new String[strings_strs.length][2];
        try{
            int i=0;
            for ( String string_str:strings_strs ) {
                strings[i]=string_str.split("#:#");
                i++;
            }
        }catch (Exception e){
            errorManager.addErrorMes("errorCode:-403");
        }
        return strings;
    }
    private HttpTask excuteTask(TaskListener tl){
        HttpTask httpTask=new HttpTask();
        httpTask.execute(tl);
        return httpTask;
    }

    public boolean cancelRequest(HttpTask httpTask){
         if(tasks.contains(httpTask)){
             if( httpTask.cancel(true)){
                 tasks.remove(httpTask);
                 return true;
             }else
                 return false;

        }else
            return false;
    }
    public boolean cancelAllRequest(){
         for(HttpTask ht:tasks){
            if(! ht.cancel(true)){
                return false;
            }else {
                return true;
            }
         }
         tasks.clear();
         return true;
    }


   public static class RequestErrorManager{
         private List<String> errMessages=new ArrayList<>();
         private HttpFznErrorListener httpFznErrorListener;
         public RequestErrorManager(HttpFznErrorListener httpFznErrorListener){
                this.httpFznErrorListener=httpFznErrorListener;
            }
         public void freshErrMessages(){
                httpFznErrorListener.error(errMessages);
         }
        public void addErrorMes(String errorMes){
             errMessages.add(errorMes);
         }
    }
   public interface RequestCallBack{
         void beforeResult();
        void success(String data);
        void fialed(RequestErrorManager errorManager, String error_data);
        void afterResult();
    }
    interface TaskListener{
        JSONObject Do();
        void Done(JSONObject data);
    }
   public interface HttpFznErrorListener{
        void error(List<String> errMessages);
    }

    class HttpTask extends AsyncTask<TaskListener,Void,Object>{
        private JSONObject data=null;
        @Override
        protected TaskListener doInBackground(TaskListener... tl) {

            data=tl[0].Do();
            return tl[0];
        }

        @Override
        protected void onPostExecute(Object o) {
            ((TaskListener)o).Done(data);
        }
    }

}
