package game.pathFinding;

import game.extension.Int2D;

class AStar public constructor(weights: Array<Array<Double>>) {

    private val map:Array<Array<Double>> = weights;

    public fun find(start: Int2D, end: Int2D):Array<Int2D>{
        var current = Node(0.0,null,start);
        var visited = MutableList(0){ Node(0.0,null,start) };
        var queue = MutableList(0){ Node(0.0,null,start) };

        val dirs = arrayOf(Int2D(-1,0),Int2D(1,0),Int2D(0,1),Int2D(0,-1));

        while(true){
            visited.add(current);

            if(current.position == end){
                break;
            }

            var neighbors = MutableList(0){ Node(0.0,null,start) };

            for(d in dirs){
                val pos = current.position+d;
                if(!(pos.x < 0 || pos.x >= map.size || pos.y < 0 || pos.y >= map.size))
                    neighbors.add(Node(current.weight+pos.distance(end)*map[pos.x][pos.y],current,pos));
            }
            
            

            for(neighbor in neighbors){
                if(visited.find{neighbor.position == it.position} == null){
                    queue.add(neighbor);
                }
            }

            var mw = Double.MAX_VALUE;
            for(n in queue){
                if(n.weight < mw){
                    mw = n.weight;
                    current = n;
                }
            }
            queue.remove(current);
        }
        
        var path = MutableList(1){current.position};
        while(true){
            if(current.parent != null){
                path.add(0,current.parent!!.position);
                current = current.parent!!;
            }
            else break;
        }
        return path.toTypedArray<Int2D>();
    }
}

data class Node(
        val weight:Double,
        val parent: Node?,
        val position: Int2D
);