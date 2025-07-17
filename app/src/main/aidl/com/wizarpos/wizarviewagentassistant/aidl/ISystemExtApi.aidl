package com.wizarpos.wizarviewagentassistant.aidl;
// Declare any non-default types here with import statements

import com.wizarpos.wizarviewagentassistant.aidl.NetworkType;
interface ISystemExtApi{
 	/**
     * Get the preferred network type.
     * Used for device configuration by some CDMA operators.
     * <p>
     * Requires Permission:
     *   {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}
     * Or the calling app has carrier privileges. @see #hasCarrierPrivileges
     *
     * @return the preferred network type, defined in RILConstants.java.
     * @hide
     */
   int getPreferredNetworkType(int phoneId);
   /**
     * Set the preferred network type.
     * Used for device configuration by some CDMA operators.
     * <p>
     * Requires Permission:
     *   {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}
     * Or the calling app has carrier privileges. @see #hasCarrierPrivileges
     *
     * @param subId the id of the subscription to set the preferred network type for.
     * @param networkType the preferred network type, defined in RILConstants.java.
     * @return true on success; false on any failure.
     * @hide
     */
   boolean setPreferredNetworkType(int phoneId, int networkType);
   /**
     * Request to put this activity in a mode where the user is locked to a restricted set of applications.
     * <p>
     * Requires Permission:
     *	{@link android.Manifest.permission#MANAGE_ACTIVITY_STACKS}
     * @param taskid.
     * @return true on success; false on any failure.
     * @hide
     */
   boolean startLockTaskMode(int taskId);
   /**
	 * Added by Stone for task #22834 to add a interface to set screen off timeout.
	 * @param milliseconds the time you want to set, can be one of following:
	 *        15000 - 15s
	 *        30000 - 30s
	 *        60000 - 1 minute
	 *        120000 - 2 minutes
	 *        300000 - 5 minutes
	 *        600000 - 10 minutes
	 *        1800000 - 30 minutes
	 *        2147483647(Integer.MAX_VALUE) - never
	 * @return whether the new milliseconds has been set.
	 */
   boolean setScreenOffTimeout(int milliseconds);

   /**
	 * Added by Stone for task #22857 to add a interface to enable/disable mobile data.
	 * @param enable true if it should be enabled, false if it should be disabled.
	 * @return whether the new state has been set.
	 */
   boolean setMobileDataEnabled(int slot, boolean enable);

   /**
	 * Added by Stone for task #22857 to add a interface to enable/disable mobile data roaming.
	 * @param roaming 1 if it should be enabled, 0 if it should be disabled.
	 * @return whether the new state has been set.
	 */
   boolean setMobileDataRoamingEnabled(int slot, int roaming);

    /**
	 * @param enable: true means enable counter mode, false means disable counter mode.
     * @return true on success; false on any failure.
	 */
   boolean setBatteryCounterMode(boolean enable);

   /**
    * get supported network type array
    * NetworkMode: name, modeId;
    * @return network type array.
    * */
    NetworkType[] getSupportedNetworkTypes();
    /**
     * @param touch:Add wake on touch；none: Only the power button wakes up
     * @return return result for success or failed!.
     * */
    boolean setTouchScreenWakeupValue(String value);
    /**
     * @return return touch screen wakeup value. touch:Add wake on touch；none: Only the power button wakes up
     * */
    String getTouchScreenWakeupValue();
    /**
     * enable/disable auto timezone item.
     * @param enable true: enable; false: disable
     * */
    void enableAutoTimezone(boolean enable);
    /**
     * get status for enable/disable auto timezone item.
     * @return enable true: enable; false: disable
     * */
    boolean isEnableAutoTimezone();
    /**
     * enable/disable show auto timezone item GUI.
     * @param enable true: enable; false: disable
     * */
    void enableAutoTimezoneGUI(boolean enable);
    /**
     * get status for enable/disable auto timezone item GUI.
     * @return enable true: enable; false: disable
     * */
    boolean isEnableAutoTimezoneGUI();
    /**
     * enable/disable auto time item.
     * @param enable true: enable; false: disable
     * */
    void enableAutoTime(boolean enable);
    /**
     * get status for enable/disable auto time item.
     * @return enable true: enable; false: disable
     * */
    boolean isEnableAutoTime();
    /**
     * enable/disable show auto time item GUI.
     * @param enable true: enable; false: disable
     * */
    void enableAutoTimeGUI(boolean enable);
    /**
     * get status for enable/disable auto time item GUI.
     * @return enable true: enable; false: disable
     * */
    boolean isEnableAutoTimeGUI();

    /**
     * @param pkg: package name.
     * @param deviceCls: policyReceiver's class name.
     * @return active success for true ; active failed for false
     * */
    boolean setDeviceOwner(String pkg, String deviceCls);

    /**
     *  Set "persist.wp.usr.${key}"'s property , max count = 10.
     * Permission： android.permission.CLOUDPOS_SET_USR_PROP
     * @param key: property's key, length less than 16. for example: persist.wp.usr.${key} ${value}.
     * @param value: property's value, length less than 32.
     * @return set success for true ; set failed for false
     * */
    boolean setUsrProp(String key, String value);

    /**
     * Enabling ‘show touches’ in Android screen recordings for user research.
     * @param enbale: true, false;
     *
     **/
    void enableShowTouches(boolean enable);
    /**
     * get ‘show touches’ state for enable or disable.
     * @return ture: enable ‘show touches’; false: disable ‘show touches’.
     **/
    boolean getShowTouchesState();
    /**
     * Set the status bar locked as true will make the status bar can not be pull down.
     * @permission: android.permission.CLOUDPOS_LOCK_STATUS_BAR
     * @param lock, true for lock, false for disable lock.
     **/
    void setStatusBarLocked(boolean lock);
    /**
     * Block or Release The Power Key
     * @param enbale: true:Block The Power Key ; false:Release The Power Key ;
     * @permission: android.permission.CLOUDPOS_DISABLE_POWER_KEY
     */
    void setPowerKeyBlocked(boolean enable);

    /**
     * check PowerKey Button 's block status.
     * @return true : blocked the power key; false: not block the power key.
     */
    boolean isPowerKeyBlocked();
    /**
     * Enable/Disable MTP
     * @param enbale: true:Enable MTP ; false:Disable MTP;
     */
     void enableMtp(boolean enable);
    /**
     * Get MTP Status
     * @return true:Enable MTP ; false:Disable MTP;
     */
    boolean getMtpStatus();

    /**
     * set Language
     * @link https://developer.android.com/reference/java/util/Locale
     * @param language: (Null is not allowed)An ISO 639 alpha-2 or alpha-3 language code, or a language subtag up to 8 characters in length. See the Locale class description about valid language values.
     * @param country : (Null is allowed)An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code. See the Locale class description about valid country values.
     * @param variant : (Null is allowed)Any arbitrary value used to indicate a variation of a Locale. See the Locale class description for the details.
     * <p>for example: Locale(String language, String country, String variant) or Locale(String language, String country) or Locale(String language)</p>
     * @return true:set success ; false: set failed;
     *
     */
    boolean setLanguage(String language, String country, String variant);
    /**
     * enable/disable Airplane Mode
     * @param true: alirplane mode on ; false: alirplane mode off.
     */
    void enableAirplaneMode(boolean enable);

    /**
      * visibility/hide statusbar settings button
      * @param true: statusbar settings button show ; false: statusbar settings button hide.
      */
    void setStatusbarSettingsButtonVisibility(boolean visibility);

    /**
      * get statusbar settings button status.
      * @return true: statusbar settings button visibility ; false: statusbar settings button hide.
      */
    boolean getStatusbarSettingsButtonVisibility();

    /**
      * Enable packageName the permission to install apps from unknown sources.
      * @permission: android.permission.CLOUDPOS_SET_UNKOWN_SOURCE
      * @return true: set success ; false: set failed, packageName not found.
      */
    boolean enableUnkownSourceApp(String packageName, boolean enable);
    /**
	 * @param enable: true means enable auto counter mode, false means disable auto counter mode.
     * @return true on success; false on any failure.
	 */
    boolean setAutoBatteryControlMode(boolean enable);

    /**
     * Pair with Bluetooth device
     * @param mac: bluetooth's device mac address.
     * @param pin: Password for Bluetooth paired device, allow null.
     * @permission "android.permission.BLUETOOTH"
     * @return true: pair success ; false: pair failed, device not found or password is wrong.
     * */
    boolean pairBTDevice(String mac, String pin);

    /**
     * cancel Paired Bluetooth device
     * @param mac: bluetooth's device mac address.
     * @permission "android.permission.BLUETOOTH"
     * @return true: pair success ; false: pair failed, device not found or password is wrong.
     * */
    boolean cancelPairedBluetoothDevice(String mac);

    /**
     * Enable/Disable LockScreen function.
     * @param true is enable lockscreen function; false is disable lockscreen function.
     * @return true is success, false is fail.
     * */
    boolean enableLockScreen(boolean enable);

    /**
      *  Remove wifi by ssid
      *  @param ssid : wifi's ssid.
      *  @return true is success, false is fail.
      ***/
    boolean removeWifiSSID(String ssid);

   /**
    * Sets a password to unlock the device.
    * @param password the password to set, must be at least 4 characters and may contain letters and numbers.
    * @throws InvalidLockConfigurationException if the password does not meet the length requirements,
    *         or if there are issues with system configuration or credentials.
    * @return true is success, false is fail.
    * */
    boolean setPasswordLock(boolean enable, String passwor);

    /**
    * Sets a numeric PIN to unlock the device.
    * @param pin the PIN to set, must be at least 4 digits (numeric only).
    * @throws InvalidLockConfigurationException if the PIN does not meet the length or digit requirements,
    *         or if there are issues with system configuration or credentials.
    * @return true is success, false is fail.
    * */
    boolean setPinLock(boolean enable, String pin);

    /**
     * Sets a pattern to unlock the device.
     * The pattern must consist of 4 to 16 connected points in a 3x3 grid, corresponding to the following grid:
     * <pre>
     * 1    2   3
     * 4    5   6
     * 7    8   9
     * </pre>
     * <p>Each point in the pattern must be unique and cannot be repeated.</p>
     * <p>Example: To set an L-shaped pattern, use the array: {1, 4, 7, 8, 9}.</p>
     *
     * <p><b>Special Cases:</b></p>
     * <ul>
     *     <li><b>Example 1:</b> The pattern "159637" is valid because:
     *             <li>1 → 5 is valid (direct connection).</li>
     *             <li>5 → 9 is valid (direct connection).</li>
     *             <li>9 → 6 is valid (direct connection).</li>
     *             <li>6 → 3 is valid because point 5 is already connected, allowing the jump over it.</li>
     *             <li>3 → 7 is valid because point 5 is already connected, allowing the jump over it.</li>
     *     </li>
     *     <li><b>Example 2:</b> The pattern "135" is invalid because:
     *             <li>1 → 3 is invalid, as point 2 is skipped and has not been connected yet.</li>
     *     </li>
     *     <li><b>Example 3:</b> The pattern "1159" is invalid because:
     *             <li>Point 1 is used twice, but each point in the pattern must be unique.</li>
     *     </li>
     * </ul>
     *
     * @param enable a boolean indicating whether to enable or disable the pattern lock.
     * @param str a string representing the pattern, where each character is a digit between 1 and 9.
     *        The pattern must be 4 to 16 digits long.
     * @return true is success, false is fail.
     * @throws InvalidLockConfigurationException if the pattern is shorter than 4 digits, longer than 16 digits,
     *         or if the pattern is invalid (e.g., jumping over unconnected points or repeating points).
     */
    boolean setPatternLock(boolean enable, String str);

    /**
     * Enable or Disable the bluetooth device
     * @param enable a boolean indicating whether to enable or disable the bluetooth device.
     * @return true is success, false is fail.
     * @permission "android.permission.BLUETOOTH"
     * */
       boolean setBluetooth(boolean enable) ;
    /**
     * Get the current Bluetooth status (enabled or disabled).
     * @return enable true: enable; false: disable
     * */
       boolean isEnableBluetooth() ;
    /**
     * Enable or Disable the Wi-Fi device
     * @param enable a boolean indicating whether to enable or disable the Wi-Fi device.
     * @return true is success, false is fail.
     * */
       boolean setWifi(boolean enable) ;
    /**
     * Get the current Wi-Fi status (enabled or disabled).
     * @return enable true: enable; false: disable
     * @permission "android.permission.WIFI"
     * */
       boolean isEnableWifi() ;

    /**
    * Enable or Disable the USB device
    * @param enable a boolean indicating whether to enable or disable the USB device.
    * @return true is success, false is fail.
    * */
    boolean setUsb(boolean enable);
    /**
     * Get the current USB status (enabled or disabled).
     * @return enable true: enable; false: disable
     * @permission "android.permission.USB"
     * */
    boolean isEnableUsb() ;
    /**
     * Enable or Disable the USB device
     * @param enable a boolean indicating whether to enable or disable the USB device.
     * @return true is success, false is fail.
     * @permission "android.permission.CAMERA"
     * */
    boolean setCamera(boolean enable);
    /**
     * Get the current Camera status (enabled or disabled).
     * @return enable true: enable; false: disable
     * @permission "android.permission.CAMERA"
     * */
    boolean isEnableCamera();
    /**
    * Enable or Disable the ExtMedia device
    * @param enable a boolean indicating whether to enable or disable the ExtMedia device.
    * @return true is success, false is fail.
    * */
    boolean setExtMedia(boolean enable);
    /**
     * Get the current ExtMedia status (enabled or disabled).
     * @return enable true: enable; false: disable
     * @permission "android.permission.EXTMEDIA"
     * */
    boolean isEnableExtMedia() ;
}