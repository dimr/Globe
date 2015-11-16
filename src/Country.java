import org.json.simple.JSONArray;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import toxi.geom.Polygon2D;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.Rect;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh2d.Voronoi;
import toxi.processing.ToxiclibsSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by dimitris on 11/4/15.
 */
public class Country {
    private ArrayList<Polygon2D> polygons;
    private String name;
    private String fips;
    private String type;
    private int numberOfPolygons;
    private PApplet pa;
    private Polygon2D poly;
    float DELAUNAY_SIZE = 10000;
    private float mapGeoLeft = -180;
    private float mapGeoRght = 180;
    private float mapGeoTop = 90;
    private float mapGeoBottom = 90;
    private TriangleMesh mesh;
    private ToxiclibsSupport toxi;
    private int resolution = 4;
    private List<Triangle2D> tr = new ArrayList<>();
    private List<List<Triangle2D>> outer = new ArrayList<>();
    private ArrayList<Mesh3D> meshes = new ArrayList<>();


    public Country(PApplet pa, String name, String type, JSONArray coordinates) {
        this.pa = pa;
        this.name = name;
        this.type = type;
        this.numberOfPolygons = numberOfPolygons;
        Polygon2D poly = null;
        toxi = new ToxiclibsSupport(this.pa);
        this.polygons = new ArrayList<>();
        Rect r = null;
        if (type.equals("Polygon")) {
            JSONArray finalArray = (JSONArray) coordinates.get(0);
            poly = new Polygon2D();
            for (int i = 0; i < finalArray.size(); i++) {
                JSONArray point = (JSONArray) finalArray.get(i);
                double lon = ((Double) point.get(0)).doubleValue();
                double lat = ((Double) point.get(1)).doubleValue();
                float f_lon = (float) lon;
                float f_lat = (float) lat;
                poly.add(toApplicationDimension(new Vec2D(f_lon, f_lat)));
                //  r=computeBounds(poly);
            }
            polygons.add(poly);
        } else if (type.equals("MultiPolygon")) {
            for (int i = 0; i < coordinates.size(); i++) {
                int finalOneArraySize = ((JSONArray) coordinates.get(i)).size();
                JSONArray finalOneArray = (JSONArray) coordinates.get(i);
                for (int k = 0; k < finalOneArraySize; k++) {
                    int finalArrayToPolygonSize = ((JSONArray) finalOneArray.get(k)).size();
                    JSONArray finalArrayToPolygon = (JSONArray) finalOneArray.get(k);
                    poly = new Polygon2D();
                    for (int l = 0; l < finalArrayToPolygonSize; l++) {
                        JSONArray coord = (JSONArray) finalArrayToPolygon.get(l);
                        double lon = ((Double) coord.get(0)).doubleValue();
                        double lat = ((Double) coord.get(1)).doubleValue();
                        float f_lon = (float) lon;
                        float f_lat = (float) lat;
                        poly.add(toApplicationDimension(new Vec2D(f_lon, f_lat)));
                    }
                }
                polygons.add(poly);
            }
        }
        //System.out.println(name+" "+r);
        for (Polygon2D p : polygons) {
            try {
                tr = tesselatePolygon(p, resolution);
                outer.add(tr);
            } catch (NoSuchElementException e) {
                //System.out.println(name + " " + this.type);
                // e.printStackTrace();
            }
        }
//        try {
//            TriangleMesh m = buildSurfaceMesh(outer.get(0),200);
//            System.out.println(m.getFaces().size());
//        }catch(IndexOutOfBoundsException e){
//           // System.out.println(name);
//        }

        for (List<Triangle2D> t : outer) {
            meshes.add(buildSurfaceMesh(t, 200));
        }
    } //CONSTRUCTOR


    public ArrayList<Polygon2D> getPolygons() {
        return this.polygons;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    private Vec2D toApplicationDimension(Vec2D v) {
        Vec2D result = new Vec2D();
        float x = this.pa.width * (v.x() - (-180)) / (180 - (-180));
        float y = (this.pa.height - this.pa.height * (v.y() - (-90)) / (90 - (-90)));
        result.set(x, y);
        Vec3D vv = new Vec3D(x, y, 0).toCartesian();
        return result;

    }

    Rect computeBounds(Polygon2D poly) {
        Vec2D min = Vec2D.MAX_VALUE.copy();
        Vec2D max = Vec2D.MIN_VALUE.copy();
        for (Vec2D v : poly.vertices) {
            min.minSelf(v);
            max.maxSelf(v);
        }
        return new Rect(min, max);
    }

    private List<Vec2D> createInsidePoints(Polygon2D poly, int num) {
        List<Vec2D> points = new ArrayList<Vec2D>(num);
        Rect bounds = computeBounds(poly);
        for (int y = 0; y < num; y++) {
            float yy = this.pa.map(y, 0, num, bounds.getTop(), bounds.getBottom());
            for (int x = 0; x < num; x++) {
                Vec2D p = new Vec2D(this.pa.map(x, 0, num, bounds.getLeft(), bounds.getRight()), yy);
                if (poly.containsPoint(p)) {
                    points.add(p);
                }
            }
        }

        return points;
    }

    private List<Triangle2D> tesselatePolygon(Polygon2D poly, int num) {
        List<Triangle2D> result = new ArrayList<Triangle2D>();
        // a Voronoi diagram relies on a Delaunay triangulation behind the scenes
        Voronoi voronoi = new Voronoi(DELAUNAY_SIZE);
        // add perimeter points
        for (Vec2D v : poly.vertices) {
            voronoi.addPoint(v);
        }
        // add random inliers
        for (Vec2D v : createInsidePoints(poly, num)) {
            voronoi.addPoint(v);
        }
        // get filtered delaunay triangles:
        // ignore any triangles which share a vertex with the initial root triangle
        // or whose centroid is outside the polygon
        for (Triangle2D t : voronoi.getTriangles()) {
            if (Math.abs(t.a.x) != DELAUNAY_SIZE && Math.abs(t.a.y) != DELAUNAY_SIZE) {
                if (poly.containsPoint(t.computeCentroid())) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    TriangleMesh buildSurfaceMesh(List<Triangle2D> tris, float radius) {
        TriangleMesh mesh = new TriangleMesh();
        for (Triangle2D t : tris) {
            // ensure all triangles have same orientation
            if (!t.isClockwise()) {
                t.flipVertexOrder();
            }
            // lat/lon => spherical => cartesian mapping
            Vec3D a = new Vec3D(radius, this.pa.radians(t.a.x), this.pa.radians(t.a.y)).toCartesian();
            Vec3D b = new Vec3D(radius, this.pa.radians(t.b.x), this.pa.radians(t.b.y)).toCartesian();
            Vec3D c = new Vec3D(radius, this.pa.radians(t.c.x), this.pa.radians(t.c.y)).toCartesian();
            // add 3D triangle to mesh
            mesh.addFace(a, b, c);
        }
        // needed for smooth shading
        mesh.computeVertexNormals();
        return mesh;
    }


    public void drawTriangles() {
        this.pa.pushStyle();
        this.pa.stroke((float) .1);
        this.pa.beginShape(PConstants.TRIANGLE);
//        for (Triangle2D t : tr) {
//            toxi.triangle(t);
//        }
        for (List<Triangle2D> out : outer) {
            for (Triangle2D t : out) {
                toxi.triangle(t);
            }
        }
        this.pa.endShape();
        this.pa.popStyle();
    }

    public void drawMeshes() {
        for (Mesh3D m : meshes) {
            toxi.mesh(m, true, 20);
        }
    }

    public void drawPoints(){
        for (Polygon2D p:polygons){
            for (Vec2D v:p.vertices){
                Vec3D v1=new Vec3D(200,this.pa.radians(v.x)+this.pa.PI,this.pa.radians(v.y)).toCartesian();
                this.pa.point(v1.x,v1.y,v1.z);
            }
        }
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
