/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import sokoban.BoardPosition;
import sokoban.BoardState;

/**
 *
 * @author figgefred
 */
public interface ISearchPushAlgorithm {
    
    public BoardState pushBlock(BoardState state, BoardPosition blockPos);
    
}
