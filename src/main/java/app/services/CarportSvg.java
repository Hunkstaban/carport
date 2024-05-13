package app.services;

public class CarportSvg {
    private static final double DISTANCE_BETWEEN_RAFTERS = 55.714;
    private static final int TOP_BOTTOM_OFFSET = 35;
    private static final int FRONT_OFFSET = 110;
    private static final double BACK_OFFSET = 27.5;
    private static int numberOfPosts = ProductListCalc.getNumberOfPosts();
    private static int postSize = 10;
//    private static String style = "stroke-width:1px; stroke:#000000; fill: rgba(0,0,0,0)";
    private static String style = "stroke-width:1px; stroke:#000000; fill: #ffffff";
    int shed_width = 140;
    private int width;
    private int length;
    private boolean shed;
    private Svg carportSvg;

    public CarportSvg(int clength, int width, boolean shed) {
        this.length = clength + (shed ? shed_width : 0);
        this.width = width;
        this.shed = shed;
        carportSvg = new Svg(0, 0, "0 0 " + (length + 5) + " " + width, "50%");
        carportSvg.addRectangle(0, 0, width, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        if (shed == true) {
            addShed();
        }
        addBeams();
        addRafters();
        addPosts();
    }

    private void addShed() {
//        carportSvg.addRectangle(TOP_BOTTOM_OFFSET, length-BACK_OFFSET-140, width-TOP_BOTTOM_OFFSET, 140, "stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(length-BACK_OFFSET-shed_width, TOP_BOTTOM_OFFSET, width-(TOP_BOTTOM_OFFSET*2)+(postSize/2), shed_width+(postSize/2), "stroke:#000000; fill: #c9c9c9");
//        Posts
//        Top
        carportSvg.addRectangle(length-BACK_OFFSET-shed_width, TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Bottom
        carportSvg.addRectangle(length-BACK_OFFSET-shed_width, width - TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Middle left
        carportSvg.addRectangle(length-BACK_OFFSET-shed_width, width/2-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Middle Right
        carportSvg.addRectangle(length-BACK_OFFSET, width/2-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
    }

    private void addBeams() {
//        35 cm offset each side
        carportSvg.addRectangle(0, 35, 5, length, "stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0, width-35, 5, length, "stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        for (double i = 0; i < length; i += DISTANCE_BETWEEN_RAFTERS) {
            carportSvg.addRectangle(i, 0.0, width, 4.5, "stroke:#000000; fill: #ffffff");
        }
    }

    private void addPosts() {
//        27,5cm offset at the back and 110 cm offset in front
//        Top left
        carportSvg.addRectangle(FRONT_OFFSET-(postSize/4), TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Bottom Left
        carportSvg.addRectangle(FRONT_OFFSET-(postSize/4), width-TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Top Right
        carportSvg.addRectangle(length-BACK_OFFSET-(postSize/4), TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//        Bottom Right
        carportSvg.addRectangle(length-BACK_OFFSET-(postSize/4), width-TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");

        if (numberOfPosts > 4) {
            double frontPos = FRONT_OFFSET-(postSize/4);
            double backPos = length-BACK_OFFSET-(postSize/4);
//            Top left
            carportSvg.addRectangle((frontPos+backPos)/2, TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
//            Bottom Left
            carportSvg.addRectangle((frontPos+backPos)/2, width-TOP_BOTTOM_OFFSET-(postSize / 4), postSize, postSize, "stroke:#000000; fill: #ffffff");
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}