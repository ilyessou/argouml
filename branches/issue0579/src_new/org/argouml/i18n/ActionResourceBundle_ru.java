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

/** Russian resource bundle
 *
 *   @author Alexey Aphanasyev (Alexey@tigris.org)
 *   @see org.argouml.i18n.ActionResourceBundle
 */
public class ActionResourceBundle_ru extends ListResourceBundle {

  static final Object[][] _contents = {
    {"template.save_project.confirm_overwrite", "���� \"{0\"} ��� ����������. ������������?"},
    {"template.save_project.status_writing", "������������ ���� \"{0\"}..."},
    {"template.save_project.status_wrote", "������ ����� \"{0}\" ���������."},
    {"template.save_project.file_not_found",
       "���� \"{0}\" �� ������.\n" +
       "���� �������� ���������."},
    {"template.save_project.io_exception",
       "������ ��� ������� �������� ���� \"{0}\".\n" +
       "���� �������� ���������."},
    {"text.save_project.confirm_overwrite_title", "���� ��� ����������"},
    {"text.save_project.file_not_found_title", "���� �� ������"},
    {"text.save_project.io_exception_title", "������ �� ����� ���������� �����"},
    {"text.save_as_project.unstable_release",
       "��� ���������� ������ ArgoUML, ��������������� ������ ��� �������������. \n" +
       "�� ������������� ������������ ��� ��� ���������������� �����, ��� ��� \n" +
       "����������� ���� ������ �������� �� ������ ���� ��������� �������� \n" +
       "��������� ArgoUML. ��� ���������������� ����� ����������� \"����������\"\n" +
       "(stable) ������, ������� �� ������ ����� �� ����� ����� �� ��������� \n" +
       "�������: www.argouml.org\n" +
       "���������� ��� �� �������, ����������� � ������ ��������."},
    {"text.save_as_project.unstable_release_title", "��������!!!"},
    {"text.save_as_project.chooser_title", "��������� ������: "},
    {"template.new_project.save_changes_to", "��������� ��������� � {0}?"},
    {"template.open_project.save_changes_to", "��������� ��������� � {0}?"},
    {"text.open_project.chooser_title", "������� ������"},
    {"template.open_project.status_read", "������ {0}."},
    {"text.remove_from_model.will_remove_from_diagrams",
       "\n��� ����� ������� �� ��������."},
    {"text.remove_from_model.will_remove_subdiagram",
       "\n������������ ����� ����� �������."},
    {"text.remove_from_model.anon_element_name", "���� �������"},
    {"template.remove_from_model.confirm_delete", "�� ������� ��� ������ ������� {0}?{1}"},
    {"text.remove_from_model.confirm_delete_title", "�� �������?"},
    {"template.exit.save_changes_to", "��������� ��������� � {0}?"}
  };

  public Object[][] getContents() {
    return _contents;
  }
}
