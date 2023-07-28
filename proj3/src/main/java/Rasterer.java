import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    public static final double LONDPP0 = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)/256;

    public Rasterer() {

    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();

        double lrlon = params.get("lrlon");
        double ullon = params.get("ullon");
        double w = params.get("w");
        int zoom = getZoom((lrlon - ullon)/w);
        int uly = getIdx(params.get("ullat"), true, zoom);
        int ulx = getIdx(ullon, false, zoom);
        int lry = getIdx(params.get("lrlat"), true, zoom);
        int lrx = getIdx(lrlon, false, zoom);

        //System.out.println(zoom +" "+ uly +" "+ ulx +" "+ lry +" "+ lrx);

        String[][] render_grid = new String[lry-uly+1][lrx-ulx+1];
        for (int i = 0; i <= lry-uly; i++) {
            for (int j = 0; j <= lrx-ulx; j++) {
                render_grid[i][j] = "d"+zoom+"_x"+(j+ulx)+"_y"+(i+uly)+".png";
            }
        }

        int totalx = (int) Math.pow(2, zoom);
        double raster_ul_lon = MapServer.ROOT_ULLON - ulx*(MapServer.ROOT_ULLON - MapServer.ROOT_LRLON)/totalx;
        double raster_ul_lat = MapServer.ROOT_ULLAT - uly*(MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT)/totalx;
        double raster_lr_lon = MapServer.ROOT_ULLON - (lrx+1)*(MapServer.ROOT_ULLON - MapServer.ROOT_LRLON)/totalx;
        double raster_lr_lat = MapServer.ROOT_ULLAT - (lry+1)*(MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT)/totalx;
        results.put("render_grid", render_grid);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("depth", zoom);
        results.put("query_success", true);
        //System.out.println(Arrays.deepToString(render_grid));
        //System.out.println(raster_ul_lon +" "+ raster_ul_lat +" "+ raster_lr_lon +" "+ raster_lr_lat);
        return results;
    }

    public static int getIdx(double target, boolean lat, int zoom){
        double max;
        double min;
        if (lat) {
            max = MapServer.ROOT_ULLAT;
            min = MapServer.ROOT_LRLAT;
        } else {
            max = MapServer.ROOT_LRLON;
            min = MapServer.ROOT_ULLON;
        }

        double sep = (max-min)/128;
        int idx = (int) Math.floor((target - min)/sep);

        if (idx > 127) {
            idx = 127;
        } else if (idx < 0) {
            idx = 0;
        }

        if (lat) {
            idx = 127 - idx;
        }

        return idx/(int) Math.pow(2, 7-zoom);
    }

    private int getZoom(double min_lonDDP) {
        //System.out.println(LONDPP0/min_lonDDP);
        int zoom = (int) Math.ceil(Math.log(LONDPP0/min_lonDDP)/Math.log(2));
        if (zoom > 7) {
            zoom = 7;
        }
        return zoom;
    }

}
