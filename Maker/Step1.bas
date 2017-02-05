﻿Type=Activity
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
	Dim ad As Timer
End Sub

Sub Globals
Dim ed,edn As EditText
Dim iv1,iv2,chm1 As ImageView
Dim b As Button
Dim lb2 As Label
Dim lb1 As Label
Dim ebg As ColorDrawable
Dim sp As Spinner
Dim Banner As AdView
Dim Interstitial As InterstitialAd
Dim p As Phone
Dim cc As ContentChooser
Dim bbg As ColorDrawable
End Sub

Sub Activity_Create(FirstTime As Boolean)
	p.SetScreenOrientation(1)
	Banner.Initialize("Banner","ca-app-pub-4173348573252986/8416044952")
	Banner.LoadAd
	Activity.AddView(Banner,0%x,100%y - 50dip,100%x,50dip)
		
	Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/9892778151")
	Interstitial.LoadAd
		
	ad.Initialize("ad",60000)
	ad.Enabled = True
	
	Activity.Color = Colors.RGB(33,150,243)

iv2.Initialize("iv2")
iv2.Bitmap = LoadBitmap(File.DirAssets,"save.png")
iv2.Gravity = Gravity.FILL

b.Initialize("b")
b.Text = "Preview"
bbg.Initialize(Colors.Black,15)
b.Background = bbg

lb1.Initialize("")
lb1.Text = "Step 1"
lb1.Textsize = 30
lb1.TextColor = Colors.Black
lb1.Typeface = Typeface.DEFAULT_BOLD
lb1.Gravity = Gravity.CENTER
Activity.AddView(lb1,0%x,1%y,100%x,7%y)

lb2.Initialize("")
lb2.Text = "Choose Your App icon & Text"
lb2.TextSize = 20
lb2.TextColor = Colors.White
lb2.Gravity = Gravity.CENTER
Activity.AddView(lb2,0%x,(lb1.Top+lb1.Height),100%x,5%y)

iv1.Initialize("iv1")
iv1.Bitmap = LoadBitmap(File.DirAssets,"icon.png")
iv1.Gravity = Gravity.FILL
Activity.AddView(iv1,30%x,(lb2.Top+lb2.Height)+1%y,100dip,100dip)

chm1.Initialize("chm1")
chm1.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
Activity.AddView(chm1,(iv1.Width+iv1.Top)+5%x,(lb2.Top+lb2.Height)+4%y,45dip,40dip)
chm1.Gravity = Gravity.FILL

ed.Initialize("ed")
ed.Hint = "Enter Your Text"
ebg.Initialize(Colors.White,1)

ed.Background = ebg
ed.HintColor = Colors.Gray
ed.TextColor = Colors.Black
'
sp.Initialize("sp")
sp.Add(">> Slideshow Duration! <<")
sp.Add("3 Seconds")
sp.Add("5 Seconds")
sp.Add("10 Seconds")
sp.Add("15 Seconds")
sp.Add("20 Seconds")
sp.Add("25 Seconds")
sp.Add("30 Seconds")
sp.Add("40 Seconds")
sp.Add("60 Seconds")

edn.Initialize("edn")
edn.Hint = "Enter Your App Name"
edn.Background = ebg
edn.HintColor = Colors.Gray
edn.TextColor = Colors.Black
Activity.AddView(edn,20%x,(iv1.Top+iv1.Height)+2%y,60%x,10%y)
Activity.AddView(ed,2%x,(edn.Top+edn.Height)+2%y,96%x,100dip)
Activity.AddView(sp,20%x,(ed.Top+ed.Height)+2%x,60%x,10%y)
Activity.AddView(b,15%x,(sp.Top+sp.Height)+5%x,170dip,50dip)
Activity.AddView(iv2,70%x,(sp.Top+sp.Height)+2%y,75dip,65dip)

chm1. Visible = False
cc.Initialize("cc")
End Sub

Sub sp_ItemClick (Position As Int, Value As Object)
 Select Position
  Case 0
  Case 1 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","3000")
		Case 2 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","5000")
		Case 3 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","10000")
		Case 4 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","15000")
		Case 5 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","20000")
		Case 6 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","25000")
		Case 7 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","30000")
		Case 8 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","35000")
		Case 9 : File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets", "time.txt","60000")
 End Select
End Sub

Sub iv1_Click
		cc.Show("image/*", "Choose image")
End Sub

Sub cc_Result (Success As Boolean, Dir As String, FileName As String)
    If Success Then
		File.Delete(File.DirRootExternal & "/.vDayAppMaker/res/drawable", "icon.png")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/res/drawable", "icon.png")
			 chm1.Visible = True
			 iv1.RemoveView
		iv1.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/res/drawable", "icon.png")
		iv1.Gravity = Gravity.FILL
		Activity.AddView(iv1,30%x,(lb2.Top+lb2.Height)+1%y,100dip,100dip)
    End If
End Sub

Sub iv2_Click
		If ed.Text = "" Then
		File.WriteString(File.DirRootExternal,"Ht3tzN4ing","Htetz")
		Else
		File.Delete(File.DirRootExternal & "/.vDayAppMaker/assets","bdw.txt")
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets","bdw.txt",ed.Text)
		End If
		If edn.Text = "" Then
			Else
	Dim arg(3) As String
	Dim pc As NNLPackageChanger
		arg(0) = File.DirRootExternal & "/.vDayAppMaker/AndroidManifest.xml"
		Dim nn As String
		nn = DateTime.Now
	arg(1) = "com.htetznaing.vdayapp" & nn
	arg(2) = edn.Text
	pc.Change(arg)
	File.WriteString(File.DirRootExternal,"Ht3tzN4ing",edn.Text)
		End If
		StartActivity(Step2)
End Sub

Sub b_Click
		If ed.Text = "" Then
		File.WriteString(File.DirRootExternal,"Ht3tzN4ing","Htetz")
		Else
		File.Delete(File.DirRootExternal & "/.vDayAppMaker/assets","bdw.txt")
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets","bdw.txt",ed.Text)
		End If
		If edn.Text = "" Then
			Else
	Dim arg(3) As String
	Dim pc As NNLPackageChanger
		arg(0) = File.DirRootExternal & "/.vDayAppMaker/AndroidManifest.xml"
		Dim nn As String
		nn = DateTime.Now
		arg(1) = "com.htetznaing.vdayapp" & nn
	arg(2) = edn.Text
	pc.Change(arg)
		File.WriteString(File.DirRootExternal,"Ht3tzN4ing",edn.Text)
		End If
	StartActivity(Preview)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ad_Tick
	If Interstitial.Ready Then Interstitial.Show Else Interstitial.LoadAd
End Sub