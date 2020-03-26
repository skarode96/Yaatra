package com.tcd.yaatra.utils.offlinemaps;

import com.graphhopper.util.Instruction;
import com.tcd.yaatra.R;

public class NaviInstruction
{
  String curStreet;
  String nextInstruction;
  String nextInstructionShort;
  long fullTime;
  String fullTimeString;
  double nextDistance;
  int nextSign;
  int nextSignResource;
  
  public NaviInstruction(Instruction in, Instruction nextIn, long fullTime)
  {
    if (nextIn != null)
    {
      nextSign = nextIn.getSign();
      nextSignResource = Navigator.getNavigator().getDirectionSignHuge(nextIn);
      nextInstruction = Navigator.getNavigator().getDirectionDescription(nextIn, true);
      nextInstructionShort = Navigator.getNavigator().getDirectionDescription(nextIn, false);
    }
    else
    {
      nextSign = in.getSign(); // Finished?
      nextSignResource = Navigator.getNavigator().getDirectionSignHuge(in);
      nextInstruction = Navigator.getNavigator().getDirectionDescription(in, true);
      nextInstructionShort = Navigator.getNavigator().getDirectionDescription(in, false);
    }
    if (nextSignResource == 0) { nextSignResource = R.drawable.ic_exit_arrow_right; }
    nextDistance = in.getDistance();
    this.fullTime = fullTime;
    fullTimeString = Navigator.getNavigator().getTimeString(fullTime);
    curStreet = in.getName();
    if (curStreet == null) { curStreet = ""; }
  }
  
  
  public long getFullTime() { return fullTime; }
  public double getNextDistance() { return nextDistance; }
  public String getFullTimeString() { return fullTimeString; }
  public int getNextSignResource() { return nextSignResource; }
  public int getNextSign() { return nextSign; }
  public String getCurStreet() { return curStreet; }
  public String getNextInstruction() { return nextInstruction; }
  public String getNextDistanceString()
  {
    if (nextDistance > (UnitCalculator.METERS_OF_KM * 10.0))
    {
      String unit = " " + UnitCalculator.getUnit(true);
      return UnitCalculator.getBigDistance(nextDistance, 0) + unit;
    }
    String unit = " " + UnitCalculator.getUnit(false);
    return UnitCalculator.getShortDistance(nextDistance) + unit;
  }


  public void updateDist(double partDistance)
  {
    nextDistance = partDistance;
  }


  public String getVoiceText()
  {
    String unit = " meters. ";
    int roundetDistance = (int)nextDistance;
    roundetDistance = roundetDistance/10;
    roundetDistance = roundetDistance * 10;
    return "In " + roundetDistance + unit + nextInstructionShort;
  }
}
