/**
 *  @file         PatternPredictionAlgorithm.java
 *  @copyright    Copyright (c) 2000
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This Pattern Prediction Algorithm
 *  This is the description.
 *  @todo         1. Fine tune notion of epsilon.
 *                2. figure out exactly how to use sensitivity and commuinicate it.
 *
 **/

package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;
import com.centurylogix.ultralog.DebugFrame;
import java.util.*;

public class PatternPredictionAlgorithm implements AlgorithmInterface
{
  private String algName = "Pattern Prediction Algorithm";

  private String inputStreamName = null;  //name of data stream being analyzed
  private AlgorithmState state = null;    //state of algorithm's predictions, error data
  private long stepSize;                  //discrete unit of time associated with all data

  //variables that dictate algorithm behavior and are a fn. of input sensitivity
  private int minPatternLength;   // min. length of potential pattern
  private int minOccurenceLevel;  // min. # of times pattern/prefix must occur before considering it
  private double epsilon;         // allowed difference b/n two values we will consider as equal
  private int minPrefixSize;      // min. length of prefix that can be extended to a pattern
  private int maxPrefixSize;      // max. length of prefix, won't bother trying to extend further
  private double minConfidence;   // min. level of support for a prediction
  private int maxPatternLength;   // max. prediction length

  private DebugFrame df = new DebugFrame (false);

  /**
   * Each instance will likely have a unique stream of input data identified by <tt> streamName </tt>
   * @param     streamName    An dentifier for the input time-series data.
   */
  public PatternPredictionAlgorithm(String streamName)
  {
    // create debug frame
    df.setTitle("PatternPredictionAlgorithm Debug Frame");
    df.setBounds(150, 150, 500, 300);
    df.show();
    //df.printToFile ("PPA.out");

    this.inputStreamName = streamName;
    resetLevels();
  }

  /**
   * Sets the sensitivity related variables to thier default values.
   */
  private void resetLevels ()
  {
    // set default values for sensitivity-dependent variables
    this.minConfidence = .7;
    this.minPatternLength = 3;
    this.minOccurenceLevel = 2;
    this.minPrefixSize = 3;
    this.maxPrefixSize = 6; // serves as stopping point for prefix expansion attempts.
    this.epsilon = 10;
    this.maxPatternLength = 10;
  }

  /**
   * Method serves as a one-time setup for this class. It uses the input info. to
   *  set member variables that direct the behavior of the algorithm.
   * @param   pc          Data structure containing sensitiviy level information and other
   *                      user defined parameters.
   * @param   startTime   Scenario time at which analysis was initiated.
   * @requires pc != null
   **/
  public void initialize (long step, long startTime)
  {
    this.stepSize = step;
    this.state = new AlgorithmState (step, startTime, getAlgorithmName());

    double deviation = 0; //pc.getSensitivityDeviation(); should be b/n -.2 and .2

    minConfidence = .6; // pc.getSensitivityLevel ();
    minPatternLength = Math.round ( (long) (minPatternLength + (10 * deviation)));
    minOccurenceLevel = Math.round ( (long) (minOccurenceLevel + (10 * deviation)));
    minPrefixSize = (int) Math.ceil((minPrefixSize + (5 * deviation)));

    // todo: need to fine tune this so that <epsilon> is more absract.
    epsilon = .25;//epsilon - Math.round (deviation * 10);

    df.addText ("Min Confidence : " + minConfidence);
    df.addText ("Devaiation : " + deviation);
    df.addText ("Min Pattern Length : " + minPatternLength);
    df.addText ("Min Occurence Level : " + minOccurenceLevel);
    df.addText ("Min Prefix Size : " + minPrefixSize);
    df.addText ("Epsilon : " + epsilon);
  } // end void initialize (long, long)


  /**
   *  Returns name of this algorithm : "Pattern Prediction Algorithm"
   *  @return   A string representation of the name of this algorithm followed by the name given to
   *            identify the input data stream.
   *            (i.e. "Pattern Prediction Algorithm-> Input Stream : actualConsumption")
   */
  public String getAlgorithmName ()
  {
    return algName; //(algName + "-> Input Stream : " +  inputStreamName);
  }

  /**
   *  Returns a weighted error value reflecting the accuracy of past predictions.
   *  If we have made predictions and are able to determine their accuracy, we compute their
   *  weighted error value. This error value places a higher weight on more recent predictions and
   *  gradually phases out the importance of past discrepancies between our predictions and the
   *  actual data.
   * @param     info        Data structure containing consumption information.
   * @param     currTime    The current scenario time.
   * @return    1. if we have not made any predictions yet -> return null. <br>
   *            2. otherwise we return a weighted error value
   * @requires  this.state != null
   */
  public Double getError (long currTime)
  {
    df.addText ("Error returned : " + this.state.getError());

    return (this.state.getError());
  } // end Double getError ()


  /**
   * If we have any predictions for dates beyond <tt> currentTime </tt>, we return a data
   *    structure populated with them.
   * @param   currentTime   The current scenario time.
   * @return  1. if we have no predictions beyond <currentTime> -> return null. <br>
   *          2. Otherwise we return a populated instance of PredictedData.
   * @requires  this.state != null
   * @modifies  this.state
   */
  public TimeSeries getPredictions (long currentTime)
  {
    TimeSeries predictions = state.getPredictions ();

    return predictions;
  }// end TimeSeries getPredictions (long)


  /**
   *  Main prediction method called by the Algorithm Manager to signal that new
   *  data was recieved and for this algorithm to use that to try and form a prediction. This
   *  method takes the trailing edge of the input time-series data and looks for other occurences
   *  of that portion of the series within the remainder of the input data. It then attempts to
   *  determine if the values extracted from the trailing edge of the input stream are part of a
   *  larger pattern that we may use to make a prediction with. Any predicitions are saved to an
   *  private state representation. If no prediction can be made,
   *  nothing is done. In addition, the error rating for this algorithm's performance is calculated.
   * @param   info  The data structure contining time-series data needed for making predictions
   * @requires  info != null, this.state != null
   */
  public void updatePredictions (long currTime, TimeSeries inputSeries)
  {

    this.state.clearPredictionsFrom (currTime + stepSize);

    df.addText ("\n\nInput : " + inputSeries.toString() );

    if (inputSeries.isEmpty())
      return;

    /* this will have lots of implications.
      // an attempt to slice the last 50 elements and just examine those....
    if (actualUsage.size () >  50)
    {
      int diff = actualUsage.size() - 50;

      for (int i = 0; i < diff; i++)
        actualUsage.remove(0);
    }
    */

    ArrayList prefixCandidate = new ArrayList (10);
    ArrayList prefix = new ArrayList (10);
    ArrayList occurenceList = new ArrayList (10);

    // get the last element in the input series
    TimeSeriesValue tsv = inputSeries.getValueAt (inputSeries.size() -1);
    Double lastElt = new Double (((Number) tsv.getValue()).doubleValue());

    if (lastElt == null) // this check is likely un-necessary
      return;

    // the first pattern prefix candidate is just the last element in the input stream
    prefixCandidate.add (lastElt);

    // keep searching to see if this prefix can be expanded
    while (candidateIsSupported (prefixCandidate, inputSeries, occurenceList, prefix))
    {
      df.addText ("Updated occurence list is : " + occurenceList.toString());
      // if the prefix is expandable, expand it further
      updatePrefixCandidate (prefixCandidate, inputSeries);
    }

    addSpacing (prefix.size(), occurenceList);

    df.addText ("Final occurence list : " + occurenceList.toString());
    // if the prefix we found support for is long enough, use if to make a prediction
    if (prefix.size() >= this.minPrefixSize)
    {
      df.addText ("Prefix : " + prefix.toString() + " is longer than Min. Prefix Size : " + minPrefixSize);
      calcPrediction (inputSeries, currTime, prefix, occurenceList);
    }
    else
      df.addText ("NO prediction since Prefix : " + prefix.toString() + " is shorter thatn min size : " + minPrefixSize);
    // update error information
    this.state.updateError(inputSeries, currTime);

  } //end public void updatePredictions (ArrayList, long)


  /**
   * Once we have identified a valid pattern prefix that corresponds with the trailing values of
   *  the input series, we search for the most common values that follow the prefix within the
   *  time series. If we find a common set of values that occurs frequently in conjuction with
   *  our identifed prefix, we will make this common string of values our prediction. Otherwise,
   *  if no common sequences occur in conjuction with our prefix, no predictions are made.
   * @param   inputSeries   The time-series data being analyzed for patterns.
   *          currTime      The current scenario time
   *          prefix        Sequence for trailing edge of <i>inputSeries</i> that we are attempting
   *                        to extrapolate from.
   *          occurenceList Indices of the locations of all occurences of <i>prefix</i>
   *                        in <i>inputSeries</i>
   * @requires inputSeries, prefix & occurenceList != null
   * @modifies occurenceList
   */
  private void calcPrediction (TimeSeries inputSeries, long currTime,
                               ArrayList prefix, ArrayList occurenceList)
  {
    boolean keepSearchingForPattern = true;

    // lenght of prefix
    int prefLength = prefix.size ();

    // update occurence list to be the index of the first elt. following each prefix occurence
    for (int i = 0; i < occurenceList.size (); i++)
    {
      int prefixStartPoint = ( (Integer) occurenceList.get(i)).intValue();
      int beginSearchPoint = prefixStartPoint + prefLength;
      occurenceList.set (i, new Integer (beginSearchPoint));
    }

    //df.addText ("Updated OccurencList : " + occurenceList.toString());

    int searchOffset = 0; // offset from end of prefix occurence where we are looking for pattern
    ArrayList listOfValues; // list of values being compared to determine if pattern is present
    int numberOfOccurences = occurenceList.size();
    int predictionLength = 0;

    // while finding good results keep searching occurenceList indices for repeating sequences
    while (keepSearchingForPattern)
    {
      listOfValues = new ArrayList (10);
      int validValues = 0;

      // get all values following prefix occurence by searchOffset in usage data
      for (int j = 0; j < numberOfOccurences; j++)
      {
        // get index where item of interest is at
        int searchPoint = ((Integer) occurenceList.get(j)).intValue() + searchOffset;

        if (searchPoint < inputSeries.size())
        {
          // retrieve value from this location and add it to our list
          TimeSeriesValue tsv = inputSeries.getValueAt (searchPoint);
          Double value = new Double ( ((Number) tsv.getValue()).doubleValue());

          if (value != null)
          {
            listOfValues.add (value);
            validValues++;
          }
        }
      }// end for

      // if we were not able to find any values, quit searching
      if (listOfValues.size() == 0)
        keepSearchingForPattern = false;
      else
      {
        df.addText ("List Of Values : " + listOfValues.toString());

        // if we found any values, find the most common elt. and the number of times it occurs
        ArrayList modeAndFreq = calcModeAndFrequency (listOfValues);

        df.addText ("Mode and Frequencey : " + modeAndFreq.toString());

        // if no consenus value was found, quit the search.
        if (modeAndFreq == null)
          keepSearchingForPattern = false;
        else
        {
          double mode = ((Double)modeAndFreq.get (0)).doubleValue();
          int frequency = ((Integer)modeAndFreq.get (1)).intValue();

          //df.addText ("Mode : "+mode+" Frequency : "+frequency+" Size : " + size);

          // our confidence in this value is the fraction of prefixes that are followed by it
          double confidence = (double)frequency / validValues;

          df.addText ("Confidence for : "+modeAndFreq.toString()+" is " + confidence);

          // if the number of occurences and the overall percatage of times occures meets our
            // requirements, we will postulate that this is part of a pattern.
          if (confidence >= minConfidence && frequency >= minOccurenceLevel)
          {
            long time = currTime + ((searchOffset + 1) * stepSize);

            df.addText ("Adding : time -> " + time + " mode-> " + mode+ " confidence-> " + confidence);
            state.addPrediction (time, mode);
            predictionLength++;
          }
          else
          {
            keepSearchingForPattern = false;
            df.addText (" Element : " + mode+ "is of low confidence or occurs too few times.. quitting");
          }

          // if our prediction has extended to the maximum length, signal for the process to halt
          if (predictionLength >= maxPatternLength)
            keepSearchingForPattern = false;

        }//end if/else (modeAndFreq == null);

      searchOffset++;

      }// end if (listOfValues.size() == 0)

    } //end while (keepSearchingForPattern)
  } // end private void calcPrediction (ArrayList)


  /**
   * Determines the mode (the most commonly occuring element) in the input and also its frequency
   *  (the number of times it occurs).
   * @param   data  A series of Double values that we will search for mode in.
   * @return  A two element ArrayList where the first element is the mode and the second
   *          is the frequency of this value.
   * @requires input must be comprised of non-null Double objects.
   * @modifies data
   */
  private ArrayList calcModeAndFrequency (ArrayList data)
  {
    if (data == null || data.size() == 0)
      return null;

    int remainingElts = data.size();
    int highFrequency = 0;
    int thisFrequency = 0;
    Double modeValue = null;

    while (remainingElts > 0)
    {
      // pop the first element
      Double firstElt = (Double) data.remove (0);
      remainingElts--;

      thisFrequency = 1;

      // see how many elements are like this one, removing them as they are found
      for (int i = 0; i < remainingElts; i++)
      {
        Double thisElt = (Double) data.get (i);

        if (elementsAreEqual (firstElt,thisElt))
        {
          data.remove(i);
          i--; // we have 1 less element in input
          remainingElts--;
          thisFrequency++;
        }
      }

      // determine if this is the most common elemtn we have found to date
      // ties go to whatever is found first, might be best in log run to have tie-braking protocol
      if (thisFrequency > highFrequency)
      {
        highFrequency = thisFrequency;
        modeValue = new Double (firstElt.doubleValue());
      }

    } // end while (remainingElts >0)

    // compose our output.
    ArrayList modeAndFreq = new ArrayList (2);
    modeAndFreq.add (modeValue);
    modeAndFreq.add (new Integer (highFrequency));

    return modeAndFreq;
  } // end private ArrayList calcModeAndFrequency (ArrayList)


  /**
   * This expands the current prefix candidate. This candidate is just some portion
   *  of the trailing edge of the input stream that we are trying to extend with some
   *  identified pattern. Expansion just means grabbing one more element from the end of the input
   *  stream and making the prefix candidate one element longer. If the pattern candidate
   *  is as long as the input series, the candidate is not modified.
   * @param   prefixCandidate   prefix that we will be expanding.
   *          sequence          The target input stream where we will be retrieving expansion data
   * @requires  prefixCandidate, sequence != null
   * @modifies  prefixCandidate - it has neighboring elements appended to it.
   */
  private void updatePrefixCandidate (ArrayList prefixCandidate, TimeSeries timeSeries)
  {
    int prefixLength = prefixCandidate.size() + 1;
    int seqLength = timeSeries.size ();

    // grab the elemtent in <sequence> that we will be appending onto the prefix and do so
    if (seqLength >= prefixLength)
    {
      TimeSeriesValue tsv = timeSeries.getValueAt (seqLength - prefixLength);
      Double newAddition = new Double ( ((Number) tsv.getValue()).doubleValue());

//      Double newAddition = (Double) sequence.get (seqLength - prefixLength);
      prefixCandidate.add (0, newAddition);
    }
  } // end private void updatePrefixCandidate (ArrayList, TimeSeries)


  /**
   * Determines if we can further expand our pattern prefix that originates
   *  from the trailing edge of the time-series.  If we find a sufficient number of cases of
   *  this prefix candidate we will call this our prefix and this method will signal that we
   *  should continue attempts to expand the candidate further. Thie method signals the search to
   *  stop if the candidate is not supported withing the data stream of if we have already
   *  reached the maximum prefix size that we desire.
   * @param   candidate     The potential prefix candidate that we will be trying to expand
   *          series        The input time-series data stream
   *          occurenceList A list of all the indexes where <candidate> occures in <series>
   *          prefix        The verified pattern prefix that we have identified up to this point
   * @return  1. True -> if <tt> candidate </tt> occures a significant number of times within
   *            <tt> series </tt> and we will thus be attempting to expand it even further. <br>
   *          2. False -> if <tt> candidate </tt> does not occure significantly or if we have reached
   *            the maximum prefix length that we are interested in expanding to.
   * @modifies  Both prefix & occurenceList will be potentially modified.
   * @requires  All parameters must be non-null
   */
  private boolean candidateIsSupported (ArrayList candidate, TimeSeries series,
                                          ArrayList occurenceList, ArrayList prefix)
  {
    boolean ret = false;

    // if first elt. in candiate is null, dont try expanding any further.
    // this is not likely to ever be the case, but is a 'just in case' check.
    if (candidate.get (0) == null)
      return false;

    // find indexes of all occurences of <candidate> in <usage>
    findOccurences (candidate, series, occurenceList);

    df.addText ("Occurences of " + candidate.toString() + " in " + series.toString() + " are "
      + occurenceList.toString() );

    // if enough instances are found, then this prefix candidate can become our bonafide prefix
    if (occurenceList.size() >= this.minOccurenceLevel)
    {
      prefix.add (0, candidate.get (0));

      // if we have reached lower limit on prefix size, quit trying to expand it further
      if (prefix.size () >= this.minPrefixSize )
        ret = false;
      else
        ret = true;
    }
    else // if not enough occurences found, quit trying to expand candidate
    {
      ret = false;
      df.addText ("Not enough occurences found of : " + candidate.toString());
    }
    return ret;
  } // end private boolean candidateIsSupported (ArrayList, ArrayList, ArrayList, ArrayList)


  /**
   * With this method, we find occurences of a particular pattern within the input time
   *  series data. If we have no previous record of a related search recorded in the input
   *  parameter <tt>previousOccurrences</tt>, we will exhaustively locate all occurences of
   *  the sequence of interest in the target data stream. Otherwise, we assume that
   *  <tt>previousOccurrences</tt> contains the results of a previous search for the prefix from
   *  which <tt>event</tt> was extended.  This list of indices is used to very quickly locate
   *  where searches for <tt>event</tt> should commence. If instances of the event of
   *  interest cannot be extended at an index found in <tt>prevOccurences</tt>, it is removed.
   *  When an occurence of <tt>event</tt> is located, we skip all other items in the list of
   *  previous occurences that fall close enough behind the matched event that there would be
   *  overlap between the two instance. Thus, not all instances of <tt>event</tt> are identified,
   *  instead all non-overlapping instances are identified starting from the time series beginning.
   *  After several iterations of this method, <tt>prevOccurences</tt> contains indices where
   *  only valid event patterns that exist with regularity in <tt>sequence</tt>. This is a
   *  highly specialized method that will behave as desired only if used as it was designed.
   *
   * @param   event           The sequence that we are trying to locate within <tt>sequence</tt>
   *          sequence        The target time-series data for our search
   *          prevOccurences  List of indices where previous searches found predecesors of
   *                            <tt>event</tt>, if any exist
   * @return    1. If <tt>event</tt> is not found in <tt>sequence</tt> -> return an empty
   *               ArrayList object <br>
   *            2. otherwise, each element of the returned ArrayList is the index where an occurence of
   *               <tt>event</tt> can be located within <tt>sequence</tt>.
   * @modifies  prevOccurences - potentially removes indices that don't lead to expanded occurences
   */
  private void findOccurences (ArrayList event, TimeSeries series, ArrayList prevOccurences)
  {
    // if either input is null, just return an the input list of occurences
    if (event == null || series == null)
      return;

    int eventSize = event.size();

    // <event> will most likely always be a single value at this point, if no previous occurrences
    //  of <event>, we will exhaustively find them all.
    if (prevOccurences.isEmpty())
    {
      int seqIndex = 0;

      while (seqIndex < series.size())
      {
        if (compareEvent (event, seqIndex, series) )
        {
          prevOccurences.add (new Integer (seqIndex));
          seqIndex += eventSize;
        }
        else
          seqIndex++;
      } // end while (seqIndex < series.size())

    } // end if (prevOccurences.isEmpty()

    // o/w we have previous indices from which to start our search, begin looking for
    // event one element prior to that location.
    else
    {
      ArrayList newIndexList = new ArrayList ();
      int spacing = event.size();

      // cycle through the indices looking to see if <event> is found there
      while (!prevOccurences.isEmpty())
      {
        // get index one element previous to our last search.
        int lastStartIndex = ((Integer) prevOccurences.remove (0)).intValue();
        int searchIndex = lastStartIndex - 1;

        if (searchIndex < 0)
          continue;

        // if <event> is located, add it to temporary list
        if (compareEvent (event, searchIndex, series) )
        {
          newIndexList.add (new Integer (searchIndex));

          // remove other occurences that are closer than the event.size() to this matched event
          boolean addSpacing = false;
          while (addSpacing)
          {
            if (prevOccurences.isEmpty())
              addSpacing = false;
            else
            {
              // if there are still other occurences, make sure they are adequatly separated.
              int nextIndex = ((Integer) prevOccurences.get (0)).intValue();

              if (nextIndex < (lastStartIndex + spacing))
                prevOccurences.remove(0);
              else
                addSpacing =false;
            } // end else
          }
        }// end if (compareEvent(...))

      }// end while

      // copy all new occurences back into original data structure
      prevOccurences.addAll (newIndexList);
    } // end else

    //df.addText ("Number of occurences of " + event.toString() + " in " + sequence.toString() + " is " + list.toString() );
  } // end private ArrayList findOccurences (ArrayList, ArrayList, ArrayList)

  /**
   *  This method takes a list of event occurences and
   *  @modifies  occurenceList
   */
  private void addSpacing (int spacing, ArrayList occurenceList)
  {
    int firstIndex, nextIndex;
    int indexCount = occurenceList.size() - 1;

    boolean addMoreSpacing = true;

    if (occurenceList.isEmpty() || occurenceList.size() <= 2)
      addMoreSpacing = false;

    while (addMoreSpacing)
    {
      firstIndex = ((Integer) occurenceList.get (indexCount)).intValue();
      nextIndex = ((Integer) occurenceList.get (indexCount - 1)).intValue();

      if (firstIndex < (nextIndex + spacing))
      {
        occurenceList.remove(indexCount - 1);
        indexCount--;
      }
      else
        indexCount--;

      if ((indexCount - 1) < 0)// || occurenceList.size() <= 2)
        addMoreSpacing = false;

    } // end while addMoreSpacing
  }// end private void addSpacing (int, ArrayList)


  /**
   * See if <episode> occurs exactly in <tt>sequence</tt> starting at <tt>seqIndex</tt>
   * @parame  event       The sequence we are looking to identify withing <tt>sequence</tt>
   *          seqIndex    The index we will begin searching for <tt>event</tt>
   *          sequence    The target time-series data
   * @return
   *        1. if <event> matches the elements found in <sequence> - > return true. <br>
   *        2. otherwise -> return false.
   * @requires  event && sequence != null && seqIndex < sequence.size() && seqIndex >= 0
   */
  private boolean compareEvent (ArrayList event, int seqIndex, TimeSeries series)
  {
    int eventSize = event.size();
    int seqSize = series.size();
    int eventIndex = 0;
    int matchCount = 0;
    boolean ret = false;
    boolean keepSearching = true;

    // try matching all elements of <event> with those starting at <seqIndex> in <sequence>
    while (keepSearching)
    {
      TimeSeriesValue tsv = series.getValueAt (seqIndex);
      Double valToCompare = new Double ( ((Number) tsv.getValue()).doubleValue());

      // if elts are equal, proceed to comparing next pair of elts.
      if (elementsAreEqual ( (Double) event.get(eventIndex), valToCompare ))
      {
        // increment counters for indexes in each array and the number of matched elts.
        matchCount++;
        eventIndex++;
        seqIndex++;
      }
      else // as soon as we find a non-matching pair, quit searching since sequences are not same
        keepSearching = false;

      // if next query takes either structure beyond its size, stop search
      if ( eventIndex == eventSize || seqIndex == seqSize)
        keepSearching = false;
    } // end while (keepSearching)

    // only if we matched entire episode, do we want to return true
    if (matchCount == eventSize)
      ret = true;

    return ret;
  } //end private boolean compareEpisode (ArrayList, int, Vector)

  /**
   * Determines if two values are equal or at least vary by less than some Epsilon and can
   *  thus be treated as being equal for our purposes. Epsilon is a function of the users
   *  chosen level of sensitivity.
   * @param   firstEltDbl   First values being compared for equality
   *          secondEltDbl  Second values being compared for equality
   * @return  True if values are equal within epsilon bounds, false otherwise
   * @requires  this.epsilon >= 0
   */
  private boolean elementsAreEqual (Double firstEltDbl, Double secondEltDbl)
  {
    if (firstEltDbl == null || secondEltDbl == null)
      return false;

    double firstElt = firstEltDbl.doubleValue();
    double secondElt = secondEltDbl.doubleValue();

    double diff = Math.abs(firstElt - secondElt);

    if (diff <= this.epsilon)
      return true;

    return false;
  } // end private boolean elementsAreEqual (Double, Double)


  public static void main (String args[])
  {
/*
    PatternPredictionAlgorithm alg = new PatternPredictionAlgorithm ("Test");

    DebugFrame testDF = new DebugFrame (true);

    testDF.setTitle("Pattern Prediction Algorithm Test Frame");
    testDF.setBounds(5, 5, 1000, 300);
    testDF.show();
*/
    /***************** Testing of method 'elementsAreEqual (Double, Double)' ******************///////
/*
    testDF.addText ("Testing of : 'boolean elementsAreEqual (Double, Double)'");

    Double double1 = new Double (10);
    Double double2 = new Double (10);
    alg.epsilon = 0;
    testDF.addText ("Element One : " + double1);
    testDF.addText ("Element Two : " + double2);
    testDF.addText ("Epsilon : " + alg.epsilon);

    if (alg.elementsAreEqual (double1, double2))
      testDF.addText ("Pass : elements identified as equal\n");
    else
      testDF.addText ("Fail : elements NOT identified as equal\n");


    double1 = new Double (11);
    testDF.addText ("Element One : " + double1);
    testDF.addText ("Element Two : " + double2);
    testDF.addText ("Epsilon : " + alg.epsilon);

    if (!alg.elementsAreEqual (double1, double2))
      testDF.addText ("Pass : elements identified as different\n");
    else
      testDF.addText ("Fail : elements NOT identified as different\n");


    alg.epsilon = 1.5;
    testDF.addText ("Element One : " + double1);
    testDF.addText ("Element Two : " + double2);
    testDF.addText ("Epsilon : " + alg.epsilon);

    if (alg.elementsAreEqual (double1, double2))
      testDF.addText ("Pass : Elements identified to be within Epsilon of each other\n");
    else
      testDF.addText ("Fail : Elements NOT identified to be within Epsilon of each other\n");
*/
   /*****"Testing of method 'compareEvent (ArrayList, index, ArrayList)'***********///////
/*
    testDF.addText ("Testing of : 'boolean compareEvent (ArrayList event, int index, ArrayList sequence)' ");

    ArrayList event = new ArrayList (3);
    event.add (new Double (1));
    event.add (new Double (2));
    event.add (new Double (3));

    ArrayList sequence = new ArrayList (10);
    sequence.add (new Double (1));
    sequence.add (new Double (2));
    sequence.add (new Double (3));
    sequence.add (new Double (4));
    sequence.add (new Double (1.1));
    sequence.add (new Double (2.1));
    sequence.add (new Double (3.1));
    sequence.add (new Double (4.1));
    sequence.add (new Double (1));
    sequence.add (new Double (2));

    int index = 0;
    alg.epsilon = 0;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is : " + alg.epsilon);
    testDF.addText ("Index to begin search on : " + index);

    if (alg.compareEvent(event, index, sequence))
      testDF.addText ("Pass : Event identified in Sequence starting at specified Index\n");
    else
      testDF.addText ("Fail : Event NOT identified in Sequence starting at specified Index\n");

    index = 1;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is : " + alg.epsilon);
    testDF.addText ("Index to begin search on : " + index);

    if (!alg.compareEvent(event, index, sequence))
      testDF.addText ("Pass : No Event identified in Sequence starting at specified Index\n");
    else
      testDF.addText ("Fail : Event WAS identified in Sequence starting at specified Index\n");


    alg.epsilon = .5;
    index = 4;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is " + alg.epsilon);
    testDF.addText ("Index to begin search on : " + index);

    if (alg.compareEvent(event, index, sequence))
      testDF.addText("Pass : Event identified with Epsilon in Sequence starting at specified Index\n");
    else
      testDF.addText("Fail : Event NOT identified with Epsilon in Sequence starting at specified Index\n");


    index = 8;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is " + alg.epsilon);
    testDF.addText ("Index to begin search on : " + index);

    if (!alg.compareEvent(event, index, sequence))
      testDF.addText ("Pass : Index that carries search beyond Sequence length identified\n");
    else
      testDF.addText ("Fail : Index that carries search beyond Sequence length NOT identified \n");
*/

    /**Testing of 'ArrayList findOccurences (ArrayList event, ArrayList sequence ,ArrayList)' *////
/*
    testDF.addText ("Testing of : 'ArrayList findOccurences " +
                "(ArrayList event, ArrayList sequence, ArrayList prevOccurences)'");

    event = new ArrayList (3);
    event.add (new Double (1));

    sequence = new ArrayList (10);
    sequence.add (new Double (5));
    sequence.add (new Double (1));
    sequence.add (new Double (2));
    sequence.add (new Double (1));
    sequence.add (new Double (2));
    sequence.add (new Double (1.1));
    sequence.add (new Double (5));
    sequence.add (new Double (1));
    sequence.add (new Double (1.05));
    sequence.add (new Double (2));

    ArrayList prevOccur = new ArrayList ();

    alg.epsilon = .5;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is " + alg.epsilon);
    testDF.addText ("Previous Occureces are : " + prevOccur.toString());

    alg.findOccurences (event,sequence, prevOccur);

    testDF.addText ("New Occureces are : " + prevOccur.toString());

    int realIndexList[] = {1, 3, 5, 7, 8};

    if (prevOccur.size() == 5)
    {
      boolean match = true;

      for (int i = 0; i < 5; i++)
      {
        if ( ((Integer) prevOccur.get (i)).intValue() != realIndexList[i])
          match = false;
      }
      if (match)
        testDF.addText ("Pass : All event occurences identified properly \n");
      else
        testDF.addText ("Fail : All event occurences NOT identified properly \n");
    }
    else
      testDF.addText ("Fail : "+ prevOccur.size()+" occuerences of Event found in Sequence instead of 5 \n");

      //////////// no epsilon

    alg.epsilon = 0;
    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is " + alg.epsilon);
    testDF.addText ("Previous Occureces are : " + prevOccur.toString());
    prevOccur = new ArrayList ();

    alg.findOccurences (event,sequence, prevOccur);

    testDF.addText ("New Occureces are : " + prevOccur.toString());
    int realIndexList1[] = {1, 3, 7};

    if (prevOccur.size() == 3)
    {
      boolean match = true;

      for (int i = 0; i < 3; i++)
      {
        if ( ((Integer) prevOccur.get (i)).intValue() != realIndexList1[i])
          match = false;
      }
      if (match)
        testDF.addText ("Pass : Event identified properly \n");
      else
        testDF.addText ("Fail : Event NOT identified properly \n");
    }
    else
      testDF.addText ("Fail : "+prevOccur.size()+" occuerences of Event found in Sequence instead of 2 \n");

      /////////// 2 element sequence to be located
    event.add (0, new Double (5));

    testDF.addText ("Event is: : " + event.toString());
    testDF.addText ("Sequence is : " + sequence.toString());
    testDF.addText ("Epsilon is " + alg.epsilon);
    testDF.addText ("Previous Occureces are : " + prevOccur.toString());

    alg.findOccurences (event,sequence, prevOccur);

    testDF.addText ("New Occureces are : " + prevOccur.toString());
    int realIndexList2[] = {0,6};

    if (prevOccur.size() == 2)
    {
      boolean match = true;

      for (int i = 0; i < 2; i++)
      {
        if ( ((Integer) prevOccur.get (i)).intValue() != realIndexList2[i])
          match = false;
      }
      if (match)
        testDF.addText ("Pass : Event identified properly \n");
      else
        testDF.addText ("Fail : Event NOT identified properly \n");
    }
    else
      testDF.addText ("Fail : "+prevOccur.size()+" occuerences of Event found in Sequence instead of 2 \n");
*/
  /****** Testing of 'boolean candidateIsSupported (ArrayList prefixCandidate, ArrayList usage,
   *                                                ArrayList occurenceList, ArrayList prefix)'**/
/*
  testDF.addText ("Testing :'boolean candidateIsSupported (ArrayList prefixCandidate, ArrayList usage "
                                      + "ArrayList occurenceList, ArrayList prefix)'");

  ArrayList candidate = new ArrayList (3);
  candidate.add (new Double (1));
  candidate.add (new Double (2));
  candidate.add (new Double (3));

  ArrayList prefix = new ArrayList (2);
  prefix.add (new Double (2));
  prefix.add (new Double (3));

  sequence = new ArrayList (15);
  sequence.add (new Double (5));
  sequence.add (new Double (1));
  sequence.add (new Double (2));
  sequence.add (new Double (3));
  sequence.add (new Double (1));
  sequence.add (new Double (5));
  sequence.add (new Double (1));
  sequence.add (new Double (2));
  sequence.add (new Double (3));

  prevOccur = new ArrayList ();
  prevOccur.add (new Integer (2));
  prevOccur.add (new Integer (7));

  alg.minOccurenceLevel = 3;

  testDF.addText ("Candidate is: : " + candidate.toString());
  testDF.addText ("Sequence is : " + sequence.toString());
  testDF.addText ("Previous Occurence is : " + prevOccur.toString());
  testDF.addText ("Prefix is : " + prefix.toString());
  testDF.addText ("MinOccurenceLevel is : " + alg.minOccurenceLevel);

  if (!alg.candidateIsSupported (candidate, sequence, prevOccur, prefix))
    testDF.addText ("Pass : Not enough occurences of Prefix in Sequence to justify expansion \n");
  else
    testDF.addText ("Fail : Incorrect decision to expand since too few cases of Prefix present\n");

    /////////// lower min Occurence threshold

  alg.minOccurenceLevel = 1;

  prevOccur = new ArrayList ();
  prevOccur.add (new Integer (2));
  prevOccur.add (new Integer (7));

  testDF.addText ("Candidate is: : " + candidate.toString());
  testDF.addText ("Sequence is : " + sequence.toString());
  testDF.addText ("Previous Occurence is : " + prevOccur.toString());
  testDF.addText ("Prefix is : " + prefix.toString());
  testDF.addText ("MinOccurenceLevel is : " + alg.minOccurenceLevel);

  if (alg.candidateIsSupported (candidate, sequence, prevOccur, prefix))
  {
    testDF.addText ("New Prefix is : " + prefix.toString());

    int realIndexList3[] = {1,2,3};

      boolean match = true;

      for (int i = 0; i < prefix.size(); i++)
      {
        if ( ((Double) prefix.get (i)).doubleValue() != realIndexList3[i])
          match = false;
      }

      if (match)
        testDF.addText ("Pass : 2 occurences of candidate found in Sequence to justify expansion \n");
      else
        testDF.addText ("Fail : Incorrect expansion of prefix from presented candidate\n");
  }
  else
    testDF.addText ("Fail : expansion candidate not correctly identified as expandable\n");

*/
    /*
  sequence.add (new Double (1));
  sequence.add (new Double (1.2));
  sequence.add (new Double (2.24));
  sequence.add (new Double (2.89));
  sequence.add (new Double (1));
  sequence.add (new Double (2));
  alg.epsilon = .25;
  testDF.addText ("Prefix is: : " + prefix.toString());
  testDF.addText ("Sequence is : " + sequence.toString());
  testDF.addText ("MinOccurenceLevel is : " + alg.minOccurenceLevel);
  testDF.addText ("Epsilon is : " + alg.epsilon);

  if (alg.prefixIsExpandable (prefix, sequence))
    testDF.addText ("Pass : Prefix is found in Sequence enough to justify looking further\n");
  else
    testDF.addText ("Fail : Did NOT identify Prefix expansion possibility in Sequence \n");



  sequence = new ArrayList (15);
  sequence.add (new Double (2));
  sequence.add (new Double (1));
  sequence.add (new Double (3));
  sequence.add (new Double (2));
  sequence.add (new Double (1));
  sequence.add (new Double (3));
  sequence.add (new Double (2));
  sequence.add (new Double (1));
  sequence.add (new Double (3));
  sequence.add (new Double (2));
  sequence.add (new Double (2));

  prefix = new ArrayList (4);
  prefix.add (new Double (2));
  prefix.add (new Double (1));
  prefix.add (new Double (3));
  prefix.add (new Double (2));

  alg.minOccurenceLevel = 3;
  testDF.addText ("Prefix is: : " + prefix.toString());
  testDF.addText ("Sequence is : " + sequence.toString());
  testDF.addText ("MinOccurenceLevel is : " + alg.minOccurenceLevel);

  if (!alg.prefixIsExpandable (prefix, sequence))
    testDF.addText ("Pass : Not enough occurences of Prefix in Sequence to justify expansion \n");
  else
    testDF.addText ("Fail : Incorrect decision to expand since too few cases of Prefix present\n");

*/
  //*** Testing : 'void updatePrefixCandidate (ArrayList prefixCandidate, ArrayList sequence)' ****/
/*  testDF.addText ("Testing : 'void updatePrefixCandidate (ArrayList prefixCandidate, ArrayList sequence)'");

  prefix = new ArrayList (3);
  prefix.add (new Double (1));
  prefix.add (new Double (2));

  sequence = new ArrayList (4);
  sequence.add (new Double (4));
  sequence.add (new Double (6));
  sequence.add (new Double (1));
  sequence.add (new Double (2));

  testDF.addText ("Prefix is: : " + prefix.toString());
  testDF.addText ("Sequence is : " + sequence.toString());

  alg.updatePrefixCandidate (prefix, sequence);

  double elt0 = ( (Double) prefix.get (0)).doubleValue();
  double elt1 = ( (Double) prefix.get (1)).doubleValue();
  double elt2 = ( (Double) prefix.get (2)).doubleValue();

  if (elt0 == 6 || elt1 == 1 || elt2 == 2)
    testDF.addText ("Pass : Prefix updated correctly\n");
  else
    testDF.addText ("Fail : Prefix NOT updated correctly\n");
*/
  /*** Testing : 'ArrayList calcModeAndFrequency (ArrayList data)' ****************/
/*
  testDF.addText ("Testing : 'ArrayList calcModeAndFrequency (ArrayList data)'");

  ArrayList data = new ArrayList (10);
  data.add (new Double (1));
  data.add (new Double (2));
  data.add (new Double (3));

  testDF.addText ("Data is : " + data.toString());

  ArrayList results = alg.calcModeAndFrequency (data);
  double mode = ((Double) results.get (0)).doubleValue();
  int freq = ((Integer) results.get (1)).intValue();

  if (mode == 1 && freq == 1)
    testDF.addText ("Pass : Mode and Frequency correctly identified \n");
  else
    testDF.addText ("Fail : Mode and Frequency NOT identified correctly \n");


  data = new ArrayList (10);
  data.add (new Double (2));
  data.add (new Double (2));
  data.add (new Double (3));
  data.add (new Double (1));
  data.add (new Double (2.24));
  data.add (new Double (1));
  data.add (new Double (1));
  data.add (new Double (2));

  alg.epsilon = .25;
  testDF.addText ("Data is : " + data.toString());
  testDF.addText ("Epsilon is : " + alg.epsilon);

  results = alg.calcModeAndFrequency (data);
  mode = ((Double) results.get (0)).doubleValue();
  freq = ((Integer) results.get (1)).intValue();

  if (mode == 2 && freq == 4)
    testDF.addText ("Pass : Mode and Frequency correctly identified \n");
  else
    testDF.addText ("Fail : Mode and Frequency NOT identified correctly \n");
*/
  /**** Testing : 'PredictedConsumptionData calcPrediction (ArrayList actualUsage)' ****/
/*  testDF.addText ("Testing : 'PredictedConsumptionData calcPrediction (ArrayList actualUsage)'");

  ArrayList usage = new ArrayList (20);
  usage.add (new Double (3));
  usage.add (new Double (1));
  usage.add (new Double (6));
  usage.add (new Double (5));
  usage.add (new Double (3));
  usage.add (new Double (1));
  usage.add (new Double (6));
  usage.add (new Double (5));
  usage.add (new Double (3));
  usage.add (new Double (1));
  usage.add (new Double (6)); // index 10 (11th elt)
  usage.add (new Double (5));
  usage.add (new Double (3));
  usage.add (new Double (1));

  PredictedData pcd = null;

  try{
    Double lastElt = (Double) usage.get (usage.size() - 1);

    ArrayList prefixCandidate = new ArrayList (10);
    prefixCandidate.add (lastElt);
    alg.prefix = new ArrayList (10);

    testDF.addText ("PrefixCandidate : " + prefixCandidate.toString());
    testDF.addText ("Validated Prefix is : " + alg.prefix.toString());

    while (alg.candidateIsSupported (prefixCandidate, usage))
    {
      alg.updatePrefixCandidate (prefixCandidate, usage);
      testDF.addText ("PrefixCandidate : " + prefixCandidate.toString());
      testDF.addText ("Validated Prefix is : " + alg.prefix.toString());
    }

    testDF.addText ("Usage is : " + usage.toString());
    testDF.addText ("Prefix is : " + alg.prefix.toString());
    testDF.addText ("Occurence List is : " + alg.occurenceList.toString());

  }
  catch (Exception e)
  {
  testDF.addText ("Caugth Exception ");
  e.printStackTrace ();
  testDF.addText (e.getMessage());
  }

  if (pcd != null)
    testDF.addText (pcd.toString());
  else
    testDF.addText ("Fail : No Prediction Made.");


*/
  /**** Testing : 'void initialize (PredictionCriteria, long currTime)' ****/
/*
  testDF.addText ("Testing : 'void initialize (PredictionCriteria, long currTime)'");
  alg.resetLevels();
  PredictionCriteria pc = new PredictionCriteria ("assetNSN", "clusterName");
  pc.setSensitivityLevel ("VeryHigh");

  alg.initialize (pc, 0);
  double deviation = pc.getSensitivityDeviation();

  testDF.addText ("Min Confidence : " + alg.minConfidence);
  testDF.addText ("Devaiation : " + deviation);
  testDF.addText ("Min Pattern Length : " + alg.minPatternLength);
  testDF.addText ("Min Occurence Level : " + alg.minOccurenceLevel);
  testDF.addText ("Min Prefix Size : " + alg.minPrefixSize);
  testDF.addText ("Epsilon : " + alg.epsilon);

*/
  }// end main


}
