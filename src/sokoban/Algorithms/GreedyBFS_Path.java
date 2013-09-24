package sokoban.Algorithms;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import javax.swing.plaf.basic.BasicOptionPaneUI;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;
import sokoban.types.NodeType;

public class GreedyBFS_Path extends AStar2_Path {

    public GreedyBFS_Path()
    {
    	super(AlgorithmType.GREEDY_BFS);
    }
}