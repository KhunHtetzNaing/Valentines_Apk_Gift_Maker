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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.vdayappmaker.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (main) Resume **");
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
public static anywheresoftware.b4a.objects.Timer _ad = null;
public static String _sd = "";
public com.AB.ABZipUnzip.ABZipUnzip _zip = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imv = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _ibg = null;
public anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb = null;
public com.htoophyoe.anitext.animatetext _anit = null;
public anywheresoftware.b4a.objects.PanelWrapper _bar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _bbg = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public anywheresoftware.b4a.phone.Phone _p = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bd = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbf = null;
public com.htetznaing.vdayappmaker.step1 _step1 = null;
public com.htetznaing.vdayappmaker.step2 _step2 = null;
public com.htetznaing.vdayappmaker.step3 _step3 = null;
public com.htetznaing.vdayappmaker.step4 _step4 = null;
public com.htetznaing.vdayappmaker.step5 _step5 = null;
public com.htetznaing.vdayappmaker.preview _preview = null;
public com.htetznaing.vdayappmaker.about _about = null;
public com.htetznaing.vdayappmaker.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (step1.mostCurrent != null);
vis = vis | (step2.mostCurrent != null);
vis = vis | (step3.mostCurrent != null);
vis = vis | (step4.mostCurrent != null);
vis = vis | (step5.mostCurrent != null);
vis = vis | (preview.mostCurrent != null);
vis = vis | (about.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 43;BA.debugLine="p.SetScreenOrientation(1)";
mostCurrent._p.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 44;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/8416044952");
 //BA.debugLineNum = 45;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 46;BA.debugLine="Activity.AddView(Banner,0%x,100%y - 50dip,100%x,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 48;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/9892778151");
 //BA.debugLineNum = 49;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 51;BA.debugLine="ad.Initialize(\"ad\",60000)";
_ad.Initialize(processBA,"ad",(long) (60000));
 //BA.debugLineNum = 52;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 55;BA.debugLine="l1.Initialize(\"l1\")";
mostCurrent._l1.Initialize(mostCurrent.activityBA,"l1");
 //BA.debugLineNum = 56;BA.debugLine="l1.Text = \"vDay App Maker\"";
mostCurrent._l1.setText((Object)("vDay App Maker"));
 //BA.debugLineNum = 57;BA.debugLine="Activity.AddView(l1,5%x,3%y,90%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._l1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (3),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 58;BA.debugLine="l1.Gravity = Gravity.CENTER";
mostCurrent._l1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 59;BA.debugLine="l1.TextSize = 30";
mostCurrent._l1.setTextSize((float) (30));
 //BA.debugLineNum = 60;BA.debugLine="l1.TextColor = Colors.Black";
mostCurrent._l1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 //BA.debugLineNum = 61;BA.debugLine="l1.Typeface = Typeface.DEFAULT_BOLD";
mostCurrent._l1.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.DEFAULT_BOLD);
 //BA.debugLineNum = 62;BA.debugLine="bar.Initialize(\"bar\")";
mostCurrent._bar.Initialize(mostCurrent.activityBA,"bar");
 //BA.debugLineNum = 63;BA.debugLine="bar.Color = Colors.White";
mostCurrent._bar.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 64;BA.debugLine="Activity.AddView(bar,5%x,(l1.Top+l1.Height),90%x,0";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bar.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) ((mostCurrent._l1.getTop()+mostCurrent._l1.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0.5),mostCurrent.activityBA));
 //BA.debugLineNum = 66;BA.debugLine="lb.Initialize(\"\")";
mostCurrent._lb.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 67;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._lb.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 68;BA.debugLine="lb.Textsize =  20";
mostCurrent._lb.setTextSize((float) (20));
 //BA.debugLineNum = 69;BA.debugLine="lb.TextColor = Colors.White";
mostCurrent._lb.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 70;BA.debugLine="anit.initialize(\"\",Me,300)";
mostCurrent._anit._initialize(mostCurrent.activityBA,"",main.getObject(),(int) (300));
 //BA.debugLineNum = 71;BA.debugLine="anit.Run(\"Make Easily your Own\" &CRLF & \"Valentin";
mostCurrent._anit._run("Make Easily your Own"+anywheresoftware.b4a.keywords.Common.CRLF+"Valentine Gift for"+anywheresoftware.b4a.keywords.Common.CRLF+"Your Dear!",(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lb.getObject())));
 //BA.debugLineNum = 72;BA.debugLine="anit.Endable = True";
mostCurrent._anit._endable = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 73;BA.debugLine="Activity.AddView(lb,10%x,(bar.Top+bar.Height),80%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),(int) ((mostCurrent._bar.getTop()+mostCurrent._bar.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (15),mostCurrent.activityBA));
 //BA.debugLineNum = 75;BA.debugLine="Activity.Color = Colors.RGB(33,150,243)";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (33),(int) (150),(int) (243)));
 //BA.debugLineNum = 76;BA.debugLine="ibg.Initialize(LoadBitmap(File.DirAssets,\"imv.png\"";
mostCurrent._ibg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"imv.png").getObject()));
 //BA.debugLineNum = 77;BA.debugLine="imv.Initialize(\"imv\")";
mostCurrent._imv.Initialize(mostCurrent.activityBA,"imv");
 //BA.debugLineNum = 78;BA.debugLine="imv.Background = ibg";
mostCurrent._imv.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ibg.getObject()));
 //BA.debugLineNum = 79;BA.debugLine="Activity.AddView(imv,20%x,(lb.Top+lb.Height),200d";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._imv.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._lb.getTop()+mostCurrent._lb.getHeight())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (200)));
 //BA.debugLineNum = 81;BA.debugLine="b.Initialize(\"b\")";
mostCurrent._b.Initialize(mostCurrent.activityBA,"b");
 //BA.debugLineNum = 82;BA.debugLine="b.Text = \">> Start >>\"";
mostCurrent._b.setText((Object)(">> Start >>"));
 //BA.debugLineNum = 83;BA.debugLine="bbg.Initialize(Colors.Black,15)";
mostCurrent._bbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Black,(int) (15));
 //BA.debugLineNum = 84;BA.debugLine="b.Background = bbg";
mostCurrent._b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 85;BA.debugLine="Activity.AddView(b,50%x,(imv.Top+imv.Height)+1%y,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA),(int) ((mostCurrent._imv.getTop()+mostCurrent._imv.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (45),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 88;BA.debugLine="lbf.Initialize(\"lbf\")";
mostCurrent._lbf.Initialize(mostCurrent.activityBA,"lbf");
 //BA.debugLineNum = 89;BA.debugLine="lbf.Text = \"Developed By Khun Htetz Naing\"";
mostCurrent._lbf.setText((Object)("Developed By Khun Htetz Naing"));
 //BA.debugLineNum = 90;BA.debugLine="Activity.AddView(lbf,0%x,(b.Top+b.Height),100%x,5";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lbf.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((mostCurrent._b.getTop()+mostCurrent._b.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 91;BA.debugLine="lbf.Gravity = Gravity.CENTER";
mostCurrent._lbf.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 92;BA.debugLine="lbf.TextColor = Colors.White";
mostCurrent._lbf.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 94;BA.debugLine="If File.Exists(File.DirRootExternal & \"/.vDayAppM";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker","AndroidManifest.xml")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 95;BA.debugLine="File.Copy(File.DirAssets,\"vday.apk\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vday.apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"vDay.zip");
 //BA.debugLineNum = 96;BA.debugLine="zip.ABUnzip(sd & \"vDay.zip\", sd & \".vDayAppMaker";
mostCurrent._zip.ABUnzip(mostCurrent._sd+"vDay.zip",mostCurrent._sd+".vDayAppMaker");
 //BA.debugLineNum = 97;BA.debugLine="File.Delete(sd,\"vDay.zip\")";
anywheresoftware.b4a.keywords.Common.File.Delete(mostCurrent._sd,"vDay.zip");
 //BA.debugLineNum = 98;BA.debugLine="File.Copy(File.DirAssets,\"love.ttf\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"love.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","love.ttf");
 //BA.debugLineNum = 99;BA.debugLine="File.Copy(File.DirAssets,\"beikthano.ttf\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"beikthano.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","beikthano.ttf");
 //BA.debugLineNum = 100;BA.debugLine="File.Copy(File.DirAssets,\"chococooky.ttf\",File.D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chococooky.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","chococooky.ttf");
 //BA.debugLineNum = 101;BA.debugLine="File.Copy(File.DirAssets,\"flower.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flower.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","flower.ttf");
 //BA.debugLineNum = 102;BA.debugLine="File.Copy(File.DirAssets,\"matrix.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"matrix.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","matrix.ttf");
 //BA.debugLineNum = 103;BA.debugLine="File.Copy(File.DirAssets,\"yoeyar.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"yoeyar.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","yoeyar.ttf");
 //BA.debugLineNum = 104;BA.debugLine="File.Copy(File.DirAssets,\"metrix smart.ttf\",File";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"metrix smart.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","metrix smart.ttf");
 }else {
 //BA.debugLineNum = 106;BA.debugLine="bd.Initialize(\"bd\")";
mostCurrent._bd.Initialize(mostCurrent.activityBA,"bd");
 //BA.debugLineNum = 107;BA.debugLine="bd.Text =\"Delete Current Project!\"";
mostCurrent._bd.setText((Object)("Delete Current Project!"));
 //BA.debugLineNum = 108;BA.debugLine="Activity.AddView(bd,5%x,(lbf.Top+lbf.Height),60%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bd.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) ((mostCurrent._lbf.getTop()+mostCurrent._lbf.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 109;BA.debugLine="bd.Background = bbg";
mostCurrent._bd.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 };
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 159;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 140;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 141;BA.debugLine="If File.Exists(File.DirRootExternal & \"/.vDayAppM";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker","AndroidManifest.xml")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 142;BA.debugLine="File.Copy(File.DirAssets,\"vday.apk\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vday.apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"vDay.zip");
 //BA.debugLineNum = 143;BA.debugLine="zip.ABUnzip(sd & \"vDay.zip\", sd & \".vDayAppMaker";
mostCurrent._zip.ABUnzip(mostCurrent._sd+"vDay.zip",mostCurrent._sd+".vDayAppMaker");
 //BA.debugLineNum = 144;BA.debugLine="File.Delete(sd,\"vDay.zip\")";
anywheresoftware.b4a.keywords.Common.File.Delete(mostCurrent._sd,"vDay.zip");
 //BA.debugLineNum = 145;BA.debugLine="File.Copy(File.DirAssets,\"love.ttf\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"love.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","love.ttf");
 //BA.debugLineNum = 146;BA.debugLine="File.Copy(File.DirAssets,\"beikthano.ttf\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"beikthano.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","beikthano.ttf");
 //BA.debugLineNum = 147;BA.debugLine="File.Copy(File.DirAssets,\"chococooky.ttf\",File.D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chococooky.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","chococooky.ttf");
 //BA.debugLineNum = 148;BA.debugLine="File.Copy(File.DirAssets,\"flower.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flower.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","flower.ttf");
 //BA.debugLineNum = 149;BA.debugLine="File.Copy(File.DirAssets,\"matrix.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"matrix.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","matrix.ttf");
 //BA.debugLineNum = 150;BA.debugLine="File.Copy(File.DirAssets,\"yoeyar.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"yoeyar.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","yoeyar.ttf");
 //BA.debugLineNum = 151;BA.debugLine="File.Copy(File.DirAssets,\"metrix smart.ttf\",File";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"metrix smart.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","metrix smart.ttf");
 }else {
 //BA.debugLineNum = 153;BA.debugLine="bd.RemoveView";
mostCurrent._bd.RemoveView();
 //BA.debugLineNum = 154;BA.debugLine="Activity.AddView(bd,5%x,(lbf.Top+lbf.Height),60%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bd.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) ((mostCurrent._lbf.getTop()+mostCurrent._lbf.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 155;BA.debugLine="bd.Background = bbg";
mostCurrent._bd.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 };
 //BA.debugLineNum = 157;BA.debugLine="End Sub";
return "";
}
public static String  _ad_tick() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub ad_Tick";
 //BA.debugLineNum = 164;BA.debugLine="If Interstitial.Ready Then Interstitial.Show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _b_click() throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Sub b_Click";
 //BA.debugLineNum = 137;BA.debugLine="StartActivity(Step1)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._step1.getObject()));
 //BA.debugLineNum = 138;BA.debugLine="End Sub";
return "";
}
public static String  _bd_click() throws Exception{
MLfiles.Fileslib.MLfiles _ml = null;
 //BA.debugLineNum = 114;BA.debugLine="Sub bd_Click";
 //BA.debugLineNum = 115;BA.debugLine="Dim ml As MLfiles";
_ml = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 116;BA.debugLine="If ml.rmrf(File.DirRootExternal & \"/.vDayAppMaker";
if (_ml.rmrf(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 117;BA.debugLine="ToastMessageShow(\"Deleted!!!\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Deleted!!!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 118;BA.debugLine="bd.RemoveView";
mostCurrent._bd.RemoveView();
 //BA.debugLineNum = 119;BA.debugLine="File.Copy(File.DirAssets,\"vday.apk\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"vday.apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"vDay.zip");
 //BA.debugLineNum = 120;BA.debugLine="zip.ABUnzip(sd & \"vDay.zip\", sd & \".vDayAppMaker";
mostCurrent._zip.ABUnzip(mostCurrent._sd+"vDay.zip",mostCurrent._sd+".vDayAppMaker");
 //BA.debugLineNum = 121;BA.debugLine="File.Delete(sd,\"vDay.zip\")";
anywheresoftware.b4a.keywords.Common.File.Delete(mostCurrent._sd,"vDay.zip");
 //BA.debugLineNum = 122;BA.debugLine="File.Copy(File.DirAssets,\"love.ttf\",File.DirRoot";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"love.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","love.ttf");
 //BA.debugLineNum = 123;BA.debugLine="File.Copy(File.DirAssets,\"beikthano.ttf\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"beikthano.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","beikthano.ttf");
 //BA.debugLineNum = 124;BA.debugLine="File.Copy(File.DirAssets,\"chococooky.ttf\",File.D";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"chococooky.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","chococooky.ttf");
 //BA.debugLineNum = 125;BA.debugLine="File.Copy(File.DirAssets,\"flower.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"flower.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","flower.ttf");
 //BA.debugLineNum = 126;BA.debugLine="File.Copy(File.DirAssets,\"matrix.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"matrix.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","matrix.ttf");
 //BA.debugLineNum = 127;BA.debugLine="File.Copy(File.DirAssets,\"yoeyar.ttf\",File.DirRo";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"yoeyar.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","yoeyar.ttf");
 //BA.debugLineNum = 128;BA.debugLine="File.Copy(File.DirAssets,\"metrix smart.ttf\",File";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"metrix smart.ttf",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","metrix smart.ttf");
 };
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 24;BA.debugLine="Dim sd As String = File.DirRootExternal & \"/\"";
mostCurrent._sd = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/";
 //BA.debugLineNum = 25;BA.debugLine="Dim zip As ABZipUnzip";
mostCurrent._zip = new com.AB.ABZipUnzip.ABZipUnzip();
 //BA.debugLineNum = 27;BA.debugLine="Dim imv As ImageView";
mostCurrent._imv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim ibg As BitmapDrawable";
mostCurrent._ibg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 29;BA.debugLine="Dim l1,lb As Label";
mostCurrent._l1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._lb = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim anit As AnimateText";
mostCurrent._anit = new com.htoophyoe.anitext.animatetext();
 //BA.debugLineNum = 31;BA.debugLine="Dim bar As Panel";
mostCurrent._bar = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim b As Button";
mostCurrent._b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim bbg As ColorDrawable";
mostCurrent._bbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 34;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 38;BA.debugLine="Dim bd As Button";
mostCurrent._bd = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Dim lbf As Label";
mostCurrent._lbf = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
public static String  _imv_click() throws Exception{
 //BA.debugLineNum = 132;BA.debugLine="Sub imv_Click";
 //BA.debugLineNum = 133;BA.debugLine="StartActivity(About)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._about.getObject()));
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
step1._process_globals();
step2._process_globals();
step3._process_globals();
step4._process_globals();
step5._process_globals();
preview._process_globals();
about._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim ad As Timer";
_ad = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
}
