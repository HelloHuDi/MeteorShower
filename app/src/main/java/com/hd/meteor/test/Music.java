package com.hd.meteor.test;

/**
 * Created by hd on 2018/1/13 .
 * music info
 */
public class Music {
    private long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 艺术家
     */
    private String artist;
    /**
     * 专辑
     */
    private String album;
    /**
     * 专辑封面id，根据该id可以获得专辑封面图片
     */
    private long albumId ;
    /**
     * 持续时间
     */
    private long duration;
    /**
     * 音乐文件路径
     */
    private String path;
    /**
     * 音乐文件名
     */
    private String fileName;
    /**
     * 音乐文件大小
     */
    private long fileSize;
    /**
     * 专辑名
     */
    private String album_name;
    /**
     * 专辑封面图片路径
     */
    private String album_art;

    @Override
    public String toString() {
        return "Music{" + "id=" + id + ", title='" + title + '\'' + ", artist='" + artist + '\'' + ", album='" + album + '\'' + ", albumId=" + albumId + ", duration=" + duration + ", path='" + path + '\'' + ", fileName='" + fileName + '\'' + ", fileSize=" + fileSize + ", album_name='" + album_name + '\'' + ", album_art='" + album_art + '\'' + '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }
}
