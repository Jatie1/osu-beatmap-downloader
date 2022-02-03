package com.jatie;

public class Beatmap {
    private final int setId;
    private final String artistName;
    private final String songName;
    private final boolean isRemoved;

    public Beatmap(int setId, String artistName, String songName, boolean isRemoved) {
        this.setId = setId;
        this.artistName = artistName;
        this.songName = songName;
        this.isRemoved = isRemoved;
    }

    public int getSetId() {
        return setId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongName() {
        return songName;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beatmap beatmap = (Beatmap) o;
        return setId == beatmap.setId;
    }

    @Override
    public int hashCode() {
        return setId;
    }
}
