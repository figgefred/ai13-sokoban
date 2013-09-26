package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Player;
import sokoban.Algorithms.*;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_BlockPath;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.types.AlgorithmType;

public class Main_BFSPush {

	public static void main(String[] arg) throws IOException
	{
		//FileReader rawInput = new FileReader("sample.slc"); //the first twenty maps
            FileReader rawInput = new FileReader("test.slc"); //the first twenty maps
            //FileReader rawInput = new FileReader("testing/simpleplaytest5"); //the first twenty maps

		BufferedReader br = new BufferedReader(rawInput);
		String tmp = br.readLine();
		List<String> buffer = null;
		List<List<String>> levelBuffer = new ArrayList<>();
		Pattern p = Pattern.compile(";LEVEL [0-9]+");
		while(tmp != null)
		{
			if(p.matcher(tmp).matches())
			{
				if(buffer != null)
					levelBuffer.add(buffer);
				buffer = new ArrayList<>();
			}
			else
			{
				buffer.add(tmp);
			}
			tmp = br.readLine();
		}
		levelBuffer.add(buffer);
		
		int levelNumber = 1;
                
        
            for(List<String> level: levelBuffer)
		{
            for(String line: level)
            {
                System.out.println(line);
            }
            
            BoardState b = new BoardState(level);
    	
            //ISearchAlgorithmPath playerPathSearcher = new BFS_MovePlayer();              // ALgorithm for searching paths for player
            //ISearchAlgorithmPath blockPathSearcher = new BFS_PushBlock(playerPathSearcher);  // Algorithms for searching paths for blocks
            
            ISearchAlgorithmPath playerPathSearcher = new PlayerPath(AlgorithmType.BFS);              // ALgorithm for searching paths for player
            ISearchAlgorithmPath blockPathSearcher = new BlockPath(AlgorithmType.BFS, playerPathSearcher);              // ALgorithm for searching paths for player
            
            Player player = new Player(b, blockPathSearcher, playerPathSearcher);
            
            Path path = null;
//            BoardPosition posi = b.getBlockNodes().iterator().next();
            int count = 0;
            List<Path> paths = null;
            while( !b.isWin() && count < 100)
            {
                count ++;
                List<BoardPosition> posis = b.getBlockNodes();
                Collections.shuffle(posis);
                paths = new ArrayList<Path>();
                for(BoardPosition posi: posis)
                {
                    System.out.println("pushs");
                    for(BoardPosition goal: b.getGoalNodes())
                    {
                        //path = player.findPath(b.getPlayerNode(), goal);
                        path = player.pushBlockPath(posi, goal);
                        if(path == null)
                            continue;
                        if(path.getPath() != null && path.getPath().size() > 0)
                            break;
                    }
                    if(path != null)
                    {
                        System.out.println("Moving path: " + path);
                        for(BoardPosition pos: path.getPath())
                        {
                            if(!pos.equals(b.getPlayerNode()))
                            {
                            //    System.out.println("MOVING " + b.getDirection(b.getPlayerNode(), pos));
                            //    System.out.println("POS " + b.getPlayerNode() + " to " + pos);
                                b.movePlayerTo(pos);
                                System.out.println(b);
                            }
                        }
                        paths.add(path);
                    }
                    
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(paths.get(0));
            for(int i = 1; i < paths.size(); i++)
            {
                if(paths.get(i).getPath().size() == 0)
                    continue;
                sb.append(" | ").append(paths.get(i));
            }
            System.out.println("Level " + levelNumber++ +":\n\t" + sb.toString());
	}
    }

}