<template>
    <div class="card rounded-0 border-0">
        <div class="card-body py-3">
            <div>
                <div class="input-group flex-nowrap mb-2">
                    <span class="input-group-text bi bi-broadcast" id="addon-wrapping"></span>
                    <input ref="station_name" type="text" class="form-control" placeholder="Station name"
                        aria-label="Station name" aria-describedby="addon-wrapping" @keypress.enter="search_station">
                </div>
                <ul class="list-group p-0">
                    <div v-for="station in stations">
                        <li class="list-group-item theme-list-item clickable rounded d-flex p-1">
                            <div class="d-flex w-100 foreground justify-content-between">
                                <div class="d-flex">
                                    <div class="d-inline-flex justify-content-center align-items-center position-relative">
                                        <img :src="station.image" class="track-cover theme-border rounded"
                                            @error="placeholder" />
                                        <div class="position-absolute shadow opacity-75">
                                            <button class="btn btn-light action-btn bi bi-play-fill" type="button"
                                                @click="play_station(station)">
                                            </button>
                                        </div>
                                    </div>
                                    <div class="d-flex align-items-center" @click="openStation(station.guide_id)">
                                        <button class="btn btn-link search-link d-flex flex-column text-start py-0"
                                            style="display:contents;">
                                            <span class="theme-color text-break">{{ station.text }}</span>
                                            <span class="text-muted">{{ station.subtext }}</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </div>
                </ul>
            </div>
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
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { right_click, action } from '/js/events.js';

const tracks = ref([]);

const total = ref(0);
const searchFinished = ref(true);

const stations = ref([]);
const station_name = ref(null);
const selectedStation = ref(null);

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
    let data = await ft.get_local_tracks();
    if (!data) return;

    // Push track placeholders
    for (let i = 0; i < data.length; i++) {
        tracks.value.push(data[i]);
    }

    total.value = data.length;
}

async function search_station() {
    Keyboard.hide();
    if (!searchFinished.value) {
        return
    }
    searchFinished.value = false;

    let name = station_name.value.value;
    if (!name.length) {
        return;
    }

    let data = await ft.API("/station/search/" + name);
    if (!data || data.error) {
        return;
    }

    stations.value = data.stations;
    searchFinished.value = true;
}

onMounted(() => {
    get_local_tracks();
})
</script>