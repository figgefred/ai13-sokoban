/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.Set;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;

/**
 *
 * @author figgefred
 */
public interface ISearchAlgorithmPath 
{
    public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination);
    public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destination);
}
