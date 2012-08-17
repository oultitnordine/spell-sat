///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.core.comm.messages
// 
// FILE      : SPELLmessageCtxOperation.java
//
// DATE      : 2008-11-21 08:58
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
package com.astra.ses.spell.gui.core.comm.messages;

import java.util.TreeMap;

import com.astra.ses.spell.gui.core.model.notification.ErrorData;
import com.astra.ses.spell.gui.core.model.server.ContextInfo;
import com.astra.ses.spell.gui.core.model.types.ContextStatus;

public class SPELLmessageCtxOperation extends SPELLmessageOneway
{
	private ContextInfo	m_info;

	public SPELLmessageCtxOperation(TreeMap<String, String> data)
	{
		super(data);
		m_info = null;
		try
		{
			String ctxName = get(IMessageField.FIELD_CTX_NAME);
			m_info = new ContextInfo(ctxName);
			String ctxStatus = get(IMessageField.FIELD_CTX_STATUS);
			m_info.setStatus(ContextStatus.valueOf(ctxStatus));
			if (hasKey(IMessageField.FIELD_CTX_DESC))
			{
				m_info.setDescription(get(IMessageField.FIELD_CTX_DESC));
			}
			if (hasKey(IMessageField.FIELD_CTX_DRV))
			{
				m_info.setDriver(get(IMessageField.FIELD_CTX_DRV));
			}
			if (hasKey(IMessageField.FIELD_CTX_FAM))
			{
				m_info.setFamily(get(IMessageField.FIELD_CTX_FAM));
			}
			if (hasKey(IMessageField.FIELD_CTX_GCS))
			{
				m_info.setGCS(get(IMessageField.FIELD_CTX_GCS));
			}
			if (hasKey(IMessageField.FIELD_HOST))
			{
				m_info.setHost(get(IMessageField.FIELD_HOST));
			}
			if (hasKey(IMessageField.FIELD_CTX_PORT))
			{
				m_info.setPort(get(IMessageField.FIELD_CTX_PORT));
			}
			if (hasKey(IMessageField.FIELD_CTX_SC))
			{
				m_info.setSC(get(IMessageField.FIELD_CTX_SC));
			}
		}
		catch (MessageException ex)
		{
		}
	}

	public ContextInfo getData()
	{
		return m_info;
	}

	public ErrorData getErrorData()
	{
		String error = "unknown error";
		String reason = "";
		boolean fatal = false;
		try
		{
			error = get(IMessageField.FIELD_ERROR);
			reason = get(IMessageField.FIELD_REASON);
			fatal = get(IMessageField.FIELD_FATAL).equals("True");
		}
		catch (MessageException ex)
		{
			ex.printStackTrace();
		}
		return new ErrorData(m_info.getName(), error, reason, fatal);
	}

	public ContextStatus getStatus()
	{
		return m_info.getStatus();
	}
}
