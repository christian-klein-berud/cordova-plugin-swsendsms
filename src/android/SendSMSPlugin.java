package cordova.plugin.swsms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sanjeev on 28/2/18.
 */

public class SendSMSPlugin extends CordovaPlugin {

    final String TAG = SendSMSPlugin.class.getSimpleName();
    List<SMSList> smsList;
    CallbackContext mCallbackContext;
    private static BroadcastReceiver smsSentBrocarsReceiver = null;
    private static BroadcastReceiver smsDelivedBrocarsReceiver = null;

    private static String isSMSSent = "";
    private static String isSMSDelivered = "";

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        BaseActivity.internalPermissionListner = new InternalPermissionListner() {

                            @Override
                            public void permissionNotInvoked() {
                                sendErrorCallback("PERMISSION_NOT_INVOKED", callbackContext);
                            }

                            @Override
                            public void permissionGranted() {
                                executeCode(action, args,callbackContext);
                            }

                            @Override
                            public void permissionDenied() {
                                sendErrorCallback("PERMISSION_DENINED", callbackContext);
                            }
                        };

                        Intent intent = new Intent(cordova.getActivity(), BaseActivity.class);
                        intent.putExtra("PERMISSION", true);
                        cordova.getActivity().startActivity(intent);

                    } else {
                        executeCode(action, args,callbackContext);
                    }

                }
            });
            return true;

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    JSONArray statusArray;
    private void executeCode(final String action, final JSONArray args, final CallbackContext callbackContext) {
        if (action.equalsIgnoreCase("sendSMS")) {
            try {

                smsList = new ArrayList<SMSList>();
                JSONObject jsonObject = args.getJSONObject(0);
                JSONArray array = jsonObject.getJSONArray("sms_list");
                for (int i = 0; i < array.length(); i++) {
                    smsList.add(new SMSList(array.getJSONObject(i).getString("number"), array.getJSONObject(i).getString("message")));
                }

                if (smsList != null && smsList.size() > 0) {
                    statusArray = new JSONArray();
                    sendSMS(cordova.getActivity(), smsList.get(0).getNumber(), smsList.get(0).getMessage());

                }

            } catch (Exception e) {
                sendErrorCallback("EXCEPTION_SEND_SMS", callbackContext);
            }
        }

    }

    public void sendSMS(final Context context, final String mobileNo, final String smsKeyWord) {
        try {
            String smsText = "";
            try {
                smsText = smsKeyWord;
                // Log.e("smsText", "smsText   " + smsText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // if (telephonyManager.getSimState() ==
            // TelephonyManager.SIM_STATE_READY) {
            if (smsText != null && !smsText.trim().equalsIgnoreCase("")) {

                String SENT = "SMS_SENT";
                /* String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

               try {

                    isSMSSent = "NO";
                    isSMSDelivered = "NO";

                    if (smsSentBrocarsReceiver != null) {
                        context.unregisterReceiver(smsSentBrocarsReceiver);
                        smsSentBrocarsReceiver = null;
                    }

                    if(smsDelivedBrocarsReceiver != null) {
                        context.unregisterReceiver(smsDelivedBrocarsReceiver);
                        smsDelivedBrocarsReceiver = null;
                    }

                    if (smsSentBrocarsReceiver == null) {
                        smsSentBrocarsReceiver = new BroadcastReceiver() {

                            @Override
                            public void onReceive(Context arg0, Intent arg1) {
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        isSMSSent = "S";
                                        //Toast.makeText(YourActivity.this, "SMS sent",Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        isSMSSent = "F";
                                        //Toast.makeText(YourActivity.this, "Generic failure",Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        isSMSSent = "F";
                                        //Toast.makeText(YourActivity.this, "No service",Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        isSMSSent = "F";
                                        //Toast.makeText(YourActivity.this, "Null PDU",Toast.LENGTH_SHORT).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        isSMSSent = "F";
                                        //Toast.makeText(getBaseContext(), "Radio off",Toast.LENGTH_SHORT).show();
                                        break;

                                }
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("mobile", mobileNo);
                                    jsonObject.put("status", isSMSSent);

                                  //  statusArray.put(jsonObject);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                try {
                                    if(smsList.size() > 0)
                                        smsList.remove(0);

                                    if (smsList != null && smsList.size() > 0) {
                                        sendSMS(cordova.getActivity(), smsList.get(0).getNumber(), smsList.get(0).getMessage());
                                    } else if (smsList.size() <= 0) {
                                      //  sendSuccessCallback(statusArray, mCallbackContext);
                                        if (smsSentBrocarsReceiver == null) {
                                            context.unregisterReceiver(smsSentBrocarsReceiver);
                                        }
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        };
                        context.registerReceiver(smsSentBrocarsReceiver, new IntentFilter(SENT));
                    }

                } catch(Exception ex) {
                    isSMSSent = "ERROR";
                    isSMSDelivered = "ERROR";
                }*/
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> messageParts = smsManager.divideMessage(smsText);
                ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>(messageParts.size());

                for (int i = 0; i < messageParts.size(); i++) {
                    Intent sentIntent = new Intent(SENT + i);
                    pendingIntents.add(PendingIntent.getBroadcast(cordova.getActivity(), 0, sentIntent, 0));
                }
                smsManager.sendMultipartTextMessage(mobileNo, null, messageParts, pendingIntents, null);

                try {
                    if(smsList.size() > 0)
                        smsList.remove(0);

                    if (smsList != null && smsList.size() > 0) {

                        sendSMS(cordova.getActivity(), smsList.get(0).getNumber(), smsList.get(0).getMessage());

                    } else if (smsList.size() <= 0) {

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("status", "success");
                        sendSuccessCallback(jsonObject, mCallbackContext);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }
        } catch(Exception ex) {
            Log.e("Error Occured in ISDKUtils::sendSMS : ",ex.toString());
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

    private void sendSuccessCallback(final JSONArray jsonObject,final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                callbackContext.success(jsonObject);
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


