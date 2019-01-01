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

package jxbattle.common;

import java.awt.Color;

import jxbattle.bean.common.player.XBColor;

public class Consts
{
    public static final int NullId = -1; // player id of a non occupied cell, unset color id  

    private static final XBColor blue = new XBColor( "blue", 0, 0, 175, 255, 255, 255 );

    private static final XBColor red = new XBColor( "red", 255, 0, 0 );

    private static final XBColor green = new XBColor( "green", 130, 255, 0 );

    private static final XBColor magenta = new XBColor( "magenta", 255, 0, 255 );

    private static final XBColor yellow = new XBColor( "yellow", 250, 250, 130 );

    public static final XBColor white = new XBColor( "white", 255, 255, 255 );

    private static final XBColor black = new XBColor( "black", 0, 0, 0, 255, 255, 255 );

    private static final XBColor cyan = new XBColor( "cyan", 100, 255, 210 );

    private static final XBColor gator = new XBColor( "gator", 50, 200, 100, 255, 255, 255 );

    public static final XBColor[] playerColors = new XBColor[] { blue, red, green, magenta, yellow, white, black, cyan, gator };

    public static final XBColor seaMin = new XBColor( null, 114, 188, 255 );

    public static final XBColor seaMax = new XBColor( null, 20, 44, 126 );

    public static final XBColor hillMin = new XBColor( null, 175, 220, 100 );

    public static final XBColor hillMax = new XBColor( null, 171, 85, 30 );

    public static final Color gridColor = Color.BLACK; // color of the grid (between cells)

    public static final Color invisibleCell = Color.LIGHT_GRAY; // color of non visible cell (everything in cell)

    public static final int emitFlushInterval = 200; // net connection flush interval (emiting only) 

    public static final int maxElevation = 7; //10; // max height/depth of sea/hill

    public static final float maxTroopsLevel = 100.0f; // max troops level in a cell

    public static final float minTroopsMove = 0.02f;

    public static final float maxTroopsMove = 0.6f;

    public static final float minFightIntensity = 1.0f;

    public static final float maxFightIntensity = 1000.0f;

    public static final float minHillSteepness = 1.0f;

    public static final float maxHillSteepness = 15.0f;

    public static final int minTownSize = 50;

    public static final int maxTownSize = 100;

    public static final int defaultTownBuildSteps = 8;

    public static final int maxTownBuildSteps = 20;

    public static final int maxBaseCount = 5;

    public static final int maxRandomBaseCount = 10;

    public static final int maxArmiesCount = 5;

    public static final int defaultCellSize = 35;

    public static final int militiaCorrection = 3; // correction factor for militia generation

    public static final int townsCorrection = 3; // correction factor for towns generation

    public static final double halfVectorTroopsLevel = maxTroopsLevel / 100.0; // troops level under which a half move vector is drawn

    public static final int defaultServerPort = 10000;

    public static final int defaultSocketTimeout = 3000;

    public static final String webSiteUrl = "http://jxbattle.infos.st/";

    public static final String applicationVersion = "1.0";

    public static final int maxMarchUpdateSpeed = 20;

    public static int maxCellSideCount = 4;
}
