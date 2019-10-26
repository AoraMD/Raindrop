package moe.aoramd.raindrop;

import moe.aoramd.raindrop.service.SongMedium;

interface IPlayListener {
    void onPlayingSongChanged(in SongMedium songMedium, int index);

    void onPlayingListChanged(in List<SongMedium> songMediums);

    void onPlayingProgressChanged(float progress);

    void onPlayingStateChanged(int state);
}