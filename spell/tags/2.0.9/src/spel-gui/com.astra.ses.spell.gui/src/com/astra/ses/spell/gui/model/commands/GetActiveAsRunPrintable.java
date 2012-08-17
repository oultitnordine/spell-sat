///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.model.commands
// 
// FILE      : GetActiveAsRunPrintable.java
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
package com.astra.ses.spell.gui.model.commands;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import com.astra.ses.spell.gui.core.model.files.AsRunColumns;
import com.astra.ses.spell.gui.core.model.files.AsRunFile;
import com.astra.ses.spell.gui.core.model.files.AsRunFileLine;
import com.astra.ses.spell.gui.core.model.files.IServerFileLine;
import com.astra.ses.spell.gui.core.model.services.ServiceManager;
import com.astra.ses.spell.gui.core.model.types.ServerFileType;
import com.astra.ses.spell.gui.print.SpellFooterPrinter;
import com.astra.ses.spell.gui.print.SpellHeaderPrinter;
import com.astra.ses.spell.gui.print.printables.TabbedTextPrintable;
import com.astra.ses.spell.gui.procs.services.ProcedureManager;
import com.astra.ses.spell.gui.services.RuntimeSettingsService;
import com.astra.ses.spell.gui.services.RuntimeSettingsService.RuntimeProperty;

public class GetActiveAsRunPrintable extends AbstractHandler implements
        IHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// Obtain the name of the selected procedure
		RuntimeSettingsService runtime = (RuntimeSettingsService) ServiceManager
		        .get(RuntimeSettingsService.ID);
		String selectedProcedure = runtime.getRuntimeProperty(
		        RuntimeProperty.ID_PROCEDURE_SELECTION).toString();
		// Get the server file
		ProcedureManager procedureMgr = (ProcedureManager) ServiceManager
		        .get(ProcedureManager.ID);
		AsRunFile asRun = (AsRunFile) procedureMgr.getServerFile(
		        selectedProcedure, ServerFileType.ASRUN);
		ArrayList<IServerFileLine> asRunLines = asRun.getLines();
		String[][] asRunArray = new String[asRunLines.size()][];
		int i = 0;

		/*
		 * Table information
		 */
		String[] tableHeader = new String[AsRunColumns.values().length];
		int[] columnWidths = new int[AsRunColumns.values().length];
		for (AsRunColumns col : AsRunColumns.values())
		{
			int index = col.ordinal();
			tableHeader[index] = col.name;
			columnWidths[index] = col.width;
		}

		/*
		 * Process each AsRun file line
		 */
		for (IServerFileLine line : asRunLines)
		{
			AsRunFileLine asRunLine = (AsRunFileLine) line;
			String[] tabbedLine = new String[AsRunColumns.values().length];
			for (AsRunColumns column : AsRunColumns.values())
			{
				tabbedLine[column.ordinal()] = column.visit(asRunLine);
			}
			asRunArray[i] = tabbedLine;
			i++;
		}
		SpellHeaderPrinter header = new SpellHeaderPrinter(selectedProcedure
		        + " - AsRun");
		SpellFooterPrinter footer = new SpellFooterPrinter();
		return new TabbedTextPrintable(asRunArray, tableHeader, columnWidths,
		        header, footer);
	}

}
