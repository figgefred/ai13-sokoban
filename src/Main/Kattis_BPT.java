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
import sokoban.Algorithms.BFS_Path;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Player;

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
                
                BoardState state = new BoardState(input);
                Player player = new Player(state, new BFS_Path());
                String output = player.findPath(state.getPlayerNode(), state.getGoalNodes());
                
                
                for(int r = 0; r < state.getRowsCount(); r++)
                {
                    
                    
                    
                }
                
                
                System.out.println(output);
                
    }
   
}
