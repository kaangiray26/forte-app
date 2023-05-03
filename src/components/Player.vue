<template>
    <!-- Mobile player -->
    <div ref="mobilePlayer" class="player-card card text-bg-dark rounded-0">
        <div v-show="store.playing.loaded" class="card-body p-0">
            <div class="d-inline-flex flex-row w-100 align-items-center"
                :class="{ 'justify-content-between': store.playing.loaded, 'justify-content-end': !store.playing.loaded }">
                <div v-show="store.playing.loaded" class="overflow-hidden">
                    <div class="d-flex flex-row align-items-center p-2 pb-0 rounded clickable m-0" @click="openAlbum">
                        <img class="img-fluid me-2" :src="get_cover(store.playing.cover)" @error="placeholder" width="56"
                            height="56" />
                        <div class="overflow-hidden">
                            <div class="fw-bold text-nowrap clickable">{{
                                store.playing.title
                            }}</div>
                        </div>
                    </div>
                </div>
                <div class="d-flex align-items-center p-2">
                    <div class="btn-group btn-group-sm me-2" role="group" aria-label="Basic example">
                        <button type="button" class="btn btn-light bi bi-skip-start-fill" @click="play_previous"></button>
                        <button type="button" class="btn btn-light bi" :class="{
                                    'bi-play-fill': !store.playing.is_playing, 'bi-pause-fill': store.playing.is_playing
                                }" @click="play"></button>
                        <button type="button" class="btn btn-light bi bi-skip-end-fill" @click="play_next"></button>
                    </div>
                </div>
            </div>
            <div class="progress progress-minified flex-fill" @click="seekProgress($event)">
                <div class="progress-bar theme-btn progress-bar-animated" aria-valuenow="0" aria-valuemin="0"
                    aria-valuemax="100" :style="{ 'width': store.playing.progress + '%' }">
                </div>
            </div>
        </div>
    </div>
    <NavBarMobile />
    <Lyrics ref="lyricsEl" />
    <Queue ref="queueEl" />
    <MobileView ref="mobileViewEl" :seekProgress="seekProgress" :play="play" :play_next="play_next"
        :play_previous="play_previous" :repeat_icon="repeat_icon" @queue="show_queue" @lyrics="show_lyrics"
        @shuffle="shuffle" @group_session="group_session" @quality="openQuality" />
    <GroupSession ref="groupSession" :key="group_key" />
    <QualityDisplay ref="qualityDisplay" />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Tooltip } from "bootstrap"
import { store } from '/js/store.js';
import GroupSession from './GroupSession.vue';
import Hammer from "hammerjs";
import Queue from './Queue.vue';
import Lyrics from './Lyrics.vue';
import MobileView from './MobileView.vue';
import { action } from '/js/events.js';
import QualityDisplay from './QualityDisplay.vue';
import NavBarMobile from './NavBarMobile.vue';

const router = useRouter();

const queueEl = ref(null);
const lyricsEl = ref(null);

const mobileViewEl = ref(null);
const mobilePlayer = ref(null);
const volume = ref(100);

const volumeButton = ref(null);

const groupSession = ref(null);
const group_key = ref(0);

const qualityDisplay = ref(null);

async function placeholder(obj) {
    obj.target.src = "/images/track.svg";
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

async function muteVolume() {
    ft.mute();
}

const volume_icon = computed(() => {
    if (store.playing.muted) {
        return 'bi-volume-mute-fill';
    }
    if (volume.value > 75) {
        return 'bi-volume-up-fill';
    }
    if (volume.value > 50) {
        return 'bi-volume-down-fill';
    }
    if (volume.value > 0) {
        return 'bi-volume-off-fill';
    }
    return 'bi-volume-mute-fill';
});

const repeat_icon = computed(() => {
    if (store.playing.repeat == 0) {
        return 'bi-repeat text-muted';
    }
    if (store.playing.repeat == 1) {
        return 'bi-repeat theme-color';
    }
    if (store.playing.repeat == 2) {
        return 'bi-repeat-1 theme-color';
    }
});

// Must be synchronized in groupSession: ok
async function seekProgress(ev) {
    let src = null;

    if (ev.target.classList.contains('progress')) {
        src = ev.target;
    } else {
        src = ev.target.parentElement;
    }

    let rect = src.getBoundingClientRect();
    let x = ev.clientX - rect.left;
    let point = ft.player.duration() * (x / rect.width);

    action({
        func: async function op() {
            ft.seek(point);
        },
        object: [point],
        operation: "seek"
    })
}

// Must be synchronized in groupSession: ok
async function play() {
    action({
        func: async function op() {
            ft.play();
        },
        object: [null],
        operation: "play"
    });
}

// Must be synchronized in groupSession: ok
async function play_previous() {
    action({
        func: async function op() {
            ft.play_previous();
        },
        object: [null],
        operation: "playPrevious"
    });
}

// Must be synchronized in groupSession: ok
async function play_next() {
    action({
        func: async function op() {
            ft.play_next();
        },
        object: [null],
        operation: "playNext"
    });
}

async function show_queue() {
    queueEl.value.show();
}

async function openAlbum() {
    store.selected_track_id = store.playing.id;
    router.push("/album/" + store.playing.album);
}

async function show_lyrics() {
    lyricsEl.value.get_lyrics();
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

// Must be synchronized in groupSession: no
async function shuffle() {
    queueEl.value.shuffle();
}

async function group_session() {
    groupSession.value.show();
}

async function openQuality() {
    qualityDisplay.value.show();
}

defineExpose({
    show_queue,
    show_lyrics,
    shuffle,
    repeat,
    group_session,
})

onMounted(() => {
    let vol = JSON.parse(localStorage.getItem('volume'));
    if (vol) {
        volume.value = parseInt(parseFloat(vol) * 100);
        ft.player.volume(volume.value / 100);
    } else {
        localStorage.setItem('volume', JSON.stringify(volume.value / 100));
    }

    // Swipe events
    let hammertime = new Hammer(mobilePlayer.value);
    hammertime.get('swipe').set({ direction: Hammer.DIRECTION_VERTICAL });
    hammertime.on("swipeup", function () {
        mobileViewEl.value.show();
    });

    window.addEventListener('group_key', () => {
        group_key.value++;
    })
})
</script>