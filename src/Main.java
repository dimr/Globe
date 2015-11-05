import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import processing.core.PApplet;
import org.json.simple.parser.JSONParser;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.processing.ToxiclibsSupport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dimitris on 11/4/15.
 */
public class Main extends PApplet {
    String geoJSON = "./data/mike.geojson";
    String topoJSON = "/home/dimitris/data/topo/world-10m.json";
    String t = "/home/dimitris/data/shp/geo-boundaries-world-110m/countries.geojson";
    ToxiclibsSupport toxi;
    ArrayList<Country> countries;

    public void setup() {
        size(displayWidth, displayHeight);
        toxi = new ToxiclibsSupport(this);
        Object in = null;
        try {
            in = new JSONParser().parse(new FileReader(t));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException r) {
            r.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject start = (JSONObject) in;
        JSONArray features = (JSONArray) start.get("features");
        int count = 0;
        countries = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            JSONObject pr = (JSONObject) features.get(i);
            JSONObject properties = (JSONObject) pr.get("properties");
            String name = (String) properties.get("name");
            String condinent = (String) properties.get("continent");
            String fips = (String) properties.get("FIPS_10_");
            JSONObject t = ((JSONObject) pr.get("geometry"));
            String type = t.get("type").toString();
            JSONArray ar = (JSONArray) t.get("coordinates");
            if (name.equals("Greece")) {
                for (int j = 0; j < ar.size(); j++) {
                    JSONArray last=(JSONArray)ar.get(j);
                    for (int k = 0; k < last.size(); k++) {
                        JSONArray finalCoordinates = (JSONArray)last.get(k);
                        double x= ((Double)finalCoordinates.get(0)).doubleValue();
                        double y=  ((Double)finalCoordinates.get(1)).doubleValue();
                        float latitutde=(float)x;
                        float longitude=(float)y;
                        System.out.println(new Vec2D(latitutde,longitude));
                    }


                }


                count++;
           }
        }
        // System.out.println(count);
//        System.out.println(((JSONObject)features.get(11)).get("NAME"));
//        System.out.println(((JSONObject) features.get(11)).get("geometry"));
//        System.out.println(countries.size());
//        System.out.println(countries.get(30).getName() + " ");


    }


    public void draw() {
        background(100);
//        stroke((float) .5);
//        for (Country c : countries) {
//            //  if (c.getName().equals("Russia"))
//            for (Polygon2D p : c.getPolygons()) {
//                toxi.polygon2D(p);
//            }
//        }
        pushStyle();
        fill(255,90);
        noStroke();
        ellipse(frameCount % width, height / 2, 40, 40);
        popStyle();

    }


    public static void main(String[] args) {
        PApplet.main(new String[]{Main.class.getName()});

    }
}
