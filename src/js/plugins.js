// plugins.js
import { registerPlugin } from '@capacitor/core';

export let MediaControl;

if (window.navigator === undefined) {
    MediaControl = {
        setPlaybackState: async function (obj) {
            navigator.mediaSession.playbackState = obj.state;
            return
        },
        addListener: async function (obj) {
            return
        },
        setMetadata: async function (obj) {
            navigator.mediaSession.metadata = new MediaMetadata(obj);
            return
        }
    }
} else {
    MediaControl = registerPlugin('MediaControl');
}