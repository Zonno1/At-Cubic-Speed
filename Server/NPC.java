import org.joml.*;
import tage.rml.Vector3f;

public class NPC 
{
    double locationX, locationY, locationZ;
    double dir = 0.1;
    double size = 1.0;

    public NPC()
    {
        locationX = 0.0;
        locationY = 0.0;
        locationZ = 0.0;

    }

    public void randomizeLocation(int seedX, int seedZ)
    {
        locationX = ((double)seedX)/4.0- 5.0;
        locationY = 0;
        locationZ = 2;
    }

public Vector3f getPosition() {
    float locX = (float)locationX;
    float locY = (float)locationY;
    float locZ = (float)locationZ;
    return (Vector3f) Vector3f.createFrom(locX, locY, locZ);
}

    public double getX() {return locationX;}
    public double getY() {return locationY;}
    public double getZ() {return locationZ;}
    public void getBig() {size = 2.0;}
    public void getSmall() {size = 1.0;}
    public double getSize() {return size;}
    public void setSize(double s) {size = s;}

    public void updateLocation()
    {
        if(locationX>10)dir = -0.1;
        if(locationX<10)dir = 0.1;
        locationX = locationX + dir;
    }
}
