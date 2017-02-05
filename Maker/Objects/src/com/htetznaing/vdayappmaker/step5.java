package com.htetznaing.vdayappmaker;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class step5 extends Activity implements B4AActivity{
	public static step5 mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.step5");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (step5).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.step5");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.vdayappmaker.step5", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (step5) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (step5) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return step5.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (step5) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (step5) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _t = null;
public static anywheresoftware.b4a.objects.Timer _ad = null;
public com.AB.ABZipUnzip.ABZipUnzip _zip = null;
public nnl.zipsigner.NNLZipSigner _zs = null;
public static String _sd = "";
public anywheresoftware.b4a.objects.LabelWrapper _lb1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b = null;
public MLfiles.Fileslib.MLfiles _ml = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public anywheresoftware.b4a.phone.Phone _p = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _iv1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _sms = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbf = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _bbg = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public com.htetznaing.vdayappmaker.main _main = null;
public com.htetznaing.vdayappmaker.step1 _step1 = null;
public com.htetznaing.vdayappmaker.step2 _step2 = null;
public com.htetznaing.vdayappmaker.step3 _step3 = null;
public com.htetznaing.vdayappmaker.step4 _step4 = null;
public com.htetznaing.vdayappmaker.preview _preview = null;
public com.htetznaing.vdayappmaker.about _about = null;
public com.htetznaing.vdayappmaker.starter _starter = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 34;BA.debugLine="bbg.Initialize(Colors.Black,15)";
mostCurrent._bbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Black,(int) (15));
 //BA.debugLineNum = 35;BA.debugLine="p.SetScreenOrientation(1)";
mostCurrent._p.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 36;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/8416044952");
 //BA.debugLineNum = 37;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 38;BA.debugLine="Activity.AddView(Banner,0%x,100%y - 50dip,100%x,5";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 40;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/9892778151");
 //BA.debugLineNum = 41;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 43;BA.debugLine="ad.Initialize(\"ad\",60000)";
_ad.Initialize(processBA,"ad",(long) (60000));
 //BA.debugLineNum = 45;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 46;BA.debugLine="sd =  File.DirRootExternal & \"/\"";
mostCurrent._sd = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/";
 //BA.debugLineNum = 48;BA.debugLine="Activity.Color = Colors.RGB(33,150,243)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (33),(int) (150),(int) (243)));
 //BA.debugLineNum = 50;BA.debugLine="Activity.LoadLayout(\"FinalStep\")";
mostCurrent._activity.LoadLayout("FinalStep",mostCurrent.activityBA);
 //BA.debugLineNum = 52;BA.debugLine="lb1.Textsize = 30";
mostCurrent._lb1.setTextSize((float) (30));
 //BA.debugLineNum = 53;BA.debugLine="lb1.TextColor = Colors.Black";
mostCurrent._lb1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 54;BA.debugLine="lb1.Typeface = Typeface.DEFAULT_BOLD";
mostCurrent._lb1.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 55;BA.debugLine="lb1.Gravity = Gravity.CENTER";
mostCurrent._lb1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 56;BA.debugLine="lb2.Text = \"Build Your Android App Now!\"";
mostCurrent._lb2.setText((Object)("Build Your Android App Now!"));
 //BA.debugLineNum = 57;BA.debugLine="lb2.TextSize = 20";
mostCurrent._lb2.setTextSize((float) (20));
 //BA.debugLineNum = 58;BA.debugLine="lb2.TextColor = Colors.White";
mostCurrent._lb2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 59;BA.debugLine="lb2.Gravity = Gravity.CENTER";
mostCurrent._lb2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 60;BA.debugLine="T.Initialize(\"T\",1000)";
_t.Initialize(processBA,"T",(long) (1000));
 //BA.debugLineNum = 61;BA.debugLine="T.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 63;BA.debugLine="lbf.Initialize(\"lbf\")";
mostCurrent._lbf.Initialize(mostCurrent.activityBA,"lbf");
 //BA.debugLineNum = 64;BA.debugLine="lbf.Text = \"Developed By Khun Htetz Naing\"";
mostCurrent._lbf.setText((Object)("Developed By Khun Htetz Naing"));
 //BA.debugLineNum = 65;BA.debugLine="Activity.AddView(lbf,0%x,85%y,100%x,5%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbf.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (85),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 66;BA.debugLine="lbf.Gravity = Gravity.CENTER";
mostCurrent._lbf.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 67;BA.debugLine="lbf.TextColor = Colors.White";
mostCurrent._lbf.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 69;BA.debugLine="sms.Color = Colors.White";
mostCurrent._sms.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 70;BA.debugLine="sms.Hint = \"Enter Your Message\"";
mostCurrent._sms.setHint("Enter Your Message");
 //BA.debugLineNum = 71;BA.debugLine="sms.TextColor = Colors.Black";
mostCurrent._sms.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 72;BA.debugLine="b.Background = bbg";
mostCurrent._b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 73;BA.debugLine="b1.Background= bbg";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _ad_tick() throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub ad_Tick";
 //BA.debugLineNum = 128;BA.debugLine="If Interstitial.Ready Then Interstitial.Show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 129;BA.debugLine="End Sub";
return "";
}
public static String  _b_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub b_Click";
 //BA.debugLineNum = 85;BA.debugLine="If sms.Text = \"\" Then";
if ((mostCurrent._sms.getText()).equals("")) { 
 }else {
 //BA.debugLineNum = 87;BA.debugLine="File.WriteString(File.DirRootExternal & \"/.vDayA";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","message.txt",mostCurrent._sms.getText());
 };
 //BA.debugLineNum = 89;BA.debugLine="ProgressDialogShow(\"Building Your Apk!\" & CRLF &";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Building Your Apk!"+anywheresoftware.b4a.keywords.Common.CRLF+"Please Wait...");
 //BA.debugLineNum = 90;BA.debugLine="T.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 91;BA.debugLine="zs.Initialize";
mostCurrent._zs.Initialize(processBA);
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub b1_Click";
 //BA.debugLineNum = 77;BA.debugLine="If sms.Text = \"\" Then";
if ((mostCurrent._sms.getText()).equals("")) { 
 }else {
 //BA.debugLineNum = 79;BA.debugLine="File.WriteString(File.DirRootExternal & \"/.vDayA";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","message.txt",mostCurrent._sms.getText());
 };
 //BA.debugLineNum = 81;BA.debugLine="StartActivity(Preview)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._preview.getObject()));
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim zip As ABZipUnzip";
mostCurrent._zip = new com.AB.ABZipUnzip.ABZipUnzip();
 //BA.debugLineNum = 17;BA.debugLine="Dim zs As NNLZipSigner";
mostCurrent._zs = new nnl.zipsigner.NNLZipSigner();
 //BA.debugLineNum = 18;BA.debugLine="Dim sd As String";
mostCurrent._sd = "";
 //BA.debugLineNum = 19;BA.debugLine="Private lb1 As Label";
mostCurrent._lb1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private lb2 As Label";
mostCurrent._lb2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private b As Button";
mostCurrent._b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim ml As MLfiles";
mostCurrent._ml = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 23;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 26;BA.debugLine="Dim iv1 As ImageView";
mostCurrent._iv1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private sms As EditText";
mostCurrent._sms = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim lbf As Label";
mostCurrent._lbf = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim bbg As ColorDrawable";
mostCurrent._bbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 30;BA.debugLine="Private b1 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim T As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 10;BA.debugLine="Dim ad As Timer";
_ad = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _t_tick() throws Exception{
String _nam = "";
anywheresoftware.b4a.objects.IntentWrapper _in = null;
 //BA.debugLineNum = 94;BA.debugLine="Sub T_Tick";
 //BA.debugLineNum = 95;BA.debugLine="Dim nam As String";
_nam = "";
 //BA.debugLineNum = 96;BA.debugLine="nam =File.ReadString(File.DirRootExternal,\"Ht3tzN";
_nam = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Ht3tzN4ing");
 //BA.debugLineNum = 98;BA.debugLine="zip.ABZipDirectory(sd & \".vDayAppMaker\" , sd & \"v";
mostCurrent._zip.ABZipDirectory(mostCurrent._sd+".vDayAppMaker",mostCurrent._sd+"vDayApp.apk");
 //BA.debugLineNum = 99;BA.debugLine="zs.SignZip(sd & \"vDayApp.apk\" , sd & nam&\"_vDayAp";
mostCurrent._zs.SignZip(mostCurrent._sd+"vDayApp.apk",mostCurrent._sd+_nam+"_vDayApp.apk");
 //BA.debugLineNum = 101;BA.debugLine="ml.rm(sd & \"vDayApp.apk\") '---------------  sdcar";
mostCurrent._ml.rm(mostCurrent._sd+"vDayApp.apk");
 //BA.debugLineNum = 103;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 104;BA.debugLine="ToastMessageShow (\"Successfully Created \" & CRLF";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Successfully Created "+anywheresoftware.b4a.keywords.Common.CRLF+_nam+"_vDayApp.apk in SdCard!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 105;BA.debugLine="T.Enabled = False";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 106;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 107;BA.debugLine="in.Initialize(in.ACTION_VIEW,\"file:///\"&sd &  nam";
_in.Initialize(_in.ACTION_VIEW,"file:///"+mostCurrent._sd+_nam+"_vDayApp.apk");
 //BA.debugLineNum = 108;BA.debugLine="in.SetType(\"application/vnd.android.package-archiv";
_in.SetType("application/vnd.android.package-archive");
 //BA.debugLineNum = 109;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 110;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_in.getObject()));
 }else {
 //BA.debugLineNum = 112;BA.debugLine="Msgbox(\"Please Restart Project\",\"Attention!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Please Restart Project","Attention!",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 115;BA.debugLine="File.Delete(File.DirRootExternal,\"Ht3tzN4ing\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Ht3tzN4ing");
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
}
