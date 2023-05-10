import { createApp } from 'vue'
import App from '/components/App.vue'
import router from '/router'
import { Forte } from '/js/ft.js'
import { action } from '/js/events.js'

// Import our custom JS
import { MediaControl } from '/js/plugins.js'

// Import our custom CSS
import '/scss/styles.scss'
import '/assets/styles.css'

window.ft = new Forte();
window.ft.init().then(async () => {
    // Register our plugins
    await MediaControl.setMetadata({
        title: "Walking Down The Hill",
        artist: "Travis",
        album: "12 Memories",
        albumArt: "http://localhost:3000/c56d8e9ed79b53f2cb2df54f69832413"
    })

    // navigator.mediaSession.playbackState = "none";

    // Must be synchronized in groupSession:
    // MediaSessionAPI.addListener("play", () => {
    //     action({
    //         func: async function op() {
    //             window.ft.play();
    //         },
    //         object: [null],
    //         operation: "play"
    //     });
    // });

    // // Must be synchronized in groupSession:
    // MediaSessionAPI.addListener("pause", () => {
    //     action({
    //         func: async function op() {
    //             window.ft.play();
    //         },
    //         object: [null],
    //         operation: "play"
    //     });
    // });

    // // Must be synchronized in groupSession:
    // MediaSessionAPI.addListener("nexttrack", () => {
    //     action({
    //         func: async function op() {
    //             window.ft.play_next();
    //         },
    //         object: [null],
    //         operation: "playNext"
    //     });
    // });

    // // Must be synchronized in groupSession:
    // MediaSessionAPI.addListener("previoustrack", () => {
    //     action({
    //         func: async function op() {
    //             window.ft.play_previous();
    //         },
    //         object: [null],
    //         operation: "playPrevious"
    //     });
    // });

    // Test Metadata
    // MediaSessionAPI.setMetadata({
    //     title: "Walking Down The Hill",
    //     artist: "Travis",
    //     album: "12 Memories",
    //     cover: "http://localhost:3000/c56d8e9ed79b53f2cb2df54f69832413"
    // }).then(() => {
    //     MediaSessionAPI.play();
    // })

    console.log("Forte initialized.");
    createApp(App).use(router).mount('#app');
});