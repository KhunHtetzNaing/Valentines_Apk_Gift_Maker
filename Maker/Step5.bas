Type=Activity
Version=6.5
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: True
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
Dim T As Timer
	Dim ad As Timer
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
Dim zip As ABZipUnzip
Dim zs As NNLZipSigner
Dim sd As String
	Private lb1 As Label
	Private lb2 As Label
	Private b As Button
	Dim ml As MLfiles
	Dim Banner As AdView
	Dim Interstitial As InterstitialAd
Dim p As Phone
Dim iv1 As ImageView
	Private sms As EditText
	Dim lbf As Label
	Dim bbg As ColorDrawable
	Private b1 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	bbg.Initialize(Colors.Black,15)
	p.SetScreenOrientation(1)
	Banner.Initialize("Banner","ca-app-pub-4173348573252986/8416044952")
	Banner.LoadAd
	Activity.AddView(Banner,0%x,100%y - 50dip,100%x,50dip)
		
	Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/9892778151")
	Interstitial.LoadAd
	
	ad.Initialize("ad",60000)
	
		ad.Enabled = True
	sd =  File.DirRootExternal & "/"

	Activity.Color = Colors.RGB(33,150,243)
	
Activity.LoadLayout("FinalStep")

lb1.Textsize = 30
lb1.TextColor = Colors.Black
lb1.Typeface = Typeface.DEFAULT_BOLD
lb1.Gravity = Gravity.CENTER
lb2.Text = "Build Your Android App Now!"
lb2.TextSize = 20
lb2.TextColor = Colors.White
lb2.Gravity = Gravity.CENTER
T.Initialize("T",1000)
T.Enabled = False

 lbf.Initialize("lbf")
 lbf.Text = "Developed By Khun Htetz Naing"
 Activity.AddView(lbf,0%x,85%y,100%x,5%y)
 lbf.Gravity = Gravity.CENTER
 lbf.TextColor = Colors.White
 
 sms.Color = Colors.White
 sms.Hint = "Enter Your Message"
 sms.TextColor = Colors.Black
 b.Background = bbg
 b1.Background= bbg
End Sub

Sub b1_Click
	If sms.Text = "" Then
		Else
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "message.txt",sms.Text)
	End If
	StartActivity(Preview)
End Sub

Sub b_Click
	If sms.Text = "" Then
	Else
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "message.txt",sms.Text)
	End If
	ProgressDialogShow("Building Your Apk!" & CRLF & "Please Wait...")
	T.Enabled = True
	zs.Initialize
End Sub

Sub T_Tick
	Dim nam As String
	nam =File.ReadString(File.DirRootExternal,"Ht3tzN4ing")
	
	zip.ABZipDirectory(sd & ".vDayAppMaker" , sd & "vDayApp.apk") '--------------  project_hello ကို hello.apk ျဖစ္ေအာင္ ျပန္ပိတ္မယ္။
	zs.SignZip(sd & "vDayApp.apk" , sd & nam&"_vDayApp.apk") '--------------- Sign လုပ္မယ္ New apk ကို hello_Output.apk အမည္နဲ႕ ထုတ္မယ္။
	
	ml.rm(sd & "vDayApp.apk") '---------------  sdcard ထဲ႕ hello.apk အေဟာင္းကို ဖ်က္မယ္။
'	   ml.rmrf(sd & "GiftAppMaker") '--------------- sdcard ထဲက project_hello Folder ကိုလည္း ဖ်က္မယ္။
	 ProgressDialogHide
	ToastMessageShow ("Successfully Created " & CRLF & nam&"_vDayApp.apk in SdCard!",True)     '---------  ပီးပီေပါ့
		T.Enabled = False
Dim in As Intent
	in.Initialize(in.ACTION_VIEW,"file:///"&sd &  nam&"_vDayApp.apk")
in.SetType("application/vnd.android.package-archive")
If in.IsInitialized Then 
 StartActivity(in)
 Else
 Msgbox("Please Restart Project","Attention!")
 End If
 
 File.Delete(File.DirRootExternal,"Ht3tzN4ing")
End Sub


Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ad_Tick
	If Interstitial.Ready Then Interstitial.Show Else Interstitial.LoadAd
End Sub