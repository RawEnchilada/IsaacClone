package GunGame;

import GunGame.Extension.Double2D;

//this should be abstract
abstract class Component(pos:Double2D){
    companion object{
        var components = mutableListOf<Component>();
        private var disposing = mutableListOf<Component>();
        fun UpdateAll(elapsed_ms:Long){
            for(i in (components.size-1 downTo 0)){
                if(components[i].active)
                    components[i].Update(elapsed_ms);
            }
        }
        fun Dispose(){
            components.removeAll(disposing);
            disposing.clear();
        }
    }

    var position = pos;
    var active = true;

    abstract fun Update(elapsed_ms:Long);

    init{
        components.add(this);
    }

    open fun Dispose(){
        disposing.add(this);
    }
}