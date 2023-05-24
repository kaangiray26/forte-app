// plugins.js
import { registerPlugin } from '@capacitor/core';

let MediaControl;
let NativePlayer;

if (window.navigator.mediaSession) {
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
    };
    NativePlayer = {
        playDataSource: async function (obj) {
            return
        },
        addListener: async function (obj) {
            return
        },
        removeAllListeners: async function (obj) {
            return
        },
        play: async function (obj) {
            return
        },
        pause: async function (obj) {
            return
        },
    }
} else {
    MediaControl = registerPlugin('MediaControl');
    NativePlayer = registerPlugin('NativePlayer');
}

export { MediaControl, NativePlayer }