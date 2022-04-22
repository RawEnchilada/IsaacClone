package GunGame.Extension;

fun Double.clamp(min:Double,max:Double):Double{
    return if(this < min)min;
    else if(this > max)max;
    else this;
}

fun Double2D.closestPointOnLineSegment(A:Double2D, B:Double2D):Double2D
    {
        var AP = this - A;    
        var AB = B - A;        

        var magnitudeAB = A.distance(B);
        var ABAPproduct = AP.dot(AB);
        var distance = ABAPproduct / (magnitudeAB *magnitudeAB);

        if (distance < 0.0){
            return A;

        }
        else if (distance > 1.0){
            return B;
        }
        else{
            return A + AB * distance;
        }
    }