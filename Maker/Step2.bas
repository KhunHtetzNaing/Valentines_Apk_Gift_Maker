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
	Dim ad As Timer
	dim ibc as ContentChooser
End Sub

Sub Globals
	Dim bbg As ColorDrawable
	Dim iv1,iv2,iv3,iv4,iv5,iv6 As ImageView
	Dim chm1,chm2,chm3,chm6,chm5,chm7 As ImageView
	Dim b As Button
	Dim lb1,lb2 As Label
	Dim Banner As AdView
	Dim Interstitial As InterstitialAd
	Dim p As Phone
	Dim c1,c2,c3,c4,c5 As ContentChooser
	
	Dim ibg As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	c1.Initialize("c1")
	c2.Initialize("c2")
	c3.Initialize("c3")
	c4.Initialize("c4")
	c5.Initialize("c5")
	
	p.SetScreenOrientation(1)
	Banner.Initialize("Banner","ca-app-pub-4173348573252986/8416044952")
	Banner.LoadAd
	Activity.AddView(Banner,0%x,100%y - 50dip,100%x,50dip)
	
	Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/9892778151")
	Interstitial.LoadAd
	
	ad.Initialize("ad",60000)
	ad.Enabled = True
	
	Activity.Color = Colors.RGB(33,150,243)

	lb1.Initialize("")
	lb1.Text = "Step 2"
	lb1.Textsize = 30
	lb1.TextColor = Colors.Black
	lb1.Typeface = Typeface.DEFAULT_BOLD
	lb1.Gravity = Gravity.CENTER
	Activity.AddView(lb1,0%x,1%y,100%x,7%y)

	lb2.Initialize("")
	lb2.Text =  "Choose Your Image Or Next"
	lb2.TextSize = 20
	lb2.TextColor = Colors.White
	lb2.Gravity = Gravity.CENTER
	Activity.AddView(lb2,0%x,(lb1.Top+lb1.Height),100%x,5%y)

	'Imv1
	iv1.Initialize("iv1")
	iv1.Bitmap = LoadBitmap(File.DirAssets,"chooseimage.png")
	iv1.Gravity = Gravity.FILL
	Activity.AddView(iv1,1%x,(lb2.Top+lb2.Height)+2%y,80dip,80dip)

	chm1.Initialize("chm1")
	chm1.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm1.Gravity = Gravity.FILL
	Activity.AddView(chm1,(iv1.Top)+1%x,(lb2.Top+lb2.Height)+4%y,45dip,40dip)

	'Imv2
	iv2.Initialize("iv2")
	iv2.Bitmap = LoadBitmap(File.DirAssets,"chooseimage.png")
	iv2.Gravity = Gravity.FILL
	Activity.AddView(iv2,(chm1.Top+chm1.Width)+6%x,(lb2.Top+lb2.Height)+2%y,80dip,80dip)

	chm2.Initialize("chm2")
	chm2.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm2.Gravity = Gravity.FILL
	Activity.AddView(chm2,(iv2.Width+iv2.Top+iv1.Width)+4%x,(lb2.Top+lb2.Height)+4%y,45dip,40dip)

	'Imv3
	iv3.Initialize("iv3")
	iv3.Bitmap = LoadBitmap(File.DirAssets,"chooseimage.png")
	iv3.Gravity = Gravity.FILL
	Activity.AddView(iv3,1%x,(iv2.Top+iv2.Height)+2%y,80dip,80dip)

	chm3.Initialize("chm3")
	chm3.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm3.Gravity = Gravity.FILL
	Activity.AddView(chm3,(iv1.Top)+1%x,(iv2.Top+iv2.Height)+4%y,45dip,40dip)

	'Imv5
	iv5.Initialize("iv5")
	iv5.Bitmap = LoadBitmap(File.DirAssets,"chooseimage.png")
	iv5.Gravity = Gravity.FILL
	Activity.AddView(iv5,(chm1.Top+chm1.Width)+6%x,(iv2.Top+iv2.Height)+2%y,80dip,80dip)

	chm5.Initialize("chm5")
	chm5.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm5.Gravity = Gravity.FILL
	Activity.AddView(chm5,(iv2.Width+iv2.Top+iv1.Width)+4%x,(iv2.Top+iv2.Height)+4%y,45dip,40dip)

	'Imv6
	iv6.Initialize("iv6")
	iv6.Bitmap = LoadBitmap(File.DirAssets,"chooseimage.png")
	iv6.Gravity = Gravity.FILL
	Activity.AddView(iv6,1%x,(iv3.Top+iv3.Height)+2%y,80dip,80dip)

	chm6.Initialize("chm6")
	chm6.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm6.Gravity = Gravity.FILL
	Activity.AddView(chm6,(iv1.Top)+1%x,(iv3.Top+iv3.Height)+4%y,45dip,40dip)

	chm7.Initialize("chm7")
	chm7.Bitmap = LoadBitmap(File.DirAssets,"checkmark.png")
	chm7.Gravity = Gravity.FILL
	Activity.AddView(chm7,(iv2.Width+iv2.Top+iv1.Width)+4%x,(iv3.Top+iv3.Height)+4%y,45dip,40dip)

	ibg.Initialize("ibg")
	ibc.Initialize("ibc")
	bbg.Initialize(Colors.Black,15)
	ibg.Text = "Backgroud Image"
	ibg.Background = bbg
	Activity.AddView(ibg,(chm1.Top+chm1.Width)+6%x,(iv5.Top+iv5.Height)+10dip,45%x,50dip)
	
	iv4.Initialize("iv4")
	iv4.Bitmap = LoadBitmap(File.DirAssets,"save.png")
	iv4.Gravity = Gravity.FILL
	Activity.AddView(iv4,70%x,(iv6.Top+iv6.Height)+2%y,75dip,65dip)

	b.Initialize("b")
	b.Text = "Preview"
	b.Background = bbg
	Activity.AddView(b,15%x,(iv6.Top+iv6.Height)+5%y,170dip,50dip)

	chm1.Visible = False
	chm2.Visible =False
	chm3.Visible =False
	chm5.Visible = False
	chm6.Visible = False
	chm7.Visible = False
 
End Sub

Sub ibg_Click
	ibc.Show("image/*","Choose Image")
End Sub

Sub ibc_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "bg.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "bg.jpg")
		chm1.Visible = True
		ToastMessageShow("Completed",True)
		End If
End Sub

Sub b_Click
	StartActivity(Preview)
End Sub

Sub iv4_Click
	StartActivity(Step3)
End Sub

Sub iv1_Click
	c1.Show("image/*", "Choose image")
End Sub

Sub c1_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "h1.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "h1.jpg")
		chm1.Visible = True
		
		iv1.RemoveView
		iv1.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/assets", "h1.jpg")
		iv1.Gravity = Gravity.FILL
		Activity.AddView(iv1,1%x,(lb2.Top+lb2.Height)+2%y,80dip,80dip)
	End If
End Sub

Sub iv2_Click
	c2.Show("image/*", "Choose image")
End Sub

Sub c2_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "h2.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "h2.jpg")
		chm2.Visible = True
		
		iv2.RemoveView
		iv2.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/assets", "h2.jpg")
		iv2.Gravity = Gravity.FILL
		Activity.AddView(iv2,(chm1.Top+chm1.Width)+6%x,(lb2.Top+lb2.Height)+2%y,80dip,80dip)
	End If
End Sub

Sub iv3_Click
	c3.Show("image/*", "Choose image")
End Sub

Sub c3_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "h3.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "h3.jpg")
		chm3.Visible = True
		
		iv3.RemoveView
		iv3.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/assets", "h3.jpg")
		iv3.Gravity = Gravity.FILL
		Activity.AddView(iv3,1%x,(iv2.Top+iv2.Height)+2%y,80dip,80dip)
	End If
End Sub

Sub iv5_Click
	c4.Show("image/*", "Choose image")
End Sub

Sub c4_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "h4.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "h4.jpg")
		chm5.Visible = True
		
		iv5.RemoveView
		iv5.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/assets", "h4.jpg")
		iv5.Gravity = Gravity.FILL
		Activity.AddView(iv5,(chm1.Top+chm1.Width)+6%x,(iv2.Top+iv2.Height)+2%y,80dip,80dip)
	End If
End Sub

Sub iv6_Click
	c5.Show("image/*", "Choose image")
End Sub

Sub c5_Result (Success As Boolean, Dir As String, FileName As String)
	If Success Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "h5.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "h5.jpg")
		chm6.Visible = True
		
		iv6.RemoveView
		iv6.Gravity = Gravity.FILL
		iv6.Bitmap = LoadBitmap(File.DirRootExternal & "/.vDayAppMaker/assets", "h5.jpg")
		Activity.AddView(iv6,1%x,(iv3.Top+iv3.Height)+2%y,80dip,80dip)
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ad_Tick
	If Interstitial.Ready Then Interstitial.Show Else Interstitial.LoadAd
End Sub