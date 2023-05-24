<template>
    <div class="vw-100 complete-view d-flex flex-column">
        <Messages />
        <Toasts />
        <Animation />
        <div class="side-view d-flex">
            <div class="content-view">
                <div v-if="store.offline" class="d-flex justify-content-between align-items-center player-card">
                    <span class="fw-bold text-light p-0 px-2">Offline</span>
                    <div>
                        <button type="button" class="btn btn-touch player-card text-light bi bi-arrow-clockwise"
                            @click="reload"></button>
                    </div>
                </div>
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

async function reload() {
    window.location.reload();
    return
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

    App.addListener('appUrlOpen', (data) => {
        let slug = data.url.split("https://forte.buzl.uk").pop();
        if (slug) {
            router.push(slug);
        }
    });
})
</script>