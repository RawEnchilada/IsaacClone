package game.Actor;


import game.extension.Double2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.GraphicsContext;
import game.Item.Item;
import game.map.*;
import game.*;
import kotlin.math.absoluteValue


class Player(starting: Room, pos:Double2D, size:Double2D = Double2D(80.0,100.0)) : Actor(pos,size,100){
    companion object{
        var player:Player? = null;
    }

    val anim:AnimationPlayer;
    var currentRoom:Room = starting;
    val items:MutableList<Item> = mutableListOf();
    var score: Long = 0L;

    private var hitGate:Long = 0;
    init{
        anim = AnimationPlayer(
                "src/main/resources/player.png",
                listOf(
                        AnimationData("idle",1,true,3),
                        AnimationData("up",4,true,3),
                        AnimationData("down",4,true,3),
                        AnimationData("left",4,true,3),
                        AnimationData("right",4,true,3),
                        AnimationData("shootUp",2,false,2),
                        AnimationData("shootDown",2,false,2),
                        AnimationData("shootLeft",2,false,2),
                        AnimationData("shootRight",2,false,2),
                )
        );
        anim.fps = 8;
        if(player == null)player = this;
        collider.rigid = true;
        collider.onLayer = 0b0100;
        collider.useLayer = 0b1010;
        
        collider.onStay = fun(other){
            if(other.parent is Enemy){
                ReduceHealth(1);
            }
        }
    }

    override fun ReduceHealth(amount:Int){
        if(hitGate <= 0){
            hitGate = 1000;
            health-= amount;
        }
    }
 
    override fun Update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        var delta = Double2D();
        if(InputListener.isKeyDown(KeyCode.A)) delta += Double2D.constants.left;
        else if(InputListener.isKeyDown(KeyCode.D)) delta += Double2D.constants.right;
        if(InputListener.isKeyDown(KeyCode.W)) delta += Double2D.constants.up;
        else if(InputListener.isKeyDown(KeyCode.S)) delta += Double2D.constants.down;

        position += delta*speed*elapsed_s;

        if(delta == Double2D())anim.Animate("idle");
        else if(delta.x.absoluteValue > delta.y.absoluteValue){
            if(delta.x > 0)anim.Animate("right");
            else anim.Animate("left");
        }
        else{
            if(delta.y > 0)anim.Animate("down");
            else anim.Animate("up");
        }



        if(InputListener.isKeyDown(KeyCode.SPACE))CenterCamera(center);

        if(InputListener.isMouseDown(MouseButton.PRIMARY)){
            val vector = InputListener.mousePosition-getDrawPosition(center);
            Shoot(vector);
            if(vector.x.absoluteValue > vector.y.absoluteValue){
                if(vector.x > 0)anim.Animate("shootRight");
                else anim.Animate("shootLeft");
            }
            else{
                if(vector.y > 0)anim.Animate("shootDown");
                else anim.Animate("shootUp");
            }
        };

        if (Gl.ghost_mode){
            CenterCamera(position+size/2);
        }

        collider.rigid = !Gl.ghost_mode;
        collider.position = position;
        if(hitGate > 0)hitGate -= elapsed_ms;

        anim.Update(elapsed_ms);
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(anim.Sprite, pos.x, pos.y, size.x, size.y);
    }

    fun moveToRoom(target:Room,d: Direction){
        currentRoom.SetActive(false);
        currentRoom = target;
        target.focusRoom();
        target.onEnter();
        when(d){
            Direction.left -> position.x = target.position.x+Room.roomSize.x-Door.doorSize.x-size.x-16.0;
            Direction.right -> position.x = target.position.x+ Door.doorSize.x+16.0;
            Direction.up -> position.y = target.position.y+Room.roomSize.y-Door.doorSize.y-size.y-16.0;
            Direction.down -> position.y = target.position.y+Door.doorSize.y+16.0;
        }
    }

    override fun gotHit(bullet:Projectile){
        var canBeHit = true;
        for(item in items){
            canBeHit = canBeHit && item.onHit(bullet);
        }
        if(canBeHit){
            ReduceHealth(bullet.damage);
        }
    }

    override fun Shoot(vector:Double2D){
        if(Gl.elapsedTime-lastShot > 1000/fireRate){
            lastShot = Gl.elapsedTime;
            val p = Projectile(this,1, center, 15.0, vector.normalized(),bulletSpeed);
            for(item in items){
                item.onShoot(p);
            }
        }
    }

    override fun Die() {
        //TODO end game, write score
        super.Die()
    }

    override fun Dispose(){
        super.Dispose();
    }

    
}