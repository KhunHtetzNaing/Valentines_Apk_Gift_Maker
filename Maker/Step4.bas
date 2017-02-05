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
	Dim MediaPlayer1 As MediaPlayer
	Dim timer1 As Timer
	Dim ad As Timer
End Sub

Sub Globals
Dim lb1,lb2 As Label

	Dim barPosition As SeekBar
	Dim lblPosition As Label
	Dim iv1 As ImageView
Dim chm1 As ImageView
	Dim iv2 As ImageView
	Dim iv3 As ImageView
Dim  iv4 As ImageView
	Dim Banner As AdView
	Dim Interstitial As InterstitialAd
Dim p As Phone
Dim c1 As ContentChooser
Dim no As EditText
Dim bbg As ColorDrawable
	Private b As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	bbg.Initialize(Colors.Black,15)
	c1.Initialize("c1")
	
	p.SetScreenOrientation(1)
	Banner.Initialize("Banner","ca-app-pub-4173348573252986/8416044952")
	Banner.LoadAd
	Activity.AddView(Banner,0%x,100%y - 50dip,100%x,50dip)
		
	Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/9892778151")
	Interstitial.LoadAd
		
	ad.Initialize("ad",60000)
	ad.Enabled = True
'	
		MediaPlayer1.Initialize( )
		timer1.Initialize("timer1", 100)
			
	Activity.Color = Colors.RGB(33,150,243)
	
Activity.LoadLayout("l4")
lb1.Text = "Step 4"
lb1.Textsize = 30
lb1.TextColor = Colors.Black
lb1.Typeface = Typeface.DEFAULT_BOLD
lb1.Gravity = Gravity.CENTER
lb2.Text = "Choose Your Music Or Next"
lb2.TextSize = 20
lb2.TextColor = Colors.White
lb2.Gravity = Gravity.CENTER

chm1.Visible = False

no.InputType = no.INPUT_TYPE_PHONE
no.Color = Colors.White
no.Hint = "Enter Phone Number"
b.Background = bbg
no.TextColor = Colors.Black
End Sub

Sub iv1_Click
	c1.Show("audio/*","Choose audio file")
End Sub

Sub c1_Result (Success As Boolean, Dir As String, FileName As String)
    If Success Then
		If File.Exists( File.DirRootExternal & "/.vDayAppMaker/assets", "vday.mp3") = True Then
		File.Delete( File.DirRootExternal & "/.vDayAppMaker/assets", "vday.mp3")
		End If
		File.Copy(Dir, FileName, File.DirRootExternal & "/.vDayAppMaker/assets", "vday.mp3")
			 chm1.Visible = True
    End If
End Sub

Sub iv2_Click
	If MediaPlayer1.IsPlaying = False Then
		MediaPlayer1.Load(File.DirRootExternal & "/.vDayAppMaker/assets","vday.mp3")
	MediaPlayer1.Play
	timer1.Enabled = True
	End If
End Sub

Sub iv3_Click
	If MediaPlayer1.IsPlaying Then
	MediaPlayer1.Stop
	End If
End Sub

Sub iv4_Click
	If no.Text = "" Then
		Else
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets","number.txt",no.Text)
	End If
	StartActivity(Step5)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub timer1_Tick
	If MediaPlayer1.IsPlaying Then
		barPosition.Value = MediaPlayer1.Position / MediaPlayer1.Duration * 100
		lblPosition.Text = "Position: " & ConvertToTimeFormat(MediaPlayer1.Position) & _
			" (" & ConvertToTimeFormat(MediaPlayer1.Duration) & ")"
	End If
End Sub

Sub ConvertToTimeFormat(ms As Int) As String
	Dim seconds, minutes As Int
	seconds = Round(ms / 1000)
	minutes = Floor(seconds / 60)
	seconds = seconds Mod 60
	Return NumberFormat(minutes, 1, 0) & ":" & NumberFormat(seconds, 2, 0) 'ex: 3:05
End Sub

Sub barPosition_ValueChanged (Value As Int, UserChanged As Boolean)
	If UserChanged = False Then Return 'the value was changed programmatically
	MediaPlayer1.Position = Value / 100 * MediaPlayer1.Duration
	If MediaPlayer1.IsPlaying = False Then 'this can happen when the playback reached the end and the user changes the position
		MediaPlayer1.Play
	End If
	timer1_Tick 'immediately update the progress label
End Sub

Sub b_Click
	If no.Text = "" Then
	Else
		File.WriteString(File.DirRootExternal & "/.vDayAppMaker/assets","number.txt",no.Text)
	End If
	StartActivity(Preview)
End Sub

Sub ad_Tick
	If Interstitial.Ready Then Interstitial.Show Else Interstitial.LoadAd
End Sub