package sam.graphPlanner.test;

import sam.graphPlanner.graphLink;
import sam.graphPlanner.graphNode;
import sam.graphPlanner.graphPlanner;
import sam.LoadContractFiles.contractOwnerBase;
import sam.society;
import sam.cluster;
import sam.plugin;
import sam.world;
import sam.MainWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
 * @author Doug MacKenzie
 * @version 1.0
 */

public class test
{
   String path = ".";

   public test(final String dir)
   {
      if( dir != null )
         path = dir;
   }

   public static void main(String[] args)
   {
      String dir = null;
      if( args.length >= 1 )
         dir = args[0];

      test rec = new test(dir);

      // Create a main window, since pretty heavily coupled.
      MainWindow junk = new MainWindow(".");

      ///////////////////////
      System.err.println("Starting Test 1");
      if( rec.test1() )
         System.err.println("Test passed");
      else
         System.err.println("Test failed");
      System.err.println("");

      ///////////////////////
      System.err.println("Starting Test 2");
      if( rec.test2() )
         System.err.println("Test passed");
      else
         System.err.println("Test failed");
      System.err.println("");

      System.exit(0);
   }

   // test:
   // A cluster that is complete.
   // returns true on success, false on error.
   private boolean test1()
   {
      boolean ok = true;

      // Create the initial conditions.
      world theWorld = new world("Test 1");
      File file = new File(path + "/test1/" + "test.soc");

      society theSociety = theWorld.loadSocietyFromIniFiles(file, "contracts.xml", false, false, false);
      if( theSociety == null )
      {
         System.err.println("Error: Unable to load test.soc and contracts.xml");
         ok = false;
      }

      ArrayList results = new ArrayList();
      contractOwnerBase theCluster = null;
      if( ok )
      {
         theCluster = theSociety.getChildByName("test");
         if( theCluster == null )
         {
            System.err.println("Test 1 error: Didn't find cluster test");
            ok = false;
         }
      }

      if( ok )
      {
         // Create the planner
         graphPlanner p = new graphPlanner(true);

         // Run it.
         ArrayList availableNodes = new ArrayList();
         String names[] = theSociety.getAvailablePlugins();
         for(int i=0; i<names.length; i++)
         {
            availableNodes.add( new plugin(names[i], theCluster) );
         }

         boolean rtn = p.plan(theCluster, availableNodes, results);

         // Examine the results.
         if( !rtn )
         {
            System.err.println("Test 1 error: Didn't find a complete solution");
            ok = false;
         }
      }

      if( ok )
      {
         int i=0;
         Iterator it = results.iterator();
         while( it.hasNext() )
         {
            Collection solution = (Collection)it.next();

            System.err.println("Solution " + ++i + " contains " + solution.size() + " nodes");

            Iterator nodeIT = solution.iterator();
            while( nodeIT.hasNext() )
            {
               graphNode node = (graphNode)nodeIT.next();

               System.err.println("   Node " + node);
            }
         }

         // Should be 1 empty solution.
         if( results.size() != 1 || ((Collection)results.iterator().next()).size() != 0 )
            ok = false;
      }

      return ok;
   }

   // test:
   // A cluster with one missing plugin.
   // returns true on success, false on error.
   private boolean test2()
   {
      boolean ok = true;

      // Create the initial conditions.
      world theWorld = new world("Test 2");
      File file = new File(path + "/test2/" + "test.soc");

      society theSociety = theWorld.loadSocietyFromIniFiles(file, "contracts.xml", false, false, false);
      if( theSociety == null )
      {
         System.err.println("Error: Unable to load test.soc and contracts.xml");
         ok = false;
      }

      ArrayList results = new ArrayList();
      contractOwnerBase theCluster = null;
      if( ok )
      {
         theCluster = theSociety.getChildByName("test");
         if( theCluster == null )
         {
            System.err.println("Test 2 error: Didn't find cluster test");
            ok = false;
         }
      }

      if( ok )
      {
         // Create the planner
         graphPlanner p = new graphPlanner(true);

         // Run it.
         ArrayList availableNodes = new ArrayList();
         String names[] = theSociety.getAvailablePlugins();
         for(int i=0; i<names.length; i++)
         {
            plugin plug = new plugin(names[i], theCluster);
            plug.attachContracts( theWorld.getSystemContract(), false );
            availableNodes.add( plug );
         }

         boolean rtn = p.plan(theCluster, availableNodes, results);

         // Examine the results.
         if( !rtn )
         {
            System.err.println("Test 2 error: Didn't find a complete solution");
            ok = false;
         }
      }

      if( ok )
      {
         int i=0;
         Iterator it = results.iterator();
         while( it.hasNext() )
         {
            Collection solution = (Collection)it.next();

            System.err.println("Solution " + ++i + " contains " + solution.size() + " nodes");

            Iterator nodeIT = solution.iterator();
            while( nodeIT.hasNext() )
            {
               graphNode node = (graphNode)nodeIT.next();

               System.err.println("   Node " + node);
            }
         }
      }

      return ok;
   }
}