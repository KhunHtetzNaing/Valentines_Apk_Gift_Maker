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

public class preview extends Activity implements B4AActivity{
	public static preview mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.preview");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (preview).");
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
		activityBA = new BA(this, layout, processBA, "com.htetznaing.vdayappmaker", "com.htetznaing.vdayappmaker.preview");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.vdayappmaker.preview", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (preview) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (preview) Resume **");
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
		return preview.class;
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
        BA.LogInfo("** Activity (preview) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (preview) Resume **");
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
public static anywheresoftware.b4a.objects.Timer _it1 = null;
public static anywheresoftware.b4a.objects.Timer _it2 = null;
public static anywheresoftware.b4a.objects.Timer _it3 = null;
public static anywheresoftware.b4a.objects.Timer _it4 = null;
public static anywheresoftware.b4a.objects.Timer _it5 = null;
public static anywheresoftware.b4a.objects.Timer _it6 = null;
public static anywheresoftware.b4a.objects.Timer _it7 = null;
public static anywheresoftware.b4a.objects.Timer _it8 = null;
public static anywheresoftware.b4a.objects.Timer _it9 = null;
public static anywheresoftware.b4a.objects.Timer _it10 = null;
public static anywheresoftware.b4a.objects.Timer _it11 = null;
public static anywheresoftware.b4a.objects.Timer _it12 = null;
public static anywheresoftware.b4a.objects.Timer _it13 = null;
public static anywheresoftware.b4a.objects.Timer _it14 = null;
public static anywheresoftware.b4a.objects.Timer _it15 = null;
public static anywheresoftware.b4a.objects.Timer _b1t = null;
public static anywheresoftware.b4a.objects.Timer _b2t = null;
public static anywheresoftware.b4a.objects.Timer _b3t = null;
public static anywheresoftware.b4a.objects.Timer _b4t = null;
public static anywheresoftware.b4a.objects.Timer _b5t = null;
public static anywheresoftware.b4a.objects.Timer _b6t = null;
public static anywheresoftware.b4a.objects.Timer _b7t = null;
public static anywheresoftware.b4a.objects.Timer _b8t = null;
public static anywheresoftware.b4a.objects.Timer _b9t = null;
public static anywheresoftware.b4a.objects.Timer _b10t = null;
public static anywheresoftware.b4a.objects.Timer _b11t = null;
public static anywheresoftware.b4a.objects.Timer _b12t = null;
public static anywheresoftware.b4a.objects.Timer _b13t = null;
public static anywheresoftware.b4a.objects.Timer _b14t = null;
public static anywheresoftware.b4a.objects.Timer _b15t = null;
public static anywheresoftware.b4a.objects.Timer _timer1 = null;
public anywheresoftware.b4a.objects.Timer _ad = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _barposition = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblposition = null;
public com.htoophyoe.anitext.animatetext _anitext = null;
public static String[] _img = null;
public static int _counter = 0;
public anywheresoftware.b4a.objects.ImageViewWrapper _iv1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _iv2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i4 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i5 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i7 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i8 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i9 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i10 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i11 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i12 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i13 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i14 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _i15 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i1bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i2bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i3bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i4bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i5bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i6bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i7bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i8bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i9bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i10bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i11bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i12bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i13bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i14bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _i15bg = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i1ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i2ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i3ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i4ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i5ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i6ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i7ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i8ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i9ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i10ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i11ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i12ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i13ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i14ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _i15ani = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b4 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b5 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b7 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b8 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b9 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b10 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b11 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b12 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b13 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b14 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _b15 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b1bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b2bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b3bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b4bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b5bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b6bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b7bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b8bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b9bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b10bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b11bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b12bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b13bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b14bg = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b15bg = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b1ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b2ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b3ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b4ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b5ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b6ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b7ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b8ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b9ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b10ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b11ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b12ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b13ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b14ani = null;
public giuseppe.salvi.icos.library.ICOSSlideAnimation _b15ani = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b = null;
public static String _time = "";
public static String _color = "";
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _love = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _beiktano = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _chococooky = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _flower = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _matrix = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _matrixsmart = null;
public anywheresoftware.b4a.keywords.constants.TypefaceWrapper _yoeyar = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _mp = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _abg = null;
public anywheresoftware.b4a.phone.Phone _p = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bb = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _bbg = null;
public com.htetznaing.vdayappmaker.main _main = null;
public com.htetznaing.vdayappmaker.step1 _step1 = null;
public com.htetznaing.vdayappmaker.step2 _step2 = null;
public com.htetznaing.vdayappmaker.step3 _step3 = null;
public com.htetznaing.vdayappmaker.step4 _step4 = null;
public com.htetznaing.vdayappmaker.step5 _step5 = null;
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
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bm = null;
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 46;BA.debugLine="bb.Initialize(\"bb\")";
mostCurrent._bb.Initialize(mostCurrent.activityBA,"bb");
 //BA.debugLineNum = 47;BA.debugLine="bbg.Initialize(Colors.Black,15)";
mostCurrent._bbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Black,(int) (15));
 //BA.debugLineNum = 48;BA.debugLine="bb.Background = bbg";
mostCurrent._bb.setBackground((android.graphics.drawable.Drawable)(mostCurrent._bbg.getObject()));
 //BA.debugLineNum = 49;BA.debugLine="bb.Text = \"Build My App\"";
mostCurrent._bb.setText((Object)("Build My App"));
 //BA.debugLineNum = 52;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/8416044952");
 //BA.debugLineNum = 53;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 54;BA.debugLine="Activity.AddView(Banner,0%x,100%y - 50dip,100%x,5";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 56;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/9892778151");
 //BA.debugLineNum = 57;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 59;BA.debugLine="ad.Initialize(\"ad\",300000)";
mostCurrent._ad.Initialize(processBA,"ad",(long) (300000));
 //BA.debugLineNum = 60;BA.debugLine="ad.Enabled = True";
mostCurrent._ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 62;BA.debugLine="p.SetScreenOrientation(1)";
mostCurrent._p.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 63;BA.debugLine="abg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._abg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","bg.jpg").getObject()));
 //BA.debugLineNum = 64;BA.debugLine="abg.Gravity = Gravity.FILL";
mostCurrent._abg.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 65;BA.debugLine="Activity.Background = abg";
mostCurrent._activity.setBackground((android.graphics.drawable.Drawable)(mostCurrent._abg.getObject()));
 //BA.debugLineNum = 67;BA.debugLine="love = love.LoadFromAssets(\"love.ttf\")";
mostCurrent._love.setObject((android.graphics.Typeface)(mostCurrent._love.LoadFromAssets("love.ttf")));
 //BA.debugLineNum = 68;BA.debugLine="beiktano = beiktano.LoadFromAssets(\"beikthano.ttf";
mostCurrent._beiktano.setObject((android.graphics.Typeface)(mostCurrent._beiktano.LoadFromAssets("beikthano.ttf")));
 //BA.debugLineNum = 69;BA.debugLine="flower = flower.LoadFromAssets(\"flower.ttf\")";
mostCurrent._flower.setObject((android.graphics.Typeface)(mostCurrent._flower.LoadFromAssets("flower.ttf")));
 //BA.debugLineNum = 70;BA.debugLine="Matrix = Matrix.LoadFromAssets(\"matrix.ttf\")";
mostCurrent._matrix.setObject((android.graphics.Typeface)(mostCurrent._matrix.LoadFromAssets("matrix.ttf")));
 //BA.debugLineNum = 71;BA.debugLine="MatrixSmart = MatrixSmart.LoadFromAssets(\"metrix";
mostCurrent._matrixsmart.setObject((android.graphics.Typeface)(mostCurrent._matrixsmart.LoadFromAssets("metrix smart.ttf")));
 //BA.debugLineNum = 72;BA.debugLine="yoeyar = yoeyar.LoadFromAssets(\"yoeyar.ttf\")";
mostCurrent._yoeyar.setObject((android.graphics.Typeface)(mostCurrent._yoeyar.LoadFromAssets("yoeyar.ttf")));
 //BA.debugLineNum = 73;BA.debugLine="Chococooky = Chococooky.LoadFromAssets(\"chococook";
mostCurrent._chococooky.setObject((android.graphics.Typeface)(mostCurrent._chococooky.LoadFromAssets("chococooky.ttf")));
 //BA.debugLineNum = 77;BA.debugLine="iv1.Initialize(\"iv1\")";
mostCurrent._iv1.Initialize(mostCurrent.activityBA,"iv1");
 //BA.debugLineNum = 78;BA.debugLine="iv1.Bitmap = LoadBitmap(File.DirRootExternal & \"/";
mostCurrent._iv1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","iv1.png").getObject()));
 //BA.debugLineNum = 79;BA.debugLine="iv1.Gravity =  Gravity.FILL";
mostCurrent._iv1.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 80;BA.debugLine="Activity.AddView(iv1,0%x,0%y,100%x,50dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 82;BA.debugLine="lb.Initialize(\"lb\")";
mostCurrent._lb.Initialize(mostCurrent.activityBA,"lb");
 //BA.debugLineNum = 83;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._lb.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 84;BA.debugLine="lb.Typeface = love";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._love.getObject()));
 //BA.debugLineNum = 86;BA.debugLine="lb.TextColor = Colors.Yellow";
mostCurrent._lb.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Yellow);
 //BA.debugLineNum = 87;BA.debugLine="Activity.AddView(lb,0%x,(iv1.Top+iv1.Height)+1%y,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((mostCurrent._iv1.getTop()+mostCurrent._iv1.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90)));
 //BA.debugLineNum = 88;BA.debugLine="anitext.initialize(\"anitext\",Me,300)";
mostCurrent._anitext._initialize(mostCurrent.activityBA,"anitext",preview.getObject(),(int) (300));
 //BA.debugLineNum = 89;BA.debugLine="anitext.Run(File.ReadString(File.DirRootExternal";
mostCurrent._anitext._run(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","bdw.txt"),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._lb.getObject())));
 //BA.debugLineNum = 90;BA.debugLine="anitext.Endable = True";
mostCurrent._anitext._endable = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 92;BA.debugLine="Dim bm As Bitmap";
_bm = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 93;BA.debugLine="bm.Initialize(File.DirRootExternal & \"/.vDayAppMa";
_bm.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","h1.jpg");
 //BA.debugLineNum = 95;BA.debugLine="iv2.Initialize(\"iv2\")";
mostCurrent._iv2.Initialize(mostCurrent.activityBA,"iv2");
 //BA.debugLineNum = 96;BA.debugLine="iv2.Gravity = Gravity.FILL";
mostCurrent._iv2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 97;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb.";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (350)));
 //BA.debugLineNum = 101;BA.debugLine="time = File.ReadString(File.DirRootExternal & \"/.";
mostCurrent._time = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","time.txt");
 //BA.debugLineNum = 102;BA.debugLine="t.Initialize(\"t\", time) '2 seconds delay";
_t.Initialize(processBA,"t",(long)(Double.parseDouble(mostCurrent._time)));
 //BA.debugLineNum = 103;BA.debugLine="t.Enabled = True";
_t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 105;BA.debugLine="Activity.LoadLayout(\"lay1\")";
mostCurrent._activity.LoadLayout("lay1",mostCurrent.activityBA);
 //BA.debugLineNum = 107;BA.debugLine="iGroup";
_igroup();
 //BA.debugLineNum = 108;BA.debugLine="bGroup";
_bgroup();
 //BA.debugLineNum = 109;BA.debugLine="b.Initialize(\"b\")";
mostCurrent._b.Initialize(mostCurrent.activityBA,"b");
 //BA.debugLineNum = 110;BA.debugLine="b.SetBackgroundImage(LoadBitmap(File.DirRootExter";
mostCurrent._b.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","love.png").getObject()));
 //BA.debugLineNum = 111;BA.debugLine="Activity.AddView(b,100%x - 60dip,100%y - 60dip,50";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60))),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (60))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 113;BA.debugLine="mp.Initialize";
mostCurrent._mp.Initialize();
 //BA.debugLineNum = 114;BA.debugLine="If File.Exists(File.DirRootExternal & \"/.vDayAppM";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","vday.mp3")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 115;BA.debugLine="File.Copy(File.DirRootExternal & \"/.vDayAppMaker";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","vday.mp3",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),".vday.mp3");
 //BA.debugLineNum = 116;BA.debugLine="mp.Load(File.DirRootExternal,\".vday.mp3\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),".vday.mp3");
 //BA.debugLineNum = 117;BA.debugLine="timer1.Initialize(\"timer1\",1000)";
_timer1.Initialize(processBA,"timer1",(long) (1000));
 //BA.debugLineNum = 118;BA.debugLine="timer1.Enabled = True";
_timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 119;BA.debugLine="mp.Play";
mostCurrent._mp.Play();
 //BA.debugLineNum = 120;BA.debugLine="lblPosition.Initialize(\"lblPosition\")";
mostCurrent._lblposition.Initialize(mostCurrent.activityBA,"lblPosition");
 //BA.debugLineNum = 121;BA.debugLine="Activity.AddView(lblPosition,5%x,(iv2.Top+iv2.He";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lblposition.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),(int) ((mostCurrent._iv2.getTop()+mostCurrent._iv2.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 122;BA.debugLine="barPosition.Initialize(\"barPosition\")";
mostCurrent._barposition.Initialize(mostCurrent.activityBA,"barPosition");
 //BA.debugLineNum = 123;BA.debugLine="Activity.AddView(barPosition,0%x,(lblPosition.To";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._barposition.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((mostCurrent._lblposition.getTop()+mostCurrent._lblposition.getHeight())),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 };
 //BA.debugLineNum = 126;BA.debugLine="Activity.AddView(bb,20%x,50%y,60%x,50dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._bb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
 //BA.debugLineNum = 1311;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 1312;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 1313;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 1314;BA.debugLine="Answ = Msgbox2(\"Do you want to exit ? \",\"Attenti";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2("Do you want to exit ? ","Attention!","Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 1315;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 1316;BA.debugLine="If mp.IsPlaying = True Then";
if (mostCurrent._mp.IsPlaying()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 1317;BA.debugLine="mp.Stop";
mostCurrent._mp.Stop();
 //BA.debugLineNum = 1318;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 1322;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 1323;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 1326;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 827;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 828;BA.debugLine="If b1t.Enabled = False Then";
if (_b1t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 829;BA.debugLine="b1t.Enabled = True";
_b1t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 832;BA.debugLine="If b2t.Enabled = False Then";
if (_b2t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 833;BA.debugLine="b2t.Enabled = True";
_b2t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 837;BA.debugLine="If b3t.Enabled = False Then";
if (_b3t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 838;BA.debugLine="b3t.Enabled = True";
_b3t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 841;BA.debugLine="If b4t.Enabled = False Then";
if (_b4t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 842;BA.debugLine="b4t.Enabled = True";
_b4t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 845;BA.debugLine="If b5t.Enabled = False Then";
if (_b5t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 846;BA.debugLine="b5t.Enabled = True";
_b5t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 849;BA.debugLine="If b6t.Enabled = False Then";
if (_b6t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 850;BA.debugLine="b6t.Enabled = True";
_b6t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 853;BA.debugLine="If b7t.Enabled = False Then";
if (_b7t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 854;BA.debugLine="b7t.Enabled = True";
_b7t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 857;BA.debugLine="If b8t.Enabled = False Then";
if (_b8t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 858;BA.debugLine="b8t.Enabled = True";
_b8t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 861;BA.debugLine="If b9t.Enabled = False Then";
if (_b9t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 862;BA.debugLine="b9t.Enabled = True";
_b9t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 865;BA.debugLine="If b10t.Enabled = False Then";
if (_b10t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 866;BA.debugLine="b10t.Enabled = True";
_b10t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 869;BA.debugLine="If b11t.Enabled = False Then";
if (_b11t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 870;BA.debugLine="b11t.Enabled = True";
_b11t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 873;BA.debugLine="If b12t.Enabled = False Then";
if (_b12t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 874;BA.debugLine="b12t.Enabled = True";
_b12t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 877;BA.debugLine="If b13t.Enabled = False Then";
if (_b13t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 878;BA.debugLine="b13t.Enabled = True";
_b13t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 881;BA.debugLine="If b14t.Enabled = False Then";
if (_b14t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 882;BA.debugLine="b14t.Enabled = True";
_b14t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 885;BA.debugLine="If b15t.Enabled = False Then";
if (_b15t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 886;BA.debugLine="b15t.Enabled = True";
_b15t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 890;BA.debugLine="If it1.Enabled = False Then";
if (_it1.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 891;BA.debugLine="it1.Enabled = True";
_it1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 894;BA.debugLine="If it2.Enabled = False Then";
if (_it2.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 895;BA.debugLine="it2.Enabled = True";
_it2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 898;BA.debugLine="If it3.Enabled = False Then";
if (_it3.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 899;BA.debugLine="it3.Enabled = True";
_it3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 902;BA.debugLine="If it4.Enabled = False Then";
if (_it4.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 903;BA.debugLine="it4.Enabled = True";
_it4.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 906;BA.debugLine="If it5.Enabled = False Then";
if (_it5.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 907;BA.debugLine="it5.Enabled = True";
_it5.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 910;BA.debugLine="If it6.Enabled = False Then";
if (_it6.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 911;BA.debugLine="it6.Enabled = True";
_it6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 914;BA.debugLine="If it7.Enabled = False Then";
if (_it7.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 915;BA.debugLine="it7.Enabled = True";
_it7.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 918;BA.debugLine="If it8.Enabled = False Then";
if (_it8.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 919;BA.debugLine="it8.Enabled = True";
_it8.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 922;BA.debugLine="If it9.Enabled = False Then";
if (_it9.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 923;BA.debugLine="it9.Enabled = True";
_it9.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 926;BA.debugLine="If it10.Enabled = False Then";
if (_it10.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 927;BA.debugLine="it10.Enabled = True";
_it10.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 930;BA.debugLine="If it11.Enabled = False Then";
if (_it11.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 931;BA.debugLine="it11.Enabled = True";
_it11.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 934;BA.debugLine="If it12.Enabled = False Then";
if (_it12.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 935;BA.debugLine="it12.Enabled = True";
_it12.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 938;BA.debugLine="If it13.Enabled = False Then";
if (_it13.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 939;BA.debugLine="it13.Enabled = True";
_it13.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 942;BA.debugLine="If it14.Enabled = False Then";
if (_it14.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 943;BA.debugLine="it14.Enabled = True";
_it14.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 946;BA.debugLine="If it15.Enabled = False Then";
if (_it15.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 947;BA.debugLine="it15.Enabled = True";
_it15.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 949;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 703;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 704;BA.debugLine="If b1t.Enabled = False Then";
if (_b1t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 705;BA.debugLine="b1t.Enabled = True";
_b1t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 708;BA.debugLine="If b2t.Enabled = False Then";
if (_b2t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 709;BA.debugLine="b2t.Enabled = True";
_b2t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 713;BA.debugLine="If b3t.Enabled = False Then";
if (_b3t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 714;BA.debugLine="b3t.Enabled = True";
_b3t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 717;BA.debugLine="If b4t.Enabled = False Then";
if (_b4t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 718;BA.debugLine="b4t.Enabled = True";
_b4t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 721;BA.debugLine="If b5t.Enabled = False Then";
if (_b5t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 722;BA.debugLine="b5t.Enabled = True";
_b5t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 725;BA.debugLine="If b6t.Enabled = False Then";
if (_b6t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 726;BA.debugLine="b6t.Enabled = True";
_b6t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 729;BA.debugLine="If b7t.Enabled = False Then";
if (_b7t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 730;BA.debugLine="b7t.Enabled = True";
_b7t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 733;BA.debugLine="If b8t.Enabled = False Then";
if (_b8t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 734;BA.debugLine="b8t.Enabled = True";
_b8t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 737;BA.debugLine="If b9t.Enabled = False Then";
if (_b9t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 738;BA.debugLine="b9t.Enabled = True";
_b9t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 741;BA.debugLine="If b10t.Enabled = False Then";
if (_b10t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 742;BA.debugLine="b10t.Enabled = True";
_b10t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 745;BA.debugLine="If b11t.Enabled = False Then";
if (_b11t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 746;BA.debugLine="b11t.Enabled = True";
_b11t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 749;BA.debugLine="If b12t.Enabled = False Then";
if (_b12t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 750;BA.debugLine="b12t.Enabled = True";
_b12t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 753;BA.debugLine="If b13t.Enabled = False Then";
if (_b13t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 754;BA.debugLine="b13t.Enabled = True";
_b13t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 757;BA.debugLine="If b14t.Enabled = False Then";
if (_b14t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 758;BA.debugLine="b14t.Enabled = True";
_b14t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 761;BA.debugLine="If b15t.Enabled = False Then";
if (_b15t.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 762;BA.debugLine="b15t.Enabled = True";
_b15t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 766;BA.debugLine="If it1.Enabled = False Then";
if (_it1.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 767;BA.debugLine="it1.Enabled = True";
_it1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 770;BA.debugLine="If it2.Enabled = False Then";
if (_it2.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 771;BA.debugLine="it2.Enabled = True";
_it2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 774;BA.debugLine="If it3.Enabled = False Then";
if (_it3.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 775;BA.debugLine="it3.Enabled = True";
_it3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 778;BA.debugLine="If it4.Enabled = False Then";
if (_it4.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 779;BA.debugLine="it4.Enabled = True";
_it4.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 782;BA.debugLine="If it5.Enabled = False Then";
if (_it5.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 783;BA.debugLine="it5.Enabled = True";
_it5.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 786;BA.debugLine="If it6.Enabled = False Then";
if (_it6.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 787;BA.debugLine="it6.Enabled = True";
_it6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 790;BA.debugLine="If it7.Enabled = False Then";
if (_it7.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 791;BA.debugLine="it7.Enabled = True";
_it7.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 794;BA.debugLine="If it8.Enabled = False Then";
if (_it8.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 795;BA.debugLine="it8.Enabled = True";
_it8.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 798;BA.debugLine="If it9.Enabled = False Then";
if (_it9.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 799;BA.debugLine="it9.Enabled = True";
_it9.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 802;BA.debugLine="If it10.Enabled = False Then";
if (_it10.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 803;BA.debugLine="it10.Enabled = True";
_it10.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 806;BA.debugLine="If it11.Enabled = False Then";
if (_it11.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 807;BA.debugLine="it11.Enabled = True";
_it11.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 810;BA.debugLine="If it12.Enabled = False Then";
if (_it12.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 811;BA.debugLine="it12.Enabled = True";
_it12.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 814;BA.debugLine="If it13.Enabled = False Then";
if (_it13.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 815;BA.debugLine="it13.Enabled = True";
_it13.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 818;BA.debugLine="If it14.Enabled = False Then";
if (_it14.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 819;BA.debugLine="it14.Enabled = True";
_it14.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 822;BA.debugLine="If it15.Enabled = False Then";
if (_it15.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 823;BA.debugLine="it15.Enabled = True";
_it15.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 825;BA.debugLine="End Sub";
return "";
}
public static String  _ad_tick() throws Exception{
 //BA.debugLineNum = 133;BA.debugLine="Sub ad_Tick";
 //BA.debugLineNum = 134;BA.debugLine="If Interstitial.Ready Then Interstitial.show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _b_click() throws Exception{
int _id_int = 0;
com.maximus.id.id _id = null;
anywheresoftware.b4a.objects.collections.List _lis = null;
anywheresoftware.b4a.agraham.dialogs.InputDialog.ColorDialog _cd = null;
int _i = 0;
anywheresoftware.b4a.objects.IntentWrapper _kall = null;
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
 //BA.debugLineNum = 218;BA.debugLine="Sub b_Click";
 //BA.debugLineNum = 219;BA.debugLine="Dim id_int As Int";
_id_int = 0;
 //BA.debugLineNum = 220;BA.debugLine="Dim id As id";
_id = new com.maximus.id.id();
 //BA.debugLineNum = 221;BA.debugLine="Dim lis As List";
_lis = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 222;BA.debugLine="lis.Initialize";
_lis.Initialize();
 //BA.debugLineNum = 223;BA.debugLine="lis.AddAll(Array As String(\"Change Background Col";
_lis.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"Change Background Color","Change Text Color","Play Music","Stop Music","Restart","Call","Send Message","Change Image Size"}));
 //BA.debugLineNum = 224;BA.debugLine="id_int = id.InputList1(lis,\"Choose!\")";
_id_int = _id.InputList1(_lis,"Choose!",mostCurrent.activityBA);
 //BA.debugLineNum = 227;BA.debugLine="If id_int = 0 Then";
if (_id_int==0) { 
 //BA.debugLineNum = 228;BA.debugLine="Dim cd As ColorDialog";
_cd = new anywheresoftware.b4a.agraham.dialogs.InputDialog.ColorDialog();
 //BA.debugLineNum = 229;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 230;BA.debugLine="cd.RGB = Colors.DarkGray";
_cd.setRGB(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 231;BA.debugLine="i = cd.Show(\"B4A ColorPicker Dialog\", \"Yes\", \"No";
_i = _cd.Show("B4A ColorPicker Dialog","Yes","No","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 232;BA.debugLine="If i = DialogResponse.POSITIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 233;BA.debugLine="Activity.Color = cd.RGB";
mostCurrent._activity.setColor(_cd.getRGB());
 };
 //BA.debugLineNum = 235;BA.debugLine="If i = DialogResponse.NEGATIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 236;BA.debugLine="Activity.Color = \"\"";
mostCurrent._activity.setColor((int)(Double.parseDouble("")));
 };
 };
 //BA.debugLineNum = 240;BA.debugLine="If id_int = 1 Then";
if (_id_int==1) { 
 //BA.debugLineNum = 241;BA.debugLine="Dim cd As ColorDialog";
_cd = new anywheresoftware.b4a.agraham.dialogs.InputDialog.ColorDialog();
 //BA.debugLineNum = 242;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 243;BA.debugLine="cd.RGB = Colors.DarkGray";
_cd.setRGB(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 244;BA.debugLine="i = cd.Show(\"B4A ColorPicker Dialog\", \"Yes\", \"No";
_i = _cd.Show("B4A ColorPicker Dialog","Yes","No","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 245;BA.debugLine="If i = DialogResponse.POSITIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 246;BA.debugLine="lb.TextColor = cd.RGB";
mostCurrent._lb.setTextColor(_cd.getRGB());
 //BA.debugLineNum = 247;BA.debugLine="Log(cd)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_cd));
 };
 //BA.debugLineNum = 249;BA.debugLine="If i = DialogResponse.NEGATIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 250;BA.debugLine="lb.TextColor = \"\"";
mostCurrent._lb.setTextColor((int)(Double.parseDouble("")));
 };
 };
 //BA.debugLineNum = 255;BA.debugLine="If id_int = 2 Then";
if (_id_int==2) { 
 //BA.debugLineNum = 256;BA.debugLine="If mp.IsPlaying = False Then";
if (mostCurrent._mp.IsPlaying()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 257;BA.debugLine="If File.Exists(File.DirRootExternal & \"/.vDayAp";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","vday.mp3")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 258;BA.debugLine="File.Copy(File.DirRootExternal & \"/.vDayAppMak";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","vday.mp3",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),".vday.mp3");
 //BA.debugLineNum = 259;BA.debugLine="mp.Load(File.DirRootExternal,\".vday.mp3\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),".vday.mp3");
 //BA.debugLineNum = 260;BA.debugLine="mp.Play";
mostCurrent._mp.Play();
 }else {
 //BA.debugLineNum = 262;BA.debugLine="ToastMessageShow(\"No added music :( \",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("No added music :( ",anywheresoftware.b4a.keywords.Common.True);
 };
 };
 };
 //BA.debugLineNum = 268;BA.debugLine="If id_int = 3 Then";
if (_id_int==3) { 
 //BA.debugLineNum = 269;BA.debugLine="If mp.IsPlaying = True Then";
if (mostCurrent._mp.IsPlaying()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 270;BA.debugLine="mp.Stop";
mostCurrent._mp.Stop();
 };
 };
 //BA.debugLineNum = 275;BA.debugLine="If id_int = 4 Then";
if (_id_int==4) { 
 //BA.debugLineNum = 276;BA.debugLine="If mp.IsPlaying = True Then";
if (mostCurrent._mp.IsPlaying()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 277;BA.debugLine="mp.Stop";
mostCurrent._mp.Stop();
 };
 //BA.debugLineNum = 279;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 280;BA.debugLine="StartActivity(Me)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,preview.getObject());
 };
 //BA.debugLineNum = 284;BA.debugLine="If id_int = 5 Then";
if (_id_int==5) { 
 //BA.debugLineNum = 285;BA.debugLine="Dim kall As Intent";
_kall = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 286;BA.debugLine="kall.Initialize(kall.ACTION_CALL,\"tel:\" & File.R";
_kall.Initialize(_kall.ACTION_CALL,"tel:"+anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","number.txt"));
 //BA.debugLineNum = 287;BA.debugLine="StartActivity(kall)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_kall.getObject()));
 };
 //BA.debugLineNum = 291;BA.debugLine="If id_int = 6 Then";
if (_id_int==6) { 
 //BA.debugLineNum = 292;BA.debugLine="Dim shareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 293;BA.debugLine="shareIt.Initialize (shareIt.ACTION_SEND,File.Rea";
_shareit.Initialize(_shareit.ACTION_SEND,anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","number.txt"));
 //BA.debugLineNum = 294;BA.debugLine="shareIt.SetType(\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 295;BA.debugLine="shareIt.PutExtra (\"android.intent.extra.TEXT\",Fi";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","message.txt")));
 //BA.debugLineNum = 296;BA.debugLine="shareIt.PutExtra (\"android.intent.extra.SUBJECT\"";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("Get Free!!"));
 //BA.debugLineNum = 297;BA.debugLine="shareIt.WrapAsIntentChooser(\"Choose Send Via..\")";
_shareit.WrapAsIntentChooser("Choose Send Via..");
 //BA.debugLineNum = 298;BA.debugLine="StartActivity (shareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 };
 //BA.debugLineNum = 301;BA.debugLine="If id_int = 7 Then";
if (_id_int==7) { 
 //BA.debugLineNum = 302;BA.debugLine="Dim id_int As Int";
_id_int = 0;
 //BA.debugLineNum = 303;BA.debugLine="Dim id As id";
_id = new com.maximus.id.id();
 //BA.debugLineNum = 304;BA.debugLine="Dim lis As List";
_lis = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 305;BA.debugLine="lis.Initialize";
_lis.Initialize();
 //BA.debugLineNum = 306;BA.debugLine="lis.AddAll(Array As String(\"280X x 300Y\",\"280X x";
_lis.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"280X x 300Y","280X x 310Y","280X x 320Y","280X x 330Y","280X x 340Y","280X x 360Y","280X x 370Y","280X x 380Y","280X x 390Y","280X x 400Y","Change to Default Size","","300X x 300Y","300X x 310Y","300X x 320Y","300X x 330Y","300X x 340Y","300X x 360Y","300X x 370Y","300X x 380Y","300X x 390Y","300X x 400Y"}));
 //BA.debugLineNum = 307;BA.debugLine="id_int = id.InputList1(lis,\"Choose Image Size!\")";
_id_int = _id.InputList1(_lis,"Choose Image Size!",mostCurrent.activityBA);
 //BA.debugLineNum = 309;BA.debugLine="If id_int = 0 Then";
if (_id_int==0) { 
 //BA.debugLineNum = 310;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 311;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)));
 };
 //BA.debugLineNum = 314;BA.debugLine="If id_int = 1 Then";
if (_id_int==1) { 
 //BA.debugLineNum = 315;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 316;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (310)));
 };
 //BA.debugLineNum = 319;BA.debugLine="If id_int = 2 Then";
if (_id_int==2) { 
 //BA.debugLineNum = 320;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 321;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (320)));
 };
 //BA.debugLineNum = 324;BA.debugLine="If id_int = 3 Then";
if (_id_int==3) { 
 //BA.debugLineNum = 325;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 326;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (330)));
 };
 //BA.debugLineNum = 329;BA.debugLine="If id_int = 4 Then";
if (_id_int==4) { 
 //BA.debugLineNum = 330;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 331;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (340)));
 };
 //BA.debugLineNum = 334;BA.debugLine="If id_int = 5 Then";
if (_id_int==5) { 
 //BA.debugLineNum = 335;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 336;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (360)));
 };
 //BA.debugLineNum = 339;BA.debugLine="If id_int = 6 Then";
if (_id_int==6) { 
 //BA.debugLineNum = 340;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 341;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (370)));
 };
 //BA.debugLineNum = 344;BA.debugLine="If id_int = 7 Then";
if (_id_int==7) { 
 //BA.debugLineNum = 345;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 346;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (380)));
 };
 //BA.debugLineNum = 349;BA.debugLine="If id_int = 8 Then";
if (_id_int==8) { 
 //BA.debugLineNum = 350;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 351;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (390)));
 };
 //BA.debugLineNum = 354;BA.debugLine="If id_int = 9 Then";
if (_id_int==9) { 
 //BA.debugLineNum = 355;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 356;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (400)));
 };
 //BA.debugLineNum = 359;BA.debugLine="If id_int = 10 Then";
if (_id_int==10) { 
 //BA.debugLineNum = 360;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 361;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)));
 };
 //BA.debugLineNum = 364;BA.debugLine="If id_int = 12 Then";
if (_id_int==12) { 
 //BA.debugLineNum = 365;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 366;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (310)));
 };
 //BA.debugLineNum = 369;BA.debugLine="If id_int = 13 Then";
if (_id_int==13) { 
 //BA.debugLineNum = 370;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 371;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (320)));
 };
 //BA.debugLineNum = 374;BA.debugLine="If id_int = 14 Then";
if (_id_int==14) { 
 //BA.debugLineNum = 375;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 376;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (330)));
 };
 //BA.debugLineNum = 379;BA.debugLine="If id_int = 15 Then";
if (_id_int==15) { 
 //BA.debugLineNum = 380;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 381;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (340)));
 };
 //BA.debugLineNum = 384;BA.debugLine="If id_int = 16 Then";
if (_id_int==16) { 
 //BA.debugLineNum = 385;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 386;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (350)));
 };
 //BA.debugLineNum = 389;BA.debugLine="If id_int = 17 Then";
if (_id_int==17) { 
 //BA.debugLineNum = 390;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 391;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (360)));
 };
 //BA.debugLineNum = 394;BA.debugLine="If id_int = 18 Then";
if (_id_int==18) { 
 //BA.debugLineNum = 395;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 396;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (370)));
 };
 //BA.debugLineNum = 399;BA.debugLine="If id_int = 19 Then";
if (_id_int==19) { 
 //BA.debugLineNum = 400;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 401;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (380)));
 };
 //BA.debugLineNum = 404;BA.debugLine="If id_int = 20 Then";
if (_id_int==20) { 
 //BA.debugLineNum = 405;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 406;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (390)));
 };
 //BA.debugLineNum = 409;BA.debugLine="If id_int = 21 Then";
if (_id_int==21) { 
 //BA.debugLineNum = 410;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 411;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+l";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (400)));
 //BA.debugLineNum = 412;BA.debugLine="Log(\"this 21\")";
anywheresoftware.b4a.keywords.Common.Log("this 21");
 };
 };
 //BA.debugLineNum = 415;BA.debugLine="End Sub";
return "";
}
public static String  _b10ani_animationend() throws Exception{
 //BA.debugLineNum = 1279;BA.debugLine="Sub b10ani_animationend";
 //BA.debugLineNum = 1280;BA.debugLine="b10ani.StartAnim(b10)";
mostCurrent._b10ani.StartAnim((android.view.View)(mostCurrent._b10.getObject()));
 //BA.debugLineNum = 1281;BA.debugLine="End Sub";
return "";
}
public static String  _b10t_tick() throws Exception{
 //BA.debugLineNum = 1140;BA.debugLine="Sub b10t_Tick";
 //BA.debugLineNum = 1141;BA.debugLine="b10.Visible = True";
mostCurrent._b10.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1142;BA.debugLine="b10ani.SlideFadeToBottom(\"b10ani\",1200,10000)";
mostCurrent._b10ani.SlideFadeToBottom(mostCurrent.activityBA,"b10ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1143;BA.debugLine="b10ani.StartAnim(b10)";
mostCurrent._b10ani.StartAnim((android.view.View)(mostCurrent._b10.getObject()));
 //BA.debugLineNum = 1144;BA.debugLine="b10t.Enabled = False";
_b10t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1145;BA.debugLine="End Sub";
return "";
}
public static String  _b11ani_animationend() throws Exception{
 //BA.debugLineNum = 1283;BA.debugLine="Sub b11ani_animationend";
 //BA.debugLineNum = 1284;BA.debugLine="b11ani.StartAnim(b11)";
mostCurrent._b11ani.StartAnim((android.view.View)(mostCurrent._b11.getObject()));
 //BA.debugLineNum = 1285;BA.debugLine="End Sub";
return "";
}
public static String  _b11t_tick() throws Exception{
 //BA.debugLineNum = 1147;BA.debugLine="Sub b11t_Tick";
 //BA.debugLineNum = 1148;BA.debugLine="b11.Visible = True";
mostCurrent._b11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1149;BA.debugLine="b11ani.SlideFadeToBottom(\"b11ani\",1200,10000)";
mostCurrent._b11ani.SlideFadeToBottom(mostCurrent.activityBA,"b11ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1150;BA.debugLine="b11ani.StartAnim(b11)";
mostCurrent._b11ani.StartAnim((android.view.View)(mostCurrent._b11.getObject()));
 //BA.debugLineNum = 1151;BA.debugLine="b11t.Enabled = False";
_b11t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1152;BA.debugLine="End Sub";
return "";
}
public static String  _b12ani_animationend() throws Exception{
 //BA.debugLineNum = 1287;BA.debugLine="Sub b12ani_animationend";
 //BA.debugLineNum = 1288;BA.debugLine="b12ani.StartAnim(b12)";
mostCurrent._b12ani.StartAnim((android.view.View)(mostCurrent._b12.getObject()));
 //BA.debugLineNum = 1289;BA.debugLine="End Sub";
return "";
}
public static String  _b12t_tick() throws Exception{
 //BA.debugLineNum = 1154;BA.debugLine="Sub b12t_Tick";
 //BA.debugLineNum = 1155;BA.debugLine="b12.Visible = True";
mostCurrent._b12.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1156;BA.debugLine="b12ani.SlideFadeToBottom(\"b12ani\",1200,10000)";
mostCurrent._b12ani.SlideFadeToBottom(mostCurrent.activityBA,"b12ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1157;BA.debugLine="b12ani.StartAnim(b12)";
mostCurrent._b12ani.StartAnim((android.view.View)(mostCurrent._b12.getObject()));
 //BA.debugLineNum = 1158;BA.debugLine="b12t.Enabled = False";
_b12t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1159;BA.debugLine="End Sub";
return "";
}
public static String  _b13ani_animationend() throws Exception{
 //BA.debugLineNum = 1291;BA.debugLine="Sub b13ani_animationend";
 //BA.debugLineNum = 1292;BA.debugLine="b13ani.StartAnim(b13)";
mostCurrent._b13ani.StartAnim((android.view.View)(mostCurrent._b13.getObject()));
 //BA.debugLineNum = 1293;BA.debugLine="End Sub";
return "";
}
public static String  _b13t_tick() throws Exception{
 //BA.debugLineNum = 1161;BA.debugLine="Sub b13t_Tick";
 //BA.debugLineNum = 1162;BA.debugLine="b13.Visible = True";
mostCurrent._b13.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1163;BA.debugLine="b13ani.SlideFadeToBottom(\"b13ani\",1200,10000)";
mostCurrent._b13ani.SlideFadeToBottom(mostCurrent.activityBA,"b13ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1164;BA.debugLine="b13ani.StartAnim(b13)";
mostCurrent._b13ani.StartAnim((android.view.View)(mostCurrent._b13.getObject()));
 //BA.debugLineNum = 1165;BA.debugLine="b13t.Enabled = False";
_b13t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1166;BA.debugLine="End Sub";
return "";
}
public static String  _b14ani_animationend() throws Exception{
 //BA.debugLineNum = 1295;BA.debugLine="Sub b14ani_animationend";
 //BA.debugLineNum = 1296;BA.debugLine="b14ani.StartAnim(b14)";
mostCurrent._b14ani.StartAnim((android.view.View)(mostCurrent._b14.getObject()));
 //BA.debugLineNum = 1297;BA.debugLine="End Sub";
return "";
}
public static String  _b14t_tick() throws Exception{
 //BA.debugLineNum = 1168;BA.debugLine="Sub b14t_Tick";
 //BA.debugLineNum = 1169;BA.debugLine="b14.Visible = True";
mostCurrent._b14.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1170;BA.debugLine="b14ani.SlideFadeToBottom(\"b14ani\",1200,10000)";
mostCurrent._b14ani.SlideFadeToBottom(mostCurrent.activityBA,"b14ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1171;BA.debugLine="b14ani.StartAnim(b14)";
mostCurrent._b14ani.StartAnim((android.view.View)(mostCurrent._b14.getObject()));
 //BA.debugLineNum = 1172;BA.debugLine="b14t.Enabled = False";
_b14t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1173;BA.debugLine="End Sub";
return "";
}
public static String  _b15ani_animationend() throws Exception{
 //BA.debugLineNum = 1299;BA.debugLine="Sub b15ani_animationend";
 //BA.debugLineNum = 1300;BA.debugLine="b15ani.StartAnim(b15)";
mostCurrent._b15ani.StartAnim((android.view.View)(mostCurrent._b15.getObject()));
 //BA.debugLineNum = 1301;BA.debugLine="End Sub";
return "";
}
public static String  _b15t_tick() throws Exception{
 //BA.debugLineNum = 1175;BA.debugLine="Sub b15t_Tick";
 //BA.debugLineNum = 1176;BA.debugLine="b15.Visible = True";
mostCurrent._b15.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1177;BA.debugLine="b15ani.SlideFadeToBottom(\"b15ani\",1200,10000)";
mostCurrent._b15ani.SlideFadeToBottom(mostCurrent.activityBA,"b15ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1178;BA.debugLine="b15ani.StartAnim(b15)";
mostCurrent._b15ani.StartAnim((android.view.View)(mostCurrent._b15.getObject()));
 //BA.debugLineNum = 1179;BA.debugLine="b15t.Enabled = False";
_b15t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1180;BA.debugLine="End Sub";
return "";
}
public static String  _b1ani_animationend() throws Exception{
 //BA.debugLineNum = 1243;BA.debugLine="Sub b1ani_animationend";
 //BA.debugLineNum = 1244;BA.debugLine="b1ani.StartAnim(b1)";
mostCurrent._b1ani.StartAnim((android.view.View)(mostCurrent._b1.getObject()));
 //BA.debugLineNum = 1245;BA.debugLine="End Sub";
return "";
}
public static String  _b1t_tick() throws Exception{
 //BA.debugLineNum = 1071;BA.debugLine="Sub b1t_Tick";
 //BA.debugLineNum = 1073;BA.debugLine="b1.Visible = True";
mostCurrent._b1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1074;BA.debugLine="b1ani.SlideFadeToBottom(\"b1ani\",1200,10000)";
mostCurrent._b1ani.SlideFadeToBottom(mostCurrent.activityBA,"b1ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1075;BA.debugLine="b1ani.StartAnim(b1)";
mostCurrent._b1ani.StartAnim((android.view.View)(mostCurrent._b1.getObject()));
 //BA.debugLineNum = 1076;BA.debugLine="b1t.Enabled = False";
_b1t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1077;BA.debugLine="End Sub";
return "";
}
public static String  _b2ani_animationend() throws Exception{
 //BA.debugLineNum = 1247;BA.debugLine="Sub b2ani_animationend";
 //BA.debugLineNum = 1248;BA.debugLine="b2ani.StartAnim(b2)";
mostCurrent._b2ani.StartAnim((android.view.View)(mostCurrent._b2.getObject()));
 //BA.debugLineNum = 1249;BA.debugLine="End Sub";
return "";
}
public static String  _b2t_tick() throws Exception{
 //BA.debugLineNum = 1079;BA.debugLine="Sub b2t_Tick";
 //BA.debugLineNum = 1081;BA.debugLine="b2.Visible = True";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1082;BA.debugLine="b2ani.SlideFadeToBottom(\"b2ani\",1200,10000)";
mostCurrent._b2ani.SlideFadeToBottom(mostCurrent.activityBA,"b2ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1083;BA.debugLine="b2ani.StartAnim(b2)";
mostCurrent._b2ani.StartAnim((android.view.View)(mostCurrent._b2.getObject()));
 //BA.debugLineNum = 1084;BA.debugLine="b2t.Enabled = False";
_b2t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1085;BA.debugLine="End Sub";
return "";
}
public static String  _b3ani_animationend() throws Exception{
 //BA.debugLineNum = 1251;BA.debugLine="Sub b3ani_animationend";
 //BA.debugLineNum = 1252;BA.debugLine="b3ani.StartAnim(b3)";
mostCurrent._b3ani.StartAnim((android.view.View)(mostCurrent._b3.getObject()));
 //BA.debugLineNum = 1253;BA.debugLine="End Sub";
return "";
}
public static String  _b3t_tick() throws Exception{
 //BA.debugLineNum = 1087;BA.debugLine="Sub b3t_Tick";
 //BA.debugLineNum = 1089;BA.debugLine="b3.Visible = True";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1090;BA.debugLine="b3ani.SlideFadeToBottom(\"b3ani\",1200,10000)";
mostCurrent._b3ani.SlideFadeToBottom(mostCurrent.activityBA,"b3ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1091;BA.debugLine="b3ani.StartAnim(b3)";
mostCurrent._b3ani.StartAnim((android.view.View)(mostCurrent._b3.getObject()));
 //BA.debugLineNum = 1092;BA.debugLine="b3t.Enabled = False";
_b3t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1093;BA.debugLine="End Sub";
return "";
}
public static String  _b4ani_animationend() throws Exception{
 //BA.debugLineNum = 1255;BA.debugLine="Sub b4ani_animationend";
 //BA.debugLineNum = 1256;BA.debugLine="b4ani.StartAnim(b4)";
mostCurrent._b4ani.StartAnim((android.view.View)(mostCurrent._b4.getObject()));
 //BA.debugLineNum = 1257;BA.debugLine="End Sub";
return "";
}
public static String  _b4t_tick() throws Exception{
 //BA.debugLineNum = 1095;BA.debugLine="Sub b4t_Tick";
 //BA.debugLineNum = 1097;BA.debugLine="b4.Visible = True";
mostCurrent._b4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1098;BA.debugLine="b4ani.SlideFadeToBottom(\"b4ani\",1200,10000)";
mostCurrent._b4ani.SlideFadeToBottom(mostCurrent.activityBA,"b4ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1099;BA.debugLine="b4ani.StartAnim(b4)";
mostCurrent._b4ani.StartAnim((android.view.View)(mostCurrent._b4.getObject()));
 //BA.debugLineNum = 1100;BA.debugLine="b4t.Enabled = False";
_b4t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1101;BA.debugLine="End Sub";
return "";
}
public static String  _b5ani_animationend() throws Exception{
 //BA.debugLineNum = 1259;BA.debugLine="Sub b5ani_animationend";
 //BA.debugLineNum = 1260;BA.debugLine="b5ani.StartAnim(b5)";
mostCurrent._b5ani.StartAnim((android.view.View)(mostCurrent._b5.getObject()));
 //BA.debugLineNum = 1261;BA.debugLine="End Sub";
return "";
}
public static String  _b5t_tick() throws Exception{
 //BA.debugLineNum = 1103;BA.debugLine="Sub b5t_Tick";
 //BA.debugLineNum = 1105;BA.debugLine="b5.Visible = True";
mostCurrent._b5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1106;BA.debugLine="b5ani.SlideFadeToBottom(\"b5ani\",1200,10000)";
mostCurrent._b5ani.SlideFadeToBottom(mostCurrent.activityBA,"b5ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1107;BA.debugLine="b5ani.StartAnim(b5)";
mostCurrent._b5ani.StartAnim((android.view.View)(mostCurrent._b5.getObject()));
 //BA.debugLineNum = 1108;BA.debugLine="b5t.Enabled = False";
_b5t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1109;BA.debugLine="End Sub";
return "";
}
public static String  _b6ani_animationend() throws Exception{
 //BA.debugLineNum = 1263;BA.debugLine="Sub b6ani_animationend";
 //BA.debugLineNum = 1264;BA.debugLine="b6ani.StartAnim(b6)";
mostCurrent._b6ani.StartAnim((android.view.View)(mostCurrent._b6.getObject()));
 //BA.debugLineNum = 1265;BA.debugLine="End Sub";
return "";
}
public static String  _b6t_tick() throws Exception{
 //BA.debugLineNum = 1111;BA.debugLine="Sub b6t_Tick";
 //BA.debugLineNum = 1113;BA.debugLine="b6.Visible = True";
mostCurrent._b6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1114;BA.debugLine="b6ani.SlideFadeToBottom(\"b6ani\",1200,10000)";
mostCurrent._b6ani.SlideFadeToBottom(mostCurrent.activityBA,"b6ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1115;BA.debugLine="b6ani.StartAnim(b6)";
mostCurrent._b6ani.StartAnim((android.view.View)(mostCurrent._b6.getObject()));
 //BA.debugLineNum = 1116;BA.debugLine="b6t.Enabled = False";
_b6t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1117;BA.debugLine="End Sub";
return "";
}
public static String  _b7ani_animationend() throws Exception{
 //BA.debugLineNum = 1267;BA.debugLine="Sub b7ani_animationend";
 //BA.debugLineNum = 1268;BA.debugLine="b7ani.StartAnim(b7)";
mostCurrent._b7ani.StartAnim((android.view.View)(mostCurrent._b7.getObject()));
 //BA.debugLineNum = 1269;BA.debugLine="End Sub";
return "";
}
public static String  _b7t_tick() throws Exception{
 //BA.debugLineNum = 1119;BA.debugLine="Sub b7t_Tick";
 //BA.debugLineNum = 1120;BA.debugLine="b7.Visible = True";
mostCurrent._b7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1121;BA.debugLine="b7ani.SlideFadeToBottom(\"b7ani\",1200,10000)";
mostCurrent._b7ani.SlideFadeToBottom(mostCurrent.activityBA,"b7ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1122;BA.debugLine="b7ani.StartAnim(b7)";
mostCurrent._b7ani.StartAnim((android.view.View)(mostCurrent._b7.getObject()));
 //BA.debugLineNum = 1123;BA.debugLine="b7t.Enabled = False";
_b7t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1124;BA.debugLine="End Sub";
return "";
}
public static String  _b8ani_animationend() throws Exception{
 //BA.debugLineNum = 1271;BA.debugLine="Sub b8ani_animationend";
 //BA.debugLineNum = 1272;BA.debugLine="b8ani.StartAnim(b8)";
mostCurrent._b8ani.StartAnim((android.view.View)(mostCurrent._b8.getObject()));
 //BA.debugLineNum = 1273;BA.debugLine="End Sub";
return "";
}
public static String  _b8t_tick() throws Exception{
 //BA.debugLineNum = 1126;BA.debugLine="Sub b8t_Tick";
 //BA.debugLineNum = 1127;BA.debugLine="b8.Visible = True";
mostCurrent._b8.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1128;BA.debugLine="b8ani.SlideFadeToBottom(\"b8ani\",1200,10000)";
mostCurrent._b8ani.SlideFadeToBottom(mostCurrent.activityBA,"b8ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1129;BA.debugLine="b8ani.StartAnim(b8)";
mostCurrent._b8ani.StartAnim((android.view.View)(mostCurrent._b8.getObject()));
 //BA.debugLineNum = 1130;BA.debugLine="b8t.Enabled = False";
_b8t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1131;BA.debugLine="End Sub";
return "";
}
public static String  _b9ani_animationend() throws Exception{
 //BA.debugLineNum = 1275;BA.debugLine="Sub b9ani_animationend";
 //BA.debugLineNum = 1276;BA.debugLine="b9ani.StartAnim(b9)";
mostCurrent._b9ani.StartAnim((android.view.View)(mostCurrent._b9.getObject()));
 //BA.debugLineNum = 1277;BA.debugLine="End Sub";
return "";
}
public static String  _b9t_tick() throws Exception{
 //BA.debugLineNum = 1133;BA.debugLine="Sub b9t_Tick";
 //BA.debugLineNum = 1134;BA.debugLine="b9.Visible = True";
mostCurrent._b9.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1135;BA.debugLine="b9ani.SlideFadeToBottom(\"b9ani\",1200,10000)";
mostCurrent._b9ani.SlideFadeToBottom(mostCurrent.activityBA,"b9ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1136;BA.debugLine="b9ani.StartAnim(b9)";
mostCurrent._b9ani.StartAnim((android.view.View)(mostCurrent._b9.getObject()));
 //BA.debugLineNum = 1137;BA.debugLine="b9t.Enabled = False";
_b9t.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1138;BA.debugLine="End Sub";
return "";
}
public static String  _barposition_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 209;BA.debugLine="Sub barPosition_ValueChanged (Value As Int, UserCh";
 //BA.debugLineNum = 210;BA.debugLine="If UserChanged = False Then Return 'the value was";
if (_userchanged==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 211;BA.debugLine="mp.Position = Value / 100 * mp.Duration";
mostCurrent._mp.setPosition((int) (_value/(double)100*mostCurrent._mp.getDuration()));
 //BA.debugLineNum = 212;BA.debugLine="If mp.IsPlaying = False Then 'this can happen whe";
if (mostCurrent._mp.IsPlaying()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 213;BA.debugLine="mp.Play";
mostCurrent._mp.Play();
 };
 //BA.debugLineNum = 215;BA.debugLine="timer1_Tick";
_timer1_tick();
 //BA.debugLineNum = 216;BA.debugLine="End Sub";
return "";
}
public static String  _bb_click() throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Sub bb_Click";
 //BA.debugLineNum = 130;BA.debugLine="StartActivity(Step5)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._step5.getObject()));
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static String  _bgroup() throws Exception{
 //BA.debugLineNum = 499;BA.debugLine="Sub bGroup";
 //BA.debugLineNum = 500;BA.debugLine="b1bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b1bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i5.png").getObject()));
 //BA.debugLineNum = 501;BA.debugLine="b2bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b2bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i6.png").getObject()));
 //BA.debugLineNum = 502;BA.debugLine="b3bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b3bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i1.png").getObject()));
 //BA.debugLineNum = 503;BA.debugLine="b4bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b4bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i8.png").getObject()));
 //BA.debugLineNum = 504;BA.debugLine="b5bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b5bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i7.png").getObject()));
 //BA.debugLineNum = 505;BA.debugLine="b6bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b6bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i3.png").getObject()));
 //BA.debugLineNum = 506;BA.debugLine="b7bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b7bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i1.png").getObject()));
 //BA.debugLineNum = 507;BA.debugLine="b8bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b8bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i8.png").getObject()));
 //BA.debugLineNum = 508;BA.debugLine="b9bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._b9bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i2.png").getObject()));
 //BA.debugLineNum = 509;BA.debugLine="b10bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b10bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i6.png").getObject()));
 //BA.debugLineNum = 510;BA.debugLine="b11bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b11bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i4.png").getObject()));
 //BA.debugLineNum = 511;BA.debugLine="b12bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b12bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i1.png").getObject()));
 //BA.debugLineNum = 512;BA.debugLine="b13bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b13bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i9.png").getObject()));
 //BA.debugLineNum = 513;BA.debugLine="b14bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b14bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i2.png").getObject()));
 //BA.debugLineNum = 514;BA.debugLine="b15bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._b15bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","love.png").getObject()));
 //BA.debugLineNum = 515;BA.debugLine="b1.Background = b1bg";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b1bg.getObject()));
 //BA.debugLineNum = 516;BA.debugLine="b2.Background = b2bg";
mostCurrent._b2.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b2bg.getObject()));
 //BA.debugLineNum = 517;BA.debugLine="b3.Background = b3bg";
mostCurrent._b3.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b3bg.getObject()));
 //BA.debugLineNum = 518;BA.debugLine="b4.Background = b4bg";
mostCurrent._b4.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b4bg.getObject()));
 //BA.debugLineNum = 519;BA.debugLine="b5.Background = b5bg";
mostCurrent._b5.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b5bg.getObject()));
 //BA.debugLineNum = 520;BA.debugLine="b6.Background = b6bg";
mostCurrent._b6.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b6bg.getObject()));
 //BA.debugLineNum = 521;BA.debugLine="b7.Background = b7bg";
mostCurrent._b7.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b7bg.getObject()));
 //BA.debugLineNum = 522;BA.debugLine="b8.Background = b8bg";
mostCurrent._b8.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b8bg.getObject()));
 //BA.debugLineNum = 523;BA.debugLine="b9.Background = b9bg";
mostCurrent._b9.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b9bg.getObject()));
 //BA.debugLineNum = 524;BA.debugLine="b10.Background = b10bg";
mostCurrent._b10.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b10bg.getObject()));
 //BA.debugLineNum = 525;BA.debugLine="b11.Background = b11bg";
mostCurrent._b11.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b11bg.getObject()));
 //BA.debugLineNum = 526;BA.debugLine="b12.Background = b12bg";
mostCurrent._b12.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b12bg.getObject()));
 //BA.debugLineNum = 527;BA.debugLine="b13.Background = b13bg";
mostCurrent._b13.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b13bg.getObject()));
 //BA.debugLineNum = 528;BA.debugLine="b14.Background = b14bg";
mostCurrent._b14.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b14bg.getObject()));
 //BA.debugLineNum = 529;BA.debugLine="b15.Background = b15bg";
mostCurrent._b15.setBackground((android.graphics.drawable.Drawable)(mostCurrent._b15bg.getObject()));
 //BA.debugLineNum = 531;BA.debugLine="b1.Visible = False";
mostCurrent._b1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 532;BA.debugLine="b2.Visible = False";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 533;BA.debugLine="b3.Visible = False";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 534;BA.debugLine="b4.Visible = False";
mostCurrent._b4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 535;BA.debugLine="b5.Visible = False";
mostCurrent._b5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 536;BA.debugLine="b6.Visible = False";
mostCurrent._b6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 537;BA.debugLine="b7.Visible = False";
mostCurrent._b7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 538;BA.debugLine="b8.Visible = False";
mostCurrent._b8.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 539;BA.debugLine="b9.Visible = False";
mostCurrent._b9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 540;BA.debugLine="b10.Visible = False";
mostCurrent._b10.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 541;BA.debugLine="b11.Visible = False";
mostCurrent._b11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 542;BA.debugLine="b12.Visible = False";
mostCurrent._b12.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 543;BA.debugLine="b13.Visible = False";
mostCurrent._b13.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 544;BA.debugLine="b14.Visible = False";
mostCurrent._b14.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 545;BA.debugLine="b15.Visible = False";
mostCurrent._b15.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 547;BA.debugLine="b1t.Initialize(\"b1t\",5500)";
_b1t.Initialize(processBA,"b1t",(long) (5500));
 //BA.debugLineNum = 548;BA.debugLine="b2t.Initialize(\"b2t\",6000)";
_b2t.Initialize(processBA,"b2t",(long) (6000));
 //BA.debugLineNum = 549;BA.debugLine="b3t.Initialize(\"b3t\",6500)";
_b3t.Initialize(processBA,"b3t",(long) (6500));
 //BA.debugLineNum = 550;BA.debugLine="b4t.Initialize(\"b4t\",7500)";
_b4t.Initialize(processBA,"b4t",(long) (7500));
 //BA.debugLineNum = 551;BA.debugLine="b5t.Initialize(\"b5t\",8500)";
_b5t.Initialize(processBA,"b5t",(long) (8500));
 //BA.debugLineNum = 552;BA.debugLine="b6t.Initialize(\"b6t\",9500)";
_b6t.Initialize(processBA,"b6t",(long) (9500));
 //BA.debugLineNum = 553;BA.debugLine="b7t.Initialize(\"b7t\",6000)";
_b7t.Initialize(processBA,"b7t",(long) (6000));
 //BA.debugLineNum = 554;BA.debugLine="b8t.Initialize(\"b8t\",5500)";
_b8t.Initialize(processBA,"b8t",(long) (5500));
 //BA.debugLineNum = 555;BA.debugLine="b9t.Initialize(\"b9t\",9500)";
_b9t.Initialize(processBA,"b9t",(long) (9500));
 //BA.debugLineNum = 556;BA.debugLine="b10t.Initialize(\"b10t\",8500)";
_b10t.Initialize(processBA,"b10t",(long) (8500));
 //BA.debugLineNum = 557;BA.debugLine="b11t.Initialize(\"b11t\",5500)";
_b11t.Initialize(processBA,"b11t",(long) (5500));
 //BA.debugLineNum = 558;BA.debugLine="b12t.Initialize(\"b12t\",6500)";
_b12t.Initialize(processBA,"b12t",(long) (6500));
 //BA.debugLineNum = 559;BA.debugLine="b13t.Initialize(\"b13t\",7500)";
_b13t.Initialize(processBA,"b13t",(long) (7500));
 //BA.debugLineNum = 560;BA.debugLine="b14t.Initialize(\"b14t\",7000)";
_b14t.Initialize(processBA,"b14t",(long) (7000));
 //BA.debugLineNum = 561;BA.debugLine="b15t.Initialize(\"b7t\",5000)";
_b15t.Initialize(processBA,"b7t",(long) (5000));
 //BA.debugLineNum = 563;BA.debugLine="b1t.Enabled = True";
_b1t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 564;BA.debugLine="b2t.Enabled = True";
_b2t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 565;BA.debugLine="b3t.Enabled = True";
_b3t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 566;BA.debugLine="b4t.Enabled = True";
_b4t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 567;BA.debugLine="b5t.Enabled = True";
_b5t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 568;BA.debugLine="b6t.Enabled = True";
_b6t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 569;BA.debugLine="b7t.Enabled = True";
_b7t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 570;BA.debugLine="b8t.Enabled = True";
_b8t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 571;BA.debugLine="b9t.Enabled = True";
_b9t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 572;BA.debugLine="b10t.Enabled = True";
_b10t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 573;BA.debugLine="b11t.Enabled = True";
_b11t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 574;BA.debugLine="b12t.Enabled = True";
_b12t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 575;BA.debugLine="b13t.Enabled = True";
_b13t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 576;BA.debugLine="b14t.Enabled = True";
_b14t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 577;BA.debugLine="b15t.Enabled = True";
_b15t.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 578;BA.debugLine="End Sub";
return "";
}
public static String  _converttotimeformat(int _ms) throws Exception{
int _seconds = 0;
int _minutes = 0;
 //BA.debugLineNum = 201;BA.debugLine="Sub ConvertToTimeFormat(ms As Int) As String";
 //BA.debugLineNum = 202;BA.debugLine="Dim seconds, minutes As Int";
_seconds = 0;
_minutes = 0;
 //BA.debugLineNum = 203;BA.debugLine="seconds = Round(ms / 1000)";
_seconds = (int) (anywheresoftware.b4a.keywords.Common.Round(_ms/(double)1000));
 //BA.debugLineNum = 204;BA.debugLine="minutes = Floor(seconds / 60)";
_minutes = (int) (anywheresoftware.b4a.keywords.Common.Floor(_seconds/(double)60));
 //BA.debugLineNum = 205;BA.debugLine="seconds = seconds Mod 60";
_seconds = (int) (_seconds%60);
 //BA.debugLineNum = 206;BA.debugLine="Return NumberFormat(minutes, 1, 0) & \":\" & Number";
if (true) return anywheresoftware.b4a.keywords.Common.NumberFormat(_minutes,(int) (1),(int) (0))+":"+anywheresoftware.b4a.keywords.Common.NumberFormat(_seconds,(int) (2),(int) (0));
 //BA.debugLineNum = 207;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim ad As Timer";
mostCurrent._ad = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 16;BA.debugLine="Dim barPosition As SeekBar";
mostCurrent._barposition = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim lblPosition As Label";
mostCurrent._lblposition = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim anitext As AnimateText";
mostCurrent._anitext = new com.htoophyoe.anitext.animatetext();
 //BA.debugLineNum = 19;BA.debugLine="Dim Img() As String";
mostCurrent._img = new String[(int) (0)];
java.util.Arrays.fill(mostCurrent._img,"");
 //BA.debugLineNum = 20;BA.debugLine="Dim Counter As Int";
_counter = 0;
 //BA.debugLineNum = 21;BA.debugLine="Dim iv1,iv2 As ImageView";
mostCurrent._iv1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._iv2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim lb As Label";
mostCurrent._lb = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13,i1";
mostCurrent._i1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i4 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i5 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i6 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i7 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i8 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i9 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i10 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i11 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i12 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i13 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i14 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._i15 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim i1bg,i2bg,i3bg,i4bg,i5bg,i6bg,i7bg,i8bg,i9bg,";
mostCurrent._i1bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i2bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i3bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i4bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i5bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i6bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i7bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i8bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i9bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i10bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i11bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i12bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i13bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i14bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._i15bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 26;BA.debugLine="Dim i1ani, i2ani, i3ani, i4ani, i5ani, i6ani, i7a";
mostCurrent._i1ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i2ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i3ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i4ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i5ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i6ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i7ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i8ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i9ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i10ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i11ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i12ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i13ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i14ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._i15ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
 //BA.debugLineNum = 27;BA.debugLine="Dim b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11,";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b4 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b5 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b6 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b7 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b8 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b9 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b10 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b11 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b12 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b13 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b14 = new anywheresoftware.b4a.objects.ImageViewWrapper();
mostCurrent._b15 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim b1bg, b2bg, b3bg, b4bg, b5bg, b6bg, b7bg, b8b";
mostCurrent._b1bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b2bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b3bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b4bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b5bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b6bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b7bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b8bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b9bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b10bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b11bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b12bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b13bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b14bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
mostCurrent._b15bg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 29;BA.debugLine="Dim b1ani, b2ani, b3ani, b4ani, b5ani, b6ani, b7a";
mostCurrent._b1ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b2ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b3ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b4ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b5ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b6ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b7ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b8ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b9ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b10ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b11ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b12ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b13ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b14ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
mostCurrent._b15ani = new giuseppe.salvi.icos.library.ICOSSlideAnimation();
 //BA.debugLineNum = 30;BA.debugLine="Dim b As Button";
mostCurrent._b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim time,color As String";
mostCurrent._time = "";
mostCurrent._color = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim love,beiktano,Chococooky,flower,Matrix,Matrix";
mostCurrent._love = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._beiktano = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._chococooky = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._flower = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._matrix = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._matrixsmart = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
mostCurrent._yoeyar = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim mp As MediaPlayer";
mostCurrent._mp = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim abg As BitmapDrawable";
mostCurrent._abg = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 37;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 39;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Dim bb As Button";
mostCurrent._bb = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Dim bbg As ColorDrawable";
mostCurrent._bbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _i10ani_animationend() throws Exception{
 //BA.debugLineNum = 1219;BA.debugLine="Sub i10ani_animationend";
 //BA.debugLineNum = 1220;BA.debugLine="i10ani.StartAnim(i10)";
mostCurrent._i10ani.StartAnim((android.view.View)(mostCurrent._i10.getObject()));
 //BA.debugLineNum = 1221;BA.debugLine="End Sub";
return "";
}
public static String  _i11ani_animationend() throws Exception{
 //BA.debugLineNum = 1223;BA.debugLine="Sub i11ani_animationend";
 //BA.debugLineNum = 1224;BA.debugLine="i11ani.StartAnim(i11)";
mostCurrent._i11ani.StartAnim((android.view.View)(mostCurrent._i11.getObject()));
 //BA.debugLineNum = 1225;BA.debugLine="End Sub";
return "";
}
public static String  _i12ani_animationend() throws Exception{
 //BA.debugLineNum = 1227;BA.debugLine="Sub i12ani_animationend";
 //BA.debugLineNum = 1228;BA.debugLine="i12ani.StartAnim(i12)";
mostCurrent._i12ani.StartAnim((android.view.View)(mostCurrent._i12.getObject()));
 //BA.debugLineNum = 1229;BA.debugLine="End Sub";
return "";
}
public static String  _i13ani_animationend() throws Exception{
 //BA.debugLineNum = 1231;BA.debugLine="Sub i13ani_animationend";
 //BA.debugLineNum = 1232;BA.debugLine="i13ani.StartAnim(i13)";
mostCurrent._i13ani.StartAnim((android.view.View)(mostCurrent._i13.getObject()));
 //BA.debugLineNum = 1233;BA.debugLine="End Sub";
return "";
}
public static String  _i14ani_animationend() throws Exception{
 //BA.debugLineNum = 1235;BA.debugLine="Sub i14ani_animationend";
 //BA.debugLineNum = 1236;BA.debugLine="i14ani.StartAnim(i14)";
mostCurrent._i14ani.StartAnim((android.view.View)(mostCurrent._i14.getObject()));
 //BA.debugLineNum = 1237;BA.debugLine="End Sub";
return "";
}
public static String  _i15ani_animationend() throws Exception{
 //BA.debugLineNum = 1239;BA.debugLine="Sub i15ani_animationend";
 //BA.debugLineNum = 1240;BA.debugLine="i15ani.StartAnim(i15)";
mostCurrent._i15ani.StartAnim((android.view.View)(mostCurrent._i15.getObject()));
 //BA.debugLineNum = 1241;BA.debugLine="End Sub";
return "";
}
public static String  _i1ani_animationend() throws Exception{
 //BA.debugLineNum = 1183;BA.debugLine="Sub i1ani_animationend";
 //BA.debugLineNum = 1184;BA.debugLine="i1ani.StartAnim(i1)";
mostCurrent._i1ani.StartAnim((android.view.View)(mostCurrent._i1.getObject()));
 //BA.debugLineNum = 1185;BA.debugLine="End Sub";
return "";
}
public static String  _i2ani_animationend() throws Exception{
 //BA.debugLineNum = 1187;BA.debugLine="Sub i2ani_animationend";
 //BA.debugLineNum = 1188;BA.debugLine="i2ani.StartAnim(i2)";
mostCurrent._i2ani.StartAnim((android.view.View)(mostCurrent._i2.getObject()));
 //BA.debugLineNum = 1189;BA.debugLine="End Sub";
return "";
}
public static String  _i3ani_animationend() throws Exception{
 //BA.debugLineNum = 1191;BA.debugLine="Sub i3ani_animationend";
 //BA.debugLineNum = 1192;BA.debugLine="i3ani.StartAnim(i3)";
mostCurrent._i3ani.StartAnim((android.view.View)(mostCurrent._i3.getObject()));
 //BA.debugLineNum = 1193;BA.debugLine="End Sub";
return "";
}
public static String  _i4ani_animationend() throws Exception{
 //BA.debugLineNum = 1195;BA.debugLine="Sub i4ani_animationend";
 //BA.debugLineNum = 1196;BA.debugLine="i4ani.StartAnim(i4)";
mostCurrent._i4ani.StartAnim((android.view.View)(mostCurrent._i4.getObject()));
 //BA.debugLineNum = 1197;BA.debugLine="End Sub";
return "";
}
public static String  _i5ani_animationend() throws Exception{
 //BA.debugLineNum = 1199;BA.debugLine="Sub i5ani_animationend";
 //BA.debugLineNum = 1200;BA.debugLine="i5ani.StartAnim(i5)";
mostCurrent._i5ani.StartAnim((android.view.View)(mostCurrent._i5.getObject()));
 //BA.debugLineNum = 1201;BA.debugLine="End Sub";
return "";
}
public static String  _i6ani_animationend() throws Exception{
 //BA.debugLineNum = 1203;BA.debugLine="Sub i6ani_animationend";
 //BA.debugLineNum = 1204;BA.debugLine="i6ani.StartAnim(i6)";
mostCurrent._i6ani.StartAnim((android.view.View)(mostCurrent._i6.getObject()));
 //BA.debugLineNum = 1205;BA.debugLine="End Sub";
return "";
}
public static String  _i7ani_animationend() throws Exception{
 //BA.debugLineNum = 1207;BA.debugLine="Sub i7ani_animationend";
 //BA.debugLineNum = 1208;BA.debugLine="i7ani.StartAnim(i7)";
mostCurrent._i7ani.StartAnim((android.view.View)(mostCurrent._i7.getObject()));
 //BA.debugLineNum = 1209;BA.debugLine="End Sub";
return "";
}
public static String  _i8ani_animationend() throws Exception{
 //BA.debugLineNum = 1211;BA.debugLine="Sub i8ani_animationend";
 //BA.debugLineNum = 1212;BA.debugLine="i8ani.StartAnim(i8)";
mostCurrent._i8ani.StartAnim((android.view.View)(mostCurrent._i8.getObject()));
 //BA.debugLineNum = 1213;BA.debugLine="End Sub";
return "";
}
public static String  _i9ani_animationend() throws Exception{
 //BA.debugLineNum = 1215;BA.debugLine="Sub i9ani_animationend";
 //BA.debugLineNum = 1216;BA.debugLine="i9ani.StartAnim(i9)";
mostCurrent._i9ani.StartAnim((android.view.View)(mostCurrent._i9.getObject()));
 //BA.debugLineNum = 1217;BA.debugLine="End Sub";
return "";
}
public static String  _igroup() throws Exception{
 //BA.debugLineNum = 417;BA.debugLine="Sub iGroup";
 //BA.debugLineNum = 418;BA.debugLine="i1bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i1bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i1.png").getObject()));
 //BA.debugLineNum = 419;BA.debugLine="i2bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i2bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i2.png").getObject()));
 //BA.debugLineNum = 420;BA.debugLine="i3bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i3bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i3.png").getObject()));
 //BA.debugLineNum = 421;BA.debugLine="i4bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i4bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i4.png").getObject()));
 //BA.debugLineNum = 422;BA.debugLine="i5bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i5bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i5.png").getObject()));
 //BA.debugLineNum = 423;BA.debugLine="i6bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i6bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i6.png").getObject()));
 //BA.debugLineNum = 424;BA.debugLine="i7bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i7bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i7.png").getObject()));
 //BA.debugLineNum = 425;BA.debugLine="i8bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i8bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i8.png").getObject()));
 //BA.debugLineNum = 426;BA.debugLine="i9bg.Initialize(LoadBitmap(File.DirRootExternal &";
mostCurrent._i9bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i9.png").getObject()));
 //BA.debugLineNum = 427;BA.debugLine="i10bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i10bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i10.png").getObject()));
 //BA.debugLineNum = 428;BA.debugLine="i11bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i11bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i11.png").getObject()));
 //BA.debugLineNum = 429;BA.debugLine="i12bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i12bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i1.png").getObject()));
 //BA.debugLineNum = 430;BA.debugLine="i13bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i13bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i2.png").getObject()));
 //BA.debugLineNum = 431;BA.debugLine="i14bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i14bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i3.png").getObject()));
 //BA.debugLineNum = 432;BA.debugLine="i15bg.Initialize(LoadBitmap(File.DirRootExternal";
mostCurrent._i15bg.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets","i4.png").getObject()));
 //BA.debugLineNum = 434;BA.debugLine="i1.Background = i1bg";
mostCurrent._i1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i1bg.getObject()));
 //BA.debugLineNum = 435;BA.debugLine="i2.Background = i5bg";
mostCurrent._i2.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i5bg.getObject()));
 //BA.debugLineNum = 436;BA.debugLine="i3.Background = i2bg";
mostCurrent._i3.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i2bg.getObject()));
 //BA.debugLineNum = 437;BA.debugLine="i4.Background = i6bg";
mostCurrent._i4.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i6bg.getObject()));
 //BA.debugLineNum = 438;BA.debugLine="i5.Background = i3bg";
mostCurrent._i5.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i3bg.getObject()));
 //BA.debugLineNum = 439;BA.debugLine="i6.Background = i7bg";
mostCurrent._i6.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i7bg.getObject()));
 //BA.debugLineNum = 440;BA.debugLine="i7.Background = i4bg";
mostCurrent._i7.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i4bg.getObject()));
 //BA.debugLineNum = 441;BA.debugLine="i8.Background = i8bg";
mostCurrent._i8.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i8bg.getObject()));
 //BA.debugLineNum = 442;BA.debugLine="i9.Background = i9bg";
mostCurrent._i9.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i9bg.getObject()));
 //BA.debugLineNum = 443;BA.debugLine="i10.Background = i10bg";
mostCurrent._i10.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i10bg.getObject()));
 //BA.debugLineNum = 444;BA.debugLine="i11.Background = i11bg";
mostCurrent._i11.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i11bg.getObject()));
 //BA.debugLineNum = 445;BA.debugLine="i12.Background = i12bg";
mostCurrent._i12.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i12bg.getObject()));
 //BA.debugLineNum = 446;BA.debugLine="i13.Background = i13bg";
mostCurrent._i13.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i13bg.getObject()));
 //BA.debugLineNum = 447;BA.debugLine="i14.Background = i14bg";
mostCurrent._i14.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i14bg.getObject()));
 //BA.debugLineNum = 448;BA.debugLine="i15.Background = i15bg";
mostCurrent._i15.setBackground((android.graphics.drawable.Drawable)(mostCurrent._i15bg.getObject()));
 //BA.debugLineNum = 450;BA.debugLine="i1.Visible = False";
mostCurrent._i1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 451;BA.debugLine="i2.Visible = False";
mostCurrent._i2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 452;BA.debugLine="i3.Visible = False";
mostCurrent._i3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 453;BA.debugLine="i4.Visible = False";
mostCurrent._i4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 454;BA.debugLine="i5.Visible = False";
mostCurrent._i5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 455;BA.debugLine="i6.Visible = False";
mostCurrent._i6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 456;BA.debugLine="i7.Visible = False";
mostCurrent._i7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 457;BA.debugLine="i8.Visible = False";
mostCurrent._i8.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 458;BA.debugLine="i9.Visible = False";
mostCurrent._i9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 459;BA.debugLine="i10.Visible = False";
mostCurrent._i10.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 460;BA.debugLine="i11.Visible = False";
mostCurrent._i11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 461;BA.debugLine="i12.Visible = False";
mostCurrent._i12.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 462;BA.debugLine="i13.Visible = False";
mostCurrent._i13.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 463;BA.debugLine="i14.Visible = False";
mostCurrent._i14.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 464;BA.debugLine="i15.Visible = False";
mostCurrent._i15.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 466;BA.debugLine="it1.Initialize(\"it1\",1000)";
_it1.Initialize(processBA,"it1",(long) (1000));
 //BA.debugLineNum = 467;BA.debugLine="it2.Initialize(\"it2\",2000)";
_it2.Initialize(processBA,"it2",(long) (2000));
 //BA.debugLineNum = 468;BA.debugLine="it3.Initialize(\"it3\",1000)";
_it3.Initialize(processBA,"it3",(long) (1000));
 //BA.debugLineNum = 469;BA.debugLine="it4.Initialize(\"it4\",4000)";
_it4.Initialize(processBA,"it4",(long) (4000));
 //BA.debugLineNum = 470;BA.debugLine="it5.Initialize(\"it5\",3000)";
_it5.Initialize(processBA,"it5",(long) (3000));
 //BA.debugLineNum = 471;BA.debugLine="it6.Initialize(\"it6\",2500)";
_it6.Initialize(processBA,"it6",(long) (2500));
 //BA.debugLineNum = 472;BA.debugLine="it7.Initialize(\"it7\",3500)";
_it7.Initialize(processBA,"it7",(long) (3500));
 //BA.debugLineNum = 473;BA.debugLine="it8.Initialize(\"it8\",4500)";
_it8.Initialize(processBA,"it8",(long) (4500));
 //BA.debugLineNum = 474;BA.debugLine="it9.Initialize(\"it9\",5000)";
_it9.Initialize(processBA,"it9",(long) (5000));
 //BA.debugLineNum = 475;BA.debugLine="it10.Initialize(\"it10\",5500)";
_it10.Initialize(processBA,"it10",(long) (5500));
 //BA.debugLineNum = 476;BA.debugLine="it11.Initialize(\"it11\",1500)";
_it11.Initialize(processBA,"it11",(long) (1500));
 //BA.debugLineNum = 477;BA.debugLine="it12.Initialize(\"it12\",6000)";
_it12.Initialize(processBA,"it12",(long) (6000));
 //BA.debugLineNum = 478;BA.debugLine="it13.Initialize(\"it13\",7000)";
_it13.Initialize(processBA,"it13",(long) (7000));
 //BA.debugLineNum = 479;BA.debugLine="it14.Initialize(\"it14\",8000)";
_it14.Initialize(processBA,"it14",(long) (8000));
 //BA.debugLineNum = 480;BA.debugLine="it15.Initialize(\"it15\",9000)";
_it15.Initialize(processBA,"it15",(long) (9000));
 //BA.debugLineNum = 482;BA.debugLine="it1.Enabled = True";
_it1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 483;BA.debugLine="it2.Enabled = True";
_it2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 484;BA.debugLine="it3.Enabled = True";
_it3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 485;BA.debugLine="it4.Enabled = True";
_it4.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 486;BA.debugLine="it5.Enabled = True";
_it5.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 487;BA.debugLine="it6.Enabled = True";
_it6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 488;BA.debugLine="it7.Enabled = True";
_it7.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 489;BA.debugLine="it8.Enabled = True";
_it8.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 490;BA.debugLine="it9.Enabled = True";
_it9.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 491;BA.debugLine="it10.Enabled = True";
_it10.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 492;BA.debugLine="it11.Enabled = True";
_it11.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 493;BA.debugLine="it12.Enabled = True";
_it12.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 494;BA.debugLine="it13.Enabled = True";
_it13.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 495;BA.debugLine="it14.Enabled = True";
_it14.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 496;BA.debugLine="it15.Enabled = True";
_it15.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 497;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_adclosed() throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Sub Interstitial_AdClosed";
 //BA.debugLineNum = 138;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return "";
}
public static String  _it1_tick() throws Exception{
 //BA.debugLineNum = 951;BA.debugLine="Sub it1_Tick";
 //BA.debugLineNum = 953;BA.debugLine="i1.Visible = True";
mostCurrent._i1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 954;BA.debugLine="i1ani.SlideFadeToBottom(\"i1ani\",1200,10000)";
mostCurrent._i1ani.SlideFadeToBottom(mostCurrent.activityBA,"i1ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 955;BA.debugLine="i1ani.StartAnim(i1)";
mostCurrent._i1ani.StartAnim((android.view.View)(mostCurrent._i1.getObject()));
 //BA.debugLineNum = 956;BA.debugLine="it1.Enabled = False";
_it1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 957;BA.debugLine="End Sub";
return "";
}
public static String  _it10_tick() throws Exception{
 //BA.debugLineNum = 1023;BA.debugLine="Sub it10_Tick";
 //BA.debugLineNum = 1025;BA.debugLine="i10.Visible = True";
mostCurrent._i10.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1026;BA.debugLine="i10ani.SlideFadeToBottom(\"i10ani\",1200,10000)";
mostCurrent._i10ani.SlideFadeToBottom(mostCurrent.activityBA,"i10ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1027;BA.debugLine="i10ani.StartAnim(i10)";
mostCurrent._i10ani.StartAnim((android.view.View)(mostCurrent._i10.getObject()));
 //BA.debugLineNum = 1028;BA.debugLine="it10.Enabled = False";
_it10.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1029;BA.debugLine="End Sub";
return "";
}
public static String  _it11_tick() throws Exception{
 //BA.debugLineNum = 1031;BA.debugLine="Sub it11_Tick";
 //BA.debugLineNum = 1033;BA.debugLine="i11.Visible = True";
mostCurrent._i11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1034;BA.debugLine="i11ani.SlideFadeToBottom(\"i11ani\",1200,10000)";
mostCurrent._i11ani.SlideFadeToBottom(mostCurrent.activityBA,"i11ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1035;BA.debugLine="i11ani.StartAnim(i11)";
mostCurrent._i11ani.StartAnim((android.view.View)(mostCurrent._i11.getObject()));
 //BA.debugLineNum = 1036;BA.debugLine="it11.Enabled = False";
_it11.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1037;BA.debugLine="End Sub";
return "";
}
public static String  _it12_tick() throws Exception{
 //BA.debugLineNum = 1039;BA.debugLine="Sub it12_Tick";
 //BA.debugLineNum = 1041;BA.debugLine="i12.Visible = True";
mostCurrent._i12.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1042;BA.debugLine="i12ani.SlideFadeToBottom(\"i12ani\",1200,10000)";
mostCurrent._i12ani.SlideFadeToBottom(mostCurrent.activityBA,"i12ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1043;BA.debugLine="i12ani.StartAnim(i12)";
mostCurrent._i12ani.StartAnim((android.view.View)(mostCurrent._i12.getObject()));
 //BA.debugLineNum = 1044;BA.debugLine="it12.Enabled = False";
_it12.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1045;BA.debugLine="End Sub";
return "";
}
public static String  _it13_tick() throws Exception{
 //BA.debugLineNum = 1047;BA.debugLine="Sub it13_Tick";
 //BA.debugLineNum = 1049;BA.debugLine="i13.Visible = True";
mostCurrent._i13.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1050;BA.debugLine="i13ani.SlideFadeToBottom(\"i13ani\",1200,10000)";
mostCurrent._i13ani.SlideFadeToBottom(mostCurrent.activityBA,"i13ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1051;BA.debugLine="i13ani.StartAnim(i13)";
mostCurrent._i13ani.StartAnim((android.view.View)(mostCurrent._i13.getObject()));
 //BA.debugLineNum = 1052;BA.debugLine="it13.Enabled = False";
_it13.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1053;BA.debugLine="End Sub";
return "";
}
public static String  _it14_tick() throws Exception{
 //BA.debugLineNum = 1055;BA.debugLine="Sub it14_Tick";
 //BA.debugLineNum = 1057;BA.debugLine="i14.Visible = True";
mostCurrent._i14.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1058;BA.debugLine="i14ani.SlideFadeToBottom(\"i14ani\",1200,10000)";
mostCurrent._i14ani.SlideFadeToBottom(mostCurrent.activityBA,"i14ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1059;BA.debugLine="i14ani.StartAnim(i14)";
mostCurrent._i14ani.StartAnim((android.view.View)(mostCurrent._i14.getObject()));
 //BA.debugLineNum = 1060;BA.debugLine="it14.Enabled = False";
_it14.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1061;BA.debugLine="End Sub";
return "";
}
public static String  _it15_tick() throws Exception{
 //BA.debugLineNum = 1063;BA.debugLine="Sub it15_Tick";
 //BA.debugLineNum = 1065;BA.debugLine="i15.Visible = True";
mostCurrent._i15.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1066;BA.debugLine="i15ani.SlideFadeToBottom(\"i15ani\",1200,10000)";
mostCurrent._i15ani.SlideFadeToBottom(mostCurrent.activityBA,"i15ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1067;BA.debugLine="i15ani.StartAnim(i15)";
mostCurrent._i15ani.StartAnim((android.view.View)(mostCurrent._i15.getObject()));
 //BA.debugLineNum = 1068;BA.debugLine="it15.Enabled = False";
_it15.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1069;BA.debugLine="End Sub";
return "";
}
public static String  _it2_tick() throws Exception{
 //BA.debugLineNum = 959;BA.debugLine="Sub it2_Tick";
 //BA.debugLineNum = 961;BA.debugLine="i2.Visible = True";
mostCurrent._i2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 962;BA.debugLine="i2ani.SlideFadeToBottom(\"i2ani\",1200,10000)";
mostCurrent._i2ani.SlideFadeToBottom(mostCurrent.activityBA,"i2ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 963;BA.debugLine="i2ani.StartAnim(i2)";
mostCurrent._i2ani.StartAnim((android.view.View)(mostCurrent._i2.getObject()));
 //BA.debugLineNum = 964;BA.debugLine="it2.Enabled = False";
_it2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 965;BA.debugLine="End Sub";
return "";
}
public static String  _it3_tick() throws Exception{
 //BA.debugLineNum = 967;BA.debugLine="Sub it3_Tick";
 //BA.debugLineNum = 969;BA.debugLine="i3.Visible = True";
mostCurrent._i3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 970;BA.debugLine="i3ani.SlideFadeToBottom(\"i3ani\",1200,10000)";
mostCurrent._i3ani.SlideFadeToBottom(mostCurrent.activityBA,"i3ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 971;BA.debugLine="i3ani.StartAnim(i3)";
mostCurrent._i3ani.StartAnim((android.view.View)(mostCurrent._i3.getObject()));
 //BA.debugLineNum = 972;BA.debugLine="it3.Enabled = False";
_it3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 973;BA.debugLine="End Sub";
return "";
}
public static String  _it4_tick() throws Exception{
 //BA.debugLineNum = 975;BA.debugLine="Sub it4_Tick";
 //BA.debugLineNum = 977;BA.debugLine="i4.Visible = True";
mostCurrent._i4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 978;BA.debugLine="i4ani.SlideFadeToBottom(\"i4ani\",1200,10000)";
mostCurrent._i4ani.SlideFadeToBottom(mostCurrent.activityBA,"i4ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 979;BA.debugLine="i4ani.StartAnim(i4)";
mostCurrent._i4ani.StartAnim((android.view.View)(mostCurrent._i4.getObject()));
 //BA.debugLineNum = 980;BA.debugLine="it4.Enabled = False";
_it4.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 981;BA.debugLine="End Sub";
return "";
}
public static String  _it5_tick() throws Exception{
 //BA.debugLineNum = 983;BA.debugLine="Sub it5_Tick";
 //BA.debugLineNum = 985;BA.debugLine="i5.Visible = True";
mostCurrent._i5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 986;BA.debugLine="i5ani.SlideFadeToBottom(\"i5ani\",1200,10000)";
mostCurrent._i5ani.SlideFadeToBottom(mostCurrent.activityBA,"i5ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 987;BA.debugLine="i5ani.StartAnim(i5)";
mostCurrent._i5ani.StartAnim((android.view.View)(mostCurrent._i5.getObject()));
 //BA.debugLineNum = 988;BA.debugLine="it5.Enabled = False";
_it5.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 989;BA.debugLine="End Sub";
return "";
}
public static String  _it6_tick() throws Exception{
 //BA.debugLineNum = 991;BA.debugLine="Sub it6_Tick";
 //BA.debugLineNum = 993;BA.debugLine="i6.Visible = True";
mostCurrent._i6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 994;BA.debugLine="i6ani.SlideFadeToBottom(\"i6ani\",1200,10000)";
mostCurrent._i6ani.SlideFadeToBottom(mostCurrent.activityBA,"i6ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 995;BA.debugLine="i6ani.StartAnim(i6)";
mostCurrent._i6ani.StartAnim((android.view.View)(mostCurrent._i6.getObject()));
 //BA.debugLineNum = 996;BA.debugLine="it6.Enabled = False";
_it6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 997;BA.debugLine="End Sub";
return "";
}
public static String  _it7_tick() throws Exception{
 //BA.debugLineNum = 999;BA.debugLine="Sub it7_Tick";
 //BA.debugLineNum = 1001;BA.debugLine="i7.Visible = True";
mostCurrent._i7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1002;BA.debugLine="i7ani.SlideFadeToBottom(\"i7ani\",1200,10000)";
mostCurrent._i7ani.SlideFadeToBottom(mostCurrent.activityBA,"i7ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1003;BA.debugLine="i7ani.StartAnim(i7)";
mostCurrent._i7ani.StartAnim((android.view.View)(mostCurrent._i7.getObject()));
 //BA.debugLineNum = 1004;BA.debugLine="it7.Enabled = False";
_it7.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1005;BA.debugLine="End Sub";
return "";
}
public static String  _it8_tick() throws Exception{
 //BA.debugLineNum = 1007;BA.debugLine="Sub it8_Tick";
 //BA.debugLineNum = 1009;BA.debugLine="i8.Visible = True";
mostCurrent._i8.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1010;BA.debugLine="i8ani.SlideFadeToBottom(\"i8ani\",1200,10000)";
mostCurrent._i8ani.SlideFadeToBottom(mostCurrent.activityBA,"i8ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1011;BA.debugLine="i8ani.StartAnim(i8)";
mostCurrent._i8ani.StartAnim((android.view.View)(mostCurrent._i8.getObject()));
 //BA.debugLineNum = 1012;BA.debugLine="it8.Enabled = False";
_it8.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1013;BA.debugLine="End Sub";
return "";
}
public static String  _it9_tick() throws Exception{
 //BA.debugLineNum = 1015;BA.debugLine="Sub it9_Tick";
 //BA.debugLineNum = 1017;BA.debugLine="i9.Visible = True";
mostCurrent._i9.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1018;BA.debugLine="i9ani.SlideFadeToBottom(\"i9ani\",1200,10000)";
mostCurrent._i9ani.SlideFadeToBottom(mostCurrent.activityBA,"i9ani",(float) (1200),(long) (10000));
 //BA.debugLineNum = 1019;BA.debugLine="i9ani.StartAnim(i9)";
mostCurrent._i9ani.StartAnim((android.view.View)(mostCurrent._i9.getObject()));
 //BA.debugLineNum = 1020;BA.debugLine="it9.Enabled = False";
_it9.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1021;BA.debugLine="End Sub";
return "";
}
public static String  _iv2_click() throws Exception{
 //BA.debugLineNum = 584;BA.debugLine="Sub iv2_Click";
 //BA.debugLineNum = 585;BA.debugLine="SlideImage";
_slideimage();
 //BA.debugLineNum = 586;BA.debugLine="End Sub";
return "";
}
public static String  _iv2_longclick() throws Exception{
int _id_int = 0;
com.maximus.id.id _id = null;
anywheresoftware.b4a.objects.collections.List _lis = null;
 //BA.debugLineNum = 588;BA.debugLine="Sub iv2_LongClick";
 //BA.debugLineNum = 589;BA.debugLine="Dim id_int As Int";
_id_int = 0;
 //BA.debugLineNum = 590;BA.debugLine="Dim id As id";
_id = new com.maximus.id.id();
 //BA.debugLineNum = 591;BA.debugLine="Dim lis As List";
_lis = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 592;BA.debugLine="lis.Initialize";
_lis.Initialize();
 //BA.debugLineNum = 593;BA.debugLine="lis.AddAll(Array As String(\"280X x 300Y\",\"280X x";
_lis.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"280X x 300Y","280X x 310Y","280X x 320Y","280X x 330Y","280X x 340Y","280X x 360Y","280X x 370Y","280X x 380Y","280X x 390Y","280X x 400Y","Change to Default Size","","300X x 300Y","300X x 310Y","300X x 320Y","300X x 330Y","300X x 340Y","300X x 360Y","300X x 370Y","300X x 380Y","300X x 390Y","300X x 400Y"}));
 //BA.debugLineNum = 594;BA.debugLine="id_int = id.InputList1(lis,\"Choose Image Size!\")";
_id_int = _id.InputList1(_lis,"Choose Image Size!",mostCurrent.activityBA);
 //BA.debugLineNum = 596;BA.debugLine="If id_int = 0 Then";
if (_id_int==0) { 
 //BA.debugLineNum = 597;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 598;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)));
 };
 //BA.debugLineNum = 601;BA.debugLine="If id_int = 1 Then";
if (_id_int==1) { 
 //BA.debugLineNum = 602;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 603;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (310)));
 };
 //BA.debugLineNum = 606;BA.debugLine="If id_int = 2 Then";
if (_id_int==2) { 
 //BA.debugLineNum = 607;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 608;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (320)));
 };
 //BA.debugLineNum = 611;BA.debugLine="If id_int = 3 Then";
if (_id_int==3) { 
 //BA.debugLineNum = 612;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 613;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (330)));
 };
 //BA.debugLineNum = 616;BA.debugLine="If id_int = 4 Then";
if (_id_int==4) { 
 //BA.debugLineNum = 617;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 618;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (340)));
 };
 //BA.debugLineNum = 621;BA.debugLine="If id_int = 5 Then";
if (_id_int==5) { 
 //BA.debugLineNum = 622;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 623;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (360)));
 };
 //BA.debugLineNum = 626;BA.debugLine="If id_int = 6 Then";
if (_id_int==6) { 
 //BA.debugLineNum = 627;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 628;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (370)));
 };
 //BA.debugLineNum = 631;BA.debugLine="If id_int = 7 Then";
if (_id_int==7) { 
 //BA.debugLineNum = 632;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 633;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (380)));
 };
 //BA.debugLineNum = 636;BA.debugLine="If id_int = 8 Then";
if (_id_int==8) { 
 //BA.debugLineNum = 637;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 638;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (390)));
 };
 //BA.debugLineNum = 641;BA.debugLine="If id_int = 9 Then";
if (_id_int==9) { 
 //BA.debugLineNum = 642;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 643;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (400)));
 };
 //BA.debugLineNum = 646;BA.debugLine="If id_int = 10 Then";
if (_id_int==10) { 
 //BA.debugLineNum = 647;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 648;BA.debugLine="Activity.AddView(iv2,50%x - 140dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (140))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)));
 };
 //BA.debugLineNum = 651;BA.debugLine="If id_int = 12 Then";
if (_id_int==12) { 
 //BA.debugLineNum = 652;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 653;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (310)));
 };
 //BA.debugLineNum = 656;BA.debugLine="If id_int = 13 Then";
if (_id_int==13) { 
 //BA.debugLineNum = 657;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 658;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (320)));
 };
 //BA.debugLineNum = 661;BA.debugLine="If id_int = 14 Then";
if (_id_int==14) { 
 //BA.debugLineNum = 662;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 663;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (330)));
 };
 //BA.debugLineNum = 666;BA.debugLine="If id_int = 15 Then";
if (_id_int==15) { 
 //BA.debugLineNum = 667;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 668;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (340)));
 };
 //BA.debugLineNum = 671;BA.debugLine="If id_int = 16 Then";
if (_id_int==16) { 
 //BA.debugLineNum = 672;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 673;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (350)));
 };
 //BA.debugLineNum = 676;BA.debugLine="If id_int = 17 Then";
if (_id_int==17) { 
 //BA.debugLineNum = 677;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 678;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (360)));
 };
 //BA.debugLineNum = 681;BA.debugLine="If id_int = 18 Then";
if (_id_int==18) { 
 //BA.debugLineNum = 682;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 683;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (370)));
 };
 //BA.debugLineNum = 686;BA.debugLine="If id_int = 19 Then";
if (_id_int==19) { 
 //BA.debugLineNum = 687;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 688;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (380)));
 };
 //BA.debugLineNum = 691;BA.debugLine="If id_int = 20 Then";
if (_id_int==20) { 
 //BA.debugLineNum = 692;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 693;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (390)));
 };
 //BA.debugLineNum = 696;BA.debugLine="If id_int = 21 Then";
if (_id_int==21) { 
 //BA.debugLineNum = 697;BA.debugLine="iv2.RemoveView";
mostCurrent._iv2.RemoveView();
 //BA.debugLineNum = 698;BA.debugLine="Activity.AddView(iv2,50%x - 150dip,(lb.Height+lb";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._iv2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150))),(int) ((mostCurrent._lb.getHeight()+mostCurrent._lb.getTop())),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (300)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (400)));
 //BA.debugLineNum = 699;BA.debugLine="Log(\"this 21\")";
anywheresoftware.b4a.keywords.Common.Log("this 21");
 };
 //BA.debugLineNum = 701;BA.debugLine="End Sub";
return "";
}
public static String  _lb_click() throws Exception{
int _id_int = 0;
com.maximus.id.id _id = null;
anywheresoftware.b4a.objects.collections.List _lis = null;
 //BA.debugLineNum = 141;BA.debugLine="Sub lb_Click";
 //BA.debugLineNum = 142;BA.debugLine="Dim id_int As Int";
_id_int = 0;
 //BA.debugLineNum = 143;BA.debugLine="Dim id As id";
_id = new com.maximus.id.id();
 //BA.debugLineNum = 144;BA.debugLine="Dim lis As List";
_lis = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 145;BA.debugLine="lis.Initialize";
_lis.Initialize();
 //BA.debugLineNum = 146;BA.debugLine="lis.AddAll(Array As String(\"BeikThaNo\",\"Chococook";
_lis.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"BeikThaNo","Chococooky","Flower","Matrix","Matrix Smart","YoeYar","Love"}));
 //BA.debugLineNum = 147;BA.debugLine="id_int = id.InputList1(lis,\"Choose Font Style!\")";
_id_int = _id.InputList1(_lis,"Choose Font Style!",mostCurrent.activityBA);
 //BA.debugLineNum = 149;BA.debugLine="If id_int = 0 Then";
if (_id_int==0) { 
 //BA.debugLineNum = 150;BA.debugLine="lb.Typeface = beiktano";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._beiktano.getObject()));
 };
 //BA.debugLineNum = 153;BA.debugLine="If id_int = 1 Then";
if (_id_int==1) { 
 //BA.debugLineNum = 154;BA.debugLine="lb.Typeface = Chococooky";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._chococooky.getObject()));
 };
 //BA.debugLineNum = 157;BA.debugLine="If id_int = 2 Then";
if (_id_int==2) { 
 //BA.debugLineNum = 158;BA.debugLine="lb.Typeface = flower";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._flower.getObject()));
 };
 //BA.debugLineNum = 161;BA.debugLine="If id_int = 3 Then";
if (_id_int==3) { 
 //BA.debugLineNum = 162;BA.debugLine="lb.Typeface = Matrix";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._matrix.getObject()));
 };
 //BA.debugLineNum = 165;BA.debugLine="If id_int = 4 Then";
if (_id_int==4) { 
 //BA.debugLineNum = 166;BA.debugLine="lb.Typeface = MatrixSmart";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._matrixsmart.getObject()));
 };
 //BA.debugLineNum = 169;BA.debugLine="If id_int = 5 Then";
if (_id_int==5) { 
 //BA.debugLineNum = 170;BA.debugLine="lb.Typeface = yoeyar";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._yoeyar.getObject()));
 };
 //BA.debugLineNum = 173;BA.debugLine="If id_int = 6 Then";
if (_id_int==6) { 
 //BA.debugLineNum = 174;BA.debugLine="lb.Typeface = love";
mostCurrent._lb.setTypeface((android.graphics.Typeface)(mostCurrent._love.getObject()));
 };
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return "";
}
public static String  _lb_longclick() throws Exception{
anywheresoftware.b4a.agraham.dialogs.InputDialog.ColorDialog _cd = null;
int _i = 0;
 //BA.debugLineNum = 179;BA.debugLine="Sub lb_LongClick";
 //BA.debugLineNum = 180;BA.debugLine="Dim cd As ColorDialog";
_cd = new anywheresoftware.b4a.agraham.dialogs.InputDialog.ColorDialog();
 //BA.debugLineNum = 181;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 182;BA.debugLine="cd.RGB = Colors.DarkGray";
_cd.setRGB(anywheresoftware.b4a.keywords.Common.Colors.DarkGray);
 //BA.debugLineNum = 183;BA.debugLine="i = cd.Show(\"B4A ColorPicker Dialog\", \"Yes\", \"No\"";
_i = _cd.Show("B4A ColorPicker Dialog","Yes","No","",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 184;BA.debugLine="If i = DialogResponse.POSITIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 185;BA.debugLine="lb.TextColor = cd.RGB";
mostCurrent._lb.setTextColor(_cd.getRGB());
 //BA.debugLineNum = 186;BA.debugLine="Log(cd)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_cd));
 };
 //BA.debugLineNum = 188;BA.debugLine="If i = DialogResponse.NEGATIVE Then";
if (_i==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 189;BA.debugLine="lb.TextColor = \"\"";
mostCurrent._lb.setTextColor((int)(Double.parseDouble("")));
 };
 //BA.debugLineNum = 191;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim t As Timer";
_t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 9;BA.debugLine="Dim it1,it2,it3,it4,it5,it6,it7,it8,it9,it10,it11";
_it1 = new anywheresoftware.b4a.objects.Timer();
_it2 = new anywheresoftware.b4a.objects.Timer();
_it3 = new anywheresoftware.b4a.objects.Timer();
_it4 = new anywheresoftware.b4a.objects.Timer();
_it5 = new anywheresoftware.b4a.objects.Timer();
_it6 = new anywheresoftware.b4a.objects.Timer();
_it7 = new anywheresoftware.b4a.objects.Timer();
_it8 = new anywheresoftware.b4a.objects.Timer();
_it9 = new anywheresoftware.b4a.objects.Timer();
_it10 = new anywheresoftware.b4a.objects.Timer();
_it11 = new anywheresoftware.b4a.objects.Timer();
_it12 = new anywheresoftware.b4a.objects.Timer();
_it13 = new anywheresoftware.b4a.objects.Timer();
_it14 = new anywheresoftware.b4a.objects.Timer();
_it15 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 10;BA.debugLine="Dim b1t, b2t, b3t, b4t, b5t, b6t, b7t, b8t, b9t,";
_b1t = new anywheresoftware.b4a.objects.Timer();
_b2t = new anywheresoftware.b4a.objects.Timer();
_b3t = new anywheresoftware.b4a.objects.Timer();
_b4t = new anywheresoftware.b4a.objects.Timer();
_b5t = new anywheresoftware.b4a.objects.Timer();
_b6t = new anywheresoftware.b4a.objects.Timer();
_b7t = new anywheresoftware.b4a.objects.Timer();
_b8t = new anywheresoftware.b4a.objects.Timer();
_b9t = new anywheresoftware.b4a.objects.Timer();
_b10t = new anywheresoftware.b4a.objects.Timer();
_b11t = new anywheresoftware.b4a.objects.Timer();
_b12t = new anywheresoftware.b4a.objects.Timer();
_b13t = new anywheresoftware.b4a.objects.Timer();
_b14t = new anywheresoftware.b4a.objects.Timer();
_b15t = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 11;BA.debugLine="Dim timer1 As Timer";
_timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _slideimage() throws Exception{
 //BA.debugLineNum = 1303;BA.debugLine="Sub SlideImage";
 //BA.debugLineNum = 1304;BA.debugLine="Img =  Array As String(\"h1.jpg\",\"h2.jpg\",\"h3.jpg\"";
mostCurrent._img = new String[]{"h1.jpg","h2.jpg","h3.jpg","h4.jpg","h5.jpg","h6.jpg","h7.jpg","h8.jpg","h9.jpg","h10.jpg"};
 //BA.debugLineNum = 1305;BA.debugLine="iv2.SetBackgroundImage(LoadBitmap(File.DirRootExt";
mostCurrent._iv2.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.vDayAppMaker/assets",mostCurrent._img[_counter]).getObject()));
 //BA.debugLineNum = 1306;BA.debugLine="Counter = Counter + 1";
_counter = (int) (_counter+1);
 //BA.debugLineNum = 1307;BA.debugLine="If Counter > 9 Then Counter = 0";
if (_counter>9) { 
_counter = (int) (0);};
 //BA.debugLineNum = 1308;BA.debugLine="Log(\"This is 10 > \" & Counter)";
anywheresoftware.b4a.keywords.Common.Log("This is 10 > "+BA.NumberToString(_counter));
 //BA.debugLineNum = 1309;BA.debugLine="End Sub";
return "";
}
public static String  _t_tick() throws Exception{
 //BA.debugLineNum = 580;BA.debugLine="Sub t_Tick";
 //BA.debugLineNum = 581;BA.debugLine="SlideImage";
_slideimage();
 //BA.debugLineNum = 582;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 193;BA.debugLine="Sub timer1_Tick";
 //BA.debugLineNum = 194;BA.debugLine="If mp.IsPlaying Then";
if (mostCurrent._mp.IsPlaying()) { 
 //BA.debugLineNum = 195;BA.debugLine="barPosition.Value = mp.Position / mp.Duration *";
mostCurrent._barposition.setValue((int) (mostCurrent._mp.getPosition()/(double)mostCurrent._mp.getDuration()*100));
 //BA.debugLineNum = 196;BA.debugLine="lblPosition.Text = ConvertToTimeFormat(mp.Positi";
mostCurrent._lblposition.setText((Object)(_converttotimeformat(mostCurrent._mp.getPosition())+" ("+_converttotimeformat(mostCurrent._mp.getDuration())+")"));
 };
 //BA.debugLineNum = 199;BA.debugLine="End Sub";
return "";
}
}
