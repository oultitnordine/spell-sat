///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.views
// 
// FILE      : NavigationView.java
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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.astra.ses.spell.gui.core.model.services.ServiceManager;
import com.astra.ses.spell.gui.core.model.types.Level;
import com.astra.ses.spell.gui.core.utils.Logger;
import com.astra.ses.spell.gui.model.commands.OpenProcedure;
import com.astra.ses.spell.gui.model.commands.ShowProperties;
import com.astra.ses.spell.gui.model.commands.helpers.CommandHelper;
import com.astra.ses.spell.gui.model.nav.ProcedureListContentProvider;
import com.astra.ses.spell.gui.model.nav.ProcedureListLabelProvider;
import com.astra.ses.spell.gui.model.nav.ProceduresStructureManager;
import com.astra.ses.spell.gui.model.nav.content.BaseProcedureSystemElement;
import com.astra.ses.spell.gui.model.nav.content.CategoryNode;
import com.astra.ses.spell.gui.model.nav.content.NodeSorter;
import com.astra.ses.spell.gui.model.nav.content.ProcedureNode;
import com.astra.ses.spell.gui.services.RuntimeSettingsService;
import com.astra.ses.spell.gui.services.RuntimeSettingsService.RuntimeProperty;
import com.astra.ses.spell.gui.services.ViewManager;

/*******************************************************************************
 * @brief This view allows go over the list of available procedures and to
 *        select one of them to be opened.
 * @date 09/10/07
 ******************************************************************************/
public class NavigationView extends ViewPart implements IOpenListener,
        ISelectionChangedListener, SelectionListener
{
	/***************************************************************************
	 * 
	 * {@link NavigationViewFilter} filters elements whose name matches the
	 * String pattern given
	 * 
	 **************************************************************************/
	private class NavigationViewFilter extends ViewerFilter
	{
		/** String pattern to follow */
		private Pattern	m_filterPattern;

		/***********************************************************************
		 * Constructor
		 **********************************************************************/
		public NavigationViewFilter()
		{
			super();
			m_filterPattern = Pattern.compile(globify(""));
		}

		/***********************************************************************
		 * Convert the input into a regular expression
		 * 
		 * @param input
		 * @return
		 **********************************************************************/
		public String globify(String input)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(".*");
			char[] chars = input.toCharArray();
			for (int i = 0; i < chars.length; ++i)
			{
				char c = chars[i];
				switch (c)
				{
				case '\\':
					if ((i + 1 == chars.length) || chars[i + 1] != '*')
					{
						buffer.append(c);
					}
					break;
				case '*':
					if (i == 0 || chars[i - 1] != '\\')
					{
						buffer.append('.');
					}
					buffer.append(c);
					break;
				default:
					buffer.append(c);
				}

			}
			buffer.append(".*");
			return buffer.toString();
		}

		/*
		 * ======================================================================
		 * (non-Javadoc)
		 * 
		 * @see ViewerFilter#select(Viewer, Object, Object)
		 * =====================================================================
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement,
		        Object element)
		{
			boolean result = false;
			if (element instanceof ProcedureNode)
			{
				ProcedureNode node = (ProcedureNode) element;
				String procName = node.getName().toUpperCase();
				String filterPattern = m_filterPattern.toString();
				result = procName.matches(filterPattern);
			}
			else
			{
				CategoryNode node = (CategoryNode) element;

				for (BaseProcedureSystemElement child : node.getChildren())
				{
					if (select(viewer, node, child))
					{
						result = true;
						break;
					}
				}
			}
			return result;
		}

		/**********************************************************************
		 * Change the filtering pattern
		 * 
		 * @param newPattern
		 *********************************************************************/
		public void setPattern(Pattern newPattern)
		{

			m_filterPattern = newPattern;
		}
	}

	// =========================================================================
	// STATIC DATA MEMBERS
	// =========================================================================

	// PRIVATE -----------------------------------------------------------------
	private static final String	       MENU_SHOW_PROPERTIES	= "Show properties";
	// PROTECTED ---------------------------------------------------------------
	// PUBLIC ------------------------------------------------------------------
	public static final String	       ID	                = "com.astra.ses.spell.gui.views.NavigationView";

	// =========================================================================
	// INSTANCE DATA MEMBERS
	// =========================================================================

	// PRIVATE -----------------------------------------------------------------
	private TreeViewer	               m_procTree;
	/** Procedure tree filter */
	private NavigationViewFilter	   m_procTreeFilter;
	/** Text input widget for filtering the text */
	private Text	                   m_filterText;
	/** Procedures structure manager */
	private ProceduresStructureManager	m_proceduresManager;

	// PROTECTED ---------------------------------------------------------------
	// PUBLIC ------------------------------------------------------------------

	// =========================================================================
	// ACCESSIBLE METHODS
	// =========================================================================

	/***********************************************************************
	 * Create the view contents.
	 * 
	 * @param parent
	 *            The top composite of the view
	 **********************************************************************/
	public void createPartControl(Composite parent)
	{
		Logger.debug("Created", Level.INIT, this);

		FillLayout layout = (FillLayout) parent.getLayout();
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		layout.type = SWT.VERTICAL;

		// Container widget
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout containerLayout = new GridLayout(1, true);
		containerLayout.marginHeight = 0;
		containerLayout.marginWidth = 0;
		containerLayout.verticalSpacing = 1;
		container.setLayout(containerLayout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		m_proceduresManager = new ProceduresStructureManager();
		m_proceduresManager.setView(this);

		// Tree viewer
		m_procTree = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL
		        | SWT.V_SCROLL | SWT.BORDER);
		// layout data
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		m_procTree.getControl().setLayoutData(layoutData);

		// Set the providers
		m_procTree.setContentProvider(new ProcedureListContentProvider());
		m_procTree.setLabelProvider(new ProcedureListLabelProvider());
		m_procTree.setSorter(new NodeSorter());
		// Set the procedure manager as the model provider
		m_procTree.setInput(m_proceduresManager.getRootElements());
		// Register the navigation view in the selection service
		getSite().setSelectionProvider(m_procTree);
		// Register this view as open listener as well
		m_procTree.addOpenListener(this);

		// Create the contextual menu
		Menu menu = new Menu(m_procTree.getControl());
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(MENU_SHOW_PROPERTIES);
		item.addSelectionListener(this);
		m_procTree.getControl().setMenu(menu);

		m_procTree.addSelectionChangedListener(this);
		// Register the view as a service listener for the procedure manager
		// in order to receive updates when the list of available procedures
		// may have changed.

		// Filter
		m_procTreeFilter = new NavigationViewFilter();
		m_procTree.addFilter(m_procTreeFilter);

		// Filter text area
		GridData textData = new GridData(GridData.FILL_HORIZONTAL
		        | GridData.GRAB_HORIZONTAL);
		textData.horizontalIndent = 0;
		textData.verticalIndent = 0;
		m_filterText = new Text(container, SWT.BORDER);
		m_filterText.setToolTipText("Find the procedure by typing its name");
		m_filterText.setLayoutData(textData);
		m_filterText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				Text filterText = (Text) e.widget;
				String filter = filterText.getText().toUpperCase();
				try
				{
					Pattern regex = Pattern.compile(m_procTreeFilter
					        .globify(filter));
					m_procTreeFilter.setPattern(regex);
					m_procTree.refresh();
					if (filter.isEmpty())
					{
						m_procTree.collapseAll();
					}
					else
					{
						m_procTree.expandAll();
					}
				}
				catch (PatternSyntaxException p)
				{
					// do nothing
				}
			}
		});

		ViewManager vmgr = (ViewManager) ServiceManager.get(ViewManager.ID);
		vmgr.registerView(ID, this);
	}

	/***********************************************************************
	 * Destroy the view.
	 **********************************************************************/
	public void dispose()
	{
		m_proceduresManager.setView(null);
		super.dispose();
		Logger.debug("Disposed", Level.PROC, this);
	}

	/***********************************************************************
	 * Receive the input focus.
	 **********************************************************************/
	public void setFocus()
	{
		m_procTree.getControl().setFocus();
	}

	/***********************************************************************
	 * Refresh the view
	 **********************************************************************/
	public void refresh()
	{
		m_procTree.setInput(m_proceduresManager.getRootElements());
		m_procTree.refresh();
	}

	/***********************************************************************
	 * Obtain the model
	 **********************************************************************/
	public ProceduresStructureManager getModel()
	{
		return m_proceduresManager;
	}

	/***********************************************************************
	 * Open event on double click
	 **********************************************************************/
	@Override
	public void open(OpenEvent event)
	{
		TreeSelection sel = (TreeSelection) event.getSelection();
		try
		{
			ProcedureNode item = (ProcedureNode) sel.getFirstElement();
			// Open procedures only, ignore categories
			String category = item.getProcID();
			if (category == null || category.equals("")
			        || category.equals("CATEGORY")) { return; }
			CommandHelper.execute(OpenProcedure.ID);
		}
		catch (Exception e)
		{
			return;
		}
	}

	/***********************************************************************
	 * Selection changed on the tree
	 **********************************************************************/
	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		TreeSelection sel = (TreeSelection) event.getSelection();
		RuntimeSettingsService runtime = (RuntimeSettingsService) ServiceManager
		        .get(RuntimeSettingsService.ID);
		try
		{
			ProcedureNode item = (ProcedureNode) sel.getFirstElement();
			runtime.setRuntimeProperty(
			        RuntimeProperty.ID_NAVIGATION_VIEW_SELECTION,
			        item.getProcID());
		}
		catch (Exception ex)
		{
			runtime.setRuntimeProperty(
			        RuntimeProperty.ID_NAVIGATION_VIEW_SELECTION, null);
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		MenuItem item = (MenuItem) e.widget;
		if (item.getText().equals(MENU_SHOW_PROPERTIES))
		{
			CommandHelper.execute(ShowProperties.ID);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
		widgetSelected(e);
	}
}
