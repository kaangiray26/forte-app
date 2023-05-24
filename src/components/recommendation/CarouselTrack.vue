<template>
    <div class="card border-0" @contextmenu.prevent="right_click({ item: props.track, event: $event })">
        <div class="p-0">
            <div class="ph-track-carousel" @click="openTrack">
                <img class="img-square carousel-img rounded" :src="get_cover(props.track.cover)" @error="placeholder" />
            </div>
            <div class="mt-2">
                <h6 class="fw-bold clickable theme-color " @click="openTrack">{{ track.title }}</h6>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { store } from '/js/store.js';
import { right_click } from '/js/events.js';

const router = useRouter();

async function placeholder(obj) {
    obj.target.remove();
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

async function openTrack() {
    store.selected_track_id = props.track.id;
    router.push("/album/" + props.track.album);
}

const props = defineProps({
    track: {
        type: Object,
    }
});
</script>