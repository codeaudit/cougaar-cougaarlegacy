/**
 *  @file         DemoCommand.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created July 2, 2001.
 *  @todo
 **/

package com.centurylogix.finalPredictiveAssessor;

public class DemoCommand
{
  String command = null;

  public DemoCommand(String cmd)
  {
    this.command = cmd;
  }

  public String getCommand ()
  {
    return this.command;
  }
}