

package sam.display;

import diva.graph.layout.GlobalLayout;
import diva.graph.layout.LayoutTarget;
import diva.graph.layout.LayoutUtilities;
import diva.graph.model.*;
import java.awt.geom.*;
import java.util.*;
import diva.util.*;

/**
 * A level-based  layout algorithm originally implemented
 * by Ulfar Erlingsson at Cornell/RPI and modified to fit into
 * this system.<p>
 *
 * The algorithm is structured in the following way:
 *
 * <ul>
 *
 *   <li> Copy the original graph.  The copy will be augmented with
 *        dummy nodes, edges, etc.  The method which performs the copy
 *        can be overridden in a subclass so that the the layout can
 *        be customized.  For example, one might wish to create dummy
 *        edges from a composite node, which represent edges from
 *        subnodes of the composite node to subnodes in other
 *        composite nodes in the graph (e.g.  if you are laying out a
 *        circuit schematic, with composite nodes representing
 *        components and subnodes representing pins on the
 *        components).
 *
 *   <li> Perform the levelizing layout on the graph copy.  This
 *        process consists of several steps:
 *        <ul>
 *            <li> Calculate the levels of the nodes
 *                 in the graph.
 *            <li> Add dummy nodes on edges which span
 *                 multiple levels in the graph.
 *            <li> Perform a sorting on each level
 *                 in the graph based on some cost
 *                 function (not yet implemented).
 *            <li> Assign a position based on the level
 *                 and sorting order of the node.
 *        </ul>
 *
 *   <li> Copy the layout results from the graph copy back into the
 *        original graph, ignoring dummy nodes.  This should also be
 *        overridden if the copy process was overridden (described above).
 *
 * </ul>
 *
 * TODO:
 * <ul>
 *   <li> Break cycles in the graph.
 *   <li> Implement barycentric layout (currently commented out).
 * </ul>
 *
 * @author Michael Shilman (michaels@eecs.berkeley.edu)
 * @version $Revision: 1.1 $
 * @rating Red
 * $Id: samLayout.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 */

public class samLayout implements GlobalLayout {
    /**
     * Layout the graph in levels from top to bottom.
     *
     * @see setOrientation(int)
     */
    public static final int VERTICAL = 0;

    /**
     * Layout the graph in levels from left to right.
     *
     * @see setOrientation(int)
     */
    public static final int HORIZONTAL = 1;

    /**
     * Keep track of the orientation; vertical by
     * default.
     */
    private int _orientation = VERTICAL;

    /**
     * The layout target that is passed in by the user and
     * used to assign the layout to the view.
     */
    private LayoutTarget _target;

    /**
     * The original graph that is passed in by the user on
     * which the layout is eventually being assigned.
     */
    private Graph _origGraph;

    /**
     * The local graph copy of the user's graph, to which
     * dummy nodes/edges are added, and on which the actual
     * layout is first performed before these values are
     * copied back into the user's graph.
     */
    private Graph _copyGraph;

    /**
     * A variable that is used to keep track of the maximum
     * level in the graph, starting at -1 and then incremented
     * as the level-finding algorithm is applied.
     */
    private int _maxLevel = -1;

    /**
     * A simple data structure to keep track of the levels.
     * This is an array of array lists.  Each array list represents
     * a level in the graph and contains references to nodes in
     * the graph.
     */
    private ArrayList _levels[] = null;

    /**
     * A meta-node which is a dummy node that is added to the graph
     * with edges to every other node in the graph in order to make it
     * easier to perform a topological sort.
     */
    private Node _meta = null;

    /**
     * The graph implementation of the graph copy, for adding
     * dummy nodes/edges to the graph copy.
     */
    private GraphImpl _impl = null;

    /**
     * Construct a new levelizing layout with a vertical orientation.
     */
    public samLayout() {
        this(new BasicGraphImpl());
    }

    /**
     * Construct a new levelizing layout with a vertical orientation
     * which uses the given graph implementation on which to perform
     * its layout, create dummy nodes, etc.
     */
    public samLayout(GraphImpl impl) {
        _impl = impl;
    }

    /**
     * Copy the given graph and make the nodes/edges in the copied
     * graph point to the nodes/edges in the original.
     */
    protected Graph copyGraph(Graph origGraph, LayoutTarget target) {
        Graph copyGraph = _impl.createGraph(null);
        Hashtable map = new Hashtable();

        for(Iterator i = origGraph.nodes(); i.hasNext(); ) {
            Node origNode = (Node)i.next();
            if(target.isNodeVisible(origNode)) {
                Rectangle2D r = target.getBounds(origNode);
                LevelInfo inf = new LevelInfo();
                inf.origNode = origNode;
                inf.x = r.getX();
                inf.y = r.getY();
                inf.width = r.getWidth();
                inf.height = r.getHeight();
                Node copyNode = _impl.createNode(inf);
                _impl.addNode(copyNode, copyGraph);
                map.put(origNode, copyNode);
            }
        }

        for(Iterator i = origGraph.nodes(); i.hasNext(); ) {
            Node origTail = (Node)i.next();
            for(Iterator j = origTail.outEdges(); j.hasNext(); ) {
                Edge origEdge = (Edge)j.next();
                Node origHead = origEdge.getHead();

                if(origHead != null) {
                    Node copyTail = (Node)map.get(origTail);
                    Node copyHead = (Node)map.get(origHead);
                    if(copyHead != null && copyTail != null) {
                        Edge copyEdge = _impl.createEdge(origEdge);
                        _impl.setEdgeTail(copyEdge, copyTail);
                        _impl.setEdgeHead(copyEdge, copyHead);
                    }
                }
            }
        }
        return copyGraph;
    }


    /**
     * Take the layout generated by the core layout algorithm and copy
     * it back into the view of the original graph passed in by the
     * user.
     */
    protected void copyLayout(Graph copyGraph, LayoutTarget target)
    {
        for(Iterator ns = copyGraph.nodes(); ns.hasNext(); )
        {
            Node copyNode = (Node)ns.next();
            LevelInfo inf = getLevelInfo(copyNode);
            ASSERT(inf != null, "null inf");

            if(inf.origNode != null)
            {
                Rectangle2D r = target.getBounds(inf.origNode);
                ASSERT(r != null, "null rect");

                double dx = inf.x-r.getX();
                double dy = inf.y-r.getY();
                target.translate(inf.origNode, dx, dy);

                for(Iterator i = inf.origNode.inEdges(); i.hasNext(); )
                {
                    Edge e = (Edge)i.next();
                    if(target.isEdgeVisible(e)) {
                        target.route(e);
                    }
                }

                for(Iterator i = inf.origNode.outEdges(); i.hasNext(); )
                {
                    Edge e = (Edge)i.next();
                    if(target.isEdgeVisible(e)) {
                        target.route(e);
                    }
                }
            }
        }
    }


    /**
     * Return the orientation in which the graph is to be layed out,
     * either VERTICAL or HORIZONTAL.
     */
    public int getOrientation() {
        return _orientation;
    }

    /**
     * Perform the levelizing layout on the given graph in the given
     * target environment.  This operation only works on acyclic
     * graphs (for the moment) and will throw an
     * IllegalArgumentException if the given graph is cyclic.  It
     * operates on a copy of the graph and then copies the layout
     * results back into the original view (the given layout target).
     */
    public void layout(LayoutTarget t, Graph g) {
        _origGraph = g;
        _target = t;

	if(g.getNodeCount() > 0) {
            _copyGraph = copyGraph(g, t);

//              if(isCyclic(_copyGraph)) {
//                  String err = "Unable to perform levelizing layout on cyclic graphs";
//                  throw new IllegalArgumentException(err);
//              }
            breakCycles(_copyGraph);
            doLayout();
            copyLayout(_copyGraph, t);

            cleanupStructures();
        }

    }

    /**
     * Set the orientation in which the graph is to be layed out,
     * either VERTICAL or HORIZONTAL.
     */
    public void setOrientation(int o) {
        if((o != VERTICAL) && (o != HORIZONTAL)) {
            String err = "Orientation must be either VERTICAL or HORIZONTAL";
            throw new IllegalArgumentException(err);
        }
        _orientation = o;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Private methods follow
    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * Assert the given condition and throw a runtime exception with
     * the given string if the assertion fails.
     */
    private void ASSERT(boolean b, String err) throws RuntimeException {
        if(!b) {
            throw new RuntimeException(err);
        }
    }

    /**
     * Perform the levelizing layout on the local copy of the graph, according
     * to the algorithm outlined in the class description.
     */
    private void doLayout() {
        //Assign level numbers to the nodes in the graph.
        computeLevels();

        //POST all nodes have level greater than
        //     their incoming nodes
        for(Iterator i = _copyGraph.nodes(); i.hasNext(); ) {
                Node n = (Node)i.next();
                int lvl = getLevel(n);
                for(Iterator j = GraphUtilities.inNodes(n); j.hasNext(); ) {
                    Node n2 = (Node)j.next();
                    int lvl2 = getLevel(n2);
                    ASSERT(lvl2 < lvl, "Level order error " + n + ", " + n2);
                }
        }
        ASSERT(LayoutUtilities.checkContainment(_copyGraph, _target),
                "Inconsistent post-computeLevels");

        //Add dummies to edges that span multiple levels in the
        //graph.
        addDummies();

        //POST all nodes have level one greater
        //     than their incoming nodes
        //POST all dummy nodes have one in-edge and
        //     one out-edge
        for(Iterator i = _copyGraph.nodes(); i.hasNext(); ) {
            Node n = (Node)i.next();
            int lvl = getLevel(n);
            for(Iterator j = GraphUtilities.inNodes(n); j.hasNext(); ) {
                Node n2 = (Node)j.next();
                int lvl2 = getLevel(n2);
                ASSERT((lvl2 == lvl-1), "Level equality error " + n + ", " + n2);
            }
        }
        for(Iterator i = _copyGraph.nodes(); i.hasNext(); ) {
            Node n = (Node)i.next();
            if(isDummy(n)) {
                Iterator outs = n.outEdges();
                ASSERT(outs.hasNext(), "Dummy w/ no out-edges");
                outs.next();
                ASSERT(!outs.hasNext(), "Dummy w/ multiple out edges");

                Iterator ins = n.inEdges();
                ASSERT(ins.hasNext(), "Dummy w/ no in edges");
                ins.next();
                ASSERT(!ins.hasNext(), "Dummy w/ multiple in edges");
            }
        }
        ASSERT(LayoutUtilities.checkContainment(_copyGraph, _target),
                "Inconsistent post-addDummies");

        //Create the _levels data structure which provides
        //convenient access to all the nodes in each level.
        makeLevels();
/*
        //POST no levels are empty, and for each
        //     node in a level it's level is appropriate
        for(int i  = 1; i < _levels.length; i++)
        {
            ArrayList nodes = _levels[i];
            ASSERT((nodes.size() != 0), "Empty level " + i);
        }
        ASSERT(LayoutUtilities.checkContainment(_copyGraph, _target),
                "Inconsistent post-makeLevels");
*/
        //Place the nodes in the viewport according to their
        //levels and sorting order (note: sorting is not yet
        //implemented).
        Rectangle2D r = _target.getViewport(_origGraph);
        placeNodes(r);

        //POST no post per se because this step does not
        //     modify graph topology.
    }


    /**
     * Inefficient check for cycles in a graph.
     */
    private boolean isCyclic(Graph g) {
	for(Iterator i = g.nodes(); i.hasNext(); ) {
	    Node root = (Node)i.next();
	    GraphUtilities.setAllVisited(g, false);
	    if(checkCyclic(root)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Inefficient algorithm to break cycles in
     * the graph.
     */
    private void breakCycles(Graph g) {
        boolean hasCycles = true;

        while(hasCycles) {
            hasCycles = false;
            for(Iterator i = g.nodes(); i.hasNext(); ) {
                Node root = (Node)i.next();
                GraphUtilities.setAllVisited(g, false);
                if(checkAndBreak(null, root)) {
                    hasCycles = true;
                    break;
                }
            }
        }
    }

    /**
     * Return true if a cycle was broken.
     */
    private boolean checkAndBreak(Edge e, Node n) {
        ASSERT(n != null, "null tail: " + n);

        if(n.isVisited()) {
            ASSERT(e != null, "null incoming edge: " + n);
//            debug("BROKEN CYCLE AT: " + n);
            Node h = e.getHead();
            Node t = e.getTail();
            //reverse the edge
            _impl.setEdgeHead(e, t);
            _impl.setEdgeTail(e, h);
            return true;
        }

        n.setVisited(true);
        for(Iterator i = n.outEdges(); i.hasNext(); ) {
            Edge outEdge = (Edge)i.next();
            Node outNode = outEdge.getHead();
            ASSERT(outNode != null, "null head: " + e);
            if(checkAndBreak(outEdge, outNode)) {
                return true;
            }
        }
        n.setVisited(false);
        return false;
    }



    /**
     * Perform DFS from the given node, checking for
     * cycles.
     */
    private boolean checkCyclic(Node n) {
        ASSERT(n != null, "null tail: " + n);

        if(n.isVisited()) {
            //          debug("CYCLE AT: " + n);
            return true;
        }
        n.setVisited(true);
        for(Iterator i = n.outEdges(); i.hasNext(); ) {
            Edge e = (Edge)i.next();
            Node out = e.getHead();
            ASSERT(out != null, "null head: " + e);
            if(checkCyclic(out)) {
                return true;
            }
        }
        n.setVisited(false);
        return false;
    }

    /**
     * Cleanup the data structures that are setup in each invocation
     * of the algorithm, so that the next time the algorithm is invoked
     * they will be empty.
     */
    private void cleanupStructures() {
        _target = null;
        _copyGraph = null;
        _maxLevel = -1;
        for(Iterator i = new ArrayIterator(_levels); i.hasNext(); ) {
            ArrayList l = (ArrayList)i.next();
            l.clear();
        }
        _levels = null;
        _meta = null;
    }


    /**
     * Add dummy nodes between nodes along edges that span multiple
     * levels.  For example:
     *
     * <pre>
     *
     *   o from	    o from	  o from
     *   |	    |		  | <------ original edge
     *   |	    |		  o dum1
     *   |    ==>   |	    ==>	  |	  ==> ...
     *   |	    o dum1	  o dum2
     *   | <- e --> |		  |
     *  o to	    o to	  o to
     *
     * </pre>
     */
    private void addDummies() {
        ArrayList dummies = new ArrayList();
        for(Iterator nodes = _copyGraph.nodes(); nodes.hasNext();) {
            Node to = (Node)nodes.next();
            if(isDummy(to)) { continue; }
            LevelInfo nlinfo = getLevelInfo(to);

            for(Iterator in = to.inEdges(); in.hasNext();) {
                Edge e = (Edge)in.next();
                if(isDummy(e.getTail())) { continue; }
                while(getLevel(to) > getLevel(e.getTail())+1 ) {
                    //                    debug("Creating dummy between " + e.getTail() + " & " + e.getHead());

                    //dummy gets stuck between e.tail and e.head
                    LevelInfo dumInfo = new LevelInfo();
                    Node dummy = _impl.createNode(dumInfo);
                    dumInfo.level = getLevel(e.getTail())+1;
                    // XXX postpone until later!  this is a
                    // hack to avoid concurrent modification
                    // exception.... =(
                    // _impl.addNode(dummy, _copyGraph);
                    dummies.add(dummy);
                    _impl.setEdgeHead(e, dummy);
                    e = _impl.createEdge(null);
                    _impl.setEdgeTail(e, dummy);
                    _impl.setEdgeHead(e, to);
                }
            }
        }

        //avoid concurrent modification exception...
        for(Iterator i = dummies.iterator(); i.hasNext(); ) {
            _impl.addNode((Node)i.next(), _copyGraph);
        }
    }

    /**
     * Debugging output to standard err.
     */
    private void debug(String s) {
	System.err.println(s);
    }


    /**
     * Get the level of <i>n</i> in the graph.
     * Requires that all nodes have LevelInfo attributes.
     */
    private void makeLevels() {
        _maxLevel = -1;
        Node maxNode = null;
        int level;

        //find the topmost node
        for(Iterator e = _copyGraph.nodes(); e.hasNext();) {
            Node n = (Node)e.next();
            if((level = getLevel(n)) > _maxLevel) {
                _maxLevel = level;
                maxNode = n;
            }
        }

        //        debug("max = " + maxNode);

        //create some buckets to store the nodes
        _levels = new ArrayList[_maxLevel+1];
        for(int i = 0; i < _maxLevel+1; i++) {
            _levels[i] = new ArrayList();
        }

        //clear all the nodes
        GraphUtilities.setAllVisited(_copyGraph, false);
        initialOrderNodes(maxNode);

	//debug
//          for(int i = 0; i < _levels.length; i++) {
//              ArrayList l = _levels[i];
//              IteratorUtil.printElements("Level " + i + ":", l.iterator());
//          }
    }

    /**
     * Assign an initial ordering to the nodes.  This starts with the
     * "maximum" node (i.e.  a node in the maximum level) and
     * traverses its predecessors, adding them all to the level
     * buckets.  Then it adds all the other unmarked nodes.
     */
    private void initialOrderNodes(Node maxNode) {
        addSubGraphReverseDFS(maxNode);

        for(Iterator e = _copyGraph.nodes(); e.hasNext();) {
            Node n = (Node)e.next();
            if(!n.isVisited()) {
                addSubGraphReverseDFS(n);
            }
        }
    }


    /**
     * Add this node and all of its parent nodes to the levels array
     * in a reverse DFS.
     */
    private void addSubGraphReverseDFS(Node n) {
        n.setVisited(true);
        for(Iterator ins = GraphUtilities.inNodes(n); ins.hasNext();) {
            Node in = (Node)ins.next();
            ASSERT((in != null), "NULL found, n = " + n);
            if(!in.isVisited()) {
                addSubGraphReverseDFS(in);
            }
        }
        _levels[getLevel(n)].add(n);
    }

    /**
     * Place the nodes in the graph, based on the previous level
     * calculations and the order of the nodes in the _levels array.
     * Because we can make the placement either horiz. we need to be
     * clever to share code.  The algorithm is written as if it were
     * vertical placement, and in the horizontal case, different
     * values are used.
     */
   private void placeNodes(Rectangle2D vp)
   {
      // debug("vp = " + vp);

      //XXX this whole thing is a hack.  there
      //    really should be no empty levels.
      //    fix is elsewhere...
      int nonEmptyLevels = 0;


      for(Iterator i = new ArrayIterator(_levels); i.hasNext(); )
      {
         ArrayList nodes = (ArrayList)i.next();

         // Check if there is a displayable node in this level.
         int numDisplayNodes = 0;
         Iterator it = nodes.iterator();
         while(it.hasNext())
         {
            Node n = (Node)it.next();
            if( !isDummy(n) )
            {
               nonEmptyLevels++;
               break;
            }
         }
      }
      nonEmptyLevels = (int)Math.max(1, nonEmptyLevels);

	if(getOrientation() == VERTICAL)
        {
	    double ystep, y;
	    ystep = vp.getHeight()/(nonEmptyLevels-1);
	    y = vp.getY();

	    for(Iterator i = new ArrayIterator(_levels); i.hasNext(); ) {
		ArrayList nodes = (ArrayList)i.next();

		double xstep, x;

		xstep = vp.getWidth()/(nodes.size()-1);
		x = vp.getX();
		if(nodes.size() == 0) {
		    continue;  //XXX why do we have an empty level???
		}
		if(nodes.size() == 1) {
		    //center it as a special case
		    x += vp.getWidth()/2;
		}

		for(Iterator ns = nodes.iterator(); ns.hasNext();) {
		    Node n = (Node)ns.next();
                    if(!isDummy(n)) {
                        placeNode(n, x, y);
                    }
		    x += xstep;
		}
		y += ystep;
	    }
	}
	else
        {
            double xstep;
            double x;

            if( nonEmptyLevels > 1 )
            {
               xstep = vp.getWidth() / (nonEmptyLevels-1);
               x = vp.getX();
            }
            else
            {
               // Special case: center if only one level.
               xstep = 0;
               x = vp.getX() + vp.getWidth()/2;
            }

            for(Iterator i = new ArrayIterator(_levels); i.hasNext(); )
            {
                ArrayList nodes = (ArrayList)i.next();

                // Count the displayable nodes in this level and add up their total height
                int numDisplayNodes = 0;
                double totalHeight = 0;
                Iterator it = nodes.iterator();
                while(it.hasNext())
                {
                    Node n = (Node)it.next();
                    if( !isDummy(n) )
                    {
                       // Accumulate the number of displayable nodes
                       numDisplayNodes ++;

                       // Accumulate their total height
                       LevelInfo inf = getLevelInfo(n);
                       totalHeight += inf.height;
                    }
                }

                // Special case: Handle empty levels.
                if(numDisplayNodes == 0)
                {
                    continue;  //XXX why do we have an empty level???
                }

                // Compute our placement increments
                double yGap = (vp.getHeight() - totalHeight) / numDisplayNodes;
                double y = vp.getY() + yGap/2;

                for(Iterator ns = nodes.iterator(); ns.hasNext();)
                {
                    Node n = (Node)ns.next();

                    if(!isDummy(n))
                    {
                       LevelInfo inf = getLevelInfo(n);

                       // placeNode places the center of the object at x,y so compute the offsets.
                       double placeY = y + inf.height/2;
                       double placeX = x;

                       // Special case: place the inputBar and outputBar objects at the edges.
                       if( placeX - inf.width/2 <= 0 )
                          placeX += inf.width/2 + 1;
                       if( placeX + inf.width/2 >= vp.getWidth() )
                          placeX -= inf.width/2 + 1;

                       // Place this node.
                       placeNode(n, placeX, placeY);

                       // Step to the next Y location: height of this node plus the gap.
                       y += inf.height + yGap;
                    }
                }

                x += xstep;
            }
	}
    }

    // add random purturbation for now.
    private void placeNode(Node n, double x, double y)
    {
        LevelInfo inf = getLevelInfo(n);

//dcm        x += Math.random()*.25*inf.width;
//dcm        y += Math.random()*.25*inf.height;
        inf.x = x - inf.width/2;
        inf.y = y - inf.height/2;
    }


    //==================================================================
    // UTILITY FUNCTIONS HERE
    //==================================================================


    private double getX(Node n) {
        return _target.getBounds(n).getX();
    }

    private LevelInfo getLevelInfo(Node n) {
	return (LevelInfo)n.getSemanticObject();
    }

    /**
     * Get the level of <i>n</i> in the graph.  Requires that node has
     * LevelInfo attribute.
     */
    private int getLevel(Node n) {
        return getLevelInfo(n).level;
    }

    /**
     * Set the level of <i>n</i> in the graph.
     * Requires that node has LevelInfo attribute.
     */
    private void setLevel(Node n, int l) {
        getLevelInfo(n).level = l;
    }

    /**
     * Get the level of <i>n</i> in the graph.
     * Requires that node has LevelInfo attribute.
     */
    private int getUsage(Node n) {
        return getLevelInfo(n).usage;
    }

    /**
     * Return whether or not the given node
     * is a dummy.
     */
    private boolean isDummy(Node n) {
        LevelInfo inf = getLevelInfo(n);
        return (inf.origNode == null);
    }

    /**
     * Set the level of <i>n</i> in the graph.
     * Requires that node has LevelInfo attribute.
     */
    private void setUsage(Node n, int val) {
        getLevelInfo(n).usage = val;
    }


    /**
     * Topologically sort from the given node and
     * place the results in the given array list.
     */
    private void topoSort(Node n, ArrayList topo) {
        n.setVisited(true);
        for(Iterator e = GraphUtilities.inNodes(n); e.hasNext();) {
            Node n2 = (Node)e.next();
            if(!n2.isVisited()) {
                topoSort(n2, topo);
            }
	}
        topo.add(n);
    }

    /**
     * Create a meta-node and add it to the graph, connecting
     * it to each existing node in the graph.
     */
    private void makeMeta() {
        _meta = _impl.createNode(new LevelInfo());

        for(Iterator ns = _copyGraph.nodes(); ns.hasNext(); ) {
            Node n = (Node)ns.next();
            Edge e = _impl.createEdge(null);
            _impl.setEdgeTail(e, n);
            _impl.setEdgeHead(e, _meta);
        }

	//avoid self-loop
	_impl.addNode(_meta, _copyGraph);
    }


    /**
     * Remove the meta-node from the graph.
     */
    private void removeMeta() {
        GraphUtilities.purgeNode(_impl, _meta);
        _meta = null;
    }

    /**
     * The semantic object of each node in the graph copy that is
     * being layed out.
     */
    private static class LevelInfo
    {
      public Node origNode = null;
      public double barycenter;
      public int level = -1;
      public int usage = Integer.MAX_VALUE;
      public double x;
      public double y;
      public double width;
      public double height;

      public String toString()
      {
         return "LevelInfo[" + origNode + ", barycenter=" + barycenter + ", level=" + level +
                           ", usage= " + usage + ", x=" + x + ", y=" + y + ", width=" + width +
                           ", height=" + height;
      }

    }

    /**
     * Topological sort of graph and then set level numbers
     * for the nodes.
     */
    private void computeLevels() {
        GraphUtilities.setAllVisited(_copyGraph, false);

        // Topological sort
        makeMeta();
        ArrayList topo = new ArrayList();
        topoSort(_meta, topo);
	//DEBUG
	//IteratorUtil.printElements("TOPOLOGICAL SORT:", topo.iterator());

        /*
	 * Find maximum level, which is 1 + the maximum
         * of any of your predecessors.
         *
         *         A
         *       / | \
         *      B  |  D
         *      |\ |  |
         *      |  C  |
         *       \ | /
         *       meta
         */
        int maxLevel = 0;
        for(Iterator e = topo.iterator(); e.hasNext();) {
            int level = 0;
            Node n = (Node)e.next();
            for(Iterator ins = GraphUtilities.inNodes(n); ins.hasNext();) {
                Node in = (Node)ins.next();
                level = Math.max(level, getLevel(in)+1);
            }
            //            debug("INITIAL: " + getLevelInfo(n).origNode + ", " + level);
//              for(Iterator ins = GraphUtilities.inNodes(n); ins.hasNext();) {
//                  Node in = (Node)ins.next();
//                  debug("\tin: " + getLevelInfo(in).origNode + ", " + getLevel(in));
//              }
            setLevel(n, level);
            maxLevel = Math.max(maxLevel, level);
        }

 	/* Find maximum usage, which is the maximum
         * level of any of your predecessors - 1.
         *
         *         A
         *       / | \
         *      B  |  |
         *      |\ |  |
         *      |  C  D
         *       \ | /
         *       meta
         */
        for(int i = topo.size()-1; i >= 0; i--) {
            Node n = (Node)(topo.get(i));
            int usage = maxLevel;

            if(!n.outEdges().hasNext()) {
                usage = getLevel(n);
            }
            for(Iterator outs = GraphUtilities.outNodes(n); outs.hasNext(); ) {
                //there was an XXX here?
                Node out = (Node)outs.next();
                usage = Math.min(usage, getUsage(out)-1);
            }
            setUsage(n, usage);
        }

        // Add two to maxLevel to leave room for the input and output bars.
        maxLevel += 2;

	// Assign level number based on usage.
        for(Iterator e = topo.iterator(); e.hasNext();)
        {
            Node n = (Node)e.next();

            // Add one to the level to force the input bar to be in its own level.
            int level = getUsage(n) + 1;

            Node origNode = getLevelInfo(n).origNode;
            if( origNode != null )
            {
               if( origNode.getSemanticObject() instanceof sam.inputBar )
               {
                  level = 0;
               }
               else if( origNode.getSemanticObject() instanceof sam.outputBar )
               {
                  level = maxLevel -1;
               }
            }

            setLevel(n, level);

            //debug("LEVEL: " + getLevelInfo(n).origNode + ", " + getUsage(n));
        }

    	removeMeta();
    }
}
















/*
    ///////////////////////////////////////////////////////////////////////
    // Under construction
    ///////////////////////////////////////////////////////////////////////



    /**
     * Do insertion sort on the level based on the barycenters,
     * then reorder
     *
     private final void sortLevel(ArrayList nodes) {
        Object []ns = nodes.toArray();
        Arrays.sort(ns, new BarycentricComparator());
        nodes.clear();
        for(Iterator i = new ArrayIterator(ns); i.hasNext(); ) {
            nodes.add(i.next());
        }

        /*
          int len = nodes.size();
          for(int i = 1; i < len; i++) {
          Node n1 = (Node) nodes.get(i);
          double bc = barycenter(n1);
          int j;
          for(j = i; j > 0; j--) {
          Node n2 = (Node)nodes.get(j-1);
          if( bc >= getBarycenter(n2)) break;
          nodes.add(j, n2);
          }
          nodes.add(j, n1);
          }*
    }


    private final void orderLevel( ArrayList nodes, double l, double y,
            boolean doin, boolean doout ) {
        int levelcnt = nodes.size();
        for(Iterator e = nodes.iterator(); e.hasNext();) {
            Node n = (Node) e.next();
            computeBarycenter(n, doin, doout);
        }
        sortLevel( nodes );
        //XXX placeLevel( l, y, nodes );
    }

    // Do downwards barycentering on first pass, upwards on second, then average
    private final void orderNodes( double l, int op ) {
        boolean doup = ((op & 0x1) == 1);
        boolean doin = (op > 5 || !doup);
        boolean doout = (op > 5 || doup);
        double ystep = (_maxLevel>0) ? (_target.getViewport(_origGraph).getHeight()/_maxLevel) : 0.0;
        if( doup ) {
            double y = 0.0;
            for( int i = 0; i <= _maxLevel; ++i ) {		// Going upwards
                ArrayList nodes = _levels[i];
                orderLevel( nodes, l, y, doin, doout );
                y += ystep;
            }
        }
        else {
            double y = l;
            for( int i = _maxLevel; i >= 0; --i ) {		// Going downwards
                ArrayList nodes = _levels[i];
                orderLevel( nodes, l, y, doin, doout );
                y -= ystep;
            }
        }
    }

      protected final void straightenDummy(Node n) {
      Node tail = n.getInNode(0);
      Node head = n.getOutNode(0);
      double avg = (n.getX() + tail.getX() + head.getX()) / 3;
      n.setX(avg);
      }

      private final int xmarginSize = 10;
      protected synchronized final void straightenLayout( double l ) {
      double ystep = l/(_maxLevel+1);
      double y = 0.0;
      for(int i = 0; i <= _maxLevel; i++) {
      ArrayList nodes = _levels[i];
      for(Iterator e = nodes.iterator(); e.hasNext(); ) {
      Node n = (Node)e.next();
      if(n instanceof DummyNode) {
      straightenDummy(n);
      }
      }

      for(int j = 1; j < nodes.size(); j++) {
      Node n = (Node)nodes.get(j);
      Node prev = (Node)nodes.get( j-1 );
      double prevright = prev.getX() + prev.getW()/2 + xmarginSize;
      double thisleft =  n.getX() - n.getW()/2 - xmarginSize;
      double overlap = prevright - thisleft;
      if( overlap > 0 ) {
      prev.setX(prev.getX() - overlap/2);
      n.setX(n.getX() + overlap/2);
      }
      n.setY(y);
      }
      y += ystep;
      }
      }



      protected int _operation = 0;
      protected final int _Order = 100;
      private final void Embed() {
      double L = _bb.globals.L();
      _bb.setArea( 0, 0, L, L );
      if( _operation < _Order ) {
      orderNodes( L, _operation );
      }
      else {
      straightenLayout( L );
      }
      _bb.Update();
      ++_operation;
      _bb.globals.Temp( (double)_operation );
      }

    private void computeBarycenter(Node n, boolean doin, boolean doout) {
        double insum = 0.0;
        double outsum = 0.0;
        int insize = 0;
        int outsize = 0;

        if(doin) {
            for(Iterator e = GraphUtilities.inNodes(n); e.hasNext();) {
                insize++;
                insum += getX((Node)e.next());
            }
            if(insize == 0) {
                insize = 1;
                insum = getX(n);
            }
        }

        if( doout ) {
            for(Iterator e = GraphUtilities.outNodes(n); e.hasNext();) {
                outsize++;
                outsum += getX(n);
            }
            if(outsize == 0) {
                outsize = 1;
                outsum = getX(n);
            }
        }

        double barycenter;
        if( doin && doout ) {
            barycenter = (insum+outsum)/(insize+outsize);
        }
        else if(doin) {
            barycenter = insum/insize;
        }
        else if(doout) {
            barycenter = outsum/outsize;
        }
        else {
            barycenter = getX(n);
        }

        LevelInfo info = getLevelInfo(n);
        info.barycenter = barycenter;
    }


    private double getBarycenter(Node n) {
        return getLevelInfo(n).barycenter;
    }



    private Filter _defaultIgnoreFilter = new Filter() {
        public boolean accept(Object o) {
            if(o instanceof Node) {
                Node n = (Node)o;
                return (n == null) || !_target.isNodeVisible(n);
            }
            return false;
        }
    };

    private Filter _ignoreFilter = _defaultIgnoreFilter;

    public void setIgnoreFilter(Filter f) {
        if(f == null) {
            _ignoreFilter = _defaultIgnoreFilter;
        }
        _ignoreFilter = new OrFilter(f, _defaultIgnoreFilter);
    }

    private boolean ignoreNode(Node n) {
        return _ignoreFilter.accept(n);
    }

    private boolean ignoreEdge(Edge e) {
        return (ignoreNode(e.getHead()) || ignoreNode(e.getTail()));
    }
    */


