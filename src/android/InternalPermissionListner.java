//package co.hyperverge.hvfecdemoapp.plugins;
package cordova.plugin.swsms;

/**
 * Created by snap70 on 14/6/17.
 */

public interface InternalPermissionListner {
    public void permissionGranted();
    public void permissionDenied();
    public void permissionNotInvoked();
}
