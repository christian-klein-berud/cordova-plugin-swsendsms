package cordova.plugin.swsms;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sanjeev on 06/06/18.
 */

public class SendWhatsappMessagePlugin extends CordovaPlugin {

    final String TAG = SendWhatsappMessagePlugin.class.getSimpleName();
    List<SMSList> smsList;
    CallbackContext mCallbackContext;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    executeCode(action, args,callbackContext);

                }
            });
            return true;

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

   // JSONArray statusArray;
    private void executeCode(final String action, final JSONArray args, final CallbackContext callbackContext){
        if (action.equalsIgnoreCase("sendMessage")) {
            try {

                smsList = new ArrayList<SMSList>();
                JSONObject jsonObject = args.getJSONObject(0);
                JSONArray array = jsonObject.getJSONArray("sms_list");
                for(int i = 0; i <array.length(); i++){
                    smsList.add(new SMSList(array.getJSONObject(i).getString("number"), array.getJSONObject(i).getString("message")));
                }

                if(smsList != null && smsList.size() > 0) {
                   // statusArray = new JSONArray();
                    openWhatsApp(cordova.getActivity(), smsList.get(0).getNumber(), smsList.get(0).getMessage());
                    smsList.remove(0);
                }

            } catch (Exception e) {
                sendErrorCallback("EXCEPTION_SEND_SMS", callbackContext);
            }
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        try {

            if (smsList != null && smsList.size() <= 0) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "success");
                    sendSuccessCallback(obj, mCallbackContext);
                } catch (Exception ex) {
                    if (mCallbackContext != null)
                        sendErrorCallback("EXCEPTION" + ex.toString(), mCallbackContext);
                    Log.d("EXCEPTION::", "" + ex.toString());
                }
            }

            if (smsList != null && smsList.size() > 0) {
                // statusArray = new JSONArray();
                openWhatsApp(cordova.getActivity(), smsList.get(0).getNumber(), smsList.get(0).getMessage());
                smsList.remove(0);
            }
        }catch (Exception ex){
            Log.d("EXCEPTION::", ""+ex.toString());
        }

    }

    public void openWhatsApp(Context context, String toNumber, String message) {
        try {
            toNumber = toNumber.replace("+", "").replace(" ", "");
            try {
                toNumber = toNumber.substring(toNumber.length() - 10);
                toNumber = "+91"+toNumber;
            }catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                PackageManager packageManager = context.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = "https://api.whatsapp.com/send?phone="+ toNumber +"&text=" + URLEncoder.encode(message, "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i);
                }
            } catch (Exception ex){
                Log.d("EXCEPTION::", ""+ex.toString());
            }

        } catch(Exception e) {
            Log.e("AppUtils", "ERROR_OPEN_MESSANGER"+e.toString());
        }
    }


    private void sendErrorCallback(final String errorMsg, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                callbackContext.error(errorMsg);
            }
        });
    }

    private void sendSuccessCallback(final JSONObject jsonObject,final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                callbackContext.success(jsonObject);
            }
        });
    }

    private void sendSuccessCallback(final String success,final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                callbackContext.success(success);
            }
        });
    }

}

