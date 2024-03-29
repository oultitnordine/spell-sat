// ################################################################################
// FILE       : SPELLipc_Context.H
// DATE       : Mar 17, 2011
// PROJECT    : SPELL
// DESCRIPTION: Inter process communication, data definitions for context process
// --------------------------------------------------------------------------------
//
//  Copyright (C) 2008, 2012 SES ENGINEERING, Luxembourg S.A.R.L.
//
//  This file is part of SPELL.
//
// SPELL is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// SPELL is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with SPELL. If not, see <http://www.gnu.org/licenses/>.
//
// ################################################################################

#ifndef __SPELL_IPC_CONTEXT_H__
#define __SPELL_IPC_CONTEXT_H__

// FILES TO INCLUDE ////////////////////////////////////////////////////////
// System includes ---------------------------------------------------------
// Local includes ----------------------------------------------------------
// Project includes --------------------------------------------------------
#include "SPELL_UTIL/SPELLbase.H"

/** \addtogroup SPELL_IPC */
/*@{*/


// FORWARD REFERENCES //////////////////////////////////////////////////////
// TYPES ///////////////////////////////////////////////////////////////////
// DEFINES /////////////////////////////////////////////////////////////////

/** Define the message identifiers */
namespace MessageId
{
	/** Executor operations */
	static const std::string MSG_ID_EXEC_OP("MSG_EXEC_OP");
	/** Sent as a response when a requested peer is lost */
	static const std::string  MSG_PEER_LOST("MSG_PEER_LOST");
};

/** Defines message fields */
namespace MessageField
{
	/** Holds a comma separated list of executor names */
	static const std::string  FIELD_EXEC_LIST("ExecutorList");
	/** Holds a comma separated list of procedure names */
	static const std::string  FIELD_PROC_LIST("ProcList");
	/** Holds the code name string */
	static const std::string  FIELD_CODE_NAME("CodeName");
	/** Holds the procedure code */
	static const std::string  FIELD_PROC_CODE("ProcCode");
	/** Holds a server file lines (data) */
	static const std::string  FIELD_SERVER_FILE("ServerFile");
	/** Holds the GUI connection mode (commanding, monitoring) */
	static const std::string  FIELD_GUI_MODE("GuiMode");
	/** Holds client key */
	static const std::string  FIELD_GUI_KEY("GuiKey");           // Client and exec operations
	/** Executor operation */
	static const std::string  FIELD_EXOP("ExecOp");              // Exec operations
	/** Client operation */
	static const std::string  FIELD_CLOP("CltOp");               // Client operations
	/** Holds a list of file names */
	static const std::string FIELD_FILE_LIST("FileList");
	/** Holds a file name */
	static const std::string FIELD_FILE_NAME("FileName");
	/** Holds a directory name */
	static const std::string FIELD_DIR_NAME("DirName");
};

/** Defines values for the message fields */
namespace MessageValue
{
	/** Control client mode */
	static const std::string  DATA_GUI_MODE_C("CONTROL");
	/** Monitor client mode */
	static const std::string  DATA_GUI_MODE_M("MONITOR");
	/** Background execution mode */
	static const std::string  DATA_GUI_MODE_B("BACKGROUND");
	/** Schedule execution mode */
	static const std::string  DATA_GUI_MODE_S("SCHEDULE");

	// Executor operation codes (FIELD_EXOP)
	/** Executor open */
	static const std::string  DATA_EXOP_OPEN("OPEN");
	/** Executor closed */
	static const std::string  DATA_EXOP_CLOSE("CLOSE");
	/** Executor killed */
	static const std::string  DATA_EXOP_KILL("KILL");
	/** Executor attached */
	static const std::string  DATA_EXOP_ATTACH("ATTACH");
	/** Executor detached */
	static const std::string  DATA_EXOP_DETACH("DETACH");
	/** Executor failed */
	static const std::string  DATA_EXOP_CRASH("CRASH");

	// Client operation codes (FIELD_CLOP)
	/** Client logged in */
	static const std::string  DATA_CLOP_LOGIN     = "LOGIN";
	/** Client logged out */
	static const std::string  DATA_CLOP_LOGOUT    = "LOGOUT";

	// Typs of server files
	/** Server file type AsRUN **/
	static const std::string DATA_FILE_ASRUN("ASRUN");
	/** Server file type LOG */
	static const std::string DATA_FILE_EXEC_LOG("EXECUTOR_LOG");

//###############################################################################
//# For context status (FIELD_CTX_STATUS)
    static const std::string DATA_CTX_AVAILABLE("AVAILABLE");
    static const std::string DATA_CTX_RUNNING("RUNNING");
    static const std::string DATA_CTX_ERROR("ERROR");
    static const std::string DATA_CTX_KILLED("KILLED");
    static const std::string DATA_CTX_STARTING("STARTING");
    static const std::string DATA_CTX_STOPPING("STOPPING");
};

/** Defines context process messages */
namespace ContextMessages
{
	/** Request list of available executors (running procedures) */
	static const std::string  REQ_EXEC_LIST("REQ_EXEC_LIST");
	/** Response to REQ_EXEC_LIST */
	static const std::string  RSP_EXEC_LIST("RSP_EXEC_LIST");

	/** Request executor (procedure) information */
	static const std::string  REQ_EXEC_INFO("REQ_EXEC_INFO");
	/** Response to REQ_EXEC_INFO */
	static const std::string  RSP_EXEC_INFO("RSP_EXEC_INFO");

	/** Request opening a new executor (procedure)
	 This request may be sent by executors as well, when starting a procedure
	 by using StartProc function. */
	static const std::string  REQ_OPEN_EXEC("REQ_OPEN_EXEC");
	/** Response to REQ_OPEN_EXEC */
	static const std::string  RSP_OPEN_EXEC("RSP_OPEN_EXEC");

	/** Request closing an executor */
	static const std::string  REQ_CLOSE_EXEC("REQ_CLOSE_EXEC");
	/** Response to REQ_CLOSE_EXEC */
	static const std::string  RSP_CLOSE_EXEC("RSP_CLOSE_EXEC");

	/** Request killing an executor */
	static const std::string  REQ_KILL_EXEC("REQ_KILL_EXEC");
	/** Response to REQ_KILL_EXEC */
	static const std::string  RSP_KILL_EXEC("RSP_KILL_EXEC");

	/** Request attaching to an executor */
	static const std::string  REQ_ATTACH_EXEC("REQ_ATTACH_EXEC");
	/** Response to REQ_ATTACH_EXEC */
	static const std::string  RSP_ATTACH_EXEC("RSP_ATTACH_EXEC");

	/** Request detaching from an executor */
	static const std::string  REQ_DETACH_EXEC("REQ_DETACH_EXEC");
	/** Response to REQ_DETACH_EXEC */
	static const std::string  RSP_DETACH_EXEC("RSP_DETACH_EXEC");

	/** Request for procedure list */
	static const std::string  REQ_PROC_LIST("REQ_PROC_LIST");
	/** Response to REQ_PROC_LIST */
	static const std::string  RSP_PROC_LIST("RSP_PROC_LIST");

	/** Request for procedure properties */
	static const std::string  REQ_PROC_PROP("REQ_PROC_PROP");
	/** Response to REQ_PROC_PROP */
	static const std::string  RSP_PROC_PROP("RSP_PROC_PROP");

	/** Request for procedure code */
	static const std::string  REQ_PROC_CODE("REQ_PROC_CODE");
	/** Response to REQ_PROC_CODE */
	static const std::string  RSP_PROC_CODE("RSP_PROC_CODE");

	/** Request for client info */
	static const std::string  REQ_CLIENT_INFO("REQ_CLIENT_INFO");
	/** Response to REQ_CLIENT_INFO */
	static const std::string  RSP_CLIENT_INFO("RSP_CLIENT_INFO");

	/** Request for a server file */
	static const std::string  REQ_SERVER_FILE_PATH("REQ_SERVER_FILE_PATH");
	/** Response to REQ_SERVER_FILE */
	static const std::string  RSP_SERVER_FILE_PATH("RSP_SERVER_FILE_PATH");

	/** Request for available instance id */
	static const std::string  REQ_INSTANCE_ID("REQ_INSTANCE_ID");
	/** Response to REQ_INSTANCE_ID */
	static const std::string  RSP_INSTANCE_ID("RSP_INSTANCE_ID");

	/** Request for closure condition */
	static const std::string  REQ_CAN_CLOSE("REQ_CAN_CLOSE");
	/** Response to REQ_CAN_CLOSE */
	static const std::string  RSP_CAN_CLOSE("RSP_CAN_CLOSE");

	/** Request for procedure recovery list */
	static const std::string  REQ_RECOVERY_LIST("REQ_RECOVERY_LIST");
	/** Response to procedure recovery list */
	static const std::string  RSP_RECOVERY_LIST("RSP_RECOVERY_LIST");

	/** Request for procedure recovery list */
	static const std::string  REQ_RECOVER_EXEC("REQ_RECOVER_EXEC");
	/** Response to procedure recovery list */
	static const std::string  RSP_RECOVER_EXEC("RSP_RECOVER_EXEC");

	/** Request for procedure recovery list */
	static const std::string  REQ_DELETE_RECOVERY("REQ_DELETE_RECOVERY");
	/** Response to procedure recovery list */
	static const std::string  RSP_DELETE_RECOVERY("RSP_DELETE_RECOVERY");

	/** Request for dumping interpreter info */
	static const std::string  REQ_DUMP_INTERPRETER("REQ_DUMP_INTERPRETER");
	/** Response */
	static const std::string  RSP_DUMP_INTERPRETER("RSP_DUMP_INTERPRETER");

	/** Request for removing controlling client (forced) */
	static const std::string  REQ_REMOVE_CONTROL("REQ_REMOVE_CONTROL");
	/** Response */
	static const std::string  RSP_REMOVE_CONTROL("RSP_REMOVE_CONTROL");

	/** Request for file listing */
	static const std::string  REQ_LIST_FILES("REQ_LIST_FILES");
	/** Response */
	static const std::string  RSP_LIST_FILES("RSP_LIST_FILES");

	/** Request for list of data directories */
	static const std::string  REQ_LIST_DATADIRS("REQ_LIST_DATADIRS");
	/** Response */
	static const std::string  RSP_LIST_DATADIRS("RSP_LIST_DATADIRS");

	/** Request for a SPELL data dictionary or input file */
	static const std::string  REQ_INPUT_FILE("REQ_INPUT_FILE");
	/** Response */
	static const std::string  RSP_INPUT_FILE("RSP_INPUT_FILE");

	// ################################################################################
	// MESSAGES FROM LISTENER
	// ################################################################################

	/** Message for closing the context */
	static const std::string  MSG_CLOSE_CTX("MSG_CLOSE_CTX");

	/** Request for starting the context */
	static const std::string  REQ_OPEN_CTX("REQ_OPEN_CTX");
	/** Response */
	static const std::string  RSP_OPEN_CTX("RSP_OPEN_CTX");

	/** Request for closing the context */
	static const std::string  REQ_CLOSE_CTX("REQ_CLOSE_CTX");
	/** Response */
	static const std::string  RSP_CLOSE_CTX("RSP_CLOSE_CTX");

	/** Request for closing the context */
	static const std::string  REQ_DESTROY_CTX("REQ_DESTROY_CTX");
	/** Response */
	static const std::string  RSP_DESTROY_CTX("RSP_DESTROY_CTX");

	/** Request for getting the context list */
	static const std::string  REQ_CTX_LIST("REQ_CTX_LIST");
	/** Response */
	static const std::string  RSP_CTX_LIST("RSP_CTX_LIST");

	/** Request for getting a context info */
	static const std::string  REQ_CTX_INFO("REQ_CTX_INFO");
	/** Response */
	static const std::string  RSP_CTX_INFO("RSP_CTX_INFO");

    /** Sent by GUIs when starting a session */
    static const std::string  REQ_GUI_LOGIN("REQ_GUI_LOGIN");
    static const std::string  RSP_GUI_LOGIN("RSP_GUI_LOGIN");

    /** Sent by GUIs when ending a session */
    static const std::string  REQ_GUI_LOGOUT("REQ_GUI_LOGOUT");
    static const std::string  RSP_GUI_LOGOUT("RSP_GUI_LOGOUT");

	/** Sent by GUIs when attaching to a context */
	static const std::string  REQ_ATTACH_CTX("REQ_ATTACH_CTX");
	static const std::string  RSP_ATTACH_CTX("RSP_ATTACH_CTX");

	/** Sent by GUIs when detaching to a context */
	static const std::string  REQ_DETACH_CTX("REQ_DETACH_CTX");
	static const std::string  RSP_DETACH_CTX("RSP_DETACH_CTX");

	// ################################################################################
	// # MESSAGES TO SPELL CLIENTS
	// ################################################################################

	/** Sent by context to GUIs when a client logs in/out */
	static const std::string  MSG_CLIENT_OP("MSG_CLIENT_OP");
	/** Sent by context to GUIs when a executor changes the status, is open/closed... */
	static const std::string  MSG_EXEC_OP("MSG_EXEC_OP");
	/** Sent by context to GUIs when a executor changes the configuration */
	static const std::string  MSG_EXEC_CONFIG("MSG_EXEC_CONFIG");
	/** Sent by context to GUIs when there is an executor control error in context side */
	static const std::string  MSG_EXEC_ERROR("MSG_EXEC_ERROR");
	/** Sent by context to GUIs when the listener connection is lost */
	static const std::string  MSG_LISTENER_LOST("MSG_LISTENER_LOST");
};

/*@}*/
#endif

