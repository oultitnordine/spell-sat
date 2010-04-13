///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.views
// 
// FILE      : LogFileView.java
//
// DATE      : 2008-11-21 08:55
//
// Copyright (C) 2008, 2010 SES ENGINEERING, Luxembourg S.A.R.L.
//
// By using this software in any way, you are agreeing to be bound by
// the terms of this license.
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// NO WARRANTY
// EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED
// ON AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER
// EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR
// CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
// PARTICULAR PURPOSE. Each Recipient is solely responsible for determining
// the appropriateness of using and distributing the Program and assumes all
// risks associated with its exercise of rights under this Agreement ,
// including but not limited to the risks and costs of program errors,
// compliance with applicable laws, damage to or loss of data, programs or
// equipment, and unavailability or interruption of operations.
//
// DISCLAIMER OF LIABILITY
// EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, NEITHER RECIPIENT NOR ANY
// CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT LIMITATION
// LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM OR THE
// EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGES.
//
// Contributors:
//    SES ENGINEERING - initial API and implementation and/or initial documentation
//
// PROJECT   : SPELL
//
// SUBPROJECT: SPELL GUI Client
//
///////////////////////////////////////////////////////////////////////////////
package com.astra.ses.spell.gui.views;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.astra.ses.spell.gui.core.model.server.TabbedFileLine;
import com.astra.ses.spell.gui.core.model.server.LogFile.LogLine;
import com.astra.ses.spell.gui.core.model.types.ItemStatus;
import com.astra.ses.spell.gui.core.services.ServiceManager;
import com.astra.ses.spell.gui.services.ConfigurationManager;

/*******************************************************************************
 * LogFile view extends tabbed view setting background color
 * @author jpizar
 *
 ******************************************************************************/
public class LogFileView extends TabbedView {

	/** Browser ID used by main plugin for preparing the perspective layout */
	public static final String ID = "com.astra.ses.spell.gui.views.LogFileView";
	
	private class LogFileColorProvider extends LabelProvider implements ITableLabelProvider, 
	ITableColorProvider, ITableFontProvider
	{
		/** background color */
		private Color m_warningColor;
		/** foreground color */
		private Color m_errorColor;
		/** Table font */
		private Font m_font;
		
		public LogFileColorProvider()
		{
			ConfigurationManager cfg = (ConfigurationManager) ServiceManager.get(ConfigurationManager.ID);
			m_warningColor = cfg.getStatusColor(ItemStatus.WARNING);
			m_errorColor = cfg.getStatusColor(ItemStatus.ERROR);
			m_font = cfg.getFont("MASTERC");
		}
		
		@Override
		public String getColumnText(Object obj, int index) {
			return ((TabbedFileLine) obj).getElement(index);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		@Override
		public Color getBackground(Object element, int columnIndex) {
			LogLine obj = (LogLine) element;
			switch (obj.getLogLineType())
			{
				case ERROR: 
					return m_errorColor;
				case WARNING: 
					return m_warningColor;
				default: // INFO
					return null;
			}
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			return null;
		}

		@Override
		public Font getFont(Object element, int columnIndex) {
			return m_font;
		}
	}
	
	/***************************************************************************
	 * Constructor
	 **************************************************************************/
	public LogFileView()
	{
		super();
	}
	
	/**************************************************************************
	 * Initialize contents
	 *************************************************************************/
	public void initContents()
	{
		super.initContents();
		getViewer().setLabelProvider(new LogFileColorProvider());
	}
}
