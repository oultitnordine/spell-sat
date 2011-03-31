///////////////////////////////////////////////////////////////////////////////
//
// PACKAGE   : com.astra.ses.spell.gui.preferences
// 
// FILE      : ConfigurationManager.java
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
package com.astra.ses.spell.gui.preferences;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.astra.ses.spell.gui.core.model.server.ServerInfo;
import com.astra.ses.spell.gui.core.model.services.BaseService;
import com.astra.ses.spell.gui.core.model.types.Environment;
import com.astra.ses.spell.gui.core.model.types.ExecutorStatus;
import com.astra.ses.spell.gui.core.model.types.ICoreConstants;
import com.astra.ses.spell.gui.core.model.types.ItemStatus;
import com.astra.ses.spell.gui.core.model.types.Level;
import com.astra.ses.spell.gui.core.model.types.Scope;
import com.astra.ses.spell.gui.core.utils.Logger;
import com.astra.ses.spell.gui.preferences.initializer.elements.StyleInfo;
import com.astra.ses.spell.gui.preferences.keys.FontKey;
import com.astra.ses.spell.gui.preferences.keys.GuiColorKey;
import com.astra.ses.spell.gui.preferences.keys.PreferenceCategory;
import com.astra.ses.spell.gui.preferences.keys.ProcColorKey;
import com.astra.ses.spell.gui.preferences.keys.PropertyKey;
import com.astra.ses.spell.gui.preferences.keys.StatusColorKey;
import com.astra.ses.spell.gui.preferences.model.BooleanValue;
import com.astra.ses.spell.gui.preferences.model.PresentationsManager;
import com.astra.ses.spell.gui.preferences.model.ServersManager;
import com.astra.ses.spell.gui.preferences.utils.PreferencesConverter;

/*******************************************************************************
 * Configuration manager provides attributes stored in the preferences system
 ******************************************************************************/
public class ConfigurationManager extends BaseService
{

	// PRIVATE
	// ------------------------------------------------------------------
	/** Holds the path of the XML configuration file */
	private static final String	DEFAULT_CONFIG_PATH	   = "config";
	private static final String	INTERNAL_CONFIG_PATH	= "data";
	private static final String	DEFAULT_CONFIG_FILE	   = "default-config.xml";
	/** Preferences service */
	private IPreferenceStore	m_preferences;
	// PUBLIC ------------------------------------------------------------------
	/** Holds the service ID */
	public static final String	ID	                   = "com.astra.ses.spell.gui.config.ConfigurationManager";
	/** Presentations separator */
	public static final String	PRESENTATION_SEPARATOR	= "<!>";
	/** Holds the current configuration file */
	private String	            m_configFile;

	/***************************************************************************
	 * Default constructor
	 **************************************************************************/
	public ConfigurationManager()
	{
		super(ID);
		m_configFile = resolveConfigurationFile();
		Logger.debug("Created", Level.CONFIG, this);
	}

	@Override
	public void cleanup()
	{
		Logger.debug("Cleanup", Level.CONFIG, this);
	}

	@Override
	public void setup()
	{
		m_preferences = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void subscribe()
	{
	}

	/***************************************************************************
	 * Get the configuration file
	 **************************************************************************/
	public String getConfigurationFile()
	{
		return m_configFile;
	}

	/***************************************************************************
	 * Get the configuration file
	 **************************************************************************/
	private String resolveConfigurationFile()
	{
		// File separator
		String pathSeparator = File.separator;
		// Home
		String home = System.getenv(ICoreConstants.CLIENT_HOME_ENV);
		if (home == null)
		{
			home = ".";
		}

		String[] args = Platform.getApplicationArgs();
		String path = null;
		if (args.length > 0)
		{
			int count = 0;
			for (String arg : args)
			{
				if (arg.equals("-config") && (args.length - 1) > count)
				{
					path = args[count + 1];
					break;
				}
				count++;
			}
		}
		if (path == null)
		{
			path = home + pathSeparator + DEFAULT_CONFIG_PATH + pathSeparator
			        + DEFAULT_CONFIG_FILE;
			if (!(new File(path).canRead()))
			{
				String loc = Platform.getBundle(Activator.PLUGIN_ID)
				        .getLocation();
				loc = loc.substring(loc.lastIndexOf(":") + 1, loc.length());
				path = loc + pathSeparator + INTERNAL_CONFIG_PATH
				        + pathSeparator + DEFAULT_CONFIG_FILE;
			}
		}
		return path;
	}

	/***************************************************************************
	 * Get the value of a environment variable
	 * 
	 * @param var
	 *            the {@link Environment} variable
	 * @return the variable value as it is defined in the os
	 **************************************************************************/
	public String getEnvironmentVariable(Environment var)
	{
		return System.getenv(var.systemName());
	}

	/***************************************************************************
	 * Obtain the list of available servers. This list is defined in the XML
	 * configuration file.
	 * 
	 * @return The list of available servers
	 **************************************************************************/
	public String[] getAvailableServers()
	{
		String managerRepr = m_preferences
		        .getString(PreferenceCategory.SERVER.tag);
		ServersManager manager = ServersManager.fromString(managerRepr);
		return manager.getServerIds();
	}

	/***************************************************************************
	 * Update available servers info
	 * 
	 * @param servers
	 **************************************************************************/
	public void updateServers(ServerInfo[] servers)
	{
		ServersManager mgr = new ServersManager(servers);
		String repr = mgr.toString();
		m_preferences.setValue(PreferenceCategory.SERVER.tag, repr);
	}

	/***************************************************************************
	 * Obtain the server information
	 * 
	 * @return Server data structure
	 **************************************************************************/
	public ServerInfo getServerData(String serverID)
	{
		String managerRepr = m_preferences
		        .getString(PreferenceCategory.SERVER.tag);
		ServersManager manager = ServersManager.fromString(managerRepr);
		return manager.getServer(serverID);
	}

	/***************************************************************************
	 * Restore servers configuration to its default value
	 **************************************************************************/
	public void restoreServers()
	{
		m_preferences.setToDefault(PreferenceCategory.SERVER.tag);
		m_preferences.setToDefault(PropertyKey.INITIAL_SERVER
		        .getPreferenceName());
	}

	/***************************************************************************
	 * Get the required procedure presentations
	 **************************************************************************/
	public Vector<String> getPresentations()
	{
		String reprPresentations = m_preferences
		        .getString(PreferenceCategory.PRESENTATIONS.tag);
		PresentationsManager mgr = PresentationsManager
		        .valueOf(reprPresentations);
		return mgr.getEnabledPresentations();
	}

	/***************************************************************************
	 * Get user disabled presentations
	 * 
	 * @return
	 **************************************************************************/
	public Vector<String> getDisabledPresentations()
	{
		String reprPresentations = m_preferences
		        .getString(PreferenceCategory.PRESENTATIONS.tag);
		PresentationsManager mgr = PresentationsManager
		        .valueOf(reprPresentations);
		return mgr.getDisabledPresentations();
	}

	/***************************************************************************
	 * Update presentations property
	 **************************************************************************/
	public void updatePresentations(String[] enabled, String[] disabled)
	{
		Vector<String> enabledPres = new Vector<String>(Arrays.asList(enabled));
		Vector<String> disabledPres = new Vector<String>(
		        Arrays.asList(disabled));
		PresentationsManager mgr = new PresentationsManager(enabledPres,
		        disabledPres);
		String serialized = mgr.toString();
		m_preferences
		        .putValue(PreferenceCategory.PRESENTATIONS.tag, serialized);
	}

	/***************************************************************************
	 * Restore presentations to its default value
	 **************************************************************************/
	public void restorePresentations()
	{
		m_preferences.setToDefault(PreferenceCategory.PRESENTATIONS.tag);
	}

	/***************************************************************************
	 * Return a preferences values as stored in the eclipse preferences system
	 * 
	 * @param id
	 *            the id of the property to look for
	 * @return the stringified value
	 **************************************************************************/
	public String getProperty(PropertyKey pref)
	{
		return m_preferences.getString(pref.getPreferenceName());
	}

	/***************************************************************************
	 * Store a value in the preferences node
	 * 
	 * @param id
	 *            the id of the property to look for
	 * @return the stringified value
	 **************************************************************************/
	public void setProperty(PropertyKey pref, String value)
	{
		m_preferences.setValue(pref.getPreferenceName(), value);
		if (pref.equals(PropertyKey.DEBUG_LEVEL))
		{
			Logger.setShowLevel(value);
		}
	}

	/***************************************************************************
	 * Reset this property to its defautl value
	 * 
	 * @param pref
	 **************************************************************************/
	public void resetProperty(PropertyKey pref)
	{
		m_preferences.setToDefault(pref.getPreferenceName());
	}

	/***************************************************************************
	 * Return a preferences values as stored in the eclipse preferences system,
	 * but as a boolean value
	 * 
	 * @param id
	 *            the id of the property to look for
	 * @return the stringified value
	 **************************************************************************/
	public boolean getBooleanProperty(PropertyKey pref)
	{
		String value = this.getProperty(pref);
		return BooleanValue.valueOf(value).booleanValue();
	}

	/***************************************************************************
	 * Store a boolean value
	 * 
	 * @param pref
	 * @param value
	 **************************************************************************/
	public void setBooleanProperty(PropertyKey pref, boolean value)
	{
		m_preferences.setValue(pref.getPreferenceName(),
		        BooleanValue.valueOf(value).toString());

		if (pref.equals(PropertyKey.USE_TRACES))
		{
			Logger.enableTraces(value);
		}
		else if (pref.equals(PropertyKey.SHOW_DEBUG))
		{
			Logger.showDebug(value);
		}
	}

	/***************************************************************************
	 * Obtain a predefined color
	 * 
	 * @param code
	 *            Color code
	 * @return The color object
	 **************************************************************************/
	public Color getStatusColor(ItemStatus status)
	{
		StatusColorKey key = PreferencesConverter.getStatusColor(status);
		return getColor(key.getPreferenceName());
	}

	/***************************************************************************
	 * Store a color model in preferences
	 * 
	 * @param key
	 * @param color
	 **************************************************************************/
	public void setStatusColor(ItemStatus status, RGB color)
	{
		StatusColorKey key = PreferencesConverter.getStatusColor(status);
		setColor(key.getPreferenceName(), color);
	}

	/***************************************************************************
	 * Set the preference related to the given {@link StatusColorKey} to its
	 * default value
	 * 
	 * @param key
	 **************************************************************************/
	public void resetStatusColor(StatusColorKey key)
	{
		m_preferences.setToDefault(key.getPreferenceName());
	}

	/***************************************************************************
	 * Obtain a predefined color
	 * 
	 * @param code
	 *            Color code
	 * @return The color object
	 **************************************************************************/
	public Color getGuiColor(GuiColorKey key)
	{
		return getColor(key.getPreferenceName());
	}

	/***************************************************************************
	 * Store a color model in preferences
	 * 
	 * @param key
	 * @param color
	 **************************************************************************/
	public void setGuiColor(GuiColorKey key, RGB color)
	{
		setColor(key.getPreferenceName(), color);
	}

	/***************************************************************************
	 * Set the preference related to the given {@link GuiColorKey} to its
	 * default value
	 * 
	 * @param key
	 **************************************************************************/
	public void resetGuiColor(GuiColorKey key)
	{
		m_preferences.setToDefault(key.getPreferenceName());
	}

	/***************************************************************************
	 * Obtain a predefined color
	 * 
	 * @param code
	 *            Color code
	 * @return The color object
	 **************************************************************************/
	public Color getProcedureColor(ExecutorStatus status)
	{
		return getColor(ProcColorKey.getPreferenceName(status));
	}

	/***************************************************************************
	 * Store a color model in preferences
	 * 
	 * @param key
	 * @param color
	 **************************************************************************/
	public void setProcedureColor(ExecutorStatus status, RGB color)
	{
		setColor(ProcColorKey.getPreferenceName(status), color);
	}

	/***************************************************************************
	 * Set the preference related to the given {@link ProcColorKey} to its
	 * default value
	 * 
	 * @param key
	 **************************************************************************/
	public void resetProcedureColor(ExecutorStatus st)
	{
		m_preferences.setToDefault(ProcColorKey.getPreferenceName(st));
	}

	/***************************************************************************
	 * Obtain a predefined style
	 **************************************************************************/
	public Color getScopeColor(Scope scope)
	{
		String prefId = PreferenceCategory.STYLES.tag + "." + scope.tag;
		String styleRepr = m_preferences.getString(prefId);
		if ((styleRepr != null) && (!styleRepr.trim().isEmpty()))
		{
			StyleInfo info = StyleInfo.valueOf(styleRepr);
			RGB rgb = new RGB(info.color_r, info.color_g, info.color_b);
			return new Color(Display.getCurrent(), rgb);
		}
		return null;
	}

	/***************************************************************************
	 * Obtain a predefined style
	 **************************************************************************/
	public String getScopeFont(Scope scope)
	{
		String styleRepr = m_preferences
		        .getString(PreferenceCategory.STYLES.tag + "." + scope.tag);
		StyleInfo info = StyleInfo.valueOf(styleRepr);
		return info.font;
	}

	/***************************************************************************
	 * Obtain a predefined style
	 **************************************************************************/
	public int getScopeStyle(Scope scope)
	{
		String styleRepr = m_preferences
		        .getString(PreferenceCategory.STYLES.tag + "." + scope.tag);
		StyleInfo info = StyleInfo.valueOf(styleRepr);
		return info.style;
	}

	/***************************************************************************
	 * Update the style info associated to the given scope
	 **************************************************************************/
	public void setScopeStyleInfo(Scope scope, StyleInfo style)
	{
		String prefId = PreferenceCategory.STYLES.tag + "." + scope.tag;
		String styleRepr = style.toString();
		m_preferences.setValue(prefId, styleRepr);
	}

	/***************************************************************************
	 * Restore scope styles to its default values
	 **************************************************************************/
	public void restoreScopeStyleInfo()
	{
		for (Scope scope : Scope.values())
		{
			String key = PreferenceCategory.STYLES.tag + "." + scope.tag;
			m_preferences.setToDefault(key);
		}
	}

	/***************************************************************************
	 * Get the color stored in the given preference name
	 * 
	 * @param preferenceName
	 **************************************************************************/
	public Color getColor(String preferenceName)
	{
		RGB rgb = PreferenceConverter.getColor(m_preferences, preferenceName);
		return new Color(Display.getDefault(), rgb);
	}

	/***************************************************************************
	 * Store the given color in the given preference key
	 * 
	 * @param preferenceName
	 * @param color
	 **************************************************************************/
	public void setColor(String preferenceName, RGB color)
	{
		PreferenceConverter.setValue(m_preferences, preferenceName, color);
	}

	/***************************************************************************
	 * Obtain predefined font
	 * 
	 * @return The font
	 **************************************************************************/
	public Font getFont(FontKey code)
	{
		FontData data = PreferenceConverter.getFontData(m_preferences,
		        code.getPreferenceName());
		return new Font(Display.getDefault(), data);
	}

	/***************************************************************************
	 * Obtain predefined font
	 * 
	 * @return The font
	 **************************************************************************/
	public Font getFont(FontKey code, int size)
	{
		FontData data = PreferenceConverter.getFontData(m_preferences,
		        code.getPreferenceName());
		data.height = (float) size;
		return new Font(Display.getDefault(), data);
	}

	/***************************************************************************
	 * Set a font for the given key preference
	 * 
	 * @param key
	 * @param font
	 **************************************************************************/
	public void setFont(FontKey key, Font font)
	{
		PreferenceConverter.setValue(m_preferences, key.getPreferenceName(),
		        font.getFontData());
	}

	/***************************************************************************
	 * Add a preference change listener
	 **************************************************************************/
	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		m_preferences.addPropertyChangeListener(listener);
	}

	/***************************************************************************
	 * Add a preference change listener
	 **************************************************************************/
	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		m_preferences.removePropertyChangeListener(listener);
	}
}
