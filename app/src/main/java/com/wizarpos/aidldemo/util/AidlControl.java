package com.wizarpos.aidldemo.util;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.constraintlayout.widget.Placeholder;

import com.wizarpos.wizarviewagentassistant.aidl.ISystemExtApi;


/**
 * create by rf.w 19-4-25下午1:59
 */
public class AidlControl {

    private AidlControl() {
    }

    private static AidlControl instance;

    public static AidlControl getInstance() {
        if (instance == null) {
            instance = new AidlControl();
        }
        return instance;
    }

    public void startStatusBarLocked(final Context context, final boolean show) {
        ServiceConnection systemExtApiConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ISystemExtApi systemExtApiService = ISystemExtApi.Stub.asInterface(service);
                try {
                    systemExtApiService.setStatusBarLocked(show);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    context.unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        boolean success = AidlMgr.getInstance().startSystemExtApiService(context, systemExtApiConn);
    }

    public void startLockTaskMode(final Context context, final int taskID) {
//        String KIOSK_PACKAGE  = "com.smartpos.paywizard.tsys.smartpay";
//        String[] APP_PACKAGES = {KIOSK_PACKAGE};
//        DevicePolicyManager dmp = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        ComponentName componentName = ((Activity)context).getComponentName();
//        dmp.setLockTaskPackages(componentName,APP_PACKAGES);

        ServiceConnection systemExtApiConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ISystemExtApi systemExtApiService = ISystemExtApi.Stub.asInterface(service);
                try {
                    systemExtApiService.startLockTaskMode(taskID);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    context.unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        boolean success = AidlMgr.getInstance().startSystemExtApiService(context, systemExtApiConn);
    }
}
