<template>
    <div v-show="!loaded">
        <div class="d-flex justify-content-center m-3">
            <button class="btn btn-dark" type="button" disabled>
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Loading...
            </button>
        </div>
    </div>
    <div class="card rounded-0 border-0" v-show="loaded">
        <div class="card-body p-4">
            <div class="row g-3">
                <div class="col-12">
                    <div class="d-flex position-relative">
                        <div class="ph-station-full">
                            <img class="img-square playlist-img rounded" :src="station.logo" @error="placeholder" />
                        </div>
                        <div class="position-absolute bottom-0 right-0">
                            <button class="btn btn-light action-btn bi bi-play-fill m-2" type="button"
                                @click="play_station()">
                            </button>
                        </div>
                    </div>
                </div>
                <div class="col theme-color">
                    <div class="d-flex flex-column">
                        <h1 class="album-title">{{ station.name }}</h1>
                        <small class="text-muted">{{ station.slogan }}</small>
                    </div>
                    <div class="pt-2">
                        <div class="d-flex flex-wrap">
                            <div class="m-1">
                                <button ref="wiki_btn" type="button" class="btn theme-btn  text-white fw-bold"
                                    @click="get_website">Website</button>
                            </div>
                        </div>
                    </div>
                    <hr />
                    <div>
                        <p>{{ station.description }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { CapacitorHttp } from '@capacitor/core';

const router = useRouter();

const station = ref({});
const loaded = ref(false);

async function placeholder(obj) {
    obj.target.remove();
}

async function get_website() {
    window.open(station.value.url, "_blank")
}

async function play_station() {
    ft.playStation({
        guide_id: station.value.guide_id,
        text: station.value.name,
        image: station.value.logo,
    });
}

async function get_station(id) {
    // Get station info
    let data = await CapacitorHttp.get({
        url: "https://opml.radiotime.com/Describe.ashx",
        params: {
            id: id,
            render: "json",
        }
    })
        .then(res => res.data.body[0])
        .catch(() => null);

    if (!data || data.error) {
        return;
    }

    station.value = data;
    loaded.value = true;
}

onMounted(() => {
    get_station(router.currentRoute.value.params.id);
})
</script>