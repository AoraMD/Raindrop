package moe.aoramd.raindrop;

import moe.aoramd.raindrop.service.SongMedium;
import moe.aoramd.raindrop.IPlayListener;

import android.support.v4.media.session.MediaSessionCompat;

interface IPlayService {

    MediaSessionCompat.Token sessionToken();

    SongMedium playingSong();

    void addSong(in SongMedium songMedium);

    void addSongAsNext(in SongMedium songMedium);

    void resetPlayingList(in List<SongMedium> songMediums, long index);

    void addPlayingListener(String tag, in IPlayListener listener);

    void removePlayingListener(String tag);

    boolean emptyList();
}
