/**
 *  @file         CorrelationAnalysisDisplay.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created June 21, 2001.
 *  @todo
 **/

package com.centurylogix.timeSeries.correlationAnalysis;

import visad.*;
import visad.java2d.DisplayImplJ2D;
import java.rmi.RemoteException;
import java.awt.*;
import javax.swing.*;
import java.util.Iterator;

import com.centurylogix.timeSeries.*;

public class CorrelationAnalysisDisplay
{
  // The quantities to be displayed in x- and y-axes: time and TimeSeriesValues, respectively
  // Our index is also a RealType
  private RealType time;
  private RealType indepTS_rt;
  private RealType depTS_rt;
  private RealType corrTS_rt;
  private RealType index;

  // A Tuple, to pack time and TimeSeriesValue together
  private RealTupleType t_indepTS_tuple;
  private RealTupleType t_depTS_tuple;
  private RealTupleType t_corrTS_tuple;

  // The function ( elevation(i), height(i) ), where i = index,
  // represented by ( index -> ( elevation, height) )
  // ( elevation, height) are a Tuple, so we have a FunctionType
  // from index to a tuple
  private FunctionType func_i_indepTsTuple;
  private FunctionType func_i_depTsTuple;
  private FunctionType func_i_corrTsTuple;

  // these functions map a time to time series value and are used for line drawing.
  private FunctionType func_time_indepTS;
  private FunctionType func_time_depTS;
  private FunctionType func_time_corrTS;

  // Our Data values: the domain Set time_set for ( time -> height )
  // and the Set index_set for the indexed points
  private Set time_set;
  private Set index_set;
  private Set corr_index_set;
  private Set corr_time_set;

  // The Data class FlatField, which will hold time and height data.
  // time Data for line is implicitely given by the Set time_set
  // point_vals_ff holds the point values
  private FlatField indepTS_points_ff;
  private FlatField depTS_points_ff;
  private FlatField corrTS_points_ff;

  private FlatField indepTS_line_ff;
  private FlatField depTS_line_ff;
  private FlatField corrTS_line_ff;


  // The DataReference from the data to display
  private DataReferenceImpl indepTS_points_ref;
  private DataReferenceImpl indepTS_line_ref;
  private DataReferenceImpl depTS_line_ref;
  private DataReferenceImpl depTS_points_ref;
  private DataReferenceImpl corrTS_line_ref;
  private DataReferenceImpl corrTS_points_ref;

  // The 2D display, and its the maps
  private DisplayImpl display;

  private ScalarMap timeMap;
  private ScalarMap timeRangeMap;
  private ScalarMap indepTSMap;
  private ScalarMap depTSMap;
  private ScalarMap corrTSMap;


  public CorrelationAnalysisDisplay (CorrelationDisplayRequest cdr) throws RemoteException, VisADException
  {
    TimeSeries indepTS = cdr.getIndependentTimeSeries();
    TimeSeries depTS = cdr.getDependentTimeSeries ();
    TimeSeries corrTS = cdr.getCorrelationTimeSeries();

    // Create the quantities
    // Use RealType(String name)
    time = new RealType("time");
    indepTS_rt = new RealType(indepTS.getName());
    depTS_rt = new RealType (depTS.getName());
    corrTS_rt = new RealType (corrTS.getName());

    t_indepTS_tuple = new RealTupleType(time, indepTS_rt);
    t_depTS_tuple = new RealTupleType (time, depTS_rt);
    t_corrTS_tuple = new RealTupleType (time, corrTS_rt);

    // Index has no unit, just a name
    index = new RealType("Index");

    // Create a FunctionType ( index -> ( time, TimeSeriesValue) ), for points
    // Use FunctionType(MathType domain, MathType range)
    func_i_indepTsTuple = new FunctionType(index, t_indepTS_tuple);
    func_i_depTsTuple = new FunctionType (index, t_depTS_tuple);
    func_i_corrTsTuple = new FunctionType (index, t_corrTS_tuple);

    // Create index_set, but this time using a Integer1DSet(MathType type, int length)
    index_set = new Integer1DSet(index, indepTS.size());

    // These are our actual data values for time and height
    // Note that these values correspond to the parabola of the
    // previous examples. The y (height) values are the same, but the x (time)
    // values are explicitely given.

    // generate points for independent time series
    int indep_size = indepTS.size();
    System.out.println("Time series size " + indep_size );
    double [][] indepTS_point_vals = new double [2][indep_size];

    Iterator tsIter = indepTS.iterator();
    int count = 0;

    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
      double tsVal = ((Number)tsv.getValue()).doubleValue();

//      System.out.println ("looking to add " + tsVal +" at index : " + count);
      indepTS_point_vals [0][count] = (double) count;
      indepTS_point_vals [1][count] = tsVal;

      count++;
    }


    // generate points for dependent time series
    System.out.println ("Adding points for dependent time series.");
    int dep_size = depTS.size();
    double [][] depTS_point_vals = new double [2][dep_size];

    tsIter = depTS.iterator();
    count = 0;
    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
      double tsVal = ((Number)tsv.getValue()).doubleValue();

//      System.out.println ("looking to add " + tsVal +" at index : " + count);
      depTS_point_vals [0][count] = (double) count;
      depTS_point_vals [1][count] = tsVal;

      count++;
    }


    // generate points for the correlation time series
    int corr_size = corrTS.size();
    System.out.println("Correlation series size " + corr_size );
    double [][] corrTS_point_vals = new double [2][corr_size];

    tsIter = corrTS.iterator();
    count = 0;
    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
      double tsVal = ((Number)tsv.getValue()).doubleValue();

//      System.out.println ("looking to add " + tsVal +" at index : " + count);
      corrTS_point_vals [0][count] = (double) count;
      corrTS_point_vals [1][count] = tsVal;

      count++;
    }

    // Create a FlatField, that is the Data class for the samples
    // Use FlatField(FunctionType type, Set domain_set)

    corr_index_set = new Integer1DSet(index, corrTS.size());

    // for the (time, height) points
    indepTS_points_ff = new FlatField(func_i_indepTsTuple, index_set);
    depTS_points_ff = new FlatField (func_i_depTsTuple , index_set);
    corrTS_points_ff = new FlatField (func_i_corrTsTuple , corr_index_set);

    // and finally put the points and height values above into the points FlatField
    indepTS_points_ff.setSamples( indepTS_point_vals );
    depTS_points_ff.setSamples (depTS_point_vals);

    System.out.println ("about to set samples for correlation points");
    corrTS_points_ff.setSamples (corrTS_point_vals);
    System.out.println ("done setting samples for correlation points");
   // Code for setting LINE data

    // the FunctionType for the line, function ( time -> height)

    func_time_indepTS = new FunctionType(time, indepTS_rt);
    func_time_depTS = new FunctionType (time, depTS_rt);
    func_time_corrTS = new FunctionType (time, corrTS_rt);

    // may need unique one of these things for each time series since lengths may not be the same
    int length = indepTS.size();
    time_set = new Linear1DSet(time, 0, length - 1, length ); // these are starting start end indices.

    //System.out.println ("making new time set for correlation");
    //int corrSize = corrTS.size();
    //corr_time_set = new Linear1DSet(time, 0, corrSize - 1, corrSize * 2);

      //  System.out.println ("done making new time set for correlation");
    // ...then we use a method of Set to get the samples from time_set;
    // this call will get the time values
    // "true" means we get a copy from the samples
    float[][] d_vals  = time_set.getSamples( true);

    /******* create the set of lines that will create the time series values **************/
    double[][] indepTS_vals = new double [1][length];

    for(int i = 0; i < length; i++)
    {
      indepTS_vals[0][i] = ((Number)indepTS.getValueAt(i).getValue()).doubleValue();
    }

    // Create a FlatField, that is the Data class for the samples
    // Use FlatField(FunctionType type, Set domain_set) for the line
    indepTS_line_ff = new FlatField( func_time_indepTS, time_set);

    // and finally put the points and height values into the line FlatField
    indepTS_line_ff.setSamples( indepTS_vals );




    double[][] depTS_vals = new double [1][length];

    for(int i = 0; i < length; i++)
    {
      depTS_vals[0][i] = ((Number)depTS.getValueAt(i).getValue()).doubleValue();//val;
    }

    // Create a FlatField, that is the Data class for the samples
    // Use FlatField(FunctionType type, Set domain_set) for the line
    depTS_line_ff = new FlatField( func_time_depTS, time_set);

    // and finally put the points and height values into the line FlatField
    depTS_line_ff.setSamples( depTS_vals );



    double[][] corrTS_vals = new double [1][corrTS.size()];

    for(int i = 0; i < corrTS.size(); i++)
    {
      corrTS_vals[0][i] = ((Number)corrTS.getValueAt(i).getValue()).doubleValue();
    }

    corr_time_set = new Linear1DSet(time, 0, corrTS.size() - 1,corrTS.size() ); // these are starting start end indices.
    // Create a FlatField, that is the Data class for the samples
    // Use FlatField(FunctionType type, Set domain_set) for the line
    corrTS_line_ff = new FlatField( func_time_corrTS, corr_time_set);

    // and finally put the points and height values into the line FlatField
    corrTS_line_ff.setSamples( corrTS_vals );


    /*************************  Create Display and its maps *****************************/
    // A 2D display
    display = new DisplayImplJ2D("Time Series Test Display");

    // Get display's graphics mode control and draw scales
    GraphicsModeControl dispGMC = (GraphicsModeControl) display.getGraphicsModeControl();
    dispGMC.setScaleEnable(true);

    // Create the ScalarMaps: quantity time is to be displayed along XAxis  and height along YAxis
    // Use ScalarMap(ScalarType scalar, DisplayRealType display_scalar)
    timeMap = new ScalarMap( time, Display.XAxis );
    corrTSMap = new ScalarMap (corrTS_rt, Display.YAxis);
    indepTSMap = new ScalarMap( indepTS_rt,   Display.YAxis );
    depTSMap = new ScalarMap (depTS_rt, Display.YAxis);


    float[] corrTSColor = new float[]{1f,1f,1f};
    corrTSMap.setScaleColor (corrTSColor);

    float[] indepTSColor = new float[]{0f,.05f,.85f};
    indepTSMap.setScaleColor (indepTSColor);
    indepTSMap.setScaleEnable (false);

    float[] depTSColor = new float[]{1f,0f,0f};
    depTSMap.setScaleColor (depTSColor);
    depTSMap.setScaleEnable (false);


    // We create a new ScalarMap, with time as RealType and SelectRange as DisplayRealType
    timeRangeMap = new ScalarMap( time, Display.SelectRange );

    // Add maps to display
    display.addMap (corrTSMap);
    display.addMap(timeMap );
    display.addMap(indepTSMap );
    display.addMap (depTSMap);
    display.addMap(timeRangeMap );

    // Scale heightMap. This will scale the y-axis, because heightMap has DisplayRealType YAXIS
    // We simply choose the range from -4 to 4 for the x-axis
    // and -10.0 to 50.0 for
    timeMap.setRange( -5, length+5);

    int lowerBound = (int) (TimeSeriesUtilities.getMinValue (indepTS) * 1.1);
    int upperBound = (int) (TimeSeriesUtilities.getMaxValue (indepTS) * 1.1);
    indepTSMap.setRange( lowerBound , upperBound);

    // Select the range of RealType time
    RangeControl distRangeControl = (RangeControl) timeRangeMap.getControl();

    float[] timeRange = { 0, length - 1};
    distRangeControl.setRange( timeRange );


    // Create a data reference and set the FlatField as our data
    indepTS_points_ref = new DataReferenceImpl("indepTS_points_ref");
    indepTS_line_ref = new DataReferenceImpl("indepTS_line_ref");
    depTS_line_ref = new DataReferenceImpl("depTS_line_ref");
    depTS_points_ref = new DataReferenceImpl("depTS_points_ref");
    corrTS_line_ref = new DataReferenceImpl("corrTS_line_ref");
    corrTS_points_ref = new DataReferenceImpl("corrTS_points_ref");


    corrTS_line_ref.setData (corrTS_line_ff);
    indepTS_points_ref.setData(indepTS_points_ff );
    indepTS_line_ref.setData(indepTS_line_ff );
    depTS_line_ref.setData (depTS_line_ff);
    depTS_points_ref.setData (depTS_points_ff);
//    corrTS_points_ref.setData (corrTS_points_ff);


    // Only change from the previous version
    // Define a ConstantMap to draw large red points
    ConstantMap[] depTS_pointsCMap = {     new ConstantMap(0, Display.Red),
                                     new ConstantMap(0, Display.Green),
                                     new ConstantMap(1, Display.Blue),
                                     new ConstantMap( 4.0f, Display.PointSize)};

    ConstantMap[] indepTS_pointsCMap = {new ConstantMap(0, Display.Red),
                                     new ConstantMap(1, Display.Green),
                                     new ConstantMap(0, Display.Blue),
                                     new ConstantMap( 4.0f, Display.PointSize)};

    ConstantMap[] indepTS_lineCMap = {new ConstantMap( 0.0f, Display.Red),
                                     new ConstantMap( 1.0f, Display.Green),
                                     new ConstantMap( 0.0f, Display.Blue),
                                     new ConstantMap( 1.75f, Display.LineWidth)};

    ConstantMap[] depTS_lineCMap = { new ConstantMap( 0, Display.Red),
                                     new ConstantMap( 0, Display.Green),
                                     new ConstantMap( 1, Display.Blue),
                                     new ConstantMap( 1.75f, Display.LineWidth)};

    ConstantMap[] corrTS_lineCMap = { new ConstantMap( 1, Display.Red),
                                     new ConstantMap( 1, Display.Green),
                                     new ConstantMap( 1, Display.Blue),
                                     new ConstantMap( 1.5f, Display.LineWidth)};

    // Add reference to display, and link DataReference to ConstantMap
  //  display.addReference( corrTS_points_ref, pointsCMap );
    display.addReference( corrTS_line_ref , corrTS_lineCMap);

    display.addReference( indepTS_points_ref, indepTS_pointsCMap );
    display.addReference( indepTS_line_ref , indepTS_lineCMap);

    display.addReference( depTS_points_ref, depTS_pointsCMap );
    display.addReference( depTS_line_ref , depTS_lineCMap);

    // Create application window, put display into it
    JFrame jframe = new JFrame("Time Series Display Test");
    jframe.getContentPane().add(display.getComponent());

    // Set window size and make it visible
    jframe.setSize(650, 650);
    jframe.setVisible(true);

  } // end constructor

  public static void main(String[] args) throws RemoteException, VisADException
  {
    TimeSeriesValue tsv0_two = new TimeSeriesValue ("Test2", new Double (0), 0, 5);
    TimeSeriesValue tsv1_two = new TimeSeriesValue ("Test2", new Double (0), 5, 5);
    TimeSeriesValue tsv2_two = new TimeSeriesValue ("Test2", new Double (1), 10, 5);
    TimeSeriesValue tsv3_two = new TimeSeriesValue ("Test2", new Double (2), 15, 5);
    TimeSeriesValue tsv4_two = new TimeSeriesValue ("Test2", new Double (5), 20, 5);
    TimeSeriesValue tsv5_two = new TimeSeriesValue ("Test2", new Double (2), 25, 5);
    TimeSeriesValue tsv6_two = new TimeSeriesValue ("Test2", new Double (0), 30, 5);
    TimeSeriesValue tsv7_two = new TimeSeriesValue ("Test2", new Double (0), 35, 5);
    TimeSeriesValue tsv8_two = new TimeSeriesValue ("Test2", new Double (0), 40, 5);
    TimeSeriesValue tsv9_two = new TimeSeriesValue ("Test2", new Double (0), 45, 5);
    TimeSeriesValue tsv10_two = new TimeSeriesValue ("Test2", new Double (5), 50, 5);
    TimeSeriesValue tsv11_two = new TimeSeriesValue ("Test2", new Double (10), 55, 5);
    TimeSeriesValue tsv12_two = new TimeSeriesValue ("Test2", new Double (15), 60, 5);
    TimeSeriesValue tsv13_two = new TimeSeriesValue ("Test2", new Double (5), 65, 5);
    TimeSeriesValue tsv14_two = new TimeSeriesValue ("Test2", new Double (0), 70, 5);


    // add these values to a time series
    TimeSeries independentTS = new TimeSeries (tsv0_two);
    independentTS.addElement(tsv1_two);
    independentTS.addElement(tsv2_two);
    independentTS.addElement(tsv3_two);
    independentTS.addElement(tsv4_two);
    independentTS.addElement(tsv5_two);
    independentTS.addElement(tsv6_two);
    independentTS.addElement(tsv7_two);
    independentTS.addElement(tsv8_two);
    independentTS.addElement(tsv9_two);
    independentTS.addElement(tsv10_two);
    independentTS.addElement(tsv11_two);
    independentTS.addElement(tsv12_two);
    independentTS.addElement(tsv13_two);
    independentTS.addElement(tsv14_two);

    TimeSeriesValue tsv0_one = new TimeSeriesValue ("Test1", new Double (0), 0, 5);
    TimeSeriesValue tsv1_one = new TimeSeriesValue ("Test1", new Double (0), 5, 5);
    TimeSeriesValue tsv2_one = new TimeSeriesValue ("Test1", new Double (0), 10, 5);
    TimeSeriesValue tsv3_one = new TimeSeriesValue ("Test1", new Double (0), 15, 5);
    TimeSeriesValue tsv4_one = new TimeSeriesValue ("Test1", new Double (3), 20, 5);
    TimeSeriesValue tsv5_one = new TimeSeriesValue ("Test1", new Double (8), 25, 5);
    TimeSeriesValue tsv6_one = new TimeSeriesValue ("Test1", new Double (10), 30, 5);
    TimeSeriesValue tsv7_one = new TimeSeriesValue ("Test1", new Double (5), 35, 5);
    TimeSeriesValue tsv8_one = new TimeSeriesValue ("Test1", new Double (2), 40, 5);
    TimeSeriesValue tsv9_one = new TimeSeriesValue ("Test1", new Double (0), 45, 5);
    TimeSeriesValue tsv10_one = new TimeSeriesValue ("Test1", new Double (0), 50, 5);
    TimeSeriesValue tsv11_one = new TimeSeriesValue ("Test1", new Double (0), 55, 5);
    TimeSeriesValue tsv12_one = new TimeSeriesValue ("Test1", new Double (2), 60, 5);
    TimeSeriesValue tsv13_one = new TimeSeriesValue ("Test1", new Double (20), 65, 5);
    TimeSeriesValue tsv14_one = new TimeSeriesValue ("Test1", new Double (0), 70, 5);


    // add these values to a time series
    TimeSeries dependentTS = new TimeSeries (tsv0_one);
    dependentTS.addElement(tsv1_one);
    dependentTS.addElement(tsv2_one);
    dependentTS.addElement(tsv3_one);
    dependentTS.addElement(tsv4_one);
    dependentTS.addElement(tsv5_one);
    dependentTS.addElement(tsv6_one);
    dependentTS.addElement(tsv7_one);
    dependentTS.addElement(tsv8_one);
    dependentTS.addElement(tsv9_one);
    dependentTS.addElement(tsv10_one);
    dependentTS.addElement(tsv11_one);
    dependentTS.addElement(tsv12_one);
    dependentTS.addElement(tsv13_one);
    dependentTS.addElement(tsv14_one);


    TimeSeriesValue tsv0_three = new TimeSeriesValue ("Corr", new Double (-.25), 0, 5);
    TimeSeriesValue tsv1_three = new TimeSeriesValue ("Corr", new Double (-.3), 5, 5);
    TimeSeriesValue tsv2_three = new TimeSeriesValue ("Corr", new Double (-1), 10, 5);
    TimeSeriesValue tsv3_three = new TimeSeriesValue ("Corr", new Double (-.89), 15, 5);
    TimeSeriesValue tsv4_three = new TimeSeriesValue ("Corr", new Double (-.5), 20, 5);
    TimeSeriesValue tsv5_three = new TimeSeriesValue ("Corr", new Double (-.2), 25, 5);
    TimeSeriesValue tsv6_three = new TimeSeriesValue ("Corr", new Double (0), 30, 5);
    TimeSeriesValue tsv7_three = new TimeSeriesValue ("Corr", new Double (.002), 35, 5);
    TimeSeriesValue tsv8_three = new TimeSeriesValue ("Corr", new Double (.04), 40, 5);
    TimeSeriesValue tsv9_three = new TimeSeriesValue ("Corr", new Double (.6), 45, 5);
    TimeSeriesValue tsv10_three = new TimeSeriesValue ("Corr", new Double (.2), 50, 5);
    TimeSeriesValue tsv11_three = new TimeSeriesValue ("Corr", new Double (.57), 55, 5);
    TimeSeriesValue tsv12_three = new TimeSeriesValue ("Corr", new Double (0), 60, 5);
    TimeSeriesValue tsv13_three = new TimeSeriesValue ("Corr", new Double (.45), 65, 5);
    TimeSeriesValue tsv14_three = new TimeSeriesValue ("Corr", new Double (1), 70, 5);


    // add these values to a time series
    TimeSeries corrTS = new TimeSeries (tsv0_three);
    corrTS.addElement(tsv1_three);
    corrTS.addElement(tsv2_three);
    corrTS.addElement(tsv3_three);
    corrTS.addElement(tsv4_three);
    corrTS.addElement(tsv5_three);
    corrTS.addElement(tsv6_three);
    corrTS.addElement(tsv7_three);
    corrTS.addElement(tsv8_three);
    corrTS.addElement(tsv9_three);
    corrTS.addElement(tsv10_three);
 //   corrTS.addElement(tsv11_three);
 //   corrTS.addElement(tsv12_three);
//  corrTS.addElement(tsv13_three);
 //   corrTS.addElement(tsv14_three);

    CorrelationDisplayRequest cdr = new CorrelationDisplayRequest(independentTS, dependentTS);
    cdr.setCorrelationSeries (corrTS);

    new CorrelationAnalysisDisplay (cdr);
  }

}// end class
