package sam;

import sam.society;
import sam.world;
import sam.cluster;
import sam.plugin;
import sam.community;
import sam.world;
import sam.MainWindow;
import sam.LoadContractFiles.systemContracts;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import sam.LoadContractFiles.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import org.w3c.dom.*;
/**
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author: Sridevi Salagrama
 * @version 1.0
 */

public class societyXmlLoader
{
   private BContentHandler myContentHandler = new BContentHandler();
   public contractOwnerBase cob;

     //creating a vector to store the communities and the cluster objects
    //A Society can have clusters and communities.
    Vector society_children = new Vector();

    //creating a new vector to hold the communities and the cluster objects
    //A community can hold clusters and also communities.
    Vector community_children = new Vector();

    //creating a vector to hold the info for the plugins that have been created.
    Vector pluginVector;

    //creating input and output bars
    inputBar theInputBar;
    outputBar theOutputBar;

   public society loadXml(File xmlFile, String contractFileName,boolean debug, boolean verbose)
   {
      FileReader freader = null;
      try
      {
         freader = new FileReader( xmlFile );

         if( debug )
            System.out.println("\nStarting to read the xml file to load the society " + xmlFile.getAbsolutePath() );
      }
      catch(java.io.FileNotFoundException jfne)
      {
         System.err.println("XML society file not found: " + xmlFile.toString());
      }

      //create the xml parser
      SAXParser sax_p = new SAXParser();

      //try to activate validation
      try
      {
         sax_p.setFeature("http://xml.org/sax/features/validation" , true);
      }
      catch (SAXException e)
      {
         System.err.println(" Cannot activate validation.");
      }

      //register event handlers
      sax_p.setContentHandler(myContentHandler);
      sax_p.setErrorHandler( new BErrorHandler());

      //parse the document
      try
      {
         InputSource in = new InputSource(freader);
         sax_p.parse(in);
      }
      catch(IOException e)
      {
         System.out.println( "XMl Exception reading the file " + e);
      }
      catch (SAXException se)
      {
         System.err.println( "xml exception reading society's xml file " + se);
         se.printStackTrace();
      }

      //now walk through the xml file and load the society
      OperatorFactory opFactory = OperatorFactory.getInstance();

      //should be 1 root element:<society>
      int numroots = myContentHandler.getRootElements().size();
      if(numroots !=1)
      {
         System.err.println( "There must be one root level key word: <society> you have " + numroots);
         return null;
      }

      Iterator it = myContentHandler.getRootElements().iterator();
      if(! it.hasNext() )
      {
         System.out.println( "Internal Error: There appears to be  " + numroots + " root level keywords, but the iterator failed ");
         return null;
      }

      //get the root element
      BElement rootElement = (BElement)it.next();


      //validate the top level
      if( !rootElement.getLocalName().equalsIgnoreCase("society"))
      {
         System.out.println( "The root level keyword must be <society>, not " + rootElement.getLocalName());
         return null;
      }

      if (debug)
      System.out.println( "The file contains xml for " + rootElement.getNumberChildren() + "objects");

       //get the name
      String name = rootElement.getProperty("name");

      //creating a new society
      // now load  the current element's (which should be the society) xml
      society new_society = (society)loadXmlForSociety(rootElement, MainWindow.theMainWindow.theWorld, debug);

      // Remember the file name.
      new_society.tabbedpane_filename = xmlFile.toString();

      int tab = MainWindow.theMainWindow.theWorld.attachSociety(new_society,new_society.getName());
      if(tab < 0)
      {
         System.err.println("societyxmlloader::loadxml returned illegal tab:");
      }

      //-----------------Load the contracts from the xml files---------------------//
      File contractFile = new File( xmlFile.getParent(), contractFileName);

      //create the contract object
      systemContracts tmpContracts = new systemContracts();

      //now try to load the contracts
      if( !tmpContracts.loadFile( contractFile,debug ))
      {
         System.err.println("Unable to Load the Contracts file" + contractFile.toString());
         return null;
      }

      //set the contracts for this society
      new_society.setSystemContract(tmpContracts);

      //-----------now walk the society and add the pointers to the contracts------------------------//
      new_society.attachContracts(tmpContracts , debug);

      //walk the society and compute the dependencies based on the contracts
      new_society.computeDependencies(null, debug);

      //now print the dependencies
      if(MainWindow.theMainWindow.properties.getVerbose())
      {
         MainWindow.theMainWindow.theSociety.printDependencies();
      }

      MainWindow.theMainWindow.activateTab(tab);

      //to reload the jtree
      MainWindow.theMainWindow.theSociety.theTreeDisplay.reload();

      return new_society;

   } //load society xml ends

////////////////////////////////////Method that loads the society's XML //////////////////////////

   contractOwnerBase loadXmlForSociety( BElement element , contractOwnerBase parent, boolean debug)
   {
      //trying to get the name of the element
      String name = element.getProperty("name");

      //creating a new society object
      society new_society = new society(name , parent);

      // first need to get the stuff for the subelements
      Iterator it3 = element.getMySubElements().iterator();
      while(it3.hasNext())
      {
         BElement curElement = (BElement)it3.next();

         if(curElement.getLocalName().equalsIgnoreCase( "community" ))
         {
            //parse the xml for the community and add the new community to the vector
           society_children.add(loadXmlForCommunity(curElement, new_society , debug));

         }
         else if(curElement.getLocalName().equalsIgnoreCase( "cluster" ))
         {
          //parse the xml for the cluster and add the new cluster to the vector.
          society_children.add(loadXmlForCluster(curElement, new_society, debug));

         }
         else
         {
            //print our an error message
            System.err.println("Error Message:: unable to parse the xml for the society.");
         }
      }

      // Create the input and output bars and add them as fake plugins.
      // Give them our name, since they want our contracts.
      theInputBar = new inputBar(name, new_society);

      //add the input bar to the vector that holds the children of the society
      society_children.add(theInputBar);

      //creating the output bar
      theOutputBar = new outputBar(name, new_society);

      //add the output bar to the vector that holds the children of the society
      society_children.add(theOutputBar);

      //make sure to add the vector which holds the socities children i.e., to the new society object
      new_society.setChildren(society_children);

      return new_society;


   }
////////////////////////////Method that loads the community's XML //////////////////////////////////

   contractOwnerBase loadXmlForCommunity(BElement element, contractOwnerBase parent, boolean debug)
   {
      //trying to get the name of the element
      String name = element.getProperty("name");

      //create a new object for the community
      community newCommunity = new community( name, parent);

      Iterator community_iterator = element.getMySubElements().iterator();
      while( community_iterator.hasNext() )
      {
         BElement curElement = (BElement)community_iterator.next();
         if(curElement.getLocalName().equalsIgnoreCase( "community" ))
         {
            //parse the xml for the community
            //this is recursive
            community_children.add(loadXmlForCommunity(curElement, newCommunity, debug));
         }
         else if(curElement.getLocalName().equalsIgnoreCase( "cluster" ))
         {
            //parse the xml for the cluster
            community_children.add(loadXmlForCluster(curElement, newCommunity, debug));
         }
         else
         {
            System.err.println( "Error Message:: Unable to parse the xml for the community");
            return null;
         }

      }
      // Create the input and output bars and add them as fake plugins.
      // Give them our name, since they want our contracts.
      theInputBar = new inputBar(name, newCommunity);

      //add the input bar to the vector that holds the children of the community
      community_children.add(theInputBar);

      //create the output bar
      theOutputBar = new outputBar(name, newCommunity);

      //add the output bar to the vecotr that holds the children of the community
      community_children.add(theOutputBar);

      newCommunity.setChildren(community_children);

      return newCommunity;
   }

   contractOwnerBase loadXmlForCluster(BElement element, contractOwnerBase parent, boolean debug)
   {

      //trying to get the name of the element
      String name = element.getProperty("name");

      //create a new object for the cluster
      cluster newCluster = new cluster( name, parent);

      //create  a new vecotr to hold the plugins
      pluginVector = new Vector();

      Iterator cluster_iterator = element.getMySubElements().iterator();
      while ( cluster_iterator.hasNext() )
      {
         BElement curElement = (BElement) cluster_iterator.next();

         if( curElement.getLocalName().equalsIgnoreCase( "plugin"))
         {
            //parse xml for the plugin, while adding it to the vector.
            pluginVector.add(loadXmlForPlugin( curElement, newCluster , debug) );
         }
         else
         {
            //print an error message
            System.out.println( "Error Message:: Unable to parse the xml for the cluster");
         }

      }
      // Create the input and output bars and add them as fake plugins.
      // Give them our name, since they want our contracts.
      theInputBar = new inputBar(name, newCluster);

      //add the input bar to the vector
      pluginVector.add(theInputBar);

      //create  a output bar object
      theOutputBar = new outputBar(name, newCluster);

      //add the output bar to the vector.
      pluginVector.add(theOutputBar);

      //setting the cluster object with the vector
      newCluster.setChildren(pluginVector);

      return newCluster;
   }


   contractOwnerBase loadXmlForPlugin( BElement element , contractOwnerBase parent, boolean debug)
   {
      //get the name of the plugin that is being currently loaded.
      String name = element.getProperty("name");

      //parse the xml for the plugin
      plugin newPlugin = new plugin(name, parent);

      return newPlugin;
   }



} //class society xml loader ends