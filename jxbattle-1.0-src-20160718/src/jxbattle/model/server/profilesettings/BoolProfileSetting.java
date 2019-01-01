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

import org.generic.bean.parameter.BoolParameter;

/**
 * boolean profile setting in Properties file 
 */
public class BoolProfileSetting extends ProfileSetting
{
    /**
     * boolean value of the setting
     */
    private BoolParameter param;

    public BoolProfileSetting( Properties props, BoolParameter bp, String settingName )
    {
        super( props );
        param = bp;
        setSettingName( settingName );
    }

    //    @Override
    //    public void toProperties( Properties props, int profileNumber )
    //    {
    //        props.setProperty( getPropertyName( profileNumber ), param.getValue() ? "true" : "false" );
    //    }

    @Override
    public void toProperties( String propertyName )
    {
        properties.setProperty( propertyName, param.getValue() ? "true" : "false" );
    }

    @Override
    public void toPropertiesDefault( String propertyName )
    {
        properties.setProperty( propertyName, param.getDefaultValue() ? "true" : "false" );
    }

    @Override
    public void fromProperties( String propertyName )
    {
        String propValue = properties.getProperty( propertyName );

        if ( propValue == null )
            param.resetToDefault();
        else
            param.setValue( propValue.equals( "true" ) );
    }
}
