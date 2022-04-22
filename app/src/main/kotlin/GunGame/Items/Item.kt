package GunGame.Item;

import GunGame.Actor.Player;
import GunGame.Gl;
import GunGame.Projectile;
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
                    Item("src/main/resources/items/battery.png", onPickup = fun(player){player.fireRate *= 2.0;player.speedMultiplier *= 1.3;}),
                    Item("src/main/resources/items/bubble.png", onShoot = fun(bullet){bullet.ttl = (bullet.ttl*0.75).toLong();bullet.updateEvent += fun(_){bullet.radius += 1;}}),
                    Item("src/main/resources/items/orangejuice.png", onShoot = fun(b){b.fillColor = Color.ORANGE;b.damage += 1;}),
                    Item("src/main/resources/items/ghost.png",onShoot = fun(b){b.onEnterEvent += fun(_):Boolean{return false;}}),
                    Item("src/main/resources/items/shield.png",onShoot = fun(b){b.onEnterEvent += fun(other):Boolean{if(other.parent is Projectile){other.Dispose();b.Dispose();};return true;}})
            )
        }
    }
}