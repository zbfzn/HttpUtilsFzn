package top.lyfzn.httputilsfzn;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FznData{
    public Object params;
    public static final int TEXT=0,Json=1,FORM=2;
    private int type;
    private String[] params_origin;

    public FznData(int type,String[] params) {
        this.type = type;
        this.params_origin = params;
    }
    public void create(HttpUtilFzn.RequestErrorManager errorManager){
        if(params_origin!=null&&params_origin.length>0){
            if(type==TEXT){
                this.params= FormBody.create(MediaType.parse("text/plain"),params_origin[0]);
            }else if(type==FORM){
                FormBody.Builder formBody_b=new FormBody.Builder();
                try{
                    String[][] arr=HttpUtilFzn.praseDataToStrings(errorManager,params_origin);
                    for(String[] kv:arr){
                        formBody_b.add(kv[0],kv[1]);
                    }
                }catch (Exception e){
                    Log.e("prase_data_error","解析Form数据异常");
                    errorManager.addErrorMes("errorCode:-404   "+e.getMessage());
                }
                RequestBody form=formBody_b.build();
                this.params= form;
            }else if(type==Json){
                try{
                    this.params= FormBody.create(MediaType.parse("application/json"),params_origin[0]);
                }catch (Exception e){

                    this.params= FormBody.create(MediaType.parse("application/json"),"{}");
                }

            }
        }
    }
}
