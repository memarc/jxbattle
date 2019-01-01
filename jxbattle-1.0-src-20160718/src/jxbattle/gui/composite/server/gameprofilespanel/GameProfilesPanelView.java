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

package jxbattle.gui.composite.server.gameprofilespanel;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelView;
import jxbattle.model.server.GameParametersProfileModel;
import net.miginfocom.swing.MigLayout;

public class GameProfilesPanelView extends JPanel
{
    private JButton btnResetDefaults;

    private JPanel pnlProfiles;

    private JComboBox<GameParametersProfileModel> cmbProfiles;

    private JButton btnCopyProfile;

    private JButton btnDeleteProfile;

    private GameSettingsPanelView gameSettingsPanelView;

    private JLabel lblDuplicateName;

    private JLabel lblName;

    private JTextField tfProfileName;

    public GameProfilesPanelView()
    {
        setLayout( new MigLayout( "", "[grow][]", "[][]" ) );
        add( getGameSettingsPanelView(), "cell 0 0 2 1,grow" );
        add( getBtnResetDefaults(), "cell 0 1,alignx center,aligny baseline" );
        add( getPnlProfiles(), "cell 1 1,grow" );
    }

    public JButton getBtnResetDefaults()
    {
        if ( btnResetDefaults == null )
        {
            btnResetDefaults = new JButton( "Reset to defaults" );
        }
        return btnResetDefaults;
    }

    public JPanel getPnlProfiles()
    {
        if ( pnlProfiles == null )
        {
            pnlProfiles = new JPanel();
            pnlProfiles.setBorder( new TitledBorder( null, "Profiles", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
            pnlProfiles.setLayout( new MigLayout( "", "[][][grow][][]", "[][]" ) );
            pnlProfiles.add( getCmbProfiles(), "cell 0 0 4 1,growx" );
            pnlProfiles.add( getBtnCopyProfile(), "cell 4 0,growx" );
            pnlProfiles.add( getLblName(), "cell 1 1,alignx trailing" );
            pnlProfiles.add( getTfProfileName(), "cell 2 1,growx" );
            pnlProfiles.add( getLblDuplicateName(), "cell 3 1" );
            pnlProfiles.add( getBtnDeleteProfile(), "cell 4 1,growx" );
        }
        return pnlProfiles;
    }

    public JComboBox<GameParametersProfileModel> getCmbProfiles()
    {
        if ( cmbProfiles == null )
        {
            cmbProfiles = new JComboBox<>();
        }
        return cmbProfiles;
    }

    public JButton getBtnCopyProfile()
    {
        if ( btnCopyProfile == null )
        {
            btnCopyProfile = new JButton( "Copy profile" );
        }
        return btnCopyProfile;
    }

    public JButton getBtnDeleteProfile()
    {
        if ( btnDeleteProfile == null )
        {
            btnDeleteProfile = new JButton( "Delete profile" );
        }
        return btnDeleteProfile;
    }

    public GameSettingsPanelView getGameSettingsPanelView()
    {
        if ( gameSettingsPanelView == null )
        {
            gameSettingsPanelView = new GameSettingsPanelView();
        }
        return gameSettingsPanelView;
    }

    public JLabel getLblDuplicateName()
    {
        if ( lblDuplicateName == null )
        {
            lblDuplicateName = new JLabel( "Duplicate name" );
            lblDuplicateName.setForeground( Color.RED );
        }
        return lblDuplicateName;
    }

    public JLabel getLblName()
    {
        if ( lblName == null )
        {
            lblName = new JLabel( "Name" );
        }
        return lblName;
    }

    public JTextField getTfProfileName()
    {
        if ( tfProfileName == null )
        {
            tfProfileName = new JTextField();
            tfProfileName.setColumns( 10 );
        }
        return tfProfileName;
    }
}
