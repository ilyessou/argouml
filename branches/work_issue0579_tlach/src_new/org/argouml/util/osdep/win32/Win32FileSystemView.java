//
// The following code is taken and only slightly modified
// from Sun's java discussion forums.
//
// http://forum.java.sun.com/thread.jsp?forum=38&thread=71610
//
package org.argouml.util.osdep.win32;

import javax.swing.filechooser.*; 
import java.io.*; 
import java.util.*; 
import java.lang.reflect.Method; 

/** This class is necessary due to an annoying bug on Windows NT where 
 *  instantiating a JFileChooser with the default FileSystemView will 
 *  cause a "drive A: not ready" error every time. I grabbed the 
 *  Windows FileSystemView impl from the 1.3 SDK and modified it so 
 *  as to not use java.io.File.listRoots() to get fileSystem roots. 
 *  java.io.File.listRoots() does a SecurityManager.checkRead() which 
 *  causes the OS to try to access drive A: even when there is no disk, 
 *  causing an annoying "abort, retry, ignore" popup message every time 
 *  we instantiate a JFileChooser! 
 *  
 *  Instead of calling listRoots() we use a straightforward alternate 
 *  method of getting file system roots. 
 *
 *  @author http://forum.java.sun.com/thread.jsp?forum=38&amp;thread=71610
 *  @since ARGO0.9.8
 */

public class Win32FileSystemView extends FileSystemView { 
    private static final Object[] noArgs = {}; 
    private static final Class[] noArgTypes = {}; 

    private static Method listRootsMethod = null; 
    private static boolean listRootsMethodChecked = false; 

    public Win32FileSystemView() {
        super();
    }

    /** 
    * Returns true if the given file is a root. 
    */ 
    public boolean isRoot(File f) { 
        if(!f.isAbsolute()) { 
            return false; 
        } 
    
        String parentPath = f.getParent(); 
        if(parentPath == null) { 
            return true; 
        } else { 
            File parent = new File(parentPath); 
            return parent.equals(f); 
        } 
    } 
    
    /** 
    * creates a new folder with a default folder name. 
    */ 
    public File createNewFolder(File containingDir) throws IOException { 
        if(containingDir == null) { 
            throw new IOException("Containing directory is null:"); 
        } 
        File newFolder = null; 
        // Using NT's default folder name 
        newFolder = createFileObject(containingDir, "New Folder"); 
        int i = 2; 
        while (newFolder.exists() && (i < 100)) { 
            newFolder = createFileObject(containingDir,
	                                 "New Folder (" + i + ")"); 
            i++; 
        } 
    
        if(newFolder.exists()) { 
            throw new IOException("Directory already exists:" +
	                          newFolder.getAbsolutePath()); 
        } else { 
            newFolder.mkdirs(); 
        } 
    
        return newFolder; 
    } 
    
    /** 
    * Returns whether a file is hidden or not. On Windows 
    * there is currently no way to get this information from 
    * io.File, therefore always return false. 
    */ 
    public boolean isHiddenFile(File f) { 
        return false; 
    } 
    
    /** 
    * Returns all root partitians on this system. On Windows, this 
    * will be the A: through Z: drives. 
    *
    * Note - This appears to bypass the B drive!  Should
    * we treat the B drive the same as the A drive, or should
    * we continue to bypass it?
    */ 
    public File[] getRoots() { 
    
        Vector rootsVector = new Vector(); 
    
        // Create the A: drive whether it is mounted or not 
        FileSystemRoot floppy = new FileSystemRoot("A" + ":" + File.separator); 
        rootsVector.addElement(floppy); 
    
        // Run through all possible mount points and check 
        // for their existance. 
        for (char c = 'C'; c <= 'Z'; c++) { 
            char device[] = {c, ':', File.separatorChar}; 
            String deviceName = new String(device); 
            File deviceFile = new FileSystemRoot(deviceName); 
            if (deviceFile != null && deviceFile.exists()) { 
                rootsVector.addElement(deviceFile); 
            } 
        } 
        File[] roots = new File[rootsVector.size()]; 
        rootsVector.copyInto(roots); 
        return roots; 
    
        // } 
        // return null; 
    } 
    
    class FileSystemRoot extends File { 
        public FileSystemRoot(File f) { 
            super(f, ""); 
        } 
    
        public FileSystemRoot(String s) { 
            super(s); 
        } 
    
        public boolean isDirectory() { 
            return true; 
        } 
    } 
}
