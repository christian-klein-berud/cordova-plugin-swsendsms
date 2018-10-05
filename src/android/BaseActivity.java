//package co.hyperverge.hvfecdemoapp.plugins;
package cordova.plugin.swsms;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends Activity {

    final private static int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 4880;

    public static InternalPermissionListner internalPermissionListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ISDKUtils.setStatusBar(BaseActivity.this);

        Bundle activityBundle = this.getIntent().getExtras();

        if (activityBundle != null) {
            if (activityBundle.getBoolean("PERMISSION")) {
                checkIfCameraPermissionAcquired(internalPermissionListner);
                return;
            }
        }
    }

    public void checkIfCameraPermissionAcquired(InternalPermissionListner internalPermissionListner) {
        try {

            if(internalPermissionListner == null) {
                BaseActivity.this.finish();
                return;
            }

            this.internalPermissionListner = internalPermissionListner;

            if(Build.VERSION.SDK_INT >= 23) {
                requestCameraPermission(this.internalPermissionListner);
            } else {
                BaseActivity.this.finish();
                internalPermissionListner.permissionGranted();
            }
        } catch(Exception ex) {
            Log.e("BaseActivity::checkIfSTORAGEPermissionAcquired : ",ex.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkIfUserGrantedPermissions() {
        try {
            int hasReadPermissionGranted = checkSelfPermission(Manifest.permission.SEND_SMS);

            if(hasReadPermissionGranted != PackageManager.PERMISSION_GRANTED ) {
                return false;
            }
        } catch(Exception ex) {
            Log.e("BA::cIUGP : ",ex.toString());
        }

        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission(InternalPermissionListner internalPermissionListner) {
        try {
            if(internalPermissionListner == null) {

                return;
            }

            if(!checkIfUserGrantedPermissions()) {
                //if(shouldAskPermissionDialogStatus()) {
                requestPermissions(new String[] {Manifest.permission.SEND_SMS}, REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                /*} else {
                    internalPermissionListner.permissionDenied();
                }*/
            } else {
                BaseActivity.this.finish();
                internalPermissionListner.permissionGranted();
            }
        } catch(Exception ex) {
            Log.e("requestCameraPermission : ",ex.toString());
            BaseActivity.this.finish();
            internalPermissionListner.permissionDenied();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if(internalPermissionListner!=null) {
                switch (requestCode) {
                    case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                        if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            BaseActivity.this.finish();
                            this.internalPermissionListner.permissionGranted();
                        } else if(grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            //if(shouldAskPermissionDialogStatus()) {
                            showPermissionDialog("App requires access to this permission",
                                    new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //disablePermissionDialogStatus();
                                            requestPermissions(new String[] {Manifest.permission.SEND_SMS}, REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                                        }
                                    },
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            BaseActivity.this.finish();
                                            BaseActivity.this.internalPermissionListner.permissionDenied();
                                        }
                                    });
                            /*} else {
                                BaseActivity.this.internalPermissionListner.permissionDenied();
                            }*/
                        } else {
                            BaseActivity.this.finish();
                            BaseActivity.this.internalPermissionListner.permissionNotInvoked();
                        }
                        break;

                    default:
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        } catch(Exception ex) {
            Log.e("Error ::onRequestPermissionsResult : ",ex.toString());
        }
    }

    private void showPermissionDialog(String message, DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }

}

