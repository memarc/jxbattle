/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jxbattle.model.server.profilesettings;

import java.util.Properties;

/**
 * mother class for settings in profiles
 */
public abstract class ProfileSetting
{
    /**
     * setting name
     */
    private String settingName;

    protected Properties properties;

    public ProfileSetting( Properties props )
    {
        properties = props;
    }

    public String getSettingName()
    {
        return settingName;
    }

    public void setSettingName( String name )
    {
        settingName = name;
    }

    /**
     * get matching property name
     * @param profileNumber number of the profile in properties file
     */
    protected String getPropertyName( int profileNumber )
    {
        return "profile" + profileNumber + "." + settingName;
    }

    /**
     * write setting value to Properties instance
     */
    public abstract void toProperties( String propertyName );

    public void toProperties( int profileNumber )
    {
        toProperties( getPropertyName( profileNumber ) );
    }

    /**
     * write setting default value to Properties instance
     */
    public abstract void toPropertiesDefault( String propertyName );

    public void toPropertiesDefault( int profileNumber )
    {
        toPropertiesDefault( getPropertyName( profileNumber ) );
    }

    /**
     * get setting value from Properties instance
     */
    public abstract void fromProperties( String propertyName );

    public void fromProperties( int profileNumber )
    {
        fromProperties( getPropertyName( profileNumber ) );
    }
}
