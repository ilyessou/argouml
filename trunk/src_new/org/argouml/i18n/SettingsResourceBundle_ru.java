// Copyright (c) 1996-2001 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.i18n;
import java.util.*;
import org.argouml.util.*;
import javax.swing.*;
import java.awt.event.*;


/** Russian Resource bundle for internationalization of Settings dialog
 *
 *   @author Alexey Aphanasyev (Alexey@tigris.org)
 *   @see org.argouml.i18n.SettingsResourceBundle
 */
public class SettingsResourceBundle_ru extends ListResourceBundle {

   static final Object[][] _contents = {
        {"button.apply", "���������" },  
        {"button.cancel", "��������" },  
        {"button.ok", "OK" },  

        {"caption.settings", "���������" },

        {"tab_user", "������������" },  
        {"label_user", "������ ���:" },  
        {"label_email", "����� ����������� �����:" },  
        {"label_splash", "���������� ��������" },  
        {"label_preload", "�������������� ��������� ����� ������" },  
        {"label_edem", "������������ � ���������� �������������" },  
        {"label_profile", "������������ � ������� ��������" },  
        {"tab_preferences", "������������" },  
        {"tab_environment", "���������" },  

	{"tab_fonts", "������" },
	{"label_fonts_normal", "����������" },
	{"label_fonts_big", "�������" },
	{"label_fonts_huge", "����������" },

   };

     public Object[][] getContents() {
        return _contents;
     }
}
