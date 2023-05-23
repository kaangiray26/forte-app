<template>
    <ul class="nav nav-pills">
        <li class="nav-item">
            <router-link to="/profile" class="nav-link fw-bold theme-btn  text-white">Profile</router-link>
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
            <router-link to="/profile/downloads" class="nav-link fw-bold  theme-color">Downloads</router-link>
        </li>
    </ul>
    <hr>
    <div class="d-inline-flex flex-column">
        <h3 class="theme-color">Last.fm</h3>
        <div v-if="lastfm_profile" class="d-inline-flex border rounded mb-2 p-2">
            <div class="d-flex">
                <img :src="lastfm_profile.image[3]['#text']" class="img-lastfm" width="50" height="50" />
            </div>
            <div class="d-flex flex-column mx-2">
                <span class="theme-color fw-bold">{{ lastfm_profile.name }}</span>
                <div class="d-flex flex-wrap align-items-center">
                    <span class="text-muted fw-bold">Scrobbles:</span>
                    <span class="badge theme-btn mx-2">{{ formatNumber(lastfm_profile.playcount) }}</span>
                </div>
            </div>
        </div>
        <div class="input-group flex-nowrap mb-2">
            <span class="input-group-text foreground-no-border theme-color theme-border" id="basic-addon1">Scrobbling</span>
            <button v-show="!lastfm_config.scrobbling" type="button" class="btn btn-danger flex-fill rounded-end"
                @click="toggle_scrobbling">Off</button>
            <button v-show="lastfm_config.scrobbling" type="button" class="btn btn-success flex-fill rounded-end"
                @click="toggle_scrobbling">On</button>
        </div>
        <div v-if="lastfm_auth" class="border theme-border rounded p-2">
            <div class="input-group">
                <span class="input-group-text bi bi-person-circle" id="basic-addon2"></span>
                <input ref="username" type="text" class="form-control" placeholder="Username" aria-label="Username"
                    aria-describedby="basic-addon2">
            </div>
            <div class="input-group mb-2">
                <span class="input-group-text bi bi-key-fill" id="basic-addon3"></span>
                <input ref="password" type="password" class="form-control" placeholder="Password" aria-label="Password"
                    aria-describedby="basic-addon3">
            </div>
            <div class="d-flex flex-column">
                <button class="btn theme-btn text-white fw-bold" @click="get_mobile_session">Login</button>
            </div>
        </div>
        <button v-if="lastfm_profile" class="btn theme-btn text-white fw-bold mb-2" @click="remove_account">Remove
            account</button>
    </div>
    <div v-if="lastfm_profile" class="d-flex flex-column mt-4">
        <h5 class="theme-color">Top Tracks This Week</h5>
        <ul class="list-group list-group-flush flex-fill rounded">
            <li v-for="track in top_tracks" class="list-group-item list-group-item-action clickable"
                @click="openTrack(track)">
                <div class="d-flex">
                    <img :src="track.image[3]['#text']" class="img-lastfm" width="50" height="50" />
                    <div class="d-flex flex-fill justify-content-between">
                        <div class="d-flex flex-column mx-2">
                            <span class="fw-bold">{{ track.artist.name }}</span>
                            <span>{{ track.name }}</span>
                        </div>
                        <div class="d-flex align-items-center">
                            <span class="badge bg-dark">{{ track.playcount }}</span>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
    </div>
    <hr>
    <div class="d-inline-flex">
        <div class="d-flex flex-column">
            <router-link to="/servers">
                <button class="btn theme-btn  text-white fw-bold flex-nowrap text-start m-1">
                    <span class="bi bi-globe me-2"></span>
                    <span>Servers</span>
                </button>
            </router-link>
            <button class="btn theme-btn  text-white fw-bold flex-nowrap text-start m-1" @click="change_theme">
                <span class="bi me-2"
                    :class="{ 'bi-sun-fill': store.theme == 'light', 'bi-moon-fill': store.theme == 'dark' }"></span>
                <span>Theme</span>
            </button>
            <button class="btn theme-btn  text-white fw-bold flex-nowrap text-start m-1" @click="reset_menu">
                <span class="bi bi-box-arrow-right me-2"></span>
                <span>Log out</span>
            </button>
        </div>
    </div>
    <hr />
    <div class="d-flex align-items-center">
        <button class="btn theme-btn text-white fw-bold" @click="github_repo">
            <span class="bi bi-android me-2"></span>
            <span>Forte May 3 Version.</span></button>
    </div>
    <Reset ref="resetModal" />
</template>

<script setup>
import { onBeforeMount, ref } from 'vue'
import { store } from '/js/store.js';
import { useRouter } from 'vue-router';
import Reset from '/components/Reset.vue';

const router = useRouter();

const resetModal = ref(null);

const lastfm_auth = ref(false);
const username = ref(null);
const password = ref(null);

const lastfm_config = ref({});
const lastfm_api_key = ref(null);
const lastfm_profile = ref(null);

const top_tracks = ref([]);

async function get_mobile_session() {
    if (!username.value.value.length) {
        username.value.focus();
        return
    }

    if (!password.value.value.length) {
        password.value.focus();
        return
    }

    let response = await ft.lastfm_mobile_auth(username.value.value, password.value.value);
    if (!response || response.error) {
        return
    }

    localStorage.setItem('lastfm_username', JSON.stringify(response.username));
    localStorage.setItem('lastfm_key', JSON.stringify(response.key));
    localStorage.setItem('scrobbling', JSON.stringify(true));
    store.scrobbling = true;
    lastfm_auth.value = false;

    lastfm_config.value['scrobbling'] = true;
    lastfm_config.value['lastfm_key'] = response.key;

    // load lastfm profile
    get_lastfm_profile();
    get_top_tracks();
}

async function change_theme() {
    store.theme = store.theme == 'dark' ? 'light' : 'dark';
    localStorage.setItem("theme", JSON.stringify(store.theme));
    document.body.className = store.theme + '-theme';
}

async function reset_menu(ev) {
    ev.preventDefault();
    resetModal.value.show();
}

async function openTrack(track) {
    window.open(track.url, '_blank');
}

async function toggle_scrobbling() {
    if (!lastfm_config.value.lastfm_key) {
        lastfm_auth.value = true;
        return
    }

    lastfm_config.value.scrobbling = !lastfm_config.value.scrobbling;
    localStorage.setItem('scrobbling', JSON.stringify(lastfm_config.value.scrobbling));
    store.scrobbling = lastfm_config.value.scrobbling;
}

async function get_lastfm_profile() {
    let username = JSON.parse(localStorage.getItem('lastfm_username'));
    if (!username) {
        return
    }

    let response = await fetch(`https://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=${username}&api_key=${lastfm_api_key.value}&format=json`)
        .then(res => res.json());
    lastfm_profile.value = response.user;
}

async function get_top_tracks() {
    let username = JSON.parse(localStorage.getItem('lastfm_username'));
    if (!username) {
        return
    }

    let response = await fetch(`https://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&limit=24&period=7day&user=${username}&api_key=${lastfm_api_key.value}&format=json`)
        .then(res => res.json());
    top_tracks.value = response.toptracks.track;
}

async function remove_account() {
    localStorage.removeItem('lastfm_key');
    localStorage.removeItem('lastfm_username');
    localStorage.removeItem('scrobbling');
    lastfm_config.value = {};
    lastfm_profile.value = null;
}

function formatNumber(num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
}

async function github_repo() {
    window.open("https://github.com/kaangiray26/forte-app", "_blank");
    return
}

async function setup() {
    ['lastfm_key', 'scrobbling'].forEach((key) => {
        let value = JSON.parse(localStorage.getItem(key));
        if (value) {
            lastfm_config.value[key] = value;
        } else {
            lastfm_config.value[key] = null
        }
    })

    let response = await ft.API('/lastfm/auth');
    if (response.hasOwnProperty('error')) {
        return
    }
    lastfm_api_key.value = response.api_key;

    get_lastfm_profile();
    get_top_tracks();
}

onBeforeMount(() => {
    if (store.offline) {
        router.push("/offline");
        return
    }
    setup();
})
</script>