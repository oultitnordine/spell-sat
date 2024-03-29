// ################################################################################
// FILE       : SPELLframeManager.H
// DATE       : Mar 17, 2011
// PROJECT    : SPELL
// DESCRIPTION: Operations in relation with the current Python frame (runtime)
// --------------------------------------------------------------------------------
//
//  Copyright (C) 2008, 2014 SES ENGINEERING, Luxembourg S.A.R.L.
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

#ifndef __SPELL_FRAME_MANAGER_H__
#define __SPELL_FRAME_MANAGER_H__

// FILES TO INCLUDE ////////////////////////////////////////////////////////
// Local includes ----------------------------------------------------------
#include "SPELL_EXC/SPELLexecutionModel.H"
#include "SPELL_EXC/SPELLexecutionTrace.H"
#include "SPELL_EXC/SPELLcallstackIF.H"
#include "SPELL_EXC/SPELLbreakpoint.H"
#include "SPELL_EXC/SPELLastAnalyzer.H"
#include "SPELL_EXC/SPELLdispatchListener.H"
// Project includes --------------------------------------------------------
#include "SPELL_UTIL/SPELLerror.H"
#include "SPELL_WS/SPELLwarmstart.H"
// System includes ---------------------------------------------------------

/** \addtogroup SPELL_EXC */
/*@{*/


// FORWARD REFERENCES //////////////////////////////////////////////////////
// TYPES ///////////////////////////////////////////////////////////////////
/** Types of execution results */
typedef enum SPELLexecutionResult_
{
	/** Procedure reached a finish state without problems */
    EXECUTION_SUCCESS    = 0,
    /** Procedure was aborted by the user */
    EXECUTION_ABORTED    = 1,
    /** Procedure failed due to an error */
    EXECUTION_ERROR      = 2,
    /** Procedure has been forced to finish in order to be closed */
    EXECUTION_TERMINATED = 3,
    /** Unknown result */
    EXECUTION_UNKNOWN    = 4
}
SPELLexecutionResult;

// DEFINES /////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////
/**
 ** \brief Decouples Python frames manipulations from the rest of the executor.
 **
 ** \par Description and usage:
 **
 ** 	Provides mechanisms to manipulate the procedure execution (go-to
 ** 	mechanisms, INIT step, etc. and contains/controlls the warmstart
 ** 	mechanism.
 **
 ** 	The Python interpretation occurs inside this class.
 **
**////////////////////////////////////////////////////////////////////////////
class SPELLframeManager : public SPELLdispatchListener
{
public: //--------------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////
    // LIFECYCLE /////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////
		/** Constructor.
		**////////////////////////////////////////////////////////////////////
		SPELLframeManager();

		//////////////////////////////////////////////////////////////////////
		/** Destructor.
		**////////////////////////////////////////////////////////////////////
		~SPELLframeManager();

    // METHODS ///////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////
		/** Initialize the frame with the given script. The script will be
		 * compiled and the initial frame generated.
		 *
		 * \param scriptFile IN: absolute path to the script file (.py)
		 *
		 * \throws SPELLcoreException if the file cannot be found, or the
		 *  compilation fails.
		**////////////////////////////////////////////////////////////////////
		void initialize( const std::string& scriptFile );

		//////////////////////////////////////////////////////////////////////
		/** Execute the frame in the Python environment
		 *
		 * \returns The execution result code
		**////////////////////////////////////////////////////////////////////
		const SPELLexecutionResult execute();

		//////////////////////////////////////////////////////////////////////
		/** Provide the currently used execution model.
		 *
		 * \return The execution model in use.
		**////////////////////////////////////////////////////////////////////
		SPELLexecutionModel& getModel() { return *m_model; };

		//////////////////////////////////////////////////////////////////////
		/** Provide the model corresponding to the given identifier
		 *
		 * \return The execution model.
		**////////////////////////////////////////////////////////////////////
		SPELLexecutionModel& getModel( const std::string& code_id );

		//////////////////////////////////////////////////////////////////////
		/** Replay the frame structure in the given stack
		**////////////////////////////////////////////////////////////////////
		void replayStack( SPELLcallstackIF* callstack );

		//////////////////////////////////////////////////////////////////////
		/** Enable warmstart capabilities.
		 *
		 * \param warmstart IN: warmstart implementation
	     *
		**////////////////////////////////////////////////////////////////////
		void setWarmStart( SPELLwarmStart* warmstart ) { m_warmStart = warmstart; };

		//////////////////////////////////////////////////////////////////////
		/** Perform state save on demand
		**////////////////////////////////////////////////////////////////////
		void saveState();

		//////////////////////////////////////////////////////////////////////
		/** Restore execution from scratch (warm start)
		**////////////////////////////////////////////////////////////////////
		void restoreState();

		//////////////////////////////////////////////////////////////////////
		/** Restore execution from error (error recovery)
		**////////////////////////////////////////////////////////////////////
		void fixState();

		//////////////////////////////////////////////////////////////////////
		/** Check if warmstart is available
		**////////////////////////////////////////////////////////////////////
		const bool haveWarmStart() { return (m_warmStart != NULL); };

		//////////////////////////////////////////////////////////////////////
		/** \see SPELLdispatchListener.
		**////////////////////////////////////////////////////////////////////
		std::string getId() { return "(frame manager)"; };

		//////////////////////////////////////////////////////////////////////
		/** \see SPELLdispatchListener
		**////////////////////////////////////////////////////////////////////
		void callbackEventLine( PyFrameObject* frame, const std::string& file, const int line, const std::string& name);

		//////////////////////////////////////////////////////////////////////
		/** \see SPELLdispatchListener
		**////////////////////////////////////////////////////////////////////
		void callbackEventCall( PyFrameObject* frame, const std::string& file, const int line, const std::string& name);

		//////////////////////////////////////////////////////////////////////
		/** \see SPELLdispatchListener
		**////////////////////////////////////////////////////////////////////
		void callbackEventReturn( PyFrameObject* frame, const std::string& file, const int line, const std::string& name);

		//////////////////////////////////////////////////////////////////////
		/** \see SPELLdispatchListener
		**////////////////////////////////////////////////////////////////////
		void callbackEventError( PyFrameObject* frame, const std::string& file, const int line, const std::string& name );

		//////////////////////////////////////////////////////////////////////
		/** Notify about a stage event
		**////////////////////////////////////////////////////////////////////
		void eventStage();

		//////////////////////////////////////////////////////////////////////
		/** Obtain the current line number.
		 *
		 * \returns The current line nu7mer
		**////////////////////////////////////////////////////////////////////
		const unsigned int getCurrentLine();

		//////////////////////////////////////////////////////////////////////
		/** Check if we can go to the next line
		**////////////////////////////////////////////////////////////////////
		const bool canSkip();

		//////////////////////////////////////////////////////////////////////
		/** Move the execution to the next available line in the code block.
		 *
		 * \returns False if no next line is available in this block.
		**////////////////////////////////////////////////////////////////////
		const bool goNextLine();

		//////////////////////////////////////////////////////////////////////
		/** Move the execution to the line corresponding to the given label.
		 *
		 * \param label IN: label where to go
		 * \param report IN: report the jump to the user
		 *
		 * \returns True if success.
		**////////////////////////////////////////////////////////////////////
		const bool goLabel( const std::string& label, bool report );

		//////////////////////////////////////////////////////////////////////
		/** Move the execution to the line given.
		 *
		 * \param line IN: line where to go
		 * \param report IN: report the jump to the user
		 *
		 * \returns True if success.
		**////////////////////////////////////////////////////////////////////
		const bool goLine( const int& line, bool report );

		//////////////////////////////////////////////////////////////////////
		/** Run the given script in the frame context.
		 *
		 * \param script IN: code to execute
		**////////////////////////////////////////////////////////////////////
		void runScript( const std::string& script );

		//////////////////////////////////////////////////////////////////////
		/** Modify the current line
		**////////////////////////////////////////////////////////////////////
		const bool setNewLine( const int& lineno, const int& offset );

		//////////////////////////////////////////////////////////////////////
		/** Goto implementation
		**////////////////////////////////////////////////////////////////////
		bool programmedGoto( const int& frameLine );

		//////////////////////////////////////////////////////////////////////
		/** Reset the frame to its initial state. Recompile the script to
		 *  take into account any possible source code change in the meantime.
		**////////////////////////////////////////////////////////////////////
		void reset();

		//////////////////////////////////////////////////////////////////////
		/** Check if the frame is ready to work.
		**////////////////////////////////////////////////////////////////////
		bool isReady() { return (m_model!=NULL); };

		//////////////////////////////////////////////////////////////////////
		/** Terminate the frame execution right away
		**////////////////////////////////////////////////////////////////////
		void terminate();

		//////////////////////////////////////////////////////////////////////
		/** Callback for warmstart filter dict change notification
		**////////////////////////////////////////////////////////////////////
		void filterDictUpdated();

		//////////////////////////////////////////////////////////////////////
		/** Update the current frame being used in the Python environment
		 *
		 * \param frame IN: currently used frame
		 *
		**////////////////////////////////////////////////////////////////////
		void updateCurrentFrame( PyFrameObject* frame, int dispatchType );

		//////////////////////////////////////////////////////////////////////
		/** Get the current breakpoint map.
		 *
		 * \return Reference to the breakpoint map.
		**////////////////////////////////////////////////////////////////////
		SPELLbreakpoint& getBreakpoints() { return m_breakpoints; };

		//////////////////////////////////////////////////////////////////////
		/** Get the AST model.
		 *
		 * \return Reference to the breakpoint map.
		**////////////////////////////////////////////////////////////////////
		SPELLastAnalyzer& getAST() { return m_ast; };

		//////////////////////////////////////////////////////////////////////
		/** Get the trace model corresponding to the given code identifier
		 *
		 * \param codeId IN: the code identifier
		 *
		 * \return The corresponding trace model
		**////////////////////////////////////////////////////////////////////
		SPELLexecutionTrace& getTraceModel( const std::string& codeId );

		//////////////////////////////////////////////////////////////////////
		/** Get the currently used trace model
		 *
		 * \return The corresponding trace model
		**////////////////////////////////////////////////////////////////////
		SPELLexecutionTrace& getCurrentTraceModel();

		//////////////////////////////////////////////////////////////////////
		/** Check if we are at the initial frame
		 *
		 * \return True if the execution is at the initial frame
		**////////////////////////////////////////////////////////////////////
		bool isAtInitialFrame() { return (m_initialFrame == m_currentFrame); };

protected: //-------------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////
    // LIFECYCLE /////////////////////////////////////////////////////////////
    // METHODS ///////////////////////////////////////////////////////////////

private: //-------------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////

		/** Map of model objects */
		typedef std::map<std::string,SPELLexecutionModel*> ModelMap;

		/** Map of trace objects */
		typedef std::map<std::string,SPELLexecutionTrace> TraceMap;

		/** Ordered list of model keys */
		typedef std::vector<std::string> KeyList;

	// LIFECYCLE /////////////////////////////////////////////////////////////
    // METHODS ///////////////////////////////////////////////////////////////


		//////////////////////////////////////////////////////////////////////
		/** Compile the current script and obtain the bytecode.
		 *
		 * \throws SPELLcoreException if the file cannot be found, or the
		 *  compilation fails.
		**////////////////////////////////////////////////////////////////////
		void compile();

		//////////////////////////////////////////////////////////////////////
		/** Compile the given script and obtain the bytecode.
		 *
		 * \throws SPELLcoreException if the compilation fails.
		**////////////////////////////////////////////////////////////////////
		PyCodeObject* compileScript( const std::string& script );

		//////////////////////////////////////////////////////////////////////
		/** Create initial frame out of the current code
		**////////////////////////////////////////////////////////////////////
		void createInitialFrame();

		//////////////////////////////////////////////////////////////////////
		/** Create frame model for a new frame
		**////////////////////////////////////////////////////////////////////
		void createFrameModel( int callLine, PyFrameObject* frame );

		//////////////////////////////////////////////////////////////////////
		/** Check wether the execution failed due to an error or not.
		**////////////////////////////////////////////////////////////////////
		void checkRuntimeError();

		//////////////////////////////////////////////////////////////////////
		/** Retrieve the collection of initial names that shall be discarded
		 *  when existing variables are requested
		 *
		 *  \param frame IN: The frame which provides the initial vars
		**////////////////////////////////////////////////////////////////////
		void retrieveDiscardedNames(PyFrameObject* frame);

		//////////////////////////////////////////////////////////////////////
		/** Management of databases through modules
		**////////////////////////////////////////////////////////////////////
		void copyDatabase( const std::string& database, PyFrameObject* toFrame );

		//////////////////////////////////////////////////////////////////////
		/** Reset the execution trace
		**////////////////////////////////////////////////////////////////////
		void resetExecutionTrace();

		//////////////////////////////////////////////////////////////////////
		/** Update data for watch of variables when a new frame arrives
		**////////////////////////////////////////////////////////////////////
		void updateVariables( PyFrameObject* frame );

		//////////////////////////////////////////////////////////////////////
		/** Update SPELL builtins using the given frames
		**////////////////////////////////////////////////////////////////////
		void updateDefinitions( PyFrameObject* source, PyFrameObject* target );

		//////////////////////////////////////////////////////////////////////
		/** Update databases when a new frame arrives
		**////////////////////////////////////////////////////////////////////
		void updateDatabases( PyFrameObject* frame );

    // DATA MEMBERS //////////////////////////////////////////////////////////

		/** Warm start mechanism reference */
		SPELLwarmStart*       m_warmStart;
		/** Terminated flag */
		bool                  m_terminated;
		/** Error flag */
		bool                  m_inError;
		/** Execution result */
		SPELLexecutionResult  m_status;
		/** Script file being executed */
		std::string           m_filename;
		/** Python bytecode object */
		PyCodeObject*         m_code;
		/** Python frame, the first one in the stack */
		PyFrameObject*        m_initialFrame;
		/** Current python frame */
		PyFrameObject*        m_currentFrame;
		/** List of SPELL definitions filtered for warmstart */
		PyObject*             m_definitions;
		/** Holds the currently active execution model */
		SPELLexecutionModel*  m_model;
		/** Holds the AST analyzer */
		SPELLastAnalyzer      m_ast;
		/** Map of model objects per code object */
		ModelMap              m_modelMap;
		/** Ordered list of model keys */
		KeyList				  m_modelKeys;
		/** Holds the breakpoint map */
		SPELLbreakpoint       m_breakpoints;
		/** Holds the execution trace map */
		TraceMap              m_traceMap;
		/** Holds the current execution trace */
		std::string           m_currentTraceModel;
		/** Map of initial variables to discard on any request */
		std::set<std::string> m_discardedNames;
};

/*@}*/
#endif
