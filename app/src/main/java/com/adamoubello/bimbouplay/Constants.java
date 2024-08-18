package com.adamoubello.bimbouplay;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.adamoubello.bimbouplay.action.main";
        public static String PREV_ACTION = "com.adamoubello.bimbouplay.action.prev";
        public static String PLAY_ACTION = "com.adamoubello.bimbouplay.action.play";
        public static String NEXT_ACTION = "com.adamoubello.bimbouplay.action.next";
        public static String STARTFOREGROUND_ACTION = "com.adamoubello.bimbouplay.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.adamoubello.bimbouplay.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
