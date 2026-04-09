package com.playground;

public class Components {}
//implements ComponentsApi {
//    public static String[] getCategories() {
//        return null;
//    }
//
//    public static <T> Map<String, Map<String,T>> getSubcategories(String cat) {
//    }


//    private static final Map<String, List<ComponentVariant>> BUTTONS = new LinkedHashMap(){{
//        put("FxButton", loadVariants("playground.variants.fxbutton"));
//        put("FxLink", loadVariants("playground.variants.fxlink"));
//        put("FxToggleButton", loadVariants("playground.variants.fxtogglebutton"));
//        put("FxBinaryToggle", loadVariants("playground.variants.fxbinarytoggle"));
//        put("FxGroupToggle", loadVariants("playground.variants.fxgrouptoggle"));
//        put("FxCheckbox", loadVariants("playground.variants.fxcheckbox"));
//        put("FxToggleRadio", loadVariants("playground.variants.fxtoggleradio"));
//        put("FxRadio", loadVariants("playground.variants.fxradio"));
//        put("FxToggleRadios", loadVariants("playground.variants.fxtoggleradios"));
//        put("FxRadios", loadVariants("playground.variants.fxradios"));
//    }};
//
//    private static final Map<String, List<ComponentVariant>> CHARTS = new LinkedHashMap(){{
//        put("FxBarChart", loadVariants("playground.variants.fxbarchart"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> FIELDS = new LinkedHashMap() {{
//        put("FxSelectField", loadVariants("playground.variants.fxselectfield"));
//        put("FxTextField", loadVariants("playground.variants.fxtextfield"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> COMPONENTS = new LinkedHashMap() {{
//        put("FxLabel", loadVariants("playground.variants.fxlabel"));
//        put("FxStage", loadVariants("playground.variants.fxstage"));
//        put("FxTable", loadVariants("playground.variants.fxtable"));
//        put("FxLed", loadVariants("playground.variants.fxled"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> CONTAINERS = new LinkedHashMap(){{
//        put("FxBox", loadVariants("playground.variants.fxbox"));
//        put("FxHBox", loadVariants("playground.variants.fxhbox"));
//        put("FxVBox", loadVariants("playground.variants.fxvbox"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> PANES = new LinkedHashMap(){{
//        put("FxPane", loadVariants("playground.variants.fxpane"));
//        put("FxTabPane", loadVariants("playground.variants.fxtabpane"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> UTILS = new LinkedHashMap(){{
//        put("FxListeners", loadVariants("playground.variants.fxutil"));
//    }};
//
//    //<<"ComponentCategory", <"ComponentName", List<IComponentVariant>>>
//    private static Map<String, Map<String, List<ComponentVariant>>> components = new LinkedHashMap(){{
//        put("Buttons", BUTTONS);
//        put("Charts", CHARTS);
//        put("Containers", CONTAINERS);
//        put("Components", COMPONENTS);
//        put("Fields", FIELDS);
//        put("Panes", PANES);
//        put("Utils", UTILS);
//    }};
//
//    private static final Map<String, ComponentVariant[]> HISTOGRAM = new LinkedHashMap() {{
//        put("FxThresholdsHistogram", loadVariants("playground.variants.fxthresholdshistogram"));
//        put("FxThresholdsTable", loadVariants("playground.variants.fxthresholdstable"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> INSPECTOR = new LinkedHashMap() {{
//        put("FxContent", loadVariants("playground.variants.fxcontent"));
//    }};
//
//    private static final Map<String, ComponentVariant[]> DEVICE_CONFIG = new LinkedHashMap() {{
//        put("FxDeviceConfig", loadVariants("playground.variants.fxdeviceconfig"));
//        put("PhaseAssignmentPane",  loadVariants("playground.variants.fxdeviceconfig.fxphaseassignment"));
//        put("PhaseAssignmentPane2", loadVariants("playground.variants.fxdeviceconfig.fxphaseassignment2"));
//        put("FxPinSelector", loadVariants("playground.variants.fxdeviceconfig.fxpinselector"));
//    }};
//
//    private static Map<String, Map<String, List<ComponentVariant>>> features = new LinkedHashMap() {{
//        put("histogram", HISTOGRAM);
//        put("inspector", INSPECTOR);
//        put("DeviceConfig", DEVICE_CONFIG);
//    }};
//
//    private static Map<String, Map<String, Map<String, List<ComponentVariant>>>> categories = new HashMap(){{
//       put("Components", components);
//       put("Features", features);
//    }};
//
//    public static Set<String> getCategories() {
//        return categories.keySet();
//    }
//
//    public static Map<String, Map<String, List<ComponentVariant>>> getSubcategories(String category) {
//        return categories.get(category);
//    }
//
//}