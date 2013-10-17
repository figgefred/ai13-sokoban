package kattisredo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeadlockPattern {
	
	private List<List<NodeType>> Map = new ArrayList<>();
	private int rows;
	private int cols;
	 
    public DeadlockPattern(List<String> rows)
    {
    	this(rows, true);
    }    
    
    public DeadlockPattern(List<String> rows, boolean initZobrist)
    {    
        // Init board
    	buildMap(rows);
        this.rows = rows.size();
        if(initZobrist) {
        	cols = Integer.MIN_VALUE;
        	for(List<NodeType> row : Map)
        		cols = Math.max(row.size(), cols);
        }
    }
    
    public DeadlockPattern() {
    	
    }
    
  	public static DeadlockPattern getPatternFromFile(String filename) throws IOException
  	{
  		return getPatternFromFile(filename, true);
  	}
  	
	public static DeadlockPattern getPatternFromFile(String filename, boolean initHash) throws IOException
	{
		FileReader rawInput = new FileReader(filename);
		BufferedReader br = new BufferedReader(rawInput);
		
		List<String> buffer = new ArrayList<>();
		
		while(true)
		{
			String tmp = br.readLine();
			if(tmp == null)
				break;
			buffer.add(tmp);			
		}
		br.close();
	
		
		return new DeadlockPattern(buffer, initHash);
	}
	
    private void buildMap(List<String> rows)
    {        
        //IDCounter = 1;
        Map = new ArrayList<>();
        
        char[] columns = null;
        String tmp ;
        for(int rIndex = 0; rIndex < rows.size(); rIndex++)
        {
            tmp = rows.get(rIndex);
            if(tmp == null || tmp.equals(""))
                break;
            columns = tmp.toCharArray();
            
            Map.add(new ArrayList<NodeType>(columns.length));
            for(int cIndex = 0; cIndex  < columns.length; cIndex++)
            {				                
                NodeType type = NodeType.parse(columns[cIndex]);
                
                Map.get(rIndex).add(type);
            }
        }
    }
    
    public boolean isMatch(BoardState board)  {  	
		for(int y = 0; y < board.getRowsCount() - rows + 1; ++y) {
			
			for(int x = 0; x < board.getColumnsCount() - cols + 1; ++x) {
				
				BoardPosition pos = new BoardPosition(y, x);				
				
	    		int blocks = 0;
	    		boolean matched = true;
	    		
	    		normal:
	    		for(int r = 0; r < rows; ++r) {
	    			for(int c = 0; c < Map.get(r).size(); ++c)
	    			{    				
	    				NodeType node = board.getNode(pos.Row + r, pos.Column + c);	    				
	    				NodeType patt = Map.get(r).get(c);
	    				
	    				// Tolka pattern filer lite olika:
	    				// ? som vad som helst.
	    				
	    				if(node == NodeType.BLOCK)
	    					++blocks;
	    				
	    				if(patt == NodeType.INVALID || node.equals(patt))    				
	    					continue;    				
	    				
	    				if(node == NodeType.BLOCK_ON_GOAL && patt == NodeType.BLOCK)
	    					continue;
	    				
	    				matched = false;
	    				break normal;
	    			}
	    		}
	    		
	    		if(blocks > 0 && matched)
	    			return true;
	    		
	    		blocks = 0;
	    		matched = false;
	    		
	    		// Upp och ner (bah)
	    		upsidedown:
	    		for(int r = 0; r < rows; ++r) {
	    			for(int c = 0; c < Map.get(r).size(); ++c)
	    			{    				
	    				NodeType node = board.getNode(pos.Row + r, pos.Column + c);
	    				NodeType patt = Map.get(rows - 1 - r).get(c);
	    				
	    				// Tolka pattern filer lite olika:
	    				// ? som vad som helst.
	    				
	    				if(node == NodeType.BLOCK)
	    					++blocks;
	    				
	    				if(patt == NodeType.INVALID || node.equals(patt))    				
	    					continue;    				
	    				
	    				if(node == NodeType.BLOCK_ON_GOAL && patt == NodeType.BLOCK)
	    					continue;
	    					
	    				matched = false;
	    				break upsidedown;
	    			}
	    		}
	    		
	    		if(blocks > 0 && matched)
	    			return true;
	    		
	    		blocks = 0;
	    		matched = true;
	    		
	    		// Mirrored
	    		mirrored:
	    		for(int r = 0; r < rows; ++r) {
	    			for(int c = 0; c < Map.get(r).size(); ++c)
	    			{    				
	    				NodeType node = board.getNode(pos.Row + r, pos.Column + c);
	    				NodeType patt = Map.get(r).get(Map.get(r).size() - 1 - c);
	    				
	    				// Tolka pattern filer lite olika:
	    				// ? som vad som helst.
	    				
	    				if(node == NodeType.BLOCK)
	    					++blocks;
	    				
	    				if(patt == NodeType.INVALID || node.equals(patt))    				
	    					continue;    				
	    				
	    				if(node == NodeType.BLOCK_ON_GOAL && patt == NodeType.BLOCK)
	    					continue;
	    				
	    				matched = false;
	    				break mirrored;
	    			}
	    		}
	    		
	    		if(blocks > 0 && matched)
	    			return true;
	    	}
		}
    	
    	return false;
    }
    
    public static void main(String[] args) throws IOException {
    	BoardState board = BoardState.getBoardFromFile("testing/deadlocktest7");
		DeadlockPattern pattern = DeadlockPattern.getPatternFromFile("deadlockpatterns/8");
		
		System.out.println(board);
		System.out.println(pattern);
		System.out.println(pattern.isMatch(board));    	
    }
    
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(List<NodeType> row : Map) {
    		for(NodeType t : row)
    			builder.append(t.getChar());
    		builder.append('\n');
    	}
    	
    	return builder.toString();   		
    }
	
    

}
