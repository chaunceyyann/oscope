package cc.arduino.adk.processing;

import processing.app.Editor;
import processing.app.Sketch;
import processing.app.Base;
import processing.mode.android.AndroidMode;
import processing.app.SketchException;
import java.io.IOException;

class MyCompileThread extends Thread {
	Editor editor;
	
	MyBuild build;
	MyRunner runner;
	
	public MyCompileThread (Editor editor) {
		this.editor = editor;
		setDaemon( true ); 
		start();
	}
	
	public void run() {
		compileAndUpload();
	}

	public void compileAndUpload() {
		// This is where we want to compile our source and send to phone...
		this.editor.statusNotice("compile and upload");
		Sketch sketch = editor.getSketch();
		// this is to detect if we are running a mode that is not Android mode
		if ( editor.getMode().getClass() == processing.mode.java.JavaMode.class )
		{
			Base.showError( "Error",
						    "This tool can only be used in Android mode.",
						    null );
			return;
		} else {
			try {
				build = new MyBuild(sketch, (AndroidMode) editor.getMode());
			try {
				build.build("debug");
			    runner = new MyRunner(build,editor);
			    runner.launch(MyDevices.getInstance().getHardware());
			} catch (SketchException e) {
				e.printStackTrace();
			}

			} catch (IOException e) {
				System.out.println("IOException");
			} catch (MyBadSDKException e) {
				System.out.println("MyBadSDKException");
			}
		}
	}

}