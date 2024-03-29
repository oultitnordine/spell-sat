// ################################################################################
// FILE       : SPELLsocket.H
// DATE       : Mar 17, 2011
// PROJECT    : SPELL
// DESCRIPTION: Socket wrapper
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

#ifndef __SPELL_SOCKETS_H__
#define __SPELL_SOCKETS_H__

// FILES TO INCLUDE ////////////////////////////////////////////////////////
// System includes ---------------------------------------------------------
// Local includes ----------------------------------------------------------
#include "SPELL_IPC/SPELLipc.H"
// Project includes --------------------------------------------------------

/** \addtogroup SPELL_IPC */
/*@{*/


// FORWARD REFERENCES //////////////////////////////////////////////////////
// TYPES ///////////////////////////////////////////////////////////////////
// DEFINES /////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////
/**
 ** \brief Socket implementation wrapper
 **
 *////////////////////////////////////////////////////////////////////////////
class SPELLsocket
{
public: //--------------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////
    // LIFECYCLE /////////////////////////////////////////////////////////////

		/*////////////////////////////////////////////////////////////////////
		* Constructor
		*
		* \param fd IN: socket file descriptor.
		*/////////////////////////////////////////////////////////////////////
		SPELLsocket( int fd );

		/*////////////////////////////////////////////////////////////////////
		* Destructor
		*/////////////////////////////////////////////////////////////////////
		~SPELLsocket();

    // METHODS ///////////////////////////////////////////////////////////////

		/*////////////////////////////////////////////////////////////////////
		* Receive data from socket. Blocking call.
		*
		* \param buffer INOUT: buffer to store the data.
		* \param size IN: bytes to read
		*
		* \return Actual amount of bytes read
		*/////////////////////////////////////////////////////////////////////
		int receive( void* buffer, int size );

		/*////////////////////////////////////////////////////////////////////
		* Receive all data from socket. Blocking call.
		*
		* \param buffer INOUT: buffer to store the data.
		* \param size IN: bytes to read
		*
		*/////////////////////////////////////////////////////////////////////
		int receiveAll( void* buffer, int size );

		/*////////////////////////////////////////////////////////////////////
		* Send data through socket.
		*
		* \param buffer INOUT: buffer to read the data from.
		* \param size IN: bytes to write
		*
		* \return Actual amount of bytes written
		*/////////////////////////////////////////////////////////////////////
		int send( const void* buffer, int size );

		/*////////////////////////////////////////////////////////////////////
		* Accept a new client on this listening socket.
		*
		* \param disconnected INOUT: disconnected flag.
		*
		* \return Socket associated to the client.
		*/////////////////////////////////////////////////////////////////////
		SPELLsocket* acceptClient( bool* disconnected );

		/*////////////////////////////////////////////////////////////////////
		* Wait until data arrives.
		*
		* \param timeout IN: maximum time to wait.
		*
		* \return True if there is a timeout.
		*/////////////////////////////////////////////////////////////////////
		bool waitData( int timeout );

		/*////////////////////////////////////////////////////////////////////
		* Disconnect the socket.
		*/////////////////////////////////////////////////////////////////////
		void disconnect();

		/*////////////////////////////////////////////////////////////////////
		* Shutdown the socket input.
		*/////////////////////////////////////////////////////////////////////
		void shutdownRead();

		/*////////////////////////////////////////////////////////////////////
		* Shutdown the socket output.
		*/////////////////////////////////////////////////////////////////////
		void shutdownWrite();

		/*////////////////////////////////////////////////////////////////////
		* Shutdown both socket input and socket output.
		*/////////////////////////////////////////////////////////////////////
		int shutdown();

		/*////////////////////////////////////////////////////////////////////
		* Hard-close the socket connection.
		*/////////////////////////////////////////////////////////////////////
		void close();

    // STATIC ////////////////////////////////////////////////////////////////

		/*////////////////////////////////////////////////////////////////////
		* Create a socket connected to the given server.
		*
		* \param server IN: server hostname
		* \param port IN: socket port.
		*
		* \return The new socket.
		*/////////////////////////////////////////////////////////////////////
		static SPELLsocket* connect( const std::string& server, const int& port );

		/*////////////////////////////////////////////////////////////////////
		* Listen to the given local port and return a socket for a new
		* incoming connection.
		*
		* \param port INOUT: listening port and the port for the client connection
		*
		* \return The new socket.
		*/////////////////////////////////////////////////////////////////////
		static SPELLsocket* listen( int* port );

    // DATA MEMBERS //////////////////////////////////////////////////////////

protected: //-----------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////
    // LIFECYCLE /////////////////////////////////////////////////////////////
    // METHODS ///////////////////////////////////////////////////////////////
    // STATIC ////////////////////////////////////////////////////////////////
    // DATA MEMBERS //////////////////////////////////////////////////////////

private: //-------------------------------------------------------------------

    // EXCEPTIONS ////////////////////////////////////////////////////////////
    // ENUMS /////////////////////////////////////////////////////////////////
    // TYPES /////////////////////////////////////////////////////////////////
    // LIFECYCLE /////////////////////////////////////////////////////////////
    // METHODS ///////////////////////////////////////////////////////////////
    // STATIC ////////////////////////////////////////////////////////////////
    // DATA MEMBERS //////////////////////////////////////////////////////////
		/** Socket file descriptor */
		int m_socketFd;
		/** Socket connected flag */
		bool m_connected;
};

/*@}*/
#endif

