<template>
    <ul class="nav nav-pills">
        <li class="nav-item">
            <router-link to="/profile" class="nav-link fw-bold  theme-color">Profile</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/history" class="nav-link fw-bold  theme-color">Listening
                History</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/tracks" class="nav-link fw-bold  theme-color">Favorite
                Tracks</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/playlists" class="nav-link fw-bold  theme-color">Playlists</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/albums" class="nav-link fw-bold  theme-color">Albums</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/artists" class="nav-link fw-bold  theme-color">Artists</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/stations" class="nav-link fw-bold  theme-color">Stations</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/friends" class="nav-link fw-bold  theme-color">Friends</router-link>
        </li>
        <li class="nav-item">
            <router-link to="/profile/downloads" class="nav-link fw-bold theme-btn  text-white">Downloads</router-link>
        </li>
    </ul>
    <hr />
    <ul class="list-group">
        <li class="list-group-item rounded mb-1 theme-btn text-light d-flex">
            <div class="d-flex w-100 justify-content-between">
                <div>
                    <span class="fw-bold">{{ total }} downloaded tracks</span>
                </div>
            </div>
        </li>
        <li v-if="tracks.length == 0"
            class="list-group-item theme-list-item-no-hover foreground d-flex justify-content-between">
            <div class="d-flex flex-fill align-items-center">
                <div class="d-flex flex-column">
                    <span class="theme-color fw-bold">No tracks found</span>
                </div>
            </div>
        </li>
        <li class="list-group-item theme-list-item clickable rounded d-flex justify-content-between p-1"
            v-for="track in tracks" @contextmenu.prevent="right_click({ item: track, event: $event })"
            @click="playTrack(track)">
            <div class="d-flex flex-fill foreground align-items-center">
                <img :src="get_cover(track.cover)" class="track-cover theme-border rounded" @error="placeholder" />
                <div class="d-flex align-items-center">
                    <button class="btn btn-link search-link d-flex text-start py-0" :content_id="track.id"
                        :content_type="track.type" style="display:contents;">
                        <span class="theme-color text-break">{{
                            track.title }}</span>
                    </button>
                </div>
            </div>
        </li>
    </ul>
    <div class="d-flex justify-content-end mt-3">
        <button v-show="searchFinished" type="button" class="btn theme-btn  text-white fw-bold" @click="get_tracks">Load
            more</button>
        <button v-show="!searchFinished" class="btn btn-dark" type="button" disabled>
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            Loading...
        </button>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { right_click, action } from '/js/events.js';

const tracks = ref([]);

const total = ref(0);
const searchFinished = ref(true);

function get_cover(cover) {
    if (!cover) {
        return "/images/track.svg"
    }

    if (cover.startsWith("http")) {
        return cover;
    }

    return ft.server + '/' + cover;
}

async function placeholder(obj) {
    obj.target.src = "/images/track.svg";
}

// Must be synchronized in groupSession: ok
async function playTrack(track) {
    action({
        func: async function op() {
            ft.playLocalTrack(track.id)
        },
        object: [track.id],
        operation: "playTrack"
    })
}

async function get_local_tracks() {
    if (!searchFinished.value) {
        return
    }
    searchFinished.value = false;

    let data = await ft.get_local_tracks();
    if (!data) return;

    // Push track placeholders
    for (let i = 0; i < data.length; i++) {
        tracks.value.push(data[i]);
    }

    total.value = data.length;
    searchFinished.value = true;
}

onMounted(() => {
    get_local_tracks();
})
</script>