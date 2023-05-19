<template>
    <div ref="offcanvasEl" class="offcanvas offcanvas-bottom bg-dark border-0 vh-100 vw-100" tabindex="-1"
        id="offcanvasBottom" aria-labelledby="offcanvasBottomLabel">
        <div class="offcanvas-body">
            <div class="row h-100 justify-content-center align-items-end g-0">
                <div class="col h-100">
                    <div ref="cardView" class="card border-0 h-100">
                        <div>
                            <div class="m-3">
                                <img class="card-img-top image-stable rounded" :src="get_cover(store.playing.cover)"
                                    @error="placeholder">
                            </div>
                        </div>
                        <div class="card-body d-flex align-items-end p-2">
                            <div class="d-flex flex-fill flex-column">
                                <div class="overflow-hidden text-start mb-4">
                                    <div class="text-wrap clickable" @click="openAlbum">
                                        <h5 class="fw-bold  theme-color rounded mb-0">{{
                                            store.playing.title }}
                                        </h5>
                                    </div>
                                </div>
                                <div class="progress mb-2" @click="props.seekProgress($event)">
                                    <div class="progress-bar theme-btn progress-bar-animated" aria-valuenow="0"
                                        aria-valuemin="0" aria-valuemax="100"
                                        :style="{ 'width': store.playing.progress + '%' }">
                                        <span class="visually-hidden"></span>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <div class="d-flex align-items-center font-monospace theme-color me-2">
                                        <small>{{ formatTime(store.playing.seek) }}</small>
                                    </div>
                                    <div class="d-flex align-items-center font-monospace theme-color me-2">
                                        <small>{{ formatTime(store.playing.duration) }}</small>
                                    </div>
                                </div>
                                <!-- First set of buttons -->
                                <div class="btn-group btn-group justify-content-center" role="group">
                                    <div class="d-flex">
                                        <div class="d-flex align-items-center">
                                            <button type="button"
                                                class="btn player-btn btn-lg theme-color bi bi-skip-start-fill"
                                                @click="play_previous"></button>
                                        </div>
                                        <div class="d-flex align-items-center">
                                            <button type="button" class="btn btn-lg btn-light action-btn bi" :class="{
                                                'bi-play-fill': !store.playing.is_playing, 'bi-pause-fill': store.playing.is_playing
                                            }" @click="play"></button>
                                        </div>
                                        <div class="d-flex align-items-center">
                                            <button type="button"
                                                class="btn player-btn btn-lg theme-color rounded bi bi-skip-end-fill"
                                                @click="play_next"></button>
                                        </div>
                                    </div>
                                </div>
                                <hr />
                                <!-- Second set of buttons -->
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn player-btn theme-color bi bi-shuffle mx-1"
                                        @click="emit('shuffle')" />
                                    <button type="button" class="btn player-btn theme-color bi bi-soundwave mx-1"
                                        @click="emit('group_session')" />
                                    <button type="button"
                                        class="btn player-btn theme-color bi bi-chat-square-text-fill mx-1"
                                        @click="emit('lyrics')" />
                                    <button type="button" class="btn player-btn theme-color bi bi-collection-fill mx-1"
                                        @click="emit('queue')" />
                                    <button type="button" class="btn player-btn bi mx-1" :class="props.repeat_icon"
                                        @click="repeat" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { Offcanvas } from "bootstrap"
import { store } from "/js/store.js"
import { action } from '/js/events.js';
import Hammer from "hammerjs";

const router = useRouter();

const emit = defineEmits(['queue', 'lyrics', 'shuffle', 'group_session', 'quality']);

let offcanvas = null;
const offcanvasEl = ref(null);
const cardView = ref(null);

async function placeholder(obj) {
    obj.target.src = "/images/track.svg";
}

async function openAlbum() {
    store.selected_track_id = store.playing.id;
    router.push("/album/" + store.playing.album);
    _hide();
}

function get_cover(cover) {
    if (!cover) {
        return "/images/track.svg"
    }

    if (cover.startsWith("http")) {
        return cover;
    }

    return ft.server + '/' + cover;
}

function _show() {
    offcanvas.show();
}

function _hide() {
    offcanvas.hide();
}

function formatTime(milisecs) {
    let seconds = Math.floor(milisecs / 1000);
    let minutes = Math.floor(seconds / 60);
    seconds = seconds % 60;
    return minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
}

// Must be synchronized in groupSession: ok
async function repeat() {
    action({
        func: async function op() {
            ft.repeat();
        },
        object: [null],
        operation: "repeat"
    });
}

defineExpose({
    show: _show,
    hide: _hide,
})

const props = defineProps({
    seekProgress: {
        type: Function,
        default: () => { }
    },
    play: {
        type: Function,
        default: () => { }
    },
    play_next: {
        type: Function,
        default: () => { }
    },
    play_previous: {
        type: Function,
        default: () => { }
    },
    repeat_icon: {
        type: String,
        default: "bi-repeat text-muted"
    },
})

onMounted(() => {
    let hammertime = new Hammer(cardView.value);
    hammertime.get('swipe').set({ direction: Hammer.DIRECTION_ALL });
    hammertime.on("swipeleft", () => {
        props.play_next();
    });
    hammertime.on("swiperight", () => {
        props.play_previous();
    });
    hammertime.on("swipedown", function () {
        _hide();
    });

    offcanvas = new Offcanvas(offcanvasEl.value);
})
</script>