package com.wizarpos.aidldemo

//import com.github.ksuid.Ksuid
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.GsonBuilder
import com.wizarpos.aidldemo.databinding.MainBinding
import com.wizarpos.aidldemo.util.AidlControl
import com.wizarpos.aidldemo.util.LogUtil
import com.wizarpos.payment.aidl.GlobalAidlRequest
import com.wizarpos.payment.aidl.IPaymentPay
import com.wizarpos.payment.aidl.IPaymentPayCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.StringWriter
import java.lang.ref.WeakReference
import kotlin.random.Random


class MainActivity : Activity() {

    companion object {

        private const val TAG = "MainActivity"
        const val AIDL_PACKAGE = "com.wizarpos.opc"
        const val AIDL_ACTION = "com.wizarpos.payment.aidl.pay"


        fun getPrettyGsonString(data:Any?):String{
            // Create a Gson instance and enable pretty-printing.
            val gson = GsonBuilder().setPrettyPrinting().create()

            // Use a StringWriter to capture the output.
            val stringWriter = StringWriter()
            gson.toJson(data,stringWriter)


            // Obtain the beautified JSON string.
            val prettyJson = stringWriter.toString()

            return prettyJson
        }
    }
    // for holding a weak reference to an Activity.
    private val activityWeakReference: WeakReference<MainActivity> = WeakReference(this)

    //霸屏模式
    private var isKioskMode = false

    private lateinit var binding: MainBinding

    private lateinit var paymentService: IPaymentPay
    private lateinit var connection: ServiceConnection
    private var isServiceBound = false


    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogUtil.d(TAG,"==>version:${App.instance.getVersionName()}==>")

        binding.btnKioskMode.setOnClickListener{
            if(isKioskMode){
                isKioskMode = false
            }else{
                isKioskMode = true
            }
//            showStatusBar(!isKioskMode)
            enableKioskMode(isKioskMode)
        }

        binding.tvResult.movementMethod = ScrollingMovementMethod.getInstance()

        binding.llRoot.setOnClickListener{
            LogUtil.d(TAG,"==>llRoot onClick==>")
            // Close the keyboard.
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.llRoot.getWindowToken(), 0);
        }

        binding.btnBind.setOnClickListener {
            showLog("bindService","===>start==>")
            val intent = Intent(AIDL_ACTION)
            intent.setPackage(AIDL_PACKAGE)
            val bindResult = bindService(intent, connection, BIND_AUTO_CREATE)
            if(!bindResult){
                showLog("bindService","connection == null")
            }else{
                showLog("bindService","connection != null")
            }
        }

        binding.btnStartTrans.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.Purchase))

        }
        binding.btnCancelTrans.setOnClickListener{
            doTrans(cancel = true)
        }

        binding.btnReversalTrans.setOnClickListener{


            paymentService?.run {
                doTrans(generateRequest( GlobalAidlRequest.Reversal))

            }
        }

        binding.btnQrSale.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.Purchase,GlobalAidlRequest.TransScheme_QR))
        }

        binding.btnPreAuth.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.PreAuth))

        }

        binding.btnAuthIncrement.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.IncrementAuth))

        }


        //Auth Complete
        binding.btnAuthComplete.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.AuthCompletion))
        }


        //Refund
        binding.btnRefund.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.Refund))
        }
        binding.btnGetPosInfo.setOnClickListener {
            doTrans(generateRequest( GlobalAidlRequest.GetPosInfo))
        }

        binding.btnQueryDeal.setOnClickListener{

            doTrans(generateRequest( GlobalAidlRequest.QueryTransaction))
        }

        binding.btnBatchClose.setOnClickListener{
            doTrans(generateRequest( GlobalAidlRequest.Settle))
        }


        binding.btnPrintlast.setOnClickListener{
            doTrans(generateRequest( GlobalAidlRequest.PrintLast))
        }

        binding.btnPrintTotal.setOnClickListener{
            doTrans(generateRequest( GlobalAidlRequest.PrintTotal))
        }

        binding.btnPrintDetail.setOnClickListener{
            doTrans(generateRequest( GlobalAidlRequest.PrintDetail))
        }

        binding.btnPrintParameter.setOnClickListener{
            doTrans(generateRequest( GlobalAidlRequest.PrintParameter))
        }

    }


    fun showLog(tag : String="",log : Any){
        var logStr = log.toString()

        LogUtil.d(tag,logStr)
        GlobalScope.launch(Dispatchers.Main) {
            binding.tvResult.append("\n [ $tag ] $log \n")
            //Scroll to the bottom of the TextView
            binding.svLog.smoothScrollTo(0,binding.tvResult.height)
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d(TAG,"==>onResume==>")

        reBindService()

        showLog("onResume", "===>lockMode start==>")
        showLog("onResume", "===>this.taskId==>${this.taskId}")
//        AidlControl.getInstance().startLockTaskMode(this,this.taskId)
    }

    override fun onDestroy() {
        super.onDestroy()

        LogUtil.d(TAG,"==>connection.isInitialized==>${::connection.isInitialized}")
        //解绑服务
        if(::connection.isInitialized) {
            LogUtil.d(TAG,"==>isServiceBound:$isServiceBound==>")
            if(isServiceBound){
                try {
                    LogUtil.d(TAG,"==>unbindService==>")
                    unbindService(connection)
                }catch (e:Exception){
                    LogUtil.d(TAG,""+e)
                }

            }
        }

    }

    private fun reBindService(){
        LogUtil.d(TAG,"==>reBindService==>")
        val isServiceExist = App.instance.isAppInstalled( AIDL_PACKAGE)

        if(!isServiceExist){
            showLog(TAG,"service $AIDL_PACKAGE does not exsit")
            return
        }
        LogUtil.d(TAG,"==>!isServiceBound:${!isServiceBound}==>")
        if(!isServiceBound){

            connection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    // safely obtain a reference to the Activity.
                    activityWeakReference.get()?.let { activity ->
                        // Here, handle the logic for successful service connection, and you can safely use the activity.
                        activity.paymentService = IPaymentPay.Stub.asInterface(service)
                        showLog("bindService", "===>onServiceConnected==>")
                        isServiceBound = true
                    }

                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    // safely obtain a reference to the Activity.
                    showLog("bindService", "===>onServiceDisconnected==>")

                }
            }
            showLog("bindService", "===>start==>")
            val intent = Intent(AIDL_ACTION)
            intent.setPackage(AIDL_PACKAGE)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun generateRequest(transTypeParam:String,transSchemaParam:String=""):GlobalAidlRequest{
        var oldRRn = ""
        if(!TextUtils.isEmpty(binding.etOldTransRrn.text)){
            oldRRn = binding.etOldTransRrn.text.toString()
        }
        var oldTrace = ""
        if(!TextUtils.isEmpty(binding.etOldTrace.text)){
            oldTrace = binding.etOldTrace.text.toString()
        }
        var oldTransIndexCode = ""
        if(!TextUtils.isEmpty(binding.etOldTransIndexCode.text)){
            oldTransIndexCode = binding.etOldTransIndexCode.text.toString()
        }
        var oldTransId = ""
        if(!TextUtils.isEmpty(binding.etOldTransId.text)){
            oldTransId = binding.etOldTransId.text.toString()
        }
        var oldInvoiceNumber = ""
        if(!TextUtils.isEmpty(binding.etOldTransInvoiceNumber.text)){
            oldInvoiceNumber = binding.etOldTransInvoiceNumber.text.toString()
        }


        return GlobalAidlRequest().apply {
            callerName = "test caller"
            transType =transTypeParam
            transScheme = transSchemaParam
            enableReceipt = true
            transAmount = binding.etTransAmount.text.toString()
            tipAmount = binding.etTipAmount.text.toString()
            otherAmount = binding.etOtherAmount.text.toString()
            oriRrn = oldRRn
            transIndexCode = Build.SERIAL + Random(System.currentTimeMillis()).nextBytes(4).toHexString()
            oriTraceNum = oldTrace
            oriTransIndexCode = oldTransIndexCode
            oriTransId = oldTransId
            oriInvoiceNum = oldInvoiceNumber
            enableReceipt = true
            skipConfirmProcedure = true
            isSkipUI = false
        }
    }


    // A variable used to record the number of times the back button is clicked
    private var doubleBackToExitPressedOnce = false


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click the back button again to exit.", Toast.LENGTH_SHORT).show()

        // Use a Handler to delay the reset of a variable to prevent the user from exiting the app by quickly clicking the back button twice consecutively.
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // 延迟时间设为2秒
    }


    protected fun hindStateBarAndNavigationBar(visiable:Boolean){
        if(visiable){
            //statusBar，navigationBar are all not display
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }else{
            //statusBar，navigationBar are all not display
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE
                   )
        }

    }

    protected fun showStatusBar(bValue:Boolean){
        //hideBars:
        val service = getSystemService("statusbar")
        val statusBarManager = Class.forName("android.app.StatusBarManager")
        val method = statusBarManager.getMethod("hideBars", Int::class.javaPrimitiveType)
        if(bValue)
            method.invoke(service, 0)
        else
            method.invoke(service, 3)

        if (bValue) {
            binding.btnKioskMode.text = getString(R.string.lock_task_mode_txt)
        }else{
            binding.btnKioskMode.text = getString(R.string.exist_lock_task_mode_txt)
        }
    }

    open fun enableKioskMode(enable: Boolean) {
        showLog(TAG,"==>enableKioskMode==>$enable")
      if(enable){
          hideSystemBars(this)
          AidlControl.getInstance().startStatusBarLocked(this,enable)
      }else{

          showSystemBars(this)
          stopLockTask()
      }


        if (enable) {
            binding.btnKioskMode.text = getString(R.string.exist_lock_task_mode_txt)
        }else{
            binding.btnKioskMode.text = getString(R.string.lock_task_mode_txt)
        }



    }

    // 在类级别添加变量
    private var isProcessing = false
    // 在ViewModel或Presenter中
    private val transactionMutex = Mutex()

    fun doTrans(request: GlobalAidlRequest?=null,cancel:Boolean=false){

        // UI层面防止点击
        if (isProcessing) return

        // 协程层面防止并发
        CoroutineScope(Dispatchers.Main).launch {
            if (!transactionMutex.tryLock()) return@launch

            isProcessing = true
            try {
                // 原有函数逻辑，改为协程方式
                withContext(Dispatchers.IO) {
                    // 实际业务逻辑
                    isProcessing = true
        LogUtil.d(TAG,"==>doTrans==>")
        LogUtil.d(TAG,"==>cancel:$cancel==>")
        LogUtil.d(TAG,"==>::paymentService.isInitialized${::paymentService.isInitialized}==>")
        try {
            if(::paymentService.isInitialized&& paymentService.asBinder()?.isBinderAlive == true){
                paymentService.run {

                    if(cancel){
                        showLog("cancelRequest","request: ${getPrettyGsonString(request)}")
                        val response = cancelRequest(getPrettyGsonString(request))
                        showLog("transact Resp", response)
                    }
                    if(null!=request) {
                        addProcedureCallback(object : IPaymentPayCallback.Stub() {
                            override fun process(processCode: Int, processMsg: String?) {
                                showLog("addProcedureCallback", "process: $processCode $processMsg")
                            }

                        })
                        GlobalScope.launch(Dispatchers.IO) {
                            showLog("transact", "request: ${getPrettyGsonString(request)}")
                            val response = paymentService.transact(getPrettyGsonString(request))
                            showLog("transact Resp", "" + response)
                            isProcessing = false // 操作完成后重置标志位
                        }
                    }

                    //Cancel the ongoing transaction after 2 seconds
//                    GlobalScope.launch (Dispatchers.IO){
//                        delay(2000)
//                        val cancelRequest = GlobalAidlRequest()
//                        val response2 = cancelRequest(getPrettyGsonString(cancelRequest))
//                        LogUtil.i(TAG, "cancelResponse2: $response2")
//                        withContext(Dispatchers.Main) {
//                            binding.tvResult.append("\n cancelResponse2:\n$response2")
//                        }
//                    }

                }

            }else{
                showLog(TAG,"paymentService does not initialize or bind")
                isProcessing = false // 操作完成后重置标志位
            }

        }catch (e :Exception){
            LogUtil.e(TAG,e.toString())
            showLog("exception", ""+e)
            isProcessing = false // 操作完成后重置标志位
        }
                }
            } catch (e: Exception) {
                // 异常处理
            } finally {
                isProcessing = false
                transactionMutex.unlock()
            }
        }

//        // 检查是否正在处理中
//        if (isProcessing) {
//            LogUtil.d(TAG, "==>doTrans==> 操作正在进行中，请勿重复点击")
//            return
//        }
//        isProcessing = true
//        LogUtil.d(TAG,"==>doTrans==>")
//        LogUtil.d(TAG,"==>cancel:$cancel==>")
//        LogUtil.d(TAG,"==>::paymentService.isInitialized${::paymentService.isInitialized}==>")
//        try {
//            if(::paymentService.isInitialized&& paymentService.asBinder()?.isBinderAlive == true){
//                paymentService.run {
//
//                    if(cancel){
//                        showLog("cancelRequest","request: ${getPrettyGsonString(request)}")
//                        val response = cancelRequest(getPrettyGsonString(request))
//                        showLog("transact Resp", response)
//                    }
//                    if(null!=request) {
//                        addProcedureCallback(object : IPaymentPayCallback.Stub() {
//                            override fun process(processCode: Int, processMsg: String?) {
//                                showLog("addProcedureCallback", "process: $processCode $processMsg")
//                            }
//
//                        })
//                        GlobalScope.launch(Dispatchers.IO) {
//                            showLog("transact", "request: ${getPrettyGsonString(request)}")
//                            val response = paymentService.transact(getPrettyGsonString(request))
//                            showLog("transact Resp", "" + response)
//                            isProcessing = false // 操作完成后重置标志位
//                        }
//                    }
//
//                    //Cancel the ongoing transaction after 2 seconds
////                    GlobalScope.launch (Dispatchers.IO){
////                        delay(2000)
////                        val cancelRequest = GlobalAidlRequest()
////                        val response2 = cancelRequest(getPrettyGsonString(cancelRequest))
////                        LogUtil.i(TAG, "cancelResponse2: $response2")
////                        withContext(Dispatchers.Main) {
////                            binding.tvResult.append("\n cancelResponse2:\n$response2")
////                        }
////                    }
//
//                }
//
//            }else{
//                showLog(TAG,"paymentService does not initialize or bind")
//                isProcessing = false // 操作完成后重置标志位
//            }
//
//        }catch (e :Exception){
//            LogUtil.e(TAG,e.toString())
//            showLog("exception", ""+e)
//            isProcessing = false // 操作完成后重置标志位
//        }
    }

    fun hideSystemBars(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView

        // Android 12+（API 31+）使用现代API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        // Android 4.1-11使用传统方法
        else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }

    fun showSystemBars(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(activity.window, activity.window.decorView).show(
                WindowInsetsCompat.Type.systemBars()
            )
        } else {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

}