################################################################################
"""
DESCRIPTION: Helpers for telecommand wrapper functions. 
    
PACKAGE: spell.lang.helpers.tchelper

PROJECT: SPELL

 Copyright (C) 2008, 2010 SES ENGINEERING, Luxembourg S.A.R.L.

 This file is part of SPELL.

 This library is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, either
 version 3 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License and GNU General Public License (to which the GNU Lesser
 General Public License refers) along with this library.
 If not, see <http://www.gnu.org/licenses/>.
 
"""
###############################################################################

#===============================================================================
# SPELL imports
#===============================================================================
from spell.utils.log import *
from spell.lang.constants import *
from spell.lang.modifiers import *
from spell.lib.exception import *
from spell.utils.ttime import *
from spell.lang.functions import *
from spell.lib.adapter.tc_item import TcItemClass
from spell.lib.adapter.constants.notification import *
from spell.lib.registry import *

#===============================================================================
# Local imports
#===============================================================================
from basehelper import *

#===============================================================================
# System imports
#===============================================================================
import time,sys

################################################################################
class Send_Helper(WrapperHelper):

    """
    DESCRIPTION:
        Helper for the SendAndVerify wrapper.
    """    
    
    _isGroup = False
    _isSequence = False
    _cmdName = None
    _cmdDef = None
    _cmdArgs = None
    # This flag is used in case of failures. The user may want to resend the
    # command AND verify the parameters, or only repeat the parameters 
    # verification.
    __doSendCommand = True
    # This flag is used in case of failures. The user may want to resend the
    # command and verify the parameters, but not re-adjust the limits
    __doAdjustLimits = True
    __doAdjustLimitsP = True
    __doCheckTelemetry = True
    # True if adjusting limits is feasible
    __canAdjustLimits = False
    # Holds the current stage of the function (TC,TM,LIM) 
    __section = None
    __verifyCondition = None
    # Stores the original OnFailure config
    __originalOnFailure = None

    #===========================================================================
    def __init__(self):
        WrapperHelper.__init__(self,"TC")
        self._opName = "Send"
        self._reset()

    #===========================================================================
    def _reset(self):
        self._isGroup = False
        self.__originalOnFailure = None
        self._isSequence = False
        self._cmdName = None
        self._cmdDef = None
        self._cmdArgs = None
        self.__doSendCommand = True
        self.__doAdjustLimits = False
        self.__doAdjustLimitsP = False
        self.__canAdjustLimits = False
        self.__section = 'TC'
        self.__verifyCondition = None
        self.__doCheckTelemetry = False

    #===========================================================================
    def _obtainVerificationDefinition(self,*args,**kargs):
        # Obtain verification steps
        if self._cmdArgs is not None and len(args)>=3:
            self.__verifyCondition = args[3]
            if type(self.__verifyCondition) != list:
                raise SyntaxException("Expected a list of verification steps")
        elif self._cmdArgs is None and len(args)>=2:
            self.__verifyCondition = args[2]
            if type(self.__verifyCondition) != list:
                raise SyntaxException("Expected a list of verification steps")
        elif kargs.has_key('verify'):
            self.__verifyCondition = kargs.pop('verify')
        else:
            self.__verifyCondition = None
        if self.__verifyCondition:
            self.__doCheckTelemetry = True

    #===========================================================================
    def _obtainCommandDefinition(self, *args, **kargs):
        LOG("Obtaining command definition", level = LOG_LANG)
        if len(args) == 0:
            LOG("No positional arguments", level = LOG_LANG)
            # If no positional arguments are given, the command shall be
            # given with these keywords
            if not kargs.has_key('command') and\
               not kargs.has_key('sequence') and\
               not kargs.has_key('group'): 
                raise SyntaxException("Expected a command item or name")
            else:
                if kargs.has_key('command'):
                    LOG("Using keyword argument command", level = LOG_LANG)
                    self._isSequence = False
                    self._isGroup = False
                    self._cmdDef = kargs.pop('command')
                    if type(self._cmdDef)==list:
                        raise SyntaxException("Cannot accept list as single command")
                elif kargs.has_key('group'):
                    LOG("Using keyword argument group", level = LOG_LANG)
                    self._isSequence = False
                    self._isGroup = True
                    self._cmdDef = kargs.pop('group')
                    if type(self._cmdDef)!=list:
                        raise SyntaxException("Shall provide a command list")
                else:
                    LOG("Using keyword argument sequence", level = LOG_LANG)
                    self._isSequence = True
                    self._isGroup = False
                    self._cmdDef = kargs.pop('sequence')
                    if type(self._cmdDef)==list:
                        raise SyntaxException("Cannot accept command list as a sequence")
        else:
            raise SyntaxException("Expected keyword: command, group or sequence")
    
        # Create the command item if necessary
        if type(self._cmdDef)==str:
            self._cmdDef = REGISTRY['TC'][self._cmdDef]
        # Do it for each item in the list, if it is the case
        elif type(self._cmdDef)==list:
            cpy = []
            for item in self._cmdDef:
                if type(item)==str:
                    cpy += [REGISTRY['TC'][item]]
                elif isinstance(item,TcItemClass):
                    cpy += [item]
                else:
                    raise SyntaxException("Unexpected item in group: " + repr(item))

        # Obtain the string representation of the entity being sent
        if type(self._cmdDef)==list:
            self._cmdName = []
            for item in self._cmdDef:
                if type(item)==str:
                    self._cmdName += [item]
                # Must be tc item, the check was done already
                else:
                    self._cmdName += [item.name()]
                # The else case is already controlled
        else:
            self._cmdName = self._cmdDef.name()
            
        LOG("Got command definition: " + str(self._cmdName), level = LOG_LANG)
        LOG("Sequence flag: " + str(self._isSequence), level = LOG_LANG)
        LOG("Group flag   : " + str(self._isGroup), level = LOG_LANG)

        # Copy the flags to config
        self._config[Sequence] = self._isSequence

    #===========================================================================
    def _checkCommandDefinition(self):
        if not isinstance(self._cmdDef,TcItemClass) and\
           not type(self._cmdDef) == str and\
           not type(self._cmdDef) == list:
            raise SyntaxException("Expected a TC name, TC item or TC list")
        
    #===========================================================================
    def _obtainCommandArguments(self, *args, **kargs):
        # 3. Obtain the arguments
        self._cmdArgs = None
        if not self._isGroup:
            LOG("Getting arguments for single command", level = LOG_LANG)
            if kargs.has_key('args'):
                LOG("Using keyword args", level = LOG_LANG)
                self._cmdArgs = kargs.pop('args')
            else:
                LOG("No arguments found", level = LOG_LANG)
                self._cmdArgs = None
        # Using a group and args kword is not accepted (??)
        else:
            if kargs.has_key('args'):
                raise SyntaxException("Cannot use args with TC lists")
        
    #===========================================================================
    def _parseCommandArguments(self):
        # 6. Parse arguments if any
        if self._cmdArgs is not None:
            if len(self._cmdArgs)==0:
                raise SyntaxException("Cannot accept empty argument list")
            # Clear any previously existing argument
            self._cmdDef.clear()
            for argument in self._cmdArgs:
                if type(argument)!=list:
                    raise SyntaxException("Malformed argument")
                if len(argument)<1 or type(argument[0])!=str:
                    raise SyntaxException("Malformed argument")
                argName = argument[0]
                argument = argument[1:]
                LOG("Set argument: " + str(argName) + "=" + repr(argument), level = LOG_LANG)
                self._cmdDef[argName] = argument
        
    #===========================================================================
    def _checkCommandArguments(self):
        if not self._cmdArgs is None and type(self._cmdArgs)!=list:
            raise SyntaxException("Expected an argument list")

    #===========================================================================
    def _doPreOperation(self, *args, **kargs ):
        
        #-----------------------------------------------------------------------
        # Parse the command information
        #-----------------------------------------------------------------------
        # 1. Obtain the command/sequence
        self._obtainCommandDefinition(*args,**kargs)
        # 2. Check the command correctness
        self._checkCommandDefinition()
        # 3. Obtain tc arguments
        self._obtainCommandArguments(*args,**kargs)
        # 4. Check arguments correctness
        self._checkCommandArguments()
        # 5. Parse command arguments
        self._parseCommandArguments()

        if self._isSequence:
            self._write("Sending sequence " +  repr(self._cmdName))
        elif self._isGroup:
            self._write("Sending group of " + str(len(self._cmdDef)) + " commands")
            for name in self._cmdName:
                self._write("    - Command: " + repr(name))
        else:
            self._write("Sending command " +  repr(self._cmdName))

        #-----------------------------------------------------------------------
        # Parse the telemetry information
        #-----------------------------------------------------------------------
        self._obtainVerificationDefinition(*args,**kargs)
        if type(self.__verifyCondition)==list:
            if type(self.__verifyCondition[0])!=list:
                self.__verifyCondition = [self.__verifyCondition]

        #-----------------------------------------------------------------------
        # Avoid alarms if the conditions are ok
        #-----------------------------------------------------------------------
        self.__doAdjustLimits = self._config.has_key(AdjLimits) and \
                            type(self.__verifyCondition)==list and \
                            self._config.get(AdjLimits)==True
        self.__doAdjustLimitsP = self.__doAdjustLimits
        self.__canAdjustLimits = self.__doAdjustLimits
                            
    #===========================================================================
    def _doOperation(self, *args, **kargs ):

        repeat = False
        self.__originalOnFailure = self._config[OnFailure]

        #-----------------------------------------------------------------------
        # LIMIT ADJUSTMENT SECTION
        #-----------------------------------------------------------------------
        if self.__canAdjustLimits and self.__doAdjustLimitsP:
            self.__section = 'LIM1'
            # We don't allow resend nor recheck, only repeat
            self._config[OnFailure] = self._config[OnFailure] & (~RESEND)
            self._config[OnFailure] = self._config[OnFailure] & (~RECHECK)
            
            # We need to enlarge the limit range to the maximum to
            # avoid alarms (analog parameters) or to allow any
            # status value (status parameters)
            REGISTRY['CIF'].write("Avoiding alarms by adjusting limits before TC execution")
            for condition in self.__verifyCondition:
                paramName = condition[0]
                paramValue = condition[2]
                operator = condition[1]
                
                # Do not adjust limits if the condition config dict says the contrary
                if type(condition[-1])==dict:
                    itemCfg = condition[-1]
                    if itemCfg.has_key(AdjLimits) and itemCfg[AdjLimits] == False: continue
                        
                # Do not adjust limits if eq operator is not used
                if operator != eq: continue
                
                # Proceed with limit adjustment
                if type(paramValue)==str: #Status parameters
                    # First get the currentValue
                    currentValue = REGISTRY['TM'][paramName].eng( Wait = False )
                    # Build the expected value list
                    if (currentValue != paramValue):
                        expectedValues = currentValue + ", " + paramValue
                    else:
                        continue
                    limits = {}
                    limits[Expected] = expectedValues
                    
                    # Adjust the limits accordingly
                    REGISTRY['CIF'].write("    - " + repr(paramName) + " adjusting to expected values: " + expectedValues)
                else: #Analog parameters
                    # Set the limit to the maximum value
                    limits = {}
                    limits[LoRed] = -1.7e+308
                    limits[LoYel] = -1.7e+308
                    limits[HiRed] = 1.7e+308
                    limits[HiYel] = 1.7e+308
                    REGISTRY['CIF'].write("    - " + repr(paramName) + " enlarged analog limits to the maximum")
                REGISTRY['TM'].setLimits( paramName, limits, config = self._config )

        # Reset the OnFailure config
        self._config[OnFailure] = self.__originalOnFailure
                    
        #-----------------------------------------------------------------------
        # COMMAND SECTION
        #-----------------------------------------------------------------------
        # If we are repeating the operation due to an user action, check
        # the flag to see if we have to resend the command

        if self.__doSendCommand:
            self.__section = 'TC'
            # We do not allow recheck or repeat yet, only resend
            self._config[OnFailure] = self._config[OnFailure] & (~REPEAT)
            self._config[OnFailure] = self._config[OnFailure] & (~RECHECK)

            try:
                # Actually send the command
                tcIsSuccess = REGISTRY['TC'].send(self._cmdDef, config = self._config )
                
            # Handle cancel exception only
            except CancelException:
                LOG("Cancelled command execution")
                self._write("Execution cancelled", {Severity:WARNING} )
                return [False,False]

            if tcIsSuccess:
                self._write("Execution success")
            else:
                self._write("Execution failed", {Severity:ERROR} )
                raise DriverException("Command execution failed")
        else:
            tcIsSuccess = True
        
        # Reset the OnFailure config
        self._config[OnFailure] = self.__originalOnFailure

        #-----------------------------------------------------------------------
        # TELEMETRY SECTION
        #-----------------------------------------------------------------------
        # If there are verification sets, verify them
        if self.__doCheckTelemetry and self.__verifyCondition and tcIsSuccess:
            self.__section = 'TM'

            # Wait some time before verifying if requested
            if self._config.has_key(Delay):
                delay = self._config.get(Delay)
                if delay:
                    from spell.lang.functions import WaitFor
                    self._write("Waiting "+ str(delay) + " seconds before TM verification", {Severity:INFORMATION})
                    WaitFor(delay, Notify=False, Verbosity=999)

            # We dont allow repeat here but allow recheck at least
            self._config[OnFailure] = self._config[OnFailure] & (~REPEAT)
            #self._config[OnFailure] = self._config[OnFailure] | RECHECK
            #self._config[OnFailure] = self._config[OnFailure] | RESEND

            # Perform verification
            tmIsSuccess = REGISTRY['TM'].verify(self.__verifyCondition, config=self._config)

            #repeat, tmIsSuccess = self._processActionOnResult(tmIsSuccess)
            
        else:
            tmIsSuccess = True

        # Reset the OnFailure config
        self._config[OnFailure] = self.__originalOnFailure

        #-----------------------------------------------------------------------
        # ADJUST LIMITS SECTION
        #-----------------------------------------------------------------------
        if tmIsSuccess and self.__canAdjustLimits and self.__doAdjustLimits:
            self.__section = "LIM2"
            # We dont allow recheck/resend for this, only repeat if the user wants
            self._config[OnFailure] = self._config[OnFailure] & (~RESEND)
            self._config[OnFailure] = self._config[OnFailure] & (~RECHECK)

            REGISTRY['CIF'].write("Adjusting limit definitions after TC execution")
            for condition in self.__verifyCondition:
                paramName = condition[0]
                paramValue = condition[2]
                operator = condition[1]
                
                # Do not adjust limits if not eq operator used 
                if operator != eq: continue

                # Do not adjust limits if the condition config dict says the contrary
                if type(condition[-1])==dict:
                    itemCfg = condition[-1]
                    if itemCfg.has_key(AdjLimits) and itemCfg[AdjLimits] == False: continue
                        
                if type(paramValue)==str: #Status parameters
                    # Build the expected value list
                    limits = {}
                    limits[Expected] = paramValue
                    # Adjust the limits accordingly
                    REGISTRY['CIF'].write("    - " + repr(paramName) + " adjusting to expected value: " + paramValue)
                else: #Analog parameters
                    # Set the limit to the maximum value
                    tolerance = self.getConfig(Tolerance)
                    if tolerance is None: tolerance = 0.1
                    limits = {}
                    limits[LoRed] = paramValue - tolerance
                    limits[LoYel] = paramValue - tolerance
                    limits[HiRed] = paramValue + tolerance
                    limits[HiYel] = paramValue + tolerance
                    REGISTRY['CIF'].write("    - " + repr(paramName) + " limits set to " + repr(limits))
                REGISTRY['TM'].setLimits( paramName, limits, config = self._config )

        # Reset the OnFailure config
        self._config[OnFailure] = self.__originalOnFailure

        # Depending on the result of both operations we decide to repeat the whole
        # or part of the operation.

        if self.__verifyCondition is None:
            result = tcIsSuccess
        else:
            result = tcIsSuccess and tmIsSuccess

        return [ repeat, result ]

    #===========================================================================
    def _doSkip(self):
        self._write("Operation skipped", {Severity:WARNING} )
        # By skipping the operation, if we are in LIM1 or TC stages we still
        # want to verify TM
        if self.__section in ['LIM1','TC']:
            self.__doAdjustLimitsP = False
            self.__doAdjustLimits = False
            self.__doSendCommand = False
            self.__doCheckTelemetry = True
            return [True,False]
        elif self.__section == 'TM':
            self.__doAdjustLimitsP = False
            self.__doAdjustLimits = False
            self.__doSendCommand = False
            self.__doCheckTelemetry = False
            return [True,False]
        else:
            return [False,True]

    #===========================================================================
    def _doCancel(self):
        self._write("Operation cancelled", {Severity:WARNING} )
        return [False,False]

    #===========================================================================
    def _doResend(self):
        if self._isSequence:
            self._write("Retrying sequence execution", {Severity:WARNING} )
        elif self._isGroup:
            self._write("Retrying group execution", {Severity:WARNING} )
        else:
            self._write("Retrying command execution", {Severity:WARNING} )
        self.__doSendCommand = True
        self.__doAdjustLimitsP = False
        self.__doCheckTelemetry = True
        return [True,False]
                
    #===========================================================================
    def _doRepeat(self):
        self._write("Retry whole operation", {Severity:WARNING} )
        self.__doAdjustLimits = True
        self.__doAdjustLimitsP = True
        self.__doSendCommand = True
        self.__doCheckTelemetry = True
        return [True,False]

    #===========================================================================
    def _doRecheck(self):
        self._write("Retry verification block", {Severity:WARNING} )
        self.__doSendCommand = False
        self.__doAdjustLimitsP = False
        self.__doAdjustLimits = True
        self.__doCheckTelemetry = True
        return [True,False]

################################################################################
class BuildTC_Helper(WrapperHelper):

    """
    DESCRIPTION:
        Helper for the Build TC wrapper.
    """    
    _tcName = None
    _tcArguments = []
    _tcItem = None
    _isSequence = False
    
    #===========================================================================
    def __init__(self):
        WrapperHelper.__init__(self, "TC")
        self._tcName = None
        self._tcArguments = []
        self._opName = "TC build"
        self._tcItem = None
        self._isSequence = False

    #===========================================================================
    def _obtainCommandName(self, *args, **kargs ):
        if len(args)==1: 
            if type(args[0])!=str:
                raise SyntaxException("Expected a command name")
            self._tcName = args[0]
        elif len(args)==0:
            if kargs.has_key('command'):
                self._tcName = kargs.get('command')
            elif kargs.has_key('sequence'):
                self._tcName = kargs.get('sequence')
                self._isSequence = True
            else:
                raise SyntaxException("Expected a command or sequence")
        else:
            raise SyntaxException("Expected a command name")
        
    #===========================================================================
    def _obtainCommandArguments(self, *args, **kargs ):
        if len(args)<=1: 
            if kargs.has_key('args'):
                self._tcArguments = kargs.get('args')
        else:
            if type(args[1])!=list:
                raise SyntaxException("Expected a list of arguments")
            self._tcArguments = args[1]

    #===========================================================================
    def _doPreOperation(self, *args, **kargs ):
        self._obtainCommandName(*args,**kargs)
        self._obtainCommandArguments(*args,**kargs)

    #===========================================================================
    def _doOperation(self, *args, **kargs ):

        if self._isSequence:
            self._write("Building sequence " + repr(self._tcName))
        else:
            self._write("Building command " + repr(self._tcName))

        # Create the item
        LOG("Obtaining TC entity: " + repr(self._tcName), level = LOG_LANG)
        self._tcItem = REGISTRY['TC'][self._tcName]
        self._tcItem.clear()
        self._tcItem.configure(self._config.copy())
        
        # Assign the arguments
        for tcArg in self._tcArguments:
            LOG("Parsed TC argument: " + repr(tcArg[0]), level = LOG_LANG)
            LOG("Argument config   : " + repr(tcArg[1:]), level = LOG_LANG)
            self._tcItem[ tcArg[0] ] = tcArg[1:]
            self._write("    - Argument " + repr(tcArg[0]) + " value " + repr(tcArg[1:]))
            
        return [False,self._tcItem]

    #===========================================================================
    def _doSkip(self):
        self._write("Skipping command construction", {Severity:WARNING} )
        self._tcItem = None
        return [False,None]

    #===========================================================================
    def _doRepeat(self):
        self._write("Repeat command construction", {Severity:WARNING} )
        return [True,False]
        
################################################################################
class BuildMemoryLoad_Helper(BuildTC_Helper):

    """
    DESCRIPTION:
        Helper for the BuildMemoryLoad wrapper.
    """    

    #===========================================================================
    def __init__(self):
        BuildTC_Helper.__init__(self)

    #===========================================================================
    def _doOperation(self, *args, **kargs ):
        repeat, tcItem = super(BuildMemoryLoad_Helper, self)._doOperation(args,kargs);
        tcItem._isMemoryLoad = True

        return [repeat,tcItem]
