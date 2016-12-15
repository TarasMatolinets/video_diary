package com.mti.videodiary.data;

import java.io.File;

/**
 * Created by Terry on 11/5/2016.
 */

public class Constants {
    public static final String TAG = "com.video.diary";

    public static final String APPLICATION_DIRECTORY = "videoDairy";
    public static final String SEPARATOR = "/";
    public static final String VIDEO_DIR = SEPARATOR + APPLICATION_DIRECTORY + SEPARATOR + "video";
    public static final String NOTE_DIR = SEPARATOR + APPLICATION_DIRECTORY + SEPARATOR + "note";
    public static final String IMAGE_DIR = SEPARATOR + APPLICATION_DIRECTORY + SEPARATOR + "image";


    /*splash activity*/
    public static final String KEY_PERSON_NAME = "com.video.daily.personal.name";

    /*menu activity*/
    public static final String IMAGE_HEADER_MENU = "com.video.dairy.image.header";
    public static final int UPDATE_VIDEO_ADAPTER = 22;
    public static final int UPDATE_NOTE_ADAPTER = 33;
    public static final int RESULT_LOAD_IMAGE = 133;

    /*video fragment*/
    public static final String UPDATE_ADAPTER_INTENT = "com.mti.video.dairy.update.adapter";
    public static final String KEY_POSITION = "com.mti.position.key";
    public static final String FILE_FORMAT = ".mp4";
    public static String VIDEO_FILE_NAME = SEPARATOR + "video-dairy" + FILE_FORMAT;
    public static final String KEY_VIDEO_PATH = "com.mti.video-dairy.key-video-file-path";
    public static final String ORIENTATION = "com.mti.video-dairy.orientation";

/*video adapter*/


    /*note fragment*/
    public static final String KEY_POSITION_NOTE = "com.mti.position.position.note";
    public static final String UPDATE_ADAPTER_NOTE = "com.video.dairy.note.update.adapter";

    /*note adapter*/
    public static final String KEY_POSITION_NOTE_ADAPTER = "com.mti.position.position.note.adapter";

    /*image avatar*/
    public static final String IMAGE_AVATAR = "com.mti.image.avatar";
}
