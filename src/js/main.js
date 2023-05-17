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
import '/assets/bootstrap-icons.css'

window.ft = new Forte();
window.ft.init().then(async () => {
    // setPlaybackState to NONE
    await MediaControl.setPlaybackState({
        state: "none"
    });

    // // play Action Listener
    await MediaControl.addListener('fortePlayAction', () => {
        action({
            func: async function op() {
                window.ft.play();
            },
            object: [null],
            operation: "play"
        });
    });

    // // pause Action Listener
    await MediaControl.addListener('fortePauseAction', () => {
        action({
            func: async function op() {
                window.ft.play();
            },
            object: [null],
            operation: "play"
        });
    });

    // // next Action Listener
    await MediaControl.addListener('forteNextAction', () => {
        action({
            func: async function op() {
                window.ft.play_next();
            },
            object: [null],
            operation: "playNext"
        });
    });

    // // previous Action Listener
    await MediaControl.addListener('fortePreviousAction', () => {
        action({
            func: async function op() {
                window.ft.play_previous();
            },
            object: [null],
            operation: "playPrevious"
        });
    });

    console.log("Forte initialized.");
    createApp(App).use(router).mount('#app');
});