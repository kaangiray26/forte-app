<template>
    <div v-show="!store.offline && !loaded">
        <div class="d-flex justify-content-center m-3">
            <button class="btn btn-dark" type="button" disabled>
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Loading...
            </button>
        </div>
    </div>
    <div class="card rounded-0 border-0" v-show="store.offline">
        <div class="card-body py-3">
            <OfflineTrack />
        </div>
    </div>
    <div class="card rounded-0 border-0 mx-3" v-show="loaded">
        <div class="card-body px-3">
            <div>
                <div class="d-flex justify-content-between align-items-center">
                    <h3 class="theme-color fw-bold mb-0">Recommended for you</h3>
                    <div class="dropdown">
                        <button
                            class="dropdown-toggle bi bi-layers-fill btn theme-btn  text-white fw-bold flex-nowrap text-start"
                            type="button" data-bs-toggle="dropdown" aria-expanded="false">
                        </button>
                        <ul class="dropdown-menu">
                            <li>
                                <router-link to="/" class="dropdown-item">
                                    <span class="bi bi-grid-fill me-1"></span>
                                    <span>Recommendations</span>
                                </router-link>
                            </li>
                            <li>
                                <router-link to="/artists" class="dropdown-item">
                                    <span class="bi bi-person-fill me-1"></span>
                                    <span>Artists</span>
                                </router-link>
                            </li>
                            <li>
                                <router-link to="/albums" class="dropdown-item">
                                    <span class="bi bi-vinyl-fill me-1"></span>
                                    <span>Albums</span>
                                </router-link>
                            </li>
                            <li>
                                <router-link to="/playlists" class="dropdown-item">
                                    <span class="bi bi-music-note-beamed me-1"></span>
                                    <span>Playlists</span>
                                </router-link>
                            </li>
                        </ul>
                    </div>
                </div>
                <p v-if="!tracks.length" class="px-3 text-decoration-underline">No content in the library.</p>
                <ul class="list-group list-group-horizontal mobile-carousel py-3">
                    <li v-for="(track, index) in tracks.slice(0, 12)" class="list-group-item theme-background border-0 p-0"
                        :class="{ 'pe-3': index < 11 }">
                        <CarouselTrack :track="track" />
                    </li>
                </ul>
                <div class="row gy-3">
                    <div v-for="track in tracks.slice(12, 24)" class="col-12">
                        <Track :track="track" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import { store } from "/js/store.js";
import Track from "/components/recommendation/Track.vue";
import CarouselTrack from "/components/recommendation/CarouselTrack.vue";
import OfflineTrack from '/components/recommendation/OfflineTrack.vue';

const tracks = ref([]);
const loaded = ref(false);

async function get_random_tracks() {
    let data = await ft.API("/random/tracks");
    if (!data) return;

    tracks.value = tracks.value.concat(data.tracks);
    loaded.value = true;
}

onBeforeMount(() => {
    get_random_tracks();
})
</script>