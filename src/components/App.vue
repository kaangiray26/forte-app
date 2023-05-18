<template>
    <div class="vw-100 complete-view d-flex flex-column">
        <Messages />
        <Toasts />
        <Animation />
        <div class="side-view d-flex">
            <div class="content-view">
                <NavigationBar />
                <ContentView ref="thisContentView" />
            </div>
        </div>
        <div class="player-view">
            <Player ref="thisPlayer" />
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import { action } from '/js/events.js';
import { store } from '/js/store.js';
import { useRouter } from 'vue-router';
import { App } from '@capacitor/app';
import NavigationBar from './NavigationBar.vue';
import ContentView from './ContentView.vue';
import Player from './Player.vue';
import Toasts from './Toasts.vue';
import Messages from './Messages.vue';
import Animation from './Animation.vue';

const router = useRouter();

const thisContentView = ref(null);
const thisPlayer = ref(null);

async function keyPress(event) {
    // Must be synchronized in groupSession: ok
    if (event.target.tagName != 'INPUT' && event.target.tagName != 'TEXTAREA' && event.code == 'Space') {
        event.preventDefault();
        action({
            func: async function op() {
                ft.play();
            },
            object: [null],
            operation: "play"
        });
        return;
    }

    // Must be synchronized in groupSession: ok
    if (event.target.tagName != 'INPUT' && event.target.tagName != 'TEXTAREA' && event.key == 'ArrowLeft') {
        event.preventDefault();
        action({
            func: async function op() {
                ft.play_previous();
            },
            object: [null],
            operation: "playPrevious"
        });
        return;
    }

    // Must be synchronized in groupSession: ok
    if (event.target.tagName != 'INPUT' && event.target.tagName != 'TEXTAREA' && event.key == 'ArrowRight') {
        event.preventDefault();
        action({
            func: async function op() {
                ft.play_next();
            },
            object: [null],
            operation: "playNext"
        });
        return;
    }
}

onBeforeMount(() => {
    // Theme
    let theme = JSON.parse(localStorage.getItem("theme"));
    if (!theme) {
        if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
            document.body.classList = "dark-theme";
            store.theme = "dark";
        }
    } else {
        document.body.classList = theme + "-theme";
        store.theme = theme;
    }

    // let vh = window.innerHeight * 0.01;
    // Then we set the value in the --vh custom property to the root of the document
    // document.documentElement.style.setProperty('--vh', `${vh}px`);

    window.focus();
    window.addEventListener('keydown', keyPress);

    App.addListener('appUrlOpen', (data) => {
        console.log("APP URL OPEN", data);
        // Example url: https://forte.buzl.uk/albums/1234
        // slug = /albums/1234
        const slug = data.url.split("https://forte.buzl.uk").pop();
        if (slug) {
            console.log("ROUTING TO", slug);
            router.push(slug);
        }
    });
})
</script>