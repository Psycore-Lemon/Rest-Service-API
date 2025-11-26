package ilp.tutorials.simpledemo.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ilp.tutorials.simpledemo.entity.Cw1.Position;

public class PathCache {

    private final Map<String, List<Position>> cache = new HashMap<String, List<Position>>();

    public String makeKey(Position start, Position end)
    {
        return String.format("%.6f_%.6f__%.6f_%.6f", 
                start.getLng(), start.getLat(),
                end.getLng(), end.getLat());
    }

    public boolean contains(Position start, Position end)
    {
        return cache.containsKey(makeKey(start, end));
    }

    public List<Position> get(Position start, Position end)
    {
        return cache.get(makeKey(start, end));
    }

    public void put(Position start, Position end, List<Position> path)
    {
        cache.put(makeKey(start, end), path);
    }
}
