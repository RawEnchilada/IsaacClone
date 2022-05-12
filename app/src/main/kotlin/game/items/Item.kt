package game.Item;

import game.actors.Player;
import game.extension.Double2D;
import game.Gl;
import game.actors.Projectile;
import javafx.scene.paint.Color;

class Item(val path:String,
           val onShoot: (bullet: Projectile) -> Unit = {},
           val onHit: (bullet: Projectile) -> Boolean = { _: Projectile -> true; },
           val onPickup: (player: Player) -> Unit = {}
){

    companion object{
        private var items = listOf<Item>();
        fun getRandomItem():Item{
            val n = Gl.randomInt(0,items.size);
            return items[n];
        }

        fun initItems(){
            items = listOf(
                    Item("src/main/resources/items/battery.png", onPickup = fun(p){p.fireRate *= 2.0;p.speedMultiplier *= 1.3;}),
                    Item("src/main/resources/items/bubble.png", onShoot = fun(b){b.ttl = (b.ttl*0.75).toLong();b.updateEvent += fun(_){b.radius += 1;}}),
                    Item("src/main/resources/items/orangejuice.png", onShoot = fun(b){b.fillColor = Color.ORANGE;b.damage += 1;}),
                    Item("src/main/resources/items/ghost.png",onShoot = fun(b){b.onEnterEvent += fun(_):Boolean{return false;}}),
                    Item("src/main/resources/items/shield.png",onShoot = fun(b){b.collider.useLayer = b.collider.useLayer or 0b000001}),
                    Item("src/main/resources/items/meat.png",onPickup = fun(p){p.maxHealth+=2;}),
                    Item("src/main/resources/items/moon.png",onShoot = fun(b){b.updateEvent += fun(_){b.force = b.force.rotate(0.03);}}),
                    Item("src/main/resources/items/mushroom.png",onPickup = fun(p){p.size *= 0.9;p.collider.size *= 0.9;}, onShoot = fun(b){b.force += Double2D(Math.random()-0.5,Math.random()-0.5);})
                    

            )
        }
    }
}