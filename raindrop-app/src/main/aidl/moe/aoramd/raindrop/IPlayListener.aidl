package moe.aoramd.raindrop;

import android.os.Bundle;
import moe.aoramd.raindrop.service.SongMedium;

interface IPlayListener {
    void onPlayingSongChanged(in SongMedium songMedium, int index);

    void onPlayingListChanged(in List<SongMedium> songMediums);

    void onPlayingProgressChanged(float progress);

    void onPlayingStateChanged(int state);

    void onPlayingShuffleModeChanged(int mode);

    void eventListener(String event);
}