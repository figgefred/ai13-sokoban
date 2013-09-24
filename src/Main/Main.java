/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import sokoban.Algorithms.*;
import sokoban.*;


public class Main {

	private static boolean VERBOSE = false;
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
                
		//ISearchAlgorithmPath pathSearcher = new Greedy_Path();
		ISearchAlgorithmPath pathSearcher = new AStar2_Path();

		//BufferedReader br = new BufferedReader(
		//new InputStreamReader(System.in));
		
		//FileReader rawInput = new FileReader("all.slc");
		//FileReader rawInput = new FileReader("sample.slc");
		FileReader rawInput = new FileReader("twenty.slc"); //the first twenty maps

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
			if(VERBOSE)
			{
				for(String line: level)
				{
					System.out.println(line);
				}
			}
			BoardState b = new BoardState(level);
                        
                        Player player = new Player(b, pathSearcher);
            
            for(BoardPosition goal: b.getGoalNodes())
            {
    			Path output = player.findPath(b.getPlayerNode(), goal);
    			
    			System.out.println("Level " + levelNumber + ": " + output);
    			if(output != null)
    				break;
            }
                        if(VERBOSE)
                        {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 0; i < b.getRowsCount(); i++)
                            {
                                for(int j = 0; j < b.getColumnsCount(i); j++)
                                {
                                    sb.append(Constants.GetTypeAsString(b.getNode(i, j)));
                                }
                                sb.append("\n");
                            }
                            System.out.println("###########DEBUG##############");
                            System.out.println(sb.toString());
                            System.out.println("###########DEBUG##############");
                        }
			levelNumber++;
		}
	}
} // End Main


