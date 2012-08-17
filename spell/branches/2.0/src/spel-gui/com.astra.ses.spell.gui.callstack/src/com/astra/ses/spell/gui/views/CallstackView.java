///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.views
// 
// FILE      : CallstackView.java
//
// DATE      : 2008-11-21 08:55
//
// Copyright (C) 2008, 2011 SES ENGINEERING, Luxembourg S.A.R.L.
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.Page;

import com.astra.ses.spell.gui.Activator;
import com.astra.ses.spell.gui.views.controls.callstack.CallStackPage;

/**************************************************************************
 * View for the call stack (execution tree) of the active procedure.
 * 
 *************************************************************************/
public class CallstackView extends ProcedurePageView
{
	/** The ID of the view as specified by the extension. */
	public static final String	ID	= "com.astra.ses.spell.gui.views.CallstackView";

	/** Hide finished action */
	private Action	           m_hideFinished;

	/***************************************************************************
	 * 
	 **************************************************************************/
	public CallstackView()
	{
		super("(no stack data)", "Stack");
		makeActions();
	}

	@Override
	protected Page createMyPage(String procedureId, String name)
	{
		return new CallStackPage(procedureId);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		contributeToActionBars();
	}

	/***************************************************************************
	 * 
	 **************************************************************************/
	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	/***************************************************************************
	 * 
	 * @param manager
	 **************************************************************************/
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(m_hideFinished);
		manager.add(new Separator());
	}

	/***************************************************************************
	 * Make actions to apply over the different pages on demand
	 **************************************************************************/
	private void makeActions()
	{
		m_hideFinished = new Action()
		{
			public void run()
			{
				IPage current = getCurrentPage();
				if (current.getClass().equals(CallStackPage.class))
				{
					CallStackPage page = (CallStackPage) current;
					page.setFinishedFilter(this.isChecked());
				}
			}
		};
		m_hideFinished.setText("Hide finished nodes");
		m_hideFinished.setToolTipText("Hide finished nodes");
		m_hideFinished.setImageDescriptor(Activator
		        .getImageDescriptor("icons/16x16/eye.png"));
		/* By default finished nodes are visible */
		m_hideFinished.setChecked(false);
	}

	@Override
	protected void showPageRec(PageRec pageRec)
	{
		super.showPageRec(pageRec);

		IPage current = pageRec.page;
		/*
		 * Set filters
		 */
		if (current.getClass().equals(CallStackPage.class))
		{
			CallStackPage page = (CallStackPage) current;
			page.setFinishedFilter(m_hideFinished.isChecked());
		}
	}
}
