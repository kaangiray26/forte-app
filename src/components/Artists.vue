<template>
    <div v-show="!loaded">
        <div class="d-flex justify-content-center m-3">
            <button class="btn btn-dark" type="button" disabled>
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Loading...
            </button>
        </div>
    </div>
    <div v-show="loaded" class="card rounded-0 border-0 mx-3">
        <div class="card-body px-3">
            <div class="d-flex justify-content-between align-items-center">
                <h3 class="theme-color fw-bold mb-0">Artists</h3>
                <div class="dropdown">
                    <button
                        class="dropdown-toggle bi bi-layers-fill btn theme-btn black-on-hover text-white fw-bold flex-nowrap text-start"
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
            <div class="row gy-3">
                <p v-if="!artists.length" class="px-3 text-decoration-underline theme-color">No artists in the library.</p>
                <div class="col-12 pt-3" v-for="artist in artists">
                    <Artist :artist="artist" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import Artist from "/components/recommendation/Artist.vue";

const artists = ref([]);
const loaded = ref(false);
const search_finished = ref(true);

async function get_all_artists(offset = 0) {
    search_finished.value = false;

    let data = await ft.API("/artists/" + offset);
    if (!data) {
        loaded.value = true;
        return;
    }
    artists.value = artists.value.concat(data.artists);

    loaded.value = true;
    search_finished.value = true;
}

onBeforeMount(() => {
    get_all_artists();

    // User scrolled to the bottom of the page
    let content_view = document.querySelector('.content-view')
    content_view.addEventListener('scroll', () => {
        if ((content_view.scrollTop + content_view.clientHeight >= content_view.scrollHeight - 50) && search_finished.value) {
            get_all_artists(artists.value.length);
        }
    });
})
</script>