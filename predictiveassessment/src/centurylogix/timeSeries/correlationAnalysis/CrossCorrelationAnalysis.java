/**
 *  @file         CrossCorrelationAnalysis.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This is the description.
 *  @history      Created June 19, 2001.
 *  @todo
 *
 **/

package com.centurylogix.timeSeries.correlationAnalysis;

import com.centurylogix.ultralog.DebugFrame;
import com.centurylogix.timeSeries.*;

public class CrossCorrelationAnalysis
{
  private double threshold = .75;
  private int maxTimeLag = 25;      // this is the max delay that will be allowed in search for
                                      // optimal time lag that results in best correlation.

  private int optimalTimeLag = 0;
  private double optimalCorrelation = 0;
  private TimeSeries correlationTS = null;

  public CrossCorrelationAnalysis()
  {
  }

  public CrossCorrelationAnalysis(double newThreshold, int newTimeLag)
  {
    this.threshold = newThreshold;
    this.maxTimeLag = newTimeLag;
  }

  public void setThreshold (double newThreshold)
  {
    this.threshold = newThreshold;
  }

  public void setMaxTimeLag (int newTimeLag)
  {
    this.maxTimeLag = newTimeLag;
  }

  // methods to get r-squared && one to get time lage && one to get name of dependent time series.
  public int getOptimalTimeLag ()
  {
    return this.optimalTimeLag;
  }

  public double getCorrelation ()
  {
    return this.optimalCorrelation;
  }

  public TimeSeries getCorrelationTimeSeries ()
  {
    return this.correlationTS;
  }

  // requires the two time series to be of the same size, have the same number of elements
  public boolean correlationExists (TimeSeries tsOne, TimeSeries tsTwo)
  {
    long startTime = System.currentTimeMillis();

    if (tsOne.size() != tsTwo.size())
      return false;

    boolean ret = false;

    int bestLag = 0;
    double bestCorrelation = 0;

    try
    {
      for (int lag = -this.maxTimeLag; lag <= this.maxTimeLag; lag++)
      {
    //    System.out.println ("\n Looking for correlation when lag is : " + lag);

        double numerator = 0;       // this is the summed numerator value of the equation
        double oneSquaresSum = 0;
        double twoSquaresSum = 0;
        int valuesCompared = 0;
        double oneSum = 0;
        double twoSum = 0;
        double oneMean = 0;
        double twoMean = 0;

        if (lag < 0)
        {
          int count = 0;
          for (int i = -lag; i < tsOne.size(); i++)
          {
            valuesCompared++;

            Number twoNum = (Number) (tsTwo.getValueAt(count).getValue());
            Number oneNum = (Number) (tsOne.getValueAt(i).getValue());

            double twoVal = twoNum.doubleValue();
            double oneVal = oneNum.doubleValue();

            twoSum = twoSum + twoVal;
            oneSum = oneSum + oneVal;

            count++;
          }


          if (valuesCompared == 0 || oneSum == 0)
            oneMean = 0;
          else
            oneMean = oneSum / valuesCompared;

          if (valuesCompared == 0 || twoSum == 0)
            twoMean = 0;
          else
            twoMean = twoSum / valuesCompared;

          // incrementally compare tsTwo vs. version of tsOne shifted back in time
          count = 0;
          for (int i = -lag; i < tsOne.size(); i++)
          {
            Number twoNum = (Number) (tsTwo.getValueAt(count).getValue());
            Number oneNum = (Number) (tsOne.getValueAt(i).getValue());

            double twoVal = twoNum.doubleValue();
            double oneVal = oneNum.doubleValue();

            numerator = numerator + ((twoVal - twoMean) * (oneVal-oneMean));

            oneSquaresSum += ((oneVal - oneMean) * (oneVal - oneMean));
            twoSquaresSum += ((twoVal - twoMean) * (twoVal - twoMean));

            count++;
          }
        }// end if (lag < 0)

        else
        {
          int count = 0;
          // incrementally compares tsTwo versus versions of tsOne that are shifted to the right

          for (int j = lag; j < tsOne.size(); j++)
          {
            valuesCompared++;

            Number twoNum = (Number) (tsTwo.getValueAt(j).getValue());
            Number oneNum = (Number) (tsOne.getValueAt(count).getValue());

            double twoVal = twoNum.doubleValue();
            double oneVal = oneNum.doubleValue();

            twoSum = twoSum + twoVal;
            oneSum = oneSum + oneVal;

            count++;
          }

          if (valuesCompared == 0 || oneSum == 0)
            oneMean = 0;
          else
            oneMean = oneSum / valuesCompared;

          if (valuesCompared == 0 || twoSum == 0)
            twoMean = 0;
          else
            twoMean = twoSum / valuesCompared;

          count = 0;

          for (int j = lag; j < tsOne.size(); j++)
          {
            Number twoNum = (Number) (tsTwo.getValueAt(j).getValue());
            Number oneNum = (Number) (tsOne.getValueAt(count).getValue());

            double twoVal = twoNum.doubleValue();
            double oneVal = oneNum.doubleValue();

            numerator = numerator + ((twoVal - twoMean) * (oneVal-oneMean));

            oneSquaresSum += ((oneVal - oneMean) * (oneVal - oneMean));
            twoSquaresSum += ((twoVal - twoMean) * (twoVal - twoMean));

            count++;
          }
        } // end else

      //  System.out.println("one Mean : " + oneMean + " two Mean : " + twoMean );

        double oneSquaresRoot = Math.sqrt (oneSquaresSum);
        double twoSquaresRoot = Math.sqrt (twoSquaresSum);
        double correlation;

        if (numerator == 0 || oneSquaresRoot == 0 || twoSquaresRoot == 0)
          correlation = 0;
        else
          correlation = numerator / (oneSquaresRoot * twoSquaresRoot);

        //System.out.println ("Values compared : " + valuesCompared);
        //System.out.println ("Correlation % : " + (correlation * correlation));

        TimeSeriesValue corrTSV = new TimeSeriesValue ("Correlation",
                                   new Double (correlation * correlation),lag + maxTimeLag, 1);

        if (correlationTS == null)
          correlationTS = new TimeSeries (corrTSV);
        else
          correlationTS.addElement (corrTSV);


        if (Math.abs (correlation) > Math.abs (bestCorrelation) )
        {
          bestCorrelation = correlation;
          bestLag = lag;
        }

      } // end for (lag)

    } //end try
    catch (Exception e)
    {
      System.out.println ("Exception caught while looking for correlation: " + e.getMessage());
      ret = false;
    }

    this.optimalTimeLag = bestLag;
    this.optimalCorrelation = bestCorrelation * bestCorrelation;

    System.out.println ("optimal correlation is : " + this.optimalCorrelation);
    System.out.println ("optimal time lag is : " + this.optimalTimeLag);
    if (this.optimalCorrelation >= threshold)
      ret = true;

    long endTime = System.currentTimeMillis();
    long totalTime = (endTime - startTime) / 1000;

    TimeSeriesUtilities.replaceNulls(correlationTS);

    System.out.println ("Total calc time in seconds : " + totalTime);

    return ret;
  }// end public boolean correlationExists (TimeSeries, TimeSeries)



  public static void main(String[] args)
  {
    DebugFrame df = new DebugFrame ();
    df.show();
    df.setTitle ("Cross-Correlation Analysis BlackBox testing");

    CrossCorrelationAnalysis cca = new CrossCorrelationAnalysis(.05, 7);

    /******************* testing of correlationExists () *****************************/

    /////////// testing a positively correlated, identical pair of time series.
    TimeSeriesValue tsv0_one = new TimeSeriesValue ("Test1", new Double (0), 0, 5);
    TimeSeriesValue tsv1_one = new TimeSeriesValue ("Test1", new Double (1), 5, 5);
    TimeSeriesValue tsv2_one = new TimeSeriesValue ("Test1", new Double (2), 10, 5);
    TimeSeriesValue tsv3_one = new TimeSeriesValue ("Test1", new Double (3), 15, 5);
    TimeSeriesValue tsv4_one = new TimeSeriesValue ("Test1", new Double (4), 20, 5);
    TimeSeriesValue tsv5_one = new TimeSeriesValue ("Test1", new Double (5), 25, 5);
    TimeSeriesValue tsv6_one = new TimeSeriesValue ("Test1", new Double (6), 30, 5);

    // add these values to a time series
    TimeSeries ts_one = new TimeSeries (tsv0_one);
    ts_one.addElement(tsv1_one);
    ts_one.addElement(tsv2_one);
    ts_one.addElement(tsv3_one);
    ts_one.addElement(tsv4_one);
    ts_one.addElement(tsv5_one);
    ts_one.addElement(tsv6_one);


    TimeSeriesValue tsv0_two = new TimeSeriesValue ("Test2", new Double (0), 0, 5);
    TimeSeriesValue tsv1_two = new TimeSeriesValue ("Test2", new Double (1), 5, 5);
    TimeSeriesValue tsv2_two = new TimeSeriesValue ("Test2", new Double (2), 10, 5);
    TimeSeriesValue tsv3_two = new TimeSeriesValue ("Test2", new Double (3), 15, 5);
    TimeSeriesValue tsv4_two = new TimeSeriesValue ("Test2", new Double (4), 20, 5);
    TimeSeriesValue tsv5_two = new TimeSeriesValue ("Test2", new Double (5), 25, 5);
    TimeSeriesValue tsv6_two = new TimeSeriesValue ("Test2", new Double (6), 30, 5);

    // add these values to a time series
    TimeSeries ts_two = new TimeSeries (tsv0_two);
    ts_two.addElement(tsv1_two);
    ts_two.addElement(tsv2_two);
    ts_two.addElement(tsv3_two);
    ts_two.addElement(tsv4_two);
    ts_two.addElement(tsv5_two);
    ts_two.addElement(tsv6_two);

    if (cca.correlationExists (ts_one, ts_two))
    {
      df.addText ("1. Pass : correlationExists() found an acceptable correlation.");
      df.addText ("Optimal Time Lag : " + cca.getOptimalTimeLag());
      df.addText ("Correlation Coefficient " + cca.getCorrelation());
    }
    else
      df.addText ("1. Fail : correlationExists() failed to correlate identical time series.");

   // df.addText (cca.getCorrelationTimeSeries().toString());

    /////////////////testing a negatively correlated, but identical, pair of time series.

    tsv0_one = new TimeSeriesValue ("Test1", new Double (0), 0, 5);
    tsv1_one = new TimeSeriesValue ("Test1", new Double (1), 5, 5);
    tsv2_one = new TimeSeriesValue ("Test1", new Double (2), 10, 5);
    tsv3_one = new TimeSeriesValue ("Test1", new Double (3), 15, 5);
    tsv4_one = new TimeSeriesValue ("Test1", new Double (4), 20, 5);
    tsv5_one = new TimeSeriesValue ("Test1", new Double (5), 25, 5);
    tsv6_one = new TimeSeriesValue ("Test1", new Double (6), 30, 5);

    // add these values to a time series
    ts_one = new TimeSeries (tsv0_one);
    ts_one.addElement(tsv1_one);
    ts_one.addElement(tsv2_one);
    ts_one.addElement(tsv3_one);
    ts_one.addElement(tsv4_one);
    ts_one.addElement(tsv5_one);
    ts_one.addElement(tsv6_one);


    tsv0_two = new TimeSeriesValue ("Test2", new Double (6), 0, 5);
    tsv1_two = new TimeSeriesValue ("Test2", new Double (5), 5, 5);
    tsv2_two = new TimeSeriesValue ("Test2", new Double (4), 10, 5);
    tsv3_two = new TimeSeriesValue ("Test2", new Double (3), 15, 5);
    tsv4_two = new TimeSeriesValue ("Test2", new Double (2), 20, 5);
    tsv5_two = new TimeSeriesValue ("Test2", new Double (1), 25, 5);
    tsv6_two = new TimeSeriesValue ("Test2", new Double (0), 30, 5);

    // add these values to a time series
    ts_two = new TimeSeries (tsv0_two);
    ts_two.addElement(tsv1_two);
    ts_two.addElement(tsv2_two);
    ts_two.addElement(tsv3_two);
    ts_two.addElement(tsv4_two);
    ts_two.addElement(tsv5_two);
    ts_two.addElement(tsv6_two);

    if (cca.correlationExists (ts_one, ts_two))
    {
      df.addText ("2. Pass : correlationExists() found an acceptable correlation.");
      df.addText ("Optimal Time Lag : " + cca.getOptimalTimeLag());
      df.addText ("Correlation Coefficient " + cca.getCorrelation());
    }
    else
      df.addText ("2. Fail : correlationExists() failed to correlate identical time series.");

//    df.addText (cca.getCorrelationTimeSeries().toString());

    ////// testing correlation of two identical shifted time series
    cca.setMaxTimeLag (4);

    tsv0_one = new TimeSeriesValue ("Test1", new Double (1), 0, 5);
    tsv1_one = new TimeSeriesValue ("Test1", new Double (1), 5, 5);
    tsv2_one = new TimeSeriesValue ("Test1", new Double (5), 10, 5);
    tsv3_one = new TimeSeriesValue ("Test1", new Double (5), 15, 5);
    tsv4_one = new TimeSeriesValue ("Test1", new Double (7), 20, 5);
    tsv5_one = new TimeSeriesValue ("Test1", new Double (7), 25, 5);
    tsv6_one = new TimeSeriesValue ("Test1", new Double (7), 30, 5);
    TimeSeriesValue tsv7_one = new TimeSeriesValue ("Test1", new Double (7), 35, 5);
    TimeSeriesValue tsv8_one = new TimeSeriesValue ("Test1", new Double (7), 40, 5);

    // add these values to a time series
    ts_one = new TimeSeries (tsv0_one);
    ts_one.addElement(tsv1_one);
    ts_one.addElement(tsv2_one);
    ts_one.addElement(tsv3_one);
    ts_one.addElement(tsv4_one);
    ts_one.addElement(tsv5_one);
    ts_one.addElement(tsv6_one);
    ts_one.addElement(tsv7_one);
    ts_one.addElement(tsv8_one);


    tsv0_two = new TimeSeriesValue ("Test2", new Double (1), 0, 5);
    tsv1_two = new TimeSeriesValue ("Test2", new Double (1), 5, 5);
    tsv2_two = new TimeSeriesValue ("Test2", new Double (1), 10, 5);
    tsv3_two = new TimeSeriesValue ("Test2", new Double (1), 15, 5);
    tsv4_two = new TimeSeriesValue ("Test2", new Double (1), 20, 5);
    tsv5_two = new TimeSeriesValue ("Test2", new Double (5), 25, 5);
    tsv6_two = new TimeSeriesValue ("Test2", new Double (5), 30, 5);
    TimeSeriesValue tsv7_two = new TimeSeriesValue ("Test2", new Double (7), 35, 5);
    TimeSeriesValue tsv8_two = new TimeSeriesValue ("Test2", new Double (7), 40, 5);

    // add these values to a time series
    ts_two = new TimeSeries (tsv0_two);
    ts_two.addElement(tsv1_two);
    ts_two.addElement(tsv2_two);
    ts_two.addElement(tsv3_two);
    ts_two.addElement(tsv4_two);
    ts_two.addElement(tsv5_two);
    ts_two.addElement(tsv6_two);
    ts_two.addElement(tsv7_two);
    ts_two.addElement(tsv8_two);

    if (cca.correlationExists (ts_one, ts_two))
    {
      if (cca.getOptimalTimeLag() == 3)
      {
        df.addText ("3. Pass : correlationExists() found the optimal correlation.");
        df.addText ("Optimal Time Lag : " + cca.getOptimalTimeLag());
        df.addText ("Correlation Coefficient " + cca.getCorrelation());
      }
    }
    else
      df.addText ("3. Fail : correlationExists() failed to correlate identical, shifted time series.");

//    df.addText (cca.getCorrelationTimeSeries().toString());

    ////////////// testing another more convoluted, shifted time series.

    cca.setMaxTimeLag (8);

    tsv0_one = new TimeSeriesValue ("Test1", new Double (1), 0, 5);
    tsv1_one = new TimeSeriesValue ("Test1", new Double (2), 5, 5);
    tsv2_one = new TimeSeriesValue ("Test1", new Double (3), 10, 5);
    tsv3_one = new TimeSeriesValue ("Test1", new Double (6), 15, 5);
    tsv4_one = new TimeSeriesValue ("Test1", new Double (6), 20, 5);
    tsv5_one = new TimeSeriesValue ("Test1", new Double (2), 25, 5);
    tsv6_one = new TimeSeriesValue ("Test1", new Double (3), 30, 5);
    tsv7_one = new TimeSeriesValue ("Test1", new Double (10), 35, 5);
    tsv8_one = new TimeSeriesValue ("Test1", new Double (12), 40, 5);
    TimeSeriesValue tsv9_one = new TimeSeriesValue ("Test1", new Double (15), 45, 5);
    TimeSeriesValue tsv10_one = new TimeSeriesValue ("Test1", new Double (16), 50, 5);
    TimeSeriesValue tsv11_one = new TimeSeriesValue ("Test1", new Double (10), 55, 5);
    TimeSeriesValue tsv12_one = new TimeSeriesValue ("Test1", new Double (9), 60, 5);
    TimeSeriesValue tsv13_one = new TimeSeriesValue ("Test1", new Double (8), 65, 5);
    TimeSeriesValue tsv14_one = new TimeSeriesValue ("Test1", new Double (6), 70, 5);

    // add these values to a time series
    ts_one = new TimeSeries (tsv0_one);
    ts_one.addElement(tsv1_one);
    ts_one.addElement(tsv2_one);
    ts_one.addElement(tsv3_one);
    ts_one.addElement(tsv4_one);
    ts_one.addElement(tsv5_one);
    ts_one.addElement(tsv6_one);
    ts_one.addElement(tsv7_one);
    ts_one.addElement(tsv8_one);
    ts_one.addElement(tsv9_one);
    ts_one.addElement(tsv10_one);
    ts_one.addElement(tsv11_one);
    ts_one.addElement(tsv12_one);
    ts_one.addElement(tsv13_one);
    ts_one.addElement(tsv14_one);


    tsv0_two = new TimeSeriesValue ("Test2", new Double (0), 0, 5);
    tsv1_two = new TimeSeriesValue ("Test2", new Double (0), 5, 5);
    tsv2_two = new TimeSeriesValue ("Test2", new Double (1), 10, 5);
    tsv3_two = new TimeSeriesValue ("Test2", new Double (1), 15, 5);
    tsv4_two = new TimeSeriesValue ("Test2", new Double (2), 20, 5);
    tsv5_two = new TimeSeriesValue ("Test2", new Double (3), 25, 5);
    tsv6_two = new TimeSeriesValue ("Test2", new Double (3), 30, 5);
    tsv7_two = new TimeSeriesValue ("Test2", new Double (3), 35, 5);
    tsv8_two = new TimeSeriesValue ("Test2", new Double (2), 40, 5);
    TimeSeriesValue tsv9_two = new TimeSeriesValue ("Test2", new Double (1), 45, 5);
    TimeSeriesValue tsv10_two = new TimeSeriesValue ("Test2", new Double (0), 50, 5);
    TimeSeriesValue tsv11_two = new TimeSeriesValue ("Test2", new Double (2), 55, 5);
    TimeSeriesValue tsv12_two = new TimeSeriesValue ("Test2", new Double (3), 60, 5);
    TimeSeriesValue tsv13_two = new TimeSeriesValue ("Test2", new Double (4), 65, 5);
    TimeSeriesValue tsv14_two = new TimeSeriesValue ("Test2", new Double (5), 70, 5);


    // add these values to a time series
    ts_two = new TimeSeries (tsv0_two);
    ts_two.addElement(tsv1_two);
    ts_two.addElement(tsv2_two);
    ts_two.addElement(tsv3_two);
    ts_two.addElement(tsv4_two);
    ts_two.addElement(tsv5_two);
    ts_two.addElement(tsv6_two);
    ts_two.addElement(tsv7_two);
    ts_two.addElement(tsv8_two);
    ts_two.addElement(tsv9_two);
    ts_two.addElement(tsv10_two);
    ts_two.addElement(tsv11_two);
    ts_two.addElement(tsv12_two);
    ts_two.addElement(tsv13_two);
    ts_two.addElement(tsv14_two);

    if (cca.correlationExists (ts_two, ts_one))
    {
      df.addText ("4. Pass : correlationExists() found a correlation.");
      df.addText ("Optimal Time Lag : " + cca.getOptimalTimeLag());
      df.addText ("Correlation Coefficient " + cca.getCorrelation());
    }
    else
      df.addText ("4. Fail : correlationExists() failed to correlate identical, shifted time series.");

    df.addText (cca.getCorrelationTimeSeries().toString());

  } // end main
}// end class