<template>
    <div class="card h-100 w-100 border-0" @contextmenu.prevent="right_click({ item: props.artist, event: $event })">
        <div class="p-0">
            <div class="position-relative rounded">
                <div class="ph-artist-full" @click="openArtist">
                    <img class="img-fluid placeholder-img rounded" :src="get_cover(props.artist.cover)"
                        @error="placeholder" />
                </div>
            </div>
            <div class="d-flex flex-fill">
                <h6 class="theme-color  fw-bold text-break text-wrap clickable p-2 ps-0" @click="openArtist">
                    {{
                        props.artist.title
                    }}</h6>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { right_click } from '/js/events.js';

const router = useRouter();

async function placeholder(obj) {
    obj.target.remove();
}

function get_cover(cover) {
    if (!cover) {
        return "/images/artist.svg"
    }

    if (cover.startsWith("http")) {
        return cover;
    }

    return ft.server + '/' + cover;
}

async function openArtist() {
    router.push("/artist/" + props.artist.id);
}

const props = defineProps({
    artist: {
        type: Object,
    }
});
</script>