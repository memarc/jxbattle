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

package jxbattle.gui.base.common.gamesettingspanel;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.generic.gui.checkboxpanel.JCheckBoxPanel;

import net.miginfocom.swing.MigLayout;

public class GameSettingsPanelView extends JPanel
{
    private JPanel pnlBoard;

    private JLabel lblBoardSize;

    private JSpinner spinBoardx;

    private JSpinner spinBoardy;

    private final ButtonGroup buttonGroupVisibility = new ButtonGroup();

    private JPanel pnlInit;

    private JLabel lblArmies;

    private JSpinner spinArmies;

    private JPanel pnlMoving;

    private JLabel lblMove;

    private JSpinner spinTroopsMove;

    private JPanel pnlResupply;

    private JLabel lblArming;

    private JSpinner spinFarms;

    private JLabel lblFightIntensity;

    private JSpinner spinFight;

    private JLabel lblMilitia;

    private JSpinner spinMilitia;

    private JLabel lblSeaDensity;

    private JSpinner spinSeaDensity;

    private JLabel lblHillDensity;

    private JSpinner spinHillDensity;

    private JPanel pnlTerrainModification;

    private JCheckBox cbEnableBuildBase;

    private JLabel lblRandomBases;

    private JSpinner spinRandomBases;

    private JLabel lblBases;

    private JSpinner spinBases;

    private JLabel lblTowns;

    private JSpinner spinTowns;

    private JLabel lblRandomSeed;

    private JCheckBox cbGenerateRandom;

    private JTextField tfRandomSeed;

    private JCheckBox cbEnableFill;

    private JCheckBox cbEnableScuttleBase;

    private JCheckBox cbEnableDig;

    private JLabel lblBuildCost;

    private JLabel lblScuttleCost;

    private JLabel lblFillCost;

    private JLabel lblDigCost;

    private JSpinner spinBuildCost;

    private JSpinner spinScuttleCost;

    private JSpinner spinFillCost;

    private JSpinner spinDigCost;

    private JLabel lblBuildscuttleSteps;

    private JSpinner spinBuildSteps;

    private JLabel lblDecay;

    private JSpinner spinDecay;

    private JLabel lblHillSteepness;

    private JSpinner spinHillSteepness;

    private JCheckBox cbAllowKeyRepetition;

    private JPanel pnlVisibility;

    private JCheckBox cbRememberExploredCells;

    private JCheckBox cbHideEnemyVectors;

    private JRadioButton radioVisibilityEnemy;

    private JRadioButton radioVisibilityMap;

    private JSpinner spinVisibilityRange;

    private JLabel lblRange;

    private JCheckBox cbEnableAttack;

    private JRadioButton radioWrappingFull;

    private JRadioButton radioWrappingLeftRight;

    private JRadioButton radioWrappingTopDown;

    private final ButtonGroup buttonGroupWrapping = new ButtonGroup();

    //private JCheckBox cbDrawGrid;

    //private JCheckBox cbEnableParatroops;

    private JCheckBoxPanel pnlPara;

    private JCheckBoxPanel pnlWrapping;

    private JCheckBoxPanel pnlEnemyMap;

    private JLabel lblParaRange;

    private JLabel lblCost;

    private JLabel lblDamage;

    private JSpinner spinParaDamage;

    private JSpinner spinParaRange;

    private JSpinner spinParaCost;

    private JCheckBoxPanel pnlGun;

    private JLabel lblGunRange;

    private JLabel lblGunCost;

    private JLabel lblGunDamage;

    private JSpinner spinGunRange;

    private JSpinner spinGunCost;

    private JSpinner spinGunDamage;

    private JTabbedPane tabbedPane;

    private JPanel pnlGameplay;

    private JPanel pnlMap;

    private JCheckBox cbManagedCommands;

    private JCheckBoxPanel pnlMarch;

    private JLabel lblSpeed;

    private JSpinner spinMarchSpeed;

    public GameSettingsPanelView()
    {
        setLayout( new MigLayout( "", "[grow]", "[]" ) );
        add( getTabbedPane(), "cell 0 0,grow" );
    }

    JPanel getPnlBoard()
    {
        if ( pnlBoard == null )
        {
            pnlBoard = new JPanel();
            pnlBoard.setBorder( new TitledBorder( null, "Board", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
            pnlBoard.setLayout( new MigLayout( "", "[][60px:n][60px:n][]", "[][]" ) );
            pnlBoard.add( getLblBoardSize(), "cell 0 0" );
            pnlBoard.add( getSpinBoardx(), "cell 1 0,growx" );
            pnlBoard.add( getSpinBoardy(), "cell 2 0,growx" );
            //pnlBoard.add( getCbDrawGrid(), "cell 3 0,alignx right" );
            pnlBoard.add( getPnlWrapping(), "cell 0 1 4 1,grow" );
        }
        return pnlBoard;
    }

    private JLabel getLblBoardSize()
    {
        if ( lblBoardSize == null )
        {
            lblBoardSize = new JLabel( "Size" );
        }
        return lblBoardSize;
    }

    JSpinner getSpinBoardx()
    {
        if ( spinBoardx == null )
        {
            spinBoardx = new JSpinner();
            spinBoardx.setToolTipText( "Width of map (number of cells)" );
        }
        return spinBoardx;
    }

    JSpinner getSpinBoardy()
    {
        if ( spinBoardy == null )
        {
            spinBoardy = new JSpinner();
            spinBoardy.setToolTipText( "Height of map (number of cells)" );
        }
        return spinBoardy;
    }

    JPanel getPnlInit()
    {
        if ( pnlInit == null )
        {
            pnlInit = new JPanel();
            pnlInit.setBorder( new TitledBorder( new LineBorder( new Color( 184, 207, 229 ) ), "Map initialisation", TitledBorder.LEADING, TitledBorder.TOP, null, new Color( 51, 51, 51 ) ) );
            pnlInit.setLayout( new MigLayout( "", "[][150.00:n]", "[][][][][][][][][]" ) );
            pnlInit.add( getLblRandomSeed(), "cell 0 0" );
            pnlInit.add( getCbGenerateRandom(), "cell 1 0" );
            pnlInit.add( getTfRandomSeed(), "cell 1 1,growx" );
            pnlInit.add( getLblBases(), "cell 0 2" );
            pnlInit.add( getSpinBases(), "cell 1 2,growx" );
            pnlInit.add( getLblRandomBases(), "cell 0 3" );
            pnlInit.add( getSpinRandomBases(), "cell 1 3,growx" );
            pnlInit.add( getLblTowns(), "cell 0 4" );
            pnlInit.add( getSpinTowns(), "cell 1 4,growx" );
            pnlInit.add( getLblArmies(), "cell 0 5" );
            pnlInit.add( getSpinArmies(), "cell 1 5,growx" );
            pnlInit.add( getLblMilitia(), "cell 0 6" );
            pnlInit.add( getSpinMilitia(), "cell 1 6,growx" );
            pnlInit.add( getLblSeaDensity(), "cell 0 7" );
            pnlInit.add( getSpinSeaDensity(), "cell 1 7,growx" );
            pnlInit.add( getLblHillDensity(), "cell 0 8" );
            pnlInit.add( getSpinHillDensity(), "cell 1 8,growx" );
        }
        return pnlInit;
    }

    private JLabel getLblArmies()
    {
        if ( lblArmies == null )
        {
            lblArmies = new JLabel( "Armies" );
        }
        return lblArmies;
    }

    JSpinner getSpinArmies()
    {
        if ( spinArmies == null )
        {
            spinArmies = new JSpinner();
            spinArmies.setToolTipText( "Number of regularly placed full cells" );
        }
        return spinArmies;
    }

    JPanel getPnlMoving()
    {
        if ( pnlMoving == null )
        {
            pnlMoving = new JPanel();
            pnlMoving.setBorder( new TitledBorder( new LineBorder( new Color( 184, 207, 229 ) ), "Moving", TitledBorder.LEADING, TitledBorder.TOP, null, new Color( 51, 51, 51 ) ) );
            pnlMoving.setLayout( new MigLayout( "", "[][60px:n]", "[][][]" ) );
            pnlMoving.add( getLblMove(), "cell 0 0" );
            pnlMoving.add( getSpinTroopsMove(), "cell 1 0,growx" );
            pnlMoving.add( getLblFightIntensity(), "flowy,cell 0 1" );
            pnlMoving.add( getSpinFight(), "cell 1 1,growx" );
            pnlMoving.add( getLblHillSteepness(), "cell 0 2" );
            pnlMoving.add( getSpinHillSteepness(), "cell 1 2,growx" );
        }
        return pnlMoving;
    }

    private JLabel getLblMove()
    {
        if ( lblMove == null )
        {
            lblMove = new JLabel( "Move speed" );
        }
        return lblMove;
    }

    JSpinner getSpinTroopsMove()
    {
        if ( spinTroopsMove == null )
        {
            spinTroopsMove = new JSpinner();
            spinTroopsMove.setToolTipText( "<html>Portion of troops moving from a cell to another at each update cycle.<br/>low : slow moves, high : fast moves</html>" );
        }
        return spinTroopsMove;
    }

    JPanel getPnlResupply()
    {
        if ( pnlResupply == null )
        {
            pnlResupply = new JPanel();
            pnlResupply.setBorder( new TitledBorder( null, "Resupply", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
            pnlResupply.setLayout( new MigLayout( "", "[][60px:n]", "[][]" ) );
            pnlResupply.add( getLblFarming(), "cell 0 0" );
            pnlResupply.add( getSpinFarms(), "cell 1 0,growx" );
            pnlResupply.add( getLblDecay(), "cell 0 1" );
            pnlResupply.add( getSpinDecay(), "cell 1 1,growx" );
        }
        return pnlResupply;
    }

    private JLabel getLblFarming()
    {
        if ( lblArming == null )
        {
            lblArming = new JLabel( "Farms" );
        }
        return lblArming;
    }

    JSpinner getSpinFarms()
    {
        if ( spinFarms == null )
        {
            spinFarms = new JSpinner();
            spinFarms.setToolTipText( "<html>Probability of troops creation by a occupied cell (in case there is no base on it).<br/>0 : no farming<br/>10 : high probability</html>" );
        }
        return spinFarms;
    }

    private JLabel getLblDecay()
    {
        if ( lblDecay == null )
        {
            lblDecay = new JLabel( "Decay" );
        }
        return lblDecay;
    }

    JSpinner getSpinDecay()
    {
        if ( spinDecay == null )
        {
            spinDecay = new JSpinner();
            spinDecay.setToolTipText( "<html>Troops decay probability.<br/>0 : no decay<br/>10 : high probability</html>" );
        }
        return spinDecay;
    }

    private JLabel getLblFightIntensity()
    {
        if ( lblFightIntensity == null )
        {
            lblFightIntensity = new JLabel( "Fight intensity" );
        }
        return lblFightIntensity;
    }

    JSpinner getSpinFight()
    {
        if ( spinFight == null )
        {
            spinFight = new JSpinner();
            spinFight.setToolTipText( "<html>Intensity of fights.<br/>low : long fight, advantage to defense strategy<br/>high : quick fight, advantage to attack</html>" );
        }
        return spinFight;
    }

    private JLabel getLblMilitia()
    {
        if ( lblMilitia == null )
        {
            lblMilitia = new JLabel( "Militia" );
        }
        return lblMilitia;
    }

    JSpinner getSpinMilitia()
    {
        if ( spinMilitia == null )
        {
            spinMilitia = new JSpinner();
            spinMilitia.setToolTipText( "<html>Density of randomly placed, partially filled cells.<br/>0 : low density<br/>10 : high density</html>" );
        }
        return spinMilitia;
    }

    private JLabel getLblSeaDensity()
    {
        if ( lblSeaDensity == null )
        {
            lblSeaDensity = new JLabel( "Sea density" );
        }
        return lblSeaDensity;
    }

    JSpinner getSpinSeaDensity()
    {
        if ( spinSeaDensity == null )
        {
            spinSeaDensity = new JSpinner();
            spinSeaDensity.setToolTipText( "<html>Density of sea cells.<br/>0 : no sea<br/>10 : high density</html>" );
        }
        return spinSeaDensity;
    }

    private JLabel getLblHillDensity()
    {
        if ( lblHillDensity == null )
        {
            lblHillDensity = new JLabel( "Hill density" );
        }
        return lblHillDensity;
    }

    JSpinner getSpinHillDensity()
    {
        if ( spinHillDensity == null )
        {
            spinHillDensity = new JSpinner();
            spinHillDensity.setToolTipText( "<html>Density of hill cells.<br/>0 : no hill<br/>10 : high density</html>" );
        }
        return spinHillDensity;
    }

    JPanel getPnlTerrainModification()
    {
        if ( pnlTerrainModification == null )
        {
            pnlTerrainModification = new JPanel();
            pnlTerrainModification.setBorder( new TitledBorder( null, "Terrain modification", TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
            pnlTerrainModification.setLayout( new MigLayout( "", "[][grow][grow]", "[][][][][]" ) );
            pnlTerrainModification.add( getCbEnableBuildBase(), "cell 0 0,alignx left" );
            pnlTerrainModification.add( getLblBuildCost(), "cell 1 0,alignx right" );
            pnlTerrainModification.add( getSpinBaseBuildCost(), "cell 2 0,growx" );
            pnlTerrainModification.add( getCbEnableScuttleBase(), "cell 0 1,alignx left" );
            pnlTerrainModification.add( getLblScuttleCost(), "cell 1 1,alignx right" );
            pnlTerrainModification.add( getSpinBaseScuttleCost(), "cell 2 1,growx" );
            pnlTerrainModification.add( getLblBuildscuttleSteps(), "cell 1 2,alignx right" );
            pnlTerrainModification.add( getSpinBuildSteps(), "cell 2 2,growx" );
            pnlTerrainModification.add( getCbEnableFill(), "cell 0 3,alignx left" );
            pnlTerrainModification.add( getLblFillCost(), "cell 1 3,alignx right" );
            pnlTerrainModification.add( getSpinFillCost(), "cell 2 3,growx" );
            pnlTerrainModification.add( getCbEnableDig(), "cell 0 4,alignx left" );
            pnlTerrainModification.add( getLblDigCost(), "cell 1 4,alignx right" );
            pnlTerrainModification.add( getSpinDigCost(), "cell 2 4,growx" );
        }
        return pnlTerrainModification;
    }

    JCheckBox getCbEnableBuildBase()
    {
        if ( cbEnableBuildBase == null )
        {
            cbEnableBuildBase = new JCheckBox( "Base building" );
            cbEnableBuildBase.setToolTipText( "Enables base building." );
        }
        return cbEnableBuildBase;
    }

    private JLabel getLblRandomBases()
    {
        if ( lblRandomBases == null )
        {
            lblRandomBases = new JLabel( "Random bases" );
        }
        return lblRandomBases;
    }

    JSpinner getSpinRandomBases()
    {
        if ( spinRandomBases == null )
        {
            spinRandomBases = new JSpinner();
            spinRandomBases.setToolTipText( "Number of randomly distributed bases" );
        }
        return spinRandomBases;
    }

    private JLabel getLblBases()
    {
        if ( lblBases == null )
        {
            lblBases = new JLabel( "Bases" );
        }
        return lblBases;
    }

    JSpinner getSpinBases()
    {
        if ( spinBases == null )
        {
            spinBases = new JSpinner();
            spinBases.setToolTipText( "Regularly placed base count" );
        }
        return spinBases;
    }

    private JLabel getLblTowns()
    {
        if ( lblTowns == null )
        {
            lblTowns = new JLabel( "Towns" );
        }
        return lblTowns;
    }

    JSpinner getSpinTowns()
    {
        if ( spinTowns == null )
        {
            spinTowns = new JSpinner();
            spinTowns.setToolTipText( "<html>Density of randomly placed, variable size bases.<br/>\n0 : low density<br/>\n10 : high density</html>" );
        }
        return spinTowns;
    }

    private JLabel getLblRandomSeed()
    {
        if ( lblRandomSeed == null )
        {
            lblRandomSeed = new JLabel( "Random seed" );
        }
        return lblRandomSeed;
    }

    public JCheckBox getCbGenerateRandom()
    {
        if ( cbGenerateRandom == null )
        {
            cbGenerateRandom = new JCheckBox( "Generate" );
            cbGenerateRandom.setToolTipText( "Enable random seed automatic choice" );
        }
        return cbGenerateRandom;
    }

    JTextField getTfRandomSeed()
    {
        if ( tfRandomSeed == null )
        {
            tfRandomSeed = new JTextField();
            tfRandomSeed.setToolTipText( "Random seed value" );
        }
        return tfRandomSeed;
    }

    JCheckBox getCbEnableFill()
    {
        if ( cbEnableFill == null )
        {
            cbEnableFill = new JCheckBox( "Fill ground" );
            cbEnableFill.setToolTipText( "Enables raising ground/filling sea." );
        }
        return cbEnableFill;
    }

    JCheckBox getCbEnableScuttleBase()
    {
        if ( cbEnableScuttleBase == null )
        {
            cbEnableScuttleBase = new JCheckBox( "Base scuttling" );
            cbEnableScuttleBase.setToolTipText( "Enables destroying a base." );
        }
        return cbEnableScuttleBase;
    }

    JCheckBox getCbEnableDig()
    {
        if ( cbEnableDig == null )
        {
            cbEnableDig = new JCheckBox( "Dig ground" );
            cbEnableDig.setToolTipText( "Enables digging the ground/sea." );
        }
        return cbEnableDig;
    }

    JLabel getLblBuildCost()
    {
        if ( lblBuildCost == null )
        {
            lblBuildCost = new JLabel( "Cost" );
        }
        return lblBuildCost;
    }

    JLabel getLblScuttleCost()
    {
        if ( lblScuttleCost == null )
        {
            lblScuttleCost = new JLabel( "Cost" );
        }
        return lblScuttleCost;
    }

    JLabel getLblFillCost()
    {
        if ( lblFillCost == null )
        {
            lblFillCost = new JLabel( "Cost" );
        }
        return lblFillCost;
    }

    JLabel getLblDigCost()
    {
        if ( lblDigCost == null )
        {
            lblDigCost = new JLabel( "Cost" );
        }
        return lblDigCost;
    }

    JSpinner getSpinBaseBuildCost()
    {
        if ( spinBuildCost == null )
        {
            spinBuildCost = new JSpinner();
            spinBuildCost.setToolTipText( "<html>Number of troops used to build a base (one step at a time)<br/>0 : no troops used<br/>10 : all troops in the cell is used</html>" );
        }
        return spinBuildCost;
    }

    JSpinner getSpinBaseScuttleCost()
    {
        if ( spinScuttleCost == null )
        {
            spinScuttleCost = new JSpinner();
            spinScuttleCost.setToolTipText( "Number of troops used to destroy a base (one step at a time)" );
        }
        return spinScuttleCost;
    }

    JSpinner getSpinFillCost()
    {
        if ( spinFillCost == null )
        {
            spinFillCost = new JSpinner();
            spinFillCost.setToolTipText( "<html>Number of troops used to fill a cell.<br/>0 : no troops used<br/>10 : all troops in the cell is used</html>" );
        }
        return spinFillCost;
    }

    JSpinner getSpinDigCost()
    {
        if ( spinDigCost == null )
        {
            spinDigCost = new JSpinner();
            spinDigCost.setToolTipText( "<html>Number of troops used to dig a cell.<br/>0 : no troops used<br/>10 : all troops in the cell is used</html>" );
        }
        return spinDigCost;
    }

    JLabel getLblBuildscuttleSteps()
    {
        if ( lblBuildscuttleSteps == null )
        {
            lblBuildscuttleSteps = new JLabel( "Steps" );
        }
        return lblBuildscuttleSteps;
    }

    JSpinner getSpinBuildSteps()
    {
        if ( spinBuildSteps == null )
        {
            spinBuildSteps = new JSpinner();
            spinBuildSteps.setToolTipText( "Number of steps to build/destroy a base" );
        }
        return spinBuildSteps;
    }

    private JLabel getLblHillSteepness()
    {
        if ( lblHillSteepness == null )
        {
            lblHillSteepness = new JLabel( "Hill steepness" );
        }
        return lblHillSteepness;
    }

    JSpinner getSpinHillSteepness()
    {
        if ( spinHillSteepness == null )
        {
            spinHillSteepness = new JSpinner();
            spinHillSteepness.setToolTipText( "How hard to climb hills are." );
        }
        return spinHillSteepness;
    }

    JCheckBox getCbAllowKeyRepeat()
    {
        if ( cbAllowKeyRepetition == null )
        {
            cbAllowKeyRepetition = new JCheckBox( "Key repetition" );
            cbAllowKeyRepetition.setToolTipText( "If disabled, any key must be released before repeting command" );
        }
        return cbAllowKeyRepetition;
    }

    JPanel getPnlVisibility()
    {
        if ( pnlVisibility == null )
        {
            pnlVisibility = new JPanel();
            pnlVisibility.setBorder( new TitledBorder( new LineBorder( new Color( 184, 207, 229 ) ), "Invisibility", TitledBorder.LEADING, TitledBorder.TOP, null, new Color( 51, 51, 51 ) ) );
            pnlVisibility.setLayout( new MigLayout( "", "[grow]", "[][]" ) );
            pnlVisibility.add( getCbHideEnemyVectors(), "cell 0 0" );
            pnlVisibility.add( getPnlEnemyMap(), "cell 0 1,grow" );
        }
        return pnlVisibility;
    }

    JCheckBox getCbRememberExploredCells()
    {
        if ( cbRememberExploredCells == null )
        {
            cbRememberExploredCells = new JCheckBox( "Remember cells" );
            cbRememberExploredCells.setToolTipText( "Remember already discovered cells. If disabled, revealed cells disappear if you move away." );
        }
        return cbRememberExploredCells;
    }

    JCheckBox getCbHideEnemyVectors()
    {
        if ( cbHideEnemyVectors == null )
        {
            cbHideEnemyVectors = new JCheckBox( "Hide vectors" );
            cbHideEnemyVectors.setToolTipText( "Hide enemy's move vectors" );
        }
        return cbHideEnemyVectors;
    }

    JRadioButton getRadioVisibilityEnemy()
    {
        if ( radioVisibilityEnemy == null )
        {
            radioVisibilityEnemy = new JRadioButton( "Enemy" );
            radioVisibilityEnemy.setToolTipText( "<html>Enemy is not visible outside a given range;<br/>ground is still visible all over the map</html>" );
            buttonGroupVisibility.add( radioVisibilityEnemy );
        }
        return radioVisibilityEnemy;
    }

    JRadioButton getRadioVisibilityMap()
    {
        if ( radioVisibilityMap == null )
        {
            radioVisibilityMap = new JRadioButton( "Map" );
            radioVisibilityMap.setToolTipText( "Nothing is visible outside a given range, either ground or enemy." );
            buttonGroupVisibility.add( radioVisibilityMap );
        }
        return radioVisibilityMap;
    }

    JSpinner getSpinVisibilityRange()
    {
        if ( spinVisibilityRange == null )
        {
            spinVisibilityRange = new JSpinner();
            spinVisibilityRange.setToolTipText( "Area size around a cell in which you can see map/enemy" );
        }
        return spinVisibilityRange;
    }

    private JLabel getLblRange()
    {
        if ( lblRange == null )
        {
            lblRange = new JLabel( "Range" );
        }
        return lblRange;
    }

    JCheckBox getCbEnableAttack()
    {
        if ( cbEnableAttack == null )
        {
            cbEnableAttack = new JCheckBox( "Surrounding attack" );
            cbEnableAttack.setToolTipText( "Enables a surrounded cell to be attacked at once (default 'a' key)" );
        }
        return cbEnableAttack;
    }

    JCheckBox getCbManagedCommands()
    {
        if ( cbManagedCommands == null )
        {
            cbManagedCommands = new JCheckBox( "Managed commands" );
        }
        return cbManagedCommands;
    }

    JRadioButton getRadioWrappingFull()
    {
        if ( radioWrappingFull == null )
        {
            radioWrappingFull = new JRadioButton( "Full" );
            buttonGroupWrapping.add( radioWrappingFull );
        }
        return radioWrappingFull;
    }

    JRadioButton getRadioWrappingLeftRight()
    {
        if ( radioWrappingLeftRight == null )
        {
            radioWrappingLeftRight = new JRadioButton( "Left right" );
            buttonGroupWrapping.add( radioWrappingLeftRight );
        }
        return radioWrappingLeftRight;
    }

    JRadioButton getRadioWrappingTopDown()
    {
        if ( radioWrappingTopDown == null )
        {
            radioWrappingTopDown = new JRadioButton( "Top down" );
            buttonGroupWrapping.add( radioWrappingTopDown );
        }
        return radioWrappingTopDown;
    }

    //    public JCheckBox getCbEnableParatroops()
    //    {
    //        if ( cbEnableParatroops == null )
    //        {
    //            cbEnableParatroops = new JCheckBox( "Enable paratroops" );
    //        }
    //        return cbEnableParatroops;
    //    }

    JCheckBoxPanel getPnlPara()
    {
        if ( pnlPara == null )
        {
            pnlPara = new JCheckBoxPanel( "Paratroops" );
            pnlPara.setCheckBoxTitle( "Para troops" );
            pnlPara.setLayout( new MigLayout( "", "[][60px:n]", "[][][]" ) );
            pnlPara.add( getLblParaRange(), "flowy,cell 0 0,alignx left" );
            pnlPara.add( getSpinParaRange(), "cell 1 0,growx" );
            pnlPara.add( getLblCost(), "cell 0 1,alignx left" );
            pnlPara.add( getSpinParaCost(), "cell 1 1,growx" );
            pnlPara.add( getLblDamage(), "cell 0 2,alignx left" );
            pnlPara.add( getSpinParaDamage(), "cell 1 2,growx" );
        }
        return pnlPara;
    }

    JCheckBoxPanel getPnlWrapping()
    {
        if ( pnlWrapping == null )
        {
            pnlWrapping = new JCheckBoxPanel( "Wrapping" );
            pnlWrapping.setLayout( new MigLayout( "", "[grow][grow][grow]", "[]" ) );
            pnlWrapping.add( getRadioWrappingLeftRight(), "cell 0 0" );
            pnlWrapping.add( getRadioWrappingTopDown(), "cell 1 0" );
            pnlWrapping.add( getRadioWrappingFull(), "cell 2 0" );
        }
        return pnlWrapping;
    }

    ButtonGroup getButtonGroupWrapping()
    {
        return buttonGroupWrapping;
    }

    JCheckBoxPanel getPnlEnemyMap()
    {
        if ( pnlEnemyMap == null )
        {
            pnlEnemyMap = new JCheckBoxPanel( (String)null );
            pnlEnemyMap.setCheckBoxTitle( "Enemy/Map" );
            pnlEnemyMap.setLayout( new MigLayout( "", "[grow][][]", "[][]" ) );
            pnlEnemyMap.add( getRadioVisibilityEnemy(), "cell 0 0" );
            pnlEnemyMap.add( getCbRememberExploredCells(), "cell 1 0 2 1,alignx left" );
            pnlEnemyMap.add( getRadioVisibilityMap(), "cell 0 1" );
            pnlEnemyMap.add( getLblRange(), "cell 1 1,growx" );
            pnlEnemyMap.add( getSpinVisibilityRange(), "cell 2 1,growx" );
        }
        return pnlEnemyMap;
    }

    ButtonGroup getButtonGroupVisibility()
    {
        return buttonGroupVisibility;
    }

    private JLabel getLblParaRange()
    {
        if ( lblParaRange == null )
        {
            lblParaRange = new JLabel( "Range" );
        }
        return lblParaRange;
    }

    private JLabel getLblCost()
    {
        if ( lblCost == null )
        {
            lblCost = new JLabel( "Cost" );
        }
        return lblCost;
    }

    private JLabel getLblDamage()
    {
        if ( lblDamage == null )
        {
            lblDamage = new JLabel( "Damage" );
        }
        return lblDamage;
    }

    JSpinner getSpinParaDamage()
    {
        if ( spinParaDamage == null )
        {
            spinParaDamage = new JSpinner();
            spinParaDamage.setToolTipText( "Damage caused to enemy by paratroops (ranges from 1 to 10)" );
        }
        return spinParaDamage;
    }

    JSpinner getSpinParaRange()
    {
        if ( spinParaRange == null )
        {
            spinParaRange = new JSpinner();
            spinParaRange.setToolTipText( "Paratroops range. Values from 1 (near) to 3 (far)" );
        }
        return spinParaRange;
    }

    JSpinner getSpinParaCost()
    {
        if ( spinParaCost == null )
        {
            spinParaCost = new JSpinner();
            spinParaCost.setToolTipText( "Cost of sending paratroops (ranges from 1 to 10)" );
        }
        return spinParaCost;
    }

    JCheckBoxPanel getPnlGun()
    {
        if ( pnlGun == null )
        {
            pnlGun = new JCheckBoxPanel( (String)null );
            pnlGun.setCheckBoxTitle( "Gun troops" );
            pnlGun.setLayout( new MigLayout( "", "[][60px:n]", "[][][]" ) );
            pnlGun.add( getLblGunRange(), "cell 0 0" );
            pnlGun.add( getSpinGunRange(), "cell 1 0,growx" );
            pnlGun.add( getLblGunCost(), "cell 0 1" );
            pnlGun.add( getSpinGunCost(), "cell 1 1,growx" );
            pnlGun.add( getLblGunDamage(), "cell 0 2" );
            pnlGun.add( getSpinGunDamage(), "cell 1 2,growx" );
        }
        return pnlGun;
    }

    private JLabel getLblGunRange()
    {
        if ( lblGunRange == null )
        {
            lblGunRange = new JLabel( "Range" );
        }
        return lblGunRange;
    }

    private JLabel getLblGunCost()
    {
        if ( lblGunCost == null )
        {
            lblGunCost = new JLabel( "Cost" );
        }
        return lblGunCost;
    }

    private JLabel getLblGunDamage()
    {
        if ( lblGunDamage == null )
        {
            lblGunDamage = new JLabel( "Damage" );
        }
        return lblGunDamage;
    }

    JSpinner getSpinGunRange()
    {
        if ( spinGunRange == null )
        {
            spinGunRange = new JSpinner();
            spinGunRange.setToolTipText( "Gun range. Values from 1 (near) to 3 (far)" );
        }
        return spinGunRange;
    }

    JSpinner getSpinGunCost()
    {
        if ( spinGunCost == null )
        {
            spinGunCost = new JSpinner();
            spinGunCost.setToolTipText( "Cost of firing gun (ranges from 1 to 10)" );
        }
        return spinGunCost;
    }

    JSpinner getSpinGunDamage()
    {
        if ( spinGunDamage == null )
        {
            spinGunDamage = new JSpinner();
            spinGunDamage.setToolTipText( "Damage caused to enemy by gun (ranges from 1 to 10)" );
        }
        return spinGunDamage;
    }

    private JTabbedPane getTabbedPane()
    {
        if ( tabbedPane == null )
        {
            tabbedPane = new JTabbedPane( SwingConstants.TOP );
            tabbedPane.addTab( "Map", null, getPnlMap(), null );
            tabbedPane.addTab( "Game play", null, getPnlGameplay(), null );
        }
        return tabbedPane;
    }

    JPanel getPnlGameplay()
    {
        if ( pnlGameplay == null )
        {
            pnlGameplay = new JPanel();
            pnlGameplay.setLayout( new MigLayout( "", "[276px,grow][grow]", "[123px][][][][][]" ) );
            pnlGameplay.add( getPnlMoving(), "cell 0 0,grow" );
            pnlGameplay.add( getPnlPara(), "cell 1 0,grow" );
            pnlGameplay.add( getPnlVisibility(), "cell 0 1 1 2,grow" );
            pnlGameplay.add( getPnlMarch(), "cell 1 2,grow" );
            pnlGameplay.add( getPnlResupply(), "cell 0 3 1 3,grow" );
            pnlGameplay.add( getPnlGun(), "cell 1 1,grow" );
            pnlGameplay.add( getCbEnableAttack(), "cell 1 3,aligny top" );
            pnlGameplay.add( getCbAllowKeyRepeat(), "cell 1 4,aligny top" );
            pnlGameplay.add( getCbManagedCommands(), "cell 1 5" );
        }
        return pnlGameplay;
    }

    JPanel getPnlMap()
    {
        if ( pnlMap == null )
        {
            pnlMap = new JPanel();
            pnlMap.setLayout( new MigLayout( "", "[grow][grow]", "[][]" ) );
            pnlMap.add( getPnlBoard(), "cell 0 0,grow" );
            pnlMap.add( getPnlInit(), "cell 1 0 1 2,grow" );
            pnlMap.add( getPnlTerrainModification(), "cell 0 1,grow" );
        }
        return pnlMap;
    }

    JCheckBoxPanel getPnlMarch()
    {
        if ( pnlMarch == null )
        {
            pnlMarch = new JCheckBoxPanel( (String)null );
            pnlMarch.setCheckBoxTitle( "Marching" );
            pnlMarch.setLayout( new MigLayout( "", "[60px:n][60px:n]", "[]" ) );
            pnlMarch.add( getLblSpeed(), "cell 0 0" );
            pnlMarch.add( getSpinMarchSpeed(), "cell 1 0,growx" );
        }
        return pnlMarch;
    }

    private JLabel getLblSpeed()
    {
        if ( lblSpeed == null )
        {
            lblSpeed = new JLabel( "Speed" );
        }
        return lblSpeed;
    }

    JSpinner getSpinMarchSpeed()
    {
        if ( spinMarchSpeed == null )
        {
            spinMarchSpeed = new JSpinner();
            spinMarchSpeed.setToolTipText( "March update speed (1 : slow, 10 : fast)" );
        }
        return spinMarchSpeed;
    }
}
