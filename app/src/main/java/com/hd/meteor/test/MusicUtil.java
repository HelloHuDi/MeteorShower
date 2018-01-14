package com.hd.meteor.test;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hd on 2018/1/13 .
 * play music util
 */
public class MusicUtil {

    private MediaPlayer player;

    private Context context;

    public MusicUtil(Context context) {
        this.context = context;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        try{
            return player != null && player.isPlaying();
        }catch (IllegalStateException e){
            return false;
        }
    }

    public void playDefaultMusic() {
        player = MediaPlayer.create(context, R.raw.meteor);
        player.setLooping(true);
        player.start();
    }

    public void playMusic(String path) {
        try {
            Log.d("tag","current path :"+path);
            player = new MediaPlayer();
            player.setDataSource(context, Uri.parse(path));
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (IOException e) {
            Log.e("tag","play music error :"+e);
            playDefaultMusic();
        }
    }

    public void stopPlayMusic() {
        if (player != null) {
            player.stop();
            player.release();
        }
        player = null;
    }

    private String[] queryMusic = new String[]{BaseColumns._ID,//
                                               MediaStore.Audio.AudioColumns.IS_MUSIC,//
                                               MediaStore.Audio.AudioColumns.TITLE,//
                                               MediaStore.Audio.AudioColumns.ARTIST,//
                                               MediaStore.Audio.AudioColumns.ALBUM,//
                                               MediaStore.Audio.AudioColumns.ALBUM_ID,//
                                               MediaStore.Audio.AudioColumns.DATA,//
                                               MediaStore.Audio.AudioColumns.DISPLAY_NAME,//
                                               MediaStore.Audio.AudioColumns.SIZE,//
                                               MediaStore.Audio.AudioColumns.DURATION//
    };

    public List<Music> scanMusic() {
        List<Music> musicList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, queryMusic, //
                    null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return musicList;
        }
        while (cursor.moveToNext()) {
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            Music music = new Music();
            getAlbumInfo(albumId, music);
            music.setId(id);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }

    private String[] proj_album = new String[]{BaseColumns._ID, MediaStore.Audio.Albums.ALBUM_ART,//
                                               MediaStore.Audio.Albums.ALBUM, //
                                               MediaStore.Audio.Albums.NUMBER_OF_SONGS, //
                                               MediaStore.Audio.Albums.ARTIST};//

    public void getAlbumInfo(long albumId, Music music) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj_album, "_id =" + String.valueOf(albumId), null, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            music.setAlbum_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            music.setAlbum_art(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
        }
        cursor.close();
    }

}
