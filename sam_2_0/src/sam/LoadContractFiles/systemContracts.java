package sam.LoadContractFiles;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import sam.cluster;
//import alp.util.SetRelationship;

import java.util.*;
import java.io.*;
/**
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Doug MacKenzie
 * @version 1.0
 */
public class systemContracts
{
   // Holds the list of "String:setOfContractOperators" records.
   HashMap thePluginContracts;

   // Holds the list of "String:setOfContractOperators" records.
   HashMap theClusterContracts;

   // Holds the list of "String:setOfContractOperators" records.
   HashMap theCommunityContracts;

   // Holds the list of "String:setOfContractOperators" records.
   HashMap theSocietyContracts;

   // Construct the object.
   public systemContracts()
   {
      wipe();
   }


   /// Free the existing contracts and init to an empty list.
   public void wipe()
   {
      // Free the old records so they will get garbage collected.
      if( thePluginContracts != null )
         thePluginContracts.clear();
      else
         thePluginContracts = new HashMap();

      if( theClusterContracts != null )
         theClusterContracts.clear();
      else
         theClusterContracts = new HashMap();

      if( theSocietyContracts != null )
         theSocietyContracts.clear();
      else
         theSocietyContracts = new HashMap();

      if(theCommunityContracts !=null)
         theCommunityContracts.clear();
      else
         theCommunityContracts = new HashMap();
   }


   // Load the contracts from an XML file.
   // Returns true on success, false on failure.
   public boolean loadFile(File contractFile, boolean debug)
   {
      // *************** load the plugin contracts file ****************

      // Create the loader
      PluginContractLoader loader = new PluginContractLoader();

      // Load it.
      boolean rtn = loader.loadContracts(contractFile, thePluginContracts, theClusterContracts, theCommunityContracts, theSocietyContracts, debug);


      // Report the results for debugging.
      if( debug )
      {
         System.out.println("------------------- Plugin Contracts ------------------\n");

         Set s = thePluginContracts.entrySet();
         Iterator mapit = s.iterator();
         while( mapit.hasNext() )
         {
            Map.Entry e = (Map.Entry)mapit.next();
            String key = (String)e.getKey();
            setOfContractOperators pc = (setOfContractOperators)e.getValue();


             System.out.println("\nPlugin " + key + " has publish contracts:");

            Iterator cit = pc.publish.iterator();
            while( cit.hasNext() )
            {
               Operator op = (Operator)cit.next();
               System.out.println("      " + op.toString() + "\n");
            }

            System.out.println("\n   Subscribe contracts:");

            cit = pc.subscribe.iterator();
            while( cit.hasNext() )
            {
               Operator op = (Operator)cit.next();
               System.out.println("      " + op.toString() + "\n");
            }
         }

         System.out.println("------------------- End ------------------\n");
      }
      return rtn;
   }

   //***********************************************Method to write /save the contracts xml*******************************//
   public boolean saveContracts (String fileName, boolean debug)
   {
      //writing/saving the contracts for the society
      //printing the debug message
      if (debug)
      {
         System.out.println( "Printing the contracts::systemContracts to the file :" + fileName);
      }

      try
      {
         FileOutputStream fo = new FileOutputStream(fileName);
         PrintWriter pw = new PrintWriter(fo);

         //print out the first line that you want.
         pw.println("<contracts>");

         //looping through the plugin contracts hash map
         //writing out the plugin contracts
         Set plugins = thePluginContracts.entrySet();
         Iterator mapit = plugins.iterator();
         while (mapit.hasNext())
         {
            //this will get the collection of the key-vlaue pairs
            Map.Entry e = (Map.Entry) mapit.next();

            //get the value of the key in this case it is a string which holds the names of the plugins
            String key  = (String)e.getKey();

            //now get value of that particular plugin
            setOfContractOperators pc = (setOfContractOperators) e.getValue();

            //write down the openinig tag of the plugin's xml
            pw.println("  <plugin name="+"\""+key+"\""+">");

            //now write the publish and the subscribe contracts
            pc.writeOutContracts(pw,debug);

            //now write out the closing tag for the plugin
            pw.println("  </plugin>");
         }

         //looping through the cluster contracts hash map
         //writitng out the cluster contracts
         Set clusters = theClusterContracts.entrySet();
         Iterator map_clusterit = clusters.iterator();
         while (map_clusterit.hasNext())
         {
            //this will get the collection of the key-value pairs
            Map.Entry e = (Map.Entry) map_clusterit.next();

            //get the value of the key in this case it is a string which holds the names of the clusters
            String key  = (String)e.getKey();


            //now get the value of that particular cluster
            setOfContractOperators pc = (setOfContractOperators) e.getValue();

            //write down the opening tag of the cluster's xml
            pw.println("  <cluster name="+"\""+key+"\""+">");

            //now write down the publish and the subscribe contracts
            pc.writeOutContracts(pw,debug);

            //now write down the closing tag for the cluster
            pw.println("  </cluster>");
         }

         //looping through the the community contracts hash map
         //writitng the community contracts
         Set communities = theCommunityContracts.entrySet();
         Iterator map_communityit = communities.iterator();
         while (map_communityit.hasNext())
         {
            //this will get the collection of the key-value pairs
            Map.Entry e = (Map.Entry) map_communityit.next();

            //get the value of the key in this case it is a string which holds the names of the clusters
            String key  = (String)e.getKey();

            //now get the value of that particular community
            setOfContractOperators pc = (setOfContractOperators) e.getValue();

            //write the opening tag fot the community's xml
            pw.println("  <community name="+"\""+key+"\""+">");

            //now write down the publish and the subscribe contracts
            pc.writeOutContracts(pw,debug);

            //write down the closing tag for the comunity
            pw.println("  </community>");
         }

         //loop through the society's contracts hash map
         //writitng the society contracts
         Set societies = theSocietyContracts.entrySet();
         Iterator map_societyit = societies.iterator();
         while (map_societyit.hasNext())
         {
             //this will get the collection of the key-value pairs
            Map.Entry e = (Map.Entry) map_societyit.next();

            //get the value of the key in this case it is a string which holds the names of the societies
            String key  = (String)e.getKey();

            //now get the value of that particular community
            setOfContractOperators pc = (setOfContractOperators) e.getValue();

            //write the opening tag fot the society's xml
            pw.println("  <society name="+"\""+key+"\""+">");

            //now write down the publish and the subscribe contracts
            pc.writeOutContracts(pw,debug);

            //write down the closing tag for the society
            pw.println("  </society>");
         }
         //write out the closing tag for the contracts
         pw.println("</contracts>");

         //to write out every thing that there to write out
         pw.flush();

         //close the print writer
         pw.close();

      }
      catch (Exception e)
      {
         System.out.println( "Error:: occured during the writitng out of the contracts::systemcontracts");
         e.printStackTrace();
      }

      return true;
   }

   //******************************************************
   // Get the contract record for this plugin
   // returns the contract record or null, if not found.
   public setOfContractOperators getPluginContract(final String name)
   {
      return (setOfContractOperators)thePluginContracts.get(name);
   }

   //******************************************************
   // Get the contract record for this cluster
   // returns the contract record or null, if not found.
   public setOfContractOperators getClusterContract(final String name)
   {
      return (setOfContractOperators)theClusterContracts.get(name);
   }

   //******************************************************
   // Get the contract record for this community
   // returns the contract record or null, if not found.
   public setOfContractOperators getCommunityContract(final String name)
   {
      return (setOfContractOperators)theCommunityContracts.get(name);
   }

   //******************************************************
   // Get the contract record for this society
   // returns the contract record or null, if not found.
   public setOfContractOperators getSocietyContract(final String name)
   {
      return (setOfContractOperators)theSocietyContracts.get(name);
   }

   //******************************************************
   // Get an array of strings that has only the names(keys) of all the plugins
   // that we have contracts for.
   public String[] getListOfPlugins()
   {
      Set thekeys = thePluginContracts.keySet();
      String [] pluginNames = new String[thekeys.size()];

      return (String [])thekeys.toArray( pluginNames );
   }
   //***********************************************************************
    // Get an array of strings that has only the names(keys) of all the clusters
   // that we have contracts for.
   public String[] getListOfClusters()
   {
      Set theKeys = theClusterContracts.keySet();
      String [] clusterNames = new String[theKeys.size()];
      return (String [])theKeys.toArray(clusterNames);
   }
    // Get an array of strings that has only the names(keys) of all the Communities
   // that we have contracts for.
   public String[] getListOfCommunities()
   {
      Set theKeys = theCommunityContracts.keySet();
      String [] communityNames = new String[theKeys.size()];
      return (String [])theKeys.toArray(communityNames);
   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // add the contract record for this plugin
   public boolean addPluginContract(final String name, Operator theNewOperator, boolean isPublish)
   {
      setOfContractOperators newContractOperators =  (setOfContractOperators)thePluginContracts.get(name);

      //return false if the name was not found
      if(newContractOperators == null)
         return false;


      if(isPublish)
      {
        //adding the new contract to the vector
         newContractOperators.addPublishContract(theNewOperator);
      }
      else
      {
        newContractOperators.addSubscribeContract(theNewOperator);
      }
      return true;

   }

   //******************************************************
   // Add the contract record for this cluster
   public boolean addClusterContract(final String name, Operator theNewOperator, boolean isPublish)
   {
       setOfContractOperators newContractOperators =   (setOfContractOperators)theClusterContracts.get(name);

      //return false if the name was not found
      if(newContractOperators == null)
         return false;

       if(isPublish)
      {
        //adding the new contract to the vector
         newContractOperators.addPublishContract(theNewOperator);
      }
      else
      {
        newContractOperators.addSubscribeContract(theNewOperator);
      }
      return true;
   }

   //******************************************************
   // Add the contract record for this community
   public boolean addCommunityContract(final String name, Operator theNewOperator, boolean isPublish)
   {
      setOfContractOperators newContractOperators =  (setOfContractOperators)theCommunityContracts.get(name);

      //return false if the name was not found
      if(newContractOperators == null)
         return false;

      if(isPublish)
      {
        //adding the new contract to the vector
         newContractOperators.addPublishContract(theNewOperator);
      }
      else
      {
        newContractOperators.addSubscribeContract(theNewOperator);
      }

      return true;
   }

   //******************************************************
   // Add the contract record for this society
   public boolean addSocietyContract(final String name, Operator theNewOperator, boolean isPublish)
   {
       setOfContractOperators newContractOperators =   (setOfContractOperators)theSocietyContracts.get(name);

      //return false if the name was not found
      if(newContractOperators == null)
         return false;

      if(isPublish)
      {
        //adding the new contract to the vector
         newContractOperators.addPublishContract(theNewOperator);
      }
      else
      {
        newContractOperators.addSubscribeContract(theNewOperator);
      }
      return true;
   }

   //******************************************************//

   //Add contracts to toally new objects
   public boolean addNewClusterContracts(final String name)
   {
      //Initialyzing the setOfContractOperators.
      setOfContractOperators newContractOperators = new setOfContractOperators();

      //now add this new contracts to the hash map
      theClusterContracts.put((Object)name, newContractOperators);

      //return true on success;
      return true;

   }

   //Add contracts to toally new objects
   public boolean addNewCommunityContracts(final String name)
   {
      setOfContractOperators newContractOperators = new setOfContractOperators();

      //now add this new contracts to the hash map
      theCommunityContracts.put((Object)name, newContractOperators);
      return true;

   }

}
