package game.actors;


import game.extension.Double2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.GraphicsContext;
import game.Item.Item;
import game.map.*;
import game.*;
import game.ui.Restart
import kotlin.math.absoluteValue


class Player(starting: Room, pos:Double2D, size:Double2D = Double2D(80.0,100.0)) : Actor(pos,size,100){
    companion object{
        var player: Player? = null;
    }

    private var changeRooms = false;
    private var targetRoom = starting;

    private val head:AnimationPlayer = AnimationPlayer(
            "src/main/resources/playerHead.png",
            listOf(
                    AnimationData("down",1,true,3),
                    AnimationData("up",1,true,3),
                    AnimationData("left",1,true,3),
                    AnimationData("right",1,true,3),
                    AnimationData("shootDown",1,false,2),
                    AnimationData("shootUp",1,false,2),
                    AnimationData("shootLeft",1,false,2),
                    AnimationData("shootRight",1,false,2),
            )
    );
    private val body:AnimationPlayer = AnimationPlayer(
            "src/main/resources/playerBody.png",
            listOf(
                    AnimationData("idle",1,true,3),
                    AnimationData("down",4,true,3),
                    AnimationData("up",4,true,3),
                    AnimationData("left",4,true,3),
                    AnimationData("right",4,true,3)
            )
    );

    var currentRoom:Room = starting;
    val items:MutableList<Item> = mutableListOf();

    private var hitGate:Long = 0;
    init{
        speedMultiplier = 0.85;
        fireRate = 0.95;
        body.fps = 8;
        head.fps = 8;
        if(player == null) player = this;
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
 
    override fun update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());

        if(changeRooms){
            currentRoom.isActive(false);
            currentRoom = targetRoom;
            targetRoom.onEnter();
            targetRoom.focusRoom();
            changeRooms = false;
        }

        var delta = Double2D();
        if(InputListener.isKeyDown(KeyCode.A)) delta += Double2D.constants.left;
        else if(InputListener.isKeyDown(KeyCode.D)) delta += Double2D.constants.right;
        if(InputListener.isKeyDown(KeyCode.W)) delta += Double2D.constants.up;
        else if(InputListener.isKeyDown(KeyCode.S)) delta += Double2D.constants.down;

        position += delta*speed*elapsed_s;

        if(delta == Double2D()){
            head.Animate("down");
            body.Animate("idle");
        }
        else if(delta.x.absoluteValue > delta.y.absoluteValue){
            if(delta.x > 0){
                head.Animate("right");
                body.Animate("right");
            }
            else {
                head.Animate("left");
                body.Animate("left");
            }
        }
        else{
            if(delta.y > 0){
                head.Animate("down");
                body.Animate("down");
            }
            else {
                head.Animate("up");
                body.Animate("up");
            }
        }



        if(InputListener.isKeyDown(KeyCode.SPACE))centerCamera(center);

        if(InputListener.isMouseDown(MouseButton.PRIMARY)){
            val vector = InputListener.mousePosition-getDrawPosition(center);
            shoot(vector);
            if(vector.x.absoluteValue > vector.y.absoluteValue){
                if(vector.x > 0)head.Animate("shootRight");
                else head.Animate("shootLeft");
            }
            else{
                if(vector.y > 0)head.Animate("shootDown");
                else head.Animate("shootUp");
            }
        };

        if (Gl.ghost_mode){
            centerCamera(position+size/2);
        }

        collider.rigid = !Gl.ghost_mode;
        collider.position = position;
        if(hitGate > 0)hitGate -= elapsed_ms;

        head.Update(elapsed_ms);
        body.Update(elapsed_ms);
    }

    override fun draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(head.sprite, pos.x, pos.y, size.x, size.y/2);
        gc.drawImage(body.sprite, pos.x, pos.y+size.y/2, size.x, size.y/2);
    }

    fun moveToRoom(target:Room,d: Direction){
        changeRooms = true;
        targetRoom = target;
        when(d){
            Direction.left -> position.x = target.position.x+Room.roomSize.x-Door.doorSize.x-size.x-16.0;
            Direction.right -> position.x = target.position.x+ Door.doorSize.x+16.0;
            Direction.up -> position.y = target.position.y+Room.roomSize.y-Door.doorSize.y-size.y-16.0;
            Direction.down -> position.y = target.position.y+Door.doorSize.y+16.0;
        }
    }

    override fun gotHit(bullet: Projectile){
        var canBeHit = true;
        for(item in items){
            canBeHit = canBeHit && item.onHit(bullet);
        }
        if(canBeHit){
            ReduceHealth(bullet.damage);
        }
    }

    override fun shoot(vector:Double2D){
        if(Gl.elapsedTime-lastShot > 1000/fireRate){
            lastShot = Gl.elapsedTime;
            val p = Projectile(this,1, center, 15.0, vector.normalized(),bulletSpeed);
            for(item in items){
                item.onShoot(p);
            }
        }
    }

    override fun die() {
        Gl.disposeAll();
        Restart();
    }

    override fun dispose(){
        super.dispose();
    }

    
}