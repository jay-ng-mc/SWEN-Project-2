package mycontroller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Path {
    // Store sequences of actions to be executed by the car
    // Actions stored as strings

    private LinkedList<Move> path;
    public enum Move {BRAKE, FORWARD, BACKWARD, LEFT, RIGHT, PASS}
    private int length;

    public LinkedList<Move> getPath(){
        return this.path;
    }

    public void addMove(Move move){
        this.path.add(move);
        this.length++;
    }

    public void removeMove(Move move){
        this.path.remove(move);
        this.length--;
    }

    public Move pop(){
        this.length--;
        return this.path.pop();
    }

    public Move first(){
        this.length--;
        return this.path.removeFirst();
    }

    public int getLength(){ return this.length; }
}
