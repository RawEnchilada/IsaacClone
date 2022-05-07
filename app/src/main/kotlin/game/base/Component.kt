package game;

import game.extension.Double2D;

//this should be abstract
abstract class Component(pos:Double2D){
    companion object{
        var components = mutableListOf<Component>();
        private var disposing = mutableListOf<Component>();
        fun updateAll(elapsed_ms:Long){
            for(i in (components.size-1 downTo 0)){
                val c = components[i];
                if(c.active)c.update(elapsed_ms);
            }
        }
        fun dispose(){
            components.removeAll(disposing);
            disposing.clear();
        }
    }

    var position = pos;
    var active = true;

    abstract fun update(elapsed_ms:Long);

    init{
        components.add(this);
    }

    open fun dispose(){
        disposing.add(this);
    }
}