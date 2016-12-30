package com.mti.videodiary.data;

import static java.io.File.separator;

/**
 * Created by Terry on 11/5/2016.
 */

public class Constants {
    public static final String TAG = "com.video.diary";

    private static final String APPLICATION_DIRECTORY = "videoDairy";
    public static final String VIDEO_DIR = separator + APPLICATION_DIRECTORY + separator + "video";
    public static final String NOTE_DIR = separator + APPLICATION_DIRECTORY + separator + "note";
    public static final String IMAGE_DIR = separator + APPLICATION_DIRECTORY + separator + "image";
    public static final String FILE_FORMAT = ".mp4";
    public static String VIDEO_FILE_NAME = separator + "video-dairy" + FILE_FORMAT;
    public static final int RESULT_LOAD_IMAGE = 133;

    public static final String IMAGE_HEADER_MENU = TAG + "image.header";
    public static final String KEY_PERSON_NAME = TAG + "personal.name";

    public static final String KEY_POSITION = TAG + "position";
    public static final String KEY_VIDEO_PATH = TAG + "key-video-file-path";
    public static final String ORIENTATION = TAG + ".orientation";
    public static final String WIDTH = TAG + ".width";
    public static final String HEIGHT = TAG + ".height";

}
