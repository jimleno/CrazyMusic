package com.zyb.example.model;

public class Song {

	//歌曲的名称
	private String mSongName;
	//歌曲的文件名
	private String mSongFileName;
	//歌曲的名字长度
	private int mNameLength;
	
	public char[] getNameCharacters(){
		return mSongName.toCharArray();
	}
	
	
	public String getSongName() {
		return mSongName;
	}
	public void setSongName(String SongName) {
		this.mSongName = SongName;
		this.mNameLength=SongName.length();
	}
	public String getSongFileName() {
		return mSongFileName;
	}
	public void setSongFileName(String SongFileName) {
		this.mSongFileName = SongFileName;
	}
	public int getNameLength() {
		return mNameLength;
	}
	public void setNameLength(int NameLength) {
		this.mNameLength = NameLength;
	}
	
}
