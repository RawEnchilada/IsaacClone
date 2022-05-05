package game.extension;

import kotlin.math.absoluteValue
import kotlin.math.pow;
import kotlin.math.sqrt;


class Int2D(x:Int,y:Int){
    var x:Int = 0;
    var y:Int = 0;

    object constants{
        val left = Int2D(-1,0);
        val right = Int2D(1,0);
        val down = Int2D(0,1);
        val up = Int2D(0,-1);
    }

    
    init{
        this.x = x;
        this.y = y;
    }
    constructor():this(0,0);
    constructor(x:Double,y:Double):this(x.toInt(),y.toInt());


    operator fun plus(other: Int2D): Int2D {
        return Int2D(this.x+other.x,this.y+other.y);
    }


    operator fun minus(other: Int2D): Int2D {
        return Int2D(this.x-other.x,this.y-other.y);
    }


    operator fun times(n:Float): Int2D {
        return Int2D((this.x*n).toInt(),(this.y*n).toInt());
    }
    operator fun times(n:Int): Int2D {
        return Int2D(this.x*n,this.y*n);
    }
    operator fun times(other:Int2D):Int2D{
        return Int2D(this.x*other.x,this.y*other.y);
    }
    operator fun times(other:Double2D):Int2D{
        return Int2D(this.x*other.x,this.y*other.y);
    }


    operator fun div(n:Float): Int2D {
        return Int2D((this.x/n).toInt(),(this.y/n).toInt());
    }
    operator fun div(n:Int): Int2D {
        return Int2D(this.x/n,this.y/n);
    }
    operator fun div(other:Int2D): Int2D {
        return Int2D(this.x/other.x,this.y/other.y);
    }
    operator fun div(other:Double2D): Int2D {
        return Int2D(this.x/other.x,this.y/other.y);
    }


    override fun equals(other:Any?):Boolean{
        return if(other is Int2D)
            (this.x == other.x && this.y == other.y);
        else false;
    }
    override fun toString():String{
        return "{x:$x,y:$y}";
    }
    fun toDouble2D():Double2D{
        return Double2D(x.toDouble(),y.toDouble());
    }

    public fun distance(other: Int2D):Float{
        return sqrt((other.x-x).toFloat().pow(2)+(other.y-y).toFloat().pow(2));
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

}


class Double2D(x:Double,y:Double){
    var x:Double = 0.0;
    var y:Double = 0.0;

    object constants{
        val left = Double2D(-1.0,0.0);
        val right = Double2D(1.0,0.0);
        val down = Double2D(0.0,1.0);
        val up = Double2D(0.0,-1.0);
    }

    
    init{
        this.x = x;
        this.y = y;
    }
    constructor():this(0.0,0.0);


    operator fun plus(other: Double2D): Double2D {
        return Double2D(this.x+other.x,this.y+other.y);
    }
    operator fun plus(other: Int2D): Double2D {
        return Double2D(this.x+other.x,this.y+other.y);
    }
    operator fun plus(n: Int): Double2D {
        return Double2D(this.x+n,this.y+n);
    }


    operator fun minus(other: Double2D): Double2D {
        return Double2D(this.x-other.x,this.y-other.y);
    }
    operator fun minus(n: Int): Double2D {
        return Double2D(this.x-n,this.y-n);
    }
    operator fun minus(other: Int2D): Double2D {
        return Double2D(this.x-other.x,this.y-other.y);
    }


    operator fun times(n:Float): Double2D {
        return Double2D((this.x*n),(this.y*n));
    }
    operator fun times(n:Double): Double2D {
        return Double2D((this.x*n),(this.y*n));
    }
    operator fun times(n:Int): Double2D {
        return Double2D(this.x*n,this.y*n);
    }
    operator fun times(other:Double2D):Double2D{
        return Double2D(this.x*other.x,this.y*other.y);
    }
    operator fun times(other:Int2D):Double2D{
        return Double2D(this.x*other.x,this.y*other.y);
    }


    operator fun div(n:Float): Double2D {
        return Double2D((this.x/n),(this.y/n));
    }
    operator fun div(n:Double): Double2D {
        return Double2D((this.x/n),(this.y/n));
    }
    operator fun div(n:Int): Double2D {
        return Double2D(this.x/n,this.y/n);
    }
    operator fun div(other:Double2D):Double2D{
        return Double2D(this.x/other.x,this.y/other.y);
    }
    operator fun div(other:Int2D):Double2D{
        return Double2D(this.x/other.x,this.y/other.y);
    }


    override fun equals(other:Any?):Boolean{
        return if(other is Double2D)
            (this.x == other.x && this.y == other.y);
        else false;
    }
    override fun toString():String{
        return "{x:$x,y:$y}";
    }
    fun toInt2D():Int2D{
        return Int2D(x.toInt(), y.toInt());
    }

    public fun distance(other: Double2D):Double{
        return sqrt((other.x-x).pow(2)+(other.y-y).pow(2));
    }
    public fun magnitude():Double{
        return sqrt(x.pow(2)+y.pow(2));
    }
    public fun normalized():Double2D{
        return this/magnitude();
    }
    public fun dot(other:Double2D):Double{
        var product = 0.0;
 
        product += x*other.x;
        product += y*other.y;
        
        return product;
    }

    override fun hashCode(): Int {
        var result = x;
        result = 31 * result + y;
        return result.toInt();
    }


    fun inverse(): Double2D {
        val d = Double2D(x,y);
        if(d.x != 0.0)d.x = 1.0/d.x;
        if(d.y != 0.0)d.y = 1.0/d.y;
        return d;
    }

    fun clamp(min: Double, max: Double): Double2D {
        return Double2D(x.clamp(min,max),y.clamp(min,max));
    }

    fun removeComponent(i: Int): Double2D {
        if(i == 0)return Double2D(0.0,y);
        else return Double2D(x,0.0);
    }

    fun absolute(): Double2D {
        return Double2D(x.absoluteValue,y.absoluteValue);
    }

}