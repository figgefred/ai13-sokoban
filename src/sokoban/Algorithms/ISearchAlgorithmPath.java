/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.Set;
import sokoban.Node;
import sokoban.Path;

/**
 *
 * @author figgefred
 */
public interface ISearchAlgorithmPath 
{
    public Path getPath(Node initialPosition, Set<Node> destinations);
}
