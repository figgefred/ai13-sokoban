/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import sokoban.Algorithms.BFS_Path;
import sokoban.Algorithms.ISearchAlgorithmPath;


public class Main {

	private static boolean VERBOSE = false;
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
                
		ISearchAlgorithmPath pathSearcher = new BFS_Path();
		//BufferedReader br = new BufferedReader(
		//new InputStreamReader(System.in));
		
		//FileReader rawInput = new FileReader("all.slc");
		FileReader rawInput = new FileReader("sample.slc");
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
			BoardState b = new BoardState(pathSearcher, level);
			String output = b.findPath(b.getGoalNodes());
			System.out.println("Level " + levelNumber + ": " + output);
			levelNumber++;
		}
	}
} // End Main

