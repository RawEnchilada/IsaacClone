package GunGame;

import GunGame.Math.Double2D;

//this should be abstract
abstract class Component(pos:Double2D){
    companion object{
        var components = mutableListOf<Component>();
        fun UpdateAll(elapsed_ms:Long){
            for(i in (components.size-1 downTo 0)){
                if(components[i].active)
                    components[i].Update(elapsed_ms);
            }
        }
    }

    var position = pos;
    var active = true;
    var team = 0;

    abstract fun Update(elapsed_ms:Long);

    init{
        components.add(this);
    }

    open fun Dispose(){
        components.remove(this);
    }
}