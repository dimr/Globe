import org.json.simple.JSONArray;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

import java.util.ArrayList;

/**
 * Created by dimitris on 11/4/15.
 */
public class Country {
    private ArrayList<Polygon2D> polygons;
    private String name;
    private String fips;
    private int numberOfPolygons;
    private PApplet pa;
    private Polygon2D poly;

    public Country(PApplet pa, String name, String type, JSONArray coordinates) {
        this.pa = pa;
        this.name = name;
        this.numberOfPolygons = numberOfPolygons;
        this.polygons = new ArrayList<>();
        for (int j = 0; j < coordinates.size(); j++) {
            //   if (name.equals("Greece")) {
            int finalOneArraySize = ((JSONArray) coordinates.get(j)).size();
            JSONArray finalOneArray = (JSONArray) coordinates.get(j);
            //finalOneArraySize: contains each array that represents one polygon
            for (int k = 0; k < finalOneArraySize; k++) {
                int finalArrayToPolygonSize = ((JSONArray) finalOneArray.get(k)).size();
                JSONArray finalArrayToPolygon = (JSONArray) finalOneArray.get(k);
                if (type.equals("Polygon")) {
                    poly = new Polygon2D();
                    // System.out.println("POLYGON");
                    double lon = ((Double) finalArrayToPolygon.get(0)).doubleValue();
                    double lat = ((Double) finalArrayToPolygon.get(1)).doubleValue();
                    float f_lon = (float) lon;
                    float f_lat = (float) lat;
                    poly.add(toApplicationDimension(new Vec2D(f_lon, f_lat)));
                    polygons.add(poly);
                   // System.out.println(poly);
                } else if (type.equals("MultiPolygon")) {
                    poly = new Polygon2D();
                    //  System.out.println("MULTIPOLYGON");
                    for (int l = 0; l < finalArrayToPolygonSize; l++) {
                        //   System.out.println(finalArrayToPolygon.get(l).getClass().getCanonicalName());
                        JSONArray coord = (JSONArray) finalArrayToPolygon.get(l);
                        double lon = ((Double) coord.get(0)).doubleValue();
                        double lat = ((Double) coord.get(1)).doubleValue();
                        float f_lon = (float) lon;
                        float f_lat = (float) lat;
                        poly.add(toApplicationDimension(new Vec2D(f_lon, f_lat)));
                        //double lon = ((Double) finalArrayToPolygon.get(l).).doubleValue();
                        //      }
                        polygons.add(poly);
                    }
                }
                // System.out.println(name + " " + finalArrayToPolygon + " " + finalOneArray.get(k));
            }
        }

        System.out.println("CONSTRUCTOR:"+polygons.size());


    } //CONSTRUCTOR

    public ArrayList<Polygon2D> getPolygons() {
        return this.polygons;
    }

    public String getName() {
        return this.name;
    }

    private Vec2D toApplicationDimension(Vec2D v) {
        Vec2D result = new Vec2D();
        float x = this.pa.width * (v.x() - (-180)) / (180 - (-180));
        float y = (this.pa.height - this.pa.height * (v.y() - (-90)) / (90 - (-90)));
        result.set(x, y);
        Vec3D vv = new Vec3D(x, y, 0).toCartesian();
        return result;

    }


    @Override
    public String toString() {
        return "Country{" +
                "polygons=" + polygons.size() +
                ", name='" + name + '\'' +
                ", fips='" + fips + '\'' +
                ", numberOfPolygons=" + numberOfPolygons +
                '}';
    }
}
