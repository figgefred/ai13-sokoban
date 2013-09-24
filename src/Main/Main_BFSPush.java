package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Player;
import sokoban.Algorithms.*;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BlockPath;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;

public class Main_BFSPush {

	public static void main(String[] arg) throws IOException
	{
		FileReader rawInput = new FileReader("sample.slc"); //the first twenty maps

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

    		IExploreCondition pushCondition = new ExploreCondition_BlockPath(b);
    		IExploreCondition playerPathCondition = new ExploreCondition_FindPath(b);
    		
            ISearchAlgorithmPath blockPathSearcher = new BFS_PushBlock(pushCondition, playerPathCondition);
            ISearchAlgorithmPath playerPathSearcher = new BFS_Path(playerPathCondition);
            Player player = new Player(b, blockPathSearcher, playerPathSearcher);

            Path path = null;
            
            for(BoardPosition goal: b.getGoalNodes())
            {
                //path = player.findPath(b.getPlayerNode(), goal);
                path = player.pushBlockPath(b.getBlockNodes().iterator().next(), goal);
                if(path == null)
                    continue;
                if(path.getPath() != null && path.getPath().size() > 0)
                    break;
            }
			System.out.println("Level " + levelNumber++ +": " + path);
		}
	}
}
