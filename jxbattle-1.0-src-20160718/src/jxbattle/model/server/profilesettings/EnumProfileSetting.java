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

import org.generic.EnumValue;
import org.generic.bean.parameter.EnumParameter;

/**
 * integer profile setting in Properties file 
 */
public class EnumProfileSetting<T extends EnumValue> extends ProfileSetting
{
    /**
     * enum value of the setting
     */
    private EnumParameter<T> param;

    public EnumProfileSetting( Properties props, EnumParameter<T> ep, String settingName )
    {
        super( props );
        param = ep;
        setSettingName( settingName );
    }

    //    @Override
    //    public void toProperties( Properties props, int profileNumber )
    //    {
    //        props.setProperty( getPropertyName( profileNumber ), String.valueOf( param.getValue() ) );
    //    }

    @Override
    public void toProperties( String propertyName )
    {
        //        props.setProperty( getPropertyName( profileNumber ), String.valueOf( param.getValue() ) );
        properties.setProperty( propertyName, String.valueOf( param.getValue() ) );
    }

    @Override
    public void toPropertiesDefault( String propertyName )
    {
        properties.setProperty( propertyName, String.valueOf( param.getDefaultValue() ) );
    }

    @Override
    //    public void fromProperties( Properties props, int profileNumber )
    public void fromProperties( String propertyName )
    {
        //        String propValue = props.getProperty( getPropertyName( profileNumber ) );
        String propValue = properties.getProperty( propertyName );

        if ( propValue == null )
            param.resetToDefault();
        else
            try
            {
                param.setValue( param.getValueOf( propValue ) );
            }
            catch( NumberFormatException e )
            {
                param.resetToDefault();
            }
    }
}
