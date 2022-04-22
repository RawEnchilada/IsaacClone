package GunGame.Actor;


import GunGame.Extension.Double2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import GunGame.Item.Item;
import GunGame.Map.*;
import GunGame.*;



class Player(starting: Room, pos:Double2D, size:Double2D = Double2D(80.0,100.0)) : Actor(pos,size,100){
    companion object{
        var player:Player? = null;
    }

    var sprite: Image;
    var currentRoom:Room = starting;
    val items:MutableList<Item> = mutableListOf();
    var score: Long = 0L;

    private var hitGate:Long = 0;
    init{
        sprite = Image(FileInputStream("src/main/resources/player.png")); 
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

        if(InputListener.isKeyDown(KeyCode.W))position+=Double2D.constants.up*speed*elapsed_s;
        else if(InputListener.isKeyDown(KeyCode.S))position+=Double2D.constants.down*speed*elapsed_s;
        if(InputListener.isKeyDown(KeyCode.A))position+=Double2D.constants.left*speed*elapsed_s;
        else if(InputListener.isKeyDown(KeyCode.D))position+=Double2D.constants.right*speed*elapsed_s;
        if(InputListener.isKeyDown(KeyCode.SPACE)) Drawable.CenterCamera(center);

        if(InputListener.isMouseDown(MouseButton.PRIMARY))Shoot((InputListener.mousePosition-getDrawPosition(center)));

        if (Gl.ghost_mode){
            Drawable.CenterCamera(position+size/2);
        }

        collider.rigid = !Gl.ghost_mode;
        collider.position = position;
        if(hitGate > 0)hitGate -= elapsed_ms;
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x, pos.y, size.x, size.y);
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

        super.Die()
    }

    override fun Dispose(){
        super.Dispose();
    }

    
}