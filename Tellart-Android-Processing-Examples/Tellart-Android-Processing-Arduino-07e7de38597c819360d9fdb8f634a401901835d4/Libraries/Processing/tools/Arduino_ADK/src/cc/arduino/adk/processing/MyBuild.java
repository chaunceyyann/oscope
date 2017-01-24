/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 Part of the Processing project - http://processing.org

 Copyright (c) 2009-11 Ben Fry and Casey Reas

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package cc.arduino.adk.processing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import processing.app.Base;
import processing.app.Library;
import processing.app.Platform;
import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.SketchException;
import processing.app.exec.ProcessHelper;
import processing.app.exec.ProcessResult;
import processing.core.PApplet;
import processing.mode.android.AndroidMode;
import processing.mode.android.AndroidPreprocessor;
import processing.mode.android.Manifest;
import processing.mode.java.JavaBuild;
import processing.xml.XMLElement;

class MyBuild extends JavaBuild {
  static final String basePackage = "processing.android.test";
  static final String sdkVersion = "10";
  static final String sdkTarget = "Google Inc.:Google APIs:" + sdkVersion;
//XXX  static final String sdkTarget = "android-" + sdkVersion;

//  private final Editor editor;
  private final MyAndroidSDK sdk;
  private final File coreZipFile;
  private File coreZipLocation;

  private String target;
  private Manifest manifest;

  /** temporary folder safely inside a 8.3-friendly folder */
  private File tmpFolder;

  /** build.xml file for this project */
  private File buildFile;
  static final String MANIFEST_XML = "AndroidManifest.xml";
  static final String ACC_MANIFEST_XML = "accessory_filter.xml";

  /** the manifest data read from the file */
  private XMLElement xml;

  /** debug variable **/
  static boolean DEBUG = true;

  public MyBuild(final Sketch sketch, final AndroidMode mode) throws MyBadSDKException, IOException {
    super(sketch);
    
 	sdk = MyBuild.getSDK();
 	if (sdk == null) {
 		if (DEBUG) System.out.println("cannot configure your sdk, sorry");
 	}

 	// this is a patch compared to the normal build class
 	File coreZipFile2 = getCoreZipLocation();
 	File f = new File(coreZipFile2.toString());
 	String path = f.getParent();
 	String name = f.getName();
    path = path + "/modes/android/";
	coreZipFile = new File(path + name);

    
//    AVD.ensureEclairAVD(sdk);
  }


  /**
   * Build into temporary folders (needed for the Windows 8.3 bugs in the Android SDK). 
   * @param target
   * @throws SketchException 
   * @throws IOException 
   */
  public File build(String target) throws IOException, SketchException {
/*    boolean avd = AVD.ensureEclairAVD(sdk);
    if (!avd) {
      SketchException se = 
        new SketchException("Could not create a virtual device for the emulator.");
      se.hideStackTrace();
      throw se;
    }
*/
    this.target = target;
    File folder = createProject();
    if (folder != null) {
      if (!antBuild()) {
        return null;
      }
    }
    return folder;
  }
  
  
  /** 
   * Tell the PDE to not complain about android.* packages and others that are
   * part of the OS library set as if they're missing.
   */
  protected boolean ignorableImport(String pkg) {
    if (pkg.startsWith("android.")) return true;
    if (pkg.startsWith("java.")) return true;
    if (pkg.startsWith("javax.")) return true;
    if (pkg.startsWith("org.apache.http.")) return true;
    if (pkg.startsWith("org.json.")) return true;
    if (pkg.startsWith("org.w3c.dom.")) return true;
    if (pkg.startsWith("org.xml.sax.")) return true;
    return false;
  }
 
  
  /**
   * Create an Android project folder, and run the preprocessor on the sketch. 
   * Populates the 'src' folder with Java code, and 'libs' folder with the 
   * libraries and code folder contents. Also copies data folder to 'assets'.
   */
  public File createProject() throws IOException, SketchException {
    tmpFolder = createTempBuildFolder(sketch);

    // Create the 'src' folder with the preprocessed code.
//    final File srcFolder = new File(tmpFolder, "src");
    srcFolder = new File(tmpFolder, "src");
    // this folder isn't actually used, but it's used by the java preproc to 
    // figure out the classpath, so we have to set it to something
//    binFolder = new File(tmpFolder, "bin");
    // use the src folder, since 'bin' might be used by the ant build
    binFolder = srcFolder;
    if (processing.app.Base.DEBUG) {
      Base.openFolder(tmpFolder);
    }

    manifest = new Manifest(sketch);
    // grab code from current editing window (GUI only)
//    prepareExport(null);

    // build the preproc and get to work
    AndroidPreprocessor preproc = new AndroidPreprocessor(sketch, getPackageName());
/*    if (!preproc.parseSketchSize()) {
      throw new SketchException("Could not parse the size() command.");
    }
*/
    sketchClassName = preprocess(srcFolder, manifest.getPackageName(), preproc);
    if (sketchClassName != null) {
      File tempManifest = new File(tmpFolder, "AndroidManifest.xml");
	  writeBuild(tempManifest, sketchClassName, target.equals("debug"));
	  writeBuildProps(new File(tmpFolder, "build.properties"));
      buildFile = new File(tmpFolder, "build.xml");
      writeBuildXML(buildFile, sketch.getName());
      writeDefaultProps(new File(tmpFolder, "default.properties"));
      writeLocalProps(new File(tmpFolder, "local.properties"));
      final File resFolder = mkdirs(tmpFolder, "res"); 
      final File xmlFolder = mkdirs(resFolder, "xml"); 
      File manifestFile = new File(xmlFolder, "accessory_filter.xml");
      try {
    	  Base.copyFile(this.getAccManifestFile(), manifestFile);
      } catch (Exception e) {
    	  System.out.println(e.getMessage());
      }
      writeRes(new File(tmpFolder, "res"), sketchClassName);

      final File libsFolder = mkdirs(tmpFolder, "libs");
      final File assetsFolder = mkdirs(tmpFolder, "assets");

		Base.copyFile(coreZipFile, new File(libsFolder, "processing-core.jar"));
//      try {
      // Copy any imported libraries or code folder contents to the project
      copyLibraries(libsFolder, assetsFolder);
      copyCodeFolder(libsFolder);

      // Copy the data folder, if one exists, to the 'assets' folder of the
      // project
      final File sketchDataFolder = sketch.getDataFolder();
      if (sketchDataFolder.exists()) {
        Base.copyDir(sketchDataFolder, assetsFolder);
      }
//      } catch (final IOException e) {
//        e.printStackTrace();
//        throw new SketchException(e.getMessage());
//      }
    }
//    } catch (final SketchException e) {
//      editor.statusError(e);
//      return null;
//    } catch (final IOException e) {
//      editor.statusError(e);
//      return null;
//    }
    return tmpFolder;
  }


  /**
   * The Android dex util pukes on paths containing spaces, which will happen
   * most of the time on Windows, since Processing sketches wind up in
   * "My Documents". Therefore, build android in a temp file.
   * http://code.google.com/p/android/issues/detail?id=4567
   *
   * TODO: better would be to retrieve the 8.3 name for the sketch folder!
   *
   * @param sketch
   * @return A folder in which to build the android sketch
   * @throws IOException
   */
  private File createTempBuildFolder(final Sketch sketch) throws IOException {
    final File tmp = File.createTempFile("android", ".pde");
    if (!(tmp.delete() && tmp.mkdir())) {
      throw new IOException("Cannot create temp dir " + tmp
          + " to build android sketch");
    }
    return tmp;
  }


  protected File createExportFolder() throws IOException {
//    Sketch sketch = editor.getSketch();
    // Create the 'android' build folder, and move any existing version out. 
    File androidFolder = new File(sketch.getFolder(), "android");
    if (androidFolder.exists()) {
//      Date mod = new Date(androidFolder.lastModified());
      String stamp = AndroidMode.getDateStamp(androidFolder.lastModified());
      File dest = new File(sketch.getFolder(), "android." + stamp);
      boolean result = androidFolder.renameTo(dest);
      if (!result) {
        ProcessHelper mv;
        ProcessResult pr;
        try {
          System.err.println("createProject renameTo() failed, resorting to mv/move instead.");
          mv = new ProcessHelper("mv", androidFolder.getAbsolutePath(), dest.getAbsolutePath());
          pr = mv.execute();

//        } catch (IOException e) {
//          editor.statusError(e);
//          return null;
//
        } catch (InterruptedException e) {
          e.printStackTrace();
          return null;
        }
        if (!pr.succeeded()) {
          System.err.println(pr.getStderr());
          Base.showWarning("Failed to rename", 
                           "Could not rename the old “android” build folder.\n" + 
                           "Please delete, close, or rename the folder\n" + 
                           androidFolder.getAbsolutePath() + "\n" +  
                           "and try again." , null);
          Base.openFolder(sketch.getFolder());
          return null;
        }
      }
    } else {
      boolean result = androidFolder.mkdirs();
      if (!result) {
        Base.showWarning("Folders, folders, folders", 
                         "Could not create the necessary folders to build.\n" +
                         "Perhaps you have some file permissions to sort out?", null);
        return null;
      }
    }
    return androidFolder;
  }
  
  
  public File exportProject() throws IOException, SketchException {
    File projectFolder = build("debug");
    if (projectFolder == null) {
      return null;
    }

    File exportFolder = createExportFolder();
    Base.copyDir(projectFolder, exportFolder);
    return exportFolder;
  }


  public boolean exportPackage() throws IOException, SketchException {
    File projectFolder = build("release");
    if (projectFolder == null) {
      return false;
    }
    
    // TODO all the signing magic needs to happen here

    File exportFolder = createExportFolder();
    Base.copyDir(projectFolder, exportFolder);
    return true;
  }


  /**
   * @param target "debug" or "release"
   */
  protected boolean antBuild() throws SketchException {
    final Project p = new Project();
    String path = buildFile.getAbsolutePath().replace('\\', '/');
    p.setUserProperty("ant.file", path);

    // deals with a problem where javac error messages weren't coming through
    p.setUserProperty("build.compiler", "extJavac");
    // p.setUserProperty("build.compiler.emacs", "true"); // does nothing

    // try to spew something useful to the console
    final DefaultLogger consoleLogger = new DefaultLogger();
    consoleLogger.setErrorPrintStream(System.err);
    consoleLogger.setOutputPrintStream(System.out);  // ? uncommented before
    // WARN, INFO, VERBOSE, DEBUG
    //consoleLogger.setMessageOutputLevel(Project.MSG_ERR);
    consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
    p.addBuildListener(consoleLogger);

    // This logger is used to pick up javac errors to be parsed into 
    // SketchException objects. Note that most errors seem to show up on stdout
    // since that's where the [javac] prefixed lines are coming through.
    final DefaultLogger errorLogger = new DefaultLogger();
    final ByteArrayOutputStream errb = new ByteArrayOutputStream();
    final PrintStream errp = new PrintStream(errb);
    errorLogger.setErrorPrintStream(errp);
    final ByteArrayOutputStream outb = new ByteArrayOutputStream();
    final PrintStream outp = new PrintStream(outb);
    errorLogger.setOutputPrintStream(outp);
    errorLogger.setMessageOutputLevel(Project.MSG_INFO);
    p.addBuildListener(errorLogger);

    try {
//      editor.statusNotice("Building sketch for Android...");
      p.fireBuildStarted();
      p.init();
      final ProjectHelper helper = ProjectHelper.getProjectHelper();
      p.addReference("ant.projectHelper", helper);
      helper.parse(p, buildFile);
      // p.executeTarget(p.getDefaultTarget());
      p.executeTarget(target);
//      editor.statusNotice("Finished building sketch.");
      return true;

    } catch (final BuildException e) {
      // Send a "build finished" event to the build listeners for this project.
      p.fireBuildFinished(e);

      // PApplet.println(new String(errb.toByteArray()));
      // PApplet.println(new String(outb.toByteArray()));

      // String errorOutput = new String(errb.toByteArray());
      // String[] errorLines =
      // errorOutput.split(System.getProperty("line.separator"));
      // PApplet.println(errorLines);

      final String outPile = new String(outb.toByteArray());
      final String[] outLines = outPile.split(System.getProperty("line.separator"));
      // PApplet.println(outLines);

      for (final String line : outLines) {
        final String javacPrefix = "[javac]";
        final int javacIndex = line.indexOf(javacPrefix);
        if (javacIndex != -1) {
          // System.out.println("checking: " + line);
//          final Sketch sketch = editor.getSketch();
          // String sketchPath = sketch.getFolder().getAbsolutePath();
          int offset = javacIndex + javacPrefix.length() + 1;
          String[] pieces = 
            PApplet.match(line.substring(offset), "^(.+):([0-9]+):\\s+(.+)$");
          if (pieces != null) {
            // PApplet.println(pieces);
            String fileName = pieces[1];
            // remove the path from the front of the filename
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            final int lineNumber = PApplet.parseInt(pieces[2]) - 1;
            // PApplet.println("looking for " + fileName + " line " +
            // lineNumber);
            SketchException rex = placeException(pieces[3], fileName, lineNumber);
            if (rex != null) {
//              rex.hideStackTrace();
//              editor.statusError(rex);
//              return false; // get outta here
              throw rex;
            }
          }
        }
      }
      // this info will have already been printed
//      System.err.println("Problem during build: " + e.getMessage());
      // this info is a huge stacktrace of where it died inside the ant code (totally useless)
//      e.printStackTrace();
      // Couldn't parse the exception, so send something generic
      SketchException skex = 
        new SketchException("Error from inside the Android tools, check the console.");
      skex.hideStackTrace();
      throw skex;
    }
    //return false;
  }

  
//  protected String getClassName() {
//    return className;
//  }

  
  String getPathForAPK() {
    String suffix = target.equals("release") ? "unsigned" : "debug";
    String apkName = "bin/" + sketch.getName() + "-" + suffix + ".apk";
    final File apkFile = new File(tmpFolder, apkName);
    return apkFile.getAbsolutePath();
  }

    
  private void writeBuildProps(final File file) {
    final PrintWriter writer = PApplet.createWriter(file);
    writer.println("application-package=" + getPackageName());
    writer.flush();
    writer.close();
  }

  
  private void writeBuildXML(final File file, final String projectName) {
    final PrintWriter writer = PApplet.createWriter(file);
    writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

    writer.println("<project name=\"" + projectName + "\" default=\"help\">");
    writer.println("  <property file=\"local.properties\"/>");
    writer.println("  <property file=\"build.properties\"/>");
    writer.println("  <property file=\"default.properties\"/>");

    writer.println("  <path id=\"android.antlibs\">");
    writer.println("    <pathelement path=\"${sdk.dir}/tools/lib/anttasks.jar\" />");
    writer.println("    <pathelement path=\"${sdk.dir}/tools/lib/sdklib.jar\" />");
    writer.println("    <pathelement path=\"${sdk.dir}/tools/lib/androidprefs.jar\" />");
    writer.println("    <pathelement path=\"${sdk.dir}/tools/lib/apkbuilder.jar\" />");
    writer.println("    <pathelement path=\"${sdk.dir}/tools/lib/jarutils.jar\" />");
    writer.println("  </path>");

    writer.println("  <taskdef name=\"setup\"");
    writer.println("           classname=\"com.android.ant.SetupTask\"");
    writer.println("           classpathref=\"android.antlibs\" />");

    writer.println("  <setup />");

    writer.println("</project>");
    writer.flush();
    writer.close();
  }

  
  private void writeDefaultProps(final File file) {
    final PrintWriter writer = PApplet.createWriter(file);
    //writer.println("target=Google Inc.:Google APIs:" + sdkVersion);
    writer.println("target=" + sdkTarget);
    writer.flush();
    writer.close();
  }

  
  private void writeLocalProps(final File file) {
    if (DEBUG) System.out.println("using SDK folder: " + sdk.getSdkFolder().getAbsolutePath().toString());
    final PrintWriter writer = PApplet.createWriter(file);
    final String sdkPath = sdk.getSdkFolder().getAbsolutePath();
    if (Base.isWindows()) {
      // Windows needs backslashes escaped, or it will also accept forward
      // slashes in the build file. We're using the forward slashes since this
      // path gets concatenated with a lot of others that use forwards anyway.
      writer.println("sdk.dir=" + sdkPath.replace('\\', '/'));
    } else {
      writer.println("sdk.dir=" + sdkPath);
    }
    writer.flush();
    writer.close();
  }

  
  static final String ICON_72 = "icon-72.png";
  static final String ICON_48 = "icon-48.png";
  static final String ICON_36 = "icon-36.png";

  private void writeRes(File resFolder, 
                        String className) throws SketchException {
    File layoutFolder = mkdirs(resFolder, "layout");
    File layoutFile = new File(layoutFolder, "main.xml");
    writeResLayoutMain(layoutFile);

    // write the icon files
    File sketchFolder = sketch.getFolder();
    File localIcon36 = new File(sketchFolder, ICON_36);
    File localIcon48 = new File(sketchFolder, ICON_48);
    File localIcon72 = new File(sketchFolder, ICON_72);
    
//    File drawableFolder = new File(resFolder, "drawable");
//    drawableFolder.mkdirs()
    File buildIcon48 = new File(resFolder, "drawable/icon.png");
    File buildIcon36 = new File(resFolder, "drawable-ldpi/icon.png");
    File buildIcon72 = new File(resFolder, "drawable-hdpi/icon.png");

    if (!localIcon36.exists() && 
        !localIcon48.exists() && 
        !localIcon72.exists()) {
      try {
        // if no icons are in the sketch folder, then copy all the defaults
        if (buildIcon36.getParentFile().mkdirs()) {
          Base.copyFile(mode.getContentFile("icons/" + ICON_36), buildIcon36);
        } else {
          System.err.println("Could not create \"drawable-ldpi\" folder.");
        }
        if (buildIcon48.getParentFile().mkdirs()) {
          Base.copyFile(mode.getContentFile("icons/" + ICON_48), buildIcon48);
        } else {
          System.err.println("Could not create \"drawable\" folder.");
        }
        if (buildIcon72.getParentFile().mkdirs()) {
          Base.copyFile(mode.getContentFile("icons/" + ICON_72), buildIcon72);
        } else {
          System.err.println("Could not create \"drawable-hdpi\" folder.");
        }
      } catch (IOException e) {
        e.printStackTrace();
        //throw new SketchException("Could not get Android icons");
      }
    } else {
      // if at least one of the icons already exists, then use that across the board
      try {
        if (localIcon36.exists()) {
          if (new File(resFolder, "drawable-ldpi").mkdirs()) {
            Base.copyFile(localIcon36, buildIcon36);
          }
        }
        if (localIcon48.exists()) {
          if (new File(resFolder, "drawable").mkdirs()) {
            Base.copyFile(localIcon48, buildIcon48);
          }
        }
        if (localIcon72.exists()) {
          if (new File(resFolder, "drawable-hdpi").mkdirs()) {
            Base.copyFile(localIcon72, buildIcon72);
          }
        }
      } catch (IOException e) {
        System.err.println("Problem while copying icons.");
        e.printStackTrace();
      }
    }
    
//    final File valuesFolder = mkdirs(resFolder, "values");
//    final File stringsFile = new File(valuesFolder, "strings.xml");
//    writeResValuesStrings(stringsFile, className);
  }

  
  private File mkdirs(final File parent, final String name) throws SketchException {
    final File result = new File(parent, name);
    if (!(result.exists() || result.mkdirs())) {
      throw new SketchException("Could not create " + result);
    }
    return result;
  }

  
  private void writeResLayoutMain(final File file) {
    final PrintWriter writer = PApplet.createWriter(file);
    writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    writer.println("<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"");
    writer.println("              android:orientation=\"vertical\"");
    writer.println("              android:layout_width=\"fill_parent\"");
    writer.println("              android:layout_height=\"fill_parent\">");
    writer.println("</LinearLayout>");
    writer.flush();
    writer.close();
  }


  // This recommended to be a string resource so that it can be localized. 
  // nah.. we're gonna be messing with it in the GUI anyway... 
  // people can edit themselves if they need to
//  private static void writeResValuesStrings(final File file,
//                                            final String className) {
//    final PrintWriter writer = PApplet.createWriter(file);
//    writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
//    writer.println("<resources>");
//    writer.println("  <string name=\"app_name\">" + className + "</string>");
//    writer.println("</resources>");
//    writer.flush();
//    writer.close();
//  }


  /**
   * For each library, copy .jar and .zip files to the 'libs' folder, 
   * and copy anything else to the 'assets' folder.
   */
  private void copyLibraries(final File libsFolder, 
                             final File assetsFolder) throws IOException {
    // Copy any libraries to the 'libs' folder
    for (Library library : getImportedLibraries()) {
      File libraryFolder = new File(library.getPath());
      // in the list is a File object that points the
      // library sketch's "library" folder
      final File exportSettings = new File(libraryFolder, "export.txt");
      final HashMap<String, String> exportTable = 
        Base.readSettings(exportSettings);
      final String androidList = exportTable.get("android");
      String exportList[] = null;
      if (androidList != null) {
        exportList = PApplet.splitTokens(androidList, ", ");
      } else {
        exportList = libraryFolder.list();
      }
      for (int i = 0; i < exportList.length; i++) {
        exportList[i] = PApplet.trim(exportList[i]);
        if (exportList[i].equals("") || exportList[i].equals(".")
            || exportList[i].equals("..")) {
          continue;
        }

        final File exportFile = new File(libraryFolder, exportList[i]);
        if (!exportFile.exists()) {
          System.err.println("File " + exportList[i] + " does not exist");
        } else if (exportFile.isDirectory()) {
          System.err.println("Ignoring sub-folder \"" + exportList[i] + "\"");
        } else {
          final String name = exportFile.getName();
          final String lcname = name.toLowerCase();
          if (lcname.endsWith(".zip") || lcname.endsWith(".jar")) {
            // As of r4 of the Android SDK, it looks like .zip files
            // are ignored in the libs folder, so rename to .jar
            final String jarName = 
              name.substring(0, name.length() - 4) + ".jar";
            Base.copyFile(exportFile, new File(libsFolder, jarName));
          } else {
            // just copy other files over directly
            Base.copyFile(exportFile, new File(assetsFolder, name));
          }
        }
      }
    }
  }
  
  
  private void copyCodeFolder(final File libsFolder) throws IOException {
    // Copy files from the 'code' directory into the 'libs' folder
    final File codeFolder = sketch.getCodeFolder();
    if (codeFolder != null && codeFolder.exists()) {
      for (final File item : codeFolder.listFiles()) {
        if (!item.isDirectory()) {
          final String name = item.getName();
          final String lcname = name.toLowerCase();
          if (lcname.endsWith(".jar") || lcname.endsWith(".zip")) {
            String jarName = name.substring(0, name.length() - 4) + ".jar";
            Base.copyFile(item, new File(libsFolder, jarName));
          }
        }
      }
    }
  }

  
  protected String getPackageName() {
    return manifest.getPackageName();
  }


  public void cleanup() {
    // don't want to be responsible for this
    //rm(tempBuildFolder);
    tmpFolder.deleteOnExit();
  }
  
  protected File getCoreZipLocation() {
	    if (coreZipLocation == null) {
	      // for debugging only, check to see if this is an svn checkout
	      File debugFile = new File("../../../android/core.zip");
	      if (!debugFile.exists() && processing.app.Base.isMacOS()) {
	        // current path might be inside Processing.app, so need to go much higher
	        debugFile = new File("../../../../../../../android/core.zip");
	      }
	      if (debugFile.exists()) {
	        System.out.println("Using version of core.zip from local SVN checkout.");
//	        return debugFile;
	        coreZipLocation = debugFile;
	      }

	      // otherwise do the usual
	      //    return new File(base.getSketchbookFolder(), ANDROID_CORE_FILENAME);
	      coreZipLocation = processing.app.Base.getContentFile("android-core.zip");
	    }
	    return coreZipLocation;
	  }
	  
  public static MyAndroidSDK getSDK() throws MyBadSDKException, IOException {
	    final Platform platform = processing.app.Base.getPlatform();
	    if (DEBUG) System.out.println(processing.app.Preferences.get("android.sdk.path"));
	    // The environment variable is king. The preferences.txt entry is a page.
	    String sdkEnvPath = platform.getenv("ANDROID_SDK");
	    //    for some reason we aren't able of reading the env var in Windows
	    //    what leads to an error upon compilation. Therefore we introduce the
	    //    following alternative method to get the SDK's path
	    if (sdkEnvPath == null) {
	    	sdkEnvPath = processing.app.Preferences.get("android.sdk.path");
	    }
	    if (sdkEnvPath != null) {
		      try {
		        final MyAndroidSDK androidSDK = new MyAndroidSDK(new File(sdkEnvPath));
		        // Set this value in preferences.txt, in case ANDROID_SDK
		        // gets knocked out later. For instance, by that pesky Eclipse,
		        // which nukes all env variables when launching from the IDE.
		        Preferences.set("android.sdk.path", sdkEnvPath);
		        return androidSDK;
		      } catch (final MyBadSDKException drop) { }
	    }
	    if (DEBUG) System.out.println("** HALT ** SDK not found, sorry");
	    return null;
  }

  /**
   * Save a new version of the manifest info to the build location. 
   * Also fill in any missing attributes that aren't yet set properly.
   */
  protected void writeBuild(File file, String className, 
                            boolean debug) throws IOException {
    // write a copy to the build location
	save(file);
    
	// added, seems like it is not copying the manifest otherwise after creating it
	File file2 = new File(sketch.getFolder().toString() + "/AndroidManifest.xml");
	Base.copyFile(file2, file);

	// load the copy from the build location and start messing with it
	XMLElement mf = new XMLElement(new FileReader(file));
	
    // package name, or default
    String p = mf.getString("package").trim();
    if (p.length() == 0) {
      mf.setString("package", defaultPackageName());
    }
    // uses sdk
    XMLElement us = new XMLElement("uses-sdk");
    mf.addChild(us);
    us.setString("android:minSdkVersion", "10");
    // uses feature
 	XMLElement uf = new XMLElement("uses-feature");
 	mf.addChild(uf);
    uf.setString("android:name", "android.hardware.usb.accessory");
    // app name and label, or the class name
    XMLElement app = mf.getChild("application");
    // uses library
	XMLElement ul = new XMLElement("uses-library");
 	app.addChild(ul);
    ul.setString("android:name", "com.android.future.usb.accessory");
    // classname
    String label = app.getString("android:label");
    if (label.length() == 0) {
      app.setString("android:label", className);
    }
    app.setString("android:debuggable", debug ? "true" : "false");
    XMLElement activity = app.getChild("activity");
    // the '.' prefix is just an alias for the full package name
    // http://developer.android.com/guide/topics/manifest/activity-element.html#name
    activity.setString("android:name", "." + className);  // this has to be right
    // meta data
	XMLElement md = new XMLElement("meta-data");
    activity.addChild(md);
    md.setString("android:name", "android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
    md.setString("android:resource", "@xml/accessory_filter");
    //intent filter's action
    XMLElement intent = activity.getChild("intent-filter");
    // add the intent for launching when pluging in the Accessory
	XMLElement aa = new XMLElement("action");
    intent.addChild(aa);
    aa.setString("android:name", "android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
//    XMLElement ia = intent.getChild("action");
//    ia.setString("android:name", "android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
//    XMLElement ca = intent.getChild("category");
//    intent.removeChild(ca);
    
    PrintWriter writer = PApplet.createWriter(file);
    try {
    mf.write(writer);
    writer.close();
    } catch (java.lang.NullPointerException e) {
    	//
    	System.out.println("error generating the manifest file");
    }
  }

  protected void save() {
	    save(getManifestFile());
	  }

	  
	  /**
	   * Save to the sketch folder, so that it can be copied in later.
	   */
	  protected void save(File file) {
	    PrintWriter writer = PApplet.createWriter(file);
	      try {
	        xml.write(writer);
	        writer.close();
	      } catch (java.lang.NullPointerException e) {
	    	  //
	      }
	  }


	  private File getManifestFile() {
		    return new File(sketch.getFolder(), MANIFEST_XML);
		  }

	  private File getAccManifestFile() {
		    return new File(sketch.getFolder(), ACC_MANIFEST_XML);
		  }

	  private String defaultPackageName() {
//	    Sketch sketch = editor.getSketch();
	    return MyBuild.basePackage + "." + sketch.getName().toLowerCase();
	  }
	 }