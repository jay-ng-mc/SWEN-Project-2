package mycontroller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Path {
    private LinkedList<String> path;
    private static final String[] OPTIONS_VALUES = new String[]{
            "brake",
            "forward",
            "backward",
            "left",
            "right",
    };
    private static final Set<String> OPTIONS = new HashSet<String>(Arrays.asList(OPTIONS_VALUES));

    public LinkedList<String> getPath(){
        return this.path;
    }

    public void addMove(String move){
        assert(OPTIONS.contains(move));
        this.path.add(move);
    }
}
