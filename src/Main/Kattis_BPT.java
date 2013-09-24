/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sokoban.Algorithms.*;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Player;
import sokoban.types.NodeType;

/**
 *
 * @author figgefred
 */
public class Kattis_BPT {

    public static void main(String args[]) throws IOException
    {	
        
                BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
                
                String tmp = br.readLine();
                List<String> input = new ArrayList<String>();
                while(true)
                {
                    if(tmp == null || tmp.equals(""))
                        break;
                   // System.out.println("Read tmp: " + tmp);
                    input.add(tmp);
                    tmp = br.readLine();
                    
                }
                tmp = null;
                
                //ISearchAlgorithmPath pathSearcher = new BFS_Path();
                //ISearchAlgorithmPath pathSearcher = new AStar_Path();
                ISearchAlgorithmPath pathSearcher = new AStar2_Path();
                                
                BoardState state = new BoardState(input);
                Player player = new Player(state, pathSearcher);
               
                if(state.getNode((state.getPlayerNode())) == NodeType.PLAYER_ON_GOAL)
                {
                    System.out.println("");
                }
                else
                {
                    Path p = null;
                    for(BoardPosition goal: state.getGoalNodes())
                    {
                        p = player.findPath(state.getPlayerNode(), goal);
                        if(p == null)
                            continue;
                        if(p.getPath() != null && p.getPath().size() > 0)
                            break;
                    }
                    System.out.println(p.toString());
                }
                
    }
   
}
